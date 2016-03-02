package com.kk.ticket.system;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import com.kk.ticket.model.HoldHeader;
import com.kk.ticket.model.LevelInfo;
import com.kk.ticket.model.SeatHold;
import com.kk.ticket.model.VenueLevel;
import com.kk.ticket.service.TicketServiceImpl;
import com.kk.ticket.util.TicketServiceUtil;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for Ticketing App.
 */
public class AppTest 
    extends TestCase
{
	private static SessionFactory factory;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {    	
        super( testName );
		TicketServiceUtil util = new TicketServiceUtil();
		factory= util.createHibernateSession();        
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }
    
    
    

 
	public void testHoldOperation() { 
		
    	TicketServiceImpl ts = new TicketServiceImpl(factory);
    	
    	// Testing the Num Seats Available Call 
    	int seatsLevel1 = ts.numSeatsAvailable(1);
    	
		SeatHold sResp = new SeatHold();
		
		
		
    	// Testing the Find and Hold Seats Service 

		sResp = ts.findAndHoldSeats(10, 1, 2, "test@test.com");
		
		int assignedSeats = 0;
		for(LevelInfo li : sResp.getLevelInfo()){
			if (li.getLevelID() ==1) {
				assignedSeats = li.getSeatCount();
			}
		}
		
		assertEquals(seatsLevel1-assignedSeats, seatsLevel1-10);
		
		
    	/**Testing the Reserve seats Service 
    	 * Testing the following scenarios  
    	 * 1. Reserve Flag must be updated to Y 
    	 * 2. Remaining should be reduced to the updated value
    	 */

		String reserveResp = ts.reserveSeats(sResp.getHoldID(), "test@test.com");
		System.out.println("sResp.getHoldID() " +sResp.getHoldID());

		HoldHeader hh = new HoldHeader();
		
		hh = ts.getHolds(sResp.getHoldID());
 		assertTrue(hh.getReservedFlag().equals("Y"));
		
 		int availableCount = ts.numSeatsAvailable(1);
 		assertEquals(seatsLevel1-10,availableCount);
 		
	}
			
}
