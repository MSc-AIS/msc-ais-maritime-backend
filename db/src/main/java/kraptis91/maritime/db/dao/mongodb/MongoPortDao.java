package kraptis91.maritime.db.dao.mongodb;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import kraptis91.maritime.db.dao.PortDao;
import kraptis91.maritime.db.enums.MongoDB;
import kraptis91.maritime.db.enums.MongoDBCollection;
import kraptis91.maritime.model.Port;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Konstantinos Raptis [kraptis at unipi.gr] on 30/12/2020.
 */
public class MongoPortDao implements PortDao {

    public static MongoCollection<Port> createWorldPortCollection() {
        return MongoDB.MARITIME
            .getDatabase()
            .getCollection(
                MongoDBCollection.WORLD_PORTS.getCollectionName(), Port.class);
    }

    @Override
    public void insertMany(List<Port> ports) {
        MongoCollection<Port> collection = createWorldPortCollection();
        collection.insertMany(ports);
    }

    @Override
    public List<Port> findPorts(int skip, int limit) {
        final List<Port> portList = new ArrayList<>();
        createWorldPortCollection()
            .find()
            .skip(skip)
            .limit(limit)
            .forEach((Consumer<Port>) portList::add);
        return portList;
    }

    @Override
    public List<Port> findPortsByCountryCode(String countryCode) {
        final List<Port> portList = new ArrayList<>();
        createWorldPortCollection()
            .find(Filters.eq("country", countryCode))
            .forEach((Consumer<Port>) portList::add);
        return portList;
    }
}
