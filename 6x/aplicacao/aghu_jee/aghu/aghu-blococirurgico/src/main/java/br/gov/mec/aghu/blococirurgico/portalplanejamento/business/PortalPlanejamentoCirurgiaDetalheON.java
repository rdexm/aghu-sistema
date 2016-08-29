package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.DetalheAgendaCirurgiaHorarioVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasSalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasTurnoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirgId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PortalPlanejamentoCirurgiaDetalheON  extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PortalPlanejamentoCirurgiaDetalheON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;


	@EJB
	private RelatorioPortalPlanejamentoCirurgiasRN relatorioPortalPlanejamentoCirurgiasRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;


	private static final long serialVersionUID = 6727355541065849901L;
	
	/**
	 * Montar os horários dos turnos não contemplados e setá os seqp de todos os horários.
	 * @param vo
	 * @param unfSeq
	 */
	public void getMontarHorariosTurnoNaoContSeqp(PortalPlanejamentoCirurgiasDiaVO vo, Short unfSeq) {		
//		Long minutosTotaisDia = montarListaHorarios(vo);
		Integer countSeqp = 0;
		for(PortalPlanejamentoCirurgiasSalaVO voSala : vo.getListaSalas()){
			for(PortalPlanejamentoCirurgiasTurnoVO voTurno : voSala.getListaTurnos()){
				
				if(voTurno.getListaHorarios().isEmpty()){
					MbcHorarioTurnoCirg horarioTurnoCirg = getMbcHorarioTurnoCirgDAO().obterPorChavePrimaria(new MbcHorarioTurnoCirgId(unfSeq, voTurno.getTurno()));
					DetalheAgendaCirurgiaHorarioVO voHorario = new DetalheAgendaCirurgiaHorarioVO();
					voHorario.setHoraInicial(horarioTurnoCirg.getHorarioInicial());
					voHorario.setHoraFinal(horarioTurnoCirg.getHorarioFinal());
					voHorario.setSeqpVO(countSeqp++);
					if(voTurno.getSemUso() != null && voTurno.getSemUso()){
						voHorario.setIndisponivel(true);
					}else if(voTurno.getBloqueado() != null && voTurno.getBloqueado()){
						voHorario.setBloqueado(true);
					}
					voTurno.getListaHorarios().add(voHorario);
					//getAdicionarHorarioALista(voHorario, voTurno, unfSeq);
				}
				
				for(DetalheAgendaCirurgiaHorarioVO voHorario : voTurno.getListaHorarios()){
//					Long minutosHorario = getDiferencaEmMinutos(voHorario.getHoraFinal(),voHorario.getHoraInicial());
//					Double height = (minutosHorario * 100.0) / minutosTotaisDia;
//					voHorario.setHeight(height);
					voHorario.setSeqpVO(countSeqp++);
				}
			}
		}
	}

