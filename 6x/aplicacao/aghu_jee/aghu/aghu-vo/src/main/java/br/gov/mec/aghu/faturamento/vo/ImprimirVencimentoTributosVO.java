package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.List;

public class ImprimirVencimentoTributosVO implements Serializable {


	/**
	 *@author felipe.santos
	 */
	private static final long serialVersionUID = 7875055156068823746L;
	private String mesAno;
	private List<TributosVO> tributosFederal;
	private List<TributosVO> tributosMunicipal;
	private List<TributosVO> tributosPrevidenciarios;

	public String getMesAno() {
		return mesAno;
	}

	public void setMesAno(String mesAno) {
		this.mesAno = mesAno;
	}

	public List<TributosVO> getTributosFederal() {
		return tributosFederal;
	}

	public void setTributosFederal(List<TributosVO> tributosFederal) {
		this.tributosFederal = tributosFederal;
	}

	public List<TributosVO> getTributosMunicipal() {
		return tributosMunicipal;
	}

	public void setTributosMunicipal(List<TributosVO> tributosMunicipal) {
		this.tributosMunicipal = tributosMunicipal;
	}

	public List<TributosVO> getTributosPrevidenciarios() {
		return tributosPrevidenciarios;
	}

	public void setTributosPrevidenciarios(List<TributosVO> tributosPrevidenciarios) {
		this.tributosPrevidenciarios = tributosPrevidenciarios;
	}

	
	
	
}
