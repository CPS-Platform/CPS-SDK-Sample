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

package ru.ctvt.cps.sdk.sample.deviceRole;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.AccountControl;
import ru.ctvt.cps.sdk.model.RecorderDevice;
import ru.ctvt.cps.sdk.sample.Model;

import java.io.IOException;

import static ru.ctvt.cps.sdk.sample.deviceRole.DeviceRoleRemoteActivity.startDeviceRemoteActivity;

/**
 * Экран начала работы в режиме реального устройства
 */

public class DeviceRoleEntryActivity extends AppCompatActivity implements View.OnClickListener {

    AccountControl acc = null;
    RecorderDevice currentDevice;

    TextView tvInfo;
    Button btn_getCode;
    Button btn_getToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_as_device_start);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        toolbar.setTitle("Привязка устройства");
        toolbar.setSubtitle("Получение кода и токена");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tvInfo = (TextView) findViewById(R.id.tvWADinfo);
        btn_getCode = (Button) findViewById(R.id.btnGetCode);
        btn_getToken = (Button) findViewById(R.id.btnGetToken);

        btn_getCode.setOnClickListener(this);
        btn_getToken.setOnClickListener(this);
        acc = AccountControl.getInstance().withRole(AccountControl.Role.device);
        try {
            Model.getInstance().setCurrentDevice(acc.instantiateDeviceRecorder(getResources().getString(R.string.service_id)));
        } catch (BaseCpsException e) {
            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        currentDevice = (RecorderDevice) Model.getInstance().getCurrentDevice();

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
            case R.id.btnGetCode:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            currentDevice.fetchRegistrationCode();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvInfo.setText("Код: " + currentDevice.getRegistrationCode() + "\n Введите полученный код в приложении");
                                }
                            });
                        } catch (IOException | BaseCpsException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
                break;
            case R.id.btnGetToken:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            currentDevice.fetchAccessToken();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Устройство успешно привязано!", Toast.LENGTH_SHORT).show();
                                    startActivity(startDeviceRemoteActivity(DeviceRoleEntryActivity.this));
                                    finish();
                                }
                            });
                        } catch (IOException | BaseCpsException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                }).start();
                break;
        }
    }
}
