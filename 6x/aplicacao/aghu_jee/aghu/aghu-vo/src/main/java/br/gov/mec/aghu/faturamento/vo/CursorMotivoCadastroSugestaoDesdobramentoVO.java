package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CursorMotivoCadastroSugestaoDesdobramentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4853525980207405457L;

	private Integer maxSeqInternacao;

	private Integer codigoClinica;

	public CursorMotivoCadastroSugestaoDesdobramentoVO() {
	}

	public CursorMotivoCadastroSugestaoDesdobramentoVO(Integer maxSeqInternacao, Integer codigoClinica) {
		this.maxSeqInternacao = maxSeqInternacao;
		this.codigoClinica = codigoClinica;
	}

	public Integer getMaxSeqInternacao() {
		return maxSeqInternacao;
	}

	public void setMaxSeqInternacao(Integer maxSeqInternacao) {
		this.maxSeqInternacao = maxSeqInternacao;
	}

	public Integer getCodigoClinica() {
		return codigoClinica;
	}

	public void setCodigoClinica(Integer codigoClinica) {
		this.codigoClinica = codigoClinica;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigoClinica == null) ? 0 : codigoClinica.hashCode());
		result = prime * result + ((maxSeqInternacao == null) ? 0 : maxSeqInternacao.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		CursorMotivoCadastroSugestaoDesdobramentoVO other = (CursorMotivoCadastroSugestaoDesdobramentoVO) obj;
		if (codigoClinica == null) {
			if (other.codigoClinica != null) {
				return false;
			}
		} else if (!codigoClinica.equals(other.codigoClinica)) {
			return false;
		}
		if (maxSeqInternacao == null) {
			if (other.maxSeqInternacao != null) {
				return false;
			}
		} else if (!maxSeqInternacao.equals(other.maxSeqInternacao)) {
			return false;
		}
		return true;
	}

}
