package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnotacaoDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendamentoVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasReservaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasSalaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasTurno2VO;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MbcAgendaAnotacao;
import br.gov.mec.aghu.model.MbcAgendaAnotacaoId;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PortalPlanejamentoCirurgia3ON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PortalPlanejamentoCirurgia3ON.class);

	@Inject
	private MbcAgendaAnotacaoDAO mbcAgendaAnotacaoDAO;

	@EJB
	private PortalPlanejamentoCirurgia2ON portalPlanejamentoCirurgia2ON;

	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public Boolean isRemoverAgenda(List<PortalPlanejamentoCirurgiasAgendaVO> agendas, PortalPlanejamentoCirurgiasAgendaVO agendaValidar, List<Integer> listaAgendamentosProcessados) {
		if(agendaValidar.getInicioAgenda() == null && agendaValidar.getFimAgenda() == null) {
			for(PortalPlanejamentoCirurgiasAgendaVO agenda : agendas) {
				Boolean retorno = verificaTurnoRemoveAgenda(agenda, agendaValidar);
				if(retorno != null) {
					return retorno;
				}
			}
		}
		return false;
	}
	
	private Boolean verificaTurnoRemoveAgenda(PortalPlanejamentoCirurgiasAgendaVO agenda, PortalPlanejamentoCirurgiasAgendaVO agendaValidar) {
		if(DateUtil.isDatasIguais(DateUtil.truncaData(agendaValidar.getDataAgenda()), DateUtil.truncaData(agenda.getDataAgenda())) && agendaValidar.getSala().equals(agenda.getSala())) {
			if(agendaValidar.getTurno().equalsIgnoreCase(DominioTurno.M.toString())) {
				if(agenda.getTurno().equalsIgnoreCase(DominioTurno.T.toString()) || agenda.getTurno().equalsIgnoreCase(DominioTurno.N.toString())) {
					if((agenda.getEquipe() != null && agendaValidar.getEquipe() != null && agenda.getEquipe().equals(agendaValidar.getEquipe()))
							&& agenda.getInicioAgenda() != null && agenda.getFimAgenda() != null) {
						return true;
					}
				}
			}
			else if(agendaValidar.getTurno().equalsIgnoreCase(DominioTurno.T.toString())) {
				if(agenda.getTurno().equalsIgnoreCase(DominioTurno.N.toString())) {
					if((agenda.getEquipe() != null && agendaValidar.getEquipe() != null && agenda.getEquipe().equals(agendaValidar.getEquipe()))
							&& agenda.getInicioAgenda() != null && agenda.getFimAgenda() != null) {
						return true;
					}
				}
			}
			else if(agendaValidar.getTurno().equalsIgnoreCase(DominioTurno.N.toString())) {
				return false;
			}
			else {
				if(agenda.getTurno().equalsIgnoreCase(DominioTurno.M.toString()) || agenda.getTurno().equalsIgnoreCase(DominioTurno.T.toString()) || agenda.getTurno().equalsIgnoreCase(DominioTurno.N.toString())) {
					if((agenda.getEquipe() != null && agendaValidar.getEquipe() != null && agenda.getEquipe().equals(agendaValidar.getEquipe()))
							&& agenda.getInicioAgenda() != null && agenda.getFimAgenda() != null) {
						return true;
					}
				}
			}
		}
		return null;
	}

	public Boolean isRemoverAgendamento(PortalPlanejamentoCirurgiasAgendaVO agenda, List<PortalPlanejamentoCirurgiasAgendamentoVO> agendamenstosPorDia, List<Integer> listaAgendamentosProcessados) {
		if(agenda.getInicioAgenda() == null && agenda.getFimAgenda() == null) {
			for(PortalPlanejamentoCirurgiasAgendamentoVO agendamento : agendamenstosPorDia) {
				 if(StringUtils.stripAccents(DateUtil.dataToString(agenda.getDataAgenda(), "EE")).equalsIgnoreCase(agendamento.getDiaSemana().getDescricao())) {
					if(agenda.getTurno().equalsIgnoreCase(DominioTurno.M.toString())) {
						if(agendamento.getTurno().equalsIgnoreCase(DominioTurno.T.toString()) || agendamento.getTurno().equalsIgnoreCase(DominioTurno.N.toString())) {
							if((agenda.getEquipe() != null && agendamento.getEquipe() != null && agenda.getEquipe().equals(agendamento.getEquipe()))) {
								return true;
							}
						}
					}
					else if(agenda.getTurno().equalsIgnoreCase(DominioTurno.T.toString())) {
						if(agendamento.getTurno().equalsIgnoreCase(DominioTurno.N.toString())) {
							if((agenda.getEquipe() != null && agendamento.getEquipe() != null && agenda.getEquipe().equals(agendamento.getEquipe()))) {
								return true;
							}
						}
					}
					else if(agenda.getTurno().equalsIgnoreCase(DominioTurno.N.toString())) {
						return false;
					}
					else {
						if(agendamento.getTurno().equalsIgnoreCase(DominioTurno.M.toString()) || agendamento.getTurno().equalsIgnoreCase(DominioTurno.T.toString()) || agendamento.getTurno().equalsIgnoreCase(DominioTurno.N.toString())) {
							if((agenda.getEquipe() != null && agendamento.getEquipe() != null && agenda.getEquipe().equals(agendamento.getEquipe()))) {
								return true;
							}
						}
					}
				 }
			}
		}
		
		return false;
	}
	
	public void ordenarSalasEAdiconarReservas(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO, List<PortalPlanejamentoCirurgiasAgendamentoVO> agendamenstosPorDia, List<Date> datas, Short unidadeFiltro) {
		Integer seqVO = obterMaxSeqReserva(listaSalasVO);
		
		for(PortalPlanejamentoCirurgiasSalaVO sala : listaSalasVO) {
			if(sala.getListaTurnos2() != null && !sala.getListaTurnos2().isEmpty()) {
				
				adicionarReservas(sala, agendamenstosPorDia, datas, unidadeFiltro, seqVO);
				
				for(PortalPlanejamentoCirurgiasTurno2VO turno : sala.getListaTurnos2()) {
					if(turno.getListaAgendas() != null && !turno.getListaAgendas().isEmpty()) {
						ComparatorChain sorter = new ComparatorChain();
						sorter.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendaVO.Fields.DATA.toString()));
						sorter.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendaVO.Fields.HORA_INICIAL.toString(), new NullComparator(false)));
						sorter.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasAgendaVO.Fields.EQUIPE.toString()));
				        Collections.sort(turno.getListaAgendas(), sorter);
					}
				}
				ComparatorChain sorter = new ComparatorChain();
				sorter.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasTurno2VO.Fields.DIA.toString()));
				sorter.addComparator(new BeanComparator(PortalPlanejamentoCirurgiasTurno2VO.Fields.HORA_INICIAL_TURNO.toString()));
		        Collections.sort(sala.getListaTurnos2(), sorter);
			}
		}

		BeanComparator ordemComparator = new BeanComparator(PortalPlanejamentoCirurgiasSalaVO.Fields.SALA.toString());
        Collections.sort(listaSalasVO, ordemComparator);
	}
	
	private void adicionarReservas(PortalPlanejamentoCirurgiasSalaVO sala, List<PortalPlanejamentoCirurgiasAgendamentoVO> agendamenstosPorDia, List<Date> datas, Short unidade, Integer seqVO) {
		for(PortalPlanejamentoCirurgiasAgendamentoVO agendaDia : agendamenstosPorDia) {
			if(!agendaDia.getSala().equals(sala.getNumeroSala())) {
				continue;
			}
			
			List<Integer> diasSemana = obterIndiceDiaSemana(agendaDia.getDiaSemana(), datas);
			for(Integer dia : diasSemana) {
				int indiceTurno = sala.getListaTurnos2().indexOf(new PortalPlanejamentoCirurgiasTurno2VO(dia, agendaDia.getTurno()));
				if(indiceTurno != -1){
					PortalPlanejamentoCirurgiasTurno2VO turno = sala.getListaTurnos2().get(indiceTurno);
					PortalPlanejamentoCirurgiasReservaVO reserva = new PortalPlanejamentoCirurgiasReservaVO();
					MbcProfAtuaUnidCirgs profissional = new MbcProfAtuaUnidCirgs();
					profissional.setId(new MbcProfAtuaUnidCirgsId(agendaDia.getMatricula(), agendaDia.getVinCodigo(), unidade, agendaDia.getIndFuncaoProf()));
					reserva.setEquipe(agendaDia.getEquipe());
					reserva.setEquipeAgenda(profissional);
					reserva.setEspSeq(agendaDia.getEspSeq());
					reserva.setEspecialidade(agendaDia.getEspecialidade());
					reserva.setHoraInicial(agendaDia.getDataInicio() == null ? turno.getHorarioInicialTurno() : agendaDia.getDataInicio());
					reserva.setHoraFinal(agendaDia.getDataFim() == null ? turno.getHorarioFinalTurno() : agendaDia.getDataFim());
					reserva.setBloqueio(turno.getBloqueado());
					reserva.setSeqpVO(seqVO++);
		
					MbcAgendaAnotacao agendaAnotacao = getMbcAgendaAnotacaoDAO().obterPorChavePrimaria(new MbcAgendaAnotacaoId((Date)datas.get(dia), 
							agendaDia.getMatricula(), agendaDia.getVinCodigo(), unidade, agendaDia.getIndFuncaoProf()));
					if(agendaAnotacao != null){
						reserva.setAnotacao(agendaAnotacao.getDescricao());
					}
		
					turno.setLivre(true);
					turno.setIndisponivel(false);
					if(!turno.getListaReservas().contains(reserva)) {
						turno.getListaReservas().add(reserva);
					}
				}
			}
		}
		
	}
	
	public Integer obterMaxSeqReserva(List<PortalPlanejamentoCirurgiasSalaVO> listaSalasVO) {
		Integer seqVO = 0;
		for(PortalPlanejamentoCirurgiasSalaVO sala : listaSalasVO) {
			for(PortalPlanejamentoCirurgiasTurno2VO turno : sala.getListaTurnos2()) {
				for(PortalPlanejamentoCirurgiasReservaVO reserva : turno.getListaReservas()) {
					if(seqVO < (reserva.getSeqpVO() == null ? 0 : reserva.getSeqpVO())) {
						seqVO = reserva.getSeqpVO();
					}
				}
			}
		}
		return seqVO;
	}
	
	private Integer obterTempoDuracaoCirurgiaRealizada(List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadasComDesc, 
			PortalPlanejamentoCirurgiasAgendaVO agenda) {
		for(PortalPlanejamentoCirurgiasAgendaVO cirurgia : cirurgiasRealizadas) {
			if(cirurgia.getSeq().equals(agenda.getSeq())) {

				Integer tempoCirgComDescCir = obterTempoDuracaoCirurgiaRealizadaComDescCir(cirurgiasRealizadasComDesc, agenda.getSeq());
				if(tempoCirgComDescCir > 0) {
					return tempoCirgComDescCir + agenda.getIntervaloEscala();
				}
				
				if (cirurgia.getSaidaSala() != null && cirurgia.getEntradaSala() != null) {
					return portalPlanejamentoCirurgia2ON.getDiferencaEmMinutos(cirurgia.getSaidaSala(), cirurgia.getEntradaSala());
				} else {
					return portalPlanejamentoCirurgia2ON.getDiferencaEmMinutos(cirurgia.getFimCirurgia(), cirurgia.getInicioCirurgia());
				}
			}
		}
		return 0;
	}
	
	private Integer obterTempoDuracaoCirurgiaRealizadaComDescCir(List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas, Integer agenda) {
		for(PortalPlanejamentoCirurgiasAgendaVO cirurgia : cirurgiasRealizadas) {
			if(cirurgia.getSeq().equals(agenda)) {
				return portalPlanejamentoCirurgia2ON.getDiferencaEmMinutos(cirurgia.getFimCirurgia(), cirurgia.getInicioCirurgia());
			}
		}
		return 0;
	}

	private Boolean isCirurgiaEscala(List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas, Integer agenda) {
		for(PortalPlanejamentoCirurgiasAgendaVO cirurgia : cirurgiasRealizadas) {
			if(cirurgia.getSeq().equals(agenda)) {
				return true;
			}
		}
		return false;
	}
	
	public Date processarSituacaoAgenda(PortalPlanejamentoCirurgiasAgendaVO agenda, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadas, 
			List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasRealizadasComDesc, List<PortalPlanejamentoCirurgiasAgendaVO> cirurgiasPlanejadas) {
		Integer tempoCirurgiaRealizada = obterTempoDuracaoCirurgiaRealizada(cirurgiasRealizadas, cirurgiasRealizadasComDesc, agenda);
		
		if(agenda.getSala().equals(Short.valueOf("-1"))) {
			agenda.setPlanejado(true);
			return null;
		}
		
		if(tempoCirurgiaRealizada > 0) {
			agenda.setRealizada(true);
			return portalPlanejamentoCirurgia2ON.calcularMinutosEmDate(tempoCirurgiaRealizada); 
		} else {
			if(agenda.getInicioAgenda() == null && agenda.getFimAgenda() == null) {
				agenda.setOverbooking(true);
			} else if (DominioSimNao.valueOf(agenda.getGeradoSistema()).isSim()) {
				agenda.setEscala(true);
			} else if(isCirurgiaEscala(cirurgiasPlanejadas, agenda.getSeq())){
				agenda.setEscala(true);
			} else {
				agenda.setPlanejado(true);
			}
		}
		return null;
	}
	
	private List<Integer> obterIndiceDiaSemana(DominioDiaSemana diaSemana, List<Date> datas) {
		int dia = 0;
		List<Integer> dias = new ArrayList<Integer>();
		for(Date data : datas) {
			if(StringUtils.stripAccents(DateUtil.dataToString(data, "EE")).equalsIgnoreCase(diaSemana.getDescricao())) {
				dias.add(dia);
			}
			dia++;
		}
		return dias;
	}
	
	protected MbcAgendaAnotacaoDAO getMbcAgendaAnotacaoDAO(){
		return mbcAgendaAnotacaoDAO;
	}

}
