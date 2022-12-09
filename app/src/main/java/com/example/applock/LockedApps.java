package com.example.applock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class LockedApps extends AppCompatActivity {

    RecyclerView recyclerView;

    List<AppModel> appModelList = new ArrayList<>();

    AppAdapter adapter;

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locked_apps);

        recyclerView = findViewById(R.id.lockedappslist);

        adapter = new AppAdapter(appModelList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.setAdapter(adapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setOnShowListener(dialog -> getLockedApps());
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressDialog.setTitle("Завантаження програм");
        progressDialog.setMessage("Завантаження");
        progressDialog.show();
    }

    public void getLockedApps(){

        List<String> list = SharedPrefUtil.getInstance(adapter.con).getListString();

        List<PackageInfo> packageInfos = getPackageManager().getInstalledPackages(0);

        for (int i = 0; i < packageInfos.size(); i++){
            String name = packageInfos.get(i).applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = packageInfos.get(i).applicationInfo.loadIcon(getPackageManager());
            String packname = packageInfos.get(i).packageName;

            if (list.contains(packname)){
                appModelList.add(new AppModel(name, icon, 1, packname));
            }
            else {
                continue;
            }

        }

        adapter.notifyDataSetChanged();
        progressDialog.dismiss();

    }

}