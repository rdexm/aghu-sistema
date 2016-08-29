package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterMaterialAnaliseON extends BaseBusiness {


@EJB
private ManterMaterialAnaliseRN manterMaterialAnaliseRN;

private static final Log LOG = LogFactory.getLog(ManterMaterialAnaliseON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = -7793045529500231592L;

	public AelMateriaisAnalises persistirMaterialAnalise(AelMateriaisAnalises material) throws ApplicationBusinessException {
		AelMateriaisAnalises retorno = null;
		
		if(material.getSeq() == null) {
			//Realiza inserção
			retorno = getManterMaterialAnaliseRN().inserir(material);
		} else {
			//Realiza atualização
			retorno = getManterMaterialAnaliseRN().atualizar(material);
		}
		
		return retorno;
	}
	
	public void removerMaterialAnalise(Integer codigo) throws ApplicationBusinessException {
		//Realiza deleção
		getManterMaterialAnaliseRN().remover(codigo);
	}
	
	//--------------------------------------------------
	//Getters
	
	
	protected ManterMaterialAnaliseRN getManterMaterialAnaliseRN() {
		return manterMaterialAnaliseRN;
	}
	
}