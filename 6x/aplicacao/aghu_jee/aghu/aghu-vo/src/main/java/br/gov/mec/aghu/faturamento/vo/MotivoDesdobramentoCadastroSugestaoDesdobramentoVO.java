package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioMotivoDesdobramento;


public class MotivoDesdobramentoCadastroSugestaoDesdobramentoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4268175517420859385L;

	private Byte seq;

	private Short diasAposInternacao;

	private Short qtdMinima;

	private Short diasAposProcedimento;

	private DominioMotivoDesdobramento tipoDesdobramento;

	private Byte codigoSus;

	public MotivoDesdobramentoCadastroSugestaoDesdobramentoVO() {
	}

	public MotivoDesdobramentoCadastroSugestaoDesdobramentoVO(Byte seq, Short diasAposInternacao, Short qtdMinima,
			Short diasAposProcedimento, DominioMotivoDesdobramento tipoDesdobramento, Byte codigoSus) {
		this.seq = seq;
		this.diasAposInternacao = diasAposInternacao;
		this.qtdMinima = qtdMinima;
		this.diasAposProcedimento = diasAposProcedimento;
		this.tipoDesdobramento = tipoDesdobramento;
		this.codigoSus = codigoSus;
	}
	
	public MotivoDesdobramentoCadastroSugestaoDesdobramentoVO(Short seq, Short diasAposInternacao, Short qtdMinima,
			Short diasAposProcedimento, DominioMotivoDesdobramento tipoDesdobramento, Byte codigoSus) {
		this.seq = seq != null ? seq.byteValue() : null;
		this.diasAposInternacao = diasAposInternacao;
		this.qtdMinima = qtdMinima;
		this.diasAposProcedimento = diasAposProcedimento;
		this.tipoDesdobramento = tipoDesdobramento;
		this.codigoSus = codigoSus;
	}

	public Byte getSeq() {
		return seq;
	}

	public void setSeq(Byte seq) {
		this.seq = seq;
	}

	public Short getDiasAposInternacao() {
		return diasAposInternacao;
	}

	public void setDiasAposInternacao(Short diasAposInternacao) {
		this.diasAposInternacao = diasAposInternacao;
	}

	public Short getQtdMinima() {
		return qtdMinima;
	}

	public void setQtdMinima(Short qtdMinima) {
		this.qtdMinima = qtdMinima;
	}

	public Short getDiasAposProcedimento() {
		return diasAposProcedimento;
	}

	public void setDiasAposProcedimento(Short diasAposProcedimento) {
		this.diasAposProcedimento = diasAposProcedimento;
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
		result = prime * result + ((diasAposInternacao == null) ? 0 : diasAposInternacao.hashCode());
		result = prime * result + ((diasAposProcedimento == null) ? 0 : diasAposProcedimento.hashCode());
		result = prime * result + ((qtdMinima == null) ? 0 : qtdMinima.hashCode());
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		MotivoDesdobramentoCadastroSugestaoDesdobramentoVO other = (MotivoDesdobramentoCadastroSugestaoDesdobramentoVO) obj;
		if (codigoSus == null) {
			if (other.codigoSus != null) {
				return false;
			}
		} else if (!codigoSus.equals(other.codigoSus)) {
			return false;
		}
		if (diasAposInternacao == null) {
			if (other.diasAposInternacao != null) {
				return false;
			}
		} else if (!diasAposInternacao.equals(other.diasAposInternacao)) {
			return false;
		}
		if (diasAposProcedimento == null) {
			if (other.diasAposProcedimento != null) {
				return false;
			}
		} else if (!diasAposProcedimento.equals(other.diasAposProcedimento)) {
			return false;
		}
		if (qtdMinima == null) {
			if (other.qtdMinima != null) {
				return false;
			}
		} else if (!qtdMinima.equals(other.qtdMinima)) {
			return false;
		}
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		if (tipoDesdobramento != other.tipoDesdobramento) {
			return false;
		}
		return true;
	}

}
