package br.gov.mec.aghu.internacao.pesquisa.vo;

import java.math.BigDecimal;
import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;


public class PesquisarSituacaoLeitosVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8851823640648217202L;

	private String descricao;
	
	private Integer capacidadeInstalada;
	
	private Integer capacidadeOperacao;
	
	private Integer livre;
	
	private BigDecimal percentualOcupacao;
	
	private Integer ocupado;
	
	private Integer bloqueioLimpeza;
	
	private Integer bloqueio;
	
	private Integer bloqueioInfeccao;
	
	private Integer desativado;
	
	private Integer reserva;
	
	private List<DetalhesPesquisarSituacaoLeitosVO> detalhes;
	
	private Integer bloqueioTotal;

	public PesquisarSituacaoLeitosVO() {
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Integer getCapacidadeInstalada() {
		return capacidadeInstalada;
	}

	public void setCapacidadeInstalada(Integer capacidadeInstalada) {
		this.capacidadeInstalada = capacidadeInstalada;
	}

	public Integer getCapacidadeOperacao() {
		return capacidadeOperacao;
	}

	public void setCapacidadeOperacao(Integer capacidadeOperacao) {
		this.capacidadeOperacao = capacidadeOperacao;
	}

	public Integer getLivre() {
		return livre;
	}

	public void setLivre(Integer livre) {
		this.livre = livre;
	}

	public BigDecimal getPercentualOcupacao() {
		return percentualOcupacao;
	}

	public void setPercentualOcupacao(BigDecimal percentualOcupacao) {
		this.percentualOcupacao = percentualOcupacao;
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
	
	public Integer getBloqueioTotal() {
		return bloqueioTotal;
	}

	public void setBloqueioTotal(Integer bloqueioTotal) {
		this.bloqueioTotal = bloqueioTotal;
	}
	
}
