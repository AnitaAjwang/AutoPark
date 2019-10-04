package com.example.smartparking;

public class VehicleData {

    private String plate_number,car_model;

    public VehicleData()
    {

    }
    public VehicleData(String plate_number, String car_model)
    {
        this.plate_number = plate_number;
        this.car_model = car_model;

    }


    public String getPlate_number() {
        return plate_number;
    }

    public void setPlate_number(String plate_number) {
        this.plate_number = plate_number;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }


}
