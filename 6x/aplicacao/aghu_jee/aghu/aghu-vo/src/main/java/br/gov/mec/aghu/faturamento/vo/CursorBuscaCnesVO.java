package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;


public class CursorBuscaCnesVO implements Serializable {


	private static final long serialVersionUID = 7821559430235109848L;
	
	private String codigo;
	private String servico;
	private String codigoClass;
	private String classificacao;
	private String servClass;
	private Short unfSeq;

	public enum Fields {
		CODIGO("codigo"),
		SERVICO("servico"),
		CODIGO_CLASS("codigoClass"),
		CLASSIFICACAO("classificacao"),
		SERV_CLASS("servClass"),
		UNF_SEQ("unfSeq")
		;
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getServico() {
		return servico;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public String getCodigoClass() {
		return codigoClass;
	}

	public void setCodigoClass(String codigoClass) {
		this.codigoClass = codigoClass;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public String getServClass() {
		return servClass;
	}

	public void setServClass(String servClass) {
		this.servClass = servClass;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
}