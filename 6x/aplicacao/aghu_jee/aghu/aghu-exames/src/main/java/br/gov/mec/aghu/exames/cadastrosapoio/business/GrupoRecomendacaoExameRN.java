package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelGrupoRecomendacaoExamJnDAO;
import br.gov.mec.aghu.exames.dao.AelGrupoRecomendacaoExameDAO;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExamJn;
import br.gov.mec.aghu.model.AelGrupoRecomendacaoExame;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class GrupoRecomendacaoExameRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoRecomendacaoExameRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelGrupoRecomendacaoExamJnDAO aelGrupoRecomendacaoExamJnDAO;
	
	@Inject
	private AelGrupoRecomendacaoExameDAO aelGrupoRecomendacaoExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1647116332895990222L;

	public enum GrupoRecomendacaoExameRNExceptionCode implements
			BusinessExceptionCode {
		ERRO_POS_REMOVER, ERRO_POS_ATUALIZAR, ERRO_GENERICO_REMOCAO_GRUPO_RECOMENDACAO_EXAME, AEL_00353;
		public void throwException(Object... params)
				throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this, params);
		}
	}

	public AelGrupoRecomendacaoExame inserir(AelGrupoRecomendacaoExame entity) throws BaseException {
		this.preInserir(entity);
		this.getGrupoRecomendacaoExameDAO().persistir(entity);
		this.getGrupoRecomendacaoExameDAO().flush();
		return entity;
	}

	public void remover(AelGrupoRecomendacaoExame entity) throws BaseException {
		String descricao = entity.getGrupoRecomendacao().getDescricao();
		String descricaoExame = entity.getNomeUsualMaterial();
		try{
			this.getGrupoRecomendacaoExameDAO().remover(entity);
			this.getGrupoRecomendacaoExameDAO().flush();
		} catch (PersistenceException e) {
			logError("Exceção capturada: ", e);
			GrupoRecomendacaoExameRNExceptionCode.ERRO_GENERICO_REMOCAO_GRUPO_RECOMENDACAO_EXAME.throwException(descricao, descricaoExame);
		}
		this.posRemover(entity);
	}

	// VALIDACOES
	// =====================================================================

	/**
	 * ORADB TRIGGER AELT_GRX_ARD<br>
	 * 
	 * Insere na tabela Journalling.<br>
	 * 
	 * @param entity
	 * @throws ApplicationBusinessException
	 */
	protected void posRemover(AelGrupoRecomendacaoExame entity) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {
			AelGrupoRecomendacaoExamJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelGrupoRecomendacaoExamJn.class, servidorLogado.getUsuario());
			this.doSetJournal(journal, entity);
			getAelGrupoRecomendacaoExamJnDAO().persistir(journal);
			getAelGrupoRecomendacaoExamJnDAO().flush();
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(GrupoRecomendacaoExameRNExceptionCode.ERRO_POS_REMOVER, AelGrupoRecomendacaoExame.class.getSimpleName());
		}
	}

	/**
	 * ORADB TRIGGER AELT_GRX_BRI<br>
	 * 
	 * Atualiza o campo CRIADO_EM para a data atual.<br>
	 * 
	 * Verifica se a matricula do usuario eh nulo. Se for nulo apresenta uma
	 * mensagem de erro. Senao atualiza o campo SERVIDOR.<br>
	 * 
	 * @param entity
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	protected void preInserir(AelGrupoRecomendacaoExame entity) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		entity.setCriadoEm(new Date());
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(GrupoRecomendacaoExameRNExceptionCode.AEL_00353);
		}
		
		entity.setServidor(servidorLogado);
	}

	/**
	 * ORADB TRIGGER AELT_GRX_ARU<br>
	 * 
	 * Se algum dado foi modificado insere na tabela Journalling.<br>
	 * 
	 * @param entity
	 *  
	 */
	@SuppressWarnings("ucd")
	protected void posAtualizar(AelGrupoRecomendacaoExame entity, AelGrupoRecomendacaoExame oldEntity) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {
			if (this.hasModificacao(entity, oldEntity)) {
				AelGrupoRecomendacaoExamJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelGrupoRecomendacaoExamJn.class, servidorLogado.getUsuario());
				this.doSetJournal(journal, oldEntity);
				getAelGrupoRecomendacaoExamJnDAO().persistir(journal);
				getAelGrupoRecomendacaoExamJnDAO().flush();
			}
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(GrupoRecomendacaoExameRNExceptionCode.ERRO_POS_ATUALIZAR, AelGrupoRecomendacaoExame.class.getSimpleName());
		}
	}

	protected void doSetJournal(AelGrupoRecomendacaoExamJn journal, AelGrupoRecomendacaoExame oldEntity) {
		journal.setCriadoEm(oldEntity.getCriadoEm());
		journal.setEmaExaSigla(oldEntity.getId().getEmaExaSigla());
		journal.setEmaManSeq(oldEntity.getId().getEmaManSeq());
		journal.setGrmSeq(oldEntity.getId().getGrmSeq());
		journal.setServidor(oldEntity.getServidor());
	}

	protected boolean hasModificacao(AelGrupoRecomendacaoExame entity, AelGrupoRecomendacaoExame oldEntity) {
		if (entity == null || oldEntity == null) {
			throw new IllegalArgumentException("Parametro Obrigatorio nao informado.");
		}
		return (CoreUtil.modificados(entity.getCriadoEm(), oldEntity.getCriadoEm())
				|| CoreUtil.modificados(entity.getServidor(), oldEntity.getServidor()) 
				|| CoreUtil.modificados(entity.getId(), oldEntity.getId()));
	}

	// GETTERS
	// =====================================================================

	protected AelGrupoRecomendacaoExameDAO getGrupoRecomendacaoExameDAO() {
		return aelGrupoRecomendacaoExameDAO;
	}

	protected AelGrupoRecomendacaoExamJnDAO getAelGrupoRecomendacaoExamJnDAO() {
		return aelGrupoRecomendacaoExamJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
