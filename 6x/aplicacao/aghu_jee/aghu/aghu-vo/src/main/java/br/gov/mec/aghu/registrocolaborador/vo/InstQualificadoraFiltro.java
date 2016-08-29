package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

/**
 * Filtro para busca de Instituições Qualificadoras via web service
 * 
 * @author agerling
 * 
 */
public class InstQualificadoraFiltro  implements Serializable {

	private static final long serialVersionUID = -3042478342956249629L;
	
	private String descricao;  //like
	private String indInterno; //"S" ou "N"

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getIndInterno() {
		return indInterno;
	}

	public void setIndInterno(String indInterno) {
		this.indInterno = indInterno;
	}

}
