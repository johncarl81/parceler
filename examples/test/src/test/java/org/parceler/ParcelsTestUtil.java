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

import android.os.Parcelable;

import java.lang.reflect.Field;

/**
 * @author John Ericksen
 */
public class ParcelsTestUtil {

    public static Parcelable wrap(Object input) {
        if(input == null){
            return wrap(null, null);
        }
        return wrap(input.getClass(), input);
    }

    public static <T> Parcelable wrap(Class<? extends T> inputType, T input){
        try{
            android.os.Parcel parcel = android.os.Parcel.obtain();

            Parcelable parcelable = Parcels.wrap(inputType, input);

            if (parcelable != null) {
                parcelable.writeToParcel(parcel, 0);
            }
            else {
                return null;
            }
            parcel.setDataPosition(0);

            Field creatorField = parcelable.getClass().getField("CREATOR");

            return (Parcelable) ((Parcelable.Creator)creatorField.get(parcelable)).createFromParcel(parcel);
        } catch (IllegalAccessException e) {
            throw new ParcelerRuntimeException("IllegalAccessException", e);
        } catch (NoSuchFieldException e) {
            throw new ParcelerRuntimeException("NoSuchFieldException", e);
        }
    }
}
