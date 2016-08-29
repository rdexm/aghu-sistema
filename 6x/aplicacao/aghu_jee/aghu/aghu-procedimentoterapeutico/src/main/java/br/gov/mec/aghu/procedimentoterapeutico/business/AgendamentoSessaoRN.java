package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptBloqueio;
import br.gov.mec.aghu.model.MptDiaTipoSessao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptAgendamentoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptBloqueioDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptDiaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptHorarioSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptLocalAtendimentoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptPrescricaoPacienteDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTurnoTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentoAmbulatorioVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioAgendamentoAmbulatorioVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorariosAgendaVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorariosAgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PercentualOcupacaoVO;
import br.gov.mec.aghu.protocolos.vo.CadIntervaloTempoVO;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class AgendamentoSessaoRN extends BaseBMTBusiness {

	private static final Log LOG = LogFactory.getLog(AgendamentoSessaoRN.class);
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	public final static int TRANSACTION_TIMEOUT_12_HORAS = 60 * 60 * 12; //= 12 horas
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	@EJB
	private MptAgendamentoSessaoRN mptAgendamentoSessaoRN;
	@EJB
	private MptHorarioSessaoRN mptHorarioSessaoRN;
	@Inject
	private MptBloqueioDAO mptBloqueioDAO;
	@Inject
	private MptDiaTipoSessaoDAO mptDiaTipoSessaoDAO;
	@Inject
	private MptTurnoTipoSessaoDAO mptTurnoTipoSessaoDAO;
	@Inject
	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;
	@Inject
	private MptHorarioSessaoDAO mptHorarioSessaoDAO;
	@Inject
	private MptLocalAtendimentoDAO mptLocalAtendimentoDAO;
	@Inject
	private MptPrescricaoPacienteDAO mptPrescricaoPacienteDAO;
	@Inject
	private MptAgendamentoSessaoDAO mptAgendamentoSessaoDAO;
	
	private static final String DDMMYYYY = "dd/MM/yyyy";
	private static final long serialVersionUID = 3987754454140226597L;
	
	public enum AgendamentoSessaoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_TURNO_NAO_CADASTRADO_AGENDAR_SESSAO, MENSAGEM_ERRO_TIPO_SESSAO_SEM_TURNO,
		MENSAGEM_ERRO_CONVENIO_NAO_ENCONTRADO_AGENDAR_SESSAO, MENSAGEM_AVISO_INFORMAR_HORA_INICIO_MAIOR;
	}
	public List<HorariosAgendamentoSessaoVO> sugerirAgendaSessao(List<CadIntervaloTempoVO> listaHorarios, Short tpsSeq,
			Short salaSeq, Short espSeq, Integer pacCodigo, Integer prescricao, DominioTipoLocal acomodacao, DominioTurno turno,
			Date dataInicio, Date dataFim, Date dthrHoraInicio, String[] diasSelecionados, boolean isSugestao) throws ApplicationBusinessException {
		
		this.beginTransaction(TRANSACTION_TIMEOUT_12_HORAS);
		
		listaHorarios = this.obterListaHorariosFiltrada(listaHorarios);
		
		List<Short> listaAcomodacoes = this.mptLocalAtendimentoDAO.listarLocaisPorSala(salaSeq, acomodacao);
		List<MptTurnoTipoSessao> turnos = this.mptTurnoTipoSessaoDAO.obterTurnosPorTipoSessaoETurno(tpsSeq, turno);
		if (turnos.isEmpty()) {
			if (turno != null) {
				this.commitTransaction();
				throw new ApplicationBusinessException(AgendamentoSessaoRNExceptionCode.MENSAGEM_ERRO_TURNO_NAO_CADASTRADO_AGENDAR_SESSAO);
				
			} else {
				this.commitTransaction();
				throw new ApplicationBusinessException(AgendamentoSessaoRNExceptionCode.MENSAGEM_ERRO_TIPO_SESSAO_SEM_TURNO);
			}
		}
		HorariosAgendaVO horariosAgenda = new HorariosAgendaVO();
		horariosAgenda.setSugestao(isSugestao);
		horariosAgenda.setLogTentativas(new StringBuilder());
		horariosAgenda.setSugestao(isSugestao);
		
		if (this.mptCaracteristicaTipoSessaoDAO.existeCaracteristicaTipoSessao("TUCO", tpsSeq)) {
			horariosAgenda.setTuco(true);
			if (turno != null) {
				horariosAgenda.setFiltroTurno(turnos.get(0));
			}
			// Carrega a lista com todos os turnos para setar o início do turno a partir do filtro informado na tela
			// e o fim do turno a partir dos turnos cadastrados no banco.
			List<MptTurnoTipoSessao> turnosAux = this.mptTurnoTipoSessaoDAO.obterTurnosPorTipoSessaoETurno(tpsSeq, null);
			
			MptTurnoTipoSessao trn = new MptTurnoTipoSessao();
			trn.setHoraInicio(turnos.get(0).getHoraInicio());
			trn.setHoraFim(turnosAux.get(turnosAux.size()-1).getHoraFim());
			
			turnos.clear();
			turnos.add(trn);
		}
		List<MptDiaTipoSessao> diasTipoSessao = this.mptDiaTipoSessaoDAO.obterDiasPorTipoSessao(tpsSeq);
		List<DominioDiaSemana> diasSemana = this.obterListaDiasSemanaEmDominio(diasTipoSessao);
		if (diasSelecionados.length > 0 ) {
			horariosAgenda.setDiasSemanaFiltro(obterListaDiasSemanaFiltrado(diasSelecionados));
		}
		if (DateUtil.isDatasIguais(dataInicio, DateUtil.truncaData(new Date()))) {
			if (dthrHoraInicio != null && DateUtil.validaHoraMaior(new Date(), dthrHoraInicio)) {
				this.commitTransaction();
				throw new ApplicationBusinessException(AgendamentoSessaoRNExceptionCode.MENSAGEM_AVISO_INFORMAR_HORA_INICIO_MAIOR);
			} else if (dthrHoraInicio != null) {
				horariosAgenda.setDthrHoraInicio(dthrHoraInicio);
				dataInicio = DateUtil.comporDiaHora(dataInicio, dthrHoraInicio);
			} else {
				dataInicio = DateUtil.comporDiaHora(dataInicio, new Date());
			}
		} else if (dthrHoraInicio != null) {
			horariosAgenda.setDthrHoraInicio(dthrHoraInicio);
			dataInicio = DateUtil.comporDiaHora(dataInicio, dthrHoraInicio);
		} else {
			dataInicio = DateUtil.comporDiaHora(dataInicio, turnos.get(0).getHoraInicio());
		}
		horariosAgenda.setDataInicioCalculada(dataInicio);
		dataFim = DateUtil.obterDataComHoraFinal(dataFim);
		
		List<HorariosAgendamentoSessaoVO> listaRetorno = this
				.gerarAgendamentoSessao(listaAcomodacoes, listaHorarios, diasSemana, turnos, tpsSeq, salaSeq, espSeq, pacCodigo,
						prescricao, acomodacao, dataFim, dataInicio, horariosAgenda);
		
		this.commitTransaction();
		return listaRetorno;
	}
	
	private List<HorariosAgendamentoSessaoVO> gerarAgendamentoSessao(List<Short> listaAcomodacoes, List<CadIntervaloTempoVO> listaHorarios,
			List<DominioDiaSemana> diasSemana, List<MptTurnoTipoSessao> turnos, Short tpsSeq, Short salaSeq, Short espSeq, Integer pacCodigo, Integer prescricao,
			DominioTipoLocal acomodacao, Date dataFim, Date proximaData, HorariosAgendaVO horariosAgenda) throws ApplicationBusinessException {
		
		horariosAgenda.setListaFinal(new ArrayList<HorariosAgendamentoSessaoVO>());
		horariosAgenda.setGerouLogAmbulatorio(false);
		for (CadIntervaloTempoVO horario : listaHorarios) {
			// Se for D1 ou for chamado da estória #41724 não faz nada.
			if (horario.getDiaReferencia() > 1 && horariosAgenda.isSugestao()) {
				horariosAgenda.setGerouLogAmbulatorio(false);
				proximaData = DateUtil.adicionaDias(proximaData, horario.getDiaReferencia() - horariosAgenda.getDiaRefAnterior());
				proximaData = this.obterProximaDataComHoraInicial(turnos.get(0).getHoraInicio(), proximaData, horariosAgenda);
			}
			horariosAgenda.setDiaRefAnterior(horario.getDiaReferencia());
			if (DateUtil.validaDataMaior(proximaData, dataFim)) {
				horariosAgenda.setListaFinal(new ArrayList<HorariosAgendamentoSessaoVO>(Arrays.asList(
						new HorariosAgendamentoSessaoVO(horariosAgenda.getLogTentativas().toString()))));
				return horariosAgenda.getListaFinal();
			}
			// Se não atende no dia da semana chama o método novamente passando o proximo dia.
			if (!this.atendeNoDiaDaSemana(diasSemana, proximaData, horario, horariosAgenda)) {
				proximaData = DateUtil.adicionaDias(horariosAgenda.getDataInicioCalculada(), 1);
				proximaData = this.obterProximaDataComHoraInicial(turnos.get(0).getHoraInicio(), proximaData, horariosAgenda);
				horariosAgenda.setDataInicioCalculada(proximaData);
				this.gerarAgendamentoSessao(listaAcomodacoes, listaHorarios, diasSemana, 
						turnos, tpsSeq, salaSeq, espSeq, pacCodigo, prescricao, acomodacao, dataFim, proximaData, horariosAgenda);
				break;
			}
			// Zera o menor horário marcado no "D" anterior.
			horariosAgenda.setMenorHorario(null);
			horariosAgenda.setEncontrouMenorHorario(false);
			for (Short loaSeq : listaAcomodacoes) {
				if (!horariosAgenda.isMarcouHorario() || !horariosAgenda.isEncontrouMenorHorario()) {
					if (!horariosAgenda.getListaFinal().isEmpty() && horariosAgenda.getListaFinal().get(0).getLogTentativas() != null) {
						return horariosAgenda.getListaFinal();
					}
					List<AgendamentoSessaoVO> horariosAgendados = this.mptHorarioSessaoDAO
							.obterListaHorariosSessaoMarcados(salaSeq, tpsSeq, loaSeq, acomodacao, DateUtil.truncaData(proximaData),
									DateUtil.obterDataComHoraFinal(proximaData));
					if (!horariosAgendados.isEmpty()) {
						horariosAgenda.setPercorreuDia(false);
						this.realizarAgendamentoEntreHorariosExistentes(turnos, tpsSeq, salaSeq, espSeq, acomodacao, proximaData, dataFim,
								horariosAgenda, horario, loaSeq, horariosAgendados, pacCodigo, prescricao);
						// Caso tenha sido informado o filtro de Horário Inicial, tenta marcar o horário, desde que tenha sido encontrado.
						this.marcarHorario(horariosAgenda);
					} else {
						// Zera o menor horário, pois a acomodação está livre (menor horário garantido)
						horariosAgenda.setMenorHorario(null);
						horariosAgenda.setMarcouHorario(false);
						this.realizarAgendamentoLivreNoDia(turnos, proximaData, dataFim, horariosAgenda, horario, tpsSeq, loaSeq, espSeq, pacCodigo, prescricao);
					}
				} else {
					break;
				}
			}
			// Após percorrer todos os locais, tenta marcar o menor horário encontrado, desde que tenha sido encontrado.
			this.marcarMenorHorario(horariosAgenda);
			
			// Testa se marcou o horário no dia.
			if (!horariosAgenda.isMarcouHorario()) {
				this.popularLogTentativas(horariosAgenda, horario.getDiaReferencia(), proximaData, "Não foi encontrada agenda na sessão terapêutica.", false);
				proximaData = DateUtil.adicionaDias(horariosAgenda.getDataInicioCalculada(), 1);
				proximaData = this.obterProximaDataComHoraInicial(turnos.get(0).getHoraInicio(), proximaData, horariosAgenda);
				horariosAgenda.setDataInicioCalculada(proximaData);
				this.gerarAgendamentoSessao(listaAcomodacoes, listaHorarios, diasSemana, 
						turnos, tpsSeq, salaSeq, espSeq, pacCodigo, prescricao, acomodacao, dataFim, proximaData, horariosAgenda);
				break;
			} else {
				horariosAgenda.setMarcouHorario(false);
			}
		}
		if (!horariosAgenda.getListaFinal().isEmpty() && horariosAgenda.getListaFinal().get(0).getLogTentativas() != null) {
			return horariosAgenda.getListaFinal();
			
		} else {
			// Testa se todos os dias foram marcados; caso contrário chama o método recursivamente passando horariosAgenda.getDataInicioCalculada() + 1.
			if (Integer.valueOf(horariosAgenda.getListaFinal().size()).compareTo(Integer.valueOf(listaHorarios.size())) < 0) {
				proximaData = DateUtil.adicionaDias(horariosAgenda.getDataInicioCalculada(), 1);
				proximaData = this.obterProximaDataComHoraInicial(turnos.get(0).getHoraInicio(), proximaData, horariosAgenda);
				horariosAgenda.setDataInicioCalculada(proximaData);
				this.gerarAgendamentoSessao(listaAcomodacoes, listaHorarios, diasSemana, 
						turnos, tpsSeq, salaSeq, espSeq, pacCodigo, prescricao, acomodacao, dataFim, proximaData, horariosAgenda);
			} else {
				// #41693 - Realiza o agendamento no ambulatório.
				this.efetuaPreAgendamentoAmbulatorio(listaAcomodacoes, listaHorarios, diasSemana, turnos, tpsSeq, salaSeq,
						espSeq, acomodacao, dataFim, proximaData, horariosAgenda,
						horariosAgenda.getListaFinal(), pacCodigo, prescricao);
			}
		}
		return horariosAgenda.getListaFinal();
	}

	private Date obterProximaDataComHoraInicial(Date hrInicioTurno, Date proximaData, HorariosAgendaVO horariosAgenda) {
		if (horariosAgenda.getDthrHoraInicio() != null) {
			proximaData = DateUtil.comporDiaHora(proximaData, horariosAgenda.getDthrHoraInicio());
		} else {
			proximaData = DateUtil.comporDiaHora(proximaData, hrInicioTurno);
		}
		return proximaData;
	}
	
	private boolean atendeNoDiaDaSemana(List<DominioDiaSemana> diasSemana, Date proximaData, CadIntervaloTempoVO horario, HorariosAgendaVO horariosAgenda) {
		if (!diasSemana.contains(CoreUtil.retornaDiaSemana(proximaData))
				|| !this.respeitaFiltroDiasSemana(horario.getDiaReferencia(), proximaData, horariosAgenda.getDiasSemanaFiltro())) {
			return false;
		}
		return true;
	}

	private void realizarAgendamentoEntreHorariosExistentes(List<MptTurnoTipoSessao> turnos, Short tpsSeq, Short salaSeq,
			Short espSeq, DominioTipoLocal acomodacao, Date proximaData, Date dataFim, HorariosAgendaVO horariosAgenda, CadIntervaloTempoVO horario,
			Short loaSeq, List<AgendamentoSessaoVO> horariosAgendados, Integer pacCodigo, Integer prescricao) throws ApplicationBusinessException {
		
		int count = 0;
		for (AgendamentoSessaoVO agendado : horariosAgendados) {
			if (!horariosAgenda.isMarcouHorario() && !horariosAgenda.isPercorreuDia()) {
				count++;
				// Percorre os turnos...
				for (MptTurnoTipoSessao turno : turnos) {
					Date horaInicialTurno = null;
					if (horariosAgenda.getDthrHoraInicio() != null) {
						horaInicialTurno = DateUtil.comporDiaHora(proximaData, horariosAgenda.getDthrHoraInicio());
					} else if (DateUtil.validaHoraMenorIgual(turno.getHoraInicio(), proximaData)) {
						horaInicialTurno = proximaData;
					} else {
						horaInicialTurno = DateUtil.comporDiaHora(proximaData, turno.getHoraInicio());
					}
					if (!DateUtil.validaHoraMaior(turno.getHoraInicio(), horaInicialTurno)) {
						Date horaFinalTurno = DateUtil.comporDiaHora(proximaData, turno.getHoraFim());
						Integer minutosDisp = DateUtil
								.obterQtdMinutosEntreDuasDatas(horaInicialTurno, agendado.getDataInicio());
						Integer minutosReq = DateUtil
								.obterQtdMinutosEntreDuasDatas(horario.getHoraInicioReferencia(), horario.getHoraFimReferencia());
						// Se tiver tempo disponível marca horário.
						if (minutosReq.compareTo(minutosDisp) <= 0) {
							Date dataIni = DateUtil.comporDiaHora(proximaData, horaInicialTurno);
							Date dtFim = DateUtil.adicionaMinutos(dataIni, minutosReq);
							// Só marca o horário se não ultrapassar o limite do turno e se não possuir bloqueio.
							if (DateUtil.validaHoraMenorIgual(dtFim, horaFinalTurno)) {
								// Verifica se possui o horário no ambulatório.
								if (!this.possuiLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim, horariosAgenda, horario.getDiaReferencia())
										&& this.horarioRespeitaFiltroTurno(horariosAgenda, dataIni)
										&& this.possuiHorarioAmbulatorio(espSeq, dataIni, dtFim, pacCodigo, prescricao, horariosAgenda, horario.getDiaReferencia())
										&& !this.possuiHorarioAgendado(salaSeq, tpsSeq, loaSeq, acomodacao,
												dataIni, dtFim, DateUtil.truncaData(proximaData),
												DateUtil.obterDataComHoraFinal(proximaData))) {
									HorariosAgendamentoSessaoVO vo = this.montaHorariosAgendamentoSessaoVO(loaSeq, horario, dataIni, dtFim);
									// Assegura que o sistema irá sugerir sempre o menor horário livre.
									this.verificarMenorHorarioMarcado(horariosAgenda, vo);
									horariosAgenda.setMarcouHorario(true);
									break;
								} else {
									// Só segue a regra de adicionar 30 minutos caso não possua filtro de Hora Inicial.
									if (horariosAgenda.getDthrHoraInicio() == null) {
										proximaData = this.obterProximaDataAposBloqueio(proximaData, tpsSeq, loaSeq, dataIni, dtFim);
										if (DateUtil.validaDataMaior(proximaData, dataFim)) {
											horariosAgenda.setListaFinal(new ArrayList<HorariosAgendamentoSessaoVO>(Arrays.asList(
													new HorariosAgendamentoSessaoVO(horariosAgenda.getLogTentativas().toString()))));
											return;
										}
										this.realizarAgendamentoEntreHorariosExistentes(turnos, tpsSeq, salaSeq, espSeq, acomodacao, proximaData, dataFim,
												horariosAgenda, horario, loaSeq, horariosAgendados, pacCodigo, prescricao);
										break;
									}
								}
							}
						} else {
							minutosDisp = DateUtil.obterQtdMinutosEntreDuasDatas(agendado.getDataFim(), horaFinalTurno);
							if (minutosReq.compareTo(minutosDisp) <= 0) {
								Date dataIni = null;
								// Se a hora é menor que o início do turno então seta o início do turno como dataIni.
								if (DateUtil.validaHoraMaior(horaInicialTurno, agendado.getDataFim())) {
									dataIni = DateUtil.comporDiaHora(proximaData, horaInicialTurno);
								} else {
									dataIni = DateUtil.comporDiaHora(proximaData, agendado.getDataFim());
								}
								Date dtFim = DateUtil.adicionaMinutos(dataIni, minutosReq);
								
								if ((horariosAgenda.getDthrHoraInicio() != null
										&& DateUtil.isDatasIguais(dataIni, DateUtil.comporDiaHora(proximaData, horariosAgenda.getDthrHoraInicio())))
										|| horariosAgenda.getDthrHoraInicio() == null) {
									// Só marca o horário se não ultrapassar o limite do turno e se não possuir bloqueio.
									if (DateUtil.validaHoraMenorIgual(dtFim, horaFinalTurno)) {
										// Verifica se possui o horário no ambulatório.
										if (!this.possuiLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim, horariosAgenda, horario.getDiaReferencia())
												&& this.horarioRespeitaFiltroTurno(horariosAgenda, dataIni)
												&& this.possuiHorarioAmbulatorio(espSeq, dataIni, dtFim, pacCodigo,
														prescricao, horariosAgenda, horario.getDiaReferencia())) {
											boolean agendou = this.realizarAgendamentoEntreHorariosExistentesParte2(dataIni, dtFim, proximaData, horariosAgenda, tpsSeq,
													loaSeq, horario, horariosAgendados, salaSeq, acomodacao, count);
											if (agendou) {
												break;
											}
											
										} else {
											// Só segue a regra de adicionar 30 minutos caso não possua filtro de Hora Inicial.
											if (horariosAgenda.getDthrHoraInicio() == null) {
												proximaData = this.obterProximaDataAposBloqueio(proximaData, tpsSeq, loaSeq, dataIni, dtFim);
												if (DateUtil.validaDataMaior(proximaData, dataFim)) {
													horariosAgenda.setListaFinal(new ArrayList<HorariosAgendamentoSessaoVO>(Arrays.asList(
															new HorariosAgendamentoSessaoVO(horariosAgenda.getLogTentativas().toString()))));
													return;
												}
												this.realizarAgendamentoEntreHorariosExistentes(turnos, tpsSeq, salaSeq, espSeq, acomodacao, proximaData, dataFim,
														horariosAgenda, horario, loaSeq, horariosAgendados, pacCodigo, prescricao);
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			} else {
				break;
			}
		}
		horariosAgenda.setPercorreuDia(true);
	}
	
	private boolean realizarAgendamentoEntreHorariosExistentesParte2(Date dataIni, Date dtFim, Date proximaData, HorariosAgendaVO horariosAgenda,
			Short tpsSeq, Short loaSeq, CadIntervaloTempoVO horario, List<AgendamentoSessaoVO> horariosAgendados, Short salaSeq,
			DominioTipoLocal acomodacao, int count) {
		boolean agendou = false;
		// Faz busca no banco pois os próximos itens da lista podem estar utilizando o horário.
		if (horariosAgendados.size() > count && !this.possuiHorarioAgendado(salaSeq, tpsSeq, loaSeq, acomodacao,
				dataIni, dtFim, DateUtil.truncaData(proximaData),
				DateUtil.obterDataComHoraFinal(proximaData))) {
			HorariosAgendamentoSessaoVO vo = this.montaHorariosAgendamentoSessaoVO(loaSeq, horario, dataIni, dtFim);
			// Assegura que o sistema irá sugerir sempre o menor horário livre.
			this.verificarMenorHorarioMarcado(horariosAgenda, vo);
			horariosAgenda.setMarcouHorario(true);
			agendou = true;
			// Último item da lista; pode inserir livremente.
		} else if (horariosAgendados.size() == count) {
			HorariosAgendamentoSessaoVO vo = this.montaHorariosAgendamentoSessaoVO(loaSeq, horario, dataIni, dtFim);
			// Assegura que o sistema irá sugerir sempre o menor horário livre.
			this.verificarMenorHorarioMarcado(horariosAgenda, vo);
			horariosAgenda.setMarcouHorario(true);
			agendou = true;
		}
		return agendou;
	}
	
	private void verificarMenorHorarioMarcado(HorariosAgendaVO horariosAgenda, HorariosAgendamentoSessaoVO vo) {
		if (horariosAgenda.getMenorHorario() != null) {
			if (DateUtil.validaHoraMaior(horariosAgenda.getMenorHorario().getDataInicio(), vo.getDataInicio())) {
				horariosAgenda.setMenorHorario(vo);
			}
		} else {
			horariosAgenda.setMenorHorario(vo);
		}
	}
	
	private void marcarHorario(HorariosAgendaVO horariosAgenda) {
		if (horariosAgenda.getDthrHoraInicio() != null && horariosAgenda.isMarcouHorario() && !horariosAgenda.isEncontrouMenorHorario()) {
			horariosAgenda.getListaFinal().add(horariosAgenda.getMenorHorario());
			horariosAgenda.setEncontrouMenorHorario(true);
		}
	}
	
	private void marcarMenorHorario(HorariosAgendaVO horariosAgenda) {
		if (horariosAgenda.getDthrHoraInicio() == null && horariosAgenda.isMarcouHorario() && !horariosAgenda.isEncontrouMenorHorario()) {
			horariosAgenda.getListaFinal().add(horariosAgenda.getMenorHorario());
			horariosAgenda.setEncontrouMenorHorario(true);
		}
	}
	
	private boolean possuiLocaisIndisponiveisNoPeriodo(Short tpsSeq, Short loaSeq, Date dataIni, Date dtFim, HorariosAgendaVO horariosAgenda,
			Short diaReferencia) {
		boolean possuiBloqueio = this.mptBloqueioDAO.possuiLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim);
		if (possuiBloqueio) {
			MptLocalAtendimento local = this.mptLocalAtendimentoDAO.obterPorChavePrimaria(loaSeq);
			String descricaoLocal = local.getTipoLocal().getDescricao().concat(": ").concat(loaSeq.toString());
			MptBloqueio ultimoBloqueio = this.mptBloqueioDAO.obterLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim).get(0);
			String hrIni = DateUtil.obterDataFormatada(ultimoBloqueio.getApartirDe(), "HH:mm");
			String hrFim = DateUtil.obterDataFormatada(ultimoBloqueio.getAte(), "HH:mm");
			this.popularLogTentativas(horariosAgenda, diaReferencia, dataIni,
					"Este dia/horário está bloqueado. " + descricaoLocal + " - (Das " + hrIni + " às " + hrFim + ")", true);
		}
		return possuiBloqueio;
	}
	
	private Date obterProximaDataAposBloqueio(Date proximaData, Short tpsSeq, Short loaSeq, Date dataIni, Date dtFim) {
		if (!this.mptBloqueioDAO.possuiLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim)) {
			proximaData = DateUtil.adicionaMinutos(proximaData, 30);
			
			//  #54808
			// Caso os minutos estejam quebrados, arredonda para o valor mais próximo (tendo como base 30 em 30 minutos)
			Integer horas = DateUtil.getHoras(proximaData);
			Integer minutos = DateUtil.getMinutos(proximaData).intValue();
			if (minutos > 0 && minutos < 30) {
				proximaData = DateUtil.truncaData(proximaData);
				proximaData = DateUtil.adicionaHoras(proximaData, horas);
			} else if (minutos > 30) {
				proximaData = DateUtil.truncaData(proximaData);
				proximaData = DateUtil.adicionaHoras(proximaData, horas);
				proximaData = DateUtil.adicionaMinutos(proximaData, 30);
			}	
			
		} else {
			MptBloqueio ultimoBloqueio = this.mptBloqueioDAO.obterLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim).get(0);
			proximaData = DateUtil.comporDiaHora(proximaData, ultimoBloqueio.getAte());		
		}
		return proximaData;
	}
	
	private boolean horarioRespeitaFiltroTurno(HorariosAgendaVO horariosAgenda, Date dataIni) {
		boolean horarioOK = true;
		if (horariosAgenda.isTuco() && horariosAgenda.getFiltroTurno() != null) {
			// O sistema deve garantir que o inicio da sessão será dentro do turno selecionado na tela.
			if (DateUtil.validaHoraMaior(dataIni, horariosAgenda.getFiltroTurno().getHoraFim())) {
				horarioOK = false;
			}
		}
		return horarioOK;
	}
	
	private boolean possuiHorarioAgendado(Short salaSeq, Short tpsSeq, Short loaSeq, DominioTipoLocal acomodacao,
			Date dataIni, Date dtFim, Date inicioTurno, Date fimTurno) {
		
		List<AgendamentoSessaoVO> horariosAgendados = this.mptHorarioSessaoDAO
				.obterListaHorariosSessaoMarcados(salaSeq, tpsSeq, loaSeq, acomodacao, inicioTurno, fimTurno);
		for (AgendamentoSessaoVO vo : horariosAgendados) {
			if (DateUtil.isDatasIguais(vo.getDataInicio(), dataIni)) {
				return true;
			} else if (DateUtil.validaHoraMaior(vo.getDataInicio(), dataIni) && DateUtil.validaHoraMaior(dtFim, vo.getDataInicio())) {
				return true;
			} else if (DateUtil.validaHoraMaior(dataIni, vo.getDataInicio())
					&& this.mptHorarioSessaoDAO.possuiHorarioAgendado(salaSeq, tpsSeq, loaSeq, acomodacao, dataIni, dtFim)) {
				return true;
			}
		}
		return false;
	}
	
	private void realizarAgendamentoLivreNoDia(List<MptTurnoTipoSessao> turnos, Date proximaData, Date dataFim, HorariosAgendaVO horariosAgenda,
			CadIntervaloTempoVO horario, Short tpsSeq, Short loaSeq, Short espSeq, Integer pacCodigo, Integer prescricao) throws ApplicationBusinessException {
		// Percorre os turnos...
		for (MptTurnoTipoSessao turno : turnos) {
			Date horaInicialTurno = null;
			if (horariosAgenda.getDthrHoraInicio() != null) {
				horaInicialTurno = DateUtil.comporDiaHora(proximaData, horariosAgenda.getDthrHoraInicio());
			} else if (DateUtil.validaHoraMenorIgual(turno.getHoraInicio(), proximaData)) {
				horaInicialTurno = proximaData;
			} else {
				horaInicialTurno = DateUtil.comporDiaHora(proximaData, turno.getHoraInicio());
			}
			if (!DateUtil.validaHoraMaior(turno.getHoraInicio(), horaInicialTurno)) {
				Date horaFinalTurno = DateUtil.comporDiaHora(proximaData, turno.getHoraFim());
				Integer minutosDisp = DateUtil
						.obterQtdMinutosEntreDuasDatas(horaInicialTurno, horaFinalTurno);
				Integer minutosReq = DateUtil
						.obterQtdMinutosEntreDuasDatas(horario.getHoraInicioReferencia(), horario.getHoraFimReferencia());
				// Se tiver tempo disponível marca horário.
				if (minutosReq.compareTo(minutosDisp) <= 0) {
					Date dataIni = DateUtil.comporDiaHora(proximaData, horaInicialTurno);
					Date dtFim = DateUtil.adicionaMinutos(dataIni, minutosReq);
					// Verifica se possui o horário no ambulatório e se não possui bloqueios
					if (this.possuiHorarioAmbulatorio(espSeq, dataIni, dtFim, pacCodigo, prescricao, horariosAgenda, horario.getDiaReferencia())
							&& !this.possuiLocaisIndisponiveisNoPeriodo(tpsSeq, loaSeq, dataIni, dtFim, horariosAgenda, horario.getDiaReferencia())
							&& this.horarioRespeitaFiltroTurno(horariosAgenda, dataIni)) {
						HorariosAgendamentoSessaoVO vo = this.montaHorariosAgendamentoSessaoVO(loaSeq, 
								horario, dataIni, dtFim);
						horariosAgenda.getListaFinal().add(vo);
						horariosAgenda.setMarcouHorario(true);
						horariosAgenda.setEncontrouMenorHorario(true);
						break;
					} else {
						// Só segue a regra de adicionar 30 minutos caso não possua filtro de Hora Inicial.
						if (horariosAgenda.getDthrHoraInicio() == null) {
							proximaData = this.obterProximaDataAposBloqueio(proximaData, tpsSeq, loaSeq, dataIni, dtFim);
							if (DateUtil.validaDataMaior(proximaData, dataFim)) {
								horariosAgenda.setListaFinal(new ArrayList<HorariosAgendamentoSessaoVO>(Arrays.asList(
										new HorariosAgendamentoSessaoVO(horariosAgenda.getLogTentativas().toString()))));
								return;
							}
							this.realizarAgendamentoLivreNoDia(turnos, proximaData, dataFim, horariosAgenda, horario,
									tpsSeq, loaSeq, espSeq, pacCodigo, prescricao);
							break;
						}
					}
				}
			}
		}
	}
	
	private boolean possuiHorarioAmbulatorio(Short espSeq, Date horaInicio, Date dataFim, Integer pacCodigo, Integer prescricao,
			HorariosAgendaVO horariosAgenda, Short diaReferencia) {
		Date horaInicioAjustada = null;
		Integer minutos = DateUtil.getMinutos(horaInicio).intValue();
		// Só ajusta o horário inicial caso os minutos estejam quebrados.
		if (minutos > 0 && (minutos < 30 || minutos > 30)) {
			horaInicioAjustada = this.obterHorarioAjustado(horaInicio);
		} else {
			horaInicioAjustada = horaInicio;
		}
		Date horaFimAjustada = this.obterHorarioAjustado(dataFim);
		List<Integer> listaGrdSeq = this.ambulatorioFacade
				.obterGradesComHorariosDisponiveis(espSeq, horaInicioAjustada, horaFimAjustada);
		for (Integer grdSeq : listaGrdSeq) {
			List<AgendamentoAmbulatorioVO> listaHorariosDisponiveis = this.ambulatorioFacade
					.obterHorariosDisponiveisAmbulatorio(espSeq, grdSeq, horaInicioAjustada, horaFimAjustada);
			
			Integer totalMinutos = DateUtil.obterQtdMinutosEntreDuasDatas(horaInicio, dataFim);
			// Se tentar marcar uma sessão com menos de 30 minutos, seta para 30 o total para fechar o cálculo abaixo.
			if (totalMinutos < 30) {
				totalMinutos = 30;
			}
			// Divide o total de minutos por 30 (tempo de cada registro no ambulatório)
			Integer qtdHorariosNecessarios = (totalMinutos / 30);
			// Se dataFim possuir minutos quebrados, adiciona + 1 horário necessário
			Integer minutosDataFim = DateUtil.getMinutos(dataFim).intValue();
			if (minutosDataFim > 0 && minutosDataFim > 30) {
				qtdHorariosNecessarios = qtdHorariosNecessarios + 1;
			}
			// Verifica se a quantidade de registros retornados supre a qtdHorariosNecessarios.
			if (Integer.valueOf(listaHorariosDisponiveis.size()).compareTo(qtdHorariosNecessarios) == 0) {
				return true;
			}
		}
		if (!horariosAgenda.isGerouLogAmbulatorio()) {
			this.popularLogTentativas(horariosAgenda, diaReferencia, horaInicio, "Não foi encontrado agenda no ambulatório.", false);
			horariosAgenda.setGerouLogAmbulatorio(true);
		}
		return false;
	}
	
	/**
	 * #41693 - Realiza o agendamento no ambulatório
	 * @param listaAcomodacoes
	 * @param listaHorarios
	 * @param diasSemana
	 * @param turnos
	 * @param tpsSeq
	 * @param salaSeq
	 * @param espSeq
	 * @param acomodacao
	 * @param dataInicio
	 * @param dataFim
	 * @param proximaData
	 * @param horariosAgenda
	 * @param horariosAgendados
	 * @param pacCodigo
	 * @param prescricao
	 * @throws ApplicationBusinessException
	 */
	public void efetuaPreAgendamentoAmbulatorio(List<Short> listaAcomodacoes, List<CadIntervaloTempoVO> listaHorarios,
			List<DominioDiaSemana> diasSemana, List<MptTurnoTipoSessao> turnos, Short tpsSeq, Short salaSeq, Short espSeq,
			DominioTipoLocal acomodacao, Date dataFim, Date proximaData, HorariosAgendaVO horariosAgenda,
			List<HorariosAgendamentoSessaoVO> horariosAgendados, Integer pacCodigo, Integer prescricao) throws ApplicationBusinessException {
		// Inicia como true para passar no if abaixo.
		boolean agendou = true;
		for (HorariosAgendamentoSessaoVO item : horariosAgendados) {
			// Se agendou o último 'item', tenta agendar o próximo...
			if (agendou) {
				agendou = false;
				Date horaInicioAjustada = null;
				Integer minutos = DateUtil.getMinutos(item.getDataInicio()).intValue();
				// Só ajusta o horário inicial caso os minutos estejam quebrados.
				if (minutos > 0 && (minutos < 30 || minutos > 30)) {
					horaInicioAjustada = this.obterHorarioAjustado(item.getDataInicio());
				} else {
					horaInicioAjustada = item.getDataInicio();
				}
				Date horaFimAjustada = this.obterHorarioAjustado(item.getDataFim());
				List<Integer> listaGrdSeq = this.ambulatorioFacade
						.obterGradesComHorariosDisponiveis(espSeq, horaInicioAjustada, horaFimAjustada);
				for (Integer grdSeq : listaGrdSeq) {
					List<AgendamentoAmbulatorioVO> listaHorariosDisponiveis = this.ambulatorioFacade
							.obterHorariosDisponiveisAmbulatorio(espSeq, grdSeq, horaInicioAjustada, horaFimAjustada);
					
					Integer totalMinutos = DateUtil.obterQtdMinutosEntreDuasDatas(item.getDataInicio(), item.getDataFim());
					// Se tentar marcar uma sessão com menos de 30 minutos, seta para 30 o total para fechar o cálculo abaixo.
					if (totalMinutos < 30) {
						totalMinutos = 30;
					}
					// Divide o total de minutos por 30 (tempo de cada registro no ambulatório)
					Integer qtdHorariosNecessarios = (totalMinutos / 30);
					// Se dataFim possuir minutos quebrados, adiciona + 1 horário necessário
					Integer minutosDataFim = DateUtil.getMinutos(item.getDataFim()).intValue();
					if (minutosDataFim > 0 && minutosDataFim > 30) {
						qtdHorariosNecessarios = qtdHorariosNecessarios + 1;
					}
					// Verifica se a quantidade de registros retornados supre a qtdHorariosNecessarios.
					if (Integer.valueOf(listaHorariosDisponiveis.size()).compareTo(qtdHorariosNecessarios) == 0) {
						agendou = true;
						List<HorarioAgendamentoAmbulatorioVO> listaHorariosAmb = new ArrayList<HorarioAgendamentoAmbulatorioVO>();
						int count = 0;
						for (AgendamentoAmbulatorioVO horarioAmb : listaHorariosDisponiveis) {
							// Verifica se a prescrição informada é válida (Futuramente o campo Prescrição vai ser uma suggestion.
							// Portanto só deve ser testado se prescricao != null)
							if (prescricao != null && this.mptPrescricaoPacienteDAO
									.verificarExistePrescricaoPacientePorSeq(prescricao)) {
								HorarioAgendamentoAmbulatorioVO vo = this.mptPrescricaoPacienteDAO.obterConvenioPlanoPorPaciente(pacCodigo, prescricao);
								if (vo != null) {
									vo.setConNumero(horarioAmb.getConNumero());
									if (count > 0) {
										vo.setStcSituacao("R");
										// Seta o convênio para null, pois o mesmo foi populado na consulta.
										vo.setCspCnvCodigo(null);
										vo.setCspSeq(null);
									} else {
										vo.setPacCodigo(pacCodigo);
										vo.setStcSituacao("M");
										vo.setCaaSeq((short) 8);
										vo.setRetSeq(DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
										if (vo.getGrupoConvenio().equals(DominioGrupoConvenio.S)) {
											vo.setTagSeq((short) 1);
											vo.setPgdSeq((short) 1);
										} else {
											vo.setTagSeq((short) 5);
											vo.setPgdSeq((short) 2);
										}
									}
									listaHorariosAmb.add(vo);
									count++;
								} else {
									this.commitTransaction();
									throw new ApplicationBusinessException(AgendamentoSessaoRNExceptionCode.MENSAGEM_ERRO_CONVENIO_NAO_ENCONTRADO_AGENDAR_SESSAO);
								}
							} else {
								HorarioAgendamentoAmbulatorioVO vo = new HorarioAgendamentoAmbulatorioVO();
								vo.setConNumero(horarioAmb.getConNumero());
								if (count > 0) {
									vo.setStcSituacao("R");
								} else {
									vo.setPacCodigo(pacCodigo);
									vo.setCspCnvCodigo((short) 1);
									vo.setCspSeq((short) 2);
									vo.setStcSituacao("M");
									vo.setRetSeq(DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
									vo.setCaaSeq((short) 8);
									vo.setTagSeq((short) 1);
									vo.setPgdSeq((short) 1);
								}
								listaHorariosAmb.add(vo);
								count++;
							}
						}
						item.setHorariosAgendamentoAmbulatorio(listaHorariosAmb);
						break;
					}
				}
			} else {
				break;
			}
		}
		if (!agendou) {
			proximaData = DateUtil.adicionaDias(horariosAgenda.getDataInicioCalculada(), 1);
			proximaData = this.obterProximaDataComHoraInicial(turnos.get(0).getHoraInicio(), proximaData, horariosAgenda);
			horariosAgenda.setDataInicioCalculada(proximaData);
			this.gerarAgendamentoSessao(listaAcomodacoes, listaHorarios, diasSemana, 
					turnos, tpsSeq, salaSeq, espSeq, pacCodigo, prescricao, acomodacao, dataFim, proximaData, horariosAgenda);
		}
	}
	
	private Date obterHorarioAjustado(Date horario) {
		Integer horas = DateUtil.getHoras(horario);
		Integer minutos = DateUtil.getMinutos(horario).intValue();
		if (minutos > 0 && minutos < 30) {
			horario = DateUtil.truncaData(horario);
			horario = DateUtil.adicionaHoras(horario, horas);
			horario = DateUtil.adicionaMinutos(horario, 30);
		} else if (minutos > 30) {
			horario = DateUtil.truncaData(horario);
			horario = DateUtil.adicionaHoras(horario, horas + 1);
		}
		// Diminui 30 minutos para pegar o range certo de horários no ambulatório.
		horario = DateUtil.adicionaMinutos(horario, -30);
		return horario;
	}
	
	private HorariosAgendamentoSessaoVO montaHorariosAgendamentoSessaoVO(Short loaSeq, CadIntervaloTempoVO horario,
			Date dataIni, Date dtFim) {
		HorariosAgendamentoSessaoVO vo = new HorariosAgendamentoSessaoVO();
		vo.setLoaSeq(loaSeq);
		MptLocalAtendimento local = this.mptLocalAtendimentoDAO.obterPorChavePrimaria(loaSeq);
		vo.setAcomodacao(local.getTipoLocal());
		vo.setDia(horario.getDiaReferencia());
		vo.setTempo(horario.getQtdeHoras());
		vo.setCiclo(horario.getCiclo());
		vo.setSesSeq(horario.getSesSeq());
		vo.setDataInicio(dataIni);
		vo.setDataFim(dtFim);
		vo.setIndSituacao(DominioSituacaoHorarioSessao.R);
		return vo;
	}
	
	private List<CadIntervaloTempoVO> obterListaHorariosFiltrada(List<CadIntervaloTempoVO> listaHorarios) {
		List<CadIntervaloTempoVO> listaRetorno = new ArrayList<CadIntervaloTempoVO>();
		for (CadIntervaloTempoVO item : listaHorarios) {
			if (item.isAgendar()) {
				listaRetorno.add(item);
			}
		}
		return listaRetorno;
	}
	
	public List<PercentualOcupacaoVO> preencherPercentualOcupacao(Short salSeq, Short tpsSeq, List<HorariosAgendamentoSessaoVO> horariosAgendados) {
		Date dataInicial = DateUtil.truncaData(horariosAgendados.get(0).getDataInicio());
		Date dataFinal = DateUtil.truncaData(horariosAgendados.get(horariosAgendados.size()-1).getDataFim());
		//Ajusta o período de consulta
		while (CoreUtil.retornaDiaSemana(dataInicial) != DominioDiaSemana.DOMINGO) {
			dataInicial = DateUtil.adicionaDias(dataInicial, -1);
		}
		while (CoreUtil.retornaDiaSemana(dataFinal) != DominioDiaSemana.SABADO) {
			dataFinal = DateUtil.adicionaDias(dataFinal, 1);
		}
		List<MptDiaTipoSessao> diasTipoSessao = this.mptDiaTipoSessaoDAO.obterDiasPorTipoSessao(tpsSeq);
		List<DominioDiaSemana> diasSemana = this.obterListaDiasSemanaEmDominio(diasTipoSessao);
		List<PercentualOcupacaoVO> listaRetorno = new ArrayList<PercentualOcupacaoVO>();
		
		while (DateUtil.validaDataMenorIgual(dataInicial, dataFinal)) {
			PercentualOcupacaoVO percVO = this.mptAgendamentoSessaoDAO.obterPercentualOcupacaoNoDia(salSeq, dataInicial);
			// Possui ocupação; formata o tempo disponível
			if (percVO != null) {
				percVO.setDataEvento(dataInicial);
				percVO.setTempoDisponivelFormatado(this.obterTempoFormatado(percVO.getMinutosDisponiveis()));
				listaRetorno.add(percVO);
			// Não possui ocupação no dia; monta VO com 0% de ocupação caso atenda no dia, ou com 100% caso não atenda.
			} else {
				PercentualOcupacaoVO vo = new PercentualOcupacaoVO();
				if (diasSemana.contains(CoreUtil.retornaDiaSemana(dataInicial))) {
					Integer tempoTotalTurnos = this.mptTurnoTipoSessaoDAO.obterTempoTotalTurnos(salSeq);
					vo.setTempoDisponivelFormatado(this.obterTempoFormatado(tempoTotalTurnos));
					vo.setPercentual(0);
				} else {
					vo.setTempoDisponivelFormatado("00:00");
					vo.setPercentual(100);
				}
				vo.setDataInicio(DateUtil.obterDataFormatada(dataInicial, DDMMYYYY));
				vo.setNumeroPacientes(0);
				vo.setDataEvento(dataInicial);
				listaRetorno.add(vo);
			}
			dataInicial = DateUtil.adicionaDias(dataInicial, 1);
		}
		return listaRetorno;
	}
	
	private String obterTempoFormatado(Integer tempoTotalTurnos) {
		String tempoDisp = "";
		Integer horas = tempoTotalTurnos / 60;
		Integer minutos = tempoTotalTurnos % 60;
		if (horas < 10) {
			tempoDisp = tempoDisp.concat("0");
		}
		tempoDisp = tempoDisp.concat(horas.toString()).concat(":");
		if (minutos < 10) {
			tempoDisp = tempoDisp.concat("0");
		}
		tempoDisp = tempoDisp.concat(minutos.toString());
		return tempoDisp;
	}
	
	private List<DominioDiaSemana> obterListaDiasSemanaEmDominio(List<MptDiaTipoSessao> diasTipoSessao) {
		List<DominioDiaSemana> listaRetorno = new ArrayList<DominioDiaSemana>();
		for (MptDiaTipoSessao dts : diasTipoSessao) {
			switch (dts.getDia()) {
			case (byte) 1:
				listaRetorno.add(DominioDiaSemana.DOMINGO);
				break;
			case (byte) 2:
				listaRetorno.add(DominioDiaSemana.SEGUNDA);
				break;
			case (byte) 3:
				listaRetorno.add(DominioDiaSemana.TERCA);
				break;
			case (byte) 4:
				listaRetorno.add(DominioDiaSemana.QUARTA);
				break;
			case (byte) 5:
				listaRetorno.add(DominioDiaSemana.QUINTA);
				break;
			case (byte) 6:
				listaRetorno.add(DominioDiaSemana.SEXTA);
				break;
			case (byte) 7:
				listaRetorno.add(DominioDiaSemana.SABADO);
				break;
			}
		}
		return listaRetorno;
	}
	
	private List<DominioDiaSemana> obterListaDiasSemanaFiltrado(String[] diasSelecionados) {
		List<DominioDiaSemana> listaRetorno = new ArrayList<DominioDiaSemana>();
		for (String ds : diasSelecionados) {
			switch (ds) {
			case "Domingo":
				listaRetorno.add(DominioDiaSemana.DOMINGO);
				break;
			case "Segunda-feira":
				listaRetorno.add(DominioDiaSemana.SEGUNDA);
				break;
			case "Terça-feira":
				listaRetorno.add(DominioDiaSemana.TERCA);
				break;
			case "Quarta-feira":
				listaRetorno.add(DominioDiaSemana.QUARTA);
				break;
			case "Quinta-feira":
				listaRetorno.add(DominioDiaSemana.QUINTA);
				break;
			case "Sexta-feira":
				listaRetorno.add(DominioDiaSemana.SEXTA);
				break;
			case "Sábado":
				listaRetorno.add(DominioDiaSemana.SABADO);
				break;
			}
		}
		return listaRetorno;
	}
	
	private boolean respeitaFiltroDiasSemana(Short diaReferencia, Date proximaData, List<DominioDiaSemana> diasSemanaFiltro) {
		boolean respeita = true;
		if (diaReferencia == 1 && diasSemanaFiltro != null) {
			if (!diasSemanaFiltro.contains(CoreUtil.retornaDiaSemana(proximaData))) {
				respeita = false;
			}
		}
		return respeita;
	}
	
	private void popularLogTentativas(HorariosAgendaVO horariosAgenda, Short diaReferencia, Date proximaData, String msg, boolean hrBloqueado) {
		String hifen = " - ";
		String msgBloq = "";
		if (horariosAgenda.getLogsHorarioBloqueado() == null) {
			horariosAgenda.setLogsHorarioBloqueado(new ArrayList<String>());
		}
		if (hrBloqueado) {
			msgBloq = "D".concat(diaReferencia.toString()).concat(hifen)
					.concat(DateUtil.obterDataFormatada(proximaData, DDMMYYYY))
					.concat(hifen).concat(msg);
		}
		if (horariosAgenda.getLogTentativas().length() == 0) {
			horariosAgenda.setNroTentativas(1);
			StringBuilder log = new StringBuilder();
			log.append("1ª Tentativa - D")
			.append(diaReferencia).append(hifen)
			.append(DateUtil.obterDataFormatada(proximaData, DDMMYYYY))
			.append(hifen).append(msg);
			horariosAgenda.setLogTentativas(log);
			if (hrBloqueado) {
				horariosAgenda.getLogsHorarioBloqueado().add(msgBloq);
			}
		} else if (!hrBloqueado || !horariosAgenda.getLogsHorarioBloqueado().contains(msgBloq)) {
			horariosAgenda.setNroTentativas(horariosAgenda.getNroTentativas() + 1);
			horariosAgenda.getLogTentativas().append("<br/>").append(horariosAgenda.getNroTentativas()).append("ª Tentativa")
			.append(" - D").append(diaReferencia).append(hifen)
			.append(DateUtil.obterDataFormatada(proximaData, DDMMYYYY))
			.append(hifen).append(msg);
			if (hrBloqueado) {
				horariosAgenda.getLogsHorarioBloqueado().add(msgBloq);
			}
		}
	}
	
	public MptAgendamentoSessao agendarSessao(List<HorariosAgendamentoSessaoVO> horariosGerados, Short tpsSeq, Short salaSeq, DominioTurno turno,
			Integer pacCodigo, Short vpaPtaSeq, Short vpaSeqp, DominioTipoLocal acomodacao, Date dataInicio,
			Date dataFim, String nomeMicrocomputador) throws BaseException {
		
		MptAgendamentoSessao agendamentoSessao = this.mptAgendamentoSessaoRN.inserirMptAgendamentoSessao(tpsSeq, salaSeq, 
				turno, pacCodigo, vpaPtaSeq, vpaSeqp, acomodacao, dataInicio, dataFim);
		for (HorariosAgendamentoSessaoVO item : horariosGerados) {
			String conNumeros = null;
			for (HorarioAgendamentoAmbulatorioVO horarioAmbulatorio : item.getHorariosAgendamentoAmbulatorio()) {
				// #41693 - Integração com o ambulatório.
				this.ambulatorioFacade.atualizarAacConsultas(horarioAmbulatorio.getConNumero(), horarioAmbulatorio.getPacCodigo(),
						horarioAmbulatorio.getCspCnvCodigo(), horarioAmbulatorio.getCspSeq(), horarioAmbulatorio.getStcSituacao(),
						horarioAmbulatorio.getRetSeq(), horarioAmbulatorio.getCaaSeq(), horarioAmbulatorio.getTagSeq(),
						horarioAmbulatorio.getPgdSeq(), nomeMicrocomputador);
				if (conNumeros == null) {
					conNumeros = horarioAmbulatorio.getConNumero().toString();
				} else {
					conNumeros = conNumeros.concat(", ").concat(horarioAmbulatorio.getConNumero().toString());
				}
			}
			this.mptHorarioSessaoRN.inserirMptHorarioSessao(item.getLoaSeq(), agendamentoSessao.getSeq(), item.getDia(), item.getTempo(),
					item.getCiclo(), item.getSesSeq(), item.getDataInicio(), item.getDataFim(), item.getIndSituacao(), conNumeros);
		}
		
		return agendamentoSessao;
	}
}
