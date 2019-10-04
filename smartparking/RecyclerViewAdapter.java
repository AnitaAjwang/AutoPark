package com.example.smartparking;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

  Context context;
  List<RecyclerViewData> data;

  public RecyclerViewAdapter(Context context, List<RecyclerViewData> data) {
    this.context = context;
    this.data = data;
  }

  @NonNull
  @Override
  public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

    View view;
    view = LayoutInflater.from(context).inflate(R.layout.item_layout,parent,false);
    MyViewHolder viewHolder = new MyViewHolder(view);
    return viewHolder;
  }

  @Override
  public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {



    holder.parkingName.setText(data.get(position).getParking_name());
    holder.parkingAvailability.setText(data.get(position).getParking_availability());
    holder.parkingDistance.setText(data.get(position).getParking_distance());
    holder.parkingPrice.setText(data.get(position).getParking_price());
    holder.parkingImage.setImageResource(data.get(position).getParking_image());

    holder.frameLayout.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent intent = new Intent(context,ParkingInformation.class);
        context.startActivity(intent);
      }
    });


  }

  @Override
  public int getItemCount() {
    return data.size();
  }

  public static class MyViewHolder extends RecyclerView.ViewHolder {

    private TextView parkingName, parkingAvailability, parkingDistance, parkingPrice;
    private ImageView parkingImage;
    private FrameLayout frameLayout;

    public MyViewHolder(@NonNull View itemView) {
      super(itemView);

      parkingName = (TextView) itemView.findViewById(R.id.parkingname);
      parkingAvailability = (TextView) itemView.findViewById(R.id.parkingavailability);
      parkingDistance = (TextView) itemView.findViewById(R.id.parkingdistance);
      parkingPrice = (TextView) itemView.findViewById(R.id.parkingprice);
      parkingImage = (ImageView) itemView.findViewById(R.id.thumbnail);
      frameLayout = (FrameLayout) itemView.findViewById(R.id.frameLayout);

    }
  }
}