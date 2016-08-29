package br.gov.mec.aghu.model;

// Generated 26/03/2010 14:34:15 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

/**
 * Usar a classe MamAreaAtuacaoNumeroPosto
 */
//@Entity
//@Table(name = "MAM_AREA_ATU_NRO_POSTOS", schema = "AGH")
@Deprecated
public class MamAreaAtuNroPostos implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2733486592424776780L;
	private MamAreaAtuNroPostosId id;
	private MpmPostoSaude mpmPostoSaudes;
	private MamAreaAtuacaoNumero mamAreaAtuNro;
	private Date criadoEm;
	private String indSituacao;
	private RapServidores rapServidor;

	public MamAreaAtuNroPostos() {
	}

	public MamAreaAtuNroPostos(final MamAreaAtuNroPostosId id, final MpmPostoSaude mpmPostoSaudes, final Date criadoEm,
			final String indSituacao, final RapServidores rapServidor) {
		this.id = id;
		this.mpmPostoSaudes = mpmPostoSaudes;
		this.criadoEm = criadoEm;
		this.indSituacao = indSituacao;
		this.rapServidor = rapServidor;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "arnAraSeq", column = @Column(name = "ARN_ARA_SEQ", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "arnSeqp", column = @Column(name = "ARN_SEQP", nullable = false, precision = 5, scale = 0)),
			@AttributeOverride(name = "pssSeq", column = @Column(name = "PSS_SEQ", nullable = false, precision = 4, scale = 0)) })
	public MamAreaAtuNroPostosId getId() {
		return this.id;
	}

	public void setId(final MamAreaAtuNroPostosId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PSS_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmPostoSaude getMpmPostoSaudes() {
		return this.mpmPostoSaudes;
	}

	public void setMpmPostoSaudes(final MpmPostoSaude mpmPostoSaudes) {
		this.mpmPostoSaudes = mpmPostoSaudes;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(final Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Length(max = 1)
	public String getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(final String indSituacao) {
		this.indSituacao = indSituacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( { @JoinColumn(name = "SER_MATRICULA"), @JoinColumn(name = "SER_VIN_CODIGO") })
	public RapServidores getRapServidor() {
		return this.rapServidor;
	}

	public void setRapServidor(final RapServidores rapServidor) {
		this.rapServidor = rapServidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( { @JoinColumn(name = "ARN_ARA_SEQ", insertable = false, updatable = false),
			@JoinColumn(name = "ARN_SEQP", insertable = false, updatable = false) })
	public MamAreaAtuacaoNumero getMamAreaAtuNro() {
		return this.mamAreaAtuNro;
	}

	public void setMamAreaAtuNro(final MamAreaAtuacaoNumero mamAreaAtuNro) {
		this.mamAreaAtuNro = mamAreaAtuNro;
	}

	public enum Fields {

		ID("id"),
		MPM_POSTO_SAUDES("mpmPostoSaudes"),
		MAM_AREA_ATU_NRO("mamAreaAtuNro"),
		CRIADO_EM("criadoEm"),
		IND_SITUACAO("indSituacao"),
		RAP_SERVIDOR("rapServidor");

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
		if (!(obj instanceof MamAreaAtuNroPostos)) {
			return false;
		}
		MamAreaAtuNroPostos other = (MamAreaAtuNroPostos) obj;
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
