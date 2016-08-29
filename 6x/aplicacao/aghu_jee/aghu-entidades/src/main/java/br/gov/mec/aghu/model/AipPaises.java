package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntity;

@Entity
@Table(name = "AIP_PAISES", schema = "AGH")
public class AipPaises implements BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1841118687196873711L;

	private String sigla;

	private String nome;

	private Integer version;

	public AipPaises() {
	}

	public AipPaises(String sigla, String nome) {
		this.sigla = sigla;
		this.nome = nome;
	}

	@Id
	@Column(name = "SIGLA")
	@Length(max = 3)
	@Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Não é permitido caracteres especiais.")
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	@Column(name = "NOME", unique = true, nullable = false, length = 30)
	@Length(max = 30)
	public String getNome() {
		return this.nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public enum Fields {
		SIGLA("sigla"), NOME("nome");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sigla == null) ? 0 : sigla.hashCode());
		return result;
	}

	@Override
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
		AipPaises other = (AipPaises) obj;
		if (sigla == null) {
			if (other.sigla != null) {
				return false;
			}
		} else if (!sigla.equals(other.sigla)) {
			return false;
		}
		return true;
	}
}