package com.kk.ticket.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    	//Scanner reader = new Scanner(System.in);  // Reading from System.in
    	
		BufferedReader buf = new BufferedReader (new InputStreamReader (System.in));
		String line;

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
				
		    	line = buf.readLine ();
		    	
		    	if(line.isEmpty()){
		    		System.out.println("Please enter valid option, Option is required");
		    		continue;
		    	}

		    	option = Integer.parseInt(line);
		    	TicketServiceImpl ts = new TicketServiceImpl(factory);
	
		    	if(option == 1 ){
		    		
		    		/* Getting the Latest Ticket Count */
		    		System.out.println("Please enter the level ID (Optional)");
			    	line = buf.readLine ();
			    	int LevelID;
			    	
			    	if(line.isEmpty()){
			    		LevelID =0 ;
			    	} else { 
			    		LevelID = Integer.parseInt(line);
			    	}
			    	
		    		System.out.println("Please Enter the Venue Level ID" );
		    		//int LevelID = reader.nextInt();		    		
		    		System.out.println("Total Available Seats " +ts.numSeatsAvailable(LevelID));
		    		
		    	} else if(option == 2 ){
		    		
		    		/* Holding the seats */ 
		    		
		    		System.out.println("Please Enter the Total Number Of Seats (Required)" );
		    		int totalNumberOfSeats;
			    	line = buf.readLine ();
			    	if(line.isEmpty()){
			    		System.out.println("Please enter the valid number of Seats" );
			    		continue;
 			    	} else {  			    		
 			    		totalNumberOfSeats = Integer.parseInt(line);
 			    	}
			    	
		    		System.out.println("Please Enter Min Level(Optional) " );	    		
		    		int minLevel;
			    	line = buf.readLine ();
			    	if(line.isEmpty()){
			    		minLevel=0;
 			    	} else {  			    		
 			    		minLevel = Integer.parseInt(line);
 			    	}		    		
		    		
		    		System.out.println("Please Enter Max Level(Optional) " );
		    		int maxLevel;
			    	line = buf.readLine ();
			    	if(line.isEmpty()){
			    		maxLevel=0;
 			    	} else {  			    		
 			    		maxLevel = Integer.parseInt(line);
 			    	}			    		
		    		
		    		System.out.println("Please Enter Customer Email " );
		    		String customerEmail ;
			    	line = buf.readLine ();	
			    	
			    	if(line.isEmpty()){
			    		System.out.println("Please Enter the valid Email ");
			    		continue;
 			    	} else {  			    		
 			    		customerEmail = line;
 			    	}			    		

		    		
		    		SeatHold sResp = new SeatHold();
		    		
		    		sResp = ts.findAndHoldSeats(totalNumberOfSeats, minLevel, maxLevel, customerEmail);
		    		
		    		Gson gson = new Gson();
		    		System.out.println(gson.toJson(sResp));
		    		
		    	} else if(option == 3) { 
		    		System.out.println("Please Enter the HoldID" );
		    		int HoldID ;
			    	line = buf.readLine ();	
			    	
			    	if(line.isEmpty()){
			    		System.out.println("Please Enter the Valid HolidID ");
			    		continue;
 			    	} else {  			    		
 			    		HoldID = Integer.parseInt(line);
 			    	}			    		
		    		
		    		
		    		System.out.println("Please Enter Customer Email " );
		    		String customerEmail;
			    	line = buf.readLine ();	
			    	if(line.isEmpty()){
			    		customerEmail = "";			    		
 			    	} else {  			    		
 			    		customerEmail = line;
 			    	}			    		

		    		String reserveResp = ts.reserveSeats(HoldID, customerEmail);
		    		
		    		System.out.println(reserveResp);
		    		
		    	} else if(option == 4){	  
		    		buf.close();
		    		System.out.println("Closing the Application");
		    		System.exit(0);
	 	    	}else {
		    		System.out.println("Invalid Option, Please select the valid option");	    		
		    	}
	    	}catch (IOException io){
	    		io.printStackTrace();
	    		break;	    		
	    	}
	    	catch(NumberFormatException ne) {
	    		ne.printStackTrace();
	    		break;
	    	}catch (InputMismatchException ie) {
 	    		ie.printStackTrace();
	    		break;
	    	}
    	}
    	System.out.println("System Exited");
    }
}