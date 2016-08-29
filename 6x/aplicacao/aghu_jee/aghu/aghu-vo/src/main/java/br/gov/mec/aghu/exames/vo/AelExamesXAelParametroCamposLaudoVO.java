package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;

public class AelExamesXAelParametroCamposLaudoVO implements Serializable{

	private static final long serialVersionUID = 7194181372425487006L;
	
	private Integer seq;
	private String sigla;
	private String nome;
	private String descricao;
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public enum Fields {
		SEQ("seq"),
		SIGLA("sigla"),
		NOME("nome"),
		DESCRICAO("descricao");
		
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
