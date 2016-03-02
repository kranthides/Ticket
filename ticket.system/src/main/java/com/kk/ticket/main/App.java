package com.kk.ticket.main;

import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.hibernate.SessionFactory;

import com.google.gson.Gson;
import com.kk.ticket.model.SeatHold;
import com.kk.ticket.service.TicketServiceImpl;
import com.kk.ticket.util.TicketServiceUtil;

 
/**
 * Hello world!
 *
 */
public class App 
{
	private static SessionFactory factory;
	
    public static void main( String[] args )
    {
    	
		System.out.println("Initializing the database");
		TicketServiceUtil util = new TicketServiceUtil();
		factory= util.createHibernateSession();
		System.out.println("Database Intilization Completed");

    	Scanner reader = new Scanner(System.in);  // Reading from System.in

    	while (true) {
	    	int option = 0;
	    	try {
		    	System.out.println("");
		    	System.out.println("-----------------------------" );
		    	System.out.println("Select the following options" );
		    	System.out.println("-----------------------------" );	    	
		    	System.out.println("1. Get the Seats Information");
		    	System.out.println("2. Hold the Seats " );
		    	System.out.println("3. Reserve the Seats ");
		    	System.out.println("4. Exit ");
		    	System.out.println("");
	
		    	option = reader.nextInt();
		    	System.out.println("Selected Option " +option);
		    	TicketServiceImpl ts = new TicketServiceImpl(factory);
	
		    	if(option == 1 ){
		    		
		    		/* Getting the Latest Ticket Count */
		    		
		    		System.out.println("Please Enter the Venue Level ID" );
		    		int LevelID = reader.nextInt();		    		
		    		System.out.println("Total Available Seats " +ts.numSeatsAvailable(LevelID));
		    		
		    	} else if(option == 2 ){
		    		
		    		/* Holding the seats */ 
		    		
		    		System.out.println("Please Enter the Total Number Of Seats (optional)" );
		    		int totalNumberOfSeats = reader.nextInt();
		    		
		    		System.out.println("Please Enter Min Level(Optional) " );	    		
		    		int minLevel = reader.nextInt();
		    		
		    		System.out.println("Please Enter Max Level(Optional) " );
		    		int maxLevel = reader.nextInt();
		    		
		    		System.out.println("Please Enter Customer Email " );
		    		String customerEmail = reader.next();
		    		
		    		SeatHold sResp = new SeatHold();
		    		
		    		sResp = ts.findAndHoldSeats(totalNumberOfSeats, minLevel, maxLevel, customerEmail);
		    		
		    		Gson gson = new Gson();
		    		System.out.println(gson.toJson(sResp));
		    		
		    	} else if(option == 3) { 
		    		System.out.println("Please Enter the HoldID" );
		    		int HoldID = reader.nextInt();
		    		
		    		System.out.println("Please Enter Customer Email " );
		    		String customerEmail = reader.next();
	
		    		String reserveResp = ts.reserveSeats(HoldID, customerEmail);
		    		
		    		System.out.println(reserveResp);
		    		
		    	} else if(option == 4){	    		
		    		System.out.println("Closing the Application");
		    		break;
	 	    	}else {
		    		System.out.println("Invalid Option, Please select the valid option");	    		
		    	}
	    	}catch(NumberFormatException ne) {
	    		ne.printStackTrace();
	    		break;
	    	}catch (InputMismatchException ie) {
 	    		ie.printStackTrace();
	    		break;
	    	}
    	}
		reader.close();	
		return;
    }
}