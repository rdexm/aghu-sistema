package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;


public class RelatorioAFJnVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8204946066804440305L;
	
	private ScoAutorizacaoFornJn autorizacaoForn;
	private List<ScoItemAutorizacaoFornJnVO> listaItensAfVO;
	
		
		
	public ScoAutorizacaoFornJn getAutorizacaoForn() {
		return autorizacaoForn;
	}
	public void setAutorizacaForn(ScoAutorizacaoFornJn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	


	public List<ScoItemAutorizacaoFornJnVO> getListaItensAfVO() {
		return listaItensAfVO;
	}
	public void setListaItensAfVO(List<ScoItemAutorizacaoFornJnVO> listaItensAfVO) {
		this.listaItensAfVO = listaItensAfVO;
	}
	public void setAutorizacaoForn(ScoAutorizacaoFornJn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}





	public enum Fields {
		SCO_AUT_FORN("autorizacaForn");
	
	
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		public String toString() {
			return this.field;
		}
	}

}
