package br.gov.mec.aghu.paciente.service;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.paciente.vo.ComponenteSanguineo;
import br.gov.mec.aghu.paciente.vo.DadosSanguineos;
import br.gov.mec.aghu.paciente.vo.Medicamento;
import br.gov.mec.aghu.paciente.vo.Paciente;
import br.gov.mec.aghu.paciente.vo.PacienteFiltro;
import br.gov.mec.aghu.paciente.vo.PacienteProntuarioConsulta;
import br.gov.mec.aghu.paciente.vo.ProcedimentoReanimacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Local
@Deprecated
public interface IPacienteService {

	/**
	 * Retorna o paciente encontrado com o filtro especificado.
	 * 
	 * @param filtro
	 * @return
	 * @throws ServiceException
	 */
	Paciente obterPacientePorCodigoOuProntuario(
			PacienteFiltro filtro) throws ServiceException, ApplicationBusinessException;
	
	public List<Paciente> pesquisarPorFonemas(
			PacienteFiltro filtro) throws ServiceException, ApplicationBusinessException;
	
	public Long pesquisarPorFonemasCount(
			PacienteFiltro filtro) throws ServiceException, ApplicationBusinessException;
	
	String gerarEtiquetaPulseira(
			Integer pacCodigo, Integer atdSeq) throws ServiceException, ApplicationBusinessException;
	
	Integer gerarNumeroProntuarioVirtualPacienteEmergencia(
			Integer pacCodigo, String nomeMicrocomputador) throws ServiceException, ApplicationBusinessException;

	Paciente obterDadosPacientePelaConsulta(Integer numeroConsulta)
			throws ServiceException, ApplicationBusinessException;

	Paciente obterDadosContatoPacientePeloCodigo(Integer pacCodigo)
			throws ServiceException, ApplicationBusinessException;
	
	/**
	 * Buscar os dados da Gestante tendo como parâmetro o Prontuário
	 * 
	 * Web Service #36616
	 * 
	 * @param nroProntuario
	 * @return
	 * @throws ServiceException
	 */
	PacienteProntuarioConsulta obterDadosGestantePorProntuario(Integer nroProntuario) throws ServiceException;

	/**
	 * Buscar os dados da Gestante tendo como parâmetro o número da consulta
	 * 
	 * Web Service #36608
	 * 
	 * @param nroConsulta
	 * @return
	 */
	PacienteProntuarioConsulta obterDadosGestantePorConsulta(final Integer nroConsulta) throws ServiceException;

	/**
	 * Buscar a última consulta da gestante tendo como parâmetro o sequencial da gestação e o código do paciente
	 * 
	 * Web Service #36620
	 * 
	 * @param gsoSeqp
	 * @param pacCodigo
	 * @return
	 */
	Integer obterDadosGestantePorGestacaoPaciente(final Short gsoSeqp, final Integer gsoPacCodigo) throws ServiceException;
	
	/**
	 * Buscar os dados sanguíneos da gestante tendo como parâmetro o código da paciente
	 * 
	 * Web Service #36968
	 * 
	 * @param pacCodigo
	 * @param seqp
	 * @return
	 */
	DadosSanguineos obterRegSanguineosPorCodigoPaciente(Integer pacCodigo, Byte seqp) throws ServiceException;

	/**
	 * Verificar se existe tipo sanguíneo para o paciente
	 * 
	 * Web Service #37245
	 * 
	 * @param pacCodigo
	 * @return
	 */
	Boolean existsRegSanguineosPorCodigoPaciente(Integer pacCodigo) throws ServiceException;
	
	/**
	 * Remover registros sem dados na tabela de AIP_REG_SANGUINEOS através do código do paciente
	 * 
	 * Web Service #36971
	 * 
	 * @param pacCodigo
	 */
	void removerRegSanguineosSemDadosPorPaciente(Integer pacCodigo) throws ServiceException;
	
	/**
	 * Verificar se existe registro sem dados na tabela de AIP_REG_SANGUINEOS através do código do paciente
	 * 
	 * Web Service #37235
	 * 
	 * @param pacCodigo
	 * @return
	 */
	Boolean existsRegSanguineosSemDadosPorPaciente(Integer pacCodigo) throws ServiceException;
	
