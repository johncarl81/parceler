package org.parceler;

import android.arch.lifecycle.MutableLiveData;
import android.os.Parcelable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class MutableLiveDataTest {

    @Parcel
    public static class TestTarget {
        MutableLiveData<TestParcel> test = new MutableLiveData<TestParcel>();
    }

    @Parcel
    public static class TestParcel {
        String value;
    }

    @Test
    public void testMutableLiveData() {

        TestParcel parcel = new TestParcel();
        parcel.value = "test";
        TestTarget target = new TestTarget();
        target.test.setValue(parcel);

        Parcelable wrap = ParcelsTestUtil.wrap(target);
        TestTarget unwrap = Parcels.unwrap(wrap);
        assertNotNull(unwrap.test.getValue());
        assertEquals("test", unwrap.test.getValue().value);
    }
}
