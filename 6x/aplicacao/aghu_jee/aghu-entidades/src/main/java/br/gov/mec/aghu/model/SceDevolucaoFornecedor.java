package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.sql.Timestamp;
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
import javax.persistence.Version;

import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_devolucao_fornecedores database table.
 * 
 */
@Entity
@Table(name="SCE_DEVOLUCAO_FORNECEDORES")
public class SceDevolucaoFornecedor extends BaseEntitySeq<Integer> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -1757225917503354429L;
	private Integer seq;
	private Integer bocSeq;
	//private Integer dfeSeq;
	private Timestamp dtEmissaoNfd;
	private Timestamp dtEstorno;
	private Timestamp dtFechamentoNfd;
	private Timestamp dtGeracao;
	private Boolean indEstorno;
	private Boolean indTramiteInterno;
	private String indGerado;
	private String motivo;
	private Integer nroNfDevolucao;
	private RapServidores servidor;
	private RapServidores servidorEstornado;
	private String serieNfDevolucao;
	private Integer tmvComplemento;
	private Integer tmvSeq;
	private Integer version;
	private Set<SceItemDevolucaoFornecedor> sceItemDfs;
	private SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada;
	private SceTipoMovimento tipoMovimento;
	
	public enum Fields{
		SEQ("seq"),
		IND_ESTORNO("indEstorno"),
		ITEM_DFS("sceItemDfs"),
		DATA_GERACAO("dtGeracao"),
		DOCUMENTO_FISCAL_ENTRADA("sceDocumentoFiscalEntrada"),
		TIPO_MOVIMENTO("tipoMovimento"),
		DFE_SEQ("sceDocumentoFiscalEntrada.seq"),
		IND_TRAMITE_INTERNO("indTramiteInterno");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

    public SceDevolucaoFornecedor() {
    }


	@Id
	@SequenceGenerator(name="SCE_DEVOLUCAO_FORNECEDORES_SEQ_GENERATOR", sequenceName="AGH.SCE_DFS_SQ1", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCE_DEVOLUCAO_FORNECEDORES_SEQ_GENERATOR")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@Column(name="BOC_SEQ")
	public Integer getBocSeq() {
		return this.bocSeq;
	}

	public void setBocSeq(Integer bocSeq) {
		this.bocSeq = bocSeq;
	}


	/*@Column(name="DFE_SEQ")
	public Integer getDfeSeq() {
		return this.dfeSeq;
	}

	public void setDfeSeq(Integer dfeSeq) {
		this.dfeSeq = dfeSeq;
	}
*/

	@Column(name="DT_EMISSAO_NFD")
	public Timestamp getDtEmissaoNfd() {
		return this.dtEmissaoNfd;
	}

	public void setDtEmissaoNfd(Timestamp dtEmissaoNfd) {
		this.dtEmissaoNfd = dtEmissaoNfd;
	}


	@Column(name="DT_ESTORNO")
	public Timestamp getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Timestamp dtEstorno) {
		this.dtEstorno = dtEstorno;
	}


	@Column(name="DT_FECHAMENTO_NFD")
	public Timestamp getDtFechamentoNfd() {
		return this.dtFechamentoNfd;
	}

	public void setDtFechamentoNfd(Timestamp dtFechamentoNfd) {
		this.dtFechamentoNfd = dtFechamentoNfd;
	}


	@Column(name="DT_GERACAO")
	public Timestamp getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Timestamp dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Column(name="IND_ESTORNO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstorno() {
		return indEstorno;
	}
	
	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}

	@Column(name="IND_TRAMITE_INTERNO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTramiteInterno() {
		return indTramiteInterno;
	}
	
	public void setIndTramiteInterno(Boolean indTramiteInterno) {
		this.indTramiteInterno = indTramiteInterno;
	}

	@Column(name="IND_GERADO")
	public String getIndGerado() {
		return this.indGerado;
	}

	public void setIndGerado(String indGerado) {
		this.indGerado = indGerado;
	}


	public String getMotivo() {
		return this.motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}


	@Column(name="NRO_NF_DEVOLUCAO")
	public Integer getNroNfDevolucao() {
		return this.nroNfDevolucao;
	}

	public void setNroNfDevolucao(Integer nroNfDevolucao) {
		this.nroNfDevolucao = nroNfDevolucao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_ESTORNADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ESTORNADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEstornado() {
		return this.servidorEstornado;
	}

	public void setServidorEstornado(RapServidores servidorEstornado) {
		this.servidorEstornado = servidorEstornado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "TMV_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false),
			@JoinColumn(name = "TMV_COMPLEMENTO", referencedColumnName = "COMPLEMENTO", insertable = false, updatable = false) })
	public SceTipoMovimento getTipoMovimento() {
		return this.tipoMovimento;
	}
	
	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}

	@Column(name="SERIE_NF_DEVOLUCAO")
	public String getSerieNfDevolucao() {
		return this.serieNfDevolucao;
	}

	public void setSerieNfDevolucao(String serieNfDevolucao) {
		this.serieNfDevolucao = serieNfDevolucao;
	}


	@Column(name="TMV_COMPLEMENTO")
	public Integer getTmvComplemento() {
		return this.tmvComplemento;
	}

	public void setTmvComplemento(Integer tmvComplemento) {
		this.tmvComplemento = tmvComplemento;
	}


	@Column(name="TMV_SEQ")
	public Integer getTmvSeq() {
		return this.tmvSeq;
	}

	public void setTmvSeq(Integer tmvSeq) {
		this.tmvSeq = tmvSeq;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to SceItemDf
	@OneToMany(mappedBy="sceDevolucaoFornecedor")
	public Set<SceItemDevolucaoFornecedor> getSceItemDfs() {
		return this.sceItemDfs;
	}

	public void setSceItemDfs(Set<SceItemDevolucaoFornecedor> sceItemDfs) {
		this.sceItemDfs = sceItemDfs;
	}
	
	//bi-directional many-to-one association to SceDocumentoFiscalEntrada
    @ManyToOne
	@JoinColumn(name="DFE_SEQ")
	public SceDocumentoFiscalEntrada getSceDocumentoFiscalEntrada() {
		return this.sceDocumentoFiscalEntrada;
	}

	public void setSceDocumentoFiscalEntrada(SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) {
		this.sceDocumentoFiscalEntrada = sceDocumentoFiscalEntrada;
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
		if (!(obj instanceof SceDevolucaoFornecedor)) {
			return false;
		}
		SceDevolucaoFornecedor other = (SceDevolucaoFornecedor) obj;
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