package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;

public class FareCalculatorServiceTest {

    private static FareCalculatorService fareCalculatorService;
    private Ticket ticket;

    @BeforeAll
    private static void setUp() {
	fareCalculatorService = new FareCalculatorService();
    }

    @BeforeEach
    private void setUpPerTest() {
	ticket = new Ticket();
    }

    @Test
    public void calculateFareCar() {
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));
	Calendar outTime = Calendar.getInstance();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareBike() {
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));
	Calendar outTime = Calendar.getInstance();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertEquals(ticket.getPrice(), Fare.BIKE_RATE_PER_HOUR);
    }

    @Test
    public void calculateFareUnkownType() {
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));
	Calendar outTime = Calendar.getInstance();
	ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithFutureInTime() {
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() + (60 * 60 * 1000));
	Calendar outTime = Calendar.getInstance();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    @Test
    public void calculateFareBikeWithLessThanOneHourParkingTime() {
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give
									      // 3/4th parking fare
	Calendar outTime = Calendar.getInstance();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithLessThanOneHourParkingTime() {
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() - (45 * 60 * 1000));// 45 minutes parking time should give
									      // 3/4th parking fare
	Calendar outTime = Calendar.getInstance();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarLessThanThirtyMinutes() {
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() - (29 * 60 * 1000));// under 30 minutes parking time should
									      // give
									      // 0$ parking fare
	Calendar outTime = Calendar.getInstance();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertEquals((0 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void calculateFareCarWithMoreThanADayParkingTime() {
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() - (25 * 60 * 60 * 1000));// 25 hours parking time should give
										   // 25 * parking fare per hour
	Calendar outTime = Calendar.getInstance();
	ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

	ticket.setInTime(inTime);
	ticket.setOutTime(outTime);
	ticket.setParkingSpot(parkingSpot);
	fareCalculatorService.calculateFare(ticket);
	assertEquals((25 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    @Test
    public void fivePercentDiscountReturningCustomer() {
	// when registration number already exists in DB on exit apply 5% discount on
	// fare
	Ticket fivePercentTicket = new Ticket();
	fivePercentTicket.setVehicleRegNumber("AEIOU");
	fivePercentTicket.setIsExistingUser(true);
	Calendar inTime = Calendar.getInstance();
	inTime.setTimeInMillis(System.currentTimeMillis() - (60 * 60 * 1000));
	Calendar outTime = Calendar.getInstance();
	ParkingSpot secondParkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
	fivePercentTicket.setInTime(inTime);
	fivePercentTicket.setOutTime(outTime);
	fivePercentTicket.setParkingSpot(secondParkingSpot);
	fareCalculatorService.calculateFare(fivePercentTicket);
	assertEquals((Fare.CAR_RATE_PER_HOUR - (Fare.CAR_RATE_PER_HOUR * 0.05)), fivePercentTicket.getPrice());
    }
}
