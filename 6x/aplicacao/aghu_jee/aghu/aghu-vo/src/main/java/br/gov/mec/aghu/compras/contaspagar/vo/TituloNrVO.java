package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioMotivoEstornoTitulo;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.core.commons.BaseBean;

public class TituloNrVO implements Serializable, BaseBean {

	private static final long serialVersionUID = 7420988791529113012L;
	
	private Integer tituloSeq;
	private Short numeroParcela;
	private Date dataCompetencia;
	private Date dataGeracao;
	private Integer notaRecebimentoNumero;
	private Date tituloDataVencimento;
	private Double tituloValor;
	private DominioSituacaoTitulo tituloIndSituacao;
	private Boolean pagamentoIndEstorno;
	private DominioMotivoEstornoTitulo motivoEstorno;
	private Date notaRecebimentoDataGeracao;
	private Integer lctNumero;
	private Short autorizFornNumCompl;
	private Long documentoFiscalEntradaNumero;
	private String documentoFiscalSerie;
	private Date dataEstorno;

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
		DOCUMENTO_FISCAL_SERIE("documentoFiscalSerie"),
		DATA_ESTORNO("dataEstorno");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getTituloSeq() {
		return tituloSeq;
	}

	public void setTituloSeq(Integer tituloSeq) {
		this.tituloSeq = tituloSeq;
	}

	public Short getNumeroParcela() {
		return numeroParcela;
	}

	public void setNumeroParcela(Short numeroParcela) {
		this.numeroParcela = numeroParcela;
	}

	public Date getDataCompetencia() {
		return dataCompetencia;
	}

	public void setDataCompetencia(Date dataCompetencia) {
		this.dataCompetencia = dataCompetencia;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Integer getNotaRecebimentoNumero() {
		return notaRecebimentoNumero;
	}

	public void setNotaRecebimentoNumero(Integer notaRecebimentoNumero) {
		this.notaRecebimentoNumero = notaRecebimentoNumero;
	}

	public Date getTituloDataVencimento() {
		return tituloDataVencimento;
	}

	public void setTituloDataVencimento(Date tituloDataVencimento) {
		this.tituloDataVencimento = tituloDataVencimento;
	}

	public Double getTituloValor() {
		return tituloValor;
	}

	public void setTituloValor(Double tituloValor) {
		this.tituloValor = tituloValor;
	}

	public DominioSituacaoTitulo getTituloIndSituacao() {
		return tituloIndSituacao;
	}

	public void setTituloIndSituacao(DominioSituacaoTitulo tituloIndSituacao) {
		this.tituloIndSituacao = tituloIndSituacao;
	}

	public Boolean getPagamentoIndEstorno() {
		return pagamentoIndEstorno;
	}
	
	public String getPagamentoIndEstornoDescricao() {
		
		String descricao = "";
		
		if (getPagamentoIndEstorno() != null) {
			if (getPagamentoIndEstorno()) {
				descricao = "SIM";
			} else {
				descricao = "NÃO";
			}
		}
		
		return descricao;
	}

	public void setPagamentoIndEstorno(Boolean pagamentoIndEstorno) {
		this.pagamentoIndEstorno = pagamentoIndEstorno;
	}

	public DominioMotivoEstornoTitulo getMotivoEstorno() {
		return motivoEstorno;
	}

	public void setMotivoEstorno(DominioMotivoEstornoTitulo motivoEstorno) {
		this.motivoEstorno = motivoEstorno;
	}

	public Date getNotaRecebimentoDataGeracao() {
		return notaRecebimentoDataGeracao;
	}

	public void setNotaRecebimentoDataGeracao(Date notaRecebimentoDataGeracao) {
		this.notaRecebimentoDataGeracao = notaRecebimentoDataGeracao;
	}

	public Integer getLctNumero() {
		return lctNumero;
	}

	public void setLctNumero(Integer lctNumero) {
		this.lctNumero = lctNumero;
	}

	public Short getAutorizFornNumCompl() {
		return autorizFornNumCompl;
	}

	public void setAutorizFornNumCompl(Short autorizFornNumCompl) {
		this.autorizFornNumCompl = autorizFornNumCompl;
	}

	public Long getDocumentoFiscalEntradaNumero() {
		return documentoFiscalEntradaNumero;
	}

	public void setDocumentoFiscalEntradaNumero(Long documentoFiscalEntradaNumero) {
		this.documentoFiscalEntradaNumero = documentoFiscalEntradaNumero;
	}

	public String getDocumentoFiscalSerie() {
		return documentoFiscalSerie;
	}

	public void setDocumentoFiscalSerie(String documentoFiscalSerie) {
		this.documentoFiscalSerie = documentoFiscalSerie;
	}

	public Date getDataEstorno() {
		return dataEstorno;
	}

	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

}
