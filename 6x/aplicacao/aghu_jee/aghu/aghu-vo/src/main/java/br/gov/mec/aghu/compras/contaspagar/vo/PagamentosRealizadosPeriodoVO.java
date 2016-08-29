package br.gov.mec.aghu.compras.contaspagar.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PagamentosRealizadosPeriodoVO implements BaseBean {

	/*-*-*-* Constante de Inicializacao *-*-*-*/
	private static final long serialVersionUID = 3483227679069715917L;
	
	
	/*-*-*-* Variaveis *-*-*-*/
	private String tipoDoc;
	private Integer numeroDocumento;
	private Double valorTotal;
	private Double tributos;
	private Long cnpj;
	private Long cpf;
	private String razaoSocial;
	private Long nf;
	private Integer titulo;
	private Integer nrsSeq;
	private Double vlrPagamento;
	
	
	/*-*-*-* Construtores *-*-*-*/
	public PagamentosRealizadosPeriodoVO() {
		super();
	}
	

	/*-*-*-* Enum *-*-*-*/
	public enum Fields {
		TIPO_DOC("tipoDoc"), NRO_DOCUMENTO("numeroDocumento"), VALOR_TOTAL(
				"valorTotal"), TRIBUTOS("tributos"), CNPJ("cnpj"), CPF("cpf"), RAZAO_SOCIAL(
				"razaoSocial"), NUMERO("nf"), TITULO("titulo"), NRS_SEQ("nrsSeq"),
				VALOR_PAGAMENTO("vlrPagamento");

		String field;

		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}

	}

	
	/*-*-*-* Metodos Publicos *-*-*-*/
	private void setValorPagamentoCorreto(){
		if(vlrPagamento != null && tributos != null) {
			vlrPagamento -= tributos;
		}
	}

	
	/*-*-*-* Getters e Setters *-*-*-*/
	public String getTipoDoc() {
		return tipoDoc;
	}

	public void setTipoDoc(String tipoDoc) {
		this.tipoDoc = tipoDoc;
	}

	public Integer getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(Integer numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Double getTributos() {
		return tributos;
	}

	public void setTributos(Double tributos) {
		this.tributos = tributos;
		setValorPagamentoCorreto();
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		this.cnpj = cnpj;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public Long getNf() {
		return nf;
	}

	public void setNf(Long nf) {
		this.nf = nf;
	}

	public Integer getTitulo() {
		return titulo;
	}

	public void setTitulo(Integer titulo) {
		this.titulo = titulo;
	}

	public Integer getNrsSeq() {
		return nrsSeq;
	}

	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}

	public Double getVlrPagamento() {
		return vlrPagamento;
	}

	public void setVlrPagamento(Double vlrPagamento) {
		this.vlrPagamento = vlrPagamento;
		setValorPagamentoCorreto();
	}
}
