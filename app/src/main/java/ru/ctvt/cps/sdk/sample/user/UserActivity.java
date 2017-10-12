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

package ru.ctvt.cps.sample.user;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cpsplatform.android.sdk.errorprocessing.BaseCpsException;
import com.cpsplatform.android.sdk.SDKManager;
import com.cpsplatform.android.sdk.model.AccountControl;
import com.cpsplatform.android.sdk.model.User;
import ru.ctvt.cps.sample.Model;
import ru.ctvt.cps.sample.user.device.DevicesListActivity;
import ru.ctvt.cps.sample.keyValueStorage.KeyValueStorageViewerActivity;
import ru.ctvt.cps.sample.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import static ru.ctvt.cps.sample.auth.AccountControlActivity.ACCOUNT_CONTROL;

/**
 * Экран для работы с пользователем
 */

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    TextView textViewUserID;
    TextView textViewEMail;
    TextView textViewName;

    User user;

    Button btn_devices;
    Button btn_logout;
    Button btn_user_kvs;
    Button btn_edit_name;
    Button button_user_avatar;

    ImageView iv_logo;
    ImageView iv_avatar;

    SharedPreferences mSharedPreferences;

    private static final int RC_SELECT_IMAGE = 111;

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
        textViewName = (TextView) findViewById(R.id.textview_name);

        btn_devices = (Button) findViewById(R.id.button_devices);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_user_kvs = (Button) findViewById(R.id.button_user_kvs);
        btn_edit_name = (Button) findViewById(R.id.button_edit_name);
        button_user_avatar = (Button) findViewById(R.id.btn_user_avatar);

        iv_logo = (ImageView) findViewById(R.id.iv_service_logo);
        iv_avatar = (ImageView) findViewById(R.id.iv_user_avatar);

        btn_devices.setOnClickListener(this);
        btn_logout.setOnClickListener(this);
        btn_user_kvs.setOnClickListener(this);
        btn_edit_name.setOnClickListener(this);
        button_user_avatar.setOnClickListener(this);

        new Thread(() -> {
            try {
                Bitmap bmp = AccountControl.getInstance().getServiceLogo(getResources().getString(R.string.service_id));
                runOnUiThread(() -> iv_logo.setImageBitmap(bmp));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (BaseCpsException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try {
                user.fetchAvatar();
                runOnUiThread(() -> iv_avatar.setImageBitmap(user.getAvatar()));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (BaseCpsException e) {
                e.printStackTrace();
            }
        }).start();

        mSharedPreferences = getSharedPreferences(SDKManager.PreferencesNameConsts.FILE_NAME, Context.MODE_PRIVATE);

        if (mSharedPreferences.contains(SDKManager.PreferencesNameConsts.USER_ID))
            textViewUserID.setText("User ID: " + mSharedPreferences.getString(SDKManager.PreferencesNameConsts.USER_ID, ""));
        if (mSharedPreferences.contains(SDKManager.PreferencesNameConsts.LOGIN))
            textViewEMail.setText("Email: " + mSharedPreferences.getString(SDKManager.PreferencesNameConsts.LOGIN, ""));
        if (mSharedPreferences.contains(SDKManager.PreferencesNameConsts.FIRST_NAME))
            textViewName.setText(mSharedPreferences.getString(SDKManager.PreferencesNameConsts.FIRST_NAME, "") + " " +
            mSharedPreferences.getString(SDKManager.PreferencesNameConsts.LAST_NAME, ""));



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        textViewName.setText(mSharedPreferences.getString(SDKManager.PreferencesNameConsts.FIRST_NAME, "") + " " +
        mSharedPreferences.getString(SDKManager.PreferencesNameConsts.LAST_NAME, ""));
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
            case R.id.button_edit_name:
                startActivity(EditNameActivity.createActivity(this));
                break;
            case R.id.btn_user_avatar:
                Intent intent = new Intent();
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                } else {
                    intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
                }
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");

                this.startActivityForResult(intent, RC_SELECT_IMAGE);
                break;
        }
    }


    void logout() {
        final ProgressDialog dialog = new ProgressDialog(UserActivity.this);
        dialog.setTitle("Выполнение запроса");
        dialog.setMessage("Подождите");
        dialog.show();
        new Thread(() -> {
            try {
                ACCOUNT_CONTROL.logout();
            } catch (BaseCpsException | IOException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            runOnUiThread(() -> {
                dialog.dismiss();
                finish();
            });
        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null) {
            if (requestCode == RC_SELECT_IMAGE && resultCode == -1) {
                Uri selectedImageURI = data.getData();
                //File imageFile = new File(getRealPathFromURI(selectedImageURI));

                new Thread(() -> {
                    try {
                        user.setAvatar(selectedImageURI,UserActivity.this);
                        user.fetchAvatar();
                        runOnUiThread(() -> iv_avatar.setImageBitmap(user.getAvatar()));

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (BaseCpsException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        }
    }
    /*private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }*/
}



