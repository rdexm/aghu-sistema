package br.gov.mec.aghu.compras.vo;

import java.util.List;


public class ImprimirJulgamentoPropostasLicitacaoVO {

	
	private ImprimirJulgamentoPropostasLicitacaoLctVO imprimirJulgamentoPropostasLicitacaoLctVO;//01 até 04 
	
	private List<ImprimirJulgamentoPropostasLicitacaoDetalhesVO> imprimirJulgamentoPropostasLicitacaoDetalhesVO; //05 até 36
	
	private List<ImprimirJulgamentoPropostasLicitacaoRodapeVO> imprimirJulgamentoPropostasLicitacaoRodapeVO;//38 até 43
	
	
			
	public ImprimirJulgamentoPropostasLicitacaoLctVO getImprimirJulgamentoPropostasLicitacaoLctVO() {
		return imprimirJulgamentoPropostasLicitacaoLctVO;
	}
	public void setImprimirJulgamentoPropostasLicitacaoLctVO(
			ImprimirJulgamentoPropostasLicitacaoLctVO imprimirJulgamentoPropostasLicitacaoLctVO) {
		this.imprimirJulgamentoPropostasLicitacaoLctVO = imprimirJulgamentoPropostasLicitacaoLctVO;
	}
	
	public List<ImprimirJulgamentoPropostasLicitacaoDetalhesVO> getImprimirJulgamentoPropostasLicitacaoDetalhesVO() {
		return imprimirJulgamentoPropostasLicitacaoDetalhesVO;
	}
	public void setImprimirJulgamentoPropostasLicitacaoDetalhesVO(
			List<ImprimirJulgamentoPropostasLicitacaoDetalhesVO> imprimirJulgamentoPropostasLicitacaoDetalhesVO) {
		this.imprimirJulgamentoPropostasLicitacaoDetalhesVO = imprimirJulgamentoPropostasLicitacaoDetalhesVO;
	}
	public List<ImprimirJulgamentoPropostasLicitacaoRodapeVO> getImprimirJulgamentoPropostasLicitacaoRodapeVO() {
		return imprimirJulgamentoPropostasLicitacaoRodapeVO;
	}
	public void setImprimirJulgamentoPropostasLicitacaoRodapeVO(
			List<ImprimirJulgamentoPropostasLicitacaoRodapeVO> imprimirJulgamentoPropostasLicitacaoRodapeVO) {
		this.imprimirJulgamentoPropostasLicitacaoRodapeVO = imprimirJulgamentoPropostasLicitacaoRodapeVO;
	}
	

	
}
