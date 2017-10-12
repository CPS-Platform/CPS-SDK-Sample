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

package ru.ctvt.cps.sample.keyValueStorage;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cpsplatform.android.sdk.errorprocessing.BaseCpsException;
import com.cpsplatform.android.sdk.model.Device;
import com.cpsplatform.android.sdk.model.KeyValueStorage;
import com.cpsplatform.android.sdk.model.User;
import ru.ctvt.cps.sample.Model;
import ru.ctvt.cps.sample.R;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.HashMap;


/**
 * Экран демонстрации работы хранилища
 */
public class KeyValueStorageViewerActivity extends AppCompatActivity implements View.OnClickListener {

    Button getKVS;
    Button getKVG;
    Button getVal;
    Button setKVG;
    Button setVal;
    Button delKVG;
    Button delVal;

    ToggleButton toggle;
    TextView tv;
    EditText editTextGroup;
    EditText editTextKey;

    /*
    Так как этот экран универсален для любого хранилища, будь то хранилище пользователя
    или хранилище устройства - в экстра мы передаем контейнер хранилища для того, чтобы определить,
    какой KeyValueStorage нам следует инициализировать.
     */
    public static Intent createActivity(Context context, int container) {
        Intent intent = new Intent(context, KeyValueStorageViewerActivity.class);
        intent.putExtra("container", container);
        return intent;
    }

    boolean visibility = false; //флаг области видимости контейнера. true = public, false = local

    KeyValueStorage localStorage;
    KeyValueStorage publicStorage;
    Device currentDevice;
    User currentUser;
    Model model;

    Intent intent;

    HashMap<String, Object> sampleGroupValue = new HashMap<>(); //тестовая группа
    SampleValue sampleValue = new SampleValue();
    /*
    В тестовом приложении мы просто выводим на экран тот Json, что пришел нам в ответ.
    В SDK предусмотрены инструменты для десериализации
     */
    Gson gson = new GsonBuilder().setPrettyPrinting().create();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_value_storage_viewer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle("KeyValueStorage");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tv = (TextView) findViewById(R.id.textViewKVS);
        tv.setMovementMethod(new ScrollingMovementMethod());

        editTextGroup = (EditText) findViewById(R.id.storageEditGroup);
        editTextKey = (EditText) findViewById(R.id.storageEditKey);

        getKVS = (Button) findViewById(R.id.btnGetKVS);
        getKVG = (Button) findViewById(R.id.btnGetKVG);
        getVal = (Button) findViewById(R.id.btnGetVal);
        setKVG = (Button) findViewById(R.id.btnSetKVG);
        setVal = (Button) findViewById(R.id.btnSetVal);
        getVal = (Button) findViewById(R.id.btnGetVal);
        delKVG = (Button) findViewById(R.id.btnDelKVG);
        delVal = (Button) findViewById(R.id.btnDelVal);

        toggle = (ToggleButton) findViewById(R.id.toggleButton);
        toggle.setTextOn("Public requests");
        toggle.setTextOff("Local requests");
        toggle.setChecked(false);

        getKVS.setOnClickListener(this);
        getKVG.setOnClickListener(this);
        getVal.setOnClickListener(this);
        setKVG.setOnClickListener(this);
        setVal.setOnClickListener(this);
        delKVG.setOnClickListener(this);
        delVal.setOnClickListener(this);

        model = Model.getInstance();
        intent = getIntent();

        /*
         * тестовая группа для отправки в хранилище
         */
        sampleGroupValue.put("sampleKey", sampleValue); //Запись объекта-значения в коллекцию, которая является value группы

