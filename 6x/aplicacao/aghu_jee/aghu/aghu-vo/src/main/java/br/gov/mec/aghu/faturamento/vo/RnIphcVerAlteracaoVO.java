package br.gov.mec.aghu.faturamento.vo;

import java.math.BigDecimal;

/**
 * VO utilizado na function RN_IPHC_VER_ALTERACAO
 */
public class RnIphcVerAlteracaoVO implements java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8966774472475799777L;
	private BigDecimal vVlrServHospitalar;
	private BigDecimal vVlrSadt;
	private BigDecimal vVlrProcedimento;
	private BigDecimal vVlrAnestesista;
	private BigDecimal vVlrServProfissional;
	private Boolean retorno;
	
	public BigDecimal getvVlrServHospitalar() {
		return vVlrServHospitalar;
	}
	public void setvVlrServHospitalar(BigDecimal vVlrServHospitalar) {
		this.vVlrServHospitalar = vVlrServHospitalar;
	}
	public BigDecimal getvVlrSadt() {
		return vVlrSadt;
	}
	public void setvVlrSadt(BigDecimal vVlrSadt) {
		this.vVlrSadt = vVlrSadt;
	}
	public BigDecimal getvVlrProcedimento() {
		return vVlrProcedimento;
	}
	public void setvVlrProcedimento(BigDecimal vVlrProcedimento) {
		this.vVlrProcedimento = vVlrProcedimento;
	}
	public BigDecimal getvVlrAnestesista() {
		return vVlrAnestesista;
	}
	public void setvVlrAnestesista(BigDecimal vVlrAnestesista) {
		this.vVlrAnestesista = vVlrAnestesista;
	}
	public BigDecimal getvVlrServProfissional() {
		return vVlrServProfissional;
	}
	public void setvVlrServProfissional(BigDecimal vVlrServProfissional) {
		this.vVlrServProfissional = vVlrServProfissional;
	}
	public Boolean getRetorno() {
		return retorno;
	}
	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}
	
	
}