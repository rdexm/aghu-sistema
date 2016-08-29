package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;

public class RecebimentoMaterialServicoVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8204946066804440306L;

	private Short itlNumero;
	private Integer parcela;
	private Integer afp;
	private Date dtPrevEntrega;
	private String unidade;
	private Integer saldoQtd;
	private Integer qtdEntregue;
	private Integer qtdEntregueAnterior;
	private BigDecimal valorEntregue;
	private BigDecimal valorTotal;
	private BigDecimal valorUnitario;
	private BigDecimal valorSaldo;
	private Integer codigoMaterialServico;
//	private String nomeTruncadoMaterialServico;
	private String nomeMaterialServico;
	private String descricaoMaterialServico;
	private String descricaoSolicitacao;
	private String marcaComercial;
	private Integer fatorConversao;
	private Boolean indConsignado;
	private String unidadeMedida;
	private Integer qtdSaldoAssinado;
	private BigDecimal valorSaldoAssinado;
	private String descricaoQuantidadeItemAF;
	private String localEstoque;
	
	private Integer seqParcela;
	private Integer numeroAf;
	private Integer numeroItemAf;
	
	private DominioTipoFaseSolicitacao tipoSolicitacao;
	private Boolean somenteUmaSolicitacaoParaParcela;
	
	private List<PriorizaEntregaVO> listaPriorizacao;
	private boolean permiteRecebimento;
	
	
	public Short getItlNumero() {
		return itlNumero;
	}
	
	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}
	
	public Integer getParcela() {
		return parcela;
	}
	
	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}
	
	public Integer getAfp() {
		return afp;
	}
	
	public void setAfp(Integer afp) {
		this.afp = afp;
	}
	
	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}
	
	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}
	
	public String getUnidade() {
		return unidade;
	}
	
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	
	public Integer getSaldoQtd() {
		return saldoQtd;
	}
	
	public void setSaldoQtd(Integer saldoQtd) {
		this.saldoQtd = saldoQtd;
	}
	
	public Integer getQtdEntregue() {
		return qtdEntregue;
	}
	
	public void setQtdEntregue(Integer qtdEntregue) {
		this.qtdEntregue = qtdEntregue;
	}
	
	public Integer getQtdEntregueAnterior() {
		return qtdEntregueAnterior;
	}

	public void setQtdEntregueAnterior(Integer qtdEntregueAnterior) {
		this.qtdEntregueAnterior = qtdEntregueAnterior;
	}
	
	public BigDecimal getValorEntregue() {
		return valorEntregue;
	}

	public void setValorEntregue(BigDecimal valorEntregue) {
		this.valorEntregue = valorEntregue;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}
	
	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorSaldo() {
		return valorSaldo;
	}
	
	public void setValorSaldo(BigDecimal valorSaldo) {
		this.valorSaldo = valorSaldo;
	}
	
	public Integer getCodigoMaterialServico() {
		return codigoMaterialServico;
	}
	
	public void setCodigoMaterialServico(Integer codigoMaterialServico) {
		this.codigoMaterialServico = codigoMaterialServico;
	}
	
	public String getNomeTruncadoMaterialServico() {
		return StringUtils.abbreviate(getNomeMaterialServico(), 150);
	}

//	public void setNomeTruncadoMaterialServico(String nomeTruncadoMaterialServico) {
//		this.nomeTruncadoMaterialServico = StringUtils.abbreviate(getNomeMaterialServico(), 40);
//		this.nomeTruncadoMaterialServico = this.nomeTruncadoMaterialServico + "...";
//		//this.nomeTruncadoMaterialServico = nomeTruncadoMaterialServico;
//	}

	public String getNomeMaterialServico() {
		return nomeMaterialServico;
	}
	
	public void setNomeMaterialServico(String nomeMaterialServico) {
		this.nomeMaterialServico = nomeMaterialServico;
	}
	
	public String getDescricaoMaterialServico() {
		return descricaoMaterialServico;
	}
	
	public void setDescricaoMaterialServico(String descricaoMaterialServico) {
		this.descricaoMaterialServico = descricaoMaterialServico;
	}
	
	public String getDescricaoSolicitacao() {
		return descricaoSolicitacao;
	}

	public void setDescricaoSolicitacao(String descricaoSolicitacao) {
		this.descricaoSolicitacao = descricaoSolicitacao;
	}

	public String getMarcaComercial() {
		return marcaComercial;
	}
	
	public void setMarcaComercial(String marcaComercial) {
		this.marcaComercial = marcaComercial;
	}
	
	public Integer getFatorConversao() {
		return fatorConversao;
	}
	
	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}
	
	public Boolean getIndConsignado() {
		return indConsignado;
	}
	
	public void setIndConsignado(Boolean indConsignado) {
		this.indConsignado = indConsignado;
	}
	
	public String getUnidadeMedida() {
		return unidadeMedida;
	}
	
	public void setUnidadeMedida(String unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public Integer getQtdSaldoAssinado() {
		return qtdSaldoAssinado;
	}

	public void setQtdSaldoAssinado(Integer qtdSaldoAssinado) {
		this.qtdSaldoAssinado = qtdSaldoAssinado;
	}

	public BigDecimal getValorSaldoAssinado() {
		return valorSaldoAssinado;
	}

	public void setValorSaldoAssinado(BigDecimal valorSaldoAssinado) {
		this.valorSaldoAssinado = valorSaldoAssinado;
	}	
	
	public Integer getSeqParcela() {
		return seqParcela;
	}

	public void setSeqParcela(Integer seqParcela) {
		this.seqParcela = seqParcela;
	}

	public Integer getNumeroItemAf() {
		return numeroItemAf;
	}

	public void setNumeroItemAf(Integer numeroItemAf) {
		this.numeroItemAf = numeroItemAf;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public DominioTipoFaseSolicitacao getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	public void setTipoSolicitacao(DominioTipoFaseSolicitacao tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;
	}

	public Boolean getSomenteUmaSolicitacaoParaParcela() {
		return somenteUmaSolicitacaoParaParcela;
	}

	public void setSomenteUmaSolicitacaoParaParcela(
			Boolean somenteUmaSolicitacaoParaParcela) {
		this.somenteUmaSolicitacaoParaParcela = somenteUmaSolicitacaoParaParcela;
	}

	public List<PriorizaEntregaVO> getListaPriorizacao() {
		return listaPriorizacao;
	}

	public void setListaPriorizacao(List<PriorizaEntregaVO> listaPriorizacao) {
		this.listaPriorizacao = listaPriorizacao;
	}

	public boolean isPermiteRecebimento() {
		return permiteRecebimento;
	}

	public void setPermiteRecebimento(boolean permiteRecebimento) {
		this.permiteRecebimento = permiteRecebimento;
	}
 
	public String getDescricaoQuantidadeItemAF() {
		return descricaoQuantidadeItemAF;
	}

	public void setDescricaoQuantidadeItemAF(String descricaoQuantidadeItemAF) {
		this.descricaoQuantidadeItemAF = descricaoQuantidadeItemAF;
	}

	public String getLocalEstoque() {
		return localEstoque;
	}

	public void setLocalEstoque(String localEstoque) {
		this.localEstoque = localEstoque;
	}	
}
