package smpl.ordering.repositories.mock;

import smpl.ordering.BadRequestException;
import smpl.ordering.ConflictingRequestException;
import smpl.ordering.TestPath;
import smpl.ordering.models.*;
import smpl.ordering.repositories.OrderRepository;
import smpl.ordering.repositories.QuoteRepository;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory order repository implementation class
 */
public class MockOrderRepository
        implements OrderRepository, TestPath
{
    public MockOrderRepository(QuoteRepository quotes)
    {
        this.orders = new ArrayList<>();
        this.quotes = quotes;
    }

    @Override
    public boolean hasOrder(String id)
    {
        for (Order order : orders)
        {
            if (order.getOrderId().equals(id))
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public Order getOrder(String id)
    {
        for (Order order : orders)
        {
            if (order.getOrderId().equals(id))
            {
                return order;
            }
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status)
    {
        List<Order> lst = new ArrayList<>();
        for (Order order : orders)
        {
            if (status == OrderStatus.None || order.getStatus() == status)
            {
                lst.add(order);
            }
        }
        return lst;
    }

    @Override
    public List<Order> getOrdersByDealerName(String name, OrderStatus status)
    {
        List<Order> lst = new ArrayList<>();
        for (Order order : orders)
        {
            Quote q = quotes.getQuote(order.getQuoteId());
            if (q.getDealerName().toLowerCase().equals(name.toLowerCase()) &&
                    (status == OrderStatus.None || status == order.getStatus()))
            {
                lst.add(order);
            }
        }
        return lst;
    }

    @Override
    public Order getOrderByQuoteId(String id)
    {
        for (Order order : orders)
        {
            if (order.getQuoteId().equals(id))
            {
                return order;
            }
        }
        return null;
    }


    @Override
    public Order createOrder(String from) throws BadRequestException
    {
        Quote q = quotes.getQuote(from);
        if (q == null)
        {
            throw new BadRequestException(String.format("No such quote: %s", from));
        }

        Order assocOrder = getOrderByQuoteId(from);

        if (assocOrder != null)
        {
            throw new ConflictingRequestException(String.format("The quote has already been used to create an order: %s", assocOrder.getOrderId()));
        }

        Order result = new Order();
        DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

        result.setOrderDate(df.format(new Date()));
        result.setOrderId(String.format("order-%s", from));
        result.setQuoteId(from);
        result.setStatus(OrderStatus.Created);

        orders.add(result);

        return result;
    }

    @Override
    public boolean updateOrder(String id, Order order, String eTag)
    {
        Order old = getOrder(id);
        if (old == null) return false;
        int idx = orders.indexOf(old);
        orders.set(idx, order);
        return true;
    }

    @Override
    public boolean updateOrder(String id, OrderUpdateInfo info, String eTag) throws BadRequestException
    {
        Order old = getOrder(id);
        if (old == null) throw new BadRequestException("No such order");
        old.addEvent(info.getEventInfo());
        old.setStatus(info.getStatus());
        return true;
    }

    @Override
    public boolean removeOrder(String id, String eTag)
    {
        return false;
    }

    static
    {
        s_counter = new AtomicLong(0L);
    }

    private final List<Order> orders;
    private static AtomicLong s_counter;
    private final QuoteRepository quotes;

    @Override
    public void reset()
    {
        orders.clear();
        s_counter = new AtomicLong(0L);
    }
}
