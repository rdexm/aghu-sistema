package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;


public class RelatorioAFPVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8204946066804440305L;
	
	private ScoAutorizacaoForn autorizacaoForn;
	private ScoAutorizacaoFornecedorPedido afp;
	
	private List<ScoItemAFPVO> listaItensAfVO;
	
			
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}
	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaForn) {
		this.autorizacaoForn = autorizacaForn;
	}
	

	public List<ScoItemAFPVO> getListaItensAfVO() {
		return listaItensAfVO;
	}
	public void setListaItensAfVO(List<ScoItemAFPVO> listaItensAfVO) {
		this.listaItensAfVO = listaItensAfVO;
	}



	public ScoAutorizacaoFornecedorPedido getAfp() {
		return afp;
	}
	public void setAfp(ScoAutorizacaoFornecedorPedido afp) {
		this.afp = afp;
	}

}
