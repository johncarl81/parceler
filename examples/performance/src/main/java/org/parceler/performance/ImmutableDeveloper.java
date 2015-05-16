/**
 * Copyright 2011-2015 John Ericksen
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

import com.google.auto.value.AutoValue;
import org.parceler.Parcel;
import org.parceler.ParcelFactory;
import org.parceler.ParcelProperty;

import java.util.List;

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
    @ParcelProperty("skillSet")
    public abstract List<Skill> skillSet();
    @ParcelProperty("favoriteFloat")
    public abstract float favoriteFloat();

    @ParcelFactory
    public static ImmutableDeveloper build(String name, int yearsOfExperience, List<Skill> skillSet, float favoriteFloat){
        return new AutoValue_ImmutableDeveloper(name, yearsOfExperience,  skillSet, favoriteFloat);
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
