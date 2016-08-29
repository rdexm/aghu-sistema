package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoTurnosSalaVO;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MbcAgendasHorarioPrevisaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcAgendasHorarioPrevisaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;


	@EJB
	private MbcAgendasRN mbcAgendasRN;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	private static final long serialVersionUID = 1732219395079005446L;
	 
	/**
	 * @throws BaseException 
	 * @ORADB mbcp_gera_escala2
	 */	
	public MbcAgendas gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(RapServidores pucServidor, MbcProfAtuaUnidCirgs profAtuaUnidCirgs, 
			AghEspecialidades especialidade, AghUnidadesFuncionais unidadeFuncional, Date dtAgenda, MbcAgendas agenda, 
			Date dataInicial, Short sciSeqp, Byte ordemOverbooking, Byte proximaOrdemOverbooking, Boolean deslocaHorario, 
			Boolean insereAtualizaAgendaEscala) throws BaseException{
		
		agenda = getMbcAgendasDAO().obterPorChavePrimaria(agenda.getSeq());
		agenda.setDthrPrevInicio(null);
		agenda.setDthrPrevFim(null);
		agenda.setOrdemOverbooking(null);
		
		MbcAgendas agendaOriginal = getMbcAgendasDAO().obterOriginal(agenda.getSeq());
		Date vInicioTurno = null;
		Date vFimTurno = null;
		Date vIni = null;
		String vTurno = null;
		Boolean vAchou = false;
		Byte vOrdemOverbooking = null;
		Byte vInt = null;
		
		//c_agenda
		agenda.setIntervaloEscala(buscarIntervaloEscalaCirurgiaProcedimento(agenda));
		
		if(sciSeqp != null){
			List<PortalPlanejamentoTurnosSalaVO> voTurnos = buscarTurnosHorariosDisponiveis(agenda);
			
			for (PortalPlanejamentoTurnosSalaVO voTurno : voTurnos) {
				vAchou = true;
				if(vFimTurno != null && 
						DateUtil.isDatasIguais(DateUtil.truncaHorario(voTurno.getDataInicio()),DateUtil.truncaHorario(vFimTurno))){
					vInicioTurno = concatenaDiaHora(agenda.getDtAgenda(), voTurno.getHorarioInicial()).getTime();
					vFimTurno = concatenaDiaHora(agenda.getDtAgenda(), voTurno.getHorarioFinal()).getTime();
					vTurno = voTurno.getTurno();
					if(DateUtil.validaDataMaior(DateUtil.truncaHorario(vInicioTurno), DateUtil.truncaHorario(vFimTurno))){
						vFimTurno = concatenaDiaHora(DateUtil.adicionaDias(agenda.getDtAgenda(),1), voTurno.getHorarioFinal()).getTime();
					}
				}else{
					vInicioTurno = concatenaDiaHora(agenda.getDtAgenda(), voTurno.getHorarioInicial()).getTime();
					vFimTurno = concatenaDiaHora(agenda.getDtAgenda(), voTurno.getHorarioFinal()).getTime();
					vTurno = voTurno.getTurno();
					if(DateUtil.validaDataMaior(DateUtil.truncaHorario(vInicioTurno), DateUtil.truncaHorario(vFimTurno))){
						vFimTurno = concatenaDiaHora(DateUtil.adicionaDias(agenda.getDtAgenda(),1), voTurno.getHorarioFinal()).getTime();
					}
					vIni = calcularvIni(agenda, dataInicial, vInicioTurno);
				}
				
				if(agenda.getSalaCirurgica() == null) {
					MbcSalaCirurgicaId salaId = new MbcSalaCirurgicaId(unidadeFuncional.getSeq(), sciSeqp);
					MbcSalaCirurgica sala = getBlocoCirurgicoFacade().obterSalaCirurgicaPorId(salaId);
					agenda.setSalaCirurgica(sala);
				}
				
				vIni = buscarVIniAgendas(agenda, vInicioTurno, vFimTurno, vIni, dataInicial, insereAtualizaAgendaEscala);				
				
				if((calcularTempoEmMinutos(vFimTurno) - calcularTempoEmMinutos(vIni)) >= (calcularTempoEmMinutos(agenda.getTempoSala()) + agenda.getIntervaloEscala())){
					break;
				}
			}
			
			agenda = calcularVIniVFimAtualizarAgenda(profAtuaUnidCirgs,
					unidadeFuncional, dtAgenda, agenda, agendaOriginal, dataInicial, sciSeqp,
					ordemOverbooking, proximaOrdemOverbooking, deslocaHorario,
					vInicioTurno, vIni, vTurno, vAchou, vOrdemOverbooking,
					vInt, voTurnos);
		
		}
		return agenda;
	}

	public MbcAgendas calcularVIniVFimAtualizarAgenda(
			MbcProfAtuaUnidCirgs profAtuaUnidCirgs,
			AghUnidadesFuncionais unidadeFuncional, Date dtAgenda,
			MbcAgendas agenda, MbcAgendas agendaOriginal, Date dataInicial, Short sciSeqp,
			Byte ordemOverbooking, Byte proximaOrdemOverbooking,
			Boolean deslocaHorario, Date vInicioTurno, Date vIni,
			String vTurno, Boolean vAchou, Byte vOrdemOverbooking, Byte vInt,
			List<PortalPlanejamentoTurnosSalaVO> voTurnos) throws BaseException {
		Date vFim;
		if(vAchou){
			vFim = DateUtil.adicionaMinutos(vIni, calcularTempoEmMinutos(agenda.getTempoSala())); 
			
			if(agenda.getSeq() != null){
				vInt = buscarIntervaloEscalaCirurgiaProcedimento(agenda);
			}
			
			if((agenda.getDthrPrevInicio() != null && vInt < calcularTempoEmMinutos(agenda.getDthrPrevInicio()) && dataInicial != null) ||
					(dataInicial != null && deslocaHorario != null && deslocaHorario && DateUtil.getHoras(dataInicial) != 0 && DateUtil.getMinutos(dataInicial) != 0 )){ 
				vIni = dataInicial;
				vFim = DateUtil.adicionaMinutos(vIni, calcularTempoEmMinutos(agenda.getTempoSala()));
			}
			
//			if((deslocaHorario != null && deslocaHorario && dataInicial != null && DateUtil.getHoras(dataInicial) == 0 && DateUtil.getMinutos(dataInicial) == 0) ||
//				(!verificaTurnoValido(voTurnos, DateUtil.adicionaMinutos(vFim, vInt))) ||
//				((vTurno.equals("N")) && DateUtil.validaDataMenor(DateUtil.truncaHorario(vIni), DateUtil.truncaHorario(vInicioTurno)))) {
//				vIni = null;
//				vFim = null;
//			}
			
			if(calcularTempoEmMinutos(DateUtil.adicionaMinutos(vFim, vInt)) <= calcularTempoEmMinutos(vIni)
					&& calcularTempoEmMinutos(DateUtil.adicionaMinutos(vFim, vInt)) > calcularTempoEmMinutos(voTurnos.get(voTurnos.size()-1).getDataFim())
					|| !verificaTurnoValido(voTurnos, DateUtil.adicionaMinutos(vFim, vInt))) {
				vIni = null;
				vFim = null;
			}
			
			vOrdemOverbooking = calcularOrdemOverbooking(profAtuaUnidCirgs,
					unidadeFuncional, dtAgenda, sciSeqp, ordemOverbooking,
					proximaOrdemOverbooking, vIni, vFim, vOrdemOverbooking);
			
			agenda.setDthrPrevInicio(vIni);
			agenda.setDthrPrevFim(vFim);
			agenda.setIntervaloEscala(vInt);
			agenda.setOrdemOverbooking(vOrdemOverbooking);
			agenda = getMbcAgendasRN().persistirAgendaEscala(agenda, agendaOriginal);
		}
		return agenda;
	}

	public Date calcularvIni(MbcAgendas agenda, Date dataInicial,
			Date vInicioTurno) {
		Date vIni;
		if(dataInicial != null){
			vIni = dataInicial;
		}else{
			vIni = vInicioTurno;
		}
		return vIni;
	}

	public Byte calcularOrdemOverbooking(
			MbcProfAtuaUnidCirgs profAtuaUnidCirgs,
			AghUnidadesFuncionais unidadeFuncional, Date dtAgenda,
			Short sciSeqp, Byte ordemOverbooking, Byte proximaOrdemOverbooking,
			Date vIni, Date vFim, Byte vOrdemOverbooking) {
		if(vIni == null && vFim == null ){
			if(ordemOverbooking != null){
				vOrdemOverbooking = ordemOverbooking;
			}else if(proximaOrdemOverbooking != null){
				vOrdemOverbooking = proximaOrdemOverbooking;
			}else{
				List<Byte> ordens = getMbcAgendasDAO().buscarOrdemOverbook(dtAgenda, sciSeqp, profAtuaUnidCirgs.getId().getUnfSeq(), unidadeFuncional.getSeq());
				if(ordens != null && !ordens.isEmpty()){
					vOrdemOverbooking = Byte.valueOf(String.valueOf((ordens.get(0).byteValue() + Byte.valueOf("1"))));
				}else{
					vOrdemOverbooking = 1;
				}
				 
			}
		}else{
			vOrdemOverbooking = null;
		}
		return vOrdemOverbooking;
	}
	
	private Date buscarVIniAgendas(MbcAgendas agenda, Date vInicioTurno,
			Date vFimTurno, Date vIni, Date dataInicial, Boolean insereAtualizaAgendaEscala) {
		Byte vIntervaloLooping;
		Integer vTempoVago;
		Integer vTempoDuracao;
		List<MbcAgendas> agendas = getMbcAgendasDAO().buscarEscalaAgendas(agenda, vInicioTurno, vFimTurno, insereAtualizaAgendaEscala);
		for(MbcAgendas escala : agendas){
			vIntervaloLooping = escala.getIntervaloEscala();
			vTempoVago = calcularTempoEmMinutos(escala.getDthrPrevInicio() != null ? escala.getDthrPrevInicio() : vIni) - calcularTempoEmMinutos(vIni);
			vTempoDuracao = calcularTempoEmMinutos(agenda.getTempoSala()) + agenda.getIntervaloEscala();
			if (!escala.getSeq().equals(agenda.getSeq())) {
				if(vTempoVago >= vTempoDuracao) {
					if(DateUtil.validaDataMaiorIgual(DateUtil.truncaHorario(vIni), DateUtil.truncaHorario(dataInicial != null ? dataInicial : vIni))){
						break;
					}
				}
				
				if(dataInicial == null || (DateUtil.getHoras(dataInicial) == 0 && DateUtil.getMinutos(dataInicial) == 0)){
					vIni = DateUtil.adicionaMinutos(escala.getDthrPrevFim(), vIntervaloLooping);
				}
			}
		}
		return vIni;
	}
	
	private Integer calcularTempoEmMinutos(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
	}
	
	public List<PortalPlanejamentoTurnosSalaVO> buscarTurnosHorariosDisponiveis(MbcAgendas agenda) {
		List<PortalPlanejamentoTurnosSalaVO> lista = new ArrayList<PortalPlanejamentoTurnosSalaVO>(0);
	
		//buscar turnos da sala
		lista.addAll(montarVOTurnoSalas(agenda.getDtAgenda(), getMbcHorarioTurnoCirgDAO().buscarSalasTurnosHorariosDisponiveisUnion1(agenda), false));
		//salas extras (que outras equipes cederam à equipe atual)
		lista.addAll(montarVOTurnoSalas(agenda.getDtAgenda(), getMbcHorarioTurnoCirgDAO().buscarSalasTurnosHorariosDisponiveisUnion2(agenda), false));
		//salas extras cedidas pelo HCPA
		lista.addAll(montarVOTurnoSalas(agenda.getDtAgenda(), getMbcHorarioTurnoCirgDAO().buscarSalasTurnosHorariosDisponiveisUnion3(agenda), true));	
		
		Collections.sort(lista, new Comparator<PortalPlanejamentoTurnosSalaVO>() {
			@Override
			public int compare(PortalPlanejamentoTurnosSalaVO o1,
					PortalPlanejamentoTurnosSalaVO o2) {
				if (o1.getDataInicio().compareTo(o2.getDataInicio()) == 0) {
					return o1.getDataFim().compareTo(o2.getDataFim());
				} else {
					return o1.getDataInicio().compareTo(o2.getDataInicio());
				}
			}
		});
		
		return lista;
	}
	
	public List<PortalPlanejamentoTurnosSalaVO> montarVOTurnoSalas(Date dataAgenda, List<Object[]> resultado, boolean salasExtrasCedidasHcpa) {
		List<PortalPlanejamentoTurnosSalaVO> lista = new ArrayList<PortalPlanejamentoTurnosSalaVO>(0);
		
		for (Object[] object : resultado) {
			PortalPlanejamentoTurnosSalaVO vo = new PortalPlanejamentoTurnosSalaVO();
			if (salasExtrasCedidasHcpa == false) {			
				if (object[0] != null) { 
					vo.setHorarioInicial((Date) object[0]);
				} else {
					vo.setHorarioInicial((Date) object[3]);
				}
							
				if ((DateUtil.dataToString((object[2] != null) ? (Date) object[2] : (Date) object[1], "HHmm").equals("0000"))) { 
					vo.setHorarioFinal(DateUtil.adicionaMinutos(((object[2] != null) ? (Date) object[2] : (Date) object[1]), -1));
				} else {			
					vo.setHorarioFinal((object[2] != null) ? (Date) object[2] : (Date) object[1]);
				}
				//turno
				vo.setTurno(object[4].toString());
				//Data inicio e fim
				vo.setDataInicio(concatenaDiaHora(dataAgenda, (Date) object[3]).getTime());				
				vo.setDataFim(concatenaDiaHora(dataAgenda, (Date) object[1]).getTime());
			} else {				
				vo.setHorarioInicial((Date) object[1]);
				
				if ((DateUtil.dataToString((Date) object[0], "HHmm").equals("0000"))) {
					vo.setHorarioFinal(DateUtil.adicionaMinutos((Date) object[0], -1));
				} else {
					vo.setHorarioFinal((Date) object[0]);
				}
				//turno
				vo.setTurno(object[2].toString());
				//Data inicio e fim
				vo.setDataInicio(concatenaDiaHora(dataAgenda, (Date) object[1]).getTime());				
				vo.setDataFim(concatenaDiaHora(dataAgenda, (Date) object[0]).getTime());
			}
			
			lista.add(vo);
		}
		
		return lista;
	}
	
	/**
	 * @ORABD c_agenda
	 */
	@SuppressWarnings("ucd")
	public void obterIntervaloAgenda(MbcAgendas agenda) {
		agenda.setIntervaloEscala(buscarIntervaloEscalaCirurgiaProcedimento(agenda));
	}
	
