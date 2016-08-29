package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * @author rafael.fonseca
 *
 */
@Entity
@Table(name = "SCO_PED_ITENS_MAT_EXPEDIENTE", schema = "AGH")
public class ScoPedItensMatExpediente extends BaseEntityId<ScoPedItensMatExpedienteId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7378753549986043974L;

	private ScoPedItensMatExpedienteId id;
	private ScoMaterial material;
	private Integer quantidade;
	private BigDecimal valorUnitario;
	private Date dataCriacao;
	private Date dataAlteracao;
	private RapServidores servidorInclusao;
	private RapServidores servidorAlteracao;
	private ScoPedidoMatExpediente pedido;
	private Boolean indValidacao = Boolean.TRUE;
	
	
	@EmbeddedId
	@AttributeOverrides({
			@AttributeOverride(name = "pmxSeq", column = @Column(name = "PMX_SEQ", nullable = false)),
			@AttributeOverride(name = "itemNumero", column = @Column(name = "ITEM_NUMERO", nullable = false)) })
	public ScoPedItensMatExpedienteId getId() {
		return id;
	}
	
	@ManyToOne
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "codigo")
	public ScoMaterial getMaterial() {
		return material;
	}
	
	@Column(name = "QUANTIDADE", nullable = false)
	public Integer getQuantidade() {
		return quantidade;
	}
	
	@Column(name = "VALOR_UNITARIO", nullable = false, precision = 15, scale = 2)
	public BigDecimal getValorUnitario() {
		return valorUnitario;
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
		@JoinColumn(name = "SER_Matricula_Alteracao", referencedColumnName = "MATRICULA", nullable = true),
		@JoinColumn(name = "SER_Vinculo_Alteracao", referencedColumnName = "VIN_CODIGO", nullable = true)
	})
	public RapServidores getServidorAlteracao() {
		return servidorAlteracao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PMX_SEQ", nullable = false, insertable = false, updatable = false)
	public ScoPedidoMatExpediente getPedido() {
		return pedido;
	}
	
	@Transient
	public BigDecimal getValorTotal() {
		return this.valorUnitario.multiply(new BigDecimal(this.quantidade));
	}
	
	@Transient
	@Type(type="br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndValidacao() {
		return this.indValidacao;
	}	
	
	public void setIndValidacao(Boolean indValidacao) {
		this.indValidacao = indValidacao;
	}
	
	
	public void setId(ScoPedItensMatExpedienteId id) {
		this.id = id;
	}
	
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
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
	public void setPedido(ScoPedidoMatExpediente pedido) {
		this.pedido = pedido;
	}
	
	public enum Fields {
		PEDIDO("pedido"),
		PEDIDO_SEQ("pedido.seq"),
		PEDIDO_NOTA_FISCAL("pedido.numeroNotaFiscal"),
		ITEM_NUMERO("itemNumero"),
		MATERIAL("material"),
		MATERIAL_CODIGO("material.codigo"),
		QUANTIDADE("quantidade"),
		VALOR_UNITARIO("valorUnitario"),
		DATA_CRIACAO("dataCriacao"),
		DATA_ALTERACAO("dataAlteracao"),
		SERVIDOR_INCLUSAO("servidorInclusao"),
		SERVIDOR_ALTERACAO("servidorAlteracao");

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
				+ ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof ScoPedItensMatExpediente)) {
			return false;
		}
		ScoPedItensMatExpediente other = (ScoPedItensMatExpediente) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	
	

}
