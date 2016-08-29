package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

public class RegistrarConsultaCOVO implements Serializable {

	private static final long serialVersionUID = -4084673962319549782L;
	
	private Integer consulta;
	private Date dataHoraConsulta;
	private String motivo;

	public Integer getConsulta() {
		return consulta;
	}

	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}

	public Date getDataHoraConsulta() {
		return dataHoraConsulta;
	}

	public void setDataHoraConsulta(Date dataHoraConsulta) {
		this.dataHoraConsulta = dataHoraConsulta;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

}
