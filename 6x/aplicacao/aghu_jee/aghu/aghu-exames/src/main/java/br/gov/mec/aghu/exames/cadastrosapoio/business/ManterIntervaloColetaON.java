package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelIntervaloColeta;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterIntervaloColetaON extends BaseBusiness {


@EJB
private ManterIntervaloColetaRN manterIntervaloColetaRN;

private static final Log LOG = LogFactory.getLog(ManterIntervaloColetaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1575634989142946909L;

	public enum ManterIntervaloColetaONExceptionCode implements BusinessExceptionCode {
		AEL_00352;

		public void throwException(Object... params) throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	public AelIntervaloColeta persistirIntervaloColeta(AelIntervaloColeta intervalo) throws ApplicationBusinessException {
		AelIntervaloColeta retorno = null;
		
		//Verifica preenchimento de campos dependentes
		validarPreenchimentoCampos(intervalo);
		
		if(intervalo.getSeq() == null) {
			//Realiza inserção
			retorno = getManterIntervaloColetaRN().inserir(intervalo);
		} else {
			//Realiza atualização
			retorno = getManterIntervaloColetaRN().atualizar(intervalo);
		}
		
		return retorno;
	}
	
	public void removerIntervaloColeta(Short codigo) throws ApplicationBusinessException {
		//Realiza deleção
		getManterIntervaloColetaRN().remover(codigo);
	}
	
	private void validarPreenchimentoCampos(AelIntervaloColeta intervalo) throws ApplicationBusinessException {
		//Verifica o preenchimento dos campos volume ingerido, unid. medida volume e tipo substância
		int countValores = 0;
		if(intervalo.getVolumeIngerido() != null) {
			countValores++;
		}
		if(intervalo.getUnidMedidaVolume() != null) {
			countValores++;		
		}
		if(intervalo.getTipoSubstancia() != null) {
			countValores++;
		}
		if(countValores != 0 && countValores != 3) {
			ManterIntervaloColetaONExceptionCode.AEL_00352.throwException();
		}
	}
	
	//--------------------------------------------------
	//Getters
	
	protected ManterIntervaloColetaRN getManterIntervaloColetaRN() {
		return manterIntervaloColetaRN;
	}
	
}
