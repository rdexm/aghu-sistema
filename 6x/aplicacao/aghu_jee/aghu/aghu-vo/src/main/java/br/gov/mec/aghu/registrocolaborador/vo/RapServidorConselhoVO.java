package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * vo view RapServidorConselhoVO
 */
public class RapServidorConselhoVO implements Serializable {
	private static final long serialVersionUID = 8239218015158452478L;

	private String nome;
	private String nroRegConselho;
	private String sigla;
	private String situacao;
	private Date dtInicioVinculo;
	private Date dtFimVinculo;
	private Integer matricula;
	private Short vinCodigo;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNroRegConselho() {
		return nroRegConselho;
	}

	public void setNroRegConselho(String nroRegConselho) {
		this.nroRegConselho = nroRegConselho;
	}

	public Date getDtInicioVinculo() {
		return dtInicioVinculo;
	}

	public void setDtInicioVinculo(Date dtInicioVinculo) {
		this.dtInicioVinculo = dtInicioVinculo;
	}

	public Date getDtFimVinculo() {
		return dtFimVinculo;
	}

	public void setDtFimVinculo(Date dtFimVinculo) {
		this.dtFimVinculo = dtFimVinculo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
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
