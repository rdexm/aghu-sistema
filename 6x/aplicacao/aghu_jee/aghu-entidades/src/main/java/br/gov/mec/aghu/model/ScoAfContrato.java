package br.gov.mec.aghu.model;

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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntity;

/**
 * The persistent class for the sco_af_contratos database table.
 * 
 */
@Entity
@Table(name = "SCO_AF_CONTRATOS", schema = "AGH")
public class ScoAfContrato implements BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5010882963584382464L;

	@Id
	@SequenceGenerator(name = "SCO_AF_CONTRATOS_SEQ_GENERATOR", sequenceName = "AGH.SCO_AFCO_SQ1", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SCO_AF_CONTRATOS_SEQ_GENERATOR")
	private Integer seq;

	// bi-directional many-to-one association to ScoAutorizacoesForn
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "AFN_NUMERO")
	private ScoAutorizacaoForn scoAutorizacoesForn;

	// bi-directional many-to-one association to ScoContrato
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "CONT_SEQ")
	private ScoContrato scoContrato;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	private RapServidores servidor;

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date criadoEm;

	@Column(name = "ALTERADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	private Date alteradoEm;

	@Column(name = "VERSION", length= 7)
	@Version
	private Integer version;	
	
	public ScoAfContrato() {
	}

	public Integer getSeq() {
		return this.seq;
	}

	public ScoContrato getScoContrato() {
		return scoContrato;
	}

	public void setScoContrato(ScoContrato scoContrato) {
		this.scoContrato = scoContrato;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public ScoAutorizacaoForn getScoAutorizacoesForn() {
		return this.scoAutorizacoesForn;
	}

	public void setScoAutorizacoesForn(ScoAutorizacaoForn scoAutorizacoesForn) {
		this.scoAutorizacoesForn = scoAutorizacoesForn;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoAfContrato)){
			return false;
		}
		ScoAfContrato castOther = (ScoAfContrato) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), 
		AUT_FORN("scoAutorizacoesForn"),
		NUMERO_AUT_FORN("scoAutorizacoesForn.numero"), 
		NR_CONTRATO("scoContrato.seq"), 
		SERVIDOR("servidor"), 
		CRIADO_EM("criadoEm"), 
		ALTERADO_EM("alteradoEm"),
		CONTRATO("scoContrato"),
		VERSION("version");

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