package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class PtmServAreaTecAvaliacaoId implements EntityCompositeId{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8871895618260162905L;

	private Integer seqAreaTecAvaliacao;
	private Integer matRapTecnico;
	private Short serVinCodigoTecnico;
	
	public PtmServAreaTecAvaliacaoId(){}
	
	public PtmServAreaTecAvaliacaoId(Integer seqAreaTecAvaliacao, Integer matRapTecnico, Short vinCodigoRapServidores){
		this.seqAreaTecAvaliacao = seqAreaTecAvaliacao;
		this.matRapTecnico = matRapTecnico;
		this.serVinCodigoTecnico = vinCodigoRapServidores;
	}
	

	@Column(name = "SEQ_AREA_TEC_AVALIACAO", nullable = false)
	public Integer getSeqAreaTecAvaliacao() {
		return seqAreaTecAvaliacao;
	}
	
	public void setSeqAreaTecAvaliacao(Integer seqAreaTecAvaliacao) {
		this.seqAreaTecAvaliacao = seqAreaTecAvaliacao;
	}
	
	@Column(name = "MAT_RAP_TECNICO", nullable = false)
	public Integer getmatRapTecnico() {
		return matRapTecnico;
	}
	
	public void setmatRapTecnico(Integer matRapTecnico) {
		this.matRapTecnico = matRapTecnico;
	}
	
	@Column(name = "SER_VIN_CODIGO_TECNICO", nullable = false)
	public Short getserVinCodigoTecnico() {
		return serVinCodigoTecnico;
	}

	public void setserVinCodigoTecnico(Short serVinCodigoTecnico) {
		this.serVinCodigoTecnico = serVinCodigoTecnico;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((matRapTecnico == null) ? 0 : matRapTecnico.hashCode());
		result = prime
				* result
				+ ((seqAreaTecAvaliacao == null) ? 0 : seqAreaTecAvaliacao
						.hashCode());
		result = prime
				* result
				+ ((serVinCodigoTecnico == null) ? 0
						: serVinCodigoTecnico.hashCode());
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
		PtmServAreaTecAvaliacaoId other = (PtmServAreaTecAvaliacaoId) obj;
		if (matRapTecnico == null) {
			if (other.matRapTecnico != null){
				return false;
			}
		} else if (!matRapTecnico.equals(other.matRapTecnico)){
			return false;
		}
		if (seqAreaTecAvaliacao == null) {
			if (other.seqAreaTecAvaliacao != null){
				return false;
			}
		} else if (!seqAreaTecAvaliacao.equals(other.seqAreaTecAvaliacao)){
			return false;
		}
		if (serVinCodigoTecnico == null) {
			if (other.serVinCodigoTecnico != null){
				return false;
			}
		} else if (!serVinCodigoTecnico.equals(other.serVinCodigoTecnico)){
			return false;
		}
		return true;
	}
	
	
}
