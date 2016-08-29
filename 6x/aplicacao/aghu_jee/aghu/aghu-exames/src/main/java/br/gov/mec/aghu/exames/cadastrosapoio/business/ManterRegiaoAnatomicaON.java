package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelRegiaoAnatomica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterRegiaoAnatomicaON extends BaseBusiness {

	@EJB
	private ManterRegiaoAnatomicaRN manterRegiaoAnatomicaRN;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LogFactory.getLog(ManterRegiaoAnatomicaON.class);
	}

	private static final long serialVersionUID = 4441951986806020950L;

	public AelRegiaoAnatomica persistirRegiaoAnatomica(AelRegiaoAnatomica regiao) throws ApplicationBusinessException {
		AelRegiaoAnatomica retorno = null;
		
		if(regiao.getSeq() == null) {
			//Realiza inserção
			retorno = getManterRegiaoAnatomicaRN().inserir(regiao);
		} else {
			//Realiza atualização
			retorno = getManterRegiaoAnatomicaRN().atualizar(regiao);
		}
		
		return retorno;
	}
	
	public void removerRegiaoAnatomica(Integer seqRegiao) throws ApplicationBusinessException {
		//Realiza deleção
		getManterRegiaoAnatomicaRN().remover(seqRegiao);
	}
	
	protected ManterRegiaoAnatomicaRN getManterRegiaoAnatomicaRN() {
		return manterRegiaoAnatomicaRN;
	}
}