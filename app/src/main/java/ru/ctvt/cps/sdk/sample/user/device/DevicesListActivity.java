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

package ru.ctvt.cps.sdk.sample.user.device;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.UserDevice;
import ru.ctvt.cps.sdk.model.User;
import ru.ctvt.cps.sdk.sample.Model;
import ru.ctvt.cps.sdk.sample.commandQueue.CommandQueuesActivity;
import ru.ctvt.cps.sdk.sample.keyValueStorage.KeyValueStorageViewerActivity;
import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.sample.sequence.SequencesActivity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Экран, отображающий список устройств текущего пользователя.
 * Есть возможность добавления новых устройств
 */
public class DevicesListActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {
    DevicesAdapter devicesAdapter;
    List<UserDevice> list = new ArrayList<>();
    ListView lv;
    ProgressDialog progressDialog;
    Button buttonAddByCode;
    EditText editTextCode;
    Model model;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devices_list);

        model = Model.getInstance();

        user = model.getCurrentUser();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Список устройств");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        buttonAddByCode = (Button) findViewById(R.id.addDeviceByCode);
        lv = (ListView) findViewById(R.id.listOfDevices);
        editTextCode = (EditText) findViewById(R.id.etCode);
        editTextCode.setFocusable(true);
        devicesAdapter = new DevicesAdapter(DevicesListActivity.this, list);
        lv.setAdapter(devicesAdapter);

        buttonAddByCode.setOnClickListener(this);

        lv.setOnItemClickListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Обработчик нажатия на кнопку добавления нового устройства
     * @param view
     */
    @Override
    public void onClick(View view) {
        new Thread(new Runnable() {
            ProgressDialog progres;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progres = new ProgressDialog(DevicesListActivity.this);
                        progres.setTitle("Выполнение запроса");
                        progres.setMessage("Подождите");
                        progres.show();
                    }
                });
                try {
                    //добавляем новое устройство текущему пользователю, передавая код привязки
                    //если поле ввода пустое - новое устройство добавится без привязки
                    user.addDevice(editTextCode.getText().toString());
                    list = user.fetchDevices();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (editTextCode.getText().toString().isEmpty())
                                Toast.makeText(getApplicationContext(), "Код не был указан, устройство добавлено без привязки", Toast.LENGTH_LONG).show();
                            else
                                //текст в поле ввода нам больше не нужен
                                editTextCode.setText("");
                            devicesAdapter.updateData(list);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (final BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            editTextCode.setText("");
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progres.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        deviceContextMenu(view, position);
    }


    @Override
    protected void onResume() {
        super.onResume();
        getDevices(); //список должен обновляться всякий раз, когда приложение выходит на передний план
    }

    /**
     * Обновление списка устройств пользователя
     */
    void getDevices() {
        new Thread(new Runnable() {
            ProgressDialog dialog;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog = new ProgressDialog(DevicesListActivity.this);
                        dialog.setTitle("Выполнение запроса");
                        dialog.setMessage("Подождите");
                        dialog.show();
                    }
                });
                try {
                    list = user.fetchDevices();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            devicesAdapter.updateData(list);
                        }
                    });

                } catch (final IOException | BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }


        }).start();

    }

    /**
     * Контестное меню, вызываемое при нажатии на определенный элемент списка
     *
     * @param view     элемент списка
     * @param position идентификатор устройства, соответствующий элементу списка
     */
    private void deviceContextMenu(View view, final int position) {
        PopupMenu menu = new PopupMenu(this, view);
        menu.inflate(R.menu.device_menu);

        menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                model.setCurrentDevice(list.get(position));
                switch (item.getItemId()) {
                    case R.id.menu_item_kv_storage:
                        startActivity(KeyValueStorageViewerActivity.createActivity(DevicesListActivity.this, 0));
                        break;
                    case R.id.menu_item_command_queues:
                        startActivity(CommandQueuesActivity.createActivity(DevicesListActivity.this));
                        break;
                    case R.id.menu_item_sequences:
                        startActivity(SequencesActivity.createActivity(DevicesListActivity.this));
                        break;
                    case R.id.menu_item_set_device_code:
                        startActivity(SetDeviceCodeActivity.setDeviceCodeIntent(DevicesListActivity.this));
                        break;
                }
                return false;
            }
        });
        menu.show();
    }

}
