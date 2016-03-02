package com.kk.ticket.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;

import com.kk.ticket.model.HoldHeader;
import com.kk.ticket.model.HoldLines;
import com.kk.ticket.model.LevelInfo;
import com.kk.ticket.model.SeatHold;
import com.kk.ticket.model.VenueLevel;

public class TicketServiceImpl implements TicketService{

	
	private static SessionFactory factory;
	private static int NoOfSeconds =10; 
	
	public TicketServiceImpl (SessionFactory sessionFactory){
		factory = sessionFactory;
	}
	
	public int numSeatsAvailable(int levelID) {
		// TODO Auto-generated method stub
		
		Session session = factory.openSession();
		
		Criteria c = session.createCriteria(VenueLevel.class);
		
		if(levelID > 0 ) { 
			c.add(Restrictions.like("levelID", levelID));
		}
			
		
	    @SuppressWarnings("unchecked")
		final List<VenueLevel> slist = c.list();
	    int totalRemainingSeats = 0;
	    
	    for (final VenueLevel b : slist) {	    	
	    	totalRemainingSeats = totalRemainingSeats+b.getRemainingSeats();	    	
	    }
	    int activeHolds= getActiveHoldCount(levelID);
	
		return totalRemainingSeats-activeHolds;
		
	}

	public SeatHold findAndHoldSeats(int numSeats, int minLevel, int maxLevel, String customerEmail) {
		// TODO Auto-generated method stub
		SeatHold resp = new SeatHold();
		
		HoldHeader hh = new HoldHeader();
		
		Set<HoldLines> holdLinesList = new HashSet<HoldLines>();  
				
		int unAssignedSeats = numSeats;
		String successFlag = "Y";
		
		for (int i= minLevel ; i<=maxLevel && unAssignedSeats>0 ; i++){
			
			HoldLines hl = new HoldLines();	
			int seatsAvailable = numSeatsAvailable(i); 
			int remainingSeats = seatsAvailable - unAssignedSeats;
			
			System.out.println("Seats Available ......"+seatsAvailable);
			
			if(seatsAvailable> 0 ){
				
				if (remainingSeats>=0){
					hl.setLevelID(i);
					hl.setSeatCount(unAssignedSeats);
					//insertHolds(customerEmail,i,unAssignedSeats);
					unAssignedSeats = 0;
					//Save The records Normally
				} 
				else {
					
					unAssignedSeats = unAssignedSeats - seatsAvailable;
					hl.setLevelID(i);
					hl.setSeatCount(seatsAvailable);										
				}		
				System.out.println("unAssignedSeats ...... " +unAssignedSeats);
				holdLinesList.add(hl);
			}
			else  { 
				
				if(i==maxLevel) { 
					successFlag = "N";
					resp.setReturnMessage("Unable to assign the seats. Seats are not available");
					System.out.println("Unable to assign the seats. Seats are not available" ); 
				}
			}
		}
		if (successFlag.equals("Y")) { 
			hh.setCustomerEmail(customerEmail);
			Date holdTime = new Date();
			System.out.println("Hold time "+holdTime);
			hh.setHoldTime(holdTime );
			//hs.setLevelID(levelID);
			hh.setReservedFlag("N");			
			hh.setHoldLines( holdLinesList);
			int holdID = saveHolds(hh,"N");
			
			resp.setHoldID(holdID);
			double totalPrice = 0;
			
			Set<LevelInfo> levelInfoList = new HashSet<LevelInfo>();  

			for (final HoldLines holdLines : hh.getHoldLines()) {
				LevelInfo l = new LevelInfo();
				l.setLevelID(holdLines.getLevelID());
				l.setSeatCount(holdLines.getSeatCount());
				VenueLevel vl = new VenueLevel();
				totalPrice = holdLines.getSeatCount() * vl.getPrice();
				levelInfoList.add(l);
			}
			resp.setPrice(totalPrice);
			resp.setLevelInfo(levelInfoList);
			System.out.println(holdID);
		}
		
			
		return resp;
	}

	public String reserveSeats(int seatHoldId, String customerEmail) {
		// TODO Auto-generated method stub
		String resp  = "";
		Date currentDate = new Date();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.SECOND, -NoOfSeconds);
		
		HoldHeader hh = getHolds(seatHoldId);
		
