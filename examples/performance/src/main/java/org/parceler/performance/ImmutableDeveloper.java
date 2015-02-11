package org.parceler.performance;

import com.google.auto.value.AutoValue;
import org.parceler.Parcel;
import org.parceler.ParcelFactory;
import org.parceler.ParcelProperty;

/**
 * @author John Ericksen
 */
@Parcel
@AutoValue
public abstract class ImmutableDeveloper {

    @ParcelProperty("name")
    public abstract String name();
    @ParcelProperty("yearsOfExperience")
    public abstract int yearsOfExperience();
    //@ParcelProperty("skillSet")
    //public abstract List<Skill> skillSet();
    @ParcelProperty("favoriteFloat")
    public abstract float favoriteFloat();

    @ParcelFactory
    public static ImmutableDeveloper build(String name, int yearsOfExperience,/* List<Skill> skillSet,*/ float favoriteFloat){
        return new AutoValue_ImmutableDeveloper(name, yearsOfExperience,  /*skillSet, */favoriteFloat);
    }

    @Parcel
    @AutoValue
    public static abstract class Skill {
        @ParcelProperty("name")
        public abstract String getName();
        @ParcelProperty("programmingRelated")
        public abstract boolean getProgrammingRelated();

        @ParcelFactory
        public static Skill build(String name, boolean programmingRelated){
            return new AutoValue_ImmutableDeveloper_Skill(name, programmingRelated);
        }
    }
}
