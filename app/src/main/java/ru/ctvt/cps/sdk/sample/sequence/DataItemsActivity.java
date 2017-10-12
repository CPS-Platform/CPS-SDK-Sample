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

package ru.ctvt.cps.sample.sequence;

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
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.cpsplatform.android.sdk.errorprocessing.BaseCpsException;
import com.cpsplatform.android.sdk.model.DataItem;
import ru.ctvt.cps.sample.Model;
import ru.ctvt.cps.sample.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Экран информации выбранной последовательности
 */
public class DataItemsActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    Toolbar toolbar;
    ListView dataItemsListView;
    Button btnAddDataItem;
    Button btnDelDataItem;
    Button btnRefreshDataItems;

    //Ассоциативный массив для получения команд с сервера
    HashMap<String, Object> dataItemsHashMap = new HashMap<>();
    //Список команд для удобной работы
    public List<DataItem> dataItemsList = new ArrayList<>();
    //Список ключей выбранных элементов последовательности
    public List<String> selectedDataItemsID = new ArrayList<>();
    //Ассоциативный массив для хранения выбранных элементов последовательности
    public HashMap<String, String> selectedDataItems = new HashMap<>();

    private DataItemsAdapter dataItemsAdapter;

    public static Intent createActivity(Context context) {
        return new Intent(context, DataItemsActivity.class);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sequence_info);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("Последовательность данных");
        toolbar.setSubtitle("ID: " + Model.getInstance().getCurrentSequence().getSequenceName() + " (" + Model.getInstance().getCurrentSequence().getType().toString() + ")");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        dataItemsListView = (ListView) findViewById(R.id.list_view_data_items);
        btnAddDataItem = (Button) findViewById(R.id.btn_add_data_item);
        btnDelDataItem = (Button) findViewById(R.id.btn_del_data_item);
        btnDelDataItem.setEnabled(false);
        btnRefreshDataItems = (Button) findViewById(R.id.btn_refresh_data_items);

        dataItemsAdapter = new DataItemsAdapter(DataItemsActivity.this, dataItemsList);
        dataItemsListView.setAdapter(dataItemsAdapter);

        btnAddDataItem.setOnClickListener(this);
        btnDelDataItem.setOnClickListener(this);
        btnRefreshDataItems.setOnClickListener(this);
        dataItemsListView.setOnItemClickListener(this);
        dataItemsListView.setOnItemLongClickListener(this);

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
        showDataItems();
    }

    private void showDataItems() {
        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
        new Thread(new Runnable() {
            ProgressDialog progressDialog;

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog = new ProgressDialog(DataItemsActivity.this);
                        progressDialog.setTitle("Выполнение запроса");
                        progressDialog.setMessage("Подождите");
                        progressDialog.show();
                    }
                });

                try {
                    //Получаем все элементы текущей последовательности
                    dataItemsHashMap = Model.getInstance().getCurrentSequence().fetchAllValues();
                } catch (IOException | BaseCpsException e) {
                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }

                dataItemsList = new ArrayList<>();

                //Заполняем список для удобной работы с адаптером
                for (Map.Entry entry : dataItemsHashMap.entrySet())
                    dataItemsList.add(new DataItem(entry.getKey().toString(), entry.getValue()));

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Обновляем информацию в ListView
                        dataItemsAdapter.updateData(dataItemsList);
                        progressDialog.dismiss();
                    }
                });
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_add_data_item:
                AlertDialog.Builder alert1 = new AlertDialog.Builder(this);

                alert1.setTitle("Добавление нового элемента");
                alert1.setMessage("Введите значение");

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
                                        progressDialog = new ProgressDialog(DataItemsActivity.this);
                                        progressDialog.setTitle("Выполнение запроса");
                                        progressDialog.setMessage("Подождите");
                                        progressDialog.show();
                                    }
                                });

                                try {
                                    switch (Model.getInstance().getCurrentSequence().getType().toString()) {
                                        case "datetime":
                                            //Добавляем новый элемент в последовательность (добавление без ключа работает только для DATETIME последовательностей)
                                            Model.getInstance().getCurrentSequence().addDataKeyless(value);
                                            break;
                                        case "integer":
                                            //Генерируем ключ (он должен быть целым числом)
                                            Integer newIntegerKey = dataItemsList.size() + 1;
                                            //Добавляем новый элемент в последовательность (необходимо передать сгенерированный ключ)
                                            Model.getInstance().getCurrentSequence().addDataByKey(value, newIntegerKey.toString());
                                            break;
                                        case "real":
                                            //Генерируем ключ (он должен быть нецелым числом)
                                            Long tmp = Calendar.getInstance().getTimeInMillis();
                                            Double newDoubleKey = Double.parseDouble(tmp.toString());
                                            newDoubleKey /= 10000;
                                            //Добавляем новый элемент в последовательность (необходимо передать сгенерированный ключ)
                                            Model.getInstance().getCurrentSequence().addDataByKey(value, newDoubleKey.toString());
                                            break;
                                    }
                                } catch (IOException | BaseCpsException e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        //Обновляем список элементов последовательности
                                        showDataItems();
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
            case R.id.btn_del_data_item:
                new Thread(new Runnable() {
                    ProgressDialog progressDialog;

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog = new ProgressDialog(DataItemsActivity.this);
                                progressDialog.setTitle("Выполнение запроса");
                                progressDialog.setMessage("Подождите");
                                progressDialog.show();
                            }
                        });
                        try {
                            //Удаляем множество элементов последовательности (список элементов формируется методом onItemClick)
                            Model.getInstance().getCurrentSequence().deleteManyData(selectedDataItemsID, selectedDataItems);
                        } catch (IOException | BaseCpsException e) {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.dismiss();
                                //Обновляем список элементов последовательностей
                                showDataItems();
                            }
                        });
                    }
                }).start();
                break;
            case R.id.btn_refresh_data_items:
                //Обновляем список элементов последовательностей
                showDataItems();
                break;
        }
    }

    //Управление списком выбранных элементов
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView firstView = (CheckedTextView) view.findViewById(R.id.view_item_title_first);
        if (!firstView.isChecked()) {
            firstView.setChecked(true);
            selectedDataItemsID.add(((DataItem) dataItemsAdapter.getItem(position)).getKey());
            selectedDataItems.put(((DataItem) dataItemsAdapter.getItem(position)).getKey(), ((DataItem) dataItemsAdapter.getItem(position)).getValue().toString());
        } else {
            firstView.setChecked(false);
            selectedDataItemsID.remove(((DataItem) dataItemsAdapter.getItem(position)).getKey());
            selectedDataItems.remove(((DataItem) dataItemsAdapter.getItem(position)).getKey());
        }

        if (selectedDataItemsID.size() > 0)
            btnDelDataItem.setEnabled(true);
        else
            btnDelDataItem.setEnabled(false);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        final PopupMenu popupMenu = new PopupMenu(DataItemsActivity.this, view);
        popupMenu.inflate(R.menu.data_item_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.data_item_menu_item_change:
                        AlertDialog.Builder changeDialog = new AlertDialog.Builder(DataItemsActivity.this);

                        changeDialog.setTitle("Измнение значения элемента");
                        changeDialog.setMessage("Введите новое значение");

                        final EditText input1 = new EditText(DataItemsActivity.this);
                        changeDialog.setView(input1);

                        changeDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                                                progressDialog = new ProgressDialog(DataItemsActivity.this);
                                                progressDialog.setTitle("Выполнение запроса");
                                                progressDialog.setMessage("Подождите");
                                                progressDialog.show();
                                            }
                                        });

                                        try {
                                            //Изменяем значение выбранного элмента, используя метод добавления и ключ выбранного элемента (метод перезаписывает элемент в данном режиме)
                                            Model.getInstance().getCurrentSequence().addDataByKey(value, dataItemsList.get(position).getKey());
                                        } catch (IOException | BaseCpsException e) {
                                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                        }

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                progressDialog.dismiss();
                                                //Обновляем список элементов последовательности
                                                showDataItems();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });

                        changeDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });

                        changeDialog.show();
                        break;
                    case R.id.data_item_menu_item_delete:
                        //Все вызовы методов нашего SDK должны осуществляться из отдельного потока
                        new Thread(new Runnable() {
                            ProgressDialog progressDialog;

                            @Override
                            public void run() {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog = new ProgressDialog(DataItemsActivity.this);
                                        progressDialog.setTitle("Выполнение запроса");
                                        progressDialog.setMessage("Подождите");
                                        progressDialog.show();
                                    }
                                });

                                try {
                                    //Удаляем выбранный элемент последовательности
                                    Model.getInstance().getCurrentSequence().deleteOneItem(dataItemsList.get(position).getKey(), dataItemsList.get(position).getValue());
                                } catch (IOException | BaseCpsException e) {
                                    Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        progressDialog.dismiss();
                                        //Обновляем список элементов последовательности
                                        showDataItems();
                                    }
                                });
                            }
                        }).start();
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
        return true;
    }
}
