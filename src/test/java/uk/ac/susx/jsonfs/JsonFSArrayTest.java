package uk.ac.susx.jsonfs;

import com.google.common.collect.testing.ListTestSuiteBuilder;
import com.google.common.collect.testing.TestStringListGenerator;
import com.google.common.collect.testing.features.CollectionFeature;
import com.google.common.collect.testing.features.CollectionSize;
import com.google.common.collect.testing.features.ListFeature;
import junit.framework.Test;


import java.util.Arrays;
import java.util.List;


/**
 * Created by sw206 on 14/09/2016.
 */
public class JsonFSArrayTest {
    public static Test suite() {
        return new JsonFSArrayTest().myListTestSuite();
    }

    public Test myListTestSuite() {
        return ListTestSuiteBuilder.using(
                // This class is responsible for creating the collection
                // And providing data, which can be put into the collection
                // Here we use a abstract generator which will create strings
                // which will be put into the collection
                new TestStringListGenerator(){
                    @Override
                    protected List create(String[] elements) {
                        JsonFSArray array = new JsonFS("test").array(Arrays.asList(elements));
                        // Fill here your collection with the given elements
                        return array;
                    }
                })
                // The name of the test suite
                .named("My List Tests")
                // Here we give a hit what features our collection supports
                .withFeatures(ListFeature.GENERAL_PURPOSE,
                        CollectionFeature.ALLOWS_NULL_VALUES,
                        CollectionFeature.FAILS_FAST_ON_CONCURRENT_MODIFICATION,
                        CollectionSize.ANY)
                .createTestSuite();
    }
}
