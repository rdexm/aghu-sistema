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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioValidacaoNF;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * @author rafael.fonseca
 * 
 */
@Entity
@Table(name = "SCO_PEDIDOS_MAT_EXPEDIENTE", schema = "AGH")
@SequenceGenerator(name = "scoPedidoMatExpedienteSeq", sequenceName = "AGH.SCO_PMX_SQ1")
public class ScoPedidoMatExpediente extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5950274080621383126L;

	private Integer seq;
	private Integer numeroPedido;
	private Date dataPedido;
	private RapServidores servidorSolicitante;
	private FccCentroCustos centroCusto;
	private Date dataRecebimento;
	private Date dataConferencia;
	private RapServidores servidorConferente;
	private Integer numeroNotaFiscal;
	private Date dataNotaFiscal;
	private String comentarios;
	private Date dataCriacao;
	private Date dataAlteracao;
	private RapServidores servidorInclusao;
	private RapServidores servidorAlteracao;
	private Boolean indValidacaoPedido = Boolean.FALSE;
	private DominioValidacaoNF indValidacaoNotaFiscal;
	private Boolean indIntegracaoEstoque;
	private Boolean indGeracaoRM;
	private List<ScoPedItensMatExpediente> listaItens;
	
	private BigDecimal valorTotal;
	
	@Id
	@Column(name = "SEQ", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoPedidoMatExpedienteSeq")
	public Integer getSeq() {
		return seq;
	}

	@Column(name = "NUMERO_PEDIDO", nullable = false)
	public Integer getNumeroPedido() {
		return numeroPedido;
	}

	@Column(name = "DT_PEDIDO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataPedido() {
		return dataPedido;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_SOLICITANTE", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VINCULO_SOLICITANTE", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidorSolicitante() {
		return servidorSolicitante;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", nullable = false)
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	@Column(name = "DT_RECEBIMENTO", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getDataRecebimento() {
		return dataRecebimento;
	}

	@Column(name = "DT_CONFERENCIA", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getDataConferencia() {
		return dataConferencia;
	}

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_CONFERENTE", referencedColumnName = "MATRICULA", nullable = true),
		@JoinColumn(name = "SER_VINCULO_CONFERENTE", referencedColumnName = "VIN_CODIGO", nullable = true)
	})
	public RapServidores getServidorConferente() {
		return servidorConferente;
	}

	@Column(name = "NUMERO_NOTA_FISCAL", nullable = true)
	public Integer getNumeroNotaFiscal() {
		return numeroNotaFiscal;
	}

	@Column(name = "DT_NOTA_FISCAL", nullable = true)
	@Temporal(TemporalType.DATE)
	public Date getDataNotaFiscal() {
		return dataNotaFiscal;
	}

	@Column(name = "COMENTARIOS", length = 1000, nullable = true)
	@Length(max = 250)
	public String getComentarios() {
		return comentarios;
	}

	@Column(name = "DT_CRIACAO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataCriacao() {
		return dataCriacao;
	}

	@Column(name = "DT_ALTERACAO", nullable = true)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_INCLUSAO", referencedColumnName = "MATRICULA", nullable = false),
		@JoinColumn(name = "SER_VINCULO_INCLUSAO", referencedColumnName = "VIN_CODIGO", nullable = false)
	})
	public RapServidores getServidorInclusao() {
		return servidorInclusao;
	}

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_ALTERACAO", referencedColumnName = "MATRICULA", nullable = true),
		@JoinColumn(name = "SER_VINCULO_ALTERACAO", referencedColumnName = "VIN_CODIGO", nullable = true)
	})
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}

	@Column(name = "INDICADOR_VALIDACAO_PEDIDO", nullable = false, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndValidacaoPedido() {
		return indValidacaoPedido;
	}

	@Column(name = "INDICADOR_VALIDACAO_NF", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioValidacaoNF getIndValidacaoNotaFiscal() {
		return indValidacaoNotaFiscal;
	}

	@Column(name = "INDICADOR_INTEGRACAO_ESTOQUE", nullable = false, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndIntegracaoEstoque() {
		return indIntegracaoEstoque;
	}

	@Column(name = "INDICADOR_GERACAO_RM", nullable = false, length = 1)
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndGeracaoRM() {
		return indGeracaoRM;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setNumeroPedido(Integer numeroPedido) {
		this.numeroPedido = numeroPedido;
	}

	public void setDataPedido(Date dataPedido) {
		this.dataPedido = dataPedido;
	}

	public void setServidorSolicitante(RapServidores servidorSolicitante) {
		this.servidorSolicitante = servidorSolicitante;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public void setDataRecebimento(Date dataRecebimento) {
		this.dataRecebimento = dataRecebimento;
	}

	public void setDataConferencia(Date dataConferencia) {
		this.dataConferencia = dataConferencia;
	}

	public void setServidorConferente(RapServidores servidorConferente) {
		this.servidorConferente = servidorConferente;
	}

	public void setNumeroNotaFiscal(Integer numeroNotaFiscal) {
		this.numeroNotaFiscal = numeroNotaFiscal;
	}

	public void setDataNotaFiscal(Date dataNotaFiscal) {
		this.dataNotaFiscal = dataNotaFiscal;
	}

	public void setComentarios(String comentarios) {
		this.comentarios = comentarios;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

	public void setServidorInclusao(RapServidores servidorInclusao) {
		this.servidorInclusao = servidorInclusao;
	}

	public void setServidorAlteracao(RapServidores servidorAlteracao) {
		this.servidorAlteracao = servidorAlteracao;
	}

	public void setIndValidacaoPedido(Boolean indValidacaoPedido) {
		this.indValidacaoPedido = indValidacaoPedido;
	}

	public void setIndValidacaoNotaFiscal(DominioValidacaoNF indValidacaoNotaFiscal) {
		this.indValidacaoNotaFiscal = indValidacaoNotaFiscal;
	}

	public void setIndIntegracaoEstoque(Boolean indIntegracaoEstoque) {
		this.indIntegracaoEstoque = indIntegracaoEstoque;
	}

	public void setIndGeracaoRM(Boolean indGeracaoRM) {
		this.indGeracaoRM = indGeracaoRM;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "id.pmxSeq")
	public List<ScoPedItensMatExpediente> getListaItens() {
		return listaItens;
	}
	
	public void setListaItens(List<ScoPedItensMatExpediente> listaItens) {
		this.listaItens = listaItens;
	}
	
	@Transient
	public BigDecimal getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}
	
	public enum Fields {
		SEQ("seq"), 
		NUMERO_PEDIDO("numeroPedido"), 
		DATA_PEDIDO("dataPedido"), 
		SERVIDOR_SOLICITANTE("servidorSolicitante"),
		SERVIDOR_SOLICITANTE_MATRICULA("servidorSolicitante.id.matricula"),
		SERVIDOR_SOLICITANTE_VINCULO("servidorSolicitante.id.vinCodigo"),
		CENTRO_CUSTO("centroCusto"),
		CENTRO_CUSTO_CODIGO("centroCusto.codigo"),
		DATA_RECEBIMENTO("dataRecebimento"), 
		DATA_CONFERENCIA("dataConferencia"), 
		SERVIDOR_CONFERENTE("servidorConferente"), 
		SERVIDOR_CONFERENTE_MATRICULA("servidorConferente.id.matricula"),
		SERVIDOR_CONFERENTE_VINCULO("servidorConferente.id.vinculo"),
		NUMERO_NOTA_FISCAL("numeroNotaFiscal"), 
		DATA_NOTA_FISCAL("dataNotaFiscal"), 
		LISTA_ITENS("listaItens"),
		IND_VALIDACAO_PEDIDO("indValidacaoPedido"),
		IND_VALIDACAO_NF("indValidacaoNotaFiscal"),
		IND_INTEGRACAO_ESTOQUE("indIntegracaoEstoque"),
		IND_GERACAO_RM("indGeracaoRM"),
		SERVIDOR_ALTERACAO("servidorAlteracao"),
		DATA_ALTERACAO("dataAlteracao")
		;

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof ScoPedidoMatExpediente)) {
			return false;
		}
		ScoPedidoMatExpediente other = (ScoPedidoMatExpediente) obj;
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
