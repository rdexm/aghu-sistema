package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

public class AtendimentoPrescricaoPacienteVO implements Serializable {

	private static final long serialVersionUID = 5190360800364327025L;

	private Integer seqPte;
	
	private Integer seqAtd;
	
	private Boolean indPrcrImpressao;
	
	public enum Fields {

		SEQ_PTE("seqPte"), 
		SEQ_ATD("seqAtd"),
		IND_PRCR_IMPRESSAO("indPrcrImpressao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getSeqPte() {
		return seqPte;
	}

	public void setSeqPte(Integer seqPte) {
		this.seqPte = seqPte;
	}

	public Integer getSeqAtd() {
		return seqAtd;
	}

	public void setSeqAtd(Integer seqAtd) {
		this.seqAtd = seqAtd;
	}

	public Boolean getIndPrcrImpressao() {
		return indPrcrImpressao;
	}

	public void setIndPrcrImpressao(Boolean indPrcrImpressao) {
		this.indPrcrImpressao = indPrcrImpressao;
	}	
	
}
