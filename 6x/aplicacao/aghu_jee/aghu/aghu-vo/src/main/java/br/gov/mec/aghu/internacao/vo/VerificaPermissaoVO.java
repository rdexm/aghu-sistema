package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class VerificaPermissaoVO {
	private Integer diasInt;
	private Short  diasPerm;
	private DominioSimNao  permMaior;
	
	//### GETS e SETS ###
	public Integer getDiasInt() {
		return diasInt;
	}
	public void setDiasInt(Integer diasInt) {
		this.diasInt = diasInt;
	}
	public Short getDiasPerm() {
		return diasPerm;
	}
	public void setDiasPerm(Short diasPerm) {
		this.diasPerm = diasPerm;
	}
	public DominioSimNao getPermMaior() {
		return permMaior;
	}
	public void setPermMaior(DominioSimNao permMaior) {
		this.permMaior = permMaior;
	} 
	

	
}
