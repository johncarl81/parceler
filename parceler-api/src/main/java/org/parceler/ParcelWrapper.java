package org.parceler;

/**
 * Defines the getter for the contents of a wrapped `@Parcel` instance.
 *
 * @author John Ericksen
 */
public interface ParcelWrapper<T> {

    String GET_PARCEL = "getParcel";

    /**
     * Return wrapped `@Parcel` instance
     *
     * @return `@Parcel` instance
     */
    T getParcel();
}