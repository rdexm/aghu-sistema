package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * AelMaterialAp generated by hbm2java
 */
@Entity
@SequenceGenerator(name="aelLurSq1", sequenceName="AGH.AEL_LUR_SQ1", allocationSize = 1)
@Table(name = "AEL_MATERIAL_APS", schema = "AGH")
public class AelMaterialAp extends BaseEntitySeq<Long> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8454669679084217334L;
	private Long seq;
	private Integer version;
	private AelExameAp aelExameAp;
	private RapServidores rapServidores;
	private String material;
	private Date criadoEm;
	private Short ordem;
	private AelItemSolicitacaoExames itemSolicitacaoExame;

	public AelMaterialAp() {
	}

	public AelMaterialAp(Long seq, AelExameAp aelExameAp, RapServidores rapServidores, String material, Date criadoEm) {
		this.seq = seq;
		this.aelExameAp = aelExameAp;
		this.rapServidores = rapServidores;
		this.material = material;
		this.criadoEm = criadoEm;
	}

	public AelMaterialAp(Long seq, AelExameAp aelExameAp, RapServidores rapServidores, String material, Date criadoEm, Short ordem) {
		this.seq = seq;
		this.aelExameAp = aelExameAp;
		this.rapServidores = rapServidores;
		this.material = material;
		this.criadoEm = criadoEm;
		this.ordem = ordem;
		
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aelLurSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LUX_SEQ", nullable = false)
	public AelExameAp getAelExameAp() {
		return this.aelExameAp;
	}

	public void setAelExameAp(AelExameAp aelExameAp) {
		this.aelExameAp = aelExameAp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "MATERIAL", nullable = false, length = 2000)
	@Length(max = 2000)
	public String getMaterial() {
		return this.material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ORDEM")
	public Short getOrdem() {
		return this.ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "ISE_SOE_SEQ", referencedColumnName = "SOE_SEQ"),
			@JoinColumn(name = "ISE_SEQP", referencedColumnName = "SEQP") })
	public AelItemSolicitacaoExames getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}

	public void setItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		AEL_EXAME_APS("aelExameAp"),
		AEL_EXAME_APS_SEQ("aelExameAp.seq"),
		RAP_SERVIDORES("rapServidores"),
		MATERIAL("material"),
		CRIADO_EM("criadoEm"),
		ORDEM("ordem"),
		ITEM_SOLICITACAO_EXAME("itemSolicitacaoExame"),
		ISE_SOE_SEQ("itemSolicitacaoExame.id.soeSeq"),
		ISE_SEQP("itemSolicitacaoExame.id.seqp")
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof AelMaterialAp)) {
			return false;
		}
		AelMaterialAp other = (AelMaterialAp) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
