package org.parceler;

import android.os.Parcel;

/**
 * Convenience class to handle nullable objects.
 *
 * @author John Ericksen
 */
public abstract class NullableParcelConverter<T> implements ParcelConverter<T> {

    private static final int NULL = -1;
    private static final int NOT_NULL = 1;

    @Override
    public void toParcel(T input, Parcel parcel) {
        if (input == null) {
            parcel.writeInt(NULL);
        } else {
            parcel.writeInt(NOT_NULL);
            nullSafeToParcel(input, parcel);
        }
    }

    public abstract void nullSafeToParcel(T input, Parcel parcel);

    @Override
    public T fromParcel(Parcel parcel) {
        T result;
        if (parcel .readInt() == NULL) {
            result = null;
        } else {
            result = nullSafeFromParcel(parcel);
        }
        return result;
    }

    public abstract T nullSafeFromParcel(Parcel parcel);
}
