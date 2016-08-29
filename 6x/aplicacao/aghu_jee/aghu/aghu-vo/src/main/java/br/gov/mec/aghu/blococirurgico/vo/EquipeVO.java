package br.gov.mec.aghu.blococirurgico.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class EquipeVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8271043494549856958L;
	private Integer seq;
	private String equipe;
	private String nomePessoa;
	private String funcao;
	private Integer matricula;
	private Short vinculo;
	private String nome;

	public String getEquipe() {
		return equipe;
	}

	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}

	public String getNomePessoa() {
		return nomePessoa;
	}

	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}

	public String getFuncao() {
		return funcao;
	}

	public void setFuncao(String funcao) {
		this.funcao = funcao;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public enum Fields {
		SEQ("seq"), 
		EQUIPE("equipe"),
		NOME("nome"),
		MATRICULA("matricula"),
		VINCULO("vinculo");

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
