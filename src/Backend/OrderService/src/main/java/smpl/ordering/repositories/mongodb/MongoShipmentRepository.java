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
import smpl.ordering.repositories.ShipmentRepository;
import smpl.ordering.repositories.mongodb.models.ShipmentDetails;

import java.util.ArrayList;
import java.util.List;

/**
 * MongoDB-based shipment repository implementation class
 */
public class MongoShipmentRepository implements ShipmentRepository, TestPath
{
    @Override
    public List<ShipmentRecord> getShipments(OrderStatus status)
    {
        List<Order> ordrs = orders.getOrdersByStatus(status);
        List<String> oids = new ArrayList<>();
        for (Order o : ordrs)
        {
            oids.add(o.getOrderId());
        }

        Query q = new Query(Criteria.where("orderId").in(oids));
        List<ShipmentDetails> details = operations.find(q, ShipmentDetails.class);

        List<ShipmentRecord> result = new ArrayList<>();
        if (details != null)
        {
            for (ShipmentDetails d : details)
            {
                result.add(d.toShipmentRecord());
            }
        }

        return result;
    }

    @Override
    public ShipmentRecord getShipmentById(String id)
    {
        Query q = new Query(Criteria.where("orderId").is(id));
        ShipmentDetails existing = operations.findOne(q, ShipmentDetails.class);
        return (existing != null) ? existing.toShipmentRecord() : null;
    }

    @Override
    public ShipmentRecord createShipment(ShipmentRecord info) throws BadRequestException
    {
        Order assocOrder = orders.getOrder(info.getOrderId());
        if (assocOrder == null)
        {
            throw new BadRequestException(String.format("Order '%s' could not be found: ", info.getOrderId()));
        }

        ShipmentDetails existing = findExistingShipmentDetails(info.getOrderId());
        if (existing != null)
        {
            throw new ConflictingRequestException(String.format("A shipment record for order '%s' already exists", info.getOrderId()));
        }

        operations.insert(new ShipmentDetails(info));
        return new ShipmentRecord(info);
    }

    private ShipmentDetails findExistingShipmentDetails(String id)
    {
        Query q = new Query(Criteria.where("orderId").is(id));
        return operations.findOne(q, ShipmentDetails.class);
    }

    @Override
    public boolean addEvent(String id, ShipmentEventInfo event)
    {
        ShipmentDetails existing = findExistingShipmentDetails(id);
        if (existing == null) return false;

        ShipmentRecord result = existing.toShipmentRecord();
        result.addEvent(event);
        return saveUpdates(existing, result);
    }

    private boolean saveUpdates(ShipmentDetails existing, ShipmentRecord result)
    {
        ShipmentDetails updated = new ShipmentDetails(result);
        updated.setId(existing.getId());

        operations.save(updated);

        return true;
    }

    @Override
    public boolean updateShipment(ShipmentRecord info)
    {
        ShipmentDetails existing = findExistingShipmentDetails(info.getOrderId());
        return (existing != null) && saveUpdates(existing, info);
    }

    @Override
    public boolean removeShipment(String id, String eTag)
    {
        Query findExisting = new Query(Criteria.where("orderId").is(id));
        ShipmentDetails existing = operations.findAndRemove(findExisting, ShipmentDetails.class);
        return existing != null;
    }

    public MongoShipmentRepository(MongoTemplate template, OrderRepository orders)
    {
        this.operations = new MongoOperationsWithRetry(template);
        this.orders = orders;
    }

    private final OrderRepository orders;
    private final MongoOperations operations;

    @Override
    public void reset()
    {
        operations.dropCollection("shipments");
    }
}
