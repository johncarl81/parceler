package org.parceler.internal;

import android.os.Parcel;
import org.parceler.ParcelConverter;

/**
 * @author John Ericksen
 */
public  class StringWriterConverter implements ParcelConverter<String> {

    @Override
    public void toParcel(String input, Parcel parcel) {
        parcel.writeString(input);
    }

    @Override
    public String fromParcel(Parcel parcel) {
        return parcel.readString();
    }
}