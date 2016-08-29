package br.gov.mec.aghu.exames.agendamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.VAelExaAgendPac;

/**
 * 
 * @author gzapalaglio
 *
 */
public class VAelExaAgendPacVO implements Serializable {

	
	
	private static final long serialVersionUID = 1823747687850033068L;
	private Integer id;
	private VAelExaAgendPac exameAgendadoPaciente;
	
	public VAelExaAgendPacVO() {
	}

	public void setExameAgendadoPaciente(VAelExaAgendPac exameAgendadoPaciente) {
		this.exameAgendadoPaciente = exameAgendadoPaciente;
	}

	public VAelExaAgendPac getExameAgendadoPaciente() {
		return exameAgendadoPaciente;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	

}
