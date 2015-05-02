package smpl.ordering.repositories;

import smpl.ordering.BadRequestException;
import smpl.ordering.models.OrderStatus;
import smpl.ordering.models.ShipmentEventInfo;
import smpl.ordering.models.ShipmentRecord;

import java.util.List;

/**
 * Interface for repositories holding shipment data.
 */
public interface ShipmentRepository
{
    List<ShipmentRecord> getShipments(OrderStatus status);

    ShipmentRecord getShipmentById(String id);

    ShipmentRecord createShipment(ShipmentRecord info) throws BadRequestException;

    boolean addEvent(String id, ShipmentEventInfo event);

    boolean updateShipment(ShipmentRecord info);

    boolean removeShipment(String id, String eTag);
}
