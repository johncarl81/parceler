package org.parceler;

import android.os.Parcel;

/**
 * @author John Ericksen
 */
public class ManuallyRegisteredConverter implements ParcelConverter<ManuallyRegistered> {

    @Override
    public void toParcel(ManuallyRegistered input, Parcel destinationParcel) {
        destinationParcel.writeString(input.getValue());
    }

    @Override
    public ManuallyRegistered fromParcel(Parcel parcel) {
        ManuallyRegistered serialized = new ManuallyRegistered();

        serialized.setValue(parcel.readString());

        return serialized;
    }
}
