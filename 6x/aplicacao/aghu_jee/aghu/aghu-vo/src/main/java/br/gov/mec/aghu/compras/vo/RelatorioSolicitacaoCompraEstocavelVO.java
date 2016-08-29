package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class RelatorioSolicitacaoCompraEstocavelVO implements Serializable, Comparable<RelatorioSolicitacaoCompraEstocavelVO> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8154925820449395503L;

	private Integer codigoGrupoMaterial;
	private Integer codigo;
	private String unidade;
	private Integer numSolicitacao;
	private String descricao;	
	private List<ConsumoMedioMensalVO> consumoMensal;
	private Integer mediaMensal;	
	private String periodo;
	private Integer consumoMedioSazonal;
	private Integer estoqueMaximo;
	private Integer pontoPedido;
	private Integer estoqueSeg;
	private Integer loteRep;
	private String classeAbc;
	private Date dtUltimaMovimentacao;
	private Date dtUltimaCompra;
	private BigDecimal valorUnitarioCompra;
	private Integer saldoTotalEstoque;
	private Integer quantidadeBloqueada;
	private Integer quantidadeDisponivel;
	private Integer quantidadeSolicitada;	
	private List<SubRelatorioSolicitacoesEstocaveisVO> solicitacoesEstocaveis;	
	private Date dataSolicitacao;
	private Integer centroCustoRequisitante;
	private Integer almox;
	private Integer tempoReposicao;
	private Integer saldoAtual;
	private BigDecimal duracaoEstoque;
	private Integer pontoPedidoPlanejamento;
	private String pontoPedCalc;
	private Integer quantidadeSolcitadaReferente;
	private Integer quantidadeAutorizada;
	private String digitadoPor;
	
	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public Integer getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	public String getUnidade() {
		return unidade;
	}
	
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	
	public Integer getNumSolicitacao() {
		return numSolicitacao;
	}
	
	public void setNumSolicitacao(Integer numSolicitacao) {
		this.numSolicitacao = numSolicitacao;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public List<ConsumoMedioMensalVO> getConsumoMensal() {
		return consumoMensal;
	}
	
	public void setConsumoMensal(List<ConsumoMedioMensalVO> consumoMensal) {
		this.consumoMensal = consumoMensal;
	}
	
	public Integer getMediaMensal() {
		return mediaMensal;
	}
	
	public void setMediaMensal(Integer mediaMensal) {
		this.mediaMensal = mediaMensal;
	}
	
	public String getPeriodo() {
		return periodo;
	}
	
	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}
	
	public Integer getConsumoMedioSazonal() {
		return consumoMedioSazonal;
	}
	
	public void setConsumoMedioSazonal(Integer consumoMedioSazonal) {
		this.consumoMedioSazonal = consumoMedioSazonal;
	}
	
	public Integer getEstoqueMaximo() {
		return estoqueMaximo;
	}
	
	public void setEstoqueMaximo(Integer estoqueMaximo) {
		this.estoqueMaximo = estoqueMaximo;
	}
	
	public Integer getPontoPedido() {
		return pontoPedido;
	}
	
	public void setPontoPedido(Integer pontoPedido) {
		this.pontoPedido = pontoPedido;
	}
	
	public Integer getEstoqueSeg() {
		return estoqueSeg;
	}
	
	public void setEstoqueSeg(Integer estoqueSeg) {
		this.estoqueSeg = estoqueSeg;
	}
	
	public Integer getLoteRep() {
		return loteRep;
	}
	
	public void setLoteRep(Integer loteRep) {
		this.loteRep = loteRep;
	}
	
	public String getClasseAbc() {
		return classeAbc;
	}
	
	public void setClasseAbc(String classeAbc) {
		this.classeAbc = classeAbc;
	}
	
	public Date getDtUltimaMovimentacao() {
		return dtUltimaMovimentacao;
	}
	
	public void setDtUltimaMovimentacao(Date dtUltimaMovimentacao) {
		this.dtUltimaMovimentacao = dtUltimaMovimentacao;
	}
	
	public Date getDtUltimaCompra() {
		return dtUltimaCompra;
	}
	
	public void setDtUltimaCompra(Date dtUltimaCompra) {
		this.dtUltimaCompra = dtUltimaCompra;
	}
	
	public BigDecimal getValorUnitarioCompra() {
		return valorUnitarioCompra;
	}
	
	public void setValorUnitarioCompra(BigDecimal valorUnitarioCompra) {
		this.valorUnitarioCompra = valorUnitarioCompra;
	}
	
	public Integer getSaldoTotalEstoque() {
		return saldoTotalEstoque;
	}
	
	public void setSaldoTotalEstoque(Integer saldoTotalEstoque) {
		this.saldoTotalEstoque = saldoTotalEstoque;
	}
	
	public Integer getQuantidadeBloqueada() {
		return quantidadeBloqueada;
	}
	
	public void setQuantidadeBloqueada(Integer quantidadeBloqueada) {
		this.quantidadeBloqueada = quantidadeBloqueada;
	}
	
	public Integer getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}
	
	public void setQuantidadeDisponivel(Integer quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}
	
	public Integer getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}
	
	public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}

	
	public List<SubRelatorioSolicitacoesEstocaveisVO> getSolicitacoesEstocaveis() {
		return solicitacoesEstocaveis;
	}

	public void setSolicitacoesEstocaveis(
			List<SubRelatorioSolicitacoesEstocaveisVO> solicitacoesEstocaveis) {
		this.solicitacoesEstocaveis = solicitacoesEstocaveis;
	}

	public Date getDataSolicitacao() {
		return dataSolicitacao;
	}
	
	public void setDataSolicitacao(Date dataSolicitacao) {
		this.dataSolicitacao = dataSolicitacao;
	}
	
	public Integer getCentroCustoRequisitante() {
		return centroCustoRequisitante;
	}
	
	public void setCentroCustoRequisitante(Integer centroCustoRequisitante) {
		this.centroCustoRequisitante = centroCustoRequisitante;
	}
	
	public Integer getAlmox() {
		return almox;
	}
	
	public void setAlmox(Integer almox) {
		this.almox = almox;
	}
		
	public Integer getTempoReposicao() {
		return tempoReposicao;
	}
	
	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}
	
	public Integer getSaldoAtual() {
		return saldoAtual;
	}
	
	public void setSaldoAtual(Integer saldoAtual) {
		this.saldoAtual = saldoAtual;
	}
	
	public BigDecimal getDuracaoEstoque() {
		return duracaoEstoque;
	}
	
	public void setDuracaoEstoque(BigDecimal duracaoEstoque) {
		this.duracaoEstoque = duracaoEstoque;
	}
	
	public Integer getPontoPedidoPlanejamento() {
		return pontoPedidoPlanejamento;
	}
	
	public void setPontoPedidoPlanejamento(Integer pontoPedidoPlanejamento) {
		this.pontoPedidoPlanejamento = pontoPedidoPlanejamento;
	}
	
	public String getPontoPedCalc() {
		return pontoPedCalc;
	}
	
	public void setPontoPedCalc(String pontoPedCalc) {
		this.pontoPedCalc = pontoPedCalc;
	}
	
	public Integer getQuantidadeSolcitadaReferente() {
		return quantidadeSolcitadaReferente;
	}
	
	public void setQuantidadeSolcitadaReferente(Integer quantidadeSolcitadaReferente) {
		this.quantidadeSolcitadaReferente = quantidadeSolcitadaReferente;
	}
	
	public Integer getQuantidadeAutorizada() {
		return quantidadeAutorizada;
	}
	
	public void setQuantidadeAutorizada(Integer quantidadeAutorizada) {
		this.quantidadeAutorizada = quantidadeAutorizada;
	}
	
	public String getDigitadoPor() {
		return digitadoPor;
	}
	
	public void setDigitadoPor(String digitadoPor) {
		this.digitadoPor = digitadoPor;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(getCodigoGrupoMaterial())
				.append(getCodigo()).append(getPontoPedido()).toHashCode();
	}

	@Override
	public boolean equals(Object o) {
		RelatorioSolicitacaoCompraEstocavelVO other = (RelatorioSolicitacaoCompraEstocavelVO) o;
		return new EqualsBuilder()
				.append(getCodigoGrupoMaterial(),
						other.getCodigoGrupoMaterial())
				.append(getCodigo(), other.getCodigo())
				.append(getPontoPedido(), other.getPontoPedido()).isEquals();
	}

	@Override
	public int compareTo(RelatorioSolicitacaoCompraEstocavelVO o) {
		return new CompareToBuilder()
				.append(getCodigoGrupoMaterial(), o.getCodigoGrupoMaterial())
				.append(getCodigo(), o.getCodigo())
				.append(getPontoPedido(), o.getPontoPedido()).toComparison();
	}
}
























