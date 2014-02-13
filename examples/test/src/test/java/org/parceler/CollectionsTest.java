/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler;

import android.util.SparseArray;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class CollectionsTest {

    @Test
    public void testList(){
        List<SubParcel> exampleList = new ArrayList<SubParcel>();

        exampleList.add(new SubParcel("name"));

        List<SubParcel> exampleParcelList = Parcels.unwrap(new CollectionsRepository.ListParcelable(ParcelsTestUtil.wrap(exampleList)));
        assertEquals(1, exampleParcelList.size());

        SubParcel exampleParcel = exampleParcelList.get(0);
        assertEquals("name", exampleParcel.getName());
    }

    @Test
    public void testSet(){

        Set<SubParcel> exampleSet = new HashSet<SubParcel>();

        exampleSet.add(new SubParcel("name"));

        Set<SubParcel> exampleParcelList = Parcels.unwrap(new CollectionsRepository.SetParcelable(ParcelsTestUtil.wrap(exampleSet)));
        assertEquals(1, exampleParcelList.size());

        SubParcel exampleParcel = exampleParcelList.iterator().next();
        assertEquals("name", exampleParcel.getName());
    }

    @Test
    public void testMap(){

        Map<SubParcel, SubParcel> exampleSet = new HashMap<SubParcel, SubParcel>();

        SubParcel key = new SubParcel("key");
        exampleSet.put(key, new SubParcel("name"));

        Map<SubParcel, SubParcel> exampleParcelList = Parcels.unwrap(new CollectionsRepository.MapParcelable(ParcelsTestUtil.wrap(exampleSet)));
        assertEquals(1, exampleParcelList.size());

        SubParcel exampleParcel = exampleParcelList.get(key);
        assertEquals("name", exampleParcel.getName());
    }


    @Test
    public void testSparseArray(){

        SparseArray<SubParcel> exampleArray = new SparseArray<SubParcel>();

        exampleArray.append(1, new SubParcel("name"));

        SparseArray<SubParcel> exampleSparseArray = Parcels.unwrap(new CollectionsRepository.SparseArrayParcelable(ParcelsTestUtil.wrap(exampleArray)));
        assertEquals(1, exampleSparseArray.size());

        SubParcel exampleParcel = exampleSparseArray.get(1);
        assertEquals("name", exampleParcel.getName());
    }
}
