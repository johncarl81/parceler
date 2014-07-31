package org.parceler.performance;

import android.content.Context;
import android.os.Parcel;
import android.widget.Toast;

/**
 * @author John Ericksen
 */
public class ParcelExperiment {

    private final ParcelMutator mutator;
    private final Context context;
    private final String name;

    private static final int ITERS = 10000;

    public ParcelExperiment(Context context, String name, ParcelMutator mutator) {
        this.mutator = mutator;
        this.name = name;
        this.context = context;
    }



    public void run(){
        Parcel parcel = Parcel.obtain();

        long start = System.nanoTime();
        for (int i = 0; i < ITERS; i++) {

            mutator.write(parcel);

            parcel.setDataPosition(0);

            mutator.read(parcel);
        }
        long total = System.nanoTime() - start;
        double timePer = 1.0 * total / ITERS;

        Toast.makeText(context, name + " Time: " + timePer, 1000).show();
    }
}
