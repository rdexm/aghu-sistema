package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.Date;


public class InternacaoPacienteVO implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1653591879244860403L;

	
	private Date dthrAltaMedica;
	
	private String descricao;
	
	private Date dthrInicio;
	
	private Integer matricula;
	private Short vinculo;

	
	public Date getDthrAltaMedica() {
		return dthrAltaMedica;
	}

	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setDthrInicio(
			Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Date getDthrInicioUltimoAtendimento() {
		return dthrInicio;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public Short getVinculo() {
		return vinculo;
	}
	
	
}
