package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnotacaoDAO;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class IncluirAnotacaoEquipeRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(IncluirAnotacaoEquipeRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendaAnotacaoDAO mbcAgendaAnotacaoDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	private static final long serialVersionUID = 1779953909111759691L;

	private enum IncluirAnotacaoEquipeRNExceptionCode implements BusinessExceptionCode {
		MBC_01065,
		MBC_01066,
		MBC_01067
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	/**
	 * #22454 RN1 RN2
	 * @ORADB MBCT_AGN_BRI 
	 */
	public void mbctAgnBri(MbcAgendaAnotacao mbcAgendaAnotacao) throws ApplicationBusinessException {
		mbcAgendaAnotacao.setCriadoEm(new Date());
		mbcAgendaAnotacao.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		rnAgnpVerData(mbcAgendaAnotacao.getId().getData());
	}
	
	/**
	 * #22454 RN3
	 * @ORADB RN_AGNP_VER_DATA
	 */
	public void rnAgnpVerData(Date data) throws ApplicationBusinessException {
		if(DateUtil.truncaData(data).before(DateUtil.truncaData(new Date()))) {
			throw new ApplicationBusinessException(IncluirAnotacaoEquipeRNExceptionCode.MBC_01065);
		}
	}

	protected MbcAgendaAnotacaoDAO getMbcAgendaAnotacaoDAO() {
		return mbcAgendaAnotacaoDAO;
	}
	
	/**
	 * #22454 RN4 RN5
	 * @ORADB MBCT_AGN_BRU
	 */
	public void mbctAgnBru(MbcAgendaAnotacao mbcAgendaAnotacao) throws ApplicationBusinessException {
		mbcAgendaAnotacao.setAlteradoEm(new Date());
		mbcAgendaAnotacao.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		MbcAgendaAnotacao mbcAgendaAnotacaoOriginal = getMbcAgendaAnotacaoDAO().obterOriginal(mbcAgendaAnotacao.getId());
		if (CoreUtil.modificados(mbcAgendaAnotacao.getId().getData(),mbcAgendaAnotacaoOriginal.getId().getData()) || 
			CoreUtil.modificados(mbcAgendaAnotacao.getDescricao(),mbcAgendaAnotacaoOriginal.getDescricao())) {
			rnAgnpVerAltData(mbcAgendaAnotacao.getId().getData(),mbcAgendaAnotacaoOriginal.getId().getData(),mbcAgendaAnotacao.getDescricao(),mbcAgendaAnotacaoOriginal.getDescricao());
		}
	}
	
	/**
	 * #22454 RN6
	 * @ORADB RN_AGNP_VER_ALT_DATA 
	 */
	public void rnAgnpVerAltData(Date dataNova, Date dataAntiga, String descricaoNova, String descricaoAntiga) throws ApplicationBusinessException {
		if (CoreUtil.modificados(descricaoNova,descricaoAntiga) && dataNova.before(dataAntiga)) {
			throw new ApplicationBusinessException(IncluirAnotacaoEquipeRNExceptionCode.MBC_01066);
		}
		if (CoreUtil.modificados(dataNova,dataAntiga) && dataNova.before(dataAntiga)) {
			throw new ApplicationBusinessException(IncluirAnotacaoEquipeRNExceptionCode.MBC_01067);
		}
	}
	
}
