package br.gov.mec.aghu.compras.parecer.business;


import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParecerAvalDesempDAO;
import br.gov.mec.aghu.model.ScoParecerAvalDesemp;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoParecerAvalDesempON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(ScoParecerAvalDesempON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoParecerAvalDesempDAO scoParecerAvalDesempDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7518477434707318927L;
	
	public enum ScoParecerAvalDesempONExceptionCode implements
			BusinessExceptionCode {

		MENSAGEM_PARAM_OBRIG;
	}

	public boolean isGravarAvaliacaoDesempenho(ScoParecerAvalDesemp scoParecerAvalDesemp){
		return (StringUtils.isNotBlank(scoParecerAvalDesemp.getDescricao()) ||
				(scoParecerAvalDesemp.getDtAvaliacao() != null &&
				 StringUtils.isNotBlank(scoParecerAvalDesemp.getDtAvaliacao().toString())) ||
				scoParecerAvalDesemp.getServidorAvaliacao() != null); 
		
		
	}
	
	public void persistirParecerAvaliacaoDesempenho(ScoParecerAvalDesemp scoParecerAvalDesemp) throws ApplicationBusinessException{
		
		if (scoParecerAvalDesemp == null) {
			throw new ApplicationBusinessException(
					ScoParecerAvalDesempONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		if (isGravarAvaliacaoDesempenho(scoParecerAvalDesemp)) {
			if (scoParecerAvalDesemp.getCodigo() == null) {

				inserirParecerAvaliacaoDesempenho(scoParecerAvalDesemp);

			} else {
				this.getScoParecerAvalDesempDAO().merge(
						scoParecerAvalDesemp);
			}
		} else if (scoParecerAvalDesemp.getCodigo() != null) {
			       this.getScoParecerAvalDesempDAO().remover(scoParecerAvalDesemp);
		}
	}
	
	public void inserirParecerAvaliacaoDesempenho(ScoParecerAvalDesemp scoParecerAvalDesemp) throws ApplicationBusinessException{
		scoParecerAvalDesemp.setDtCriacao(new Date());
		this.getScoParecerAvalDesempDAO().persistir(scoParecerAvalDesemp);
		
	}	
	
	protected ScoParecerAvalDesempDAO getScoParecerAvalDesempDAO() {
		return scoParecerAvalDesempDAO;
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}