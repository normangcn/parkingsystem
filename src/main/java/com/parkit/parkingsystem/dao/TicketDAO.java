package com.parkit.parkingsystem.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;

public class TicketDAO {

    private static final Logger logger = LogManager.getLogger("TicketDAO");

    public DataBaseConfig dataBaseConfig = new DataBaseConfig();

    public boolean saveTicket(Ticket ticket) {
	Connection con = null;
	PreparedStatement ps = null;
	try {
	    con = dataBaseConfig.getConnection();
	    ps = con.prepareStatement(DBConstants.SAVE_TICKET);
	    // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
	    // ps.setInt(1,ticket.getId());
	    ps.setInt(1, ticket.getParkingSpot().getId());
	    ps.setString(2, ticket.getVehicleRegNumber());
	    ps.setDouble(3, ticket.getPrice());
	    ps.setTimestamp(4, new Timestamp(ticket.getInTime().getTimeInMillis()));
	    ps.setTimestamp(5,
		    (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().getTimeInMillis())));
	    ps.setBoolean(6, ticket.getIsExistingUser());
	    return ps.execute();

	} catch (Exception ex) {
	    logger.error("Error fetching next available slot", ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closePreparedStatement(ps);
	}
	return false;
    }

    public Ticket getTicket(String vehicleRegNumber) {
	Connection con = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
	Ticket ticket = null;
	Timestamp inTimeTimeStampQuery;
	Timestamp outTimeTimeStampQuery;
	try {
	    con = dataBaseConfig.getConnection();
	    ps = con.prepareStatement(DBConstants.GET_TICKET);
	    ps.setString(1, vehicleRegNumber);
	    // ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
	    rs = ps.executeQuery();
	    if (rs.next()) {
		ticket = new Ticket();
		ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(7)), false);
		ticket.setParkingSpot(parkingSpot);
		ticket.setId(rs.getInt(2));
		ticket.setVehicleRegNumber(vehicleRegNumber);
		ticket.setPrice(rs.getDouble(3));
		inTimeTimeStampQuery = rs.getTimestamp(4);
		Calendar inTime = Calendar.getInstance();
		inTime.setTimeInMillis(inTimeTimeStampQuery.getTime());
		ticket.setInTime(inTime);
		outTimeTimeStampQuery = rs.getTimestamp(5);
		Calendar outTime = Calendar.getInstance();
		outTime.setTimeInMillis(outTimeTimeStampQuery.getTime());
		ticket.setOutTime(outTime);
		ticket.setIsExistingUser(rs.getBoolean(6));
	    }
	} catch (Exception ex) {
	    logger.error("Error fetching next available slot", ex);
	} finally {

	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closePreparedStatement(ps);
	    dataBaseConfig.closeResultSet(rs);
	}
	return ticket;
    }

    public boolean updateTicket(Ticket ticket) {
	Connection con = null;
	PreparedStatement ps = null;
	try {
	    con = dataBaseConfig.getConnection();
	    ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
	    ps.setDouble(1, ticket.getPrice());
	    ps.setTimestamp(2, new Timestamp(ticket.getOutTime().getTimeInMillis()));
	    ps.setInt(3, ticket.getId());
	    ps.execute();
	    return true;
	} catch (Exception ex) {
	    logger.error("Error saving ticket info", ex);
	} finally {
	    dataBaseConfig.closeConnection(con);
	    dataBaseConfig.closePreparedStatement(ps);
	}
	return false;
    }
}
