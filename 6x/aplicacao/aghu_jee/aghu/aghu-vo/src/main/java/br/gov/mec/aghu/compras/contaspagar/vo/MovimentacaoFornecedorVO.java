package br.gov.mec.aghu.compras.contaspagar.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioMotivoEstornoTitulo;
import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.core.commons.BaseBean;

public class MovimentacaoFornecedorVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6654696817257239492L;

	private Integer seqTitulo;
	private Short nroParcela;
	private DominioSituacaoTitulo indSituacao;
	private Integer nrsSeq;
	private Integer pfrLctNumero;
	private Short nroComplemento;
	private Integer nroContrato;
	private Date dataGeracao;
	private Integer matricula;
	private Short vinCodigo;
	private Date dtVencimento;
	private Double valor;
	private String nroEmpenho;
	private BigDecimal valorEmpenho;
	private String razaoSocial;
	private Long cgc;
	private Long cpf;
	private String cpfCnpj;
	private Boolean indEstorno;
	private DominioMotivoEstornoTitulo motivoEstorno;
	private Date dataEstorno;
	private Integer matriculaEstornado;
	private Short serVinCodigoEstornado;
	private Integer nroDocumento;
	private Date dataPagamento;
	private Double valorPagamento;
	private Double vlrDesconto;
	private Double valorTributo;
	private String corSituacao;
	private String toolTipTitulo;
	private Long numeroDocumentoFiscal;
	private String serie;
	private Integer nroAF;

	public MovimentacaoFornecedorVO() {
		super();
	}

	public Integer getSeqTitulo() {
		return seqTitulo;
	}

	public void setSeqTitulo(Integer seqTitulo) {
		this.seqTitulo = seqTitulo;
	}

	public Short getNroParcela() {
		return nroParcela;
	}

	public void setNroParcela(Short nroParcela) {
		this.nroParcela = nroParcela;
	}

	public DominioSituacaoTitulo getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoTitulo indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getNrsSeq() {
		return nrsSeq;
	}

	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}

	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}

	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public Integer getNroContrato() {
		return nroContrato;
	}

	public void setNroContrato(Integer nroContrato) {
		this.nroContrato = nroContrato;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Date getDtVencimento() {
		return dtVencimento;
	}

	public void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getNroEmpenho() {
		return nroEmpenho;
	}

	public void setNroEmpenho(String nroEmpenho) {
		this.nroEmpenho = nroEmpenho;
	}

	public BigDecimal getValorEmpenho() {
		return valorEmpenho;
	}

	public void setValorEmpenho(BigDecimal valorEmpenho) {
		this.valorEmpenho = valorEmpenho;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
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

	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public Boolean getIndEstorno() {
		return indEstorno;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}

	public DominioMotivoEstornoTitulo getMotivoEstorno() {
		return motivoEstorno;
	}

	public void setMotivoEstorno(DominioMotivoEstornoTitulo motivoEstorno) {
		this.motivoEstorno = motivoEstorno;
	}

	public Date getDataEstorno() {
		return dataEstorno;
	}

	public void setDataEstorno(Date dataEstorno) {
		this.dataEstorno = dataEstorno;
	}

	public Integer getMatriculaEstornado() {
		return matriculaEstornado;
	}

	public void setMatriculaEstornado(Integer matriculaEstornado) {
		this.matriculaEstornado = matriculaEstornado;
	}

	public Short getSerVinCodigoEstornado() {
		return serVinCodigoEstornado;
	}

	public void setSerVinCodigoEstornado(Short serVinCodigoEstornado) {
		this.serVinCodigoEstornado = serVinCodigoEstornado;
	}

	public Integer getNroDocumento() {
		return nroDocumento;
	}

	public void setNroDocumento(Integer nroDocumento) {
		this.nroDocumento = nroDocumento;
	}

	public Date getDataPagamento() {
		return dataPagamento;
	}

	public void setDataPagamento(Date dataPagamento) {
		this.dataPagamento = dataPagamento;
	}

	public Double getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(Double valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public Double getVlrDesconto() {
		return vlrDesconto;
	}

	public void setVlrDesconto(Double vlrDesconto) {
		this.vlrDesconto = vlrDesconto;
	}
	
	public String getCorSituacao() {
		return corSituacao;
	}

	public void setCorSituacao(String corSituacao) {
		this.corSituacao = corSituacao;
	}

	public Double getValorTributo() {
		return valorTributo;
	}

	public void setValorTributo(Double valorTributo) {
		this.valorTributo = valorTributo;
	}

	public String getToolTipTitulo() {
		return toolTipTitulo;
	}

	public void setToolTipTitulo(String toolTipTitulo) {
		this.toolTipTitulo = toolTipTitulo;
	}

	public Long getNumeroDocumentoFiscal() {
		return numeroDocumentoFiscal;
	}

	public void setNumeroDocumentoFiscal(Long numeroDocumentoFiscal) {
		this.numeroDocumentoFiscal = numeroDocumentoFiscal;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public enum Fields {
		NUMERO_TITULO("seqTitulo"),
		NRO_PARCELA("nroParcela"),
		IND_SITUACAO("indSituacao"),
		NRS_SEQ("nrsSeq"),
		PFRL_CT_NUMERO("pfrCtNumero"),
		NRO_COMPLEMENTO("nroComplemento"),
		NRO_CONTRATO("nroContrato"),
		DATA_GERACAO("dataGeracao"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		DATA_VENCIMENTO("dtVencimento"),
		VALOR("valor"),
		NRO_EMPENHO("nroEmpenho"),
		VALOR_EMPENHO("valorEmpenho"),
		RAZAO_SOCIAL("razaoSocial"),
		CGC("cgc"),
		CPF("cpf"),
		CPF_CNPJ("cpfCnpj"),
		IND_ESTORNO("indEstorno"),
		MOTIVO_ESTORNO("motivoEstorno"),
		DATA_ESTORNO("dataEstorno"),
		MATRICULA_ESTORNADO("matriculaEstornado"),
		SER_VIN_CODIGO_ESTORNADO("serVinCodigoEstornado"),
		NRO_DOCUMENTO("nroDocumento"),
		DATA_PAGAMENTO("dataPagamento"),
		VALOR_PAGAMENTO("valorPagamento"),
		VALOR_DESCONTO("vlrDesconto"),
		NUMERO_DOCUMENTO_FISCAL("numeroDocumentoFiscal"),
		SERIE("serie"),
		PFRLCTNUMERO("pfrLctNumero"),
		NRO_AF("nroAF");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((seqTitulo == null) ? 0 : seqTitulo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MovimentacaoFornecedorVO other = (MovimentacaoFornecedorVO) obj;
		if (seqTitulo == null) {
			if (other.seqTitulo != null){
				return false;
			}	
		} else if (!seqTitulo.equals(other.seqTitulo)){
			return false;
		}
		return true;
	}
	
	
	
}
