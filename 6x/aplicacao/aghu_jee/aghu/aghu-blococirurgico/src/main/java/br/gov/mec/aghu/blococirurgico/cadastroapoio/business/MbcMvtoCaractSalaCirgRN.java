package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcMvtoCaractSalaCirgDAO;
import br.gov.mec.aghu.model.MbcMvtoCaractSalaCirg;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcMvtoCaractSalaCirgRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcMvtoCaractSalaCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMvtoCaractSalaCirgDAO mbcMvtoCaractSalaCirgDAO;


	/**
	 * @author fpalma
	 */
	private static final long serialVersionUID = -2713802877963189677L;
	
	public enum MbcMvtoCaractSalaCirgRNExceptionCode implements BusinessExceptionCode {
		MBC_01280;
	}
	

	public void persistirMbcMvtoCaractSalaCirg(MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirg) throws BaseException {
		if (mbcMvtoCaractSalaCirg.getSeq() == null) {
			inserir(mbcMvtoCaractSalaCirg);
		} else {
			atualizar(mbcMvtoCaractSalaCirg);
		}
	}
	
	public void inserir(MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirg) throws BaseException {
		preInserir(mbcMvtoCaractSalaCirg);
		this.getMbcMvtoCaractSalaCirgDAO().persistir(mbcMvtoCaractSalaCirg);
	}
	
	public void atualizar(MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirg) throws BaseException {
		preAtualizar(mbcMvtoCaractSalaCirg);
		this.getMbcMvtoCaractSalaCirgDAO().merge(mbcMvtoCaractSalaCirg);
	}

	/**
	 * @ORADB MBCT_MCS_BRI
	 * 
	 */
	private void preInserir(MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirg) throws ApplicationBusinessException {
		mbcMvtoCaractSalaCirg.setCriadoEm(new Date());
		mbcMvtoCaractSalaCirg.setRapServidoresByMbcMcsSerFk1(servidorLogadoFacade.obterServidorLogado());
	}
	
	/**
	 * @ORADB MBCT_MCS_BRU
	 * 
	 */
	private void preAtualizar(MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirg) throws BaseException  {
		mbcMvtoCaractSalaCirg.setAlteradoEm(new Date());
		mbcMvtoCaractSalaCirg.setRapServidoresByMbcMcsSerFk2(servidorLogadoFacade.obterServidorLogado());
		verificarDadosAtualizados(mbcMvtoCaractSalaCirg);
	}
	
	protected void verificarDadosAtualizados(MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirg) throws BaseException {
		MbcMvtoCaractSalaCirg mbcMvtoCaractSalaCirgOriginal = getMbcMvtoCaractSalaCirgDAO().obterOriginal(mbcMvtoCaractSalaCirg);

		if (CoreUtil.modificados(mbcMvtoCaractSalaCirg.getMbcCaracteristicaSalaCirg(),mbcMvtoCaractSalaCirgOriginal.getMbcCaracteristicaSalaCirg())
				|| CoreUtil.modificados(mbcMvtoCaractSalaCirg.getMbcSalaCirurgica(), mbcMvtoCaractSalaCirgOriginal.getMbcSalaCirurgica())
				|| CoreUtil.modificados(mbcMvtoCaractSalaCirg.getMbcHorarioTurnoCirg(), mbcMvtoCaractSalaCirgOriginal.getMbcHorarioTurnoCirg())
				|| CoreUtil.modificados(mbcMvtoCaractSalaCirg.getDiaSemana(), mbcMvtoCaractSalaCirgOriginal.getDiaSemana())
				|| CoreUtil.modificados(mbcMvtoCaractSalaCirg.getCirurgiaParticular(), mbcMvtoCaractSalaCirgOriginal.getCirurgiaParticular())
				|| CoreUtil.modificados(mbcMvtoCaractSalaCirg.getSituacao(), mbcMvtoCaractSalaCirgOriginal.getSituacao())
				|| CoreUtil.modificados(mbcMvtoCaractSalaCirg.getIndUrgencia(), mbcMvtoCaractSalaCirgOriginal.getIndUrgencia())
				|| CoreUtil.modificados(mbcMvtoCaractSalaCirg.getIndDisponivel(), mbcMvtoCaractSalaCirgOriginal.getIndDisponivel())) {
			throw new ApplicationBusinessException(MbcMvtoCaractSalaCirgRNExceptionCode.MBC_01280);
		}

	}
	
	protected MbcMvtoCaractSalaCirgDAO getMbcMvtoCaractSalaCirgDAO() {
		return mbcMvtoCaractSalaCirgDAO;
	}
}
