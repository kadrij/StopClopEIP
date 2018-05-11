package com.eip.stopclopeip;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class CategoryActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        ImageView mImage = findViewById(R.id.categorie_icon);
        TextView mDesc = findViewById(R.id.categorie_desc);
        switch (getIntent().getStringExtra("category")) {
            case "Zen":
                mImage.setImageResource(R.drawable.zen_icon);
                mDesc.setText(R.string.zen_desc);
                break;
            case "Sport":
                mImage.setImageResource(R.drawable.sport_icon);
                mDesc.setText(R.string.sport_desc);
                break;
            default:
                mImage.setImageResource(R.drawable.other_icon);
                mDesc.setText(R.string.other_desc);
                break;
        }
    }
}
