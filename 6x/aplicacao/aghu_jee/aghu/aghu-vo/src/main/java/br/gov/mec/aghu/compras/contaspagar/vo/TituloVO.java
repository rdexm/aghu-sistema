package br.gov.mec.aghu.compras.contaspagar.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * @author Jaime Brolesi
 *
 */
public class TituloVO implements BaseBean {
	
	private static final long serialVersionUID = -2963363107018831169L;
	
	private int notaRecebimentoNumero;
	
	private Integer liquidacaoSiafiVinculacaoPagamento;
	
	private Date notaRecebimentoDataGeracao;
	
	private Date dataGeracao;
	
	private Long fonteRecursoFinancCodigo;                                           
	
	private String fonteRecursoFinanDescricao;
	
	private Integer tituloSeq;
	
	private Long documentoFiscalEntradaNumero;
	
	private String documentoFiscalSerie;
	
	private Integer verbaGestaoSeq;
	
	private String verbaGestaoDescricao;
	
	private Date tituloDataVencimento;
	
	private String liquidacaoSiafiNumeroEmpenho;
	
	private Date tituloDataProgramacaoPagamento;
	
	private DominioSituacaoTitulo tituloIndSituacao;
	
	private Integer fornecedorNumero;
	
	private Long fornecedorCgc; 
	
	private Long fornecedorCpf; 
	
	private String fornecedorRazaoSocial;
	
	private ScoFornecedor fornecedor;
	
	private Date fornecedorDataValidadeFgts;
	
	private Date fornecedorDataValidadeInss;
	
	private Date fornecedorDataValidadeRecFed;	
	
	private String fornecedorTelefone;
	
	private Integer autorizacaoFornNaturezaDespesaGndCodigo;
	
	private Byte autorizacaoFornNaturezaDespesaCodigo;
	
	private String naturezaDespesaDescricao;
	
	private Integer autorizacaoFornNaturezaPropostaFornecedorLctNumero;
	
	private Short autorizacaoFornNumeroComplemento;
	
	private Short autorizFornNumCompl;
	
	private String tipoMaterial;
	
	private Integer pagamentoNumeroDocumento;
	
	private String tipoDocPagamentoDescricao;
	
	private String pagamentoIndEstorno;
	
	private Date pagamentoDataPagamento;
	
	private String pagamentoObservacao;	
	
	private Double retencaoAliquotaValorTributos;
	
	private Double tituloValor;
	
	private Double retencaoAliquotaValorDesconto;
	
	private Double retencaoAliquotaValorMulta;
	
	private Double pagamentoValorDesconto;
	
	private Double pagamentoValorAcrescimo;
	
	private Integer contaCorrenteFornecedorAgbBcoCodigo;
	
	private Integer contaCorrenteFornecedorAgbCodigo;
	
	private String bancoNomeBanco;
	
	private String contaCorrenteFornecedorContaCorrente;
	
	private String formaPagamentoDescricao;
	
	private String temINSS;
	
	private String temMulta;

	private boolean selecionado;
	
	private Date dataPrevistaPagamento;
	
	private Integer numeroParcela;
	
	private Date dataCompetencia;
	
	private String motivoEstorno;
	
	private String dataEstorno;
	
	private Integer lctNumero;
	
	public TituloVO(){
		
	}

	public int getNotaRecebimentoNumero() {
		return this.notaRecebimentoNumero;
	}

	public void setNotaRecebimentoNumero(int notaRecebimentoNumero) {
		this.notaRecebimentoNumero = notaRecebimentoNumero;
	}

	public Integer getLiquidacaoSiafiVinculacaoPagamento() {
		return liquidacaoSiafiVinculacaoPagamento;
	}

	public void setLiquidacaoSiafiVinculacaoPagamento(
			Integer liquidacaoSiafiVinculacaoPagamento) {
		this.liquidacaoSiafiVinculacaoPagamento = liquidacaoSiafiVinculacaoPagamento;
	}

	public Date getNotaRecebimentoDataGeracao() {
		return this.notaRecebimentoDataGeracao;
	}

	public void setNotaRecebimentoDataGeracao(Date notaRecebimentoDataGeracao) {
		this.notaRecebimentoDataGeracao = notaRecebimentoDataGeracao;
	}

	public Long getFonteRecursoFinancCodigo() {
		return this.fonteRecursoFinancCodigo;
	}

	public void setFonteRecursoFinancCodigo(Long fonteRecursoFinancCodigo) {
		this.fonteRecursoFinancCodigo = fonteRecursoFinancCodigo;
	}

	public String getFonteRecursoFinanDescricao() {
		return this.fonteRecursoFinanDescricao;
	}

	public void setFonteRecursoFinanDescricao(String fonteRecursoFinanDescricao) {
		this.fonteRecursoFinanDescricao = fonteRecursoFinanDescricao;
	}

