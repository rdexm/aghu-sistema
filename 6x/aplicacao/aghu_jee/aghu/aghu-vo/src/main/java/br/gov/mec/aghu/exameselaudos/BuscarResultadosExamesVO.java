package br.gov.mec.aghu.exameselaudos;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


public class BuscarResultadosExamesVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3063582624086562588L;

	private BigDecimal resultado;

	private Date data;

	public BuscarResultadosExamesVO() {
	}

	public BuscarResultadosExamesVO(BigDecimal resultado, Date data) {
		this.resultado = resultado;
		this.data = data;
	}

	public BigDecimal getResultado() {
		return resultado;
	}

	public void setResultado(BigDecimal resultado) {
		this.resultado = resultado;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

}
