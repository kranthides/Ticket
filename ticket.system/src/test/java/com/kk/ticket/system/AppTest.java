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
 * Unit test for simple App.
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

    /* Testing GetUser Calls  */
 /*   
	public void testGetOperation() { 
		Session session = factory.openSession();
		Criteria stageLevel = session.createCriteria(VenueLevel.class);
		
		
	    @SuppressWarnings("unchecked")
		final List<VenueLevel> slist = stageLevel.list();
	    
	    int count =0 ;
	    int totalSeats = 0;
	    for (final VenueLevel b : slist) {
	    	count++;
	    	totalSeats = totalSeats + b.getRemainingSeats();
	    }
		
		assertEquals(count, 4);
		assertEquals(totalSeats, 6250);

	}
*/	
	public void testHoldOperation() { 
		
    	TicketServiceImpl ts = new TicketServiceImpl(factory);
    	int seatsLevel1 = ts.numSeatsAvailable(1);
    	int seatsLevel2 = ts.numSeatsAvailable(2);
    	
		SeatHold sResp = new SeatHold();
		
		sResp = ts.findAndHoldSeats(10, 1, 2, "test@test.com");
		
		int assignedSeats = 0;
		for(LevelInfo li : sResp.getLevelInfo()){
			if (li.getLevelID() ==1) {
				assignedSeats = li.getSeatCount();
			}
		}
		
		assertEquals(seatsLevel1-assignedSeats, seatsLevel1-10);
		
		String reserveResp = ts.reserveSeats(sResp.getHoldID(), "test@test.com");
		System.out.println("sResp.getHoldID() " +sResp.getHoldID());

		System.out.println("Xyyyyyz " +reserveResp);
		HoldHeader hh = new HoldHeader();
		
		hh = ts.getHolds(sResp.getHoldID());
		System.out.println(hh.getReservedFlag());
 		assertTrue(hh.getReservedFlag().equals("Y"));
		
    	

	}
	
}
