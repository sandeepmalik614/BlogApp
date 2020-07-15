package com.blog.app.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blog.app.R;
import com.blog.app.adapter.PostAdapter;
import com.blog.app.model.PostData;
import com.blog.app.utils.AppConstant;
import com.blog.app.utils.AppUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView recyclerView;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<PostData> dataList;
    private ProgressDialog pd;
    private DatabaseReference reference;
    private PostAdapter adapter;
    private TextView tvNoData;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        refreshLayout = view.findViewById(R.id.swipeRefresh);
        refreshLayout.setOnRefreshListener(this);
        recyclerView = view.findViewById(R.id.recyclerView3);
        tvNoData = view.findViewById(R.id.textView15);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        pd = new ProgressDialog(getActivity());
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);
        reference = FirebaseDatabase.getInstance().getReference();
        reference.keepSynced(true);

        dataList = new ArrayList<>();
        adapter = new PostAdapter(getActivity(), dataList);
        recyclerView.setAdapter(adapter);

        if (AppUtils.isInternetAvailable(getActivity())) {
            getData();
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void getData() {
        pd.show();
        reference.child(AppConstant.POST_TABLE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        pd.dismiss();
                        dataList.clear();
                        try {
                            if(dataSnapshot.exists()) {
                                tvNoData.setVisibility(View.GONE);
                                recyclerView.setVisibility(View.VISIBLE);
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                        PostData postData = snapshot1.getValue(PostData.class);
                                        dataList.add(postData);
                                    }
                                }
                                Collections.reverse(dataList);
                                adapter.notifyDataSetChanged();
                            }else{
                                tvNoData.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                tvNoData.setText("No data available");
                            }
                        } catch (Exception e) {
                            Toast.makeText(getActivity(), "Exception: " +
                                    e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getActivity(), "DatabaseError: "+
                                error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onRefresh() {
        if (AppUtils.isInternetAvailable(getActivity())) {
            getData();
        } else {
            Toast.makeText(getActivity(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
        }
        refreshLayout.setRefreshing(false);
    }
}