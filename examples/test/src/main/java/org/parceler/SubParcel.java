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

/**
 * @author John Ericksen
 */
@Parcel(Parcel.Serialization.BEAN)
public class SubParcel implements Comparable<SubParcel> {

    private String name;

    public SubParcel(){
        //empty bean constructor
    }

    public SubParcel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof SubParcel)){
            return false;
        }

        SubParcel subParcel = (SubParcel) o;

        return !(name != null ? !name.equals(subParcel.name) : subParcel.name != null);

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }


    @Override
    public int compareTo(SubParcel that) {
        return name.compareTo(that.name);
    }
}
