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


import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.dominio.DominioUnidadeMedidaIdade;
import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "ECP_LIMITE_ITEM_CONTROLES_JN", schema = "AGH")
@SequenceGenerator(name = "ecpLicJnSq1", sequenceName = "AGH.ECP_LIC_jn_seq", allocationSize = 1)

@Immutable
public class EcpLimiteItemControleJn extends BaseJournal implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6861710425158270136L;
	private Integer seq;
	private Byte idadeMinima;
	private Short idadeMaxima;
	private DominioUnidadeMedidaIdade medidaIdade;
	private BigDecimal limiteInferiorErro;
	private BigDecimal limiteInferiorNormal;
	private BigDecimal limiteSuperiorNormal;
	private BigDecimal limiteSuperiorErro;
	private Date criadoEm;
	private EcpItemControle itemControle;
	private RapServidores servidor;
		
	
	public EcpLimiteItemControleJn() {
	}
	
	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "ecpLicJnSq1")
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", length = 8, nullable = false)
	public Integer getSeq() {
		return seq;
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
	public BigDecimal getLimiteInferiorErro() {
		return this.limiteInferiorErro;
	}

	public void setLimiteInferiorErro(BigDecimal limiteInferiorErro) {
		this.limiteInferiorErro = limiteInferiorErro;
	}

	@Column(name = "LIMITE_INF_NORMAL", precision = 8, scale = 2)
	public BigDecimal getLimiteInferiorNormal() {
		return this.limiteInferiorNormal;
	}

	public void setLimiteInferiorNormal(BigDecimal limiteInferiorNormal) {
		this.limiteInferiorNormal = limiteInferiorNormal;
	}

	@Column(name = "LIMITE_SUP_NORMAL", precision = 8, scale = 2)
	public BigDecimal getLimiteSuperiorNormal() {
		return this.limiteSuperiorNormal;
	}

	public void setLimiteSuperiorNormal(BigDecimal limiteSuperiorNormal) {
		this.limiteSuperiorNormal = limiteSuperiorNormal;
	}

	@Column(name = "LIMITE_SUP_ERRO", precision = 8, scale = 2, nullable = false)
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
		return new ToStringBuilder(this).toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getSeqJn() == null) ? 0 : getSeqJn().hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		EcpLimiteItemControleJn other = (EcpLimiteItemControleJn) obj;
		if (getSeqJn() == null) {
			if (other.getSeqJn() != null) {
				return false;
			}
		} else if (!getSeqJn().equals(other.getSeqJn())) {
			return false;
		}
		return true;
	}

	public enum Fields {
		JN_USER("nomeUsuario"), JN_DATE_TIME("dataAlteracao"), JN_OPERATION(
				"operacao"), SEQ("seq"), IDADE_MINIMA("idadeMinima"), IDADE_MAXIMA(
				"idadeMaxima"), MEDIDA_IDADE("medidaIdade"), LIMITE_INFERIOR_ERRO(
				"limiteInferiorErro"), LIMITE_INFERIOR_NORMAL(
				"limiteInferiorNormal"), LIMITE_SUPERIOR_NORMAL(
				"limiteSuperiorNormal"), LIMITE_SUPERIOR_ERRO(
				"limiteSuperiorErro"), CRIADO_EM("criadoEm"), ITEM_CONTROLE(
				"itemControle"), SERVIDOR("servidor");

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