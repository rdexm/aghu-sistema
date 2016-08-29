package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.AipPacientes;

public class HistoricoPacienteVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5078125359358328483L;
	
	private List<SituacaoPacienteVO> situacoes;
	private AipPacientes aipPaciente;
	private Date dtUltExame;
	private String localUltExame;
	private String localUltInternacao;
	private String localUltAlta;
	private String localUltProcedimento;
	private String localUltConsulta;

	public List<SituacaoPacienteVO> getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(List<SituacaoPacienteVO> situacoes) {
		this.situacoes = situacoes;
	}

	public AipPacientes getAipPaciente() {
		return aipPaciente;
	}

	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}

	public Date getDtUltExame() {
		return dtUltExame;
	}

	public void setDtUltExame(Date dtUltExame) {
		this.dtUltExame = dtUltExame;
	}

	public String getLocalUltExame() {
		return localUltExame;
	}

	public void setLocalUltExame(String localUltExame) {
		this.localUltExame = localUltExame;
	}

	public String getLocalUltInternacao() {
		return localUltInternacao;
	}

	public void setLocalUltInternacao(String localUltInternacao) {
		this.localUltInternacao = localUltInternacao;
	}

	public String getLocalUltAlta() {
		return localUltAlta;
	}

	public void setLocalUltAlta(String localUltAlta) {
		this.localUltAlta = localUltAlta;
	}

	public String getLocalUltProcedimento() {
		return localUltProcedimento;
	}

	public void setLocalUltProcedimento(String localUltProcedimento) {
		this.localUltProcedimento = localUltProcedimento;
	}

	public String getLocalUltConsulta() {
		return localUltConsulta;
	}

	public void setLocalUltConsulta(String localUltConsulta) {
		this.localUltConsulta = localUltConsulta;
	}

}
