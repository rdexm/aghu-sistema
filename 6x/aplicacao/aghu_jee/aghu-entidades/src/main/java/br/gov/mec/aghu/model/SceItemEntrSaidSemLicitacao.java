package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the SCE_ITEM_ESL database table.
 * 
 */
@Entity
@SequenceGenerator(name="sceIslSq1", sequenceName="AGH.SCE_ISL_SQ1", allocationSize = 1)
@Table(name = "SCE_ITEM_ESL", schema = "AGH")
public class SceItemEntrSaidSemLicitacao extends BaseEntitySeq<Integer> implements Serializable {
	private static final long serialVersionUID = -3217759651866389603L;

	private Integer seq;
	private SceEntrSaidSemLicitacao sceEntrSaidSemLicitacao;
	private ScoMaterial scoMaterial;
	private ScoUnidadeMedida scoUnidadeMedida;
	private String umdCodigo;
	private ScoItemAutorizacaoForn itemAutorizacaoForn;
	private Integer iafAfnNumero;
	private ScoMarcaComercial scoMarcaComercial;
	private ScoNomeComercial scoNomeComercial;
	private ScoSolicitacaoDeCompra scoSolicitacoesCompras;
	private ScoSolicitacaoServico scoSolicitacoesServico;
	private Integer quantidade;
	private Double valor;
	private Integer qtdeConsumida;
	private Integer qtdeDevolvida;
	private Boolean indEncerrado;
	private Date dtEncerramento;
	private RapServidores servidorEncerrado;
	private Boolean indEstorno;
	private Date dtEstorno;
	private RapServidores servidorEstornado;
	private SceItemBoc sceItemBoc;
	private SceItemRmps sceItemRmps;
	private Integer nroPatrimonio;
	private Integer nroControleFisico;
	private String observacao;
	private String dadosComplementares;
	private Integer version;
	private SceAlmoxarifado almoxarifados;
	private Integer islSeqOrigem;
	private Integer islEslSeqOrigem;
	private Integer eslSeq;
	private Integer slcNumero;
	private SceItemEntrSaidSemLicitacao sceItemEntrSaidSemLicitacao;
	private SceEntrSaidSemLicitacao sceEntrSaidSemLicitacaoOrigem;
	private SceMovimentoMaterial sceMovimentoMaterial;

	public SceItemEntrSaidSemLicitacao() {
		
	} 
	
