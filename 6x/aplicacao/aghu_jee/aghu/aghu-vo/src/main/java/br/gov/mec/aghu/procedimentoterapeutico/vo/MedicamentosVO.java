package br.gov.mec.aghu.procedimentoterapeutico.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class MedicamentosVO implements BaseBean {
			
	private static final long serialVersionUID = -5390709362184204302L;
	
	private String medDescricao;
	private Integer medMatCodigo;
	private Integer medUmmSeq;
	private Boolean medIndExigeObservacao;
	private String ummDescricao;
	
	
	public String getMedDescricao() {
		return medDescricao;
	}
	public void setMedDescricao(String medDescricao) {
		this.medDescricao = medDescricao;
	}
	public Integer getMedMatCodigo() {
		return medMatCodigo;
	}
	public void setMedMatCodigo(Integer medMatCodigo) {
		this.medMatCodigo = medMatCodigo;
	}
	public Integer getMedUmmSeq() {
		return medUmmSeq;
	}
	public void setMedUmmSeq(Integer medUmmSeq) {
		this.medUmmSeq = medUmmSeq;
	}
	public Boolean getMedIndExigeObservacao() {
		return medIndExigeObservacao;
	}
	public void setMedIndExigeObservacao(Boolean medIndExigeObservacao) {
		this.medIndExigeObservacao = medIndExigeObservacao;
	}
	public String getUmmDescricao() {
		return ummDescricao;
	}
	public void setUmmDescricao(String ummDescricao) {
		this.ummDescricao = ummDescricao;
	}
	

	public enum Fields {
		
		MEDICAMENTO_UMM_SEQ("medUmmSeq"),
		MEDICAMENTO_DESCRICAO("medDescricao"),
		IND_EXIGE_OBSERVACAO("medIndExigeObservacao"),
		MED_MAT_CODIGO("medMatCodigo"),
		UMM_DESCRICAO("ummDescricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
}
