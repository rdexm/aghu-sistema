package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SCO_SIASG_CARACTERISTICA", schema = "AGH")
public class ScoSiasgCaracteristica implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5846718301978317607L;

	private String itInStatus;
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Short itHoMovimento;
	private String itCoNetname;
	private String itNoCaracteristica;
	private String itCoCaracteristica;
	private Integer itDaMovimentoN8;
	private String itTxDescricaoCaract;
	private String itInStatusCaracteristica;

	public ScoSiasgCaracteristica(){
		
	}
	
	public ScoSiasgCaracteristica(String itInStatus, Long itCoCpfUsuario,
								Integer itDaMovimento, Short itHoMovimento, 
								String itCoNetname, String itNoCaracteristica, 
								String itCoCaracteristica, Integer itDaMovimentoN8, 
								String itTxDescricaoCaract, String itInStatusCaracteristica) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoNetname = itCoNetname;
		this.itNoCaracteristica = itNoCaracteristica;
		this.itCoCaracteristica = itCoCaracteristica;
		this.itDaMovimentoN8 = itDaMovimentoN8;
		this.itTxDescricaoCaract = itTxDescricaoCaract;
		this.itInStatusCaracteristica = itInStatusCaracteristica;
	}
	
	@Id
	@Column(name = "IT_CO_CARACTERISTICA", length = 4, nullable = false)
	public String getItCoCaracteristica() {
		return itCoCaracteristica;
	}

	public void setItCoCaracteristica(String itCoCaracteristica) {
		this.itCoCaracteristica = itCoCaracteristica;
	}

	@Column(name = "IT_IN_STATUS", length = 1)
	public String getItInStatus() {
		return itInStatus;
	}

	public void setItInStatus(String itInStatus) {
		this.itInStatus = itInStatus;
	}

	@Column(name = "IT_CO_CPF_USUARIO")
	public Long getItCoCpfUsuario() {
		return itCoCpfUsuario;
	}

	public void setItCoCpfUsuario(Long itCoCpfUsuario) {
		this.itCoCpfUsuario = itCoCpfUsuario;
	}

	@Column(name = "IT_DA_MOVIMENTO")
	public Integer getItDaMovimento() {
		return itDaMovimento;
	}

	public void setItDaMovimento(Integer itDaMovimento) {
		this.itDaMovimento = itDaMovimento;
	}

	@Column(name = "IT_HO_MOVIMENTO")
	public Short getItHoMovimento() {
		return itHoMovimento;
	}

	public void setItHoMovimento(Short itHoMovimento) {
		this.itHoMovimento = itHoMovimento;
	}

	@Column(name = "IT_CO_NETNAME", length = 8)
	public String getItCoNetname() {
		return itCoNetname;
	}

	public void setItCoNetname(String itCoNetname) {
		this.itCoNetname = itCoNetname;
	}

	@Column(name = "IT_NO_CARACTERISTICA", length = 40)
	public String getItNoCaracteristica() {
		return itNoCaracteristica;
	}

	public void setItNoCaracteristica(String itNoCaracteristica) {
		this.itNoCaracteristica = itNoCaracteristica;
	}

	@Column(name = "IT_DA_MOVIMENTO_N8")
	public Integer getItDaMovimentoN8() {
		return itDaMovimentoN8;
	}

	public void setItDaMovimentoN8(Integer itDaMovimentoN8) {
		this.itDaMovimentoN8 = itDaMovimentoN8;
	}

	@Column(name = "IT_TX_DESCRICAO_CARACT", length = 200)
	public String getItTxDescricaoCaract() {
		return itTxDescricaoCaract;
	}

	public void setItTxDescricaoCaract(String itTxDescricaoCaract) {
		this.itTxDescricaoCaract = itTxDescricaoCaract;
	}

	@Column(name = "IT_IN_STATUS_CARACTERISTICA", length = 1)
	public String getItInStatusCaracteristica() {
		return itInStatusCaracteristica;
	}

	public void setItInStatusCaracteristica(String itInStatusCaracteristica) {
		this.itInStatusCaracteristica = itInStatusCaracteristica;
	}
	
	public enum Fields {

		IT_IN_STATUS("itInStatus"),
		IT_CO_CPF_USUARIO("itCoCpfUsuario"),
		IT_DA_MOVIMENTO("itDaMovimento"),
		IT_HO_MOVIMENTO("itHoMovimento"),
		IT_CO_NETNAME("itCoNetname"),
		IT_NO_CARACTERISTICA("itNoCaracteristica"),
		IT_CO_CARACTERISTICA("itCoCaracteristica"),
		IT_DA_MOVIMENTO_N8("itDaMovimentoN8"),
		IT_TX_DESCRICAO_CARACT("itTxDescricaoCaract"),
		IT_IN_STATUS_CARACTERISTICA("itInStatusCaracteristica");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getItCoCaracteristica() == null) ? 0 : getItCoCaracteristica().hashCode());
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
		if (!(obj instanceof ScoSiasgCaracteristica)) {
			return false;
		}
		ScoSiasgCaracteristica other = (ScoSiasgCaracteristica) obj;
		if (getItCoCaracteristica() == null) {
			if (other.getItCoCaracteristica() != null) {
				return false;
			}
		} else if (!getItCoCaracteristica().equals(other.getItCoCaracteristica())) {
			return false;
		}
		return true;
	}

}
