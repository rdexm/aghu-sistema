package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "SCO_CONDICOES_PGTO_LICITACAO", schema = "AGH")
@SequenceGenerator(name = "scoCplSq1", sequenceName = "AGH.SCO_CPL_SQ1", allocationSize = 1)
public class ScoCondicaoPgtoLicitacao extends BaseEntitySeq<Integer> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5713058109666821921L;
	private Integer seq;
	private Integer numero;
	private ScoLicitacao licitacao;
	private ScoItemLicitacao itemLicitacao;
	private ScoFormaPagamento formaPagamento;
	private BigDecimal percAcrescimo;
	private BigDecimal percDesconto;
	private Integer version;
	private Set<ScoParcelasPagamento> parcelas;
	
	@Id
	@Column(name = "SEQ", length = 7, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoCplSq1")		
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	@Column(name = "NUMERO", length = 7, nullable = false)
	public Integer getNumero() {
		return numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LCT_NUMERO", referencedColumnName = "NUMERO")		
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}
	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "ITL_LCT_NUMERO", referencedColumnName = "LCT_NUMERO"),
			@JoinColumn(name = "ITL_NUMERO", referencedColumnName = "NUMERO") })
	public ScoItemLicitacao getItemLicitacao() {
		return itemLicitacao;
	}
	public void setItemLicitacao(ScoItemLicitacao itemLicitacao) {
		this.itemLicitacao = itemLicitacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FPG_CODIGO", referencedColumnName = "CODIGO", nullable = false)	
	public ScoFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}
	public void setFormaPagamento(ScoFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	
	@Column(name = "PERC_ACRESCIMO", precision = 15, scale = 2)
	public BigDecimal getPercAcrescimo() {
		return percAcrescimo;
	}
	
	public void setPercAcrescimo(BigDecimal percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}
	
	@Column(name = "PERC_DESCONTO", precision = 15, scale = 2)
	public BigDecimal getPercDesconto() {
		return percDesconto;
	}
	public void setPercDesconto(BigDecimal percDesconto) {
		this.percDesconto = percDesconto;
	}
	
	@Column(name = "VERSION", length = 7, nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "condicaoPgtoLicitacao")
	public Set<ScoParcelasPagamento> getParcelas() {
		return parcelas;
	}
	
	public void setParcelas(Set<ScoParcelasPagamento> parcelas) {
		this.parcelas = parcelas;
	}

	public enum Fields {
		SEQ("seq"),		
		NUMERO("numero"),
		LCT_NUMERO("licitacao.numero"),
		ITL_LCT_NUMERO("itemLicitacao.id.lctNumero"), 
		ITL_NUMERO("itemLicitacao.id.numero"),
		FORMA_PAGAMENTO("formaPagamento"),
		FPG_CODIGO("formaPagamento.codigo"),
		PERC_ACRESCIMO("perc_acrescimo"),
		PERC_DESCONTO("percDesconto"),
		VERSION("version");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		if (!(obj instanceof ScoCondicaoPgtoLicitacao)) {
			return false;
		}
		ScoCondicaoPgtoLicitacao other = (ScoCondicaoPgtoLicitacao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	
 
}
