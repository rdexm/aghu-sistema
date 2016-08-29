package br.gov.mec.aghu.exames.patologia.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.model.AelMaterialAp;

public class AelKitMatPatologiaVO implements Serializable {

	private static final long serialVersionUID = 1775286677592072800L;

	private AelMaterialAp materialAp;
	
	/** TRUE se é pra cancelar a imuno histoquimica, FALSE se não é pra cancelar */
	private Boolean imunoHist;

	private DominioSituacaoExamePatologia etapaLaudo;
	
	private final String CANCELADO = "Cancelado";
	private final String A_CANCELAR = "A Cancelar";
	private final String A_REALIZAR = "A Realizar";
	
	
	public AelKitMatPatologiaVO(AelMaterialAp materialAp,
			Boolean imunoHist, DominioSituacaoExamePatologia etapaLaudo) {
		super();
		this.materialAp = materialAp;
		this.imunoHist = imunoHist;
		this.etapaLaudo = etapaLaudo;
	}

	public AelMaterialAp getMaterialAp() {
		return materialAp;
	}

	public void setMaterialAp(AelMaterialAp materialAp) {
		this.materialAp = materialAp;
	}

	/** TRUE se é pra cancelar a imuno histoquimica, FALSE se não é pra cancelar */
	public Boolean getImunoHist() {
		return imunoHist;
	}

	public void setImunoHist(Boolean imunoHist) {
		this.imunoHist = imunoHist;
	}

	public DominioSituacaoExamePatologia getEtapaLaudo() {
		return etapaLaudo;
	}

	public void setEtapaLaudo(DominioSituacaoExamePatologia etapaLaudo) {
		this.etapaLaudo = etapaLaudo;
	}

	public String getImunoHistoquimicoLabel() {
		if (DominioSituacaoExamePatologia.LA.equals(etapaLaudo)) {
			if (Boolean.TRUE.equals(imunoHist)) {
				return CANCELADO;
			}
			else if (Boolean.FALSE.equals(imunoHist)) {
				return A_REALIZAR;
			}
			else {
				return "";
			}
		}
		else {
			if (Boolean.TRUE.equals(imunoHist)) {
				return A_CANCELAR;
			}
			else if (Boolean.FALSE.equals(imunoHist)) {
				return A_REALIZAR;
			}
			else {
				return "";
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((etapaLaudo == null) ? 0 : etapaLaudo.hashCode());
		result = prime * result
				+ ((materialAp == null) ? 0 : materialAp.hashCode());
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
		if (!(obj instanceof AelKitMatPatologiaVO)){
			return false;
		}
		AelKitMatPatologiaVO other = (AelKitMatPatologiaVO) obj;
		if (etapaLaudo != other.etapaLaudo){
			return false;
		}
		if (materialAp == null) {
			if (other.materialAp != null){
				return false;
			}
		} else if (!materialAp.equals(other.materialAp)){
			return false;
		}
		return true;
	}
	
	
	
}
