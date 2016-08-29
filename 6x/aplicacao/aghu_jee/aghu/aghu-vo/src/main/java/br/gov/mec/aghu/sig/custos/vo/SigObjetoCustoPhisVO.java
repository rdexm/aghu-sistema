package br.gov.mec.aghu.sig.custos.vo;

public class SigObjetoCustoPhisVO implements java.io.Serializable {
	
	private static final long serialVersionUID = -842234839793272375L;
	
	private Integer ocvSeq;
	private Integer tvdSeq;
	
	public SigObjetoCustoPhisVO(){}
	
	public SigObjetoCustoPhisVO(Integer ocvSeq, Integer tvdSeq) {
		this.ocvSeq = ocvSeq;
		this.tvdSeq = tvdSeq;
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}

	public Integer getTvdSeq() {
		return tvdSeq;
	}

	public void setTvdSeq(Integer tvdSeq) {
		this.tvdSeq = tvdSeq;
	}	
	
}
