package smpl.fabrikant.ordering.repositories.mock;

import smpl.fabrikant.ordering.BadRequestException;
import smpl.fabrikant.ordering.TestPath;
import smpl.fabrikant.ordering.models.Order;
import smpl.fabrikant.ordering.models.OrderStatus;
import smpl.fabrikant.ordering.models.ShipmentEventInfo;
import smpl.fabrikant.ordering.models.ShipmentRecord;
import smpl.fabrikant.ordering.repositories.OrderRepository;
import smpl.fabrikant.ordering.repositories.ShipmentRepository;

import java.util.ArrayList;
import java.util.List;

public class MockShipmentRepository
        implements ShipmentRepository, TestPath
{
    public MockShipmentRepository(OrderRepository orders)
    {
        this.orders = orders;
    }

    @Override
    public List<ShipmentRecord> getShipments(OrderStatus status)
    {
        List<ShipmentRecord> result = new ArrayList<>();
        for (ShipmentRecord record : records)
        {
            if (status == OrderStatus.None)
            {
                result.add(new ShipmentRecord(record));
            }
            else
            {
                Order o = orders.getOrder(record.getOrderId());
                if (o.getStatus() == status)
                {
                    result.add(new ShipmentRecord(record));
                }
            }
        }
        return result;
    }

    @Override
    public ShipmentRecord getShipmentById(String id)
    {
        for (ShipmentRecord record : records)
        {
            if (record.getOrderId().equals(id))
            {
                return new ShipmentRecord(record);
            }
        }
        return null;
    }

    @Override
    public ShipmentRecord createShipment(ShipmentRecord info) throws BadRequestException
    {
        Order order = orders.getOrder(info.getOrderId());
        if (order == null)
        {
            throw new BadRequestException(String.format("No such order: %s", info.getOrderId()));
        }

        ShipmentRecord existing = getShipmentById(info.getOrderId());
        if (existing != null)
        {
            throw new BadRequestException(String.format("A shipment record for order '%s' already exists", info.getOrderId()));
        }

        ShipmentRecord result = new ShipmentRecord(info);
        records.add(result);
        return result;
    }

    @Override
    public boolean addEvent(String id, ShipmentEventInfo event)
    {
        ShipmentRecord existing = null;

        for (ShipmentRecord record : records)
        {
            if (record.getOrderId().equals(id))
            {
                existing = record;
                break;
            }
        }

        if (existing == null) return false;

        existing.addEvent(new ShipmentEventInfo(event.getDate(), event.getComments()));

        return true;
    }

    @Override
    public boolean updateShipment(ShipmentRecord info)
    {
        int idx = -1;
        String id = info.getOrderId();

        for (int i = 0; i < records.size(); ++i)
        {
            ShipmentRecord record = records.get(i);
            if (record.getOrderId().equals(id))
            {
                idx = i;
                break;
            }
        }

        if (idx == -1) return false;

        // Replace shipment in the same location

        records.set(idx, new ShipmentRecord(info));

        return true;
    }

    @Override
    public boolean removeShipment(String id, String eTag)
    {
        return false;
    }

    public void reset()
    {
        records.clear();
    }

    private final List<ShipmentRecord> records = new ArrayList<>();
    private final OrderRepository orders;
}
