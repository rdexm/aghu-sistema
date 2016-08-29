package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class AacGradeAgendamenConsultasVO implements Serializable {
	
	private static final long serialVersionUID = 2733282867979584975L;
	
	private Short preSerVinCodigo;
	private Integer preSerMatricula;
	private Short espSeq;
	private String nomeEspecialidade;
	private String nome;
	
	public String obterEspecialidadeSubString(){
		if(nomeEspecialidade != null){
			return nomeEspecialidade.substring(1, 25);
		}else{
			return null;
		}
	}
	
	public String obterEquipeSubString(){
		if(nome != null){
			return nome.substring(1, 20);
		}else{
			return null;
		}
	}
	
	public enum Fields {
		PRE_SER_VIN_CODIGO("preSerVinCodigo"),
		PRE_SER_MATRICULA("preSerMatricula"),
		ESP_SEQ("espSeq"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		NOME("nome")
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public Short getPreSerVinCodigo() {
		return preSerVinCodigo;
	}


	public void setPreSerVinCodigo(Short preSerVinCodigo) {
		this.preSerVinCodigo = preSerVinCodigo;
	}


	public Integer getPreSerMatricula() {
		return preSerMatricula;
	}


	public void setPreSerMatricula(Integer preSerMatricula) {
		this.preSerMatricula = preSerMatricula;
	}


	public Short getEspSeq() {
		return espSeq;
	}


	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}


	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}


	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
}