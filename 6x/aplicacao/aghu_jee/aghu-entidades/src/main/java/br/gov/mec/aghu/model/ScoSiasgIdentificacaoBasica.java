package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import br.gov.mec.aghu.core.persistence.BaseEntity;

@Entity
@Table(name = "SCO_SIASG_IDENTIFICACAO_BASICA", schema = "AGH")
public class ScoSiasgIdentificacaoBasica implements BaseEntity {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -776395262354314807L;

	private String itCoIdenBasica;
	private String itInStatus;	
	private Long itCoCpfUsuario;
	private Integer itDaMovimento;
	private Short itHoMovimento;
	private Short itCoClasse;
	private String itNoAlias;
	private String itNoBasico;
	private String itNoPalavraChave;
	private String itCnChave;
	private Integer itCoEstruturaDespesa;
	private Integer itCoUnidadeGestora;
	private Short itInSirep;
	private Integer itDaMovimentoN8;
	private Integer itCoConjuntoMateriais;
	private String itInStatusPdm;
	private String itCoFiig;
	private Short itNuUltimoSeqItem;
	private Long itNuCpfInclusao;
	private Integer itCoClassificacaoContabil;
	private Integer itCoCpv;
	private String itCoInc;
	
	public ScoSiasgIdentificacaoBasica(){ }
	
	public ScoSiasgIdentificacaoBasica(String itInStatus, Long itCoCpfUsuario,
									Integer itDaMovimento, Short itHoMovimento, String itCoIdenBasica,
									Short itCoClasse, String itNoAlias, String itNoBasico,
									String itNoPalavraChave, String itCnChave,
									Integer itCoEstruturaDespesa, Integer itCoUnidadeGestora,
									Short itInSirep, Integer itDaMovimentoN8,
									Integer itCoConjuntoMateriais, String itInStatusPdm, String itCoFiig,
									Short itNuUltimoSeqItem, Long itNuCpfInclusao,
									Integer itCoClassificacaoContabil, Integer itCoCpv, String itCoInc) {

		this.itInStatus = itInStatus;
		this.itCoCpfUsuario = itCoCpfUsuario;
		this.itDaMovimento = itDaMovimento;
		this.itHoMovimento = itHoMovimento;
		this.itCoIdenBasica = itCoIdenBasica;
		this.itCoClasse = itCoClasse;
		this.itNoAlias = itNoAlias;
		this.itNoBasico = itNoBasico;
		this.itNoPalavraChave = itNoPalavraChave;
		this.itCnChave = itCnChave;
		this.itCoEstruturaDespesa = itCoEstruturaDespesa;
		this.itCoUnidadeGestora = itCoUnidadeGestora;
		this.itInSirep = itInSirep;
		this.itDaMovimentoN8 = itDaMovimentoN8;
		this.itCoConjuntoMateriais = itCoConjuntoMateriais;
		this.itInStatusPdm = itInStatusPdm;
		this.itCoFiig = itCoFiig;
		this.itNuUltimoSeqItem = itNuUltimoSeqItem;
		this.itNuCpfInclusao = itNuCpfInclusao;
		this.itCoClassificacaoContabil = itCoClassificacaoContabil;
		this.itCoCpv = itCoCpv;
		this.itCoInc = itCoInc;
	}
	
	@Id
	@Column(name = "IT_CO_IDEN_BASICA", length = 5, nullable = false)
	public String getItCoIdenBasica() {
		return itCoIdenBasica;
	}

