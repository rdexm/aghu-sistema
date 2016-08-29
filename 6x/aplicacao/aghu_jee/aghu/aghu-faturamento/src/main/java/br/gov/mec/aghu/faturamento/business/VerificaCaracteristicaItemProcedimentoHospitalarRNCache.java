package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.util.Map;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.core.utils.CacheMap;


@RequestScoped
public class VerificaCaracteristicaItemProcedimentoHospitalarRNCache implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 912009710723561407L;
	
	@EJB
	private VerificaCaracteristicaItemProcedimentoHospitalarRN verificaCaracteristicaItemProcedimentoHospitalarRN;
	
	private Map<VerificaCaracteristicaItemProcedimentoHospitalarRNInner, Boolean> vos = new CacheMap<VerificaCaracteristicaItemProcedimentoHospitalarRNInner, Boolean>(
			5000);

	public Boolean verificarCaracteristicaItemProcHosp(Short iphPhoSeq, Integer iphSeq, DominioFatTipoCaractItem caracteristica) {
		VerificaCaracteristicaItemProcedimentoHospitalarRNInner innerObject = new VerificaCaracteristicaItemProcedimentoHospitalarRNInner(
				iphPhoSeq, iphSeq, caracteristica);

		Boolean retorno = this.vos.get(innerObject);

		if (retorno == null) {
			retorno = this.getVerificaCaracteristicaItemProcedimentoHospitalarRN().verificarCaracteristicaItemProcHosp(iphPhoSeq,
					iphSeq, caracteristica);
			this.vos.put(innerObject, retorno);
		}

		return retorno;
	}

	private class VerificaCaracteristicaItemProcedimentoHospitalarRNInner {

		private Short iphPhoSeq;

		private Integer iphSeq;

		private DominioFatTipoCaractItem caracteristica;

		public VerificaCaracteristicaItemProcedimentoHospitalarRNInner(Short iphPhoSeq, Integer iphSeq,
				DominioFatTipoCaractItem caracteristica) {
			this.iphPhoSeq = iphPhoSeq;
			this.iphSeq = iphSeq;
			this.caracteristica = caracteristica;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((caracteristica == null) ? 0 : caracteristica.hashCode());
			result = prime * result + ((iphPhoSeq == null) ? 0 : iphPhoSeq.hashCode());
			result = prime * result + ((iphSeq == null) ? 0 : iphSeq.hashCode());
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
			VerificaCaracteristicaItemProcedimentoHospitalarRNInner other = (VerificaCaracteristicaItemProcedimentoHospitalarRNInner) obj;
			if (!getOuterType().equals(other.getOuterType())){
				return false;
			}
			if (caracteristica != other.caracteristica){
				return false;
			}
			if (iphPhoSeq == null) {
				if (other.iphPhoSeq != null){
					return false;
				}
			} else if (!iphPhoSeq.equals(other.iphPhoSeq)){
				return false;
			}
			if (iphSeq == null) {
				if (other.iphSeq != null){
					return false;
				}
			} else if (!iphSeq.equals(other.iphSeq)){
				return false;
			}
			return true;
		}

		private VerificaCaracteristicaItemProcedimentoHospitalarRNCache getOuterType() {
			return VerificaCaracteristicaItemProcedimentoHospitalarRNCache.this;
		}

	}

	protected VerificaCaracteristicaItemProcedimentoHospitalarRN getVerificaCaracteristicaItemProcedimentoHospitalarRN() {
		return verificaCaracteristicaItemProcedimentoHospitalarRN;
	}

}
