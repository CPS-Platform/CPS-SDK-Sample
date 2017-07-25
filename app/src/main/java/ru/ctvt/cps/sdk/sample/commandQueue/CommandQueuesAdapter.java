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

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;

//import ru.ctvt.cps.sdk.model.CommandQueue;
import ru.ctvt.cps.sdk.sample.R;

import java.util.ArrayList;
import java.util.List;

public class CommandQueuesAdapter extends BaseAdapter {
    Context context;
    LayoutInflater layoutInflater;
    List<CommandQueue> commandQueueList = new ArrayList<>();

    CommandQueuesAdapter(Context context, @NonNull List<CommandQueue> commandQueueList) {
        this.context = context;
        this.commandQueueList = commandQueueList;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateData(List<CommandQueue> newList) {
        commandQueueList.clear();
        commandQueueList.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return commandQueueList.size();
    }

    @Override
    public Object getItem(int position) {
        return commandQueueList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = layoutInflater.inflate(R.layout.adapter_item, parent, false);
        CheckedTextView firstView = (CheckedTextView) view.findViewById(R.id.view_item_title_first);
        firstView.setText("Имя очереди: " + commandQueueList.get(position).getName());
        firstView.setCheckMarkDrawable(null);
        view.findViewById(R.id.view_item_title_second).setVisibility(TextView.GONE);
        view.findViewById(R.id.view_item_title_third).setVisibility(TextView.GONE);
        return view;
    }
}
