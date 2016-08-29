package br.gov.mec.aghu.paciente.vo;

import br.gov.mec.aghu.core.commons.BaseBean;



public class CodPacienteFoneticaVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 653990119302259411L;
	private Integer codigo;

	
	public Integer getCodigo() {
		return codigo;
	}

	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
}
