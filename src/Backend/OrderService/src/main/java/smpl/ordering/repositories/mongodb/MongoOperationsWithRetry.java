package smpl.ordering.repositories.mongodb;

import com.microsoft.applicationinsights.TelemetryClient;
import com.microsoft.applicationinsights.channel.TelemetryChannel;
import com.microsoft.applicationinsights.telemetry.RemoteDependencyTelemetry;
import com.microsoft.applicationinsights.internal.schemav2.DependencyKind;
import com.microsoft.applicationinsights.internal.schemav2.DependencySourceType;
import com.mongodb.CommandResult;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import org.springframework.data.mongodb.core.*;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.geo.GeoResults;
import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.mapreduce.MapReduceOptions;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import smpl.ordering.Utility;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

@SuppressWarnings({"ALL", "deprecation"})
public class MongoOperationsWithRetry
        implements MongoOperations
{
    private MongoOperations underlying;

    MongoOperationsWithRetry(MongoOperations underlying)
    {
        this.underlying = underlying;
    }

    @Override
    public String getCollectionName(Class<?> entityClass)
    {
        return underlying.getCollectionName(entityClass);
    }

    @Override
    public CommandResult executeCommand(String jsonCommand)
    {
        return underlying.executeCommand(jsonCommand);
    }

    @Override
    public CommandResult executeCommand(DBObject command)
    {
        return underlying.executeCommand(command);
    }

    @Override
    public CommandResult executeCommand(DBObject command, int options)
    {
        return underlying.executeCommand(command, options);
    }

    @Override
    public void executeQuery(Query query, String collectionName, DocumentCallbackHandler dch)
    {
        underlying.executeQuery(query, collectionName, dch);
    }

    @Override
    public <T> T execute(DbCallback<T> action)
    {
        return underlying.execute(action);
    }

    @Override
    public <T> T execute(Class<?> entityClass, CollectionCallback<T> action)
    {
        return underlying.execute(entityClass, action);
    }

    @Override
    public <T> T execute(String collectionName, CollectionCallback<T> action)
    {
        return underlying.execute(collectionName, action);
    }

    @Override
    public <T> T executeInSession(DbCallback<T> action)
    {
        return underlying.executeInSession(action);
    }

    @Override
    public <T> DBCollection createCollection(Class<T> entityClass)
    {
        return underlying.createCollection(entityClass);
    }

    @Override
    public <T> DBCollection createCollection(Class<T> entityClass, CollectionOptions collectionOptions)
    {
        return underlying.createCollection(entityClass, collectionOptions);
    }

    @Override
    public DBCollection createCollection(String collectionName)
    {
        return underlying.createCollection(collectionName);
    }

    @Override
    public DBCollection createCollection(String collectionName, CollectionOptions collectionOptions)
    {
        return underlying.createCollection(collectionName, collectionOptions);
    }

    @Override
    public Set<String> getCollectionNames()
    {
        return underlying.getCollectionNames();
    }

    @Override
    public DBCollection getCollection(String collectionName)
    {
        return underlying.getCollection(collectionName);
    }

    @Override
    public <T> boolean collectionExists(Class<T> entityClass)
    {
        return underlying.collectionExists(entityClass);
    }

    @Override
    public boolean collectionExists(String collectionName)
    {
        return underlying.collectionExists(collectionName);
    }

    @Override
    public <T> void dropCollection(Class<T> entityClass)
    {
        boolean success = false;

        Date start = new Date();
        try
        {
            try
            {
                underlying.dropCollection(entityClass);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                underlying.dropCollection(entityClass);
            }
            success = true;
        }
        finally
        {
            sendTelemetry(start, new Date(), "dropCollection", success);
        }
    }

    private void sendTelemetry(Date start, Date end, String operation, boolean success)
    {
        TelemetryClient client = Utility.getTelemetryClient();
        if (client != null)
        {
            RemoteDependencyTelemetry rdt = new RemoteDependencyTelemetry(String.format("MongoDB.%s", operation));
            rdt.setValue((double) (end.getTime() - start.getTime()));
            rdt.setCount(1);
            rdt.setDependencyKind(DependencyKind.Undefined);
            rdt.setSuccess(success);
            client.track(rdt);
        }
    }

    @Override
    public void dropCollection(String collectionName)
    {
        boolean success = false;
        Date start = new Date();
        try
        {
            try
            {
                underlying.dropCollection(collectionName);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                underlying.dropCollection(collectionName);
            }
            success = true;
        }
        finally
        {
            sendTelemetry(start, new Date(), "dropCollection", success);
        }
    }

    @Override
    public IndexOperations indexOps(String collectionName)
    {
        return underlying.indexOps(collectionName);
    }

    @Override
    public IndexOperations indexOps(Class<?> entityClass)
    {
        return underlying.indexOps(entityClass);
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass)
    {
        List<T> result = null;
        Date start = new Date();
        boolean success = false;
        try
        {
            try
            {
                result = underlying.findAll(entityClass);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                result = underlying.findAll(entityClass);
            }
            success = true;
        }
        finally
        {
            sendTelemetry(start, new Date(), "findAll", success);
        }

        return result;
    }

    @Override
    public <T> List<T> findAll(Class<T> entityClass, String collectionName)
    {
        return underlying.findAll(entityClass, collectionName);
    }

    @Override
    public <T> GroupByResults<T> group(String inputCollectionName, GroupBy groupBy, Class<T> entityClass)
    {
        return underlying.group(inputCollectionName, groupBy, entityClass);
    }

    @Override
    public <T> GroupByResults<T> group(Criteria criteria, String inputCollectionName, GroupBy groupBy, Class<T> entityClass)
    {
        return underlying.group(criteria, inputCollectionName, groupBy, entityClass);
    }

    @Override
    public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, String collectionName, Class<O> outputType)
    {
        return underlying.aggregate(aggregation, collectionName, outputType);
    }

    @Override
    public <O> AggregationResults<O> aggregate(TypedAggregation<?> aggregation, Class<O> outputType)
    {
        return underlying.aggregate(aggregation, outputType);
    }

    @Override
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, Class<?> inputType, Class<O> outputType)
    {
        return underlying.aggregate(aggregation, inputType, outputType);
    }

    @Override
    public <O> AggregationResults<O> aggregate(Aggregation aggregation, String collectionName, Class<O> outputType)
    {
        return underlying.aggregate(aggregation, collectionName, outputType);
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass)
    {
        return underlying.mapReduce(inputCollectionName, mapFunction, reduceFunction, entityClass);
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass)
    {
        return underlying.mapReduce(inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass);
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, Class<T> entityClass)
    {
        return underlying.mapReduce(query, inputCollectionName, mapFunction, reduceFunction, entityClass);
    }

    @Override
    public <T> MapReduceResults<T> mapReduce(Query query, String inputCollectionName, String mapFunction, String reduceFunction, MapReduceOptions mapReduceOptions, Class<T> entityClass)
    {
        return underlying.mapReduce(query, inputCollectionName, mapFunction, reduceFunction, mapReduceOptions, entityClass);
    }

    @Override
    public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass)
    {
        return underlying.geoNear(near, entityClass);
    }

    @Override
    public <T> GeoResults<T> geoNear(NearQuery near, Class<T> entityClass, String collectionName)
    {
        return underlying.geoNear(near, entityClass, collectionName);
    }

    @Override
    public <T> T findOne(Query query, Class<T> entityClass)
    {
        T result = null;
        Date start = new Date();
        boolean success = false;
        try
        {
            try
            {
                result = underlying.findOne(query, entityClass);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                result = underlying.findOne(query, entityClass);
            }
            success = true;
        }
        finally
        {
            sendTelemetry(start, new Date(), "findOne", success);
        }

        return result;
    }

    @Override
    public <T> T findOne(Query query, Class<T> entityClass, String collectionName)
    {
        return underlying.findOne(query, entityClass, collectionName);
    }

    @Override
    public boolean exists(Query query, String collectionName)
    {
        return underlying.exists(query, collectionName);
    }

    @Override
    public boolean exists(Query query, Class<?> entityClass)
    {
        boolean result = false;
        Date start = new Date();
        boolean success = false;
        try
        {
            try
            {
                result = underlying.exists(query, entityClass);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                result = underlying.exists(query, entityClass);
            }
        }
        finally
        {
            sendTelemetry(start, new Date(), "exists", success);
        }

        return result;
    }

    @Override
    public boolean exists(Query query, Class<?> entityClass, String collectionName)
    {
        return underlying.exists(query, entityClass, collectionName);
    }

    @Override
    public <T> List<T> find(Query query, Class<T> entityClass)
    {
        List<T> result = null;
        Date start = new Date();
        boolean success = false;
        try
        {
            try
            {
                result = underlying.find(query, entityClass);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                result = underlying.find(query, entityClass);
            }
        }
        finally
        {
            sendTelemetry(start, new Date(), "find", success);
        }

        return result;
    }

    @Override
    public <T> List<T> find(Query query, Class<T> entityClass, String collectionName)
    {
        return underlying.find(query, entityClass, collectionName);
    }

    @Override
    public <T> T findById(Object id, Class<T> entityClass)
    {
        return underlying.findById(id, entityClass);
    }

    @Override
    public <T> T findById(Object id, Class<T> entityClass, String collectionName)
    {
        return underlying.findById(id, entityClass, collectionName);
    }

    @Override
    public <T> T findAndModify(Query query, Update update, Class<T> entityClass)
    {
        return underlying.findAndModify(query, update, entityClass);
    }

    @Override
    public <T> T findAndModify(Query query, Update update, Class<T> entityClass, String collectionName)
    {
        return underlying.findAndModify(query, update, entityClass, collectionName);
    }

    @Override
    public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass)
    {
        return underlying.findAndModify(query, update, options, entityClass);
    }

    @Override
    public <T> T findAndModify(Query query, Update update, FindAndModifyOptions options, Class<T> entityClass, String collectionName)
    {
        return underlying.findAndModify(query, update, options, entityClass, collectionName);
    }

    @Override
    public <T> T findAndRemove(Query query, Class<T> entityClass)
    {
        T result = null;
        Date start = new Date();
        boolean success = false;
        try
        {
            try
            {
                result = underlying.findAndRemove(query, entityClass);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                result = underlying.findAndRemove(query, entityClass);
            }
        }
        finally
        {
            sendTelemetry(start, new Date(), "findAndRemove", success);
        }

        return result;
    }

    @Override
    public <T> T findAndRemove(Query query, Class<T> entityClass, String collectionName)
    {
        return underlying.findAndRemove(query, entityClass, collectionName);
    }

    @Override
    public long count(Query query, Class<?> entityClass)
    {
        return underlying.count(query, entityClass);
    }

    @Override
    public long count(Query query, String collectionName)
    {
        return underlying.count(query, collectionName);
    }

    @Override
    public void insert(Object objectToSave)
    {
        Date start = new Date();
        boolean success = false;
        try
        {
            try
            {
                underlying.insert(objectToSave);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                underlying.insert(objectToSave);
            }
        }
        finally
        {
            sendTelemetry(start, new Date(), "insert", success);
        }
    }

    @Override
    public void insert(Object objectToSave, String collectionName)
    {
        underlying.insert(objectToSave, collectionName);
    }

    @Override
    public void insert(Collection<? extends Object> batchToSave, Class<?> entityClass)
    {
        underlying.insert(batchToSave, entityClass);
    }

    @Override
    public void insert(Collection<? extends Object> batchToSave, String collectionName)
    {
        underlying.insert(batchToSave, collectionName);
    }

    @Override
    public void insertAll(Collection<? extends Object> objectsToSave)
    {
        underlying.insertAll(objectsToSave);
    }

    @Override
    public void save(Object objectToSave)
    {
        Date start = new Date();
        boolean success = false;
        try
        {
            try
            {
                underlying.save(objectToSave);
            }
            catch (org.springframework.dao.DataAccessResourceFailureException darf)
            {
                if (darf.getRootCause().getClass() != java.net.SocketTimeoutException.class)
                {
                    throw darf;
                }
                underlying.save(objectToSave);
            }
        }
        finally
        {
            sendTelemetry(start, new Date(), "save", success);
        }

    }

    @Override
    public void save(Object objectToSave, String collectionName)
    {
        underlying.save(objectToSave, collectionName);
    }

    @Override
    public WriteResult upsert(Query query, Update update, Class<?> entityClass)
    {
        return underlying.upsert(query, update, entityClass);
    }

    @Override
    public WriteResult upsert(Query query, Update update, String collectionName)
    {
        return underlying.upsert(query, update, collectionName);
    }

    @Override
    public WriteResult upsert(Query query, Update update, Class<?> entityClass, String collectionName)
    {
        return underlying.upsert(query, update, entityClass, collectionName);
    }

    @Override
    public WriteResult updateFirst(Query query, Update update, Class<?> entityClass)
    {
        return underlying.updateFirst(query, update, entityClass);
    }

    @Override
    public WriteResult updateFirst(Query query, Update update, String collectionName)
    {
        return underlying.updateFirst(query, update, collectionName);
    }

    @Override
    public WriteResult updateFirst(Query query, Update update, Class<?> entityClass, String collectionName)
    {
        return underlying.updateFirst(query, update, entityClass, collectionName);
    }

    @Override
    public WriteResult updateMulti(Query query, Update update, Class<?> entityClass)
    {
        return underlying.updateMulti(query, update, entityClass);
    }

    @Override
    public WriteResult updateMulti(Query query, Update update, String collectionName)
    {
        return underlying.updateMulti(query, update, collectionName);
    }

    @Override
    public WriteResult updateMulti(Query query, Update update, Class<?> entityClass, String collectionName)
    {
        return underlying.updateMulti(query, update, entityClass, collectionName);
    }

    @Override
    public WriteResult remove(Object object)
    {
        return underlying.remove(object);
    }

    @Override
    public WriteResult remove(Object object, String collection)
    {
        return underlying.remove(object, collection);
    }

    @Override
    public WriteResult remove(Query query, Class<?> entityClass)
    {
        return underlying.remove(query, entityClass);
    }

    @Override
    public WriteResult remove(Query query, Class<?> entityClass, String collectionName)
    {
        return underlying.remove(query, entityClass, collectionName);
    }

    @Override
    public WriteResult remove(Query query, String collectionName)
    {
        return underlying.remove(query, collectionName);
    }

    @Override
    public <T> List<T> findAllAndRemove(Query query, String collectionName)
    {
        return underlying.findAllAndRemove(query, collectionName);
    }

    @Override
    public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass)
    {
        return underlying.findAllAndRemove(query, entityClass);
    }

    @Override
    public <T> List<T> findAllAndRemove(Query query, Class<T> entityClass, String collectionName)
    {
        return underlying.findAllAndRemove(query, entityClass, collectionName);
    }

    @Override
    public MongoConverter getConverter()
    {
        return underlying.getConverter();
    }
}
