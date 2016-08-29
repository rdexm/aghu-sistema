package br.gov.mec.aghu.ambulatorio.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.jws.WebParam;

import br.gov.mec.aghu.ambulatorio.vo.BoletimAtendimento;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaEspecialidadeAlteradaRetornoVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaGradeAtendimentoVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.Especialidade;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendaVo;
import br.gov.mec.aghu.ambulatorio.vo.GradeAgendamentoAmbulatorioServiceVO;
import br.gov.mec.aghu.ambulatorio.vo.LaudoAihVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.service.ServiceException;

/**
 * 
 * @author frutkowski
 * 
 */
@Local
@Deprecated
public interface IAmbulatorioService {
	
	/**
	 * Retorna o se da grade e o seq da unidade funcional da grade com o filtro especificado.
	 * 
	 * @param filtro
	 * @return
	 * @throws ServiceException
	 */
	GradeAgendaVo obterGradesAgendaPorEspecialidadeDataLimiteParametros(@WebParam(name = "espSeq") Short espSeq, 
			@WebParam(name = "pPagadorSUS") Short pPagadorSUS, @WebParam(name = "pTagDemanda") Short pTagDemanda, 
			@WebParam(name = "pCondATEmerg") Short pCondATEmerg , @WebParam(name = "dataLimite") Date dataLimite) throws ServiceException;
	
	Integer inserirConsultaEmergencia(@WebParam(name = "dataConsulta")  Date dataConsulta,
			@WebParam(name = "gradeSeq")  Integer gradeSeq, 
			@WebParam(name = "pacCodigo") Integer pacCodigo, 
			@WebParam(name = "pConvenioSUSPadrao") Short pConvenioSUSPadrao,
			@WebParam(name = "pSusPlanoAmbulatorio") Byte pSusPlanoAmbulatorio, 
			@WebParam(name = "pPagadorSUS") Short pPagadorSUS, 
			@WebParam(name = "pTagDemanda") Short pTagDemanda,
			@WebParam(name = "pCondATEmerg") Short pCondATEmerg, 
			@WebParam(name = "nomeMicrocomputador")  String nomeMicrocomputador) throws ServiceException;

	
	List<ConsultaVO> obterConsultasPorNumero(List<Integer> numeros) throws ServiceException;
	
	List<ConsultaEspecialidadeAlteradaRetornoVO> obterConsultasEspecialidadeAlterada(Short espSeq, Integer grdSeq, Integer conNumero) throws ServiceException;
	
	
	/**
	 * Serviço: #34389 e #34391
	 * Obter consulta por especialidade e grade de atendimento
	 * 
	 * @param conNumero - número da consulta
	 * @param espSeq - seq especialidade
	 * @return
	 * @throws ServiceException
	 */
	List<ConsultaGradeAtendimentoVO> pesquisarPorConsultaPorNumeroConsultaEspecialidade(List<Integer> conNumero, Short espSeq) throws ServiceException;

	List<GradeAgendamentoAmbulatorioServiceVO> listarQuantidadeConsultasAmbulatorio(List<String> sitConsultas, Short espSeq,
			String situacaoMarcado, Short pPagadorSUS, Short pTagDemanda, Short pCondATEmerg, boolean isDtConsultaMaiorDtAtual) throws ServiceException;
	
	Integer atualizarConsultaEmergencia(Integer numeroConsulta, Integer pacCodigo, Date dataHoraInicio, Short pConvenioSUSPadrao, Byte pSusPlanoAmbulatorio,
			String indSitConsulta, String nomeMicrocomputador) throws ServiceException;

	/**
	 * Serviço: #36347
	 * Obter especialidade da consulta
	 * 
	 * @param conNumero - número da consulta
	 */
	Especialidade obterEspecialidadeDaConsulta(Integer numeroConsulta) throws ServiceException, ApplicationBusinessException;

	/**
	 * Serviço: #35605
	 * Obter boletim de atendimento pela consulta
	 * 
	 * @param conNumero - número da consulta
	 */
	BoletimAtendimento obterBoletimAtendimentoPelaConsulta(Integer nroConsulta)
			throws ServiceException, ApplicationBusinessException;
	
	/**
	 * Buscar consulta do paciente tem consulta no CO
	 * 
	 * Web Service #36972
	 * 
	 * @param pacCodigo
	 * @param unfSeq1
	 * @param unfSeq2
	 * @return
	 * @throws ServiceException
	 */
	List<Integer> pesquisarConsultaPorPacienteUnidadeFuncional(final Integer pacCodigo, final Short unfSeq1, final Short unfSeq2) throws ServiceException;
	
	/**
	 * Buscar a data da consulta anterior a consulta atual, sendo informado o código do paciente e o sequencial da gestação.
	 * 
	 * Web Service #37687
	 * @param pacCodigo
	 * @param conNumero
	 * @param gsoSeqp
	 * @return
	 * @throws ServiceException
	 */
	List<Date> pesquisarPorPacienteConsultaGestacao(Integer pacCodigo, Integer conNumero, Short gsoSeqp) throws ServiceException;
	
	Long pesquisarConsultasPorEspecialidade(Short espSeq, List<Integer> consultasPacientesEmAtendimento);

	/**
	 * Obter laudos aih de uma determinada consulta
	 * 
	 * Web Service #38473
	 * 
	 * @param conNumero
	 * @param pacCodigo
	 * @return
	 */
	List<LaudoAihVO> pesquisarLaudosAihPorConsultaPaciente(final Integer conNumero, final Integer pacCodigo) throws ServiceException;
	
	Short obtemEspecialidadeDaGradePeloNumeroConsulta(Integer conNumero);
	
	Short obterUnidadeAssociadaAgendaPorNumeroConsulta(Integer conNumero);
	
	Boolean validarProcesso(Short serVinCodigo, Integer serMatricula, Short seqProcesso) throws ApplicationBusinessException;
	
	Integer obterAtendimentoPorConNumero(Integer conNumero);
	
	/**
	 * Web Service #37234
	 * @param numeroConsulta
	 * @return atd_seq
	 */
	Integer obterAtdSeqPorNumeroConsulta(Integer numeroConsulta);
}