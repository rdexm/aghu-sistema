package br.gov.mec.aghu.compras.contaspagar.impl;


public class RegistroCsvPagamentosRealizados extends AbstractRegistroCsv {
	
	protected enum RegistroCsvTitulosPendentesEnum implements CamposEnum {

		TIPO_DOC("tipoDoc","Tipo Doc"), 
		NRO_DOCUMENTO("numeroDocumento","Nro Doc Pagamento"), 
		VALOR_TOTAL("valorTotal","Valor Total"), 
		TRIBUTOS("tributos","Tributos"), 
		CNPJ("cnpj","CNPJ"), 
		CPF("cpf","CPF"), 
		RAZAO_SOCIAL("razaoSocial","Razão Social"), 
		NUMERO("nf","NF"), 
		TITULO("titulo","Título"), 
		NRS_SEQ("nrsSeq","NR"), 
		VALOR_PAGAMENTO("vlrPagamento","Valor Pagamento");

		private final String desc;
		private final String campo;

		private RegistroCsvTitulosPendentesEnum(final String campo, final String desc) {
			this.campo = campo;
			this.desc = desc;
		}

		@Override
		public int getIndice() {
			return this.ordinal();
		}

		@Override
		public String getDescricao() {
			return this.desc;
		}

		@Override
		public String getCampo() {
			return this.campo;
		}
	}

	private String tipoDoc;
	private Integer numeroDocumento;
	private Double valorTotal;
	private Double tributos;
	private Long cnpj;
	private Long cpf;
	private String razaoSocial;
	private Integer nf;
	private Integer titulo;
	private Integer nrsSeq;
	private Double vlrPagamento;
	
	public RegistroCsvPagamentosRealizados() {
		super(NOME_TEMPLATE_LINHA_PONTO_VIRGULA);
	}

	@Override
	public CamposEnum[] obterCampos() {
		// TODO Auto-generated method stub
		return RegistroCsvTitulosPendentesEnum.values();
	}

	/**
	 *  Formatar resultados
	 */
	@Override
	protected Object obterRegistro(CamposEnum campo) {
		
		Object result = null;
		
		result = super.obterRegistro(campo);
		
		return result;
	}


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


	public Integer getNf() {
		return nf;
	}


	public void setNf(Integer nf) {
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
	}

	
	
}