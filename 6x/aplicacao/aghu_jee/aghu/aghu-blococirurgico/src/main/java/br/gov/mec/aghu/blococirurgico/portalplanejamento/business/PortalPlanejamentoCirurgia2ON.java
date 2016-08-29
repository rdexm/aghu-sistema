package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnotacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcBloqSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCedenciaSalaHcpaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendamentoVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasCedenciaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasDiaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasReservaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasSalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasTurno2VO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghFeriados;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.model.MbcAgendaAnotacaoId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCedenciaSalaHcpa;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings({"PMD.NPathComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class PortalPlanejamentoCirurgia2ON extends BaseBusiness {

	private static final String _HIFEN_ = " - ";

	private static final Log LOG = LogFactory.getLog(PortalPlanejamentoCirurgia2ON.class);

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
	private MbcBloqSalaCirurgicaDAO mbcBloqSalaCirurgicaDAO;

	@Inject
	private MbcSubstEscalaSalaDAO mbcSubstEscalaSalaDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@Inject
	private MbcCedenciaSalaHcpaDAO mbcCedenciaSalaHcpaDAO;

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;

	@Inject
	private MbcAgendaAnotacaoDAO mbcAgendaAnotacaoDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;

	@EJB
	private RelatorioPortalPlanejamentoCirurgiasRN relatorioPortalPlanejamentoCirurgiasRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private PortalPlanejamentoCirurgiaON portalPlanejamentoCirurgiaON;

	@EJB
	private PortalPlanejamentoCirurgia3ON portalPlanejamentoCirurgia3ON;
	
	private static final long serialVersionUID = 1062959705824884705L;
	
	private enum PortalPlanejamentoCirurgia2ONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NAO_POSSIVEL_LOCALIZAR_DATA_VALIDA
	}

	/**
	 * Método principal, é retornado o VO que será utilizado na exibição do calendario. Busca 7 datas, baseadas nos filtros, para que sejam
	 * exibidos os turnos e salas.
	 * 
	 * @ORADB c_busca_proximo_dia
	 * @param unfSeq
	 * @param dataBase
	 * @param espSeq
	 * @param atuaUnidCirgs
	 * @param salaCirurgica
	 * @return
	 * @throws ApplicationBusinessException 
	 * @throws ApplicationBusinessException 
	 */

	public PortalPlanejamentoCirurgiasDiaVO pesquisarPortalPlanejamentoCirurgia(Short unfSeq, Date dataBase, AghEspecialidades especialidade, MbcProfAtuaUnidCirgs atuaUnidCirgs, 
			MbcSalaCirurgica salaCirurgica, Boolean reverse, Integer qtdeDias) throws ApplicationBusinessException, ApplicationBusinessException{
		return pesquisarPortalPlanejamentoCirurgia(unfSeq, dataBase, especialidade, atuaUnidCirgs, salaCirurgica, reverse, qtdeDias, false);
	}
	
	
	public PortalPlanejamentoCirurgiasDiaVO pesquisarPortalPlanejamentoCirurgia(Short unfSeq, Date dataBase, AghEspecialidades especialidade, MbcProfAtuaUnidCirgs atuaUnidCirgs, 
			MbcSalaCirurgica salaCirurgica, Boolean reverse, Integer qtdeDias, Boolean otimizado) throws ApplicationBusinessException, ApplicationBusinessException{
		//primeiramente, buscamos as 7 datas que vão popular a tela da agenda, ou apenas uma data, para a tela de detalhes da agenda.
		List<Date> datas = new ArrayList<Date>();
		Date dataFinal = null;
		Short sciSeqp = null;
		Integer countBuscas = 0;
		sciSeqp = salaCirurgica != null ? salaCirurgica.getId().getSeqp().equals(Short.valueOf("-1")) ? null : obterSeqpSalaCirurgica(salaCirurgica) : obterSeqpSalaCirurgica(salaCirurgica);
		AghParametros parametroLimiteBuscaDias = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_LIMITE_DIAS_PLANEJAMENTO_CIRURGIA);
		
		Integer maximoTentativas = null;
		if (parametroLimiteBuscaDias != null && parametroLimiteBuscaDias.getVlrNumerico() != null) {
			maximoTentativas = parametroLimiteBuscaDias.getVlrNumerico().intValue();
		} else {
			maximoTentativas = 1000; // 1000 tentativas é o valor padrão
		}
		
		Short espSeq = null;
		if(especialidade != null) {
			espSeq = especialidade.getSeq();
		}
		while(datas.size() != qtdeDias && !countBuscas.equals(maximoTentativas)){ // repete enquanto não preencher os 7 dias (ou 1 dia, na tela de detalhe) ou não chegar a 1000 tentativas de encontrar uma data valida
			countBuscas++;
			List<Date> dataExtraList = null;
			if(atuaUnidCirgs != null){
				dataExtraList = getMbcSalaCirurgicaDAO().buscarHorariosExtras(dataBase, atuaUnidCirgs, unfSeq, sciSeqp, reverse);
			}
			Boolean existeListaAgendamentos = getMbcAgendasDAO().existeAgendamentosPorData(dataBase, atuaUnidCirgs, espSeq, unfSeq, sciSeqp, false);
			if(Boolean.TRUE.equals(existeListaAgendamentos)){			
				datas.add(dataBase);
				dataBase = adicionarDiasTruncado(dataBase, reverse);
				continue;
			}
			DominioDiaSemana diaSemana = obterDiaSemana(dataBase,especialidade,unfSeq,atuaUnidCirgs, sciSeqp, reverse); 
			if(diaSemana == null){
				if(dataExtraList == null || dataExtraList.isEmpty()){
					dataBase = adicionarDiasTruncado(dataBase, reverse);
					continue;
				}else{
					datas.add(dataExtraList.get(0));
					dataBase = dataExtraList.get(0);
					dataBase = adicionarDiasTruncado(dataBase, reverse);
				}
			}else{
				dataFinal = dataBase;
				Boolean encerraLoop = false;
				while(!encerraLoop){
					Calendar cal = Calendar.getInstance();
					cal.setTime(dataFinal);
					Integer dataFinalDiaSemana = cal.get(Calendar.DAY_OF_WEEK);
					if((dataFinalDiaSemana-1) == diaSemana.ordinal()){
						if(!verificarFeriado(dataFinal,atuaUnidCirgs,unfSeq,especialidade, sciSeqp)){ 
							datas.add(dataFinal);
							dataBase = dataFinal;
							dataBase = adicionarDiasTruncado(dataBase, reverse);
							encerraLoop = true;
						}else{
							dataFinal = adicionarDiasTruncado(dataFinal, reverse);
							diaSemana = obterDiaSemana(dataFinal,especialidade,unfSeq,atuaUnidCirgs, sciSeqp, reverse);
							dataExtraList = getMbcSalaCirurgicaDAO().buscarHorariosExtras(dataFinal, atuaUnidCirgs, unfSeq, sciSeqp, reverse);
						}
					}else if(dataExtraList != null && !dataExtraList.isEmpty() &&
							((DateUtil.validaDataMaiorIgual(dataFinal, dataExtraList.get(0)) && !reverse)
									|| (DateUtil.validaDataMenorIgual(dataFinal, dataExtraList.get(0)) && reverse))){
						datas.add(dataExtraList.get(0));
						dataBase = dataExtraList.get(0);
						dataBase = adicionarDiasTruncado(dataBase, reverse);
						encerraLoop = true;
					}else{
						dataFinal = adicionarDiasTruncado(dataFinal, reverse);
						diaSemana = obterDiaSemana(dataFinal,especialidade,unfSeq,atuaUnidCirgs, sciSeqp, reverse);
						dataExtraList = getMbcSalaCirurgicaDAO().buscarHorariosExtras(dataFinal, atuaUnidCirgs, unfSeq, sciSeqp, reverse);
					}
				}
			}
		}
		if(reverse) {
			Collections.reverse(datas);
		}	
		if(datas.size() > 0){
			return montarVODia(atuaUnidCirgs, datas,especialidade,unfSeq, obterSeqpSalaCirurgica(salaCirurgica), otimizado);
		}else{
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			throw new ApplicationBusinessException(PortalPlanejamentoCirurgia2ONExceptionCode.MENSAGEM_NAO_POSSIVEL_LOCALIZAR_DATA_VALIDA, sdf.format(dataBase)); 
		}
	}

	private Short obterSeqpSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		if(salaCirurgica != null && salaCirurgica.getId() != null) {
			return salaCirurgica.getId().getSeqp();
		}
		return null;
	}

	/**
	 * Montar o VO, buscando as salas e os turnos de cada dia e a taxa de ocupação de cada turno.
	 * 
	 * @param dataBase
	 * @param atuaUnidCirgs
	 * @param vo
	 * @param datas
	 * @param especialidade
	 * @param unfSeq
	 * @param sciSeqp
	 */
	private PortalPlanejamentoCirurgiasDiaVO montarVODia(MbcProfAtuaUnidCirgs equipeFiltro, List<Date> datas, AghEspecialidades especialidadeFiltro, Short unidadeFiltro, Short salaFiltro, Boolean otimizado) {
		//Montar dia
		PortalPlanejamentoCirurgiasDiaVO voDia = new PortalPlanejamentoCirurgiasDiaVO();
		voDia.setDatasAgenda(new String[7]);
		voDia.setDatasAgendaDate(new Date[7]);
		Integer contDias = 0;
		for(Date data : datas){			
			contDias = montarVODias(voDia, contDias, data);			
		}		
		// Montar Salas
		if(Boolean.TRUE.equals(otimizado)) {
			voDia.setListaSalas(montarSalasOtimizado(datas,equipeFiltro,especialidadeFiltro,unidadeFiltro,salaFiltro));	
		} else {
			voDia.setListaSalas(montarSalas(datas,equipeFiltro,especialidadeFiltro,unidadeFiltro,salaFiltro));
		}
		
		
		return voDia;
	}

	private void adicionarSalasSemReservaOuCedencia(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasComTurnos, Map<Short, PortalPlanejamentoCirurgiasSalaVO> mapSalasComTurnos, Short unidadeFiltro, Short salaFiltro) {
		List<MbcSalaCirurgica> listaMbcSalaCirurgica = getMbcSalaCirurgicaDAO().pesquisarSalasCirurgicasPorDatasEquipeEspecUnidSala(unidadeFiltro, salaFiltro);
		for(MbcSalaCirurgica sala : listaMbcSalaCirurgica) {
			Boolean adiconar = true;
			for(PortalPlanejamentoCirurgiasSalaVO salaComReservaOucedencia : listaSalasComTurnos) {
				if(sala.getId().getSeqp().equals(salaComReservaOucedencia.getNumeroSala())) {
					adiconar = false;
					break;
				}
			}
			if(adiconar) {
				PortalPlanejamentoCirurgiasSalaVO novaSala = new PortalPlanejamentoCirurgiasSalaVO(sala.getId().getSeqp(), unidadeFiltro);
				listaSalasComTurnos.add(novaSala);
				mapSalasComTurnos.put(sala.getId().getSeqp(), novaSala);
			}
		}
	}
	
	private List<PortalPlanejamentoCirurgiasSalaVO> montarSalasOtimizado(List<Date> datas,
			MbcProfAtuaUnidCirgs equipeFiltro,
			AghEspecialidades especialidadeFiltro, Short unidadeFiltro,
			Short salaFiltro) {
		
		//carregar equipeFiltro para evitar o lazy
		if(equipeFiltro!=null){
			equipeFiltro =   getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(equipeFiltro.getId());
		}

		List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO = new ArrayList<PortalPlanejamentoCirurgiasSalaVO>();		
		List<MbcHorarioTurnoCirg> listaTurnosUnidade = getMbcHorarioTurnoCirgDAO().buscarTurnosPorUnidadeFuncional(unidadeFiltro);
		HashMap<Short, PortalPlanejamentoCirurgiasSalaVO> mapSalasComTurnos = new HashMap<Short, PortalPlanejamentoCirurgiasSalaVO>();
		int indiceData = 0;
		
		for(Date data: datas) {
			List<PortalPlanejamentoCirurgiasVO> listaSalasComTurnos = getMbcSubstEscalaSalaDAO().buscarSalasTurnos(data, equipeFiltro, unidadeFiltro, 
					salaFiltro, especialidadeFiltro != null ? especialidadeFiltro.getSeq() : null);
			if(listaSalasComTurnos != null && !listaSalasComTurnos.isEmpty()) {
				for(PortalPlanejamentoCirurgiasVO salaComTurno : listaSalasComTurnos) {
					PortalPlanejamentoCirurgiasSalaVO salaVO = mapSalasComTurnos.get(salaComTurno.getSala());
					if(salaVO == null) {
						salaVO = new PortalPlanejamentoCirurgiasSalaVO();	
						salaVO.setNumeroSala(salaComTurno.getSala());
						salaVO.setUnfSeq(unidadeFiltro);
						salaVO.setListaTurnos2(new ArrayList<PortalPlanejamentoCirurgiasTurno2VO>());
					}

					MbcHorarioTurnoCirg turno = obterDadosTurnoPadraoUnidade(listaTurnosUnidade, salaComTurno.getTurno());
					PortalPlanejamentoCirurgiasTurno2VO turnoVO = new PortalPlanejamentoCirurgiasTurno2VO();
					turnoVO.setSiglaTurno(salaComTurno.getTurno());
					turnoVO.setDescricaoTurno(obterDescricaoTurno(turnoVO.getSiglaTurno()));
					turnoVO.setHorarioInicialTurno(ajustarData((salaComTurno.getHoraInicio()) == null ? turno.getHorarioInicial() : salaComTurno.getHoraInicio()));
					turnoVO.setHorarioFinalTurno(ajustarData((salaComTurno.getHoraFim()) == null ? turno.getHorarioFinal() : salaComTurno.getHoraFim()));
					turnoVO.setMinutosOcupacao(0);
					turnoVO.setTotalMinutosTurno(getDiferencaEmMinutos(turnoVO.getHorarioFinalTurno(), turnoVO.getHorarioInicialTurno()));
					turnoVO.setDia(indiceData);
					turnoVO.setCedencia(salaComTurno.isCedencia());
					
					verificarSalaTurnoBloqueada(equipeFiltro, data, salaComTurno.getSala(), unidadeFiltro, salaComTurno.getTurno(), turnoVO);
					
					salaVO.getListaTurnos2().add(turnoVO);
					mapSalasComTurnos.put(salaComTurno.getSala(), salaVO);
				}
			}
			indiceData++;
		}

		List<Integer> listaAgendamentosProcessados = new ArrayList<Integer>();

		 if(!mapSalasComTurnos.isEmpty()) {
			 listaSalasVO =  new ArrayList<PortalPlanejamentoCirurgiasSalaVO>(mapSalasComTurnos.values());
		 }
		 
		 adicionarSalasSemReservaOuCedencia(listaSalasVO, mapSalasComTurnos, unidadeFiltro, salaFiltro);
		 adicionarTurnosIndisponiveis(listaSalasVO, listaTurnosUnidade, datas);
		
		if(!mapSalasComTurnos.isEmpty()) {
			listaSalasVO =  new ArrayList<PortalPlanejamentoCirurgiasSalaVO>(mapSalasComTurnos.values());

			List<Short> salas = new ArrayList<Short>();
			for(PortalPlanejamentoCirurgiasSalaVO sala : listaSalasVO) {
				salas.add(sala.getNumeroSala());
			}
			
			List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendas = getMbcSubstEscalaSalaDAO().buscarAgendasPorUnfSeqListaSalasDatas(salas, unidadeFiltro, 
					(Date)CollectionUtils.get(datas, 0), (Date)CollectionUtils.get(datas, datas.size() -1), especialidadeFiltro != null ? especialidadeFiltro.getSeq() : null, 
							equipeFiltro != null ? new RapServidoresId(equipeFiltro.getId().getSerMatricula(), equipeFiltro.getId().getSerVinCodigo()) : null);

			HashSet<Integer> agendas = new HashSet<Integer>();
			for(PortalPlanejamentoCirurgiasAgendaVO agenda : listaAgendas) {
				agendas.add(agenda.getSeq());
			}

			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas = new ArrayList<PortalPlanejamentoCirurgiasAgendaVO>();
			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadasComDescricao = new ArrayList<PortalPlanejamentoCirurgiasAgendaVO>();
			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasPlanejadas = new ArrayList<PortalPlanejamentoCirurgiasAgendaVO>();

			if(agendas != null && !agendas.isEmpty()) {
				cirurgiasRealizadas = getMbcSubstEscalaSalaDAO().buscarAgendasCirurgiasRealizadasPorUnfSeqListaSalasDatas(new ArrayList<Integer>(agendas), unidadeFiltro);
				cirurgiasRealizadasComDescricao = getMbcSubstEscalaSalaDAO().buscarAgendasDescCirurgiasRealizadasPorUnfSeqListaSalasDatas(new ArrayList<Integer>(agendas), unidadeFiltro);
				cirurgiasPlanejadas = getMbcSubstEscalaSalaDAO().buscarAgendasCirurgiasPlanejadasPorUnfSeqListaSalasDatas(new ArrayList<Integer>(agendas), unidadeFiltro);
			}
			
			List<PortalPlanejamentoCirurgiasAgendamentoVO> agendamenstosPorDia = getMbcSubstEscalaSalaDAO().buscarAgendamentosEquipeNaSalaPorDiaSemana(equipeFiltro, unidadeFiltro, 
					especialidadeFiltro != null ? especialidadeFiltro.getSeq() : null, salas);
			ordenarAgendamentosComAgendas(listaAgendas, agendamenstosPorDia);

			Iterator<PortalPlanejamentoCirurgiasAgendaVO> i = listaAgendas.iterator();
			while (i.hasNext()) {
				PortalPlanejamentoCirurgiasAgendaVO agenda = i.next();
				if(listaAgendamentosProcessados.contains(agenda.getSeq()) || portalPlanejamentoCirurgia3ON.isRemoverAgenda(listaAgendas, agenda, listaAgendamentosProcessados) || portalPlanejamentoCirurgia3ON.isRemoverAgendamento(agenda, agendamenstosPorDia, listaAgendamentosProcessados)) {
					i.remove();
					continue;
				}
				listaAgendamentosProcessados.add(agenda.getSeq());

				processarAgenda(mapSalasComTurnos, datas, agenda, listaAgendas, cirurgiasRealizadas, cirurgiasRealizadasComDescricao, cirurgiasPlanejadas);

			}
			
			portalPlanejamentoCirurgia3ON.ordenarSalasEAdiconarReservas(listaSalasVO, agendamenstosPorDia, datas, unidadeFiltro);
		}
		
		adicionarReservasOutraEspecialidade(listaSalasVO, listaAgendamentosProcessados, mapSalasComTurnos, datas, unidadeFiltro, equipeFiltro);

		//**** SEM SALA
		agendasSemSala(listaSalasVO, datas, listaTurnosUnidade, equipeFiltro, especialidadeFiltro, unidadeFiltro, salaFiltro, indiceData);
		
		if(listaSalasVO != null && !listaSalasVO.isEmpty()) {
			Iterator<PortalPlanejamentoCirurgiasSalaVO> iteratorSala = listaSalasVO.iterator();
			while (iteratorSala.hasNext()) {
				PortalPlanejamentoCirurgiasSalaVO sala = iteratorSala.next();
				if(verificarTurnosDatasSalaIndisponiveis(sala) || verificarTurnosDatasSalaSemAgendamentoOuReserva(sala)) {
					iteratorSala.remove();
				}
			}
		}
		
		return listaSalasVO;
	}
	
	private void agendasSemSala(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO, List<Date> datas,
			List<MbcHorarioTurnoCirg> listaTurnosUnidade, MbcProfAtuaUnidCirgs equipeFiltro,
			AghEspecialidades especialidadeFiltro, Short unidadeFiltro,
			Short salaFiltro, int indiceData) {
		 /*
		  * ADICIONAR AGENDAS SEM SALA
		  */
		if((equipeFiltro != null && especialidadeFiltro != null && salaFiltro == null) || Short.valueOf("-1").equals(salaFiltro)) {
			List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendasSemSala = mbcSubstEscalaSalaDAO.buscarAgendasPorUnfSeqSemSala(unidadeFiltro, (Date)CollectionUtils.get(datas, 0), (Date)CollectionUtils.get(datas, datas.size() -1), especialidadeFiltro.getSeq(), 
					new RapServidoresId(equipeFiltro.getId().getSerMatricula(), equipeFiltro.getId().getSerVinCodigo()));
			if(listaAgendasSemSala != null && !listaAgendasSemSala.isEmpty()) {
				PortalPlanejamentoCirurgiasSalaVO consultasSemSala = new PortalPlanejamentoCirurgiasSalaVO();
				consultasSemSala.setUnfSeq(unidadeFiltro);
				consultasSemSala.setNumeroSala(Short.valueOf("-1"));
				
				for(int c= 0; c < datas.size(); c++) {
					PortalPlanejamentoCirurgiasTurno2VO turnoVO = new PortalPlanejamentoCirurgiasTurno2VO();
					consultasSemSala.setListaTurnos2(new ArrayList<PortalPlanejamentoCirurgiasTurno2VO>());
					turnoVO.setSiglaTurno("O");
					turnoVO.setDescricaoTurno(obterDescricaoTurno(turnoVO.getSiglaTurno()));
					MbcHorarioTurnoCirg turno = obterDadosTurnoPadraoUnidade(listaTurnosUnidade, turnoVO.getSiglaTurno());
					turnoVO.setHorarioInicialTurno(ajustarData(turno.getHorarioInicial()));
					turnoVO.setHorarioFinalTurno(ajustarData(turno.getHorarioFinal()));
					turnoVO.setMinutosOcupacao(0);
					turnoVO.setIndisponivel(true);
					turnoVO.setDia(indiceData);
					consultasSemSala.getListaTurnos2().add(turnoVO);
					indiceData++;
				}
				
				List<PortalPlanejamentoCirurgiasSalaVO> listaAgendaSemSalas = new ArrayList<PortalPlanejamentoCirurgiasSalaVO>();
				listaAgendaSemSalas.add(consultasSemSala);
				adicionarTurnosIndisponiveis(listaAgendaSemSalas, listaTurnosUnidade, datas);
				
				Map<Short, PortalPlanejamentoCirurgiasSalaVO> mapSemSala = new HashMap<Short, PortalPlanejamentoCirurgiasSalaVO>();
				mapSemSala.put(Short.valueOf("-1"), consultasSemSala);
				
				HashSet<Integer> agendas = new HashSet<Integer>();
				for(PortalPlanejamentoCirurgiasAgendaVO agenda : listaAgendasSemSala) {
					agendas.add(agenda.getSeq());
				}

				List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadasComDescricao = getMbcSubstEscalaSalaDAO().buscarAgendasDescCirurgiasRealizadasPorUnfSeqListaSalasDatas(new ArrayList<Integer>(agendas), unidadeFiltro);
				for(PortalPlanejamentoCirurgiasAgendaVO agenda : listaAgendasSemSala) {
					agenda.setTurno("O");
					agenda.setSala(Short.valueOf("-1"));
					processarAgenda(mapSemSala, datas, agenda, listaAgendasSemSala, cirurgiasRealizadasComDescricao, cirurgiasRealizadasComDescricao, null);
				}

				ComparatorChain sorter = new ComparatorChain();
				sorter.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasTurno2VO.Fields.DIA.toString()));
				sorter.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasTurno2VO.Fields.HORA_INICIAL_TURNO.toString(), new NullComparator(false)));
		        Collections.sort(consultasSemSala.getListaTurnos2(), sorter);
				
				listaSalasVO.add(consultasSemSala);
				final BeanComparator ordemSorter = new BeanComparator(PortalPlanejamentoCirurgiasSalaVO.Fields.SALA.toString(), new NullComparator(true));
		        Collections.sort(listaSalasVO, ordemSorter);
			}
		}
	}
	
	private PortalPlanejamentoCirurgiasTurno2VO obterTurnoPelaSalaESilgla(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO, Short numeroSala, String turno, Integer dia) {
		for(PortalPlanejamentoCirurgiasSalaVO sala : listaSalasVO) {
			for(PortalPlanejamentoCirurgiasTurno2VO turnoVO : sala.getListaTurnos2()) {
				if(sala.getNumeroSala().equals(numeroSala) && turnoVO.getSiglaTurno().equals(turno) && turnoVO.getDia().equals(dia)) {
					return turnoVO;
				}
			}
		}
		
		return null;
	}

	private void ajustarReservasCedenciasSalas(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO, List<PortalPlanejamentoCirurgiasCedenciaVO> listaCedencias, List<Date> datas) {
		Integer seqVO = portalPlanejamentoCirurgia3ON.obterMaxSeqReserva(listaSalasVO);
		
		for(PortalPlanejamentoCirurgiasCedenciaVO cedencia : listaCedencias) {
			Boolean possuiReserva = false;
			for(PortalPlanejamentoCirurgiasSalaVO sala : listaSalasVO) {
				for(PortalPlanejamentoCirurgiasTurno2VO turno : sala.getListaTurnos2()) {
					for(PortalPlanejamentoCirurgiasReservaVO reserva : turno.getListaReservas()) {
						if(cedencia.getSala().equals(sala.getNumeroSala()) && cedencia.getTurno().equals(turno.getSiglaTurno()) 
								&& cedencia.getEquipeRecebe().getPessoaFisica().getNome().equals(reserva.getEquipe())) {
							possuiReserva = true;
						}
					}
				}
			}
			
			if(!possuiReserva) {
				PortalPlanejamentoCirurgiasTurno2VO turnoVO = obterTurnoPelaSalaESilgla(listaSalasVO, cedencia.getSala(), cedencia.getTurno(), obterIndiceData(datas, cedencia.getData()));
				if(turnoVO != null) {
					PortalPlanejamentoCirurgiasReservaVO reserva = new PortalPlanejamentoCirurgiasReservaVO();
					reserva.setEquipe(cedencia.getEquipeRecebe().getPessoaFisica().getNome());
					reserva.setHoraInicial(turnoVO.getHorarioInicialTurno());
					reserva.setHoraFinal(turnoVO.getHorarioFinalTurno());
					reserva.setCedencia(true);
					reserva.setSeqpVO(seqVO++);
					reserva.setEquipeAgenda(cedencia.getEquipe());
					MbcAgendaAnotacao agendaAnotacao = getMbcAgendaAnotacaoDAO().obterPorChavePrimaria(new MbcAgendaAnotacaoId(cedencia.getData(), 
							cedencia.getEquipe().getId().getSerMatricula(), cedencia.getEquipe().getId().getSerVinCodigo(), cedencia.getEquipe().getId().getUnfSeq(), cedencia.getEquipe().getId().getIndFuncaoProf()));
					if(agendaAnotacao != null){
						reserva.setAnotacao(agendaAnotacao.getDescricao());
					}
					turnoVO.getListaReservas().add(reserva);
				}
			}
		}
	}

	private void ajustarCedenciasSalas(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO, List<PortalPlanejamentoCirurgiasCedenciaVO> listaCedencias, List<Date> datas) {
		for(PortalPlanejamentoCirurgiasSalaVO sala : listaSalasVO) {
			for(PortalPlanejamentoCirurgiasTurno2VO turno : sala.getListaTurnos2()) {
				turno.setCedencia(isTurnoComCedencia(sala.getNumeroSala(), turno, listaCedencias, datas));
				for(PortalPlanejamentoCirurgiasReservaVO reserva : turno.getListaReservas()) {
					reserva.setCedencia(turno.getCedencia() && isReservaComCedencia(sala.getNumeroSala(), turno.getSiglaTurno(), reserva, listaCedencias));
				}
			}
		}
	}
	
	private Boolean isReservaComCedencia(Short sala, String turno, PortalPlanejamentoCirurgiasReservaVO reserva, List<PortalPlanejamentoCirurgiasCedenciaVO> listaCedencias) {
		for(PortalPlanejamentoCirurgiasCedenciaVO cedencia : listaCedencias) {
			if(cedencia.getSala().equals(sala) && cedencia.getTurno().equals(turno) && cedencia.getEquipeOriginal() != null && cedencia.getEquipeOriginal().getPessoaFisica().getNome().equals(reserva.getEquipe())) {
				reserva.setEspecialidade(null);
				reserva.setEspSeq(null);
				reserva.setEquipe(cedencia.getEquipeRecebe().getPessoaFisica().getNome());
				reserva.setEquipeAgenda(cedencia.getEquipe());
				MbcAgendaAnotacao agendaAnotacao = getMbcAgendaAnotacaoDAO().obterPorChavePrimaria(new MbcAgendaAnotacaoId(cedencia.getData(), 
						cedencia.getEquipe().getId().getSerMatricula(), cedencia.getEquipe().getId().getSerVinCodigo(), cedencia.getEquipe().getId().getUnfSeq(), cedencia.getEquipe().getId().getIndFuncaoProf()));
				if(agendaAnotacao != null){
					reserva.setAnotacao(agendaAnotacao.getDescricao());
				} else {
					reserva.setAnotacao(null);
				}
				return true;
			}
			if(cedencia.getSala().equals(sala) && cedencia.getTurno().equals(turno) && cedencia.getEquipeRecebe().getPessoaFisica().getNome().equals(reserva.getEquipe())) {
				MbcAgendaAnotacao agendaAnotacao = getMbcAgendaAnotacaoDAO().obterPorChavePrimaria(new MbcAgendaAnotacaoId(cedencia.getData(), 
						cedencia.getEquipe().getId().getSerMatricula(), cedencia.getEquipe().getId().getSerVinCodigo(), cedencia.getEquipe().getId().getUnfSeq(), cedencia.getEquipe().getId().getIndFuncaoProf()));
				if(agendaAnotacao != null){
					reserva.setAnotacao(agendaAnotacao.getDescricao());
				} else {
					reserva.setAnotacao(null);
				}
				return true;
			}
		}
		return false;
	}

	private Boolean isTurnoComCedencia(Short sala, PortalPlanejamentoCirurgiasTurno2VO turno, List<PortalPlanejamentoCirurgiasCedenciaVO> listaCedencias, List<Date> datas) {
		for(PortalPlanejamentoCirurgiasCedenciaVO cedencia : listaCedencias) {
			Integer indice = obterIndiceData(datas, cedencia.getData());
			if(cedencia.getSala().equals(sala) && cedencia.getTurno().equals(turno.getSiglaTurno()) && indice.equals(turno.getDia())) {
				return true;
			}
		}
		return false;
	}
	
	private void adicionarReservasOutraEspecialidade(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO, List<Integer> listaAgendamentosProcessados, Map<Short, PortalPlanejamentoCirurgiasSalaVO> mapSalasComTurnos, 
			List<Date> datas, Short unidadeFiltro, MbcProfAtuaUnidCirgs equipeFiltro) {
		if(!listaSalasVO.isEmpty()) {

			List<Short> salas = new ArrayList<Short>();
			for(PortalPlanejamentoCirurgiasSalaVO sala : listaSalasVO) {
				salas.add(sala.getNumeroSala());
			}
			
			List<PortalPlanejamentoCirurgiasCedenciaVO> listaCedencias = getMbcSubstEscalaSalaDAO().buscarCedenciasSalas((Date)CollectionUtils.get(datas, 0), (Date)CollectionUtils.get(datas, datas.size() -1), 
					equipeFiltro, unidadeFiltro, salas);

			List<PortalPlanejamentoCirurgiasCedenciaVO> listaCedenciasProgramadas = getMbcSubstEscalaSalaDAO().buscarCedenciasProgramadasSalas((Date)CollectionUtils.get(datas, 0), (Date)CollectionUtils.get(datas, datas.size() -1), 
					equipeFiltro, unidadeFiltro, salas);

			listaCedencias.addAll(listaCedenciasProgramadas);
			
			List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendas = getMbcSubstEscalaSalaDAO().buscarAgendasPorUnfSeqListaSalasDatas(salas, unidadeFiltro, 
					(Date)CollectionUtils.get(datas, 0), (Date)CollectionUtils.get(datas, datas.size() -1), null, 
							equipeFiltro != null ? new RapServidoresId(equipeFiltro.getId().getSerMatricula(), equipeFiltro.getId().getSerVinCodigo()) : null);

			HashSet<Integer> agendas = new HashSet<Integer>();
			for(PortalPlanejamentoCirurgiasAgendaVO agenda : listaAgendas) {
				agendas.add(agenda.getSeq());
			}

			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas = new ArrayList<PortalPlanejamentoCirurgiasAgendaVO>();
			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadasComDescricao = new ArrayList<PortalPlanejamentoCirurgiasAgendaVO>();
			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasPlanejadas = new ArrayList<PortalPlanejamentoCirurgiasAgendaVO>();

			if(agendas != null && !agendas.isEmpty()) {
				cirurgiasRealizadas = getMbcSubstEscalaSalaDAO().buscarAgendasCirurgiasRealizadasPorUnfSeqListaSalasDatas(new ArrayList<Integer>(agendas), unidadeFiltro);
				cirurgiasRealizadasComDescricao = getMbcSubstEscalaSalaDAO().buscarAgendasDescCirurgiasRealizadasPorUnfSeqListaSalasDatas(new ArrayList<Integer>(agendas), unidadeFiltro);
				cirurgiasPlanejadas = getMbcSubstEscalaSalaDAO().buscarAgendasCirurgiasPlanejadasPorUnfSeqListaSalasDatas(new ArrayList<Integer>(agendas), unidadeFiltro);
			}
			
			List<PortalPlanejamentoCirurgiasAgendamentoVO> agendamenstosPorDia = getMbcSubstEscalaSalaDAO().buscarAgendamentosEquipeNaSalaPorDiaSemana(equipeFiltro, unidadeFiltro, 
					null, salas);
			ordenarAgendamentosComAgendas(listaAgendas, agendamenstosPorDia);

			Iterator<PortalPlanejamentoCirurgiasAgendaVO> i = listaAgendas.iterator();
			while (i.hasNext()) {
				PortalPlanejamentoCirurgiasAgendaVO agenda = i.next();
				if(listaAgendamentosProcessados.contains(agenda.getSeq()) || portalPlanejamentoCirurgia3ON.isRemoverAgenda(listaAgendas, agenda, listaAgendamentosProcessados) || portalPlanejamentoCirurgia3ON.isRemoverAgendamento(agenda, agendamenstosPorDia, listaAgendamentosProcessados)) {
					i.remove();
					continue;
				}
				listaAgendamentosProcessados.add(agenda.getSeq());

				processarAgenda(mapSalasComTurnos, datas, agenda, listaAgendas, cirurgiasRealizadas, cirurgiasRealizadasComDescricao, cirurgiasPlanejadas);

			}
			
			portalPlanejamentoCirurgia3ON.ordenarSalasEAdiconarReservas(listaSalasVO, agendamenstosPorDia, datas, unidadeFiltro);
			
			ajustarCedenciasSalas(listaSalasVO, listaCedencias, datas);
			ajustarReservasCedenciasSalas(listaSalasVO, listaCedencias, datas);
		}
	}
	
	private void processarAgenda(Map<Short, PortalPlanejamentoCirurgiasSalaVO> mapSalasComTurnos, List<Date> datas, PortalPlanejamentoCirurgiasAgendaVO agenda, 
			List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendas, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadasComDescricao,
			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasPlanejadas) {
		PortalPlanejamentoCirurgiasSalaVO sala = mapSalasComTurnos.get(agenda.getSala());
		int indice = obterIndiceData(datas, agenda.getDataAgenda());
		if(indice != -1) {
			int indiceTurno = sala.getListaTurnos2().indexOf(new PortalPlanejamentoCirurgiasTurno2VO(indice, agenda.getTurno()));
			if(indiceTurno != -1) {
				ajustarAgendaVO(agenda, cirurgiasRealizadas, cirurgiasRealizadasComDescricao, cirurgiasPlanejadas);
				PortalPlanejamentoCirurgiasTurno2VO turno = sala.getListaTurnos2().get(indiceTurno);
				
				if(turno.getHorarioInicialTurno() != null && turno.getHorarioFinalTurno() != null) {
					turno.setTotalMinutosTurno(getDiferencaEmMinutos(turno.getHorarioFinalTurno(), turno.getHorarioInicialTurno()));
				}
				turno.setIndisponivel(false);
				
				processarHorarioCirurgia(turno, mapSalasComTurnos, datas, agenda, listaAgendas, cirurgiasRealizadas, cirurgiasRealizadasComDescricao, cirurgiasPlanejadas);
				if(Boolean.TRUE.equals(agenda.getOverbooking())) {
					turno.setOverbooking(true);
				}
				
				turno.getListaAgendas().add(agenda);
			}
		}
	}

	private void processarHorarioCirurgia(PortalPlanejamentoCirurgiasTurno2VO turno, Map<Short, PortalPlanejamentoCirurgiasSalaVO> mapSalasComTurnos, List<Date> datas, PortalPlanejamentoCirurgiasAgendaVO agenda, 
			List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendas, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadasComDescricao,
			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasPlanejadas) {
		try {
			final String DATE_FORMAT = "dd/MM/yyyy HHmm";
			if(agenda.getInicioAgenda() != null && agenda.getFimAgenda() != null) {
				Date dataInicio = DateUtils.parseDate(agenda.getInicioAgenda(), DATE_FORMAT);
				Date dataFim = DateUtils.parseDate(agenda.getFimAgenda(), DATE_FORMAT);
				agenda.setHoraInicial(dataInicio);
				agenda.setHoraFinal(dataFim);

				Integer horasIntervalo = agenda.getIntervaloEscala() / 100;
				Integer minutosIntervalo = agenda.getIntervaloEscala() % 100;

				Boolean descontarIntervalo = false;
				
				if(getDiferencaEmMinutos(agenda.getHoraInicial(), turno.getHorarioInicialTurno()) < 0) {
					agenda.setHoraInicial(turno.getHorarioInicialTurno());
					descontarIntervalo = true;
				}
				if(getDiferencaEmMinutos(turno.getHorarioFinalTurno(), agenda.getHoraFinal()) < 0 || getDiferencaEmMinutos(agenda.getHoraFinal(), turno.getHorarioInicialTurno()) < 0) {
					agenda.setHoraFinal(turno.getHorarioFinalTurno());
					PortalPlanejamentoCirurgiasAgendaVO novaAgenda = gerarAtendimento(agenda);
					processarAgenda(mapSalasComTurnos, datas, novaAgenda, listaAgendas, cirurgiasRealizadas, cirurgiasRealizadasComDescricao, cirurgiasPlanejadas);
					descontarIntervalo = true;
				}
				if(getDiferencaEmMinutos(turno.getHorarioFinalTurno(), agenda.getHoraFinal()) == 0){
					descontarIntervalo = true;
				}
				
				turno.setMinutosOcupacao(turno.getMinutosOcupacao() + getDiferencaEmMinutos(agenda.getHoraFinal(), agenda.getHoraInicial()) + (descontarIntervalo ? 0 : (horasIntervalo*60) + minutosIntervalo));
			} else {
				Integer horas = Integer.valueOf(agenda.getTempoSala()) / 100;
				Integer minutos = Integer.valueOf(agenda.getTempoSala()) % 100;
				Integer horasIntervalo = agenda.getIntervaloEscala() / 100;
				Integer minutosIntervalo = agenda.getIntervaloEscala() % 100;
				turno.setMinutosOcupacao(turno.getMinutosOcupacao() + (horas*60) + minutos  + ((horasIntervalo*60) + minutosIntervalo ) );
			}
		} catch (ParseException e) {
			LOG.error(agenda, e);
		}
	}
	
	private PortalPlanejamentoCirurgiasAgendaVO gerarAtendimento(PortalPlanejamentoCirurgiasAgendaVO agenda) {
		try {
			final String DATE_FORMAT = "dd/MM/yyyy HHmm";
			PortalPlanejamentoCirurgiasAgendaVO novaAgenda = (PortalPlanejamentoCirurgiasAgendaVO)BeanUtils.cloneBean(agenda);
			novaAgenda.setInicioAgenda(DateUtil.dataToString(agenda.getHoraFinal(), DATE_FORMAT));
			novaAgenda.setOrdemTurno(agenda.getOrdemTurno()+1);
			novaAgenda.setTurno(obterProximoTurno(agenda));
			
			if(novaAgenda.getOrdemTurno() > 3) {
				novaAgenda.setOrdemTurno(0);
				novaAgenda.setDataAgenda(DateUtil.adicionaDias(agenda.getDataAgenda(), 1));
			}
			return novaAgenda;
		} catch (IllegalAccessException | InstantiationException
				| InvocationTargetException | NoSuchMethodException e) {
			LOG.error(agenda, e);
		}
		return null;
	}

	private void ordenarAgendamentosComAgendas(List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendas, List<PortalPlanejamentoCirurgiasAgendamentoVO> agendamenstosPorDia) {
		for(PortalPlanejamentoCirurgiasAgendamentoVO agendamento : agendamenstosPorDia) {
			if(DominioTurno.M.toString().equals(agendamento.getTurno())) {
				agendamento.setOrderTurno(1);
			}
			else if(DominioTurno.T.toString().equals(agendamento.getTurno())) {
				agendamento.setOrderTurno(2);
			}
			else if(DominioTurno.N.toString().equals(agendamento.getTurno())) {
				agendamento.setOrderTurno(3);
			}
			else {
				agendamento.setOrderTurno(0);
			}
		}
		ComparatorChain sorterAgendamentos = new ComparatorChain();
		sorterAgendamentos.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendamentoVO.Fields.ORDER_TURNO.toString()));
		sorterAgendamentos.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendamentoVO.Fields.DIA_SEMANA.toString()));
        Collections.sort(agendamenstosPorDia, sorterAgendamentos);

		for(PortalPlanejamentoCirurgiasAgendaVO agendamento : listaAgendas) {
			if(DominioTurno.M.toString().equals(agendamento.getTurno())) {
				agendamento.setOrdemTurno(1);
			}
			else if(DominioTurno.T.toString().equals(agendamento.getTurno())) {
				agendamento.setOrdemTurno(2);
			}
			else if(DominioTurno.N.toString().equals(agendamento.getTurno())) {
				agendamento.setOrdemTurno(3);
			}
			else {
				agendamento.setOrdemTurno(0);
			}
		}
		ComparatorChain sorterAgenda = new ComparatorChain();
		sorterAgenda.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendaVO.Fields.SALA.toString()));
		sorterAgenda.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendaVO.Fields.DATA.toString()));
		sorterAgenda.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendaVO.Fields.INICIO_AGENDA.toString(), new NullComparator(false)));
		sorterAgenda.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendaVO.Fields.ORDEM_TURNO.toString()));
        Collections.sort(listaAgendas, sorterAgenda);
	}
	
	private void adicionarTurnosIndisponiveis(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO, List<MbcHorarioTurnoCirg> listaTurnosUnidade, List<Date> datas) {
		//Se não houver turnos em C13, fazer um “merge” entre os resultados da C14 com a C13
		for(MbcHorarioTurnoCirg turno : listaTurnosUnidade) {
			for(PortalPlanejamentoCirurgiasSalaVO sala : listaSalasVO) {
				int indiceData = 0;
				for(int c=0; c<datas.size(); c++) {
					PortalPlanejamentoCirurgiasTurno2VO turnoPadrao = new PortalPlanejamentoCirurgiasTurno2VO(Boolean.TRUE, indiceData++, turno.getId().getTurno(), ajustarData(turno.getHorarioInicial()), ajustarData(turno.getHorarioFinal()), 0);
					turnoPadrao.setDescricaoTurno(obterDescricaoTurno(turnoPadrao.getSiglaTurno()));
					if(!sala.getListaTurnos2().contains(turnoPadrao)) {
						sala.getListaTurnos2().add(turnoPadrao);
					}
				}
			}
		}
	}

	private String obterDescricaoTurno(String turno) {
		if(DominioTurno.M.toString().equals(turno)) {
			return DominioTurno.M.getDescricao();
		}
		else if(DominioTurno.T.toString().equals(turno)) {
			return DominioTurno.T.getDescricao();
		}
		else if(DominioTurno.N.toString().equals(turno)) {
			return DominioTurno.N.getDescricao();
		}
		return "Outro ou sem turno";
	}
	
	private String obterProximoTurno(PortalPlanejamentoCirurgiasAgendaVO agendamento) {
		if(DominioTurno.M.toString().equals(agendamento.getTurno())) {
			return DominioTurno.T.toString();
		}
		else if(DominioTurno.T.toString().equals(agendamento.getTurno())) {
			return DominioTurno.N.toString();
		}
		else if(DominioTurno.N.toString().equals(agendamento.getTurno())) {
			return "O";
		}
		return DominioTurno.M.toString();
	}

	private int obterIndiceData(List<Date> datas, Date data) {
		for(int i=0; i<datas.size(); i++) {
			if(DateUtil.diffInDaysInteger(datas.get(i), data) == 0) {
				return i;
			}
		}
		
		return -1;
	}
	
	private MbcHorarioTurnoCirg obterDadosTurnoPadraoUnidade(List<MbcHorarioTurnoCirg> turnos, String sigla) {
		for(MbcHorarioTurnoCirg turno : turnos) {
			if(turno.getId().getTurno().equalsIgnoreCase(sigla)) {
				return turno;
			}
		}
		return null;
	}
	
	/**
	 * Montar os VOs de salas, realizando validações para montagem do mesmo.
	 * @param datas
	 * @param equipeFiltro
	 * @param especialidadeFiltro
	 * @param unidadeFiltro
	 * @param salaFiltro
	 * @return
	 */
	private List<PortalPlanejamentoCirurgiasSalaVO> montarSalas(List<Date> datas,
			MbcProfAtuaUnidCirgs equipeFiltro,
			AghEspecialidades especialidadeFiltro, Short unidadeFiltro,
			Short salaFiltro) {
		
		//carregar equipeFiltro para evitar o lazy
		if(equipeFiltro!=null){
			equipeFiltro =   getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(equipeFiltro.getId());
		}
		
		// turnos
		List<MbcHorarioTurnoCirg> turnos =  getMbcHorarioTurnoCirgDAO().buscarTurnosPorUnidadeFuncional(unidadeFiltro);
		turnos = getMbcHorarioTurnoCirgDAO().ordenarMbcHorarioTurnoCirgPorHorario(turnos);
		
		List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO = new ArrayList<PortalPlanejamentoCirurgiasSalaVO>();		
		List<MbcSalaCirurgica> listaMbcSalaCirurgica = getMbcSalaCirurgicaDAO().pesquisarSalasCirurgicasPorDatasEquipeEspecUnidSala(unidadeFiltro,salaFiltro);
		
		for(MbcSalaCirurgica sala : listaMbcSalaCirurgica){
			PortalPlanejamentoCirurgiasSalaVO salaVO = new PortalPlanejamentoCirurgiasSalaVO();
			salaVO.setNumeroSala(sala.getId().getSeqp());
			salaVO.setUnfSeq(unidadeFiltro);
			salaVO.setListaTurnos2(new ArrayList<PortalPlanejamentoCirurgiasTurno2VO>());
			Integer countData = 0;
			Boolean adicionarSala = false;
			for(Date data : datas){ //para cada data, montar os turnos daquela sala
				salaVO.getListaTurnos2().addAll(
						this.montarTurnos(data, equipeFiltro, especialidadeFiltro,
								unidadeFiltro, sala, countData++, turnos));
				// Se não houver filtros de esp e equipe, deve adicionar a sala 
				// OU
				// Se houver reservas ou agendas com a especialidade e equipe informadas, deve adicionar a sala
				// OU
				// Se existe cedencia para equipe que recebeu a sala, deve adicionar a sala
				if (((especialidadeFiltro == null && equipeFiltro == null) || 
						!salaVO.getListaTurnos2().isEmpty() &&
							(verificarExisteAgendasReservas(salaVO.getListaTurnos2(),especialidadeFiltro,equipeFiltro)
									|| verificaEquipeRecebeuOuCedeuSala(data, equipeFiltro, unidadeFiltro, especialidadeFiltro, sala.getId().getSeqp(), null)))){
					adicionarSala = true;
				}
			}
			if(adicionarSala && !verificarTurnosDatasSalaIndisponiveis(salaVO)){ // só irá adicionar a sala se gerou turnos para as datas deste turno e se todos os turnos destas datas não são indisponiveis.
				listaSalasVO.add(salaVO);
			}
		}
		return listaSalasVO;
	}
	
	/**
	 * Verifica se determinada sala foi cedida.
	 * 
	 * @ORADB c_ver_cedencia
	 * @param dataFinal
	 * @param espSeq 
	 * @param unfSeq 
	 * @param atuaUnidCirgs 
	 */
	private Boolean verificaEquipeRecebeuOuCedeuSala(Date data, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq,
			AghEspecialidades especialidade, Short sciSeqp, MbcHorarioTurnoCirg turno) {
		boolean retorno = false;
		
		retorno = getMbcSubstEscalaSalaDAO().verificaEquipeRecebeuOuCedeuSala(atuaUnidCirgs, sciSeqp, unfSeq, data, turno);
		 if (retorno){
			 return retorno;
		 }
		 List<MbcCedenciaSalaHcpa> listaCedencias = this.getMbcCedenciaSalaHcpaDAO().
				 								pesquisarCedenciasPorDataTurnoUnidadeSala(data, turno, unfSeq, sciSeqp);
		 
		 if (listaCedencias != null && listaCedencias.size() > 0 && atuaUnidCirgs != null) {
			for(MbcCedenciaSalaHcpa cedencia : listaCedencias) {
				if (cedencia.getPucServidor().getId().getMatricula().equals(atuaUnidCirgs.getId().getSerMatricula())
						&& cedencia.getPucServidor().getId().getVinCodigo().equals(atuaUnidCirgs.getId().getSerVinCodigo())){
					retorno = true;
				}
			}
			return retorno;
		 }
		 return retorno;
	}

	private boolean verificarTurnosDatasSalaIndisponiveis(PortalPlanejamentoCirurgiasSalaVO salaVO) {
		Boolean indisponivel = true;
		for(PortalPlanejamentoCirurgiasTurno2VO turno : salaVO.getListaTurnos2()){
			if(!turno.getIndisponivel()){
				indisponivel = false;
			}
		}
		return indisponivel;
	}

	private boolean verificarTurnosDatasSalaSemAgendamentoOuReserva(PortalPlanejamentoCirurgiasSalaVO salaVO) {
		Boolean semAgendamentoOuReserva = true;
		for(PortalPlanejamentoCirurgiasTurno2VO turno : salaVO.getListaTurnos2()){
			if((turno.getListaAgendas() != null && !turno.getListaAgendas().isEmpty()) || (turno.getListaReservas() != null && !turno.getListaReservas().isEmpty())) {
				semAgendamentoOuReserva = false;
			}
		}
		return semAgendamentoOuReserva;
	}

	/**
	 * Verificar se existem reservas e agendas nos turnos da sala, caso tenha sido informado filtros de especialidade e equipe.
	 * @param listaTurnos2
	 * @param especialidadeFiltro
	 * @param equipeFiltro
	 * @return
	 */
	private boolean verificarExisteAgendasReservas(
			List<PortalPlanejamentoCirurgiasTurno2VO> listaTurnos2, AghEspecialidades especialidadeFiltro, MbcProfAtuaUnidCirgs equipeFiltro) {
			for(PortalPlanejamentoCirurgiasTurno2VO turno : listaTurnos2){
				if((!turno.getListaAgendas().isEmpty()) || 
						(!turno.getListaReservas().isEmpty())){
					for(PortalPlanejamentoCirurgiasAgendaVO agenda : turno.getListaAgendas()){
						if(equipeFiltro != null && especialidadeFiltro == null){
							if(agenda.getEquipe().equals(equipeFiltro.getRapServidores().getPessoaFisica().getNome()) ){
								return true;
							}
						}
						else if(equipeFiltro == null && especialidadeFiltro != null){
							if(agenda.getEspecialidade().equals(especialidadeFiltro.getNomeEspecialidade()) ){
								return true;
							}
						}
						else{
							if(agenda.getEspecialidade().equals(especialidadeFiltro.getNomeEspecialidade()) && 
									agenda.getEquipe().equals(equipeFiltro.getRapServidores().getPessoaFisica().getNome())){
								return true;
							}
						}
					}
					for(PortalPlanejamentoCirurgiasReservaVO reserva : turno.getListaReservas()){
						if(!reserva.getCedencia()){
							if(equipeFiltro != null && especialidadeFiltro == null){
								if(reserva.getEquipe().equals(equipeFiltro.getRapServidores().getPessoaFisica().getNome()) ){
									return true;
								}
							}
							else if(equipeFiltro == null && especialidadeFiltro != null){
								if(reserva.getEspecialidade().equals(especialidadeFiltro.getNomeEspecialidade())){
									return true;
								}
							}
							else {
								if(reserva.getEspecialidade()!=null){
									if(reserva.getEspecialidade().equals(especialidadeFiltro.getNomeEspecialidade()) && 
											reserva.getEquipe().equals(equipeFiltro.getRapServidores().getPessoaFisica().getNome())){
											return true;
									}
								}
							}
						}
					}
				}
			}
		return false;
	}

	/**
	 * Montar os VOs de turnos, setando seus status e atributos.
	 * @param data
	 * @param equipeFiltro	turnoVO.getListaAgendas().clear();
					turnoVO.setIndisponivel(Boolean.TRUE);
	 * @param especialidadeFiltro
	 * @param unidadeFiltro
	 * @param sala
	 * @param countData
	 * @return
	 */
	private List<PortalPlanejamentoCirurgiasTurno2VO> montarTurnos(Date data,
			MbcProfAtuaUnidCirgs equipeFiltro,
			AghEspecialidades especialidadeFiltro, Short unidadeFiltro,
			MbcSalaCirurgica sala, Integer countData, List<MbcHorarioTurnoCirg> turnos) {
		List<PortalPlanejamentoCirurgiasTurno2VO> listaTurnosVO = new ArrayList<PortalPlanejamentoCirurgiasTurno2VO>();
		//TODO tirar do loop
//		List<MbcHorarioTurnoCirg> turnos =  getMbcHorarioTurnoCirgDAO().buscarTurnosPorUnidadeFuncional(unidadeFiltro);
//		turnos = getMbcHorarioTurnoCirgDAO().ordenarMbcHorarioTurnoCirgPorHorario(turnos);

		Integer countTurno = 0;
		for(MbcHorarioTurnoCirg turno : turnos){
			PortalPlanejamentoCirurgiasTurno2VO turnoVO = new PortalPlanejamentoCirurgiasTurno2VO();
			turnoVO.setDescricaoTurno(turno.getMbcTurnos().getDescricao());
			turnoVO.setSiglaTurno(turno.getMbcTurnos().getTurno());
			turnoVO.setHorarioInicialTurno(turno.getHorarioInicial());
			turnoVO.setHorarioFinalTurno(turno.getHorarioFinal());
			calcularHorarioTotal(turnoVO);
			turnoVO.setListaReservas(montarReservas(turno,data,equipeFiltro,especialidadeFiltro,unidadeFiltro,sala));
			for(PortalPlanejamentoCirurgiasReservaVO reserva : turnoVO.getListaReservas()) {
				if(reserva.getBloqueio() != null && reserva.getBloqueio()){
					verificarSalaTurnoBloqueada(equipeFiltro, data, sala.getId().getSeqp(), sala.getId().getUnfSeq(), turno.getMbcTurnos().getTurno(), turnoVO);
					break;
				}
				if(reserva.getCedencia()) {
					turnoVO.setCedencia(true); // flag para progress bar na parte 1 da agenda
					continue;
				}
			}
			turnoVO.setListaAgendas(montarAgendas(turnoVO,turno,data,equipeFiltro,especialidadeFiltro,unidadeFiltro,sala,countTurno,turnos,listaTurnosVO));
			if(turnoVO.getListaReservas().isEmpty() && turnoVO.getListaAgendas().isEmpty()
					&& (equipeFiltro != null || especialidadeFiltro != null)
					&& (equipeFiltro == null || !verificaEquipeRecebeuOuCedeuSala(data, equipeFiltro, unidadeFiltro, especialidadeFiltro, sala.getId().getSeqp(), turno))
					&& (turnoVO.getListaReservas().isEmpty() && !verificarExisteAgendasEquipeEsp(turnoVO.getListaAgendas(),equipeFiltro,especialidadeFiltro)) 
					|| !getMbcCaracteristicaSalaCirgDAO().existeTurnosValidos(data,sala,turno)) {
				// Caso não tenha reserva agenda, é indisponivel OU
				// Verifica, caso informado filtros de equipe e esp, se existe agendas daquela equipe (reservas sempre são da equipe ou esp do filtro), senão tornar turno indisponivel.
				//caso não haver cirurgias urgentes ou de emergência remove agendas listadas e seta o turno como indisponivel
				if(!verificarCirurgiasUrgenciaEmergencia(turnoVO.getListaAgendas())){
					turnoVO.getListaAgendas().clear();
					turnoVO.setIndisponivel(Boolean.TRUE);
					if(!turnoVO.getBloqueado()) {
						turnoVO.setHorarioOcupado("-");
						turnoVO.setPorcentagem(new Double("100"));
					}
				}
			}
			
			if(equipeFiltro != null ) {
				Boolean temAgenda = false;
				Boolean temReserva = false;
				for(PortalPlanejamentoCirurgiasAgendaVO agenda : turnoVO.getListaAgendas()) {
					if(agenda.getEquipe().equals(equipeFiltro.getRapServidores().getPessoaFisica().getNome()) ){
						temAgenda = true;
					}
					
				}
				
				for(PortalPlanejamentoCirurgiasReservaVO reserva : turnoVO.getListaReservas()) {
					if(reserva.getEquipe().equals(equipeFiltro.getRapServidores().getPessoaFisica().getNome()) ){
						temReserva = true;
					}
				}
				if(!temAgenda && !temReserva) {
					turnoVO.getListaAgendas().clear();
					turnoVO.setIndisponivel(Boolean.TRUE);
					if(!turnoVO.getBloqueado()) {
						turnoVO.setHorarioOcupado("-");
						turnoVO.setPorcentagem(new Double("100"));
					}
				}
			}
			
			countTurno++;
			turnoVO.setDia(countData);
			listaTurnosVO.add(turnoVO);
		}
		return listaTurnosVO;
	}
	
	private Boolean verificarCirurgiasUrgenciaEmergencia(List<PortalPlanejamentoCirurgiasAgendaVO> agendas){
		for(PortalPlanejamentoCirurgiasAgendaVO agenda : agendas){
			if(agenda.getIndGeradoSistema()){
				return true;
			}
		}
		return false;
	}
	
	private Boolean verificarExisteAgendasEquipeEsp(
			List<PortalPlanejamentoCirurgiasAgendaVO> listAgendas,
			MbcProfAtuaUnidCirgs equipeFiltro,
			AghEspecialidades especialidadeFiltro) {
		for(PortalPlanejamentoCirurgiasAgendaVO agenda : listAgendas){
			if(equipeFiltro != null && especialidadeFiltro == null){
				if(agenda.getEquipe().equals(equipeFiltro.getRapServidores().getPessoaFisica().getNome()) ){
					return true;
				}
			}
			else if(equipeFiltro == null && especialidadeFiltro != null){
				if(agenda.getEspecialidade().equals(especialidadeFiltro.getNomeEspecialidade()) ){
					return true;
				}
			}
			else{
				if(agenda.getEspecialidade().equals(especialidadeFiltro.getNomeEspecialidade()) && 
						agenda.getEquipe().equals(equipeFiltro.getRapServidores().getPessoaFisica().getNome())){
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Verifica se determinado turno da sala está bloqueado na determinada dataBase, se estiver bloqueado, seta os status.
	 * 
	 * @param dataBase
	 * @param atuaUnidCirgs
	 * @param data
	 * @param caract
	 * @param voTurno
	 */
	private void verificarSalaTurnoBloqueada(MbcProfAtuaUnidCirgs equipeFiltro,
			Date data, Short salaSeqp, Short salaUnfSeq, String turno,
			PortalPlanejamentoCirurgiasTurno2VO turnoVO) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		StringBuffer sb = new StringBuffer();
		Integer diaSem = cal.get(Calendar.DAY_OF_WEEK);
		List<MbcBloqSalaCirurgica> bloqSalaCirurgicas = getMbcBloqSalaCirurgicaDAO().pesquisarBloqueioSalaPorSalaDataProf(salaSeqp, salaUnfSeq, 
				data, DominioDiaSemanaSigla.getDiaSemanaSigla(diaSem), 
				turno, 
				equipeFiltro);
		if(bloqSalaCirurgicas != null && !bloqSalaCirurgicas.isEmpty()){
			turnoVO.setBloqueado(true);
			sb = new StringBuffer();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			turnoVO.setHorarioOcupado("-");
			turnoVO.setPorcentagem(100.0);
			sb.append(bloqSalaCirurgicas.get(0).getMotivo());
			sb.append(" - Período: ");
			if(DateUtil.isDatasIguais(bloqSalaCirurgicas.get(0).getDtInicio(), bloqSalaCirurgicas.get(0).getDtFim())){
				sb.append(sdf.format(bloqSalaCirurgicas.get(0).getDtInicio()));
			}else{
				sb.append(sdf.format(bloqSalaCirurgicas.get(0).getDtInicio()));
				sb.append(_HIFEN_).append(sdf.format(bloqSalaCirurgicas.get(0).getDtFim()));
			}
			turnoVO.setMotivoBloqueio(getQuebrarToolTip(sb.toString(), 60));
		}else{
			turnoVO.setBloqueado(false);
		}
	}
	
	public String getQuebrarToolTip(String motivo, int intervalo) {		
		StringBuilder retorno = new StringBuilder();
		boolean flag = false;
	    int atualIndex = intervalo, auxIndex = 0;	    
	    retorno.append(motivo);
	    if (motivo.length() > intervalo) {
	        while (true) {
	        	if (retorno.charAt(atualIndex) != ' ') {
	        		atualIndex++;
	        		auxIndex++;
	        		flag = false;
	            } else {
	            	retorno.insert(atualIndex, "<br/>");
	                atualIndex += intervalo + 5;
	                
	                flag = true;
	            }

	            if (atualIndex >= retorno.length()) {
	            	break;
	            }
	        }
	        if (flag == false) {
	        	atualIndex -= auxIndex;
	        	while (true) {
	        		retorno.insert(atualIndex, "<br/>");
	                atualIndex += intervalo + 5;
	        		
	        		if (atualIndex >= retorno.length()) {
	        			break;
	        		}
	        	}	        	
	        }
	    }
	    return retorno.toString();
	}

	/**
	 * Calcular o horario total do turno, baseado horario de inicio e fim do cadastro do turno.
	 * @param turnoVO
	 */
	public void calcularHorarioTotal(
			PortalPlanejamentoCirurgiasTurno2VO turnoVO) {
		Calendar calHoraInicio = Calendar.getInstance();
		calHoraInicio.setTime(turnoVO.getHorarioInicialTurno());
		Calendar calHoraFim = Calendar.getInstance();
		calHoraFim.setTime(turnoVO.getHorarioFinalTurno());
		Integer minutosTotais = (calHoraFim.get(Calendar.HOUR_OF_DAY)*60)+calHoraFim.get(Calendar.MINUTE) - (calHoraInicio.get(Calendar.HOUR_OF_DAY)*60)+calHoraInicio.get(Calendar.MINUTE);
		Date date = new Date();
		date = DateUtil.truncaData(date);
		Calendar calHorarioTotal = Calendar.getInstance();
		calHorarioTotal.setTime(date);
		calHorarioTotal.set(Calendar.MINUTE, minutosTotais);
		turnoVO.setHorarioTotal(calHorarioTotal.getTime());
	}

	/**
	 * Montar as reservas daquele turno, seus status e atributos, baseado nos filtros, se informados.
	 * @param turno
	 * @param data
	 * @param equipeFiltro
	 * @param especialidadeFiltro
	 * @param unidadeFiltro
	 * @param sala
	 * @return
	 */
	private List<PortalPlanejamentoCirurgiasReservaVO> montarReservas(
			MbcHorarioTurnoCirg turno, Date data,
			MbcProfAtuaUnidCirgs equipeFiltro,
			AghEspecialidades especialidadeFiltro, Short unidadeFiltro,
			MbcSalaCirurgica sala) {
		List<PortalPlanejamentoCirurgiasReservaVO> listaReservasVO = new ArrayList<PortalPlanejamentoCirurgiasReservaVO>();
		List<MbcCedenciaSalaHcpa> cedencias = getMbcCedenciaSalaHcpaDAO().pesquisarCedenciasPorDataTurnoUnidadeSala(data, turno, unidadeFiltro, sala.getId().getSeqp());
		for(MbcCedenciaSalaHcpa ced : cedencias) {
			PortalPlanejamentoCirurgiasReservaVO reservaVO = new PortalPlanejamentoCirurgiasReservaVO();
			reservaVO.setEquipeAgenda(ced.getMbcProfAtuaUnidCirgs());
			reservaVO.setCedencia(true);
			popularReserva(data, listaReservasVO, null, ced.getId().hashCode(),
					ced.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg().getHorarioInicial(),
					ced.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg().getHorarioFinal(),
					ced.getId().getPucSerMatricula(),
					ced.getId().getPucSerVinCodigo(),
					ced.getId().getPucUnfSeq(),
					ced.getId().getPucIndFuncaoProf(), reservaVO);
		}
		if(listaReservasVO.isEmpty()) {
			List<MbcCaractSalaEsp> reservas = getMbcCaractSalaEspDAO().pesquisarReservasPorDataEquipeEspUnidSalaTurno(data,especialidadeFiltro,unidadeFiltro,sala,turno);
			for(MbcCaractSalaEsp reserva : reservas){
				List<MbcSubstEscalaSala> listaSubstEscalaSala = getMbcSubstEscalaSalaDAO().pesquisarCedenciaSalaPorDataCasSeqEspSeqSeqp(data, reserva.getId().getCasSeq(), reserva.getId().getEspSeq(), reserva.getId().getSeqp());
				List<MbcBloqSalaCirurgica> listaBloqueioSala = getMbcBloqSalaCirurgicaDAO().pesquisarBloqueioSala(sala, data);
				String turnoSala = turno.getMbcTurnos().getTurno().toString();
				DominioDiaSemanaSigla diaSigla = DominioDiaSemanaSigla.valueOf(DateFormatUtil.diaDaSemana(data));
				Boolean bloqueio = false;
		        Date horaInicio;
				Date horaFim;
				if(reserva.getHoraInicioEquipe() != null && reserva.getHoraFimEquipe() != null) {
					horaInicio = reserva.getHoraInicioEquipe();
					horaFim = reserva.getHoraFimEquipe();
				} else {
					horaInicio = reserva.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg().getHorarioInicial();
					horaFim = reserva.getMbcCaracteristicaSalaCirg().getMbcHorarioTurnoCirg().getHorarioFinal();
				}
				if(equipeFiltro != null) {
					if(!listaSubstEscalaSala.isEmpty()){
						for(MbcSubstEscalaSala ced : listaSubstEscalaSala) {
							if(ced.getMbcProfAtuaUnidCirgs().equals(equipeFiltro)
									|| equipeFiltro.equals(reserva.getMbcProfAtuaUnidCirgs())) {
								PortalPlanejamentoCirurgiasReservaVO reservaVO = new PortalPlanejamentoCirurgiasReservaVO();
								reservaVO.setEquipeAgenda(ced.getMbcProfAtuaUnidCirgs());
								reservaVO.setCedencia(true);
								bloqueio = getPortalPlanejamentoCirurgiaON().verificarBloqueioSala(turnoSala,diaSigla, ced.getMbcProfAtuaUnidCirgs(), listaBloqueioSala);
								reservaVO.setBloqueio(bloqueio);
								popularReserva(data, listaReservasVO, null, ced.getId().hashCode(),
										horaInicio, horaFim, ced.getMbcProfAtuaUnidCirgs().getId().getSerMatricula(),
										ced.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo(), 
										ced.getMbcProfAtuaUnidCirgs().getId().getUnfSeq(),
										ced.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf(), reservaVO);
							}
						}
					} else if(equipeFiltro.equals(reserva.getMbcProfAtuaUnidCirgs())){
						PortalPlanejamentoCirurgiasReservaVO reservaVO = new PortalPlanejamentoCirurgiasReservaVO();
						reservaVO.setEquipeAgenda(reserva.getMbcProfAtuaUnidCirgs());
						reservaVO.setCedencia(false);
						bloqueio = getPortalPlanejamentoCirurgiaON().verificarBloqueioSala(turnoSala,diaSigla, reserva.getMbcProfAtuaUnidCirgs(), listaBloqueioSala);
						reservaVO.setBloqueio(bloqueio);
						
						popularReserva(data, listaReservasVO, reserva.getAghEspecialidades(), reserva.getId().hashCode(),
								horaInicio, horaFim, reserva.getMbcProfAtuaUnidCirgs().getId().getSerMatricula(),
								reserva.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo(), 
								reserva.getMbcProfAtuaUnidCirgs().getId().getUnfSeq(),
								reserva.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf(), reservaVO);
					} else{
						continue;
					}
				} else{
					if(!listaSubstEscalaSala.isEmpty()) {
						for(MbcSubstEscalaSala ced : listaSubstEscalaSala) {
							PortalPlanejamentoCirurgiasReservaVO reservaVO = new PortalPlanejamentoCirurgiasReservaVO();
							reservaVO.setEquipeAgenda(ced.getMbcProfAtuaUnidCirgs());
							reservaVO.setCedencia(true);
							bloqueio = getPortalPlanejamentoCirurgiaON().verificarBloqueioSala(turnoSala,diaSigla, ced.getMbcProfAtuaUnidCirgs(), listaBloqueioSala);
							reservaVO.setBloqueio(bloqueio);
							popularReserva(data, listaReservasVO, null, ced.getId().hashCode(),
									horaInicio, horaFim, ced.getMbcProfAtuaUnidCirgs().getId().getSerMatricula(),
									ced.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo(), 
									ced.getMbcProfAtuaUnidCirgs().getId().getUnfSeq(),
									ced.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf(), reservaVO);
						}
					} else {
						PortalPlanejamentoCirurgiasReservaVO reservaVO = new PortalPlanejamentoCirurgiasReservaVO();
						reservaVO.setEquipeAgenda(reserva.getMbcProfAtuaUnidCirgs());
						reservaVO.setCedencia(false);
						bloqueio = getPortalPlanejamentoCirurgiaON().verificarBloqueioSala(turnoSala,diaSigla, reserva.getMbcProfAtuaUnidCirgs(), listaBloqueioSala);
						reservaVO.setBloqueio(bloqueio);
						popularReserva(data, listaReservasVO, reserva.getAghEspecialidades(), reserva.getId().hashCode(),
								horaInicio, horaFim, reserva.getMbcProfAtuaUnidCirgs().getId().getSerMatricula(),
								reserva.getMbcProfAtuaUnidCirgs().getId().getSerVinCodigo(), 
								reserva.getMbcProfAtuaUnidCirgs().getId().getUnfSeq(),
								reserva.getMbcProfAtuaUnidCirgs().getId().getIndFuncaoProf(), reservaVO);
					}
				}
			}
		}
		return listaReservasVO;
	}

	private void popularReserva(Date data,
			List<PortalPlanejamentoCirurgiasReservaVO> listaReservasVO,
			AghEspecialidades especialidade, Integer seqpVO, Date horaInicio, Date horaFim,
			Integer matricula, Short vinCodigo, Short unfSeq, DominioFuncaoProfissional indFuncaoProf,
			PortalPlanejamentoCirurgiasReservaVO reservaVO) {
		reservaVO.setSeqpVO(seqpVO);
		reservaVO.setEquipe(reservaVO.getEquipeAgenda().getRapServidores().getPessoaFisica().getNome());
		if(!reservaVO.getCedencia() && especialidade != null) { //quando for cedencia, a especialidade dos agendamentos será selecionada na tela de inclusao de agendamento
			reservaVO.setEspecialidade(especialidade.getNomeEspecialidade());
			reservaVO.setEspecialidadeAgenda(especialidade);
		}
		reservaVO.setHoraInicial(DateUtil.truncaHorario(horaInicio));
		reservaVO.setHoraFinal(DateUtil.truncaHorario(horaFim));
		MbcAgendaAnotacao agendaAnotacao = getMbcAgendaAnotacaoDAO().obterPorChavePrimaria(new MbcAgendaAnotacaoId(data, matricula, vinCodigo, unfSeq, indFuncaoProf));
		if(agendaAnotacao != null){
			reservaVO.setAnotacao(agendaAnotacao.getDescricao());
		}
		listaReservasVO.add(reservaVO);
	}

	/**
	 * Montar as agendas do turno, seus status e atributos, baseados nos filtros, se informados.
	 * @param turnoVO
	 * @param turno
	 * @param data
	 * @param equipeFiltro
	 * @param especialidadeFiltro
	 * @param unidadeFiltro
	 * @param sala
	 * @param countTurno
	 * @param turnos
	 * @return
	 */
	private List<PortalPlanejamentoCirurgiasAgendaVO> montarAgendas(
			PortalPlanejamentoCirurgiasTurno2VO turnoVO, MbcHorarioTurnoCirg turno, Date data,
			MbcProfAtuaUnidCirgs equipeFiltro,
			AghEspecialidades especialidadeFiltro, Short unidadeFiltro,
			MbcSalaCirurgica sala, Integer countTurno, List<MbcHorarioTurnoCirg> turnos,
			List<PortalPlanejamentoCirurgiasTurno2VO> listaTurnosVO) {
		List<PortalPlanejamentoCirurgiasAgendaVO> listaAgendasVO = new ArrayList<PortalPlanejamentoCirurgiasAgendaVO>();
		List<MbcAgendas> agendas = getMbcAgendasDAO().buscarAgendasPorUnfSeqSalaData(turno,sala,unidadeFiltro,data);
		MbcControleEscalaCirurgica controleEscala = getMbcControleEscalaCirurgicaDAO().obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
				unidadeFiltro, data, DominioTipoEscala.D);
		Date horarioOcupado = new Date();
		horarioOcupado = DateUtil.truncaData(horarioOcupado);
		for(MbcAgendas agenda : agendas){
			//só irá criar agenda na lista de agendas(e calcular a ocupação e percentagem do turno) sem dtrhPrevInicio e fim (overbooking), caso no próximo turno (se existir) 
			//não exista reserva para a equipe da mesma agenda e no turno atual exista.
			if((agenda.getDthrPrevInicio() != null && agenda.getDthrPrevFim() != null) || 
					(countTurno+1 < turnos.size() && montarReservas(turnos.get(countTurno+1),data,agenda.getProfAtuaUnidCirgs(),especialidadeFiltro,unidadeFiltro,sala).isEmpty() && !montarReservas(turnos.get(countTurno),data,agenda.getProfAtuaUnidCirgs(),especialidadeFiltro,unidadeFiltro,sala).isEmpty())
					|| (agenda.getDthrPrevInicio() == null && agenda.getDthrPrevFim() == null && 
							getPortalPlanejamentoCirurgiaON().isUltimoTurnoQueEquipePossuiReservaNoDia(agenda.getProfAtuaUnidCirgs(), turnoVO, data, agenda.getEspecialidade().getSeq(), unidadeFiltro, sala, listaTurnosVO, agenda.getSeq()))) { 
				PortalPlanejamentoCirurgiasAgendaVO agendaVO = setarAgendaVO(agenda,turno,unidadeFiltro);			
				
				listaAgendasVO.add(agendaVO);
				horarioOcupado = calcularHorarioOcupado(horarioOcupado, agendaVO,agenda, controleEscala);
			}
			
		}
		setarHorarioOcupado(turnoVO, horarioOcupado);
		return listaAgendasVO;
	}
	
	private DominioRegimeProcedimentoCirurgicoSus obterDominioRegimeProcedimentoCirurgicoSus(String codigo) {
		for(DominioRegimeProcedimentoCirurgicoSus dominio : DominioRegimeProcedimentoCirurgicoSus.values()) {
			if(dominio.getCodigo().equals(codigo)) {
				return dominio;
			}
		}
		return null;
	}

	/**
	 * Monta o VO de agenda, baseado nos parametros, setando seus status.
	 * @param agenda
	 * @param turno
	 * @param unidadeFiltro
	 * @return
	 */
	public void ajustarAgendaVO(PortalPlanejamentoCirurgiasAgendaVO agendaVO, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas, 
			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadasComDesc, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasPlanejadas) {
		String nomePaciente = getPortalPlanejamentoCirurgiasRN().obterNomeIntermediarioPacienteAbreviado(WordUtils.capitalizeFully(agendaVO.getNome()));
		Integer prontuario = agendaVO.getProntuario();
		agendaVO.setPaciente(CoreUtil.formataProntuario(prontuario)+_HIFEN_+nomePaciente);

		String regime = WordUtils.capitalize(obterDominioRegimeProcedimentoCirurgicoSus(agendaVO.getRegime()).getDescricao());
		String nomeProcedimento = agendaVO.getProcedimento();
		
		Date tempoDuracao = portalPlanejamentoCirurgia3ON.processarSituacaoAgenda(agendaVO, cirurgiasRealizadas, cirurgiasRealizadasComDesc, cirurgiasPlanejadas);
		String tempoDuracaoCirurgia;
		if(tempoDuracao == null) {
			Integer horas = Integer.valueOf(agendaVO.getTempoSala()) / 100;
			Integer minutos = Integer.valueOf(agendaVO.getTempoSala()) % 100;
			tempoDuracaoCirurgia = new String(String.format("%02d", horas) + ":" + String.format("%02d", minutos));
		}
		else {
			tempoDuracaoCirurgia = DateUtil.dataToString(tempoDuracao, "HH:mm");
		}
		
		agendaVO.setProcedimento(regime+_HIFEN_+nomeProcedimento+_HIFEN_+tempoDuracaoCirurgia);
	}

	/**
	 * Monta o VO de agenda, baseado nos parametros, setando seus status.
	 * @param agenda
	 * @param turno
	 * @param unidadeFiltro
	 * @return
	 */
	public PortalPlanejamentoCirurgiasAgendaVO setarAgendaVO(MbcAgendas agenda, MbcHorarioTurnoCirg turno, Short unidadeFiltro) {
		PortalPlanejamentoCirurgiasAgendaVO agendaVO = new PortalPlanejamentoCirurgiasAgendaVO();
		agendaVO.setSeqAgenda(agenda.getSeq());
		String nomePaciente = agenda.getPaciente().getNome();
		nomePaciente = getPortalPlanejamentoCirurgiasRN().obterNomeIntermediarioPacienteAbreviado(WordUtils.capitalizeFully(nomePaciente));
		Integer prontuario = agenda.getPaciente().getProntuario();
		agendaVO.setPaciente(CoreUtil.formataProntuario(prontuario)+_HIFEN_+nomePaciente);
		String nomeRespEquipe = null;
		//quando tiver varios agendamentos para a mesma equipe pode acontecer o lazy
		//o código abaixo obriga a ir na base buscar a equipe.
		// solução bug #31793
		if(agenda.getProfAtuaUnidCirgs()!=null && agenda.getProfAtuaUnidCirgs().getId()!=null){
			getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(agenda.getProfAtuaUnidCirgs().getId());
			nomeRespEquipe = agenda.getProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome();
		}
		agendaVO.setEquipe(nomeRespEquipe);
		agendaVO.setEspecialidade(agenda.getEspecialidade().getNomeEspecialidade());
		
		//Criado EM e por
		agendaVO.setCriadoEm(agenda.getDthrInclusao());
		agendaVO.setCriadoPor(agenda.getServidor().getPessoaFisica().getNome());
		if(agenda.getDthrPrevInicio() != null && agenda.getDthrPrevFim() != null){ 
			if(calcularTempoEmMinutos(agenda.getDthrPrevInicio(), true) < calcularTempoEmMinutos(turno.getHorarioInicial(), true)){ //se começar antes do turno
				agendaVO.setHoraInicial(turno.getHorarioInicial());
			}else{
				agendaVO.setHoraInicial(agenda.getDthrPrevInicio());
			}
			//calcular data final com intervalo de escala
			Byte intervaloEscala = agenda.getIntervaloEscala() == null ? buscarIntervaloAgendamento(unidadeFiltro, agenda.getProcedimentoCirurgico().getTipo()) : agenda.getIntervaloEscala();
			agendaVO.setHoraFinal(DateUtil.adicionaMinutos(agenda.getDthrPrevFim(), intervaloEscala));
			if(calcularTempoEmMinutos(agendaVO.getHoraFinal(), true) > calcularTempoEmMinutos(turno.getHorarioFinal(), true)
					&& calcularTempoEmMinutos(turno.getHorarioFinal(), true) != 0){
				agendaVO.setHoraFinal(turno.getHorarioFinal());
			}
		}
		String regime = WordUtils.capitalize(agenda.getRegime().getDescricao());
		String nomeProcedimento = agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getDescricao();
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date dataDescrProc = agenda.getTempoSala();
		Integer tempoCirurgiaRealizada = 0;
		if((tempoCirurgiaRealizada = getVerificarCirurgiaRealizada(agenda)) > 0){
			dataDescrProc = calcularMinutosEmDate(tempoCirurgiaRealizada);
			agendaVO.setRealizada(true);
		} else {
			if(agenda.getDthrPrevInicio() == null && agenda.getDthrPrevFim() == null){
				agendaVO.setOverbooking(true);
			}else if(agenda.getIndGeradoSistema()){
				agendaVO.setEscala(true);
			}else{
				List<MbcCirurgias> cirgs = getMbcCirurgiasDAO().buscarCirurgiaPorAgendamentoSemMotivoCancelamento(agenda.getSeq(), agenda.getDtAgenda());
				if(!cirgs.isEmpty()){
					agendaVO.setEscala(true);
				}else{
					agendaVO.setPlanejado(true);
				}
			}
		}
		
		agendaVO.setProcedimento(regime+_HIFEN_+nomeProcedimento+_HIFEN_+dateFormat.format(dataDescrProc));
		agendaVO.setIndGeradoSistema(agenda.getIndGeradoSistema());
		return agendaVO;
	}

	/**
	 * Calcular o horario ocupado do turno, baseado nas agendas.
	 * @param horarioOcupado
	 * @param agendaVO
	 * @param agenda
	 * @return
	 */
	private Date calcularHorarioOcupado(Date horarioOcupado, PortalPlanejamentoCirurgiasAgendaVO agendaVO, MbcAgendas agenda,
			MbcControleEscalaCirurgica controleEscala) {
		Date tempoSala = null;
		// Modificação decorrente da estória #28691
		if(controleEscala == null || (controleEscala != null && DominioSituacaoAgendas.ES.equals(agenda.getIndSituacao()))) {
			if(agendaVO.getHoraFinal() != null && agendaVO.getHoraInicial() != null){
				Integer diferencaMinutos = calcularTempoEmMinutos(agendaVO.getHoraFinal(), true) - calcularTempoEmMinutos(agendaVO.getHoraInicial(), true);
				tempoSala = DateUtil.adicionaMinutos(DateUtil.truncaData(new Date()) , diferencaMinutos);
			}else{
				tempoSala = DateUtil.adicionaMinutos(agenda.getTempoSala(), agenda.getIntervaloEscala());
			}
			Calendar cal = Calendar.getInstance(); 
			cal.setTime(tempoSala);			
			horarioOcupado = DateUtil.adicionaHoras(horarioOcupado, cal.get(Calendar.HOUR_OF_DAY));
			horarioOcupado = DateUtil.adicionaMinutos(horarioOcupado, cal.get(Calendar.MINUTE));
		}
		return horarioOcupado;
		
	}
	
	/**
	 * Setar o horario ocupado
	 * @param turnoVO
	 * @param tempoOcupado
	 */
	private void setarHorarioOcupado(
			PortalPlanejamentoCirurgiasTurno2VO turnoVO, Date tempoOcupado) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(tempoOcupado);
		turnoVO.setHorarioOcupado(DateUtil.dataToString(tempoOcupado, "HH:mm"));		
		getCalcularPercentualOcupacao(turnoVO,cal);
	}
	
	/**
	 * Calcular o percentual de ocupação do turno, baseado no horario total do turno em razão do tempo de ocupação.
	 * @param turnoVO
	 * @param tempoOcupado
	 */
	private void getCalcularPercentualOcupacao(
			PortalPlanejamentoCirurgiasTurno2VO turnoVO, Calendar tempoOcupado) {
		Date tempoTotal = turnoVO.getHorarioTotal();		
		Calendar tempoTotalCal = Calendar.getInstance();
		tempoTotalCal.setTime(tempoTotal);
		Integer minutosTempoTotal = tempoTotalCal.get(Calendar.HOUR_OF_DAY)*60;
		minutosTempoTotal += tempoTotalCal.get(Calendar.MINUTE);
		
		Integer minutosTempoOcupado = tempoOcupado.get(Calendar.HOUR_OF_DAY)*60;
		minutosTempoOcupado += tempoOcupado.get(Calendar.MINUTE);
		
		Double percentualOcupacao = (100.0*minutosTempoOcupado) / minutosTempoTotal;
		if(percentualOcupacao > 100.0){
			percentualOcupacao = 100.0;
			turnoVO.setOverbooking(Boolean.TRUE);
		}else{
			turnoVO.setOverbooking(false);
		}
		turnoVO.setPorcentagem(percentualOcupacao);
		
	}
	

	/**
	 * Verificar se cirurgia já foi realizada, retornando o tempo de realização.
	 * @param agenda
	 * @return
	 */
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

	private Date ajustarData(Date data) {
		if(data != null) {
			Calendar cal = Calendar.getInstance();
			Calendar atual = Calendar.getInstance();
			cal.setTime(data);
			cal.set(Calendar.DAY_OF_MONTH, atual.get(Calendar.DAY_OF_MONTH));
			cal.set(Calendar.MONTH, atual.get(Calendar.MONTH));
			cal.set(Calendar.YEAR, atual.get(Calendar.YEAR));
			
			return cal.getTime();
		}
		
		return data;
	}

	/**
	 * Calcula a diferença em minutos de uma data/hora final e inicial.
	 * @param dataFinal
	 * @param dataInicial
	 * @return
	 */
	public Integer getDiferencaEmMinutos(Date dataFinal, Date dataInicial){		
        return calcularTempoEmMinutos(dataFinal, false) - calcularTempoEmMinutos(dataInicial, true);
    }

	private Integer calcularTempoEmMinutos(Date date, Boolean retornaZero) {
		Integer retorno;
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		retorno = cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
		
		if(!retornaZero && retorno == 0){
			cal.set(Calendar.HOUR_OF_DAY, 23);  
			cal.set(Calendar.MINUTE, 59);  
			
			return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
		}
			
		return retorno;
	}

	/**
	 * Obtem o dia de semana onde a equipe de determinada especialidade atenda.
	 * 
	 * @ORADB c_dia_valido
	 * @param dataBase
	 * @param atuaUnidCirgs 
	 * @param unfSeq 
	 * @param espSeq 
	 * @return
	 */
	public DominioDiaSemana obterDiaSemana(Date dataBase, AghEspecialidades especialidade, Short unfSeq, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short sciSeqp, Boolean reverse) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(dataBase);
		Integer dataBaseDiaSemana = cal.get(Calendar.DAY_OF_WEEK);
		DominioDiaSemana diaAnterior = null;
		List<DominioDiaSemana> listaDiaSemana = null;
		if(especialidade == null && atuaUnidCirgs == null){
			listaDiaSemana = getMbcCaracteristicaSalaCirgDAO().pesquisarDiasSemana(unfSeq, sciSeqp, reverse);
		}else{
			listaDiaSemana = getMbcCaracteristicaSalaCirgDAO().pesquisarDiasSemana(atuaUnidCirgs, unfSeq, especialidade, sciSeqp, reverse);
		}
		
		for(DominioDiaSemana diaSemana : listaDiaSemana){
			if((diaSemana.ordinal() >= (dataBaseDiaSemana-1) && !reverse) || (diaSemana.ordinal() <= (dataBaseDiaSemana-1) && reverse)){
				return diaSemana;
			}else if(diaAnterior == null){
				diaAnterior = diaSemana;
			}
		}
		return diaAnterior;
	}
	
	/**
	 * Verifica se determinada data é feriado. Se for, verifica se, apesar de feriado, existe algum turno onde alguma equipe atenda. Se existir, retorna que
	 * não é feriado.
	 * 
	 * @ORADB c_ver_feriado
	 * @param dataFinal
	 * @param espSeq 
	 * @param unfSeq 
	 * @param atuaUnidCirgs 
	 * @return
	 */
	public Boolean verificarFeriado(Date dataFinal, MbcProfAtuaUnidCirgs atuaUnidCirgs, Short unfSeq, AghEspecialidades especialidade, Short sciSeqp) {
		AghFeriados aghFeriados = getAghuFacade().obterFeriado(DateUtil.truncaData(dataFinal));
		if(aghFeriados != null && aghFeriados.getTurno() == null){
			return Boolean.TRUE;
		}else if(aghFeriados != null && aghFeriados.getTurno() != null){
			List<MbcCaracteristicaSalaCirg> caractSalas = getMbcCaracteristicaSalaCirgDAO().pesquisarDiasSemanaPorTurno(dataFinal, atuaUnidCirgs, unfSeq, especialidade, sciSeqp);

			for(MbcCaracteristicaSalaCirg caract : caractSalas ){
				if(!(aghFeriados.getTurno().toString().equals(caract.getMbcTurnos().getTurno()))){
					return Boolean.FALSE;
				}
			}
		}
		return Boolean.FALSE;
	}

	/**
	 * Avança ou retorna um dia à data, baseado no parametro.
	 * @param data
	 * @param reverse
	 * @return
	 */
	private Date adicionarDiasTruncado(Date data, Boolean reverse) {
		if(reverse) {
			return DateUtil.truncaData(DateUtil.adicionaDias(data, -1));
		} else {
			return DateUtil.truncaData(DateUtil.adicionaDias(data, 1));
		}
	}
	
	/**
	 * Monta as colunas de dias do calendario, além de incrementar as posições de dias no array.
	 * 
	 * @param vo
	 * @param contDias
	 * @param data
	 * @return
	 */
	private Integer montarVODias(PortalPlanejamentoCirurgiasDiaVO vo,
			Integer contDias, Date data) {
		Date[] dates = vo.getDatasAgendaDate();
		dates[contDias] = data;
		vo.setDatasAgendaDate(dates);
		
		String[] datasAux = vo.getDatasAgenda();
		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		StringBuffer sb = new StringBuffer();
		SimpleDateFormat df = new SimpleDateFormat();
		df.applyPattern("dd/MM/yyyy");
		sb.append(df.format(cal.getTime())).append(" (").append(DominioDiaSemanaSigla.getDiaSemanaSigla(cal.get(Calendar.DAY_OF_WEEK))).append(')');
		datasAux[contDias++] = sb.toString();
		vo.setDatasAgenda(datasAux);
		return contDias;
	}	

	/**
	 * Retorna um objeto Date, baseado nos minutos informados.
	 * @param minutos
	 * @return
	 */
	public Date calcularMinutosEmDate(Integer minutos) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.truncaData(new Date()));
		cal.set(Calendar.HOUR_OF_DAY, (minutos/60));
		cal.set(Calendar.MINUTE, (minutos%60));
		return cal.getTime();
	}
	
	/**
	 * Buscar o intervalo de agendamento, baseado nos parametros informados.
	 * @param unfSeq
	 * @param tipo
	 * @return
	 */
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
	
	protected IAghuFacade getAghuFacade(){
		return this.iAghuFacade;
	}
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO(){
		return mbcSalaCirurgicaDAO;
	}
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	protected MbcSubstEscalaSalaDAO getMbcSubstEscalaSalaDAO(){
		return mbcSubstEscalaSalaDAO;
	}
	protected MbcBloqSalaCirurgicaDAO getMbcBloqSalaCirurgicaDAO(){
		return mbcBloqSalaCirurgicaDAO;
	}
	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO(){
		return mbcHorarioTurnoCirgDAO;
	}
	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO(){
		return mbcControleEscalaCirurgicaDAO;
	}
	protected RelatorioPortalPlanejamentoCirurgiasRN getPortalPlanejamentoCirurgiasRN(){
		return relatorioPortalPlanejamentoCirurgiasRN;
	}
	protected MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}
	protected MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}
	protected MbcCedenciaSalaHcpaDAO getMbcCedenciaSalaHcpaDAO(){
		return mbcCedenciaSalaHcpaDAO;
	}
	protected MbcAgendaAnotacaoDAO getMbcAgendaAnotacaoDAO(){
		return mbcAgendaAnotacaoDAO;
	}
	protected PortalPlanejamentoCirurgiaON getPortalPlanejamentoCirurgiaON(){
		return portalPlanejamentoCirurgiaON;
	}
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
}