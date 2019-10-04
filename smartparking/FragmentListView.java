package com.example.smartparking;



import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class FragmentListView extends Fragment  {
  View view;
  private RecyclerView recyclerView;
  //  private RecyclerViewAdapter recyclerViewAdapter;
  private RecyclerView.LayoutManager layoutManager;
  private FirebaseAuth firebaseAuth;

  private List<RecyclerViewData> recyclerViewData;

  public FragmentListView(){
  }
  @Nullable

  public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
    view = layoutInflater.inflate(R.layout.activity_fragment_list_view,container,false);


    recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview3);
    RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(),recyclerViewData);
    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    recyclerView.setAdapter(recyclerViewAdapter);

    return view;
  }

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initData();
  }
  private void initData() {
    recyclerViewData = new ArrayList<>();
    recyclerViewData.add(new RecyclerViewData(R.drawable.parkinglot,"Indigo Parking","30% available","5 km away","50 bob per hour"));
    recyclerViewData.add(new RecyclerViewData(R.drawable.parkinglot,"Indigo Parking","30% available","5 km away","50 bob per hour"));
  }

}
