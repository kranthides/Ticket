package com.kk.ticket.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity(name="VenueLevel")
@Table(name="VENUE_LEVEL", uniqueConstraints= {
		@UniqueConstraint(columnNames="levelID")
})
public class VenueLevel implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="levelID",unique=true,nullable=false,length=100)
	private int levelID;
	@Column(name="levelName",nullable=false,length=100)
	private String levelName;
	@Column(name="price",nullable=false,length=100)
	private double price;
	@Column(name="rowCount",nullable=false,length=100)
	private int rowCount;
	@Column(name="seatsPerRow",nullable=false,length=100)
	private int seatsPerRow;
	@Column(name="remainingSeats",length=100)
	private int remainingSeats;
	
 
	public int getLevelID() {
		return levelID;
	}
	public void setLevelID(int levelID) {
		this.levelID = levelID;
	} 
	public String getlevelName() {
		return levelName;
	}
	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public int getRowCount() {
		return rowCount;
	}
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}
	public int getSeatsPerRow() {
		return seatsPerRow;
	}
	public void setSeatsPerRow(int seatsPerRow) {
		this.seatsPerRow = seatsPerRow;
	}
	public int getRemainingSeats() {
		return remainingSeats;
	}
	public void setRemainingSeats(int remainingSeats) {
		this.remainingSeats = remainingSeats;
	}

}
