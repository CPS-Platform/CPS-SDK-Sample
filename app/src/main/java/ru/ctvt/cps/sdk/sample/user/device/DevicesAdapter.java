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

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import ru.ctvt.cps.sdk.sample.R;
import ru.ctvt.cps.sdk.model.UserDevice;

import java.util.ArrayList;
import java.util.List;


public class DevicesAdapter extends BaseAdapter {

    Context context;
    LayoutInflater inflater;

    List<UserDevice> deviceList = new ArrayList<>();

    CheckedTextView idView;
    TextView ownerView;
    TextView createDateView;

    DevicesAdapter(Context context, List<UserDevice> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return deviceList.size();
    }

    public void updateData(List<UserDevice> newList) {
        deviceList.clear();
        deviceList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return deviceList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null)
            view = inflater.inflate(R.layout.adapter_item, parent, false);
        idView = (CheckedTextView) view.findViewById(R.id.view_item_title_first);
        ownerView = (TextView) view.findViewById(R.id.view_item_title_second);
        createDateView = (TextView) view.findViewById(R.id.view_item_title_third);
        idView.setText("ID: " + deviceList.get(position).getDeviceID());
        idView.setTextSize(16);
        idView.setCheckMarkDrawable(null);
        ownerView.setText("Имя устройства: " + deviceList.get(position).getName());
        ownerView.setTextSize(12);
        createDateView.setText("Создан: " + deviceList.get(position).getCreateDate());
        createDateView.setTextSize(12);
        return view;
    }
}
