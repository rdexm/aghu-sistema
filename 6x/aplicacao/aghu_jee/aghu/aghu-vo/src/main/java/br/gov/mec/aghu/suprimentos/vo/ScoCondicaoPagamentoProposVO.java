package br.gov.mec.aghu.suprimentos.vo;

import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.core.commons.BaseBean;
 
public class ScoCondicaoPagamentoProposVO implements BaseBean{
	
	private static final long serialVersionUID = -7258088757967619458L;
	
	private ScoCondicaoPagamentoPropos condicaoPgtoProposta;
	private Integer numParcelas;
	private Boolean indEscolhida;
	
	public ScoCondicaoPagamentoPropos getCondicaoPgtoProposta() {
		return condicaoPgtoProposta;
	}
	
	public void setCondicaoPgtoProposta(
			ScoCondicaoPagamentoPropos condicaoPgtoProposta) {
		this.condicaoPgtoProposta = condicaoPgtoProposta;
	}
	
	public Integer getNumParcelas() {
		return numParcelas;
	}
	
	public void setNumParcelas(Integer numParcelas) {
		this.numParcelas = numParcelas;
	}

	public Boolean getIndEscolhida() {
		return indEscolhida;
	}

	public void setIndEscolhida(Boolean indEscolhida) {
		this.indEscolhida = indEscolhida;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((condicaoPgtoProposta.getNumero() == null) ? 0 : condicaoPgtoProposta.getNumero().hashCode());
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
		ScoCondicaoPagamentoPropos other = (ScoCondicaoPagamentoPropos) obj;
		if (condicaoPgtoProposta.getNumero() == null) {
			if (other.getNumero() != null){
				return false;
			}
		} else if (!condicaoPgtoProposta.getNumero().equals(other.getNumero())){
			return false;
		}
		return true;
	}
	
	
}
