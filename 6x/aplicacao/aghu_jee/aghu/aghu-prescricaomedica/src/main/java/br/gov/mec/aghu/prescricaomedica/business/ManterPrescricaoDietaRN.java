package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MpmItemPrescricaoDieta;
import br.gov.mec.aghu.model.MpmPrescricaoDieta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoDietaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoDietaDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterPrescricaoDietaRN extends BaseBusiness {

	private static final String PARAMETROS_OBRIGATORIOS = "Parâmetros obrigatórios";

	private static final String PARAMETRO_OBRIGATORIO = "Parâmetro obrigatório";

	@EJB
	private PrescricaoMedicaRN prescricaoMedicaRN;
	
	private static final Log LOG = LogFactory.getLog(ManterPrescricaoDietaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmItemPrescricaoDietaDAO mpmItemPrescricaoDietaDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private MpmPrescricaoDietaDAO mpmPrescricaoDietaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3010724338860177206L;

	private enum ManterPrescricaoDietaRNExceptionCode implements
			BusinessExceptionCode {
		ERRO_MENSAGEM_EXISTE_DIETA_EM_VIGOR, ERRO_MENSAGEM_EXISTE_DIETA_VALIDA, MPM_01280, MPM_00747, MPM_01281, MPM_01310, //
		MPM_03746;
	}

	/**
	 * ORADB: Procedure MPMP_ENFORCE_PDT_RULES
	 * 
	 * @param prescricaoDieta
	 * @param insercao
	 * @throws ApplicationBusinessException
	 * 
	 *             Obs: Este método somente será utilizado quando for realizado
	 *             alteração de prescrição
	 * 
	 */
	private void verificarEnforceAlteracaoPrescricaoDieta(
			MpmPrescricaoDieta prescricaoDieta,
			MpmPrescricaoDieta prescricaoDietaOld) throws ApplicationBusinessException {

		if (prescricaoDieta == null || prescricaoDietaOld == null) {
			throw new IllegalArgumentException(PARAMETROS_OBRIGATORIOS);
		}

		if (prescricaoDieta.getIndItemRecomendadoAlta()) {
			this.verificarRecomendadoAlta(prescricaoDieta);
		}

		if (!CoreUtil.igual(prescricaoDietaOld.getDthrInicio(), prescricaoDieta
				.getDthrInicio())
				|| !CoreUtil.igual(prescricaoDietaOld.getDthrFim(),
						prescricaoDieta.getDthrFim())
				|| !CoreUtil.igual(prescricaoDietaOld.getIndPendente(),
						prescricaoDieta.getIndPendente())) {
			this.verificarDatasInicioFim(prescricaoDieta);
		}

		if (DominioIndPendenteItemPrescricao.N.equals(prescricaoDieta
				.getIndPendente())) {
			this.verificarDietaValidaMesmaPrescricao(prescricaoDieta);
		}

	}

	/**
	 * ORADB: Procedure MPMP_ENFORCE_PDT_RULES
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 * 
	 *             Obs: Este método somente será utilizado quando for realizado
	 *             alteração de prescrição
	 * 
	 */
	private void enforceRules(MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETROS_OBRIGATORIOS);
		}

		this.verificarDatasInicioFim(prescricaoDieta);
		this.verificarDietaValidaMesmaPrescricao(prescricaoDieta);
	}

	/**
	 * Verificar se existe mais de um item recomendado para alta para um mesmo
	 * atendimento
	 * 
	 * ORADB: Procedure MPMK_PDT_RN.RN_PDTP_VER_IT_RECOM
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	private void verificarRecomendadoAlta(MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETROS_OBRIGATORIOS);
		}

		if (this.getMpmPrescricaoDietaDAO()
				.existePrescricaoDietaRecomendadaAltaPorAtendimento(
						prescricaoDieta)) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaRNExceptionCode.MPM_00747);
		}
	}

	/**
	 * ORADB: PROCEDURE MPMK_PDT_RN.RN_PDTP_VER_PDT_PME
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	private void verificarDietaValidaMesmaPrescricao(
			MpmPrescricaoDieta prescricaoDieta) throws ApplicationBusinessException {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		List<MpmPrescricaoDieta> lista = this.getMpmPrescricaoDietaDAO()
				.buscarPrescricaoDietasPeloAtendimento(prescricaoDieta);

		if (lista == null) {
			return;
		}

		for (MpmPrescricaoDieta listaPrescricaoDieta : lista) {
			if (this.verificarSobreposicaoDatas(
					prescricaoDieta.getDthrInicio(), prescricaoDieta
							.getDthrFim(),
					listaPrescricaoDieta.getDthrInicio(), listaPrescricaoDieta
							.getDthrFim())) {
				throw new ApplicationBusinessException(
						ManterPrescricaoDietaRNExceptionCode.ERRO_MENSAGEM_EXISTE_DIETA_VALIDA);
			}
		}
	}

	/**
	 * ORADB: Procedure MPMK_PDT_RN.RN_PDTP_VER_VIG_DATA
	 * 
	 * @param itemDieta
	 * @param itemDietaOld
	 * @throws ApplicationBusinessException
	 */
	private void verificarDatasInicioFim(MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		/*
		 * MpmPrescricaoDieta prescricaoDietaOld =
		 * this.getMpmPrescricaoDietaDAO()
		 * .obterPrescricaoDietaAnterior(prescricaoDieta);
		 * 
		 * if(prescricaoDietaOld == null){ return; }
		 */

		if (prescricaoDieta.getMpmPrescricaoDietas() == null
				|| DominioIndPendenteItemPrescricao.P.equals(prescricaoDieta
						.getMpmPrescricaoDietas().getIndPendente())
				|| DominioIndPendenteItemPrescricao.B.equals(prescricaoDieta
						.getMpmPrescricaoDietas().getIndPendente())) {
			return;
		}

		if (this.verificarSobreposicaoDatas(prescricaoDieta.getDthrInicio(),
				prescricaoDieta.getDthrFim(), prescricaoDieta
						.getMpmPrescricaoDietas().getDthrInicio(),
				prescricaoDieta.getMpmPrescricaoDietas().getDthrFim())) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaRNExceptionCode.ERRO_MENSAGEM_EXISTE_DIETA_EM_VIGOR);
		}

	}

	/**
	 * ORADB Procedure MPMC_VERIFICA_DATAS
	 * 
	 * @param dtInicio
	 * @param dtFim
	 * @param dtInicioOld
	 * @param dtFimOld
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean verificarSobreposicaoDatas(Date dtInicio, Date dtFim,
			Date dtInicioOld, Date dtFimOld) {

		if (dtInicio == null || dtInicioOld == null) {
			throw new IllegalArgumentException(PARAMETROS_OBRIGATORIOS);
		}

		/*
		 * Existe uma dthr_inicio MENOR que a nova data de inicio ou a nova de
		 * fim e que a dthr_fim existente é nula, ou seja, ainda está em vigor
		 */
		if ((dtInicioOld.before(dtInicio) || dtInicioOld.before(dtFim))
				&& dtFimOld == null) {
			return true;
		}

		if (dtInicioOld != null && dtFimOld != null && dtInicio != null
				&& dtFim != null) {

			/*
			 * Existem as datas de início e fim e as novas datas também(início e
			 * fim) e na tabela existe uma dthr_inicio entre as datas novas de
			 * início e fim ou na tabela existe uma dthr_fim entre as datas
			 * novas de início e fim
			 */
			if (!dtInicioOld.equals(dtFimOld)
					&& ((dtInicioOld.after(dtInicio) || dtInicioOld
							.equals(dtInicio)) && dtInicioOld.before(dtFim))
					|| (dtFimOld.after(dtInicio) && (dtFimOld.before(dtFim) || dtFimOld
							.equals(dtFim)))) {
				return true;
			}

			/*
			 * Existem as datas de início e fim e as novas datas também(início e
			 * fim) e na tabela existe uma dthr_fim MENOR IGUAL que a nova data
			 * de início e MAIOR que a nova data de fim
			 */
			if ((dtFimOld.before(dtInicio) || dtFimOld.equals(dtInicio))
					&& dtFimOld.after(dtFim)) {
				return true;
			}
		}

		/*
		 * A nova data de fim é nula e existe na tabela uma dthr_inicio MENOR
		 * IGUAL a nova data de início e uma dthr_fim MAIOR IGUAL a nova data de
		 * início
		 */
		if (dtFim == null
				&& dtFimOld != null
				&& ((dtInicioOld.before(dtInicio) || dtInicioOld
						.equals(dtInicio)) && dtFimOld.after(dtInicio))) {
			return true;
		}

		/* A nova data de fim é nula e existe na tabela uma dthr_fim também nula */
		if (dtFim == null && dtFimOld == null) {
			return true;
		}

		return false;
	}

	/**
	 * ORADB: Trigger MPMT_PDT_BRI
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	private void beforeRowInsert(MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		// atualiza as informações do servidor e data de criação da prescrição
		prescricaoDieta.setRapServidores(servidorLogado);
		// prescricaoDieta.setCriadoEm(new Date());
		if (prescricaoDieta.getIndItemRecTransferencia() == null) {
			prescricaoDieta.setIndItemRecTransferencia(false);
		}

		this.getPrescricaoMedicaRN().verificaAtendimento(
				prescricaoDieta.getDthrInicio(), prescricaoDieta.getDthrFim(),
				prescricaoDieta.getPrescricaoMedica().getId().getAtdSeq(),
				null, null, null);

		this.getPrescricaoMedicaRN().verificaPrescricaoMedica(
				prescricaoDieta.getPrescricaoMedica().getId().getAtdSeq(),
				prescricaoDieta.getDthrInicio(), prescricaoDieta.getDthrFim(),
				prescricaoDieta.getCriadoEm(),
				prescricaoDieta.getIndPendente(), "I", nomeMicrocomputador, dataFimVinculoServidor);
	}

	/**
	 * ORADB: Trigger MPMT_IPD_BRI
	 * 
	 * @param itemPrescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	private void beforeRowInsert(MpmItemPrescricaoDieta itemPrescricaoDieta)
			throws BaseException {

		if (itemPrescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		if (itemPrescricaoDieta.getTipoFreqAprazamento() != null) {
			this.getPrescricaoMedicaRN().validaAprazamento(
					itemPrescricaoDieta.getTipoFreqAprazamento().getSeq(),
					itemPrescricaoDieta.getFrequencia());
		}

		/* Só incluir um item de dieta se ind_pendete = 'P' ou 'B' ou 'D' */
		this.verificarSituacaoPrescricao(itemPrescricaoDieta
				.getPrescricaoDieta());

	}

	/**
	 * ORADB: Trigger MPMT_IPD_BRU
	 * 
	 * @param itemPrescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	private void beforeRowUpdate(MpmItemPrescricaoDieta itemPrescricaoDieta)
			throws ApplicationBusinessException {

		if (itemPrescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		MpmItemPrescricaoDietaDAO dao = this.getMpmItemPrescricaoDietaDAO();

		// obtem os valores do banco de dados através de projection
		MpmItemPrescricaoDieta old = dao.obterOld(itemPrescricaoDieta);

		if (itemPrescricaoDieta.getTipoFreqAprazamento() != null
				&& (!CoreUtil
						.igual(itemPrescricaoDieta.getTipoFreqAprazamento(),
								old.getTipoFreqAprazamento()) || !CoreUtil
						.igual(itemPrescricaoDieta.getFrequencia(), old
								.getFrequencia()))) {
			this.getPrescricaoMedicaRN().validaAprazamento(
					itemPrescricaoDieta.getTipoFreqAprazamento().getSeq(),
					itemPrescricaoDieta.getFrequencia());
		}

	}

	/**
	 * Verifica a situação da pendência da prescrição de dieta.<br>
	 * 
	 * ORADB: Procedure RN_IPDP_VER_INCLUSAO
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 *             se indicador de pendência for diferente de pendente, modelo
	 *             basico e desdobramento.
	 */
	private void verificarSituacaoPrescricao(MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		if (!DominioIndPendenteItemPrescricao.P.equals(prescricaoDieta
				.getIndPendente())
				&& !DominioIndPendenteItemPrescricao.B.equals(prescricaoDieta
						.getIndPendente())
				&& !DominioIndPendenteItemPrescricao.D.equals(prescricaoDieta
						.getIndPendente())) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaRNExceptionCode.MPM_01280);
		}
	}

	/**
	 * ORADB: Trigger MPMT_PDT_BRU
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	public void beforeRowUpdate(MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETROS_OBRIGATORIOS);
		}

		MpmPrescricaoDietaDAO dao = this.getMpmPrescricaoDietaDAO();
		
		// obtem os valores do banco de dados através de projection
		MpmPrescricaoDieta prescricaoDietaOld = dao.obterOld(prescricaoDieta);

		if (!CoreUtil.igual(prescricaoDietaOld.getDthrFim(), prescricaoDieta
				.getDthrFim())
				&& prescricaoDieta.getDthrFim() != null) {
			if (!DominioIndPendenteItemPrescricao.P.equals(prescricaoDieta
					.getIndPendente())
					|| !DominioIndPendenteItemPrescricao.P
							.equals(prescricaoDietaOld.getIndPendente())) {
				prescricaoDieta.setAlteradoEm(Calendar.getInstance().getTime());
			}
		}

		if (prescricaoDieta.getAlteradoEm() != null) {
			this.getPrescricaoMedicaRN().verificaAtendimento(
					prescricaoDieta.getAlteradoEm(), null,
					prescricaoDieta.getPrescricaoMedica().getId().getAtdSeq(),
					null, null, null);
		}

		if (prescricaoDieta.getDthrFim() != null
				&& !CoreUtil.igual(prescricaoDieta.getDthrFim(),
						prescricaoDietaOld.getDthrFim())) {

			this.getPrescricaoMedicaRN().verificaPrescricaoMedicaUpdate(
					prescricaoDieta.getPrescricaoMedica().getId().getAtdSeq(),
					prescricaoDieta.getDthrInicio(),
					prescricaoDieta.getDthrFim(),
					prescricaoDieta.getAlteradoEm(),
					prescricaoDieta.getIndPendente(), "U", nomeMicrocomputador, 
					dataFimVinculoServidor);

			prescricaoDieta.setServidorMovimentado(servidorLogado);
		}

		this.verificarEnforceAlteracaoPrescricaoDieta(prescricaoDieta,
				prescricaoDietaOld);

	}

	public void atualizarPrescricaoDieta(MpmPrescricaoDieta prescricaoDieta, String nomeMicrocomputaor)
			throws BaseException {
		this.verificarBombaInfusao(prescricaoDieta);
		this.beforeRowUpdate(prescricaoDieta, nomeMicrocomputaor, new Date());
		this.getMpmPrescricaoDietaDAO().merge(prescricaoDieta);
	}

	public void inserirPrescricaoDieta(MpmPrescricaoDieta prescricaoDieta, Boolean isCopiado, String nomeMicrocomputador)
			throws BaseException {
		if (!isCopiado){
			this.verificarBombaInfusao(prescricaoDieta);			
		}
		this.beforeRowInsert(prescricaoDieta, nomeMicrocomputador, new Date());
		this.enforceRules(prescricaoDieta);
		this.getMpmPrescricaoDietaDAO().persistir(prescricaoDieta);
		this.getMpmPrescricaoDietaDAO().flush();

	}

	public void atualizarItemPrescricaoDieta(MpmItemPrescricaoDieta item)
			throws ApplicationBusinessException {
		this.beforeRowUpdate(item);
		this.getMpmItemPrescricaoDietaDAO().merge(item);
	}

	public void inserirItemPrescricaoDieta(MpmItemPrescricaoDieta item)
			throws BaseException {
		this.beforeRowInsert(item);
		this.getMpmItemPrescricaoDietaDAO().persistir(item);
		this.getMpmItemPrescricaoDietaDAO().flush();

	}

	/**
	 * Verificar se a unidade funcional vinculada à prescrição de dieta permite
	 * prescrever bomba de infusão
	 * 
	 * @param prescricaoDieta
	 * @throws ApplicationBusinessException
	 */
	private void verificarBombaInfusao(MpmPrescricaoDieta prescricaoDieta)
			throws ApplicationBusinessException {

		if (prescricaoDieta == null) {
			throw new IllegalArgumentException(PARAMETRO_OBRIGATORIO);
		}

		if (!prescricaoDieta.getIndBombaInfusao()) {
			return;
		}

		if (prescricaoDieta.getPrescricaoMedica().getAtendimento() != null
				&& this
						.getAghuFacade()
						.verificarCaracteristicaDaUnidadeFuncional(
								prescricaoDieta.getPrescricaoMedica().getAtendimento()
										.getUnidadeFuncional().getSeq(),
								ConstanteAghCaractUnidFuncionais.PERMITE_PRESCRICAO_BI) == DominioSimNao.N) {
			throw new ApplicationBusinessException(
					ManterPrescricaoDietaRNExceptionCode.MPM_03746);
		}

	}

	protected MpmPrescricaoDietaDAO getMpmPrescricaoDietaDAO() {
		return mpmPrescricaoDietaDAO;
	}

	protected PrescricaoMedicaRN getPrescricaoMedicaRN() {
		return prescricaoMedicaRN;
	}

	protected MpmItemPrescricaoDietaDAO getMpmItemPrescricaoDietaDAO() {
		return mpmItemPrescricaoDietaDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
