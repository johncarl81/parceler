package org.parceler;

/**
 * @author John Ericksen
 */
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubParcel)) return false;

        SubParcel subParcel = (SubParcel) o;

        if (name != null ? !name.equals(subParcel.name) : subParcel.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
