package com.kk.ticket.service;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;

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

public class TicketServiceImpl implements TicketService {

	private static Logger log = Logger.getLogger(TicketServiceImpl.class);

	private static SessionFactory factory;
	private static int NoOfSeconds = 30;
	private final static int defaultMinLevel = 1;
	private final static int defaultMaxLevel = 4;
	private final static String G_ERROR_MSG = "ERROR";
	private final static String G_SUCCESS_MSG = "SUCCESS";
	private String errorMessage = "";

	public TicketServiceImpl(SessionFactory sessionFactory) {
		factory = sessionFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kk.ticket.service.TicketService#numSeatsAvailable(int)
	 */

	public int numSeatsAvailable(int levelID) {

		Session session = factory.openSession();

		Criteria c = session.createCriteria(VenueLevel.class);

		/*
		 * This conditional will restrict the results to specific level
		 */
		if (levelID > 0) {
			c.add(Restrictions.like("levelID", levelID));
		}

		@SuppressWarnings("unchecked")
		final List<VenueLevel> slist = c.list();
		int totalRemainingSeats = 0;

		for (final VenueLevel b : slist) {
			totalRemainingSeats = totalRemainingSeats + b.getRemainingSeats();
		}
		int activeHolds = getActiveHoldCount(levelID);

		return totalRemainingSeats - activeHolds;

	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.kk.ticket.service.TicketService#findAndHoldSeats(int, int, int,
	 * java.lang.String) Returns the SeatHold HoldID, TotalPrice, LevelInfo
	 * Array [Level ID, Total Seats ]
	 */

	public SeatHold findAndHoldSeats(int numSeats, int minLevel, int maxLevel, String customerEmail) {
		// TODO Auto-generated method stub
		SeatHold resp = new SeatHold();

		resp.setReturnMessage(G_SUCCESS_MSG);
		HoldHeader hh = new HoldHeader();

		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();

		Set<HoldLines> holdLinesList = new HashSet<HoldLines>();

		/*
		 * Since MinLevel and MaxLevel are optional, this code defaults the
		 * Minlevel to 1 and MaxLevel to 4 ..
		 */

		if (minLevel == 0) {
			minLevel = defaultMinLevel;
		}
		if (maxLevel == 0) {
			maxLevel = defaultMaxLevel;
		}

		int unAssignedSeats = numSeats;
		String successFlag = "Y";

		/*
		 * Logic to determine the best available seats per level.
		 * 
		 * Algorithm always start with lower Level ID (Assuming 1 being the best
		 * available seating ) and checks for the available seats. If the lower
		 * level ID has insufficient number of seats, it captures seats that
		 * available in the level and goes to the next level.
		 * 
		 */

		for (int i = minLevel; i <= maxLevel && unAssignedSeats > 0; i++) {
			HoldLines hl = new HoldLines();
			int seatsAvailable = numSeatsAvailable(i);
			int remainingSeats = seatsAvailable - unAssignedSeats;

			log.info("Seats Available ......" + seatsAvailable);

			if (seatsAvailable > 0) {

				if (remainingSeats >= 0) {
					hl.setLevelID(i);
					hl.setSeatCount(unAssignedSeats);
					// insertHolds(customerEmail,i,unAssignedSeats);
					unAssignedSeats = 0;
					// Save The records Normally
				} else {

					unAssignedSeats = unAssignedSeats - seatsAvailable;
					hl.setLevelID(i);
					hl.setSeatCount(seatsAvailable);
				}
				log.debug("unAssignedSeats ...... " + unAssignedSeats);
				holdLinesList.add(hl);
			} else {
				/*
				 * 
				 * If the program is unable to assign seats, program will exit
				 * out. It is All or None
				 * 
				 */
				if (i == maxLevel) {
					successFlag = "N";
					resp.setReturnMessage("Unable to assign the seats. Seats are not available");
					log.info("Unable to assign the seats. Seats are not available");
				}
			}
		}
		if(unAssignedSeats > 0){
			successFlag = "N";
			resp.setReturnMessage("Unable to assign the seats. Seats are not available");
			log.info("Unable to assign the seats. Seats are not available");			
		}

		/*
		 * Once the program determines the best available seats, data will be
		 * stored in the HOLD_HEADERS and HOLD_LINES Tables
		 * 
		 */
		if (successFlag.equals("Y")) {
			hh.setCustomerEmail(customerEmail);
			Date holdTime = new Date();
			log.info("Hold time " + holdTime);
			hh.setHoldTime(holdTime);
			// hs.setLevelID(levelID);
			hh.setReservedFlag("N");
			hh.setHoldLines(holdLinesList);
			int holdID = saveHolds(hh, session);
			if (holdID > 0) {
				resp.setHoldID(holdID);
				double totalPrice = 0;
				
				Set<LevelInfo> levelInfoList = new HashSet<LevelInfo>();

				for (final HoldLines holdLines : hh.getHoldLines()) {
					LevelInfo l = new LevelInfo();
					l.setLevelID(holdLines.getLevelID());
					l.setSeatCount(holdLines.getSeatCount());
					VenueLevel vl = new VenueLevel();
					vl = getVenueList(holdLines.getLevelID());
					totalPrice = totalPrice+ holdLines.getSeatCount() * vl.getPrice();
					levelInfoList.add(l);
				}
				resp.setPrice(totalPrice);
				resp.setLevelInfo(levelInfoList);
				tx.commit();
				log.info(holdID);

			} else {
				tx.rollback();
				resp.setReturnMessage(G_ERROR_MSG + " - " + errorMessage);
			}
		}

		session.close();
		return resp;
	}

	/*
	 * 
	 * (non-Javadoc)
	 * 
	 * @see com.kk.ticket.service.TicketService#reserveSeats(int,
	 * java.lang.String)
	 * 
	 * This function reserve the seats for a give hold ID
	 */

	public String reserveSeats(int seatHoldId, String customerEmail) {
		// TODO Auto-generated method stub
		String resp = "";
		Date currentDate = new Date();

		Session session = factory.openSession();
		Transaction tx = session.beginTransaction();

		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.SECOND, -NoOfSeconds);

		HoldHeader hh = getHolds(seatHoldId);
		/*
		 * If the user entered invalid hold ID , it returns the Error; If the
		 * user entered expired hold ID, it returns the error
		 */

		if (hh.getHoldHeaderID() == 0) {

			resp = "Invalid Hold ID";

		} else if (hh.getReservedFlag().equals("Y")) {
			resp = "This hold is already reserved";
		} else if (hh.getHoldTime().compareTo(cal.getTime()) < 0) {

			resp = "This hold is expired";

		} else if (!(hh.getCustomerEmail().equals(customerEmail))) {
			resp = "Invalid Email Address ";
		}
		else {
			/*
			 * If the hold is not expired Following logic updates two tables 1.
			 * Venue Level with Remaining Seats available 2. Reserved flag with
			 * "Y" in Holds Header table
			 * 
			 */
			hh.setReservedFlag("Y");

			String errorFlag = "N";
			for (HoldLines hl : hh.getHoldLines()) {
				VenueLevel vl = new VenueLevel();
				vl = getVenueList(hl.getLevelID());
				int remainingSeats = vl.getRemainingSeats() - hl.getSeatCount();
				vl.setLevelID(hl.getLevelID());
				vl.setRemainingSeats(remainingSeats);
				String saveVenueResp = saveVenue(vl, session);

				if (saveVenueResp.equals(G_ERROR_MSG)) {
					errorFlag = "Y";
				}

			}
			if (errorFlag.equals("N")) {
				hh.setHoldHeaderID(seatHoldId);
				hh.setHoldLines(null);
				seatHoldId = saveHolds(hh, session);
				log.info("Seat Hold " + seatHoldId);
				if (seatHoldId > 0) {
					resp = G_SUCCESS_MSG;
					tx.commit();
				} else {
					resp = G_ERROR_MSG + " - " + errorMessage;
					tx.rollback();

				}
			} else {
				resp = G_ERROR_MSG + " - " + errorMessage;
				tx.commit();
				tx.rollback();
			}

		}
		session.close();
		return resp;
	}

