package br.gov.mec.aghu.procedimentoterapeutico.vo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * VO de Ciclo de Protocolo para C6 em #44228 – Realizar Manutenção de
 * Agendamento de Sessão Terapêutica
 * 
 * @author aghu
 *
 */
public class ProtocoloCicloVO {

	private String descricao;
	private String titulo;

	public enum Fields {

		DESCRICAO("descricao"), TITULO("titulo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getDescricao());
		umHashCodeBuilder.append(this.getTitulo());
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
		if (!(obj instanceof ProtocoloCicloVO)) {
			return false;
		}
		ProtocoloCicloVO other = (ProtocoloCicloVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
		umEqualsBuilder.append(this.getTitulo(), other.getTitulo());
		return umEqualsBuilder.isEquals();
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
