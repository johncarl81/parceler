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

import android.os.Parcelable;
import auto.parcel.AutoParcel;

import java.util.List;

/**
 * @author John Ericksen
 */
@AutoParcel
public abstract class ImmutableAutoParcelDeveloper implements Parcelable {

    public abstract String name();
    public abstract int yearsOfExperience();
    public abstract List<AutoParcelSkill> skillSet();
    public abstract float favoriteFloat();

    public static ImmutableAutoParcelDeveloper build(String name, int yearsOfExperience, List<AutoParcelSkill> skillSet, float favoriteFloat){
        return new AutoParcel_ImmutableAutoParcelDeveloper(name, yearsOfExperience, skillSet, favoriteFloat);
    }

    @AutoParcel
    public static abstract class AutoParcelSkill implements Parcelable {
        public abstract String name();
        public abstract boolean programmingRelated();

        public static AutoParcelSkill build(String name, boolean programmingRelated){
            return new AutoParcel_ImmutableAutoParcelDeveloper_AutoParcelSkill(name, programmingRelated);
        }
    }
}
