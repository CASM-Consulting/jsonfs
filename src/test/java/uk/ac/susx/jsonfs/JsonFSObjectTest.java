package uk.ac.susx.jsonfs;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.MapTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.TestStringMapGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import com.google.common.collect.testing.features.MapFeature;
import junit.framework.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by sw206 on 14/09/2016.
 */
public class JsonFSObjectTest {
    public static Test suite() {
        return new JsonFSObjectTest().myMapTestSuite();
    }

    public Test myMapTestSuite() {
        return MapTestSuiteBuilder.using(
                // This class is responsible for creating the collection
                // And providing data, which can be put into the collection
                // Here we use a abstract generator which will create strings
                // which will be put into the collection
                new TestStringMapGenerator(){
                    @Override
                    protected Map create(Map.Entry<String, String>[] entries) {

                        Map<String, String> map = new HashMap<>();
                        for(Map.Entry<String,String> entry : entries) {
                            map.put(entry.getKey(), entry.getValue());
                        }

                        JsonFSObject object = new JsonFS("test").object(map);
                        return object;
                    }

                })
                // The name of the test suite
                .named("My Map Tests")
                // Here we give a hit what features our collection supports
                .withFeatures(MapFeature.GENERAL_PURPOSE,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionSize.ANY)
                .createTestSuite();
    }
}
