package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.estoque.vo.SceEntrSaidSemLicitacaoVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;

public class FiltroRecebeMaterialServicoSemAFVO {

	/*** Filtro ***/
	//#11033
	private SceEntrSaidSemLicitacaoVO numeroESL;
	private SceTipoMovimento tipoMovimento;
	private Boolean indAdiantamentoAF = false;
	private ScoAutorizacaoForn autorizacaoForn;
	private ScoAutorizacaoForn nroComplementoAF;
	private ScoFornecedor fornecedor;
	private SceDocumentoFiscalEntrada documentoFiscalEntrada;
	private String serieNotaFiscal;
	private String tipoNotaFiscal;
	private Date dataEmissao;
	private Date dataEntrada;
	private BigDecimal valorNota;
	private BigDecimal valorComprometidoNF;
	private BigDecimal valorComprometidoNFAntesRecebimento;
	private Integer numeroRecebimento;
	private Boolean inibeNotaFiscalEntrada;	
	
	
	public SceEntrSaidSemLicitacaoVO getNumeroESL() {
		return numeroESL;
	}
	public void setNumeroESL(SceEntrSaidSemLicitacaoVO numeroESL) {
		this.numeroESL = numeroESL;
	}
	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}
	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}
	public Boolean getIndAdiantamentoAF() {
		return indAdiantamentoAF;
	}
	public void setIndAdiantamentoAF(Boolean indAdiantamentoAF) {
		this.indAdiantamentoAF = indAdiantamentoAF;
	}
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}
	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}
	public ScoAutorizacaoForn getNroComplementoAF() {
		return nroComplementoAF;
	}
	public void setNroComplementoAF(ScoAutorizacaoForn nroComplementoAF) {
		this.nroComplementoAF = nroComplementoAF;
	}
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return documentoFiscalEntrada;
	}
	public void setDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		this.documentoFiscalEntrada = documentoFiscalEntrada;
	}
	public String getSerieNotaFiscal() {
		return serieNotaFiscal;
	}
	public void setSerieNotaFiscal(String serieNotaFiscal) {
		this.serieNotaFiscal = serieNotaFiscal;
	}
	public String getTipoNotaFiscal() {
		return tipoNotaFiscal;
	}
	public void setTipoNotaFiscal(String tipoNotaFiscal) {
		this.tipoNotaFiscal = tipoNotaFiscal;
	}
	public Date getDataEmissao() {
		return dataEmissao;
	}
	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}
	public Date getDataEntrada() {
		return dataEntrada;
	}
	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}
	public BigDecimal getValorNota() {
		return valorNota;
	}
	public void setValorNota(BigDecimal valorNota) {
		this.valorNota = valorNota;
	}
	public BigDecimal getValorComprometidoNF() {
		return valorComprometidoNF;
	}
	public void setValorComprometidoNF(BigDecimal valorComprometidoNF) {
		this.valorComprometidoNF = valorComprometidoNF;
	}
	public BigDecimal getValorComprometidoNFAntesRecebimento() {
		return valorComprometidoNFAntesRecebimento;
	}
	public void setValorComprometidoNFAntesRecebimento(
			BigDecimal valorComprometidoNFAntesRecebimento) {
		this.valorComprometidoNFAntesRecebimento = valorComprometidoNFAntesRecebimento;
	}
	public Integer getNumeroRecebimento() {
		return numeroRecebimento;
	}
	public void setNumeroRecebimento(Integer numeroRecebimento) {
		this.numeroRecebimento = numeroRecebimento;
	}
	public Boolean getInibeNotaFiscalEntrada() {
		return inibeNotaFiscalEntrada;
	}
	public void setInibeNotaFiscalEntrada(Boolean inibeNotaFiscalEntrada) {
		this.inibeNotaFiscalEntrada = inibeNotaFiscalEntrada;
	}
}