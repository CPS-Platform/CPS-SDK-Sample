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

package ru.ctvt.cps.sample.user.device;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cpsplatform.android.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sample.Model;
import ru.ctvt.cps.sample.R;

import java.io.IOException;


public class RenameDeviceActivity extends AppCompatActivity implements View.OnClickListener{

    public static Intent startActivity(Context context){
        return new Intent(context, RenameDeviceActivity.class);
    }

    Model model;
    Button btn;
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = Model.getInstance();
        setContentView(R.layout.activity_rename_device);

        et = (EditText) findViewById(R.id.etNewName);

        btn = (Button) findViewById(R.id.btnSend);
        btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        new Thread(() -> {
            try {
                model.getCurrentDevice().editDeviceName(et.getText().toString());
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "Имя устройства успешно изменено", Toast.LENGTH_SHORT).show();
                    finish();
                });
            } catch (IOException | BaseCpsException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), e.getLocalizedMessage(),Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
