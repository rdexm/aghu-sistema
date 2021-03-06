package br.gov.mec.aghu.model;

// Generated 15/03/2012 10:47:46 by Hibernate Tools 3.4.0.CR1

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


import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AelHorarioGradeExames generated by hbm2java
 */
@Entity
@Table(name = "AEL_HORARIO_GRADE_EXAMES", schema = "AGH")
public class AelHorarioGradeExame extends BaseEntityId<AelHorarioGradeExameId> implements java.io.Serializable {

	private static final long serialVersionUID = -2128151307818178386L;
	private AelHorarioGradeExameId id;
	private AelTipoMarcacaoExame tipoMarcacaoExame;
	private Date tempoEntreHorarios;
	private DominioSituacao situacao;
	private Date criadoEm;
	private RapServidores servidor;
	private Short numHorario;
	private Date horaFim;
	private Date alteradoEm;
	private RapServidores servidorAlterado;
	private Boolean exclusivoExecutor;
	private Boolean convenio;
	private AelGradeAgendaExame gradeAgendaExame;
	
	public AelHorarioGradeExame(){
	}
	
	public AelHorarioGradeExame(AelHorarioGradeExameId id){
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "gaeUnfSeq", column = @Column(name = "GAE_UNF_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "gaeSeqp", column = @Column(name = "GAE_SEQP", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "diaSemana", column = @Column(name = "DIA_SEMANA", nullable = false, length = 3)),
			@AttributeOverride(name = "horaInicio", column = @Column(name = "HORA_INICIO", nullable = false, length = 7)) })
	public AelHorarioGradeExameId getId() {
		return this.id;
	}

	public void setId(AelHorarioGradeExameId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TME_SEQ", nullable = false)
	public AelTipoMarcacaoExame getTipoMarcacaoExame() {
		return this.tipoMarcacaoExame;
	}

	public void setTipoMarcacaoExame(AelTipoMarcacaoExame aelTiposMarcacaoExames) {
		this.tipoMarcacaoExame = aelTiposMarcacaoExames;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO_ENTRE_HORARIOS", nullable = false, length = 7)
	public Date getTempoEntreHorarios() {
		return this.tempoEntreHorarios;
	}

	public void setTempoEntreHorarios(Date tempoEntreHorarios) {
		this.tempoEntreHorarios = tempoEntreHorarios;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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
		@JoinColumn(name = "GAE_UNF_SEQ", referencedColumnName = "UNF_SEQ", nullable = false, insertable = false, updatable = false),
		@JoinColumn(name = "GAE_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public AelGradeAgendaExame getGradeAgendaExame() {
		return this.gradeAgendaExame;
	}

	public void setGradeAgendaExame(AelGradeAgendaExame gradeAgendaExame) {
		this.gradeAgendaExame = gradeAgendaExame;
	}

	@Column(name = "NUM_HORARIO", precision = 4, scale = 0)
	public Short getNumHorario() {
		return this.numHorario;
	}

	public void setNumHorario(Short numHorario) {
		this.numHorario = numHorario;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "HORA_FIM", length = 7)
	public Date getHoraFim() {
		return this.horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 7)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlterado() {
		return this.servidorAlterado;
	}

	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	@Column(name = "IND_EXCLUSIVO_EXECUTOR", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExclusivoExecutor() {
		return this.exclusivoExecutor;
	}

	public void setExclusivoExecutor(Boolean exclusivoExecutor) {
		this.exclusivoExecutor = exclusivoExecutor;
	}

	@Column(name = "IND_CONVENIO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getConvenio() {
		return this.convenio;
	}

	public void setConvenio(Boolean convenio) {
		this.convenio = convenio;
	}
	
	public enum Fields {
		ID("id"),
		SITUACAO("situacao"),
		GAE_UNF_SEQ("id.gaeUnfSeq"),
		GAE_SEQP("id.gaeSeqp"),
		DIA_SEMANA("id.diaSemana"),
		CRIADO_EM("criadoEm"),
		TIPO_MARCACAO_EXAME("tipoMarcacaoExame");

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
		if (!(obj instanceof AelHorarioGradeExame)) {
			return false;
		}
		AelHorarioGradeExame other = (AelHorarioGradeExame) obj;
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