	/*
	 * Function to determine the active holds This includes Available Seats and
	 * seats that are not expired in the holds tables
	 * 
	 */
	public int getActiveHoldCount(int levelID) {
		int activeHolds = 0;

		Session session = factory.openSession();
		Criteria holdC = session.createCriteria(HoldHeader.class, "hh");

		Date currentDate = new Date();

		Calendar cal = Calendar.getInstance();
		cal.setTime(currentDate);
		cal.add(Calendar.SECOND, -NoOfSeconds);

		holdC.add(Restrictions.between("holdTime", cal.getTime(), currentDate));
		holdC.add(Restrictions.like("reservedFlag", "N"));

		if (levelID > 0) {

			DetachedCriteria holdLinesSQ = DetachedCriteria.forClass(HoldLines.class, "hl")
					.add(Restrictions.eq("levelID", levelID)).setProjection(Projections.property("holdHeaderID"));
			holdC.add(Subqueries.propertyIn("holdHeaderID", holdLinesSQ));
		}

		@SuppressWarnings("unchecked")
		List<HoldHeader> holdList = holdC.list();

		log.info("Size of Holds " + holdList.size());

		for (final HoldHeader hh : holdList) {

			for (final HoldLines hl : hh.getHoldLines()) {
				if (hl.getLevelID() == levelID) {
					activeHolds = activeHolds + hl.getSeatCount();
					log.info("Hold Seat Count " + hl.getLevelID() + " ..... " + hl.getSeatCount());
				}
			}

			log.info("activeHolds " + activeHolds);

			// activeHolds = activeHolds+b.getSeatCount();
		}
		return activeHolds;
	}