//	
//	private Long montarListaHorarios(PortalPlanejamentoCirurgiasDiaVO vo) {
//		List<Date> horarios = vo.getHorarios();
//		
//		Date data = DateUtil.truncaData(new Date());
//		Long countMin = 0L;
//		for(int i = 0; i<48; i++){
//			horarios.add(data);
//			data = DateUtil.adicionaMinutos(data, 30);
//			countMin += 30;
//		}
//		return countMin;
//	}
	


	public void getMontarVOHorarios(PortalPlanejamentoCirurgiasTurnoVO voTurno,
			MbcAgendas agenda, Short unfSeq) {
		if(voTurno.getListaHorarios() == null){
			voTurno.setListaHorarios(new ArrayList<DetalheAgendaCirurgiaHorarioVO>());
		}
		DetalheAgendaCirurgiaHorarioVO voHorario = new DetalheAgendaCirurgiaHorarioVO();
		
		/**
		 *  <Prontuário formatado com máscara de prontuário> - <Nome do paciente (consulta C2 + WordUrtils.capitalizeFully depois AELC_TRATA_NOME já migrado)>
		 *	<Descrição do Regime> - <Nome do procedimento (WordUrtils.capitalizeFully Neste capitalizar somente a primeira palavra)> - Tempo: <Tempo do procedimento>
		 *	Equipe <Nome do responsável da equipe>
		 */
		String nomePaciente = agenda.getPaciente().getNome();
		nomePaciente = getPortalPlanejamentoCirurgiasRN().obterNomeIntermediarioPacienteAbreviado(WordUtils.capitalizeFully(nomePaciente));
		Integer prontuario = agenda.getPaciente().getProntuario();
		String regime = WordUtils.capitalize(agenda.getRegime().getDescricao());
		String nomeProcedimento = agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getDescricao();
//		Date tempoProcedimento = null;
//		try {
//			tempoProcedimento = retornaDataFormatadaBanco(agenda.getProcedimentoCirurgico().getTempoMinimo());
//		} catch (ParseException e) {
//			logError(e.getMessage());
//		} //duvida
		String nomeRespEquipe = agenda.getProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome();
		voHorario.setPaciente(CoreUtil.formataProntuario(prontuario)+" - "+nomePaciente);
		voHorario.setEquipe(nomeRespEquipe);
		voHorario.setSeqAgenda(agenda.getSeq());
		
		// o caso de não haver hora inicio e fim é caracterizado como um overbooking, sendo assim não criar hora inicio e fim para o horário da tela.
		if(agenda.getDthrPrevInicio() != null && agenda.getDthrPrevFim() != null){ 
			if(calcularTempoEmMinutos(agenda.getDthrPrevInicio()) < calcularTempoEmMinutos(voTurno.getHorarioInicialTurno())){ //se começar antes do turno
				voHorario.setHoraInicial(voTurno.getHorarioInicialTurno());
			}else{
				voHorario.setHoraInicial(agenda.getDthrPrevInicio());
			}
			//calcular data final com intervalo de escala
			Byte intervaloEscala = agenda.getIntervaloEscala() == null ? buscarIntervaloAgendamento(unfSeq, agenda.getProcedimentoCirurgico().getTipo()) : agenda.getIntervaloEscala();
			voHorario.setHoraFinal(DateUtil.adicionaMinutos(agenda.getDthrPrevFim(), intervaloEscala));
			if(calcularTempoEmMinutos(voHorario.getHoraFinal()) > calcularTempoEmMinutos(voTurno.getHorarioFinalTurno())){
				voHorario.setHoraFinal(voTurno.getHorarioFinalTurno());
			}
		}
		Date dataDescrProc = agenda.getTempoSala();
		Integer tempoCirurgiaRealizada = 0;
		if(agenda.getIndSituacao().equals(DominioSituacaoAgendas.ES)){
			if((tempoCirurgiaRealizada = getVerificarCirurgiaRealizada(agenda)) > 0){
				dataDescrProc = calcularMinutosEmDate(tempoCirurgiaRealizada);
				voHorario.setRealizada(true);
			} else {
				if(agenda.getDthrPrevInicio() == null && agenda.getDthrPrevFim() == null){
					List<MbcCirurgias> cirgs = getMbcCirurgiasDAO().buscarCirurgiaPorAgendamentoSemMotivoCancelamento(agenda.getSeq(), agenda.getDtAgenda());
					if(!cirgs.isEmpty()){
						voHorario.setOverbooking(true);
					}
				}else if(agenda.getIndGeradoSistema()){
					voHorario.setEscala(true);
				}else{
					List<MbcCirurgias> cirgs = getMbcCirurgiasDAO().buscarCirurgiaPorAgendamentoSemMotivoCancelamento(agenda.getSeq(), agenda.getDtAgenda());
					if(!cirgs.isEmpty()){
						voHorario.setEscala(true);
					}
				}
			}
		}else{
			voHorario.setPlanejado(true);
		}
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		voHorario.setProcedimento(regime+" - "+nomeProcedimento+" - "+dateFormat.format(dataDescrProc));

		voTurno.getListaHorarios().add(voHorario);
//		getAdicionarHorarioALista(voHorario,voTurno,unfSeq);
	}
	
	private Date calcularMinutosEmDate(Integer minutos) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.truncaData(new Date()));
		cal.set(Calendar.HOUR_OF_DAY, (minutos/60));
		cal.set(Calendar.MINUTE, (minutos%60));
		return cal.getTime();
	}