	public Integer getTituloSeq() {
		return this.tituloSeq;
	}

	public void setTituloSeq(Integer tituloSeq) {
		this.tituloSeq = tituloSeq;
	}

	public Long getDocumentoFiscalEntradaNumero() {
		return this.documentoFiscalEntradaNumero;
	}

	public void setDocumentoFiscalEntradaNumero(Long documentoFiscalEntradaNumero) {
		this.documentoFiscalEntradaNumero = documentoFiscalEntradaNumero;
	}

	public String getDocumentoFiscalSerie() {
		return this.documentoFiscalSerie;
	}

	public void setDocumentoFiscalSerie(String documentoFiscalSerie) {
		this.documentoFiscalSerie = documentoFiscalSerie;
	}

	public Integer getVerbaGestaoSeq() {
		return this.verbaGestaoSeq;
	}

	public void setVerbaGestaoSeq(Integer verbaGestaoSeq) {
		this.verbaGestaoSeq = verbaGestaoSeq;
	}

	public String getVerbaGestaoDescricao() {
		return this.verbaGestaoDescricao;
	}

	public void setVerbaGestaoDescricao(String verbaGestaoDescricao) {
		this.verbaGestaoDescricao = verbaGestaoDescricao;
	}

	public Date getTituloDataVencimento() {
		return this.tituloDataVencimento;
	}

	public void setTituloDataVencimento(Date tituloDataVencimento) {
		this.tituloDataVencimento = tituloDataVencimento;
	}

	public String getLiquidacaoSiafiNumeroEmpenho() {
		return this.liquidacaoSiafiNumeroEmpenho;
	}

	public void setLiquidacaoSiafiNumeroEmpenho(String liquidacaoSiafiNumeroEmpenho) {
		this.liquidacaoSiafiNumeroEmpenho = liquidacaoSiafiNumeroEmpenho;
	}

	public Date getTituloDataProgramacaoPagamento() {
		return this.tituloDataProgramacaoPagamento;
	}

	public void setTituloDataProgramacaoPagamento(
			Date tituloDataProgramacaoPagamento) {
		this.tituloDataProgramacaoPagamento = tituloDataProgramacaoPagamento;
	}

	public DominioSituacaoTitulo getTituloIndSituacao() {
		return this.tituloIndSituacao;
	}

	public void setTituloIndSituacao(DominioSituacaoTitulo tituloIndSituacao) {
		this.tituloIndSituacao = tituloIndSituacao;
	}

	public Integer getFornecedorNumero() {
		return this.fornecedorNumero;
	}

	public void setFornecedorNumero(Integer fornecedorNumero) {
		this.fornecedorNumero = fornecedorNumero;
	}

	public Long getFornecedorCgc() {
		return this.fornecedorCgc;
	}

	public void setFornecedorCgc(Long fornecedorCgc) {
		this.fornecedorCgc = fornecedorCgc;
	}

	public Long getFornecedorCpf() {
		return fornecedorCpf;
	}

	public void setFornecedorCpf(Long fornecedorCpf) {
		this.fornecedorCpf = fornecedorCpf;
	}

	public String getFornecedorRazaoSocial() {
		return this.fornecedorRazaoSocial;
	}

