package com.google.codelabs.mdc.java.shrine.entities.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.entities.City;

import java.util.List;

public class AdapterCity extends ArrayAdapter<City> {

    LayoutInflater layoutInflater;
    View view;

    public AdapterCity(Context context, int resource, List<City> list){
        super(context,resource, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return rowView(position);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return rowView(position);
    }

    private View rowView(int position){
        City city = getItem(position);
        layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.list_item_in_register, null, false);
        TextView textView  =  view.findViewById(R.id.nameCityDistrictWard);
        view.setTag(textView);
        textView.setText(city.getName());
        return view;
    }

}
