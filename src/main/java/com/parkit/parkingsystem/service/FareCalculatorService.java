package com.parkit.parkingsystem.service;

import java.util.Calendar;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    public void calculateFare(Ticket ticket) {
	if ((ticket.getOutTime() == null) || (ticket.getOutTime().before(ticket.getInTime()))) {
	    throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
	}

	Calendar inHour = ticket.getInTime();
	Calendar outHour = ticket.getOutTime();
	boolean isFivePercentElligible = ticket.getIsExistingUser();
	long inHourMillis = inHour.getTimeInMillis();
	long outHourMillis = outHour.getTimeInMillis();
	long duration = outHourMillis - inHourMillis;
	if (duration < 1800000) {
	    switch (ticket.getParkingSpot().getParkingType()) {
	    case CAR: {
		ticket.setPrice(0 * Fare.CAR_RATE_PER_HOUR);
		break;
	    }
	    case BIKE: {
		ticket.setPrice(0 * Fare.BIKE_RATE_PER_HOUR);
		break;
	    }
	    default:
		throw new IllegalArgumentException("Unkown Parking Type");
	    }
	} else if (isFivePercentElligible) {
	    switch (ticket.getParkingSpot().getParkingType()) {
	    case CAR: {
		ticket.setPrice(((duration * Fare.CAR_RATE_PER_HOUR) / 3600000)
			- (((duration * Fare.CAR_RATE_PER_HOUR) / 3600000) * 0.05));
		break;
	    }
	    case BIKE: {
		ticket.setPrice(((duration * Fare.BIKE_RATE_PER_HOUR) / 3600000)
			- (((duration * Fare.BIKE_RATE_PER_HOUR) / 3600000) * 0.05));
		break;
	    }
	    default:
		throw new IllegalArgumentException("Unkown Parking Type");
	    }

	} else {
	    switch (ticket.getParkingSpot().getParkingType()) {
	    case CAR: {
		ticket.setPrice((duration * Fare.CAR_RATE_PER_HOUR) / 3600000);
		break;
	    }
	    case BIKE: {
		ticket.setPrice((duration * Fare.BIKE_RATE_PER_HOUR) / 3600000);
		break;
	    }
	    default:
		throw new IllegalArgumentException("Unkown Parking Type");
	    }
	}
    }
}