	public void setItCoIdenBasica(String itCoIdenBasica) {
		this.itCoIdenBasica = itCoIdenBasica;
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

	@Column(name = "IT_CO_CLASSE")
	public Short getItCoClasse() {
		return itCoClasse;
	}

	public void setItCoClasse(Short itCoClasse) {
		this.itCoClasse = itCoClasse;
	}

	@Column(name = "IT_NO_ALIAS", length = 25)
	public String getItNoAlias() {
		return itNoAlias;
	}

	public void setItNoAlias(String itNoAlias) {
		this.itNoAlias = itNoAlias;
	}

	@Column(name = "IT_NO_BASICO", length = 100)
	public String getItNoBasico() {
		return itNoBasico;
	}

	public void setItNoBasico(String itNoBasico) {
		this.itNoBasico = itNoBasico;
	}

	@Column(name = "IT_NO_PALAVRA_CHAVE", length = 25)
	public String getItNoPalavraChave() {
		return itNoPalavraChave;
	}

	public void setItNoPalavraChave(String itNoPalavraChave) {
		this.itNoPalavraChave = itNoPalavraChave;
	}

	@Column(name = "IT_CN_CHAVE", length = 104)
	public String getItCnChave() {
		return itCnChave;
	}

	public void setItCnChave(String itCnChave) {
		this.itCnChave = itCnChave;
	}

	@Column(name = "IT_CO_ESTRUTURA_DESPESA")
	public Integer getItCoEstruturaDespesa() {
		return itCoEstruturaDespesa;
	}

	public void setItCoEstruturaDespesa(Integer itCoEstruturaDespesa) {
		this.itCoEstruturaDespesa = itCoEstruturaDespesa;
	}

	@Column(name = "IT_CO_UNIDADE_GESTORA")
	public Integer getItCoUnidadeGestora() {
		return itCoUnidadeGestora;
	}

	public void setItCoUnidadeGestora(Integer itCoUnidadeGestora) {
		this.itCoUnidadeGestora = itCoUnidadeGestora;
	}

	@Column(name = "IT_IN_SIREP")
	public Short getItInSirep() {
		return itInSirep;
	}

	public void setItInSirep(Short itInSirep) {
		this.itInSirep = itInSirep;
	}

	@Column(name = "IT_DA_MOVIMENTO_N8")
	public Integer getItDaMovimentoN8() {
		return itDaMovimentoN8;
	}

	public void setItDaMovimentoN8(Integer itDaMovimentoN8) {
		this.itDaMovimentoN8 = itDaMovimentoN8;
	}

	@Column(name = "IT_CO_CONJUNTO_MATERIAIS")
	public Integer getItCoConjuntoMateriais() {
		return itCoConjuntoMateriais;
	}

	public void setItCoConjuntoMateriais(Integer itCoConjuntoMateriais) {
		this.itCoConjuntoMateriais = itCoConjuntoMateriais;
	}

	@Column(name = "IT_IN_STATUS_PDM", length = 1)
	public String getItInStatusPdm() {
		return itInStatusPdm;
	}

	public void setItInStatusPdm(String itInStatusPdm) {
		this.itInStatusPdm = itInStatusPdm;
	}

	@Column(name = "IT_CO_FIIG", length = 5)
	public String getItCoFiig() {
		return itCoFiig;
	}

	public void setItCoFiig(String itCoFiig) {
		this.itCoFiig = itCoFiig;
	}

	@Column(name = "IT_NU_ULTIMO_SEQ_ITEM")
	public Short getItNuUltimoSeqItem() {
		return itNuUltimoSeqItem;
	}

	public void setItNuUltimoSeqItem(Short itNuUltimoSeqItem) {
		this.itNuUltimoSeqItem = itNuUltimoSeqItem;
	}

	@Column(name = "IT_NU_CPF_INCLUSAO")
	public Long getItNuCpfInclusao() {
		return itNuCpfInclusao;
	}

	public void setItNuCpfInclusao(Long itNuCpfInclusao) {
		this.itNuCpfInclusao = itNuCpfInclusao;
	}

	@Column(name = "IT_CO_CLASSIFICACAO_CONTABIL")
	public Integer getItCoClassificacaoContabil() {
		return itCoClassificacaoContabil;
	}

	public void setItCoClassificacaoContabil(Integer itCoClassificacaoContabil) {
		this.itCoClassificacaoContabil = itCoClassificacaoContabil;
	}

	@Column(name = "IT_CO_CPV")
	public Integer getItCoCpv() {
		return itCoCpv;
	}

	public void setItCoCpv(Integer itCoCpv) {
		this.itCoCpv = itCoCpv;
	}

	@Column(name = "IT_CO_INC", length = 5)
	public String getItCoInc() {
		return itCoInc;
	}

	public void setItCoInc(String itCoInc) {
		this.itCoInc = itCoInc;
	}
	
	
	public enum Fields {

		IT_IN_STATUS("itInStatus"),
		IT_CO_CPF("itCoCpf"),
		IT_DA_MOVIMENTO("itDaMovimento"),
		IT_HO_MOVIMENTO("itHoMovimento"),
		IT_CO_IDEN_BASICA("itCoIdenBasica"),
		IT_CO_CLASSE("itCoClasse"),
		IT_NO_ALIAS("itNoAlias"),
		IT_NO_BASICO("itNoBasico"),
		IT_NO_PALAVRA_CHAVE("itNoPalavraChave"),
		IT_CN_CHAVE("itCnChave"),
		IT_CO_ESTRUTURA_DESPESA("itCoEstruturaDespesa"),
		IT_CO_UNIDADE_GESTORA("itCoUnidadeGestora"),
		IT_IN_SIREP("itInSirep"),
		IT_DA_MOVIMENTO_N8("itDaMovimentoN8"),
		IT_CO_CONJUNTO_MATERIAIS("itCoConjuntoMateriais"),
		IT_IN_STATUS_PDM("itInStatusPdm"),
		IT_CO_FIIG("itCoFiig"),
		IT_NU_ULTIMO_SEQ_ITEM("itNuUltimoSeqItem"),
		IT_NU_CPF_INCLUSAO("itNuCpfInclusao"),
		IT_CO_CLASSIFICACAO("itCoClassificacao"),
		IT_CO_CPV("itCoCpv"),
		IT_CO_INC("itCoInc");

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
		result = prime * result + ((getItCoIdenBasica() == null) ? 0 : getItCoIdenBasica().hashCode());
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
		if (!(obj instanceof ScoSiasgIdentificacaoBasica)) {
			return false;
		}
		ScoSiasgIdentificacaoBasica other = (ScoSiasgIdentificacaoBasica) obj;
		if (getItCoIdenBasica() == null) {
			if (other.getItCoIdenBasica() != null) {
				return false;
			}
		} else if (!getItCoIdenBasica().equals(other.getItCoIdenBasica())) {
			return false;
		}
		return true;
	}
	
}
