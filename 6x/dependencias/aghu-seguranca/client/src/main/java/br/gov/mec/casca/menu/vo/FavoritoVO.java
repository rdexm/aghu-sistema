package br.gov.mec.casca.menu.vo;

public class FavoritoVO {

	private Integer menuId;
	private Integer menuIdNovo;
	private Integer menuIdVirtual;
	private String menuUrl;
	private Integer menuIdAplicacao;
	
	public Integer getMenuId() {
		return menuId;
	}
	
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	
	public Integer getMenuIdNovo() {
		return menuIdNovo;
	}
	
	public void setMenuIdNovo(Integer menuIdNovo) {
		this.menuIdNovo = menuIdNovo;
	}
	
	public Integer getMenuIdVirtual() {
		return menuIdVirtual;
	}
	
	public void setMenuIdVirtual(Integer menuIdVirtual) {
		this.menuIdVirtual = menuIdVirtual;
	}
	
	public String getMenuUrl() {
		return menuUrl;
	}
	
	public void setMenuUrl(String menuUrl) {
		this.menuUrl = menuUrl;
	}
	
	public Integer getMenuIdAplicacao() {
		return menuIdAplicacao;
	}
	
	public void setMenuIdAplicacao(Integer menuIdAplicacao) {
		this.menuIdAplicacao = menuIdAplicacao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((menuId == null) ? 0 : menuId.hashCode());
		result = prime * result + ((menuUrl == null) ? 0 : menuUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		FavoritoVO other = (FavoritoVO) obj;
		if (menuId == null) {
			if (other.menuId != null) {
				return false;
			}
		} else if (!menuId.equals(other.menuId)) {
			return false;
		}
		if (menuUrl == null) {
			if (other.menuUrl != null) {
				return false;
			}
		} else if (!menuUrl.equals(other.menuUrl)) {
			return false;
		}
		return true;
	}
	
}
