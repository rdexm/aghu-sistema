package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudo;
import br.gov.mec.aghu.model.MpmTipoLaudoConvenio;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoConvenioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoLaudoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class LaudoRN extends BaseBusiness {

	@EJB
	private LaudoJournalRN laudoJournalRN;
	
	private static final Log LOG = LogFactory.getLog(LaudoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmTipoLaudoDAO mpmTipoLaudoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MpmTipoLaudoConvenioDAO mpmTipoLaudoConvenioDAO;
	
	@Inject
	private MpmLaudoDAO mpmLaudoDAO;

	private static final long serialVersionUID = -8270050461923865857L;

	private enum LaudoRNExceptionCode implements BusinessExceptionCode {
		TIPO_LAUDO_INATIVO, MPM_03596, MPM_03598, MPM_03601, RAP_00175, MPM_03593, MPM_03600
	}

	protected LaudoJournalRN getLaudoJournalRN() {
		return laudoJournalRN;
	}

	protected MpmLaudoDAO getMpmLaudoDAO() {
		return mpmLaudoDAO;
	}

	/**
	 * Metodo utilizado para atualizar um laudo. Realiza execucao de
	 * verificacoes antes da atualizacao e insercao de journal caso necessario.
	 * 
	 * @param laudoNew
	 * @param laudoOld
	 * @throws BaseException
	 */
	public void atualizarLaudo(MpmLaudo laudoNew, MpmLaudo laudoOld)
			throws BaseException {

		this.executarTriggerAntesDeUpdate(laudoNew);

		mpmLaudoDAO.merge(laudoNew);
		mpmLaudoDAO.flush();

		this.getLaudoJournalRN().realizarLaudoJournal(laudoNew, laudoOld,
				DominioOperacoesJournal.UPD);

	}

	/**
	 * Realiza execucao de verificacoes na atualizacao de um laudo. ORADB:
	 * trigger MPMT_LAD_BRU
	 * 
	 * @param laudo
	 * @throws ApplicationBusinessException
	 */
	public void executarTriggerAntesDeUpdate(MpmLaudo laudo)
			throws ApplicationBusinessException {
		this.verificarDatasLaudo(laudo.getDthrInicioValidade(),
				laudo.getDthrFimValidade(), laudo.getDthrFimPrevisao());
		this.verificarLaudoManual(laudo.getLaudoManual(),
				laudo.getJustificativa(), laudo.getImpresso());
		this.verificarServidorManualLaudo(laudo.getLaudoManual(),
				laudo.getServidorFeitoManual());
	}

	/**
	 * Na trigger citada abaixo, encontra-se a chamada da procedure
	 * RN_LADP_VER_DATAS que está implementada na package MPMK_LAD_RN. ORADB:
	 * trigger MPMT_LAD_BRU (tabela mpm_laudos) ORADB: package MPMK_LAD_RN
	 * ORADB: procedure RN_LADP_VER_DATAS
	 */
	public void verificarDatasLaudo(Date dthrInicioValidade,
			Date dthrFimValidade, Date dthrFimPrevisao)
			throws ApplicationBusinessException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, 50);

		if (dthrFimValidade == null) {
			dthrFimValidade = (Date) cal.getTime().clone();
		}

		if (dthrFimPrevisao == null) {
			dthrFimPrevisao = (Date) cal.getTime().clone();
		}

		if (!CoreUtil.isMenorOuIgualDatas(dthrInicioValidade, dthrFimValidade)
				|| !CoreUtil.isMenorOuIgualDatas(dthrInicioValidade,
						dthrFimPrevisao)) {
			throw new ApplicationBusinessException(
					LaudoRNExceptionCode.MPM_03596);
		}
	}

	/**
	 * ORADB MPMK_LAD_RN.RN_LADP_VER_DELECAO
	 * 
	 * @param criadoEm
	 * @throws ApplicationBusinessException
	 */
	public void verificarDelecaoLaudo(final Date criadoEm) throws ApplicationBusinessException {
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);
		int valorNumerico = parametro.getVlrNumerico().intValue();
		
		int valorHora = DateUtil.calcularDiasEntreDatas(criadoEm, new Date());
		if (CoreUtil.maior(valorHora, valorNumerico)) {
			throw new ApplicationBusinessException(LaudoRNExceptionCode.MPM_03593);
		}
	}

	/**
	 * 
	 * Na trigger citada abaixo, encontra-se a chamada da procedure
	 * RN_LADP_VER_LAUD_MAN que está implementada na package MPMK_LAD_RN. ORADB:
	 * trigger MPMT_LAD_BRU (tabela mpm_laudos) ORADB: package MPMK_LAD_RN
	 * ORADB: procedure RN_LADP_VER_LAUD_MAN
	 */
	public void verificarLaudoManual(Boolean laudoManual, String justificativa,
			Boolean impresso) throws ApplicationBusinessException {

		if (!((laudoManual == false && StringUtils.isNotBlank(justificativa))
				|| (laudoManual == false && StringUtils.isBlank(justificativa) && impresso == false) || (laudoManual == true && StringUtils
				.isBlank(justificativa)))) {
			throw new ApplicationBusinessException(
					LaudoRNExceptionCode.MPM_03598);
		}
	}

	/**
	 * 
	 * Na trigger citada abaixo, encontra-se a chamada da procedure
	 * RN_LADP_VER_SERV_MAN que está implementada na package MPMK_LAD_RN. ORADB:
	 * trigger MPMT_LAD_BRU (tabela mpm_laudos) ORADB: package MPMK_LAD_RN
	 * ORADB: procedure RN_LADP_VER_SERV_MAN
	 */
	public void verificarServidorManualLaudo(Boolean laudoManual,
			RapServidores servidorFeitoManual)
			throws ApplicationBusinessException {
		if (!((laudoManual == true && servidorFeitoManual != null) || (laudoManual == false && servidorFeitoManual == null))) {
			throw new ApplicationBusinessException(
					LaudoRNExceptionCode.MPM_03601);
		}
	}

	/**
	 * Executa a trigger de pre-insert de MPM_LAUDO
	 * ORADB: trigger MPMT_LAD_BRI
	 * ORADB: trigger MPMT_LAD_BRI
	 * 
	 * @param laudo
	 * @throws BaseException
	 */
	public void inserirLaudo(MpmLaudo laudo)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		if(servidorLogado == null){
			throw new ApplicationBusinessException(LaudoRNExceptionCode.RAP_00175);
		}
		if(servidorLogado == null){
			throw new ApplicationBusinessException(LaudoRNExceptionCode.RAP_00175);
		}

		this.verificarDatasLaudo(laudo.getDthrInicioValidade(),
				laudo.getDthrFimValidade(), laudo.getDthrFimPrevisao());

		this.verificarLaudoManual(laudo.getLaudoManual(),
				laudo.getJustificativa(), laudo.getImpresso());

		this.verificarServidorManualLaudo(laudo.getLaudoManual(),
				laudo.getServidorFeitoManual());

		this.verificarSituacaoTipoLaudo(laudo);

		laudo.setCriadoEm(new Date());
		laudo.setServidor(servidorLogado);

		this.getMpmLaudoDAO().persistir(laudo);
	}
	
	/**
	 * O@ORADB Procedure MPMP_GERA_LAUDO_AIN
	 * @throws BaseException 
	 */
	public void geraLaudoInternacao(final AghAtendimentos atendimento,
			final Date dthrInicio, final AghuParametrosEnum tipoLaudo,
			final FatConvenioSaudePlano convenioSaudePlano)
			throws BaseException {

		// Valida parametros obrigatorios
		CoreUtil.validaParametrosObrigatorios(atendimento,dthrInicio,tipoLaudo,convenioSaudePlano);
		
		// Obtem o tipo de laudo passado no parametro tipoLaudo (AghuParametrosEnum)
		AghParametros tipoLaudoAghParametro = this.getParametroFacade().buscarAghParametro(tipoLaudo);
		final MpmTipoLaudo mpmTipoLaudo = (this.getMpmTipoLaudoDAO()).obterPorChavePrimaria(tipoLaudoAghParametro.getVlrNumerico().shortValue());
		
		// Consulta o tipo de laudo do convenio ativo
		final MpmTipoLaudoConvenio laudoConvenio = this.verificarMpmTipoLaudoConvenioAtivo(mpmTipoLaudo, convenioSaudePlano);
		
		// Verifica o resultado da consulta do tipo de laudo do convenio ativo
		if(laudoConvenio != null){
	
			// Verifica a existencia de laudo anterior
			if(this.verificarLaudoAnterior(atendimento, dthrInicio, mpmTipoLaudo)){
				
				// Instancia um laudo
				MpmLaudo laudo = new MpmLaudo();
				
				laudo.setDthrInicioValidade(dthrInicio);
				
				// Atribui ao laudo o tempo de validade
				final Short tempoValidade = laudoConvenio.getMpmTipoLaudos().getTempoValidade();		
				if(tempoValidade != null){
					laudo.setDthrFimPrevisao(new Date(dthrInicio.getTime() + tempoValidade));
				} else{
					laudo.setDthrFimPrevisao(dthrInicio);
				}
				
				laudo.setContaDesdobrada(false); 
				laudo.setImpresso(false);
				laudo.setLaudoManual(false);
				
				// Atribui ao laudo o atendimento
				laudo.setAtendimento(atendimento);
				
				laudo.setTipoLaudo(mpmTipoLaudo);
				
				// Persiste o laudo
				inserirLaudo(laudo);

			}
			
		}

	}
	
	/**
	 * Verifica se o tipo de laudo do convenio esta ativo
	 * 
	 * @return
	 */
	protected MpmTipoLaudoConvenio verificarMpmTipoLaudoConvenioAtivo(final MpmTipoLaudo mpmTipoLaudo, FatConvenioSaudePlano convenioSaudePlano) {
		
		// Valida parametros obrigatorios	
		CoreUtil.validaParametrosObrigatorios(mpmTipoLaudo,convenioSaudePlano);

		// Consulta o tipo de laudo do convenio ativo
		MpmTipoLaudoConvenio laudoConvenio = this.getMpmTipoLaudoConvenioDAO().obterMpmTipoLaudoConvenio(mpmTipoLaudo.getSeq(), convenioSaudePlano);
		
		return laudoConvenio;
	}

	/**
	 * @ORADB Function MPMP_GERA_LAUDO_AIN.function verifica_laudo_anterior
	 * Verifica a existencia de laudo anterior
	 * 
	 * @return
	 */
	protected boolean verificarLaudoAnterior(final AghAtendimentos atendimento, final Date dthrInicio, final MpmTipoLaudo mpmTipoLaudo) {
		
		// Valida parametros obrigatorios	
		CoreUtil.validaParametrosObrigatorios(dthrInicio,mpmTipoLaudo);

		// Busca laudos anteriores ordenados pela data de validade e de maneira descendente 
		List<MpmLaudo> listaLaudoAnterior = this.getMpmLaudoDAO().listarLaudosAnteriores(atendimento,mpmTipoLaudo);
		
		// Verifica a existencia de laudo anterior
		if (listaLaudoAnterior != null && !listaLaudoAnterior.isEmpty()) {

			if (mpmTipoLaudo.equals(AghuParametrosEnum.P_LAUDO_UTI)) { // Para laudo do tipo P_LAUDO_UTI...
				
				// Obtem o primeiro laudo (mais recente) da lista de laudos anteriores
				MpmLaudo laudoAnterior = listaLaudoAnterior.get(0);
				
				// Atribui no laudo a data e hora de validade
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(dthrInicio);
				calendar.add(Calendar.MINUTE, -1); // Subtrai 1 minuto
				laudoAnterior.setDthrFimValidade(calendar.getTime());

				// Atualiza informacoes do laudo anterior
				this.getMpmLaudoDAO().atualizar(laudoAnterior);
				this.getMpmLaudoDAO().flush();
				
			} else if (mpmTipoLaudo.equals(AghuParametrosEnum.P_LAUDO_ACOMPANHANTE)) { // Para laudo do tipo P_LAUDO_ACOMPANHANTE...  

				return false;
			}
		}
		// O valor de retorno padrao conforme a procedure = TRUE
		return true;
	}

	/**
	 * ORADB MPMK_LAD_RN.RN_LADP_VER_SITUACAO
	 * 
	 * Valida se o tipo de laudo a ser inserido não está inativo.
	 * 
	 * @param laudo
	 * @throws ApplicationBusinessException
	 */
	private void verificarSituacaoTipoLaudo(MpmLaudo laudo)
			throws ApplicationBusinessException {

		if (laudo.getTipoLaudo() != null
				&& laudo.getTipoLaudo().getSituacao() == DominioSituacao.I) {
			throw new ApplicationBusinessException(
					LaudoRNExceptionCode.TIPO_LAUDO_INATIVO);

		}

	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected MpmTipoLaudoConvenioDAO getMpmTipoLaudoConvenioDAO() {
		return mpmTipoLaudoConvenioDAO;
	}

	/**
	 * insere uma coleção de laudos.
	 * 
	 * @param laudos
	 * @throws BaseException
	 */
	public void inserirListaLaudos(Collection<MpmLaudo> laudos)
			throws BaseException {
		for (MpmLaudo laudo : laudos) {
			this.inserirLaudo(laudo);
		}

	}
	
	/**
	 * ORADB TRIGGER MPMT_LAD_BRD
	 * @param laudo
	 * @throws BaseException
	 */
	public void preRemoverLaudo(MpmLaudo laudo) throws ApplicationBusinessException {
		this.verificarDelecaoLaudo(laudo.getCriadoEm());
	}

	public void removerLaudo(MpmLaudo laudo) throws ApplicationBusinessException {
		preRemoverLaudo(laudo);
		MpmLaudo laudoOld = this.getMpmLaudoDAO().obterOriginal(laudo);
		getMpmLaudoDAO().remover(laudo);
		posRemoverLaudo(laudoOld);

	}

	/**
	 * ORADB TRIGGER MPMT_LAD_ARD
	 * @param laudoOld
	 * @throws BaseException
	 */
	public void posRemoverLaudo(MpmLaudo laudoOld) throws ApplicationBusinessException {
		this.getLaudoJournalRN().realizarLaudoJournal(null, laudoOld, DominioOperacoesJournal.DEL);
	}

	protected MpmTipoLaudoDAO getMpmTipoLaudoDAO() {
		return mpmTipoLaudoDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
