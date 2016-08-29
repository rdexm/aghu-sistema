package br.gov.mec.aghu.compras.contaspagar.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RelatorioMovimentacaoFornecedorVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6654696817257239492L;

	private Integer numero;
	private Date dtEmissao;
	private String razaoSocial;
	private String cpfCnpj;
	private Long cgc;
	private Long cpf;
	private String nf;
	private Long nfNumero;
	private String nfSerie;
	private Integer bo;
	private Integer titulo;
	private Short pc;
	private Date dtPagto;
	private Integer agencia;
	private String banco;
	private String ctaCorrente;
	private String estornado;
	private Double valorTitulo;
	private Double tributos;
	private Double desconto;
	private Double valorPagamento;
	private Double valorDf;
	private Double valorLiquido;
	private Double vlrAcresc;
	private Date dtVencto;
	private Integer doc;
	private Integer nrsSeq;
	private String dtEmissaoFormatada;
	private String dtVenctoFormatada;
	private String dtPagtoFormatada;

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getDtEmissao() {
		return dtEmissao;
	}

	public void setDtEmissao(Date dtEmissao) {
		this.dtEmissao = dtEmissao;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setNf(String nf) {
		this.nf = nf;
	}

	public String getNf() {
		return nf;
	}

	public Integer getBo() {
		return bo;
	}

	public void setBo(Integer bo) {
		this.bo = bo;
	}

	public Integer getTitulo() {
		return titulo;
	}

	public void setTitulo(Integer titulo) {
		this.titulo = titulo;
	}

	public Short getPc() {
		return pc;
	}

	public void setPc(Short pc) {
		this.pc = pc;
	}

	public Date getDtPagto() {
		return dtPagto;
	}

	public void setDtPagto(Date dtPagto) {
		this.dtPagto = dtPagto;
	}

	public Integer getAgencia() {
		return agencia;
	}

	public void setAgencia(Integer agencia) {
		this.agencia = agencia;
	}

	public String getBanco() {
		return banco;
	}

	public void setBanco(String banco) {
		this.banco = banco;
	}

	public String getCtaCorrente() {
		return ctaCorrente;
	}

	public void setCtaCorrente(String ctaCorrente) {
		this.ctaCorrente = ctaCorrente;
	}

	public String getEstornado() {
		return estornado;
	}

	public void setEstornado(String estornado) {
		this.estornado = estornado;
	}

	public Double getValorTitulo() {
		return valorTitulo;
	}

	public void setValorTitulo(Double valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public Double getTributos() {
		return tributos;
	}

	public void setTributos(Double tributos) {
		this.tributos = tributos;
	}

	public Double getDesconto() {
		return desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Double getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(Double valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public Double getValorDf() {
		return valorDf;
	}

	public void setValorDf(Double valorDf) {
		this.valorDf = valorDf;
	}

	public Double getValorLiquido() {
		return valorLiquido;
	}

	public void setValorLiquido(Double valorLiquido) {
		this.valorLiquido = valorLiquido;
	}

	public Date getDtVencto() {
		return dtVencto;
	}

	public void setDtVencto(Date dtVencto) {
		this.dtVencto = dtVencto;
	}

	public Integer getDoc() {
		return doc;
	}

	public void setDoc(Integer doc) {
		this.doc = doc;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Long getNfNumero() {
		return nfNumero;
	}

	public void setNfNumero(Long nfNumero) {
		this.nfNumero = nfNumero;
	}

	public String getNfSerie() {
		return nfSerie;
	}

	public void setNfSerie(String nfSerie) {
		this.nfSerie = nfSerie;
	}

	public Double getVlrAcresc() {
		return vlrAcresc;
	}

	public void setVlrAcresc(Double vlrAcresc) {
		this.vlrAcresc = vlrAcresc;
	}

	public Integer getNrsSeq() {
		return nrsSeq;
	}

	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}

	public enum Fields {
		DT_EMISSAO("dtEmissao"), 
		NUMERO("numero"), 
		RAZAO_SOCIAL("razaoSocial"), 
		CPF_CNPJ("cpfCnpj"), 
		CGC("cgc"), 
		CPF("cpf"), 
		NF("nf"), 
		NF_NUMERO("nfNumero"), 
		NF_SERIE("nfSerie"), 
		BO("bo"), 
		TITULO("titulo"), 
		PC("pc"), 
		DT_PAGAMENTO("dtPagto"), 
		AGENCIA("agencia"), 
		BANCO("banco"), 
		CONTA_CORRENTE("ctaCorrente"), 
		ESTORNADO("estornado"), 
		VALOR_TITULO("valorTitulo"), 
		TRIBUTOS("tributos"), 
		DESCONTO("desconto"), 
		VALOR_PAGAMENTO("valorPagamento"), 
		VALOR_DF("valorDf"), 
		VALOR_LIQUIDO("valorLiquido"), 
		DT_VENCIMENTO("dtVencto"), 
		VLR_ACRESC("vlrAcresc"), 
		NRS_SEQ("nrsSeq"), 
		DOC("doc");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getDtEmissaoFormatada() {
		return dtEmissaoFormatada;
	}

	public void setDtEmissaoFormatada(String dtEmissaoFormatada) {
		this.dtEmissaoFormatada = dtEmissaoFormatada;
	}

	public String getDtVenctoFormatada() {
		return dtVenctoFormatada;
	}

	public void setDtVenctoFormatada(String dtVenctoFormatada) {
		this.dtVenctoFormatada = dtVenctoFormatada;
	}

	public String getDtPagtoFormatada() {
		return dtPagtoFormatada;
	}

	public void setDtPagtoFormatada(String dtPagtoFormatada) {
		this.dtPagtoFormatada = dtPagtoFormatada;
	}

}
