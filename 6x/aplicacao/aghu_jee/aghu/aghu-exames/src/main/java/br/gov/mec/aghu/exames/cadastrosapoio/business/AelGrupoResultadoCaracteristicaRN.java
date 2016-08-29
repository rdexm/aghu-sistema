package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoResultadoCaracteristicaDAO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelExameGrupoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelGrupoResultadoCaracteristicaRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelGrupoResultadoCaracteristicaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@Inject
	private AelExameGrupoCaracteristicaDAO aelExameGrupoCaracteristicaDAO;
	
	@Inject
	private AelGrupoResultadoCaracteristicaDAO aelGrupoResultadoCaracteristicaDAO;
	
	@Inject
	private AelCampoLaudoDAO aelCampoLaudoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7605465514505453564L;

	public enum AelGrupoResultadoCaracteristicaRNExceptionCode implements
			BusinessExceptionCode {

		AEL_00369,//
		EXISTE_DEPENDENCIA_EXAME_GRUPO_CARACTERISTICA, //
		EXISTE_DEPENDENCIA_CAMPO_LAUDO, //
		MSG_GRUPO_CARACTERISTICA_EXIST, //
		ERRO_REMOVER_GRUPO_CARACTERISTICA; //

	}

	
	/**
	 * Atualiza um registro na tabela AEL_GRUPO_RESULTADO_CARACTERIS
	 */
	public void atualizar(AelGrupoResultadoCaracteristica elemento) throws BaseException {
		this.preAtualizar(elemento);
		this.getAelGrupoResultadoCaracteristicaDAO().merge(elemento);
	}
	
	/**
	 * Persiste um registro na tabela AEL_GRUPO_RESULTADO_CARACTERIS
	 */
	public void persistir(AelGrupoResultadoCaracteristica elemento) throws BaseException {
		this.inserir(elemento);
	}

	
	/**
	 * Insere um registro na tabela AEL_GRUPO_RESULTADO_CARACTERIS
	 */
	private void inserir(AelGrupoResultadoCaracteristica elemento) throws BaseException {
		this.preInserir(elemento);
		this.getAelGrupoResultadoCaracteristicaDAO().persistir(elemento);
	}
	

	/**
	 * ORADB TRIGGER AELT_GCA_BRI
	 */
	protected void preInserir(AelGrupoResultadoCaracteristica elemento) throws BaseException {
		 this.obterServidorLogado(elemento);
		 this.verificaExistenciaGrupo(elemento);
		 elemento.setCriadoEm(new Date());
	}
	
	
	/**
	 * ORADB TRIGGER AELT_GCA_BRU
	 */
	protected void preAtualizar(AelGrupoResultadoCaracteristica elemento) throws BaseException {
		
		// A a existência na descrição do Grupo será validada quando a mesma for modificada
		AelGrupoResultadoCaracteristica oldElemento = this.getAelGrupoResultadoCaracteristicaDAO().obterOriginal(elemento.getSeq());
		if(oldElemento != null && CoreUtil.modificados(elemento.getDescricao(), oldElemento.getDescricao())) {
			this.verificaExistenciaGrupo(elemento);
		}
		
		this.verificarAlteracaoCampos(elemento);
	}

	
	/**
	 * ORADB TRIGGER AELT_GCA_BRD
	 */
	protected void preRemover(AelGrupoResultadoCaracteristica elemento) throws BaseException {
		
		//RN1
		this.verificarDependencias(elemento.getSeq());
		
		//RN2
		getAgendamentoExamesFacade().verificarDelecaoTipoMarcacaoExame(elemento.getCriadoEm());
	}
		
	
	/**
	 * Remove um registro na tabela AEL_GRUPO_RESULTADO_CARACTERIS
	 */
	public AelGrupoResultadoCaracteristica remover(Integer seqExclusao) throws BaseException {
		
		AelGrupoResultadoCaracteristica elemento = this.getAelGrupoResultadoCaracteristicaDAO().obterPorChavePrimaria(seqExclusao);
		
		if (elemento == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.preRemover(elemento);
		
		this.getAelGrupoResultadoCaracteristicaDAO().remover(elemento);
		
		try {
			// Caso nenhum erro ocorra faz o flush das alterações
			this.getAelGrupoResultadoCaracteristicaDAO().flush();
		} catch(PersistenceException e) {
			throw new ApplicationBusinessException(AelGrupoResultadoCaracteristicaRNExceptionCode.ERRO_REMOVER_GRUPO_CARACTERISTICA);
		}
		
		return elemento;
	}
	
	/**
	 * Obtem o servidor logado
	 */
	protected void obterServidorLogado(AelGrupoResultadoCaracteristica elemento) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		elemento.setServidor(servidorLogado);
	}
	
	/**
	 * trigger ael_gca_uk1
	 *  
	 */
	protected void verificaExistenciaGrupo(AelGrupoResultadoCaracteristica elemento) throws ApplicationBusinessException{
		AelGrupoResultadoCaracteristica grupo = this.getAelGrupoResultadoCaracteristicaDAO().obterGrupoResultadoCaracteristicaPorDescricao(elemento.getDescricao());
		if(grupo != null){
			if(elemento.getSeq() != null && !elemento.getSeq().equals(grupo.getSeq())){
				throw new ApplicationBusinessException(AelGrupoResultadoCaracteristicaRNExceptionCode.MSG_GRUPO_CARACTERISTICA_EXIST, elemento.getDescricao());	
			}else{
				throw new ApplicationBusinessException(AelGrupoResultadoCaracteristicaRNExceptionCode.MSG_GRUPO_CARACTERISTICA_EXIST, elemento.getDescricao());
			}
		}
	}

	
	/**
	 * ORADB TRIGGER AELT_GCA_BRU
	 */
	protected void verificarAlteracaoCampos(AelGrupoResultadoCaracteristica elemento) throws ApplicationBusinessException {
		AelGrupoResultadoCaracteristica oldElemento = this.getAelGrupoResultadoCaracteristicaDAO().obterOriginal(elemento.getSeq());
		
		if(CoreUtil.modificados(elemento.getServidor(), oldElemento.getServidor()) 
				|| CoreUtil.modificados(elemento.getCriadoEm(), oldElemento.getCriadoEm())) {
			throw new ApplicationBusinessException(AelGrupoResultadoCaracteristicaRNExceptionCode.AEL_00369);
		}
	}
	
	
	/**
	 * ORADB PROCEDURE CHK_AEL_GRUPO_RESULTADO_C
	 */
	protected void verificarDependencias(Integer seq) throws ApplicationBusinessException {
		List<AelExameGrupoCaracteristica> exameGrupoCaracteristicaList = 
			this.getAelExameGrupoCaracteristicaDAO().listarExameGrupoCarateristicaPorSeq(seq);
		
		List<AelCampoLaudo> campoLaudoList = this.getAelCampoLaudoDAO().listarCampoLaudoPorSeq(seq); 
		
		if(!exameGrupoCaracteristicaList.isEmpty()){
			throw new ApplicationBusinessException(AelGrupoResultadoCaracteristicaRNExceptionCode
					.EXISTE_DEPENDENCIA_EXAME_GRUPO_CARACTERISTICA);
		}
		
		if(!campoLaudoList.isEmpty()) {
			throw new ApplicationBusinessException(AelGrupoResultadoCaracteristicaRNExceptionCode
					.EXISTE_DEPENDENCIA_CAMPO_LAUDO);
		}
		
	}
	
	
	/** GET **/
	
	protected IAgendamentoExamesFacade getAgendamentoExamesFacade() {
		return agendamentoExamesFacade;
	}	

	protected AelGrupoResultadoCaracteristicaDAO getAelGrupoResultadoCaracteristicaDAO() {
		return aelGrupoResultadoCaracteristicaDAO;
	}

	protected AelExameGrupoCaracteristicaDAO getAelExameGrupoCaracteristicaDAO() {
		return aelExameGrupoCaracteristicaDAO;
	}
	
	protected AelCampoLaudoDAO getAelCampoLaudoDAO() {
		return aelCampoLaudoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}