package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_nota_receb_provisorios database table.
 * 
 */
@Entity
@Table(name="SCE_NOTA_RECEB_PROVISORIOS")
public class SceNotaRecebProvisorio extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4876839000999439991L;

	private Integer seq;
	private ScoAutorizacaoFornecedorPedido scoAfPedido;
	private String codCfop;
	private SceDocumentoFiscalEntrada documentoFiscalEntrada;
	private Date dtEstorno;
	private Date dtGeracao;
	private Integer eslSeq;
	private Long frfCodigo;
	private Boolean indConfirmado;
	private Boolean indEstorno;
	private String numeroEmpenhoSiafi;
	private RapServidores servidorEstorno;
	private Integer version;
	private ScoAutorizacaoForn autorizacaoFornecimento;
	private Set<SceItemRecebProvisorio> itemRecebProvisorio;
	private RapServidores servidor;
	private Double valorNotaFiscal;
	private Integer afeAfnNumero;

	private SceNotaRecebimento notaRecebimento;
	
	public enum Fields{
		
		IND_ESTORNO("indEstorno"),
		IND_CONFIRMADO("indConfirmado"),
		DT_GERACAO("dtGeracao"),
		SERVIDOR("servidor"),
		NRP_SEQ("seq"),
		AUTORIZACAO_FORN("autorizacaoFornecimento"),
		AF_PEDIDO("scoAfPedido"),
		AF_PEDIDO_AFN_NUMERO("scoAfPedido.id.afnNumero"),
		DOCUMENTO_FISCAL_ENTRADA("documentoFiscalEntrada"),
		DFE_SEQ("documentoFiscalEntrada.seq"),
		DFE_NUMERO("documentoFiscalEntrada.numero"),
		AFN_NUMERO("autorizacaoFornecimento.numero"),
		ESL_SEQ("eslSeq"),
		ITENS_NOTA_RECEB_PROV("itemRecebProvisorio"),
		NOTA_RECEBIMENTO("notaRecebimento"),
		SCE_ITEM_RECEB_PROVISORIO("itemRecebProvisorio"),
		AFE_AFN_NUMERO("afeAfnNumero"),
		VALOR_NOTA_FISCAL("valorNotaFiscal");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}


    public SceNotaRecebProvisorio() {
    }

//	@SequenceGenerator(name="SCE_NOTA_RECEB_PROVISORIOS_SEQ_GENERATOR" , allocationSize = 1)
//	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCE_NOTA_RECEB_PROVISORIOS_SEQ_GENERATOR")
	@Id
	@Column(name="seq")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "AFE_AFN_NUMERO", referencedColumnName = "AFN_NUMERO"), @JoinColumn(name = "AFE_NUMERO", referencedColumnName = "NUMERO") })
	public ScoAutorizacaoFornecedorPedido getScoAfPedido() {
		return this.scoAfPedido;
	}

	public void setScoAfPedido(ScoAutorizacaoFornecedorPedido scoAfPedido) {
		this.scoAfPedido = scoAfPedido;
	}

	@Column(name = "AFE_AFN_NUMERO", insertable = false, updatable = false)
	public Integer getAfeAfnNumero() {
		return afeAfnNumero;
	}

	public void setAfeAfnNumero(Integer afeAfnNumero) {
		this.afeAfnNumero = afeAfnNumero;
	}

	@Column(name="COD_CFOP")
	public String getCodCfop() {
		return this.codCfop;
	}

	public void setCodCfop(String codCfop) {
		this.codCfop = codCfop;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_ESTORNO")
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_GERACAO")
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	@Column(name="ESL_SEQ")
	public Integer getEslSeq() {
		return this.eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}
	
	@Column(name="FRF_CODIGO")
	public Long getFrfCodigo() {
		return this.frfCodigo;
	}

	public void setFrfCodigo(Long frfCodigo) {
		this.frfCodigo = frfCodigo;
	}


	@Column(name="IND_CONFIRMADO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConfirmado() {
		return this.indConfirmado;
	}

	public void setIndConfirmado(Boolean indConfirmado) {
		this.indConfirmado = indConfirmado;
	}


	@Column(name="IND_ESTORNO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstorno() {
		return this.indEstorno;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}


	@Column(name="NUMERO_EMPENHO_SIAFI")
	public String getNumeroEmpenhoSiafi() {
		return this.numeroEmpenhoSiafi;
	}

	public void setNumeroEmpenhoSiafi(String numeroEmpenhoSiafi) {
		this.numeroEmpenhoSiafi = numeroEmpenhoSiafi;
	}

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_ESTORNO", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_ESTORNO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorEstorno() {
		return servidorEstorno;
	}

	public void setServidorEstorno(RapServidores servidorEstorno) {
		this.servidorEstorno = servidorEstorno;
	}


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
	
	@OneToMany(mappedBy="notaRecebimentoProvisorio", fetch = FetchType.LAZY)
	public Set<SceItemRecebProvisorio> getItemRecebProvisorio() {
		return itemRecebProvisorio;
	}

	public void setItemRecebProvisorio(
			Set<SceItemRecebProvisorio> itemRecebProvisorio) {
		this.itemRecebProvisorio = itemRecebProvisorio;
	}

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne
	@JoinColumn(name = "DFE_SEQ")
	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return documentoFiscalEntrada;
	}

	public void setDocumentoFiscalEntrada(SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		this.documentoFiscalEntrada = documentoFiscalEntrada;
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
		if (!(obj instanceof SceNotaRecebProvisorio)) {
			return false;
		}
		SceNotaRecebProvisorio other = (SceNotaRecebProvisorio) obj;
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

	public void setValorNotaFiscal(Double valorNotaFiscal) {
		this.valorNotaFiscal = valorNotaFiscal;
	}

	@Transient
	public Double getValorNotaFiscal() {
		return valorNotaFiscal;
	}

	public void setNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	// @Transient
	// Mapeamento OneToOne não realizado pois é possível ter duas ou mais
	// ocorrências em
	// banco, o que não deveria acontecer negocialmente.
	// Sendo assim, ao buscar no banco, validamos a ON e lançamos mensagem de
	// não conformidade. Ver estória #28709
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "notaRecebProvisorio")
	public SceNotaRecebimento getNotaRecebimento() {
		return notaRecebimento;
	}
}