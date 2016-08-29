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
import org.hibernate.validator.constraints.Length;

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
@Table(name = "ANU_HORARIO_HIDRATANTES", schema = "AGH")
public class AnuHorarioHidratante extends BaseEntityId<AnuHorarioHidratanteId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3846984596479800264L;
	private AnuHorarioHidratanteId id;
	private Integer version;
	private RapServidores rapServidoresByAnuHhiSerFk1;
	private RapServidores rapServidoresByAnuHhiSerFk3;
	private RapServidores rapServidoresByAnuHhiSerFk2;
	private AnuHidratanteAplicado anuHidratanteAplicado;
	private Date criadoEm;
	private Date horario;
	private Date dthrInicio;
	private String indSuspenso;
	private Date dthrFim;
	private Date alteradoEm;

	public AnuHorarioHidratante() {
	}

	public AnuHorarioHidratante(AnuHorarioHidratanteId id, RapServidores rapServidoresByAnuHhiSerFk1,
			AnuHidratanteAplicado anuHidratanteAplicado, Date criadoEm, Date horario, Date dthrInicio) {
		this.id = id;
		this.rapServidoresByAnuHhiSerFk1 = rapServidoresByAnuHhiSerFk1;
		this.anuHidratanteAplicado = anuHidratanteAplicado;
		this.criadoEm = criadoEm;
		this.horario = horario;
		this.dthrInicio = dthrInicio;
	}

	public AnuHorarioHidratante(AnuHorarioHidratanteId id, RapServidores rapServidoresByAnuHhiSerFk1,
			RapServidores rapServidoresByAnuHhiSerFk3, RapServidores rapServidoresByAnuHhiSerFk2,
			AnuHidratanteAplicado anuHidratanteAplicado, Date criadoEm, Date horario, Date dthrInicio, String indSuspenso,
			Date dthrFim, Date alteradoEm) {
		this.id = id;
		this.rapServidoresByAnuHhiSerFk1 = rapServidoresByAnuHhiSerFk1;
		this.rapServidoresByAnuHhiSerFk3 = rapServidoresByAnuHhiSerFk3;
		this.rapServidoresByAnuHhiSerFk2 = rapServidoresByAnuHhiSerFk2;
		this.anuHidratanteAplicado = anuHidratanteAplicado;
		this.criadoEm = criadoEm;
		this.horario = horario;
		this.dthrInicio = dthrInicio;
		this.indSuspenso = indSuspenso;
		this.dthrFim = dthrFim;
		this.alteradoEm = alteradoEm;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "hipHidSeq", column = @Column(name = "HIP_HID_SEQ", nullable = false)),
			@AttributeOverride(name = "hipDnuAtdSeq", column = @Column(name = "HIP_DNU_ATD_SEQ", nullable = false)),
			@AttributeOverride(name = "hipDnuSeq", column = @Column(name = "HIP_DNU_SEQ", nullable = false)),
			@AttributeOverride(name = "hipSeq", column = @Column(name = "HIP_SEQ", nullable = false)),
			@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false)) })
	public AnuHorarioHidratanteId getId() {
		return this.id;
	}

	public void setId(AnuHorarioHidratanteId id) {
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
	public RapServidores getRapServidoresByAnuHhiSerFk1() {
		return this.rapServidoresByAnuHhiSerFk1;
	}

	public void setRapServidoresByAnuHhiSerFk1(RapServidores rapServidoresByAnuHhiSerFk1) {
		this.rapServidoresByAnuHhiSerFk1 = rapServidoresByAnuHhiSerFk1;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SUSPENSO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_SUSPENSO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuHhiSerFk3() {
		return this.rapServidoresByAnuHhiSerFk3;
	}

	public void setRapServidoresByAnuHhiSerFk3(RapServidores rapServidoresByAnuHhiSerFk3) {
		this.rapServidoresByAnuHhiSerFk3 = rapServidoresByAnuHhiSerFk3;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MOVIMENTADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MOVIMENTADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresByAnuHhiSerFk2() {
		return this.rapServidoresByAnuHhiSerFk2;
	}

	public void setRapServidoresByAnuHhiSerFk2(RapServidores rapServidoresByAnuHhiSerFk2) {
		this.rapServidoresByAnuHhiSerFk2 = rapServidoresByAnuHhiSerFk2;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "HIP_HID_SEQ", referencedColumnName = "HID_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "HIP_DNU_ATD_SEQ", referencedColumnName = "DNU_ATD_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "HIP_DNU_SEQ", referencedColumnName = "DNU_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "HIP_SEQ", referencedColumnName = "SEQ", nullable = false, insertable = false, updatable = false) })
	public AnuHidratanteAplicado getAnuHidratanteAplicado() {
		return this.anuHidratanteAplicado;
	}

	public void setAnuHidratanteAplicado(AnuHidratanteAplicado anuHidratanteAplicado) {
		this.anuHidratanteAplicado = anuHidratanteAplicado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HORARIO", nullable = false, length = 29)
	public Date getHorario() {
		return this.horario;
	}

	public void setHorario(Date horario) {
		this.horario = horario;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_INICIO", nullable = false, length = 29)
	public Date getDthrInicio() {
		return this.dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	@Column(name = "IND_SUSPENSO", length = 1)
	@Length(max = 1)
	public String getIndSuspenso() {
		return this.indSuspenso;
	}

	public void setIndSuspenso(String indSuspenso) {
		this.indSuspenso = indSuspenso;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 29)
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		RAP_SERVIDORES_BY_ANU_HHI_SER_FK1("rapServidoresByAnuHhiSerFk1"),
		RAP_SERVIDORES_BY_ANU_HHI_SER_FK3("rapServidoresByAnuHhiSerFk3"),
		RAP_SERVIDORES_BY_ANU_HHI_SER_FK2("rapServidoresByAnuHhiSerFk2"),
		ANU_HIDRATANTE_APLICADOS("anuHidratanteAplicado"),
		CRIADO_EM("criadoEm"),
		HORARIO("horario"),
		DTHR_INICIO("dthrInicio"),
		IND_SUSPENSO("indSuspenso"),
		DTHR_FIM("dthrFim"),
		ALTERADO_EM("alteradoEm");

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
		if (!(obj instanceof AnuHorarioHidratante)) {
			return false;
		}
		AnuHorarioHidratante other = (AnuHorarioHidratante) obj;
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
