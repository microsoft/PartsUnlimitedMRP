package smpl.ordering.repositories.mongodb;

import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import smpl.ordering.BadRequestException;
import smpl.ordering.ConflictingRequestException;
import smpl.ordering.TestPath;
import smpl.ordering.models.*;
import smpl.ordering.repositories.OrderRepository;
import smpl.ordering.repositories.QuoteRepository;
import smpl.ordering.repositories.mongodb.models.OrderDetails;
import smpl.ordering.repositories.mongodb.models.QuoteDetails;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MongoOrderRepository
        implements OrderRepository, TestPath
{
    @Override
    public boolean hasOrder(String id)
    {
        Query findExisting = new Query(Criteria.where("orderId").is(id));
        return operations.exists(findExisting, OrderDetails.class);
    }

    @Override
    public Order getOrder(String id)
    {
        OrderDetails existing = findExistingOrder(id);
        return (existing != null) ? existing.toOrder() : null;
    }

    private OrderDetails findExistingOrder(String id)
    {
        Query findExisting = new Query(Criteria.where("orderId").is(id));
        return operations.findOne(findExisting, OrderDetails.class);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status)
    {
        List<OrderDetails> found;
        if (status == OrderStatus.None)
        {
            found = operations.findAll(OrderDetails.class);
        }
        else
        {
            Query findExisting = new Query(Criteria.where("status").is(status));
            found = operations.find(findExisting, OrderDetails.class);
        }

        List<Order> result = new ArrayList<>();
        if (found != null && found.size() > 0)
        {
            for (OrderDetails details : found)
            {
                result.add(details.toOrder());
            }
        }
        return result;
    }


    @Override
    public List<Order> getOrdersByDealerName(String dealer, OrderStatus status)
    {
        List<String> quotesIds = quotes.getQuoteIdsByDealerName(dealer);

        Criteria criteria = Criteria.where("quoteId").in(quotesIds);

        if (status != OrderStatus.None)
        {
            criteria = criteria.and("status").is(status);
        }

        Query findExisting = new Query(criteria);

        List<OrderDetails> found = operations.find(findExisting, OrderDetails.class);

        List<Order> result = new ArrayList<>();
        if (found != null && found.size() > 0)
        {
            for (OrderDetails details : found)
            {
                result.add(details.toOrder());
            }
        }
        return result;
    }

    @Override
    public Order getOrderByQuoteId(String id)
    {
        Query findExisting = new Query(Criteria.where("quoteId").is(id));
        OrderDetails existing = operations.findOne(findExisting, OrderDetails.class);
        return (existing != null) ? existing.toOrder() : null;
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

        operations.insert(new OrderDetails(result));

        return result;
    }

    @Override
    public boolean updateOrder(String id, Order order, String eTag)
    {
        OrderDetails existing = findExistingOrder(id);
        return (existing != null) && saveOrder(id, order, existing);
    }

    private boolean saveOrder(String id, Order order, OrderDetails existing)
    {
        order.setOrderId(id); // Just to make sure

        OrderDetails details = new OrderDetails(order);
        details.setId(existing.getId());

        operations.save(details);

        return true;
    }

    @Override
    public boolean updateOrder(String id, OrderUpdateInfo info, String eTag) throws BadRequestException
    {
        OrderDetails existing = findExistingOrder(id);

        Order old = existing.toOrder();
        old.addEvent(info.getEventInfo());
        old.setStatus(info.getStatus());
        return saveOrder(id, old, existing);
    }

    @Override
    public boolean removeOrder(String id, String eTag)
    {
        Query findExisting = new Query(Criteria.where("orderId").is(id));
        OrderDetails existing = operations.findAndRemove(findExisting, OrderDetails.class);
        return existing != null;
    }

    public MongoOrderRepository(MongoTemplate template, QuoteRepository quotes)
    {
        this.operations = new MongoOperationsWithRetry(template);
        this.quotes = quotes;
    }

    static
    {
        s_counter = new AtomicLong(0L);
    }

    private static AtomicLong s_counter;
    private final QuoteRepository quotes;

    private final MongoOperations operations;

    @Override
    public void reset()
    {
        operations.dropCollection("orders");
        s_counter = new AtomicLong(0L);
    }
}
