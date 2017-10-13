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

package ru.ctvt.cps.sdk.sample.sequence;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.Device;
import ru.ctvt.cps.sdk.model.Sequence;
import ru.ctvt.cps.sdk.sample.Model;
import ru.ctvt.cps.sdk.sample.triggers.TriggersActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Экран со списком последовательностей текущего аккаунта
 */

public class SequencesActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    ListView sequencesListView;
    Button btnAdd;
    Button btnRefresh;

    //Ассоциативный массив для получения последовательностей с сервера
    private HashMap<String, Sequence> sequencesHashMap = new HashMap<>();
    //Список последовательностей для удобной работы
    public static List<Sequence> sequencesList = new ArrayList<>();
    //Список типов ключа последовательностей
    private static List<String> typeList = new ArrayList<>();

    private SequencesAdapter sequencesAdapter;

    public static Intent createActivity(Context context) {
        return new Intent(context, SequencesActivity.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sequences);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Последовательности данных устройства");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //Ключ последовательностей может быть трёх типов
        typeList.add("DATETIME");
        typeList.add("INTEGER");
        typeList.add("REAL");

        sequencesListView = (ListView) findViewById(R.id.list_view_sequences);
        btnAdd = (Button) findViewById(R.id.btn_add_sequence);
        btnRefresh = (Button) findViewById(R.id.btn_refresh_sequences);

        sequencesAdapter = new SequencesAdapter(SequencesActivity.this, sequencesList);
        sequencesListView.setAdapter(sequencesAdapter);

        toolbar.setSubtitle("ID: " + Model.getInstance().getCurrentDevice().getDeviceID());

        btnAdd.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);

        sequencesListView.setOnItemClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onResume(){
        super.onResume();
        //Обновляем список последовательностей
        showSequences();
    }

    private void showSequences() {
        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
        new Thread(new Runnable() {
            ProgressDialog progress;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress = new ProgressDialog(SequencesActivity.this);
                        progress.setTitle("Выполнение запроса");
                        progress.setMessage("Подождите");
                        progress.show();
                    }
                });

                try {
                    //Получаем все последовательности для текущего устройства
                    sequencesHashMap = Model.getInstance().getCurrentDevice().fetchAllSequences();
                } catch (IOException | BaseCpsException e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                sequencesList = new ArrayList<>();

                //Зполняем список для удобной работы
                sequencesList.addAll(sequencesHashMap.values());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Обновляем информацию в ListView
                        sequencesAdapter.updateData(sequencesList);
                        progress.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_sequence:
                AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

                alert1.setTitle("Добавление новой последовательности данных");
                alert1.setMessage("Введите имя");

                View dialogView = getLayoutInflater().inflate(R.layout.add_sequence_dialog, null);
                alert1.setView(dialogView);

                final EditText editName = (EditText) dialogView.findViewById(R.id.add_sequence_name_edit_dialog);
                final Spinner electType = (Spinner) dialogView.findViewById(R.id.add_sequence_type_elect);

                alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
                        new Thread(new Runnable() {
                            ProgressDialog progress;
                            String name = editName.getText().toString();
                            String type = electType.getSelectedItem().toString();

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress = new ProgressDialog(SequencesActivity.this);
                                        progress.setTitle("Выполнение запроса");
                                        progress.setMessage("Подождите");
                                        progress.show();
                                    }
                                });

                                try {
                                    switch (type) {
                                        //Создаем новую последовательность с определенным типом ключа
                                        case "DATETIME":
                                            Model.getInstance().getCurrentDevice().createSequence(name, Sequence.Type.datetime, Device.CreationMode.skip_if_exactly_exist);
                                            break;
                                        case "INTEGER":
                                            Model.getInstance().getCurrentDevice().createSequence(name, Sequence.Type.integer, Device.CreationMode.skip_if_exactly_exist);
                                            break;
                                        case "REAL":
                                            Model.getInstance().getCurrentDevice().createSequence(name, Sequence.Type.real, Device.CreationMode.skip_if_exactly_exist);
                                            break;
                                    }
                                } catch (IOException | BaseCpsException e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        //Обновляем список последовательностей
                                        showSequences();
                                    }
                                });
                            }
                        }).start();
                    }
                });

                alert1.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

                alert1.show();
                break;
            case R.id.btn_refresh_sequences:
                //Обновляем список последовательностей
                showSequences();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.inflate(R.menu.queue_and_seq_menu);

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.queue_and_seq_menu_item_view:
                        //Используя модель, записываем выбранную последовательность данных
                        Model.getInstance().setCurrentSequence((Sequence) sequencesAdapter.getItem(position));
                        startActivity(DataItemsActivity.createActivity(getApplicationContext()));
                        break;
                    case R.id.queue_and_seq_menu_item_triggers:
                        Model.getInstance().setCurrentSequence((Sequence) sequencesAdapter.getItem(position));
                        startActivity(TriggersActivity.startActivity(getApplicationContext(), 2));
                        break;
                    case R.id.queue_and_seq_menu_item_delete:
                        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
                        new Thread(new Runnable() {
                            ProgressDialog progress;

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress = new ProgressDialog(SequencesActivity.this);
                                        progress.setTitle("Выполнение запроса");
                                        progress.setMessage("Подождите");
                                        progress.show();
                                    }
                                });

                                try {
                                    //Удаляем выбранную последовательность
                                    Model.getInstance().getCurrentDevice().deleteSequence(((Sequence) sequencesAdapter.getItem(position)).getSequenceName());
                                } catch (IOException | BaseCpsException e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        //Обновляем список последовательностей
                                        showSequences();
                                    }
                                });
                            }
                        }).start();
                        break;
                }
                return false;
            }
        });
        menu.show();
    }
}
