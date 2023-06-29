package com.api.parkingcontrol.controllers;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.parkingcontrol.dtos.ParkingSpotDto;
import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.services.ParkingSpotService;

import jakarta.validation.Valid;

@RestController()
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {
  final ParkingSpotService parkingSpotService;

  public ParkingSpotController(ParkingSpotService parkingSpotService) {
    this.parkingSpotService = parkingSpotService;
  }

  @PostMapping
  public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid ParkingSpotDto body) {
    if (parkingSpotService.existsByLicensePlateCar(body.getLicensePlateCar())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Parking spot already exists!");
    }
    if (parkingSpotService.existsByParkingSpotNumber(body.getParkingSpotNumber())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body("Parking spot already exists!");
    }
    if (parkingSpotService.existsByApartmentAndBlock(body.getApartment(), body.getBlock())) {
      return ResponseEntity.status(HttpStatus.CONFLICT)
          .body("Parking spot already exists for this apartment and block!");
    }

    ParkingSpotModel parkingSpotModel = new ParkingSpotModel();
    BeanUtils.copyProperties(body, parkingSpotModel);
    parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
    return ResponseEntity.status(HttpStatus.CREATED).body(parkingSpotService.save(parkingSpotModel));
  }

  @GetMapping
  public ResponseEntity<Page<ParkingSpotModel>> getAllParkingSpots(
      @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.findAll(pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<Object> getParkingSpotById(@PathVariable("id") UUID id) {
    Optional<ParkingSpotModel> parkingSpot = parkingSpotService.findById(id);
    if (!parkingSpot.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found!");
    }
    return ResponseEntity.status(HttpStatus.OK).body(parkingSpot.get());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Object> deleteParkingSpot(@PathVariable("id") UUID id) {
    Optional<ParkingSpotModel> parkingSpot = parkingSpotService.findById(id);
    if (!parkingSpot.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found!");
    }
    parkingSpotService.delete(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }

  @PutMapping("/{id}")
  public ResponseEntity<Object> updateParkingSpot(@PathVariable("id") UUID id,
      @RequestBody @Valid ParkingSpotDto body) {
    Optional<ParkingSpotModel> parkingSpot = parkingSpotService.findById(id);
    if (!parkingSpot.isPresent()) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Parking spot not found!");
    }
    var parkingSpotModel = new ParkingSpotModel();
    BeanUtils.copyProperties(body, parkingSpotModel);
    parkingSpotModel.setId(parkingSpot.get().getId());
    parkingSpotModel.setRegistrationDate(parkingSpot.get().getRegistrationDate());

    return ResponseEntity.status(HttpStatus.OK).body(parkingSpotService.save(parkingSpotModel));
  }
}
