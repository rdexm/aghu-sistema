package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.Transient;
import javax.validation.constraints.Digits;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.BaseEntityNumero;


@Entity
@Table(name = "SCO_AUTORIZACOES_FORN", schema = "AGH")
@SequenceGenerator(name = "scoAfnSq1", sequenceName = "AGH.SCO_AFN_SQ1", allocationSize = 1)
public class ScoAutorizacaoForn extends BaseEntityNumero<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1146651985634483646L;
	private Integer numero;
	private FsoConveniosFinanceiro convenioFinanceiro;
	private FsoNaturezaDespesa naturezaDespesa;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private Date dtGeracao;
	private Short nroComplemento;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Boolean geracao;
	private Date dtAlteracao;
	private Date dtPrevEntrega;
	private Date dtExclusao;
	private Short sequenciaAlteracao;
	private BigDecimal valorFrete;
	private String observacao;
	private BigDecimal valorEmpenho;
	private String nroEmpenho;
	private Integer nroContrato;
	private Boolean exclusao;
	private DominioAprovadaAutorizacaoForn aprovada;
	private Date dtEstorno;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private Boolean imprRefContrato;
	private Date dtAssinaturaChefia;
	private Date dtAssinaturaCoord;
	private Date dtVenctoContrato;
	private Boolean entregaProgramada;
	private RapServidores servidor;
	private RapServidores servidorControlado;
	private RapServidores servidorExcluido;
	private RapServidores servidorAutorizado;
	private RapServidores servidorEstorno;
	private RapServidores servidorAssinaCoord;
	private RapServidores servidorGestor;
	private FcpMoeda moeda;
	private ScoFornecedor scoFornecedor;
	private ScoPropostaFornecedor propostaFornecedor;
	private ScoCondicaoPagamentoPropos condicaoPagamentoPropos;
	private ScoMotivoAlteracaoAf motivoAlteracaoAf;
	private List<ScoAfContrato> afContratos;
	private List<ScoItemAutorizacaoForn> itensAutorizacaoForn;
	private List<ScoAfEmpenho> afEmpenho;
	private FsoVerbaGestao verbaGestao;
	private List<ScoAutorizacaoFornecedorPedido> scoAutorizacaoFornecedorPedido;
	private ScoLicitacao scoLicitacao;
	private Integer pfrLctNumero;
	
	
	private enum ScoAutorizacaoFornExceptionCode implements BusinessExceptionCode {
		NRO_EMPENHO_E_VALR_EMPENHO_INVALIDOS
	}
	// construtores
	public ScoAutorizacaoForn() {
	}
	
	public ScoAutorizacaoForn(Integer numero) {
		this.numero = numero;
	}

	// getters & setters
	@Id
	@Column(name = "NUMERO", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoAfnSq1")
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CVF_CODIGO", referencedColumnName = "CODIGO")	
	public FsoConveniosFinanceiro getConvenioFinanceiro() {
		return convenioFinanceiro;
	}

	public void setConvenioFinanceiro(FsoConveniosFinanceiro convenioFinanceiro) {
		this.convenioFinanceiro = convenioFinanceiro;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "NTD_CODIGO", referencedColumnName = "CODIGO"),
			@JoinColumn(name = "NTD_GND_CODIGO", referencedColumnName = "GND_CODIGO") })	
	public FsoNaturezaDespesa getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(FsoNaturezaDespesa naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "NTD_GND_CODIGO", referencedColumnName = "CODIGO", insertable=false, updatable=false, nullable=false)	
	public FsoGrupoNaturezaDespesa getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}

	public void setGrupoNaturezaDespesa(FsoGrupoNaturezaDespesa grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}

	@Column(name = "DT_GERACAO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	@Column(name = "NRO_COMPLEMENTO", length = 3, nullable = false)
	public Short getNroComplemento() {
		return this.nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	@Column(name = "IND_SITUACAO", length = 2, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}

	@Column(name = "IND_GERACAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getGeracao() {
		return geracao;
	}

	public void setGeracao(Boolean geracao) {
		this.geracao = geracao;
	}

	@Column(name = "DT_ALTERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAlteracao() {
		return this.dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}

	@Column(name = "DT_PREV_ENTREGA")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtPrevEntrega() {
		return this.dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}

	@Column(name = "DT_EXCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtExclusao() {
		return this.dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}

	@Column(name = "SEQUENCIA_ALTERACAO", length = 3)
	public Short getSequenciaAlteracao() {
		return this.sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Short sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	@Column(name = "VALOR_FRETE", precision = 18, scale = 2)
	@Digits(integer=16, fraction=2, message="Valor do frete dever ter no máximo 16 números inteiros e 2 decimais")
	public BigDecimal getValorFrete() {
		return this.valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}

	@Column(name = "OBSERVACAO", length = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "VALOR_EMPENHO", precision = 18, scale = 2)
	@Digits(integer=16, fraction=2, message="Valor do empenho dever ter no máximo 16 números inteiros e 2 decimais")
	public BigDecimal getValorEmpenho() {
		return this.valorEmpenho;
	}

	public void setValorEmpenho(BigDecimal valorEmpenho) {
		this.valorEmpenho = valorEmpenho;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PFR_LCT_NUMERO", referencedColumnName = "NUMERO", insertable = false, updatable = false)
	public ScoLicitacao getScoLicitacao() {
		return scoLicitacao;
	}

	public void setScoLicitacao(ScoLicitacao scoLicitacao) {
		this.scoLicitacao = scoLicitacao;
	}

	@Column(name = "NRO_EMPENHO", length = 10)
	public String getNroEmpenho() {
		return this.nroEmpenho;
	}

	public void setNroEmpenho(String nroEmpenho) {
		this.nroEmpenho = nroEmpenho;
	}

	@Column(name = "NRO_CONTRATO", length = 5)
	public Integer getNroContrato() {
		return this.nroContrato;
	}

	public void setNroContrato(Integer nroContrato) {
		this.nroContrato = nroContrato;
	}

	@Column(name = "IND_EXCLUSAO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getExclusao() {
		return exclusao;
	}

	public void setExclusao(Boolean exclusao) {
		this.exclusao = exclusao;
	}

	@Column(name = "IND_APROVADA", length = 1, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioAprovadaAutorizacaoForn getAprovada() {
		return aprovada;
	}

	public void setAprovada(DominioAprovadaAutorizacaoForn aprovada) {
		this.aprovada = aprovada;
	}

	@Column(name = "DT_ESTORNO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}

	@Column(name = "MODALIDADE_EMPENHO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioModalidadeEmpenho") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")	
	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return this.modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	@Column(name = "IND_IMPR_REF_CONTRATO", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getImprRefContrato() {
		return imprRefContrato;
	}

	public void setImprRefContrato(Boolean imprRefContrato) {
		this.imprRefContrato = imprRefContrato;
	}

	@Column(name = "DT_ASSINATURA_CHEFIA")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAssinaturaChefia() {
		return this.dtAssinaturaChefia;
	}

	public void setDtAssinaturaChefia(Date dtAssinaturaChefia) {
		this.dtAssinaturaChefia = dtAssinaturaChefia;
	}

	@Column(name = "DT_ASSINATURA_COORD")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAssinaturaCoord() {
		return this.dtAssinaturaCoord;
	}

	public void setDtAssinaturaCoord(Date dtAssinaturaCoord) {
		this.dtAssinaturaCoord = dtAssinaturaCoord;
	}

	@Column(name = "DT_VENCTO_CONTRATO")
	@Temporal(TemporalType.DATE)
	public Date getDtVenctoContrato() {
		return this.dtVenctoContrato;
	}

	public void setDtVenctoContrato(Date dtVenctoContrato) {
		this.dtVenctoContrato = dtVenctoContrato;
	}

	@Column(name = "IND_ENTREGA_PROGRAMADA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEntregaProgramada() {
		return entregaProgramada;
	}

	public void setEntregaProgramada(Boolean entregaProgramada) {
		this.entregaProgramada = entregaProgramada;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_CONTOLADA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_CONTOLADA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorControlado() {
		return servidorControlado;
	}

	public void setServidorControlado(RapServidores servidorControlado) {
		this.servidorControlado = servidorControlado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ESTORNO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ESTORNO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEstorno() {
		return servidorEstorno;
	}

	public void setServidorEstorno(RapServidores servidorEstorno) {
		this.servidorEstorno = servidorEstorno;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MDA_CODIGO", referencedColumnName = "CODIGO")
	public FcpMoeda getMoeda() {
		return moeda;
	}

	public void setMoeda(FcpMoeda moeda) {
		this.moeda = moeda;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PFR_FRN_NUMERO", referencedColumnName = "NUMERO", insertable = false, updatable = false)
	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "PFR_FRN_NUMERO", referencedColumnName = "FRN_NUMERO"),
			@JoinColumn(name = "PFR_LCT_NUMERO", referencedColumnName = "LCT_NUMERO") })
	public ScoPropostaFornecedor getPropostaFornecedor() {
		return propostaFornecedor;
	}

	public void setPropostaFornecedor(
			ScoPropostaFornecedor propostaFornecedor) {
		this.propostaFornecedor = propostaFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CDP_NUMERO", referencedColumnName = "NUMERO")
	public ScoCondicaoPagamentoPropos getCondicaoPagamentoPropos() {
		return condicaoPagamentoPropos;
	}

	public void setCondicaoPagamentoPropos(
			ScoCondicaoPagamentoPropos condicaoPagamentoPropos) {
		this.condicaoPagamentoPropos = condicaoPagamentoPropos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_EXCLUIDA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_EXCLUIDA", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorExcluido() {
		return servidorExcluido;
	}

	public void setServidorExcluido(RapServidores servidorExcluido) {
		this.servidorExcluido = servidorExcluido;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAA_CODIGO", referencedColumnName = "CODIGO")
	public ScoMotivoAlteracaoAf getMotivoAlteracaoAf() {
		return motivoAlteracaoAf;
	}

	public void setMotivoAlteracaoAf(
			ScoMotivoAlteracaoAf motivoAlteracaoAf) {
		this.motivoAlteracaoAf = motivoAlteracaoAf;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_AUTORIZADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_AUTORIZADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAutorizado() {
		return servidorAutorizado;
	}

	public void setServidorAutorizado(RapServidores servidorAutorizado) {
		this.servidorAutorizado = servidorAutorizado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ASSINA_COORD", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ASSINA_COORD", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAssinaCoord() {
		return servidorAssinaCoord;
	}

	public void setServidorAssinaCoord(RapServidores servidorAssinaCoord) {
		this.servidorAssinaCoord = servidorAssinaCoord;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_GESTOR", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_GESTOR", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorGestor() {
		return servidorGestor;
	}

	public void setServidorGestor(RapServidores servidorGestor) {
		this.servidorGestor = servidorGestor;
	}

	@OneToMany(mappedBy="scoAutorizacoesForn", fetch = FetchType.LAZY)
	public List<ScoAfContrato> getAfContratos() {
		return afContratos;
	}

	public void setAfContratos(List<ScoAfContrato> afContratos) {
		this.afContratos = afContratos;
	}
	
	@OneToMany(mappedBy="scoAutorizacoesForn", fetch=FetchType.LAZY)	
	public List<ScoAfEmpenho> getAfEmpenho() {
		return afEmpenho;
	}

	public void setAfEmpenho(List<ScoAfEmpenho> afEmpenho) {
		this.afEmpenho = afEmpenho;
	}

	// outros
	//bi-directional many-to-one association to ScoItensAutorizacaoForn
	@OneToMany(mappedBy="autorizacoesForn", fetch = FetchType.LAZY)
	public List<ScoItemAutorizacaoForn> getItensAutorizacaoForn() {
		return itensAutorizacaoForn;
	}

	public void setItensAutorizacaoForn(
			List<ScoItemAutorizacaoForn> itensAutorizacaoForn) {
		this.itensAutorizacaoForn = itensAutorizacaoForn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "VBG_SEQ", referencedColumnName = "SEQ")
	public FsoVerbaGestao getVerbaGestao() {
		return verbaGestao;
	}

	public void setVerbaGestao(FsoVerbaGestao verbaGestao) {
		this.verbaGestao = verbaGestao;
	}

	@OneToMany(mappedBy = "scoAutorizacaoForn")
	public List<ScoAutorizacaoFornecedorPedido> getScoAutorizacaoFornecedorPedido() {
		return scoAutorizacaoFornecedorPedido;
	}

	public void setScoAutorizacaoFornecedorPedido(
			List<ScoAutorizacaoFornecedorPedido> scoAutorizacaoFornecedorPedido) {
		this.scoAutorizacaoFornecedorPedido = scoAutorizacaoFornecedorPedido;
	}
		
	@Column(name = "PFR_LCT_NUMERO", insertable = false, updatable = false)
	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}

	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}

	@SuppressWarnings("unused")
	@PrePersist
	@PreUpdate
	private void validacoes() {
		if (!((this.getNroEmpenho() == null && this.getValorEmpenho() == null) || (this.getNroEmpenho() != null && this.getValorEmpenho() != null))) {
			throw new BaseRuntimeException(ScoAutorizacaoFornExceptionCode.NRO_EMPENHO_E_VALR_EMPENHO_INVALIDOS);
		}
 	}
	
	@Transient
	public BigDecimal getValEfetAF(){
		
		double val = 0;
		
		if(this.getItensAutorizacaoForn().size()>0){
			
			for(ScoItemAutorizacaoForn it : this.itensAutorizacaoForn){
				
				if(it.getIndSituacao().equals(DominioSituacaoAutorizacaoFornecimento.EX)){
					continue;
				}
				
				val = val + it.getValorEfetivado();
			}
		}
		
		return new BigDecimal(val);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("numero", this.numero)
				.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ScoAutorizacaoForn)){
			return false;
		}
		ScoAutorizacaoForn castOther = (ScoAutorizacaoForn) other;
		return new EqualsBuilder().append(this.numero, castOther.getNumero())
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.numero).toHashCode();
	}

	public enum Fields {
				NUMERO("numero"), 
				CVF_CODIGO("convenioFinanceiro"), 
				NATUREZA_DESPESA("naturezaDespesa"),
				GRUPO_NATUREZA_DESPESA("grupoNaturezaDespesa"),
				DT_GERACAO("dtGeracao"), 
				NRO_COMPLEMENTO("nroComplemento"), 
				SITUACAO("situacao"), 
				IND_GERACAO("geracao"), 
				DT_ALTERACAO("dtAlteracao"), 
				DT_PREV_ENTREGA("dtPrevEntrega"), 
				DT_EXCLUSAO("dtExclusao"), 
				SEQUENCIA_ALTERACAO("sequenciaAlteracao"), 
				VALOR_FRETE("valorFrete"), 
				OBSERVACAO("observacao"), 
				VALOR_EMPENHO("valorEmpenho"), 
				NRO_EMPENHO("nroEmpenho"), 
				NRO_CONTRATO("nroContrato"), 
				IND_EXCLUSAO("exclusao"), 
				APROVADA("aprovada"), 
				DT_ESTORNO("dtEstorno"), 
				MODALIDADE_EMPENHO("modalidadeEmpenho"), 
				IND_IMPR_REF_CONTRATO("imprRefContrato"), 
				DT_ASSINATURA_CHEFIA("dtAssinaturaChefia"), 
				DT_ASSINATURA_COORD("dtAssinaturaCoord"), 
				DT_VENCTO_CONTRATO("dtVenctoContrato"), 
				IND_ENTREGA_PROGRAMADA("entregaProgramada"), 
				SERVIDOR("servidor"), 
				SERVIDOR_CONTROLADO("servidorControlado"), 
				SERVIDOR_EXCLUIDO("servidorExcluido"), 
				MOEDA("moeda"), 
				PROPOSTA_FORNECEDOR("propostaFornecedor"), 
				PFR_LCT_NUMERO("propostaFornecedor.id.lctNumero"),
				PFR_FRN_NUMERO("propostaFornecedor.id.frnNumero"),
				CONDICAO_PAGAMENTO_PROPOS("condicaoPagamentoPropos"), 
				SERVIDOR_AUTORIZADO("servidorAutorizado"), 
				SERVIDOR_ESTORNO("servidorEstorno"), 
				MOTIVO_ALTERACAO_AF("motivoAlteracaoAf"), 
				SERVIDOR_ASSINA_COORD("servidorAssinaCoord"), 
				SERVIDOR_GESTOR("servidorGestor"),
				SER_MATRICULA_GESTOR("servidorGestor.id.matricula"),
				SER_VIN_CODIGO_GESTOR("servidorGestor.id.vinCodigo"),
				FORNECEDOR("propostaFornecedor.fornecedor"),
				FORNECEDOR_NUMERO("propostaFornecedor.fornecedor.numero"),
				SCO_FORNECEDOR("scoFornecedor"),
				LICITACAO("propostaFornecedor.licitacao"),
				LICITACAO_NUMERO("propostaFornecedor.licitacao.numero"),
				ITENSAUTORIZACAOFORN("itensAutorizacaoForn"),				
				AF_EMPENHO("afEmpenho"),
				VERBA_GESTAO("verbaGestao"),
				VBG_SEQ("verbaGestao.seq"),
				NTD_GND_CODIGO("naturezaDespesa.id.gndCodigo"),
				NTD_CODIGO("naturezaDespesa.id.codigo"),
				SCO_AF_PEDIDOS("scoAutorizacaoFornecedorPedido"),
				SCO_LICITACAO("scoLicitacao"),
				SER_MATRICULA("servidor.id.matricula"),
				SER_VIN_CODIGO("servidor.id.vinCodigo"),
				IND_SITUACAO("situacao"),
				ITENS_AUTORIZACAO_FORN_ID_AFN_NUMERO("itensAutorizacaoForn.id.afnNumero"),
				PFRLCFNUMERO("pfrLctNumero");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
}