package br.gov.mec.aghu.compras.vo;

import java.util.Date;


public class ProcessoGeracaoAutomaticaVO {

	private Integer seqProcesso;
	private String dtGeracaoFormatada;
	private Date dtGeracao;

	
	public Integer getSeqProcesso() {
		return seqProcesso;
	}
	
	public void setSeqProcesso(Integer seqProcesso) {
		this.seqProcesso = seqProcesso;
	}
	
	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seqProcesso == null) ? 0 : seqProcesso.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ProcessoGeracaoAutomaticaVO other = (ProcessoGeracaoAutomaticaVO) obj;
		if (seqProcesso == null) {
			if (other.seqProcesso != null){
				return false;
			}
		} else if (!seqProcesso.equals(other.seqProcesso)){
			return false;
		}
		return true;
	}
	
	public String getDtGeracaoFormatada() {
		return dtGeracaoFormatada;
	}

	public void setDtGeracaoFormatada(String dtGeracaoFormatada) {
		this.dtGeracaoFormatada = dtGeracaoFormatada;
	}

	public enum Fields {
		DT_GERACAO_FORMATADA("dtGeracaoFormatada"), 
		DT_GERACAO("dtGeracao"),
		SEQ_PROCESSO("seqProcesso"); 

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
