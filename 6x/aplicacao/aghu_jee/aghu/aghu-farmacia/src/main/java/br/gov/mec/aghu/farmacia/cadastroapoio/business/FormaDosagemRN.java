package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.farmacia.business.exception.FarmaciaExceptionCode;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemDAO;
import br.gov.mec.aghu.farmacia.dao.AfaFormaDosagemJnDAO;
import br.gov.mec.aghu.farmacia.dao.AfaMedicamentoDAO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaFormaDosagemJn;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Triggers de:<br/>
 * @ORADB: <code>AFA_FORMA_DOSAGENS</code>
 * @author gandriotti
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FormaDosagemRN
		extends AbstractAGHUCrudRn<AfaFormaDosagem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8568616205664179385L;

	private static final Log LOG = LogFactory.getLog(FormaDosagemRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private AfaFormaDosagemJnDAO afaFormaDosagemJnDAO;
	
	@Inject
	private AfaFormaDosagemDAO afaFormaDosagemDAO;
	
	@Inject
	private AfaMedicamentoDAO afaMedicamentoDAO;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	
	protected AfaFormaDosagemJnDAO getEntidadeJournalDao() {

		return afaFormaDosagemJnDAO;
	}

	protected AfaFormaDosagemDAO getEntidadeDao() {

		return afaFormaDosagemDAO;
	}

	protected AfaMedicamentoDAO getMedicamentoDao() {

		return afaMedicamentoDAO;
	}

	protected AfaFormaDosagemJn getNewJournal(final AfaFormaDosagem entidade, final DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		AfaFormaDosagemJn jn = null;

		jn = BaseJournalFactory.getBaseJournal(operacao, AfaFormaDosagemJn.class, servidorLogado.getUsuario());
		jn.setSeq(entidade.getSeq());
		jn.setMedMatCodigo(entidade.getAfaMedicamentos().getMatCodigo());
		jn.setUmmSeq((entidade.getUnidadeMedidaMedicas() != null ? entidade.getUnidadeMedidaMedicas().getSeq() : null));
		jn.setFatorConversaoUp(entidade.getFatorConversaoUp());
		jn.setIndUsualPrescricao(entidade.getIndUsualPrescricao());
		jn.setIndUsualNpt(entidade.getIndUsualNpt());
		jn.setCriadoEm(entidade.getCriadoEm());
		jn.setIndSituacao(entidade.getIndSituacao());

		return jn;
	}

	/**
	 * <p>
	 * Ajusta a referencia ao usuario atualmente logado e a data atual.<br/>
	 * </p>
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @see #getDataCriacao()
	 */
	protected void setServidorData(final AfaFormaDosagem entidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (entidade == null) {
			throw new IllegalArgumentException();
		}

		if (servidorLogado == null) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
		
		entidade.setServidor(servidorLogado);
		//entidade.setCriadoEm(this.getDataCriacao());
	}

	/**
	 * <p>
	 * Ajusta a referencia ao usuario atualmente logado e a data atual.<br/>
	 * </p>
	 * 
	 * @param entidade
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 * @see #getDataCriacao()
	 */
	protected void setServidorData(final AfaFormaDosagemJn entidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (entidade == null) {
			throw new IllegalArgumentException();
		}
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00169);
		}
		
		entidade.setSerMatricula(servidorLogado.getId().getMatricula());
		entidade.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		//entidade.setCriadoEm(this.getDataCriacao());
	}

	/**
	 * <p>
	 * Apenas insere a entrada adequada no Journal.
	 * </p>
	 * 
	 * @param entidade
	 * @param operacao
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	
	protected boolean inserirEntradaJournal(final AfaFormaDosagem entidade, final DominioOperacoesJournal operacao) throws ApplicationBusinessException {

		boolean result = false;
		AfaFormaDosagemJn journal = null;
		AfaFormaDosagemJnDAO dao = null;

		dao = this.getEntidadeJournalDao();
		journal = this.getNewJournal(entidade, operacao);
		if (dao != null) {
			this.setServidorData(journal);
			dao.persistir(journal);
			dao.flush();
			result = true;
		}

		return result;
	}

	/**
	 * @param original
	 * @param modificada
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected boolean isAtualizacaoPermitida(final AfaFormaDosagem original, final AfaFormaDosagem modificada) throws ApplicationBusinessException {

		boolean result = true;

		result = modificada.getAfaMedicamentos().getMatCodigo().equals(original.getAfaMedicamentos().getMatCodigo());
		result = (result && ((modificada.getUnidadeMedidaMedicas() == null) && (original.getUnidadeMedidaMedicas() == null)))
				|| (((modificada.getUnidadeMedidaMedicas() != null) && (original.getUnidadeMedidaMedicas() != null) && modificada.getUnidadeMedidaMedicas()
						.getSeq()
						.equals(original.getUnidadeMedidaMedicas().getSeq())));
		result = result && modificada.getSeq().equals(original.getSeq());
		result = result
				&& modificada.getServidor().getMatriculaVinculo().equals(original.getServidor().getMatriculaVinculo());
		result = result
				&& modificada.getServidor()
						.getVinculo()
						.getCodigo()
						.equals(original.getServidor().getVinculo().getCodigo());
		result = result && modificada.getCriadoEm().equals(original.getCriadoEm());

		return result;
	}

	/**
	 * @ORADB: <code>AFAT_FDS_BRU</code>
	 * 
	 * @see #setServidorData(AfaFormaDosagem)
	 * @see FarmaciaExceptionCode#AFA_00208
	 */
	@Override
	@SuppressWarnings("ucd")
	public boolean bruPreAtualizacaoRow(AfaFormaDosagem original,
			AfaFormaDosagem modificada, String nomeMicrocomputador,
			Date dataFimVinculoServidor)
			throws BaseException {
		boolean result = false;

		//Constraint afa_fds_ck4
		if (modificada.getFatorConversaoUp().compareTo(BigDecimal.ZERO) == 0) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_FDS_CK4);
		}
		
		if (!this.isAtualizacaoPermitida(original, modificada)) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00208);
		}
		result = this.obrigaRegrasFormaDosagem(original, modificada, DominioOperacoesJournal.UPD);
		if (result) {
			this.setServidorData(modificada);
		}

		return result;
	}

	/**
	 * @ORADB: <code>AFAT_FDS_ARU</code>
	 * <p>
	 * Se entidade nova entidade for diferente da original, entao inserir
	 * entrada correspondente no Journal
	 * </p>
	 */
	@Override
	@SuppressWarnings("ucd")
	public boolean aruPosAtualizacaoRow(AfaFormaDosagem original,
			AfaFormaDosagem modificada, String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		boolean result = true;

		if (!modificada.equals(original)) {
			result = this.inserirEntradaJournal(original,
					DominioOperacoesJournal.UPD);
		}

		return result;
	}

	/**
	 * <p>
	 * @ORADB: <code>afak_fds_rn.RN_FDSP_VER_IND_USUL</code><br/>
	 * Verifica se eh necessario verificar a exclusivade para formas usuais: NPT
	 * e Prescricao
	 * </p>
	 * 
	 * @param entidade
	 * @return
	 */
	protected boolean afetaUsual(final AfaFormaDosagem entidade) {

		boolean result = false;

		result = entidade.getIndUsualNpt().booleanValue() || entidade.getIndUsualPrescricao().booleanValue();

		return result;
	}

	/**
	 * <p>
	 * Verifica se existe apenas uma entidade {@link AfaFormaDosagem} com: <br/>
	 * {@link AfaFormaDosagem#getIndUsualNpt()}<br/>
	 * {@link AfaFormaDosagem#getIndUsualPrescricao()}<br/>
	 * Ativos por medicamento.
	 * <p/>
	 * 
	 * @param entidade
	 *            entidade a ser verificada
	 * @param ignorarEstaSeq
	 *            a ser fornecido em caso de modificacao em uma entidade. Aceita
	 *            <code>null</code>
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link FarmaciaExceptionCode#AFA_00210}
	 * @see {@link FarmaciaExceptionCode#AFA_00211}
	 */
	protected boolean verificaSingularidadeUsual(final AfaFormaDosagem entidade, final Integer ignorarEstaSeq) throws ApplicationBusinessException {

		boolean result = false;
		AfaFormaDosagemDAO dao = null;
		List<AfaFormaDosagem> fdMed = null;
		AfaMedicamento medicamento = null;
		boolean isNpt = false;
		boolean isPresc = false;

		dao = this.getEntidadeDao();
		medicamento = entidade.getAfaMedicamentos();
		isNpt = entidade.getIndUsualNpt().booleanValue();
		isPresc = entidade.getIndUsualPrescricao().booleanValue();
		fdMed = dao.listaFormaDosagemMedicamento(medicamento.getMatCodigo());
		for (AfaFormaDosagem e : fdMed) {
			// nao eh a mesma entidade a ser modificada
			if (!e.getSeq().equals(ignorarEstaSeq)) {
				// verifica exclusividade NPT
				if (isNpt && e.getIndUsualNpt().booleanValue()) {
					throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00210);
				}
				// verifica exclusividade Prescricao
				if (isPresc && e.getIndUsualPrescricao().booleanValue()) {
					throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00211);
				}
			}
		}
		result = true;

		return result;
	}

	/**
	 * @ORADB: <code>AFAP_ENFORCE_FDS_RULES('UPDATE')</code><br/>
	 * @ORADB: <code>afak_fds_rn.RN_FDSP_VER_IND_USUL</code><br/>
	 * 
	 * @param original
	 *            aceita <code>null</code> para o caso insercao
	 * @param modificada
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link #afetaUsual(AfaFormaDosagem)}
	 * @see #verificaSingularidadeUsual(AfaFormaDosagem, Integer)
	 */
	protected boolean obrigaSingularidadeUsual(final AfaFormaDosagem original, final AfaFormaDosagem modificada) throws ApplicationBusinessException {

		boolean result = false;

		result = !this.afetaUsual(modificada);
		// afak_fds_rn.rn_fdsp_ver_ind_usul
		if (!result) {
			result = this.verificaSingularidadeUsual(modificada, (original != null ? original.getSeq() : null));
		}

		return result;
	}

	/**
	 * @ORADB: <code>AFAP_ENFORCE_FDS_RULES</code>
	 * 
	 * @param original
	 *            ignorado em caso de {@link DominioOperacoesJournal#INS}
	 * @param modificada
	 *            novo estado da entidade
	 * @param operacao
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected boolean obrigaRegrasFormaDosagem(final AfaFormaDosagem original,
			final AfaFormaDosagem modificada,
			final DominioOperacoesJournal operacao) throws ApplicationBusinessException {

		boolean result = false;

		switch (operacao) {
		case DEL:
			break;
		case INS:
			result = this.obrigaSingularidadeUsual(null, modificada);
			break;
		case UPD:
			result = this.obrigaSingularidadeUsual(original, modificada);
			break;
		default:
			throw new IllegalStateException();
		}

		return result;
	}

	/**
	 * @ORADB: <code>PROCEDURE RN_FDSP_VER_MDTO</code>
	 * 
	 * @param medicamento
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link FarmaciaExceptionCode#AFA_00204}
	 */
	protected boolean verificaSituacaoMedicamento(final AfaMedicamento medicamento) throws ApplicationBusinessException {

		boolean result = false;
		AfaMedicamentoDAO dao = null;
		AfaMedicamento batimento = null;

		// condicao necessaria para se incluir um novo medicamento, via materiais.
		if (medicamento != null) {
			if (!DominioSituacaoMedicamento.A.equals(medicamento.getIndSituacao())) {
				result = true;
			} else {
				dao = this.getMedicamentoDao();
				batimento = dao.obterPorChavePrimaria(medicamento.getMatCodigo());
				result = batimento != null;
			}
		}

		return result;

	}

	protected MpmUnidadeMedidaMedica getUMM(final MpmUnidadeMedidaMedica umm) {

		MpmUnidadeMedidaMedica result = null;

		result = getPrescricaoMedicaFacade().obterUnidadeMedicaPorId(umm.getSeq());

		return result;
	}
	
	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}

	/**
	 * <p>
	 * @ORADB: <code>afak_fds_rn.rn_fdsp_ver_uni_mdda</code><br/>
	 * @ORADB: <code>afak_rn.rn_afac_ver_umm_ativ</code><br/>
	 * </p>
	 * 
	 * @return
	 * @throws ApplicationBusinessException
	 * @see {@link FarmaciaExceptionCode#AFA_00207}
	 * @see {@link FarmaciaExceptionCode#AFA_00300}
	 */
	protected boolean verificaUnidadeMedida(final AfaFormaDosagem entidade) throws ApplicationBusinessException {

		boolean result = false;
		MpmUnidadeMedidaMedica umm = null;
		MpmUnidadeMedidaMedica batimento = null;

		// RN_FDSP_VER_UNI_MDDA
		umm = entidade.getUnidadeMedidaMedicas();
		if (umm != null) {
			if (umm.getSeq() == null) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00300);
			}
			batimento = this.getUMM(umm);
			if (batimento == null) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00300);
			}
			// afak_fds_rn.rn_fdsp_ver_uni_mdda
			if (!batimento.getIndSituacao().isAtivo()) {
				throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00207);
			}
		}
		if (!this.isUMMDisponivel(entidade.getAfaMedicamentos(), umm)) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00044);
		}
		result = true;

		return result;
	}

	/**
	 * @ORADB: <code>AFAT_FDS_BRI</code>
	 * 
	 * @see #verificaSituacaoMedicamento(AfaMedicamento)
	 * @see #verificaUnidadeMedida(AfaFormaDosagem)
	 * @see #setServidorData(AfaFormaDosagem)
	 */
	@Override
	@SuppressWarnings("ucd")
	public boolean briPreInsercaoRow(AfaFormaDosagem entidade,
			String nomeMicrocomputador,
			Date dataFimVinculoServidor) throws BaseException {
		boolean result = false;

		//Constraint afa_fds_ck4
		if (entidade.getFatorConversaoUp().compareTo(BigDecimal.ZERO) == 0) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_FDS_CK4);
		}

		entidade.setCriadoEm(new Date());
		result = this.verificaSituacaoMedicamento(entidade.getAfaMedicamentos());
		if (result) {
			if (this.afetaUsual(entidade)) {
				result = this.obrigaRegrasFormaDosagem(null, entidade, DominioOperacoesJournal.INS);
			}
			result = result && this.verificaUnidadeMedida(entidade);
		}
		this.setServidorData(entidade);

		return result;
	}

	protected BigDecimal getAghParamVlrNum() throws ApplicationBusinessException {

		BigDecimal result = null;
		AghParametros params = null;

		params = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_AFA);
		if (params != null) {
			result = params.getVlrNumerico();
		}

		return result;
	}

	/**
	 * @param criadoEm
	 * @see FarmaciaExceptionCode#AFA_00173
	 * @see AghuParametrosEnum#P_DIAS_PERM_DEL_AFA
	 */
	protected boolean verificaDentroPeriodoRemocaoValido(final Date criadoEm) throws ApplicationBusinessException,
			ApplicationBusinessException {

		boolean result = false;
		BigDecimal paramNum = null;
		float aghDiasPerm = 0.0f;
		float diff = 0.0f;

		paramNum = this.getAghParamVlrNum();
		// RN_AFAP_VER_DEL
		if (paramNum == null) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00173);
		}
		aghDiasPerm = paramNum.floatValue();
		diff = CoreUtil.diferencaEntreDatasEmDias(this.getDataCriacao(), criadoEm).floatValue();
		result = diff <= aghDiasPerm;

		return result;
	}

	/**
	 * @ORADB: <code>AFAT_FDS_BRD</code><br/>
	 * @ORADB: <code>AFAK_FDS_RN.RN_FDSP_VER_DELECAO</code><br/>
	 * @ORADB: <code>AFAK_RN.RN_AFAP_VER_DEL</code><br/>
	 * 
	 * @see FarmaciaExceptionCode#AFA_00172
	 * @see #verificaDentroPeriodoRemocaoValido(Date)
	 */
	@Override
	@SuppressWarnings("ucd")
	public boolean brdPreRemocaoRow(AfaFormaDosagem entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {

		boolean result = false;
		Date criadoEm = null;
		String which = null;

		// RN_AFAP_VER_DEL
		criadoEm = entidade.getCriadoEm();
		result = this.verificaDentroPeriodoRemocaoValido(criadoEm);
		if (!result) {
			throw new ApplicationBusinessException(FarmaciaExceptionCode.AFA_00172);
		}
		//Carrega entidade com informações necessárias para exclusão
		result = getEntidadeDao().obterAfaFormaDosagemWithJoin(entidade.getSeq()).getItemPrescricaoMedicamentos().isEmpty();
		if (!result) {
			which = entidade.getFatorConversaoUp().toPlainString() + " "
					+ ((entidade.getUnidadeMedidaMedicas() != null 
						&& entidade.getUnidadeMedidaMedicas().getDescricao() != null)
						? entidade.getUnidadeMedidaMedicas().getDescricao() 
						: "");
			throw new ApplicationBusinessException(
					ApplicationBusinessExceptionCode.OFG_00005,
					which,
					MpmItemPrescricaoMdto.class.getSimpleName());
		}

		return result;
	}

	/**
	 * @ORADB: <code>AFAT_FDS_ARD</code>
	 * <p>
	 * Apenas insere a entrada adequada no Journal.
	 * </p>
	 */
	@Override
	@SuppressWarnings("ucd")
	public boolean ardPosRemocaoRow(AfaFormaDosagem entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) throws BaseException {
		return this.inserirEntradaJournal(entidade,
				DominioOperacoesJournal.DEL);
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	public boolean isUMMDisponivel(AfaMedicamento medicamento, MpmUnidadeMedidaMedica umm) {
		
		boolean result = false;
		List<AfaFormaDosagem> fdUsadas = null;
		
		fdUsadas = getEntidadeDao().listaFormaDosagemMedicamento(medicamento.getMatCodigo());
		if (fdUsadas.isEmpty()) {
			result = true;
		} else {
			for (AfaFormaDosagem fd : fdUsadas) {
				if (umm == null) { // caso especial da apresentacao
					result = fd.getUnidadeMedidaMedicas() != null;
				} else if (fd.getUnidadeMedidaMedicas() == null) { // umm != null : necessariamente 
					result = true;
				} else {
					result = !fd.getUnidadeMedidaMedicas().getSeq().equals(umm.getSeq());
				}
				if (!result) {
					break;
				}
			}
		}
		
		return result;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
