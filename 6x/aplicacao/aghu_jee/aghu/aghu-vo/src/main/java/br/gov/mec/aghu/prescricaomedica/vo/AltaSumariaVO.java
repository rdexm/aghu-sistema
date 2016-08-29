package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
/**
 * 
 * @author frocha
 *
 */
public class AltaSumariaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1352744858618728093L;
	private String tipo;
	
	private Date dthrAlta;
	
	public Date getDthrAlta() {
		return dthrAlta;
	}
	public void setDthrAlta(Date dthrAlta) {
		this.dthrAlta = dthrAlta;
	}
	
	
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	
	
}