	public SceItemEntrSaidSemLicitacao(Integer nroControleFisico, Integer patrimonio){
		this.nroControleFisico =  nroControleFisico;		
		this.nroPatrimonio = patrimonio;
	} 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceIslSq1")
	@Column(name = "SEQ", unique = true, nullable = false, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALM_SEQ", referencedColumnName = "SEQ", nullable = false)
	public SceAlmoxarifado getAlmoxarifados() {
		return this.almoxarifados;
	}

	public void setAlmoxarifados(SceAlmoxarifado almoxarifados) {
		this.almoxarifados = almoxarifados;
	}

	@Column(name = "ISL_SEQ_ORIGEM")
	public Integer getIslSeqOrigem() {
		return islSeqOrigem;
	}

	public void setIslSeqOrigem(Integer islSeqOrigem) {
		this.islSeqOrigem = islSeqOrigem;
	}

	@Column(name = "ISL_ESL_SEQ_ORIGEM")
	public Integer getIslEslSeqOrigem() {
		return islEslSeqOrigem;
	}

	public void setIslEslSeqOrigem(Integer islEslSeqOrigem) {
		this.islEslSeqOrigem = islEslSeqOrigem;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_ENCERRAMENTO", length = 7)
	public Date getDtEncerramento() {
		return this.dtEncerramento;
	}

	public void setDtEncerramento(Date dtEncerramento) {
		this.dtEncerramento = dtEncerramento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_ENCERRADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ENCERRADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEncerrado() {
		return this.servidorEncerrado;
	}

	public void setServidorEncerrado(RapServidores servidorEncerrado) {
		this.servidorEncerrado = servidorEncerrado;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ESTORNO")
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_ESTORNADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ESTORNADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEstornado() {
		return this.servidorEstornado;
	}

	public void setServidorEstornado(RapServidores servidorEstornado) {
		this.servidorEstornado = servidorEstornado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESL_SEQ", referencedColumnName="SEQ", insertable = false, updatable = false)
	public SceEntrSaidSemLicitacao getSceEntrSaidSemLicitacao() {
		return sceEntrSaidSemLicitacao;
	}

	public void setSceEntrSaidSemLicitacao(
			SceEntrSaidSemLicitacao sceEntrSaidSemLicitacao) {
		this.sceEntrSaidSemLicitacao = sceEntrSaidSemLicitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "IAF_AFN_NUMERO", referencedColumnName = "AFN_NUMERO"),
			@JoinColumn(name = "IAF_NUMERO", referencedColumnName = "NUMERO") })
	public ScoItemAutorizacaoForn getItemAutorizacaoForn() {
		return itemAutorizacaoForn;
	}

	public void setItemAutorizacaoForn(ScoItemAutorizacaoForn itemAutorizacaoForn) {
		this.itemAutorizacaoForn = itemAutorizacaoForn;
	}

	@Column(name="IAF_AFN_NUMERO", insertable=false, updatable=false )
	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	@Column(name = "IND_ENCERRADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEncerrado() {
		return this.indEncerrado;
	}

	public void setIndEncerrado(Boolean indEncerrado) {
		this.indEncerrado = indEncerrado;
	}

	@Column(name = "IND_ESTORNO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstorno() {
		return this.indEstorno;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MCM_CODIGO", referencedColumnName = "CODIGO")
	public ScoMarcaComercial getScoMarcaComercial() {
		return this.scoMarcaComercial;
	}

	public void setScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) {
		this.scoMarcaComercial = scoMarcaComercial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "NC_MCM_CODIGO", referencedColumnName = "MCM_CODIGO"),
			@JoinColumn(name = "NC_NUMERO", referencedColumnName = "NUMERO") })
	public ScoNomeComercial getScoNomeComercial() {
		return this.scoNomeComercial;
	}

	public void setScoNomeComercial(ScoNomeComercial scoNomeComercial) {
		this.scoNomeComercial = scoNomeComercial;
	}

	@Column(name = "NRO_CONTROLE_FISICO")
	public Integer getNroControleFisico() {
		return this.nroControleFisico;
	}

	public void setNroControleFisico(Integer nroControleFisico) {
		this.nroControleFisico = nroControleFisico;
	}

	@Column(name = "NRO_PATRIMONIO")
	public Integer getNroPatrimonio() {
		return this.nroPatrimonio;
	}

	public void setNroPatrimonio(Integer nroPatrimonio) {
		this.nroPatrimonio = nroPatrimonio;
	}

	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "DADOS_COMPLEMENTARES")
	public String getDadosComplementares() {
		return this.dadosComplementares;
	}

	public void setDadosComplementares(String dadosComplementares) {
		this.dadosComplementares = dadosComplementares;
	}

	@Column(name = "QTDE_CONSUMIDA")
	public Integer getQtdeConsumida() {
		return this.qtdeConsumida;
	}

	public void setQtdeConsumida(Integer qtdeConsumida) {
		this.qtdeConsumida = qtdeConsumida;
	}

	@Column(name = "QTDE_DEVOLVIDA")
	public Integer getQtdeDevolvida() {
		return this.qtdeDevolvida;
	}

	public void setQtdeDevolvida(Integer qtdeDevolvida) {
		this.qtdeDevolvida = qtdeDevolvida;
	}

	@Column(name = "QUANTIDADE")
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SLC_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoDeCompra getScoSolicitacoesCompras() {
		return scoSolicitacoesCompras;
	}

