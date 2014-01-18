package org.parceler;

import android.os.Parcelable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import static org.junit.Assert.assertTrue;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class ConverterTest {

    private PodamFactory factory;

    @Before
    public void init() {
        factory = new PodamFactoryImpl();
    }

    @Test
    public void testTypes(){

        ConverterTarget target = factory.manufacturePojo(ConverterTarget.class);

        android.os.Parcel parcel = android.os.Parcel.obtain();
        Parcelable parcelable = new ConverterTarget$$Parcelable(target);
        parcelable.writeToParcel(parcel, 0);

        ConverterTarget unwrapped = Parcels.unwrap(new ConverterTarget$$Parcelable(parcel));

        assertTrue(target.equals(unwrapped));
    }
}
