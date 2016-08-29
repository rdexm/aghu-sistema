package br.gov.mec.aghu.model;

import java.io.Serializable;
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


import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sco_iaf_jn database table.
 * 
 */
@Entity
@Table(name="sco_iaf_jn")
public class ScoItemAutorizacaoFornJn extends BaseEntitySeq<Long> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -1608216076314253289L;
	private Long seqJn;
//	private ScoAutorizacaoFornJn scoAfJn;
	private Integer afnNumero;
	private Date dtEstorno;
	private Date dtExclusao;
	private Integer fatorConversao;
	private Boolean indConsignado;
	private Boolean indContrato;
	private Boolean indEstorno;
	private Boolean indExclusao;
	private Boolean indRecebimento;
	private DominioSituacaoAutorizacaoFornecedor indSituacao;
	private Date jnDateTime;
	private String jnOperation;
	private String jnUser;
	private Integer numero;
	private Double percAcrescimo;
	private Double percAcrescimoItem;
	private Double percDesconto;
	private Double percDescontoItem;
	private Double percIpi;
	private Double percVarPreco;
	private Integer qtdeRecebida;
	private Integer qtdeRecebidaAtual;
	private Integer qtdeSolicitada;
	private Integer sequenciaAlteracao;
	private Integer serMatricula;
	private Integer serMatriculaEstorno;
	private Integer serVinCodigo;
	private Integer serVinCodigoEstorno;
	
	private ScoUnidadeMedida unidadeMedida;
	private Double valorEfetivado;
	private Double valorEfetivadoAtual;
	private Double valorUnitario;
	private ScoItemPropostaFornecedor itemPropostaFornecedor;
	private ScoNomeComercial nomeComercial;
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo marcaModelo;
	private ScoItemAutorizacaoForn scoItemAutorizacaoForn;
	
	public enum Fields{
		ID("seqJn"),
		IND_SITUACAO("indSituacao"),
		NUMERO("numero"),
		SEQUENCIA_ALTERACAO("sequenciaAlteracao"),
		QTDE_SOLICITADA("qtdeSolicitada"),
		QTDE_RECEBIDA("qtdeRecebida"), 
		VALOR_UNITARIO("valorUnitario"),
		VALOR_EFETIVADO("valorEfetivado"),
		PERC_DESCONTO_ITEM("percDescontoItem"), 
		PERC_DESCONTO("percDesconto"), 
		PERC_ACRESCIMO_ITEM("percAcrescimo"),
		PERC_ACRESCIMO("percAcrescimo"),
		PERC_IPI("percIpi"),
		MCM_CODIGO("marcaComercial.mcmCodigo"), 
		NC_MCM_CODIGO("nomeComercial.ncMcmCodigo"),
	    NC_NUMERO("nomeComercial.ncNumero"), 
		UMD_CODIGO("unidadeMedida.codigo"),
		AUTORIZACAO_FORN_JN_ID("scoAfJn.numero"),
		FATOR_CONVERSAO("fatorConversao"),
		IND_EXCLUSAO("indExclusao"),
		AFN_NUMERO("afnNumero"),
		ITEM_AUTORIZACAO_FORN("scoItemAutorizacaoForn"),
		ITEM_PROPOSTA_FORNECEDOR("itemPropostaFornecedor");
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

    public ScoItemAutorizacaoFornJn() {
    }


	@Id
	@SequenceGenerator(name="SCO_IAF_JN_SEQJN_GENERATOR", sequenceName="SCO_IAF_JN_SQ1", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCO_IAF_JN_SEQJN_GENERATOR")
	@Column(name="seq_jn")
	public Long getSeqJn() {
		return this.seqJn;
	}

	public void setSeqJn(Long seqJn) {
		this.seqJn = seqJn;
	}

	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="afn_numero",referencedColumnName="numero", updatable=false, insertable=false)
	public ScoAutorizacaoFornJn getScoAfJn() {
		return this.scoAfJn;
	}

	public void setScoAfJn(ScoAutorizacaoFornJn afnNumero) {
		this.scoAfJn = afnNumero;
	} */
	
	@Column(name="afn_numero")
	public Integer getAfnNumero() {
		return afnNumero;
	}


	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
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


	@Column(name="fator_conversao")
	public Integer getFatorConversao() {
		return this.fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}


	@Column(name="ind_consignado")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsignado() {
		return this.indConsignado;
	}

	public void setIndConsignado(Boolean indConsignado) {
		this.indConsignado = indConsignado;
	}


	@Column(name="ind_contrato")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndContrato() {
		return this.indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}


	@Column(name="ind_estorno")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstorno() {
		return this.indEstorno;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}


	@Column(name="ind_exclusao")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExclusao() {
		return this.indExclusao;
	}

	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}


	@Column(name="ind_recebimento")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRecebimento() {
		return this.indRecebimento;
	}

	public void setIndRecebimento(Boolean indRecebimento) {
		this.indRecebimento = indRecebimento;
	}


	@Column(name="ind_situacao")
	@Enumerated(EnumType.STRING)
	public DominioSituacaoAutorizacaoFornecedor getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoAutorizacaoFornecedor indSituacao) {
		this.indSituacao = indSituacao;
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


	

	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}


	@Column(name="perc_acrescimo")
	public Double getPercAcrescimo() {
		return this.percAcrescimo;
	}

	public void setPercAcrescimo(Double percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}


	@Column(name="perc_acrescimo_item")
	public Double getPercAcrescimoItem() {
		return this.percAcrescimoItem;
	}

	public void setPercAcrescimoItem(Double percAcrescimoItem) {
		this.percAcrescimoItem = percAcrescimoItem;
	}


	@Column(name="perc_desconto")
	public Double getPercDesconto() {
		return this.percDesconto;
	}

	public void setPercDesconto(Double percDesconto) {
		this.percDesconto = percDesconto;
	}


	@Column(name="perc_desconto_item")
	public Double getPercDescontoItem() {
		return this.percDescontoItem;
	}

	public void setPercDescontoItem(Double percDescontoItem) {
		this.percDescontoItem = percDescontoItem;
	}


	@Column(name="perc_ipi")
	public Double getPercIpi() {
		return this.percIpi;
	}

	public void setPercIpi(Double percIpi) {
		this.percIpi = percIpi;
	}


	@Column(name="perc_var_preco")
	public Double getPercVarPreco() {
		return this.percVarPreco;
	}

	public void setPercVarPreco(Double percVarPreco) {
		this.percVarPreco = percVarPreco;
	}


	@Column(name="qtde_recebida")
	public Integer getQtdeRecebida() {
		return this.qtdeRecebida;
	}

	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}


	@Column(name="qtde_recebida_atual")
	public Integer getQtdeRecebidaAtual() {
		return this.qtdeRecebidaAtual;
	}

	public void setQtdeRecebidaAtual(Integer qtdeRecebidaAtual) {
		this.qtdeRecebidaAtual = qtdeRecebidaAtual;
	}


	@Column(name="qtde_solicitada")
	public Integer getQtdeSolicitada() {
		return this.qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}


	@Column(name="sequencia_alteracao")
	public Integer getSequenciaAlteracao() {
		return this.sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Integer sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}


	@Column(name="ser_matricula")
	public Integer getSerMatricula() {
		return this.serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}


	@Column(name="ser_matricula_estorno")
	public Integer getSerMatriculaEstorno() {
		return this.serMatriculaEstorno;
	}

	public void setSerMatriculaEstorno(Integer serMatriculaEstorno) {
		this.serMatriculaEstorno = serMatriculaEstorno;
	}


	@Column(name="ser_vin_codigo")
	public Integer getSerVinCodigo() {
		return this.serVinCodigo;
	}

	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}


	@Column(name="ser_vin_codigo_estorno")
	public Integer getSerVinCodigoEstorno() {
		return this.serVinCodigoEstorno;
	}

	public void setSerVinCodigoEstorno(Integer serVinCodigoEstorno) {
		this.serVinCodigoEstorno = serVinCodigoEstorno;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", referencedColumnName = "CODIGO")
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}



	@Column(name="valor_efetivado")
	public Double getValorEfetivado() {
		return this.valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}


	@Column(name="valor_efetivado_atual")
	public Double getValorEfetivadoAtual() {
		return this.valorEfetivadoAtual;
	}

	public void setValorEfetivadoAtual(Double valorEfetivadoAtual) {
		this.valorEfetivadoAtual = valorEfetivadoAtual;
	}


	@Column(name="valor_unitario")
	public Double getValorUnitario() {
		return this.valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	
	//bi-directional many-to-one association to ScoItemPropostasFornecedor
		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumns({
			@JoinColumn(name="IPF_NUMERO", referencedColumnName="NUMERO"),
			@JoinColumn(name="IPF_PFR_FRN_NUMERO", referencedColumnName="PFR_FRN_NUMERO"),
			@JoinColumn(name="IPF_PFR_LCT_NUMERO", referencedColumnName="PFR_LCT_NUMERO")
			})
		public ScoItemPropostaFornecedor getItemPropostaFornecedor() {
			return itemPropostaFornecedor;
		}
		
		public void setItemPropostaFornecedor(
				ScoItemPropostaFornecedor itemPropostaFornecedor) {
			this.itemPropostaFornecedor = itemPropostaFornecedor;
		}
		
		
		@ManyToOne(fetch=FetchType.LAZY)
		@JoinColumns({
			@JoinColumn(name="NC_MCM_CODIGO", referencedColumnName="MCM_CODIGO"),
			@JoinColumn(name="NC_NUMERO", referencedColumnName="NUMERO")})
		public ScoNomeComercial getNomeComercial() {
			return nomeComercial;
		}

		public void setNomeComercial(ScoNomeComercial nomeComercial) {
			this.nomeComercial = nomeComercial;
		}
		
		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumn(name = "MCM_CODIGO", referencedColumnName = "CODIGO")
		public ScoMarcaComercial getMarcaComercial() {
			return marcaComercial;
		}

		public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
			this.marcaComercial = marcaComercial;
		}
		
		@ManyToOne(fetch = FetchType.LAZY)
		@JoinColumns({
			@JoinColumn(name = "MOM_MCM_CODIGO", referencedColumnName = "MCM_CODIGO"),
			@JoinColumn(name = "MOM_SEQP", referencedColumnName = "SEQP")})
		public ScoMarcaModelo getMarcaModelo(){
			return marcaModelo;
		}
		
		public void setMarcaModelo(ScoMarcaModelo marcaModelo){
			this.marcaModelo = marcaModelo;
		}

	public void setScoItemAutorizacaoForn(ScoItemAutorizacaoForn scoItemAutorizacaoForn) {
		this.scoItemAutorizacaoForn = scoItemAutorizacaoForn;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "AFN_NUMERO", referencedColumnName = "AFN_NUMERO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "NUMERO", referencedColumnName = "NUMERO", nullable = false, insertable = false, updatable = false) })
	public ScoItemAutorizacaoForn getScoItemAutorizacaoForn() {
		return scoItemAutorizacaoForn;
	}
	

 
 @Transient public Long getSeq(){ return this.getSeqJn();} 
 public void setSeq(Long seq){ this.setSeqJn(seq);}
}