package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaDiagnosticoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendaDiagnosticoId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcAgendaDiagnosticoRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendaDiagnosticoRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAgendaDiagnosticoDAO mbcAgendaDiagnosticoDAO;


	@EJB
	private MbcAgendaHistoricoRN mbcAgendaHistoricoRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	private static final long	serialVersionUID	= -2593920062710280442L;

	public enum MbcAgendaDiagnosticoRNExceptionCode implements BusinessExceptionCode {
		MBC_01130, ERRO_INESPERADO_OPERACAO
		;
	}

	public void persistirAgendaDiagnostico(final MbcAgendaDiagnostico agendaDiagnostico) throws BaseException {
		if (agendaDiagnostico.getId() == null) {
			try {
				this.executarAntesInserir(agendaDiagnostico, agendaDiagnostico.getMbcAgendas().getSeq());
				this.getMbcAgendaDiagnosticoDAO().persistir(agendaDiagnostico);
			} catch (BaseException e) {
				agendaDiagnostico.setId(null);
				throw e;
			} catch (Exception e) {
				agendaDiagnostico.setId(null);
				throw new BaseException(MbcAgendaDiagnosticoRNExceptionCode.ERRO_INESPERADO_OPERACAO, e);
			}
		} else {
			MbcAgendaDiagnostico oldMbcAgendaDiag = this.getMbcAgendaDiagnosticoDAO().obterOriginal(agendaDiagnostico.getId());
			this.executarAntesAtualizar(agendaDiagnostico, oldMbcAgendaDiag);
			this.getMbcAgendaDiagnosticoDAO().merge(agendaDiagnostico);
		}
	}

	public void excluirAgendaDiagnostico(final MbcAgendaDiagnostico agendaDiagnostico) throws BaseException {
		if (agendaDiagnostico.getId() != null) {
			this.executarAntesDeletar(agendaDiagnostico);
			this.getMbcAgendaDiagnosticoDAO().remover(agendaDiagnostico);
		}
	}

	/**
	 * @ORADB MBCT_ADI_BRD
	 * 
	 * 
	 * @throws BaseException
	 */
	private void executarAntesDeletar(final MbcAgendaDiagnostico agendaDiagnostico) throws BaseException {
		this.validarAgendaComControleEscalaCirurgicaDefinitiva(agendaDiagnostico.getMbcAgendas().getSeq());
		this.incluirHistoricoAgendaDiagnostico(null, agendaDiagnostico, DominioOperacaoAgenda.E);
	}

	private void definirId(MbcAgendaDiagnostico agendaDiagnostico) {
		MbcAgendaDiagnosticoId id = new MbcAgendaDiagnosticoId();
		id.setAgdSeq(agendaDiagnostico.getMbcAgendas().getSeq());
		id.setCidSeq(agendaDiagnostico.getAghCid().getSeq());
		agendaDiagnostico.setId(id);
	}

	/**
	 * @ORADB MBCT_ADI_BRI
	 * 
	 * 
	 * @throws BaseException
	 */
	private void executarAntesInserir(final MbcAgendaDiagnostico agendaDiagnostico, final Integer agdSeq) throws BaseException {
		this.validarAgendaComControleEscalaCirurgicaDefinitiva(agdSeq);

		this.definirId(agendaDiagnostico);
		agendaDiagnostico.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		agendaDiagnostico.setCriadoEm(new Date());
	}


	/**
	 * @ORADB mbck_adi_rn.rn_adip_ver_escala
	 * 
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarAgendaComControleEscalaCirurgicaDefinitiva(final Integer agdSeq) throws ApplicationBusinessException {
		final MbcAgendas result = getMbcAgendasDAO().pesquisarAgendaComControleEscalaCirurgicaDefinitiva(agdSeq);
		if (result != null && result.getIndGeradoSistema() != null && !result.getIndGeradoSistema()) {
			throw new ApplicationBusinessException(MbcAgendaDiagnosticoRNExceptionCode.MBC_01130);
		}
	}

	/**
	 * @throws BaseException
	 * @ORADB mbck_adi_rn.rn_adip_inc_historic
	 * 
	 * 
	 * @throws BaseException
	 */
	public void incluirHistoricoAgendaDiagnostico(final MbcAgendaDiagnostico newAgendaDiagnostico, final MbcAgendaDiagnostico oldAgendaDiagnostico,
			final DominioOperacaoAgenda operacao) throws BaseException {
		final StringBuffer hist = new StringBuffer(144);
		switch (operacao) {
		case E:
			hist.append("CID excluído: ").append(oldAgendaDiagnostico.getAghCid().getDescricao());
			if (hist.length() != 0) {
				this.getMbcAgendaHistoricoRN().inserir(oldAgendaDiagnostico.getMbcAgendas().getSeq(), oldAgendaDiagnostico.getMbcAgendas().getIndSituacao(),
						DominioOrigem.H, hist.toString(), operacao);
			}
			break;
		case A:
			if (CoreUtil.modificados(newAgendaDiagnostico.getAghCid(), oldAgendaDiagnostico.getAghCid())) {
				hist.append("CID alterado de ").append(oldAgendaDiagnostico.getAghCid().getDescricao()).append(" para ")
						.append(newAgendaDiagnostico.getAghCid().getDescricao());
			}
			if (hist.length() != 0) {
				this.getMbcAgendaHistoricoRN().inserir(newAgendaDiagnostico.getMbcAgendas().getSeq(), newAgendaDiagnostico.getMbcAgendas().getIndSituacao(),
						DominioOrigem.H, hist.toString(), operacao);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @ORADB MBCT_ADI_BRU
	 * 
	 * 
	 * @throws BaseException
	 */
	private void executarAntesAtualizar(final MbcAgendaDiagnostico newAgendaDiagnostico, final MbcAgendaDiagnostico oldAgendaDiagnostico) throws BaseException {
		this.validarAgendaComControleEscalaCirurgicaDefinitiva(newAgendaDiagnostico.getMbcAgendas().getSeq());
		if (CoreUtil.modificados(newAgendaDiagnostico.getAghCid(), oldAgendaDiagnostico.getAghCid())) {
			this.incluirHistoricoAgendaDiagnostico(newAgendaDiagnostico, oldAgendaDiagnostico, DominioOperacaoAgenda.A);
		}
	}

	protected MbcAgendaDiagnosticoDAO getMbcAgendaDiagnosticoDAO() {
		return mbcAgendaDiagnosticoDAO;
	}

	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}

	protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN() {
		return mbcAgendaHistoricoRN;
	}
}