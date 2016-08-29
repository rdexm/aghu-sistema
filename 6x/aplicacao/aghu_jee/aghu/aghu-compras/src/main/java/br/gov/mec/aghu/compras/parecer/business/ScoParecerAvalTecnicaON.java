package br.gov.mec.aghu.compras.parecer.business;


import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParecerAvalTecnicaDAO;
import br.gov.mec.aghu.model.ScoParecerAvalTecnica;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoParecerAvalTecnicaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoParecerAvalTecnicaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoParecerAvalTecnicaDAO scoParecerAvalTecnicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7518477434707318927L;
	
	public enum ScoParecerAvalTecnicaONExceptionCode implements
			BusinessExceptionCode {

		MENSAGEM_PARAM_OBRIG;
	}

	public boolean isGravarAvaliacaoTecnica(ScoParecerAvalTecnica scoParecerAvalTecnica){
		return (StringUtils.isNotBlank(scoParecerAvalTecnica.getDescricao()) ||
				(scoParecerAvalTecnica.getDtAvaliacao() != null &&
				 StringUtils.isNotBlank(scoParecerAvalTecnica.getDtAvaliacao().toString())) ||
				scoParecerAvalTecnica.getServidorAvaliacao() != null); 
		
		
	}
	
	public void persistirParecerAvaliacaoTecnica(ScoParecerAvalTecnica scoParecerAvalTecnica) throws ApplicationBusinessException{
		
		if (scoParecerAvalTecnica == null) {
			throw new ApplicationBusinessException(
					ScoParecerAvalTecnicaONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		if (isGravarAvaliacaoTecnica(scoParecerAvalTecnica)) {
			if (scoParecerAvalTecnica.getCodigo() == null) {

				inserirParecerAvaliacaoTecnica(scoParecerAvalTecnica);

			} else {
				this.getScoParecerAvalTecnicaDAO().merge(
						scoParecerAvalTecnica);
			}
		} else if (scoParecerAvalTecnica.getCodigo() != null){
					this.getScoParecerAvalTecnicaDAO().remover(scoParecerAvalTecnica);		
		}
	}
	
	public void inserirParecerAvaliacaoTecnica(ScoParecerAvalTecnica scoParecerAvalTecnica) throws ApplicationBusinessException{
		scoParecerAvalTecnica.setDtCriacao(new Date());
		this.getScoParecerAvalTecnicaDAO().persistir(scoParecerAvalTecnica);
		
	}	
	
	protected ScoParecerAvalTecnicaDAO getScoParecerAvalTecnicaDAO() {
		return scoParecerAvalTecnicaDAO;
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}