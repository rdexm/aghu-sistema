package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHistoricoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.dominio.DominioOperacaoAgenda;
import br.gov.mec.aghu.dominio.DominioOrigem;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio de MbcAgendaHemoterapia.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class MbcAgendaHemoterapiaRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendaHemoterapiaRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcAgendaHistoricoDAO mbcAgendaHistoricoDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;


	@EJB
	private IBancoDeSangueFacade iBancoDeSangueFacade;

	@EJB
	private MbcAgendaHistoricoRN mbcAgendaHistoricoRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	private static final long	serialVersionUID	= -2593920062710280442L;

	public enum MbcAgendaProcedimentoRNExceptionCode implements BusinessExceptionCode {
		MBC_00836, MBC_00865, MBC_00866, MBC_00868, MBC_00867, ERRO_INESPERADO_OPERACAO;
	}

	public void persistirAgendaHemoterapia(final MbcAgendaHemoterapia agendaHemoterapia) throws BaseException {
		MbcAgendaHemoterapia mbcAgendaHemoterapiaOriginal = this.getMbcAgendaHemoterapiaDAO().obterOriginal(agendaHemoterapia.getId());
		
		if (mbcAgendaHemoterapiaOriginal == null) {
			try {
				this.executarAntesInserir(agendaHemoterapia, agendaHemoterapia.getId().getAgdSeq());
				this.getMbcAgendaHemoterapiaDAO().persistir(agendaHemoterapia);
			} catch (BaseException e) {
				agendaHemoterapia.setId(null);
				throw e;
			} catch (Exception e) {
				agendaHemoterapia.setId(null);
				throw new BaseException(MbcAgendaProcedimentoRNExceptionCode.ERRO_INESPERADO_OPERACAO, e);
			}
		} else {
			this.executarAntesAtualizar(agendaHemoterapia, mbcAgendaHemoterapiaOriginal);
			this.getMbcAgendaHemoterapiaDAO().merge(agendaHemoterapia);
		}
	}

	public void excluirAgendaHemoterapia(final MbcAgendaHemoterapia agendaHemoterapia) throws BaseException {
		if (agendaHemoterapia.getId() != null) {
			this.executarAntesDeletar(agendaHemoterapia);
			this.getMbcAgendaHemoterapiaDAO().remover(agendaHemoterapia);
		}
	}

	/**
	 * @ORADB MBCT_AGH_BRD
	 * 
	 * 
	 * @throws BaseException
	 */
	private void executarAntesDeletar(final MbcAgendaHemoterapia agendaHemoterapia) throws BaseException {
		this.validarAgendaComControleEscalaCirurgicaDefinitiva(agendaHemoterapia.getId().getAgdSeq());
		this.incluirHistoricoAgendaHemoterapia(null, agendaHemoterapia, DominioOperacaoAgenda.E);
	}

	/*private void definirId(MbcAgendaHemoterapia agendaHemoterapia) {
		MbcAgendaHemoterapiaId id = new MbcAgendaHemoterapiaId();
		id.setAgdSeq(agendaHemoterapia.getId().getAgdSeq());
		id.setCsaCodigo(agendaHemoterapia.getId().getCsaCodigo());
		agendaHemoterapia.setId(id);
	}*/

	/**
	 * @ORADB MBCT_AGH_BRI
	 * 
	 * 
	 * @throws BaseException
	 */
	private void executarAntesInserir(final MbcAgendaHemoterapia agendaHemoterapia, final Integer agdSeq) throws BaseException {
		this.validarAgendaComControleEscalaCirurgicaDefinitiva(agdSeq);

		this.verificarIndicadores(agendaHemoterapia);

		//this.definirId(agendaHemoterapia);
		agendaHemoterapia.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		agendaHemoterapia.setCriadoEm(new Date());
	}

	/**
	 * @ORADB mbck_agh_rn.rn_aghp_ver_indicad
	 * 
	 * 
	 * @throws BaseException
	 */
	public void verificarIndicadores(final MbcAgendaHemoterapia agendaHemoterapia) throws ApplicationBusinessException {

		AbsComponenteSanguineo absComponenteSanguineo = getBancoDeSangueFacade().obterAbsComponenteSanguineoOriginal(agendaHemoterapia.getId().getCsaCodigo());
		if (agendaHemoterapia.getIndIrradiado() || agendaHemoterapia.getIndFiltrado() || agendaHemoterapia.getIndLavado()) {
			if (absComponenteSanguineo == null) {
				throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00865);
			}
			if (absComponenteSanguineo.getIndIrradiado().equals(Boolean.FALSE) && agendaHemoterapia.getIndIrradiado().equals(Boolean.TRUE)) {
				throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00866);
			}
			if (absComponenteSanguineo.getIndFiltrado().equals(Boolean.FALSE) && agendaHemoterapia.getIndFiltrado().equals(Boolean.TRUE)) {
				throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00867);
			}
			if (absComponenteSanguineo.getIndLavado().equals(Boolean.FALSE) && agendaHemoterapia.getIndLavado().equals(Boolean.TRUE)) {
				throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00868);
			}
		}
	}

	/**
	 * @ORADB mbck_agh_rn.rn_aghp_ver_escala
	 * 
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarAgendaComControleEscalaCirurgicaDefinitiva(final Integer agdSeq) throws ApplicationBusinessException {
		final MbcAgendas result = getMbcAgendasDAO().pesquisarAgendaComControleEscalaCirurgicaDefinitiva(agdSeq);
		if (result != null && result.getIndGeradoSistema() != null && !result.getIndGeradoSistema()) {
			throw new ApplicationBusinessException(MbcAgendaProcedimentoRNExceptionCode.MBC_00836);
		}
	}

	/**
	 * @throws BaseException
	 * @ORADB mbck_agh_rn.rn_aghp_inc_historic
	 * 
	 * 
	 * @throws BaseException
	 */
	public void incluirHistoricoAgendaHemoterapia(final MbcAgendaHemoterapia newAgendaHemoterapia, final MbcAgendaHemoterapia oldAgendaHemoterapia,
			final DominioOperacaoAgenda operacao) throws BaseException {
		final StringBuffer hist = new StringBuffer(144);
		switch (operacao) {
		case E:
			this.gerarHistoricoExclusao(oldAgendaHemoterapia, hist);
			if (hist.length() != 0) {
				this.getMbcAgendaHistoricoRN().inserir(oldAgendaHemoterapia.getMbcAgendas().getSeq(), oldAgendaHemoterapia.getMbcAgendas().getIndSituacao(),
						DominioOrigem.H, hist.toString(), operacao);
			}
			break;
		case A:
			this.gerarHistoricoAtualizacao(newAgendaHemoterapia, oldAgendaHemoterapia, hist);
			if (hist.length() != 0) {
				this.getMbcAgendaHistoricoRN().inserir(newAgendaHemoterapia.getMbcAgendas().getSeq(), newAgendaHemoterapia.getMbcAgendas().getIndSituacao(),
						DominioOrigem.H, hist.toString(), operacao);
			}
			break;
		default:
			break;
		}
	}

	private void gerarHistoricoExclusao(MbcAgendaHemoterapia oldAgendaHemoterapia, StringBuffer hist) {
		hist.append("componente sanguineo excluído: ").append(this.getMbcAgendaHemoterapiaDAO().obterOriginal(oldAgendaHemoterapia.getId()).getAbsComponenteSanguineo().getDescricao());
		if (oldAgendaHemoterapia.getIndIrradiado()) {
			hist.append(", irradiado");
		}
		if (oldAgendaHemoterapia.getIndFiltrado()) {
			hist.append(", filtrado");
		}
		if (oldAgendaHemoterapia.getIndLavado()) {
			hist.append(", lavado");
		}
		if (oldAgendaHemoterapia.getQtdeUnidade() == null) {
			// migrado diferente do AGH pois não faz sentido imprimir um
			// valor null e ml.
			// v_descricao := v_descricao || ', ' || p_old_qtde_unidade || '
			// ml';
			hist.append(", ").append(oldAgendaHemoterapia.getQtdeMl()).append(" ml");
		} else {
			hist.append(", ").append(oldAgendaHemoterapia.getQtdeUnidade());
			if (oldAgendaHemoterapia.getQtdeUnidade() == 1) {
				hist.append(" unidade");
			} else {
				hist.append(" unidades");
			}
		}
	}

	private void gerarHistoricoAtualizacao(MbcAgendaHemoterapia newAgendaHemoterapia, MbcAgendaHemoterapia oldAgendaHemoterapia, StringBuffer hist) {
		if (CoreUtil.modificados(newAgendaHemoterapia.getAbsComponenteSanguineo(), oldAgendaHemoterapia.getAbsComponenteSanguineo())) {
			hist.append("componente sanguineo alterado de ").append(oldAgendaHemoterapia.getAbsComponenteSanguineo().getDescricao()).append(" para ")
					.append(newAgendaHemoterapia.getAbsComponenteSanguineo().getDescricao()).append(", ");
		} else {
			hist.append("componente sanguineo ").append(newAgendaHemoterapia.getAbsComponenteSanguineo().getDescricao());
		}
		if (CoreUtil.modificados(newAgendaHemoterapia.getIndIrradiado(), oldAgendaHemoterapia.getIndIrradiado())) {
			if (oldAgendaHemoterapia.getIndIrradiado()) {
				hist.append(", alterado para marcar irradiado ");
			} else {
				hist.append(", alterado para desmarcar irradiado ");
			}
		}
		if (CoreUtil.modificados(newAgendaHemoterapia.getIndFiltrado(), oldAgendaHemoterapia.getIndFiltrado())) {
			if (oldAgendaHemoterapia.getIndFiltrado()) {
				hist.append(", alterado para marcar filtrado ");
			} else {
				hist.append(", alterado para desmarcar filtrado ");
			}
		}
		if (CoreUtil.modificados(newAgendaHemoterapia.getIndLavado(), oldAgendaHemoterapia.getIndLavado())) {
			if (oldAgendaHemoterapia.getIndLavado()) {
				hist.append(", alterado para marcar lavado ");
			} else {
				hist.append(", alterado para desmarcar lavado ");
			}
		}
		if (CoreUtil.modificados(newAgendaHemoterapia.getQtdeUnidade(), oldAgendaHemoterapia.getQtdeUnidade())
				|| CoreUtil.modificados(newAgendaHemoterapia.getQtdeMl(), oldAgendaHemoterapia.getQtdeMl())) {
			hist.append(", quantidade alterada de ");
			if (oldAgendaHemoterapia.getQtdeUnidade() == null) {
				// migrado diferente do AGH pois não faz sentido imprimir um
				// valor null e ml.
				// v_descricao := v_descricao || ', ' || p_old_qtde_unidade
				// || '
				// ml';
				hist.append(", ").append(oldAgendaHemoterapia.getQtdeMl()).append(" ml");
			} else {
				hist.append(", ").append(oldAgendaHemoterapia.getQtdeUnidade());
				if (oldAgendaHemoterapia.getQtdeUnidade() == 1) {
					hist.append(" unidade");
				} else {
					hist.append(" unidades");
				}
			}
		}
		adicionarHistoricoQuantidadeAdicional(newAgendaHemoterapia,
				oldAgendaHemoterapia, hist);
	}

	private void adicionarHistoricoQuantidadeAdicional(
			MbcAgendaHemoterapia newAgendaHemoterapia,
			MbcAgendaHemoterapia oldAgendaHemoterapia, StringBuffer hist) {
		if (CoreUtil.modificados(newAgendaHemoterapia.getQtdeUnidadeAdic(), oldAgendaHemoterapia.getQtdeUnidadeAdic())) {
			hist.append(", quantidade adicional alterada de ").append(
					oldAgendaHemoterapia.getQtdeUnidadeAdic() != null ? oldAgendaHemoterapia.getQtdeUnidadeAdic() : "vazio").append(" para ")
						.append(newAgendaHemoterapia.getQtdeUnidadeAdic() != null ? newAgendaHemoterapia.getQtdeUnidadeAdic() : "vazio");
		}
	}

	/**
	 * @ORADB MBCT_AGH_BRU
	 * 
	 * 
	 * @throws BaseException
	 */
	private void executarAntesAtualizar(final MbcAgendaHemoterapia newAgendaHemoterapia, final MbcAgendaHemoterapia oldAgendaHemoterapia) throws BaseException {
		this.validarAgendaComControleEscalaCirurgicaDefinitiva(newAgendaHemoterapia.getId().getAgdSeq());
		this.verificarIndicadores(newAgendaHemoterapia);
		if (CoreUtil.modificados(newAgendaHemoterapia.getId().getCsaCodigo(), oldAgendaHemoterapia.getId().getCsaCodigo())
				|| CoreUtil.modificados(newAgendaHemoterapia.getQtdeMl(), oldAgendaHemoterapia.getQtdeMl())
				|| CoreUtil.modificados(newAgendaHemoterapia.getIndFiltrado(), oldAgendaHemoterapia.getIndFiltrado())
				|| CoreUtil.modificados(newAgendaHemoterapia.getIndIrradiado(), oldAgendaHemoterapia.getIndIrradiado())
				|| CoreUtil.modificados(newAgendaHemoterapia.getIndLavado(), oldAgendaHemoterapia.getIndLavado())
				|| CoreUtil.modificados(newAgendaHemoterapia.getQtdeUnidade(), oldAgendaHemoterapia.getQtdeUnidade())
				|| CoreUtil.modificados(newAgendaHemoterapia.getQtdeUnidadeAdic(), oldAgendaHemoterapia.getQtdeUnidadeAdic())) {
			this.incluirHistoricoAgendaHemoterapia(newAgendaHemoterapia, oldAgendaHemoterapia, DominioOperacaoAgenda.A);
		}
	}

	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO() {
		return mbcAgendaHemoterapiaDAO;
	}

	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}

	protected MbcAgendaHistoricoDAO getMbcAgendaHistoricoDAO() {
		return mbcAgendaHistoricoDAO;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return iBancoDeSangueFacade;
	}

	protected MbcAgendaHistoricoRN getMbcAgendaHistoricoRN() {
		return mbcAgendaHistoricoRN;
	}
}