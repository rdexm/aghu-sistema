package br.gov.mec.aghu.prescricaomedica.vo;


/**
 * 
 * @author feoliveira
 *
 */
public class PrescricaoNotificacaoTbVO  {
	private Integer seq;
	private Integer pmeSeq;
	private Integer pmeAtdSeq;
	private Boolean indConcluido;
	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Boolean getIndConcluido() {
		return indConcluido;
	}
	public void setIndConcluido(Boolean indConcluido) {
		this.indConcluido = indConcluido;
	}
	
	public Integer getPmeAtdSeq() {
		return pmeAtdSeq;
	}
	public void setPmeAtdSeq(Integer pmeAtdSeq) {
		this.pmeAtdSeq = pmeAtdSeq;
	}

	public Integer getPmeSeq() {
		return pmeSeq;
	}
	public void setPmeSeq(Integer pmeSeq) {
		this.pmeSeq = pmeSeq;
	}

	public enum Fields {
		SEQ("seq"),
		PME_SEQ("pmeSeq"),
		PME_ATD_SEQ("pmeAtdSeq"),
		IND_CONCLUIDO("indConcluido");
		
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
