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

package ru.ctvt.cps.sdk.sample.commandQueue;

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
import android.widget.Toast;

import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.CommandQueue;
import ru.ctvt.cps.sdk.model.Device;
import ru.ctvt.cps.sdk.sample.Model;
import ru.ctvt.cps.sdk.sample.triggers.TriggersActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Экран со списком очередей команд
 */
public class CommandQueuesActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    Toolbar toolbar;
    ListView commandQueuesListView;
    Button btnAdd;
    Button btnRefresh;

    //Ассоциативный массив для хранения очередей команд
    private HashMap<String, CommandQueue> commandQueuesHashMap = new HashMap<>();
    //Список очередей команд для удобной работы
    private static List<CommandQueue> commandQueuesList = new ArrayList<>();

    private CommandQueuesAdapter commandQueuesAdapter;

    //Метод для создания этого экрана
    public static Intent createActivity(Context context) {
        return new Intent(context, CommandQueuesActivity.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_command_queues);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle("Очереди команд устройства");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        commandQueuesListView = (ListView) findViewById(R.id.list_view_command_queues);
        btnAdd = (Button) findViewById(R.id.btn_add_command_queue);
        btnRefresh = (Button) findViewById(R.id.btn_refresh_command_queues);

        commandQueuesAdapter = new CommandQueuesAdapter(CommandQueuesActivity.this, commandQueuesList);
        commandQueuesListView.setAdapter(commandQueuesAdapter);

        btnAdd.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        commandQueuesListView.setOnItemClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onResume(){
        super.onResume();
        //Обновляем список очередей команд
        showCommandQueues();
    }

    //Метод получения и вывода на экран очередей команд
    private void showCommandQueues() {
        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
        new Thread(new Runnable() {
            ProgressDialog progress;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progress = new ProgressDialog(CommandQueuesActivity.this);
                        progress.setTitle("Выполнение запроса");
                        progress.setMessage("Подождите");
                        progress.show();
                    }
                });

                try {
                    //Получаем все очереди команд
                    commandQueuesHashMap = Model.getInstance().getCurrentDevice().fetchAllCommandQueues();
                } catch (IOException | BaseCpsException e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                commandQueuesList = new ArrayList<>();
                //Заполняем список для удобной работы с адаптером
                commandQueuesList.addAll(commandQueuesHashMap.values());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toolbar.setSubtitle("ID: " + Model.getInstance().getCurrentDevice().getDeviceID());
                        //Обновляем информацию в ListView
                        commandQueuesAdapter.updateData(commandQueuesList);
                        progress.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_command_queue:
                AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

                alert1.setTitle("Добавление новой очереди команд");
                alert1.setMessage("Введите имя");

                final EditText input1 = new EditText(this);
                alert1.setView(input1);

                alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String value = input1.getText().toString();
                        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
                        new Thread(new Runnable() {
                            ProgressDialog progress;

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress = new ProgressDialog(CommandQueuesActivity.this);
                                        progress.setTitle("Выполнение запроса");
                                        progress.setMessage("Подождите");
                                        progress.show();
                                    }
                                });

                                try {
                                    //Создаем новую очередь команд
                                    Model.getInstance().getCurrentDevice().createCommandQueue(value, Device.CreationMode.skip_if_exactly_exist);
                                } catch (IOException | BaseCpsException e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        //Обновляем список очередей команд
                                        showCommandQueues();
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
            case R.id.btn_refresh_command_queues:
                //Обновляем список очередей команд
                showCommandQueues();
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
                        //Используя модель, записываем выбранную очередь команд
                        Model.getInstance().setCurrentCommandQueue((CommandQueue) commandQueuesAdapter.getItem(position));
                        startActivity(CommandsActivity.createActivity(getApplicationContext()));
                        break;
                    case R.id.queue_and_seq_menu_item_triggers:
                        Model.getInstance().setCurrentCommandQueue((CommandQueue) commandQueuesAdapter.getItem(position));
                        startActivity(TriggersActivity.startActivity(getApplicationContext(), 1));
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
                                        progress = new ProgressDialog(CommandQueuesActivity.this);
                                        progress.setTitle("Выполнение запроса");
                                        progress.setMessage("Подождите");
                                        progress.show();
                                    }
                                });
                                try {
                                    //Удаляем выбранную очередь команд
                                    Model.getInstance().getCurrentDevice().deleteCommandQueue(((CommandQueue) commandQueuesAdapter.getItem(position)).getName());
                                } catch (IOException | BaseCpsException e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progress.dismiss();
                                        //Обновляем список очередей команд
                                        showCommandQueues();
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
