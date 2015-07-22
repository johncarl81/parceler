package org.parceler;

/**
 * @author John Ericksen
 */
@Parcel
public class Enums {

    @Parcel
    public enum Values{
        A, B, C;
    }

    Values one;
    Values two;
    Values three;

}
