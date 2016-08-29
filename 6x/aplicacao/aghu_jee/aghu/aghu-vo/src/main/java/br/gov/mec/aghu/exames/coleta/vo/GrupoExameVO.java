package br.gov.mec.aghu.exames.coleta.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author dansantos
 *
 */
public class GrupoExameVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6651343283810553198L;
	/**
	 * 
	 */
	
	private Integer seq;
	private Short unfSeq;
	private Date dthrReativacao;
	
	public GrupoExameVO() {
	}

	public GrupoExameVO(Integer seq, Short unfSeq, Date dthrReativacao) {
		this.seq = seq;
		this.unfSeq = unfSeq;
		this.dthrReativacao = dthrReativacao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Date getDthrReativacao() {
		return dthrReativacao;
	}

	public void setDthrReativacao(Date dthrReativacao) {
		this.dthrReativacao = dthrReativacao;
	}
}
	
