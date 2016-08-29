package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthcVerDadosProfVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4570455697169730168L;

	private Integer equipe;

	private Integer cpfCns;

	private String cbo;

	private Boolean retorno;

	public Integer getEquipe() {
		return equipe;
	}

	public void setEquipe(Integer equipe) {
		this.equipe = equipe;
	}

	public Integer getCpfCns() {
		return cpfCns;
	}

	public void setCpfCns(Integer cpfCns) {
		this.cpfCns = cpfCns;
	}

	public String getCbo() {
		return cbo;
	}

	public void setCbo(String cbo) {
		this.cbo = cbo;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

}
