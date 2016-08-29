package br.gov.mec.aghu.farmacia.vo;

import br.gov.mec.aghu.model.AfaMedicamentoJn;



public class HistoricoCadastroMedicamentoVO extends AfaMedicamentoJn {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9019702824481850767L;
	private String nomeMaterial;
	private String descricaoTprSigla;
	private String descricaoSiglaTipoUsoMtdo;
	private String descricacaoUnidadeMedidaMedica;
	private String descricaoTipoFrequenciaAprazamento;
	private String responsavelCadastro;
	
	
	//Getters and Setters
	
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	
	public String getDescricaoTprSigla() {
		return descricaoTprSigla;
	}
	
	public void setDescricaoTprSigla(String descricaoTprSigla) {
		this.descricaoTprSigla = descricaoTprSigla;
	}
	
	public String getDescricaoSiglaTipoUsoMtdo() {
		return descricaoSiglaTipoUsoMtdo;
	}
	
	public void setDescricaoSiglaTipoUsoMtdo(String descricaoSiglaTipoUsoMtdo) {
		this.descricaoSiglaTipoUsoMtdo = descricaoSiglaTipoUsoMtdo;
	}
	
	public String getDescricacaoUnidadeMedidaMedica() {
		return descricacaoUnidadeMedidaMedica;
	}
	
	public void setDescricacaoUnidadeMedidaMedica(
			String descricacaoUnidadeMedidaMedica) {
		this.descricacaoUnidadeMedidaMedica = descricacaoUnidadeMedidaMedica;
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return descricaoTipoFrequenciaAprazamento;
	}

	public void setDescricaoTipoFrequenciaAprazamento(
			String descricaoTipoFrequenciaAprazamento) {
		this.descricaoTipoFrequenciaAprazamento = descricaoTipoFrequenciaAprazamento;
	}

	public String getResponsavelCadastro() {
		return responsavelCadastro;
	}

	public void setResponsavelCadastro(String responsavelCadastro) {
		this.responsavelCadastro = responsavelCadastro;
	}
	
	
}
