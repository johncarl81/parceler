package org.parceler;

import org.parceler.Parcels;

public class RepositoryUpdater {

    public static void updateParcels(ClassLoader classLoader){
        Parcels.update(classLoader);
    }
}