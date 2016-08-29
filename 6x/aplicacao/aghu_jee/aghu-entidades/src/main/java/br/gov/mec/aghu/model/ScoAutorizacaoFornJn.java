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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioAprovadaAutorizacaoForn;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the sco_af_jn database table.
 * 
 */
@Entity
@SequenceGenerator(name="scoAfJn", sequenceName="AGH.SCO_AFN_JN_SQ1", allocationSize = 1)
@Table(name="sco_af_jn" , schema = "AGH")
public class ScoAutorizacaoFornJn extends BaseEntitySeq<Long> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 5776523642799971931L;
	private Long seq;
	private ScoCondicaoPagamentoPropos condicaoPagamentoPropos;
	private Integer cvfCodigo;
	private Date dtAlteracao;
	private Date dtAssinaturaChefia;
	private Date dtAssinaturaCoord;
	private Date dtEstorno;
	private Date dtExclusao;
	private Date dtGeracao;
	private Date dtPrevEntrega;
	private DominioAprovadaAutorizacaoForn indAprovada;
	private Boolean indExclusao;
	private Boolean indGeracao;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Date jnDateTime;
	private String jnOperation;
	private String jnUser;
	private ScoMotivoAlteracaoAf motivoAlteracaoAf;
	private FcpMoeda mdaCodigo;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	private Short nroComplemento;
	private Integer nroContrato;
	private String nroEmpenho;
	private FsoNaturezaDespesa naturezaDespesa;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;
	private Integer numero;
	private String observacao;
	private ScoPropostaFornecedor propostaFornecedor;
	private Short sequenciaAlteracao;
	private Double valorEmpenho;
	private BigDecimal valorFrete;
	private ScoAutorizacaoForn scoAutorizacaoForn;
	private List<ScoItemAutorizacaoFornJn> itensAutorizacaoForn;
	private FsoVerbaGestao verbaGestao;
	private RapServidores servidor;
	private RapServidores servidorControlado;
	private RapServidores servidorExcluido;
	private RapServidores servidorAutorizado;
	private RapServidores servidorEstorno;
	private RapServidores servidorAssinaCoord;
	private RapServidores servidorGestor;
