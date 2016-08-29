package br.gov.mec.aghu.faturamento.vo;

public class RnCapcCboProcResVO {
	
	private String pErro;
	
	private Short pSerVinCodigo;
	
	private Integer pSerMatr;
	
	private String retorno;

	public RnCapcCboProcResVO() {		
	}
	
	public RnCapcCboProcResVO(String pErro, Short pSerVinCodigo,
			Integer pSerMatr) {
		super();
		this.pErro = pErro;
		this.pSerVinCodigo = pSerVinCodigo;
		this.pSerMatr = pSerMatr;
	}

	public String getpErro() {
		return pErro;
	}

	public void setpErro(String pErro) {
		this.pErro = pErro;
	}

	public Short getpSerVinCodigo() {
		return pSerVinCodigo;
	}

	public void setpSerVinCodigo(Short pSerVinCodigo) {
		this.pSerVinCodigo = pSerVinCodigo;
	}

	public Integer getpSerMatr() {
		return pSerMatr;
	}

	public void setpSerMatr(Integer pSerMatr) {
		this.pSerMatr = pSerMatr;
	}

	public String getRetorno() {
		return retorno;
	}

	public void setRetorno(String retorno) {
		this.retorno = retorno;
	}
	
	
}
