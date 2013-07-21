package org.parceler;

import android.os.Parcel;

/**
 * @author John Ericksen
 */
public class ManualConverter implements ParcelConverter<Manual> {

    @Override
    public void toParcel(Manual input, Parcel destinationParcel) {
        destinationParcel.writeString(input.getValue());
    }

    @Override
    public Manual fromParcel(Parcel parcel) {
        Manual serialized = new Manual();

        serialized.setValue(parcel.readString());

        return serialized;
    }
}
