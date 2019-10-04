package com.example.smartparking;

public class RecyclerViewData {
  private int parking_image;
  private String parking_name,parking_availability, parking_distance, parking_price;

  public RecyclerViewData()
  {

  }
  public RecyclerViewData(int parking_image, String parking_name, String parking_availability, String parking_distance, String parking_price)
  {
    this.parking_image = parking_image;
    this.parking_name = parking_name;
    this.parking_availability = parking_availability;
    this.parking_distance= parking_distance;
    this.parking_price = parking_price;
  }


  public int getParking_image() {
    return parking_image;
  }

  public void setParking_image(int parking_image) {
    this.parking_image = parking_image;
  }

  public String getParking_name() {
    return parking_name;
  }

  public void setParking_name(String parking_name) {
    this.parking_name = parking_name;
  }

  public String getParking_availability() {
    return parking_availability;
  }

  public void setParking_availability(String parking_availability) {
    this.parking_availability = parking_availability;
  }

  public String getParking_distance() {
    return parking_distance;
  }

  public void setParking_distance(String parking_distance) {
    this.parking_distance = parking_distance;
  }

  public String getParking_price() {
    return parking_price;
  }

  public void setParking_price(String parking_price) {
    this.parking_price = parking_price;
  }
}
