package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioExamesPendentesVO implements Serializable {
	
	private static final long serialVersionUID = 6306203977193638835L;
	
	private Date dataHoraEvento;
	private String dataHoraEventoRel; // Data formatada
	private Integer solicitacao;
	private Long numeroAp;
	private Integer lu2Seq;
	private String tipo;
	private Integer prontuario;
	private String prontuarioRel; // Prontuário formatado
	private String nomePaciente;
	private String exameMaterial;
	private String patologistaResponsavel;
	private String residenteResponsavel;
	private Integer diasPendente;
	
	public RelatorioExamesPendentesVO() {
		super();
	}
	
	public enum Fields {
		DTHR_EVENTO("dataHoraEvento"),
		DTHR_EVENTO_REL("dataHoraEventoRel"),
		SOLICITACAO("solicitacao"),
		NUMERO_AP("numeroAp"),
		LU2_SEQ("lu2Seq"),
		TIPO("tipo"),
		PRONTUARIO("prontuario"),
		PRONTUARIO_REL("prontuarioRel"),
		NOME_PACIENTE("nomePaciente"),
		EXAME_MATERIAL("exameMaterial"),
		PATOLOGISTA_RESPONSAVEL("patologistaResponsavel"),
		RESIDENTE_RESPONSAVEL("residenteResponsavel"),
		DIAS_PENDENTE("diasPendente");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Date getDataHoraEvento() {
		return dataHoraEvento;
	}

	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}

	public String getDataHoraEventoRel() {
		return dataHoraEventoRel;
	}

	public void setDataHoraEventoRel(String dataHoraEventoRel) {
		this.dataHoraEventoRel = dataHoraEventoRel;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public Long getNumeroAp() {
		return numeroAp;
	}

	public void setNumeroAp(Long numeroAp) {
		this.numeroAp = numeroAp;
	}

	public Integer getLu2Seq() {
		return lu2Seq;
	}

	public void setLu2Seq(Integer lu2Seq) {
		this.lu2Seq = lu2Seq;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getProntuarioRel() {
		return prontuarioRel;
	}

	public void setProntuarioRel(String prontuarioRel) {
		this.prontuarioRel = prontuarioRel;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getExameMaterial() {
		return exameMaterial;
	}

	public void setExameMaterial(String exameMaterial) {
		this.exameMaterial = exameMaterial;
	}

	public String getPatologistaResponsavel() {
		return patologistaResponsavel;
	}

	public void setPatologistaResponsavel(String patologistaResponsavel) {
		this.patologistaResponsavel = patologistaResponsavel;
	}

	public String getResidenteResponsavel() {
		return residenteResponsavel;
	}

	public void setResidenteResponsavel(String residenteResponsavel) {
		this.residenteResponsavel = residenteResponsavel;
	}

	public Integer getDiasPendente() {
		return diasPendente;
	}

	public void setDiasPendente(Integer diasPendente) {
		this.diasPendente = diasPendente;
	}
	
	
}