package br.gov.mec.aghu.faturamento.vo;

import java.util.List;

public class RelatorioImpressaoEspelhoContaVO {

	private FatEspelhoEncerramentoPreviaVO fatEspelhoEncerramentoPreviaVO;
	private List<ProcedimentoRealizadoDadosOPMVO> listaProcedimentosRealizados;
    private List<ProcedimentoRealizadoDadosOPMVO> listaDadosOPM;
    private List<ValoresPreviaVO> listaValoresPrevia;
    private List<FatMotivoRejeicaoContasVO> listaMotivosRejeicao;
	
	public FatEspelhoEncerramentoPreviaVO getFatEspelhoEncerramentoPreviaVO() {
		return fatEspelhoEncerramentoPreviaVO;
	}

	public void setFatEspelhoEncerramentoPreviaVO(FatEspelhoEncerramentoPreviaVO fatEspelhoEncerramentoPreviaVO) {
		this.fatEspelhoEncerramentoPreviaVO = fatEspelhoEncerramentoPreviaVO;
	}

	public List<ProcedimentoRealizadoDadosOPMVO> getListaProcedimentosRealizados() {
		return listaProcedimentosRealizados;
	}

	public void setListaProcedimentosRealizados(
			List<ProcedimentoRealizadoDadosOPMVO> listaProcedimentosRealizados) {
		this.listaProcedimentosRealizados = listaProcedimentosRealizados;
	}

	public List<ProcedimentoRealizadoDadosOPMVO> getListaDadosOPM() {
		return listaDadosOPM;
	}

	public void setListaDadosOPM(List<ProcedimentoRealizadoDadosOPMVO> listaDadosOPM) {
		this.listaDadosOPM = listaDadosOPM;
	}

	public List<ValoresPreviaVO> getListaValoresPrevia() {
		return listaValoresPrevia;
	}

	public void setListaValoresPrevia(List<ValoresPreviaVO> listaValoresPrevia) {
		this.listaValoresPrevia = listaValoresPrevia;
	}

	public List<FatMotivoRejeicaoContasVO> getListaMotivosRejeicao() {
		return listaMotivosRejeicao;
	}

	public void setListaMotivosRejeicao(
			List<FatMotivoRejeicaoContasVO> listaMotivosRejeicao) {
		this.listaMotivosRejeicao = listaMotivosRejeicao;
	}
}
