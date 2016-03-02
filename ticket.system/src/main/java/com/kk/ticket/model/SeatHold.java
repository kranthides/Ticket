package com.kk.ticket.model;

import java.util.Set;

public class SeatHold {
	
	private int holdID;
	private double price;
	private String returnMessage;
	
	private Set<LevelInfo> levelInfo;

	public int getHoldID() {
		return holdID;
	}

	public void setHoldID(int holdID) {
		this.holdID = holdID;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public Set<LevelInfo> getLevelInfo() {
		return levelInfo;
	}

	public void setLevelInfo(Set<LevelInfo> levelInfo) {
		this.levelInfo = levelInfo;
	}

	public String getReturnMessage() {
		return returnMessage;
	}

	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}		

 }
