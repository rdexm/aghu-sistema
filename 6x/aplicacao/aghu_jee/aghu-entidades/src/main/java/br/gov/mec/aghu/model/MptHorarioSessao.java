package br.gov.mec.aghu.model;

import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptHrsSq1", sequenceName="AGH.MPT_HRS_SQ1", allocationSize = 1)
@Table(name = "MPT_HORARIO_SESSAO", schema = "AGH")

public class MptHorarioSessao extends BaseEntitySeq<Short> implements java.io.Serializable {
	
	private static final long serialVersionUID = 3934601518964037741L;
	
	private Short seq;
	private MptAgendamentoSessao agendamentoSessao;
	private MptPrescricaoPaciente prescricaoPaciente;
	private MptLocalAtendimento localAtendimento;
	private Short dia;
	private Date tempo;
	private Date dataInicio;
	private Date dataFim;
	private Short ciclo;
	private DominioSituacaoHorarioSessao indSituacao;
	private Date criadoEm;
	private RapServidores servidor;
	private String consultasAmb;
	private Integer sesSeq;
	private MptSessao mptSessao;
	private Integer version; 	
	
	public MptHorarioSessao() {
		
	}

	public MptHorarioSessao(Short seq, MptAgendamentoSessao agendamentoSessao, MptPrescricaoPaciente prescricaoPaciente,
			MptLocalAtendimento localAtendimento, Short dia, Date tempo, Date dataInicio, Date dataFim, Short ciclo,
			DominioSituacaoHorarioSessao indSituacao, Date criadoEm, RapServidores servidor, String consultasAmb, Integer sesSeq) {
		this.seq = seq;
		this.agendamentoSessao = agendamentoSessao;
		this.prescricaoPaciente = prescricaoPaciente;
		this.localAtendimento = localAtendimento;
		this.dia = dia;
		this.tempo = tempo;
		this.dataInicio = dataInicio;
		this.dataFim = dataFim;
		this.ciclo = ciclo;
		this.indSituacao = indSituacao;
		this.criadoEm = criadoEm;
		this.servidor = servidor;
		this.consultasAmb = consultasAmb;
		this.sesSeq = sesSeq;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptHrsSq1")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return this.seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AGS_SEQ", nullable = false)
	public MptAgendamentoSessao getAgendamentoSessao() {
		return agendamentoSessao;
	}

	public void setAgendamentoSessao(MptAgendamentoSessao agendamentoSessao) {
		this.agendamentoSessao = agendamentoSessao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "PTE_ATD_SEQ", referencedColumnName = "ATD_SEQ"),
		@JoinColumn(name = "PTE_SEQ", referencedColumnName = "SEQ") })
	public MptPrescricaoPaciente getPrescricaoPaciente() {
		return prescricaoPaciente;
	}

	public void setPrescricaoPaciente(MptPrescricaoPaciente prescricaoPaciente) {
		this.prescricaoPaciente = prescricaoPaciente;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOA_SEQ", nullable = false)
	public MptLocalAtendimento getLocalAtendimento() {
		return localAtendimento;
	}

	public void setLocalAtendimento(MptLocalAtendimento localAtendimento) {
		this.localAtendimento = localAtendimento;
	}

	@Column(name = "DIA", precision = 4, scale = 0)
	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "TEMPO", length = 7)
	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_INICIO", length = 7)
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_FIM", length = 7)
	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	@Column(name = "CICLO", precision = 4, scale = 0)
	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}
	
	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoHorarioSessao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoHorarioSessao indSituacao) {
		this.indSituacao = indSituacao;
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

	@Column(name = "CONSULTAS_AMB", length = 1000)
	@Length(max = 1000)
	public String getConsultasAmb() {
		return consultasAmb;
	}

	public void setConsultasAmb(String consultasAmb) {
		this.consultasAmb = consultasAmb;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SES_SEQ", referencedColumnName = "seq")
	public MptSessao getMptSessao() {
		return mptSessao;
	}

	public void setMptSessao(MptSessao mptSessao) {
		this.mptSessao = mptSessao;
	}

	@Column(name = "SES_SEQ", precision = 8, scale = 0, insertable = false, updatable = false)
	public Integer getSesSeq() {
		return sesSeq;
	}

	public void setSesSeq(Integer sesSeq) {
		this.sesSeq = sesSeq;
	}
	
	


	public enum Fields {
		
		SEQ("seq"),
		AGENDAMENTO_SESSAO("agendamentoSessao"),
		AGS_SEQ("agendamentoSessao.seq"),
		PRESCRICAO_PACIENTE("prescricaoPaciente"),
		PTE_ATD_SEQ("prescricaoPaciente.id.atdSeq"),
		PTE_SEQ("prescricaoPaciente.id.seq"),
		LOCAL_ATENDIMENTO("localAtendimento"),
		LOA_SEQ("localAtendimento.seq"),
		DIA("dia"),
		TEMPO("tempo"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim"),
		CICLO("ciclo"),
		IND_SITUACAO("indSituacao"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_MATRICULA("servidor.id.matricula"),
		SERVIDOR_VIN_CODIGO("servidor.id.vinCodigo"),
		CONSULTAS_AMB("consultasAmb"),
		SES_SEQ("sesSeq"),
		SESSAO("mptSessao"),
		SESSAO_CODIGO("mptSessao.seq");
		
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
		MptHorarioSessao other = (MptHorarioSessao) obj;
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


}
