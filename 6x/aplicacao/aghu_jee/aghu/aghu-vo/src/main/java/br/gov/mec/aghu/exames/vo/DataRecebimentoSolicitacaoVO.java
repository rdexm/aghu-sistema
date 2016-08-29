package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;


public class DataRecebimentoSolicitacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3962481841768310070L;

	private Date dataRecebimento;
	
	private Integer soeSeq;

	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	
	
	
}