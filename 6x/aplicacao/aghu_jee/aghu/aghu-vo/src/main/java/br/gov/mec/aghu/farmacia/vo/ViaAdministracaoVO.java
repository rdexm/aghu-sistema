package br.gov.mec.aghu.farmacia.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ViaAdministracaoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7990863789433171379L;

	private String sigla;

	private String descricao;

	public ViaAdministracaoVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ViaAdministracaoVO(String sigla, String descricao) {
		super();
		this.sigla = sigla;
		this.descricao = descricao;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
