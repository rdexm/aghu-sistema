package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the v_ult_compras_mat database table.
 * 
 */
@Entity
@Table(name="V_SCO_ULT_COMPRAS_MAT")
@Immutable
public class VScoUltComprasMat extends BaseEntitySeq<Integer> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -6112739577949956060L;
	private Timestamp dtAberturaProposta;
	private Timestamp dtGeracao;
	private String formaPag;
	private Integer nroComplemento;
	private Integer nroNf;
	private Integer nrsSeq;
	private Integer pfrLctNumero;
	private Integer quantidade;
	private Double valor;
	private Integer numeroSolicitacao; 
	
	ScoLicitacao licitacao;
	ScoMaterial material;	
	ScoFornecedor fornecedor;
	ScoMarcaComercial marcaComercial;
	
	public enum Fields {
			
		    DT_ABERTURA("dtAberturaProposta"),
		    DT_GERACAO("dtGeracao"),
		    FORMA_PGTO("formaPag"),
		    NUM_COMPLEMENTO("nroComplemento"),
		    NUM_NF("nroNf"),
		    NUM_SEQ("nrsSeq"),
		    QUANTIDADE("quantidade"),
		    VALOR("valor"),
		    LICITACAO("licitacao"),
		    MATERIAL("material"),		   
		    FORNECEDOR("fornecedor"),
		    NUMERO_SOLICITACAO("numeroSolicitacao"),
		    MARCA_COMERCIAL("marcaComercial");
		    
		    private String fields;

			private Fields(String fields) {
				this.fields = fields;
			}

			@Override
			public String toString() {
				return fields;
			}
	}

    public VScoUltComprasMat() {
    }


	@Column(name="DT_ABERTURA_PROPOSTA")
	public Timestamp getDtAberturaProposta() {
		return this.dtAberturaProposta;
	}

	public void setDtAberturaProposta(Timestamp dtAberturaProposta) {
		this.dtAberturaProposta = dtAberturaProposta;
	}


	@Column(name="DT_GERACAO")
	public Timestamp getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Timestamp dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Column(name="FORMA_PAG")
	public String getFormaPag() {
		return this.formaPag;
	}

	public void setFormaPag(String formaPag) {
		this.formaPag = formaPag;
	}


	@Column(name="NRO_COMPLEMENTO")
	public Integer getNroComplemento() {
		return this.nroComplemento;
	}

	public void setNroComplemento(Integer nroComplemento) {
		this.nroComplemento = nroComplemento;
	}


	@Column(name="NRO_NF")
	public Integer getNroNf() {
		return this.nroNf;
	}

	public void setNroNf(Integer nroNf) {
		this.nroNf = nroNf;
	}

	@Id
	@Column(name="NRS_SEQ")
	public Integer getNrsSeq() {
		return this.nrsSeq;
	}

	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}

	@Column(name="PFR_LCT_NUMERO")
	public Integer getPfrLctNumero() {
		return this.pfrLctNumero;
	}

	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}


	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="LCT_NUMERO", referencedColumnName = "NUMERO")
	public ScoLicitacao getLicitacao() {
		return licitacao;
	}


	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MAT_CODIGO", referencedColumnName = "CODIGO")
	public ScoMaterial getMaterial() {
		return material;
	}
	
	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MCM_CODIGO", referencedColumnName = "CODIGO")
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}


	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}


	
	


	/*@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SLC_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoDeCompra getSolicitacao() {
		return solicitacao;
	}


	public void setSolicitacao(ScoSolicitacaoDeCompra solicitacao) {
		this.solicitacao = solicitacao;
	}*/

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PFR_FRN_NUMERO", referencedColumnName = "NUMERO")
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}


	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Column(name="SLC_NUMERO")
	public Integer getNumeroSolicitacao() {
		return numeroSolicitacao;
	}


	public void setNumeroSolicitacao(Integer numeroSolicitacao) {
		this.numeroSolicitacao = numeroSolicitacao;
	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getNrsSeq() == null) ? 0 : getNrsSeq().hashCode());
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
		if (!(obj instanceof VScoUltComprasMat)) {
			return false;
		}
		VScoUltComprasMat other = (VScoUltComprasMat) obj;
		if (getNrsSeq() == null) {
			if (other.getNrsSeq() != null) {
				return false;
			}
		} else if (!getNrsSeq().equals(other.getNrsSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####
 
 @Transient public Integer getSeq(){ return this.getNrsSeq();} 
 public void setSeq(Integer seq){ this.setNrsSeq(seq);}
}