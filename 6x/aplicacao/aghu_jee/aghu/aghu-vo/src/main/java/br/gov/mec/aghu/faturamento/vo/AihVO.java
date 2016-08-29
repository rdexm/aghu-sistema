package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.VFatContaHospitalarPac;

public class AihVO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5118617244433168577L;
	
	private FatAih fatAih;
	private VFatContaHospitalarPac contaHospitalarPac;
	
	
	public FatAih getFatAih() {
		return fatAih;
	}
	public void setFatAih(FatAih fatAih) {
		this.fatAih = fatAih;
	}
	
	public VFatContaHospitalarPac getContaHospitalarPac() {
		return contaHospitalarPac;
	}
	public void setContaHospitalarPac(VFatContaHospitalarPac contaHospitalarPac) {
		this.contaHospitalarPac = contaHospitalarPac;
	}
	
	
	
}
