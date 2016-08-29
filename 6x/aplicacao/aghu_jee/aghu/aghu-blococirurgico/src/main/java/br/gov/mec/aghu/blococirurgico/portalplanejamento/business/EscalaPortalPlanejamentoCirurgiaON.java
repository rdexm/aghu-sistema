package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.util.CapitalizeEnum;

@Stateless
public class EscalaPortalPlanejamentoCirurgiaON extends BaseBusiness {
	
	private static final String _HIFEN_ = " - ";

	private static final Log LOG = LogFactory.getLog(EscalaPortalPlanejamentoCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@EJB
	private PortalPlanejamentoCirurgia2ON portalPlanejamentoCirurgia2ON;

	@EJB
	private MovimentacoesEscalaPortalPlanejamentoCirurgiaON movimentacoesEscalaPortalPlanejamentoCirurgiaON;

	@EJB
	private IProntuarioOnlineFacade iProntuarioOnlineFacade;

	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private EscalaPortalPlanejamentoCirurgiaRN escalaPortalPlanejamentoCirurgiaRN;

	@EJB
	private RemarcarPacienteAgendaON remarcarPacienteAgendaON;

	@EJB
	private IAghuFacade iAghuFacade;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8085717057144667231L;
	
	public enum EscalaPortalPlanejamentoCirurgiaONExceptionCode implements BusinessExceptionCode {
		MBC_01102, MBC_01074, MBC_00939, MBC_00943, MBC_00944, MBC_01098;
	}
	
	//Agendas planejadas
	public List<EscalaPortalPlanejamentoCirurgiasVO> pesquisarAgendasPlanejadas(Date dtAgenda, Integer pucSerMatricula, Short pucSerVinCodigo,
			Short pucUnfSeq, DominioFuncaoProfissional funProf, Short espSeq, Short unfSeq) {
		List<MbcAgendas> listaAgendasPlanejadas = getMbcAgendasDAO().buscarAgendasPlanejadasParaEscala(dtAgenda,
				pucSerMatricula, pucSerVinCodigo, pucUnfSeq, funProf, espSeq, unfSeq);
		
		List<EscalaPortalPlanejamentoCirurgiasVO> agendasPlanejadas = new ArrayList<EscalaPortalPlanejamentoCirurgiasVO>();
		for(MbcAgendas agenda : listaAgendasPlanejadas) {
			EscalaPortalPlanejamentoCirurgiasVO vo = popularEscalaPortalPlanejamentoCirurgiasVO(agenda, null);
			vo.setPlanejado(true);
			
			agendasPlanejadas.add(vo);
		}
		
		return agendasPlanejadas;
	}

	//Agendas em escala
	public List<EscalaPortalPlanejamentoCirurgiasVO> pesquisarAgendasEmEscala(Date dtAgendaParam, Short sciUnfSeqCombo,
			Short sciSeqpCombo, Short unfSeqParam, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam) {
		MbcProfAtuaUnidCirgsId equipeId = new MbcProfAtuaUnidCirgsId(pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam);
		
		List<MbcAgendas> listaAgendasPlanejadas = getMbcAgendasDAO().buscarAgendasEmEscala(
				unfSeqParam, dtAgendaParam, sciUnfSeqCombo, sciSeqpCombo, null, null, null, null, true, false);
		
		List<EscalaPortalPlanejamentoCirurgiasVO> agendasEscala = new ArrayList<EscalaPortalPlanejamentoCirurgiasVO>();
		
		Date vDtHrInicioOverbooking = null;
		Date vDtHrFimOverbooking = null;
		Byte intervaloOverbooking = null;
		
		for(MbcAgendas agenda : listaAgendasPlanejadas) {
			if(getEscalaPortalPlanejamentoCirurgiaRN().verificarHoraEscala(agenda, pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam,
					espSeqParam, pucUnfSeqParam, dtAgendaParam, sciSeqpCombo)) {
				
				EscalaPortalPlanejamentoCirurgiasVO vo = popularEscalaPortalPlanejamentoCirurgiasVO(agenda, equipeId);
				
				if(vo.getPrevInicio() != null) {
					vDtHrInicioOverbooking = vo.getPrevInicio();
				} else {
					vDtHrInicioOverbooking = adicionarMinuto(vDtHrFimOverbooking, intervaloOverbooking != null
							? intervaloOverbooking.intValue() : 0);
					vo.setDtHrInicioOverbooking(vDtHrInicioOverbooking);
				}
				
				if(vo.getPrevFim() != null) {
					vDtHrFimOverbooking = vo.getPrevFim();
				} else {
					vDtHrFimOverbooking = adicionarMinuto(vDtHrFimOverbooking, calcularTempoEmMinutos(vo.getTempoSala()));
					vo.setDtHrFimOverbooking(vDtHrFimOverbooking);
				}
				intervaloOverbooking = vo.getIntervaloEscala();
				
				String nomeEquipe = null;
				
				if (CoreUtil.modificados(agenda.getProfAtuaUnidCirgs().getId(), equipeId)) {
					nomeEquipe = agenda.getProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsualOuNome();
					
					if(nomeEquipe.length() > 30) {
						nomeEquipe = StringUtils.abbreviate(nomeEquipe, 30);
					}
				}
				
				vo.setEquipe((nomeEquipe != null) ? getAmbulatorioFacade().obterDescricaoCidCapitalizada(nomeEquipe, CapitalizeEnum.PRIMEIRA) : null);
				
				vo.setTitulo(vo.getNomePaciente() + _HIFEN_ + vo.getProntuario() + _HIFEN_ + vo.getProcedimento());
				
				colorirEscala(pucSerMatriculaParam, pucSerVinCodigoParam,
						agenda, vo);
				
				agendasEscala.add(vo);
			}
		}
		
		return agendasEscala;
	}

	private void colorirEscala(Integer pucSerMatriculaParam,
			Short pucSerVinCodigoParam, MbcAgendas agenda,
			EscalaPortalPlanejamentoCirurgiasVO vo) {
		//Colorir escala
		Boolean portalComCirurgia = getProntuarioOnlineFacade().verificarSeEscalaPortalAgendamentoTemCirurgia(
				agenda.getSeq(), agenda.getDtAgenda());
		
		if(vo.getPrevInicio() == null && vo.getPrevFim() == null) {
			vo.setOverbooking(true);
			if(agenda.getIndGeradoSistema()
					|| !agenda.getProfAtuaUnidCirgs().getId().getSerMatricula().equals(pucSerMatriculaParam)
					|| !agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo().equals(pucSerVinCodigoParam)) {
				vo.setEditavel(false);
			}
		} else {
			if(agenda.getIndGeradoSistema() ||
					(portalComCirurgia && (!agenda.getProfAtuaUnidCirgs().getId().getSerMatricula().equals(pucSerMatriculaParam)
							|| !agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo().equals(pucSerVinCodigoParam)))) {
				vo.setEditavel(false);
				vo.setEscala(true);
			} else {
				if(portalComCirurgia) {
					vo.setEscala(true);
				} else if(!agenda.getProfAtuaUnidCirgs().getId().getSerMatricula().equals(pucSerMatriculaParam)
						|| !agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo().equals(pucSerVinCodigoParam)) {
					vo.setEditavel(false);
				}
			}
			if(vo.getEscala() == null || vo.getEscala()) {
				vo.setPlanejado(true);
			}
		}
	}
	
	private EscalaPortalPlanejamentoCirurgiasVO popularEscalaPortalPlanejamentoCirurgiasVO(MbcAgendas agenda, MbcProfAtuaUnidCirgsId equipeId) {
		Boolean abreviou = false;
		EscalaPortalPlanejamentoCirurgiasVO vo = new EscalaPortalPlanejamentoCirurgiasVO();
		
		String nomePaciente = getAmbulatorioFacade().obterDescricaoCidCapitalizada(agenda.getPaciente().getNome(), CapitalizeEnum.TODAS);
		
		if (nomePaciente.length() > 20) {
			vo.setNomePaciente(StringUtils.abbreviate(nomePaciente, 20));
			abreviou = true;
		} else {
			vo.setNomePaciente(nomePaciente);
		}
		vo.setNomePacienteCompleto(nomePaciente);
		
		vo.setProntuario(CoreUtil.formataProntuario(agenda.getPaciente().getProntuario()));
		
		if (agenda.getRegime().getDescricao().length() > 15) {
			vo.setRegime(StringUtils.abbreviate(agenda.getRegime().getDescricao(), 15));
			vo.setRegimeCodigo(agenda.getRegime().getCodigo());
			abreviou = true;
		} else {
			vo.setRegime(agenda.getRegime().getDescricao());
			vo.setRegimeCodigo(agenda.getRegime().getCodigo());
		}
		
		String procedimento = getAmbulatorioFacade().obterDescricaoCidCapitalizada(agenda.getEspProcCirgs().getMbcProcedimentoCirurgicos().getDescricao(),
				CapitalizeEnum.PRIMEIRA);
		
		if (procedimento.length() > 40) {
			vo.setProcedimento(StringUtils.abbreviate(procedimento, 57));
			abreviou = true;
		} else {
			vo.setProcedimento(procedimento);
		}		
		
		vo.setTempoSala(agenda.getTempoSala());
		
		if (agenda.getComentario() != null && agenda.getComentario().length() > 52) {
			vo.setComentario(StringUtils.abbreviate(agenda.getComentario(), 52));
			abreviou = true;
		} else {
			vo.setComentario(agenda.getComentario());
		}
		
		vo.setPrevInicio(agenda.getDthrPrevInicio());
		vo.setPrevFim(agenda.getDthrPrevFim());
		vo.setIntervaloEscala(agenda.getIntervaloEscala());
		
		vo.setAgdSeq(agenda.getSeq());
		if(agenda.getEspecialidade() != null) {
			vo.setEspSeq(agenda.getEspecialidade().getSeq());
		}
		vo.setUnfSeq(agenda.getUnidadeFuncional().getSeq());
		vo.setPucSerMatricula(agenda.getProfAtuaUnidCirgs().getId().getSerMatricula());
		vo.setPucSerVinCodigo(agenda.getProfAtuaUnidCirgs().getId().getSerVinCodigo());
		vo.setPucUnfSeq(agenda.getProfAtuaUnidCirgs().getId().getUnfSeq());
		vo.setPucFuncProf(agenda.getProfAtuaUnidCirgs().getId().getIndFuncaoProf().toString());
		vo.setDtAgenda(agenda.getDtAgenda());
		if(agenda.getSalaCirurgica()!= null && agenda.getSalaCirurgica().getId()!= null ){
			vo.setSciUnfSeq(agenda.getSalaCirurgica().getId().getUnfSeq());
		}
		if(agenda.getSalaCirurgica()!= null && agenda.getSalaCirurgica().getId()!= null ){
			vo.setSciSeqp(agenda.getSalaCirurgica().getId().getSeqp());
		}
		vo.setPciSeq(agenda.getEspProcCirgs().getId().getPciSeq());
		
		vo.setIndGeradoSistema(agenda.getIndGeradoSistema());
		
		vo.setCspCnvCodigo(agenda.getConvenioSaudePlano().getId().getCnvCodigo());
		vo.setCspSeq(agenda.getConvenioSaudePlano().getId().getSeq());
		vo.setPacCodigo(agenda.getPaciente().getCodigo());
		vo.setIndPrecaucaoEspecial(agenda.getIndPrecaucaoEspecial());
		vo.setQtdeProc(agenda.getQtdeProc());
		
		vo.setTextoToolTip(null);
		if(abreviou) {
			vo.setTextoToolTip(montarToolTip(agenda, nomePaciente, procedimento, equipeId));
		}
		
		return vo;
	}

	public String montarToolTip(MbcAgendas agenda, String nomePaciente, String procedimento, MbcProfAtuaUnidCirgsId equipeId) {
		StringBuilder builder = new StringBuilder(34);
		
		builder.append(nomePaciente ).append(_HIFEN_)
		.append(CoreUtil.formataProntuario(agenda.getPaciente().getProntuario())).append(_HIFEN_)
		.append(agenda.getRegime().getDescricao()).append("<br/>")
		.append(procedimento).append(_HIFEN_)
		.append(DateUtil.obterDataFormatada(agenda.getTempoSala(), "hh:mm")).append("<br/>");
		
		if (agenda.getIndSituacao().equals(DominioSituacaoAgendas.AG)) {
			if(agenda.getComentario()!=null){
				if (agenda.getComentario().length() > 60) {
					builder.append(getPortalPlanejamentoCirurgia2ON().getQuebrarToolTip(agenda.getComentario(), 60));
				} else {
					builder.append(agenda.getComentario());
				}
			}	
		} else {
			if (CoreUtil.modificados(agenda.getProfAtuaUnidCirgs().getId(), equipeId)) {
				builder.append(" - equipe ").append(getAmbulatorioFacade().obterDescricaoCidCapitalizada(agenda.getProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsualOuNome(),
						CapitalizeEnum.PRIMEIRA));
			}
		}
		
		return builder.toString();
	}
	
	public Integer calcularTempoEmMinutos(Date date) {
		if(date != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
		}
		return 0;
	}

	public MbcAgendas montarParametroParaBuscarHorariosDisponiveis(
			Integer pucSerMatriculaParam, Short pucSerVinCodigoParam,
			Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Short espSeqParam, Short unfSeqParam, Date dtAgendaParam,
			Short sciSeqpCombo) {
		MbcProfAtuaUnidCirgs prof = new MbcProfAtuaUnidCirgs();
		MbcProfAtuaUnidCirgsId idProf = new MbcProfAtuaUnidCirgsId(pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam);
		prof.setId(idProf);
		
		AghEspecialidades esp = new AghEspecialidades();
		esp.setSeq(espSeqParam);
		
		MbcSalaCirurgica sala = new MbcSalaCirurgica();
		MbcSalaCirurgicaId idSala = new MbcSalaCirurgicaId(unfSeqParam, sciSeqpCombo);
		sala.setId(idSala);
		
		MbcAgendas param = new MbcAgendas();
		param.setProfAtuaUnidCirgs(prof);
		param.setEspecialidade(esp);
		param.setSalaCirurgica(sala);
		param.setDtAgenda(dtAgendaParam);
		return param;
	}
	
	/**
	 * @ORADB p_atualiza_hora_inicio_escala 
	 * @param EscalaPortalPlanejamentoCirurgiasVO
	 * @param Date horaInicioEscala
	 * @throws ApplicationBusinessException 
	 */
	public Date atualizaHoraInicioEscala(Date pHoraInicioEscala, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam, 
			Short espSeq, Short unfSeq, Short sciSeqp, Date dataAgenda) throws ApplicationBusinessException {
		
		MbcAgendas agenda = montarParametroParaBuscarHorariosDisponiveis(
				pucSerMatriculaParam, pucSerVinCodigoParam,
				pucUnfSeqParam, pucFuncProfParam,
				espSeq, unfSeq, dataAgenda,	sciSeqp);
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setSeq(unfSeq);
		agenda.setUnidadeFuncional(unidade);
		
		Date horarioInicioTurno = getMbcCaracteristicaSalaCirgDAO().buscarMaiorHorarioInicioFimTurno(pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, 
				dataAgenda, sciSeqp, agenda, false, false, null);
		
		if(horarioInicioTurno == null) {
			throw new ApplicationBusinessException(EscalaPortalPlanejamentoCirurgiaONExceptionCode.MBC_01102);
		}
		
		List<MbcAgendas> agendas = getMbcAgendasDAO().buscarAgendasEmEscala(
				unfSeq, dataAgenda, unfSeq, sciSeqp, espSeq, null, null, null, true, false);
		List<MbcAgendas> agendasFiltradasPorData = new ArrayList<MbcAgendas>();
		
		for(MbcAgendas agd : agendas) {
			if (agd.getDthrPrevInicio() != null && 
					calcularTempoEmMinutos(agd.getDthrPrevInicio()) >= calcularTempoEmMinutos(horarioInicioTurno)) {
				agendasFiltradasPorData.add(agd);
			}
		}
		
		Date vDthrFimAnt = null;
		Calendar cal = Calendar.getInstance();
		DateUtil.zeraHorario(cal);
		Date horaZerada = cal.getTime();
		
		Date vNovaHora = null;
		Integer countOverbooking = 0;
		Integer count = 0;
		
		for(MbcAgendas escala : agendasFiltradasPorData) {
			if(count > 0) {
				if(!calcularTempoEmMinutos(nvl(escala.getDthrPrevInicio(), adicionarMinuto(vDthrFimAnt, 1))).equals(calcularTempoEmMinutos(vDthrFimAnt))
						&& calcularTempoEmMinutos(nvl(escala.getDthrPrevInicio(), adicionarMinuto(vDthrFimAnt, 1)))
							> calcularTempoEmMinutos(nvl(pHoraInicioEscala, horaZerada))
						&& vNovaHora == null) {
					vNovaHora = vDthrFimAnt;
				}
			} else if(!calcularTempoEmMinutos(nvl(escala.getDthrPrevInicio(), new Date())).equals(calcularTempoEmMinutos(horarioInicioTurno))
					&& calcularTempoEmMinutos(nvl(escala.getDthrPrevInicio(), horaZerada)) > calcularTempoEmMinutos(nvl(pHoraInicioEscala, horaZerada))) {
				vNovaHora = horarioInicioTurno;
			}
			
			if(escala.getDthrPrevInicio() == null) {
				countOverbooking++;
			}
			
			vDthrFimAnt = adicionarMinuto(escala.getDthrPrevFim(), obterIntervaloEscala(escala));
			count++;
		}
		
		if(count.equals(0) || count.equals(countOverbooking)) {
			return horarioInicioTurno;
		} else if(count > 0 && vDthrFimAnt == null && vNovaHora == null) {
			return horarioInicioTurno;
		} else if(vNovaHora == null) {
			return vDthrFimAnt;
		} else {
			return vNovaHora;
		}
	}

	private Integer obterIntervaloEscala(MbcAgendas escala) {
		Integer intervaloEscala = 0;
		if(escala.getIntervaloEscala() != null) {
			intervaloEscala = escala.getIntervaloEscala().intValue();
		}
		return intervaloEscala;
	}
	
	public Date adicionarMinuto(Date data, Integer min) {
		if(data != null) {
			return DateUtil.adicionaMinutos(data, min != null ? min : 0);
		}
		return null;
	}
	
	private Date nvl(Date data1, Date data2) {
		if(data1 != null) {
			return data1;
		}
		return data2;
	}
	
	
	public Boolean transferirAgendamentosEscala(EscalaPortalPlanejamentoCirurgiasVO planejado,Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam, Short unfSeqParam, Date dtAgendaParam, Date horaEscala,
			Short sciSeqpCombo) throws BaseException {
		Boolean gerouReservaHemoterapica = false;
		Date hrInicioAjustada = obterHoraInicioAjustada(unfSeqParam, dtAgendaParam,
				unfSeqParam, sciSeqpCombo, pucSerMatriculaParam, pucSerVinCodigoParam, horaEscala,
				pucUnfSeqParam, pucFuncProfParam, planejado.getAgdSeq());
		if(hrInicioAjustada != null) {
			horaEscala = hrInicioAjustada;
		}
		
		MbcSalaCirurgicaId idSala = new MbcSalaCirurgicaId(unfSeqParam, sciSeqpCombo);
		MbcSalaCirurgica sala = getBlocoCirurgicoFacade().obterSalaCirurgicaPorId(idSala);
		getMovimentacoesEscalaPortalPlanejamentoCirurgiaON().deslocarHorariosBotaoDireita(planejado.getAgdSeq(), horaEscala, dtAgendaParam, pucSerMatriculaParam,
				pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, sala, unfSeqParam, espSeqParam, servidorLogadoFacade.obterServidorLogado().getUsuario(), null);
		Boolean gerouReservaAux = getEscalaPortalPlanejamentoCirurgiaRN().gerarSangue(planejado.getAgdSeq());
		if(!gerouReservaHemoterapica) {
			gerouReservaHemoterapica = gerouReservaAux;
		}
		
		return gerouReservaHemoterapica;
	}
	
	
	public void verificarHoraTurnoValido(Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam, Short unfSeqParam, Date dtAgendaParam,
			Short sciSeqpCombo, Date vHrEscala) throws ApplicationBusinessException {
				////RN1
			if(!getEscalaPortalPlanejamentoCirurgiaRN().verificarHoraTurnoValido(pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, espSeqParam, unfSeqParam, dtAgendaParam, sciSeqpCombo, vHrEscala)){
					throw new ApplicationBusinessException(EscalaPortalPlanejamentoCirurgiaONExceptionCode.MBC_01098);
			}		
	}
	
	
	public void chamarTelaEscala(Date dtAgenda, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional pucFuncProf, Short unfSeq, Short espSeq) throws ApplicationBusinessException {
		MbcProfAtuaUnidCirgs prof = new MbcProfAtuaUnidCirgs();
		MbcProfAtuaUnidCirgsId idProf = new MbcProfAtuaUnidCirgsId(pucSerMatricula, pucSerVinCodigo, pucUnfSeq, pucFuncProf);
		prof.setId(idProf);
		
		if(!getRemarcarPacienteAgendaON().validarDataReagendamento(dtAgenda, prof, espSeq, unfSeq)) {
			throw new ApplicationBusinessException(EscalaPortalPlanejamentoCirurgiaONExceptionCode.MBC_01074);
		}
		
		if(getRemarcarPacienteAgendaON().verificarEscalaDefinitivaFoiExecutada(dtAgenda, unfSeq)) {
			throw new ApplicationBusinessException(EscalaPortalPlanejamentoCirurgiaONExceptionCode.MBC_00939);
		}
		
		verificaDiasConfigurados(unfSeq, dtAgenda);
	}
	
	public void verificaDiasConfigurados(Short unfSeq, Date data) throws ApplicationBusinessException {
		AghUnidadesFuncionais unid = getAghuFacade().obterUnidadeFuncional(unfSeq);
		Short qtdDiasLimite = 0;
		
		if(unid != null && unid.getQtdDiasLimiteCirg() != null){
			qtdDiasLimite = unid.getQtdDiasLimiteCirg();
		}
		
		if(data.compareTo(DateUtil.adicionaDias(new Date(), qtdDiasLimite.intValue())) > 0) {
			throw new ApplicationBusinessException(EscalaPortalPlanejamentoCirurgiaONExceptionCode.MBC_00943, qtdDiasLimite);
		}
		if(data.compareTo(new Date()) <= 0) {
			throw new ApplicationBusinessException(EscalaPortalPlanejamentoCirurgiaONExceptionCode.MBC_00944);
		}
	}
	
	public void transferirAgendasEmEscalaParaPlanejamento(Date dataAgenda, Short sciUnfSeqCombo,
			Short sciSeqpCombo, Short unfSeqParam, Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Short espSeqParam, Integer agdSeq) throws BaseException {
		//refaz consulta para evitar que indSituacao dos agendamentos tenham sido alterados
		List<EscalaPortalPlanejamentoCirurgiasVO> escalas = pesquisarAgendasEmEscala(dataAgenda, sciUnfSeqCombo, sciSeqpCombo, 
				unfSeqParam, pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, espSeqParam);
		
		for (EscalaPortalPlanejamentoCirurgiasVO escala : escalas) {
			if (agdSeq == null) {
				getEscalaPortalPlanejamentoCirurgiaRN().transferirAgendasEscalaParaPlanejamento(escala, pucSerMatriculaParam, pucSerVinCodigoParam);
			} else if (agdSeq.equals(escala.getAgdSeq())) {
				getEscalaPortalPlanejamentoCirurgiaRN().transferirAgendasEscalaParaPlanejamento(escala, pucSerMatriculaParam, pucSerVinCodigoParam);
				break;
			}
		}
	}
	
	
	/**
	 * @ORADB c_ajusta_hora_inicio
	 * @return dataInicioAjustada
	 */
	public Date obterHoraInicioAjustada(Short unfSeqParam, Date dtAgendaParam, Short sciUnfSeqCombo, Short sciSeqpCombo,
			Integer paramPucSerMatricula, Short paramPucSerVinCodigo, Date horaInicio, Short pucUnfSeqParam, DominioFuncaoProfissional pucFuncProfParam,
			Integer agdSeq) {
		//c_escala_nao_gerado
		List<MbcAgendas> listaAgendasNaoGeradoEscala = getMbcAgendasDAO().buscarAgendasEmEscala(unfSeqParam, dtAgendaParam, sciUnfSeqCombo,
				sciSeqpCombo, null, Boolean.FALSE, paramPucSerMatricula, paramPucSerVinCodigo, true, false);
		
		Integer horaInicioEscalaEmMinutos = calcularTempoEmMinutos(horaInicio);
		
		for(MbcAgendas agd : listaAgendasNaoGeradoEscala) {
			if(getEscalaPortalPlanejamentoCirurgiaRN().verificarHoraEscala(agd, paramPucSerMatricula,
					paramPucSerVinCodigo, pucUnfSeqParam, pucFuncProfParam, agd.getEspecialidade().getSeq(), unfSeqParam,
					dtAgendaParam, sciSeqpCombo)) {
				if(horaInicioEscalaEmMinutos >=
						calcularTempoEmMinutos(agd.getDthrPrevInicio())
					&& horaInicioEscalaEmMinutos <
						(calcularTempoEmMinutos(agd.getDthrPrevFim()) + agd.getIntervaloEscala())) {
					return null;
				}
			}
		}
		
		MbcAgendas agenda = getMbcAgendasDAO().obterPorChavePrimaria(agdSeq);
		Byte intervaloEscala = getBlocoCirurgicoFacade().buscarIntervaloEscalaCirurgiaProcedimento(agenda);
		Integer minutos = calcularTempoEmMinutos(agenda.getTempoSala());
		minutos = minutos + intervaloEscala;
		
		Date vAgdDthrPrevFim = adicionarMinuto(horaInicio, minutos);
		
		
		Date vUltimaHoraFim = null;
		Date vNovaHoraInicio = null;
		Boolean vTrocouHora = false;
		Boolean vEncontrouHorario = false;
		
		List<MbcAgendas> listaAgendasGeradaEscala = getMbcAgendasDAO().buscarAgendasEmEscalaGeradoSistema(unfSeqParam,
				dtAgendaParam, sciUnfSeqCombo, sciSeqpCombo, paramPucSerMatricula, paramPucSerVinCodigo);
		
		for(MbcAgendas agd : listaAgendasGeradaEscala) {
			if(!vEncontrouHorario) {
				
				Integer dtHrPrevFimComIntervalo = calcularTempoEmMinutos(agd.getDthrPrevFim())
					+ agd.getIntervaloEscala();
				
				if((horaInicioEscalaEmMinutos >= calcularTempoEmMinutos(agd.getDthrPrevInicio())
							&& horaInicioEscalaEmMinutos < dtHrPrevFimComIntervalo)
						|| (calcularTempoEmMinutos(vAgdDthrPrevFim) >
								calcularTempoEmMinutos(agd.getDthrPrevInicio())
							&& calcularTempoEmMinutos(vAgdDthrPrevFim) < dtHrPrevFimComIntervalo)
						|| (calcularTempoEmMinutos(vAgdDthrPrevFim) >= dtHrPrevFimComIntervalo)
						|| (calcularTempoEmMinutos(agd.getDthrPrevInicio()) == 
							calcularTempoEmMinutos(vUltimaHoraFim) && vTrocouHora)) {
					vNovaHoraInicio = adicionarMinuto(agd.getDthrPrevFim(), agd.getIntervaloEscala().intValue());
					vTrocouHora = true;
				} else if(calcularTempoEmMinutos(vAgdDthrPrevFim) >= 
								calcularTempoEmMinutos(vUltimaHoraFim)
							&& calcularTempoEmMinutos(vAgdDthrPrevFim) <= dtHrPrevFimComIntervalo) {
					vEncontrouHorario = true;
				}
				vUltimaHoraFim = adicionarMinuto(agd.getDthrPrevFim(), agd.getIntervaloEscala().intValue());
			}
		}
		
		if(vNovaHoraInicio != null) {
			if(calcularTempoEmMinutos(vNovaHoraInicio)
					> calcularTempoEmMinutos(horaInicio)) {
				
				Date vHrFimTurno = getMbcCaracteristicaSalaCirgDAO().buscarMaiorHorarioInicioFimTurno(paramPucSerMatricula, paramPucSerVinCodigo,
						pucUnfSeqParam, pucFuncProfParam, dtAgendaParam, sciSeqpCombo, agenda, true, true, null);
				
				if(calcularTempoEmMinutos(vNovaHoraInicio)
						> calcularTempoEmMinutos(vHrFimTurno)) {
					return vHrFimTurno;
				} else {
					return vNovaHoraInicio;
				}
			}
		}
		
		return null;
	}
	
	public String verificarRegimeMinimoSus(Integer seq){
		MbcAgendas agendaPaciente = null;
		String pergunta = null;
		//cursor c_regime_proced
		List<DominioRegimeProcedimentoCirurgicoSus> regimes = getMbcAgendasDAO().buscarRegimeSusPorId(seq);
		if(regimes.size() > 0 && regimes.get(0) != null){
			DominioRegimeProcedimentoCirurgicoSus regime = regimes.get(0);
		    //cursor c_paciente
		    List<MbcAgendas> agendaPacientes= getMbcAgendasDAO().buscarRegimeSusPacientePorId(seq);
		    agendaPaciente = agendaPacientes.get(0);
		    String paciente ="  ";
		    if(agendaPaciente.getRegime().getOrdem()< regime.getOrdem()){
		    	if(agendaPaciente.getPaciente()!=null){
		    		paciente = agendaPaciente.getPaciente().getNome();
		    	}
		    	String regimeSus = regime.getDescricao();
		    	String regimeMinimo = agendaPaciente.getRegime().getDescricao();
		    	
				pergunta = perguntarRegimeMinimo(paciente,regimeSus,regimeMinimo); 
				return pergunta;
			}
		}
        return pergunta;
		
	}
	
	private String perguntarRegimeMinimo(String paciente, String regimeSus, String regimeMinimo){
		StringBuilder pergunta = new StringBuilder(94);
		pergunta.append("Agenda de ")
		.append(paciente)
		.append(" possui regime ")
		.append(regimeMinimo)
		.append(", porém regime mínimo previsto pelo SUS é ")
		.append(regimeSus)
		.append(".\nTrocar automaticamente? ");
		return pergunta.toString();
	}
	
	
	public MbcAgendas atualizaRegimeMinimoSus(Integer seq) throws BaseException{
		List<DominioRegimeProcedimentoCirurgicoSus> regimes = getMbcAgendasDAO().buscarRegimeSusPorId(seq);
	    MbcAgendas agenda = getBlocoCirurgicoFacade().obterAgendaPorChavePrimaria(seq);
	    agenda.setRegime(regimes.get(0));
	    RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
	    getBlocoCirurgicoFacade().persistirAgenda(agenda, servidorLogado);
	    return agenda;
	}
	
	//c_verif_prox_turno
	public Date buscarProximoTurno(Integer pucSerMatriculaParam, Short pucSerVinCodigoParam, Short pucUnfSeqParam,
			DominioFuncaoProfissional pucFuncProfParam, Date dtAgendaParam, Short sciSeqpCombo, Short unfSeq, Short espSeq,
			Date hrInicioEscala) {
		MbcAgendas agenda = montarParametroParaBuscarHorariosDisponiveis(
				pucSerMatriculaParam, pucSerVinCodigoParam,
				pucUnfSeqParam, pucFuncProfParam,
				espSeq, unfSeq, dtAgendaParam,	sciSeqpCombo);
		
		AghUnidadesFuncionais unidade = new AghUnidadesFuncionais();
		unidade.setSeq(unfSeq);
		agenda.setUnidadeFuncional(unidade);
		
		return getMbcCaracteristicaSalaCirgDAO().buscarMaiorHorarioInicioFimTurno(
				pucSerMatriculaParam, pucSerVinCodigoParam, pucUnfSeqParam, pucFuncProfParam, dtAgendaParam,
				sciSeqpCombo, agenda, false, false, hrInicioEscala);
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}

	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO() {
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected MovimentacoesEscalaPortalPlanejamentoCirurgiaON getMovimentacoesEscalaPortalPlanejamentoCirurgiaON() {
		return movimentacoesEscalaPortalPlanejamentoCirurgiaON;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.iAmbulatorioFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade(){
		return this.iBlocoCirurgicoFacade;
	}
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return this.iProntuarioOnlineFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
	protected PortalPlanejamentoCirurgia2ON getPortalPlanejamentoCirurgia2ON() {
		return portalPlanejamentoCirurgia2ON;
	}
	
	protected RemarcarPacienteAgendaON getRemarcarPacienteAgendaON() {
		return remarcarPacienteAgendaON;
	}
	
	protected EscalaPortalPlanejamentoCirurgiaRN getEscalaPortalPlanejamentoCirurgiaRN() {
		return escalaPortalPlanejamentoCirurgiaRN;
	}
}