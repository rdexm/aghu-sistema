package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_pontos_servidores database table.
 * 
 */
@Embeddable
public class ScoPontoServidorId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3394004632164907665L;
	
	private Short codigoPontoParadaSolicitacao;
	
	private Short vinCodigo;
	
	private Integer matricula;

    public ScoPontoServidorId() {
    }

    @Column(name="PPS_CODIGO")
	public Short getCodigoPontoParadaSolicitacao() {
		return codigoPontoParadaSolicitacao;
	}

	public void setCodigoPontoParadaSolicitacao(Short codigoPontoParadaSolicitacao) {
		this.codigoPontoParadaSolicitacao = codigoPontoParadaSolicitacao;
	}

	@Column(name="SER_VIN_CODIGO")
	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	@Column(name="SER_MATRICULA")
	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((codigoPontoParadaSolicitacao == null) ? 0
						: codigoPontoParadaSolicitacao.hashCode());
		result = prime * result
				+ ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result
				+ ((vinCodigo == null) ? 0 : vinCodigo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (getClass() != obj.getClass())
		{
			return false;
		}
		ScoPontoServidorId other = (ScoPontoServidorId) obj;
		if (codigoPontoParadaSolicitacao == null) {
			if (other.codigoPontoParadaSolicitacao != null)
			{
				return false;
			}
		} else if (!codigoPontoParadaSolicitacao
				.equals(other.codigoPontoParadaSolicitacao))
			{
				return false;
			}
		if (matricula == null) {
			if (other.matricula != null)
			{
				return false;
			}
		} else if (!matricula.equals(other.matricula))
			{
				return false;
			}
		if (vinCodigo == null) {
			if (other.vinCodigo != null)
			{
				return false;
			}
		} else if (!vinCodigo.equals(other.vinCodigo))
			{
				return false;			
			}
		return true;
	}
}