package br.gov.mec.aghu.compras.parecer.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParecerAvalConsulDAO;
import br.gov.mec.aghu.model.ScoParecerAvalConsul;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoParecerAvalConsulON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ScoParecerAvalConsulON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoParecerAvalConsulDAO scoParecerAvalConsulDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7518477434707318927L;
	
	public enum ScoParecerAvalConsulONExceptionCode implements
			BusinessExceptionCode {

		MENSAGEM_PARAM_OBRIG;
	}

	public boolean isGravarAvaliacaoConsul(ScoParecerAvalConsul scoParecerAvalConsul){
		return (StringUtils.isNotBlank(scoParecerAvalConsul.getDescricao()) ||
				(scoParecerAvalConsul.getDtAvaliacao() != null &&
				 StringUtils.isNotBlank(scoParecerAvalConsul.getDtAvaliacao().toString())) ||
				scoParecerAvalConsul.getServidorAvaliacao() != null); 
		
		
	}
	
	public void persistirParecerAvaliacaoConsul(ScoParecerAvalConsul scoParecerAvalConsul) throws ApplicationBusinessException{
		
		if (scoParecerAvalConsul == null) {
			throw new ApplicationBusinessException(
					ScoParecerAvalConsulONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		if (isGravarAvaliacaoConsul(scoParecerAvalConsul)) {
			if (scoParecerAvalConsul.getCodigo() == null) {

				inserirParecerAvaliacaoConsul(scoParecerAvalConsul);

			} else {
				this.getScoParecerAvalConsulDAO().merge(
						scoParecerAvalConsul);
			}
		} else if (scoParecerAvalConsul.getCodigo() != null) {
					this.getScoParecerAvalConsulDAO().remover(scoParecerAvalConsul);					
		}
		
	}
	
	public void inserirParecerAvaliacaoConsul(ScoParecerAvalConsul scoParecerAvalConsul) throws ApplicationBusinessException{
		scoParecerAvalConsul.setDtCriacao(new Date());
		this.getScoParecerAvalConsulDAO().persistir(scoParecerAvalConsul);
		
	}	
	
	protected ScoParecerAvalConsulDAO getScoParecerAvalConsulDAO() {
		return scoParecerAvalConsulDAO;
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}