package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MbcEquipeVO implements Serializable {

	private static final long serialVersionUID = 4059717321128987045L;

	private Integer matricula;
	private Short vinCodigo;
	private String nome;

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getMatricula());
		umHashCodeBuilder.append(this.getVinCodigo());
		umHashCodeBuilder.append(this.getNome());
		return umHashCodeBuilder.toHashCode();
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
		MbcEquipeVO other = (MbcEquipeVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getMatricula(), other.getMatricula());
		umEqualsBuilder.append(this.getVinCodigo(), other.getVinCodigo());
		umEqualsBuilder.append(this.getNome(), other.getNome());
		return umEqualsBuilder.isEquals();
	}

	public enum Fields {
		MATRICULA("matricula"), VIN_CODIGO("vinCodigo"), NOME("nome");

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
