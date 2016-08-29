package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class MovimentacoesEscalaPortalPlanejamentoCirurgiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MovimentacoesEscalaPortalPlanejamentoCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;


	@EJB
	private EscalaPortalPlanejamentoCirurgiaRN escalaPortalPlanejamentoCirurgiaRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private EscalaPortalPlanejamentoCirurgiaON escalaPortalPlanejamentoCirurgiaON;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5279562815940723897L;
	
	
	public enum MovimentacoesEscalaPortalPlanejamentoCirurgiaONExceptionCode implements BusinessExceptionCode {
		TROCA_HORARIOS_NAO_REALIZADA;
	}
	
	private Date obterHorarioDisponivelParaAgenda(List<MbcAgendas> cEscala, int i, Date pHoraInicioEscala, MbcAgendas agenda,
			Byte intervaloAgenda, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam) {
		
		if(isNecessarioDeslocarProximoItem(cEscala, i, pHoraInicioEscala, agenda, intervaloAgenda)) {
			if(isItemBloqueado(cEscala.get(i), pucSerMatriculaParam, pucSerVinCodigoParam)) {
				// recebe hora final do item bloqueado
				Byte intervalo = getBlocoCirurgicoFacade().buscarIntervaloEscalaCirurgiaProcedimento(cEscala.get(i));
				pHoraInicioEscala = getEscalaPortalPlanejamentoCirurgiaON().adicionarMinuto(cEscala.get(i).getDthrPrevFim(), intervalo.intValue());
			}
			if(cEscala.size() > i+1) {
				return obterHorarioDisponivelParaAgenda(cEscala, i+1, pHoraInicioEscala, agenda, intervaloAgenda, pucSerMatriculaParam, pucSerVinCodigoParam);
			}
		}
		
		return pHoraInicioEscala;
	}
	
	private Boolean isNecessarioDeslocarProximoItem(List<MbcAgendas> cEscala, int i, Date pHoraInicioEscala, MbcAgendas agenda,
			Byte intervaloAgenda) {
		
		Integer horaOcupacaoFinal = getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(pHoraInicioEscala)
			+ getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(agenda.getTempoSala()) + intervaloAgenda;
		
		if(isOverbooking(cEscala.get(i)) || horaOcupacaoFinal <= getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(cEscala.get(i).getDthrPrevInicio())) {
			return false;
		}
		
		return true;
	}
	
	private Boolean isOverbooking(MbcAgendas agenda) {
		if(agenda.getDthrPrevInicio() == null && agenda.getOrdemOverbooking() != null) {
			return true;
		}
		return false;
	}
	
	private Integer obterIndiceProximoItemNaoBloqueado(List<MbcAgendas> cEscala, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam) {
		Integer count = 0;
		for(MbcAgendas item : cEscala) {
			if(!isItemBloqueado(item, pucSerMatriculaParam, pucSerVinCodigoParam)) {
				return count;
			}
			count++;
		}
		return null;
	}
	
	/**
	 * Desloca Horários - p_desloca_escala
	 * @throws BaseException
	 */
	public void deslocarHorariosBotaoDireita(Integer agdSeq, Date pHoraInicioEscala, Date dtAgenda, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam, MbcSalaCirurgica sala,
			Short unfSeq, Short espSeq, String nomeUsuario, Integer agdSeqRemove) throws BaseException {
		
		pHoraInicioEscala = DateUtil.comporDiaHora(dtAgenda, pHoraInicioEscala);
		
		MbcAgendas agenda = getMbcAgendasDAO().obterPorChavePrimaria(agdSeq);
		
		MbcProfAtuaUnidCirgsId idProf = new MbcProfAtuaUnidCirgsId(pucSerMatriculaParam, pucSerVinCodigoParam,
				pucUnfSeqParam, pucFuncProfParam);
		MbcProfAtuaUnidCirgs profAtuaUnidCirgs = new MbcProfAtuaUnidCirgs();
		profAtuaUnidCirgs.setId(idProf);
		
		AghUnidadesFuncionais unf = new AghUnidadesFuncionais(unfSeq);
		
		List<MbcAgendas> cEscala = obterEscalasCEscala(agdSeq, pHoraInicioEscala,
				dtAgenda, pucSerMatriculaParam,
				pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam,
				sala.getId().getSeqp(), unfSeq, espSeq, agdSeqRemove);
		
		agenda.setSalaCirurgica(sala);
		agenda.setIndSituacao(DominioSituacaoAgendas.ES);
		Byte intervaloAgenda = getBlocoCirurgicoFacade().buscarIntervaloEscalaCirurgiaProcedimento(agenda);
		
		if(cEscala.isEmpty()) {
			if(!isItemBloqueado(agenda, pucSerMatriculaParam, pucSerVinCodigoParam)) {
				agenda.setDthrPrevInicio(pHoraInicioEscala);
				agenda.setDthrPrevFim(getEscalaPortalPlanejamentoCirurgiaON().adicionarMinuto(agenda.getDthrPrevInicio(),
						getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(agenda.getTempoSala())));
				salvarHorarioEscala(dtAgenda, sala, agenda, profAtuaUnidCirgs, unf);
			}
		} else {
			Boolean deslocaProximoItem = false;
			if(isNecessarioDeslocarProximoItem(cEscala, 0, pHoraInicioEscala, agenda, intervaloAgenda)) {
				deslocaProximoItem = true;
			}
			pHoraInicioEscala = obterHorarioDisponivelParaAgenda(cEscala, 0, pHoraInicioEscala, agenda, intervaloAgenda,
					pucSerMatriculaParam, pucSerVinCodigoParam);
			agenda.setDthrPrevInicio(pHoraInicioEscala);
			agenda.setDthrPrevFim(getEscalaPortalPlanejamentoCirurgiaON().adicionarMinuto(agenda.getDthrPrevInicio(),
					getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(agenda.getTempoSala())));
			if(deslocaProximoItem) {
				Integer index = obterIndiceProximoItemNaoBloqueado(cEscala, pucSerMatriculaParam, pucSerVinCodigoParam);
				if(index != null) {
					deslocarHorariosBotaoDireita(cEscala.get(index).getSeq(), getEscalaPortalPlanejamentoCirurgiaON()
							.adicionarMinuto(agenda.getDthrPrevFim(), intervaloAgenda.intValue()),
							dtAgenda, pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam,
							pucFuncProfParam, sala, unfSeq, espSeq, nomeUsuario, null);
				}
			}
			salvarHorarioEscala(dtAgenda, sala, agenda, profAtuaUnidCirgs, unf);
			recalcularOrdemOverbookingsEmEscala(dtAgenda, pucSerMatriculaParam,
					pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam,
					sala.getId().getSeqp(), unfSeq, espSeq);
		}
	}
	
	private void salvarHorarioEscala(Date dtAgenda,
			MbcSalaCirurgica sala, MbcAgendas agenda,
			MbcProfAtuaUnidCirgs profAtuaUnidCirgs, AghUnidadesFuncionais unf
			) throws BaseException {
		getBlocoCirurgicoFacade().gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(null, profAtuaUnidCirgs, null,
				unf, dtAgenda, agenda, agenda.getDthrPrevInicio(), sala.getId().getSeqp(), null, null,
				true);
	}

	
	private void recalcularOrdemOverbookingsEmEscala(Date dtAgenda, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short sciSeqpCombo,
			Short unfSeq, Short espSeq) throws BaseException {
		//c_overbooking
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		List<MbcAgendas> listaAgendas = getMbcAgendasDAO().buscarAgendasEmEscala(unfSeq, dtAgenda, unfSeq,
				sciSeqpCombo, null, null, null, null, false, true);
		
		Byte countOverbooking = 1;
		for(MbcAgendas agd : listaAgendas) {
			if(getEscalaPortalPlanejamentoCirurgiaRN().verificarHoraEscala(agd, pucSerMatriculaParam,
					pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, espSeq, unfSeq,
					dtAgenda, sciSeqpCombo)) {
				if(agd.getDthrPrevFim() == null) {
					agd.setOrdemOverbooking(countOverbooking);
					countOverbooking++;
					getBlocoCirurgicoFacade().persistirAgenda(agd, servidorLogado);
				}
			}
		}
	}

	//c_escala
	public List<MbcAgendas> obterEscalasCEscala(Integer agdSeq, Date pHoraInicioEscala, Date dtAgenda,
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short sciSeqpCombo, Short unfSeq, Short espSeq, Integer agdSeqRemove) {
		List<MbcAgendas> listaAgendas = getMbcAgendasDAO().buscarAgendasEmEscala(unfSeq, dtAgenda, unfSeq,
				sciSeqpCombo, null, null, null, null, false, false);
		List<MbcAgendas> listaAgendasFiltradas = new ArrayList<MbcAgendas>();
		
		for(MbcAgendas agd : listaAgendas) {
			if(agdSeq.equals(agd.getSeq()) || agd.getSeq().equals(agdSeqRemove)) {
				continue;
			}
			if(getEscalaPortalPlanejamentoCirurgiaRN().verificarHoraEscala(agd, pucSerMatriculaParam,
					pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, espSeq, unfSeq,
					dtAgenda, sciSeqpCombo)) {
				
				if(agd.getDthrPrevFim() != null) {
					Date dtPrevFimInterEscala = DateUtil.truncaHorario(getEscalaPortalPlanejamentoCirurgiaON()
							.adicionarMinuto(agd.getDthrPrevFim(), (agd.getIntervaloEscala() == null ? null : agd.getIntervaloEscala().intValue())));
					Integer horario = getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(dtPrevFimInterEscala);
					if(horario == 0) {
						dtPrevFimInterEscala = DateUtil.obterDataComHoraFinal(dtPrevFimInterEscala);
					}
					if(getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(dtPrevFimInterEscala) >
						getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(pHoraInicioEscala)) {
						listaAgendasFiltradas.add(agd);
					}
				} else {
					listaAgendasFiltradas.add(agd);
				}
			}
		}
		return listaAgendasFiltradas;
	}
	
	private boolean isItemBloqueado(MbcAgendas item, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam) {
		if(item.getIndGeradoSistema() 
				|| !item.getProfAtuaUnidCirgs().getRapServidores().getId().getMatricula().equals(pucSerMatriculaParam)
				|| !item.getProfAtuaUnidCirgs().getRapServidores().getId().getVinCodigo().equals(pucSerVinCodigoParam)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Desloca Horário para cima
	 */
	public void deslocarHorariosBotaoAcima(Integer agdSeq, Date dtAgenda, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam, Short sciSeqpCombo,
			Short unfSeq, Short espSeq, String nomeUsuario) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MbcAgendas agendaSelecionada = getMbcAgendasDAO().obterPorChavePrimaria(agdSeq);
		Date hrAgendaSelecionada = agendaSelecionada.getDthrPrevInicio();
		Byte ordemOverbookingInicio = agendaSelecionada.getOrdemOverbooking();
		
		List<MbcAgendas> escalas = getMbcAgendasDAO().buscarAgendasEmEscala(unfSeq, dtAgenda, unfSeq,
				sciSeqpCombo, null, null, null, null, false, false);
		Collections.reverse(escalas);
		
		MbcSalaCirurgicaId idSala = new MbcSalaCirurgicaId(unfSeq, sciSeqpCombo);
		MbcSalaCirurgica sala = getBlocoCirurgicoFacade().obterSalaCirurgicaPorId(idSala);
		Integer indiceAgendaSelecionada = 0;
		for(int i=0; i < escalas.size(); i++) {
			if(escalas.get(i).getSeq().equals(agdSeq)) {
				indiceAgendaSelecionada = i;
				continue;
			}
			if(isItemBloqueado(escalas.get(i), pucSerMatriculaParam, pucSerVinCodigoParam)) { //pula se estiver bloqueado
				if(escalas.size() == i+1) { //ultimo item da lista está bloqueado. dispara excecao
					throw new ApplicationBusinessException(
							MovimentacoesEscalaPortalPlanejamentoCirurgiaONExceptionCode.TROCA_HORARIOS_NAO_REALIZADA);
				}
				continue;
			}
			
			if(agendaSelecionada.getDthrPrevInicio() != null) {
				if(escalas.get(i).getDthrPrevInicio() == null || getEscalaPortalPlanejamentoCirurgiaON()
							.calcularTempoEmMinutos(escalas.get(i).getDthrPrevInicio()) >=
						getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(agendaSelecionada.getDthrPrevInicio())) {
					continue;
				}
			} else {
				if(escalas.get(i).getOrdemOverbooking() != null) {
					if(escalas.get(i).getOrdemOverbooking() >= agendaSelecionada.getOrdemOverbooking()) {
						continue;
					}
				}
			}
			
			if(!isOverbooking(escalas.get(i))) {
				Date hrAgendaAnterior = escalas.get(i).getDthrPrevInicio();
				
				escalas.get(i).setDthrPrevInicio(null);
				escalas.get(i).setDthrPrevFim(null);
				escalas.get(i).setOrdemOverbooking(ordemOverbookingInicio);
				
				deslocarHorariosBotaoDireita(agdSeq, hrAgendaAnterior, dtAgenda, pucSerMatriculaParam,
						pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sala, unfSeq, espSeq, nomeUsuario, escalas.get(i).getSeq());
				
				Byte intervaloAgendaSelecionada = getBlocoCirurgicoFacade().buscarIntervaloEscalaCirurgiaProcedimento(agendaSelecionada);
				Date novoTempoTotalSelecionado = getEscalaPortalPlanejamentoCirurgiaON().adicionarMinuto(agendaSelecionada.getDthrPrevFim(),
						intervaloAgendaSelecionada.intValue());
				
				if(novoTempoTotalSelecionado != null) {
					if((getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(novoTempoTotalSelecionado) >
							getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(hrAgendaSelecionada) && !estaoJuntos(i, indiceAgendaSelecionada))
							|| estaoJuntos(i, indiceAgendaSelecionada)) {
						escalas.get(i).setDthrPrevInicio(novoTempoTotalSelecionado);
					} else {
						escalas.get(i).setDthrPrevInicio(hrAgendaSelecionada);
					}
					escalas.get(i).setOrdemOverbooking(null);
				} else {
					Integer ordemOverbooking = agendaSelecionada.getOrdemOverbooking() + 1;
					escalas.get(i).setOrdemOverbooking(ordemOverbooking.byteValue());
					getBlocoCirurgicoFacade().persistirAgenda(escalas.get(i), servidorLogado);
					if(indiceAgendaSelecionada-1 >= 0) {
						recalcularOverbookingsBtSobe(escalas, indiceAgendaSelecionada-1, ordemOverbooking.intValue());
					}
					break;
				}
				
				deslocarHorariosBotaoDireita(escalas.get(i).getSeq(), escalas.get(i).getDthrPrevInicio(), dtAgenda, pucSerMatriculaParam,
						pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sala, unfSeq, espSeq, nomeUsuario, null);
				
				if(escalas.get(i).getDthrPrevInicio() == null) {
					escalas.get(i).setOrdemOverbooking(Byte.valueOf("1"));
					recalcularOverbookingsBtSobe(escalas, indiceAgendaSelecionada-1, escalas.get(i).getOrdemOverbooking().intValue());
					getBlocoCirurgicoFacade().persistirAgenda(escalas.get(i), servidorLogado);
				}
				
				break;
			} else { // é overbooking
				Byte ordemOverbookingSelecionada = agendaSelecionada.getOrdemOverbooking();
				Byte ordemOverbookingAnterior = escalas.get(i).getOrdemOverbooking();
				
				if(escalas.size() > i+1) {
					escalas.get(i).setOrdemOverbooking(ordemOverbookingSelecionada);
					if(isOverbooking(escalas.get(i+1))) {
						agendaSelecionada.setOrdemOverbooking(ordemOverbookingAnterior);
						getBlocoCirurgicoFacade().persistirAgenda(escalas.get(i), servidorLogado);
						getBlocoCirurgicoFacade().persistirAgenda(agendaSelecionada, servidorLogado);
					} else {
						Byte intervalo = getBlocoCirurgicoFacade().buscarIntervaloEscalaCirurgiaProcedimento(escalas.get(i+1));
						Date tempoTotal = getEscalaPortalPlanejamentoCirurgiaON().adicionarMinuto(escalas.get(i+1).getDthrPrevFim(),
								intervalo.intValue());
						agendaSelecionada.setOrdemOverbooking(null);
						agendaSelecionada.setDthrPrevInicio(tempoTotal);
						deslocarHorariosBotaoDireita(agendaSelecionada.getSeq(), agendaSelecionada.getDthrPrevInicio(), dtAgenda, pucSerMatriculaParam,
								pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sala, unfSeq, espSeq, nomeUsuario, null);
						if(isOverbooking(agendaSelecionada)) {
							agendaSelecionada.setOrdemOverbooking(ordemOverbookingAnterior);
							Integer ordemOverbooking = agendaSelecionada.getOrdemOverbooking() + 1;
							escalas.get(i).setOrdemOverbooking(ordemOverbooking.byteValue());
							getBlocoCirurgicoFacade().persistirAgenda(agendaSelecionada, servidorLogado);
							getBlocoCirurgicoFacade().persistirAgenda(escalas.get(i), servidorLogado);
						}
					}
				} else {
					agendaSelecionada.setOrdemOverbooking(ordemOverbookingAnterior);
					escalas.get(i).setOrdemOverbooking(ordemOverbookingSelecionada);
					getBlocoCirurgicoFacade().persistirAgenda(agendaSelecionada, servidorLogado);
					getBlocoCirurgicoFacade().persistirAgenda(escalas.get(i), servidorLogado);
				}
				if(i-2 >= 0) {
					recalcularOverbookingsBtSobe(escalas, i-2, escalas.get(i).getOrdemOverbooking().intValue());
				}
				break;
			}
		}
	}
	
	private void recalcularOverbookingsBtSobe(List<MbcAgendas> listaEscalas, int i, Integer ordemOverbooking
			) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		while(i != -1) {
			ordemOverbooking++;
			listaEscalas.get(i).setOrdemOverbooking(ordemOverbooking.byteValue());
			getBlocoCirurgicoFacade().persistirAgenda(listaEscalas.get(i),servidorLogado);
			i--;
		}
	}
	
	private Boolean estaoJuntos(Integer i, Integer indiceAgendaSelecionada) {
		if(i - indiceAgendaSelecionada == 1 || indiceAgendaSelecionada - i == 1) {
			return true;
		}
		return false;
	}
	
	/**
	 * Desloca Horário para baixo
	 * @throws BaseException 
	 */
	public void deslocarHorariosBotaoBaixo(Integer agdSeq, Date dtAgenda, Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam, Short sciSeqpCombo,
			Short unfSeq, Short espSeq, String nomeUsuario) throws BaseException {
		
		MbcAgendas agendaSelecionada = getMbcAgendasDAO().obterPorChavePrimaria(agdSeq);
		Date hrAgendaSelecionada = agendaSelecionada.getDthrPrevInicio();
		Byte ordemOverbookingInicio = agendaSelecionada.getOrdemOverbooking();
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		List<MbcAgendas> escalas = getMbcAgendasDAO().buscarAgendasEmEscala(unfSeq, dtAgenda, unfSeq,
				sciSeqpCombo, null, null, null, null, false, false);
		
		MbcSalaCirurgicaId idSala = new MbcSalaCirurgicaId(unfSeq, sciSeqpCombo);
		MbcSalaCirurgica sala = getBlocoCirurgicoFacade().obterSalaCirurgicaPorId(idSala);
		Integer indiceAgendaSelecionada = 0;
		for(int i=0; i < escalas.size(); i++) {
			if(escalas.get(i).getSeq().equals(agdSeq)) {
				indiceAgendaSelecionada = i;
				continue;
			}
			if(isItemBloqueado(escalas.get(i), pucSerMatriculaParam, pucSerVinCodigoParam)) {
				continue;
			}
			if(agendaSelecionada.getDthrPrevInicio() != null) {
				if(escalas.get(i).getDthrPrevInicio() != null
						&& getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(escalas.get(i).getDthrPrevInicio()) <=
							getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(agendaSelecionada.getDthrPrevInicio())) {
					continue;
				}
			} else {
				if(escalas.get(i).getDthrPrevInicio() != null
						|| escalas.get(i).getOrdemOverbooking() <= agendaSelecionada.getOrdemOverbooking()) {
					continue;
				}
			}
			
			if(!isOverbooking(agendaSelecionada)) {
				Date hrAgendaPosterior = escalas.get(i).getDthrPrevInicio();
				
				agendaSelecionada.setDthrPrevInicio(null);
				agendaSelecionada.setDthrPrevFim(null);
				agendaSelecionada.setOrdemOverbooking(ordemOverbookingInicio);
				
				deslocarHorariosBotaoDireita(escalas.get(i).getSeq(), hrAgendaSelecionada, dtAgenda, pucSerMatriculaParam,
						pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sala, unfSeq, espSeq, nomeUsuario, agdSeq);
				
				Byte intervaloAgendaPosterior = getBlocoCirurgicoFacade().buscarIntervaloEscalaCirurgiaProcedimento(escalas.get(i));
				Date novoTempoTotalPosterior = getEscalaPortalPlanejamentoCirurgiaON().adicionarMinuto(escalas.get(i).getDthrPrevFim(),
						intervaloAgendaPosterior.intValue());
				
				if(novoTempoTotalPosterior != null) {
					if((getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(novoTempoTotalPosterior) >
							getEscalaPortalPlanejamentoCirurgiaON().calcularTempoEmMinutos(hrAgendaPosterior) && !estaoJuntos(i, indiceAgendaSelecionada))
							|| estaoJuntos(i, indiceAgendaSelecionada)) {
						agendaSelecionada.setDthrPrevInicio(novoTempoTotalPosterior);
					} else {
						agendaSelecionada.setDthrPrevInicio(hrAgendaPosterior);
					}
					agendaSelecionada.setOrdemOverbooking(null);
				} else {
					Integer ordemOverbooking = escalas.get(i).getOrdemOverbooking() + 1;
					agendaSelecionada.setOrdemOverbooking(ordemOverbooking.byteValue());
					getBlocoCirurgicoFacade().persistirAgenda(agendaSelecionada, servidorLogado);
					if(i+1 >= escalas.size()) {
						recalcularOverbookingsBtDesce(escalas, i+1, agendaSelecionada.getOrdemOverbooking().intValue());
					}
					break;
				}
				
				deslocarHorariosBotaoDireita(agendaSelecionada.getSeq(), agendaSelecionada.getDthrPrevInicio(), dtAgenda, pucSerMatriculaParam,
						pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sala, unfSeq, espSeq, nomeUsuario, null);
				
				if(agendaSelecionada.getDthrPrevInicio() == null) {
					agendaSelecionada.setOrdemOverbooking(Byte.valueOf("1"));
					recalcularOverbookingsBtDesce(escalas, indiceAgendaSelecionada+2, agendaSelecionada.getOrdemOverbooking().intValue());
					getBlocoCirurgicoFacade().persistirAgenda(agendaSelecionada,servidorLogado);
				}
				
				break;
			} else { // é overbooking
				Byte ordemOverbookingSelecionada = agendaSelecionada.getOrdemOverbooking();
				Byte ordemOverbookingPosterior = escalas.get(i).getOrdemOverbooking();
				
				if(i-2 >= 0) {
					escalas.get(i).setOrdemOverbooking(ordemOverbookingSelecionada);
					if(isOverbooking(escalas.get(i-2))) {
						agendaSelecionada.setOrdemOverbooking(ordemOverbookingPosterior);
						getBlocoCirurgicoFacade().persistirAgenda(escalas.get(i),servidorLogado);
						getBlocoCirurgicoFacade().persistirAgenda(agendaSelecionada,servidorLogado);
					} else {
						Byte intervalo = getBlocoCirurgicoFacade().buscarIntervaloEscalaCirurgiaProcedimento(escalas.get(i-2));
						Date tempoTotal = getEscalaPortalPlanejamentoCirurgiaON().adicionarMinuto(escalas.get(i-2).getDthrPrevFim(),
								intervalo.intValue());
						escalas.get(i).setOrdemOverbooking(null);
						escalas.get(i).setDthrPrevInicio(tempoTotal);
						deslocarHorariosBotaoDireita(escalas.get(i).getSeq(), escalas.get(i).getDthrPrevInicio(), dtAgenda, pucSerMatriculaParam,
								pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sala, unfSeq, espSeq, nomeUsuario, null);
						if(isOverbooking(escalas.get(i))) {
							escalas.get(i).setOrdemOverbooking(ordemOverbookingSelecionada);
							Integer ordemOverbooking = escalas.get(i).getOrdemOverbooking() + 1;
							agendaSelecionada.setOrdemOverbooking(ordemOverbooking.byteValue());
							getBlocoCirurgicoFacade().persistirAgenda(agendaSelecionada,servidorLogado);
							getBlocoCirurgicoFacade().persistirAgenda(escalas.get(i),servidorLogado);
						}
					}
				} else {
					agendaSelecionada.setOrdemOverbooking(ordemOverbookingPosterior);
					escalas.get(i).setOrdemOverbooking(ordemOverbookingSelecionada);
					getBlocoCirurgicoFacade().persistirAgenda(agendaSelecionada,servidorLogado);
					getBlocoCirurgicoFacade().persistirAgenda(escalas.get(i),servidorLogado);
				}
				if(i+1 > escalas.size()) {
					recalcularOverbookingsBtSobe(escalas, i+1, agendaSelecionada.getOrdemOverbooking().intValue());
				}
				break;
			}
		}
	}
	
	private void recalcularOverbookingsBtDesce(List<MbcAgendas> listaEscalas, int i, Integer ordemOverbooking
			) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		while(i != listaEscalas.size()) {
			ordemOverbooking++;
			listaEscalas.get(i).setOrdemOverbooking(ordemOverbooking.byteValue());
			getBlocoCirurgicoFacade().persistirAgenda(listaEscalas.get(i),servidorLogado);
			i++;
		}
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade(){
		return this.iBlocoCirurgicoFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	protected EscalaPortalPlanejamentoCirurgiaRN getEscalaPortalPlanejamentoCirurgiaRN() {
		return escalaPortalPlanejamentoCirurgiaRN;
	}
	
	protected EscalaPortalPlanejamentoCirurgiaON getEscalaPortalPlanejamentoCirurgiaON() {
		return escalaPortalPlanejamentoCirurgiaON;
	}
}