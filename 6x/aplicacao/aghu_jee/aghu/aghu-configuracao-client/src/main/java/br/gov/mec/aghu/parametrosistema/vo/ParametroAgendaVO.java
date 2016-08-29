package br.gov.mec.aghu.parametrosistema.vo;

import java.math.BigDecimal;

public class ParametroAgendaVO {
	private BigDecimal codCnvSusCod;
	private BigDecimal codCspIntSeq;
	private BigDecimal codCrgPrzPln;
	
	public BigDecimal getCodCnvSusCod() {
		return codCnvSusCod;
	}
	public void setCodCnvSusCod(BigDecimal codCnvSusCod) {
		this.codCnvSusCod = codCnvSusCod;
	}
	public BigDecimal getCodCspIntSeq() {
		return codCspIntSeq;
	}
	public void setCodCspIntSeq(BigDecimal codCspIntSeq) {
		this.codCspIntSeq = codCspIntSeq;
	}
	public BigDecimal getCodCrgPrzPln() {
		return codCrgPrzPln;
	}
	public void setCodCrgPrzPln(BigDecimal codCrgPrzPln) {
		this.codCrgPrzPln = codCrgPrzPln;
	}
}
