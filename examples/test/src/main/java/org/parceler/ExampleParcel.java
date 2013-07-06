package org.parceler;

/**
 * @author John Ericksen
 */
@Parcel
public class ExampleParcel {
    private String one;
    private int two;
    private long three;
    private SubParcel four;

    public ExampleParcel(){
        //empty bean constructor
    }

    public ExampleParcel(String one, int two, long three, SubParcel four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public SubParcel getFour() {
        return four;
    }

    public void setFour(SubParcel four) {
        this.four = four;
    }

    public long getThree() {
        return three;
    }

    public void setThree(long three) {
        this.three = three;
    }

    public int getTwo() {
        return two;
    }

    public void setTwo(int two) {
        this.two = two;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }
}
