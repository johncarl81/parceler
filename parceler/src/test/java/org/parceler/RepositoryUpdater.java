package org.parceler;

public class RepositoryUpdater {

    public static void updateParcels(ClassLoader classLoader){
        Parcels.update(classLoader);
    }
}