//	public void getAdicionarHorarioALista(
//			DetalheAgendaCirurgiaHorarioVO voHorario,
//			PortalPlanejamentoCirurgiasTurnoVO voTurno, Short unfSeq) {
//		
//		List<DetalheAgendaCirurgiaHorarioVO> horarios = voTurno.getListaHorarios();
//		List<DetalheAgendaCirurgiaHorarioVO> horariosNovos = new ArrayList<DetalheAgendaCirurgiaHorarioVO>();
//		for(DetalheAgendaCirurgiaHorarioVO voHorarioAux : horarios){
//			if(verificarEquipeHorarioExisteNaReserva(voHorario.getEquipe(),voHorarioAux.getEquipe()) && voHorarioAux.getSeqAgenda() == null){ //significa que encontrou uma reserva para a equipe
////			if(voHorario.getEquipe().containsAll(voHorarioAux.getEquipe()) && voHorarioAux.getSeqAgenda() == null){ //significa que encontrou uma reserva para a equipe
//				Boolean alterouReserva = false;
//				
//				if(calcularTempoEmMinutos(voHorarioAux.getHoraInicial()) < calcularTempoEmMinutos(voHorario.getHoraInicial()) &&
//						calcularTempoEmMinutos(voHorario.getHoraFinal()) <= calcularTempoEmMinutos(voHorarioAux.getHoraFinal())){
//					//criar novo horario
//					DetalheAgendaCirurgiaHorarioVO novoHorario = new DetalheAgendaCirurgiaHorarioVO();
//					try {
//						novoHorario = (DetalheAgendaCirurgiaHorarioVO) BeanUtils.cloneBean(voHorarioAux);
//					} catch (Exception e) {
//						logError(e.getMessage());
//					} 
//					novoHorario.setHoraInicial(voHorarioAux.getHoraInicial());
//					novoHorario.setHoraFinal(voHorario.getHoraInicial());
//					
//					horariosNovos.add(novoHorario);
//					alterouReserva = true;
//				}
//				
//				if(calcularTempoEmMinutos(voHorarioAux.getHoraFinal()) > calcularTempoEmMinutos(voHorario.getHoraFinal()) &&
//						calcularTempoEmMinutos(voHorario.getHoraInicial()) >= calcularTempoEmMinutos(voHorarioAux.getHoraInicial())){
//					DetalheAgendaCirurgiaHorarioVO novoHorario = new DetalheAgendaCirurgiaHorarioVO();
//					try {
//						novoHorario = (DetalheAgendaCirurgiaHorarioVO) BeanUtils.cloneBean(voHorarioAux);
//					} catch (Exception e) {
//						logError(e.getMessage());
//					} 
//					novoHorario.setHoraInicial(voHorario.getHoraFinal());
//					novoHorario.setHoraFinal(voHorarioAux.getHoraFinal());
//					
//					horariosNovos.add(novoHorario);
//					alterouReserva = true;
//				}
//				
//				if(calcularTempoEmMinutos(voHorarioAux.getHoraInicial()).equals(calcularTempoEmMinutos(voHorario.getHoraInicial())) && 
//						calcularTempoEmMinutos(voHorario.getHoraFinal()).equals(calcularTempoEmMinutos(voHorarioAux.getHoraFinal()))	){
//					horariosNovos.add(voHorario);
//					continue;
//				}
//				
////				horariosNovos.add(voHorario);
//				if(!alterouReserva){
//					horariosNovos.add(voHorarioAux);
//				}else{
//					horariosNovos.add(voHorario);
//				}
//				
//			}else{
//				horariosNovos.add(voHorarioAux);
//			}
//		}
//		
//		if(!horariosNovos.isEmpty()){
//			voTurno.setListaHorarios(horariosNovos);
//		}else{
//			horarios.add(voHorario);
//		}
//		
//	}

