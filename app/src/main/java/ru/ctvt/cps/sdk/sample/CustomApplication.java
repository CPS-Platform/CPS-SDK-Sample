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

package ru.ctvt.cps.sdk.sample;

import android.app.Application;
import android.content.Context;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;

//import ru.ctvt.cps.sdk.SDKManager;

/**
 * Инициализация библиотеки CPS при старте программы
 */
public class CustomApplication extends Application {

    private static CustomApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        SDKManager.getInstance().init(this);
    }
}
