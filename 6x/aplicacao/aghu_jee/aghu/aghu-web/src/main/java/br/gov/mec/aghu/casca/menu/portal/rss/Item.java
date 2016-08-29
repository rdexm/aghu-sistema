package br.gov.mec.aghu.casca.menu.portal.rss;

import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "item")
public class Item {
    

	private String title;
	private String link;
	private String pubDate;
	private String description;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getDataFormatada() {
		return FormataDataPortal.formataData(this.pubDate);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