        /*
        Тогл доступен только в случае, когдв мы получаем хранилище пользователя.
        В ином случае - тогла нет.
        В зависимости от положения toggle элементы Button реализуют разные запросы
         */
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                visibility = isChecked;
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getExtras();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /*
     * В зависимости от visibility шлем разные запросы
     * Для хранилища устройства visibility всегда false - мы можем получить только локальное хранилище
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetKVS:
                getKVS(visibility);
                break;
            case R.id.btnGetKVG:
                getKVG(visibility, editTextGroup.getText().toString());
                break;
            case R.id.btnGetVal:
                getValue(visibility, editTextGroup.getText().toString(), editTextKey.getText().toString());
                break;
            case R.id.btnSetKVG:
                putKVG(visibility, editTextGroup.getText().toString(), sampleGroupValue);
                break;
            case R.id.btnSetVal:
                putValue(visibility, editTextGroup.getText().toString(), editTextKey.getText().toString(), sampleValue);
                break;
            case R.id.btnDelKVG:
                deleteKVG(visibility, editTextGroup.getText().toString());
                break;
            case R.id.btnDelVal:
                deleteValue(visibility, editTextGroup.getText().toString(), editTextKey.getText().toString());
                break;
        }
    }

    void getExtras() {
        /*
        Получаем экстра из прошлого экрана.
        container - контейнер хранилища. Может принимать значения 0 (устройство) и 1 (пользователь)
        если был передан 0 - получаем KVS текущего устройства. Текущее устройство записывается в Model.class во время перехода
        в этот экран с экрана со списком устройств.
        Если же мы работаем в режиме устройства - устройство будет сохранено в модели раньше.
        Если мы обращаемся к хранилищу устройства, нужно скрыть тогл visibility,
        поскольку устройство имеет только локальное хранилище.
         */
        if (intent.getIntExtra("container", 0) != 1) {
            currentDevice = model.getCurrentDevice();
            try {
                localStorage = currentDevice.getLocalKVStorage();
            } catch (IOException | BaseCpsException e) {
                Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
            toggle.setVisibility(View.INVISIBLE);
        } else {
            if (currentUser == null)
                currentUser = model.getCurrentUser();
            localStorage = currentUser.getLocalKVStorage();
            publicStorage = currentUser.getPublicKVStorage();
            toggle.setVisibility(View.VISIBLE);
        }
    }

//region request methods

    /*
     * Реализация запросов производится в отдельном потоке
     * запрещено использовать методы объекта KeyValueStorage в UI потоке
     * В данной реализации во все методы передается visibility
     */

    void getKVS(final boolean visibility) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, HashMap<String, Object>> storageInfo;
                    if (visibility) {
                        storageInfo = publicStorage.fetchKVStorageData();
                    } else {
                        storageInfo = localStorage.fetchKVStorageData();
                    }
                        final String info = gson.toJson(storageInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(info);
                            }
                        });
                } catch (IOException | BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }


    void getKVG(final boolean visibility, final String groupName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    HashMap<String, Object> group;
                    if (visibility) {
                        group = publicStorage.fetchGroup(groupName);
                    } else {
                        group = localStorage.fetchGroup(groupName);
                    }
                        final String info = gson.toJson(group);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(info);
                            }
                        });
                } catch (IOException | BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }


    void putKVG(final boolean visibility, final String groupName, final HashMap<String, Object> value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (visibility) {
                        publicStorage.putGroup(groupName, value);
                    } else {
                        localStorage.putGroup(groupName, value);
                    }
                } catch (IOException | BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    void deleteKVG(final boolean visibility, final String groupName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (visibility) {
                        publicStorage.deleteGroup(groupName);
                    } else {
                        localStorage.deleteGroup(groupName);
                    }
                } catch (IOException | BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();


    }

    void getValue(final boolean visibility, final String groupName, final String key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    Object value;
                    if (visibility) {
                        value = publicStorage.fetchValue(groupName, key, SampleValue.class);
                    } else {
                        value = localStorage.fetchValue(groupName, key, SampleValue.class);
                    }
                        final String info = gson.toJson(value);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(info);
                            }
                        });

                } catch (IOException | BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();

    }

    void putValue(final boolean visibility, final String groupName, final String key, final Object value) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (visibility) {
                        publicStorage.putValue(groupName, key, value);
                    } else {
                        localStorage.putValue(groupName, key, value);
                    }
                } catch (IOException | BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();


    }

    void deleteValue(final boolean visibility, final String groupName, final String key) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (visibility) {
                        publicStorage.deleteValue(groupName, key);
                    } else {
                        localStorage.deleteValue(groupName, key);
                    }
                } catch (IOException | BaseCpsException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).start();
    }


//endregion
}

