package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Filtro para busca de AacConsultas
 * 
 * @author israel.haas
 * 
 */
public class ConsultaVO implements Serializable {

	private static final long serialVersionUID = -4195817103912553934L;

	private Integer numero;
	private Date dthrInicio;
	
	
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	public Date getDthrInicio() {
		return dthrInicio;
	}
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
}
