package org.parceler.performance;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.List;

/**
 * @author John Ericksen
 */
@Parcel
public class Developer implements Serializable {
    public String name;
    public int yearsOfExperience;
    public List<Skill> skillSet;
    public float favoriteFloat;

    @Parcel
    public static class Skill implements Serializable {
        public String name;
        public boolean programmingRelated;
    }
}
