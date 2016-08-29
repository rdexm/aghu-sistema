package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

@Entity
@Table(name="SCO_SIASG_GRUPO_SERVICO", schema = "AGH")
public class ScoSiasgGrupoServico implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6234976031971400484L;
	
	private String itInStatus;
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Integer itHoMovimento;
	private String itCoNetname;
	private Short itCoGrupoServico;
	private String itNoGrupoServico;
	private String itTxNotaGrupoServico;
	private Integer itDaMovimentoN8;
	
	public ScoSiasgGrupoServico() {	}

	public ScoSiasgGrupoServico(String itInStatus, Long itCoCpfUsuario,
								Integer itDaMovimento, Integer itHoMovimento, 
								String itCoNetname, Short itCoGrupoServico, 
								String itNoGrupoServico, String itTxNotaGrupoServico, Integer itDaMovimentoN8) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoNetname = itCoNetname;
		this.itCoGrupoServico = itCoGrupoServico;
		this.itNoGrupoServico = itNoGrupoServico;
		this.itTxNotaGrupoServico = itTxNotaGrupoServico;
		this.itDaMovimentoN8 = itDaMovimentoN8;
	}
	
	@Id
	@Column(name = "IT_CO_GRUPO_SERVICO", nullable = false, length = 2)
	public Short getItCoGrupoServico() {
		return itCoGrupoServico;
	}

	public void setItCoGrupoServico(Short itCoGrupoServico) {
		this.itCoGrupoServico = itCoGrupoServico;
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

	@Column(name = "IT_NO_GRUPO_SERVICO", length = 120)
	public String getItNoGrupoServico() {
		return itNoGrupoServico;
	}

	public void setItNoGrupoServico(String itNoGrupoServico) {
		this.itNoGrupoServico = itNoGrupoServico;
	}

	@Lob
	@Type(type="text")
	@Column(name = "IT_TX_NOTA_GRUPO_SERVICO", length = 13370)
	public String getItTxNotaGrupoServico() {
		return itTxNotaGrupoServico;
	}

	public void setItTxNotaGrupoServico(String itTxNotaGrupoServico) {
		this.itTxNotaGrupoServico = itTxNotaGrupoServico;
	}

	@Column(name = "IT_DA_MOVIMENTO_N8", length = 8)
	public Integer getItDaMovimentoN8() {
		return itDaMovimentoN8;
	}

	public void setItDaMovimentoN8(Integer itDaMovimentoN8) {
		this.itDaMovimentoN8 = itDaMovimentoN8;
	}
	
	public enum Fields{
		
		IT_IN_STATUS("itInStatus"),
		IT_CO_CPF_USUARIO("itCoCpfUsuario"),
		IT_DA_MOVIMENTO("itDaMovimento"),
		IT_HO_MOVIMENTO("itHoMovimento"),
		IT_CO_NETNAME("itCoNetname"),
		IT_CO_GRUPO_SERVICO("itCoGrupoServico"),
		IT_NO_GRUPO_SERVICO("itNoGrupoServico"),
		IT_TX_NOTA_GRUPO_SERVICO("itTxNotaGrupoServico"),
		IT_DA_MOVIMENTO_N8("itDaMovimentoN8");
		
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
		result = prime * result + ((getItCoGrupoServico() == null) ? 0 : getItCoGrupoServico().hashCode());
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
		if (!(obj instanceof ScoSiasgGrupoServico)) {
			return false;
		}
		ScoSiasgGrupoServico other = (ScoSiasgGrupoServico) obj;
		if (getItCoGrupoServico() == null) {
			if (other.getItCoGrupoServico() != null) {
				return false;
			}
		} else if (!getItCoGrupoServico().equals(other.getItCoGrupoServico())) {
			return false;
		}
		return true;
	}

}