	/**
	 * Alteração dos dados sanguíneos da paciente.
	 * 
	 * Web Service #39651
	 * 
	 * @param pacCodigo
	 * @param seqp
	 * @param grupoSanguineoMae
	 * @param fatorRhMae
	 * @param coombs
	 * @throws ServiceException
	 */
	void atualizarRegSanguineo(Integer pacCodigo, Byte seqp, String grupoSanguineoMae, String fatorRhMae,
			String coombs) throws ServiceException;
	
	/**
	 * Inserção dos dados sanguíneos da paciente.
	 * 
	 * Web Service #39651
	 * 
	 * @param pacCodigo
	 * @param grupoSanguineoMae
	 * @param fatorRhMae
	 * @param coombs
	 * @param serMatricula
	 * @param serVinCodigo
	 * @throws ServiceException
	 */
	void inserirRegSanguineo(Integer pacCodigo, String grupoSanguineoMae, String fatorRhMae,
			String coombs, Integer serMatricula, Short serVinCodigo) throws ServiceException;

	/**
	 * Web Service #41092
	 * Serviço verificar se servidor tem qualificação para acessar registros
	 * @param descricao
	 * @return
	 * @throws ServiceException
	 */
	Boolean verificarAcaoQualificacaoMatricula(String descricao) throws ServiceException;
	
	/**
	 * Web Service #40707
	 * 
	 * Atualizar a data de nascimento com a Data de Nascimento do Recém-Nascido na tabela AIP_PACIENTES
	 * 
	 * @param pacCodigo
	 * @param dthrNascimento
	 */
	void atualizarPacienteDthrNascimento(final Integer pacCodigo, final Date dthrNascimento) throws ServiceException;
	
	/**
	 * Gera imagem do gráfico de Frequencia Cardiaca
	 * 
	 * Web Service #37282
	 * 
	 * @param pacCodigo
	 * @param gsoSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	byte[] getGraficoFrequenciaCardiaca(Integer pacCodigo, Short gsoSeqp) throws ServiceBusinessException, ServiceException;

	/**
	 * Gera imagem do gráfico de Partograma
	 * 
	 * Web Service #37282
	 * 
	 * @param pacCodigo
	 * @param gsoSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	byte[] getGraficoPartograma(Integer pacCodigo, Short gsoSeqp) throws ServiceBusinessException, ServiceException;
	
	/**
	 * Web Service #42557
	 * @param pacCodigo
	 * @return
	 * @throws ServiceException
	 */
	BigDecimal obterAlturasPaciente(Integer pacCodigo) throws ServiceException;
	
	/**
	 * Web Service #42480
	 * @param pacCodigo
	 * @return
	 * @throws ServiceException
	 */
	BigDecimal obterPesoPacientesPorCodigoPaciente(Integer pacCodigo) throws ServiceException;

	List<ComponenteSanguineo> listarComponentesSuggestion(Object objPesquisa) throws ServiceBusinessException, ServiceException;

	Long listarComponentesSuggestionCount(Object objPesquisa)
			throws ServiceBusinessException, ServiceException;

	List<Medicamento> listarMedicamentosSuggestion(Object objPesquisa)
			throws ServiceBusinessException, ServiceException;

	Long listarMedicamentosSuggestionCount(Object objPesquisa)
			throws ServiceBusinessException, ServiceException;

	Medicamento obterMedicamentoPorId(Integer matCodigo);
	
	/**
	 * Serviço já estava pronto e pôde ser utilizado como implementação para
	 * #43243. 
	 * @param codigo
	 * @return
	 */
	ComponenteSanguineo obterComponentePorId(String codigo);

	ProcedimentoReanimacao obterProcReanimacao(Integer seq);

	void persistirSindrome(String descricao, String situacao);

	void ativarInativarSindrome(Integer seq) throws ServiceBusinessException,
			ServiceException;

	void persistirBallard(Short seq, Short escore, Short igSemanas);
	/**
	 * #43626 - Buscar o codigo do atendimento mais recente do recem nascido.
	 * Pelo pac codigo
	 * @param pacCodigo
	 * @return
	 */
	Integer obterUltimoAtdSeqRecemNascidoPorPacCodigo(Integer pacCodigo);

	void atualizarAtendimentoGestante(Integer conNumero, Integer pacCodigo, Short seqp, 
			String nomeMicroComputador, Integer matricula, Short vinCodigo, Integer atdSeq) throws ServiceBusinessException, ServiceException;
			
}
