package br.gov.mec.aghu.model;

// Generated 14/09/2010 17:49:55 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MpmFormaTbPacienteAlta generated by hbm2java
 */
@Entity

@Table(name = "MPM_FORMA_TB_PACIENTE_ALTAS", schema = "AGH")
public class MpmFormaTbPacienteAlta extends BaseEntityId<MpmFormaTbPacienteAltaId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4800574776272822656L;
	private MpmFormaTbPacienteAltaId id;
	private MpmFormaTb formaTuberculose;
	private MpmAltaHospControleTb altaHospControleTuberculose;
	private RapServidores servidor;
	private Date criadoEm;
	private String descricao;

	public MpmFormaTbPacienteAlta() {
	}

	public MpmFormaTbPacienteAlta(MpmFormaTbPacienteAltaId id,
			MpmFormaTb formaTuberculose,
			MpmAltaHospControleTb altaHospControleTuberculose,
			RapServidores servidor,	Date criadoEm) {
		this.id = id;
		this.formaTuberculose = formaTuberculose;
		this.altaHospControleTuberculose = altaHospControleTuberculose;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
	}

	public MpmFormaTbPacienteAlta(MpmFormaTbPacienteAltaId id,
			MpmFormaTb formaTuberculose,
			MpmAltaHospControleTb altaHospControleTuberculose, RapServidores servidor,
			Date criadoEm, String descricao) {
		this.id = id;
		this.formaTuberculose = formaTuberculose;
		this.altaHospControleTuberculose = altaHospControleTuberculose;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
		this.descricao = descricao;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "actAtdSeq", column = @Column(name = "ACT_ATD_SEQ", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "ftbSeq", column = @Column(name = "FTB_SEQ", nullable = false, precision = 3, scale = 0)) })
	public MpmFormaTbPacienteAltaId getId() {
		return this.id;
	}

	public void setId(MpmFormaTbPacienteAltaId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FTB_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmFormaTb getFormaTuberculose() {
		return this.formaTuberculose;
	}

	public void setFormaTuberculose(MpmFormaTb formaTuberculose) {
		this.formaTuberculose = formaTuberculose;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACT_ATD_SEQ", nullable = false, insertable = false, updatable = false)
	public MpmAltaHospControleTb getAltaHospControleTuberculose() {
		return this.altaHospControleTuberculose;
	}

	public void setAltaHospControleTuberculose(MpmAltaHospControleTb AltaHospControleTuberculose) {
		this.altaHospControleTuberculose = AltaHospControleTuberculose;
	}

	/**
	 * @return the servidor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "DESCRICAO", length = 240)
	@Length(max = 240, message="A descrição possui mais de 240 caracteres.")
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
		MpmFormaTbPacienteAlta other = (MpmFormaTbPacienteAlta) obj;
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
		ID("id"),
		FORMA_TUBERCULOSE("formaTuberculose"),
		ALTA_HOSP_CONTROLE_TUBERCULOSE("altaHospControleTuberculose"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		DESCRICAO("descricao");
		
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
