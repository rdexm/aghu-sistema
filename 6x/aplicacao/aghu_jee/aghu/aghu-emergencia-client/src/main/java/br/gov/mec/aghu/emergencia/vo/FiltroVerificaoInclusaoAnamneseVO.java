package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;

public class FiltroVerificaoInclusaoAnamneseVO implements Serializable {

	private static final long serialVersionUID = -778459105037676706L;
	
	private Integer consulta;
	private Integer paciente;
	private Short sequence;
	
	public FiltroVerificaoInclusaoAnamneseVO() {
		super();
	}

	public FiltroVerificaoInclusaoAnamneseVO(Integer consulta,
			Integer paciente, Short sequence) {
		super();
		this.consulta = consulta;
		this.paciente = paciente;
		this.sequence = sequence;
	}

	public Integer getConsulta() {
		return consulta;
	}

	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}

	public Integer getPaciente() {
		return paciente;
	}

	public void setPaciente(Integer paciente) {
		this.paciente = paciente;
	}

	public Short getSequence() {
		return sequence;
	}

	public void setSequence(Short sequence) {
		this.sequence = sequence;
	}

}
