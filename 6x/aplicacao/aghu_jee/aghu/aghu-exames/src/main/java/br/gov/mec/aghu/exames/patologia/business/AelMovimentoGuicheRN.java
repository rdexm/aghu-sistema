package br.gov.mec.aghu.exames.patologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.exames.dao.AelMovimentoGuicheDAO;
import br.gov.mec.aghu.model.AelMovimentoGuiche;
import br.gov.mec.aghu.model.AelMovimentoGuicheId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AelMovimentoGuicheRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelMovimentoGuicheRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelMovimentoGuicheDAO aelMovimentoGuicheDAO;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;

	private static final long serialVersionUID = -798406697870064652L;

	public enum AelMovimentoGuicheRNExceptionCode implements BusinessExceptionCode {
		AEL_01917;
	}

	public void inserir(final AelMovimentoGuiche aelMovimentoGuiche) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		// @ORADB AELT_MGU_BRI
		aelMovimentoGuiche.setServidor(servidorLogado);
		getAelMovimentoGuicheDAO().persistir(aelMovimentoGuiche);
		getAelMovimentoGuicheDAO().flush();
	}

	// @ORADB AELK_CGU_RN.RN_CGUP_ATU_MVTO
	public AelMovimentoGuiche criarAelMovimentoGuiche(final Short cguSeq, String nomeMicrocomputador) throws ApplicationBusinessException {
		try {
			final AelMovimentoGuiche aelMovimentoGuiche = new AelMovimentoGuiche(new AelMovimentoGuicheId(cguSeq, new Date()), null,
					this.obterComputadoDoUsuarioLogado(nomeMicrocomputador), null);
			this.inserir(aelMovimentoGuiche);
			
			return aelMovimentoGuiche;
		} catch (final ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw e;
		} catch (final Exception e) {
			throw new ApplicationBusinessException(AelMovimentoGuicheRNExceptionCode.AEL_01917, "#1");
		}
	}

	public String obterComputadoDoUsuarioLogado(String nomeMicrocomputador) throws ApplicationBusinessException {
		return getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIPException(nomeMicrocomputador).getNome();
	}

	protected AelMovimentoGuicheDAO getAelMovimentoGuicheDAO() {
		return aelMovimentoGuicheDAO;
	}

	protected IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
