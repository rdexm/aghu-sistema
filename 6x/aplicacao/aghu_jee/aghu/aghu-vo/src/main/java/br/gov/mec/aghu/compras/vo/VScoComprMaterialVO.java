package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.Date;

public class VScoComprMaterialVO {

	private Date dtGeracaoMovto;
	private Integer numeroFrn;
	private Integer numeroAfn;
	private Date dtGeracaoNr;
	private Integer numeroSlc;
	private Date dtAberturaProposta;
	private Integer nrsSeq;
	private Integer numeroLct;
	private BigDecimal valor;
	private Integer quantidade;
	private String formaPagamento;
	private BigDecimal custoUnitario;
	private Integer dfeSeq;
	private Short numeroComplemento;
	private Integer numeroDfe;
	private Integer mcmCodigo;
	private Integer ncMcmCodigo;
	private Integer ncNumero;
	private Integer codigoMaterial;
	private String razaoSocialFornecedor;
	private Long cnpjFornecedor;
	
	public Date getDtGeracaoMovto() {
		return dtGeracaoMovto;
	}

	public void setDtGeracaoMovto(Date dtGeracaoMovto) {
		this.dtGeracaoMovto = dtGeracaoMovto;
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}

	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}

	public Integer getNumeroAfn() {
		return numeroAfn;
	}

	public void setNumeroAfn(Integer numeroAfn) {
		this.numeroAfn = numeroAfn;
	}

	public Date getDtGeracaoNr() {
		return dtGeracaoNr;
	}

	public void setDtGeracaoNr(Date dtGeracaoNr) {
		this.dtGeracaoNr = dtGeracaoNr;
	}

	public Integer getNumeroSlc() {
		return numeroSlc;
	}

	public void setNumeroSlc(Integer numeroSlc) {
		this.numeroSlc = numeroSlc;
	}

	public Date getDtAberturaProposta() {
		return dtAberturaProposta;
	}

	public void setDtAberturaProposta(Date dtAberturaProposta) {
		this.dtAberturaProposta = dtAberturaProposta;
	}

	public Integer getNrsSeq() {
		return nrsSeq;
	}

	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}

	public Integer getNumeroLct() {
		return numeroLct;
	}

	public void setNumeroLct(Integer numeroLct) {
		this.numeroLct = numeroLct;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public BigDecimal getCustoUnitario() {
		return custoUnitario;
	}

	public void setCustoUnitario(BigDecimal custoUnitario) {
		this.custoUnitario = custoUnitario;
	}

	public Integer getDfeSeq() {
		return dfeSeq;
	}

	public void setDfeSeq(Integer dfeSeq) {
		this.dfeSeq = dfeSeq;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public Integer getNumeroDfe() {
		return numeroDfe;
	}

	public void setNumeroDfe(Integer numeroDfe) {
		this.numeroDfe = numeroDfe;
	}

	public Integer getMcmCodigo() {
		return mcmCodigo;
	}

	public void setMcmCodigo(Integer mcmCodigo) {
		this.mcmCodigo = mcmCodigo;
	}

	public Integer getNcMcmCodigo() {
		return ncMcmCodigo;
	}

	public void setNcMcmCodigo(Integer ncMcmCodigo) {
		this.ncMcmCodigo = ncMcmCodigo;
	}

	public Integer getNcNumero() {
		return ncNumero;
	}

	public void setNcNumero(Integer ncNumero) {
		this.ncNumero = ncNumero;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}

	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}

	public Long getCnpjFornecedor() {
		return cnpjFornecedor;
	}

	public void setCnpjFornecedor(Long cnpjFornecedor) {
		this.cnpjFornecedor = cnpjFornecedor;
	}

public enum Fields {
		
		MAT_CODIGO ("codigoMaterial"),
		DT_GERACAO_MOVTO ("dtGeracaoMovto"),
		FRN_NUMERO ("numeroFrn"),
		AFN_NUMERO ("numeroAfn"),
		DT_GERACAO_NR ("dtGeracaoNr"),
		SLC_NUMERO ("numeroSlc"),
		DT_ABERTURA_PROPOSTA ("dtAberturaProposta"),
		NRS_SEQ ("nrsSeq"),
		LCT_NUMERO ("numeroLct"),
		VALOR ("valor"),
		QUANTIDADE ("quantidade"),
		FORMA_PAG ("formaPagamento"),
		CUSTO_UNITARIO ("custoUnitario"),
		DFE_SEQ ("dfeSeq"),
		NRO_COMPLEMENTO ("numeroComplemento"),
		DFE_NUMERO ("numeroDfe"),
		MCM_CODIGO ("mcmCodigo"),
		NC_MCM_CODIGO ("ncMcmCodigo"),
		NC_NUMERO ("ncNumero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
