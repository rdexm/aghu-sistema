package br.gov.mec.aghu.faturamento.vo;

public class FtLogErrorPhiCodVO {

	
	Integer codigoPHI;
	Integer codigoErro;
	

	public FtLogErrorPhiCodVO(Integer codigoPHI,Integer codigoErro) {
		this.codigoPHI = codigoPHI;
		this.codigoErro = codigoErro;
	}
	
	public FtLogErrorPhiCodVO(){
	}

	public Integer getCodigoPHI() {
		return codigoPHI;
	}

	public void setCodigoPHI(Integer codigoPHI) {
		this.codigoPHI = codigoPHI;
	}

	public Integer getCodigoErro() {
		return codigoErro;
	}

	public void setCodigoErro(Integer codigoErro) {
		this.codigoErro = codigoErro;
	}

}
