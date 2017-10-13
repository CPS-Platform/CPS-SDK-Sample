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

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.sample.Model;

import java.io.IOException;


public class EditNameActivity extends AppCompatActivity {

    public static Intent createActivity(Context context){
        return new Intent(context, EditNameActivity.class);
    }

    EditText etFirstName;
    EditText etLastName;

    Button btn_send;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_name);


        etFirstName = (EditText) findViewById(R.id.etFirstName);
        etLastName = (EditText) findViewById(R.id.etLastName);

        btn_send = (Button) findViewById(R.id.btnOk);

        btn_send.setOnClickListener(v -> new Thread(() -> {
            try {
                Model.getInstance().getCurrentUser().editName(etFirstName.getText().toString(), etLastName.getText().toString());
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Name edited successfully", Toast.LENGTH_LONG).show());
                finish();
            } catch (IOException e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Не удалось сменить имя :(",Toast.LENGTH_SHORT).show());

            }
        }).start());
    }
}
