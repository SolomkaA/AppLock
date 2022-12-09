package com.example.applock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LockScreen extends AppCompatActivity {

    Context con = this;
    TextView packname;
    Button okbtn, cancbtn;
    EditText text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        Intent getIntnt = getIntent();
        String packExtra = getIntnt.getStringExtra("pack");
        packname = findViewById(R.id.packName);
        text = findViewById(R.id.editTextTextPassword);
        final PackageManager pm = getApplicationContext().getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo( packExtra, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        final String applicationName = (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");

        packname.setText(applicationName);

        cancbtn = findViewById(R.id.cancelbuttonls);
        cancbtn.setOnClickListener(v -> {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        });

        okbtn = findViewById(R.id.okbuttonls);
        okbtn.setOnClickListener(v -> {
            String enteredPassw = "";
            if (!text.getText().toString().equals("")) {
                enteredPassw = text.getText().toString();
            }

            if (AES.decrypt(SharedPrefUtil.getInstance(con).getString("pass"), enteredPassw).equals(enteredPassw)){
                finish();
            }
            else {
                Toast.makeText(con, "Невірний пароль!", Toast.LENGTH_LONG).show();
            }

        });

    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }
}