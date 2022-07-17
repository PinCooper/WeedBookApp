package com.example.weedbookapp;

public class Weeds {
    public String id, nameWeed, nameWeedLat, latitude, longitude, address, area, date, comment, culture, coating, chemical, efficiency, imageID, userUID, userEmail;
    public Long dateAdded;
    public int numCulture, numCoating, numChemical, numEfficiency;

    public Weeds() {
    }

    public Weeds(String id, String nameWeed, String nameWeedLat, String latitude, String longitude, String address, String area, String date, String comment, String culture, int numCulture, String coating, int numCoating, String chemical, int numChemical, String efficiency, int numEfficiency, String imageID, String userUID, Long dateAdded, String userEmail) {
        this.id = id;
        this.nameWeed = nameWeed;
        this.nameWeedLat = nameWeedLat;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.area = area;
        this.date = date;
        this.comment = comment;
        this.culture = culture;
        this.numCulture = numCulture;
        this.coating = coating;
        this.numCoating = numCoating;
        this.chemical = chemical;
        this.numChemical = numChemical;
        this.efficiency = efficiency;
        this.numEfficiency = numEfficiency;
        this.imageID = imageID;
        this.userUID = userUID;
        this.dateAdded = dateAdded;
        this.userEmail = userEmail;
    }
}