		if(hh.getHoldTime().compareTo(cal.getTime()) <0  ){
			resp = "This hold is expired";
		} else {			
			hh.setReservedFlag("Y");

			for(HoldLines hl : hh.getHoldLines()){
				VenueLevel vl = new VenueLevel();				
				vl = getVenueList(hl.getLevelID());
				int remainingSeats = vl.getRemainingSeats() - hl.getSeatCount();
				vl.setLevelID(hl.getLevelID());
				vl.setRemainingSeats(remainingSeats);
				saveVenue(vl);				
			}
			System.out.println("......... Updating the Header ..........");
			hh.setHoldHeaderID(seatHoldId);
			hh.setHoldLines(null);
			saveHolds(hh,"Y");
			System.out.println("......... Completed ..........");

			
		}
		
		return resp;
	}
	
	
	public int getActiveHoldCount(int levelID){ 
		int activeHolds = 0;
		
		Session session = factory.openSession();		
		Criteria holdC = session.createCriteria(HoldHeader.class,"hh");
		
		Date currentDate = new Date();
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.SECOND, -NoOfSeconds);

		holdC.add(Restrictions.between("holdTime", cal.getTime(), currentDate ));
		holdC.add(Restrictions.like("reservedFlag", "N"));	
		
		if(levelID > 0 ) {
			
			DetachedCriteria holdLinesSQ = DetachedCriteria.forClass(HoldLines.class, "hl")
 				    .add(Restrictions.eq("levelID", levelID))
 				    .setProjection( Projections.property("holdHeaderID") );
			holdC.add(Subqueries.propertyIn("holdHeaderID", holdLinesSQ));
		}		
		
	    @SuppressWarnings("unchecked")
		List<HoldHeader> holdList = holdC.list();
	    
	    System.out.println("Size of Holds " + holdList.size());
	    
	    for (final HoldHeader hh : holdList) {	 
	    	
	    	for(final HoldLines hl : hh.getHoldLines()) {	    		
	    		if(hl.getLevelID() == levelID) { 
	    			activeHolds = activeHolds +hl.getSeatCount();
	    			System.out.println("Hold Seat Count " + hl.getLevelID() +" ..... " + hl.getSeatCount() );
	    		}
	    	}
	    	
 	    	System.out.println("activeHolds " + activeHolds);
 	    	
	    	//activeHolds = activeHolds+b.getSeatCount();
	    }		
		return activeHolds;
	}

	public void saveVenue(VenueLevel vl) {

		Session session = factory.openSession();		
		Transaction tx = session.beginTransaction();	
		
		session.update(vl);						
		tx.commit();
		session.close();
		
	}	

	
	public int saveHolds(HoldHeader hh, String skipLines) {

		Session session = factory.openSession();		
		Transaction tx = session.beginTransaction();		
		HoldHeader saveHeader = new HoldHeader();
		
		saveHeader.setCustomerEmail(hh.getCustomerEmail());
		saveHeader.setHoldTime(hh.getHoldTime());
		saveHeader.setReservedFlag(hh.getReservedFlag());
		saveHeader.setHoldHeaderID(hh.getHoldHeaderID());
		int holdID;
	

		session.saveOrUpdate(saveHeader);			
		holdID = saveHeader.getHoldHeaderID();

		saveHeader.setHoldHeaderID(holdID);
		Set<HoldLines> holdLinesList = new HashSet<HoldLines>();  
		if(!(hh.getHoldLines() == null )) { 
			for(HoldLines hl: hh.getHoldLines()){			
				hl.setHoldHeaderID(holdID);						
				holdLinesList.add(hl);
				session.save(hl);
			}	
		}
		tx.commit();
		session.close();

		return holdID;
	}	
	
	public VenueLevel getVenueList(int levelID) {
		// TODO Auto-generated method stub
		
		VenueLevel resp = new VenueLevel();
		Session session = factory.openSession();
		
		Criteria c = session.createCriteria(VenueLevel.class);
		c.add(Restrictions.like("levelID", levelID));		
					
	    @SuppressWarnings("unchecked")
		final List<VenueLevel> slist = c.list();
	    
	    for (final VenueLevel b : slist) {
	    	resp = b;
	    }
		return resp;
	}
	
	public HoldHeader getHolds(int holdHeaderID){
		HoldHeader resp = new HoldHeader();
		
		Session session = factory.openSession();
		
		Criteria c = session.createCriteria(HoldHeader.class);
		c.add(Restrictions.like("holdHeaderID", holdHeaderID));		

		@SuppressWarnings("unchecked")
		List<HoldHeader> holdList =  c.list();
		
		for(HoldHeader hh: holdList) {
			resp = hh;
		}
		
		return resp;
		
	}
	
}