//	private List<ScoAfEmpenho> afEmpenho;
	private Date dtVenctoContrato;
	
	
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
	
	
	

	public enum Fields{
		ID("seq"),
		PFR_LCT_NUMERO("propostaFornecedor.id.lctNumero"),
		PFR_FRN_NUMERO("propostaFornecedor.id.frnNumero"),
		AF_EMPENHO("afEmpenho"),
		NUM_COMPLEMENTO("nroComplemento"), 
		NUMERO("numero"),
		SEQUENCIA_ALTERACAO("sequenciaAlteracao"),
		IND_APROVADA("indAprovada"),
		DT_ALTERACAO("dtAlteracao"),
		AUTORIZACAO_FORN("scoAutorizacaoForn"),
		IND_SITUACAO("situacao"),
		DT_ASSINATURA_COORD("dtAssinaturaCoord"),
		MATRICULA_ASSINA_COORD("servidorAssinaCoord.id.matricula"),
		OBSERVACAO("observacao"),
		NUM_LICITACAO("propostaFornecedor.id.lctNumero"),
		DT_PREV_ENTREGA("dtPrevEntrega"),
		DT_EXCLUSAO("dtExclusao"),
		DT_ESTORNO("dtEstorno"),
		IND_EXCLUSAO("indExclusao"),
		IND_GERACAO("indGeracao"),
		VALOR_FRETE("valorFrete"),
		NRO_EMPENHO("nroEmpenho"),
		NRO_CONTRATO("nroContrato"),
		MODALIDADE_EMPENHO("modalidadeEmpenho.codigo"),
		CVF_CODIGO("cvfCodigo"),
		VENCIMENTO_CONTRATO("dtVenctoContrato"),
		VALOR_EMPENHO("valorEmpenho"),
		AUTORIZACAO_FORN_NUMERO("scoAutorizacaoForn.numero"),
		DT_GERACAO("dtGeracao"),
		SERVIDOR("servidor"),
		SITUACAO("situacao"),
		SERVIDOR_GESTOR("servidorGestor"),
		SERVIDOR_CONTROLADO("servidorControlado"),
		SER_VIN_CODIGO_GESTOR("servidorGestor.id.vinCodigo"),
		SER_MATRICULA_GESTOR("servidorGestor.id.matricula"),
		MOTIVO_ALTERACAO("motivoAlteracaoAf"),
		CONDICAO_PAGAMENTO("condicaoPagamentoPropos"),
		MOEDA("mdaCodigo"),
		VERBA_GESTAO("verbaGestao"),
		NATUREZA_DESPESA("naturezaDespesa"),
		SERVIDOR_EXCLUIDO("servidorExcluido"),
		SERVIDOR_ESTORNO("servidorEstorno"),
		SERVIDOR_ASSINATURA("servidorAssinaCoord"),
		SERVIDOR_AUTORIZADO("servidorAutorizado"),
		PROPOSTA_FORNECEDOR("propostaFornecedor");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
	
    public ScoAutorizacaoFornJn() {
    }


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator = "scoAfJn")
	@Column(name="seq_jn")
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
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


	@Column(name="cvf_codigo")
	public Integer getCvfCodigo() {
		return this.cvfCodigo;
	}

	public void setCvfCodigo(Integer cvfCodigo) {
		this.cvfCodigo = cvfCodigo;
	}


	@Column(name="dt_alteracao")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAlteracao() {
		return this.dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}


	@Column(name="dt_assinatura_chefia")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAssinaturaChefia() {
		return this.dtAssinaturaChefia;
	}

	public void setDtAssinaturaChefia(Date dtAssinaturaChefia) {
		this.dtAssinaturaChefia = dtAssinaturaChefia;
	}


	@Column(name="dt_assinatura_coord")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAssinaturaCoord() {
		return this.dtAssinaturaCoord;
	}

	public void setDtAssinaturaCoord(Date dtAssinaturaCoord) {
		this.dtAssinaturaCoord = dtAssinaturaCoord;
	}


	@Column(name="dt_estorno")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}


	@Column(name="dt_exclusao")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtExclusao() {
		return this.dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}


	@Column(name="dt_geracao")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Column(name="dt_prev_entrega")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtPrevEntrega() {
		return this.dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}


	@Column(name="ind_aprovada")
	@Enumerated(EnumType.STRING)
	public DominioAprovadaAutorizacaoForn getIndAprovada() {
		return this.indAprovada;
	}

	public void setIndAprovada(DominioAprovadaAutorizacaoForn indAprovada) {
		this.indAprovada = indAprovada;
	}


	@Column(name="ind_exclusao")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExclusao() {
		return this.indExclusao;
	}

	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}


	@Column(name="ind_geracao")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndGeracao() {
		return this.indGeracao;
	}

	public void setIndGeracao(Boolean indGeracao) {
		this.indGeracao = indGeracao;
	}

	@Column(name="ind_situacao" , length = 2, nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}


	@Column(name="jn_date_time")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getJnDateTime() {
		return this.jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}


	@Column(name="jn_operation")
	public String getJnOperation() {
		return this.jnOperation;
	}

	public void setJnOperation(String jnOperation) {
		this.jnOperation = jnOperation;
	}


	@Column(name="jn_user")
	public String getJnUser() {
		return this.jnUser;
	}

	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
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
	@JoinColumn(name = "mda_codigo", referencedColumnName = "CODIGO")
	public FcpMoeda getMdaCodigo() {
		return this.mdaCodigo;
	}

	public void setMdaCodigo(FcpMoeda mdaCodigo) {
		this.mdaCodigo = mdaCodigo;
	}


	@Column(name = "MODALIDADE_EMPENHO", length = 1, nullable = false)
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioModalidadeEmpenho") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")	
	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return this.modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}


	@Column(name="nro_complemento")
	public Short getNroComplemento() {
		return this.nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}


	@Column(name="nro_contrato")
	public Integer getNroContrato() {
		return this.nroContrato;
	}

	public void setNroContrato(Integer nroContrato) {
		this.nroContrato = nroContrato;
	}


	@Column(name="nro_empenho")
	public String getNroEmpenho() {
		return this.nroEmpenho;
	}

	public void setNroEmpenho(String nroEmpenho) {
		this.nroEmpenho = nroEmpenho;
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
	
	
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}


	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
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


	@Column(name="sequencia_alteracao")
	public Short getSequenciaAlteracao() {
		return this.sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Short sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}


	


	@Column(name="valor_empenho")
	public Double getValorEmpenho() {
		return this.valorEmpenho;
	}

	public void setValorEmpenho(Double valorEmpenho) {
		this.valorEmpenho = valorEmpenho;
	}


	@Column(name="valor_frete")
	public BigDecimal getValorFrete() {
		return this.valorFrete;
	}

	public void setValorFrete(BigDecimal valorFrete) {
		this.valorFrete = valorFrete;
	}


	@OneToOne
	@JoinColumn(name="numero", insertable=false, updatable=false)
	public ScoAutorizacaoForn getScoAutorizacaoForn() {
		return scoAutorizacaoForn;
	}


	public void setScoAutorizacaoForn(ScoAutorizacaoForn scoAutorizacaoForn) {
		this.scoAutorizacaoForn = scoAutorizacaoForn;
	}


//	@OneToMany(mappedBy="scoAfJn")
	@Transient
	public List<ScoItemAutorizacaoFornJn> getItensAutorizacaoForn() {
		return itensAutorizacaoForn;
	}

	public void setItensAutorizacaoForn(
			List<ScoItemAutorizacaoFornJn> itensAutorizacaoForn) {
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
	
	@Transient
	public List<ScoAfEmpenho> getAfEmpenho() {
		return this.scoAutorizacaoForn.getAfEmpenho();
	}

	public void setDtVenctoContrato(Date dtVenctoContrato) {
		this.dtVenctoContrato = dtVenctoContrato;
	}

	@Column(name="dt_vencto_contrato")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtVenctoContrato() {
		return dtVenctoContrato;
	} 
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		if (!(obj instanceof ScoAutorizacaoFornJn)){
			return false;
		}
		ScoAutorizacaoFornJn other = (ScoAutorizacaoFornJn) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		
		return true;
	}


}