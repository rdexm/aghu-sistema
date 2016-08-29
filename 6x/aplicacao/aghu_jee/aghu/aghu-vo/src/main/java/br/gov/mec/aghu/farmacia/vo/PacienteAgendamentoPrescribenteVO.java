package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;

public class PacienteAgendamentoPrescribenteVO implements Serializable {

	private static final long serialVersionUID = -8153500080107548062L;
	
	private Integer seq;
	private Integer prontuario;
	private Integer codigo;
	private String nome;
	
	public enum Fields {
		SEQ("seq"),
		PRONTURARIO("prontuario"),
		CODIGO("codigo"),
		NOME("nome"),
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}
