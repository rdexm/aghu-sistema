package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.model.AelTipoAmostraExameId;

public class AelTipoAmostraExameVO {

	private AelTipoAmostraExameId id;
	private Integer indiceListaAelTipoAmostraExame;
	private String descricaoOrigemAtendimento;
	private String descricaoMaterialAnalise;
	private String descricaoRecipienteColeta;
	private String descricaoAnticoagulante;
	private String descricaoUnidadeColeta;

	public AelTipoAmostraExameId getId() {
		return id;
	}

	public void setId(AelTipoAmostraExameId id) {
		this.id = id;
	}

	public String getDescricaoOrigemAtendimento() {
		return descricaoOrigemAtendimento;
	}

	public void setDescricaoOrigemAtendimento(String descricaoOrigemAtendimento) {
		this.descricaoOrigemAtendimento = descricaoOrigemAtendimento;
	}

	public String getDescricaoMaterialAnalise() {
		return descricaoMaterialAnalise;
	}

	public void setDescricaoMaterialAnalise(String descricaoMaterialAnalise) {
		this.descricaoMaterialAnalise = descricaoMaterialAnalise;
	}

	public String getDescricaoRecipienteColeta() {
		return descricaoRecipienteColeta;
	}
	
	public void setDescricaoRecipienteColeta(String descricaoRecipienteColeta) {
		this.descricaoRecipienteColeta = descricaoRecipienteColeta;
	}

	public String getDescricaoAnticoagulante() {
		return descricaoAnticoagulante;
	}

	public void setDescricaoAnticoagulante(String descricaoAnticoagulante) {
		this.descricaoAnticoagulante = descricaoAnticoagulante;
	}

	public String getDescricaoUnidadeColeta() {
		return descricaoUnidadeColeta;
	}

	public void setDescricaoUnidadeColeta(String descricaoUnidadeColeta) {
		this.descricaoUnidadeColeta = descricaoUnidadeColeta;
	}

	public Integer getIndiceListaAelTipoAmostraExame() {
		return indiceListaAelTipoAmostraExame;
	}

	public void setIndiceListaAelTipoAmostraExame(
			Integer indiceListaAelTipoAmostraExame) {
		this.indiceListaAelTipoAmostraExame = indiceListaAelTipoAmostraExame;
	}

}
