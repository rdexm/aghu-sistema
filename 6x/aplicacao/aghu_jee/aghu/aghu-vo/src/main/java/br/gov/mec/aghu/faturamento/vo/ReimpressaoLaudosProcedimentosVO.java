package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

@SuppressWarnings("ucd")
public class ReimpressaoLaudosProcedimentosVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1497693638323287413L;

	private Integer atdSeq;

	private Date dthrInicio;

	private Date dthrFim;

	private Integer apaSeq;

	private Short seqp;

	public ReimpressaoLaudosProcedimentosVO() {
	}

	public ReimpressaoLaudosProcedimentosVO(Integer atdSeq, Date dthrInicio, Date dthrFim, Integer apaSeq, Short seqp) {
		this.atdSeq = atdSeq;
		this.dthrInicio = dthrInicio;
		this.dthrFim = dthrFim;
		this.apaSeq = apaSeq;
		this.seqp = seqp;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Integer getApaSeq() {
		return apaSeq;
	}

	public void setApaSeq(Integer apaSeq) {
		this.apaSeq = apaSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

}
