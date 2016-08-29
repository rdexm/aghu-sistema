package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_listas_siafi database table.
 * 
 */
@Embeddable
public class ScoListasSiafiId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3169076946187290253L;
	private Integer anoEmpenho;
	private String numeroLista;
	private ScoItemAutorizacaoForn scoItensAutorizacaoForn;

    public ScoListasSiafiId() {
    }
    
  //bi-directional many-to-one association to ScoItensAutorizacaoForn
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="IAF_AFN_NUMERO", referencedColumnName="AFN_NUMERO"),
		@JoinColumn(name="IAF_NUMERO", referencedColumnName="NUMERO")
		})
	public ScoItemAutorizacaoForn getScoItensAutorizacaoForn() {
		return this.scoItensAutorizacaoForn;
	}

	public void setScoItensAutorizacaoForn(ScoItemAutorizacaoForn scoItensAutorizacaoForn) {
		this.scoItensAutorizacaoForn = scoItensAutorizacaoForn;
	}


	@Column(name="ANO_EMPENHO")
	public Integer getAnoEmpenho() {
		return this.anoEmpenho;
	}
	public void setAnoEmpenho(Integer anoEmpenho) {
		this.anoEmpenho = anoEmpenho;
	}

	@Column(name="NUMERO_LISTA")
	public String getNumeroLista() {
		return this.numeroLista;
	}
	public void setNumeroLista(String numeroLista) {
		this.numeroLista = numeroLista;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ScoListasSiafiId)) {
			return false;
		}
		ScoListasSiafiId castOther = (ScoListasSiafiId)other;
		return 
			this.scoItensAutorizacaoForn.equals(castOther.scoItensAutorizacaoForn)
			&& this.anoEmpenho.equals(castOther.anoEmpenho)
			&& this.numeroLista.equals(castOther.numeroLista);

    }
    
	@Override
	public int hashCode() {
		final Integer prime = 31;
		Integer hash = 17;
		hash = hash * prime + this.scoItensAutorizacaoForn.hashCode();
		hash = hash * prime + this.anoEmpenho.hashCode();
		hash = hash * prime + this.numeroLista.hashCode();
		
		return hash;
    }
}