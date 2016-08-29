package br.gov.mec.aghu.business.bancosangue;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacaoHemoterapicaJnDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AbsSolicitacaoHemoterapicaJn;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class SolicitacaoHemoterapicaJournalRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(SolicitacaoHemoterapicaJournalRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AbsSolicitacaoHemoterapicaJnDAO absSolicitacaoHemoterapicaJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1540541421155738043L;


	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	
	protected AbsSolicitacaoHemoterapicaJnDAO getAbsSolicitacaoHemoterapicaJnDAO() {
		return absSolicitacaoHemoterapicaJnDAO;
	}

	/**
	 * Metodo que compara alguns atributos de uma solicitacao hemoterapica antes de depois de alterado e, conforme a validacao, 
	 * retorna true indicando se deve ser inserido um registro na tabela de journal.
	 * @param solHemNew
	 * @param solHemOld
	 * @return
	 */
	private Boolean verificarInsercaoSolicitacaoHemoterapicaJournal(AbsSolicitacoesHemoterapicas solHemNew, AbsSolicitacoesHemoterapicas solHemOld){
		boolean inserirJournal = false;
		
		if((solHemNew.getMotivoCancelaColeta() == null && solHemOld.getMotivoCancelaColeta() != null)
			||(solHemNew.getMotivoCancelaColeta() != null && solHemOld.getMotivoCancelaColeta() == null)
		){
			inserirJournal = true;
		}
		
		return inserirJournal;
		
	}
	
	
	
	/**
	 * Metodo para setar os atributos de um solHemJn antes de inseri-lo.
	 * @param solHemOld
	 * @return
	 */
	private AbsSolicitacaoHemoterapicaJn prepararSolicitacaoHemoterapicaJnParaGravar(AbsSolicitacoesHemoterapicas solHemOld) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AbsSolicitacaoHemoterapicaJn solHemJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AbsSolicitacaoHemoterapicaJn.class, servidorLogado.getUsuario());

		solHemJn.setAtdSeq(solHemOld.getPrescricaoMedica().getAtendimento().getSeq());
		solHemJn.setSeq(solHemOld.getId().getSeq());
		solHemJn.setDthrCancelamentoColeta(solHemOld.getDthrCancelamentoColeta());
		solHemJn.setMccSeq(solHemOld.getMotivoCancelaColeta() != null ? solHemOld.getMotivoCancelaColeta().getSeq() : null);
		solHemJn.setSerMatriculaCancelaColeta(solHemOld.getServidorCancelaColeta() != null ? solHemOld.getServidorCancelaColeta().getId().getMatricula() : null);
		solHemJn.setSerVinCodigoCancelaColeta(solHemOld.getServidorCancelaColeta() != null ? solHemOld.getServidorCancelaColeta().getId().getVinCodigo() : null);
		solHemJn.setIndSituacaoColeta(solHemOld.getIndSituacaoColeta());
		solHemJn.setIndResponsavelColeta(solHemOld.getIndResponsavelColeta());
		
		return solHemJn;
	}		
	
	
	/**
	 * Metodo para realizar a inclusao de journal de solicitacao hemoterapica, conforme a operacao, caso necessario.
	 * @param solHemNew
	 * @param solHemOld
	 * @param operacao
	 */
	public void realizarSolicitacaoHemoterapicaJournal(AbsSolicitacoesHemoterapicas solHemNew, AbsSolicitacoesHemoterapicas solHemOld, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
			
			if(DominioOperacoesJournal.UPD.equals(operacao)
					&& verificarInsercaoSolicitacaoHemoterapicaJournal(solHemNew, solHemOld)){
				getAbsSolicitacaoHemoterapicaJnDAO().persistirSolicitacaoHemoterapicaJn(prepararSolicitacaoHemoterapicaJnParaGravar(solHemOld));
			}
		
			//TODO Implementar aqui outros casos de operacoes de journal (delete, etc) conforme necessidade.
			
	}


	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
