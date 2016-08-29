package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnotacaoDAO;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class IncluirAnotacaoEquipeON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(IncluirAnotacaoEquipeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendaAnotacaoDAO mbcAgendaAnotacaoDAO;


	@EJB
	private IncluirAnotacaoEquipeRN incluirAnotacaoEquipeRN;

	private static final long serialVersionUID = 1779953909111759691L;

	private enum IncluirAnotacaoEquipeONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_INCLUIR_ANOTACAO_EQUIPE_ERRO_INCLUSAO_REGISTRO_JA_EXISTE
	}
	
	private MbcAgendaAnotacaoDAO getMbcAgendaAnotacaoDAO() {
		return mbcAgendaAnotacaoDAO;
	}

	/**
	 * #22454 ON1
	 */
	public void validarInsercaoMbcAgendaAnotacao(MbcAgendaAnotacao mbcAgendaAnotacao) throws ApplicationBusinessException {
		if(mbcAgendaAnotacao.getVersion() != null) {
			throw new ApplicationBusinessException(IncluirAnotacaoEquipeONExceptionCode.MENSAGEM_INCLUIR_ANOTACAO_EQUIPE_ERRO_INCLUSAO_REGISTRO_JA_EXISTE);
		}
	}
	
	public void persistirMbcAgendaAnotacao(MbcAgendaAnotacao mbcAgendaAnotacao) throws ApplicationBusinessException{
		
		if(getMbcAgendaAnotacaoDAO().contains(mbcAgendaAnotacao)){//Então é alteração
			getIncluirAnotacaoEquipeRN().mbctAgnBru(mbcAgendaAnotacao);
			getMbcAgendaAnotacaoDAO().atualizar(mbcAgendaAnotacao);
		}else{//senão é inclusão
			validarInsercaoMbcAgendaAnotacao(mbcAgendaAnotacao);
			getIncluirAnotacaoEquipeRN().mbctAgnBri(mbcAgendaAnotacao);
			getMbcAgendaAnotacaoDAO().persistir(mbcAgendaAnotacao);
		}
		
	}
	
	public IncluirAnotacaoEquipeRN getIncluirAnotacaoEquipeRN(){
		return incluirAnotacaoEquipeRN;
	}

}
