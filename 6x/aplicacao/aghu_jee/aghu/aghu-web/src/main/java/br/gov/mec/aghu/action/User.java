package br.gov.mec.aghu.action;

import java.io.Serializable;
import java.util.Date;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

@Named()
@SessionScoped
public class User implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7757960730028899625L;
	
	private Integer id;
	private String country;
	private Date date;

	public String getCountry() {
		return country;
	}

	public User(Integer id, String country, Date date) {
		super();
		this.id = id;
		this.country = country;
		this.date = date;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
		    return true;
		}
		if (obj == null){
		    return false;
		}
		if (!(obj instanceof User)){
		    return false;
		}
		User other = (User) obj;
		if (id == null) {
			if (other.id != null){
			    return false;
			}
		} else if (!id.equals(other.id)){
		    return false;
		}
		return true;
	}
}