	public void setFornecedorRazaoSocial(String fornecedorRazaoSocial) {
		this.fornecedorRazaoSocial = fornecedorRazaoSocial;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Date getFornecedorDataValidadeFgts() {
		return this.fornecedorDataValidadeFgts;
	}

	public void setFornecedorDataValidadeFgts(Date fornecedorDataValidadeFgts) {
		this.fornecedorDataValidadeFgts = fornecedorDataValidadeFgts;
	}

	public Date getFornecedorDataValidadeInss() {
		return this.fornecedorDataValidadeInss;
	}

	public void setFornecedorDataValidadeInss(Date fornecedorDataValidadeInss) {
		this.fornecedorDataValidadeInss = fornecedorDataValidadeInss;
	}

	public Date getFornecedorDataValidadeRecFed() {
		return this.fornecedorDataValidadeRecFed;
	}

	public void setFornecedorDataValidadeRecFed(Date fornecedorDataValidadeRecFed) {
		this.fornecedorDataValidadeRecFed = fornecedorDataValidadeRecFed;
	}

	public String getFornecedorTelefone() {
		return this.fornecedorTelefone;
	}

	public void setFornecedorTelefone(String fornecedorTelefone) {
		this.fornecedorTelefone = fornecedorTelefone;
	}

	public Integer getAutorizacaoFornNaturezaDespesaGndCodigo() {
		return this.autorizacaoFornNaturezaDespesaGndCodigo;
	}

	public void setAutorizacaoFornNaturezaDespesaGndCodigo(
			Integer autorizacaoFornNaturezaDespesaGndCodigo) {
		this.autorizacaoFornNaturezaDespesaGndCodigo = autorizacaoFornNaturezaDespesaGndCodigo;
	}

	public Byte getAutorizacaoFornNaturezaDespesaCodigo() {
		return this.autorizacaoFornNaturezaDespesaCodigo;
	}

	public void setAutorizacaoFornNaturezaDespesaCodigo(
			Byte autorizacaoFornNaturezaDespesaCodigo) {
		this.autorizacaoFornNaturezaDespesaCodigo = autorizacaoFornNaturezaDespesaCodigo;
	}

	public String getNaturezaDespesaDescricao() {
		return this.naturezaDespesaDescricao;
	}

	public void setNaturezaDespesaDescricao(String naturezaDespesaDescricao) {
		this.naturezaDespesaDescricao = naturezaDespesaDescricao;
	}

	public Integer getAutorizacaoFornNaturezaPropostaFornecedorLctNumero() {
		return this.autorizacaoFornNaturezaPropostaFornecedorLctNumero;
	}

	public void setAutorizacaoFornNaturezaPropostaFornecedorLctNumero(
			Integer autorizacaoFornNaturezaPropostaFornecedorLctNumero) {
		this.autorizacaoFornNaturezaPropostaFornecedorLctNumero = autorizacaoFornNaturezaPropostaFornecedorLctNumero;
	}

	public Short getAutorizacaoFornNumeroComplemento() {
		return this.autorizacaoFornNumeroComplemento;
	}

	public void setAutorizacaoFornNumeroComplemento(
			Short autorizacaoFornNumeroComplemento) {
		this.autorizacaoFornNumeroComplemento = autorizacaoFornNumeroComplemento;
	}

	public String getTipoMaterial() {
		return this.tipoMaterial;
	}

	public void setTipoMaterial(String tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}

	public Integer getPagamentoNumeroDocumento() {
		return this.pagamentoNumeroDocumento;
	}

	public void setPagamentoNumeroDocumento(Integer pagamentoNumeroDocumento) {
		this.pagamentoNumeroDocumento = pagamentoNumeroDocumento;
	}

	public String getTipoDocPagamentoDescricao() {
		return this.tipoDocPagamentoDescricao;
	}

	public void setTipoDocPagamentoDescricao(String tipoDocPagamentoDescricao) {
		this.tipoDocPagamentoDescricao = tipoDocPagamentoDescricao;
	}

	public String getPagamentoIndEstorno() {
		return this.pagamentoIndEstorno;
	}

	public void setPagamentoIndEstorno(String pagamentoIndEstorno) {
		this.pagamentoIndEstorno = pagamentoIndEstorno;
	}

	public Date getPagamentoDataPagamento() {
		return this.pagamentoDataPagamento;
	}

	public void setPagamentoDataPagamento(Date pagamentoDataPagamento) {
		this.pagamentoDataPagamento = pagamentoDataPagamento;
	}

	public String getPagamentoObservacao() {
		return this.pagamentoObservacao;
	}

	public void setPagamentoObservacao(String pagamentoObservacao) {
		this.pagamentoObservacao = pagamentoObservacao;
	}

	public Double getRetencaoAliquotaValorTributos() {
		return this.retencaoAliquotaValorTributos;
	}

	public void setRetencaoAliquotaValorTributos(
			Double retencaoAliquotaValorTributos) {
		this.retencaoAliquotaValorTributos = retencaoAliquotaValorTributos;
	}

	public Double getTituloValor() {
		return this.tituloValor;
	}

	public void setTituloValor(Double tituloValor) {
		this.tituloValor = tituloValor;
	}

	public Double getRetencaoAliquotaValorDesconto() {
		return this.retencaoAliquotaValorDesconto;
	}

	public void setRetencaoAliquotaValorDesconto(
			Double retencaoAliquotaValorDesconto) {
		this.retencaoAliquotaValorDesconto = retencaoAliquotaValorDesconto;
	}

	public Double getRetencaoAliquotaValorMulta() {
		return this.retencaoAliquotaValorMulta;
	}

	public void setRetencaoAliquotaValorMulta(Double retencaoAliquotaValorMulta) {
		this.retencaoAliquotaValorMulta = retencaoAliquotaValorMulta;
	}

	public Double getPagamentoValorDesconto() {
		return this.pagamentoValorDesconto;
	}

	public void setPagamentoValorDesconto(Double pagamentoValorDesconto) {
		this.pagamentoValorDesconto = pagamentoValorDesconto;
	}

	public Double getPagamentoValorAcrescimo() {
		return this.pagamentoValorAcrescimo;
	}

	public void setPagamentoValorAcrescimo(Double pagamentoValorAcrescimo) {
		this.pagamentoValorAcrescimo = pagamentoValorAcrescimo;
	}

	public Integer getContaCorrenteFornecedorAgbBcoCodigo() {
		return this.contaCorrenteFornecedorAgbBcoCodigo;
	}

	public void setContaCorrenteFornecedorAgbBcoCodigo(
			Integer contaCorrenteFornecedorAgbBcoCodigo) {
		this.contaCorrenteFornecedorAgbBcoCodigo = contaCorrenteFornecedorAgbBcoCodigo;
	}

	public Integer getContaCorrenteFornecedorAgbCodigo() {
		return this.contaCorrenteFornecedorAgbCodigo;
	}

	public void setContaCorrenteFornecedorAgbCodigo(
			Integer contaCorrenteFornecedorAgbCodigo) {
		this.contaCorrenteFornecedorAgbCodigo = contaCorrenteFornecedorAgbCodigo;
	}

	public String getBancoNomeBanco() {
		return this.bancoNomeBanco;
	}

	public void setBancoNomeBanco(String bancoNomeBanco) {
		this.bancoNomeBanco = bancoNomeBanco;
	}

	public String getContaCorrenteFornecedorContaCorrente() {
		return this.contaCorrenteFornecedorContaCorrente;
	}

	public void setContaCorrenteFornecedorContaCorrente(
			String contaCorrenteFornecedorContaCorrente) {
		this.contaCorrenteFornecedorContaCorrente = contaCorrenteFornecedorContaCorrente;
	}

	public String getFormaPagamentoDescricao() {
		return this.formaPagamentoDescricao;
	}

	public void setFormaPagamentoDescricao(String formaPagamentoDescricao) {
		this.formaPagamentoDescricao = formaPagamentoDescricao;
	}

	public String getTemINSS() {
		return this.temINSS;
	}

	public void setTemINSS(String temINSS) {
		this.temINSS = temINSS;
	}

	public String getTemMulta() {
		return this.temMulta;
	}

	public void setTemMulta(String temMulta) {
		this.temMulta = temMulta;
	}

	public Date getDataPagamento() {
		return dataPrevistaPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPrevistaPagamento = dataPagamento;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado  = selecionado;		
	}
	
	public boolean isSelecionado() {
        return selecionado;
    }

	public Short getAutorizFornNumCompl() {
		return autorizFornNumCompl;
	}

	public void setAutorizFornNumCompl(Short autorizFornNumCompl) {
		this.autorizFornNumCompl = autorizFornNumCompl;
	}
	
	public Date getDataPrevistaPagamento() {
		return dataPrevistaPagamento;
	}

	public void setDataPrevistaPagamento(Date dataPrevistaPagamento) {
		this.dataPrevistaPagamento = dataPrevistaPagamento;
	}

	public Integer getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(Integer numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public Date getDataCompetencia() {
		return dataCompetencia;
	}

	public void setDataCompetencia(Date dataCompetencia) {
		this.dataCompetencia = dataCompetencia;
	}

	public String getMotivoEstorno() {
		return motivoEstorno;
	}

	public void setMotivoEstorno(String motivoEstorno) {
		this.motivoEstorno = motivoEstorno;
	}

	public String getDataEstorno() {
		return dataEstorno;
	}

	public void setDataEstorno(String dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}
	
	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((tituloSeq == null) ? 0 : tituloSeq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof TituloVO)) {
			return false;
		}
		TituloVO other = (TituloVO) obj;
		if (tituloSeq == null) {
			if (other.tituloSeq != null) {
				return false;
			}
		} else if (!tituloSeq.equals(other.tituloSeq)) {
			return false;
		}
		return true;
	}



	public enum Fields {
		TITULO_SEQ("tituloSeq"),
		NUMERO_PARCELA("numeroParcela"),
		DATA_COMPETENCIA("dataCompetencia"),
		DATA_GERACAO("dataGeracao"),
		NOTA_RECEBIMENTO_NUMERO("notaRecebimentoNumero"),
		TITULO_DATA_VENCIMENTO("tituloDataVencimento"),
		TITULO_VALOR("tituloValor"),
		TITULO_IND_SITUACAO("tituloIndSituacao"),
		PAGAMENTO_IND_ESTORNO("pagamentoIndEstorno"),
		MOTIVO_ESTORNO("motivoEstorno"),
		NOTA_RECEBIMENTO_DATA_GERACAO("notaRecebimentoDataGeracao"),
		LCT_NUMERO("lctNumero"),
		AUTORIZ_FORN_NUM_COMPL("autorizFornNumCompl"),
		DOCUMENTO_FISCAL_ENTRADA_NUMERO("documentoFiscalEntradaNumero"),
		DOCUMENTO_FISCAL_SERIE("documentoFiscalSerie");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}