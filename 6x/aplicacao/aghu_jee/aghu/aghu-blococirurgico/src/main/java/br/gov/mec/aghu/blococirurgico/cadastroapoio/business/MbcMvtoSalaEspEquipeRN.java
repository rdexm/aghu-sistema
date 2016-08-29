package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoSalaEspEquipeDAO;
import br.gov.mec.aghu.model.MbcMvtoSalaEspEquipe;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcMvtoSalaEspEquipeRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcMvtoSalaEspEquipeRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMvtoSalaEspEquipeDAO mbcMvtoSalaEspEquipeDAO;


	/**
	 * @author fpalma
	 */
	private static final long serialVersionUID = -5791285976857430151L;

	public enum MbcMvtoSalaEspEquipeRNExceptionCode implements BusinessExceptionCode {
		MBC_01281;
	}
	

	public void persistirMbcMvtoSalaEspEquipe(MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe) throws BaseException {
		if (mbcMvtoSalaEspEquipe.getSeq() == null) {
			inserir(mbcMvtoSalaEspEquipe);
		} else {
			atualizar(mbcMvtoSalaEspEquipe);
		}
	}
	
	public void inserir(MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe) throws BaseException {
		preInserir(mbcMvtoSalaEspEquipe);
		this.getMbcMvtoSalaEspEquipeDAO().persistir(mbcMvtoSalaEspEquipe);
	}
	
	public void atualizar(MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe) throws BaseException {
		preAtualizar(mbcMvtoSalaEspEquipe);
		this.getMbcMvtoSalaEspEquipeDAO().atualizar(mbcMvtoSalaEspEquipe);
	}

	/**
	 * @ORADB MBCT_MSE_BRI
	 * 
	 */
	private void preInserir(MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mbcMvtoSalaEspEquipe.setCriadoEm(new Date());
		mbcMvtoSalaEspEquipe.setRapServidoresByMbcMseSerFk1(servidorLogado);
	}
	
	/**
	 * @ORADB MBCT_MSE_BRU
	 * 
	 */
	private void preAtualizar(MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe) throws BaseException  {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		mbcMvtoSalaEspEquipe.setAlteradoEm(new Date());
		mbcMvtoSalaEspEquipe.setRapServidoresByMbcMseSerFk2(servidorLogado);
		verificarDadosAtualizados(mbcMvtoSalaEspEquipe);
	}
	
	protected void verificarDadosAtualizados(MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipe) throws BaseException {
		MbcMvtoSalaEspEquipe mbcMvtoSalaEspEquipeOriginal = getMbcMvtoSalaEspEquipeDAO().obterOriginal(mbcMvtoSalaEspEquipe);

		if (CoreUtil.modificados(mbcMvtoSalaEspEquipe.getMbcCaractSalaEsp(),mbcMvtoSalaEspEquipeOriginal.getMbcCaractSalaEsp())
				|| CoreUtil.modificados(mbcMvtoSalaEspEquipe.getMbcProfAtuaUnidCirgs(), mbcMvtoSalaEspEquipeOriginal.getMbcProfAtuaUnidCirgs())
				|| CoreUtil.modificados(mbcMvtoSalaEspEquipe.getPercentualReserva(), mbcMvtoSalaEspEquipeOriginal.getPercentualReserva())
				|| CoreUtil.modificados(mbcMvtoSalaEspEquipe.getHoraInicioEquipe(), mbcMvtoSalaEspEquipeOriginal.getHoraInicioEquipe())
				|| CoreUtil.modificados(mbcMvtoSalaEspEquipe.getHoraFimEquipe(), mbcMvtoSalaEspEquipeOriginal.getHoraFimEquipe())
				|| CoreUtil.modificados(mbcMvtoSalaEspEquipe.getIndSituacao(), mbcMvtoSalaEspEquipeOriginal.getIndSituacao())) {
			throw new ApplicationBusinessException(MbcMvtoSalaEspEquipeRNExceptionCode.MBC_01281);
		}


	}
	
	protected MbcMvtoSalaEspEquipeDAO getMbcMvtoSalaEspEquipeDAO() {
		return mbcMvtoSalaEspEquipeDAO;
	}
}
