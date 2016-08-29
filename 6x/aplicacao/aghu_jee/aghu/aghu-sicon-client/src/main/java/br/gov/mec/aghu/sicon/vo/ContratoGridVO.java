package br.gov.mec.aghu.sicon.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioSortableSitEnvioContrato;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoResContrato;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ContratoGridVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7221575103922846403L;
	private ScoContrato contrato;
	private DominioSortableSitEnvioContrato sitenvio;
	private String pendenciasTooltip;
	private int flagType;
	private String fornTooltip;
	private String contratoTooltip;
	private ScoAditContrato aditContrato;
	private ScoResContrato resContrato;
	private String descBtnEnviar;
	private String flagTooltip;

	public ScoContrato getContrato() {
		return contrato;
	}

	public ContratoGridVO(ScoContrato contrato) {
		super();
		this.contrato = contrato;
	}

	public ContratoGridVO(ScoAditContrato aditContrato) {
		super();
		this.aditContrato = aditContrato;
		
		if(aditContrato.getCont() != null){
			this.contrato = aditContrato.getCont();
		}
	}
	
	public ContratoGridVO(ScoResContrato resContrato) {
		super();
		this.resContrato = resContrato;
		
		if(resContrato.getContrato() != null){
			this.contrato = resContrato.getContrato();
		}
	}
	

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public DominioSortableSitEnvioContrato getSitenvio() {
		return sitenvio;
	}

	public void setSitenvio(DominioSortableSitEnvioContrato sitenvio) {
		this.sitenvio = sitenvio;
	}

	public String getPendenciasTooltip() {
		return pendenciasTooltip;
	}

	public void setPendenciasTooltip(String pendenciasTooltip) {
		this.pendenciasTooltip = pendenciasTooltip;
	}

	public int getFlagType() {
		return flagType;
	}

	public void setFlagType(int flagType) {
		this.flagType = flagType;
	}

	public String getFornTooltip() {
		return fornTooltip;
	}

	public void setFornTooltip(String fornTooltip) {
		this.fornTooltip = fornTooltip;
	}

	public String getContratoTooltip() {
		return contratoTooltip;
	}

	public void setContratoTooltip(String contratoTooltip) {
		this.contratoTooltip = contratoTooltip;
	}
	
	public ScoAditContrato getAditContrato() {
		return aditContrato;
	}

	public void setAditContrato(ScoAditContrato aditContrato) {
		this.aditContrato = aditContrato;
	}
	
	public ScoResContrato getResContrato() {
		return resContrato;
	}

	public void setResContrato(ScoResContrato resContrato) {
		this.resContrato = resContrato;
	}
	
	public String getDescBtnEnviar() {
		
		//Label botão de Envio de Contrato
		if(contrato != null){
			
			descBtnEnviar = "Enviar Contrato";
			
			if((contrato.getSituacao() == DominioSituacaoEnvioContrato.AR) ||
			   (contrato.getSituacao() == DominioSituacaoEnvioContrato.EE && contrato.getCodInternoUasg() != null)){
				
				descBtnEnviar = "Reenviar Contrato";
			}	
			
		}else{
			
			descBtnEnviar = "Enviar Aditivo";
			
			//Label botão de Envio de Aditivo
			if(aditContrato != null){
				descBtnEnviar = "Enviar Aditivo";
			}
		}
		
		
		return descBtnEnviar;
	}

	public void setDescBtnEnviar(String descBtnEnviar) {
		this.descBtnEnviar = descBtnEnviar;
	}

	public String getFlagTooltip() {
		return flagTooltip;
	}

	public void setFlagTooltip(String flagTooltip) {
		this.flagTooltip = flagTooltip;
	}
	
	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();
		
		sb.append(getContrato().getNrContrato().toString());
		
		if (aditContrato != null && aditContrato.getId() != null){
			sb.append(aditContrato.getId().getSeq().toString());
		}
		
		return sb.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contrato == null) ? 0 : contrato.hashCode()) + ((aditContrato == null) ? 0 : aditContrato.hashCode());
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
		ContratoGridVO other = (ContratoGridVO) obj;
		if (contrato == null) {
			if (other.contrato != null) {
				return false;
			}
		} else if (!contrato.equals(other.contrato)) {
			return false;
		} else if (aditContrato != null && other.aditContrato != null) {
			if (!aditContrato.getId().getSeq()
					.equals(other.getAditContrato().getId().getSeq())) {
				return false;
			}
		}
		return true;
	}
	
	
//	@Override
//	public int hashCode() {
//		return new HashCodeBuilder().append(contrato).append(aditContrato)
//				.append(resContrato).hashCode();
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (obj == null) {
//			return false;
//		}
//		ContratoGridVO other = (ContratoGridVO) obj;
//		return new EqualsBuilder().append(contrato, other.contrato)
//				.append(aditContrato, other.aditContrato)
//				.append(resContrato, other.resContrato).isEquals();
//	}
	

// 	
//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		
//		result = prime * result
//				+ ((contrato == null) ? 0 : contrato.hashCode()) + ((aditContrato == null) ? 0 : aditContrato.hashCode());
//		
//		return result;
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj){
//			return true;
//		}	
//		if (obj == null){
//			return false;
//		}
//		if (!(obj instanceof ContratoGridVO)){
//			return false;
//		}
//		ContratoGridVO other = (ContratoGridVO) obj;
//		if (contrato == null) {
//			if (other.contrato != null){
//				return false;
//			}
//		} else if (!contrato.equals(other.contrato)){   
//			return false;
//		} else if (aditContrato != null && other.aditContrato != null){
//			if (!aditContrato.getId().getSeq().equals(other.getAditContrato().getId().getSeq())){
//				return false;
//			}
//		}
//		return true;
//	}

 
}
