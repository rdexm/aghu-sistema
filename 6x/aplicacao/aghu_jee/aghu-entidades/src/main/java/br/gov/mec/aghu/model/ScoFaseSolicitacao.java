package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityNumero;


/**
 * The persistent class for the sco_fases_solicitacoes database table.
 * 
 */
@Entity
@Table(name="SCO_FASES_SOLICITACOES", schema = "AGH")
@SequenceGenerator(name="scoFscSq1", sequenceName="AGH.SCO_FSC_SQ1", allocationSize = 1)
public class ScoFaseSolicitacao extends BaseEntityNumero<Integer> implements Serializable {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -3160452320306299369L;
	private Integer numero;
	private Date dtExclusao;
	private ScoItemAutorizacaoForn itemAutorizacaoForn;
	private ScoAutorizacaoForn autorizacaoForn;
	private Boolean exclusao;
	private ScoItemLicitacao itemLicitacao;
	private DominioTipoFaseSolicitacao tipo;
	private Integer version;
	private ScoSolicitacaoServico solicitacaoServico;
	private ScoSolicitacaoDeCompra solicitacaoDeCompra;
	private Boolean geracaoAutomatica;
	private List<ScoFaseSolicitacao> fasesSolicitacao;	
	private List<ScoFaseSolicitacao> fasesSolicitacaoServico;
	private Integer iafNumero;
	private Integer iafAfnNumero;
	
	private List<SceItemRecebProvisorio> itensRecebimentoProvisorio;
//	private ScoItemAutorizacaoFornJn itemAutorizacaoFornJn;
//	private SceItemNotaRecebimento itemNotaRecebimento;

