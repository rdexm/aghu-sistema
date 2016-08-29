package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

public class RelatorioProcedAgendPorUnidCirurgicaVO {
	
	private Date data;
	private Short sciSeqp;
	private Date dthrInicio;
	private Date dthrFim;
	private String cirurgioes;
	private String descricaoProcedimento;
	private Integer crgSeq;
	
	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public Short getSciSeqp() {
		return sciSeqp;
	}

	public void setSciSeqp(Short sciSeqp) {
		this.sciSeqp = sciSeqp;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	
	public String getCirurgioes() {
		return cirurgioes;
	}

	public void setCirurgioes(String cirurgioes) {
		this.cirurgioes = cirurgioes;
	}

	public String getDescricaoProcedimento() {
		return descricaoProcedimento;
	}

	public void setDescricaoProcedimento(String descricaoProcedimento) {
		this.descricaoProcedimento = descricaoProcedimento;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}


	public enum Fields {
		DATA("data"),
		SCI_SEQP("sciSeqp"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		CIRURGIOES("cirurgioes"),
		DESCRICAO_PROCEDIMENTO("descricaoProcedimento"),
		CRG_SEQ("crgSeq");

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
