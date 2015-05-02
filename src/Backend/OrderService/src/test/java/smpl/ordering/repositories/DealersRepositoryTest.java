package smpl.ordering.repositories;

import smpl.ordering.TestPath;
import smpl.ordering.models.DealerInfo;

import java.util.List;

import static org.junit.Assert.*;

public class DealersRepositoryTest
{
    public void setUp() throws Exception
    {
        repository = RepositoryFactory.getDealersRepository();
        ((TestPath) repository).reset();

        repository.upsertDealer(createDealer("DLR-1"), null);
        repository.upsertDealer(createDealer("DLR-2"), null);
        repository.upsertDealer(createDealer("DLR-3"), null);
        repository.upsertDealer(createDealer("DLR-4"), null);
    }

    public void testGetDealers()
    {
        List<DealerInfo> dealers = repository.getDealers();
        assertNotNull(dealers);
        assertEquals(4, dealers.size());
        for (DealerInfo info : dealers)
        {
            assertEquals("John Doe", info.getContact());
        }
        assertEquals("DLR-1", dealers.get(0).getName());
        assertEquals("DLR-2", dealers.get(1).getName());
        assertEquals("DLR-3", dealers.get(2).getName());
        assertEquals("DLR-4", dealers.get(3).getName());
    }

    public void testGetDealer()
    {
        DealerInfo dealer = repository.getDealer("DLR-1");
        assertEquals("DLR-1@tempuri.org", dealer.getEmail());
        dealer = repository.getDealer("DLR-5");
        assertNull(dealer);
    }

    public void testUpsertDealer()
    {
        DealerInfo dealer = repository.getDealer("DLR-1");
        assertEquals("DLR-1@tempuri.org", dealer.getEmail());
        dealer.setEmail("jd@tempuri.org");
        assertTrue(repository.upsertDealer(dealer, null));
        dealer = repository.getDealer("DLR-1");
        assertEquals("jd@tempuri.org", dealer.getEmail());

        assertFalse(repository.upsertDealer(createDealer("DLR-5"), null));
        dealer = repository.getDealer("DLR-5");
        assertNotNull(dealer);
        assertEquals("DLR-5@tempuri.org", dealer.getEmail());

        assertEquals(5, repository.getDealers().size());
    }

    public void testRemoveDealer()
    {
        assertTrue(repository.removeDealer("DLR-1", null));
        assertEquals(3, repository.getDealers().size());

        assertFalse(repository.removeDealer("DLR-5", null));

        assertTrue(repository.removeDealer("DLR-2", null));
        assertTrue(repository.removeDealer("DLR-3", null));
        assertTrue(repository.removeDealer("DLR-4", null));

        assertFalse(repository.removeDealer("DLR-2", null));
    }

    public static DealerInfo createDealer(String name)
    {
        DealerInfo info = new DealerInfo();
        info.setName(name);
        info.setPhone("425-555-1212");
        info.setContact("John Doe");
        info.setEmail(name + "@tempuri.org");
        info.setAddress("1234 Main St., Redmond, WA 98052");
        return info;
    }

    private DealersRepository repository;
}
