package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelUnidMedValorNormalDAO;
import br.gov.mec.aghu.exames.dao.AelUnidMedValorNormalJNDAO;
import br.gov.mec.aghu.model.AelUnidMedValorNormal;
import br.gov.mec.aghu.model.AelUnidMedValorNormalJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class UnidadeMedidaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(UnidadeMedidaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelUnidMedValorNormalDAO aelUnidMedValorNormalDAO;
	
	@Inject
	private AelUnidMedValorNormalJNDAO aelUnidMedValorNormalJNDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 624966576016640790L;

	public enum ManterUnidadeMedidaRNExceptionCode implements BusinessExceptionCode {
		AEL_01739,
		AEL_01740;
	}

	public AelUnidMedValorNormal inserir(AelUnidMedValorNormal elemento) throws ApplicationBusinessException {
		
		preInserirUnidadeMedida(elemento);

		getAelUnidMedValorNormalDAO().persistir(elemento);
		return elemento;
	}

	/**
	 * @ORADB Trigger AELT_UMV_BRI
	 * 
	 * @param aelUnidMedValorNormal
	 */
	private void preInserirUnidadeMedida(AelUnidMedValorNormal aelUnidMedValorNormal) {
		aelUnidMedValorNormal.setCriadoEm(new Date());		
	}

	public AelUnidMedValorNormal atualizar(AelUnidMedValorNormal elemento) throws ApplicationBusinessException {

		AelUnidMedValorNormal aelUnidMedValorNormalOld = getAelUnidMedValorNormalDAO().obterAntigoPeloId(elemento.getSeq());

		//		//Regras pré-update
		preAtualizarUnidadeMedida(elemento, aelUnidMedValorNormalOld);
		
		aelUnidMedValorNormalDAO.merge(elemento);

		posAtualizarUnidadeMedida(elemento, aelUnidMedValorNormalOld);

		return elemento;
	}

	
	/**
	 * @ORADB Trigger AELT_UMV_ARD
	 * 
	 * @param aelUnidMedValorNormal
	 * @throws ApplicationBusinessException 
	 */
	private void posAtualizarUnidadeMedida(AelUnidMedValorNormal aelUnidMedValorNormal, AelUnidMedValorNormal aelUnidMedValorNormalOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelUnidMedValorNormal.getCriadoEm(), aelUnidMedValorNormalOld.getCriadoEm())
				|| CoreUtil.modificados(aelUnidMedValorNormal.getServidor().getMatriculaVinculo(), aelUnidMedValorNormalOld.getServidor().getMatriculaVinculo())
				|| CoreUtil.modificados(aelUnidMedValorNormal.getServidor().getVinculo().getCodigo(), aelUnidMedValorNormalOld.getServidor().getVinculo().getCodigo())
				|| CoreUtil.modificados(aelUnidMedValorNormal.getIndSituacao().toString(), aelUnidMedValorNormalOld.getIndSituacao().toString())
				|| CoreUtil.modificados(aelUnidMedValorNormal.getDescricao(), aelUnidMedValorNormalOld.getDescricao())
				|| CoreUtil.modificados(aelUnidMedValorNormal.getSeq(), aelUnidMedValorNormalOld.getSeq())) {

			AelUnidMedValorNormalJn aelUnidMedValorNormalJn =  criarAelUnidMedValorNormalJn(aelUnidMedValorNormalOld);
			getAelUnidMedValorNormalJNDAO().persistir(aelUnidMedValorNormalJn);
		}
	}


	private AelUnidMedValorNormalJNDAO getAelUnidMedValorNormalJNDAO() {
		return aelUnidMedValorNormalJNDAO;
	}

	private AelUnidMedValorNormalJn criarAelUnidMedValorNormalJn(AelUnidMedValorNormal aelUnidMedValorNormal) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AelUnidMedValorNormalJn aelUnidMedValorNormalJn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AelUnidMedValorNormalJn.class, servidorLogado.getUsuario());
		
		aelUnidMedValorNormalJn.setSeq(aelUnidMedValorNormal.getSeq());
		aelUnidMedValorNormalJn.setDescricao(aelUnidMedValorNormal.getDescricao());
		aelUnidMedValorNormalJn.setIndSituacao(aelUnidMedValorNormal.getIndSituacao().toString());
		aelUnidMedValorNormalJn.setCriadoEm(aelUnidMedValorNormal.getCriadoEm());
		aelUnidMedValorNormalJn.setServidor(aelUnidMedValorNormal.getServidor());
		return aelUnidMedValorNormalJn;
	}


	/**
	 * @ORADB Trigger AELT_UMV_BRU
	 * @param aelUnidMedValorNormal
	 * @throws ApplicationBusinessException
	 */
	protected void preAtualizarUnidadeMedida(AelUnidMedValorNormal aelUnidMedValorNormal, AelUnidMedValorNormal aelUnidMedValorNormalOld) throws ApplicationBusinessException {
		if (CoreUtil.modificados(aelUnidMedValorNormal.getCriadoEm(), aelUnidMedValorNormalOld.getCriadoEm())
				|| CoreUtil.modificados(aelUnidMedValorNormal.getServidor(), aelUnidMedValorNormalOld.getServidor())) {
			throw new ApplicationBusinessException(ManterUnidadeMedidaRNExceptionCode.AEL_01739);
		}

		if (CoreUtil.modificados(aelUnidMedValorNormal.getDescricao(), aelUnidMedValorNormalOld.getDescricao())) {
			throw new ApplicationBusinessException(ManterUnidadeMedidaRNExceptionCode.AEL_01740);
		}			
	}

	protected AelUnidMedValorNormalDAO getAelUnidMedValorNormalDAO() {
		return aelUnidMedValorNormalDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
