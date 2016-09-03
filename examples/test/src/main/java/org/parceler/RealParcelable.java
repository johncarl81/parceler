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
package org.parceler;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author John Ericksen
 */
public class RealParcelable implements Parcelable {

    private String value;

    public RealParcelable(String value) {
        this.value = value;
    }

    public RealParcelable(Parcel parcel){
        this(parcel.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(value);
    }

    public static final Creator<RealParcelable> CREATOR = new Creator<RealParcelable>() {
        @Override
        public RealParcelable createFromParcel(Parcel in) {
            return new RealParcelable(in);
        }

        @Override
        public RealParcelable[] newArray(int size) {
            return new RealParcelable[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RealParcelable)) return false;

        RealParcelable that = (RealParcelable) o;

        return value != null ? value.equals(that.value) : that.value == null;

    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }
}
