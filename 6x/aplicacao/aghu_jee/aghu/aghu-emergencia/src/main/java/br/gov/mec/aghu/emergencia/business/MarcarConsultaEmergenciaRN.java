package br.gov.mec.aghu.emergencia.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTriagensDAO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendaVo;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioServiceVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;
import br.gov.mec.aghu.dominio.DominioTipoPrevAtende;
import br.gov.mec.aghu.emergencia.dao.MamAgrupGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamAgrupGrvEspsDAO;
import br.gov.mec.aghu.emergencia.dao.MamCapacidadeAtendDAO;
import br.gov.mec.aghu.emergencia.dao.MamCaractSitEmergDAO;
import br.gov.mec.aghu.emergencia.dao.MamExtratoTriagemDAO;
import br.gov.mec.aghu.emergencia.dao.MamGravidadeDAO;
import br.gov.mec.aghu.emergencia.dao.MamMtvoCheckOutDAO;
import br.gov.mec.aghu.emergencia.dao.MamMvtoTriagemDAO;
import br.gov.mec.aghu.emergencia.dao.MamSituacaoEmergenciaDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgPrevAtendDAO;
import br.gov.mec.aghu.emergencia.dao.MamUnidAtendeEspDAO;
import br.gov.mec.aghu.emergencia.vo.GravidadesVO;
import br.gov.mec.aghu.model.MamAgrupGravidade;
import br.gov.mec.aghu.model.MamAgrupGrvEsps;
import br.gov.mec.aghu.model.MamExtratoTriagem;
import br.gov.mec.aghu.model.MamExtratoTriagemId;
import br.gov.mec.aghu.model.MamGravidade;
import br.gov.mec.aghu.model.MamMvtoTriagem;
import br.gov.mec.aghu.model.MamMvtoTriagemId;
import br.gov.mec.aghu.model.MamTrgPrevAtend;
import br.gov.mec.aghu.model.MamTriagens;
import br.gov.mec.aghu.util.EmergenciaParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;


@Stateless
public class MarcarConsultaEmergenciaRN extends BaseBusiness {

	private static final long serialVersionUID = -2168451805689500113L;
	private static final Log LOG = LogFactory.getLog(MarcarConsultaEmergenciaRN.class);

	public enum MarcaConsultaEmergenciaRNExceptionCode implements BusinessExceptionCode {
		
		MENSAGEM_EMERG_ERRO_MAM_06168, MENSAGEM_EMERG_ERRO_MAM_06170, MENSAGEM_EMERG_ERRO_MAM_02903, MENSAGEM_SERVICO_INDISPONIVEL,
		MENSAGEM_CONSULTAS_NAO_DISPONIVEIS, ERRO_PERMISSAO_MARCAR_CONSULTA_AMBULATORIO, MAM_02902, MAM_02907, ERRO_OBTER_PARAMETRO,
		NAO_EXISTE_CONSULTA_LIVRE_PARA_MARCAR_CONSULTA_AMBULATORIAL;

	}
	
	@Inject
	private MamGravidadeDAO mamGravidadeDAO;
	
	@Inject
	private MamCaractSitEmergDAO mamCaractSitEmergDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MamTriagensDAO mamTriagensDAO;
	
	@Inject
	private MamExtratoTriagemDAO mamExtratoTriagemDAO;
		
	@Inject
	private MamSituacaoEmergenciaDAO mamSituacaoEmergenciaDAO;
	
	@EJB
	private MamTrgEncInternoRN mamTrgEncInternoRN;
	
	@EJB
	private MamExtratoTriagemRN mamExtratoTriagemRN;
	
	@EJB
	private MamTriagensRN mamTriagensRN;
	
	@EJB
	private MamTrgPrevAtendRN mamTrgPrevAtendRN;
	
	@EJB
	private MamMvtoTriagemRN mamMvtoTriagemRN;
	
	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	
	@Inject
	private MamCapacidadeAtendDAO mamCapacidadeAtendDAO;
	
	@Inject
	private MamAgrupGravidadeDAO mamAgrupGravidadeDAO;
	
	@Inject
	private MamAgrupGrvEspsDAO mamAgrupGrvEspsDAO;
	
	@Inject
	private MamTrgPrevAtendDAO mamTrgPrevAtendDAO;
	
