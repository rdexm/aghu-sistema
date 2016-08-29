package br.gov.mec.aghu.ambulatorio.business;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.service.spi.ServiceException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacCondicaoAtendimentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacPagadorDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacSituacaoConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacTipoAgendamentoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamInterconsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamSolicitacaoRetornoDAO;
import br.gov.mec.aghu.ambulatorio.vo.BuscaFormaAnteriorVO;
import br.gov.mec.aghu.ambulatorio.vo.ConverterConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendaVo;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioServiceVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeConsultasVO;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaGrade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.dao.FatCandidatosApacOtorrinoDAO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaApacDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacFormaAgendamentoId;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatItemContaApac;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.MamInterconsultas;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;

/**
 * Classe responsável por manter as regras de negócio da entidade AacConsultas.
 *
 */
@Stateless
public class AacConsultasRN extends BaseBusiness {

	private static final long serialVersionUID = -5773553087140847880L;
	private static final Log LOG = LogFactory.getLog(AacConsultasRN.class);

	@Inject
	private AacConsultasDAO aacConsultasDAO;

	@Inject
	private FatItemContaApacDAO fatItemContaApacDAO;
	
	@Inject
	private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsDAO;

	@Inject
	private MamInterconsultasDAO mamInterconsultasDAO;
	
	@Inject
	private MamSolicitacaoRetornoDAO mamSolicitacaoRetornoDAO;
	
	@Inject
	private AacSituacaoConsultasDAO aacSituacaoConsultasDAO;
	
	@Inject
	private FatCandidatosApacOtorrinoDAO fatCandidatosApacOtorrinoDAO;
	
	@Inject
	private AacPagadorDAO aacPagadorDAO;
	
	@Inject
	private AacTipoAgendamentoDAO aacTipoAgendamentoDAO;
	
	@Inject
	private AacCondicaoAtendimentoDAO aacCondicaoAtendimentoDAO;
	
	@Inject
	private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ProcedimentoConsultaRN procedimentoConsultaRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	@EJB
	private SolicitacaoRetornoRN solicitacaoRetornoRN;
	
	@EJB
	private AmbulatorioConsultaRN ambulatorioConsultaRN;
	
	@EJB
	private LiberarConsultasON liberarConsultasON;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	public enum AacConsultasRNExceptionCode implements BusinessExceptionCode {
		AGH_00455, AGH_00456, AGH_00628, AGH_00668, AGH_00630, MENSAGEM_ERRO_COPIAR_PROPRIEDADES,
	}
	
	/**
	 * Realiza a liberação de consultas por óbito, conforme regra AGHP_ATU_PAC_OBITO.
	 * 
	 * @param codigoPaciente - Código do Paciente
	 * @param nomeMicrocomputador - Nome do micro que está realizando a operação
	 * @throws BaseException 
	 */
	public void liberarConsultaPorObito(Integer codigoPaciente, String nomeMicrocomputador) throws BaseException {

		// Consulta C01 da estória #37942
		List<AacConsultas> consultas = getAacConsultasDAO() .pesquisarConsultasPorCodigoPacienteObito(codigoPaciente);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		for (AacConsultas aacConsultas : consultas) {
			try {
				if(aacConsultas.getAtendimento() != null){
					getFaturamentoFacade().atualizarProcedimentosConsulta(aacConsultas.getAtendimento().getSeq(), null, aacConsultas.getNumero(),
						nomeMicrocomputador);
				}else{
					getFaturamentoFacade().atualizarProcedimentosConsulta(null, null, aacConsultas.getNumero(),
							nomeMicrocomputador);
				}

				// Consulta C02 da estória #37942
				List<FatItemContaApac> itensFaturamento = getFaturamentoFacade().pesquisarItensProcedimentoTransferidoPorConsulta(aacConsultas.getNumero());
				
				for (FatItemContaApac fatItemContaApac : itensFaturamento) {
					getFaturamentoFacade().removerFatItemContaApac(fatItemContaApac);
				}
				
				// Consulta C03 da estória #37942
				List<FatProcedAmbRealizado> procedimentos = getFaturamentoFacade().listarProcedAmbRealizadoPorConsulta(aacConsultas.getNumero());
				
				for (FatProcedAmbRealizado fatProcedAmbRealizado : procedimentos) {
					getFaturamentoFacade().excluirProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado, nomeMicrocomputador, new Date());
				}
			} catch (BaseException e) {
				// Consulta C04 da estória #37942
				List<FatProcedAmbRealizado> procedimentos =  getFaturamentoFacade().listarProcedimentosAmbulatoriaisRealizados(aacConsultas.getNumero(),
						new DominioSituacaoProcedimentoAmbulatorio[] {
							DominioSituacaoProcedimentoAmbulatorio.APRESENTADO, DominioSituacaoProcedimentoAmbulatorio.ENCERRADO
						});
				
				for (FatProcedAmbRealizado fatProcedAmbRealizado : procedimentos) {
					getFaturamentoFacade().atualizarProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado,
							getFaturamentoFacade().obterFatProcedAmbRealizadoOriginal(fatProcedAmbRealizado), nomeMicrocomputador, new Date());
				}
				
				throw e;
			}
			
