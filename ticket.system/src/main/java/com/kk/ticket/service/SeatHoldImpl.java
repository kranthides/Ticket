package com.kk.ticket.service;

import org.hibernate.SessionFactory;

public class SeatHoldImpl {
	private static SessionFactory factory;
	
	public SeatHoldImpl (SessionFactory sessionFactory){
		factory = sessionFactory;
	}
	

}