	@Inject
	private MamUnidAtendeEspDAO mamUnidAtendeEspDAO;
	
	@Inject
	private MamMvtoTriagemDAO mamMvtoTriagemDAO;
	
	@Inject
	private MamMtvoCheckOutDAO mamMtvoCheckOutDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	/*** Consulta C6 - Utiliza o valor contido no parâmetro P_INTERV_CONSULTA_DIA para definir o dia da consulta
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Date obterDataConsulta() throws ApplicationBusinessException {		
		
		BigDecimal paramValue=null;
		paramValue = (BigDecimal) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_INTERV_CONSULTA_DIA.toString(), "vlrNumerico");		
		Integer pIntervaloConsultaDia = (paramValue != null ) ? paramValue.intValue() : 3;
				
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, (pIntervaloConsultaDia/24/60));
				
		return cal.getTime();
   }
	
  /***
   * RN 03 Executa consulta C3
     Se a consulta C3 retornou resultados, executa consulta C4 
     Caso C4 não retorne resultados retornar falso 
     Retornar verdadeiro, caso contrário
 * @throws ServiceException 
    */
	public Boolean verificaAgendaEmergencia(Short unfSeq) throws ServiceException {
		// Executa consulta C3
		if (unfSeq != null) {
			// executa consulta C4			 
			//Boolean possuiCaracteristicaEmergenciaTerreo = this.configuracaoService.verificarCaracteristicaUnidadeFuncional(seqUnf, ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO.toString());
			return (this.verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO.toString())
					 || verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.EMERGENCIA_OBSTETRICA.toString()));
		}
		else {
			return Boolean.FALSE;
		}
	}
	
	public Boolean verificarCaracteristicaUnidadeFuncional(Short unfSeq, String caracteristicaUnidadeFuncional) throws ServiceException {
		ConstanteAghCaractUnidFuncionais caracteristica = ConstanteAghCaractUnidFuncionais.getEnum(caracteristicaUnidadeFuncional);
		if (caracteristica == null) {
			throw new ServiceException("caracteristicaUnidadeFuncional não encontrada");
		}
		
		return this.aghuFacade.verificarCaracteristicaUnidadeFuncional(unfSeq,	caracteristica);
	}
		
	/***
	 * Verifica se a gravidade do paciente consiste em encaminha-lo para atendimento fora da emergencia
	 * @return 
	 * @throws ServiceException 
	 */	
	public Boolean verificaGravidadeEncaminhamentoPaciente(Integer gradeSeq, Short unfSeq, MamTriagens mamTriagem)  throws ApplicationBusinessException, ServiceException {
		String paramValue = null;
		Boolean permiteSaida = Boolean.FALSE;
		
		paramValue = (String) parametroFacade.obterAghParametroPorNome(EmergenciaParametrosEnum.P_CONSISTE_ENC_X_GRV.toString(), "vlrTexto");		
		
		if (paramValue == null) {
			throw new ApplicationBusinessException(MarcaConsultaEmergenciaRNExceptionCode.ERRO_OBTER_PARAMETRO, EmergenciaParametrosEnum.P_CONSISTE_ENC_X_GRV.toString());
		} 
		
		List<MamGravidade> listaGravidade = mamGravidadeDAO.pesquisarGravadadePorTriagem(mamTriagem);
		if (listaGravidade != null && !listaGravidade.isEmpty()) {
			if (!listaGravidade.get(0).getIndPermiteSaida() || paramValue.equalsIgnoreCase("N")) {
				permiteSaida = Boolean.FALSE;
			} else {
				permiteSaida = Boolean.TRUE;
			}

			//se for encaminhamento externo a unidade devera ser nula
			if (unfSeq != null){
				Boolean isEmergencia = this.verificaAgendaEmergencia(unfSeq);
				
				if ((permiteSaida && !isEmergencia)) {
					return Boolean.TRUE;
				} else if (permiteSaida && isEmergencia) {
					return false;
				}
			} else {
				//retorna se a gravidade permite saida para o paciente que está sendo encaminhado para atendimento externo
				return permiteSaida;
			}
		}
		return false;
	}
	
	public Integer gravarEncaminhamentoInterno(Short espSeq, Long seqTriagem, String hostName) throws BaseException, ServiceException{
		LOG.info("EMERGENCIA2: MarcarConsultaEmergenciaRN");
		LOG.info("EMERGENCIA2: entrou metodo gravarEncaminhamentoInterno");
		Integer consultaNumero = null;
		
		LOG.info("EMERGENCIA2: trg.seq=" + seqTriagem);
		
		MamTriagens mamTriagem = this.mamTriagensDAO.obterPorChavePrimaria(seqTriagem);
		MamTriagens mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(mamTriagem.getSeq());
		
		Short pPagadorSUS = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_PAGADOR_SUS.toString());
		LOG.info("EMERGENCIA2: valor parametro P_PAGADOR_SUS" + pPagadorSUS);
		Short pTagDemanda = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_TAG_DEMANDA.toString());
		LOG.info("EMERGENCIA2: valor parametro P_TAG_DEMANDA" + pTagDemanda);
		Short pCondATEmerg = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_COND_AT_EMERG.toString());
		LOG.info("EMERGENCIA2: valor parametro P_COND_AT_EMERG" + pCondATEmerg);
		Date dataLimite = this.obterDataConsulta();
		GradeAgendaVo gradeAgendaVo = null;
		
		LOG.info("EMERGENCIA2: vai obter grade para agendamento");
		gradeAgendaVo = this.ambulatorioFacade.obterGradesAgendaVOPorEspecialidadeDataLimiteParametros(espSeq, pPagadorSUS, pTagDemanda, pCondATEmerg, dataLimite);
		exibirMensagemAbortar((gradeAgendaVo != null), MarcaConsultaEmergenciaRNExceptionCode.MENSAGEM_EMERG_ERRO_MAM_06168);
		LOG.info("EMERGENCIA2: obteve a grade: " + gradeAgendaVo.getGradeSeq() + " e unidade: " + gradeAgendaVo.getUnidadeFuncionalSeq());
		
		Short pConvenioSUSPadrao = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_CONVENIO_SUS_PADRAO.toString());
		LOG.info("EMERGENCIA2: valor parametro P_CONVENIO_SUS_PADRAO" + pConvenioSUSPadrao);
		Byte pSusPlanoAmbulatorio = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_SUS_PLANO_AMBULATORIO.toString()).byteValue();
		LOG.info("EMERGENCIA2: valor parametro P_SUS_PLANO_AMBULATORIO" + pSusPlanoAmbulatorio);
		
		if (verificaAgendaEmergencia(gradeAgendaVo.getUnidadeFuncionalSeq())) {
			LOG.info("EMERGENCIA2: unidade possui caracteristica ATEND_EMERG_TERREO ou EMERGENCIA_OBSTETRICA");
			//exibirMensagemAbortar((verificaGravidadeEncaminhamentoInterno(gradeAgendaVo, mamTriagem)),MarcaConsultaEmergenciaRNExceptionCode.MENSAGEM_EMERG_ERRO_MAM_06170);
			exibirMensagemAbortar((this.mamTriagensDAO.verificaExisteCaracteriscaListaTriagem(mamTriagem.getSeq())), MarcaConsultaEmergenciaRNExceptionCode.MENSAGEM_EMERG_ERRO_MAM_02903);
			LOG.info("EMERGENCIA2: gravarEncaminhamentoInterno");
			consultaNumero = this.ambulatorioFacade
					.inserirConsultaEmergencia(dataLimite,
							gradeAgendaVo.getGradeSeq(),
							mamTriagem.getPaciente().getCodigo(), pConvenioSUSPadrao,
							pSusPlanoAmbulatorio, pPagadorSUS, pTagDemanda,
							pCondATEmerg, hostName);
			LOG.info("EMERGENCIA2: gerou consulta " + consultaNumero);
			
			this.mamTrgEncInternoRN.inserirTriagemEncInterno(mamTriagem.getSeq(), consultaNumero, hostName);
			LOG.info("EMERGENCIA2: inseriu MAM_TRG_ENC_INTERNO");
			List<Short> segSeq = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.ENC_INTERNO);
			LOG.info("EMERGENCIA2: obteve segSeq:" + segSeq.get(0) + " para caracteristica ENC_INTERNO");
			mamTriagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeq.get(0)));
			this.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, hostName);
			LOG.info("EMERGENCIA2: alterou situacao da triagem");
			this.atualizaPrevisaoAtendimento(espSeq);
			LOG.info("EMERGENCIA2: atualizou previsoes de atendimento");
			
		} else {
			LOG.info("EMERGENCIA2: unidade nao possui caracteristica ATEND_EMERG_TERREO, ira marcar consulta na zona 12 (ambulatorial)");
			// 34842
			exibirMensagemAbortar((getPermissionService().usuarioTemPermissao(
					obterLoginUsuarioLogado(), "marcarConsultaAmbulatorioZona12", "executar")),
					MarcaConsultaEmergenciaRNExceptionCode.ERRO_PERMISSAO_MARCAR_CONSULTA_AMBULATORIO);
			
			exibirMensagemAbortar((this.mamTriagensDAO.verificaExisteCaracteriscaListaTriagem(mamTriagem.getSeq())),
					MarcaConsultaEmergenciaRNExceptionCode.MAM_02902);
			
			exibirMensagemAbortar((verificaGravidadeEncaminhamentoPaciente(gradeAgendaVo.getGradeSeq(), gradeAgendaVo.getUnidadeFuncionalSeq(), mamTriagem)),
					MarcaConsultaEmergenciaRNExceptionCode.MENSAGEM_EMERG_ERRO_MAM_06170);
			
			List<Boolean> listaSituacaoMarcacao = this.mamUnidAtendeEspDAO.obterListaSituacaoMarcacao(mamTriagem.getSeq(), espSeq);
			String situacaoMarcado = "N";
			if (!listaSituacaoMarcacao.isEmpty()) {
				situacaoMarcado = listaSituacaoMarcacao.get(0).equals(Boolean.TRUE) ? "S" : "N";
			}
			List<String> sitConsultas = new ArrayList<String>();
			sitConsultas.add("L");
			List<GradeAgendamentoAmbulatorioServiceVO> listaQuantidadeConsultasAmbulatorio = this.ambulatorioFacade
					.listarQuantidadeConsultasAmbulatorioVO(sitConsultas, espSeq, situacaoMarcado, pPagadorSUS, pTagDemanda, pCondATEmerg, false);
			LOG.info("EMERGENCIA2: quantidade de consultas ambulatoriais existentes, marcadas como LIVRE: "+listaQuantidadeConsultasAmbulatorio != null ?
					listaQuantidadeConsultasAmbulatorio.size() : 0);
			// Atualiza AAC_CONSULTA
			if(listaQuantidadeConsultasAmbulatorio != null && !listaQuantidadeConsultasAmbulatorio.isEmpty()) {
				consultaNumero = this.ambulatorioFacade.atualizarConsultaEmergencia(listaQuantidadeConsultasAmbulatorio.get(0).getNumero(),
						mamTriagem.getPaciente().getCodigo(), null, pConvenioSUSPadrao, pSusPlanoAmbulatorio, "M", hostName);
				LOG.info("EMERGENCIA2: consulta numero: "+consultaNumero+", alterada para MARCADA");
			} else {
				throw new ApplicationBusinessException(MarcaConsultaEmergenciaRNExceptionCode.NAO_EXISTE_CONSULTA_LIVRE_PARA_MARCAR_CONSULTA_AMBULATORIAL);
			}
			
			this.mamTrgEncInternoRN.inserirTriagemEncInterno(mamTriagem.getSeq(), consultaNumero, hostName);
			LOG.info("EMERGENCIA2: inseriu MAM_TRG_ENC_INTERNO");
			List<Short> segSeq = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.ENC_INTERNO);
			mamTriagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeq.get(0)));
			this.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, hostName);
			LOG.info("EMERGENCIA2: atualizou situacao da triagem para ENC_INTERNO");
			mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(mamTriagem.getSeq());
			// Se a consulta marcada for fora da emergência deve-se gerar um check-out automático por este motivo.
			List<Short> segSeqCheckout = mamCaractSitEmergDAO.obterSegSeqParaEncInterno(DominioCaracteristicaEmergencia.CHECK_OUT);
			mamTriagem.setSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeqCheckout.get(0)));
			this.atualizarSituacaoTriagem(mamTriagem, mamTriagemOriginal, hostName);
			LOG.info("EMERGENCIA2: atualizou situacao da triagem para CHECK_OUT pois consulta foi marcada fora da emergencia");
			mamTriagemOriginal = this.mamTriagensDAO.obterOriginal(mamTriagem.getSeq());
			// RN17
			atualizarTriagemMvtTriagemPorTipoMovimento(mamTriagem, mamTriagemOriginal, DominioTipoMovimento.C, hostName);
			LOG.info("EMERGENCIA2: atualizou mamMvtoTriagem para C");
			
		}
		LOG.info("EMERGENCIA2: finaliza metodo gravarEncaminhamentoInterno retornando a consulta: "+consultaNumero+" para controller");
		return consultaNumero;
	}
	
	private Short buscarParametrosNumericosEncaminhamentoInterno(String nomeParametro) throws ApplicationBusinessException{
		
		BigDecimal paramValue=null;
		paramValue = (BigDecimal) parametroFacade.obterAghParametroPorNome(nomeParametro, "vlrNumerico");			
		
		return paramValue != null ? paramValue.shortValue() : null;
	}
	
	public void exibirMensagemAbortar(Boolean flag, MarcaConsultaEmergenciaRNExceptionCode mensagemErro) throws ApplicationBusinessException{
		if (!flag){
		    throw new ApplicationBusinessException(mensagemErro);
		}
	}
	
	/***
	 * @throws ApplicationBusinessException 
	 * @ORADB MAMK_SITUACAO_EMERG.MAMP_ATU_SIT_TRIAGEM
	 * RN 08
	 */	
	public void atualizarSituacaoTriagem(MamTriagens mamTriagem, MamTriagens mamTriagemOriginal, String hostName) throws ApplicationBusinessException{
		
		Short segSeq = mamTriagem.getSituacaoEmergencia().getSeq();
		
		if (mamCaractSitEmergDAO.isExisteSituacaoEmerg(segSeq, DominioCaracteristicaEmergencia.ATUALIZA_DATA_FIM)){
			mamTriagem.setDthrFim(new Date());
		} else {
			if (!mamCaractSitEmergDAO.isExisteSituacaoEmerg(segSeq, DominioCaracteristicaEmergencia.ATUALIZA_PAC_ATEND)){
			    mamTriagem.setDthrFim(null);
			    mamTriagem.setIndPacAtendimento(DominioPacAtendimento.S);
		    } else {
			   mamTriagem.setIndPacAtendimento(DominioPacAtendimento.N);
			   if (mamTriagemOriginal.getDthrFim() == null){
				   mamTriagem.setDthrFim(new Date());
			   }
		   }
		}
		if(!Boolean.FALSE.equals(mamTriagem.getIndPacEmergencia())) {
			mamTriagem.setIndPacEmergencia(!mamCaractSitEmergDAO.isExisteSituacaoEmerg(segSeq, DominioCaracteristicaEmergencia.ATUALIZA_PAC_EMERG));
		}
		
		this.mamTriagensRN.atualizarTriagem(mamTriagem, mamTriagemOriginal, segSeq, hostName);
		
		MamExtratoTriagemId mamExtratoTriagemId = new MamExtratoTriagemId(mamTriagem.getSeq(), this.mamExtratoTriagemDAO.obterMaxSeqPExtratoTriagem(mamTriagem.getSeq()));
		
		MamExtratoTriagem  mamExtratoTriagem = new MamExtratoTriagem();
		mamExtratoTriagem.setId(mamExtratoTriagemId);
		mamExtratoTriagem.setDthrSituacao(new Date());
		mamExtratoTriagem.setMamSituacaoEmergencia(this.mamSituacaoEmergenciaDAO.obterPorChavePrimaria(segSeq));
		
		mamTriagem = mamTriagensDAO.obterPorChavePrimaria(mamTriagem.getSeq());
		
		mamExtratoTriagem.setRapServidores(mamTriagem.getServidorSituacao());
				
		this.mamExtratoTriagemRN.inserir(mamExtratoTriagem, hostName);	
	}
	
	/****
	 * @throws ApplicationBusinessException 
	 * @ORADB MAMK_EMG_GENERICA_4. MAMP_ATLZ_PREV_ATEND 
	 * RN11
	 */	
	public void atualizaPrevisaoAtendimento(Short espSeq) throws ApplicationBusinessException{
		  List<Integer> consultasPacientesEmAtendimento = this.mamTrgEncInternoDAO.obterQuantidadePacienteAtendimento();
		  Long qtdAtendimento = 0L;
		  if(!consultasPacientesEmAtendimento.isEmpty()) {
			  qtdAtendimento = this.ambulatorioFacade.pesquisarConsultasPorEspecialidade(espSeq, consultasPacientesEmAtendimento);
		  }
		  Short capacidadeAtend = this.mamCapacidadeAtendDAO.obterCapacidadeAtend(espSeq,
				  Short.valueOf(qtdAtendimento != null ? qtdAtendimento.toString() : "0") );
		  
		  if (capacidadeAtend == null){
			  capacidadeAtend = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_CAPAC_ATD_PAC_EME.toString());
		  }
		  
		  if(capacidadeAtend == null) {
			  capacidadeAtend = 4;
		  }
		  calcularPrevisaoAtendimento(capacidadeAtend, espSeq, null);
		}
	
	/***
	 * R12 e RN13
	 * @param capacidadeAtend
	 * @throws ApplicationBusinessException 
	 */
	public void calcularPrevisaoAtendimento(Short capacidadeAtend , Short espSeq, List<Integer> listConsultasEspecialidade) throws ApplicationBusinessException{
		Integer intervaloMin = 0;
		
		if(!capacidadeAtend.equals(Short.valueOf("0"))) {
			intervaloMin = 60/capacidadeAtend;
		}
		
		Short pTempoIniCalcCapc = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_TEMPO_INICIAR_CALC_CAPAC.toString());
		if(pTempoIniCalcCapc == null) {
			pTempoIniCalcCapc = 0;
		}
		
		Calendar tempoIniciarCalc = Calendar.getInstance();
		tempoIniciarCalc.add(Calendar.MINUTE, pTempoIniCalcCapc);
		
		Calendar dtHrInicio = Calendar.getInstance();
		dtHrInicio.set(Calendar.MINUTE, 0);
		dtHrInicio.set(Calendar.SECOND, 0);
		dtHrInicio.set(Calendar.MILLISECOND, 0);
		
		if(intervaloMin != 0) {
			do {			
				dtHrInicio.add(Calendar.MINUTE, intervaloMin);
			} while (DateUtil.validaDataMenorIgual(dtHrInicio.getTime(), tempoIniciarCalc.getTime()));
		}
		
		calcularPrevAtend(espSeq, intervaloMin, dtHrInicio);
	}
	
	/**
	 * RN13
	 */
	private void calcularPrevAtend(Short espSeq, Integer intervaloMin, Calendar dtHrInicio) throws ApplicationBusinessException {
		Short pTempoAntAtendConsulta = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_TEMPO_ANTECIP_ATD_CONS.toString());
		
		if (pTempoAntAtendConsulta == null) {
			pTempoAntAtendConsulta = 15;
		}
		
		Long totAntecip = 0L;
		List<Integer> consultas = this.mamTriagensDAO.verificaAtendimentoPrevioGrade(pTempoAntAtendConsulta.intValue());
		if(!consultas.isEmpty()) {
			totAntecip = this.ambulatorioFacade.pesquisarConsultasPorEspecialidade(espSeq, consultas);
		}
		
		if(totAntecip > 0 && intervaloMin != 0) {
			for(int i=0; i < totAntecip; i++) {
				dtHrInicio.add(Calendar.MINUTE, intervaloMin);
			}
		}
		
	    List<GravidadesVO> listGravidadesVo = this.mamTriagensDAO.pesquisarPacientesAtendimento();
	    List<GravidadesVO> listGravidadesValidas = new ArrayList<GravidadesVO>();
	    for(GravidadesVO item : listGravidadesVo) {
	    	List<Integer> consultasGrav = new ArrayList<Integer>();
	    	consultasGrav.add(item.getConNumero());
	    	Long countConsultasGrav = 0L;
	    	if(!consultasGrav.isEmpty()) {
	    		countConsultasGrav = this.ambulatorioFacade.pesquisarConsultasPorEspecialidade(espSeq, consultasGrav);
	    	}
	    	if(countConsultasGrav != null && countConsultasGrav > 0) {
	    		listGravidadesValidas.add(item);
	    	}
	    }
	    
	    recalcularGravarPrevisaoAtendimento(espSeq, intervaloMin, dtHrInicio, listGravidadesValidas);
	}

	private void recalcularGravarPrevisaoAtendimento(Short espSeq,
			Integer intervaloMin, Calendar dtHrInicio,
			List<GravidadesVO> listGravidadesValidas)
			throws ApplicationBusinessException {
		Boolean primeiraExecucao = true;
	    Boolean indImediato = null;
	    Date dthrPrevAtend = null;
	    for (GravidadesVO gravidadesVO : listGravidadesValidas) {
	    	DominioTipoPrevAtende indPrevAtend = null;
			Boolean recalcula = true;
			
			
			MamTrgPrevAtend previsaoAtendimento = this.mamTrgPrevAtendDAO
					.obterDataPrevistaAtend(gravidadesVO.getSeqTriagem());
			
			if(previsaoAtendimento != null) {
				if(previsaoAtendimento.getDthrPrevAtend() != null) {
					if(previsaoAtendimento.getDthrPrevAtend().compareTo(new Date()) <= 0) {
						recalcula = false;
					}
				}
			}
			
			if(recalcula) {
				MamAgrupGrvEsps mamAgrupGrvEsps = this.mamAgrupGrvEspsDAO
						.obterPrevAtendAcolhimentoGravEsp(
								gravidadesVO.getSeqAgrupamentoGravidade(), espSeq);
				
				if(mamAgrupGrvEsps != null) {
					indPrevAtend = mamAgrupGrvEsps.getIndPrevAtend();
				}
				
				if (indPrevAtend == null) {
					MamAgrupGravidade mamAgrupGravidade = this.mamAgrupGravidadeDAO
							.obterPrevAtendAcolhimento(gravidadesVO
									.getSeqAgrupamentoGravidade());
					if(mamAgrupGravidade != null) {
						indPrevAtend = mamAgrupGravidade.getIndPrevAtend();
					}
				}
				
				if (indPrevAtend.equals(DominioTipoPrevAtende.I)) {
					if(intervaloMin == 0) {
						indImediato = false;
					} else {
						indImediato = true;
					}
					dthrPrevAtend = null;
				} else if (indPrevAtend.equals(DominioTipoPrevAtende.N)) {
					indImediato = false;
					dthrPrevAtend = null;
				} else if (indPrevAtend.equals(DominioTipoPrevAtende.C)) {
					indImediato = false;
					if(intervaloMin == 0) {
						dthrPrevAtend = null;
					} else {
						if(primeiraExecucao) {
							primeiraExecucao = false;
						} else {
							dtHrInicio.add(Calendar.MINUTE, intervaloMin);
						}
						dthrPrevAtend = dtHrInicio.getTime();
					}
				}
				if (previsaoAtendimento == null) {
					previsaoAtendimento = new MamTrgPrevAtend();
					previsaoAtendimento.setIndImediato(indImediato);
					previsaoAtendimento.setDthrPrevAtend(dthrPrevAtend);
					previsaoAtendimento.setTriagem(this.mamTriagensDAO.obterPorChavePrimaria(gravidadesVO.getSeqTriagem()));
					
					this.mamTrgPrevAtendRN.inserirPrevisaoAtendimento(previsaoAtendimento);
				} else {
					previsaoAtendimento.setIndImediato(indImediato);
					previsaoAtendimento.setDthrPrevAtend(dthrPrevAtend);
					previsaoAtendimento.setGeradoEm(new Date());
					this.mamTrgPrevAtendRN.atualizarPrevisaoAtendimento(previsaoAtendimento);
				}
			}
	    }
	}
	
	public List<Integer> verificarConsultasEmergencia(Short espSeq, Long trgSeq) throws ApplicationBusinessException {
		
		List<Integer> consultasMarcadasELivres = new ArrayList<Integer>();
		
		Short pPagadorSUS = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_PAGADOR_SUS.toString());
		Short pTagDemanda = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_TAG_DEMANDA.toString());
		Short pCondATEmerg = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_COND_AT_EMERG.toString());
		Date dataLimite = this.obterDataConsulta();
		GradeAgendaVo gradeAgendaVo = null;

		gradeAgendaVo = this.ambulatorioFacade.obterGradesAgendaVOPorEspecialidadeDataLimiteParametros(espSeq, pPagadorSUS, pTagDemanda, pCondATEmerg, dataLimite);
		
		exibirMensagemAbortar((gradeAgendaVo != null), MarcaConsultaEmergenciaRNExceptionCode.MENSAGEM_EMERG_ERRO_MAM_06168);
		
		try {
			if (!verificaAgendaEmergencia(gradeAgendaVo.getUnidadeFuncionalSeq())) {
				List<Boolean> listaSituacaoMarcacao = this.mamUnidAtendeEspDAO.obterListaSituacaoMarcacao(trgSeq, espSeq);
				String situacaoMarcado = "N";
				if (!listaSituacaoMarcacao.isEmpty()) {
					situacaoMarcado = listaSituacaoMarcacao.get(0).equals(Boolean.TRUE) ? "S" : "N";
				}
				List<String> sitConsultas = new ArrayList<String>();
				sitConsultas.add("L");
				sitConsultas.add("M");
				List<GradeAgendamentoAmbulatorioServiceVO> listaQuantidadeConsultasAmbulatorio = this.ambulatorioFacade
						.listarQuantidadeConsultasAmbulatorioVO(sitConsultas, espSeq, situacaoMarcado, pPagadorSUS, pTagDemanda, pCondATEmerg, false);
				
				boolean possuiConsultaLivre = false;
				
				for (GradeAgendamentoAmbulatorioServiceVO vo : listaQuantidadeConsultasAmbulatorio) {
					if (vo.getIndSituacaoConsulta().equalsIgnoreCase("L")) {
						possuiConsultaLivre = true;
						break;
					}
				}
				if (possuiConsultaLivre) {
					// Começa no 1 pois a consulta atual = Marcadas + 1.
					int marcadas = 1;
					for (GradeAgendamentoAmbulatorioServiceVO item : listaQuantidadeConsultasAmbulatorio) {
						if (item.getIndSituacaoConsulta().equalsIgnoreCase("M")) {
							marcadas++;
						}
					}
					consultasMarcadasELivres.add(marcadas);
					consultasMarcadasELivres.add(listaQuantidadeConsultasAmbulatorio.size());
					
					return consultasMarcadasELivres;
					
				} else {
					throw new ApplicationBusinessException(MarcaConsultaEmergenciaRNExceptionCode.MENSAGEM_CONSULTAS_NAO_DISPONIVEIS);
				}
			}			
		} catch (ServiceException e) {
			throw new ApplicationBusinessException(MarcaConsultaEmergenciaRNExceptionCode.MENSAGEM_SERVICO_INDISPONIVEL);
		}
		return consultasMarcadasELivres;
	}
	
	/****
	 * @throws ApplicationBusinessException 
	 * @ORADB MAMK_SITUACAO_EMERG.MAMP_ATU_MVT_TRIAGEM
	 */	
	public void atualizarTriagemMvtTriagemPorTipoMovimento(MamTriagens triagem, MamTriagens mamTriagemOriginal,
			DominioTipoMovimento tipoMovimento, String nomeComputador) throws ApplicationBusinessException {
		this.mamTriagensRN.atualizarTriagemPorTipoMovimento(triagem, mamTriagemOriginal, tipoMovimento, nomeComputador);
		
		MamMvtoTriagemId mamMvtoTriagemId = new MamMvtoTriagemId(triagem.getSeq(), this.mamMvtoTriagemDAO.obterMaxSeqPMvtoTriagem(triagem.getSeq()));
		
		MamMvtoTriagem mamMvtoTriagem = new MamMvtoTriagem();
		mamMvtoTriagem.setId(mamMvtoTriagemId);
		mamMvtoTriagem.setDthrMvto(new Date());
		mamMvtoTriagem.setTipoMvto(tipoMovimento.toString());
		Short pMvtoCheckOutCons = this.buscarParametrosNumericosEncaminhamentoInterno(EmergenciaParametrosEnum.P_MTVO_CHECK_OUT_CONS.toString());
		if (pMvtoCheckOutCons > 0) {
			mamMvtoTriagem.setMamMtvoCheckOut(this.mamMtvoCheckOutDAO.obterPorChavePrimaria(pMvtoCheckOutCons));
		}
		
		this.mamMvtoTriagemRN.inserir(mamMvtoTriagem);
	}
}
