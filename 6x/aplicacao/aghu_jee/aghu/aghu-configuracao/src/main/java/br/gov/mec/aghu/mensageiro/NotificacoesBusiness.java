package br.gov.mec.aghu.mensageiro;

import java.util.Date;

import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioIndTerminoNotificacoes;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghNotificacoes;

public abstract class NotificacoesBusiness extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6710924927671024791L;

	private static final Log LOG = LogFactory.getLog(NotificacoesBusiness.class);
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	public boolean preEnviarMensagemWhatsApp(AghJobDetail job) throws ApplicationBusinessException {
		AghNotificacoes notificacao = getAdministracaoFacade().pesquisarNotificacaoPorDescricao(job.getNomeProcesso().split("-")[1]);
		
		if(notificacao.getInicioEm().after(new Date())) {
			getSchedulerFacade().adicionarLog(job, "Mensagem apenas será enviada após a data " + notificacao.getInicioEm());
			return false;
		} else if(DominioIndTerminoNotificacoes.TA.equals(notificacao.getIndTerminoNotificacoes())) {
			if(notificacao.getTerminaApos() <= 0) {
				return removerJobDetail(job);
			}
		} else if(DominioIndTerminoNotificacoes.TE.equals(notificacao.getIndTerminoNotificacoes())) {
			if(notificacao.getTerminaEm().compareTo(new Date()) < 0) {
				return removerJobDetail(job);
			}
		}
		
		if(enviarMensagemWhatsapp(notificacao, job)) {
			if(DominioIndTerminoNotificacoes.TA.equals(notificacao.getIndTerminoNotificacoes())) {
				notificacao.setTerminaApos(notificacao.getTerminaApos() - 1);
				try {
					getAdministracaoFacade().alterarNotificacao(notificacao, null);
				} catch (BaseException e) {
					getSchedulerFacade().adicionarLog(job, "Erro ao decrementar número de ocorrências da notificação");
				}
			}
			return true;
		}
		return false;
	}

	private boolean removerJobDetail(AghJobDetail job) {
		try {
			this.schedulerFacade.removerAghJobDetail(job, Boolean.TRUE);
		} catch (BaseException e) {
			getSchedulerFacade().adicionarLog(job, e.getLocalizedMessage());
		}
		return false;
	}
	
	protected abstract Boolean enviarMensagemWhatsapp(AghNotificacoes notificacao, AghJobDetail job) throws ApplicationBusinessException;
	
	protected ISchedulerFacade getSchedulerFacade() {
		return schedulerFacade;
	}
	
	protected IAdministracaoFacade getAdministracaoFacade() {
		return administracaoFacade;
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
}
