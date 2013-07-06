package org.parceler;

/**
 * @author John Ericksen
 */
@Parcel
public class SubParcel {

    private String name;

    public SubParcel(){
        //empty bean constructor
    }

    public SubParcel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
