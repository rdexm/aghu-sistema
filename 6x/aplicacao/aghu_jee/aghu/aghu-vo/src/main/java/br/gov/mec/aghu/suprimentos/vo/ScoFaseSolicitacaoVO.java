package br.gov.mec.aghu.suprimentos.vo;

import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoFaseSolicitacaoVO  implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2509175394203537612L;
	
	private Integer numeroPac;
	private Short numeroItemPac;
	private String descricaoSolicitacao;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private ScoSolicitacaoServico solicitacaoServico;
	private ScoItemLicitacao itemLicitacao;
	
	public ScoFaseSolicitacaoVO() {
		
	}
		
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((numeroPac == null) ? 0 : numeroPac.hashCode())
				+ ((numeroItemPac == null) ? 0 : numeroItemPac.hashCode());
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
		if (!(obj instanceof ScoFaseSolicitacaoVO)){
			return false;
		}
		ScoFaseSolicitacaoVO other = (ScoFaseSolicitacaoVO) obj;
		if (numeroPac == null) {
			if (other.numeroPac != null){
				return false;
			}
		} else if (!numeroPac.equals(other.numeroPac)){
			return false;
		}
		return true;
	}




	public Integer getNumeroPac() {
		return numeroPac;
	}


	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}


	public Short getNumeroItemPac() {
		return numeroItemPac;
	}


	public void setNumeroItemPac(Short numeroItemPac) {
		this.numeroItemPac = numeroItemPac;
	}


	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}


	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}


	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}


	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}


	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}


	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	public ScoItemLicitacao getItemLicitacao() {
		return itemLicitacao;
	}

	public void setItemLicitacao(ScoItemLicitacao itemLicitacao) {
		this.itemLicitacao = itemLicitacao;
	}
	
}
