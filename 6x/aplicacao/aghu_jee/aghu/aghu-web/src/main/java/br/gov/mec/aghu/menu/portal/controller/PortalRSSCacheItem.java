package br.gov.mec.aghu.menu.portal.controller;

import java.util.Date;

public class PortalRSSCacheItem {
	
	private Object item;
	private Date data;
	
	public PortalRSSCacheItem(Object item, Date data) {
		super();
		this.item = item;
		this.data = data;
	}
	
	
	public Object getItem() {
		return item;
	}

	public void setItem(Object item) {
		this.item = item;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	
	

}
