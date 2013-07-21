package org.parceler;

/**
 * @author John Ericksen
 */
@Parcel(ManualConverter.class)
public class Manual {

    private String value;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
