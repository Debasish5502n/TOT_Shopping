package com.example.totshopping.Fregment;

import android.app.Dialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.totshopping.Adapter.RewardAdapter;
import com.example.totshopping.DatabaseQueries.DbQueries;
import com.example.totshopping.R;

public class MyRewardFragment extends Fragment {

    RecyclerView rewardRecyclerView;
    public static RewardAdapter rewardAdapter;
    private Dialog loadingDialog;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_my_reward, container, false);
        rewardRecyclerView=view.findViewById(R.id.my_reward_recyclerView);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        rewardRecyclerView.setLayoutManager(layoutManager);

        ////////////////loading dialog
        loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.banner_slider_baground));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        loadingDialog.show();
        ////////////////loading dialog

        rewardAdapter=new RewardAdapter(DbQueries.rewardModelList,false);
        rewardRecyclerView.setAdapter(rewardAdapter);

        if (DbQueries.rewardModelList.size() == 0){
            DbQueries.loadReward(getContext(),loadingDialog,true);
        }else {
            loadingDialog.dismiss();
        }


        rewardAdapter.notifyDataSetChanged();

        return view;
    }
}