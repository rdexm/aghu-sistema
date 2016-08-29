package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SCO_CATSER_GRUPO", schema = "AGH")
public class ScoCatserGrupo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 384902287891049339L;
	
	private String itInStatus;
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Integer itHoMovimento;
	private String itCoNetname;
	private String itCoGrupo;
	private String itNoGrupo;
	private String itTxNota;
	
	public ScoCatserGrupo() { }
	
	public ScoCatserGrupo(String itInStatus, Long itCoCpfUsuario,
							Integer itDaMovimento, Integer itHoMovimento, 
							String itCoNetname,	String itCoGrupo, 
							String itNoGrupo, String itTxNota) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoNetname = itCoNetname;
		this.itCoGrupo = itCoGrupo;
		this.itNoGrupo = itNoGrupo;
		this.itTxNota = itTxNota;
	}

	@Id
	@Column(name = "IT_CO_GRUPO", nullable = false, length = 3)
	public String getItCoGrupo() {
		return itCoGrupo;
	}

	public void setItCoGrupo(String itCoGrupo) {
		this.itCoGrupo = itCoGrupo;
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

	@Column(name = "IT_NO_GRUPO", length = 120)
	public String getItNoGrupo() {
		return itNoGrupo;
	}

	public void setItNoGrupo(String itNoGrupo) {
		this.itNoGrupo = itNoGrupo;
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
		IT_CO_GRUPO("itCoGrupo"),
		IT_NO_GRUPO("itNoGrupo"),
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
		result = prime * result + ((getItCoGrupo() == null) ? 0 : getItCoGrupo().hashCode());
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
		if (!(obj instanceof ScoCatserGrupo)) {
			return false;
		}
		ScoCatserGrupo other = (ScoCatserGrupo) obj;
		if (getItCoGrupo() == null) {
			if (other.getItCoGrupo() != null) {
				return false;
			}
		} else if (!getItCoGrupo().equals(other.getItCoGrupo())) {
			return false;
		}
		return true;
	}

}
