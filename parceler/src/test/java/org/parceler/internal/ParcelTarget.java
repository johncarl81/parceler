package org.parceler.internal;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.parceler.Parcel;

/**
 * @author John Ericksen
 */
@Parcel
public class ParcelTarget {
    private String stringValue;
    private Double doubleValue;
    private ParcelSecondTarget secondTarget;

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Double getDoubleValue() {
        return doubleValue;
    }

    public void setDoubleValue(Double doubleValue) {
        this.doubleValue = doubleValue;
    }

    public ParcelSecondTarget getSecondTarget() {
        return secondTarget;
    }

    public void setSecondTarget(ParcelSecondTarget secondTarget) {
        this.secondTarget = secondTarget;
    }

    @Override
    public boolean equals(Object rhs) {
        return EqualsBuilder.reflectionEquals(this, rhs);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