	public void setScoSolicitacoesCompras(ScoSolicitacaoDeCompra scoSolicitacoesCompras) {
		this.scoSolicitacoesCompras = scoSolicitacoesCompras;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "IBO_BOC_SEQ", referencedColumnName = "BOC_SEQ"),
			@JoinColumn(name = "IBO_NRO_ITEM", referencedColumnName = "NRO_ITEM") })
	public SceItemBoc getSceItemBoc() {
		return this.sceItemBoc;
	}

	public void setSceItemBoc(SceItemBoc sceItemBoc) {
		this.sceItemBoc = sceItemBoc;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SLS_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoServico getScoSolicitacoesServico() {
		return this.scoSolicitacoesServico;
	}

	public void setScoSolicitacoesServico(ScoSolicitacaoServico scoSolicitacoesServico) {
		this.scoSolicitacoesServico = scoSolicitacoesServico;
	}

	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	public ScoUnidadeMedida getScoUnidadeMedida() {
		return this.scoUnidadeMedida;
	}

	public void setScoUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.scoUnidadeMedida = scoUnidadeMedida;
	}

	@Column(name="UMD_CODIGO", insertable=false, updatable=false )
	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "IPS_RMP_SEQ", referencedColumnName = "RMP_SEQ"),
			@JoinColumn(name = "IPS_NUMERO", referencedColumnName = "NUMERO") })
	public SceItemRmps getSceItemRmps() {
		return this.sceItemRmps;
	}

	public void setSceItemRmps(SceItemRmps sceItemRmps) {
		this.sceItemRmps = sceItemRmps;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ISL_SEQ_ORIGEM", referencedColumnName="SEQ", insertable=false, updatable=false)
	public SceItemEntrSaidSemLicitacao getSceItemEntrSaidSemLicitacao() {
		return this.sceItemEntrSaidSemLicitacao;
	}

	public void setSceItemEntrSaidSemLicitacao(SceItemEntrSaidSemLicitacao sceItemEntrSaidSemLicitacao) {
		this.sceItemEntrSaidSemLicitacao = sceItemEntrSaidSemLicitacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName="MAT_CODIGO", insertable=false, updatable=false)
	public SceMovimentoMaterial getSceMovimentoMaterial() {
		return this.sceMovimentoMaterial;
	}

	public void setSceMovimentoMaterial(SceMovimentoMaterial sceMovimentoMaterial) {
		this.sceMovimentoMaterial = sceMovimentoMaterial;
	}
	
	@Column(name = "ESL_SEQ")
	public Integer getEslSeq() {
		return eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}

    @Column(name = "SLC_NUMERO", insertable = false, updatable = false)  
	public Integer getSlcNumero() {
		return slcNumero;
	}

	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ISL_ESL_SEQ_ORIGEM", insertable=false, updatable=false)	
	public SceEntrSaidSemLicitacao getSceEntrSaidSemLicitacaoOrigem() {
		return sceEntrSaidSemLicitacaoOrigem;
	}

	public void setSceEntrSaidSemLicitacaoOrigem(
			SceEntrSaidSemLicitacao sceEntrSaidSemLicitacaoOrigem) {
		this.sceEntrSaidSemLicitacaoOrigem = sceEntrSaidSemLicitacaoOrigem;
	}



	public enum Fields {

		SEQ("seq"),
		MATERIAL("scoMaterial"),
		CODIGO_MATERIAL("scoMaterial.codigo"),
		SC("scoSolicitacoesCompras"),
		SC_NUMERO("scoSolicitacoesCompras.numero"),
		IND_ESTORNO("indEstorno"),
		UNIDADE_MEDIDA("scoUnidadeMedida"),
		CODIGO_UNIDADE_MEDIDA("scoUnidadeMedida.codigo"),
		UMD_CODIGO("umdCodigo"),
		MARCA_COMERCIAL("scoMarcaComercial"),
		MCM_CODIGO("scoMarcaComercial.codigo"),
		IND_ENCERRADO("indEncerrado"),
		QUANTIDADE("quantidade"),
		ITEM_AUTORIZACAO_FORN("itemAutorizacaoForn"),
		IAF_AFN_NUMERO("itemAutorizacaoForn.id.afnNumero"),
		IAF_NUMERO("itemAutorizacaoForn.id.numero"),
		QTDE_DEVOLVIDA("qtdeDevolvida"), 
		SCE_ENTR_SAID_SEM_LICITACAO("sceEntrSaidSemLicitacao"),
		ISL("sceItemEntrSaidSemLicitacao"),
		ISL_ESL_SEQ_ORIGEM("islEslSeqOrigem"),
		ESL_SEQ("eslSeq"),
		SLC_NUMERO("slcNumero"),
		ALM("almoxarifados"),
		ALM_SEQ("almoxarifados.seq"),
		SCE_ENTR_SAID_SEM_LIC_ORIGEM("sceEntrSaidSemLicitacaoOrigem"),
		VALOR("valor"),
		IAF_AFN_NUM("iafAfnNumero"),
		NRO_PATRIMONIO("nroPatrimonio"),
		NRO_CONTROLE_FISICO("nroControleFisico"),
		MMT("sceMovimentoMaterial");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof SceItemEntrSaidSemLicitacao)) {
			return false;
		}
		SceItemEntrSaidSemLicitacao other = (SceItemEntrSaidSemLicitacao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
}