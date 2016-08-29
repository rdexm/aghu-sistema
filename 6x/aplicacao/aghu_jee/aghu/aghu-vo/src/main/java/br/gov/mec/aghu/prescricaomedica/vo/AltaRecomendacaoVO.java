package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MpmAltaRecomendacaoId;
import br.gov.mec.aghu.model.MpmAltaSumario;

public class AltaRecomendacaoVO implements Serializable {

	private static final long serialVersionUID = -9098466546525960507L;
	
	private MpmAltaRecomendacaoId id;
	private MpmAltaSumario altaSumario;
	private String descricao;
	private Boolean emEdicao = false;

	
	
	public AltaRecomendacaoVO() {
		super();
	}

	
	public AltaRecomendacaoVO(MpmAltaSumario altaSumario, 
			String descricao, Boolean emEdicao) {
		super();
		this.altaSumario = altaSumario;
		this.descricao = descricao;
		this.emEdicao = emEdicao;
	}


	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}


	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public Boolean getEmEdicao() {
		return emEdicao;
	}


	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public MpmAltaRecomendacaoId getId() {
		return id;
	}


	public void setId(MpmAltaRecomendacaoId id) {
		this.id = id;
	}

}
