package br.gov.mec.aghu.model;

// Generated 05/11/2011 13:42:10 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * SceItemDfe generated by hbm2java
 */
@Entity
@Table(name = "SCE_ITEM_DFE", schema = "AGH")
public class SceItemDocumentoFiscalEntrada extends BaseEntityId<SceItemDocumentoFiscalEntradaId> implements java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2543392334410489633L;
	private SceItemDocumentoFiscalEntradaId id;
	private Integer version;
	private Short nroItemNf;
	private Integer matCodigo;
	private Integer srvCodigo;
	private Short cfoCodigo;
	private Integer friCodigo;
	private Integer irpNrpSeq;
	private Integer irpPeaIafAfnNumero;
	private Short irpPeaIafNumero;
	private Short irpPeaParcela;
	private SceEntradaSaidaSemLicitacao sceEntradaSaidaSemLicitacao;
	private String descricao;
	private Double quantidade;
	private Double valorUnitario;
	private Double valorTotal;
	private Double valorFrete;
	private Double valorSeguro;
	private Double valorDesconto;
	private Float aliquotaIpi;
	private Double valorIpi;
	private Float aliquotaIcms;
	private Double valorIcms;
	private String ncmCodigo;
	private Double valorDescontoLivre;

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "dfeSeq", column = @Column(name = "DFE_SEQ", nullable = false)),
			@AttributeOverride(name = "nroItem", column = @Column(name = "NRO_ITEM", nullable = false)) })
	public SceItemDocumentoFiscalEntradaId getId() {
		return this.id;
	}

	public void setId(SceItemDocumentoFiscalEntradaId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "NRO_ITEM_NF")
	public Short getNroItemNf() {
		return this.nroItemNf;
	}

	public void setNroItemNf(Short nroItemNf) {
		this.nroItemNf = nroItemNf;
	}

	@Column(name = "MAT_CODIGO")
	public Integer getMatCodigo() {
		return this.matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	@Column(name = "SRV_CODIGO")
	public Integer getSrvCodigo() {
		return this.srvCodigo;
	}

	public void setSrvCodigo(Integer srvCodigo) {
		this.srvCodigo = srvCodigo;
	}

	@Column(name = "CFO_CODIGO")
	public Short getCfoCodigo() {
		return this.cfoCodigo;
	}

	public void setCfoCodigo(Short cfoCodigo) {
		this.cfoCodigo = cfoCodigo;
	}

	@Column(name = "FRI_CODIGO")
	public Integer getFriCodigo() {
		return this.friCodigo;
	}

	public void setFriCodigo(Integer friCodigo) {
		this.friCodigo = friCodigo;
	}

	@Column(name = "IRP_NRP_SEQ")
	public Integer getIrpNrpSeq() {
		return this.irpNrpSeq;
	}

	public void setIrpNrpSeq(Integer irpNrpSeq) {
		this.irpNrpSeq = irpNrpSeq;
	}

	@Column(name = "IRP_PEA_IAF_AFN_NUMERO")
	public Integer getIrpPeaIafAfnNumero() {
		return this.irpPeaIafAfnNumero;
	}

	public void setIrpPeaIafAfnNumero(Integer irpPeaIafAfnNumero) {
		this.irpPeaIafAfnNumero = irpPeaIafAfnNumero;
	}

	@Column(name = "IRP_PEA_IAF_NUMERO")
	public Short getIrpPeaIafNumero() {
		return this.irpPeaIafNumero;
	}

	public void setIrpPeaIafNumero(Short irpPeaIafNumero) {
		this.irpPeaIafNumero = irpPeaIafNumero;
	}

	@Column(name = "IRP_PEA_PARCELA")
	public Short getIrpPeaParcela() {
		return this.irpPeaParcela;
	}

	public void setIrpPeaParcela(Short irpPeaParcela) {
		this.irpPeaParcela = irpPeaParcela;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESL_SEQ", referencedColumnName = "SEQ")
	public SceEntradaSaidaSemLicitacao getSceEntradaSaidaSemLicitacao() {
		return this.sceEntradaSaidaSemLicitacao;
	}

	public void setSceEntradaSaidaSemLicitacao(SceEntradaSaidaSemLicitacao sceEntradaSaidaSemLicitacao) {
		this.sceEntradaSaidaSemLicitacao = sceEntradaSaidaSemLicitacao;
	}

	@Column(name = "DESCRICAO", length = 240)
	@Length(max = 240)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "QUANTIDADE", precision = 17, scale = 17)
	public Double getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Double quantidade) {
		this.quantidade = quantidade;
	}

	@Column(name = "VALOR_UNITARIO", precision = 17, scale = 17)
	public Double getValorUnitario() {
		return this.valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	@Column(name = "VALOR_TOTAL", precision = 17, scale = 17)
	public Double getValorTotal() {
		return this.valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	@Column(name = "VALOR_FRETE", precision = 17, scale = 17)
	public Double getValorFrete() {
		return this.valorFrete;
	}

	public void setValorFrete(Double valorFrete) {
		this.valorFrete = valorFrete;
	}

	@Column(name = "VALOR_SEGURO", precision = 17, scale = 17)
	public Double getValorSeguro() {
		return this.valorSeguro;
	}

	public void setValorSeguro(Double valorSeguro) {
		this.valorSeguro = valorSeguro;
	}

	@Column(name = "VALOR_DESCONTO", precision = 17, scale = 17)
	public Double getValorDesconto() {
		return this.valorDesconto;
	}

	public void setValorDesconto(Double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	@Column(name = "ALIQUOTA_IPI", precision = 8, scale = 8)
	public Float getAliquotaIpi() {
		return this.aliquotaIpi;
	}

	public void setAliquotaIpi(Float aliquotaIpi) {
		this.aliquotaIpi = aliquotaIpi;
	}

	@Column(name = "VALOR_IPI", precision = 17, scale = 17)
	public Double getValorIpi() {
		return this.valorIpi;
	}

	public void setValorIpi(Double valorIpi) {
		this.valorIpi = valorIpi;
	}

	@Column(name = "ALIQUOTA_ICMS", precision = 8, scale = 8)
	public Float getAliquotaIcms() {
		return this.aliquotaIcms;
	}

	public void setAliquotaIcms(Float aliquotaIcms) {
		this.aliquotaIcms = aliquotaIcms;
	}

	@Column(name = "VALOR_ICMS", precision = 17, scale = 17)
	public Double getValorIcms() {
		return this.valorIcms;
	}

	public void setValorIcms(Double valorIcms) {
		this.valorIcms = valorIcms;
	}

	@Column(name = "NCM_CODIGO", length = 10)
	@Length(max = 10)
	public String getNcmCodigo() {
		return this.ncmCodigo;
	}

	public void setNcmCodigo(String ncmCodigo) {
		this.ncmCodigo = ncmCodigo;
	}

	@Column(name = "VALOR_DESCONTO_LIVRE", precision = 17, scale = 17)
	public Double getValorDescontoLivre() {
		return this.valorDescontoLivre;
	}

	public void setValorDescontoLivre(Double valorDescontoLivre) {
		this.valorDescontoLivre = valorDescontoLivre;
	}

	public enum Fields {

		DFE_SEQ("id.dfeSeq"),
		NRO_ITEM("id.nroItem"),
		VALOR_UNITARIO("valorUnitario");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof SceItemDocumentoFiscalEntrada)) {
			return false;
		}
		SceItemDocumentoFiscalEntrada other = (SceItemDocumentoFiscalEntrada) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}