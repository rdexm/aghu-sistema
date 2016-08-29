package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the SCE_ESL database table.
 * 
 */
@Entity
@SequenceGenerator(name="sceEslSq1", sequenceName="AGH.SCE_ESL_SQ1", allocationSize = 1)
@Table(name = "SCE_ESL", schema = "AGH")
public class SceEntrSaidSemLicitacao extends BaseEntitySeq<Integer> implements Serializable {
	private static final long serialVersionUID = 835816903403246593L;

	private Integer seq;
	private Date dtGeracao;
	private RapServidores servidor;
	private SceTipoMovimento sceTipoMovimento;
	private String indAdiantamentoAf;
	private Date dtPrevEntrega;
	private SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada;
	private ScoFornecedor scoFornecedor;
	private ScoFornecedor scoFornecedorTransp;
	private ScoFornecedor scoFornecedor2;
	private String contato;
	private Long foneContato;
	private String localEntrega;
	private Boolean indEncerrado;
	private Date dtEncerramento;
	private RapServidores servidorEncerrado;
	private String indEfetivado;
	private Date dtEfetivacao;
	private RapServidores servidorEfetivado;
	private Boolean indEstorno;
	private Date dtEstorno;
	private RapServidores servidorEstornado;
	private Date dtPrevDevolucao;
	private String indFormaDevolucao;
	private Integer eslSeqOrigem;
	private Integer nroProjeto;
	private String observacao;
	private Integer version;
	private Set<SceItemEntrSaidSemLicitacao> itemEntrSaidSemLicitacao;
	private Integer tmvSeq;
	private SceEntrSaidSemLicitacao sceEntrSaidSemLicitacaoOrigem;
	
