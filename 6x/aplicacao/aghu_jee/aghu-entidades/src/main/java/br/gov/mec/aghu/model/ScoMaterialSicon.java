package br.gov.mec.aghu.model;

import java.io.Serializable;
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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "SCO_MATERIAIS_SICON", schema = "AGH")
@SequenceGenerator(name = "scoMatsSq1", sequenceName = "AGH.SCO_MATS_SQ1", allocationSize = 1)
public class ScoMaterialSicon extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 267513027336596858L;
	private Integer seq;
	private Integer codigoSicon;
	private Date criadoEm;
	private Date alteradoEm;
	private DominioSituacao situacao;
	private Integer version;
	private ScoMaterial material;
	private RapServidores servidor;

	// construtores

	public ScoMaterialSicon() {
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoMatsSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "CODIGO_SICON", length = 7, nullable = false)
	public Integer getCodigoSicon() {
		return this.codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ALTERADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@Column(name = "IND_SITUACAO", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	@Version
	@Column(name = "VERSION", length = 3, nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "CODIGO")
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
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
		if (!(other instanceof ScoMaterialSicon)){
			return false;
		}
		ScoMaterialSicon castOther = (ScoMaterialSicon) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"), 
		CODIGO_SICON("codigoSicon"), 
		CRIADO_EM("criadoEm"), 
		ALTERADO_EM("alteradoEm"), 
		SITUACAO("situacao"), 
		VERSION("version"), 
		MATERIAL("material"), 
		SERVIDOR("servidor"), ;

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