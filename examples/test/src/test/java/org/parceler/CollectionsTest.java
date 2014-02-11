package org.parceler;

import android.os.Parcelable;
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

        List<SubParcel> exampleParcelList = Parcels.unwrap(wrap(exampleList));
        assertEquals(1, exampleParcelList.size());

        SubParcel exampleParcel = exampleParcelList.get(0);
        assertEquals("name", exampleParcel.getName());
    }

    @Test
    public void testSet(){

        Set<SubParcel> exampleSet = new HashSet<SubParcel>();

        exampleSet.add(new SubParcel("name"));

        Set<SubParcel> exampleParcelList = Parcels.unwrap(wrap(exampleSet));
        assertEquals(1, exampleParcelList.size());

        SubParcel exampleParcel = exampleParcelList.iterator().next();
        assertEquals("name", exampleParcel.getName());
    }

    @Test
    public void testMap(){

        Map<SubParcel, SubParcel> exampleSet = new HashMap<SubParcel, SubParcel>();

        SubParcel key = new SubParcel("key");
        exampleSet.put(key, new SubParcel("name"));

        Map<SubParcel, SubParcel> exampleParcelList = Parcels.unwrap(wrap(exampleSet));
        assertEquals(1, exampleParcelList.size());

        SubParcel exampleParcel = exampleParcelList.get(key);
        assertEquals("name", exampleParcel.getName());
    }


    @Test
    public void testSparseArray(){

        SparseArray<SubParcel> exampleArray = new SparseArray<SubParcel>();

        exampleArray.append(1, new SubParcel("name"));

        SparseArray<SubParcel> exampleSparseArray = Parcels.unwrap(wrap(exampleArray));
        assertEquals(1, exampleSparseArray.size());

        SubParcel exampleParcel = exampleSparseArray.get(1);
        assertEquals("name", exampleParcel.getName());
    }


    private <T extends Parcelable> T wrap(Object input){
        android.os.Parcel parcel = android.os.Parcel.obtain();

        parcel.writeParcelable(Parcels.wrap(input), 0);
        
        return parcel.readParcelable(CollectionsTest.class.getClassLoader());
    }
}