	private enum ScoFaseSolicitacaoExceptionCode implements BusinessExceptionCode {
		ITEM_LIC_E_AUT_FORN_INVALIDOS, SOL_COMPR_E_SOL_SERV_INVALIDOS
	}

	
    public ScoFaseSolicitacao() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "scoFscSq1")
	@Column(name = "NUMERO", unique = true, nullable = false, precision = 9, scale = 0)
    public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Column(name="DT_EXCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtExclusao() {
		return this.dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}


	@Column(name = "IND_EXCLUSAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExclusao() {
		return exclusao;
	}

	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "ITL_LCT_NUMERO", referencedColumnName = "LCT_NUMERO", nullable = true),
		@JoinColumn(name = "ITL_NUMERO", referencedColumnName = "NUMERO", nullable = true)})
	public ScoItemLicitacao getItemLicitacao() {
		return itemLicitacao;
	}

	public void setItemLicitacao(ScoItemLicitacao itemLicitacao) {
		this.itemLicitacao = itemLicitacao;
	}

	@Column(name = "TIPO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoFaseSolicitacao getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoFaseSolicitacao tipo) {
		this.tipo = tipo;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	//bi-directional many-to-one association to ScoSolicitacaoServico
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SLS_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	//bi-directional many-to-one association to ScoSolicitacaoServico
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SLC_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoDeCompra getSolicitacaoDeCompra() {
		return solicitacaoDeCompra;
	}

	public void setSolicitacaoDeCompra(ScoSolicitacaoDeCompra solicitacaoDeCompra) {
		this.solicitacaoDeCompra = solicitacaoDeCompra;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="IAF_AFN_NUMERO", referencedColumnName="AFN_NUMERO"),
		@JoinColumn(name="IAF_NUMERO", referencedColumnName="NUMERO")
		})
    public ScoItemAutorizacaoForn getItemAutorizacaoForn() {
		return itemAutorizacaoForn;
	}

	public void setItemAutorizacaoForn(ScoItemAutorizacaoForn itemAutorizacaoForn) {
		this.itemAutorizacaoForn = itemAutorizacaoForn;
	}
	
	@Column(name = "ind_geracao_automatica", length = 1, nullable = true)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getGeracaoAutomatica() {
		return geracaoAutomatica;
	}

	public void setGeracaoAutomatica(Boolean geracaoAutomatica) {
		this.geracaoAutomatica = geracaoAutomatica;
	}
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="SLC_NUMERO", referencedColumnName="SLC_NUMERO", insertable=false, updatable=false)
	public List<ScoFaseSolicitacao> getFasesSolicitacao() {
		return fasesSolicitacao;
	}

	public void setFasesSolicitacao(List<ScoFaseSolicitacao> fasesSolicitacao) {
		this.fasesSolicitacao = fasesSolicitacao;
	}
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name="SLS_NUMERO", referencedColumnName="SLS_NUMERO", insertable=false, updatable=false)
	public List<ScoFaseSolicitacao> getFasesSolicitacaoServico() {
		return fasesSolicitacaoServico;
	}
	
	@Column(name = "IAF_NUMERO", insertable = false, updatable = false)
	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setFasesSolicitacaoServico(List<ScoFaseSolicitacao> fasesSolicitacaoServico) {
		this.fasesSolicitacaoServico = fasesSolicitacaoServico;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}
	
	@Column(name = "IAF_AFN_NUMERO", insertable = false, updatable = false)
	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="IAF_AFN_NUMERO", referencedColumnName="NUMERO", insertable = false, updatable = false)
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	@PrePersist
	@PreUpdate
	protected void validacoes() {
       /* QMS$ENFORCE_ARC_2 */      
		if (!((this.getItemLicitacao() !=null && this.getItemAutorizacaoForn() == null) || (this.getItemLicitacao() == null && this.getItemAutorizacaoForn() != null))) {
			throw new BaseRuntimeException(
					ScoFaseSolicitacaoExceptionCode.ITEM_LIC_E_AUT_FORN_INVALIDOS);
		}
       /* QMS$ENFORCE_ARC_1 */
		if(!((this.getSolicitacaoDeCompra() != null && this.getSolicitacaoServico() == null) || (this.getSolicitacaoDeCompra() == null && this.getSolicitacaoServico() != null))) {
			throw new BaseRuntimeException(
					ScoFaseSolicitacaoExceptionCode.SOL_COMPR_E_SOL_SERV_INVALIDOS);			
		}
	}
	
	@OneToMany(mappedBy="scoFaseSolicitacao", fetch = FetchType.LAZY)
	public List<SceItemRecebProvisorio> getItensRecebimentoProvisorio() {
		return itensRecebimentoProvisorio;
	}

	public void setItensRecebimentoProvisorio(
			List<SceItemRecebProvisorio> itensRecebimentoProvisorio) {
		this.itensRecebimentoProvisorio = itensRecebimentoProvisorio;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoFaseSolicitacao other = (ScoFaseSolicitacao) obj;
		if (numero == null) {
			if (other.numero != null){
				return false;
			}
		} else if (!numero.equals(other.numero)){
			return false;
		}
		return true;
	}

	public enum Fields{
		NUMERO("numero"),
		DTEXCLUSAO("dtExclusao"),
		IND_EXCLUSAO("exclusao"),
		ITEM_LICITACAO("itemLicitacao"),
		LCT_NUMERO("itemLicitacao.id.lctNumero"),
		ITL_NUMERO("itemLicitacao.id.numero"),
		ITL_LCT_NUMERO("itemLicitacao"),
		LICITACAO_NRO("itemLicitacao.licitacao.numero"),
		SOLICITACAO_SERVICO("solicitacaoServico"),
		SOLICITACAO_COMPRAS("solicitacaoDeCompra"),
		SOLICITACAO_COMPRAS_MATERIAL("solicitacaoDeCompra.material.codigo"),
		SOLICITACAO_COMPRAS_GRUPO_MATERIAL("solicitacaoDeCompra.material.grupoMaterial.codigo"),
		SLC_NUMERO("solicitacaoDeCompra.numero"),
		SLS_NUMERO("solicitacaoServico.numero"),
		TIPO("tipo"),
		SCO_ITENS_AUTORIZACAO_FORN("itemAutorizacaoForn"),
		IAF_AFN_NUMERO("itemAutorizacaoForn.id.afnNumero"),
		IAF_NUMERO("itemAutorizacaoForn.id.numero"),
		FSL2("fasesSolicitacao"),
		FSL2_SERVICO("fasesSolicitacaoServico"),		AUTORIZACOES_FORN("itemAutorizacaoForn.autorizacoesForn"),
		AUTORIZACAO_FORN("autorizacaoForn"),
		ITEM_RECEBIMENTO_PROVISORIO("itensRecebimentoProvisorio"),
		IAFNUMERO("iafNumero"),
		IAFAFNNUMERO("iafAfnNumero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}