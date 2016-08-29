package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoXMaterialAnaliseDAO;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnaliseId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoXMaterialAnaliseRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoXMaterialAnaliseRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrupoXMaterialAnaliseDAO aelGrupoXMaterialAnaliseDAO;

	public enum GrupoXMaterialAnaliseRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_INCLUIR_MATERIAL_GRUPO;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 5356073672740811073L;

	public void remover(AelGrupoXMaterialAnaliseId grupoXMaterialAnalise) {
		this.getAelGrupoXMaterialAnaliseDAO().removerPorId(grupoXMaterialAnalise);
	}
	
	public void persistir(AelGrupoXMaterialAnalise grupoXMaterialAnalise) throws ApplicationBusinessException {
		this.preInserirGrupoXMaterialAnalise(grupoXMaterialAnalise);
		this.getAelGrupoXMaterialAnaliseDAO().persistir(grupoXMaterialAnalise);
	}
	
	public void atualizar(AelGrupoXMaterialAnalise grupoXMaterialAnalise) throws ApplicationBusinessException {
		this.preAtualizarGrupoXMaterialAnalise(grupoXMaterialAnalise);
		this.getAelGrupoXMaterialAnaliseDAO().atualizar(grupoXMaterialAnalise);
	}
	
	/**
	 * ORADB
	 * Trigger AELT_GXM_BRI
	 * @param grupoXMaterialAnalise
	 * @throws ApplicationBusinessException  
	 */
	public void preInserirGrupoXMaterialAnalise(AelGrupoXMaterialAnalise grupoXMaterialAnalise) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelGrupoXMaterialAnalise grupoXMaterialAnaliseOld = this.getAelGrupoXMaterialAnaliseDAO().obterPorChavePrimaria(grupoXMaterialAnalise.getId());
		if(grupoXMaterialAnaliseOld!=null){
			throw new ApplicationBusinessException(GrupoXMaterialAnaliseRNExceptionCode.MENSAGEM_ERRO_INCLUIR_MATERIAL_GRUPO);
		}
		grupoXMaterialAnalise.setCriadoEm(new Date());
		grupoXMaterialAnalise.setServidor(servidorLogado);
	}

	/**
	 * ORADB
	 * Trigger AELT_GXM_BRU
	 * @param grupoXMaterialAnalise
	 * @throws ApplicationBusinessException  
	 */
	public void preAtualizarGrupoXMaterialAnalise(AelGrupoXMaterialAnalise grupoXMaterialAnalise) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		grupoXMaterialAnalise.setServidor(servidorLogado);
	}
		
	protected AelGrupoXMaterialAnaliseDAO getAelGrupoXMaterialAnaliseDAO() {
		return aelGrupoXMaterialAnaliseDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
