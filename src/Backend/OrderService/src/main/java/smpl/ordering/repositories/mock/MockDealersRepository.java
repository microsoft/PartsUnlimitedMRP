package smpl.ordering.repositories.mock;

import smpl.ordering.TestPath;
import smpl.ordering.models.DealerInfo;
import smpl.ordering.repositories.DealersRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * In-memory dealer repository.
 */
public class MockDealersRepository
        implements DealersRepository, TestPath
{
    public MockDealersRepository()
    {
        this.dealers = new ArrayList<>();
    }

    @Override
    public List<DealerInfo> getDealers()
    {
        List<DealerInfo> result = new ArrayList<>();
        for (DealerInfo info : dealers)
        {
            result.add(new DealerInfo(info));
        }
        return result;
    }

    @Override
    public DealerInfo getDealer(String name)
    {
        for (DealerInfo info : dealers)
        {
            if (compareDealerNames(name, info))
            {
                return new DealerInfo(info);
            }
        }
        return null;
    }

    private boolean compareDealerNames(String name, DealerInfo info)
    {
        return info.getName().toLowerCase().equals(name.toLowerCase());
    }

    @Override
    public boolean upsertDealer(DealerInfo dealer, String eTag)
    {
        String name = dealer.getName();
        for (int i = 0; i < dealers.size(); ++i)
        {
            DealerInfo info = dealers.get(i);
            if (compareDealerNames(name, info))
            {
                dealers.set(i, dealer);
                return true;
            }
        }
        dealers.add(dealer);
        return false;
    }

    @Override
    public boolean removeDealer(String name, String eTag)
    {
        for (int i = 0; i < dealers.size(); ++i)
        {
            DealerInfo info = dealers.get(i);
            if (compareDealerNames(name, info))
            {
                dealers.remove(i);
                return true;
            }
        }
        return false;
    }

    private final List<DealerInfo> dealers;

    @Override
    public void reset()
    {
        dealers.clear();
    }
}
