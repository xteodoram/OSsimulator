package memory;

import java.util.ArrayList;

public class Frame {
	private boolean occupied;
	private String frameNumber;
	private int occupiedB;
	private Page page;
	
	public Frame(String frameNumber) {
		this.frameNumber=frameNumber;
		this.occupiedB=0;
		this.page=null;
		this.occupied=false;
	}
	public void setPage(Page page) {
		this.page=page;
		occupied=true;
		occupiedB=page.getSize();
	}
	public String getFrameNumber() {
		return frameNumber;
	}
	public Page getPage() {
		return page;
	}
	public void free() {
		page=null;
		occupiedB=0;
		occupied=false;
	}
	public void addContent(Page page) {
		this.page=page;
	}
	public boolean getOccupied() {
		return occupied;
	}
	public String toString() {
		return page.toString();
	}
}
