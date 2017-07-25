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

import ru.ctvt.cps.sdk.errorprocessing.BaseCpsException;
import ru.ctvt.cps.sdk.model.AccountControl;
import ru.ctvt.cps.sdk.model.CommandQueue;
import ru.ctvt.cps.sdk.model.Device;
import ru.ctvt.cps.sdk.model.Sequence;
import ru.ctvt.cps.sdk.model.User;

/**
 * Синглтон для хранения модели данных на время работы приложения
 */

public class Model {
    private static Model instance;

    private Device currentDevice;
    private User currentUser;
    private CommandQueue currentCommandQueue;
    private Sequence currentSequence;

    public static Model getInstance() {
        if (instance == null) {
            instance = new Model();
        }
        return instance;
    }

    private Model() {
        try {
            setCurrentDevice(AccountControl.getInstance().restoreDevice());
        } catch (BaseCpsException e) {
            e.printStackTrace();
        }
        try {
            setCurrentUser(AccountControl.getInstance().restoreUser());
        } catch (BaseCpsException e) {
            e.printStackTrace();
        }

    }

    public Device getCurrentDevice() {
        return currentDevice;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public CommandQueue getCurrentCommandQueue(){
        return currentCommandQueue;
    }

    public Sequence getCurrentSequence(){
        return currentSequence;
    }

    public void setCurrentDevice(Device device) {
        currentDevice = device;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

    public void setCurrentCommandQueue(CommandQueue commandQueue){
        currentCommandQueue = commandQueue;
    }

    public void setCurrentSequence(Sequence sequence){
        currentSequence = sequence;
    }
}
