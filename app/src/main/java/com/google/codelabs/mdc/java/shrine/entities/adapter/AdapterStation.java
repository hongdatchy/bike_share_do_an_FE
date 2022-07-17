package com.google.codelabs.mdc.java.shrine.entities.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.entities.StationResponse;

import java.util.List;

public class AdapterStation extends ArrayAdapter<StationResponse> {
    LayoutInflater layoutInflater;

    public AdapterStation(Activity context, int resourceId, List<StationResponse> list){
        super(context,resourceId, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return rowView(convertView,position);
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        return rowView(convertView,position);
    }


    private View rowView(View convertView , int position){
        StationResponse stationResponse = getItem(position);
        ViewHolder holder ;
        View rowView = convertView;
        if (rowView==null) {

            holder = new ViewHolder();
            layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = layoutInflater.inflate(R.layout.my_spinner_item, null, false);
            holder.nameTextView =  rowView.findViewById(R.id.spinnerNameStation);
            holder.locationTextView =  rowView.findViewById(R.id.spinnerLocationStation);
            holder.numberBikeTextView =  rowView.findViewById(R.id.spinnerNumberBikeStation);
            rowView.setTag(holder);
        }else{
            holder = (ViewHolder) rowView.getTag();
        }
        if(!stationResponse.getId().equals(0)){
            holder.nameTextView.setText(stationResponse.getName());
            holder.locationTextView.setText(stationResponse.getLocation());
            String numberBike = "số xe: " + stationResponse.getCurrentNumberCar() + " / " + stationResponse.getSlotQuantity();
            holder.numberBikeTextView.setText(numberBike);
        }else {
            holder.nameTextView.setTextSize(30);
        }
        return rowView;
    }

    private static class ViewHolder{
        TextView nameTextView;
        TextView locationTextView;
        TextView numberBikeTextView;
    }
}
