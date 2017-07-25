/*
 * Copyright (c) Connectivity,  2017.
 *  This program is a free software: you can redistribute it and/or modify
 *   it under the terms of the Apache License, Version 2.0 (the "License");
 *
 *   You may obtain a copy of the Apache 2 License at
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   Apache 2 License for more details.
 */

package ru.ctvt.cps.sdk.sample.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.SDKManager;
import ru.ctvt.cps.sdk.model.User;
import ru.ctvt.cps.sdk.sample.Model;
import ru.ctvt.cps.sdk.sample.user.device.DevicesListActivity;
import ru.ctvt.cps.sdk.sample.keyValueStorage.KeyValueStorageViewerActivity;
import ru.ctvt.cps.sdk.sample.R;

import java.io.IOException;

import static ru.ctvt.cps.sdk.sample.auth.AccountControlActivity.ACCOUNT_CONTROL;

/**
 * Экран для работы с пользователем
 */

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textViewUserID;
    TextView textViewEMail;

    User user;

    Button btn_devices;
    Button btn_logout;
    Button btn_user_kvs;

    SharedPreferences mSharedPreferences;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (user == null)
            user = Model.getInstance().getCurrentUser();

        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Кабинет пользователя");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        textViewUserID = (TextView) findViewById(R.id.textview_user_id);
        textViewEMail = (TextView) findViewById(R.id.textview_email);

        btn_devices = (Button) findViewById(R.id.button_devices);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_user_kvs = (Button) findViewById(R.id.button_user_kvs);


        btn_devices.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_user_kvs.setOnClickListener(this);

        mSharedPreferences = getSharedPreferences(SDKManager.PreferencesNameConsts.FILE_NAME, Context.MODE_PRIVATE);

        if (mSharedPreferences.contains(SDKManager.PreferencesNameConsts.USER_ID))
            textViewUserID.setText("User ID: " + mSharedPreferences.getString(SDKManager.PreferencesNameConsts.USER_ID, ""));
        if (mSharedPreferences.contains(SDKManager.PreferencesNameConsts.LOGIN))
            textViewEMail.setText("Email: " + mSharedPreferences.getString(SDKManager.PreferencesNameConsts.LOGIN, ""));

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_devices:
                Intent deviceActivityIntent = new Intent(this, DevicesListActivity.class);
                startActivity(deviceActivityIntent);
                break;
            case R.id.btn_logout:
                logout();
                break;
            case R.id.button_user_kvs:
                startActivity(KeyValueStorageViewerActivity.createActivity(UserActivity.this, 1));
                break;
        }
    }


    void logout() {
        final ProgressDialog dialog = new ProgressDialog(UserActivity.this);
        dialog.setTitle("Выполнение запроса");
        dialog.setMessage("Подождите");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ACCOUNT_CONTROL.logout();
                } catch (BaseCpsException | IOException e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        finish();
                    }
                });
            }
        }).start();
    }
}



