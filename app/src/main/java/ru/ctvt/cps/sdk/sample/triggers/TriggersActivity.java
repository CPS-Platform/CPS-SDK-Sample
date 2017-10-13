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

package ru.ctvt.cps.sdk.sample.triggers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.Trigger;
import ru.ctvt.cps.sdk.sample.Model;

import java.io.IOException;
import java.util.ArrayList;

public class TriggersActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    public static Intent startActivity(Context context, int from){
        return new Intent(context, TriggersActivity.class).putExtra("from", from);
    }

    TriggersAdapter triggersAdapter;
    ArrayList<Trigger> triggers = new ArrayList<>();
    ListView lv;
    ProgressDialog progressDialog;
    Model model;
    int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_triggers);

        model = Model.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Список триггеров");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        from = getIntent().getIntExtra("from", 0);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        lv = (ListView) findViewById(R.id.listOfTriggers);
        triggersAdapter = new TriggersAdapter(TriggersActivity.this, triggers);
        lv.setAdapter(triggersAdapter);
        lv.setOnItemClickListener(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getTriggers();
    }


    void switchEnabled(Trigger trigger){
        new Thread(() -> {
            try {
                trigger.setEnabled(trigger.getType(), !trigger.isEnabled());
                runOnUiThread(() -> triggersAdapter.notifyDataSetChanged());
            } catch (IOException | BaseCpsException e) {
                e.printStackTrace();
            }
        }).start();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switchEnabled(triggers.get(position));
    }

    void getTriggers(){
        new Thread(new Runnable() {
            ProgressDialog dialog;

            @Override
            public void run() {
                runOnUiThread(() -> {
                    dialog = new ProgressDialog(TriggersActivity.this);
                    dialog.setTitle("Выполнение запроса");
                    dialog.setMessage("Подождите");
                    dialog.show();
                });
                try {
                    switch (from){
                        case 1:
                            triggers = model.getCurrentCommandQueue().fetchTriggers();
                            break;
                        case 2:
                            triggers = model.getCurrentSequence().fetchTriggers();
                            break;
                        case 0: finish();
                    }
                    runOnUiThread(() -> triggersAdapter.updateData(triggers));

                } catch (final IOException | BaseCpsException e) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    });
                }

                runOnUiThread(() -> dialog.dismiss());
            }

        }).start();
    }
}
