package smpl.fabrikant.ordering.repositories;

import smpl.fabrikant.ordering.BadRequestException;
import smpl.fabrikant.ordering.models.OrderStatus;
import smpl.fabrikant.ordering.models.ShipmentEventInfo;
import smpl.fabrikant.ordering.models.ShipmentRecord;

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
