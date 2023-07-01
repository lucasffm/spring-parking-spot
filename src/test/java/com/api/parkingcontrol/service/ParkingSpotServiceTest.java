package com.api.parkingcontrol.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.api.parkingcontrol.models.ParkingSpotModel;
import com.api.parkingcontrol.repositories.ParkingSpotRepository;
import com.api.parkingcontrol.services.ParkingSpotService;
import com.github.javafaker.Faker;

@ExtendWith(MockitoExtension.class)
class ParkingSpotServiceTest {

  @Mock
  private ParkingSpotRepository parkingSpotRepository;

  Faker faker = new Faker();

  @Test
  void findAll_ShouldReturnPageOfParkingSpotModels() {
    // Arrange
    int pageSize = 10;
    Pageable pageable = Pageable.ofSize(pageSize).withPage(0);
    List<ParkingSpotModel> parkingSpotModels = new ArrayList<>();
    for (int i = 0; i < pageSize; i++) {
      ParkingSpotModel model = new ParkingSpotModel();
      model.setId(UUID.randomUUID());
      model.setApartment(faker.number().toString());
      model.setBlock(faker.name().firstName());
      model.setBrandCar(faker.aviation().aircraft());
      model.setColorCar(faker.color().name());
      model.setLicensePlateCar(faker.letterify("SOME", false));
      model.setParkingSpotNumber(faker.number().toString());
      model.setModelCar(faker.lorem().word());
      model.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
      model.setResponsibleName(faker.name().fullName());
      parkingSpotModels.add(model);
    }

    Page<ParkingSpotModel> parkingSpotPage = new PageImpl<>(parkingSpotModels);
    when(parkingSpotRepository.findAll(pageable)).thenReturn(parkingSpotPage);

    ParkingSpotService parkingSpotService = new ParkingSpotService(parkingSpotRepository);

    // Act
    Page<ParkingSpotModel> result = parkingSpotService.findAll(pageable);

    // Assert
    assertEquals(parkingSpotPage.getTotalElements(), result.getTotalElements());
    assertEquals(parkingSpotPage.getContent().size(), result.getContent().size());
    assertNotNull(result.getContent().get(0).getId());
    assertNotNull(result.getContent().get(0).getResponsibleName());
    // Add more assertions if needed

  }

}
