package br.gov.mec.aghu.internacao.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.internacao.pesquisa.vo.DetalhesPesquisarSituacaoLeitosVO;


public class PesquisaSituacoesLeitosVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8207377066880838801L;

	private Integer livre;
	
	private Integer ocupado;

	private Integer bloqueioLimpeza;

	private Integer bloqueio;

	private Integer bloqueioInfeccao;

	private Integer desativado;

	private Integer reserva;
	
	//Campos adicionados em 17/11/2011 
	private Integer capacidadeInstalada;
	
	private Integer bloqueioTotal;
	
	private Integer capacidadeOperacao;

	private List<DetalhesPesquisarSituacaoLeitosVO> detalhes;

	public Integer getLivre() {
		return livre;
	}

	public void setLivre(Integer livre) {
		this.livre = livre;
	}

	public Integer getOcupado() {
		return ocupado;
	}

	public void setOcupado(Integer ocupado) {
		this.ocupado = ocupado;
	}

	public Integer getBloqueioLimpeza() {
		return bloqueioLimpeza;
	}

	public void setBloqueioLimpeza(Integer bloqueioLimpeza) {
		this.bloqueioLimpeza = bloqueioLimpeza;
	}

	public Integer getBloqueio() {
		return bloqueio;
	}

	public void setBloqueio(Integer bloqueio) {
		this.bloqueio = bloqueio;
	}

	public Integer getBloqueioInfeccao() {
		return bloqueioInfeccao;
	}

	public void setBloqueioInfeccao(Integer bloqueioInfeccao) {
		this.bloqueioInfeccao = bloqueioInfeccao;
	}

	public Integer getDesativado() {
		return desativado;
	}

	public void setDesativado(Integer desativado) {
		this.desativado = desativado;
	}

	public Integer getReserva() {
		return reserva;
	}

	public void setReserva(Integer reserva) {
		this.reserva = reserva;
	}

	public List<DetalhesPesquisarSituacaoLeitosVO> getDetalhes() {
		return detalhes;
	}

	public void setDetalhes(List<DetalhesPesquisarSituacaoLeitosVO> detalhes) {
		this.detalhes = detalhes;
	}
	
	public Integer getCapacidadeInstalada() {
		return capacidadeInstalada;
	}

	public void setCapacidadeInstalada(Integer capacidadeInstalada) {
		this.capacidadeInstalada = capacidadeInstalada;
	}

	public Integer getBloqueioTotal() {
		return bloqueioTotal;
	}

	public void setBloqueioTotal(Integer bloqueioTotal) {
		this.bloqueioTotal = bloqueioTotal;
	}

	public Integer getCapacidadeOperacao() {
		return capacidadeOperacao;
	}

	public void setCapacidadeOperacao(Integer capacidadeOperacao) {
		this.capacidadeOperacao = capacidadeOperacao;
	}
	
}
