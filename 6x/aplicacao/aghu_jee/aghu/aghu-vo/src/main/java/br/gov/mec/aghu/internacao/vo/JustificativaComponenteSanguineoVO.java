package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.core.commons.BaseBean;

public class JustificativaComponenteSanguineoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3116006664766370038L;

	private AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo;
	
	private AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa;
	
	private Boolean marcado;
	
	private String descricaoLivre;

	public JustificativaComponenteSanguineoVO() {
	}

	public JustificativaComponenteSanguineoVO(
			AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo,
			AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa,
			Boolean marcado, String descricaoLivre) {
		this.justificativaComponenteSanguineo = justificativaComponenteSanguineo;
		this.itemSolicitacaoHemoterapicaJustificativa = itemSolicitacaoHemoterapicaJustificativa;
		this.marcado = marcado;
		this.descricaoLivre = descricaoLivre;
	}

	// GETTERs e SETTERs
	public AbsJustificativaComponenteSanguineo getJustificativaComponenteSanguineo() {
		return justificativaComponenteSanguineo;
	}

	public void setJustificativaComponenteSanguineo(
			AbsJustificativaComponenteSanguineo justificativaComponenteSanguineo) {
		this.justificativaComponenteSanguineo = justificativaComponenteSanguineo;
	}

	public AbsItemSolicitacaoHemoterapicaJustificativa getItemSolicitacaoHemoterapicaJustificativa() {
		return itemSolicitacaoHemoterapicaJustificativa;
	}

	public void setItemSolicitacaoHemoterapicaJustificativa(
			AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa) {
		this.itemSolicitacaoHemoterapicaJustificativa = itemSolicitacaoHemoterapicaJustificativa;
	}

	public Boolean getMarcado() {
		return marcado;
	}

	public void setMarcado(Boolean marcado) {
		this.marcado = marcado;
	}

	public String getDescricaoLivre() {
		return descricaoLivre;
	}

	public void setDescricaoLivre(String descricaoLivre) {
		this.descricaoLivre = descricaoLivre;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((itemSolicitacaoHemoterapicaJustificativa == null) ? 0
						: itemSolicitacaoHemoterapicaJustificativa.hashCode());
		result = prime
				* result
				+ ((justificativaComponenteSanguineo == null) ? 0
						: justificativaComponenteSanguineo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof JustificativaComponenteSanguineoVO)) {
			return false;
		}
		JustificativaComponenteSanguineoVO other = (JustificativaComponenteSanguineoVO) obj;
		if (itemSolicitacaoHemoterapicaJustificativa == null) {
			if (other.itemSolicitacaoHemoterapicaJustificativa != null) {
				return false;
			}
		} else if (!itemSolicitacaoHemoterapicaJustificativa
				.equals(other.itemSolicitacaoHemoterapicaJustificativa)) {
			return false;
		}
		if (justificativaComponenteSanguineo == null) {
			if (other.justificativaComponenteSanguineo != null) {
				return false;
			}
		} else if (!justificativaComponenteSanguineo
				.equals(other.justificativaComponenteSanguineo)) {
			return false;
		}
		return true;
	}

	

}
