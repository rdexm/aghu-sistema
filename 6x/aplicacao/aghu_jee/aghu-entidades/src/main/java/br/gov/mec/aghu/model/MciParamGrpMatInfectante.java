package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "MCI_PARAM_GRP_MAT_INFECTANTES", schema = "AGH")
public class MciParamGrpMatInfectante extends BaseEntityId<MciParamGrpMatInfectanteId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 158543083983971821L;
	private MciParamGrpMatInfectanteId id;
	private Integer version;
	private MciGrupoMatInfectante mciGrupoMatInfectante;
	private RapServidores rapServidores;
	private MciParamReportUsuario mciParamReportUsuario;
	private Date criadoEm;

	public MciParamGrpMatInfectante() {
	}

	public MciParamGrpMatInfectante(MciParamGrpMatInfectanteId id, MciGrupoMatInfectante mciGrupoMatInfectante,
			RapServidores rapServidores, MciParamReportUsuario mciParamReportUsuario, Date criadoEm) {
		this.id = id;
		this.mciGrupoMatInfectante = mciGrupoMatInfectante;
		this.rapServidores = rapServidores;
		this.mciParamReportUsuario = mciParamReportUsuario;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pruSeq", column = @Column(name = "PRU_SEQ", nullable = false)),
			@AttributeOverride(name = "gmiSeq", column = @Column(name = "GMI_SEQ", nullable = false)) })
	public MciParamGrpMatInfectanteId getId() {
		return this.id;
	}

	public void setId(MciParamGrpMatInfectanteId id) {
		this.id = id;
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
	@JoinColumn(name = "GMI_SEQ", nullable = false, insertable = false, updatable = false)
	public MciGrupoMatInfectante getMciGrupoMatInfectante() {
		return this.mciGrupoMatInfectante;
	}

	public void setMciGrupoMatInfectante(MciGrupoMatInfectante mciGrupoMatInfectante) {
		this.mciGrupoMatInfectante = mciGrupoMatInfectante;
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
	@JoinColumn(name = "PRU_SEQ", nullable = false, insertable = false, updatable = false)
	public MciParamReportUsuario getMciParamReportUsuario() {
		return this.mciParamReportUsuario;
	}

	public void setMciParamReportUsuario(MciParamReportUsuario mciParamReportUsuario) {
		this.mciParamReportUsuario = mciParamReportUsuario;
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

		ID("id"),
		VERSION("version"),
		MCI_GRUPO_MAT_INFECTANTES("mciGrupoMatInfectante"),
		RAP_SERVIDORES("rapServidores"),
		MCI_PARAM_REPORT_USUARIOS("mciParamReportUsuario"),
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
		if (!(obj instanceof MciParamGrpMatInfectante)) {
			return false;
		}
		MciParamGrpMatInfectante other = (MciParamGrpMatInfectante) obj;
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
