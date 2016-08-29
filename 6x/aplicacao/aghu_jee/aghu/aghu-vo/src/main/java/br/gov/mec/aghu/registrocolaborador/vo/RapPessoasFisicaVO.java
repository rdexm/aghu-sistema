package br.gov.mec.aghu.registrocolaborador.vo;



import java.io.Serializable;

/**
 * RapPessoasFisicaVO
 * @author felipe
 *
 */
public class RapPessoasFisicaVO implements Serializable {
	private static final long serialVersionUID = 8239218015158452478L;
	private String nome;
	private String situacao;
	private Integer matricula;
	private Short vinCodigo;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}


	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
}
