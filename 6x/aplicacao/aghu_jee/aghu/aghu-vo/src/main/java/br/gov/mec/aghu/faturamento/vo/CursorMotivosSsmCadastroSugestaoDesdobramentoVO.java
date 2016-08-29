package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioMotivoDesdobramento;


public class CursorMotivosSsmCadastroSugestaoDesdobramentoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5523283403566935593L;

	private Byte mdsSeq;

	private Short diasInternacao;

	private Short iphPhoSeq;

	private Integer iphSeq;

	private DominioMotivoDesdobramento tipoDesdobramento;
	
	private Byte codigoSus;

	public CursorMotivosSsmCadastroSugestaoDesdobramentoVO() {
	}

	public CursorMotivosSsmCadastroSugestaoDesdobramentoVO(Byte mdsSeq, Short diasInternacao, Short iphPhoSeq, 
			Integer iphSeq, DominioMotivoDesdobramento tipoDesdobramento, Byte codigoSus) {
		this.mdsSeq = mdsSeq;
		this.diasInternacao = diasInternacao;
		this.iphPhoSeq = iphPhoSeq;
		this.iphSeq = iphSeq;
		this.tipoDesdobramento = tipoDesdobramento;
		this.codigoSus = codigoSus;
	}
	
	public CursorMotivosSsmCadastroSugestaoDesdobramentoVO(Short mdsSeq, Short diasInternacao, Short iphPhoSeq, 
			Integer iphSeq, DominioMotivoDesdobramento tipoDesdobramento, Byte codigoSus) {
		this.mdsSeq = mdsSeq != null ? mdsSeq.byteValue() : null;
		this.diasInternacao = diasInternacao;
		this.iphPhoSeq = iphPhoSeq;
		this.iphSeq = iphSeq;
		this.tipoDesdobramento = tipoDesdobramento;
		this.codigoSus = codigoSus;
	}

	public Byte getMdsSeq() {
		return mdsSeq;
	}

	public void setMdsSeq(Byte mdsSeq) {
		this.mdsSeq = mdsSeq;
	}

	public Short getDiasInternacao() {
		return diasInternacao;
	}

	public void setDiasInternacao(Short diasInternacao) {
		this.diasInternacao = diasInternacao;
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public DominioMotivoDesdobramento getTipoDesdobramento() {
		return tipoDesdobramento;
	}

	public void setTipoDesdobramento(DominioMotivoDesdobramento tipoDesdobramento) {
		this.tipoDesdobramento = tipoDesdobramento;
	}

	public Byte getCodigoSus() {
		return codigoSus;
	}

	public void setCodigoSus(Byte codigoSus) {
		this.codigoSus = codigoSus;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigoSus == null) ? 0 : codigoSus.hashCode());
		result = prime * result + ((diasInternacao == null) ? 0 : diasInternacao.hashCode());
		result = prime * result + ((iphPhoSeq == null) ? 0 : iphPhoSeq.hashCode());
		result = prime * result + ((iphSeq == null) ? 0 : iphSeq.hashCode());
		result = prime * result + ((mdsSeq == null) ? 0 : mdsSeq.hashCode());
		result = prime * result + ((tipoDesdobramento == null) ? 0 : tipoDesdobramento.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
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
		CursorMotivosSsmCadastroSugestaoDesdobramentoVO other = (CursorMotivosSsmCadastroSugestaoDesdobramentoVO) obj;
		if (codigoSus == null) {
			if (other.codigoSus != null) {
				return false;
			}
		} else if (!codigoSus.equals(other.codigoSus)) {
			return false;
		}
		if (diasInternacao == null) {
			if (other.diasInternacao != null) {
				return false;
			}
		} else if (!diasInternacao.equals(other.diasInternacao)) {
			return false;
		}
		if (iphPhoSeq == null) {
			if (other.iphPhoSeq != null) {
				return false;
			}
		} else if (!iphPhoSeq.equals(other.iphPhoSeq)) {
			return false;
		}
		if (iphSeq == null) {
			if (other.iphSeq != null) {
				return false;
			}
		} else if (!iphSeq.equals(other.iphSeq)) {
			return false;
		}
		if (mdsSeq == null) {
			if (other.mdsSeq != null) {
				return false;
			}
		} else if (!mdsSeq.equals(other.mdsSeq)) {
			return false;
		}
		if (tipoDesdobramento != other.tipoDesdobramento) {
			return false;
		}
		return true;
	}

}
