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
