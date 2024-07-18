package com.accommodation_management_booking.service;

import com.accommodation_management_booking.entity.Bed;

import java.util.List;

public interface BedService {

    void addBedToRoom(int roomId, String bedName);
    List<Bed> getAllBeds();
    Bed getBedById(int bedId);
    Bed updateBed(Bed bed);
    void deleteBed(int bedId);

    public boolean bedNameExistsInRoom(int roomId, String bedName);

    Bed getBedByName(String bedName);

    long countUsedBeds();
}
