package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptProtocoloCiclo;

public class AgendamentosPacienteVO implements Serializable{

	private static final long serialVersionUID = -8155940376337754087L;
	
	private Integer seqSessao;
	private Short ciclo;
	private Short dia;
	private Date tempo;
	private Date dtInicio;
	private Date dtFim;
	private DominioTurno turno;
	private DominioSituacaoHorarioSessao indSituacao;
	private Integer seqClo;
	private String protocolo;
	private List<MptProtocoloCiclo> protocolos;
	
	public Integer getSeqSessao() {
		return seqSessao;
	}
	
	public void setSeqSessao(Integer seqSessao) {
		this.seqSessao = seqSessao;
	}
	
	public Short getDia() {
		return dia;
	}
	
	public void setDia(Short dia) {
		this.dia = dia;
	}
	
	public Date getTempo() {
		return tempo;
	}
	
	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}
	
	public Date getDtInicio() {
		return dtInicio;
	}
	
	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	
	public Date getDtFim() {
		return dtFim;
	}
	
	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}
	
	public DominioTurno getTurno() {
		return turno;
	}
	
	public void setTurno(DominioTurno turno) {
		this.turno = turno;
	}
	
	public DominioSituacaoHorarioSessao getIndSituacao() {
		return indSituacao;
	}
	
	public void setIndSituacao(DominioSituacaoHorarioSessao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public Integer getSeqClo() {
		return seqClo;
	}
	
	public void setSeqClo(Integer seqClo) {
		this.seqClo = seqClo;
	}
	
	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public List<MptProtocoloCiclo> getProtocolos() {
		return protocolos;
	}

	public void setProtocolos(List<MptProtocoloCiclo> protocolos) {
		this.protocolos = protocolos;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public enum Fields {
		SEQ_SESSAO("seqSessao"),
		CICLO("ciclo"),
		DIA("dia"),
		TEMPO("tempo"),
		DATA_INICIO("dtInicio"),
		DATA_FIM("dtFim"),
		TURNO("turno"),
		IND_SITUACAO("indSituacao"),
		SEQ_CLO("seqClo"),
		PROTOCOLO("protocolo"),
		PROTOCOLOS("protocolos");
		
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
	
	
	
	
	 
	 
	