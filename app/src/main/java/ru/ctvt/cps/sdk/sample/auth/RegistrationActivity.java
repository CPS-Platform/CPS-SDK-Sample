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
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;

import java.io.IOException;

/**
 * Экран регистрации
 */
public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edit_email;
    EditText edit_password;
    EditText edit_confirm_password;

    Button btn_ok;
    Button btn_cancel;

    private String email = "";
    private String password = "";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Регистрация");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edit_email = (EditText) findViewById(R.id.edittext_email);
        edit_password = (EditText) findViewById(R.id.edittext_password);
        edit_confirm_password = (EditText) findViewById(R.id.edittext_confirmpassword);

        btn_ok = (Button) findViewById(R.id.button_ok);
        btn_cancel = (Button) findViewById(R.id.button_cancel);


        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

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
            case R.id.button_ok:
                if ((edit_password.getText().toString().equals(edit_confirm_password.getText().toString()))//проверка на совпадение пароля и подтверждения
                        &&
                        (!edit_email.getText().toString().isEmpty()
                                || !edit_password.getText().toString().isEmpty()
                                || !edit_confirm_password.getText().toString().isEmpty())
                        &&
                        (edit_password.getText().length() >= 6))//проверка на количество символов в поле ввода пароля
                {


                    email = edit_email.getText().toString();
                    password = edit_password.getText().toString();
                    register();

                }
                break;
            case R.id.button_cancel:
                finish();
                break;
        }
    }

    void register() {
        final ProgressDialog dialog;
        dialog = new ProgressDialog(RegistrationActivity.this);
        dialog.setTitle("Выполнение запроса");
        dialog.setMessage("Подождите");
        dialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AccountControlActivity.ACCOUNT_CONTROL.register(email, password, getResources().getString(R.string.service_id));
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
                } catch (BaseCpsException | IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.dismiss();
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }

}
