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

/**
 * @author John Ericksen
 */
@Parcel
public class ExampleParcel {
    private String one;
    private int two;
    private long three;
    private SubParcel four;

    public ExampleParcel(){
        //empty bean constructor
    }

    public ExampleParcel(String one, int two, long three, SubParcel four) {
        this.one = one;
        this.two = two;
        this.three = three;
        this.four = four;
    }

    public SubParcel getFour() {
        return four;
    }

    public void setFour(SubParcel four) {
        this.four = four;
    }

    public long getThree() {
        return three;
    }

    public void setThree(long three) {
        this.three = three;
    }

    public int getTwo() {
        return two;
    }

    public void setTwo(int two) {
        this.two = two;
    }

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }
}
