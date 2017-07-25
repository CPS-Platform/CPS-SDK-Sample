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

package ru.ctvt.cps.sdk.sample.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
//import ru.ctvt.cps.sdk.model.AccountControl;
//import ru.ctvt.cps.sdk.model.User;
import ru.ctvt.cps.sdk.sample.Model;
import ru.ctvt.cps.sdk.sample.deviceRole.DeviceRoleEntryAvtivity;
import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.sample.user.UserActivity;

import java.io.IOException;

import static ru.ctvt.cps.sdk.sample.auth.AccountControlActivity.ACCOUNT_CONTROL;

/**
 * Экран аутентификации
 */
public class AuthActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String SERVICE_ID = "d8cb6994-ad65-46f9-a970-cf1cdccaf4d1"; //идентификатор сервиса
    //private static final String SERVICE_ID = "8fa56e60-89e4-4f43-b9ef-6c00ebf6813d"; //временный

    EditText edit_email;
    EditText edit_password;

    private String email = "";
    private String password = "";

    Button btn_login;
    Button btn_workAsDevice;

    ProgressDialog progress;
    User user;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle("Авторизация");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        btn_login = (Button) findViewById(R.id.btn_login);
        btn_workAsDevice = (Button) findViewById(R.id.button_workAsDevice);


        edit_email = (EditText) findViewById(R.id.edittext_email);
        edit_password = (EditText) findViewById(R.id.edittext_password);


        btn_login.setOnClickListener(this);
        btn_workAsDevice.setOnClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    private void auth() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ACCOUNT_CONTROL.withRole(AccountControl.Role.user);
                try {
                    user = ACCOUNT_CONTROL.login(email, password, SERVICE_ID);
                    Model.getInstance().setCurrentUser(user);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            finish();
                            Intent intent = new Intent(AuthActivity.this, UserActivity.class);
                            startActivity(intent);
                        }
                    });
                } catch (final BaseCpsException | IOException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progress.dismiss();
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (!edit_email.getText().toString().isEmpty() || !edit_password.getText().toString().isEmpty()) {
                    email = edit_email.getText().toString();
                    password = edit_password.getText().toString();
                    progress = new ProgressDialog(AuthActivity.this);
                    progress.setTitle("Отправка запроса");
                    progress.setMessage("Пожалуйста, подождите");
                    progress.show();
                    auth();
                }
                break;
            case R.id.button_workAsDevice:
                Intent intent = new Intent(AuthActivity.this, DeviceRoleEntryAvtivity.class);
                startActivity(intent);
                finish();
                break;
        }
    }
}
