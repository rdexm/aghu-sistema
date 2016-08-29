package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoRecomendacaoDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoRecomendacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoRecomendacaoJnDAO;
import br.gov.mec.aghu.model.AelGrupoRecomendacao;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class GrupoRecomendacaoRN extends BaseBusiness {

	@EJB
	private GrupoRecomendacaoExameRN grupoRecomendacaoExameRN;
	
	private static final Log LOG = LogFactory.getLog(GrupoRecomendacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrupoRecomendacaoJnDAO aelGrupoRecomendacaoJnDAO;
	
	@Inject
	private AelGrupoRecomendacaoDAO aelGrupoRecomendacaoDAO;
	
	@Inject
	private AelGrupoRecomendacaoExameDAO aelGrupoRecomendacaoExameDAO;
		
	private static final long serialVersionUID = -3678057994078209947L;

	public enum GrupoRecomendacaoRNExceptionCode implements BusinessExceptionCode {
		AEL_00346 // A descrição não pode ser alterada
		, ERRO_GENERICO_REMOCAO_GRUPO_RECOMENDACAO
		, AEL_00353;
		
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}
	

	public void atualizar(AelGrupoRecomendacao entity, List<AelGrupoRecomendacaoExame> lista) throws BaseException {
		AelGrupoRecomendacao original = this.getGrupoRecomendacaoDAO().obterOriginal(entity);
		this.preAtualizar(entity);
		GrupoRecomendacaoExameRN rn = this.getGrupoRecomendacaoExameRN();
		
		// Remove da base os itens que nao estao mais na lista.
		List<AelGrupoRecomendacaoExame> listaOld = getGrupoRecomendacaoExameDAO().getGrupoRecomendacaoExames(entity);
		for (AelGrupoRecomendacaoExame grupoRecExameOld : listaOld) {
			if (!lista.contains(grupoRecExameOld)) {
				rn.remover(grupoRecExameOld);
			}
		}
		
		// Insere os novos. Nao precisa de atualizar.
		for (AelGrupoRecomendacaoExame grupoRecomendacaoExame : lista) {
			if (grupoRecomendacaoExame.getId() == null) {
				grupoRecomendacaoExame.setGrupoRecomendacao(entity);
				rn.inserir(grupoRecomendacaoExame);
			}
		}
		
		this.getGrupoRecomendacaoDAO().merge(entity);
		this.getGrupoRecomendacaoDAO().flush();
		this.posAtualizar(entity, original);
	}

	

	public AelGrupoRecomendacao inserir(AelGrupoRecomendacao entity, List<AelGrupoRecomendacaoExame> lista) throws BaseException {
		this.preInserir(entity);
		this.getGrupoRecomendacaoDAO().persistir(entity);
		this.getGrupoRecomendacaoDAO().flush();
		
		for (AelGrupoRecomendacaoExame aelGrupoRecomendacaoExame : lista) {
			aelGrupoRecomendacaoExame.setGrupoRecomendacao(entity);
			grupoRecomendacaoExameRN.inserir(aelGrupoRecomendacaoExame);
		}
		
		return entity;
	}

	public void remover(AelGrupoRecomendacao entity) throws BaseException {
		if (entity == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		GrupoRecomendacaoExameRN rn = this.getGrupoRecomendacaoExameRN();
		
		List<AelGrupoRecomendacaoExame> lista = entity.getGrupoRecomendacaoExames();
		String descricao = entity.getDescricao();
		for (AelGrupoRecomendacaoExame aelGrupoRecomendacaoExame : lista) {
			rn.remover(aelGrupoRecomendacaoExame);
		}
		
		try{
			this.getGrupoRecomendacaoDAO().remover(entity);
			this.getGrupoRecomendacaoDAO().flush();
		} catch (PersistenceException e) {
			LOG.error("Exceção capturada: ", e);
			GrupoRecomendacaoRNExceptionCode.ERRO_GENERICO_REMOCAO_GRUPO_RECOMENDACAO.throwException(descricao);
		}
		this.posRemover(entity);
	}

	// Validacoes
	// ===========================================================

	/**
	 * ORADB TRIGGER AELT_GRM_BRU<br>
	 * 
	 * Verifica se a descrição foi alterada.<br>
	 * Se sim apresenta uma mensagem e erro. AEL-00346.<br>
	 * @throws ApplicationBusinessException 
	 * 
	 */
	protected void preAtualizar(AelGrupoRecomendacao entity) throws ApplicationBusinessException {
		if (entity == null || entity.getSeq() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado.");
		}
		AelGrupoRecomendacao original = this.getGrupoRecomendacaoDAO().obterOriginal(entity);
		String descOriginal = (original.getDescricao() != null) ? original.getDescricao().replaceAll("\r", "") : null;
		
		entity.setDescricao((entity.getDescricao() != null) ? entity.getDescricao().replaceAll("\r", "") : null);
		
		if (CoreUtil.modificados(entity.getDescricao(), descOriginal)) {
			throw new ApplicationBusinessException(GrupoRecomendacaoRNExceptionCode.AEL_00346);
		}
	}

	/**
	 * ORADB TRIGGER AELT_GRM_ARU<br>
	 * 
	 * Se algum dado foi modificado insere na tabela Journalling<br>
	 *  
	 */
	protected void posAtualizar(AelGrupoRecomendacao entity, AelGrupoRecomendacao oldEntity) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
	
		if (this.hasModificacao(entity, oldEntity)) {
			AelGrupoRecomendacaoJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelGrupoRecomendacaoJn.class, servidorLogado.getUsuario());
			this.doSetJournal(journal, oldEntity);
			getAelGrupoRecomendacaoJnDAO().persistir(journal);
			getAelGrupoRecomendacaoJnDAO().flush();
		}
	}

	/**
	 * ORADB TRIGGER AELT_GRM_BRI<br>
	 * 
	 * Atualiza o campo CRIADO_EM para a data atual.<br>
	 * 
	 * Verifica se a matrícula do usuário é nulo. Se for nulo apresenta 
	 * uma mensagem de erro.<br>
	 * 
	 * @param entity
	 * @throws ApplicationBusinessException  
	 */
	protected void preInserir(AelGrupoRecomendacao entity) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//entity.setDescricao((entity.getDescricao() != null) ? entity.getDescricao().toUpperCase() : null);
		entity.setCriadoEm(new Date());
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(GrupoRecomendacaoRNExceptionCode.AEL_00353);
		}

		entity.setServidor(servidorLogado);
	}

	/**
	 * ORADB TRIGGER AELT_GRM_ARD<br>
	 * 
	 * Insere na tabela Journalling<br>
	 * 
	 */
	protected void posRemover(AelGrupoRecomendacao entity) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
	
		AelGrupoRecomendacaoJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelGrupoRecomendacaoJn.class, servidorLogado.getUsuario());
		this.doSetJournal(journal, entity);
		getAelGrupoRecomendacaoJnDAO().persistir(journal);
	}
	
	protected boolean hasModificacao(AelGrupoRecomendacao entity, AelGrupoRecomendacao oldEntity) {
		if (entity == null || oldEntity == null) {
			throw new IllegalArgumentException("Parametros obrigatorios nao informados.");
		}
		return (
			CoreUtil.modificados(entity.getSeq(), oldEntity.getSeq())
			|| CoreUtil.modificados(entity.getDescricao(), oldEntity.getDescricao())
			|| CoreUtil.modificados(entity.getIndSituacao(), oldEntity.getIndSituacao())
			|| CoreUtil.modificados(entity.getCriadoEm(), oldEntity.getCriadoEm())
			|| CoreUtil.modificados(entity.getServidor(), oldEntity.getServidor())
			|| CoreUtil.modificados(entity.getResponsavel(), oldEntity.getResponsavel())
			|| CoreUtil.modificados(entity.getAbrangencia(), oldEntity.getAbrangencia())
		);
	}
	
	protected void doSetJournal(AelGrupoRecomendacaoJn journal, AelGrupoRecomendacao oldEntity) {
		journal.setAbrangencia(oldEntity.getAbrangencia());
		journal.setCriadoEm(oldEntity.getCriadoEm());
		journal.setDescricao(oldEntity.getDescricao());
		journal.setIndSituacao(oldEntity.getIndSituacao());
		journal.setResponsavel(oldEntity.getResponsavel());
		journal.setSeq(oldEntity.getSeq());
		journal.setServidor(oldEntity.getServidor());
	}

	// GETTERS
	// ===========================================================

	protected AelGrupoRecomendacaoDAO getGrupoRecomendacaoDAO() {
		return aelGrupoRecomendacaoDAO;
	}
	
	protected AelGrupoRecomendacaoJnDAO getAelGrupoRecomendacaoJnDAO() {
		return aelGrupoRecomendacaoJnDAO;
	}
	
	protected GrupoRecomendacaoExameRN getGrupoRecomendacaoExameRN() {
		return grupoRecomendacaoExameRN;
	}
	
	protected AelGrupoRecomendacaoExameDAO getGrupoRecomendacaoExameDAO() {
		return aelGrupoRecomendacaoExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
}