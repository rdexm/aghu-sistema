package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoPedidoMatExpedienteVO implements BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2153815492055332900L;
	private Integer numeroNotaFiscal;
	private Integer sequencialPedido;
	private Date dataNotaFiscal;
	private BigDecimal valorTotal;
	private String indValidacaoNF;
	private String indIntegracaoEstoque;
	private String indGeracaoRM;
	
	private Date dataInicioEmissao;
	private Date dataFimEmissao;
	private Date dataInicioPedido;
	private Date dataFimPedido;

	private Date dataPedRecebInicial;
	private Date dataPedRecebFinal;
	private Integer codigoCentroCusto;
	private Integer numeroPedido;
	private Integer matriculaSolicitante;
	private Short vinculoSolicitante;
	
	public ScoPedidoMatExpedienteVO() {}

	public ScoPedidoMatExpedienteVO(Integer numeroNotaFiscal,
			Integer sequencialPedido, Date dataNotaFiscal,
			BigDecimal valorTotal, String indValidacaoNF,
			String indIntegracaoEstoque, String indGeracaoRM,
			Date dataInicioEmissao, Date dataFimEmissao, Date dataInicioPedido,
			Date dataFimPedido) {
		super();
		this.numeroNotaFiscal = numeroNotaFiscal;
		this.sequencialPedido = sequencialPedido;
		this.dataNotaFiscal = dataNotaFiscal;
		this.valorTotal = valorTotal;
		this.indValidacaoNF = indValidacaoNF;
		this.indIntegracaoEstoque = indIntegracaoEstoque;
		this.indGeracaoRM = indGeracaoRM;
		this.dataInicioEmissao = dataInicioEmissao;
		this.dataFimEmissao = dataFimEmissao;
		this.dataInicioPedido = dataInicioPedido;
		this.dataFimPedido = dataFimPedido;
	}



	public Integer getNumeroNotaFiscal() {
		return numeroNotaFiscal;
	}

	public Integer getSequencialPedido() {
		return sequencialPedido;
	}

	public Date getDataNotaFiscal() {
		return dataNotaFiscal;
	}

	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public String getIndValidacaoNF() {
		return indValidacaoNF;
	}

	public String getIndIntegracaoEstoque() {
		return indIntegracaoEstoque;
	}

	public String getIndGeracaoRM() {
		return indGeracaoRM;
	}

	public Date getDataInicioEmissao() {
		return dataInicioEmissao;
	}

	public Date getDataFimEmissao() {
		return dataFimEmissao;
	}

	public Date getDataInicioPedido() {
		return dataInicioPedido;
	}

	public Date getDataFimPedido() {
		return dataFimPedido;
	}

	public void setNumeroNotaFiscal(Integer numeroNotaFiscal) {
		this.numeroNotaFiscal = numeroNotaFiscal;
	}

	public void setSequencialPedido(Integer sequencialPedido) {
		this.sequencialPedido = sequencialPedido;
	}

	public void setDataNotaFiscal(Date dataNotaFiscal) {
		this.dataNotaFiscal = dataNotaFiscal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}

	public void setIndValidacaoNF(String indValidacaoNF) {
		this.indValidacaoNF = indValidacaoNF;
	}

	public void setIndIntegracaoEstoque(String indIntegracaoEstoque) {
		this.indIntegracaoEstoque = indIntegracaoEstoque;
	}

	public void setIndGeracaoRM(String indGeracaoRM) {
		this.indGeracaoRM = indGeracaoRM;
	}

	public void setDataInicioEmissao(Date dataInicioEmissao) {
		this.dataInicioEmissao = dataInicioEmissao;
	}

	public void setDataFimEmissao(Date dataFimEmissao) {
		this.dataFimEmissao = dataFimEmissao;
	}

	public void setDataInicioPedido(Date dataInicioPedido) {
		this.dataInicioPedido = dataInicioPedido;
	}

	public void setDataFimPedido(Date dataFimPedido) {
		this.dataFimPedido = dataFimPedido;
	}

	public void setDataPedRecebInicial(Date dataPedRecebInicial) {
		this.dataPedRecebInicial = dataPedRecebInicial;
	}

	public Date getDataPedRecebInicial() {
		return dataPedRecebInicial;
	}

	public void setDataPedRecebFinal(Date dataPedRecebFinal) {
		this.dataPedRecebFinal = dataPedRecebFinal;
	}

	public Date getDataPedRecebFinal() {
		return dataPedRecebFinal;
	}

	public void setCodigoCentroCusto(Integer codigoCentroCusto) {
		this.codigoCentroCusto = codigoCentroCusto;
	}

	public Integer getCodigoCentroCusto() {
		return codigoCentroCusto;
	}

	public void setNumeroPedido(Integer numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public Integer getNumeroPedido() {
		return numeroPedido;
	}

	public void setMatriculaSolicitante(Integer matriculaSolicitante) {
		this.matriculaSolicitante = matriculaSolicitante;
	}

	public Integer getMatriculaSolicitante() {
		return matriculaSolicitante;
	}

	public void setVinculoSolicitante(Short vinculoSolicitante) {
		this.vinculoSolicitante = vinculoSolicitante;
	}

	public Short getVinculoSolicitante() {
		return vinculoSolicitante;
	}
	
}
