package org.parceler.performance;

import android.os.Parcelable;
import auto.parcel.AutoParcel;

/**
 * @author John Ericksen
 */
@AutoParcel
public abstract class ImmutableAutoParcelDeveloper implements Parcelable {

    public abstract String name();
    public abstract int yearsOfExperience();
    //public abstract List<AutoParcelSkill> skillSet();
    public abstract float favoriteFloat();

    public static ImmutableAutoParcelDeveloper build(String name, int yearsOfExperience, /*List<AutoParcelSkill> skillSet, */float favoriteFloat){
        return new AutoParcel_ImmutableAutoParcelDeveloper(name, yearsOfExperience, favoriteFloat);
    }

    @AutoParcel
    public static abstract class AutoParcelSkill {
        public abstract String name();
        public abstract boolean programmingRelated();

        public static AutoParcelSkill build(String name, boolean programmingRelated){
            return new AutoParcel_ImmutableAutoParcelDeveloper_AutoParcelSkill(name, programmingRelated);
        }
    }
}
