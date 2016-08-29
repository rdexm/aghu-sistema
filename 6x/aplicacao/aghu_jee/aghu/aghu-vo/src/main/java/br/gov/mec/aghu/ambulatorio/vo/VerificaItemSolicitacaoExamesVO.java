package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class VerificaItemSolicitacaoExamesVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4202763989002456298L;
	private Integer soeSeq;
	private Boolean indInfComplImp;
	
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Boolean getIndInfComplImp() {
		return indInfComplImp;
	}
	public void setIndInfComplImp(Boolean indInfComplImp) {
		this.indInfComplImp = indInfComplImp;
	}
	
	public enum Fields {
		
		SOE_SEQ("soeSeq"),
		IND_INF_COMPL_IMP("indInfComplImp");
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}	
}
