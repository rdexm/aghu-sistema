package br.gov.mec.aghu.checagemeletronica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.checagemeletronica.dao.EceJustificativaMdtoDAO;
import br.gov.mec.aghu.model.EceJustificativaMdto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EceJustificativaMdtoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(EceJustificativaMdtoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private EceJustificativaMdtoDAO eceJustificativaMdtoDAO;

	private static final long serialVersionUID = -3663433333425339536L;
	
	public enum EceJustificativaMdtoExceptionCode implements BusinessExceptionCode {
		MENSAGEM_JUS_MED_FORA_HORARIO_JA_EXISTENTE
	}

	public void persistirJustificativaMdto(EceJustificativaMdto justificativaMdto) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		List<EceJustificativaMdto> jusList = getEceJustificativaMdtoDAO().pesquisarJustificativasPorSeqDescricaoSituacao(Integer.valueOf(0), 
				Integer.valueOf(10), EceJustificativaMdto.Fields.SEQ.toString(), true, null, justificativaMdto.getDescricao());
		if(jusList!=null && jusList.size()>0){
			for (EceJustificativaMdto epeJustMedco : jusList){
				if (epeJustMedco.getSeq()!=justificativaMdto.getSeq()){
					throw new ApplicationBusinessException(EceJustificativaMdtoExceptionCode.MENSAGEM_JUS_MED_FORA_HORARIO_JA_EXISTENTE);
				}
			}	
		}
				
		if (justificativaMdto.getSeq() == null){
			//Faz a inserção	
			/**
			 * ORADB TRIGGER "AGH".ECET_EJM_BRI 
			 * @param bloqSalaCirurgica, servidorLogado
			 * @throws ApplicationBusinessException 
			 * RN1
			 */
			justificativaMdto.setCriadoEm(new Date());
			justificativaMdto.setRapServidoresByEceEjmRapFk1(servidorLogado);
			getEceJustificativaMdtoDAO().persistir(justificativaMdto);
		}
		else{	
			//Faz a atualização	
			/**
			 * ORADB TRIGGER "AGH".ECET_EJM_BRU 
			 * @param bloqSalaCirurgica, servidorLogado
			 * @throws ApplicationBusinessException 
			 * RN2
			 */
			justificativaMdto.setAlteradoEm(new Date());
			justificativaMdto.setRapServidoresByEceEjmRapFk2(servidorLogado);
			getEceJustificativaMdtoDAO().merge(justificativaMdto);
		}

	}
	
	protected EceJustificativaMdtoDAO getEceJustificativaMdtoDAO() {
		return eceJustificativaMdtoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
