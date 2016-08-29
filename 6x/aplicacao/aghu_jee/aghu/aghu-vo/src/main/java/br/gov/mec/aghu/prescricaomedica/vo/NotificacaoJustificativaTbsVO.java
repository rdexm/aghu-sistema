package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class NotificacaoJustificativaTbsVO implements BaseBean {

	private static final long serialVersionUID = 7641644649323808601L;
	
	private Integer seq;
	private Boolean indConcluido;
	private Integer atdSeq;

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

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public enum Fields {
		SEQ("seq"),
		IND_CONCLUIDO("indConcluido"),
		ATD_SEQ("atdSeq");
		
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