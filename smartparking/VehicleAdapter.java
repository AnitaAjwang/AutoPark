package com.example.smartparking;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.List;

public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.MyViewHolder> {
    Context context;
    List<VehicleData> data;

   VehicleAdapter(Context context, List<VehicleData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        view = LayoutInflater.from(context).inflate(R.layout.vehicle_adapter,parent,false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        holder.carModel.setText(data.get(position).getCar_model());
        holder.plateNumber.setText(data.get(position).getPlate_number());

/*        holder.frameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context,ParkingInformation.class);
                context.startActivity(intent);
            }
        });*/


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView plateNumber, carModel;
        private FrameLayout frameLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            plateNumber = (TextView) itemView.findViewById(R.id.plateNumber);
            carModel = (TextView) itemView.findViewById(R.id.carModel);
            frameLayout = (FrameLayout) itemView.findViewById(R.id.frameLayout1);

        }
    }
}