	private Set<ScoRecusaMaterial> recusaMaterial = new HashSet<ScoRecusaMaterial>(0);
	
	
	public SceEntrSaidSemLicitacao() {
	} 

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceEslSq1")
	@Column(name = "SEQ", unique = true, nullable = false, scale = 0)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getContato() {
		return this.contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_EFETIVACAO", length = 7)
	public Date getDtEfetivacao() {
		return this.dtEfetivacao;
	}

	public void setDtEfetivacao(Date dtEfetivacao) {
		this.dtEfetivacao = dtEfetivacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ENCERRAMENTO")
	public Date getDtEncerramento() {
		return this.dtEncerramento;
	}

	public void setDtEncerramento(Date dtEncerramento) {
		this.dtEncerramento = dtEncerramento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_ESTORNO")
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_GERACAO", nullable = false)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_PREV_DEVOLUCAO", length = 7)
	public Date getDtPrevDevolucao() {
		return this.dtPrevDevolucao;
	}

	public void setDtPrevDevolucao(Date dtPrevDevolucao) {
		this.dtPrevDevolucao = dtPrevDevolucao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_PREV_ENTREGA")
	public Date getDtPrevEntrega() {
		return this.dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}

	@Column(name = "ESL_SEQ_ORIGEM", nullable = true, precision = 7, scale = 0)
	public Integer getEslSeqOrigem() {
		return this.eslSeqOrigem;
	}

	public void setEslSeqOrigem(Integer eslSeqOrigem) {
		this.eslSeqOrigem = eslSeqOrigem;
	}

	@Column(name = "FONE_CONTATO")
	public Long getFoneContato() {
		return this.foneContato;
	}

	public void setFoneContato(Long foneContato) {
		this.foneContato = foneContato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", referencedColumnName = "NUMERO")
	public ScoFornecedor getScoFornecedor() {
		return this.scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO_TRANSP", referencedColumnName = "NUMERO")
	public ScoFornecedor getScoFornecedorTransp() {
		return this.scoFornecedorTransp;
	}

	public void setScoFornecedorTransp(ScoFornecedor scoFornecedorTransp) {
		this.scoFornecedorTransp = scoFornecedorTransp;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO2", referencedColumnName = "NUMERO")
	public ScoFornecedor getScoFornecedor2() {
		return this.scoFornecedor2;
	}

	public void setScoFornecedor2(ScoFornecedor scoFornecedor2) {
		this.scoFornecedor2 = scoFornecedor2;
	}

	@Column(name = "IND_ADIANTAMENTO_AF")
	public String getIndAdiantamentoAf() {
		return this.indAdiantamentoAf;
	}

	public void setIndAdiantamentoAf(String indAdiantamentoAf) {
		this.indAdiantamentoAf = indAdiantamentoAf;
	}

	@Column(name = "IND_EFETIVADO")
	public String getIndEfetivado() {
		return this.indEfetivado;
	}

	public void setIndEfetivado(String indEfetivado) {
		this.indEfetivado = indEfetivado;
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

	@Column(name = "IND_FORMA_DEVOLUCAO")
	public String getIndFormaDevolucao() {
		return this.indFormaDevolucao;
	}

	public void setIndFormaDevolucao(String indFormaDevolucao) {
		this.indFormaDevolucao = indFormaDevolucao;
	}

	@Column(name = "LOCAL_ENTREGA")
	public String getLocalEntrega() {
		return this.localEntrega;
	}

	public void setLocalEntrega(String localEntrega) {
		this.localEntrega = localEntrega;
	}

	@Column(name = "NRO_PROJETO")
	public Integer getNroProjeto() {
		return this.nroProjeto;
	}

	public void setNroProjeto(Integer nroProjeto) {
		this.nroProjeto = nroProjeto;
	}

	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_EFETIVADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_EFETIVADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEfetivado() {
		return this.servidorEfetivado;
	}

	public void setServidorEfetivado(RapServidores servidorEfetivado) {
		this.servidorEfetivado = servidorEfetivado;
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

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "TMV_SEQ", referencedColumnName = "SEQ", nullable = false),
			@JoinColumn(name = "TMV_COMPLEMENTO", referencedColumnName = "COMPLEMENTO", nullable = false) })
	public SceTipoMovimento getSceTipoMovimento() {
		return this.sceTipoMovimento;
	}

	public void setSceTipoMovimento(SceTipoMovimento sceTipoMovimento) {
		this.sceTipoMovimento = sceTipoMovimento;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DFE_SEQ", referencedColumnName = "SEQ")
	public SceDocumentoFiscalEntrada getSceDocumentoFiscalEntrada() {
		return this.sceDocumentoFiscalEntrada;
	}

	public void setSceDocumentoFiscalEntrada(SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) {
		this.sceDocumentoFiscalEntrada = sceDocumentoFiscalEntrada;

	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sceEntrSaidSemLicitacao")
	@Cascade(CascadeType.DELETE)
	public Set<SceItemEntrSaidSemLicitacao> getItemEntrSaidSemLicitacao() {
		return itemEntrSaidSemLicitacao;
	}

	public void setItemEntrSaidSemLicitacao(
			Set<SceItemEntrSaidSemLicitacao> itemEntrSaidSemLicitacao) {
		this.itemEntrSaidSemLicitacao = itemEntrSaidSemLicitacao;
	}
	
	@Column(name="TMV_SEQ", insertable = false, updatable = false)
	public Integer getTmvSeq() {
		return this.tmvSeq;
	}

	public void setTmvSeq(Integer tmvSeq) {
		this.tmvSeq = tmvSeq;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESL_SEQ_ORIGEM", referencedColumnName = "SEQ", insertable=false, updatable=false)
	public SceEntrSaidSemLicitacao getSceEntrSaidSemLicitacaoOrigem() {
		return sceEntrSaidSemLicitacaoOrigem;
	}

	public void setSceEntrSaidSemLicitacaoOrigem(
			SceEntrSaidSemLicitacao sceEntrSaidSemLicitacaoOrigem) {
		this.sceEntrSaidSemLicitacaoOrigem = sceEntrSaidSemLicitacaoOrigem;
	}

	public enum Fields {

		SEQ("seq"), //
		CODIGO_MATERIAL("scoMaterial.codigo"), //
		DOCUMENTO_FISCAL_ENTRADA("sceDocumentoFiscalEntrada"), //
		DATA_GERACAO("dtGeracao"), //
		IND_ESTORNO("indEstorno"), //
		CONTATO("contato"),
		ESL_SEQ_ORIGEM("eslSeqOrigem"),//incluido pela estoria #11384
		TIPO_MOVIMENTO("sceTipoMovimento"), //
		FORNECEDOR("scoFornecedor"), //
		FORNECEDOR_NUMERO("scoFornecedor.numero"),
		TIPO_MOVIMENTO_SEQ("sceTipoMovimento.id.seq"), //
		IND_ENCERRADO("indEncerrado"),
		IND_ADIANTAMENTO_AF("indAdiantamentoAf"),
		IND_EFETIVADO("indEfetivado"),
		DFE_SEQ("sceDocumentoFiscalEntrada.seq"),
		FRN_NUMERO("scoFornecedor.numero"),
		ITEM_ESL("itemEntrSaidSemLicitacao"),//alterado #42423 mapeamento certo @OneToMany
		ITEM_ENT_SAIDA_SEM_LIC("itemEntrSaidSemLicitacao"),
		TMV_SEQ("tmvSeq"),
		SERVIDOR("servidor"),
		DATA_EFETIVACAO("dtEfetivacao"),
		ESL_ORIGEM("sceEntrSaidSemLicitacaoOrigem"),
		RECUSA_MATERIAL("recusaMaterial"),
		NRO_PROJETO("nroProjeto"),
		FORNECEDOR_TRANSPORTADORA("scoFornecedorTransp");//
		

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
		if (!(obj instanceof SceEntrSaidSemLicitacao)) {
			return false;
		}
		SceEntrSaidSemLicitacao other = (SceEntrSaidSemLicitacao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}

	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "sceEsl")
	public Set<ScoRecusaMaterial> getRecusaMaterial() {
		return recusaMaterial;
	}

	public void setRecusaMaterial(Set<ScoRecusaMaterial> recusaMaterial) {
		this.recusaMaterial = recusaMaterial;
	}
}