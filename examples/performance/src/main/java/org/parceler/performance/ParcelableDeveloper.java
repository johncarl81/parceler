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

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class ParcelableDeveloper extends Developer implements Parcelable {

    public List<Skill> skillSet;

    public ParcelableDeveloper() {
    }

    public ParcelableDeveloper(Parcel in) {
        name = in.readString();
        yearsOfExperience = in.readInt();
        skillSet = new ArrayList();
        in.readTypedList(skillSet, Skill.CREATOR);
        favoriteFloat = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int f) {
        dest.writeString(name);
        dest.writeInt(yearsOfExperience);
        dest.writeTypedList(skillSet);
        dest.writeFloat(favoriteFloat);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public ParcelableDeveloper createFromParcel(Parcel in) {
            return new ParcelableDeveloper(in);
        }

        @Override
        public ParcelableDeveloper[] newArray(int size) {
            return new ParcelableDeveloper[size];
        }
    };

    public static class Skill extends Developer.Skill implements Parcelable {

        public Skill() {
        }

        public Skill(Parcel in) {
            name = in.readString();
            programmingRelated = (in.readInt() == 1);
        }

        @Override
        public void writeToParcel(Parcel dest, int f) {
            dest.writeString(name);
            dest.writeInt(programmingRelated ? 1 : 0);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator CREATOR = new Creator() {
            @Override
            public Skill createFromParcel(Parcel in) {
                return new Skill(in);
            }

            @Override
            public Skill[] newArray(int size) {
                return new Skill[size];
            }
        };
    }
}