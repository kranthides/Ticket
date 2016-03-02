package com.kk.ticket.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class TicketServiceUtil {

	public SessionFactory createHibernateSession() {
	    final Configuration configuration = new Configuration().configure();
	    final StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
	    final SessionFactory factory = configuration.buildSessionFactory(builder.build());	   	 	    
	    return factory;
	}
}
