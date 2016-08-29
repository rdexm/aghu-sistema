package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class DisponibilidadeHorariosVO implements BaseBean {
	
	private static final long serialVersionUID = 2509175394203532437L;

	private Integer grdSeq;
	private String zonaSala;
	private String equipe;
	private String especialidade;
	private String profissional;
	private Integer consultasLiberadas;
	private Integer consultasBloqueadas;
	private Date horaInicio;
	private Date horaFim;
	public Integer getGrdSeq() {
		return grdSeq;
	}
	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}
	public String getZonaSala() {
		return zonaSala;
	}
	public void setZonaSala(String zonaSala) {
		this.zonaSala = zonaSala;
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
	public String getProfissional() {
		return profissional;
	}
	public void setProfissional(String profissional) {
		this.profissional = profissional;
	}
	public Integer getConsultasLiberadas() {
		return consultasLiberadas;
	}
	public void setConsultasLiberadas(Integer consultasLiberadas) {
		this.consultasLiberadas = consultasLiberadas;
	}
	public Integer getConsultasBloqueadas() {
		return consultasBloqueadas;
	}
	public void setConsultasBloqueadas(Integer consultasBloqueadas) {
		this.consultasBloqueadas = consultasBloqueadas;
	}
	public Date getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}
	public Date getHoraFim() {
		return horaFim;
	}
	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}
	
	
	
	

}
