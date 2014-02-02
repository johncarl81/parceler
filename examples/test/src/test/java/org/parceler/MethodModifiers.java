/**
 * Copyright 2013 John Ericksen
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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

@Parcel(Parcel.Serialization.METHOD)
public class MethodModifiers {
    private String one;
    private String two;
    private String three;
    private String four;

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

    String getTwo() {
        return two;
    }

    void setTwo(String two) {
        this.two = two;
    }

    protected String getThree() {
        return three;
    }

    protected void setThree(String three) {
        this.three = three;
    }

    private String getFour() {
        return four;
    }

    private void setFour(String four) {
        this.four = four;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}