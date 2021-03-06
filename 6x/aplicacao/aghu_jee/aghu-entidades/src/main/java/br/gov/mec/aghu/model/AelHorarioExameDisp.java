package br.gov.mec.aghu.model;

// Generated 18/04/2011 19:20:02 by Hibernate Tools 3.4.0.CR1

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoHorario;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * AelHorarioExameDisps generated by hbm2java
 */

@Entity
@Table(name = "AEL_HORARIO_EXAME_DISPS", schema = "AGH")
public class AelHorarioExameDisp extends BaseEntityId<AelHorarioExameDispId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6659387679027417467L;
	private AelHorarioExameDispId id;
	private Integer version;
	private AelGradeAgendaExame gradeAgendaExame;
	private AelTipoMarcacaoExame tipoMarcacaoExame;
	private DominioSituacaoHorario situacaoHorario;
	private Boolean indHorarioExtra;
	private Date criadoEm;
	private RapServidores servidor;
	private Date alteradoEm;
	private RapServidores servidorAlterado;
	private Boolean exclusivoExecutor;
	private String indConvenio;
	
	private Set<AelItemHorarioAgendado> itemHorarioAgendados = new HashSet<AelItemHorarioAgendado>(0);


	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "gaeUnfSeq", column = @Column(name = "GAE_UNF_SEQ", nullable = false)),
			@AttributeOverride(name = "gaeSeqp", column = @Column(name = "GAE_SEQP", nullable = false)),
			@AttributeOverride(name = "dthrAgenda", column = @Column(name = "DTHR_AGENDA", nullable = false, length = 29)) })
	public AelHorarioExameDispId getId() {
		return this.id;
	}

	public void setId(AelHorarioExameDispId id) {
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
	@JoinColumns({
			@JoinColumn(name = "GAE_UNF_SEQ", referencedColumnName = "UNF_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "GAE_SEQP", referencedColumnName = "SEQP", nullable = false, insertable = false, updatable = false) })
	public AelGradeAgendaExame getGradeAgendaExame() {
		return this.gradeAgendaExame;
	}

	public void setGradeAgendaExame(AelGradeAgendaExame aelGradeAgendaExames) {
		this.gradeAgendaExame = aelGradeAgendaExames;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TME_SEQ", nullable = false)
	public AelTipoMarcacaoExame getTipoMarcacaoExame() {
		return this.tipoMarcacaoExame;
	}

	public void setTipoMarcacaoExame(AelTipoMarcacaoExame aelTiposMarcacaoExames) {
		this.tipoMarcacaoExame = aelTiposMarcacaoExames;
	}

	@Column(name = "SITUACAO_HORARIO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoHorario getSituacaoHorario() {
		return this.situacaoHorario;
	}

	public void setSituacaoHorario(DominioSituacaoHorario situacaoHorario) {
		this.situacaoHorario = situacaoHorario;
	}

	@Column(name = "IND_HORARIO_EXTRA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndHorarioExtra() {
		return this.indHorarioExtra;
	}

	public void setIndHorarioExtra(Boolean indHorarioExtra) {
		this.indHorarioExtra = indHorarioExtra;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
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
		return servidor;
	}
	
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ALTERADO_EM", length = 29)
	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorAlterado() {
		return servidorAlterado;
	}
	
	public void setServidorAlterado(RapServidores servidorAlterado) {
		this.servidorAlterado = servidorAlterado;
	}

	@Column(name = "IND_EXCLUSIVO_EXECUTOR", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExclusivoExecutor() {
		return this.exclusivoExecutor;
	}

	public void setExclusivoExecutor(Boolean exclusivoExecutor) {
		this.exclusivoExecutor = exclusivoExecutor;
	}

	@Column(name = "IND_CONVENIO", length = 1)
	@Length(max = 1)
	public String getIndConvenio() {
		return this.indConvenio;
	}

	public void setIndConvenio(String indConvenio) {
		this.indConvenio = indConvenio;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "horarioExameDisp")
	public Set<AelItemHorarioAgendado> getItemHorarioAgendados() {
		return this.itemHorarioAgendados;
	}

	public void setItemHorarioAgendados(Set<AelItemHorarioAgendado> aelItemHorarioAgendadoses) {
		this.itemHorarioAgendados = aelItemHorarioAgendadoses;
	}
	
	public enum Fields {
		ID("id"),
		ID_DTHR_AGENDA("id.dthrAgenda"),
		SERVIDOR("servidor"),
		SERVIDOR_ALTERADO("servidorAlterado"),
		TIPO_MARCACAO_EXAME("tipoMarcacaoExame"),
		TIPO_MARCACAO_EXAME_SEQ("tipoMarcacaoExame.seq"),
		SITUACAO_HORARIO("situacaoHorario"),
		GAE_UNF_SEQ("id.gaeUnfSeq"),
		GAE_SEQP("id.gaeSeqp"),
		DTHR_AGENDA("id.dthrAgenda"),
		GRADE_AGENDA_EXAME("gradeAgendaExame"),
		CRIADO_EM("criadoEm"),
		IND_HORARIO_EXTRA("indHorarioExtra"), 
		EXCLUSIVO_EXECUTOR("exclusivoExecutor"),
		ITEM_HORARIO_AGENDADOS("itemHorarioAgendados");

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
		if (!(obj instanceof AelHorarioExameDisp)) {
			return false;
		}
		AelHorarioExameDisp other = (AelHorarioExameDisp) obj;
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
