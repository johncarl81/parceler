package org.parceler.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import org.demo.ExampleParcel;
import org.parceler.Parcels;

public class SecondActivity extends Activity {
    private static final String EXTRA_EXAMPLE_PARCEL = "example parcel";

    public static Intent buildIntent(Context context, ExampleParcel exampleParcel) {
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra(EXTRA_EXAMPLE_PARCEL, Parcels.wrap(exampleParcel));
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ExampleParcel exampleParcel = Parcels.unwrap(getIntent().getParcelableExtra(EXTRA_EXAMPLE_PARCEL));

        setContentView(R.layout.activity_second);
        TextView messageView = (TextView) findViewById(R.id.message);
        messageView.setText(exampleParcel.getMessage());
    }
}