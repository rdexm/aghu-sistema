package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;


/**
 * Representa um Item de Tipo Lote após o usuário selecionar um radio na tela de solicitação de
 * exames por lote
 * 
 * @author Filipe Hoffmeister
 *
 */

public class TipoLoteVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6165438445862248934L;

	/**
	 * Guarda o seq do lote de exame para pesquisar os exames posteriormente.
	 */
	private Integer leuSeq;
	
	/**
	 * Guarda a descrição do lote de exame para mostrar na combobox de lote
	 */
	private String descricao;

	public TipoLoteVO(Integer leuSeq, String descricao) {
		this.leuSeq = leuSeq;
		this.descricao = descricao;
	}

	public Integer getLeuSeq() {
		return leuSeq;
	}

	public void setLeuSeq(Integer leuSeq) {
		this.leuSeq = leuSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
