package org.parceler;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest=Config.NONE)
public class RealmTest {

    public static class Base{}
    public static class StampProxy extends Stamp{}

    @Parcel(implementations = StampProxy.class,
        value = Parcel.Serialization.BEAN,
        analyze = Stamp.class)
    public static class Stamp extends Base {
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Test
    public void testRealm() {
        Stamp stamp = new StampProxy();

        stamp.setId("123");

        Stamp unwrapped = Parcels.unwrap(ParcelsTestUtil.wrap(stamp));

        assertEquals(stamp.getId(), unwrapped.getId());
    }

}
