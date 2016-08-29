package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaAnestesiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaProcedimentoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAnestesiaCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaracteristicaSalaCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEspecialidadeProcCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMatOrteseProtCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.EscalaPortalPlanejamentoCirurgiasVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioEstornoConsulta;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioMomentoAgendamento;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioUtilizacaoSala;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgiasId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirg;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirgId;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgsId;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgiasId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgicaId;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendada;
import br.gov.mec.aghu.model.MbcSolicHemoCirgAgendadaId;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirgId;
import br.gov.mec.aghu.model.MptAgendaPrescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class EscalaPortalPlanejamentoCirurgiaBotaoGravarON extends BaseBusiness {
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(EscalaPortalPlanejamentoCirurgiaBotaoGravarON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMatOrteseProtCirgDAO mbcMatOrteseProtCirgDAO;

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;

	@Inject
	private MbcAgendaProcedimentoDAO mbcAgendaProcedimentoDAO;

	@Inject
	private MbcAnestesiaCirurgiasDAO mbcAnestesiaCirurgiasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcCaracteristicaSalaCirgDAO mbcCaracteristicaSalaCirgDAO;

	@Inject
	private MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcAgendaAnestesiaDAO mbcAgendaAnestesiaDAO;

	@Inject
	private MbcEspecialidadeProcCirgsDAO mbcEspecialidadeProcCirgsDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;


	@EJB
	private IProcedimentoTerapeuticoFacade iProcedimentoTerapeuticoFacade;

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private IPacienteFacade iPacienteFacade;

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
	
	public enum EscalaPortalPlanejamentoCirurgiaBotaoGravarONExceptionCode implements BusinessExceptionCode {
		MBC_00932, MBC_00937;
	}
	
	public void salvarEscala(List<EscalaPortalPlanejamentoCirurgiasVO> planejados,List<EscalaPortalPlanejamentoCirurgiasVO> escalas, Short unfSeq, Long dataEscala, Integer pucSerMatriculaReserva,
			Short pucSerVinCodigoReserva, Short pucUnfSeqReserva, DominioFuncaoProfissional pucFuncProfReserva, AghEspecialidades especialidadeReserva) throws BaseException{
	
		if(getRemarcarPacienteAgendaON().verificarEscalaDefinitivaFoiExecutada(new Date(dataEscala), unfSeq)){
			throw new ApplicationBusinessException(EscalaPortalPlanejamentoCirurgiaBotaoGravarONExceptionCode.MBC_00932);
		}
		
		if(!planejados.isEmpty()){
			throw new ApplicationBusinessException(EscalaPortalPlanejamentoCirurgiaBotaoGravarONExceptionCode.MBC_00937);
		}
		
		Date horarioFimTurno = null;
		
		MbcProfAtuaUnidCirgs atuaUnidCirgsReserva = getMbcProfAtuaUnidCirgsDAO().obterPorChavePrimaria(new MbcProfAtuaUnidCirgsId(pucSerMatriculaReserva,pucSerVinCodigoReserva,pucUnfSeqReserva,pucFuncProfReserva));
		
		for(EscalaPortalPlanejamentoCirurgiasVO escala : escalas){
			Integer ordemOverbooking = 1;
			MbcAgendas agenda = getMbcAgendasDAO().obterPorChavePrimaria(escala.getAgdSeq());
			horarioFimTurno = getMbcCaracteristicaSalaCirgDAO().buscarMaiorHorarioInicioFimTurno(escala.getPucSerMatricula(), escala.getPucSerVinCodigo(), escala.getUnfSeq(), DominioFuncaoProfissional.getInstance(escala.getPucFuncProf()), escala.getDtAgenda(), escala.getSciSeqp(), agenda, true, true, null); //busca_horario_fim_turno(escala.getSciSeqp());
			List<MbcCirurgias> listaCirurg = getMbcCirurgiasDAO().pesquisarCirurgiasDeReserva(escala.getDtAgenda(), escala.getAgdSeq());

			FatConvenioSaudePlano convenio = getFaturamentoFacade().obterConvenioSaudePlano(escala.getCspCnvCodigo(), escala.getCspSeq()); 
			
			MbcSalaCirurgica salaCirurgica = getMbcSalaCirurgicaDAO().obterPorChavePrimaria(new MbcSalaCirurgicaId(escala.getSciUnfSeq(),escala.getSciSeqp()));
			
			AghEspecialidades espec = getAghuFacade().obterAghEspecialidadesPorChavePrimaria(escala.getEspSeq());
			
			AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(escala.getPacCodigo());

			AghUnidadesFuncionais unf = getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(escala.getUnfSeq());
			
			MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs = getMbcEspecialidadeProcCirgsDAO().obterPorChavePrimaria(new MbcEspecialidadeProcCirgsId(escala.getPciSeq(), escala.getEspSeq()));
			
			RapServidores rapServidor = getRegistroColaboradorFacade().buscarServidor(escala.getPucSerVinCodigo(), escala.getPucSerMatricula());
			
			if(!listaCirurg.isEmpty()){

				MbcCirurgias cirurgia = getMbcCirurgiasDAO().obterPorChavePrimaria(listaCirurg.get(0).getSeq());
				
				ordemOverbooking = atualizarEscala(cirurgia,horarioFimTurno,escala.getAgdSeq(),salaCirurgica,
						escala.getDtAgenda(),convenio,espec,paciente,
						escala.getIndPrecaucaoEspecial(),escala.getRegimeCodigo(),escala.getPrevInicio(),escala.getPrevFim(),
						escala.getTempoSala(),mbcEspecialidadeProcCirgs,escala.getQtdeProc(),rapServidor, unf, DominioFuncaoProfissional.getInstance(escala.getPucFuncProf()),
						escala.getIntervaloEscala(),listaCirurg.get(0).getDataInicioOrdem(),ordemOverbooking,atuaUnidCirgsReserva,especialidadeReserva);
			}else{
//				inserirEscala(MbcCirurgias cirurgia, Date horarioFimTurno, MbcAgendas escala, FatConvenioSaudePlano convenio, 
//						Boolean indPrecaucao, String regime, AghUnidadesFuncionais unf, Integer eprPciSeq, Short eprEspSeq, Byte qtdeProc, 
//						RapServidores usuarioLogado) throws BaseException 
				ordemOverbooking = inserirEscala(null, horarioFimTurno, escala, ordemOverbooking,escala.getDtAgenda(),atuaUnidCirgsReserva,especialidadeReserva,escala.getPucSerMatricula(), 
						escala.getPucSerVinCodigo(), unf, DominioFuncaoProfissional.getInstance(escala.getPucFuncProf()),espec);
			}
			getMbcCirurgiasDAO().flush();
		}
	}

	//INSERE_ESCALA
	private Integer inserirEscala(MbcCirurgias cirurgia, Date horarioFimTurno, EscalaPortalPlanejamentoCirurgiasVO escala, 			
			Integer ordemOverbooking, Date dtAgenda, MbcProfAtuaUnidCirgs atuaUnidCirgsReserva, AghEspecialidades especialidadeReserva, Integer pucSerMatricula,
			Short pucSerVinCodigo, AghUnidadesFuncionais unf,
			DominioFuncaoProfissional pucFuncProf, AghEspecialidades espec) throws BaseException {
	//	Integer crq_seq;
		//DominioFuncaoProfissional indFuncaoProf;
		Boolean indOverbooking = false;
		Date dthrInicioOrdem = null;
		
		indOverbooking = (escala.getPrevInicio() == null);
		if(escala.getPrevInicio() == null){
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtAgenda);
			cal.set(Calendar.MINUTE, (DateUtil.getMinutos(horarioFimTurno).intValue() - ordemOverbooking));
			dthrInicioOrdem = cal.getTime();
			ordemOverbooking++;
		}else{
			dthrInicioOrdem = escala.getPrevInicio();
		}

		Boolean retorna = validarEscalaReservaInserir(espec, pucSerMatricula,
				pucSerVinCodigo, unf, pucFuncProf, 
				atuaUnidCirgsReserva, especialidadeReserva
				);
		if(retorna){
			return ordemOverbooking;
		}		
		
		FatConvenioSaudePlano convenio = getFaturamentoFacade().obterConvenioSaudePlano(escala.getCspCnvCodigo(), escala.getCspSeq()); 
		MbcSalaCirurgica salaCirurgica = getMbcSalaCirurgicaDAO().obterPorChavePrimaria(new MbcSalaCirurgicaId(escala.getSciUnfSeq(),escala.getSciSeqp()));
		
		AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(escala.getPacCodigo());
		MbcAgendas agenda = getBlocoCirurgicoFacade().obterAgendaPorChavePrimaria(escala.getAgdSeq());
		cirurgia = new MbcCirurgias();
		//insere a cirurgia

		cirurgia.setAgenda(agenda);
		cirurgia.setDigitaNotaSala(false);
		cirurgia.setContaminacao(false);
		cirurgia.setNaturezaAgenda(DominioNaturezaFichaAnestesia.ELE);
		cirurgia.setSituacao(DominioSituacaoCirurgia.AGND);
		cirurgia.setUtilizaO2(false);
		cirurgia.setMomentoAgenda(DominioMomentoAgendamento.PRV);
		cirurgia.setUtilizacaoSala(DominioUtilizacaoSala.PRE);
		cirurgia.setUtilizaProAzot(false);
		persistirCirurgia(cirurgia, salaCirurgica, escala.getDtAgenda(), convenio, espec, paciente, escala.getIndPrecaucaoEspecial(), escala.getRegimeCodigo(),
				escala.getPrevInicio(), escala.getPrevFim(), escala.getTempoSala(), unf, escala.getIntervaloEscala(),
				indOverbooking, dthrInicioOrdem);
			
		persistirProcedimentos(cirurgia, escala.getAgdSeq(), escala.getPciSeq(), escala.getEspSeq(), escala.getQtdeProc(), servidorLogadoFacade.obterServidorLogado());
		
		
		persistirSolicHemoterapica(cirurgia.getSeq(), escala.getAgdSeq(), servidorLogadoFacade.obterServidorLogado());
		
		persistirAnestesias(cirurgia.getSeq(), agenda, servidorLogadoFacade.obterServidorLogado());
		
		persistirSolicitacaoEspExecCirgs(agenda, cirurgia.getSeq());
		
		persistirMatOrteseProtese(agenda, cirurgia.getSeq());
		
		persistirProfissisionalResponsavel(cirurgia.getSeq(), escala.getPucSerMatricula(), 
				escala.getPucSerVinCodigo(), escala.getUnfSeq(),
				DominioFuncaoProfissional.getInstance(escala.getPucFuncProf()), true);
		
		verificarFuncaoProfissional(cirurgia, pucSerMatricula, pucSerVinCodigo,
				unf, servidorLogadoFacade.obterServidorLogado());
		
		navegaAgenda(cirurgia.getSeq(), agenda.getPaciente().getCodigo(), escala.getPciSeq(), false);
		
		return ordemOverbooking;
	}

	private Integer atualizarEscala(MbcCirurgias cirurgia, Date horarioFimTurno,
			Integer agdSeq, MbcSalaCirurgica salaCirurgica, Date dtAgenda,
			FatConvenioSaudePlano convenio, AghEspecialidades espec,
			AipPacientes paciente, Boolean indPrecaucaoEspecial, String regime,
			Date prevInicio, Date prevFim, Date tempoSala, MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs,Short qtdeProc, RapServidores rapServidorPuc, AghUnidadesFuncionais unf,
			DominioFuncaoProfissional pucFuncProf, Byte intervaloEscala,
			Date dataInicioOrdem, Integer ordemOverbooking,
			MbcProfAtuaUnidCirgs atuaUnidCirgsReserva, AghEspecialidades especialidadeReserva)  throws BaseException {
		
		Boolean indOverbooking = null;
		Date dthrInicioOrdem = null;
		
		indOverbooking = (prevInicio == null);
		if(prevInicio == null){
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(dtAgenda);
			cal.set(Calendar.MINUTE, (DateUtil.getMinutos(horarioFimTurno).intValue() - ordemOverbooking));
			dthrInicioOrdem = cal.getTime();
			ordemOverbooking++;
		}else{
			dthrInicioOrdem = prevInicio;
		}
		
		Boolean retorna = validarEscalaReserva(espec, rapServidorPuc.getId().getMatricula(),
				rapServidorPuc.getId().getVinCodigo(), unf, pucFuncProf, dataInicioOrdem,
				atuaUnidCirgsReserva, especialidadeReserva, indOverbooking,
				dthrInicioOrdem);
		if(retorna){
			return ordemOverbooking;
		}
		
		persistirCirurgia(cirurgia, salaCirurgica, dtAgenda, convenio, espec, paciente, indPrecaucaoEspecial, regime,
				prevInicio, prevFim, tempoSala, unf, intervaloEscala, indOverbooking, dthrInicioOrdem);
		
		removerMbcProcEspPorCirurgias(cirurgia, servidorLogadoFacade.obterServidorLogado());
		//getBlocoCirurgicoFacade().flush();
		MbcProcEspPorCirurgias espPorCirurgias = new MbcProcEspPorCirurgias();
		espPorCirurgias.setId(new MbcProcEspPorCirurgiasId(cirurgia.getSeq(),mbcEspecialidadeProcCirgs.getId().getPciSeq(),mbcEspecialidadeProcCirgs.getId().getEspSeq(),DominioIndRespProc.AGND));
		espPorCirurgias.setCirurgia(cirurgia);
		espPorCirurgias.setSituacao(DominioSituacao.A);
		espPorCirurgias.setIndPrincipal(Boolean.TRUE);
		espPorCirurgias.setQtd(qtdeProc != null ? qtdeProc.byteValue() : Byte.valueOf("1"));
		espPorCirurgias.setMbcEspecialidadeProcCirgs(mbcEspecialidadeProcCirgs);
		espPorCirurgias.setServidor(servidorLogadoFacade.obterServidorLogado());
		espPorCirurgias.setCriadoEm(new Date());
		getBlocoCirurgicoFacade().persistirProcEspPorCirurgias(espPorCirurgias);
		
		List<MbcSolicHemoCirgAgendada> hemoCirgAgendadas = getMbcSolicHemoCirgAgendadaDAO().pesquisarComponenteSanguineosEscalaCirurgica(cirurgia.getSeq());
		for(MbcSolicHemoCirgAgendada hemo : hemoCirgAgendadas){
			getBlocoCirurgicoFacade().removerMbcSolicHemoCirgAgendada(hemo);
		}
				
		persistirSolicHemoterapica(cirurgia.getSeq(), agdSeq, servidorLogadoFacade.obterServidorLogado());
		MbcAgendas agenda = getBlocoCirurgicoFacade().obterAgendaPorChavePrimaria(agdSeq);
		
		List<MbcAnestesiaCirurgias> listaAnestesiaCirurgias = getMbcAnestesiaCirurgiasDAO().listarAnestesiaCirurgiaTipoAnestesiaPorCrgSeq(cirurgia.getSeq());
		for(MbcAnestesiaCirurgias anestesiaCirg : listaAnestesiaCirurgias){
			getBlocoCirurgicoFacade().removerMbcAnestesiaCirurgias(anestesiaCirg);
		}
		persistirAnestesias(cirurgia.getSeq(), agenda, servidorLogadoFacade.obterServidorLogado());
		
		// exclui as solicitacoes especiais da cirurgia
		List<MbcSolicitacaoEspExecCirg> espExecCirgs = getMbcSolicitacaoEspExecCirgDAO().listarMbcSolicitacaoEspExecCirg(cirurgia);
		for(MbcSolicitacaoEspExecCirg espExecCirg : espExecCirgs){
			getBlocoCirurgicoFacade().removerMbcSolicitacaoEspExecCirg(espExecCirg);
		}
		persistirSolicitacaoEspExecCirgs(agenda, cirurgia.getSeq());
				
		// exclui as orteses/proteses da cirurgia
		List<MbcMatOrteseProtCirg> agendaOrtProteses = getMbcMatOrteseProtCirgDAO().pesquisarOrteseProteseEscalaCirurgicaPorCirurgia(cirurgia.getSeq());
		for(MbcMatOrteseProtCirg matOrteseProtCirg : agendaOrtProteses){
			getBlocoCirurgicoFacade().removerMatOrteseProtese(matOrteseProtCirg);
		}
		persistirMatOrteseProtese(agenda, cirurgia.getSeq());
		
		List<MbcProfCirurgias> mbcProfCirugias = getMbcProfCirurgiasDAO().listarMbcProfCirurgiasControleEscalaPorCrgSeq(cirurgia.getSeq());
		for(MbcProfCirurgias cirg : mbcProfCirugias){
			getBlocoCirurgicoFacade().removerProfessorResponsavel(cirg);
		}
		getMbcProfCirurgiasDAO().flush();
		persistirProfissisionalResponsavel(cirurgia.getSeq(), rapServidorPuc.getId().getMatricula(), rapServidorPuc.getId().getVinCodigo(), unf.getSeq(), pucFuncProf, true);
		
		verificarFuncaoProfissional(cirurgia, rapServidorPuc.getId().getMatricula(), rapServidorPuc.getId().getVinCodigo(),
				unf, servidorLogadoFacade.obterServidorLogado());
		
		navegaAgenda(cirurgia.getSeq(), paciente.getCodigo(),mbcEspecialidadeProcCirgs.getId().getPciSeq() , true);
		
		return ordemOverbooking;
	}

	public void verificarFuncaoProfissional(MbcCirurgias cirurgia,
			Integer pucSerMatricula, Short pucSerVinCodigo,
			AghUnidadesFuncionais unf, RapServidores usuarioLogado)
			throws BaseException {
		DominioFuncaoProfissional indFunc;
		if(!usuarioLogado.getId().getMatricula().equals(pucSerMatricula) && !usuarioLogado.getId().getVinCodigo().equals(pucSerVinCodigo)){
			List<MbcProfAtuaUnidCirgs> profCirgs = getMbcProfAtuaUnidCirgsDAO().pesquisarProfissionalPorUnidade(usuarioLogado.getId().getMatricula(), usuarioLogado.getId().getVinCodigo(), unf.getSeq());

			if(profCirgs == null || profCirgs.isEmpty()){
				indFunc = null;
			} else {
				indFunc = profCirgs.get(0).getId().getIndFuncaoProf();
			}
			
			if(indFunc != null){
				persistirProfissisionalResponsavel(cirurgia.getSeq(), usuarioLogado.getId().getMatricula(), usuarioLogado.getId().getVinCodigo(), unf.getSeq(), indFunc, false);
			}
		}
	}

	public Boolean validarEscalaReserva(AghEspecialidades espec,
			Integer pucSerMatricula, Short pucSerVinCodigo,
			AghUnidadesFuncionais unf, DominioFuncaoProfissional pucFuncProf,
			Date dataInicioOrdem, MbcProfAtuaUnidCirgs atuaUnidCirgsReserva,
			AghEspecialidades especialidadeReserva, Boolean indOverbooking,
			Date dthrInicioOrdem) {
		Boolean retorna = false;
		if(!atuaUnidCirgsReserva.getId().getSerMatricula().equals(pucSerMatricula) || 
				!atuaUnidCirgsReserva.getId().getSerVinCodigo().equals(pucSerVinCodigo) ||
				!atuaUnidCirgsReserva.getId().getUnfSeq().equals(unf.getSeq()) ||
				!atuaUnidCirgsReserva.getId().getIndFuncaoProf().equals(pucFuncProf) ||
				(especialidadeReserva != null && !especialidadeReserva.getSeq().equals(espec.getSeq()))){
			if(!indOverbooking || (dataInicioOrdem != null && DateUtil.isDatasIguais(dataInicioOrdem, dthrInicioOrdem)) || 
					(dataInicioOrdem == null && DateUtil.isDatasIguais(new Date(), dthrInicioOrdem)) ){
				retorna = true;
			}
		}
		return retorna;
	}
	
	public Boolean validarEscalaReservaInserir(AghEspecialidades espec,
			Integer pucSerMatricula, Short pucSerVinCodigo,
			AghUnidadesFuncionais unf, DominioFuncaoProfissional pucFuncProf, MbcProfAtuaUnidCirgs atuaUnidCirgsReserva,
			AghEspecialidades especialidadeReserva) {
		Boolean retorna = false;
		if(!atuaUnidCirgsReserva.getId().getSerMatricula().equals(pucSerMatricula) || 
				!atuaUnidCirgsReserva.getId().getSerVinCodigo().equals(pucSerVinCodigo) ||
				!atuaUnidCirgsReserva.getId().getUnfSeq().equals(unf.getSeq()) ||
				!atuaUnidCirgsReserva.getId().getIndFuncaoProf().equals(pucFuncProf) ||
				(especialidadeReserva != null && !especialidadeReserva.getSeq().equals(espec.getSeq()))){
				retorna = true;
		}
		return retorna;
	}


	public void removerMbcProcEspPorCirurgias(MbcCirurgias cirurgia,
			RapServidores usuarioLogado) throws BaseException {
		List<MbcProcEspPorCirurgias> espPorCirurgias = getMbcProcEspPorCirurgiasDAO().pesquisarMbcProcEspCirurgicoAgendamentoPorCirurgiaAtivo(cirurgia.getSeq());
		for(MbcProcEspPorCirurgias espP : espPorCirurgias){
			getBlocoCirurgicoFacade().removerMbcProcEspPorCirurgias(espP);
		}
	}

	private void persistirCirurgia(MbcCirurgias cirurgia,
			MbcSalaCirurgica salaCirurgica, Date dtAgenda,
			FatConvenioSaudePlano convenio, AghEspecialidades espec,
			AipPacientes paciente, Boolean indPrecaucaoEspecial, String regime,
			Date prevInicio, Date prevFim, Date tempoSala,
			AghUnidadesFuncionais unf, Byte intervaloEscala,
			Boolean indOverbooking, Date dthrInicioOrdem) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		cirurgia.setConvenioSaudePlano(convenio);
		cirurgia.setConvenioSaude(convenio.getConvenioSaude());
		cirurgia.setSalaCirurgica(salaCirurgica);
		cirurgia.setEspecialidade(espec);
		cirurgia.setPaciente(paciente);
		cirurgia.setData(dtAgenda);
		cirurgia.setPrecaucaoEspecial(indPrecaucaoEspecial);
		if(DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO.getCodigo().equals(regime)) {
			cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.A);
		} else {
			cirurgia.setOrigemPacienteCirurgia(DominioOrigemPacienteCirurgia.I);
		}
		if ( prevInicio != null ) {
			cirurgia.setDataPrevisaoInicio(prevInicio);
		}
		if ( prevFim != null ) {
			cirurgia.setDataPrevisaoFim(prevFim);
		}
		Integer hora = calcularTempoEmMinutos(tempoSala)+intervaloEscala;
		Integer minuto = hora % 60;
		cirurgia.setTempoPrevistoHoras(Short.valueOf( String.valueOf(hora/60)) );
		cirurgia.setTempoPrevistoMinutos(Byte.valueOf( String.valueOf(minuto)) );
		cirurgia.setUnidadeFuncional(unf);
		cirurgia.setOverbooking(indOverbooking);
		cirurgia.setDataInicioOrdem(dthrInicioOrdem);
		cirurgia.setOrigemIntLocal(regime.equals("9") ? "9S" : null);
		getBlocoCirurgicoFacade().persistirCirurgia(cirurgia, servidorLogado);
	}
	
	private Integer calcularTempoEmMinutos(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
	}
	
	private void persistirProcedimentos(MbcCirurgias cirurgia, Integer agdSeq, Integer eprPciSeq, Short eprEspSeq, Short qtdeProc, RapServidores usuarioLogado) throws BaseException {
		MbcProcEspPorCirurgias espPorCirurgias = new MbcProcEspPorCirurgias();
		
		espPorCirurgias.setId(new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), eprPciSeq, eprEspSeq, DominioIndRespProc.AGND));
		espPorCirurgias.setCirurgia(cirurgia);
		espPorCirurgias.setSituacao(DominioSituacao.A);
		espPorCirurgias.setQtd(((qtdeProc != null) ? Byte.valueOf(qtdeProc.toString()) : 1));
		espPorCirurgias.setIndPrincipal(true);
		
		//procedimento principal
		getBlocoCirurgicoFacade().persistirProcEspPorCirurgias(espPorCirurgias);

		//outros procedimentos
		List<MbcAgendaProcedimento> outrosProcedimentos = getMbcAgendaProcedimentoDAO().pesquisarPorAgdSeq(agdSeq);
		for (MbcAgendaProcedimento agendaProcedimento : outrosProcedimentos){
			MbcProcEspPorCirurgias procedimento = new MbcProcEspPorCirurgias();

			procedimento.setId(new MbcProcEspPorCirurgiasId(cirurgia.getSeq(), agendaProcedimento.getId().getEprPciSeq(), agendaProcedimento.getId().getEprEspSeq(), DominioIndRespProc.AGND));
			procedimento.setCirurgia(cirurgia);
			procedimento.setSituacao(DominioSituacao.A);
			procedimento.setQtd(((agendaProcedimento.getQtde() != null) ? Byte.valueOf(agendaProcedimento.getQtde().toString()) : 1));
			procedimento.setIndPrincipal(false);
			
			getBlocoCirurgicoFacade().persistirProcEspPorCirurgias(espPorCirurgias);
		}
	}
	
	private void persistirAnestesias(Integer crgSeq, MbcAgendas agenda, RapServidores usuarioLogado) throws BaseException {
		for(MbcAgendaAnestesia anestesia : agenda.getAgendasAnestesias()){
			MbcAnestesiaCirurgias anestesiaCirurgia = new MbcAnestesiaCirurgias();
			
			anestesiaCirurgia.setId(new MbcAnestesiaCirurgiasId(anestesia.getId().getTanSeq(), crgSeq));
			anestesiaCirurgia.setMbcTipoAnestesias(getBlocoCirurgicoFacade()
					.obterMbcTipoAnestesiaPorChavePrimaria(anestesia.getId().getTanSeq()));
			anestesiaCirurgia.setCirurgia(getBlocoCirurgicoFacade().obterCirurgiaPorChavePrimaria(crgSeq));

			getBlocoCirurgicoFacade().persistirAnestesiaCirurgias(anestesiaCirurgia);
		}
	}
	
	private void persistirSolicitacaoEspExecCirgs(MbcAgendas agenda, Integer crgSeq) throws BaseException{

		for(MbcAgendaSolicEspecial solicEspecial : agenda.getAgendasSolicEspeciais()){
			MbcSolicitacaoEspExecCirg espExecCirg = new MbcSolicitacaoEspExecCirg();
			espExecCirg.setId(new MbcSolicitacaoEspExecCirgId(crgSeq, solicEspecial.getId().getNciSeq(), solicEspecial.getId().getSeqp()));
			espExecCirg.setDescricao(solicEspecial.getDescricao());
			espExecCirg.setMbcCirurgias(getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq));
			espExecCirg.setMbcNecessidadeCirurgica(solicEspecial.getMbcNecessidadeCirurgica());
		
			getBlocoCirurgicoFacade().persistirSolicitacaoEspecial(espExecCirg);
		}
	}
	
	private void persistirMatOrteseProtese(MbcAgendas agenda, Integer crgSeq) throws BaseException{
		if(agenda.getAgendasOrtProteses() != null){
			for(MbcAgendaOrtProtese proteses : agenda.getAgendasOrtProteses()){
				MbcMatOrteseProtCirg matOrtProt = new MbcMatOrteseProtCirg();
				matOrtProt.setId(new MbcMatOrteseProtCirgId(proteses.getId().getMatCodigo(), crgSeq));
				matOrtProt.setQtdSolic(proteses.getQtde());
				matOrtProt.setMbcCirurgias(getMbcCirurgiasDAO().obterPorChavePrimaria(crgSeq));
				matOrtProt.setScoMaterial(proteses.getScoMaterial());
				
				getBlocoCirurgicoFacade().persistirMatOrteseProtese(matOrtProt);
			}
		}
	}
	
	private void persistirProfissisionalResponsavel(Integer crgSeq, Integer pucSerMatricula, Short pucSerVinCodigo, Short pucUnfSeq, 
			DominioFuncaoProfissional dominio, Boolean indResponsavel) throws BaseException{
		MbcProfCirurgiasId id = new MbcProfCirurgiasId(crgSeq, pucSerMatricula, pucSerVinCodigo, pucUnfSeq, dominio);
		MbcProfCirurgias profissional = new MbcProfCirurgias();
		
		profissional.setId(id);
		profissional.setIndResponsavel(indResponsavel);
		profissional.setIndRealizou(false);
		profissional.setIndInclEscala(false);
		profissional.setFuncaoProfissional(dominio);
		profissional.setServidorPuc(getRegistroColaboradorFacade().obterRapServidorPorVinculoMatricula(pucSerMatricula, pucSerVinCodigo));
		getBlocoCirurgicoFacade().persistirProfessorResponsavel(profissional);
	}

	private void persistirSolicHemoterapica(Integer crgSeq, Integer agdSeq, RapServidores usuarioLogado) throws BaseException{
		Set<MbcAgendaHemoterapia> agendaHemoterapia =  getMbcAgendaHemoterapiaDAO().listarAgendasHemoterapiaPorAgendaSeq(agdSeq);
		for(MbcAgendaHemoterapia agndaHemo : agendaHemoterapia){
			MbcSolicHemoCirgAgendada hemoCirgAgendada = new MbcSolicHemoCirgAgendada();
			hemoCirgAgendada.setId(new MbcSolicHemoCirgAgendadaId(crgSeq, agndaHemo.getId().getCsaCodigo()));
			hemoCirgAgendada.setQuantidade(agndaHemo.getQtdeUnidade());
			hemoCirgAgendada.setIndLavado(agndaHemo.getIndLavado());
			hemoCirgAgendada.setQtdeMl(agndaHemo.getQtdeMl());
			hemoCirgAgendada.setIndImprLaudo(false);
			hemoCirgAgendada.setIndFiltrado(agndaHemo.getIndFiltrado());
			hemoCirgAgendada.setIndIrradiado(agndaHemo.getIndIrradiado());
			hemoCirgAgendada.setIndAutoTransfusao(false);
			getBlocoCirurgicoFacade().persistirMbcSolicHemoCirgAgendada(hemoCirgAgendada);
		}
	}
	
	public void navegaAgenda(Integer crgSeq, Integer pacCodigo, Integer pciSeq, Boolean operacaoUpdate){
		//@ORADB c_unid_cca
		Short vUnidCCA = verificarUnidFuncionalPossuiCaracteristica();
		//@ORADB c_pci
		MbcProcedimentoCirurgicos procedimento = getMbcProcedimentoCirurgicoDAO().obterPorChavePrimaria(pciSeq);
		
		if(procedimento != null){
			if(!procedimento.getIndAplicacaoQuimio()){
				
				if(operacaoUpdate){
					//verifica se tem agenda
					//c_agp
					MptAgendaPrescricao agendaPrescricao = getProcedimentoTerapeuticoFacade().obterDataAgendaPrescricaoAtiva(crgSeq, vUnidCCA);
					
					//se tem agenda inativa, pois o procedimento
		            //nao e mais de aplicacaoo de quimio
					if(agendaPrescricao != null){
						agendaPrescricao.setSituacao(DominioSituacao.I);
						agendaPrescricao.setIndEstornoConsulta(DominioEstornoConsulta.C);

						//TODO Chamar RN qndo migrar regras de MPT
						getProcedimentoTerapeuticoFacade().atualizarMptAgendaPrescricao(agendaPrescricao);
					}
				}
			}
		}
	}
	
	/*
	 * c_unid_cca	
	 */
	public Short verificarUnidFuncionalPossuiCaracteristica(){
		List<AghUnidadesFuncionais> unidades = getAghuFacade().listarAghUnidadesFuncionais(null);
		List<AghUnidadesFuncionais> unf = new ArrayList<AghUnidadesFuncionais>(0);
		Boolean retorno;
		
		for(AghUnidadesFuncionais unidade: unidades){
			retorno = getAghuFacade().verificarCaracteristicaUnidadeFuncional(unidade.getSeq(), 
					ConstanteAghCaractUnidFuncionais.APLICACAO_QUIMIO_INTRATECAL);
			if(retorno){
				unf.add(unidade); 
			}
		}
		
		if(!unf.isEmpty()){
			return unf.get(0).getSeq();
		} else {
			return null;
		}
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
	
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO(){
		return mbcSalaCirurgicaDAO;
	}
	
	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO(){
		return mbcAgendaHemoterapiaDAO;
	}
	
	protected MbcAnestesiaCirurgiasDAO getMbcAnestesiaCirurgiasDAO(){
		return mbcAnestesiaCirurgiasDAO;
	}
	
	protected MbcCaracteristicaSalaCirgDAO getMbcCaracteristicaSalaCirgDAO(){
		return mbcCaracteristicaSalaCirgDAO;
	}
	
	protected MbcSolicitacaoEspExecCirgDAO getMbcSolicitacaoEspExecCirgDAO(){
		return mbcSolicitacaoEspExecCirgDAO;
	}	
	
	protected MbcMatOrteseProtCirgDAO getMbcMatOrteseProtCirgDAO(){
		return mbcMatOrteseProtCirgDAO;
	}
	
	protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO(){
		return mbcSolicHemoCirgAgendadaDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO(){
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected RemarcarPacienteAgendaON getRemarcarPacienteAgendaON(){
		return remarcarPacienteAgendaON;
	}
		
	protected IFaturamentoFacade getFaturamentoFacade(){
		return this.iFaturamentoFacade;
	}
	
	protected IPacienteFacade getPacienteFacade(){
		return this.iPacienteFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade(){
		return this.iBlocoCirurgicoFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return this.iRegistroColaboradorFacade;
	}
	
	protected IAghuFacade getAghuFacade(){
		return this.iAghuFacade;
	}
	
	protected MbcAgendaProcedimentoDAO getMbcAgendaProcedimentoDAO(){
		return mbcAgendaProcedimentoDAO;
	}
	
	protected MbcAgendaAnestesiaDAO getMbcAgendaAnestesiaDAO(){
		return mbcAgendaAnestesiaDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO(){
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO(){
		return mbcProfAtuaUnidCirgsDAO;
	}
	
	protected MbcEspecialidadeProcCirgsDAO getMbcEspecialidadeProcCirgsDAO(){
		return mbcEspecialidadeProcCirgsDAO;
	}
	
	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO(){
		return mbcProcedimentoCirurgicoDAO;
	}

	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade(){
		return this.iProcedimentoTerapeuticoFacade;
	}
}