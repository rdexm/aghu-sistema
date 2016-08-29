package br.gov.mec.aghu.exames.agendamento.vo;

import java.util.Date;

public class EtiquetaEnvelopePacienteVO {
	
	private String nomePaciente;
	private Integer soeSeq;
	private Short quarto;
	private String leito;
	private Integer prontuario;
	private String etiqueta;
	private String dataAgenda;
	private Date dataHoraEvento;
	private String nomeUnidadeFuncional;
	
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getQuarto() {
		return quarto;
	}
	public void setQuarto(Short quarto) {
		this.quarto = quarto;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getEtiqueta() {
		return etiqueta;
	}
	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}
	public String getDataAgenda() {
		return dataAgenda;
	}
	public void setDataAgenda(String dataAgenda) {
		this.dataAgenda = dataAgenda;
	}
	public String getNomeUnidadeFuncional() {
		return nomeUnidadeFuncional;
	}
	public void setNomeUnidadeFuncional(String nomeUnidadeFuncional) {
		this.nomeUnidadeFuncional = nomeUnidadeFuncional;
	}
	public void setDataHoraEvento(Date dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}
	public Date getDataHoraEvento() {
		return dataHoraEvento;
	}
	
	
}
