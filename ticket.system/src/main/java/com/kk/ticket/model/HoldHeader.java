package com.kk.ticket.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name="HoldHeader")
@Table(name="HOLD_HEADER", uniqueConstraints= {
		@UniqueConstraint(columnNames="holdHeaderID")
})
public class HoldHeader implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="holdHeaderID",unique=true,nullable=false,length=100)	
	private int holdHeaderID;
	
	
	@Column(name="holdTime",nullable=false,length=100)	
	private Date holdTime;
	
	@Column(name="reservedFlag",length=2)	
	private String reservedFlag = "N" ;
	
	@Column(name="customerEmail",length=200)	
	private String customerEmail ;
	
	@Column(name="reservationCode",length=200)
	private String reservationCode ;
	
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="holdHeaderID",insertable = false, updatable = false)
	private Set<HoldLines> holdLines;

	

	public int getHoldHeaderID() {
		return holdHeaderID;
	}
	public void setHoldHeaderID(int holdHeaderID) {
		this.holdHeaderID = holdHeaderID;
	}
	
 	public Date getHoldTime() {
		return holdTime;
	}
	public void setHoldTime(Date holdTime) {
		this.holdTime = holdTime;
	}
	public String getReservedFlag() {
		return reservedFlag;
	}
	public void setReservedFlag(String reservedFlag) {
		this.reservedFlag = reservedFlag;
	}
	public String getCustomerEmail() {
		return customerEmail;
	}
	public void setCustomerEmail(String customerEmail) {
		this.customerEmail = customerEmail;
	}
	public Set<HoldLines> getHoldLines() {
		return holdLines;
	}
	public void setHoldLines(Set<HoldLines> holdLines) {
		this.holdLines = holdLines;
	}	
	
	public String getReservationCode() {
		return reservationCode;
	}
	public void setReservationCode(String reservationCode) {
		this.reservationCode = reservationCode;
	}	
	
}
