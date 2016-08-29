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


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_lote_x_fornecedores database table.
 * 
 */
@Entity
@Table(name="SCE_LOTE_X_FORNECEDORES")
public class SceLoteFornecedor extends BaseEntitySeq<Integer> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9001757936873833606L;
	private Integer seq;
	private Date dtGeracao;
	private Date dtValidade;
	private Integer fevSeq;
	private ScoFornecedor fornecedor;
	//private String lotCodigo;
	private ScoMaterial scoMaterial;
	//private Integer lotMcmCodigo;
	private Integer quantidade;
	private Integer quantidadeSaida;
	private Integer version;
	private SceLote lote;
	private SceFornecedorEventual sceFornecedorEventual;

	public enum Fields {
		SEQ("seq"),
		MATERIAL("scoMaterial"),
		DATA_VALIDADE("dtValidade"),
		DATA_GERACAO("dtGeracao"),
		QUANTIDADE("quantidade"),
		QUANTIDADE_SAIDA("quantidadeSaida"),
		LOT_CODIGO("lote.id.codigo"),
		LOT_MAT_CODIGO("lote.id.matCodigo"),
		LOT_MCM_CODIGO("lote.id.mcmCodigo"),
		LOTE("lote"),
		FORNECEDOR_EVENTUAL("sceFornecedorEventual"),
		FORNECEDOR("fornecedor");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

    public SceLoteFornecedor() {
    }


	@Id
	@SequenceGenerator(name="SCE_LOTE_X_FORNECEDORES_SEQ_GENERATOR", sequenceName="AGH.SCE_LTF_SQ1" , allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCE_LOTE_X_FORNECEDORES_SEQ_GENERATOR")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@Column(name="DT_GERACAO")
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Column(name="DT_VALIDADE")
	@Temporal(TemporalType.DATE)
	public Date getDtValidade() {
		return this.dtValidade;
	}

	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}


	@Column(name="FEV_SEQ")
	public Integer getFevSeq() {
		return this.fevSeq;
	}

	public void setFevSeq(Integer fevSeq) {
		this.fevSeq = fevSeq;
	}

	/*
	@Column(name="LOT_CODIGO")
	public String getLotCodigo() {
		return this.lotCodigo;
	}

	public void setLotCodigo(String lotCodigo) {
		this.lotCodigo = lotCodigo;
	}
	*/

	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="LOT_MAT_CODIGO", insertable=false, updatable=false)
	public ScoMaterial getScoMaterial() {
		return scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	/*
	@Column(name="LOT_MCM_CODIGO")
	public Integer getLotMcmCodigo() {
		return this.lotMcmCodigo;
	}

	public void setLotMcmCodigo(Integer lotMcmCodigo) {
		this.lotMcmCodigo = lotMcmCodigo;
	}
	*/

	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	@Column(name="QUANTIDADE_SAIDA")
	public Integer getQuantidadeSaida() {
		return this.quantidadeSaida;
	}

	public void setQuantidadeSaida(Integer quantidadeSaida) {
		this.quantidadeSaida = quantidadeSaida;
	}

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne
	@JoinColumn(name="FRN_NUMERO")
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "LOT_CODIGO", referencedColumnName = "CODIGO", nullable = false),
		@JoinColumn(name = "LOT_MAT_CODIGO", referencedColumnName = "MAT_CODIGO", nullable = false),
		@JoinColumn(name = "LOT_MCM_CODIGO", referencedColumnName = "MCM_CODIGO", nullable = false) })
	public SceLote getLote() {
		return lote;
	}

	public void setLote(SceLote lote) {
		this.lote = lote;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FEV_SEQ", referencedColumnName = "SEQ",  insertable=false, updatable=false)
	public SceFornecedorEventual getSceFornecedorEventual() {
		return this.sceFornecedorEventual;
	}

	public void setSceFornecedorEventual(SceFornecedorEventual sceFornecedorEventual) {
		this.sceFornecedorEventual = sceFornecedorEventual;
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
		if (!(obj instanceof SceLoteFornecedor)) {
			return false;
		}
		SceLoteFornecedor other = (SceLoteFornecedor) obj;
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