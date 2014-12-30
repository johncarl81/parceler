/**
 * Copyright 2013-2015 John Ericksen
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

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import com.google.gson.Gson;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final int SKILL_SIZE = 30;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button submitView = (Button) findViewById(R.id.run1);
        submitView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final Developer developer = buildDeveloper();
                final ClassLoader classLoader = Developer.class.getClassLoader();

                new ParcelExperiment(MainActivity.this, "Parceler", new ParcelMutator() {
                    @Override
                    public void write(Parcel parcel) {
                        parcel.writeParcelable(Parcels.wrap(developer), 0);
                    }

                    @Override
                    public void read(Parcel parcel) {
                        Parcels.unwrap(parcel.readParcelable(classLoader));
                    }
                }).run();
            }
        });


        Button submitView2 = (Button) findViewById(R.id.run2);
        submitView2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final ParcelableDeveloper developer = buildParcelableDeveloper();
                final ClassLoader classLoader = Developer.class.getClassLoader();

                new ParcelExperiment(MainActivity.this, "Parcelable", new ParcelMutator() {
                    @Override
                    public void write(Parcel parcel) {
                        parcel.writeParcelable(developer, 0);
                    }

                    @Override
                    public void read(Parcel parcel) {
                        parcel.readParcelable(classLoader);
                    }
                }).run();
            }
        });


        Button submitView3 = (Button) findViewById(R.id.run3);
        submitView3.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final org.parceler.performance.Developer$$Parcelable developer = new org.parceler.performance.Developer$$Parcelable(buildDeveloper());
                final ClassLoader classLoader = Developer.class.getClassLoader();

                new ParcelExperiment(MainActivity.this, "Direct Parceler", new ParcelMutator() {
                    @Override
                    public void write(Parcel parcel) {
                        parcel.writeParcelable(developer, 0);
                    }

                    @Override
                    public void read(Parcel parcel) {
                        parcel.readParcelable(classLoader);
                    }
                }).run();
            }
        });


        Button submitView4 = (Button) findViewById(R.id.run4);
        submitView4.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final Developer inputDeveloper = buildDeveloper();
                final Gson gson = new Gson();

                new ParcelExperiment(MainActivity.this, "Gson", new ParcelMutator() {
                    @Override
                    public void write(Parcel parcel) {
                        parcel.writeString(gson.toJson(inputDeveloper));
                    }

                    @Override
                    public void read(Parcel parcel) {
                        parcel.readString();
                    }
                }).run();
            }
        });


        Button submitView5 = (Button) findViewById(R.id.run5);
        submitView5.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                final Developer inputDeveloper = buildDeveloper();

                new ParcelExperiment(MainActivity.this, "Serializable", new ParcelMutator() {
                    @Override
                    public void write(Parcel parcel) {
                        parcel.writeSerializable(inputDeveloper);
                    }

                    @Override
                    public void read(Parcel parcel) {
                        parcel.readSerializable();
                    }
                }).run();
            }
        });
    }

    private Developer buildDeveloper(){
        Developer developer = new Developer();
        developer.favoriteFloat = 32.32f;
        developer.name = "test";
        developer.yearsOfExperience = 42;
        List<Developer.Skill> skills = new ArrayList<Developer.Skill>();
        for(int i = 0; i < SKILL_SIZE; i++){
            Developer.Skill skill = new Developer.Skill();
            skill.name = "skill";
            skill.programmingRelated = true;
            skills.add(skill);
        }
        developer.skillSet = skills;
        return developer;
    }

    private ParcelableDeveloper buildParcelableDeveloper(){
        ParcelableDeveloper developer = new ParcelableDeveloper();
        developer.favoriteFloat = 32.32f;
        developer.name = "test";
        developer.yearsOfExperience = 42;
        List<ParcelableDeveloper.Skill> skills = new ArrayList<ParcelableDeveloper.Skill>();
        for(int i = 0; i < SKILL_SIZE; i++){
            ParcelableDeveloper.Skill skill = new ParcelableDeveloper.Skill();
            skill.name = "skill";
            skill.programmingRelated = true;
            skills.add(skill);
        }
        developer.skillSet = skills;
        return developer;
    }
}