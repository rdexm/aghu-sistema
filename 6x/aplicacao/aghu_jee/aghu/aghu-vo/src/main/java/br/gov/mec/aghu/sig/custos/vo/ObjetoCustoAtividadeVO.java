package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;

public class ObjetoCustoAtividadeVO implements Serializable {

	private static final long serialVersionUID = 8679415542003814227L;
	
	private Integer ocvSeq;
	private Integer tvdSeq;
	
	public ObjetoCustoAtividadeVO(){
		
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
	
	public enum Fields{
		
		OCV_SEQ("ocvSeq"),
		TVD_SEQ("tvdSeq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}