package br.gov.mec.aghu.paciente.prontuario.vo;

public class MatriculaVinculoVO {

	/**
	 * Número da matrícula do servidor do HCPA.
	 */
	private Integer matricula;
	
	/**
	 * Código do vínculo que o servidor tem com o HCPA
	 */
	private Short viniculo;

	public MatriculaVinculoVO(Integer matricula, Short viniculo) {
		super();
		this.matricula = matricula;
		this.viniculo = viniculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getViniculo() {
		return viniculo;
	}

	public void setViniculo(Short viniculo) {
		this.viniculo = viniculo;
	}
}
