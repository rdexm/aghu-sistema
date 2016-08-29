package br.gov.mec.casca.menu.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class PalavraChaveVO {

	private Integer menuId;
	private Integer idMenuPai;
	private String nomeMenuPai;
	private String palavra;
	private String url;
	private String nomeMenu;
	private DominioSimNao ativo;
	

	public PalavraChaveVO(String palavra, String url, String nomeMenu,
			String nomeMenuPai, Integer menuId, Integer idMenuPai, DominioSimNao ativo) {
		super();
		this.palavra = palavra;
		this.url = url;
		this.nomeMenu = nomeMenu;
		this.nomeMenuPai = nomeMenuPai;
		this.menuId = menuId;
		this.idMenuPai = idMenuPai;
		this.ativo = ativo;
	}

	public String getPalavra() {
		return palavra;
	}
	
	public void setPalavra(String palavra) {
		this.palavra = palavra;
	}
	public String getNomeMenu() {
		return nomeMenu;
	}
	public void setNomeMenu(String nomeMenu) {
		this.nomeMenu = nomeMenu;
	}
	public Integer getMenuId() {
		return menuId;
	}
	public void setMenuId(Integer menuId) {
		this.menuId = menuId;
	}
	public Integer getIdMenuPai() {
		return idMenuPai;
	}
	public void setIdMenuPai(Integer idMenuPai) {
		this.idMenuPai = idMenuPai;
	}
	public DominioSimNao getAtivo() {
		return ativo;
	}
	public void setAtivo(DominioSimNao ativo) {
		this.ativo = ativo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getNomeMenuPai() {
		return nomeMenuPai;
	}

	public void setNomeMenuPai(String	 nomeMenuPai) {
		this.nomeMenuPai = nomeMenuPai;
	}
}
