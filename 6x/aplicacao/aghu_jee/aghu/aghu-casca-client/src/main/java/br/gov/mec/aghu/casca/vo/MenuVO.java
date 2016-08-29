package br.gov.mec.aghu.casca.vo;

import java.util.ArrayList;
import java.util.List;

public class MenuVO {
	private Integer id;
	private String idStr;
	private String url;
	private String nome;
	private Integer idPai;
	private String classeIcone;
	private String styleClass;
	private String masterClass;
	private boolean disabled;
	private List<MenuVO> menus = new ArrayList<MenuVO>();
	private Boolean favorito=false;
	
	public MenuVO(String idStr, String url, String nome, String classeIcone) {
		super();
		this.idStr = idStr;
		this.url = url;
		this.nome = nome;
		this.classeIcone = classeIcone;
	}

	public MenuVO(String nome) {
		super();
		this.nome = nome;
	}
	
	public MenuVO() {
		super();
	}	
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getClasseIcone() {
		return classeIcone;
	}

	public void setClasseIcone(String classeIcone) {
		this.classeIcone = classeIcone;
	}

	public List<MenuVO> getMenus() {
		return menus;
	}
	
	public void add(MenuVO vo){
		menus.add(vo);
	}

	public void setMenus(List<MenuVO> menus) {
		this.menus = menus;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	
	public String getHtml() {
		StringBuilder html = new StringBuilder();
		
		if (menus.isEmpty()) {
			String div = "";
			if (favorito) {
				div="<div class='star-on' onclick='removerFavorito("+id+");fav.disabledStar(this);'></div>";
			}else{
				div="<div class='star-off' onclick='adicionarFavorito("+id+");fav.enabledStar(this);'></div>";				
			}
			String link = url.replaceAll("'", "\"");
			if (!disabled) {
				div = "<div onclick='"+link+"' class='divLink'></div>".concat(div);
			} else {
				div = "";
			}
			
			//html.append("<li><a class='").append(styleClass).append("'>" ).append( nome ).append( div ).append( "</a></li>");
			html.append("<li><a>")
			.append("<div class='casca-menu-ico'><span class='").append(styleClass).append("'></span></div>")
			.append("<span class='casca-menu-text'>").append( nome ).append("</span>")
			.append( div )
			.append( "</a><li>");
		} else {
			html.append("<li><a onclick='return false;'>" )
			.append("<div class='casca-menu-ico'><span class='").append(styleClass).append("'></span></div>")
			.append("<span class='casca-menu-text'>").append( nome ).append("</span>")
			.append( "</a><ul>");
			for (MenuVO menu : menus) {
				html.append(menu.getHtml());
			}
			html.append("</ul></li>");
		}
		return html.toString();
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MenuVO other = (MenuVO) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id)){
			return false;
		}
		return true;
	}

	public Boolean getFavorito() {
		return favorito;
	}

	public void setFavorito(Boolean favorito) {
		this.favorito = favorito;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getIdStr() {
		return idStr;
	}

	public void setIdStr(String idStr) {
		this.idStr = idStr;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}

	public String getMasterClass() {
		return masterClass;
	}

	public void setMasterClass(String masterClass) {
		this.masterClass = masterClass;
	}

	public Integer getIdPai() {
		return idPai;
	}

	public void setIdPai(Integer idPai) {
		this.idPai = idPai;
	}


}
