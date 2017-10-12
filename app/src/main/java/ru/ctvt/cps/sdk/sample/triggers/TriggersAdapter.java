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

package ru.ctvt.cps.sdk.sample.triggers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

import ru.ctvt.cps.sample.R;
import ru.ctvt.cps.sdk.model.Trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ngrypie on 10.10.2017.
 */

public class TriggersAdapter extends BaseAdapter{

    Context context;
    LayoutInflater inflater;

    List<Trigger> triggerList = new ArrayList<>();

    CheckedTextView idView;
    TextView ownerView;
    TextView createDateView;

    TriggersAdapter(Context context, List<Trigger> triggerList) {
        this.context = context;
        this.triggerList = triggerList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return triggerList.size();
    }

    public void updateData(List<Trigger> newList) {
        triggerList.clear();
        triggerList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return triggerList.get(position);
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
        idView.setText("Header: " + triggerList.get(position).getHeader());
        idView.setTextSize(16);
        idView.setCheckMarkDrawable(null);
        ownerView.setText("Имя: " + triggerList.get(position).getName());
        ownerView.setTextSize(12);
        createDateView.setText("Состояние: " + triggerList.get(position).isEnabled());
        createDateView.setTextSize(12);
        return view;
    }
}