			// Consulta C05 da estória #37942
			getProcedimentoConsultaRN().removerProcedimentosConsultaPorCodigoConsulta(aacConsultas.getNumero(), nomeMicrocomputador);
			
			// Consulta C06 da estória #37942
			boolean existeExame = getExamesFacade().verificarExistenciaSolicitacaoExamePorNumConsulta(aacConsultas.getNumero());
			// Consulta C07 da estória #37942
			boolean existeCirurgia = getBlocoCirurgicoFacade().verificarExistenciaCirurgiaPorConsulta(aacConsultas.getNumero());
			
			if (existeExame || existeCirurgia) {
				// Consulta C08 da estória #37942
				try {
					for (AghAtendimentos atendimento : aacConsultas.getAtendimentos()) {
						atendimento.setConsulta(null);
						atendimento.setIndPacAtendimento(DominioPacAtendimento.O);
						
						getPacienteFacade().atualizarAtendimento(atendimento, getPacienteFacade().obterAghAtendimentosOriginal(atendimento),
								nomeMicrocomputador, servidorLogado, new Date());
					}
				} catch (BaseException e) {
					throw new ApplicationBusinessException(AacConsultasRNExceptionCode.AGH_00455, e);
				}
			} else {
				// Consulta C09 da estória #37942
				try {
					for (AghAtendimentos atendimento : aacConsultas.getAtendimentos()) {
						getPacienteFacade().removerAtendimento(atendimento, nomeMicrocomputador);
					}
					aacConsultas.setAtendimentos(null);
				} catch (BaseException e) {
					throw new ApplicationBusinessException(AacConsultasRNExceptionCode.AGH_00456, e);
				}
			}
			
			// Consulta C10 da estória #37942
			List<MamInterconsultas> interconsultas = getMamInterconsultasDAO().obterInterconsultaPorConsultaMarcada(aacConsultas.getNumero());
			
			try {
				for (MamInterconsultas mamInterconsultas : interconsultas) {
					mamInterconsultas.setConsultaMarcada(null);
					mamInterconsultas.setPendente(DominioIndPendenteAmbulatorio.C);
					mamInterconsultas.setIndCanceladoObito(DominioSimNao.S.toString());
					
					getMarcacaoConsultaRN().atualizarInterconsultas(mamInterconsultas);
				}
			} catch (Exception e) {
				throw new ApplicationBusinessException(AacConsultasRNExceptionCode.AGH_00628, e);
			}
			
			RapServidores servidor = getServidorLogadoFacade().obterServidorLogado();
			
			// Consulta C11 da estória #37942
			List<MamSolicitacaoRetorno> solicitacoes = getMamSolicitacaoRetornoDAO().obterSolicitacaoRetornoPorConsultaRetorno(aacConsultas.getNumero());
			
			try {
				for (MamSolicitacaoRetorno solicitacao : solicitacoes) {
					solicitacao.setAacConsultasByConNumeroRetorno(null);
					solicitacao.setDthrEstorno(new Date());
					solicitacao.setRapServidoresByMamSorSerFk5(servidor);
					
					getSolicitacaoRetornoRN().atualizarSolicitacaoRetorno(getMamSolicitacaoRetornoDAO().obterOriginal(solicitacao), solicitacao);
				}
			} catch (Exception e) {
				throw new ApplicationBusinessException(AacConsultasRNExceptionCode.AGH_00668, e);
			}
			
