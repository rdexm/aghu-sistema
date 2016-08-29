package br.gov.mec.aghu.faturamento.vo;

import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;

public class ParCthSeqSituacaoContaVO {

	private Integer cthSeq;
	private DominioSituacaoConta situacaoConta;
	private List<String> listaSsmStr;

	public Integer getCthSeq() {

		return this.cthSeq;
	}

	public void setCthSeq(final Integer cthSeq) {

		this.cthSeq = cthSeq;
	}

	public DominioSituacaoConta getSituacaoConta() {

		return this.situacaoConta;
	}

	public void setSituacaoConta(final DominioSituacaoConta situacaoConta) {

		this.situacaoConta = situacaoConta;
	}

	public List<String> getListaSsmStr() {

		return this.listaSsmStr;
	}

	public void setListaSsmStr(final List<String> listaSsmStr) {

		this.listaSsmStr = listaSsmStr;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.cthSeq == null)
				? 0
				: this.cthSeq.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ParCthSeqSituacaoContaVO)) {
			return false;
		}
		ParCthSeqSituacaoContaVO other = (ParCthSeqSituacaoContaVO) obj;
		if (this.cthSeq == null) {
			if (other.cthSeq != null) {
				return false;
			}
		} else if (!this.cthSeq.equals(other.cthSeq)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
	
		return "[c: " + this.getCthSeq() + " sit: " + this.getSituacaoConta().getDescricao() + " lista: " + this.getListaSsmStr() + "]";
	}
}
