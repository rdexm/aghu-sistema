package br.gov.mec.aghu.bancosangue.vo;

import java.io.Serializable;


public class AtualizarMatriculaVinculoServidorVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7904584940256528459L;

	private Integer matricula;

	private Short vinculo;

	public AtualizarMatriculaVinculoServidorVO() {
	}

	public AtualizarMatriculaVinculoServidorVO(Integer matricula, Short vinculo) {
		this.matricula = matricula;
		this.vinculo = vinculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

}
