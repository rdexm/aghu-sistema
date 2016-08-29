package br.gov.mec.aghu.compras.pac.vo;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ItemLicitacaoCabecalhoVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4789878031278715210L;
	
	private String loteTipo5;
	private List<ItensLicitacaoVO> listaTipo05;
	private List<SelecaoFornecedorParticipanteVO> listaTipo10;
	private List<PropostaLoteLicitacaoVO> listaTipo08;
	private List<PropostaItemLicitacaoVO> listaTipo09;
	
	
	public ItemLicitacaoCabecalhoVO(){
		this.listaTipo05 = new ArrayList<ItensLicitacaoVO>();
		this.listaTipo10 = new ArrayList<SelecaoFornecedorParticipanteVO>();
		this.listaTipo08 = new ArrayList<PropostaLoteLicitacaoVO>();
		this.listaTipo09 = new ArrayList<PropostaItemLicitacaoVO>();
	}
	
	public String getLoteTipo5() {
		return loteTipo5;
	}

	public void setLoteTipo5(String loteTipo5) {
		this.loteTipo5 = loteTipo5;
	}
	
	public List<ItensLicitacaoVO> getListaTipo05() {
		return listaTipo05;
	}
	public void setListaTipo05(List<ItensLicitacaoVO> listaTipo05) {
		this.listaTipo05 = listaTipo05;
	}
	public List<SelecaoFornecedorParticipanteVO> getListaTipo10() {
		return listaTipo10;
	}
	public void setListaTipo10(List<SelecaoFornecedorParticipanteVO> listaTipo10) {
		this.listaTipo10 = listaTipo10;
	}
	public List<PropostaLoteLicitacaoVO> getListaTipo08() {
		return listaTipo08;
	}
	public void setListaTipo08(List<PropostaLoteLicitacaoVO> listaTipo08) {
		this.listaTipo08 = listaTipo08;
	}
	public List<PropostaItemLicitacaoVO> getListaTipo09() {
		return listaTipo09;
	}
	public void setListaTipo09(List<PropostaItemLicitacaoVO> listaTipo09) {
		this.listaTipo09 = listaTipo09;
	}
	
}
