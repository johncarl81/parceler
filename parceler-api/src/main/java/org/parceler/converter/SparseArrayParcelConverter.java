package org.parceler.converter;

import android.os.Parcel;
import android.util.SparseArray;
import org.parceler.ParcelConverter;

/**
 * @author John Ericksen
 */
public abstract class SparseArrayParcelConverter<T> implements ParcelConverter<SparseArray<T>> {
    @Override
    public void toParcel(SparseArray<T> input, Parcel parcel) {
        if (input == null) {
            parcel.writeInt(-1);
        } else {
            parcel.writeInt(input.size());
            for (int i = 0; (i < input.size()); i++) {
                parcel.writeInt(input.keyAt(i));
                itemToParcel(input.valueAt(i), parcel);
            }
        }
    }

    @Override
    public SparseArray<T> fromParcel(Parcel parcel) {
        SparseArray<T> array;
        int size = parcel.readInt();
        if (size < 0) {
            array = null;
        } else {
            array = new SparseArray<T>(size);
            for (int i = 0; (i < size); i++) {
                int key = parcel.readInt();
                array.append(key, itemFromParcel(parcel));
            }
        }
        return array;
    }

    public abstract void itemToParcel(T input, Parcel parcel);
    public abstract T itemFromParcel(Parcel parcel);
}
