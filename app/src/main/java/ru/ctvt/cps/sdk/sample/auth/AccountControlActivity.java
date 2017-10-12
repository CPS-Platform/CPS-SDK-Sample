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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import ru.ctvt.cps.sample.R;
import ru.ctvt.cps.sdk.SDKManager;
import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.AccountControl;
import ru.ctvt.cps.sdk.sample.deviceRole.DeviceRoleRemoteActivity;
import ru.ctvt.cps.sdk.sample.user.UserActivity;

import java.io.IOException;


/**
 * Экран для управления аккаунтом
 */
public class AccountControlActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogIn;
    Button btnSignIn;
    Button btnResetPassword;
    Button btnLogout;
    SharedPreferences mSharedPreferences;

    public static final AccountControl ACCOUNT_CONTROL = AccountControl.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getSharedPreferences(SDKManager.PreferencesNameConsts.FILE_NAME, Context.MODE_PRIVATE);
        setContentView(R.layout.activity_account_control);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Работа с учетной записью");

        btnLogIn = (Button) findViewById(R.id.btn_login);
        btnSignIn = (Button) findViewById(R.id.btn_logup);
        btnResetPassword = (Button) findViewById(R.id.btn_restore_password);
        btnLogout = (Button) findViewById(R.id.btn_logout);

        btnLogIn.setOnClickListener(this);
        btnSignIn.setOnClickListener(this);
        btnResetPassword.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }

    public void onResume() {
        super.onResume();
        updateUI();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                if (ACCOUNT_CONTROL.isAuthorized() && mSharedPreferences.getString(SDKManager.PreferencesNameConsts.ROLE, "user").equals("user")) {
                    Intent intent = new Intent(AccountControlActivity.this, UserActivity.class);
                    startActivity(intent);
                    break;
                } else if (ACCOUNT_CONTROL.isAuthorized() && mSharedPreferences.getString(SDKManager.PreferencesNameConsts.ROLE, "user").equals("device")) {
                    Intent intent = new Intent(AccountControlActivity.this, DeviceRoleRemoteActivity.class);
                    startActivity(intent);
                    break;
                }
                Intent authorization = new Intent(AccountControlActivity.this, AuthActivity.class);
                startActivity(authorization);
                break;
            case R.id.btn_logup:
                ACCOUNT_CONTROL.withRole(AccountControl.Role.user);
                Intent intent = new Intent(AccountControlActivity.this, RegistrationActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_restore_password:
                ACCOUNT_CONTROL.withRole(AccountControl.Role.user);
                Intent rp_intent = new Intent(AccountControlActivity.this, ResetPasswActivity.class);
                startActivity(rp_intent);
                break;
            case R.id.btn_logout:
                logout();
                btnLogout.setEnabled(false);
                btnLogIn.setText("Вход");
                break;
        }

    }

    void logout() {
        //Если мы пользователь - нам нужно вылогиниться
        if (ACCOUNT_CONTROL.getRole().equals(AccountControl.Role.user)) {
            final ProgressDialog progress = new ProgressDialog(AccountControlActivity.this);
            progress.setTitle("Выполнение запроса");
            progress.setMessage("Пожалуйста, подождите");
            progress.show();
            new Thread(() -> {
                    try {
                        ACCOUNT_CONTROL.logout();
                        runOnUiThread(() -> progress.dismiss());
                    } catch (BaseCpsException | IOException e) {
                        runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
                    }
                }
            ).start();
        }
        //в любом случае чистим SharedPreferences
        mSharedPreferences.edit().putString(SDKManager.PreferencesNameConsts.AUTH_TOKEN, "").apply();
        mSharedPreferences.edit().putString(SDKManager.PreferencesNameConsts.ROLE, "").apply();
    }

    /*
     * проверяем, имеем ли мы активную сессию
     * и обновляем состояние кнопок в зависимости от наличия неразорванной сессии
     */
    void updateUI() {
        if (ACCOUNT_CONTROL.isAuthorized() && ACCOUNT_CONTROL.getRole() == AccountControl.Role.user) {   //отображаем активными только те кнопки, которые должны быть активными
            btnLogout.setEnabled(true);
            btnLogIn.setText("Кабинет");
            btnSignIn.setEnabled(false);
        } else if (ACCOUNT_CONTROL.isAuthorized() && ACCOUNT_CONTROL.getRole() == AccountControl.Role.device) {
            btnLogIn.setText("Устройство");
            btnSignIn.setVisibility(View.VISIBLE);
            btnResetPassword.setEnabled(false);
            btnSignIn.setEnabled(false);
            btnLogout.setEnabled(true);
        } else {
            btnLogout.setEnabled(false);
            btnLogIn.setText("Вход");
            btnSignIn.setVisibility(View.VISIBLE);
            btnSignIn.setEnabled(true);
        }
    }
}
