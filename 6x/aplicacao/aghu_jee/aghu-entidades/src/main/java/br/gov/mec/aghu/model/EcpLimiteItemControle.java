package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Digits;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "ECP_LIMITE_ITEM_CONTROLES", schema = "AGH")
@SequenceGenerator(name = "ecpLicSq1", sequenceName = "AGH.ECP_LIC_SQ1", allocationSize = 1)
public class EcpLimiteItemControle extends BaseEntitySeq<Integer> implements Serializable, Cloneable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7918921666338479845L;
	private Integer seq;
	private Byte idadeMinima;
	private Short idadeMaxima;
	private DominioUnidadeMedidaIdade medidaIdade;
	private BigDecimal limiteInferiorErro;
	private BigDecimal limiteInferiorNormal;
	private BigDecimal limiteSuperiorNormal;
	private BigDecimal limiteSuperiorErro;
	private Date criadoEm;
	private Integer version;
	private EcpItemControle itemControle;
	private RapServidores servidor;

	// construtores
	public EcpLimiteItemControle() {
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 8, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ecpLicSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "IDADE_MINIMA", length = 2, nullable = false)
	public Byte getIdadeMinima() {
		return this.idadeMinima;
	}

	public void setIdadeMinima(Byte idadeMinima) {
		this.idadeMinima = idadeMinima;
	}

	@Column(name = "IDADE_MAXIMA", length = 3, nullable = false)
	public Short getIdadeMaxima() {
		return this.idadeMaxima;
	}

	public void setIdadeMaxima(Short idadeMaxima) {
		this.idadeMaxima = idadeMaxima;
	}

	@Column(name = "MEDIDA_IDADE", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioUnidadeMedidaIdade getMedidaIdade() {
		return this.medidaIdade;
	}

	public void setMedidaIdade(DominioUnidadeMedidaIdade medidaIdade) {
		this.medidaIdade = medidaIdade;
	}

	@Column(name = "LIMITE_INF_ERRO", precision = 8, scale = 2, nullable = false)
	@Digits(integer=6, fraction=2, message="Limite Inferior Erro dever ter no máximo 6 números inteiros e 2 decimais")
	public BigDecimal getLimiteInferiorErro() {
		return this.limiteInferiorErro;
	}

	public void setLimiteInferiorErro(BigDecimal limiteInferiorErro) {
		this.limiteInferiorErro = limiteInferiorErro;
	}

	@Column(name = "LIMITE_INF_NORMAL", precision = 8, scale = 2)
	@Digits(integer=6, fraction=2, message="Limite Inferior Normal dever ter no máximo 6 números inteiros e 2 decimais")
	public BigDecimal getLimiteInferiorNormal() {
		return this.limiteInferiorNormal;
	}

	public void setLimiteInferiorNormal(BigDecimal limiteInferiorNormal) {
		this.limiteInferiorNormal = limiteInferiorNormal;
	}

	@Column(name = "LIMITE_SUP_NORMAL", precision = 8, scale = 2)
	@Digits(integer=6, fraction=2, message="Limite Superior Normal dever ter no máximo 6 números inteiros e 2 decimais")
	public BigDecimal getLimiteSuperiorNormal() {
		return this.limiteSuperiorNormal;
	}

	public void setLimiteSuperiorNormal(BigDecimal limiteSuperiorNormal) {
		this.limiteSuperiorNormal = limiteSuperiorNormal;
	}

	@Column(name = "LIMITE_SUP_ERRO", precision = 8, scale = 2, nullable = false)
	@Digits(integer=6, fraction=2, message="Limite Superior Erro dever ter no máximo 6 números inteiros e 2 decimais")
	public BigDecimal getLimiteSuperiorErro() {
		return this.limiteSuperiorErro;
	}

	public void setLimiteSuperiorErro(BigDecimal limiteSuperiorErro) {
		this.limiteSuperiorErro = limiteSuperiorErro;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "VERSION", length = 9, nullable = false)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ICE_SEQ", referencedColumnName = "SEQ")
	public EcpItemControle getItemControle() {
		return itemControle;
	}

	public void setItemControle(EcpItemControle itemControle) {
		this.itemControle = itemControle;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	// outros
	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof EcpLimiteItemControle)) {
			return false;
		}
		EcpLimiteItemControle castOther = (EcpLimiteItemControle) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), IDADE_MINIMA("idadeMinima"), IDADE_MAXIMA("idadeMaxima"), MEDIDA_IDADE(
				"medidaIdade"), LIMITE_INFERIOR_ERRO("limiteInferiorErro"), LIMITE_INFERIOR_NORMAL(
				"limiteInferiorNormal"), LIMITE_SUPERIOR_NORMAL("limiteSuperiorNormal"), LIMITE_SUPERIOR_ERRO(
				"limiteSuperiorErro"), CRIADO_EM("criadoEm"), ITEM_CONTROLE(
				"itemControle"), SERVIDOR("servidor"), ;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException{
		return super.clone();
	}
	
	@Transient
	public String getLimiteInferiorErroFormatado() {
		if (getLimiteInferiorErro() == null) {
			return null;
		}
		String limErro = String.valueOf(getLimiteInferiorErro());
		return limErro.replace('.', ',');
	}
	
	@Transient
	public String getLimiteInferiorNormalFormatado() {
		if (getLimiteInferiorNormal() == null) {
			return null;
		}
		String limErro = String.valueOf(getLimiteInferiorNormal());
		return limErro.replace('.', ',');
	}
	
	@Transient
	public String getLimiteSuperiorErroFormatado() {
		if (getLimiteSuperiorErro() == null) {
			return null;
		}
		String limErro = String.valueOf(getLimiteSuperiorErro());
		return limErro.replace('.', ',');
	}
	
	@Transient
	public String getLimiteSuperiorNormalFormatado() {
		if (getLimiteSuperiorNormal() == null) {
			return null;
		}
		String limErro = String.valueOf(getLimiteSuperiorNormal());
		return limErro.replace('.', ',');
	}	
	
}