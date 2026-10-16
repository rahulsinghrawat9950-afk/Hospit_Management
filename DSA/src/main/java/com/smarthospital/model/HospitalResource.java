package com.smarthospital.model;

public class HospitalResource {
    private String resourceId;
    private String name;
    private int capacity;
    private int occupied;

    public HospitalResource() {}

    public HospitalResource(String resourceId, String name, int capacity, int occupied) {
        this.resourceId = resourceId;
        this.name = name;
        this.capacity = capacity;
        this.occupied = occupied;
    }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }
}
