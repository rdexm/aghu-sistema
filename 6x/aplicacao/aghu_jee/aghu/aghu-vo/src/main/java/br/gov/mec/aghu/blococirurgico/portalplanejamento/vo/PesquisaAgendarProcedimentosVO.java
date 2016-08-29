package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaAgendarProcedimentosVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1938842027342597391L;
	
	private Short sala;
	private String especialidade;
	private Date hrInicio;
	private Date hrFim;
	private Short ordemTurno;

	public PesquisaAgendarProcedimentosVO(Short sala, String especialidade,
			Date hrInicio, Date hrFim, Short ordemTurno) {
		super();
		this.sala = sala;
		this.especialidade = especialidade;
		this.hrInicio = hrInicio;
		this.hrFim = hrFim;
		this.ordemTurno = ordemTurno;
	}
	
	public Short getSala() {
		return sala;
	}

	public String getEspecialidade() {
		return especialidade;
	}

	public Date getHrInicio() {
		return hrInicio;
	}

	public Date getHrFim() {
		return hrFim;
	}

	public void setSala(Short sala) {
		this.sala = sala;
	}

	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}

	public void setHrInicio(Date hrInicio) {
		this.hrInicio = hrInicio;
	}

	public void setHrFim(Date hrFim) {
		this.hrFim = hrFim;
	}
	
	public Short getOrdemTurno() {
		return ordemTurno;
	}

	public void setOrdemTurno(Short ordemTurno) {
		this.ordemTurno = ordemTurno;
	}

}
