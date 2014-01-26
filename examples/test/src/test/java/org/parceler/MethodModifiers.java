package org.parceler;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Parcel(Parcel.Serialization.METHOD)
public class MethodModifiers {
    private String one;
    private String two;
    private String three;
    private String four;

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    String getTwo() {
        return two;
    }

    void setTwo(String two) {
        this.two = two;
    }

    protected String getThree() {
        return three;
    }

    protected void setThree(String three) {
        this.three = three;
    }

    private String getFour() {
        return four;
    }

    private void setFour(String four) {
        this.four = four;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}