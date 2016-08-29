package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SCO_CATSER_DIVISAO", schema = "AGH")
public class ScoCatserDivisao implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2712465098305659981L;
	
	private String itInStatus;
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Integer itHoMovimento;
	private String itCoNetname;
	private String itCoDivisao;
	private String itNoDivisao;
	private String itTxNota;

	public ScoCatserDivisao() {	}
	
	public ScoCatserDivisao(String itInStatus, Long itCoCpfUsuario,
							Integer itDaMovimento, Integer itHoMovimento, 
							String itCoNetname, String itCoDivisao, 
							String itNoDivisao, String itTxNota) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoNetname = itCoNetname;
		this.itCoDivisao = itCoDivisao;
		this.itNoDivisao = itNoDivisao;
		this.itTxNota = itTxNota;
	}

	@Id
	@Column(name = "IT_CO_DIVISAO", nullable = false, length = 2)
	public String getItCoDivisao() {
		return itCoDivisao;
	}

	public void setItCoDivisao(String itCoDivisao) {
		this.itCoDivisao = itCoDivisao;
	}

	@Column(name = "IT_IN_STATUS", length = 1)
	public String getItInStatus() {
		return itInStatus;
	}

	public void setItInStatus(String itInStatus) {
		this.itInStatus = itInStatus;
	}

	@Column(name = "IT_CO_CPF_USUARIO", length = 11)
	public Long getItCoCpfUsuario() {
		return itCoCpfUsuario;
	}

	public void setItCoCpfUsuario(Long itCoCpfUsuario) {
		this.itCoCpfUsuario = itCoCpfUsuario;
	}

	@Column(name = "IT_DA_MOVIMENTO", length = 8)
	public Integer getItDaMovimento() {
		return itDaMovimento;
	}

	public void setItDaMovimento(Integer itDaMovimento) {
		this.itDaMovimento = itDaMovimento;
	}

	@Column(name = "IT_HO_MOVIMENTO", length = 6)
	public Integer getItHoMovimento() {
		return itHoMovimento;
	}

	public void setItHoMovimento(Integer itHoMovimento) {
		this.itHoMovimento = itHoMovimento;
	}

	@Column(name = "IT_CO_NETNAME", length = 8)
	public String getItCoNetname() {
		return itCoNetname;
	}

	public void setItCoNetname(String itCoNetname) {
		this.itCoNetname = itCoNetname;
	}

	@Column(name = "IT_NO_DIVISAO", length = 120)
	public String getItNoDivisao() {
		return itNoDivisao;
	}

	public void setItNoDivisao(String itNoDivisao) {
		this.itNoDivisao = itNoDivisao;
	}

	@Column(name = "IT_TX_NOTA", length = 500)
	public String getItTxNota() {
		return itTxNota;
	}

	public void setItTxNota(String itTxNota) {
		this.itTxNota = itTxNota;
	}
	
	public enum Fields{
		
		IT_IN_STATUS("itInStatus"),
		IT_CO_CPF_USUARIO("itCoCpfUsuario"),
		IT_DA_MOVIMENTO("itDaMovimento"),
		IT_HO_MOVIMENTO("itHoMovimento"),
		IT_CO_NETNAME("itCoNetname"),
		IT_CO_DIVISAO("itCoDivisao"),
		IT_NO_DIVISAO("itNoDivisao"),
		IT_TX_NOTA("itTxNota");
		
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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getItCoDivisao() == null) ? 0 : getItCoDivisao().hashCode());
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
		if (!(obj instanceof ScoCatserDivisao)) {
			return false;
		}
		ScoCatserDivisao other = (ScoCatserDivisao) obj;
		if (getItCoDivisao() == null) {
			if (other.getItCoDivisao() != null) {
				return false;
			}
		} else if (!getItCoDivisao().equals(other.getItCoDivisao())) {
			return false;
		}
		return true;
	}

}
