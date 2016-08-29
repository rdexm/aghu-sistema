package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class CursorIntCadastroSugestaoDesdobramentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2969096458401198857L;

	private Date dthrInternacao;
	
	private Short qrtNumero;
	
	private String ltoLtoId;

	public CursorIntCadastroSugestaoDesdobramentoVO() {
	}

	public CursorIntCadastroSugestaoDesdobramentoVO(Date dthrInternacao, Short qrtNumero, String ltoLtoId) {
		this.dthrInternacao = dthrInternacao;
		this.qrtNumero = qrtNumero;
		this.ltoLtoId = ltoLtoId;
	}

	public Date getDthrInternacao() {
		return dthrInternacao;
	}

	public void setDthrInternacao(Date dthrInternacao) {
		this.dthrInternacao = dthrInternacao;
	}

	public Short getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dthrInternacao == null) ? 0 : dthrInternacao.hashCode());
		result = prime * result + ((ltoLtoId == null) ? 0 : ltoLtoId.hashCode());
		result = prime * result + ((qrtNumero == null) ? 0 : qrtNumero.hashCode());
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
		CursorIntCadastroSugestaoDesdobramentoVO other = (CursorIntCadastroSugestaoDesdobramentoVO) obj;
		if (dthrInternacao == null) {
			if (other.dthrInternacao != null) {
				return false;
			}
		} else if (!dthrInternacao.equals(other.dthrInternacao)) {
			return false;
		}
		if (ltoLtoId == null) {
			if (other.ltoLtoId != null) {
				return false;
			}
		} else if (!ltoLtoId.equals(other.ltoLtoId)) {
			return false;
		}
		if (qrtNumero == null) {
			if (other.qrtNumero != null) {
				return false;
			}
		} else if (!qrtNumero.equals(other.qrtNumero)) {
			return false;
		}
		return true;
	}

}
