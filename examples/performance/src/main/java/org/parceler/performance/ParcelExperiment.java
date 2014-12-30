/**
 * Copyright 2013-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
