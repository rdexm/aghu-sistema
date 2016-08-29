package br.gov.mec.aghu.faturamento.vo;

public class CursorMaximasAtosMedicoAihTempVO implements java.io.Serializable {
	
	private static final long serialVersionUID = -8063344407447966826L;
	
	private Long iphCodSus;
	private Byte seqp;
	private Short quantidadeMaxima;
	
	public enum Fields {
		IPH_COD_SUS("iphCodSus"),
		SEQP("seqp"),
		QUANTIDADE_MAXIMA("quantidadeMaxima");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	

	@Override
	public String toString() {
		return "CursorMaximasAtosMedicoAihTempVO [iphCodSus=" + iphCodSus
				+ ", seqp=" + seqp + ", quantidadeMaxima=" + quantidadeMaxima
				+ "]";
	}	
	

	public Long getIphCodSus() {
		return iphCodSus;
	}

	public void setIphCodSus(Long iphCodSus) {
		this.iphCodSus = iphCodSus;
	}

	public Byte getSeqp() {
		return seqp;
	}

	public void setSeqp(Byte seqp) {
		this.seqp = seqp;
	}

	public Short getQuantidadeMaxima() {
		return quantidadeMaxima;
	}

	public void setQuantidadeMaxima(Short quantidadeMaxima) {
		this.quantidadeMaxima = quantidadeMaxima;
	}
}