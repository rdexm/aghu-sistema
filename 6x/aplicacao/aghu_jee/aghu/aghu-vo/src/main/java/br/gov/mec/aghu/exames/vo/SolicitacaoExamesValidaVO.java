package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;

public class SolicitacaoExamesValidaVO implements Serializable{

	

	private static final long serialVersionUID = -2912587321708530893L;
	private Date criadoEm;
	private Integer serMatriculaValida;
	private Short serVinculoValida;
	public Date getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}
	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}
	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}
	public Short getSerVinculoValida() {
		return serVinculoValida;
	}
	public void setSerVinculoValida(Short serVinculoValida) {
		this.serVinculoValida = serVinculoValida;
	}
	

}
