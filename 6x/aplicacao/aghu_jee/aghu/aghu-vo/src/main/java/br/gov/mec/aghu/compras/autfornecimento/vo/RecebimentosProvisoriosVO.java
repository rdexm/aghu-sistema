package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;

public class RecebimentosProvisoriosVO implements Serializable {

	/**
	 * VO Recebimento Provisorio
	 */
	private static final long serialVersionUID = 3175513825493567997L;

	private Integer seq;
	private Date dtGeracao;
	private Integer prfLctNumero;
	private Short nroComplemento;
	private Integer afeNumero;
	private Integer eslSeq;
	private String sigla;
	private Integer afnNumero;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Long cgc;
	private Long cpf;
	private String razaoSocial;
	private Integer numeroFornecedor;
	private Date dtEmissao;
	private Date dtAutorizacao;
	private Boolean indConfirmado;
	private Double valorTotalNfe;
	private Integer docFiscalEntradaSeq;
	private Long docFiscalEntradaNumero;
	private Double valorRecebido;	

	/**
	 * @return the seq
	 */
	public Integer getSeq() {
		return seq;
	}

	/**
	 * @param seq
	 *            the seq to set
	 */
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	/**
	 * @return the dtGeracao
	 */
	public Date getDtGeracao() {
		return dtGeracao;
	}

	/**
	 * @param dtGeracao
	 *            the dtGeracao to set
	 */
	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	/**
	 * @return the prfLctNumero
	 */
	public Integer getPrfLctNumero() {
		return prfLctNumero;
	}

	/**
	 * @param prfLctNumero
	 *            the pgrLctNumero to set
	 */
	public void setPrfLctNumero(Integer prfLctNumero) {
		this.prfLctNumero = prfLctNumero;
	}

	/**
	 * @return the nroComplemento
	 */
	public Short getNroComplemento() {
		return nroComplemento;
	}

	/**
	 * @param nroComplemento
	 *            the nroComplemento to set
	 */
	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	/**
	 * @return the afeNumero
	 */
	public Integer getAfeNumero() {
		return afeNumero;
	}

	/**
	 * @param afeNumero
	 *            the afeNumero to set
	 */
	public void setAfeNumero(Integer afeNumero) {
		this.afeNumero = afeNumero;
	}

	/**
	 * @return the eslSeq
	 */
	public Integer getEslSeq() {
		return eslSeq;
	}

	/**
	 * @param eslSeq
	 *            the eslSeq to set
	 */
	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}

	/**
	 * @return the sigla
	 */
	public String getSigla() {
		return sigla;
	}

	/**
	 * @param descricao
	 *            the sigla to set
	 */
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	/**
	 * @return the afnNumero
	 */
	public Integer getAfnNumero() {
		return afnNumero;
	}

	/**
	 * @param afnNumero
	 *            the afnNumero to set
	 */
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	/**
	 * @return the situacao
	 */
	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return situacao;
	}

	/**
	 * @param situacao
	 *            the situacao to set
	 */
	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}

	/**
	 * @return the cgc
	 */
	public Long getCgc() {
		return cgc;
	}

	/**
	 * @param cgc
	 *            the cgc to set
	 */
	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	/**
	 * @return the cpf
	 */
	public Long getCpf() {
		return cpf;
	}

	/**
	 * @param cpf
	 *            the cpf to set
	 */
	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	/**
	 * @return the razaoSocial
	 */
	public String getRazaoSocial() {
		return razaoSocial;
	}

	/**
	 * @param razaoSocial
	 *            the razaoSocial to set
	 */
	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	/**
	 * @return the numero
	 */
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	/**
	 * @param numero
	 *            the numero to set
	 */
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	/**
	 * @return the dtEmissao
	 */
	public Date getDtEmissao() {
		return dtEmissao;
	}

	/**
	 * @param dtEmissao
	 *            the dtEmissao to set
	 */
	public void setDtEmissao(Date dtEmissao) {
		this.dtEmissao = dtEmissao;
	}

	/**
	 * @return the dtAutorizacao
	 */
	public Date getDtAutorizacao() {
		return dtAutorizacao;
	}

	/**
	 * @param dtAutorizacao
	 *            the dtAutorizacao to set
	 */
	public void setDtAutorizacao(Date dtAutorizacao) {
		this.dtAutorizacao = dtAutorizacao;
	}

	/**
	 * @return the indConfirmado
	 */
	public Boolean getIndConfirmado() {
		return indConfirmado;
	}

	/**
	 * @param indConfirmado
	 *            the indConfirmado to set
	 */
	public void setIndConfirmado(Boolean indConfirmado) {
		this.indConfirmado = indConfirmado;
	}

	/**
	 * @return the valorTotalNfe
	 */
	public Double getValorTotalNfe() {
		return valorTotalNfe;
	}

	/**
	 * @param valorTotalNfe
	 *            the valorTotalNfe to set
	 */
	public void setValorTotalNfe(Double valorTotalNfe) {
		this.valorTotalNfe = valorTotalNfe;
	}

	/**
	 * @param docFiscalEntradaSeq
	 *            the docFiscalEntradaSeq to set
	 */
	public void setDocFiscalEntradaSeq(Integer docFiscalEntradaSeq) {
		this.docFiscalEntradaSeq = docFiscalEntradaSeq;
	}

	/**
	 * @return the docFiscalEntradaSeq
	 */
	public Integer getDocFiscalEntradaSeq() {
		return docFiscalEntradaSeq;
	}

	/**
	 * @param docFiscalEntradaNumero
	 *            the docFiscalEntradaNumero to set
	 */
	public void setDocFiscalEntradaNumero(Long docFiscalEntradaNumero) {
		this.docFiscalEntradaNumero = docFiscalEntradaNumero;
	}

	/**
	 * @return the docFiscalEntradaNumero
	 */
	public Long getDocFiscalEntradaNumero() {
		return docFiscalEntradaNumero;
	}

	/**
	 * @param valorRecebido
	 *            the valorRecebido to set
	 */
	public void setValorRecebido(Double valorRecebido) {
		this.valorRecebido = valorRecebido;
	}

	/**
	 * @return the valorRecebido
	 */
	public Double getValorRecebido() {
		return valorRecebido;
	}

	public enum Fields {
		SEQ("seq"), DT_GERACAO("dtGeracao"), PRF_LCT_NUMERO("prfLctNumero"), NRO_COMPLEMENTO("nroComplemento"), AFE_NUMERO("afeNumero"), ESL_SEQ(
				"eslSeq"), DESCRICAO("DESCRICAO"), AFN_NUMERO("afnNumero"), SITUACAO("situacao"), CGC("cgc"), CPF("cpf"), RAZAO_SOCIAL(
				"razaoSocial"), NUMERO("numero"), DT_EMISSAO("dtEmissao"), DT_AUTORIZACAO("dtAutorizacao"), IND_CONFIRMADO("indConfirmado"), VALOR_TOTAL_NFE(
				"valorTotalNfe"), DOC_FISCAL_ENTRADA_SEQ("docFiscalEntradaSeq"), DOC_FISCAL_ENTRADA_NUMERO("docFiscalEntradaNumero"), VALOR_RECEBIDO(
				"valorRecebido");

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
