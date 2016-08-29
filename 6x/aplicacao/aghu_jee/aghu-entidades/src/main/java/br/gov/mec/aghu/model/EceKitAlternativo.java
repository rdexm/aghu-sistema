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
 * EceKitAlternativo generated by hbm2java
 */
@Entity
@SequenceGenerator(name="eceKalSq1", sequenceName="AGH.ECE_KAL_SQ1", allocationSize = 1)
@Table(name = "ECE_KIT_ALTERNATIVOS", schema = "AGH")
public class EceKitAlternativo extends BaseEntitySeq<Integer> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7698500607422371548L;
	private Integer seq;
	private Integer version;
	private EceKit eceKitsByKtsSeqAlternativo;
	private RapServidores rapServidores;
	private EceKit eceKitsByKtsSeq;
	private String situacao;
	private Date criadoEm;

	public EceKitAlternativo() {
	}

	public EceKitAlternativo(Integer seq, EceKit eceKitsByKtsSeqAlternativo, RapServidores rapServidores, EceKit eceKitsByKtsSeq,
			Date criadoEm) {
		this.seq = seq;
		this.eceKitsByKtsSeqAlternativo = eceKitsByKtsSeqAlternativo;
		this.rapServidores = rapServidores;
		this.eceKitsByKtsSeq = eceKitsByKtsSeq;
		this.criadoEm = criadoEm;
	}

	public EceKitAlternativo(Integer seq, EceKit eceKitsByKtsSeqAlternativo, RapServidores rapServidores, EceKit eceKitsByKtsSeq,
			String situacao, Date criadoEm) {
		this.seq = seq;
		this.eceKitsByKtsSeqAlternativo = eceKitsByKtsSeqAlternativo;
		this.rapServidores = rapServidores;
		this.eceKitsByKtsSeq = eceKitsByKtsSeq;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "eceKalSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
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
	@JoinColumn(name = "KTS_SEQ_ALTERNATIVO", nullable = false)
	public EceKit getEceKitsByKtsSeqAlternativo() {
		return this.eceKitsByKtsSeqAlternativo;
	}

	public void setEceKitsByKtsSeqAlternativo(EceKit eceKitsByKtsSeqAlternativo) {
		this.eceKitsByKtsSeqAlternativo = eceKitsByKtsSeqAlternativo;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KTS_SEQ", nullable = false)
	public EceKit getEceKitsByKtsSeq() {
		return this.eceKitsByKtsSeq;
	}

	public void setEceKitsByKtsSeq(EceKit eceKitsByKtsSeq) {
		this.eceKitsByKtsSeq = eceKitsByKtsSeq;
	}

	@Column(name = "SITUACAO", length = 1)
	@Length(max = 1)
	public String getSituacao() {
		return this.situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {

		SEQ("seq"),
		VERSION("version"),
		ECE_KITS_BY_KTS_SEQ_ALTERNATIVO("eceKitsByKtsSeqAlternativo"),
		RAP_SERVIDORES("rapServidores"),
		ECE_KITS_BY_KTS_SEQ("eceKitsByKtsSeq"),
		SITUACAO("situacao"),
		CRIADO_EM("criadoEm");

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
		if (!(obj instanceof EceKitAlternativo)) {
			return false;
		}
		EceKitAlternativo other = (EceKitAlternativo) obj;
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