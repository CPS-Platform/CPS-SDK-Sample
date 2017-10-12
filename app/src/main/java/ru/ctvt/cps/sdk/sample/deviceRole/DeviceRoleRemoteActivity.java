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

package ru.ctvt.cps.sample.deviceRole;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.cpsplatform.android.sdk.model.AccountControl;
import ru.ctvt.cps.sample.commandQueue.CommandQueuesActivity;
import ru.ctvt.cps.sample.keyValueStorage.KeyValueStorageViewerActivity;
import ru.ctvt.cps.sample.R;
import ru.ctvt.cps.sample.sequence.SequencesActivity;

import static ru.ctvt.cps.sample.auth.AccountControlActivity.ACCOUNT_CONTROL;

/**
 * Экран управления устройством
 */

public class DeviceRoleRemoteActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnKVS;
    Button btnCommandQueues;
    Button btnSequences;


    public static Intent startDeviceRemoteActivity(Context context) {
        return new Intent(context, DeviceRoleRemoteActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_as_device_remote);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Управление устройством");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ACCOUNT_CONTROL.withRole(AccountControl.Role.device);

        btnKVS = (Button) findViewById(R.id.btn_kvs);
        btnCommandQueues = (Button) findViewById(R.id.btn_command_queues);
        btnSequences = (Button) findViewById(R.id.btn_sequences);

        btnKVS.setOnClickListener(this);
        btnCommandQueues.setOnClickListener(this);
        btnSequences.setOnClickListener(this);

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
            case R.id.btn_kvs:
                startActivity(KeyValueStorageViewerActivity.createActivity(DeviceRoleRemoteActivity.this, 0));
                break;
            case R.id.btn_command_queues:
                startActivity(CommandQueuesActivity.createActivity(DeviceRoleRemoteActivity.this));
                break;
            case R.id.btn_sequences:
                startActivity(SequencesActivity.createActivity(DeviceRoleRemoteActivity.this));
                break;

        }
    }
}
