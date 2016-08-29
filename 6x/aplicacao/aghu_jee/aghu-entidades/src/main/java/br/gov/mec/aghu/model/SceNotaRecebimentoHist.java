package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_nota_recebimentos database table.
 * 
 */
@Entity
@Table(name="SCE_NOTA_RECEBIMENTOS", schema="HIST")
@SequenceGenerator(name = "sceNrsSq1", sequenceName = "HIST.SCE_NRS_SQ1") 
public class SceNotaRecebimentoHist extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 827844589111188320L;
	private Integer seq;
	private Date dtDebitoNotaRecebimento;
	private Date dtEstorno;
	private Date dtGeracao;
	private Date dtLiberacaoTrib;
	private Long frfCodigo;
	private Boolean debitoNotaRecebimento;
	private Boolean estorno;
	private Boolean indGerado;
	private Boolean indImpresso;
	private Boolean indSaldoEmpenho;
	private Boolean indTribLiberada;
	private Boolean indTributacao;
	private String numeroEmpenhoSiafi;
	private RapServidores servidorGeracao;
	private RapServidores servidorEstorno;
	private RapServidores servidorDebito;
	private RapServidores servidorLibTrib;
	private SceTipoMovimento tipoMovimento;	
	private Integer version;
	private ScoAutorizacaoForn autorizacaoFornecimento; 
	private SceDocumentoFiscalEntrada documentoFiscalEntrada;
	private Set<SceItemNotaRecebimentoHist> itemNotaRecebimentoHist;
	private SceNotaRecebProvisorio notaRecebProvisorio;
	private List<SceMovimentoMaterial> movimentoMaterial;
	private List<FcpTitulo> titulos;
	private List<SceBoletimOcorrencias> boletimOcorrencias;

    public SceNotaRecebimentoHist() {
    }

    
	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceNrsSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name="DT_DEBITO_NR")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtDebitoNotaRecebimento() {
		return this.dtDebitoNotaRecebimento;
	}

	public void setDtDebitoNotaRecebimento(Date dtDebitoNotaRecebimento) {
		this.dtDebitoNotaRecebimento = dtDebitoNotaRecebimento;
	}

	@Column(name="DT_ESTORNO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}

	@Column(name="DT_GERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Column(name="DT_LIBERACAO_TRIB")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtLiberacaoTrib() {
		return this.dtLiberacaoTrib;
	}

	public void setDtLiberacaoTrib(Date dtLiberacaoTrib) {
		this.dtLiberacaoTrib = dtLiberacaoTrib;
	}


	@Column(name="FRF_CODIGO")
	public Long getFrfCodigo() {
		return this.frfCodigo;
	}

	public void setFrfCodigo(Long frfCodigo) {
		this.frfCodigo = frfCodigo;
	}


	@Column(name="IND_DEBITO_NR", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getDebitoNotaRecebimento() {
		return this.debitoNotaRecebimento;
	}

	public void setDebitoNotaRecebimento(Boolean debitoNotaRecebimento) {
		this.debitoNotaRecebimento = debitoNotaRecebimento;
	}


	@Column(name="IND_ESTORNO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean  getEstorno() {
		return this.estorno;
	}

	public void setEstorno(Boolean  estorno) {
		this.estorno = estorno;
	}


	@Column(name="IND_GERADO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean  getIndGerado() {
		return this.indGerado;
	}

	public void setIndGerado(Boolean  indGerado) {
		this.indGerado = indGerado;
	}


	@Column(name="IND_IMPRESSO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImpresso() {
		return this.indImpresso;
	}

	public void setIndImpresso(Boolean indImpresso) {
		this.indImpresso = indImpresso;
	}


	@Column(name="IND_SALDO_EMPENHO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndSaldoEmpenho() {
		return this.indSaldoEmpenho;
	}

	public void setIndSaldoEmpenho(Boolean indSaldoEmpenho) {
		this.indSaldoEmpenho = indSaldoEmpenho;
	}


	@Column(name="IND_TRIB_LIBERADA", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTribLiberada() {
		return this.indTribLiberada;
	}

	public void setIndTribLiberada(Boolean indTribLiberada) {
		this.indTribLiberada = indTribLiberada;
	}


	@Column(name="IND_TRIBUTACAO", length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTributacao() {
		return this.indTributacao;
	}

	public void setIndTributacao(Boolean indTributacao) {
		this.indTributacao = indTributacao;
	}

	@ManyToOne
	@JoinColumn(name="NRP_SEQ")
	public SceNotaRecebProvisorio getNotaRecebProvisorio() {
		return this.notaRecebProvisorio;
	}

	public void setNotaRecebProvisorio(SceNotaRecebProvisorio notaRecebProvisorio) {
		this.notaRecebProvisorio = notaRecebProvisorio;
	}


	@Column(name="NUMERO_EMPENHO_SIAFI")
	public String getNumeroEmpenhoSiafi() {
		return this.numeroEmpenhoSiafi;
	}

	public void setNumeroEmpenhoSiafi(String numeroEmpenhoSiafi) {
		this.numeroEmpenhoSiafi = numeroEmpenhoSiafi;
	}

	//bi-directional many-to-one association to RapServidores
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorGeracao() {
		return this.servidorGeracao;
	}
	
    
  //bi-directional many-to-one association to RapServidores
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_DEBITADO", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_DEBITADO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorDebito() {
		return this.servidorDebito;
	}

  //bi-directional many-to-one association to RapServidores
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_ESTORNADO", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_ESTORNADO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorEstorno() {
		return this.servidorEstorno;
	}
    
    //bi-directional many-to-one association to RapServidores
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_LIB_TRIB", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_LIB_TRIB", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorLibTrib() {
		return this.servidorLibTrib;
	}

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "TMV_SEQ", referencedColumnName = "SEQ"),
			@JoinColumn(name = "TMV_COMPLEMENTO", referencedColumnName = "COMPLEMENTO")})
	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}

	public void setTipoMovimento(SceTipoMovimento sceTipoMovimento) {
		this.tipoMovimento = sceTipoMovimento;
	}

	public void setServidorGeracao(RapServidores servidor) {
		this.servidorGeracao = servidor;
	}

	public void setServidorEstorno(RapServidores servidorEstornado) {
		this.servidorEstorno = servidorEstornado;
	}

	public void setServidorDebito(RapServidores servidorDebitado) {
		this.servidorDebito = servidorDebitado;
	}

	public void setServidorLibTrib(RapServidores servidorLibTrib) {
		this.servidorLibTrib = servidorLibTrib;
	}
	
	@Transient
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne
	@JoinColumn(name="AFN_NUMERO")
	public ScoAutorizacaoForn getAutorizacaoFornecimento() {
		return autorizacaoFornecimento;
	}

	public void setAutorizacaoFornecimento(ScoAutorizacaoForn autorizacaoFornecedor) {
		this.autorizacaoFornecimento = autorizacaoFornecedor;
	}

	@ManyToOne
	@JoinColumn(name="DFE_SEQ")
	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return documentoFiscalEntrada;
	}

	public void setDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) {
		this.documentoFiscalEntrada = sceDocumentoFiscalEntrada;
	}

	
	@OneToMany(fetch=FetchType.LAZY)
	@JoinTable(name = "sce_movimento_materiais", schema = "agh",
			joinColumns = { @JoinColumn(name="NRO_DOC_GERACAO", referencedColumnName="SEQ", insertable=false, updatable=false) },
			inverseJoinColumns = { @JoinColumn(name="SEQ", referencedColumnName="SEQ", insertable=false, updatable=false) })
	public List<SceMovimentoMaterial> getMovimentoMaterial() {
		return movimentoMaterial;
	}

	public void setMovimentoMaterial(List<SceMovimentoMaterial> movimentoMaterial) {
		this.movimentoMaterial = movimentoMaterial;
	}

	@OneToMany(mappedBy="notaRecebimentoHist", fetch=FetchType.LAZY, cascade={CascadeType.ALL})
	public Set<SceItemNotaRecebimentoHist> getItemNotaRecebimentoHist() {
		return itemNotaRecebimentoHist;
	}

	public void setItemNotaRecebimentoHist(Set<SceItemNotaRecebimentoHist> itemNotaRecebimentoHist) {
		this.itemNotaRecebimentoHist = itemNotaRecebimentoHist;
	}

	@OneToMany(mappedBy="notaRecebimento")
	public List<SceBoletimOcorrencias> getBoletimOcorrencias() {
		return boletimOcorrencias;
	}


	public void setBoletimOcorrencias(List<SceBoletimOcorrencias> boletimOcorrencias) {
		this.boletimOcorrencias = boletimOcorrencias;
	}
	
	@Transient
	public void setIndAtivoGerado(Boolean ativo) {
		if (ativo) {
			this.indGerado=Boolean.TRUE;
		} else {
			this.indGerado=Boolean.FALSE;
		}
	}
	
	@Transient
	public Boolean getIndAtivoGerado() {
		
		if (this.seq != null) {
			return this.indGerado.equals(DominioSimNao.S);
		}
		return false;
	}	
	
	@Transient
	public Boolean getIndAtivoDebito() {
		
		if (this.seq != null) {
			return this.debitoNotaRecebimento.equals(DominioSimNao.S);
		}
		return false;
	}

	public enum Fields {
		NUMERO_NR("seq"),
		DATA_ESTORNO("dtEstorno"),
		SERVIDOR_GERACAO("servidorGeracao"),
		SERVIDOR_ESTORNO("servidorEstorno"),
		SERVIDOR_DEBITADO("servidorDebito"),
		SERVIDOR_LIB("servidorLibTrib"),
		DOCUMENTO_FISCAL_ENTRADA("documentoFiscalEntrada"),
		IND_ESTORNO("estorno"),
		IND_DEBITO("debitoNotaRecebimento"),
		AUTORIZACAO_FORN("autorizacaoFornecimento"),
		AFN_NUMERO("autorizacaoFornecimento.numero"),
		ITEM_NOTA_RECEBIMENTO("itemNotaRecebimentoHist"),
		TIPO_MOVIMENTO("tipoMovimento"),
		IND_GERADO("indGerado"),
		NOTA_RECEB_PROVISORIO("notaRecebProvisorio"),
		 NRP_SEQ("notaRecebProvisorio.seq"),
		DATA_GERACAO("dtGeracao"),
		MOVIMENTO_MATERIAL("movimentoMaterial"),		
		NUMERO_EMPENHO_SIAFI("numeroEmpenhoSiafi"),
		TITULO("titulos"),
		DT_DEBITO_NR("dtDebitoNotaRecebimento"),
		DT_LIBERACAO_TRIB("dtLiberacaoTrib"),
		FRF_CODIGO("frfCodigo"),
		IND_IMPRESSO("indImpresso"),
		IND_SALDO_EMPENHO("indSaldoEmpenho"),
		IND_TRIB_LIBERADA("indTribLiberada"),
		IND_TRIBUTACAO("indTributacao"),
		BOLETIM_OCORRENCIAS("boletimOcorrencias"); 
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@OneToMany(mappedBy="notaRecebimentoHist", fetch=FetchType.LAZY)
	public List<FcpTitulo> getTitulos() {
		return titulos;
	}

	public void setTitulos(List<FcpTitulo> titulos) {
		this.titulos = titulos;
	}

	// ##### GeradorEqualsHashCodeMain #####
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
		if (!(obj instanceof SceNotaRecebimentoHist)) {
			return false;
		}
		SceNotaRecebimentoHist other = (SceNotaRecebimentoHist) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}