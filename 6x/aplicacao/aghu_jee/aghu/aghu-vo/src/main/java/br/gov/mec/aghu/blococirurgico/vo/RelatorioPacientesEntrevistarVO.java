package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class RelatorioPacientesEntrevistarVO implements Serializable {
	
	private static final long serialVersionUID = -9130989986095199813L;
	
	private String indfuncao;
	private String anestesista;
	private String paciente;
	private String sala;
	private String dataHrInicio;
	private String dataHrFim;
	private String agenda;
	private String local;
	private String anestProfessor;
	private String idade;
	private String sexo;
	private String prontuario;
	private String cirurgiao;
	private String procedimentos;
	private String sugestao;
	private String crgSeq;
	
	private List<LinhaReportVO> listAnestesistaContratado;
	private List<LinhaReportVO> listProcedimentos;
	
	public RelatorioPacientesEntrevistarVO() {
		
	}

	public String getIndfuncao() {
		return indfuncao;
	}

	public void setIndfuncao(String indfuncao) {
		this.indfuncao = indfuncao;
	}

	public String getAnestesista() {
		return anestesista;
	}

	public void setAnestesista(String anestesista) {
		this.anestesista = anestesista;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getSala() {
		return sala;
	}

	public void setSala(String sala) {
		this.sala = sala;
	}

	public String getDataHrInicio() {
		return dataHrInicio;
	}

	public void setDataHrInicio(String dataHrInicio) {
		this.dataHrInicio = dataHrInicio;
	}

	public String getDataHrFim() {
		return dataHrFim;
	}

	public void setDataHrFim(String dataHrFim) {
		this.dataHrFim = dataHrFim;
	}

	public String getAgenda() {
		return agenda;
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getIdade() {
		return idade;
	}

	public void setIdade(String idade) {
		this.idade = idade;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getCirurgiao() {
		return cirurgiao;
	}

	public void setCirurgiao(String cirurgiao) {
		this.cirurgiao = cirurgiao;
	}

	public String getProcedimentos() {
		return procedimentos;
	}

	public void setProcedimentos(String procedimentos) {
		this.procedimentos = procedimentos;
	}

	public String getSugestao() {
		return sugestao;
	}

	public void setSugestao(String sugestao) {
		this.sugestao = sugestao;
	}

	public String getAnestProfessor() {
		return anestProfessor;
	}

	public void setAnestProfessor(String anestProfessor) {
		this.anestProfessor = anestProfessor;
	}

	public List<LinhaReportVO> getListAnestesistaContratado() {
		return listAnestesistaContratado;
	}

	public void setListAnestesistaContratado(
			List<LinhaReportVO> listAnestesistaContratado) {
		this.listAnestesistaContratado = listAnestesistaContratado;
	}

	public String getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(String crgSeq) {
		this.crgSeq = crgSeq;
	}

	public List<LinhaReportVO> getListProcedimentos() {
		return listProcedimentos;
	}

	public void setListProcedimentos(List<LinhaReportVO> listProcedimentos) {
		this.listProcedimentos = listProcedimentos;
	}
	
	
	
	
}