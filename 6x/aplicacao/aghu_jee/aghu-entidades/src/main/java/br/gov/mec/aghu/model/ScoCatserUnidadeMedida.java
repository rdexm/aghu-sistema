package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SCO_CATSER_UNIDADE_MEDIDA", schema = "AGH")
public class ScoCatserUnidadeMedida implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -450782849159255846L;
	
	private String itInStatus;
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Integer itHoMovimento;
	private String itCoNetname;
	private String itSgUnidadeMedida;
	private String itNoUnidadeMedida;
	private String itTxDescricaoUnidMedida;

	public ScoCatserUnidadeMedida() { }
	
	public ScoCatserUnidadeMedida(String itInStatus, Long itCoCpfUsuario,
									Integer itDaMovimento, Integer itHoMovimento, 
									String itCoNetname, String itSgUnidadeMedida, 
									String itNoUnidadeMedida, String itTxDescricaoUnidMedida) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoNetname = itCoNetname;
		this.itSgUnidadeMedida = itSgUnidadeMedida;
		this.itNoUnidadeMedida = itNoUnidadeMedida;
		this.itTxDescricaoUnidMedida = itTxDescricaoUnidMedida;
	}
	
	@Id
	@Column(name = "IT_SG_UNIDADE_MEDIDA", nullable = false, length = 7)
	public String getItSgUnidadeMedida() {
		return itSgUnidadeMedida;
	}

	public void setItSgUnidadeMedida(String itSgUnidadeMedida) {
		this.itSgUnidadeMedida = itSgUnidadeMedida;
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

	@Column(name = "IT_NO_UNIDADE_MEDIDA", length = 20)
	public String getItNoUnidadeMedida() {
		return itNoUnidadeMedida;
	}

	public void setItNoUnidadeMedida(String itNoUnidadeMedida) {
		this.itNoUnidadeMedida = itNoUnidadeMedida;
	}

	@Column(name = "IT_TX_DESCRICAO_UNID_MEDIDA", length = 120)
	public String getItTxDescricaoUnidMedida() {
		return itTxDescricaoUnidMedida;
	}

	public void setItTxDescricaoUnidMedida(String itTxDescricaoUnidMedida) {
		this.itTxDescricaoUnidMedida = itTxDescricaoUnidMedida;
	}
	
	public enum Fields{
		
		IT_IN_STATUS("itInStatus"),
		IT_CO_CPF_USUARIO("itCoCpfUsuario"),
		IT_DA_MOVIMENTO("itDaMovimento"),
		IT_HO_MOVIMENTO("itHoMovimento"),
		IT_CO_NETNAME("itCoNetname"),
		IT_SG_UNIDADE_MEDIDA("itSgUnidadeMedida"),
		IT_NO_UNIDADE_MEDIDA("itNoUnidadeMedida"),
		IT_TX_DESCRICAO_UNID_MEDIDA("itTxDescricaoUnidMedida");
		
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
		result = prime * result + ((getItSgUnidadeMedida() == null) ? 0 : getItSgUnidadeMedida().hashCode());
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
		if (!(obj instanceof ScoCatserUnidadeMedida)) {
			return false;
		}
		ScoCatserUnidadeMedida other = (ScoCatserUnidadeMedida) obj;
		if (getItSgUnidadeMedida() == null) {
			if (other.getItSgUnidadeMedida() != null) {
				return false;
			}
		} else if (!getItSgUnidadeMedida().equals(other.getItSgUnidadeMedida())) {
			return false;
		}
		return true;
	}

}
