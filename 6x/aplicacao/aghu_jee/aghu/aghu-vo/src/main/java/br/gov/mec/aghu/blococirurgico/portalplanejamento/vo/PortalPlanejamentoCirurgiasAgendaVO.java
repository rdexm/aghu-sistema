package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class PortalPlanejamentoCirurgiasAgendaVO implements Serializable {

	private static final long serialVersionUID = -8629983923847678859L;

	private Integer seq;
	private Short sala;
	private String turno;
	private Date dataAgenda;
	private String inicioAgenda;
	private String fimAgenda;
	private String tempoSala;
	private Byte intervaloEscala;
	private Integer prontuario;
	private String nome;
	private String regime;
	private String geradoSistema;
	
	private Date entradaSala;
	private Date saidaSala;
	private Date inicioCirurgia;
	private Date fimCirurgia;
	private String situacao;
	
	private String paciente;
	private String procedimento;
	private String equipe;
	private String especialidade;
	private Boolean indGeradoSistema;
	
	private Boolean overbooking = false;
	private Boolean realizada = false;
	private Boolean escala = false;
	private Boolean selecionado = false;
	private Boolean planejado = false;
	private Boolean indisponivel = false;
	
	public Date horaInicial;
	public Date horaFinal;
	
	private Date criadoEm;
	private String criadoPor;
	
	private Integer seqAgenda;

	private Integer ordemTurno;
	
	public Date getDataAgenda() {
		return dataAgenda;
	}
	public void setDataAgenda(Date dataAgenda) {
		this.dataAgenda = dataAgenda;
	}
	public Short getSala() {
		return sala;
	}
	public void setSala(Short sala) {
		this.sala = sala;
	}
	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
	public String getInicioAgenda() {
		return inicioAgenda;
	}
	public void setInicioAgenda(String inicioAgenda) {
		this.inicioAgenda = inicioAgenda;
	}
	public String getFimAgenda() {
		return fimAgenda;
	}
	public void setFimAgenda(String fimAgenda) {
		this.fimAgenda = fimAgenda;
	}
	public String getTempoSala() {
		return tempoSala;
	}
	public void setTempoSala(String tempoSala) {
		this.tempoSala = tempoSala;
	}
	public Byte getIntervaloEscala() {
		return intervaloEscala == null ? 0 : intervaloEscala;
	}
	public void setIntervaloEscala(Byte intervaloEscala) {
		this.intervaloEscala = intervaloEscala;
	}
	public String getPaciente() {
		return paciente;
	}
	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}
	public String getProcedimento() {
		return procedimento;
	}
	public void setProcedimento(String procedimento) {
		this.procedimento = procedimento;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public Boolean getOverbooking() {
		return overbooking;
	}
	public void setOverbooking(Boolean overbooking) {
		this.overbooking = overbooking;
	}
	public Boolean getRealizada() {
		return realizada;
	}
	public void setRealizada(Boolean realizada) {
		this.realizada = realizada;
	}
	public Boolean getEscala() {
		return escala;
	}
	public void setEscala(Boolean escala) {
		this.escala = escala;
	}
	public Boolean getSelecionado() {
		return selecionado;
	}
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}
	public Boolean getPlanejado() {
		return planejado;
	}
	public void setPlanejado(Boolean planejado) {
		this.planejado = planejado;
	}
	public Boolean getIndisponivel() {
		return indisponivel;
	}
	public void setIndisponivel(Boolean indisponivel) {
		this.indisponivel = indisponivel;
	}
	public Integer getSeqAgenda() {
		return seqAgenda;
	}
	public void setSeqAgenda(Integer seqAgenda) {
		this.seqAgenda = seqAgenda;
	}
	public Date getHoraInicial() {
		return horaInicial;
	}
	public void setHoraInicial(Date horaInicial) {
		this.horaInicial = horaInicial;
	}
	public Date getHoraFinal() {
		return horaFinal;
	}
	public void setHoraFinal(Date horaFinal) {
		this.horaFinal = horaFinal;
	}
	public Boolean getIndGeradoSistema() {
		return indGeradoSistema;
	}
	public void setIndGeradoSistema(Boolean indGeradoSistema) {
		this.indGeradoSistema = indGeradoSistema;
	}
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getCriadoPor() {
		return criadoPor;
	}
	public void setCriadoPor(String criadoPor) {
		this.criadoPor = criadoPor;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		setSeqAgenda(seq);
		this.seq = seq;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getRegime() {
		return regime;
	}
	public void setRegime(String regime) {
		this.regime = regime;
	}
	public Integer getOrdemTurno() {
		return ordemTurno;
	}
	public void setOrdemTurno(Integer ordemTurno) {
		this.ordemTurno = ordemTurno;
	}
	public Date getEntradaSala() {
		return entradaSala;
	}
	public void setEntradaSala(Date entradaSala) {
		this.entradaSala = entradaSala;
	}
	public Date getSaidaSala() {
		return saidaSala;
	}
	public void setSaidaSala(Date saidaSala) {
		this.saidaSala = saidaSala;
	}
	public Date getInicioCirurgia() {
		return inicioCirurgia;
	}
	public void setInicioCirurgia(Date inicioCirurgia) {
		this.inicioCirurgia = inicioCirurgia;
	}
	public Date getFimCirurgia() {
		return fimCirurgia;
	}
	public void setFimCirurgia(Date fimCirurgia) {
		this.fimCirurgia = fimCirurgia;
	}
	public String getSituacao() {
		return situacao;
	}
	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}
	public String getGeradoSistema() {
		return geradoSistema;
	}
	public void setGeradoSistema(String geradoSistema) {
		setIndGeradoSistema(DominioSimNao.valueOf(geradoSistema).isSim());
		this.geradoSistema = geradoSistema;
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
		if (!(obj instanceof PortalPlanejamentoCirurgiasAgendaVO)) {
			return false;
		}
		PortalPlanejamentoCirurgiasAgendaVO other = (PortalPlanejamentoCirurgiasAgendaVO) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}

	public enum Fields {
		SALA("sala"),
		TURNO("turno"),
		DATA("dataAgenda"),
		EQUIPE("equipe"),
		HORA_INICIAL("horaInicial"),
		INICIO_AGENDA("inicioAgenda"),
		ORDEM_TURNO("ordemTurno"); 

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


}