			finalizarAtualizacaoConsulta(aacConsultas, nomeMicrocomputador);
		}
		
		atualizarInterconsultasAssociadas(codigoPaciente);

		atualizarCirurgiasAssociadas(codigoPaciente);
	}
		
	public void atualizarAacConsultas(Integer conNumero, Integer pacCodigo, Short cspCnvCodigo, Short cspSeq, String stcSituacao,
			Integer retSeq, Short caaSeq, Short tagSeq, Short pgdSeq, String nomeMicrocomputador) throws BaseException {
		
		AacConsultas consulta = this.getAacConsultasDAO().obterPorChavePrimaria(conNumero);
		
		if (pacCodigo != null) {
		AipPacientes paciente = this.getPacienteFacade().obterPacientePorCodigo(pacCodigo);
		consulta.setPaciente(paciente);
		}
		
		if (cspCnvCodigo != null && cspSeq != null) {
			FatConvenioSaudePlano convenioSaudePlano = this.getFaturamentoFacade().obterConvenioSaudePlano(cspCnvCodigo, cspSeq.byteValue());
			consulta.setConvenioSaudePlano(convenioSaudePlano);
		}
		
		AacSituacaoConsultas situacaoConsulta = this.getAmbulatorioFacade().obterSituacaoConsultaPeloId(stcSituacao);
		consulta.setSituacaoConsulta(situacaoConsulta);
		consulta.setIndSituacaoConsulta(situacaoConsulta);
		
		if (retSeq != null) {
			AacRetornos retorno = this.getAmbulatorioFacade().obterRetorno(retSeq);
			consulta.setRetorno(retorno);
		}
		
		if (caaSeq != null) {
		AacFormaAgendamentoId faId = new AacFormaAgendamentoId(caaSeq, tagSeq, pgdSeq);
		AacFormaAgendamento formaAgendamento = this.getAmbulatorioFacade().obterAacFormaAgendamentoPorChavePrimaria(faId);
		consulta.setFormaAgendamento(formaAgendamento);
		consulta.setCondicaoAtendimento(formaAgendamento.getCondicaoAtendimento());
		consulta.setTipoAgendamento(formaAgendamento.getTipoAgendamento());
		consulta.setPagador(formaAgendamento.getPagador());
		}
		getAmbulatorioConsultaRN().atualizarConsulta(getAacConsultasDAO().obterOriginal(consulta), consulta, false, nomeMicrocomputador,
				new Date(), false, false, false, false, false);
	}

	/**
	 * Realiza a atualização de interconsultas assoiadas ao paciente informado.
	 * 
	 * @param codigoPaciente - Código do Paciente
	 * @throws ApplicationBusinessException
	 */
	private void atualizarInterconsultasAssociadas(Integer codigoPaciente) throws ApplicationBusinessException {

		// Consulta C15 da estória #37942
		List<MamInterconsultas> interconsultas = getMamInterconsultasDAO().pesquisaInterconsultaPendentePorPaciente(codigoPaciente);
		
		try {
			for (MamInterconsultas mamInterconsultas : interconsultas) {
				mamInterconsultas.setPendente(DominioIndPendenteAmbulatorio.C);
				
				getMarcacaoConsultaRN().atualizarInterconsultas(mamInterconsultas);
			}
		} catch (Exception e) {
			throw new ApplicationBusinessException(AacConsultasRNExceptionCode.AGH_00630, e);
		}

	}
	
	/**
	 * Realiza a atualização de cirurgias assoiadas ao paciente informado.
	 * 
	 * @param codigoPaciente - Código do Paciente
	 * @throws BaseException 
	 */
	private void atualizarCirurgiasAssociadas(Integer codigoPaciente) throws BaseException {

		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MTVO_CANC_OBITO);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		// Consulta C16 da estória #37942
		List<MbcCirurgias> cirurgias = getBlocoCirurgicoFacade().pesquisarCirurgiasFuturasAgendadasPorPaciente(codigoPaciente);
		
		for (MbcCirurgias mbcCirurgias2 : cirurgias) {
			mbcCirurgias2.setSituacao(DominioSituacaoCirurgia.CANC);
			mbcCirurgias2.setMotivoCancelamento(getBlocoCirurgicoFacade().obterMotivoCancelamentoPorChavePrimaria(parametro.getVlrNumerico().shortValue()));
			
			getBlocoCirurgicoFacade().persistirCirurgia(mbcCirurgias2, servidorLogado);
		}
	}
	
	public void atualizarPagadores(ConverterConsultasVO novaConsulta, Integer numero) {
		
		AacConsultas consulta = aacConsultasDAO.obterPorChavePrimaria(numero);
		AacConsultas consultaOriginal = aacConsultasDAO.obterOriginal(consulta);
		
		AacPagador pagador = aacPagadorDAO.obterPorChavePrimaria(novaConsulta.getPgdSeq());
		AacTipoAgendamento tipoAgendamento = aacTipoAgendamentoDAO.obterPorChavePrimaria(novaConsulta.getTagSeq());
		AacCondicaoAtendimento condicaoAtendimento = aacCondicaoAtendimentoDAO.obterPorChavePrimaria(novaConsulta.getCaaSeq());

		consultaOriginal.setPagador(pagador);
		consultaOriginal.setTipoAgendamento(tipoAgendamento);
		consultaOriginal.setCondicaoAtendimento(condicaoAtendimento);
		
		this.aacConsultasDAO.merge(consultaOriginal);
	}
	
	/**
	 * Realiza o processo de atualização ou remoção da consulta informada.
	 * 
	 * @param aacConsultas - Consulta a ser atualizada ou removida
	 * @param nomeMicrocomputador - Nome do computador
	 * @throws BaseException
	 */
	private void finalizarAtualizacaoConsulta(AacConsultas aacConsultas, String nomeMicrocomputador) throws BaseException {

		if (aacConsultas.getExcedeProgramacao() != null && aacConsultas.getExcedeProgramacao()) {
			if (getAmbulatorioConsultaRN().verificarCaracteristicaGrade(aacConsultas.getGradeAgendamenConsulta().getSeq(),
					DominioCaracteristicaGrade.AGENDA_PRESCRICAO_QUIMIO)) {
				getLiberarConsultasON().liberaAgenda(aacConsultas.getNumero(), TipoOperacaoEnum.DELETE);
			}
			
			// Consulta C12 da estória #37942
			aacConsultas.setConsulta(null);
			getAmbulatorioConsultaRN().atualizarConsulta(getAacConsultasDAO().obterOriginal(aacConsultas), aacConsultas, false, nomeMicrocomputador,
					new Date(), false, false, false, false, false);

			// Consulta C13 da estória #37942
			getAmbulatorioConsultaRN().excluirConsulta(aacConsultas, nomeMicrocomputador, new Date(), false);
		} else {
			if (getAmbulatorioConsultaRN().verificarCaracteristicaGrade(aacConsultas.getGradeAgendamenConsulta().getSeq(),
					DominioCaracteristicaGrade.AGENDA_PRESCRICAO_QUIMIO)) {
				getLiberarConsultasON().liberaAgenda(aacConsultas.getNumero(), TipoOperacaoEnum.UPDATE);
			}
			
			BuscaFormaAnteriorVO formaAnterior = getLiberarConsultasON().buscaFormaAnterior(aacConsultas.getNumero(), null, null, null);
			
			// Consulta C12 da estória #37942
			aacConsultas.setConsulta(null);
			getAmbulatorioConsultaRN().atualizarConsulta(getAacConsultasDAO().obterOriginal(aacConsultas), aacConsultas, false, nomeMicrocomputador,
					new Date(), false, false, false, false, false);
			
			// Consulta C14 da estória #37942
			AacSituacaoConsultas situacao = getAacSituacaoConsultasDAO().obterSituacaoConsultaPeloId("L");
			
			aacConsultas.setIndSituacaoConsulta(situacao);
			aacConsultas.setSituacaoConsulta(situacao);
			aacConsultas.setConvenioSaudePlano(null);
			aacConsultas.setRetorno(null);
			aacConsultas.setAtendimento(null);
			aacConsultas.setPaciente(null);
			aacConsultas.setMotivo(null);
			aacConsultas.setServidorConsultado(null);
			aacConsultas.setIndFuncionario(null);
			aacConsultas.setIndPendenciaFat(null);
			aacConsultas.setDthrInicio(null);
			aacConsultas.setDthrFim(null);
			aacConsultas.setIndImpressaoBa(null);
			aacConsultas.setCodCentral(null);
			aacConsultas.setConsulta(null);
			aacConsultas.setDthrMarcacao(null);
			if (formaAnterior != null) {
				if (formaAnterior.getPgdSeq() != null) {
					aacConsultas.setPagador(getAacPagadorDAO().obterPagador(formaAnterior.getPgdSeq()));
				}
				if (formaAnterior.getTagSeq() != null) {
					aacConsultas.setTipoAgendamento(getAacTipoAgendamentoDAO().obterPorChavePrimaria(formaAnterior.getTagSeq()));
				}
				if (formaAnterior.getCaaSeq() != null) {
					aacConsultas.setCondicaoAtendimento(getAacCondicaoAtendimentoDAO().obterPorChavePrimaria(formaAnterior.getCaaSeq()));
				}
			}
			aacConsultas.setServidorAtendido(null);
			aacConsultas.setPostoSaude(null);
			aacConsultas.setGrupoAtendAmb(null);
			
			getAmbulatorioConsultaRN().atualizarConsulta(getAacConsultasDAO().obterOriginal(aacConsultas), aacConsultas, false, nomeMicrocomputador,
					new Date(), false, false, false, false, false);
		}
	}
	
