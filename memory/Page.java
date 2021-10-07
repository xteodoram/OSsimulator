package memory;

import java.util.ArrayList;

public class Page {
	private ArrayList<String> content=new ArrayList<>();
	private int size=0;
	
	public Page(ArrayList<String>content) {
		this.content=content;
		size=content.size()*16;
	}
	public Page() {}
	
	public void add(String string) {
		content.add(string);
		size+=16;
	}
	public void change(int index, String data) {
		content.set(index,data);
	}
	public ArrayList<String> getContent() {
		return content;
	}
	public int getSize() {
		return size;
	}
	public String toString() {
		return content.toString();
	}
}
