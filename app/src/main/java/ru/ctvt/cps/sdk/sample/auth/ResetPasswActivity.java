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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.sample.R;

import java.io.IOException;

/**
 * Экран восстановления пароля
 */

public class ResetPasswActivity extends AppCompatActivity implements View.OnClickListener {
    EditText edit_email;

    Button btn_ok;
    Button btn_cancel;

    private String email = "";


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_restorepassword);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Восстановление пароля");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        edit_email = (EditText) findViewById(R.id.edittext_email);

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
                if (!edit_email.getText().toString().isEmpty()) {
                    email = edit_email.getText().toString();
                    resetPass();

                }
                break;
            case R.id.button_cancel:
                finish();
                break;
        }
    }

    void resetPass() {
        final ProgressDialog dialog;

        dialog = new ProgressDialog(ResetPasswActivity.this);
        dialog.setTitle("Выполнение запроса");
        dialog.setMessage("Подождите");
        dialog.show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AccountControlActivity.ACCOUNT_CONTROL.recoverPassword(email, "d8cb6994-ad65-46f9-a970-cf1cdccaf4d1");
                    dialog.dismiss();
                } catch (BaseCpsException | IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }
                    });
                }
            }
        }).start();
    }
}
