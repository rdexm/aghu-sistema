package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SCO_CATSER_CLASSE", schema = "AGH")
public class ScoCatserClasse implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7748440631908732864L;
	
	private String itInStatus;
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Integer itHoMovimento;
	private String itCoNetname;
	private String itCoClasse;
	private String itNoClasse;
	private String itTxNota;

	public ScoCatserClasse() {	}
	
	public ScoCatserClasse(String itInStatus, Long itCoCpfUsuario,
							Integer itDaMovimento, Integer itHoMovimento, 
							String itCoNetname, String itCoClasse, 
							String itNoClasse, String itTxNota) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoNetname = itCoNetname;
		this.itCoClasse = itCoClasse;
		this.itNoClasse = itNoClasse;
		this.itTxNota = itTxNota;
	}

	@Id
	@Column(name = "IT_CO_CLASSE", nullable = false, length = 4)
	public String getItCoClasse() {
		return itCoClasse;
	}

	public void setItCoClasse(String itCoClasse) {
		this.itCoClasse = itCoClasse;
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

	@Column(name = "IT_NO_CLASSE", length = 120)
	public String getItNoClasse() {
		return itNoClasse;
	}

	public void setItNoClasse(String itNoClasse) {
		this.itNoClasse = itNoClasse;
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
		IT_CO_CLASSE("itCoClasse"),
		IT_NO_CLASSE("itNoClasse"),
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
		result = prime * result + ((getItCoClasse() == null) ? 0 : getItCoClasse().hashCode());
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
		if (!(obj instanceof ScoCatserClasse)) {
			return false;
		}
		ScoCatserClasse other = (ScoCatserClasse) obj;
		if (getItCoClasse() == null) {
			if (other.getItCoClasse() != null) {
				return false;
			}
		} else if (!getItCoClasse().equals(other.getItCoClasse())) {
			return false;
		}
		return true;
	}

}
