package br.gov.mec.aghu.sig.custos.vo;


public class EquipamentoCirurgiaVO implements java.io.Serializable {
	
	private static final long serialVersionUID = 1264687248845290362L;
	private Integer seqPhi;
	private Number sumQtde;
	
	public EquipamentoCirurgiaVO(){
	}
	
	public Integer getSeqPhi() {
		return seqPhi;
	}
	public void setSeqPhi(Integer seqPhi) {
		this.seqPhi = seqPhi;
	}
	public Number getSumQtde() {
		return sumQtde;
	}
	public void setSumQtde(Number sumQtde) {
		this.sumQtde = sumQtde;
	}
	
	public enum Fields {
		SEQ("seqPhi"),
		SOMATORIO("sumQtde"),
		;
		
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
