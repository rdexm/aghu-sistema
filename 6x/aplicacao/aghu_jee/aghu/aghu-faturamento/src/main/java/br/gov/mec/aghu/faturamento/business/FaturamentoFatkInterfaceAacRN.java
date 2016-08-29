package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioLocalCobrancaProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemProcedimentoAmbulatorialRealizado;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.faturamento.dao.FatLogInterfaceDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedAmbRealizadoDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedHospInternosDAO;
import br.gov.mec.aghu.faturamento.vo.FatProcedAmbRealizadoVO;
import br.gov.mec.aghu.faturamento.vo.FatVariaveisVO;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalarId;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatLogInterface;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;

/**
 * ORADB FATK_INTERFACE_AAC
 * 
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class FaturamentoFatkInterfaceAacRN extends AbstractFatDebugLogEnableRN {


@EJB
private VerificacaoFaturamentoSusRN verificacaoFaturamentoSusRN;

@EJB
private ProcedimentosAmbRealizadosON procedimentosAmbRealizadosON;

private static final Log LOG = LogFactory.getLog(FaturamentoFatkInterfaceAacRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade faturamentoFacade;

@Inject
private FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private FatProcedHospInternosDAO fatProcedHospInternosDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private FatLogInterfaceDAO fatLogInterfaceDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6244030793095199935L;
	private static final DominioModuloCompetencia MODULO_COMPETENCIA_AMB = DominioModuloCompetencia.AMB;

	/**
	 * ORADB: Procedure FATK_INTERFACE_AAC.FATP_PMR_UPDATE_CON
	 * 
	 * Atualiza FatProcedAmbRealizado
	 * 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public void atualizarFaturamentoProcedimentoConsulta(Integer conNumero, Integer phiSeq, Short quantidade, AacRetornos retorno, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		Boolean fatura = false;
		AacRetornos vRetorno = null;
		FatCompetencia competencia = null;
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();

		List<FatCompetencia> listaCompetencias = getVerificacaoFaturamentoSusRN().obterCompetenciasAbertasNaoFaturadasPorModulo(
				MODULO_COMPETENCIA_AMB);

		if (!listaCompetencias.isEmpty()) {
			competencia = listaCompetencias.get(0);
		}

		if (retorno != null) {
			vRetorno = getAmbulatorioFacade().obterDescricaoRetornoPorCodigo(retorno.getSeq());
			if (vRetorno != null) {
				fatura = vRetorno.getFaturaSus();
			}
		} else {
			vRetorno = getAmbulatorioFacade().obterDescricaoRetornoPorCodigo(DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
			if (vRetorno != null) {
				fatura = vRetorno.getFaturaSus();
			}
		}

		if (!fatura) {
			if (phiSeq == null) {
				FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
				List<FatProcedAmbRealizado> listaProcedAmbRealizados = fatProcedAmbRealizadoDAO
						.listarProcedAmbRealizadoOrigemConsultaPorNumeroConsulta(conNumero);

				for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizados) {
					FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedAmbRealizado);

					procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
					procedAmbRealizado.setAlteradoEm(new Date());
					faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
				}
			} else {
				FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
				List<FatProcedAmbRealizado> listaProcedAmbRealizados = fatProcedAmbRealizadoDAO
						.listarProcedAmbRealizadoOrigemConsultaPorNumeroConsultaEPhiSeq(conNumero, phiSeq);

				for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizados) {
					FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedAmbRealizado);

					procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.CANCELADO);
					procedAmbRealizado.setQuantidade(quantidade);
					procedAmbRealizado.setAlteradoEm(new Date());
					faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		} else {
			if (phiSeq == null) {
				FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
				List<FatProcedAmbRealizado> listaProcedAmbRealizados = fatProcedAmbRealizadoDAO
						.listarProcedAmbRealizadoOrigemConsultaPorNumeroConsulta(conNumero);

				for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizados) {
					FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedAmbRealizado);
					procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
					procedAmbRealizado.setFatCompetencia(competencia);
					procedAmbRealizado.setAlteradoEm(new Date());
					faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
				}
			} else {
				FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
				Boolean existeProcedimentoConsultaFaturado = false;
				
				List<FatProcedAmbRealizado> listaProcedAmbRealizadosFatPhiSeq = fatProcedAmbRealizadoDAO
						.listarProcedAmbRealizadoOrigemConsultaPorNumeroConsultaEPhiSeq(conNumero, phiSeq);

				if (!listaProcedAmbRealizadosFatPhiSeq.isEmpty()) {
					existeProcedimentoConsultaFaturado = true;
				}

				if (!existeProcedimentoConsultaFaturado) {
					AacConsultas consulta = getAmbulatorioFacade().obterConsulta(conNumero);
					FatProcedHospInternos procedHospInterno = getFatProcedHospInternosDAO().obterPorChavePrimaria(phiSeq);
					AacConsultaProcedHospitalar consultaProcedHospitalar = new AacConsultaProcedHospitalar();
					AacConsultaProcedHospitalarId consultaProcedHospitalarId = new AacConsultaProcedHospitalarId();
					consultaProcedHospitalarId.setConNumero(conNumero);
					consultaProcedHospitalarId.setPhiSeq(phiSeq);
					consultaProcedHospitalar.setId(consultaProcedHospitalarId);
					consultaProcedHospitalar.setQuantidade(quantidade.byteValue());
					consultaProcedHospitalar.setConsultas(consulta);
					consultaProcedHospitalar.setProcedHospInterno(procedHospInterno);
					inserirFaturamentoProcedimentoConsulta(consultaProcedHospitalar, DateUtil.truncaData(new Date()), nomeMicrocomputador, dataFimVinculoServidor);
				}

				List<FatProcedAmbRealizado> listaProcedAmbRealizadosPrhPhiSeq = fatProcedAmbRealizadoDAO
						.listarProcedAmbRealizadoOrigemConsultaPorNumeroConsultaEPhiSeq(conNumero, phiSeq);

				for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizadosPrhPhiSeq) {
					FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedAmbRealizado);

					procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
					procedAmbRealizado.setQuantidade(quantidade);
					procedAmbRealizado.setFatCompetencia(competencia);
					procedAmbRealizado.setAlteradoEm(new Date());
					faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
				}
			}
		}
	}

	/**
	 * ORADB: Procedure FATK_INTERFACE_AAC.FATP_PMR_INSERE_PRH
	 * 
	 * Insere em FatProcedAmbRealizado
	 * 
	 */
	public void inserirFaturamentoProcedimentoConsulta(AacConsultaProcedHospitalar consultaProcedHospitalar, Date dthrRealizado, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {

		AghAtendimentos atendimento = null;
		FatCompetencia competencia = null;
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		Integer conNumero = consultaProcedHospitalar.getConsultas().getNumero();
		Integer phiSeq = consultaProcedHospitalar.getProcedHospInterno().getSeq();

		List<FatCompetencia> listaCompetencias = getVerificacaoFaturamentoSusRN().obterCompetenciasAbertasNaoFaturadasPorModulo(
				MODULO_COMPETENCIA_AMB);
		if (!listaCompetencias.isEmpty()) {
			competencia = listaCompetencias.get(0);
		}

		List<AghAtendimentos> listaAtendimentos = getAghuFacade()
			.pesquisarAtendimentoPorNumeroConsultaEDthrRealizado(conNumero, dthrRealizado);
		if (!listaAtendimentos.isEmpty()) {
			atendimento = listaAtendimentos.get(0);
		}

		List<FatProcedAmbRealizado> listaProcedAmbRealizados = getFatProcedAmbRealizadoDAO().buscarPorNumeroConsultaEProcedHospInternos(
				conNumero, phiSeq);

		for (FatProcedAmbRealizado procedAmbRealizado : listaProcedAmbRealizados) {
			FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(procedAmbRealizado);

			procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
			procedAmbRealizado.setFatCompetencia(competencia);
			faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(procedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
		}

		if (listaProcedAmbRealizados.isEmpty()) {
			try {
				AacConsultas consulta = consultaProcedHospitalar.getConsultas();
				FatProcedAmbRealizado procedAmbRealizado = new FatProcedAmbRealizado();
				procedAmbRealizado.setCriadoPor(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NOME_USUARIO_CRIADO_POR).getVlrTexto());
				procedAmbRealizado.setCriadoEm(new Date());
				procedAmbRealizado.setDthrRealizado(consulta.getDtConsulta());
				procedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
				procedAmbRealizado.setLocalCobranca(DominioLocalCobrancaProcedimentoAmbulatorialRealizado.B);
				procedAmbRealizado.setQuantidade(consultaProcedHospitalar.getQuantidade().shortValue());
				procedAmbRealizado.setConsulta(consulta);
				procedAmbRealizado.setConsultaProcedHospitalar(consultaProcedHospitalar);
				procedAmbRealizado.setProcedimentoHospitalarInterno(consultaProcedHospitalar.getProcedHospInterno());
				procedAmbRealizado.setServidor(consulta.getServidor());
				procedAmbRealizado.setUnidadeFuncional(consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getUnidadeFuncional());
				procedAmbRealizado.setEspecialidade(consulta.getGradeAgendamenConsulta().getEspecialidade());
				procedAmbRealizado.setPaciente(consulta.getPaciente());
				procedAmbRealizado.setIndOrigem(DominioOrigemProcedimentoAmbulatorialRealizado.CON);
				procedAmbRealizado.setConvenioSaudePlano(consulta.getConvenioSaudePlano());
				procedAmbRealizado.setAtendimento(atendimento);
				procedAmbRealizado.setCid(consultaProcedHospitalar.getCid());
				faturamentoFacade.inserirProcedimentosAmbulatoriaisRealizados(procedAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			} catch (Exception e) {
				LOG.error("Exceção capturada: ", e);
				AipPacientes paciente = null;
				paciente = consultaProcedHospitalar.getConsultas().getPaciente();

				FatLogInterface logInterface = new FatLogInterface();

				logInterface.setSistema(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SIGLA_CONSULTA).getVlrTexto());
				logInterface.setDthrChamada(new Date());
				logInterface.setPacCodigo(paciente != null ? paciente.getCodigo().longValue() : null);
				logInterface.setAacConNumero(conNumero);
				logInterface.setAacPhiSeq(phiSeq);
				logInterface.setSceQuantidade(consultaProcedHospitalar.getQuantidade().intValue());
				logInterface.setMensagem(e.getLocalizedMessage());
				logInterface.setLinProcedure("FATP_PMR_INSERE_PRH");

				getFatLogInterfaceDAO().persistir(logInterface);
			}
		}
	}

	/**
	 * <p>
	 * ORADB: <code>FATK_INTERFACE_AAC.FATP_PMR_CONSULTA_GR</code>
	 * </p>
	 * PROCEDURE FATP_PMR_CONSULTA_GR (P_DATA_INI IN DATE ,P_DATA_FIM IN DATE )
	 * 
	 * @param dataInicio
	 * @param dataFim
	 * @throws BaseException
	 * @throws IllegalStateException
	 */
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void rnFatpPmpConsultaGrupo(final Date dataInicio, final Date dataFim, String nomeMicrocomputador, final Date dataFimVinculoServidor, FatVariaveisVO fatVariaveisVO) throws IllegalStateException, BaseException {
		final String DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDO = "dd/MM/yyyy HH:mm:ss";
		
		FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		Short grpSeq = buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		String caracteristica = buscarAghParametro(AghuParametrosEnum.P_QTD_MINIMA_GRUPO).getVlrTexto();
		DominioSituacaoProcedimentoAmbulatorio situacaoProcedimentoAmbulatorio;
		
		Date vDtConsultaAnt = null;
		Integer vPhiSeqAnt = null;
		Short vUslUnfSeqAnt = null;
		Short vEspSeqAnt = null;
		
		armazenarAtributoExecAgrupamento(Boolean.TRUE);
		
		//UserTransaction userTransaction = obterUserTransaction(null);
		voltarPrm(fatProcedAmbRealizadoDAO, dataInicio, dataFim, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
		//commitUserTransaction(userTransaction);
		
		if (obterAtributoPmrEncerramento()) {
			situacaoProcedimentoAmbulatorio = DominioSituacaoProcedimentoAmbulatorio.CANCELADO;
		} else {
			situacaoProcedimentoAmbulatorio = DominioSituacaoProcedimentoAmbulatorio.CONSULTAS_GRUPO;
		}
		
		logar("SITUACAO " + situacaoProcedimentoAmbulatorio.getCodigo());
		
		List<FatProcedAmbRealizadoVO> fatProcedAmbRealizadVOs = fatProcedAmbRealizadoDAO.buscarPorDataGrupoCaracteristica(dataInicio, dataFim,
				caracteristica, grpSeq, DATE_PATTERN_DDMMYYYY_HORA_MINUTO_SEGUNDO);
		
		List<FatProcedAmbRealizadoVO> processados = new ArrayList<FatProcedAmbRealizadoVO>();
		
		for (FatProcedAmbRealizadoVO fatProcedAmbRealizadVO : fatProcedAmbRealizadVOs) {
			
			// --- se mudou o grupo em consulta, zera a variavel de posicao
			if ((!nvl(vDtConsultaAnt, DateUtil.obterData(2999, 01, 01)).equals(fatProcedAmbRealizadVO.getDthrRealizado()))
				|| (!nvl(vPhiSeqAnt,0).equals(fatProcedAmbRealizadVO.getPhiSeq()))
				|| (!nvl(vUslUnfSeqAnt,0).equals(fatProcedAmbRealizadVO.getUnfSeq()))
				|| (!nvl(vEspSeqAnt,0).equals(fatProcedAmbRealizadVO.getEspSeq()))
				) {
				if (!processados.isEmpty()) {
					processaPrm(fatProcedAmbRealizadoDAO, processados, situacaoProcedimentoAmbulatorio, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
				}
				processados = new ArrayList<FatProcedAmbRealizadoVO>();
			}
			processados.add(fatProcedAmbRealizadVO);
			// --- valora variaveis para controle de valores anteriores
			vDtConsultaAnt = fatProcedAmbRealizadVO.getDthrRealizado();
			vPhiSeqAnt = fatProcedAmbRealizadVO.getPhiSeq();
			vUslUnfSeqAnt = fatProcedAmbRealizadVO.getUnfSeq();
			vEspSeqAnt = fatProcedAmbRealizadVO.getEspSeq();
		}
		
		if (!processados.isEmpty()) {
			processaPrm(fatProcedAmbRealizadoDAO, processados, situacaoProcedimentoAmbulatorio, faturamentoFacade, nomeMicrocomputador, dataFimVinculoServidor);
		}
		
		armazenarAtributoExecAgrupamento(Boolean.FALSE);
	}

	private void processaPrm(final FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO, final List<FatProcedAmbRealizadoVO> processados,
			final DominioSituacaoProcedimentoAmbulatorio situacao, final IFaturamentoFacade faturamentoFacade, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws IllegalStateException,	BaseException {
		
		final FatProcedAmbRealizadoVO fatProcedAmbRealizadVO = processados.get(processados.size() - 1);
		
		boolean ignoraPrimeiro = false;
		
		if (fatProcedAmbRealizadVO.getValorNumerico().intValue() < processados.size()) {
			
			FatProcedAmbRealizadoVO fatProcedAmbRealizadVORetirar = processados.get(0);
			
			logar("RETIRAR PAC PMR " + fatProcedAmbRealizadVORetirar.getSeq());
			
			FatProcedAmbRealizado fatProcedAmbRealizado = fatProcedAmbRealizadoDAO.obterPorChavePrimaria(fatProcedAmbRealizadVORetirar.getSeq());
			FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);

			fatProcedAmbRealizado.setPaciente(null);
			
			faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(fatProcedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			
			ignoraPrimeiro = true;
		}
		
		for (FatProcedAmbRealizadoVO fatProcedAmbRealizadVOCancelar : processados) {
			
			if (!ignoraPrimeiro) {
				logar("CANCELAR PMR " + fatProcedAmbRealizadVOCancelar.getSeq());
				
				FatProcedAmbRealizado fatProcedAmbRealizado = fatProcedAmbRealizadoDAO.obterPorChavePrimaria(fatProcedAmbRealizadVOCancelar.getSeq());
				FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);
			
				fatProcedAmbRealizado.setSituacao(situacao);
				
				faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(fatProcedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor);
			} else {
				ignoraPrimeiro = false;				
			}
			
		}
		
	}
	
	/*public void rnFatpPmpConsultaGrupo(final Date dataInicio, final Date dataFim) throws IllegalStateException, BaseException {
		FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO = getFatProcedAmbRealizadoDAO();
		IFaturamentoFacade faturamentoFacade = getFaturamentoFacade();
		Short grpSeq = buscarAghParametro(AghuParametrosEnum.P_TIPO_GRUPO_CONTA_SUS).getVlrNumerico().shortValue();
		String caracteristica = buscarAghParametro(AghuParametrosEnum.P_QTD_MINIMA_GRUPO).getVlrTexto();
		DominioSituacaoProcedimentoAmbulatorio situacaoProcedimentoAmbulatorio;
		
		Date vDtConsultaAnt = null;
		Integer vPhiSeqAnt = null;
		Short vUslUnfSeqAnt = null;
		Short vEspSeqAnt = null;
		
		int i = -1;
		int lst = 0;

		armazenarAtributoExecAgrupamento(Boolean.TRUE);
		
		voltarPrm(fatProcedAmbRealizadoDAO, dataInicio, dataFim, faturamentoFacade);
		
		if (obterAtributoPmrEncerramento()) {
			situacaoProcedimentoAmbulatorio = DominioSituacaoProcedimentoAmbulatorio.CANCELADO;
		} else {
			situacaoProcedimentoAmbulatorio = DominioSituacaoProcedimentoAmbulatorio.CONSULTAS_GRUPO;
		}
		
		logar("SITUACAO " + situacaoProcedimentoAmbulatorio.getCodigo());
		
		List<FatProcedAmbRealizadoVO> fatProcedAmbRealizadVOs = fatProcedAmbRealizadoDAO.buscarPorDataGrupoCaracteristica(dataInicio, dataFim,
				caracteristica, grpSeq);
		
		List<FatProcedAmbRealizadoVO> processados = new ArrayList<FatProcedAmbRealizadoVO>();
		
		for (FatProcedAmbRealizadoVO fatProcedAmbRealizadVO : fatProcedAmbRealizadVOs) {
			
			// --- se mudou o grupo em consulta, zera a variavel de posicao
			if ((!nvl(vDtConsultaAnt, DateUtil.obterData(2999, 01, 01)).equals(fatProcedAmbRealizadVO.getDthrRealizado()))
				|| (!nvl(vPhiSeqAnt,0).equals(fatProcedAmbRealizadVO.getPhiSeq()))
				|| (!nvl(vUslUnfSeqAnt,0).equals(fatProcedAmbRealizadVO.getUnfSeq()))
				|| (!nvl(vEspSeqAnt,0).equals(fatProcedAmbRealizadVO.getEspSeq()))
				) {
				if (i > -1) {
					lst = i;
					processaPrm(fatProcedAmbRealizadoDAO, processados, i, lst, situacaoProcedimentoAmbulatorio, faturamentoFacade);
				}
				i = -1;
				processados = new ArrayList<FatProcedAmbRealizadoVO>();
			}
			i++;
			processados.add(i, fatProcedAmbRealizadVO);
			// --- valora variaveis para controle de valores anteriores
			vDtConsultaAnt = fatProcedAmbRealizadVO.getDthrRealizado();
			vPhiSeqAnt = fatProcedAmbRealizadVO.getPhiSeq();
			vUslUnfSeqAnt = fatProcedAmbRealizadVO.getUnfSeq();
			vEspSeqAnt = fatProcedAmbRealizadVO.getEspSeq();
		}
		
		if (i > -1) {
			lst = i;
			processaPrm(fatProcedAmbRealizadoDAO, processados, i, lst, situacaoProcedimentoAmbulatorio, faturamentoFacade);
		}
		
		armazenarAtributoExecAgrupamento(Boolean.FALSE);
	}

	private void processaPrm(final FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO, final List<FatProcedAmbRealizadoVO> processados, int i,
			int lst, final DominioSituacaoProcedimentoAmbulatorio situacao, final IFaturamentoFacade faturamentoFacade) throws IllegalStateException,
			BaseException {
		
		int inicio = 0;
		
		final FatProcedAmbRealizadoVO fatProcedAmbRealizadVO = processados.get(i);
		
		if (fatProcedAmbRealizadVO.getValorNumerico() < (lst + 1)) {
			
			FatProcedAmbRealizadoVO fatProcedAmbRealizadVORetirar = processados.get(0);
			logar("RETIRAR PAC PMR " + fatProcedAmbRealizadVORetirar.getSeq());
			FatProcedAmbRealizado fatProcedAmbRealizado = fatProcedAmbRealizadoDAO.obterPorChavePrimaria(fatProcedAmbRealizadVORetirar.getSeq());
			FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);

			fatProcedAmbRealizado.setPaciente(null);
			faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(fatProcedAmbRealizado, oldAmbRealizado);

			inicio = 1;
		}
		
		for (int ind = inicio; ind < lst; ind++) {
			
			FatProcedAmbRealizadoVO fatProcedAmbRealizadVOCancelar = processados.get(ind);
			logar("CANCELAR PMR " + fatProcedAmbRealizadVOCancelar.getSeq());
			FatProcedAmbRealizado fatProcedAmbRealizado = fatProcedAmbRealizadoDAO.obterPorChavePrimaria(fatProcedAmbRealizadVOCancelar.getSeq());
			FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);
			fatProcedAmbRealizado.setSituacao(situacao);
			faturamentoFacade.atualizarProcedimentosAmbulatoriaisRealizados(fatProcedAmbRealizado, oldAmbRealizado);
		}
		
	}*/

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void voltarPrm(final FatProcedAmbRealizadoDAO fatProcedAmbRealizadoDAO, final Date dataInicio, final Date dataFim,
			final IFaturamentoFacade faturamentoFacade, String nomeMicrocomputador, final Date dataFimVinculoServidor,
			FatVariaveisVO fatVariaveisVO) throws IllegalStateException, BaseException {
//		armazenarAtributoPmrJournal(Boolean.FALSE);
		if(fatVariaveisVO != null) {
			fatVariaveisVO.setvPmrJournal(Boolean.FALSE);
		}
		
		logar("voltando pmr g para a ");
		final List<FatProcedAmbRealizado> fatProcedAmbRealizados = fatProcedAmbRealizadoDAO.buscarPorSituacaoData(dataInicio, dataFim,
				DominioSituacaoProcedimentoAmbulatorio.CONSULTAS_GRUPO);
		if (fatProcedAmbRealizados != null && !fatProcedAmbRealizados.isEmpty()) {
			for (final FatProcedAmbRealizado fatProcedAmbRealizado : fatProcedAmbRealizados) {
				FatProcedAmbRealizado oldAmbRealizado = faturamentoFacade.clonarFatProcedAmbRealizado(fatProcedAmbRealizado);

				fatProcedAmbRealizado.setSituacao(DominioSituacaoProcedimentoAmbulatorio.ABERTO);
				getProcedimentosAmbRealizadosON().atualizarProcedimentoAmbulatorialRealizado(fatProcedAmbRealizado, oldAmbRealizado, nomeMicrocomputador, dataFimVinculoServidor, fatVariaveisVO);
			}
			fatProcedAmbRealizadoDAO.flush();
		}
		
		if(fatVariaveisVO != null) {
			fatVariaveisVO.setvPmrJournal(Boolean.TRUE);
		}
	}

//	public void armazenarAtributoPmrJournal(Boolean valor) {
//		atribuirContextoSessao("FATK_PMR_RN_V_PMR_JOURNAL, valor == null ? Boolean.TRUE : valor);
//	}

	public void armazenarAtributoExecAgrupamento(Boolean valor) {
		//atribuirContextoSessao("FATK_INTERFACE_AAC_V_EXEC_AGRUPAMENTO, valor == null ? Boolean.FALSE : valor);
	}

	public Boolean obterAtributoExecAgrupamento() {
//		Object obj = nullobterContextoSessao("FATK_INTERFACE_AAC_V_EXEC_AGRUPAMENTO");
//		if (obj == null) {
			return Boolean.FALSE;
//		}
//		return (Boolean) obj;
	}

	//
	// private Boolean obterAtributoPmrJournal() {
	// Object obj =
	// obterContextoSessao("FATK_PMR_RN_V_PMR_JOURNAL);
	// if (obj == null) {
	// return Boolean.TRUE;
	// }
	// return (Boolean) obj;
	// }

	private Boolean obterAtributoPmrEncerramento() {
//		Object obj = obterContextoSessao("FATK_PMR_RN_V_PMR_ENCERRAMENTO");
//		if (obj == null) {
			return Boolean.FALSE;
//		}
//		return (Boolean) obj;
	}

	protected FatProcedAmbRealizadoDAO getFatProcedAmbRealizadoDAO() {
		return fatProcedAmbRealizadoDAO;
	}



	protected FatLogInterfaceDAO getFatLogInterfaceDAO() {
		return fatLogInterfaceDAO;
	}

	protected VerificacaoFaturamentoSusRN getVerificacaoFaturamentoSusRN() {
		return verificacaoFaturamentoSusRN;
	}

	protected ProcedimentosAmbRealizadosON getProcedimentosAmbRealizadosON() {
		return procedimentosAmbRealizadosON;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected FatProcedHospInternosDAO getFatProcedHospInternosDAO() {
		return fatProcedHospInternosDAO;
	}

}
