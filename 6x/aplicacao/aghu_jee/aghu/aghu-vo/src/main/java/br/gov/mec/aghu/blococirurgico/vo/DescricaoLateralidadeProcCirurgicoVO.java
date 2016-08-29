package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.dominio.DominioLadoCirurgiaAgendas;

public class DescricaoLateralidadeProcCirurgicoVO {

	private Integer procSeq;
	private String descricaoProcedimento;
	private DominioLadoCirurgiaAgendas ladoCirurgia;
	
	public DescricaoLateralidadeProcCirurgicoVO() {}

	@SuppressWarnings("ucd")
	public DescricaoLateralidadeProcCirurgicoVO(Integer procSeq, String descricaoProcedimento, DominioLadoCirurgiaAgendas ladoCirurgia) {
		super();
		this.procSeq = procSeq;
		this.descricaoProcedimento = descricaoProcedimento;
		this.ladoCirurgia = ladoCirurgia;
	}

	public enum Fields {

		PROC_SEQ("procSeq"),
		DESCRICAO_PROC("descricaoProcedimento"),
		LADO_CIRURGIA("ladoCirurgia");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getProcSeq() {
		return procSeq;
	}

	public void setProcSeq(Integer procSeq) {
		this.procSeq = procSeq;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	public DominioLadoCirurgiaAgendas getLadoCirurgia() {
		return ladoCirurgia;
	}

	public void setLadoCirurgia(DominioLadoCirurgiaAgendas ladoCirurgia) {
		this.ladoCirurgia = ladoCirurgia;
	}
}