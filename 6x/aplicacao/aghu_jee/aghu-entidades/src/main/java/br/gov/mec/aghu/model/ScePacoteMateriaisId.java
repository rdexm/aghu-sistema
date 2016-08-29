package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sce_pacote_materiais database table.
 * 
 */
@Embeddable
public class ScePacoteMateriaisId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 74724401633630934L;
	private Integer codigoCentroCustoProprietario;
	private Integer codigoCentroCustoAplicacao;
	private Integer numero;

	/**
	 * 
	 */
    public ScePacoteMateriaisId() {
    }
    
    @Column(name="CCT_CODIGO_REFERE")
	public Integer getCodigoCentroCustoProprietario() {
		return this.codigoCentroCustoProprietario;
	}
	
	@Column(name="CCT_CODIGO")
	public Integer getCodigoCentroCustoAplicacao() {
		return this.codigoCentroCustoAplicacao;
	}
	
	@Column(name="NUMERO")
	public Integer getNumero() {
		return this.numero;
	}
	
	public void setCodigoCentroCustoProprietario(Integer codigoCentroCustoProprietario) {
		this.codigoCentroCustoProprietario = codigoCentroCustoProprietario;
	}
	
	public void setCodigoCentroCustoAplicacao(Integer codigoCentroCustoAplicacao) {
		this.codigoCentroCustoAplicacao = codigoCentroCustoAplicacao;
	}
	
	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigoCentroCustoAplicacao == null) ? 0 : codigoCentroCustoAplicacao.hashCode());
		result = prime * result + ((codigoCentroCustoProprietario == null) ? 0 : codigoCentroCustoProprietario.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		ScePacoteMateriaisId other = (ScePacoteMateriaisId) obj;
		if (codigoCentroCustoAplicacao == null) {
			if (other.codigoCentroCustoAplicacao != null) {
				return false;
			}
		} else if (!codigoCentroCustoAplicacao.equals(other.codigoCentroCustoAplicacao)) {
			return false;
		}
		if (codigoCentroCustoProprietario == null) {
			if (other.codigoCentroCustoProprietario != null) {
				return false;
			}
		} else if (!codigoCentroCustoProprietario.equals(other.codigoCentroCustoProprietario)) {
			return false;
		}
		if (numero == null) {
			if (other.numero != null) {
				return false;
			}
		} else if (!numero.equals(other.numero)) {
			return false;
		}
		return true;
	}

}