package br.gov.mec.aghu.faturamento.vo;

public class FatContasIntPacCirurgiasVO {
	
	private Integer pacCodigo;
	private Integer seqCirurgia;
	public enum Fields {
		
		PAC_CODIGO("pacCodigo"), 
		SEQ_CIRURGIA("seqCirurgia");	

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getSeqCirurgia() {
		return seqCirurgia;
	}

	public void setSeqCirurgia(Integer seqCirurgia) {
		this.seqCirurgia = seqCirurgia;
	}

}
