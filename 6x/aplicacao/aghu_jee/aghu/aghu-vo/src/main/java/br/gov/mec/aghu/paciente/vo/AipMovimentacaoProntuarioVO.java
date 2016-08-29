package br.gov.mec.aghu.paciente.vo;

import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AipMovimentacaoProntuarioVO implements BaseBean{

	private static final long serialVersionUID = -362315584833153868L;
	
	private AipMovimentacaoProntuarios movimentacao;
	private Boolean selecionado;

	public AipMovimentacaoProntuarioVO(AipMovimentacaoProntuarios movimentacao) {
		this.movimentacao = movimentacao;
		//selecionado = false;
	}

	
	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public AipMovimentacaoProntuarios getMovimentacao() {
		return movimentacao;
	}

	public void setMovimentacao(AipMovimentacaoProntuarios movimentacao) {
		this.movimentacao = movimentacao;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((movimentacao.getSeq() == null) ? 0 : movimentacao.getSeq().hashCode());
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
		if (!(obj instanceof AipMovimentacaoProntuarioVO)) {
			return false;
		}
		AipMovimentacaoProntuarioVO other = (AipMovimentacaoProntuarioVO) obj;
		if (movimentacao.getSeq() == null) {
			if (other.movimentacao.getSeq() != null) {
				return false;
			}
		} else if (!movimentacao.getSeq().equals(other.movimentacao.getSeq())) {
			return false;
		}
		return true;
	}
}