//	private boolean verificarEquipeHorarioExisteNaReserva(List<String> equipeCorrente,
//			List<String> equipesReserva) {
//		for(String equipe : equipeCorrente){
//			for(String reserva : equipesReserva){
//				if(equipe.equals(reserva)){
//					return true;
//				}
//			}
//		}
//		return false;
//	}

	private Integer getVerificarCirurgiaRealizada(MbcAgendas agenda) {
		MbcCirurgias cirurgia = getMbcCirurgiasDAO().verificarSeAgendamentoTemCirurgiaRealizada(agenda.getSeq());
		Integer tempo = 0;
			
		if (cirurgia != null) {
			if (cirurgia.getDataSaidaSala() != null && cirurgia.getDataEntradaSala() != null) {
				tempo = getDiferencaEmMinutos(cirurgia.getDataSaidaSala(), cirurgia.getDataEntradaSala());
			} else {
				tempo = getDiferencaEmMinutos(cirurgia.getDataFimCirurgia(), cirurgia.getDataInicioCirurgia());
			}
			
			MbcDescricaoItens dsc = getMbcDescricaoItensDAO().buscarMbcDescricaoItensMaxMinHoraCirg(cirurgia.getSeq());
			
			if (dsc.getDthrInicioCirg() != null) {
				tempo = getDiferencaEmMinutos(dsc.getDthrFimCirg(), dsc.getDthrInicioCirg()) + agenda.getIntervaloEscala();
			}
		} 
		
		return tempo;
	}
	
	private Byte buscarIntervaloAgendamento(Short unfSeq, DominioTipoProcedimentoCirurgico tipo) {
		Byte retorno = 0;
		AghUnidadesFuncionais unidade = getAghuFacade().obterAghUnidFuncionaisPeloId(unfSeq);
		if (tipo.toString().equals(DominioTipoProcedimentoCirurgico.CIRURGIA.toString())) {
			if (unidade.getIntervaloEscalaCirurgia() != null) {
				retorno = unidade.getIntervaloEscalaCirurgia();
			}
		} else {
			if (unidade.getIntervaloEscalaProced() != null) {
				retorno = unidade.getIntervaloEscalaProced();
			}	
		}
		
		return retorno;
	}

	public Integer getDiferencaEmMinutos(Date dataFinal, Date dataInicial){		
        return calcularTempoEmMinutos(dataFinal) - calcularTempoEmMinutos(dataInicial);
    }

	private Integer calcularTempoEmMinutos(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
	}
//	
//	private Date retornaDataFormatadaBanco(Short procEsp) throws ParseException {
//		StringBuffer strTempoMinimo = retornaTempoMinimo(procEsp);
//		DateFormat formatacaoBanco = new SimpleDateFormat("HHmm");
//		return formatacaoBanco.parse(strTempoMinimo.toString());
//	}
	
//	private StringBuffer retornaTempoMinimo(Short procEsp){
//	StringBuffer strTempoMinimo = new StringBuffer();
//	strTempoMinimo.append(procEsp);
//	
//	while (strTempoMinimo.length() < 4) {
//		// Coloca zeros a esquerda
//		strTempoMinimo.insert(0, "0");
//	}
//	return strTempoMinimo;
//}
	
	/**
	 * MBCC_GET_AGD_DT_CANC:
	 * ON2
	 * @param agdSeq
	 * @return TAC ou TAE
	 * @throws ApplicationBusinessException
	 */
	public DominioTipoAgendaJustificativa retornarParametroCirurgiasCanceladas(Integer agdSeq) throws ApplicationBusinessException{
		AghParametros parametroMotivoDesmarcar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
		
		if(parametroMotivoDesmarcar != null) {
			List<MbcCirurgias> listaMotivoCirurgiasCanceladas = getMbcCirurgiasDAO().verificarCirurgiasCanceladas(agdSeq, 
					parametroMotivoDesmarcar.getVlrNumerico().shortValueExact());
			
			if(listaMotivoCirurgiasCanceladas != null && !listaMotivoCirurgiasCanceladas.isEmpty()) {
				return DominioTipoAgendaJustificativa.TAC;
			}
		}
		return DominioTipoAgendaJustificativa.TAE;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO(){
		return mbcHorarioTurnoCirgDAO;
	}
	
	protected RelatorioPortalPlanejamentoCirurgiasRN getPortalPlanejamentoCirurgiasRN(){
		return relatorioPortalPlanejamentoCirurgiasRN;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}
	
	protected MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}
	
	protected IAghuFacade getAghuFacade(){
		return this.iAghuFacade;
	}
}
