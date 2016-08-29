package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirgPorUnidDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoPorGrupoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável por prover os métodos de negócio de MbcAgendas.
 * 
 * @autor fwinck
 * 
 */
@Stateless
public class MbcAgendasRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcAgendasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcEquipamentoCirgPorUnidDAO mbcEquipamentoCirgPorUnidDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcAgendaProcedimentoDAO mbcAgendaProcedimentoDAO;

	@Inject
	private MbcProcedimentoPorGrupoDAO mbcProcedimentoPorGrupoDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private MbcAgendaProcedimentoRN mbcAgendaProcedimentoRN;

	@EJB
	private MbcAgendasParte1RN mbcAgendasParte1RN;
	
	@EJB
	private IPermissionService permissionService; 

	private static final long serialVersionUID = -3458622371469364380L;
	
	public enum MbcAgendasRNExceptionCode implements BusinessExceptionCode {
		MBC_00840, MBC_00841, MBC_00844, MBC_00776, MBC_00780, MBC_00781, MBC_00782, MBC_00783, MBC_00848, MBC_00849, 
		MBC_01070, MBC_01824, MBC_00850, MBC_00851, MBC_00852, MBC_00853, MBC_00438, MBC_00971, MBC_00972, MBC_01321,
		MBC_00777, MBC_00778, MBC_00845, MBC_00779, MBC_00846, MBC_00884, MBC_00973, MBC_00851_1;
	}
	
	/**
	 * @ORADB MBCK_AGD_RN.consiste_horario
	 */
	public Boolean obterAtributoConsisteHorario() {
		//Object obj = obterContextoSessao("MBCK_AGD_RN_CONSISTE_HORARIO");
		//if (obj == null) {
			return Boolean.TRUE;
		//}
		//return (Boolean) obj;
	}
	
	public MbcAgendas persistirAgenda(MbcAgendas agenda, RapServidores servidorLogado) throws BaseException{
		if(agenda.getSeq() == null) {
			inserirAgenda(agenda);
			return agenda;
		} else {
			return atualizarAgenda(agenda);
		}
	}
	
	public MbcAgendas persistirAgendaEscala(MbcAgendas agenda, MbcAgendas agendaOriginal) throws BaseException{
		if(agenda.getSeq() == null) {
			inserirAgenda(agenda);
			return agenda;
		} else {
			return atualizarAgenda(agenda, agendaOriginal);
		}
	}


	private void inserirAgenda(MbcAgendas agenda) throws BaseException {
		preInserir(agenda);
		getMbcAgendasDAO().persistir(agenda);
		getMbcAgendasDAO().flush();
		posInserir(agenda);
	}
	
	private void posInserir(MbcAgendas agenda) throws BaseException {
		
		if(DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao())|| DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())) {
			getMbcAgendaProcedimentoRN().verificarDisponibilideDeEquipamentos(agenda.getEspProcCirgs().getId().getPciSeq(),
					agenda.getIndSituacao(), agenda.getSeq(), agenda.getIndGeradoSistema(), agenda.getDthrPrevInicio(),
					agenda.getDthrPrevFim(), agenda.getUnidadeFuncional().getSeq(), agenda.getDtAgenda(), "rn_agdp_ver_utlz_equ");
		}
	}

	private void preInserir(MbcAgendas agenda) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		//Observado no evento PRE-INSERT do Forms MBCF_AGD_ATUALIZAR, DataBlock AGD
        if (agenda.getIndSituacao() == null) {
                agenda.setIndSituacao(DominioSituacaoAgendas.AG);
        }
		agenda.setDthrInclusao(new Date());
		if(agenda.getDtAgenda() != null) {
			agenda.setDtAgenda(DateUtil.truncaData(agenda.getDtAgenda()));
		}
		verificarUnidadeCirurgica(agenda.getUnidadeFuncional(), agenda.getIndGeradoSistema());
		agenda.setServidor(servidorLogado);
		if(DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())) {
			verificarDataLimite(agenda.getUnidadeFuncional(), agenda.getDtAgenda(), agenda.getIndGeradoSistema());
			verificarDatasCirurgiaEletiva(agenda.getUnidadeFuncional(), agenda.getDtAgenda(), agenda.getIndGeradoSistema());
			verificarSituacaoES(agenda.getTempoSala(),agenda.getRegime(),agenda.getIndPrecaucaoEspecial(),agenda.getDtAgenda(),
					agenda.getIndGeradoSistema());
			verificarColisaoHorarioCirurgico(agenda);
		}
		verificarTempos(agenda.getSeq(),agenda.getUnidadeFuncional(),null,agenda.getTempoSala(),agenda.getIndGeradoSistema());
		verificarRegime(agenda.getSeq(),agenda.getRegime(),agenda.getIndGeradoSistema());
		verificarTurno(agenda.getDthrPrevInicio(),agenda.getDthrPrevFim(),agenda.getIndGeradoSistema(),
				agenda.getIntervaloEscala());
		atualizarDataPrevisaoFim(agenda,agenda.getIndGeradoSistema());
		verificarDatasAgendaPaciente(agenda.getPaciente().getCodigo(), agenda.getDtAgenda(),
				agenda.getSeq(), agenda.getIndGeradoSistema());
		getAgendasParte1RN().atualizarConvenio(agenda);
		getAgendasParte1RN().verificarQuantidadeProcedimento(agenda.getEspProcCirgs(),agenda.getQtdeProc(),agenda.getTempoSala(),
				agenda.getIndGeradoSistema());
		getAgendasParte1RN().verificarObitoPaciente(agenda.getPaciente(),agenda.getIndGeradoSistema());
	}


	private MbcAgendas atualizarAgenda(MbcAgendas agenda) throws BaseException {
		return atualizarAgenda(agenda, null);
	}
	
	private MbcAgendas atualizarAgenda(MbcAgendas agenda, MbcAgendas agendaOriginal) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if(agendaOriginal == null){
			agendaOriginal = getMbcAgendasDAO().obterOriginal(agenda.getSeq());
		}
		preAtualizarAgenda(agenda,agendaOriginal);
		flush();
		agenda = getMbcAgendasDAO().merge(agenda);
		posAtualizarAgenda(agenda,servidorLogado,agendaOriginal);
		
		return agenda;
	}
	
	private void posAtualizarAgenda(MbcAgendas agenda,RapServidores servidorLogado, MbcAgendas agendaOriginal) throws BaseException {
		if(CoreUtil.modificados(agenda.getIndSituacao(),agendaOriginal.getIndSituacao())){
			if(DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao()) || DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())){
				getMbcAgendaProcedimentoRN().verificarDisponibilideDeEquipamentos(agenda.getProcedimentoCirurgico().getSeq(),
						agenda.getIndSituacao(), agenda.getSeq(), agenda.getIndGeradoSistema(), agenda.getDthrPrevInicio(),
						agenda.getDthrPrevFim(), agenda.getUnidadeFuncional().getSeq(), agenda.getDtAgenda(), "rn_agdp_ver_utlz_equ");
			}			
		}
		
		if(CoreUtil.modificados(agenda.getDtAgenda(), agendaOriginal.getDtAgenda())) {
			verificarDatasAgendaPaciente(agenda.getPaciente().getCodigo(),agenda.getDtAgenda(),agenda.getSeq(),agenda.getIndGeradoSistema());
		
		}
		
		if(DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())){
			verificarColisaoHorarioCirurgico(agenda);
		}
		
		if(CoreUtil.modificados(agenda.getIndSituacao(),agendaOriginal.getIndSituacao()) &&
				CoreUtil.modificados(agenda.getDtAgenda(),agendaOriginal.getDtAgenda())){
			verificarDatasAgendaPaciente(agenda.getPaciente().getCodigo(), agenda.getDtAgenda(), agenda.getSeq(), agenda.getIndGeradoSistema());
		}
	}

	/**
	 * @ORADB MBCT_AGD_BRU
	 * @param agenda
	 * @param servidorLogado 
	 * @throws BaseException 
	 */
	private void preAtualizarAgenda(MbcAgendas agenda, MbcAgendas agendaOriginal) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		preAtualizarAgendaParte1(agenda, servidorLogado, agendaOriginal);
		
		preAtualizarAgendaParte2(agenda, agendaOriginal);
		
		getAgendasParte1RN().preAtualizarAgendaParte3(agenda, agendaOriginal);
		
		getAgendasParte1RN().verificarIncluirHistorico(agenda, agendaOriginal);
	}

	private void preAtualizarAgendaParte2(MbcAgendas agenda,
			MbcAgendas agendaOriginal) throws ApplicationBusinessException {
		if(CoreUtil.modificados(getAgendasParte1RN().zerarData(agenda.getTempoSala()),getAgendasParte1RN().zerarData(agendaOriginal.getTempoSala())) ||
				CoreUtil.modificados(agendaOriginal.getUnidadeFuncional().getSeq(),agenda.getUnidadeFuncional().getSeq()) ){
			verificarTempos(agenda.getSeq(),agenda.getUnidadeFuncional(),agendaOriginal.getTempoSala(),agenda.getTempoSala(),agenda.getIndGeradoSistema());
		}
		
		verificarRegime(agenda.getSeq(),agenda.getRegime(),agenda.getIndGeradoSistema());
		
		verificarTurno(agenda.getDthrPrevInicio(),agenda.getDthrPrevFim(),agenda.getIndGeradoSistema(), agenda.getIntervaloEscala());
		
		if(DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())){
			verificarAltElet(agenda.getUnidadeFuncional(),agenda.getDtAgenda(),agenda.getIndGeradoSistema());
		}
		
		if(CoreUtil.modificados(getAgendasParte1RN().zerarData(agenda.getTempoSala()),getAgendasParte1RN().zerarData(agendaOriginal.getTempoSala()))){
			atualizarDataPrevisaoFim(agenda,agenda.getIndGeradoSistema());
		}
	}


	public void preAtualizarAgendaParte1(MbcAgendas agenda,
			RapServidores servidorLogado, MbcAgendas agendaOriginal)
			throws ApplicationBusinessException {
		
		/* data da alteração = data atual */
		agenda.setAlteradoEm(new Date());
		
		/* Popula dt_agenda apenas com a data sem a hora(é utilizada como índice)*/
		if(agenda.getDtAgenda() != null 
				&& agendaOriginal.getDtAgenda() != null 
				&& CoreUtil.modificados(agenda.getDtAgenda(), agendaOriginal.getDtAgenda())) {
			agenda.setDtAgenda(DateUtil.truncaData(agenda.getDtAgenda()));
		}
		/*verifica unidade cirurgica esá ativa */
		if(CoreUtil.modificados(agenda.getUnidadeFuncional().getSeq(),agendaOriginal.getUnidadeFuncional().getSeq())){
			verificarUnidadeCirurgica(agenda.getUnidadeFuncional(),agenda.getIndGeradoSistema());
		}
		
		/* Dados do servidor que digita */
		agenda.setServidorAlteradoPor(servidorLogado);
		
		
		if(DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao())){
			verificarDataLimite(agenda.getUnidadeFuncional(),agenda.getDtAgenda(),agenda.getIndGeradoSistema());
			verificarSituacaoES(agenda.getTempoSala(),agenda.getRegime(),agenda.getIndPrecaucaoEspecial(),agenda.getDtAgenda(),agenda.getIndGeradoSistema());
		}
	}



	/**
	 * @ORADB mbck_agd_rn.rn_agdp_atu_prev_fim
	 * @param agendaOriginal
	 * 
	 * Calcula fim previsto da cirurgia
	 * @param indGeradoSistema 
	 */
	public void atualizarDataPrevisaoFim(MbcAgendas agenda, Boolean indGeradoSistema) {
		if(indGeradoSistema){
			return;
		}
		if(DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao()) && agenda.getDthrPrevInicio() != null){
			agenda.setDthrPrevFim(DateUtils.addHours(agenda.getDthrPrevFim(), agenda.getTempoSala().getHours()));
			agenda.setDthrPrevFim(DateUtils.addMinutes(agenda.getDthrPrevFim(), agenda.getTempoSala().getMinutes()));
		}
	}


	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_alt_elet
	 * @param unidadeFuncional
	 * @param dtAgenda
	 * @param indGeradoSistema
	 * 
	 * Não permitir alterar cirurgia see já rodou a escala definitiva e usuário
     * não tem perfil de 'agendar cirurgia não prevista (usuário médico)
	 * @throws ApplicationBusinessException 
	 */
	public void verificarAltElet(AghUnidadesFuncionais unidadeFuncional,
			Date dtAgenda, Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(indGeradoSistema){
			return;
		}
		
		
		//A verificação de permissão abaixo é uma equivalência a query a seguir:
		/*
		 *  SELECT apf.per_nome
		    FROM cse_acoes aco,
		      cse_acoes_perfis apf,
		      cse_perfis_usuarios pfu
		    WHERE pfu.usr_id     = USER
		    AND pfu.ind_situacao = 'A'
		    AND apf.per_nome     = pfu.per_nome
		    AND aco.seq          = apf.aco_seq
		    AND (aco.descricao   = 'AGENDAR CIRURGIA NAO PREVISTA'
		    OR aco.descricao     = 'ALTERAR CIRURGIA APOS ESCALA')
		 */		
		
		Boolean usuarioPossuiAcoes = permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(),"agendarCirurgiaNaoPrevista", "agendar") || 
				permissionService.usuarioTemPermissao(obterLoginUsuarioLogado(),"alterarCirurgiaAposEscala", "alterar");
		
		if(!usuarioPossuiAcoes){
			List<MbcControleEscalaCirurgica> list = getMbcControleEscalaCirurgicaDAO().pesquisarControleEscalaCirurgicaPorUnfSeqDataPesquisa(unidadeFuncional.getSeq(), dtAgenda);
			if(list != null && !list.isEmpty()){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00438);
			}
		}
	}


	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_turno
	 * @param unidadeFuncional
	 * @param dthrPrevInicio
	 * @param dthrPrevFim
	 * @param indGeradoSistema
	 * 
	 * Conforme conversado com DR JOSÉ RICARDO, não é necessário validar se o agendamento invadiu
	 * o turno da noite.
	 * Não será possível gravar se houver troca de dia (essa validação não funciona no AGH, pois a trigger está com bug).
	 * 
	 * @throws ApplicationBusinessException 
	 */
	public void verificarTurno(Date dthrPrevInicio, Date dthrPrevFim, Boolean indGeradoSistema, Byte intervaloEscala) throws ApplicationBusinessException {
		if(indGeradoSistema || dthrPrevInicio == null){
			return;
		}
		if(calcularTempoEmMinutos(dthrPrevInicio) >= calcularTempoEmMinutos(DateUtil.adicionaMinutos(dthrPrevFim, intervaloEscala))) {
			throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00851_1);
		}
	}
	
	private Integer calcularTempoEmMinutos(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		if (cal.get(Calendar.HOUR_OF_DAY)*60==0){
			return 24*60;
		}
		return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
	}
	
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_regime
	 * @param seq
	 * @param regime
	 * @param regime2
	 * @param indGeradoSistema
	 * 
	 * Verifica se o regime agendado não é menor que o regime minimo dos outros procedimentos
	 * 
	 * Obs: Não está sendo passado o parametro epr_pci_seq e P_OLD_REGIME pois o mesmo não é utilizado na procedure migrada.
	 * @throws ApplicationBusinessException 
	 */
	public void verificarRegime(Integer agdSeq,
			DominioRegimeProcedimentoCirurgicoSus regime,
			Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(indGeradoSistema || agdSeq == null){
			return;
		}
		
		List<MbcAgendaProcedimento> listAgendaProcedimento = getMbcAgendaProcedimentoDAO().pesquisarPorAgdSeq(agdSeq);
		for(MbcAgendaProcedimento agendaProcedimento : listAgendaProcedimento){			
//			Comentado. Estava quebrando nullpointer pois acessava o ponteiro antes de verificar se é diferente de nulo.
//			Verificação esta que é realizada no IF abaixo.
//			// MBCK_AGD_RN.MBCC_GET_ORD_REGIME implementado na classe DominioRegimeProcedimentoCirurgicoSus.getOrdem()
//			Integer ordemRegimeProced = agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getRegimeProcedSus().getOrdem();
//			Integer ordemRegime = regime.getOrdem();
			// MBCK_AGD_RN.MBCC_GET_DESC_REGIME implementado na classe DominioRegimeProcedimentoCirurgicoSus.getDescricao()
			//String descricaoRegime = agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getRegimeProcedSus().getDescricao();
			if(agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getRegimeProcedSus() != null &&
					regime != null && 
					agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getRegimeProcedSus().getOrdem() > regime.getOrdem()){
				String descricaoRegime = agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getRegimeProcedSus().getDescricao();
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_01824," < "+descricaoRegime+" >  do outro procedimento "+agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getDescricao());
			}
		}
	}

	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_tempos
	 * @param agdSeq
	 * @param unidadeFuncional
	 * @param tempoSalaOriginal
	 * @param tempoSala
	 * @param indGeradoSistema
	 * 
	 * Verifica o tempo previsto da cirurgia não pode ser maior que o padrão da unidade
     * e também menor que o tempo mínimo dos outros procedimentos
	 * @throws ApplicationBusinessException 
	 */
	public void verificarTempos(Integer agdSeq,
			AghUnidadesFuncionais unidadeFuncional, Date tempoSalaOriginal,
			Date tempoSala, Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(indGeradoSistema || tempoSala == null){
			return;
		}
		
		Short tempoMinimo = 0;
		Short tempoMaximo = 0;
		Byte intervaloEscala = 0;
		
		if (unidadeFuncional.getTempoMinimoCirurgia() != null) {
			tempoMinimo = unidadeFuncional.getTempoMinimoCirurgia();
		} else if (unidadeFuncional.getTempoMaximoCirurgia() != null) {
			tempoMaximo = unidadeFuncional.getTempoMaximoCirurgia();
		}
		
		if (unidadeFuncional.getIntervaloEscalaCirurgia() != null) {
			intervaloEscala = unidadeFuncional.getIntervaloEscalaCirurgia();
		}
		 
		Integer tempoCirurgia = tempoSala.getHours()*60 + tempoSala.getMinutes() + intervaloEscala;
		
		if(tempoCirurgia < tempoMinimo && tempoMinimo != 0) {
			throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00848,(tempoMinimo - intervaloEscala));
		} else if(tempoCirurgia > tempoMaximo && tempoMaximo != 0) {
			throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00849,(tempoMaximo - intervaloEscala));
		}
		
		if(CoreUtil.modificados(tempoSalaOriginal,tempoSala)){
			List<MbcAgendaProcedimento> listAgendaProcedimento = getMbcAgendaProcedimentoDAO().pesquisarPorAgdSeq(agdSeq);
			for(MbcAgendaProcedimento agendaProcedimento : listAgendaProcedimento){
				Integer tempoMinimoMinutos = 0;
				String tempoMinimoEditadoString = "";
				String pciDescricao = "";
				
				tempoMinimoMinutos = gerarTempoMinimoMinutos(agendaProcedimento.getProcedimentoCirurgico());				
				tempoMinimoEditadoString = gerarTempoMinimoEditado(tempoMinimoMinutos);				
				pciDescricao = agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().getDescricao();
				
				if(tempoMinimoMinutos != null && (
					(tempoSala.getHours()*60 + tempoSala.getMinutes()) < tempoMinimoMinutos)){ 
					throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_01070,tempoMinimoEditadoString+" h do outro procedimento "+pciDescricao);
				}
			}
		}
		
	}

	public String gerarTempoMinimoEditado(MbcAgendaProcedimento agendaProcedimento) {
		return gerarTempoMinimoEditado(gerarTempoMinimoMinutos(agendaProcedimento.getProcedimentoCirurgico()));
	}

	public String gerarTempoMinimoEditado(Integer tempoMinimoMinutos) {
		String tempoMinimoEditadoString;
		Integer tempoMinimoEditado;
		
		//LPAD(TRUNC(((SUBSTR(LPAD(tempo_minimo,4,'0'),1,2)*60) + SUBSTR(LPAD(tempo_minimo,4,'0'),3,2))/60),2,'0') || ':' ||
		tempoMinimoEditado = tempoMinimoMinutos;
		tempoMinimoEditado = NumberUtil.truncate(Double.valueOf(tempoMinimoEditado/60), 0).intValue();
		tempoMinimoEditadoString = StringUtils.leftPad(String.valueOf(tempoMinimoEditado), 2,"0")+':';
		
		//LPAD(MOD((SUBSTR(LPAD(tempo_minimo,4,'0'),1,2)*60) + SUBSTR(LPAD(tempo_minimo,4,'0'),3,2),60),2,'0')
		tempoMinimoEditado = tempoMinimoMinutos;
		tempoMinimoEditado = tempoMinimoEditado % 60;
		tempoMinimoEditadoString = new StringBuffer(tempoMinimoEditadoString).append(StringUtils.leftPad(String.valueOf(tempoMinimoEditado), 2,"0")).toString();
		return tempoMinimoEditadoString;
	}


	public Integer gerarTempoMinimoMinutos(
			MbcProcedimentoCirurgicos procedimentoCirurgicos) {
		Integer tempoMinimoMinutos;
		Integer tempoMinimoMinutosParte1 = 0;
		Integer tempoMinimoMinutosParte2 = 0;
		//(SUBSTR(LPAD(tempo_minimo,4,'0'),1,2)*60) + SUBSTR(LPAD(tempo_minimo,4,'0'),3,2) tempo_minimo_minutos,
		String tempoMinimoMinutosString = StringUtils.leftPad(String.valueOf(procedimentoCirurgicos.getTempoMinimo()), 4,"0");
		tempoMinimoMinutosParte1 = Integer.valueOf(tempoMinimoMinutosString.substring(0, 2)) * 60;
		tempoMinimoMinutosParte2 += Integer.valueOf(tempoMinimoMinutosString.substring(2, 4));
		tempoMinimoMinutos = tempoMinimoMinutosParte1 + tempoMinimoMinutosParte2;
		return tempoMinimoMinutos;
	}


	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_sit_es
	 * @param tempoSala
	 * @param regime
	 * @param indPrecaucaoEspecial
	 * @param dtAgenda
	 * @param indGeradoSistema
	 * @throws ApplicationBusinessException 
	 * 
	 * Verifica se ind-situação for  ES  preencher campos
	 * 
	 * Obs: Os campos p_new_dthr_prev_inicio e p_new_dthr_prev_fim estavam comentados na procedure original, 
	 * por isso não foram migrados.
	 */
	public void verificarSituacaoES(Date tempoSala,
			DominioRegimeProcedimentoCirurgicoSus regime,
			Boolean indPrecaucaoEspecial, Date dtAgenda,
			Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(!indGeradoSistema){
			if(tempoSala == null){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00780);
			}
			if(regime == null){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00781);
			}
			if(indPrecaucaoEspecial == null){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00782);
			}
			if(dtAgenda == null){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00783);
			}
		}
	}


	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_dt_limit
	 * @param unidadeFuncional
	 * @param dtAgenda
	 * @param indGeradoSistema
	 * @throws ApplicationBusinessException 
	 * 
	 * Determina se a cirurgia está no limite da antecedência
	 */
	public void verificarDataLimite(AghUnidadesFuncionais unidadeFuncional,
			Date dtAgenda, Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(!indGeradoSistema){
			if(!DominioSituacao.A.equals(unidadeFuncional.getIndSitUnidFunc())){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00844);
			}
			if(unidadeFuncional.getQtdDiasLimiteCirg() != null && unidadeFuncional.getQtdDiasLimiteCirg() > 0){
				Date dataLimite = new Date();
				dataLimite = DateUtil.adicionaDias(dataLimite, unidadeFuncional.getQtdDiasLimiteCirg().intValue());
				if(DateUtil.validaDataMaior(dtAgenda, dataLimite) ){
					throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00776);
				}
			}
		}
	}


	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_unidade
	 * @param unidadeFuncional
	 * @param indGeradoSistema
	 * @throws ApplicationBusinessException 
	 * 
	 * Verifica se unidade é cirurgica e está ativa
	 */
	public void verificarUnidadeCirurgica(
			AghUnidadesFuncionais unidadeFuncional, Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(!indGeradoSistema){
			if(! getAghuFacade().verificarCaracteristicaUnidadeFuncional(unidadeFuncional.getSeq(),
					ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS)){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00840);
			}
			
			if(!DominioSituacao.A.equals(unidadeFuncional.getIndSitUnidFunc())){
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00841);
			}
		}
		
	}
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_dt_cirg
	 * 
	 * @param unidadeFuncional
	 * @param dtAgenda
	 * @param indGeradoSistema
	 * @throws ApplicationBusinessException
	 */
	public void verificarDatasCirurgiaEletiva(AghUnidadesFuncionais unidadeFuncional,
			Date dtAgenda, Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(!indGeradoSistema) {
			if(DateUtil.truncaData(dtAgenda).compareTo(DateUtil.truncaData(new Date())) < 0) {
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00777);
			}
			Calendar dataAgenda = Calendar.getInstance();
			dataAgenda.setTime(dtAgenda);
			if(dataAgenda.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00778);
			}
			if(getMbcControleEscalaCirurgicaDAO().obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
					unidadeFuncional.getSeq(), dtAgenda, DominioTipoEscala.D) != null) {
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00845);
			}
			if(getAghuFacade().obterFeriadoSemTurnoDataTruncada(dtAgenda) != null) {
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00779);
			}
		}
	}
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_intersec
	 * @param agenda
	 * @throws ApplicationBusinessException
	 */
	public void verificarColisaoHorarioCirurgico(MbcAgendas agenda) throws ApplicationBusinessException {
		if(obterAtributoConsisteHorario() && !agenda.getIndGeradoSistema()
				&& agenda.getSalaCirurgica() != null && agenda.getSalaCirurgica().getId() != null
				&& agenda.getSalaCirurgica().getId().getSeqp() != null && agenda.getDthrPrevInicio() != null
				&& agenda.getDthrPrevFim() != null) {
			List<MbcAgendas> listaAgendas = getMbcAgendasDAO().buscarHorariosOcupacaoSalaCirurgica(agenda.getDtAgenda(),
					agenda.getUnidadeFuncional().getSeq(), agenda.getSalaCirurgica().getId().getUnfSeq(),
					agenda.getSalaCirurgica().getId().getSeqp(), agenda.getSeq());
			for(MbcAgendas item : listaAgendas) {
				if(item.getDthrPrevFim() != null && item.getDthrPrevInicio() != null) {
					GregorianCalendar dtPrevFimCalculada = new GregorianCalendar();
					dtPrevFimCalculada.setTime(item.getDthrPrevFim());
					
					if(item.getIntervaloEscala() != null){
						dtPrevFimCalculada.add(Calendar.MINUTE, item.getIntervaloEscala());
					}
					
					if(!(item.getDthrPrevInicio().compareTo(agenda.getDthrPrevInicio()) < 0
							&& dtPrevFimCalculada.getTime().compareTo(agenda.getDthrPrevInicio()) <= 0)) {
						
						if(!(item.getDthrPrevInicio().compareTo(agenda.getDthrPrevFim()) >= 0)) {
							throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00846);
						}
					}
				}
			}
		}
	}
	
	/**
	 * @ORADB mbck_agd_rn.rn_agdp_ver_mesma_dt
	 * @param pacCodigo
	 * @param dtAgenda
	 * @param agdSeq
	 * @param indGeradoSistema
	 * @throws ApplicationBusinessException
	 */
	public void verificarDatasAgendaPaciente(Integer pacCodigo, Date dtAgenda, Integer agdSeq,
			Boolean indGeradoSistema) throws ApplicationBusinessException {
		if(Boolean.FALSE.equals(indGeradoSistema) && dtAgenda != null) {
			if(getMbcAgendasDAO().verificarExistenciaPacienteAgendadoNaData(pacCodigo, dtAgenda, agdSeq)) {
				throw new ApplicationBusinessException(MbcAgendasRNExceptionCode.MBC_00884);
			}
		}
	}
	
	public MbcAgendaProcedimentoRN getMbcAgendaProcedimentoRN() {
		return mbcAgendaProcedimentoRN;
	}
	
	public MbcAgendaProcedimentoDAO getMbcAgendaProcedimentoDAO(){
		return mbcAgendaProcedimentoDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected MbcEquipamentoCirgPorUnidDAO getMbcEquipamentoCirgPorUnidDAO() {
		return mbcEquipamentoCirgPorUnidDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	
	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO(){
		return mbcHorarioTurnoCirgDAO;
	}
	
	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO(){
		return mbcControleEscalaCirurgicaDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}
	
	protected MbcProcedimentoPorGrupoDAO getMbcProcedimentoPorGrupoDAO() {
		return mbcProcedimentoPorGrupoDAO;
	}

	public MbcAgendasParte1RN getAgendasParte1RN() {
		return mbcAgendasParte1RN;
	}
}