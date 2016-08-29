package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptAgsSq1", sequenceName="AGH.MPT_AGS_SQ1", allocationSize = 1)
@Table(name = "MPT_AGENDAMENTO_SESSAO", schema = "AGH")
public class MptAgendamentoSessao extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	private static final long serialVersionUID = -2639420655452717637L;
	
	private Short seq;
	private MptTipoSessao tipoSessao;
	private MptSalas sala;
	private DominioTurno turno;
	private AipPacientes paciente;
	private MpaVersaoProtAssistencial mpaVersaoProtAssistencial;
	private DominioTipoLocal tipoAcomodacao;
	private Date aPartirDe;
	private Date ate;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version; 	
	
	private Set<MptHorarioSessao> horariosSessao;
	
	public MptAgendamentoSessao() {
	}

	public MptAgendamentoSessao(Short seq, MptTipoSessao tipoSessao, MptSalas sala, DominioTurno turno,
			AipPacientes paciente, MpaVersaoProtAssistencial mpaVersaoProtAssistencial, DominioTipoLocal tipoAcomodacao,
			Date aPartirDe, Date ate, RapServidores servidor, Date criadoEm) {
		this.seq = seq;
		this.tipoSessao = tipoSessao;
		this.sala = sala;
		this.turno = turno;
		this.paciente = paciente;
		this.mpaVersaoProtAssistencial = mpaVersaoProtAssistencial;
		this.tipoAcomodacao = tipoAcomodacao;
		this.aPartirDe = aPartirDe;
		this.ate = ate;
		this.servidor = servidor;
		this.criadoEm = criadoEm;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptAgsSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPS_SEQ", nullable = false)
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAL_SEQ", nullable = false)
	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	@Column(name = "TURNO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTurno getTurno() {
		return turno;
	}

	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PAC_CODIGO", nullable = false)
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "VPA_PTA_SEQ", referencedColumnName = "PTA_SEQ"),
		@JoinColumn(name = "VPA_SEQP", referencedColumnName = "SEQP") })
	public MpaVersaoProtAssistencial getMpaVersaoProtAssistencial() {
		return mpaVersaoProtAssistencial;
	}

	public void setMpaVersaoProtAssistencial(
			MpaVersaoProtAssistencial mpaVersaoProtAssistencial) {
		this.mpaVersaoProtAssistencial = mpaVersaoProtAssistencial;
	}

	@Column(name = "TIPO_ACOMODACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoLocal getTipoAcomodacao() {
		return tipoAcomodacao;
	}

	public void setTipoAcomodacao(DominioTipoLocal tipoAcomodacao) {
		this.tipoAcomodacao = tipoAcomodacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "APARTIRDE", nullable = false, length = 7)
	public Date getaPartirDe() {
		return aPartirDe;
	}

	public void setaPartirDe(Date aPartirDe) {
		this.aPartirDe = aPartirDe;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ATE", nullable = false, length = 7)
	public Date getAte() {
		return ate;
	}

	public void setAte(Date ate) {
		this.ate = ate;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(
			RapServidores servidor) {
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

	public enum Fields {
		
		SEQ("seq"),
		TIPO_SESSAO("tipoSessao"),
		TPS_SEQ("tipoSessao.seq"),
		SALA("sala"),
		SAL_SEQ("sala.seq"),
		TURNO("turno"),
		PACIENTE("paciente"),
		PAC_CODIGO("paciente.codigo"),
		MPA_VERSAO_PROT_ASSISTENCIAL("mpaVersaoProtAssistencial"),
		TIPO_ACOMODACAO("tipoAcomodacao"),
		A_PARTIR_DE("aPartirDe"),
		ATE("ate"),
		SERVIDOR("servidor"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		HORARIOS_SESSAO("horariosSessao"),
		CRIADO_EM("criadoEm");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		MptAgendamentoSessao other = (MptAgendamentoSessao) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(mappedBy = "agendamentoSessao", fetch = FetchType.LAZY)
	public Set<MptHorarioSessao> getHorariosSessao() {
		return horariosSessao;
	}

	public void setHorariosSessao(Set<MptHorarioSessao> horariosSessao) {
		this.horariosSessao = horariosSessao;
	}
}
