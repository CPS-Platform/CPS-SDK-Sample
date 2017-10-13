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

import android.app.Dialog;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.Command;
import ru.ctvt.cps.sdk.sample.Model;

/**
 * Экран информации выбранной очереди команд
 */

public class CommandsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    Toolbar toolbar;
    ListView commandsListView;
    Button btnAddCommand;
    Button btnRefreshCommands;

    //Список для хранения команд
    public static List<Command> commandsList = new ArrayList<>();

    CommandsAdapter commandsAdapter;

    public static Intent createActivity(Context context) {
        return new Intent(context, CommandsActivity.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_command_queue_info);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Очередь команд");
        toolbar.setSubtitle("ID: " + Model.getInstance().getCurrentCommandQueue().getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        commandsListView = (ListView) findViewById(R.id.list_view_commands);
        btnAddCommand = (Button) findViewById(R.id.btn_add_command);
        btnRefreshCommands = (Button) findViewById(R.id.btn_refresh_commands);

        commandsAdapter = new CommandsAdapter(CommandsActivity.this, commandsList);
        commandsListView.setAdapter(commandsAdapter);

        commandsListView.setOnItemClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAddCommand.setOnClickListener(this);
        btnRefreshCommands.setOnClickListener(this);
    }

    public void onResume(){
        super.onResume();
        //Обновляем список команд
        showCommands();
    }

    private void showCommands() {
        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
        new Thread(new Runnable() {
            ProgressDialog progressDialog;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = new ProgressDialog(CommandsActivity.this);
                        progressDialog.setTitle("Выполнение запроса");
                        progressDialog.setMessage("Подождите");
                        progressDialog.show();
                    }
                });

                commandsList = new ArrayList<>();

                try {
                    //Получаем все команды текущей очереди команд
                    commandsList = Model.getInstance().getCurrentCommandQueue().fetchCommands();
                } catch (IOException | BaseCpsException e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Обновляем информацию в ListVIew
                        commandsAdapter.updateData(commandsList);
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_command:
                AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

                alert1.setTitle("Добавление новой команды");
                alert1.setMessage("Введите имя");

                final EditText input1 = new EditText(this);
                alert1.setView(input1);

                alert1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        final String value = input1.getText().toString();
                        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
                        new Thread(new Runnable() {
                            ProgressDialog progressDialog;

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog = new ProgressDialog(CommandsActivity.this);
                                        progressDialog.setTitle("Выполнение запроса");
                                        progressDialog.setMessage("Подождите");
                                    }
                                });

                                Integer argument = 0;

                                try {
                                    //Создаем новую команду
                                    Model.getInstance().getCurrentCommandQueue().createCommand(value, argument);
                                } catch (IOException | BaseCpsException e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        //Обновляем список команд
                                        showCommands();
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
            case R.id.btn_refresh_commands:
                //Обновляем список команд
                showCommands();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        final PopupMenu popupMenu = new PopupMenu(CommandsActivity.this, view);
        popupMenu.inflate(R.menu.command_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.command_menu_item_mark:
                        final AlertDialog.Builder markDialog = new AlertDialog.Builder(CommandsActivity.this);

                        markDialog.setTitle("Изменить состояние команды");

                        final String[] commandStateArray = {"Исполняемая", "Исполненная"};

                        markDialog.setSingleChoiceItems(commandStateArray, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == Dialog.BUTTON_POSITIVE) {

                                }
                            }
                        });

                        markDialog.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ListView listView = ((AlertDialog) dialog).getListView();
                                switch (listView.getCheckedItemPosition()) {
                                    case 0:
                                        if (commandsList.get(position).getState().toString().equals("acquired")) {
                                            Toast.makeText(getApplicationContext(), "Эта команда уже исполняется", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        if (commandsList.get(position).getState().toString().equals("executed")) {
                                            Toast.makeText(getApplicationContext(), "Эта команда уже исполнена", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
                                        new Thread(new Runnable() {
                                            ProgressDialog progressDialog;

                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog = new ProgressDialog(CommandsActivity.this);
                                                        progressDialog.setTitle("Выполнение запроса");
                                                        progressDialog.setMessage("Подождите");
                                                        progressDialog.show();
                                                    }
                                                });

                                                try {
                                                    //Помечаем команду как исполняемую
                                                    commandsList.get(position).markExecuting();
                                                } catch (IOException | BaseCpsException e) {
                                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        //Обновляем список команд
                                                        showCommands();
                                                    }
                                                });
                                            }
                                        }).start();
                                        break;
                                    case 1:
                                        if (commandsList.get(position).getState().toString().equals("executed")) {
                                            Toast.makeText(getApplicationContext(), "Эта команда уже исполнена", Toast.LENGTH_SHORT).show();
                                            return;
                                        }

                                        new Thread(new Runnable() {
                                            ProgressDialog progressDialog;

                                            @Override
                                            public void run() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog = new ProgressDialog(CommandsActivity.this);
                                                        progressDialog.setTitle("Выполнение запроса");
                                                        progressDialog.setMessage("Подождите");
                                                        progressDialog.show();
                                                    }
                                                });

                                                try {
                                                    //Помечаем команду как исполненную
                                                    commandsList.get(position).markExecuted(new Object());
                                                } catch (IOException | BaseCpsException e) {
                                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                                }

                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        progressDialog.dismiss();
                                                        //Обновляем список команд
                                                        showCommands();
                                                    }
                                                });
                                            }
                                        }).start();
                                        break;
                                }
                            }
                        });

                        markDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                        markDialog.show();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
    }
}
