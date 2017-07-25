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

package ru.ctvt.cps.sdk.sample.user.device;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.UserDevice;
import ru.ctvt.cps.sdk.sample.Model;
import ru.ctvt.cps.sdk.sample.R;

import java.io.IOException;


/**
 * Экран приаязки реального устройства к платформе
 */

public class SetDeviceCodeActivity extends AppCompatActivity implements View.OnClickListener {

    TextView tv;

    Button setCode;

    EditText edtCode;

    String message;

    UserDevice currentDevice;

    public static Intent setDeviceCodeIntent(Context context) {
        return new Intent(context, SetDeviceCodeActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_device_code);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Привязка устройства");
        toolbar.setSubtitle("Укажите код привязки");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tv = (TextView) findViewById(R.id.tvCode);

        setCode = (Button) findViewById(R.id.btnSetCode);
        edtCode = (EditText) findViewById(R.id.edtCode);

        setCode.setOnClickListener(this);

        currentDevice = (UserDevice) Model.getInstance().getCurrentDevice();

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }


    void setCodeForDevice() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String code = edtCode.getText().toString();
                    final String codeText = "Введенный код: " + code + "\n";
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(codeText);
                        }
                    });
                    if (!code.isEmpty()) {
                        currentDevice.setDeviceCode(code);
                        message = "Устройство успешно привязано!";
                    } else {
                        message = "Код устройства не введен";
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(codeText + "\n" + message);
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
    }

    @Override
    public void onClick(View view) {
        setCodeForDevice();
    }
}
