package com.example.applock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.os.Build.VERSION.SDK_INT;

public class MainActivity extends AppCompatActivity {

    Button listOfApps, usagePerm, pass, lockedappsbtn, btnStartService;

    String passw;

    static final String KEY = "pass";

    final Context con = this;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        passw = SharedPrefUtil.getInstance(this).getString(KEY);
        btnStartService = findViewById(R.id.buttonStartService);

        btnStartService.setOnClickListener(v -> startService());

        listOfApps = findViewById(R.id.listOfApps);
        listOfApps.setOnClickListener(v -> {
            if (isAccessGranted()) {
                if (!passw.isEmpty()) {
                    startActivity(new Intent(MainActivity.this, InsalledApps.class));
                } else {
                    Toast.makeText(con, "Спершу треба створити пароль", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Спершу ви маєте надати дозвіл!", (Toast.LENGTH_LONG)).show();
            }

        });

        lockedappsbtn = findViewById(R.id.lockedappsbtn);
        lockedappsbtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LockedApps.class)));

        usagePerm = findViewById(R.id.usagePerm);
        usagePerm.setOnClickListener(v -> {
            Intent intent = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            }
            startActivity(intent);
        });

        pass = findViewById(R.id.pass);

        if (passw.isEmpty()) {
            pass.setText("Встановити пароль");
        } else {
            pass.setText("Оновити пароль");
        }

        pass.setOnClickListener(v -> {

            if (passw.isEmpty()) {
                setPassword(con);
            } else {
                updatePassword(con);
            }

        });

    }

    public void startService() {
        Intent serviceIntent = new Intent(this, ForegroundService.class);
        serviceIntent.putExtra("inputExtra", "Виконується захист ваших додатків");
        ContextCompat.startForegroundService(this, serviceIntent);
    }


    private void setPassword(Context con) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(con);
        LinearLayout ll = new LinearLayout(con);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView t1 = new TextView(con);
        t1.setText("Введіть пароль");

        ll.addView(t1);

        EditText input = new EditText(con);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ll.addView(input);

        dialog.setView(ll);

        dialog.setPositiveButton("OK", (dialog1, which) -> {
            String aespassw = AES.encrypt(input.getText().toString(), input.getText().toString());
            SharedPrefUtil.getInstance(con).putString(KEY, aespassw);
            Toast.makeText(con, "Пароль встановлено!", Toast.LENGTH_LONG).show();
        }).setNegativeButton("Скасування", (dialog12, which) -> dialog12.dismiss());
        dialog.show();
    }

    private void updatePassword(Context con) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(con);
        LinearLayout ll = new LinearLayout(con);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView t1 = new TextView(con);
        t1.setText("Введіть старий пароль");

        ll.addView(t1);

        EditText input = new EditText(con);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ll.addView(input);

        TextView t2 = new TextView(con);
        t2.setText("Введіть новий пароль");

        ll.addView(t2);

        EditText input2 = new EditText(con);
        input2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        ll.addView(input2);

        dialog.setView(ll);

        dialog.setPositiveButton("OK", (dialog1, which) -> {
            String encpassw = SharedPrefUtil.getInstance(con).getString(KEY);
            String decrpassw = AES.decrypt(encpassw, input.getText().toString());
            if (decrpassw.equals(input.getText().toString())) {
                String aespassw = AES.encrypt(input2.getText().toString(), input2.getText().toString());
                SharedPrefUtil.getInstance(con).putString(KEY, aespassw);
                Toast.makeText(con, "Новий пароль встановлено!", Toast.LENGTH_LONG).show();

            } else {
                Toast.makeText(con, "Невірний пароль", Toast.LENGTH_LONG).show();
            }

            //зберігання паролю
        }).setNegativeButton("Скасування", (dialog12, which) -> dialog12.dismiss());
        dialog.show();
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = null;
            if (SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            }
            int mode = 0;
            if (SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }


}