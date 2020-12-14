package kraptis91.maritime.dao;

import org.junit.Test;

import java.io.InputStream;

/**
 * @author Konstantinos Raptis [kraptis at unipi.gr] on 8/12/2020.
 */
public class VesselDaoTest {

    private final InputStream isSample =
            MongoOceanConditionsDaoTest.class.getResourceAsStream(
                    "/sample/maritime/nari_static_sample.csv");

    @Test
    public void testInsertMany() throws Exception {

//    InputStream isBig =
//        new FileInputStream(
//            DirectoryUtils.getDefaultDownloadsDirectory() + "/ais-data/nari_static.csv");

        VesselDao dao = DaoFactory.createMongoVesselDao();
        dao.insertMany(isSample);
    }

    @Test
    public void testFindObjectId() throws Exception {

        VesselDao dao = DaoFactory.createMongoVesselDao();
        System.out.println(dao.findObjectId(228157000));

    }

}
