package com.sdirin.java.newstracker.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.sdirin.java.newstracker.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LicenseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_license);

        setLicenceText();

    }

    private void setLicenceText() {
        TextView licence = (TextView) findViewById(R.id.licence);

        try {
            InputStream inputStream = getAssets().open("license.txt");

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer sb = new StringBuffer();
            String line = reader.readLine();
            while (line!=null){
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
            licence.setText(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
