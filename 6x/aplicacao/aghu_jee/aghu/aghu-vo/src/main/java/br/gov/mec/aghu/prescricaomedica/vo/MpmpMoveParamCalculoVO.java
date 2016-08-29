package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

/**
 * #990 VO para o retorno da PROCEDURE MPMP_MOVE_PARAM_CALCULO
 * 
 * @author aghu
 *
 */
public class MpmpMoveParamCalculoVO {

	private Integer atdSeq;
	private Date criadoEm;

	public MpmpMoveParamCalculoVO(Integer atdSeq, Date criadoEm) {
		super();
		this.atdSeq = atdSeq;
		this.criadoEm = criadoEm;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

}