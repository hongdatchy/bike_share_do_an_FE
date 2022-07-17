package com.google.codelabs.mdc.java.shrine.bikeshare.ui.contract;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.codelabs.mdc.java.shrine.R;
import com.google.codelabs.mdc.java.shrine.activities.MapDetailRoutesContractActivity;
import com.google.codelabs.mdc.java.shrine.api.ApiService;
import com.google.codelabs.mdc.java.shrine.databinding.FragmentContractBinding;
import com.google.codelabs.mdc.java.shrine.entities.ContractBikeResponse;
import com.google.codelabs.mdc.java.shrine.entities.MyResponse;
import com.google.codelabs.mdc.java.shrine.entities.adapter.AdapterContract;
import com.google.codelabs.mdc.java.shrine.utils.Common;
import com.google.codelabs.mdc.java.shrine.utils.Constant;
import com.google.codelabs.mdc.java.shrine.utils.MyProgressDialog;
import com.google.codelabs.mdc.java.shrine.utils.MyStorage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContractFragment extends Fragment {

    private FragmentContractBinding binding;
    private MyProgressDialog myProgressDialog;
    private MyStorage myStorage;
    private AdapterContract adapterContract;
    ListView listViewContract;
    List<ContractBikeResponse> contractBikeResponseList;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentContractBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listViewContract = root.findViewById(R.id.list_view_contract);
        myProgressDialog = new MyProgressDialog(requireActivity());
        myStorage = new MyStorage(requireActivity());


        callApiGetAllContractUser();
        return root;
    }

    private void onclickListView(){
        listViewContract.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("sjbjsbsjs");
                Gson gson = Common.getMyGson();
                ContractBikeResponse contractBikeResponse = (ContractBikeResponse) listViewContract.getItemAtPosition(i);
                Intent myIntent = new Intent(requireActivity(), MapDetailRoutesContractActivity.class);

                myIntent.putExtra("routes", contractBikeResponse.getRoutes());
                requireActivity().startActivity(myIntent);
            }
        });
    }

    private void callApiGetAllContractUser() {
        String token = myStorage.get(Constant.TOKEN_KEY);
        myProgressDialog.show();
        ApiService.apiService.getAllContractUser(token).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(@NonNull Call<MyResponse> call, @NonNull Response<MyResponse> response) {

                MyResponse myResponse = response.body();
                assert myResponse != null;

                if(myResponse.getMessage().equals(Constant.SUCCESS_MESSAGE_CALL_API)){
                    Gson gson = Common.getMyGson();
                    String json = gson.toJson(myResponse.getData());
                    contractBikeResponseList =
                            gson.fromJson(json, new TypeToken<List<ContractBikeResponse>>(){}.getType());

                    adapterContract = new AdapterContract(contractBikeResponseList);
                    listViewContract.setAdapter(adapterContract);
                    onclickListView();
                }
                myProgressDialog.dismiss();
            }

            @Override
            public void onFailure(@NonNull Call<MyResponse> call, @NonNull Throwable t) {
                Toast.makeText(getActivity(),"Call api fail",Toast.LENGTH_SHORT).show();
                myProgressDialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}