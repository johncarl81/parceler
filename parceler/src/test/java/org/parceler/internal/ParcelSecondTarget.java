package org.parceler.internal;

import org.parceler.Parcel;

/**
 * @author John Ericksen
 */
@Parcel
public class ParcelSecondTarget {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}