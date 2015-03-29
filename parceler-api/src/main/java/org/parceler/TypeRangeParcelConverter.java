package org.parceler;

/**
 * @author John Ericksen
 */
public interface TypeRangeParcelConverter<L, U extends L> {

    /**
     * Write the given input parameter to the destinationParcel.
     *
     * @param input U instance
     * @param parcel Parcel to write to
     */
    void toParcel(L input, android.os.Parcel parcel);

    /**
     * Generates an instance from the values provided in the given parcel.
     *
     * @param parcel Parcel to read from
     * @return instance of the mapped class.
     */
    U fromParcel(android.os.Parcel parcel);
}
