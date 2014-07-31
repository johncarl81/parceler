package org.parceler.performance;

import android.os.Parcel;

/**
 * @author John Ericksen
 */
public interface ParcelMutator {

    void write(Parcel parcel);

    void read(Parcel parcel);
}
