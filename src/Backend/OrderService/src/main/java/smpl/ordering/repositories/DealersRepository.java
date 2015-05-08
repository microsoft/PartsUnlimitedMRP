package smpl.ordering.repositories;

import smpl.ordering.models.DealerInfo;

import java.util.List;

/**
 * Represents the interface of repositories holding dealer information.
 */
@SuppressWarnings({"SameParameterValue", "UnusedParameters"})
public interface DealersRepository
{
    List<DealerInfo> getDealers();

    DealerInfo getDealer(String name);

    boolean upsertDealer(DealerInfo dealer, String eTag);

    boolean removeDealer(String name, String eTag);
}
