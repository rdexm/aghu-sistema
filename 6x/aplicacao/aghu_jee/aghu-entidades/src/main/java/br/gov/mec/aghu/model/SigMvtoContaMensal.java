package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;

@Entity
@SequenceGenerator(name = "sigMslSq1", sequenceName = "SIG_MSL_SQ1", allocationSize = 1)
@Table(name = "sig_mvto_conta_mensais", schema = "agh")
public class SigMvtoContaMensal extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 8764959342630230283L;

	@Transient
	private final String EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA = "^0*";
	
	private Integer seq;
	private SigControleVidaUtil sigControleVidaUteis;
	private SigProcessamentoCusto sigProcessamentoCustos;
	private Date criadoEm;
	private RapServidores rapServidores;
	private FccCentroCustos fccCentroCustos;
	private FccCentroCustos fccCentroCustosDebita;
	private DominioTipoMovimentoConta tipoMvto;
	private DominioTipoValorConta tipoValor;
	private Long qtde;
	private BigDecimal valor;
	private BigDecimal custoMedio;
	private ScoMaterial scoMaterial;
	private ScoUnidadeMedida scoUnidadeMedida;
	private SigGrupoOcupacoes sigGrupoOcupacoes;
	private ScoItensContrato scoItensContrato;
	private ScoAfContrato scoAfContrato;
	private String codPatrimonio;
	private ScoSolicitacaoServico solicitacaoServico;
	private Integer version;
	private ScoGrupoMaterial grupoMaterial;
	private SigCalculoObjetoCusto calculoObjetoCusto;
	private ScoAutorizacaoForn autorizacaoForn;
	private ScoServico servico;
	private Integer iteracao;
	
	
	public SigMvtoContaMensal() {
	}

	public SigMvtoContaMensal(Integer seq, SigProcessamentoCusto sigProcessamentoCustos, Date criadoEm, RapServidores rapServidores,
			FccCentroCustos fccCentroCustos, DominioTipoMovimentoConta tipoMvto, DominioTipoValorConta tipoValor, BigDecimal valor,
			ScoGrupoMaterial grupoMaterial) {
		this.seq = seq;
		this.sigProcessamentoCustos = sigProcessamentoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.fccCentroCustos = fccCentroCustos;
		this.tipoMvto = tipoMvto;
		this.tipoValor = tipoValor;
		this.valor = valor;
	}

	public SigMvtoContaMensal(Integer seq, SigControleVidaUtil sigControleVidaUteis, SigProcessamentoCusto sigProcessamentoCustos, Date criadoEm,
			RapServidores rapServidores, FccCentroCustos fccCentroCustos, FccCentroCustos fccCentroCustosDebita, DominioTipoMovimentoConta tipoMvto,
			DominioTipoValorConta tipoValor, Long qtde, BigDecimal valor, BigDecimal custoMedio, ScoMaterial scoMaterial, ScoUnidadeMedida scoUnidadeMedida,
			SigGrupoOcupacoes sigGrupoOcupacoes, ScoItensContrato scoItensContrato, ScoAfContrato scoAfContrato) {
		this.seq = seq;
		this.sigControleVidaUteis = sigControleVidaUteis;
		this.sigProcessamentoCustos = sigProcessamentoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.fccCentroCustos = fccCentroCustos;
		this.fccCentroCustosDebita = fccCentroCustosDebita;
		this.tipoMvto = tipoMvto;
		this.tipoValor = tipoValor;
		this.qtde = qtde;
		this.valor = valor;
		this.custoMedio = custoMedio;
		this.scoMaterial = scoMaterial;
		this.scoUnidadeMedida = scoUnidadeMedida;
		this.sigGrupoOcupacoes = sigGrupoOcupacoes;
		this.scoItensContrato = scoItensContrato;
		this.scoAfContrato = scoAfContrato;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigMslSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vit_seq", referencedColumnName = "seq")
	public SigControleVidaUtil getSigControleVidaUteis() {
		return this.sigControleVidaUteis;
	}

	public void setSigControleVidaUteis(SigControleVidaUtil sigControleVidaUteis) {
		this.sigControleVidaUteis = sigControleVidaUteis;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pmu_seq", nullable = false, referencedColumnName = "seq")
	public SigProcessamentoCusto getSigProcessamentoCustos() {
		return this.sigProcessamentoCustos;
	}

	public void setSigProcessamentoCustos(SigProcessamentoCusto sigProcessamentoCustos) {
		this.sigProcessamentoCustos = sigProcessamentoCustos;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cct_codigo", nullable = false, referencedColumnName = "codigo")
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cct_codigo_debita", referencedColumnName = "codigo")
	public FccCentroCustos getFccCentroCustosDebita() {
		return fccCentroCustosDebita;
	}

	public void setFccCentroCustosDebita(FccCentroCustos fccCentroCustosDebita) {
		this.fccCentroCustosDebita = fccCentroCustosDebita;
	}

	@Column(name = "tipo_mvto", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioTipoMovimentoConta getTipoMvto() {
		return this.tipoMvto;
	}

	public void setTipoMvto(DominioTipoMovimentoConta tipoMvto) {
		this.tipoMvto = tipoMvto;
	}

	@Column(name = "tipo_valor", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioTipoValorConta getTipoValor() {
		return this.tipoValor;
	}

	public void setTipoValor(DominioTipoValorConta tipoValor) {
		this.tipoValor = tipoValor;
	}

	@Column(name = "qtde", precision = 12, scale = 4)
	public Long getQtde() {
		return this.qtde;
	}

	public void setQtde(Long qtde) {
		this.qtde = qtde;
	}

	@Column(name = "valor", nullable = false, precision = 18, scale = 4)
	public BigDecimal getValor() {
		return this.valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Column(name = "custo_medio", precision = 18, scale = 4)
	public BigDecimal getCustoMedio() {
		return this.custoMedio;
	}

	public void setCustoMedio(BigDecimal custoMedio) {
		this.custoMedio = custoMedio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AFN_NUMERO", referencedColumnName = "NUMERO")
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mat_codigo", referencedColumnName = "codigo")
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "umd_codigo", referencedColumnName = "codigo")
	public ScoUnidadeMedida getScoUnidadeMedida() {
		return this.scoUnidadeMedida;
	}

	public void setScoUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.scoUnidadeMedida = scoUnidadeMedida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "goc_seq", referencedColumnName = "seq")
	public SigGrupoOcupacoes getSigGrupoOcupacoes() {
		return this.sigGrupoOcupacoes;
	}

	public void setSigGrupoOcupacoes(SigGrupoOcupacoes sigGrupoOcupacoes) {
		this.sigGrupoOcupacoes = sigGrupoOcupacoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "icon_seq", referencedColumnName = "seq")
	public ScoItensContrato getScoItensContrato() {
		return this.scoItensContrato;
	}

	public void setScoItensContrato(ScoItensContrato scoItensContrato) {
		this.scoItensContrato = scoItensContrato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "afco_seq", referencedColumnName = "seq")
	public ScoAfContrato getScoAfContrato() {
		return this.scoAfContrato;
	}

	public void setScoAfContrato(ScoAfContrato scoAfContrato) {
		this.scoAfContrato = scoAfContrato;
	}

	@Column(name = "COD_PATRIMONIO ")
	public String getCodPatrimonio() {
		return codPatrimonio;
	}

	public void setCodPatrimonio(String codPatrimonio) {
		if (codPatrimonio != null) {
			codPatrimonio = codPatrimonio.replaceFirst(EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA, "");
		}
		this.codPatrimonio = codPatrimonio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sls_numero", referencedColumnName = "numero")
	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gmt_codigo", referencedColumnName = "codigo")
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cbj_seq", referencedColumnName = "seq")
	public SigCalculoObjetoCusto getCalculoObjetoCusto() {
		return calculoObjetoCusto;
	}

	public void setCalculoObjetoCusto(SigCalculoObjetoCusto calculoObjetoCusto) {
		this.calculoObjetoCusto = calculoObjetoCusto;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SRV_CODIGO")
	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	@Column(name = "ITERACAO", precision = 3)
	public Integer getIteracao(){
		return this.iteracao;
	}
	
	public void setIteracao(Integer iteracao){
		this.iteracao = iteracao;
	}
	
	public enum Fields {

		SEQ("seq"),
		CONTROLE_VIDA_UTIL("sigControleVidaUteis"),
		PROCESSAMENTO_CUSTOS("sigProcessamentoCustos"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		CENTRO_CUSTO("fccCentroCustos"),
		CENTRO_CUSTO_CODIGO("fccCentroCustos.codigo"),
		CENTRO_CUSTO_DEBITA("fccCentroCustosDebita"),
		CENTRO_CUSTO_DEBITA_CODIGO("fccCentroCustosDebita.codigo"),
		TIPO_MOVIMENTO("tipoMvto"),
		TIPO_VALOR("tipoValor"),
		QTDE("qtde"),
		VALOR("valor"),
		CUSTO_MEDIO("custoMedio"),
		MATERIAL("scoMaterial"),
		MATERIAL_CODIGO("scoMaterial.codigo"),
		UNIDADE_MEDIDA("scoUnidadeMedida"),
		GRUPO_OCUPACAO("sigGrupoOcupacoes"),
		ITENS_CONTRATO("scoItensContrato"),
		AF_CONTRATO("scoAfContrato"),
		CODIGO_PATRIMONIO("codPatrimonio"),
		SOLICITACAO_SERVICO("solicitacaoServico"),
		GRUPO_MATERIAL("grupoMaterial"),
		CALCULO_OBJETO_CUSTO("calculoObjetoCusto"),
		AUTORIZACAO_FORNEC("autorizacaoForn"),
		SERVICO("servico"),
		ITERACAO("iteracao");

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
	public boolean equals(Object other) {
		if (!(other instanceof SigMvtoContaMensal)) {
			return false;
		}
		SigMvtoContaMensal castOther = (SigMvtoContaMensal) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
}
