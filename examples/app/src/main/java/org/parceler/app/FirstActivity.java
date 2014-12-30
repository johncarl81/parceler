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
package org.parceler.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import org.demo.ExampleParcel;

import java.lang.reflect.Field;

public class FirstActivity extends Activity implements OnClickListener {
    private TextView messageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        messageView = (TextView) findViewById(R.id.message);
        Button submitView = (Button) findViewById(R.id.submit);
        submitView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String message = messageView.getText().toString();
        ExampleParcel exampleParcel = ExampleParcel.create(message);

        for (Field field : ExampleParcel.class.getDeclaredFields()) {
            System.out.println(field.getName());
        }

        Intent intent = SecondActivity.buildIntent(this, exampleParcel);
        startActivity(intent);
    }
}