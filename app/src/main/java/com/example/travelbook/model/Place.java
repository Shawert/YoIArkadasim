package com.example.travelbook.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity
public class Place implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "PlaceName")
    public  String name;

    @ColumnInfo(name = "PlaceComment")
    public  String comment;
    @ColumnInfo(name = "latitude")
    public  Double lat;
    @ColumnInfo(name = "longitude")
    public  Double lot;

    public  Place(String name,String comment,Double lat,Double lot){
        this.name = name;
        this.comment = comment;
        this.lat =lat;
        this.lot =lot;
    }
}
