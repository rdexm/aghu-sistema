package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;

public class DiaPrescricaoVO implements Serializable {

	private static final long serialVersionUID = 6570101611994355268L;
	
	private Short ciclo;
	private Short dia;
	private Short tempoAdministracao;
	private Integer seq;
	
	public enum Fields {

		CICLO("ciclo"), 
		DIA("dia"),
		SEQ("seq"),
		TEMPO_ADMINISTRATIVO("tempoAdministracao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Short getDia() {
		return dia;
	}

	public void setDia(Short dia) {
		this.dia = dia;
	}

	public Short getTempoAdministracao() {
		return tempoAdministracao;
	}

	public void setTempoAdministracao(Short tempoAdministracao) {
		this.tempoAdministracao = tempoAdministracao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}
}
