package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSexo;

@SuppressWarnings("ucd")
public class ConselhoProfissionalServidorVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1754312256064730989L;

	private String nome;

	private Long cpf;

	private String siglaConselho;

	private String numeroRegistroConselho;

	private DominioSexo sexo;

	private String tituloMasculino;

	private String tituloFeminino;
	
	private Short codigoConselho;
	
	public enum Fields {

		NOME("nome"),
		CPF("cpf"),
		SIGLA_CONSELHO("siglaConselho"),
		NUMERO_REG_CONSELHO("numeroRegistroConselho"),
		SEXO("sexo"),
		TITULO_MASCULINO("tituloMasculino"),
		TITULO_FEMININO("tituloFeminino"),
		CODIGO_CONSELHO("codigoConselho");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	public ConselhoProfissionalServidorVO() {
	}

	public ConselhoProfissionalServidorVO(String nome, Long cpf, String siglaConselho,
			String numeroRegistroConselho, DominioSexo sexo, String tituloMasculino,
			String tituloFeminino, Short codigoConselho) {
		this.nome = nome;
		this.cpf = cpf;
		this.siglaConselho = siglaConselho;
		this.numeroRegistroConselho = numeroRegistroConselho;
		this.sexo = sexo;
		this.tituloMasculino = tituloMasculino;
		this.tituloFeminino = tituloFeminino;
		this.codigoConselho = codigoConselho;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
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

	public DominioSexo getSexo() {
		return sexo;
	}

	public void setSexo(DominioSexo sexo) {
		this.sexo = sexo;
	}

	public String getTituloMasculino() {
		return tituloMasculino;
	}

	public void setTituloMasculino(String tituloMasculino) {
		this.tituloMasculino = tituloMasculino;
	}

	public String getTituloFeminino() {
		return tituloFeminino;
	}

	public void setTituloFeminino(String tituloFeminino) {
		this.tituloFeminino = tituloFeminino;
	}

	public Short getCodigoConselho() {
		return codigoConselho;
	}

	public void setCodigoConselho(Short codigoConselho) {
		this.codigoConselho = codigoConselho;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codigoConselho == null) ? 0 : codigoConselho.hashCode());
		result = prime * result + ((cpf == null) ? 0 : cpf.hashCode());
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
		result = prime
				* result
				+ ((numeroRegistroConselho == null) ? 0
						: numeroRegistroConselho.hashCode());
		result = prime * result + ((sexo == null) ? 0 : sexo.hashCode());
		result = prime * result
				+ ((siglaConselho == null) ? 0 : siglaConselho.hashCode());
		result = prime * result
				+ ((tituloFeminino == null) ? 0 : tituloFeminino.hashCode());
		result = prime * result
				+ ((tituloMasculino == null) ? 0 : tituloMasculino.hashCode());
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
		ConselhoProfissionalServidorVO other = (ConselhoProfissionalServidorVO) obj;
		if (codigoConselho == null) {
			if (other.codigoConselho != null) {
				return false;
			}
		} else if (!codigoConselho.equals(other.codigoConselho)) {
			return false;
		}
		if (cpf == null) {
			if (other.cpf != null) {
				return false;
			}
		} else if (!cpf.equals(other.cpf)) {
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
		if (sexo == null) {
			if (other.sexo != null) {
				return false;
			}
		} else if (!sexo.equals(other.sexo)) {
			return false;
		}
		if (siglaConselho == null) {
			if (other.siglaConselho != null) {
				return false;
			}
		} else if (!siglaConselho.equals(other.siglaConselho)) {
			return false;
		}
		if (tituloFeminino == null) {
			if (other.tituloFeminino != null) {
				return false;
			}
		} else if (!tituloFeminino.equals(other.tituloFeminino)) {
			return false;
		}
		if (tituloMasculino == null) {
			if (other.tituloMasculino != null) {
				return false;
			}
		} else if (!tituloMasculino.equals(other.tituloMasculino)) {
			return false;
		}
		return true;
	}
	
}
