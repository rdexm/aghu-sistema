package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelRecipienteColeta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ManterRecipienteColetaON extends BaseBusiness {

	@EJB
	private ManterRecipienteColetaRN manterRecipienteColetaRN;

	private static final Log LOG = LogFactory.getLog(ManterRecipienteColetaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6171562270108448642L;


	public void saveOrUpdateRecipienteColeta(AelRecipienteColeta recipienteColeta) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(recipienteColeta != null && recipienteColeta.getSeq() == null){
			//Preenche com o usuário logado
			recipienteColeta.setServidor(servidorLogado);
			//Realiza inserção
			this.getManterRecipienteColetaRN().inserirRecipienteColeta(recipienteColeta);
		} else {
			this.getManterRecipienteColetaRN().atualizarRecipienteColeta(recipienteColeta);
		}
	}
	
	protected ManterRecipienteColetaRN getManterRecipienteColetaRN() {
		return manterRecipienteColetaRN;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
