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
@Table(name = "MCI_PARAM_PROCED_CIRURGICOS", schema = "AGH")
public class MciParamProcedCirurgico extends BaseEntityId<MciParamProcedCirurgicoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2867559633542680211L;
	private MciParamProcedCirurgicoId id;
	private Integer version;
	private RapServidores rapServidores;
	private MciParamReportUsuario mciParamReportUsuario;
	private MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos;
	private Date criadoEm;

	public MciParamProcedCirurgico() {
	}

	public MciParamProcedCirurgico(MciParamProcedCirurgicoId id, RapServidores rapServidores,
			MciParamReportUsuario mciParamReportUsuario, MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos, Date criadoEm) {
		this.id = id;
		this.rapServidores = rapServidores;
		this.mciParamReportUsuario = mciParamReportUsuario;
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "pruSeq", column = @Column(name = "PRU_SEQ", nullable = false)),
			@AttributeOverride(name = "pciSeq", column = @Column(name = "PCI_SEQ", nullable = false)) })
	public MciParamProcedCirurgicoId getId() {
		return this.id;
	}

	public void setId(MciParamProcedCirurgicoId id) {
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PCI_SEQ", nullable = false, insertable = false, updatable = false)
	public MbcProcedimentoCirurgicos getMbcProcedimentoCirurgicos() {
		return this.mbcProcedimentoCirurgicos;
	}

	public void setMbcProcedimentoCirurgicos(MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos) {
		this.mbcProcedimentoCirurgicos = mbcProcedimentoCirurgicos;
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
		RAP_SERVIDORES("rapServidores"),
		MCI_PARAM_REPORT_USUARIOS("mciParamReportUsuario"),
		MBC_PROCEDIMENTO_CIRURGICOS("mbcProcedimentoCirurgicos"),
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
		if (!(obj instanceof MciParamProcedCirurgico)) {
			return false;
		}
		MciParamProcedCirurgico other = (MciParamProcedCirurgico) obj;
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
