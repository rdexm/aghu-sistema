package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VScoComprMaterialId implements EntityCompositeId {
	
	private static final long serialVersionUID = 6950413144197817700L;

	@Column(name = "DT_GERACAO_MOVTO")
	private Date dtGeracaoMovto;
	
	@Column(name = "FRN_NUMERO")
	private Integer numeroFrn;
	
	@Column(name = "AFN_NUMERO")
	private Integer numeroAfn;
	
	@Column(name = "DT_GERACAO_NR")
	private Date dtGeracaoNr;
	
	@Column(name = "SLC_NUMERO")
	private Integer numeroSlc;
	
	@Column(name = "DT_ABERTURA_PROPOSTA")
	private Date dtAberturaProposta;
	
	@Column(name = "NRS_SEQ")
	private Integer nrsSeq;
	
	@Column(name = "LCT_NUMERO")
	private Integer numeroLct;
	
	@Column(name = "VALOR")
	private BigDecimal valor;
	
	@Column(name = "QUANTIDADE")
	private Integer quantidade;
	
	@Column(name = "FORMA_PAG")
	private String formaPagamento;
	
	@Column(name = "CUSTO_UNITARIO")
	private BigDecimal custoUnitario;
	
	@Column(name = "DFE_SEQ")
	private Integer dfeSeq;
	
	@Column(name = "NRO_COMPLEMENTO")
	private Short numeroComplemento;
	
	@Column(name = "DFE_NUMERO")
	private Integer numeroDfe;
	
	@Column(name = "MCM_CODIGO")
	private Integer mcmCodigo;
	
	@Column(name = "NC_MCM_CODIGO")
	private Integer ncMcmCodigo;
	
	@Column(name = "NC_NUMERO")
	private Integer ncNumero;
	
	@Column(name = "MAT_CODIGO")
	private Integer codigoMaterial;
	
	public VScoComprMaterialId() {
	}

	public VScoComprMaterialId(Date dtGeracaoMovto, Integer numeroFrn,
			Integer numeroAfn, Date dtGeracaoNr, Integer numeroSlc,
			Date dtAberturaProposta, Integer nrsSeq, Integer numeroLct,
			BigDecimal valor, Integer quantidade, String formaPagamento,
			BigDecimal custoUnitario, Integer dfeSeq, Short numeroComplemento,
			Integer numeroDfe, Integer mcmCodigo, Integer ncMcmCodigo,
			Integer ncNumero, Integer codigoMaterial) {
		super();
		this.dtGeracaoMovto = dtGeracaoMovto;
		this.numeroFrn = numeroFrn;
		this.numeroAfn = numeroAfn;
		this.dtGeracaoNr = dtGeracaoNr;
		this.numeroSlc = numeroSlc;
		this.dtAberturaProposta = dtAberturaProposta;
		this.nrsSeq = nrsSeq;
		this.numeroLct = numeroLct;
		this.valor = valor;
		this.quantidade = quantidade;
		this.formaPagamento = formaPagamento;
		this.custoUnitario = custoUnitario;
		this.dfeSeq = dfeSeq;
		this.numeroComplemento = numeroComplemento;
		this.numeroDfe = numeroDfe;
		this.mcmCodigo = mcmCodigo;
		this.ncMcmCodigo = ncMcmCodigo;
		this.ncNumero = ncNumero;
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codigoMaterial == null) ? 0 : codigoMaterial.hashCode());
		result = prime * result
				+ ((custoUnitario == null) ? 0 : custoUnitario.hashCode());
		result = prime * result + ((dfeSeq == null) ? 0 : dfeSeq.hashCode());
		result = prime
				* result
				+ ((dtAberturaProposta == null) ? 0 : dtAberturaProposta
						.hashCode());
		result = prime * result
				+ ((dtGeracaoMovto == null) ? 0 : dtGeracaoMovto.hashCode());
		result = prime * result
				+ ((dtGeracaoNr == null) ? 0 : dtGeracaoNr.hashCode());
		result = prime * result
				+ ((formaPagamento == null) ? 0 : formaPagamento.hashCode());
		result = prime * result
				+ ((mcmCodigo == null) ? 0 : mcmCodigo.hashCode());
		result = prime * result
				+ ((ncMcmCodigo == null) ? 0 : ncMcmCodigo.hashCode());
		result = prime * result
				+ ((ncNumero == null) ? 0 : ncNumero.hashCode());
		result = prime * result + ((nrsSeq == null) ? 0 : nrsSeq.hashCode());
		result = prime * result
				+ ((numeroAfn == null) ? 0 : numeroAfn.hashCode());
		result = prime
				* result
				+ ((numeroComplemento == null) ? 0 : numeroComplemento
						.hashCode());
		result = prime * result
				+ ((numeroDfe == null) ? 0 : numeroDfe.hashCode());
		result = prime * result
				+ ((numeroFrn == null) ? 0 : numeroFrn.hashCode());
		result = prime * result
				+ ((numeroLct == null) ? 0 : numeroLct.hashCode());
		result = prime * result
				+ ((numeroSlc == null) ? 0 : numeroSlc.hashCode());
		result = prime * result
				+ ((quantidade == null) ? 0 : quantidade.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
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
		if (!(obj instanceof VScoComprMaterialId)) {
			return false;
		}
		VScoComprMaterialId other = (VScoComprMaterialId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		
		umEqualsBuilder.append(this.getCodigoMaterial(), other.getCodigoMaterial());
		umEqualsBuilder.append(this.getCustoUnitario(), other.getCustoUnitario());
		umEqualsBuilder.append(this.getDfeSeq(), other.getDfeSeq());
		umEqualsBuilder.append(this.getDtAberturaProposta(), other.getDtAberturaProposta());
		umEqualsBuilder.append(this.getDtGeracaoMovto(), other.getDtGeracaoMovto());
		umEqualsBuilder.append(this.getDtGeracaoNr(), other.getDtGeracaoNr());
		umEqualsBuilder.append(this.getFormaPagamento(), other.getFormaPagamento());
		umEqualsBuilder.append(this.getMcmCodigo(), other.getMcmCodigo());
		umEqualsBuilder.append(this.getNcMcmCodigo(), other.getNcMcmCodigo());
		umEqualsBuilder.append(this.getNcNumero(), other.getNcNumero());
		umEqualsBuilder.append(this.getNrsSeq(), other.getNrsSeq());
		umEqualsBuilder.append(this.getNumeroAfn(), other.getNumeroAfn());
		umEqualsBuilder.append(this.getNumeroComplemento(), other.getNumeroComplemento());
		umEqualsBuilder.append(this.getNumeroDfe(), other.getNumeroDfe());
		umEqualsBuilder.append(this.getNumeroFrn(), other.getNumeroFrn());
		umEqualsBuilder.append(this.getNumeroLct(), other.getNumeroLct());
		umEqualsBuilder.append(this.getNumeroSlc(), other.getNumeroSlc());
		umEqualsBuilder.append(this.getQuantidade(), other.getQuantidade());
		umEqualsBuilder.append(this.getValor(), other.getValor());
		return umEqualsBuilder.isEquals();
	}
}
