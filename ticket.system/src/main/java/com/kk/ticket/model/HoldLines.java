package com.kk.ticket.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name="HoldLines")
@Table(name="HOLD_LINES", uniqueConstraints= {
		@UniqueConstraint(columnNames="id")
})
public class HoldLines {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id",unique=true,nullable=false,length=100)	
	private int id; 
	
	@Column(name="holdHeaderID",nullable=false,length=100)	
	private int holdHeaderID;
	
	@Column(name="levelID",nullable=false,length=100)	
	private int levelID;
	
	@Column(name="seatCount",nullable=false,length=100)
	private int seatCount;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getHoldHeaderID() {
		return holdHeaderID;
	}

	public void setHoldHeaderID(int holdHeaderID) {
		this.holdHeaderID = holdHeaderID;
	}

	public int getLevelID() {
		return levelID;
	}

	public void setLevelID(int levelID) {
		this.levelID = levelID;
	}

	public int getSeatCount() {
		return seatCount;
	}

	public void setSeatCount(int seatCount) {
		this.seatCount = seatCount;
	}
	
	
	
}
