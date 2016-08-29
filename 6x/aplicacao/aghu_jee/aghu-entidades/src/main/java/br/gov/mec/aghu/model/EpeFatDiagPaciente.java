package br.gov.mec.aghu.model;

// Generated 17/10/2011 14:21:24 by Hibernate Tools 3.4.0.CR1

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
@Table(name = "EPE_FAT_DIAG_PACIENTES", schema = "AGH")
public class EpeFatDiagPaciente extends BaseEntityId<EpeFatDiagPacienteId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 467139790226628450L;
	private EpeFatDiagPacienteId id;
	private Integer version;
	private EpeFatRelDiagnostico fatRelDiagnostico;
	private Date dthrFim;
	private RapServidores servidor;
	private RapServidores servidorEncerrado;
	private AipPacientes paciente;
	

	public EpeFatDiagPaciente() {
	}

	public EpeFatDiagPaciente(EpeFatDiagPacienteId id,
			EpeFatRelDiagnostico fatRelDiagnostico, RapServidores servidor) {
		this.id = id;
		this.fatRelDiagnostico = fatRelDiagnostico;
		this.servidor = servidor;
	}

	public EpeFatDiagPaciente(EpeFatDiagPacienteId id,
			EpeFatRelDiagnostico fatRelDiagnostico, Date dthrFim,
			RapServidores servidor,
			RapServidores servidorEncerrado) {
		this.id = id;
		this.fatRelDiagnostico = fatRelDiagnostico;
		this.dthrFim = dthrFim;
		this.servidor = servidor;
		this.servidorEncerrado = servidorEncerrado;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "fdgDgnSnbGnbSeq", column = @Column(name = "FDG_DGN_SNB_GNB_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "fdgDgnSnbSequencia", column = @Column(name = "FDG_DGN_SNB_SEQUENCIA", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "fdgDgnSequencia", column = @Column(name = "FDG_DGN_SEQUENCIA", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "fdgFreSeq", column = @Column(name = "FDG_FRE_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "dthrInicio", column = @Column(name = "DTHR_INICIO", nullable = false, length = 7)) })
	public EpeFatDiagPacienteId getId() {
		return this.id;
	}

	public void setId(EpeFatDiagPacienteId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false, precision = 9, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "FDG_DGN_SNB_GNB_SEQ", referencedColumnName = "DGN_SNB_GNB_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "FDG_DGN_SNB_SEQUENCIA", referencedColumnName = "DGN_SNB_SEQUENCIA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "FDG_DGN_SEQUENCIA", referencedColumnName = "DGN_SEQUENCIA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "FDG_FRE_SEQ", referencedColumnName = "FRE_SEQ", nullable = false, insertable = false, updatable = false) })
	public EpeFatRelDiagnostico getFatRelDiagnostico() {
		return this.fatRelDiagnostico;
	}

	public void setFatRelDiagnostico(
			EpeFatRelDiagnostico fatRelDiagnostico) {
		this.fatRelDiagnostico = fatRelDiagnostico;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DTHR_FIM", length = 7)
	public Date getDthrFim() {
		return this.dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_E_ENCERRADO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_E_ENCERRADO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorEncerrado() {
		return this.servidorEncerrado;
	}

	public void setServidorEncerrado(RapServidores servidorEncerrado) {
		this.servidorEncerrado = servidorEncerrado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable=false, insertable = false, updatable=false)
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(
			AipPacientes paciente) {
		this.paciente = paciente;
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
		EpeFatDiagPaciente other = (EpeFatDiagPaciente) obj;
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
		FAT_REL_DIAGNOSTICO("fatRelDiagnostico"),
		DTHR_FIM("dthrFim"),
		SERVIDOR("servidor"),
		SERVIDOR_ENCERRADO("servidorEncerrado"),
		PACIENTE("paciente"),
		ID_FDG_DGN_SNB_GNB_SEQ("id.fdgDgnSnbGnbSeq"),
		ID_FDG_DGN_SNB_SEQUENCIA("id.fdgDgnSnbSequencia"),
		ID_FDG_DGN_SEQUENCIA("id.fdgDgnSequencia"),
		ID_FDG_FRE_SEQ("id.fdgFreSeq");

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
