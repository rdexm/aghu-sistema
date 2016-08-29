package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;

/**
 * VO responsável por valores de programação de entrega dos itens de AF.
 * 
 * @author mlcruz
 */
public class ProgrEntregaItensAfVO {	
	private Short numeroComplemento;
	private Short numeroItemLicitacao;
	private Integer numeroProposta;
	private DominioTipoSolicitacao tipoSolicitacao;
	private String descricaoSolicitacao;
	private Integer qtdeLiberar;
	private BigDecimal valorLiberar;
	private Integer qtdeParcelaAf;
	private BigDecimal valorParcelaAf;
	private Integer qtdeDetalhada;
	private BigDecimal valorDetalhado;
	private Date previsaoEntrega;
	private DominioSimNao indPlanejada;
	private FsoVerbaGestao verbaGestao;
	private Integer indPrioridade;
	private ScoMaterial material;
	private FsoNaturezaDespesa naturezaDespesa;
	private ScoServico servico;
	private FccCentroCustos centroCusto;
	private ScoSolicitacaoServico solicitacaoServico;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private Long seqDetalhe;
	
	// Getters/Setters
	
	public Short getNumeroComplemento() {
		return numeroComplemento;
	}
	
	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}
	
	public Short getNumeroItemLicitacao() {
		return numeroItemLicitacao;
	}
	
	public void setNumeroItemLicitacao(Short numeroItemLicitacao) {
		this.numeroItemLicitacao = numeroItemLicitacao;
	}
	
	public Integer getNumeroProposta() {
		return numeroProposta;
	}
	
	public void setNumeroProposta(Integer numeroProposta) {
		this.numeroProposta = numeroProposta;
	}
	
	public DominioTipoSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}
	
	public void setTipoSolicitacao(DominioTipoSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}
	
	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}
	
	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}
	
	public Integer getQtdeLiberar() {
		return qtdeLiberar;
	}
	
	public void setQtdeLiberar(Integer qtdeLiberar) {
		this.qtdeLiberar = qtdeLiberar;
	}
	
	public BigDecimal getValorLiberar() {
		return valorLiberar;
	}
	
	public void setValorLiberar(BigDecimal valorLiberar) {
		this.valorLiberar = valorLiberar;
	}
	
	public Integer getQtdeParcelaAf() {
		return qtdeParcelaAf;
	}
	
	public void setQtdeParcelaAf(Integer qtdeParcelaAf) {
		this.qtdeParcelaAf = qtdeParcelaAf;
	}
	
	public BigDecimal getValorParcelaAf() {
		return valorParcelaAf;
	}
	
	public void setValorParcelaAf(BigDecimal valorParcelaAf) {
		this.valorParcelaAf = valorParcelaAf;
	}
	
	public Integer getQtdeDetalhada() {
		return qtdeDetalhada;
	}
	
	public void setQtdeDetalhada(Integer qtdeDetalhada) {
		this.qtdeDetalhada = qtdeDetalhada;
	}
	
	public BigDecimal getValorDetalhado() {
		return valorDetalhado;
	}
	
	public void setValorDetalhado(BigDecimal valorDetalhado) {
		this.valorDetalhado = valorDetalhado;
	}
	
	public Date getPrevisaoEntrega() {
		return previsaoEntrega;
	}
	
	public void setPrevisaoEntrega(Date previsaoEntrega) {
		this.previsaoEntrega = previsaoEntrega;
	}
	
	public DominioSimNao getIndPlanejada() {
		return indPlanejada;
	}
	
	public void setIndPlanejada(DominioSimNao indPlanejada) {
		this.indPlanejada = indPlanejada;
	}
	
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}
	
	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}
	
	public Integer getIndPrioridade() {
		return indPrioridade;
	}
	
	public void setIndPrioridade(Integer indPrioridade) {
		this.indPrioridade = indPrioridade;
	}
	
	public ScoMaterial getMaterial() {
		return material;
	}
	
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}
	
	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}
	
	public ScoServico getServico() {
		return servico;
	}
	
	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
	
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}
	
	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}
	
	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}
	
	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}
	
	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}
	
	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}
	
	public Long getSeqDetalhe() {
		return seqDetalhe;
	}
	
	public void setSeqDetalhe(Long seqDetalhe) {
		this.seqDetalhe = seqDetalhe;
	}
}