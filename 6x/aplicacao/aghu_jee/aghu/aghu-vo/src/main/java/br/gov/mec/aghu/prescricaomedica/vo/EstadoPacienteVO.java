package br.gov.mec.aghu.prescricaomedica.vo;

public class EstadoPacienteVO {
	
	private Long esaSeq;
	private Integer tsaSeq;
	private String titulo;
	
	public Long getEsaSeq() {
		return esaSeq;
	}
	public void setEsaSeq(Long esaSeq) {
		this.esaSeq = esaSeq;
	}
	public Integer getTsaSeq() {
		return tsaSeq;
	}
	public void setTsaSeq(Integer tsaSeq) {
		this.tsaSeq = tsaSeq;
	}
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	
	
	
	public enum Fields {
		TSA_SEQ("tsaSeq"),
		TITULO("titulo"),
		ESA_SEQ("esaSeq");
		  
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		@Override
		public String toString() {
			return this.field;
		}
	}

}
