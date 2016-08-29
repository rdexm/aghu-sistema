package br.gov.mec.aghu.exames.cadastrosapoio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelExameResuNotificacaoDAO;
import br.gov.mec.aghu.exames.dao.AelExamesNotificacaoDAO;
import br.gov.mec.aghu.model.AelExamesNotificacao;
import br.gov.mec.aghu.model.AelExamesNotificacaoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ExameNotificacaoON extends BaseBusiness {

	
	@EJB
	private ExameNotificacaoRN exameNotificacaoRN;
	
	private static final Log LOG = LogFactory.getLog(ExameNotificacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@Inject
	private AelExameResuNotificacaoDAO aelExameResuNotificacaoDAO;
	
	@Inject
	private AelExamesNotificacaoDAO aelExamesNotificacaoDAO;
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 8835061640001287059L;

	public enum ExameNotificacaoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_REMOVER_DEPENDENCIAS;
	}
	
	public void atualizarSituacaoExameNotificacao(final AelExamesNotificacao exameNotificacao) throws ApplicationBusinessException {
		if(exameNotificacao.getSituacao().equals(DominioSituacao.A)){
			exameNotificacao.setSituacao(DominioSituacao.I);
		} else {
			exameNotificacao.setSituacao(DominioSituacao.A);
		}
		AelExamesNotificacao exameNotificacaoOld = this.getAelExamesNotificacaoDAO().obterOriginal(exameNotificacao.getId());
		this.getExameNotificacaoRN().atualizar(exameNotificacao, exameNotificacaoOld);
	}
	
	public void persistirExameNotificacao(final AelExamesNotificacaoId id, final DominioSituacao situacao) throws ApplicationBusinessException {
		AelExamesNotificacao exameNotificacaoOld = this.getAelExamesNotificacaoDAO().obterOriginal(id);
		AelExamesNotificacao exameNotificacao = new AelExamesNotificacao();
		if(exameNotificacaoOld!=null && exameNotificacaoOld.getId()!=null){
			exameNotificacao = this.getAelExamesNotificacaoDAO().obterPorChavePrimaria(id);
			exameNotificacao.setSituacao(situacao);
			this.getExameNotificacaoRN().atualizar(exameNotificacao, exameNotificacaoOld);	
		} else {
			exameNotificacao.setId(id);
			exameNotificacao.setSituacao(situacao);
			this.getExameNotificacaoRN().persistir(exameNotificacao);
		}
	}
	
	public void remover(AelExamesNotificacaoId id) throws ApplicationBusinessException {
		final AelExamesNotificacao exameNotificacao = getAelExamesNotificacaoDAO().obterPorChavePrimaria(id);
		
		if (exameNotificacao == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		this.validarRelacionamentosExameNotificacaoBeforeDelete(exameNotificacao);
		this.getExameNotificacaoRN().remover(exameNotificacao);
	}

	
	
	/**
	 * Valida se o exame notificacao possui relacionamento com AEL_EXAMES_RESU_NOTIFICACAO
	 * 
	 * @param exameNotificacao
	 * @throws ApplicationBusinessException
	 */
	public void validarRelacionamentosExameNotificacaoBeforeDelete(AelExamesNotificacao exameNotificacao) throws ApplicationBusinessException {
		if(this.getAelExameResuNotificacaoDAO().existeDependenciaExameNotificacao(exameNotificacao)){
			throw new ApplicationBusinessException(ExameNotificacaoONExceptionCode.MENSAGEM_ERRO_REMOVER_DEPENDENCIAS,
			"AEL_EXAMES_RESU_NOTIFICACAO");
		}
	}
	
	protected ExameNotificacaoRN getExameNotificacaoRN() {
		return exameNotificacaoRN;
	}
	
	protected AelExamesNotificacaoDAO getAelExamesNotificacaoDAO() {
		return aelExamesNotificacaoDAO;
	}
	
	protected AelExameResuNotificacaoDAO getAelExameResuNotificacaoDAO() {
		return aelExameResuNotificacaoDAO;
	}

	

}
