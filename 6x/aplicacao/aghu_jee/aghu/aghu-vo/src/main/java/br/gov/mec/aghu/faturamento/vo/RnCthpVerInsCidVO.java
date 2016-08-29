package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class RnCthpVerInsCidVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6458764679486071659L;

	private String erroCid;
	
	private String cidIni;
	
	private Boolean contaOk;
	
	private Boolean retorno;
	
	private Integer codigo;

	public String getErroCid() {
		return erroCid;
	}

	public void setErroCid(String erroCid) {
		this.erroCid = erroCid;
	}

	public String getCidIni() {
		return cidIni;
	}

	public void setCidIni(String cidIni) {
		this.cidIni = cidIni;
	}

	public Boolean getContaOk() {
		return contaOk;
	}

	public void setContaOk(Boolean contaOk) {
		this.contaOk = contaOk;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
}
