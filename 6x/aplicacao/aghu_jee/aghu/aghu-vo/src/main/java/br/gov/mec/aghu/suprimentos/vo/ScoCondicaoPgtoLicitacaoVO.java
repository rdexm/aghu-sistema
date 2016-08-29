package br.gov.mec.aghu.suprimentos.vo;

import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoCondicaoPgtoLicitacaoVO implements BaseBean{
	
	private static final long serialVersionUID = -2271261604248075806L;
	
	private ScoCondicaoPgtoLicitacao condicaoPgtoLicitacao;
	private Integer numParcelas;
	private boolean indExclusao;
	
	
	public ScoCondicaoPgtoLicitacao getCondicaoPgtoLicitacao() {
		return condicaoPgtoLicitacao;
	}
	public void setCondicaoPgtoLicitacao(
			ScoCondicaoPgtoLicitacao condicaoPgtoLicitacao) {
		this.condicaoPgtoLicitacao = condicaoPgtoLicitacao;
	}
	public Integer getNumParcelas() {
		return numParcelas;
	}
	public void setNumParcelas(Integer numParcelas) {
		this.numParcelas = numParcelas;
	}
	
	public boolean isIndExclusao() {
		return indExclusao;
	}
	public void setIndExclusao(boolean indExclusao) {
		this.indExclusao = indExclusao;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((condicaoPgtoLicitacao.getSeq() == null) ? 0 : condicaoPgtoLicitacao.getSeq().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoCondicaoPgtoLicitacao other = (ScoCondicaoPgtoLicitacao) obj;
		if (condicaoPgtoLicitacao.getSeq() == null) {
			if (other.getSeq() != null){
				return false;
			}
		} else if (!condicaoPgtoLicitacao.getSeq().equals(other.getSeq())){
			return false;
		}
		return true;
	}
	
}
