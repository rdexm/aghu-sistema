package br.gov.mec.aghu.compras.parecer.business;


import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoParecerMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerOcorrenciaDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoParecerMaterial;
import br.gov.mec.aghu.model.ScoParecerOcorrencia;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoParecerOcorrenciaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ScoParecerOcorrenciaON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ScoParecerMaterialDAO scoParecerMaterialDAO;
	
	@Inject
	private ScoParecerOcorrenciaDAO scoParecerOcorrenciaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7518477434707318927L;
	
	public enum ScoParecerOcorrenciaONExceptionCode implements
			BusinessExceptionCode {

		MENSAGEM_PARAM_OBRIG,ERRO_CLONE_PARECER;
	}

	public void persistirParecerOcorrencia(ScoParecerOcorrencia scoParecerOcorrencia) throws ApplicationBusinessException{
		
		if (scoParecerOcorrencia == null) {
			throw new ApplicationBusinessException(
					ScoParecerOcorrenciaONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (scoParecerOcorrencia.getCodigo() == null){
			
			if (scoParecerOcorrencia.getIndSituacao() != null) {
				if (scoParecerOcorrencia.getIndSituacao().equals(
						DominioSituacao.A)) {
					this.inativasOcorrenciasAnteriores(scoParecerOcorrencia
							.getParecerMaterial());
				}
			}
			scoParecerOcorrencia.setDtCriacao(new Date());
			scoParecerOcorrencia.setServidorCriacao(servidorLogado);			
			this.getScoParecerOcorrenciaDAO().persistir(scoParecerOcorrencia);			
		}
		else {
			scoParecerOcorrencia.setDtCriacao(new Date());
			scoParecerOcorrencia.setServidorCriacao(servidorLogado);
			this.getScoParecerOcorrenciaDAO().merge(scoParecerOcorrencia);			
		}
		this.getScoParecerOcorrenciaDAO().flush();
	}
	
	public void inativasOcorrenciasAnteriores(ScoParecerMaterial scoParecerMaterial){
		
		List<ScoParecerOcorrencia> listaOcorrenciasAtivas = this.getScoParecerOcorrenciaDAO().listaOcorrenciaParecer(scoParecerMaterial, DominioSituacao.A);
		
		for (ScoParecerOcorrencia itemScoParecerOcorrencia : listaOcorrenciasAtivas){
			itemScoParecerOcorrencia.setIndSituacao(DominioSituacao.I);
			this.getScoParecerOcorrenciaDAO().persistir(itemScoParecerOcorrencia);
		}
		
	}	
	
	public ScoParecerOcorrencia clonarParecerOcorrencia(ScoParecerOcorrencia  scoParecerOcorrencia) throws ApplicationBusinessException{
		ScoParecerOcorrencia  scoParecerOcorrenciaCloneLocal = null;
		
		try{
			scoParecerOcorrenciaCloneLocal = (ScoParecerOcorrencia) BeanUtils.cloneBean(scoParecerOcorrencia);
		} catch(Exception e){
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(ScoParecerOcorrenciaONExceptionCode.ERRO_CLONE_PARECER);
		}
		
		if (scoParecerOcorrencia.getParecerMaterial() != null){
			scoParecerOcorrenciaCloneLocal.setParecerMaterial(this.getScoParecerMaterialDAO().obterPorChavePrimaria(scoParecerOcorrencia.getParecerMaterial().getCodigo()));
		}
		
		if(scoParecerOcorrencia.getServidorResponsavel() != null) {		
			RapServidores servidor =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(scoParecerOcorrencia.getServidorResponsavel().getId().getMatricula(), scoParecerOcorrencia.getServidorResponsavel().getId().getVinCodigo());
			scoParecerOcorrenciaCloneLocal.setServidorResponsavel(servidor);
		}
		if(scoParecerOcorrencia.getServidorCriacao() != null) {		
			RapServidores servidor =  getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(scoParecerOcorrencia.getServidorCriacao().getId().getMatricula(), scoParecerOcorrencia.getServidorCriacao().getId().getVinCodigo());
			scoParecerOcorrenciaCloneLocal.setServidorCriacao(servidor);
		}
		
		return scoParecerOcorrenciaCloneLocal;
		
	}	
	
	protected ScoParecerOcorrenciaDAO getScoParecerOcorrenciaDAO() {
		return scoParecerOcorrenciaDAO;
	}
	
	protected ScoParecerMaterialDAO getScoParecerMaterialDAO(){
		return scoParecerMaterialDAO;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}