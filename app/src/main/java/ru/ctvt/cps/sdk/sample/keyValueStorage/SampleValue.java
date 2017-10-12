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

import com.google.gson.annotations.SerializedName;

/**
 * Тестовое значение, нужно для проверки корректности запросов
 */

class SampleValue {

    @SerializedName("value")
    Data value;

    class Data {
        @SerializedName("key1")
        String value1 = "sample value  ";

        @SerializedName("key2")
        String value2 = "You can read/write any type of data";

        @SerializedName("key3")
        String value3 = "All you need is make a pojo";
    }

    SampleValue() {
        this.value = new Data();
    }
}
