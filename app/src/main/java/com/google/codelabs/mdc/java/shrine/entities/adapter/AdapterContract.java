package com.google.codelabs.mdc.java.shrine.entities.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.entities.ContractBikeResponse;
import com.google.codelabs.mdc.java.shrine.utils.Common;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class AdapterContract extends BaseAdapter {


    private final List<ContractBikeResponse> contractBikeResponseList;

    public AdapterContract(List<ContractBikeResponse> contractBikeResponseList) {
        this.contractBikeResponseList = contractBikeResponseList;
    }

    @Override
    public int getCount() {
        return contractBikeResponseList.size();
    }

    @Override
    public Object getItem(int position) {
        return contractBikeResponseList.get(position);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //convertView là View của phần tử ListView, nếu convertView != null nghĩa là
        //View này được sử dụng lại, chỉ việc cập nhật nội dung mới
        //Nếu null cần tạo mới

        View viewProduct;
        if (convertView == null) {
            viewProduct = View.inflate(parent.getContext(), R.layout.item_adapter_contract, null);
        } else viewProduct = convertView;

        //Bind sữ liệu phần tử vào View
        ContractBikeResponse contractBikeResponse = (ContractBikeResponse) getItem(position);

        String startTime = Common.formatDate(contractBikeResponse.getStartTime());
        String endTime = Common.formatDate(contractBikeResponse.getEndTime());
        Double distance = contractBikeResponse.getDistance();
        distance = distance != null ? (double)Math.round(distance * 100) / 100 : 0;
        String distanceStr = distance + " km";

        ((TextView) viewProduct.findViewById(R.id.startTime)).setText(startTime);
        ((TextView) viewProduct.findViewById(R.id.endTime)).setText(endTime);
        ((TextView) viewProduct.findViewById(R.id.distance)).setText(distanceStr);
        ((TextView) viewProduct.findViewById(R.id.paymentMethod)).setText(contractBikeResponse.getPaymentMethod());

        return viewProduct;
    }
}
