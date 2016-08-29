package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelExamesEspecialidadeDAO;
import br.gov.mec.aghu.exames.dao.AelExamesEspecialidadeJnDAO;
import br.gov.mec.aghu.model.AelExamesEspecialidade;
import br.gov.mec.aghu.model.AelExamesEspecialidadeJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterRestringirPedidoEspecialidadeCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterRestringirPedidoEspecialidadeCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AelExamesEspecialidadeJnDAO aelExamesEspecialidadeJnDAO;

	@Inject
	private AelExamesEspecialidadeDAO aelExamesEspecialidadeDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5546811022050616332L;

	public enum ManterRestringirPedidoEspecialidadeExceptionCode implements BusinessExceptionCode {
		AEL_00434, ESPECIALIDADE_JA_CADASTRADA;
	}

	public void persistirExameEspecialidade(AelExamesEspecialidade exameEspecialidade) throws ApplicationBusinessException {
		preInserirExameEspecialidade(exameEspecialidade);
		getAelExamesEspecialidadeDAO().merge(exameEspecialidade);
		getAelExamesEspecialidadeDAO().flush();
	}

	protected void preInserirExameEspecialidade(AelExamesEspecialidade exameEspecialidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		exameEspecialidade.setRapServidor(servidorLogado);
		exameEspecialidade.setCriadoEm(new Date());

		verificaStatusEspecialidade(exameEspecialidade);
		verificaEspecialidadeJaExistente(exameEspecialidade);
	}

	/**
	 * ORADB AELT_PUS_BRI
	 * 
	 * @param unidadeFuncionalSolicitante
	 * @throws ApplicationBusinessException
	 */
	protected void verificaStatusEspecialidade(AelExamesEspecialidade exameEspecialidade) throws ApplicationBusinessException {
		if (!exameEspecialidade.getAghEspecialidades().getIndSituacao().equals(DominioSituacao.A)) {
			throw new ApplicationBusinessException(ManterRestringirPedidoEspecialidadeExceptionCode.AEL_00434);
		}
	}

	/**
	 * @param unidadeFuncionalSolicitante
	 * @throws ApplicationBusinessException
	 */
	protected void verificaEspecialidadeJaExistente(AelExamesEspecialidade exameEspecialidade) throws ApplicationBusinessException {

		AelExamesEspecialidade exaEspExistente = getAelExamesEspecialidadeDAO().buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeqEspSeq(exameEspecialidade.getId().getUfeEmaExaSigla(),
				exameEspecialidade.getId().getUfeEmaManSeq(), exameEspecialidade.getId().getUfeUnfSeq(), exameEspecialidade.getId().getEspSeq());

		if (exaEspExistente != null) {
			throw new ApplicationBusinessException(ManterRestringirPedidoEspecialidadeExceptionCode.ESPECIALIDADE_JA_CADASTRADA);
		}
	}

	/**
	 * ORADB AELT_EUE_ARD
	 * 
	 * @param exameEspecialidade
	 * @throws ApplicationBusinessException
	 */
	public void removerExameEspecialidade(String emaExaSigla, Integer emaManSeq, Short unfSeq, Short espSeq) throws ApplicationBusinessException {

		AelExamesEspecialidade exameEspecialidade = getAelExamesEspecialidadeDAO().buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeqEspSeq(emaExaSigla, emaManSeq, unfSeq, espSeq);

		getAelExamesEspecialidadeDAO().remover(exameEspecialidade);
		getAelExamesEspecialidadeDAO().flush();
		posRemoverExameEspecialidade(exameEspecialidade);
	}

	/**
	 * ORADB AELT_EUE_ARD
	 * 
	 * @param exameEspecialidade
	 * @throws ApplicationBusinessException
	 */
	protected void posRemoverExameEspecialidade(AelExamesEspecialidade exameEspecialidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AelExamesEspecialidadeJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, AelExamesEspecialidadeJn.class, servidorLogado.getUsuario());

		jn.setEspSeq(exameEspecialidade.getId().getEspSeq());
		jn.setUfeEmaExaSigla(exameEspecialidade.getId().getUfeEmaExaSigla());
		jn.setUfeEmaManSeq(exameEspecialidade.getId().getUfeEmaManSeq());
		jn.setUfeUnfSeq(exameEspecialidade.getId().getUfeUnfSeq());
		jn.setSerMatricula(exameEspecialidade.getRapServidor().getId().getMatricula());
		jn.setSerVinCodigo(exameEspecialidade.getRapServidor().getId().getVinCodigo());
		jn.setCriadoEm(exameEspecialidade.getCriadoEm());
		getAelExamesEspecialidadeJnDAO().persistir(jn);
		getAelExamesEspecialidadeJnDAO().flush();
	}

	protected AelExamesEspecialidadeDAO getAelExamesEspecialidadeDAO() {
		return aelExamesEspecialidadeDAO;
	}

	protected AelExamesEspecialidadeJnDAO getAelExamesEspecialidadeJnDAO() {
		return aelExamesEspecialidadeJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}