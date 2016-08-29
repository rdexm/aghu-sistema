package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class CentralMensagemVO implements Serializable {

	private static final long serialVersionUID = 8822268768151940406L;

	private String mensagem;
	
	private Object entidade;
	
	private Boolean excluir;
	
	private Boolean represcrito;

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public Object getEntidade() {
		return entidade;
	}

	public void setEntidade(Object entidade) {
		this.entidade = entidade;
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(mensagem).toHashCode();
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

		CentralMensagemVO other = (CentralMensagemVO) obj;

		return new EqualsBuilder().append(mensagem, other.mensagem).isEquals();
	}

	public Boolean getExcluir() {
		return excluir;
	}

	public void setExcluir(Boolean excluir) {
		this.excluir = excluir;
	}

	public Boolean getReprescrito() {
		return represcrito;
	}

	public void setReprescrito(Boolean represcrito) {
		this.represcrito = represcrito;
	}

}
