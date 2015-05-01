package smpl.fabrikant.ordering.repositories;

import smpl.fabrikant.ordering.BadRequestException;
import smpl.fabrikant.ordering.models.Order;
import smpl.fabrikant.ordering.models.OrderStatus;
import smpl.fabrikant.ordering.models.OrderUpdateInfo;

import java.util.List;

/**
 * Interface for repositories holding order information
 */
@SuppressWarnings({"SameParameterValue", "UnusedParameters"})
public interface OrderRepository
{
    boolean hasOrder(String id);

    Order getOrder(String id);

    Order getOrderByQuoteId(String id);

    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getOrdersByDealerName(String dealer, OrderStatus status);

    Order createOrder(String from) throws BadRequestException;

    @SuppressWarnings("UnusedParameters")
    boolean updateOrder(String id, Order order, String eTag);

    @SuppressWarnings("UnusedParameters")
    boolean updateOrder(String id, OrderUpdateInfo info, String eTag) throws BadRequestException;

    boolean removeOrder(String id, String eTag);
}
