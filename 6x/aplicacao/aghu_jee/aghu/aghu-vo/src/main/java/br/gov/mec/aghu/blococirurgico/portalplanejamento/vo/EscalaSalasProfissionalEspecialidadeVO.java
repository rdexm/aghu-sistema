package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;

public class EscalaSalasProfissionalEspecialidadeVO implements Serializable {

	private static final long	serialVersionUID	= 1134859821281045005L;
	private Integer				matricula;
	private Short				codigo;
	private String				sigla;

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

}
