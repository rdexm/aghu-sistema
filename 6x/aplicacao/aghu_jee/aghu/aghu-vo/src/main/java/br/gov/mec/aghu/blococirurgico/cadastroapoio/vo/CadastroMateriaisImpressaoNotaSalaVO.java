package br.gov.mec.aghu.blococirurgico.cadastroapoio.vo;

import br.gov.mec.aghu.core.commons.BaseBean;


public class CadastroMateriaisImpressaoNotaSalaVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8494894673233275738L;
	private Integer codigo;
	private Integer codigoMaterial;
	private String materialDescricao;
	private String unidadeMedidaDescricao;
	private String nomeImpressao;
	private String codigoUnidadeMedida;
	private String unidadeImpressao;
	private Short ordemImpressao;
	private Boolean emEdicao;
	
	public Boolean getEmEdicao() {
		return emEdicao;
	}
	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getMaterialDescricao() {
		return materialDescricao;
	}
	public void setMaterialDescricao(String materialDescricao) {
		this.materialDescricao = materialDescricao;
	}
	public String getUnidadeMedidaDescricao() {
		return unidadeMedidaDescricao;
	}
	public void setUnidadeMedidaDescricao(String unidadeMedidaDescricao) {
		this.unidadeMedidaDescricao = unidadeMedidaDescricao;
	}
	public String getNomeImpressao() {
		return nomeImpressao;
	}
	public void setNomeImpressao(String nomeImpressao) {
		this.nomeImpressao = nomeImpressao;
	}

	public String getUnidadeImpressao() {
		return unidadeImpressao;
	}
	public void setUnidadeImpressao(String unidadeImpressao) {
		this.unidadeImpressao = unidadeImpressao;
	}
	public Short getOrdemImpressao() {
		return ordemImpressao;
	}
	public void setOrdemImpressao(Short ordemImpressao) {
		this.ordemImpressao = ordemImpressao;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}
	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}

}
