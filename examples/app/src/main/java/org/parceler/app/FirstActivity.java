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
        ExampleParcel exampleParcel = new ExampleParcel();
        exampleParcel.setMessage(message);

        for (Field field : ExampleParcel.class.getDeclaredFields()) {
            System.out.println(field.getName());
        }

        Intent intent = SecondActivity.buildIntent(this, exampleParcel);
        startActivity(intent);
    }
}