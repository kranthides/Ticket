package com.kk.ticket.system;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.kk.ticket.model.HoldHeader;
import com.kk.ticket.model.LevelInfo;
import com.kk.ticket.model.SeatHold;
import com.kk.ticket.service.TicketServiceImpl;
import com.kk.ticket.util.TicketServiceUtil;
import static org.junit.Assert.*;

/**
 * Unit test for Ticketing App.
 */
public class AppTest  {
	private static SessionFactory factory;

	
    @BeforeClass
    public static void  setUpDB() throws Exception
    {
		TicketServiceUtil util = new TicketServiceUtil();
		factory = util.createHibernateSession();
    }


	/*
	 * This is a happy path scenario, Customer queries the records Customer
	 * Holds the seats Customer reserve the seats
	 */
    
    @Test
	public void testScenario1() {

		TicketServiceImpl ts = new TicketServiceImpl(factory);

		// Testing the Num Seats Available Call
		int seatsLevel1 = ts.numSeatsAvailable(1);

		SeatHold sResp = new SeatHold();

		// Testing the Find and Hold Seats Service

		sResp = ts.findAndHoldSeats(10, 1, 2, "test@test.com");

		int assignedSeats = 0;
		for (LevelInfo li : sResp.getLevelInfo()) {
			if (li.getLevelID() == 1) {
				assignedSeats = li.getSeatCount();
			}
		}
		
		assertEquals(seatsLevel1 - assignedSeats, seatsLevel1 - 10);

		/**
		 * Testing the Reserve seats Service Testing the following scenarios 1.
		 * Reserve Flag must be updated to Y 2. Remaining should be reduced to
		 * the updated value
		 */

		String reserveResp = ts.reserveSeats(sResp.getHoldID(), "test@test.com");

		HoldHeader hh = new HoldHeader();

		hh = ts.getHolds(sResp.getHoldID());
		assertTrue(hh.getReservedFlag().equals("Y"));

		int availableCount = ts.numSeatsAvailable(1);
		assertEquals(seatsLevel1 - 10, availableCount);

	}

	/*
	 * Scenario 2 : Expired Hold Customer queries the records Customer Holds the
	 * seats Customer reserve the seats
	 */
    @Test
	public void testScenario2() {

		TicketServiceImpl ts = new TicketServiceImpl(factory);

		// Testing the Num Seats Available Call
		int seatsLevel1 = ts.numSeatsAvailable(1);

		SeatHold sResp = new SeatHold();

		// Find and Hold Seats Service

		sResp = ts.findAndHoldSeats(10, 1, 2, "test@test.com");

		int assignedSeats = 0;
		for (LevelInfo li : sResp.getLevelInfo()) {
			if (li.getLevelID() == 1) {
				assignedSeats = li.getSeatCount();
			}
		}

		assertEquals(seatsLevel1 - assignedSeats, seatsLevel1 - 10);

		/**
		 * Testing the Reserve seats Service Testing the following scenarios 1.
		 * Hold should not be expired, In the current logic it is set to 30
		 * seconds 2. Reserve Flag must be updated to Y 3. Remaining should be
		 * reduced to the updated value
		 */

		try {
			System.out.println("Waiting for 31 seconds ");
			Thread.sleep(31000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Assert.fail("Test failed : " + e.getMessage());
		} // 1000 milliseconds is one second.

		String reserveResp = ts.reserveSeats(sResp.getHoldID(), "test@test.com");

		assertEquals("This hold is expired", reserveResp);

	}

	/*
	 * Scenario 3 : Reserving the same hold Twice Customer queries the records
	 * Customer Holds the seats Customer reserve the seats
	 */
    @Test
	public void testScenario3() {

		TicketServiceImpl ts = new TicketServiceImpl(factory);

		// Testing the Num Seats Available Call
		int seatsLevel1 = ts.numSeatsAvailable(1);

		SeatHold sResp = new SeatHold();

		// Find and Hold Seats Service

		sResp = ts.findAndHoldSeats(10, 1, 2, "test@test.com");

		int assignedSeats = 0;
		for (LevelInfo li : sResp.getLevelInfo()) {
			if (li.getLevelID() == 1) {
				assignedSeats = li.getSeatCount();
			}
		}

		assertEquals(seatsLevel1 - assignedSeats, seatsLevel1 - 10);

		/**
		 * Testing the Reserve seats Service Testing the following scenarios 1.
		 * Reserving the same hold twice
		 */

		String reserveResp = ts.reserveSeats(sResp.getHoldID(), "test@test.com");

		reserveResp = ts.reserveSeats(sResp.getHoldID(), "test@test.com");

		assertEquals("This hold is already reserved", reserveResp);

	}
	
	/*
	 * Scenario 4 : Hold more number of seats than available
	 */
    @Test
	public void testScenario4() {

		TicketServiceImpl ts = new TicketServiceImpl(factory);

		// Testing the Num Seats Available Call
		int seatsLevel1 = ts.numSeatsAvailable(1);

		SeatHold sResp = new SeatHold();

		// Find and Hold Seats Service

		sResp = ts.findAndHoldSeats(1350, 1, 1, "test@test.com");

		//assertEquals("",)
		assertEquals(0,sResp.getHoldID());

		/**
		 * Testing the Reserve seats Service Testing the following scenarios 1.
		 * Reserving the same hold twice
		 */


	}
	

}
