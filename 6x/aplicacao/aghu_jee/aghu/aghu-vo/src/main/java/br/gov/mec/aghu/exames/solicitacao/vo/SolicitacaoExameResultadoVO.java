package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;


public class SolicitacaoExameResultadoVO implements Serializable {
	//TODO este VO deve ter apenas uma lista. A outra deve ser extraida daqui e colocada em outro VO proprio se for o caso.
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3035271631824608603L;
	private List<SolicitacaoExameItemVO> listaAtendimentosDoPaciente;
	private Integer totalRegistros;
	private List<VAelSolicAtendsVO> listaSolicitacaoExames;
	/**
	 * @return the listaAtendimentosDoPaciente
	 */
	public List<SolicitacaoExameItemVO> getListaAtendimentosDoPaciente() {
		return listaAtendimentosDoPaciente;
	}
	/**
	 * @param listaAtendimentosDoPaciente the listaAtendimentosDoPaciente to set
	 */
	public void setListaAtendimentosDoPaciente(
			List<SolicitacaoExameItemVO> listaAtendimentosDoPaciente) {
		this.listaAtendimentosDoPaciente = listaAtendimentosDoPaciente;
	}
	/**
	 * @return the totalRegistros
	 */
	public Integer getTotalRegistros() {
		return totalRegistros;
	}
	/**
	 * @param totalRegistros the totalRegistros to set
	 */
	public void setTotalRegistros(Integer totalRegistros) {
		this.totalRegistros = totalRegistros;
	}
	
	public List<VAelSolicAtendsVO> getListaSolicitacaoExames() {
		return listaSolicitacaoExames;
	}
	
	public void setListaSolicitacaoExames(
			List<VAelSolicAtendsVO> listaSolicitacaoExames) {
		this.listaSolicitacaoExames = listaSolicitacaoExames;
	}

}