/**
	 * #6807
		@P_TROCA_CONS_GRADE
		Este procedimento realiza a troca de grade para uma determinada consulta.
	 * @throws ApplicationBusinessException 
	 **/
	public void trocarConsultaGrade(Integer oldGrade, Integer newGrade, List<GradeConsultasVO> listaConsultasSelecionadas) throws ApplicationBusinessException{
		List<GradeConsultasVO> listaErroConsultasTransferidas = new ArrayList<GradeConsultasVO>();
		FatItemContaApac fatItemContaApac = null;
		FatCandidatosApacOtorrino fatCandidatosApacOtorrino = null;
		for (GradeConsultasVO vo : listaConsultasSelecionadas){
			//cursor c_busca_pmr (c_nro_consulta in number)
			FatProcedAmbRealizado fatProcedAmbRealizados = buscarProcedAmbRealizados(vo.getNumeroConsulta());
			if(fatProcedAmbRealizados != null){
				if(fatProcedAmbRealizados.getSeq() != null){
					fatItemContaApac = fatItemContaApacDAO.obterOriginalPorPmrSeq(fatProcedAmbRealizados.getSeq());
					fatCandidatosApacOtorrino = fatCandidatosApacOtorrinoDAO.buscarCandidatoPorPmrSeq(fatProcedAmbRealizados.getSeq());
					if(fatItemContaApac != null){
						//utilizando método de exclusão já existente, como descrito na estória
						getFaturamentoFacade().removerFatItemContaApac(fatItemContaApac);
						//método de exclusão não existente
						fatCandidatosApacOtorrinoDAO.remover(fatCandidatosApacOtorrino);
					}
				}
			}
			if(buscarConsultasPorGrade(vo.getNumeroConsulta(), oldGrade) == null){
				listaErroConsultasTransferidas.add(vo);
			}
		}
		
		if(listaErroConsultasTransferidas != null && !listaErroConsultasTransferidas.isEmpty()){
			throw new ApplicationBusinessException(
					exibirConsultasNaoTransferidas(listaErroConsultasTransferidas, "TRANSFERIR_CONSULTA_GRADE_ATUALIZADO_ERRO"), Severity.ERROR);
		}
		else{
			for (GradeConsultasVO vo : listaConsultasSelecionadas){
				AacConsultas consulta = buscarConsultasPorGrade(vo.getNumeroConsulta(), oldGrade);
				atualizarConsultasGrades(vo, newGrade,consulta);
			}
		}
	}
	private AacConsultas buscarConsultasPorGrade(Integer numero, Integer oldGrade){
		return aacConsultasDAO.buscarPorNumEGrade(numero,oldGrade);
	}
	
	private AacConsultas atualizarConsultasGrades(GradeConsultasVO vo, Integer newGrade, AacConsultas consulta) throws ApplicationBusinessException{
		AacGradeAgendamenConsultas grade = aacGradeAgendamenConsDAO.obterOriginal(newGrade);
		consulta.setGradeAgendamenConsulta(grade);
		return aacConsultasDAO.atualizar(consulta);
	}
	
	private FatProcedAmbRealizado buscarProcedAmbRealizados(Integer numCons){
		if(numCons != null){
			return fatProcedAmbRealizadoDAO.buscaProcedAmbRealizados(numCons);
		}
		return new FatProcedAmbRealizado ();
	}
	
	//Exibir lista com o numero de todas consultas que geraram erros na transferencia
	private String exibirConsultasNaoTransferidas(List<GradeConsultasVO> listaConsultasSelecionadas, String message){
		StringBuilder consultas = new StringBuilder();
		for (GradeConsultasVO vo : listaConsultasSelecionadas){
			consultas.append(vo.getNumeroConsulta().toString() + StringUtils.LF);
		}
		String conteudo = super.getResourceBundleValue(message);	
		return MessageFormat.format(conteudo,  consultas.toString());
	}
	public GradeAgendaVo obterGradesAgendaVOPorEspecialidadeDataLimiteParametros(Short espSeq, 
			Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg , Date dataLimite) throws ServiceException {
		AacGradeAgendamenConsultas  aacGradeAgendamenConsultas = this.ambulatorioFacade.obterGradesAgendaPorEspecialidadeDataLimiteParametros(espSeq, pPagadorSUS, pTagDemanda, pCondATEmerg, dataLimite);
				
		GradeAgendaVo gradeAgendaVO = null;
				
		if (aacGradeAgendamenConsultas != null){
			gradeAgendaVO = new GradeAgendaVo();		
		    gradeAgendaVO.setGradeSeq(aacGradeAgendamenConsultas.getSeq());
		    gradeAgendaVO.setUnidadeFuncionalSeq(aacGradeAgendamenConsultas.getUnidadeFuncional().getSeq());
		}
		
		return gradeAgendaVO;
	}
	
	public List<GradeAgendamentoAmbulatorioServiceVO> listarQuantidadeConsultasAmbulatorioVO(List<String> sitConsultas, Short espSeq,
			String situacaoMarcado, Short pPagadorSUS, Short pTagDemanda,
			Short pCondATEmerg, boolean isDtConsultaMaiorDtAtual) throws ApplicationBusinessException {
		
		List<GradeAgendamentoAmbulatorioVO> consultas = ambulatorioFacade
				.listarQuantidadeConsultasAmbulatorio(sitConsultas, espSeq, situacaoMarcado, pPagadorSUS, pTagDemanda,
						pCondATEmerg, isDtConsultaMaiorDtAtual);
		
		List<GradeAgendamentoAmbulatorioServiceVO> listaRetorno = new ArrayList<GradeAgendamentoAmbulatorioServiceVO>();
		for (GradeAgendamentoAmbulatorioVO item : consultas) {
			GradeAgendamentoAmbulatorioServiceVO vo = new GradeAgendamentoAmbulatorioServiceVO();
			try {
				PropertyUtils.copyProperties(vo, item);
			} catch (Exception e) {
				throw new ApplicationBusinessException(AacConsultasRNExceptionCode.MENSAGEM_ERRO_COPIAR_PROPRIEDADES, e);
			}
			listaRetorno.add(vo);	
		}
		return listaRetorno;
	}
	
	
	public Integer inserirConsultaEmergencia(Date dataConsulta, Integer gradeSeq, Integer pacCodigo, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio, Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg, String nomeMicrocomputador) throws BaseException {

		AacConsultas consulta = new AacConsultas();
		consulta.setDtConsulta(dataConsulta);
		consulta.setExcedeProgramacao(Boolean.TRUE);
		consulta.setGradeAgendamenConsulta(this.ambulatorioFacade.obterGradeAgendamentoParaMarcacaoConsultaEmergencia(gradeSeq));
		consulta.setPaciente(this.pacienteFacade.buscaPaciente(pacCodigo));
		if (pConvenioSUSPadrao != null && pSusPlanoAmbulatorio != null) {
			consulta.setConvenioSaudePlano(this.faturamentoFacade.obterFatConvenioSaudePlano(pConvenioSUSPadrao, pSusPlanoAmbulatorio));
		}
		if (pCondATEmerg != null) {
			consulta.setCondicaoAtendimento(this.ambulatorioFacade.obterCondicaoAtendimentoPorCodigo(pCondATEmerg));
		}
		if (pTagDemanda != null) {
			consulta.setTipoAgendamento(this.ambulatorioFacade.obterTipoAgendamentoPorCodigo(pTagDemanda));
		}
		if (pPagadorSUS != null) {
			consulta.setPagador(this.ambulatorioFacade.obterPagadorPorCodigo(pPagadorSUS));
		}
		consulta.setSituacaoConsulta(this.ambulatorioFacade.obterSituacaoMarcada());

		AacFormaAgendamentoId formaAgendamentoId = new AacFormaAgendamentoId();
		formaAgendamentoId.setCaaSeq(Short.valueOf(String.valueOf(pCondATEmerg)));
		formaAgendamentoId.setPgdSeq(pPagadorSUS);
		formaAgendamentoId.setTagSeq(pTagDemanda);

		AacFormaAgendamento formaAgendamento = this.ambulatorioFacade.obterAacFormaAgendamentoPorChavePrimaria(formaAgendamentoId);
		consulta.setFormaAgendamento(formaAgendamento);

		AacConsultas consultaRetorno = null;

		consultaRetorno = this.ambulatorioFacade.inserirConsulta(consulta, nomeMicrocomputador, false, true, true, true);
		

		return (consultaRetorno != null) ? consultaRetorno.getNumero() : null;
	}
	
	public Integer atualizarConsultaEmergencia(Integer numeroConsulta, Integer pacCodigo, Date dataHoraInicio, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio,
			String indSitConsulta, String nomeMicrocomputador) throws BaseException {
		
		AacConsultas consulta = this.ambulatorioFacade.obterAacConsulta(numeroConsulta);
		consulta.setPaciente(this.pacienteFacade.buscaPaciente(pacCodigo));
		if (pConvenioSUSPadrao != null && pSusPlanoAmbulatorio != null) {
			consulta.setConvenioSaudePlano(this.faturamentoFacade.obterFatConvenioSaudePlano(pConvenioSUSPadrao, pSusPlanoAmbulatorio));
		}
		if (indSitConsulta != null){
			consulta.setSituacaoConsulta(this.ambulatorioFacade.pesquisarSituacaoConsultaPeloId(indSitConsulta));
		}
		
		if (dataHoraInicio != null){
			consulta.setDthrInicio(dataHoraInicio);
		}
		
		AacConsultas consultaRetorno = null;
		
		consultaRetorno = this.ambulatorioFacade.atualizarConsulta(consulta, null, nomeMicrocomputador, new Date(), false,
					false, false, false);
		
		return (consultaRetorno != null) ? consultaRetorno.getNumero() : null;
	}

	
	
	
	protected AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected ProcedimentoConsultaRN getProcedimentoConsultaRN() {
		return procedimentoConsultaRN;
	}

	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	protected MarcacaoConsultaRN getMarcacaoConsultaRN() {
		return marcacaoConsultaRN;
	}

	protected MamInterconsultasDAO getMamInterconsultasDAO() {
		return mamInterconsultasDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	protected SolicitacaoRetornoRN getSolicitacaoRetornoRN() {
		return solicitacaoRetornoRN;
	}

	protected MamSolicitacaoRetornoDAO getMamSolicitacaoRetornoDAO() {
		return mamSolicitacaoRetornoDAO;
	}

	protected AmbulatorioConsultaRN getAmbulatorioConsultaRN() {
		return ambulatorioConsultaRN;
	}

	protected LiberarConsultasON getLiberarConsultasON() {
		return liberarConsultasON;
	}

	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected AacSituacaoConsultasDAO getAacSituacaoConsultasDAO() {
		return aacSituacaoConsultasDAO;
	}

	protected AacPagadorDAO getAacPagadorDAO() {
		return aacPagadorDAO;
	}

	protected AacTipoAgendamentoDAO getAacTipoAgendamentoDAO() {
		return aacTipoAgendamentoDAO;
	}

	protected AacCondicaoAtendimentoDAO getAacCondicaoAtendimentoDAO() {
		return aacCondicaoAtendimentoDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
}