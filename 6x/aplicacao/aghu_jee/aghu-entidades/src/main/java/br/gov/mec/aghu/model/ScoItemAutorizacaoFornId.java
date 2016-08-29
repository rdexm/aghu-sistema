package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_itens_autorizacao_forn database table.
 * 
 */
@Embeddable
public class ScoItemAutorizacaoFornId implements EntityCompositeId {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4575767496122636286L;
	private Integer afnNumero;
	private Integer numero;

    public ScoItemAutorizacaoFornId() {
    }

    @Column(name="AFN_NUMERO", nullable=false)
    public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	@Column(name="NUMERO", nullable=false)
	public Integer getNumero() {
		return this.numero;
	}
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((afnNumero == null) ? 0 : afnNumero.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoItemAutorizacaoFornId other = (ScoItemAutorizacaoFornId) obj;
		if (afnNumero == null) {
			if (other.afnNumero != null){
				return false;
			}
		} else if (!afnNumero.equals(other.afnNumero)){
			return false;
		}
		if (numero == null) {
			if (other.numero != null){
				return false;
			}
		} else if (!numero.equals(other.numero)){
			return false;
		}
		return true;
	}


	public enum Fields{
		
		AFN_NUMERO("afnNumero"),
		NUMERO("numero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
}