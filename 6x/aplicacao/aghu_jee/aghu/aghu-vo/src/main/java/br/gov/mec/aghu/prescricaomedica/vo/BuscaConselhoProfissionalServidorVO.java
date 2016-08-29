package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;


public class BuscaConselhoProfissionalServidorVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3183208751864083776L;

	private String nome;
	
	private String cpf;
	
	private String siglaConselho;
	
	private String numeroRegistroConselho;
	
	private Integer matricula;
	
	private Short vinculo;
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getSiglaConselho() {
		return siglaConselho;
	}

	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = siglaConselho;
	}

	public String getNumeroRegistroConselho() {
		return numeroRegistroConselho;
	}

	public void setNumeroRegistroConselho(String numeroRegistroConselho) {
		this.numeroRegistroConselho = numeroRegistroConselho;
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

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
		result = prime * result
				+ ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime
				* result
				+ ((numeroRegistroConselho == null) ? 0
						: numeroRegistroConselho.hashCode());
		result = prime * result
				+ ((siglaConselho == null) ? 0 : siglaConselho.hashCode());
		result = prime * result + ((vinculo == null) ? 0 : vinculo.hashCode());
		return result;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BuscaConselhoProfissionalServidorVO other = (BuscaConselhoProfissionalServidorVO) obj;
		if (cpf == null) {
			if (other.cpf != null) {
				return false;
			}
		} else if (!cpf.equals(other.cpf)) {
			return false;
		}
		if (matricula == null) {
			if (other.matricula != null) {
				return false;
			}
		} else if (!matricula.equals(other.matricula)) {
			return false;
		}
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		if (numeroRegistroConselho == null) {
			if (other.numeroRegistroConselho != null) {
				return false;
			}
		} else if (!numeroRegistroConselho.equals(other.numeroRegistroConselho)) {
			return false;
		}
		if (siglaConselho == null) {
			if (other.siglaConselho != null) {
				return false;
			}
		} else if (!siglaConselho.equals(other.siglaConselho)) {
			return false;
		}
		if (vinculo == null) {
			if (other.vinculo != null) {
				return false;
			}
		} else if (!vinculo.equals(other.vinculo)) {
			return false;
		}
		return true;
	}
	
}
