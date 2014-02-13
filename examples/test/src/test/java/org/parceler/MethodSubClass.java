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

import org.parceler.sub.SuperClass;

/**
 * @author John Ericksen
 */
@Parcel(Parcel.Serialization.METHOD)
public class MethodSubClass extends SuperClass {

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

    @Transient
    public String getSubOne() {
        return one;
    }

    public void setSubOne(String one) {
        this.one = one;
    }

    @Transient
    public String getSubTwo() {
        return two;
    }

    public void setSubTwo(String two) {
        this.two = two;
    }

    @Transient
    public String getSubThree() {
        return three;
    }

    public void setSubThree(String three) {
        this.three = three;
    }

    @Transient
    public String getSubFour() {
        return four;
    }

    public void setSubFour(String four) {
        this.four = four;
    }
}
