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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name="SCO_LOG_GERAC_SC_MAT_ESTOCAVEL", schema="AGH")
@SequenceGenerator(name = "scoLGerSq1", sequenceName = "AGH.SCO_LOG_GERAC_SQ1", allocationSize = 1)
public class ScoLogGeracaoScMatEstocavel extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9186472712607761155L;
	private Integer seq;
	private Integer seqProcesso;
	private Integer qtdeAProcessar;
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private SceAlmoxarifado almoxarifado;
	private Date dtAnalise;
	private ScoMaterial material;
	private Date dtGeracao;
	private RapServidores servidor;
	private String texto;
	private Integer qtdePontoPedido;
	private Integer qtdeSaldoConsignado;
	private Integer qtdeReforcoSc;
	private Integer qtdeEmAf;
	private Integer qtdeEmSc;
	private Integer qtdeEmFundoFixo;
	private Integer qtdeAComprar;
	private Integer qtdeBloqueada;
	private Integer qtdeDisponivel;		
	private Integer version;
		
    public ScoLogGeracaoScMatEstocavel() {
    }
	
	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoLGerSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "SEQ_PROCESSO", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoLGerSq1")
	public Integer getSeqProcesso() {
		return this.seqProcesso;
	}

	public void setSeqProcesso(Integer seqProcesso) {
		this.seqProcesso = seqProcesso;
	}

	@Column(name = "DT_GERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SLC_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "CODIGO")	
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@Column(name = "QTDE_ITENS_A_PROCESSAR", precision = 7, scale = 0)
	public Integer getQtdeAProcessar() {
		return this.qtdeAProcessar;
	}

	public void setQtdeAProcessar(Integer qtdeAProcessar) {
		this.qtdeAProcessar = qtdeAProcessar;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_ANALISE")
	public Date getDtAnalise() {
		return this.dtAnalise;
	}

	public void setDtAnalise(Date dtAnalise) {
		this.dtAnalise = dtAnalise;
	}
	
	@Column(name = "QTDE_PONTOPEDIDO", precision = 7, scale = 0)
	public Integer getQtdePontoPedido() {
		return this.qtdePontoPedido;
	}

	public void setQtdePontoPedido(Integer qtdePontoPedido) {
		this.qtdePontoPedido = qtdePontoPedido;
	}

	@Column(name = "QTDE_SALDOCONSIGNADO", precision = 7, scale = 0)
	public Integer getQtdeSaldoConsignado() {
		return qtdeSaldoConsignado;
	}

	public void setQtdeSaldoConsignado(Integer qtdeSaldoConsignado) {
		this.qtdeSaldoConsignado = qtdeSaldoConsignado;
	}

	@Column(name = "QTDE_REFORCOSC", precision = 7, scale = 0)
	public Integer getQtdeReforcoSc() {
		return qtdeReforcoSc;
	}

	public void setQtdeReforcoSc(Integer qtdeReforcoSc) {
		this.qtdeReforcoSc = qtdeReforcoSc;
	}

	@Column(name = "QTDE_EMAF", precision = 7, scale = 0)
	public Integer getQtdeEmAf() {
		return qtdeEmAf;
	}

	public void setQtdeEmAf(Integer qtdeEmAf) {
		this.qtdeEmAf = qtdeEmAf;
	}

	@Column(name = "QTDE_EMSC", precision = 7, scale = 0)
	public Integer getQtdeEmSc() {
		return qtdeEmSc;
	}

	public void setQtdeEmSc(Integer qtdeEmSc) {
		this.qtdeEmSc = qtdeEmSc;
	}

	@Column(name = "QTDE_EMFF", precision = 7, scale = 0)
	public Integer getQtdeEmFundoFixo() {
		return qtdeEmFundoFixo;
	}

	public void setQtdeEmFundoFixo(Integer qtdeEmFundoFixo) {
		this.qtdeEmFundoFixo = qtdeEmFundoFixo;
	}

	@Column(name = "QTDE_COMPRA", precision = 7, scale = 0)
	public Integer getQtdeAComprar() {
		return qtdeAComprar;
	}

	public void setQtdeAComprar(Integer qtdeAComprar) {
		this.qtdeAComprar = qtdeAComprar;
	}
	
	@Column(name = "TEXTO", length = 255)
	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="ALM_SEQ", referencedColumnName = "SEQ")
	public SceAlmoxarifado getAlmoxarifado() {
		return this.almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
	
	@Column(name = "QTDE_BLOQUEADA", precision = 7, scale = 0)
	public Integer getQtdeBloqueada() {
		return qtdeBloqueada;
	}

	public void setQtdeBloqueada(Integer qtdeBloqueada) {
		this.qtdeBloqueada = qtdeBloqueada;
	}

	@Column(name = "QTDE_DISPONIVEL", precision = 7, scale = 0)
	public Integer getQtdeDisponivel() {
		return qtdeDisponivel;
	}

	public void setQtdeDisponivel(Integer qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}

	@Column(name = "VERSION", length= 7)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public boolean equals(Object other) {

		if (!(other instanceof ScoLogGeracaoScMatEstocavel)) {
			return false;
		}

		ScoLogGeracaoScMatEstocavel castOther = (ScoLogGeracaoScMatEstocavel) other;

		return new EqualsBuilder().append(this.seq, castOther.getSeq())
				.isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
	
	
	public enum Fields {
		SEQ("seq"), 
		SEQ_PROCESSO("seqProcesso"),
		DT_GERACAO("dtGeracao"),
		SERVIDOR("servidor"),
		ALM_SEQ("almoxarifado.seq"),
		ALMOXARIFADO("almoxarifado"),
		MAT_CODIGO("material.codigo"),
		MATERIAL("material"),
		SLC_NUMERO("solicitacaoCompra.numero"),
		SOLICITACAO_COMPRA("solicitacaoCompra"),
		QTDE_PONTO_PEDIDO("qtdePontoPedido"),
		QTDE_A_PROCESSAR("qtdeAProcessar"),
		TEXTO("texto"),
		QTDE_SALDO_CONSIGNADO("qtdeSaldoConsignado"),
		QTDE_REFORCO_SC("qtdeReforcoSc"),
		QTDE_EM_AF("qtdeEmAf"),
		QTDE_EM_SC("qtdeEmSc"),
		QTDE_EM_FF("qtdeEmFundoFixo"),
		QTDE_A_COMPRAR("qtdeAComprar")
		;
		
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		public String toString() {
			return this.field;
		}

	}

}