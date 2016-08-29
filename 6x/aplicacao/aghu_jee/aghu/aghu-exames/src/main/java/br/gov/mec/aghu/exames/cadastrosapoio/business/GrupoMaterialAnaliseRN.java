package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoMaterialAnaliseDAO;
import br.gov.mec.aghu.model.AelGrupoMaterialAnalise;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoMaterialAnaliseRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoMaterialAnaliseRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrupoMaterialAnaliseDAO aelGrupoMaterialAnaliseDAO;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -8546011208326547074L;
	
	public enum GrupoMaterialRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_EXCLUIR_GRUPO_MATERIAL_ANALISE;
	}


	public void remover(Integer seqExclusao) throws ApplicationBusinessException {
		final AelGrupoMaterialAnalise grupoMaterialAnalise = this.getAelGrupoMaterialAnaliseDAO().obterPorChavePrimaria(seqExclusao);
		this.getAelGrupoMaterialAnaliseDAO().remover(grupoMaterialAnalise);
	}
	
	public void persistir(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException {
		this.preInserirGrupoMaterialAnalise(grupoMaterialAnalise);
		this.getAelGrupoMaterialAnaliseDAO().persistir(grupoMaterialAnalise);
	}
	
	public void atualizar(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException {
		this.preAtualizarGrupoMaterialAnalise(grupoMaterialAnalise);
		this.getAelGrupoMaterialAnaliseDAO().atualizar(grupoMaterialAnalise);
	}
	
	/**
	 * ORADB
	 * Trigger AELT_GMA_BRI
	 * @param grupoMaterialAnalise
	 * @throws ApplicationBusinessException  
	 */
	public void preInserirGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		grupoMaterialAnalise.setCriadoEm(new Date());
		grupoMaterialAnalise.setServidor(servidorLogado);
	}

	/**
	 * ORADB
	 * Trigger AELT_GMA_BRU
	 * @param grupoMaterialAnalise
	 * @throws ApplicationBusinessException  
	 */
	public void preAtualizarGrupoMaterialAnalise(AelGrupoMaterialAnalise grupoMaterialAnalise) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		grupoMaterialAnalise.setServidor(servidorLogado);
	}
		
	protected AelGrupoMaterialAnaliseDAO getAelGrupoMaterialAnaliseDAO() {
		return aelGrupoMaterialAnaliseDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
