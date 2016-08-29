package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * The persistent class for the AEL_DESC_MAT_LACUNAS database table.
 * 
 */
@Entity
@Table(name = "AEL_DESC_MAT_LACUNAS", schema = "AGH")
public class AelDescMatLacunas extends BaseEntityId<AelDescMatLacunasId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4645643377166628594L;

	private AelDescMatLacunasId id;
	private AelGrpDescMatLacunas aelGrpDescMatLacunas;
	private String textoLacuna;
	private DominioSituacao indSituacao;
	private Date criadoEm;
	private Integer version;
	private RapServidores servidor;

	public AelDescMatLacunas() {
	}

	public AelDescMatLacunas(AelDescMatLacunasId id, AelGrpDescMatLacunas aelGrpDescMatLacunas, String textoLacuna, DominioSituacao indSituacao,
			Date criadoEm) {
		this.id = id;
		this.aelGrpDescMatLacunas = aelGrpDescMatLacunas;
		this.textoLacuna = textoLacuna;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "gtmSeq", column = @Column(name = "GTM_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "ldaSeq", column = @Column(name = "LDA_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "gmlSeq", column = @Column(name = "GML_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)) })
	@NotNull
	public AelDescMatLacunasId getId() {
		return this.id;
	}

	public void setId(AelDescMatLacunasId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "GTM_SEQ", referencedColumnName = "GTM_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "LDA_SEQ", referencedColumnName = "LDA_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "GML_SEQ", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	@NotNull
	public AelGrpDescMatLacunas getAelGrpDescMatLacunas() {
		return this.aelGrpDescMatLacunas;
	}

	public void setAelGrpDescMatLacunas(AelGrpDescMatLacunas aelGrpDescMatLacunas) {
		this.aelGrpDescMatLacunas = aelGrpDescMatLacunas;
	}

	@Column(name = "TEXTO_LACUNA", nullable = false, length = 500)
	@Length(max = 500)
	@NotNull
	public String getTextoLacuna() {
		return this.textoLacuna;
	}

	public void setTextoLacuna(String textoLacuna) {
		this.textoLacuna = textoLacuna;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	@NotNull
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		AelDescMatLacunas other = (AelDescMatLacunas) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	public enum Fields {
		GTM_SEQ("id.gtmSeq"), LDA_SEQ("id.ldaSeq"), GML_SEQ("id.gmlSeq"), SEQP("id.seqp"), TEXTO_LACUNA(
				"textoLacuna"), IND_SITUACAO("indSituacao"), CRIADO_EM("criadoEm"), ;

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
