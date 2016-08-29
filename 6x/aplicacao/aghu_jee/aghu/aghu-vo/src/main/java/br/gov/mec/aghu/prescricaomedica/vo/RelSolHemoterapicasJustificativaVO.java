package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

public class RelSolHemoterapicasJustificativaVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5235009230559584452L;

	private String grupoDescricao;
	
	private String descricao;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setGrupoDescricao(String grupoDescricao) {
		this.grupoDescricao = grupoDescricao;
	}

	public String getGrupoDescricao() {
		return grupoDescricao;
	}
	

}
