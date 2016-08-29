package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioInclusaoLoteReposicao;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name="SCO_LTR_ITEM", schema="AGH")
public class ScoItemLoteReposicao extends BaseEntitySeq<Integer> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9186472712612761155L;
	private Integer seq;
	private ScoLoteReposicao loteReposicao;
	private ScoMaterial material;
	private ScoSolicitacaoDeCompra solicitacao;
	private ScoGrupoMaterial grupoMaterial;
	private DominioTipoMaterial tipoMaterial;
	private BigDecimal custoMedio;
	private Integer qtdPontoPedido;
	private Integer tempoReposicao;
	private Integer qtdGerada;
	private Integer qtdConfirmada;
	private DominioInclusaoLoteReposicao indInclusao;
	private Boolean indSelecionado;
	private Integer version;
		
    public ScoItemLoteReposicao() {
    }
	
	@Id
	@Column(name = "SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scoLtrItemSq2")
	@SequenceGenerator(name = "scoLtrItemSq2", sequenceName = "AGH.SCO_ILT_SQ1", allocationSize = 1)
	public Integer getSeq() {                                      
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne
	@JoinColumn(name = "LTR_SEQ")
	public ScoLoteReposicao getLoteReposicao() {
		return loteReposicao;
	}

	public void setLoteReposicao(ScoLoteReposicao loteReposicao) {
		this.loteReposicao = loteReposicao;
	}

	@ManyToOne
	@JoinColumn(name = "GMT_CODIGO")
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	@ManyToOne
	@JoinColumn(name = "MAT_CODIGO")
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
	
	@ManyToOne
	@JoinColumn(name = "SLC_NUMERO")
	public ScoSolicitacaoDeCompra getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(ScoSolicitacaoDeCompra solicitacao) {
		this.solicitacao = solicitacao;
	}
	
	@Column(name = "TIPO_MATERIAL", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoMaterial getTipoMaterial() {
		return this.tipoMaterial;
	}

	public void setTipoMaterial(DominioTipoMaterial tipoMaterial) {
		this.tipoMaterial = tipoMaterial;
	}
	
	@Column(name = "CUSTO_MEDIO", precision = 17, scale = 2)
	public BigDecimal getCustoMedio() {
		return this.custoMedio;
	}

	public void setCustoMedio(BigDecimal custoMedio) {
		this.custoMedio = custoMedio;
	}
	
	@Column(name = "VERSION")
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "QTDE_PONTO_PEDIDO", precision = 7, scale = 0)
	public Integer getQtdPontoPedido() {
		return this.qtdPontoPedido;
	}

	public void setQtdPontoPedido(Integer qtdPontoPedido) {
		this.qtdPontoPedido = qtdPontoPedido;
	}

	@Column(name = "TEMPO_REPOSICAO", precision = 3, scale = 0)
	public Integer getTempoReposicao() {
		return this.tempoReposicao;
	}

	public void setTempoReposicao(Integer tempoReposicao) {
		this.tempoReposicao = tempoReposicao;
	}
	
	@Column(name = "QTD_GERADA", precision = 10, scale = 0)
	public Integer getQtdGerada() {
		return this.qtdGerada;
	}

	public void setQtdGerada(Integer qtdGerada) {
		this.qtdGerada = qtdGerada;
	}	
	
	@Column(name = "QTD_CONFIRMADA", precision = 10, scale = 0)
	public Integer getQtdConfirmada() {
		return this.qtdConfirmada;
	}

	public void setQtdConfirmada(Integer qtdConfirmada) {
		this.qtdConfirmada = qtdConfirmada;
	}	
	
	@Column(name = "IND_INCLUSAO")
	@Enumerated(EnumType.STRING)
	public DominioInclusaoLoteReposicao getIndInclusao() {
		return indInclusao;
	}

	public void setIndInclusao(DominioInclusaoLoteReposicao indInclusao) {
		this.indInclusao = indInclusao;
	}
	
	@Column(name = "IND_SELECIONADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")	
	public Boolean getIndSelecionado() {
		return indSelecionado;
	}

	public void setIndSelecionado(Boolean indSelecionado) {
		this.indSelecionado = indSelecionado;
	}
	
	
	
	
	public enum Fields {
		
		SEQ("seq"),
		LOTE_REPOSICAO("loteReposicao"),
		LTR_SEQ("loteReposicao.seq"),
		MATERIAL("material"),
		SOLICITACAO_COMPRA("solicitacao"),
		GRUPO_MATERIAL("grupoMaterial"),
		TIPO_MATERIAL("tipoMaterial"),
		CUSTO_MEDIO("custoMedio"),
		QTD_PONTO_PEDIDO("qtdPontoPedido"),
		TEMPO_REPOSICAO("tempoReposicao"),
		QTD_GERADA("qtdGerada"),
		QTD_CONFIRMADA("qtdConfirmada"),
		IND_INCLUSAO("indInclusao"),
		IND_SELECIONADO("indSelecionado")
		
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