package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;

public class ConfirmacaoImpressaoEtiquetaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7395757668558501672L;
	
	private Boolean confirmaImpressao = Boolean.FALSE;
	private Boolean confirmaImpressaoSemModal = Boolean.FALSE;
	private String indSituacaoExameImpressao;
	private Boolean redome = Boolean.FALSE;
	
	/**
	 * 
	 * @param confirmaImpressao
	 * @param indSituacaoExameImpressao
	 */
	public ConfirmacaoImpressaoEtiquetaVO(Boolean confirmaImpressao, String indSituacaoExameImpressao) {
		this(confirmaImpressao, indSituacaoExameImpressao, Boolean.FALSE);
	}

	public ConfirmacaoImpressaoEtiquetaVO(Boolean confirmaImpressao, String indSituacaoExameImpressao,
			Boolean confirmaImpressaoSemModal) {
		this(confirmaImpressao, indSituacaoExameImpressao, confirmaImpressaoSemModal, Boolean.FALSE);
	}

	public ConfirmacaoImpressaoEtiquetaVO(Boolean confirmaImpressao, String indSituacaoExameImpressao,
			Boolean confirmaImpressaoSemModal, Boolean redome) {
		this.confirmaImpressao = confirmaImpressao;
		this.indSituacaoExameImpressao = indSituacaoExameImpressao;
		this.confirmaImpressaoSemModal = confirmaImpressaoSemModal;
		this.redome = redome;
	}

	public Boolean getConfirmaImpressao() {
		return confirmaImpressao;
	}

	public void setConfirmaImpressao(Boolean confirmaImpressao) {
		this.confirmaImpressao = confirmaImpressao;
	}

	public String getIndSituacaoExameImpressao() {
		return indSituacaoExameImpressao;
	}

	public void setIndSituacaoExameImpressao(String indSituacaoExameImpressao) {
		this.indSituacaoExameImpressao = indSituacaoExameImpressao;
	}

	public Boolean getConfirmaImpressaoSemModal() {
		return confirmaImpressaoSemModal;
	}

	public void setConfirmaImpressaoSemModal(Boolean confirmaImpressaoSemModal) {
		this.confirmaImpressaoSemModal = confirmaImpressaoSemModal;
	}

	
	public Boolean isRedome() {
		return redome;
	}

	
	public void setRedome(Boolean redome) {
		this.redome = redome;
	}
	
}
