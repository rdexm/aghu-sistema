package br.gov.mec.aghu.model;

import javax.persistence.*;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * MpmAltaPrincReceitas generated by hbm2java
 */
@Entity
@Table(name = "MPM_ALTA_PRINC_RECEITAS", schema = "AGH")
public class MpmAltaPrincReceitas extends BaseEntityId<MpmAltaPrincReceitasId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6684907418766893254L;
	
	private MpmAltaPrincReceitasId id;
	private String descReceita;
	private String indSituacao;
	private String indCarga;
	private MpmAltaSumario mpmAltaSumario;
	private Integer version;

	public MpmAltaPrincReceitas() {
	}

	public MpmAltaPrincReceitas(MpmAltaPrincReceitasId id) {
		this.id = id;
	}

	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "asuApaAtdSeq", column = @Column(name = "ASU_APA_ATD_SEQ", nullable = false, precision = 9, scale = 0)),
			@AttributeOverride(name = "asuApaSeq", column = @Column(name = "ASU_APA_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "asuSeqp", column = @Column(name = "ASU_SEQP", nullable = false, precision = 3, scale = 0)), 
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 5, scale = 0)) })
	
	public MpmAltaPrincReceitasId getId() {
		return id;
		
	}


	public void setId(MpmAltaPrincReceitasId id) {
		this.id = id;
	}

	@Column(name = "DESC_RECEITA", nullable = false, length = 160)
	@Length(max = 160)
	public String getDescReceita() {
		return descReceita;
	}

	public void setDescReceita(String descReceita) {
		this.descReceita = descReceita;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return indSituacao;
	}


	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_CARGA", length = 1)
	@Length(max = 1)
	public String getIndCarga() {
		return indCarga;
	}


	public void setIndCarga(String indCarga) {
		this.indCarga = indCarga;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "ASU_APA_ATD_SEQ", referencedColumnName = "APA_ATD_SEQ",updatable=false,insertable=false),
			@JoinColumn(name = "ASU_APA_SEQ", referencedColumnName = "APA_SEQ",updatable=false,insertable=false),
			@JoinColumn(name = "ASU_SEQP", referencedColumnName = "SEQP",updatable=false,insertable=false) })
	public MpmAltaSumario getMpmAltaSumario() {
		return mpmAltaSumario;
	}


	public void setMpmAltaSumario(MpmAltaSumario mpmAltaSumario) {
		this.mpmAltaSumario = mpmAltaSumario;
	}
			
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	
	
	public enum Fields {
			
		ASU_APA_ATD_SEQ("id.asuApaAtdSeq"),
		ASU_APA_SEQ("id.asuApaSeq"), 
		ASU_SEQP("id.asuSeqp"),
		SEQP("id.seqp"),
		ASU_SEQP3("mpmAltaSumario.id.seq"),
		DESC_RECEITA("descReceita"),
		IND_CARGA("indCarga"),
		MPM_ALTA_SUMARIO("mpmAltaSumario"),
		IND_SITUACAO("indSituacao");
		
		
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
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getId());
		umHashCodeBuilder.append(this.getDescReceita());
		umHashCodeBuilder.append(this.getIndSituacao());
		umHashCodeBuilder.append(this.getIndCarga());
		umHashCodeBuilder.append(this.getVersion());
		
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MpmAltaPrincReceitas)) {
			return false;
		}
		MpmAltaPrincReceitas other = (MpmAltaPrincReceitas) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getId(), other.getId());
		umEqualsBuilder.append(this.getDescReceita(), other.getDescReceita());
		umEqualsBuilder.append(this.getIndSituacao(), other.getIndSituacao());
		umEqualsBuilder.append(this.getIndCarga(), other.getIndCarga());
		umEqualsBuilder.append(this.getVersion(), other.getVersion());
		return umEqualsBuilder.isEquals();
	}
		
}