	/*
	 * Function to save Venue information in Venue Level Object
	 */
	public String saveVenue(VenueLevel vl, Session session) {

		String resp = G_SUCCESS_MSG;
		errorMessage = "";
		// Session session = factory.openSession();
		// Transaction tx = session.beginTransaction();
		try {
			session.update(vl);
		} catch (Exception e) {
			resp = G_ERROR_MSG;
			errorMessage = "Exception occured in SaveVenue Method - " + e.getMessage();
			// tx.rollback();
			session.close();
		}
		return resp;

	}

	/*
	 * Following function saves the hold information in HOLD_HEADER HOLD_LINES
	 * table
	 */

	public int saveHolds(HoldHeader hh, Session session) {

		int holdID = 0;
		// Session session = factory.openSession();
		// Transaction tx = session.beginTransaction();

		try {
			HoldHeader saveHeader = new HoldHeader();

			saveHeader.setCustomerEmail(hh.getCustomerEmail());
			saveHeader.setHoldTime(hh.getHoldTime());
			saveHeader.setReservedFlag(hh.getReservedFlag());
			saveHeader.setHoldHeaderID(hh.getHoldHeaderID());

			session.saveOrUpdate(saveHeader);
			holdID = saveHeader.getHoldHeaderID();

			saveHeader.setHoldHeaderID(holdID);
			Set<HoldLines> holdLinesList = new HashSet<HoldLines>();
			if (!(hh.getHoldLines() == null)) {
				for (HoldLines hl : hh.getHoldLines()) {
					hl.setHoldHeaderID(holdID);
					holdLinesList.add(hl);
					session.save(hl);
				}
			}
		} catch (Exception e) {
			holdID = -1;
			// tx.rollback();
			session.close();
			errorMessage = "Exception occured in saveHolds Method" + e.getMessage();
		}

		return holdID;
	}

	/*
	 * Following function returns the venueLevel object for a given LevelID
	 */

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

	/*
	 * Following function returns the Holds Object for a give hold ID
	 */

	public HoldHeader getHolds(int holdHeaderID) {
		HoldHeader resp = new HoldHeader();

		Session session = factory.openSession();

		Criteria c = session.createCriteria(HoldHeader.class);
		c.add(Restrictions.like("holdHeaderID", holdHeaderID));

		@SuppressWarnings("unchecked")
		List<HoldHeader> holdList = c.list();

		for (HoldHeader hh : holdList) {
			resp = hh;
		}

		return resp;

	}

}