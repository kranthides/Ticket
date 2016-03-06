##Ticketing System

    Project is designed to implement the following functionalities 
    1. Get Latest Seat Count 
    2. Find and Hold best available seats 
    3. Reserve the seats
    
##Design & Architecture 
	
	This project is using following techologies 
		Java 7
		Hibernate
		H2 (In memory database)  
		Maven build tool.
	
	Following Data Model is used to design this APP. 
	
![image](https://github.com/kranthides/Ticket/blob/master/ticket.system/TicketSystem_DataModel.png??raw=true)

##Bulding and Running the App
	
	### Downloading the App
		
		git init 
		git clone https://github.com/kranthides/Ticket.git
	
	### Building the App 
	
		$maven_home/mvn package  
	
	### Testing the App
	
		$maven_home/mvn test 

	### Running the App
		cd target 
		java -jar ticket.system.jar 
		And Select the Respective Option 
		
		
			Select the following options
			-----------------------------
			1. Get the Seats Information
			2. Hold the Seats 
			3. Reserve the Seats 
			4. Exit 

		

##Test Case Scenarios 

	Following unit test case scenarios are executing during testing of the APP 
	
	
		* Scenario 1 : This is a Happy path scenario
					 
				1. User will Query the number of seats available for a given level 
				2. User holds X number of Seats 
				3. And User then reserve the seats 
				 
		* Scenario 2 : Trying to reserve the expired holds 

				1. User will Query the number of seats available for a given level 
				2. User holds X number of Seats 
				2. User waits for 30 Seconds 
				3. And user then reserve the seats 
		
		* Scenario 3 : Reserve the same hold twice

				1. User will Query the number of seats available for a given level 
				2. User holds X number of Seats 
				3. user then reserve the seats
				4. And User tries to reserve the seats again using the same HoldID 
		
		* Scenario 4 : Hold more than available seats 

				1. User will Query the number of seats available for a given level 
				2. User holds X number of Seats which is more than available seats
	
## Things to improve 
	
	* I designed this app to run it as standalone application using In Memory database, which makes 
	  the application vard hard to test concurrent the requests.
	* This application is designed with minimum exception handling, not returning the specific
	  error codes based on the scenarios 
	* Not using the property files to store few values to avoid the hard coding.    
	
