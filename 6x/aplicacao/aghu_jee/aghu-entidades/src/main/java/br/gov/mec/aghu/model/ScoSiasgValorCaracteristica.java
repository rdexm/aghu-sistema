package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "SCO_SIASG_VALOR_CARACTERISTICA", schema = "AGH")
public class ScoSiasgValorCaracteristica implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4416245626582364630L;

	private String itInStatus;
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Short itHoMovimento;
	private String itCoNetname;
	private String itCoValorCaract;
	private String itNoValorCaract;
	private String itTxDescricaoValorCaract;
	private String itInStatusValorCaract;
	private String itTxComplementoValorCaract;
	
	
	public ScoSiasgValorCaracteristica(){
		
	}
	
	public ScoSiasgValorCaracteristica(String itInStatus, Long itCoCpfUsuario,
									Integer itDaMovimento, Short itHoMovimento, 
									String itCoNetname, String itCoValorCaract, 
									String itNoValorCaract, String itTxDescricaoValorCaract, 
									String itInStatusValorCaract, String itTxComplementoValorCaract) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoNetname = itCoNetname;
		this.itCoValorCaract = itCoValorCaract;
		this.itNoValorCaract = itNoValorCaract;
		this.itTxDescricaoValorCaract = itTxDescricaoValorCaract;
		this.itInStatusValorCaract = itInStatusValorCaract;
		this.itTxComplementoValorCaract = itTxComplementoValorCaract;
	}
	
	@Id
	@Column(name = "IT_CO_VALOR_CARACT", length = 6, nullable = false)
	public String getItCoValorCaract() {
		return itCoValorCaract;
	}

	public void setItCoValorCaract(String itCoValorCaract) {
		this.itCoValorCaract = itCoValorCaract;
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

	@Column(name = "IT_NO_VALOR_CARACT", length = 50)
	public String getItNoValorCaract() {
		return itNoValorCaract;
	}

	public void setItNoValorCaract(String itNoValorCaract) {
		this.itNoValorCaract = itNoValorCaract;
	}

	@Column(name = "IT_TX_DESCRICAO_VALOR_CARACT", length = 200)
	public String getItTxDescricaoValorCaract() {
		return itTxDescricaoValorCaract;
	}

	public void setItTxDescricaoValorCaract(String itTxDescricaoValorCaract) {
		this.itTxDescricaoValorCaract = itTxDescricaoValorCaract;
	}

	@Column(name = "IT_IN_STATUS_VALOR_CARACT", length = 1)
	public String getItInStatusValorCaract() {
		return itInStatusValorCaract;
	}

	public void setItInStatusValorCaract(String itInStatusValorCaract) {
		this.itInStatusValorCaract = itInStatusValorCaract;
	}

	@Column(name = "IT_TX_COMPLEMENTO_VALOR_CARACT", length = 78)
	public String getItTxComplementoValorCaract() {
		return itTxComplementoValorCaract;
	}

	public void setItTxComplementoValorCaract(String itTxComplementoValorCaract) {
		this.itTxComplementoValorCaract = itTxComplementoValorCaract;
	}

	public enum Fields {

		IT_IN_STATUS("itInStatus"),
		IT_CO_CPF_USUARIO("itCoCpfUsuario"),
		IT_DA_MOVIMENTO("itDaMovimento"),
		IT_HO_MOVIMENTO("itHoMovimento"),
		IT_CO_NETNAME("itCoNetname"),
		IT_CO_VALOR_CARACT("itCoValorCaract"),
		IT_NO_VALOR_CARACT("itNoValorCaract"),
		IT_TX_DESCRICAO_VALOR_CARACT("itTxDescricaoValorCaract"),
		IT_IN_STATUS_VALOR_CARACT("itInStatusValorCaract"),
		IT_TX_COMPLEMENTO_VALOR_CARACT("itTxComplementoValorCaract");

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
		result = prime * result + ((getItCoValorCaract() == null) ? 0 : getItCoValorCaract().hashCode());
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
		if (!(obj instanceof ScoSiasgValorCaracteristica)) {
			return false;
		}
		ScoSiasgValorCaracteristica other = (ScoSiasgValorCaracteristica) obj;
		if (getItCoValorCaract() == null) {
			if (other.getItCoValorCaract() != null) {
				return false;
			}
		} else if (!getItCoValorCaract().equals(other.getItCoValorCaract())) {
			return false;
		}
		return true;
	}
	
}
