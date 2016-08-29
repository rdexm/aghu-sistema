package br.gov.mec.aghu.casca.menu.portal.rss;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "channel")
public class Channel {
	
	private String title;
	
	private List<Item> item = new ArrayList<Item>();
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	public List<Item> getItem() {
		return item;
	}
	
	public void setItem(List<Item> item) {
		this.item = item;
	}

}