//	/**
//	 * @ORABD c_escala
//	 */
//	@SuppressWarnings("ucd")
//	public List<MbcAgendas> obterEscala(MbcAgendas agenda, Date dataInicio, Date dataFim) {
//		List<MbcAgendas> lista = getMbcAgendasDAO().buscarEscalaAgendas(agenda, dataInicio, dataFim);
//		
//		Collections.sort(lista, new Comparator<MbcAgendas>() {
//			@Override
//			public int compare(MbcAgendas o1, MbcAgendas o2) {
//				if (o1.getDthrPrevInicio() != null && o1.getDthrPrevInicio().compareTo(o2.getDthrPrevInicio()) == 0) {
//					return o1.getDthrPrevFim().compareTo(o2.getDthrPrevFim());
//				} else {						
//					return o1.getDthrPrevInicio().compareTo(o1.getDthrPrevInicio());						
//				}
//			}
//		});
//		
//		return lista;
//	}		
	/**
	 * @ORADB verifica_turno_valido
	 * @param PortalPlanejamentoTurnosSalaVO
	 * @param Date dtFim
	 */
	@SuppressWarnings("ucd")
	public Boolean verificaTurnoValido(List<PortalPlanejamentoTurnosSalaVO> vo, Date dtFim) {
		Date periodoInicial = getMeiaNoite();
		
		for (PortalPlanejamentoTurnosSalaVO turnoSala : vo) {
			if ((DateUtil.validaDataMaior(DateUtil.truncaHorario(dtFim), DateUtil.truncaHorario(turnoSala.getHorarioInicial()))
					&& DateUtil.validaDataMenorIgual(DateUtil.truncaHorario(dtFim), DateUtil.truncaHorario(turnoSala.getHorarioFinal())))			
					|| (DateUtil.isDatasIguais(DateUtil.truncaHorario(dtFim), DateUtil.truncaHorario(periodoInicial))
					&& DateUtil.isDatasIguais(DateUtil.truncaHorario(turnoSala.getHorarioFinal()), 
							DateUtil.truncaHorario(DateUtil.adicionaMinutos(periodoInicial, -1))))) {
				return Boolean.TRUE;
			}
		}
				
		return Boolean.FALSE; 
	}

	private Date getMeiaNoite() {
		Calendar calendar = Calendar.getInstance();    
	    
		calendar.set(Calendar.HOUR_OF_DAY, 00);  
		calendar.set(Calendar.MINUTE, 00);  
		calendar.set(Calendar.SECOND, 00);  
		calendar.set(Calendar.MILLISECOND, 00);  
	
		Date periodoInicial = calendar.getTime();
		return periodoInicial;
	}
	
	/**
	 * @ORADB MBCC_BUSCA_INTERVALO
	 * @param MbcAgendas
	 */
	public Byte buscarIntervaloEscalaCirurgiaProcedimento(MbcAgendas agenda) {
		Byte intervalo = 0;
				
		if (agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getTipo().equals(DominioTipoProcedimentoCirurgico.CIRURGIA.toString())) {
			if (agenda.getUnidadeFuncional().getIntervaloEscalaCirurgia() != null) {
				intervalo =  agenda.getUnidadeFuncional().getIntervaloEscalaCirurgia();
			} 
		} else {
			if (agenda.getUnidadeFuncional().getIntervaloEscalaProced() != null){
				intervalo = agenda.getUnidadeFuncional().getIntervaloEscalaProced();
			}
		}	
		
		return intervalo;
	}
	
	public Calendar concatenaDiaHora(Date dia, Date hora) {				
		Calendar calHora = Calendar.getInstance(); 
		calHora.setTime(hora);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dia);
		
		cal.set(Calendar.HOUR_OF_DAY, calHora.get(Calendar.HOUR_OF_DAY));
		cal.set(Calendar.MINUTE, calHora.get(Calendar.MINUTE));
		cal.set(Calendar.SECOND, calHora.get(Calendar.SECOND));
		cal.set(Calendar.MILLISECOND, calHora.get(Calendar.MILLISECOND));
		
		return cal;
	}
	
	/**
	 * Ajuste de horarios para a atualização da escala do medico
	 * Ajuste para inserção/edição de agendas em Escala
	 */
	public void ajustarHorariosAgendamentoEmEscala(Date dtAgenda,
			Short sciSeqp, MbcProfAtuaUnidCirgs atuaUnidCirgs,
			Short espSeq, Short unfSeq, Boolean ajustaInsereAtualizaEscala) throws BaseException, ApplicationBusinessException {

		List<MbcAgendas> agendas = getMbcAgendasDAO().pesquisarAgendamentosPorData(dtAgenda, (ajustaInsereAtualizaEscala ? null : atuaUnidCirgs), 
				(ajustaInsereAtualizaEscala ? null : espSeq), unfSeq, sciSeqp, ajustaInsereAtualizaEscala);

		//Primeiro organiza os horários na situacao ES
		for(MbcAgendas escala: agendas) {
			if (!escala.getIndGeradoSistema() && DominioSituacaoAgendas.ES.equals(escala.getIndSituacao())) {
				escala.setDthrPrevInicio(null);
				escala.setDthrPrevFim(null);
				escala.setOrdemOverbooking(null);
				
				gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(escala.getPucServidor(), 
						escala.getProfAtuaUnidCirgs(), escala.getEspecialidade(), escala.getUnidadeFuncional(), escala.getDtAgenda(), escala,
						null, escala.getSalaCirurgica().getId().getSeqp(), null, null, true, true);
			}
		}
		// Depois dos horarios ES estarem ajustados, organiza os AG
		for(MbcAgendas agenda: agendas) {
			if (!agenda.getIndGeradoSistema() && DominioSituacaoAgendas.AG.equals(agenda.getIndSituacao())) {			
				agenda.setDthrPrevInicio(null);
				agenda.setDthrPrevFim(null);
				agenda.setOrdemOverbooking(null);
				
				gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(agenda.getPucServidor(), 
						agenda.getProfAtuaUnidCirgs(), agenda.getEspecialidade(), agenda.getUnidadeFuncional(), agenda.getDtAgenda(), agenda,
						null, agenda.getSalaCirurgica().getId().getSeqp(), null, null, true, false);
			}
		}
	}
	
	protected MbcAgendasRN getMbcAgendasRN(){
		return mbcAgendasRN;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	
	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO(){
		return mbcHorarioTurnoCirgDAO;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade(){
		return this.iBlocoCirurgicoFacade;
	}
}
