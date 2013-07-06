package org.parceler;

import android.os.Parcelable;

/**
 * @author John Ericksen
 */
public class Tester {

    public static void main(String[] args){

        ExampleParcel example = new ExampleParcel("one", 2, 3L, new SubParcel("name"));

        Parcelable parcelable = Parcels.wrap(example);

        ExampleParcel parcel = ((ParcelWrapper<ExampleParcel>) parcelable).getParcel();

        System.out.println("Should be one: " + parcel.getOne());
        System.out.println("Should be 2: " + parcel.getTwo());
        System.out.println("Should be 3: " + parcel.getThree());
        System.out.println("Should be name: " + parcel.getFour().getName());
    }
}
