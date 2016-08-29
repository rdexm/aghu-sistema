package br.gov.mec.aghu.model;

import br.gov.mec.aghu.core.persistence.BaseEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="SCO_SIASG_SERVICO", schema = "AGH")
public class ScoSiasgServico  implements BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3025188248000147946L;
	
	private String itInStatus;
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Integer itHoMovimento;
	private String itCoNetname;
	private Integer itCoServico;
	private Short itCoGrupoServico;
	private String itNoServico;
	private String itCnChave;
	private String itNoAliasServico;
	private String itNoPalavraChave;
	private String itTxServico;
	private String itInTipoServico;
	private String itNoServicoAcent;
	private String itCoEstruturaDespesa;
	private String itCoCpc;
	private String itSgUnidadeMedidaServico;
	private String itInComplemento;
	private String itCoClassificacaoContabil;
	private String itCoCpv;

	public ScoSiasgServico() {

	}

	public ScoSiasgServico(String itInStatus, Long itCoCpfUsuario,
							Integer itDaMovimento, Integer itHoMovimento, 
							String itCoNetname, Integer itCoServico, 
							Short itCoGrupoServico, String itNoServico,
							String itCnChave, String itNoAliasServico, 
							String itNoPalavraChave, String itTxServico, 
							String itInTipoServico, String itNoServicoAcent, 
							String itCoEstruturaDespesa, String itCoCpc, 
							String itSgUnidadeMedidaServico, String itInComplemento, 
							String itCoClassificacaoContabil, String itCoCpv) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoNetname = itCoNetname;
		this.itCoServico = itCoServico;
		this.itCoGrupoServico = itCoGrupoServico;
		this.itNoServico = itNoServico;
		this.itCnChave = itCnChave;
		this.itNoAliasServico = itNoAliasServico;
		this.itNoPalavraChave = itNoPalavraChave;
		this.itTxServico = itTxServico;
		this.itInTipoServico = itInTipoServico;
		this.itNoServicoAcent = itNoServicoAcent;
		this.itCoEstruturaDespesa = itCoEstruturaDespesa;
		this.itCoCpc = itCoCpc;
		this.itSgUnidadeMedidaServico = itSgUnidadeMedidaServico;
		this.itInComplemento = itInComplemento;
		this.itCoClassificacaoContabil = itCoClassificacaoContabil;
		this.itCoCpv = itCoCpv;
	}


	
	@Id
	@Column(name = "IT_CO_SERVICO", nullable = false, length = 9)
	public Integer getItCoServico() {
		return itCoServico;
	}

	public void setItCoServico(Integer itCoServico) {
		this.itCoServico = itCoServico;
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

	@Column(name = "IT_CO_GRUPO_SERVICO", length = 2)
	public Short getItCoGrupoServico() {
		return itCoGrupoServico;
	}

	public void setItCoGrupoServico(Short itCoGrupoServico) {
		this.itCoGrupoServico = itCoGrupoServico;
	}

	@Column(name = "IT_NO_SERVICO", length = 120)
	public String getItNoServico() {
		return itNoServico;
	}

	public void setItNoServico(String itNoServico) {
		this.itNoServico = itNoServico;
	}

	@Column(name = "IT_CN_CHAVE", length = 127)
	public String getItCnChave() {
		return itCnChave;
	}

	public void setItCnChave(String itCnChave) {
		this.itCnChave = itCnChave;
	}

	@Column(name = "IT_NO_ALIAS_SERVICO", length = 125)
	public String getItNoAliasServico() {
		return itNoAliasServico;
	}

	public void setItNoAliasServico(String itNoAliasServico) {
		this.itNoAliasServico = itNoAliasServico;
	}

	@Column(name = "IT_NO_PALAVRA_CHAVE", length = 425)
	public String getItNoPalavraChave() {
		return itNoPalavraChave;
	}

	public void setItNoPalavraChave(String itNoPalavraChave) {
		this.itNoPalavraChave = itNoPalavraChave;
	}

	@Column(name = "IT_TX_SERVICO", length = 750)
	public String getItTxServico() {
		return itTxServico;
	}

	public void setItTxServico(String itTxServico) {
		this.itTxServico = itTxServico;
	}

	@Column(name = "IT_IN_TIPO_SERVICO", length = 1)
	public String getItInTipoServico() {
		return itInTipoServico;
	}

	public void setItInTipoServico(String itInTipoServico) {
		this.itInTipoServico = itInTipoServico;
	}

	@Column(name = "IT_NO_SERVICO_ACENT", length = 120)
	public String getItNoServicoAcent() {
		return itNoServicoAcent;
	}

	public void setItNoServicoAcent(String itNoServicoAcent) {
		this.itNoServicoAcent = itNoServicoAcent;
	}

	@Column(name = "IT_CO_ESTRUTURA_DESPESA", length = 8)
	public String getItCoEstruturaDespesa() {
		return itCoEstruturaDespesa;
	}

	public void setItCoEstruturaDespesa(String itCoEstruturaDespesa) {
		this.itCoEstruturaDespesa = itCoEstruturaDespesa;
	}

	@Column(name = "IT_CO_CPC", length = 7)
	public String getItCoCpc() {
		return itCoCpc;
	}

	public void setItCoCpc(String itCoCpc) {
		this.itCoCpc = itCoCpc;
	}

	@Column(name = "IT_SG_UNIDADE_MEDIDA_SERVICO", length = 70)
	public String getItSgUnidadeMedidaServico() {
		return itSgUnidadeMedidaServico;
	}

	public void setItSgUnidadeMedidaServico(String itSgUnidadeMedidaServico) {
		this.itSgUnidadeMedidaServico = itSgUnidadeMedidaServico;
	}

	@Column(name = "IT_IN_COMPLEMENTO", length = 1)
	public String getItInComplemento() {
		return itInComplemento;
	}

	public void setItInComplemento(String itInComplemento) {
		this.itInComplemento = itInComplemento;
	}

	@Column(name = "IT_CO_CLASSIFICACAO_CONTABIL", length = 120)
	public String getItCoClassificacaoContabil() {
		return itCoClassificacaoContabil;
	}

	public void setItCoClassificacaoContabil(String itCoClassificacaoContabil) {
		this.itCoClassificacaoContabil = itCoClassificacaoContabil;
	}

	@Column(name = "IT_CO_CPV", length = 9)
	public String getItCoCpv() {
		return itCoCpv;
	}

	public void setItCoCpv(String itCoCpv) {
		this.itCoCpv = itCoCpv;
	}
	
	public enum Fields{
		
		IT_IN_STATUS("itInStatus"),
		IT_CO_CPF_USUARIO("itCoCpfUsuario"),
		IT_DA_MOVIMENTO("itDaMovimento"),
		IT_HO_MOVIMENTO("itHoMovimento"),
		IT_CO_NETNAME("itCoNetname"),
		IT_CO_SERVICO("itCoServico"),
		IT_CO_GRUPO_SERVICO("itCoGrupoServico"),
		IT_NO_SERVICO("itNoServico"),
		IT_CN_CHAVE("itCnChave"),
		IT_NO_ALIAS_SERVICO("itNoAliasServico"),
		IT_NO_PALAVRA_CHAVE("itNoPalavraChave"),
		IT_TX_SERVICO("itTxServico"),
		IT_IN_TIPO_SERVICO("itInTipoServico"),
		IT_NO_SERVICO_ACENT("itNoServicoAcent"),
		IT_CO_ESTRUTURA_DESPESA("itCoEstruturaDespesa"),
		IT_CO_CPC("itCoCpc"),
		IT_SG_UNIDADE_MEDIDA_SERVICO("itSgUnidadeMedidaServico"),
		IT_IN_COMPLEMENTO("itInComplemento"),
		IT_CO_CLASSIFICACAO_CONTABIL("itCoClassificacaoContabil"),
		IT_CO_CPV("itCoCpv");
		
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
		result = prime * result + ((getItCoServico() == null) ? 0 : getItCoServico().hashCode());
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
		if (!(obj instanceof ScoSiasgServico)) {
			return false;
		}
		ScoSiasgServico other = (ScoSiasgServico) obj;
		if (getItCoServico() == null) {
			if (other.getItCoServico() != null) {
				return false;
			}
		} else if (!getItCoServico().equals(other.getItCoServico())) {
			return false;
		}
		return true;
	}

	

}