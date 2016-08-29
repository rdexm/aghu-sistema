package br.gov.mec.aghu.internacao.service;

import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.internacao.vo.DadosInternacaoUrgenciaVO;
import br.gov.mec.aghu.internacao.vo.LeitoVO;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncional;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncionalFiltro;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Local
@Deprecated
public interface IInternacaoService {

	/**
	 * Retorna as unidades funcionais encontrados com o filtro especificado.
	 * 
	 * @param filtro
	 * @return
	 * @throws ServiceException
	 */
	List<UnidadeFuncional> pesquisarUnidadeFuncional(
			UnidadeFuncionalFiltro filtro) throws ServiceException;
	
	List<UnidadeFuncional> pesquisarUnidadeFuncional(
			UnidadeFuncionalFiltro filtro, Integer maxResults) throws ServiceException; 
			
	Long pesquisarUnidadeFuncionalCount(UnidadeFuncionalFiltro filtro) throws ServiceException;
	
	Short pesquisarUnidadeFuncionalTriagemRecepcao(
			List<Short> listaUnfSeqTriagemRecepcao, Short unfSeqMicroComputador) throws ServiceException;

	String obterNomeInstituicaoHospitalar() throws ServiceException;
	
	/**
	 * Obter código do atendimento de urgência pelo número da consulta
	 * 
	 * Web Service #38477
	 * 
	 * @param conNumero
	 * @return
	 */
	Integer obterSeqAtendimentoUrgenciaPorConsulta(final Integer conNumero) throws ServiceException;
	
	/**
	 * Obter os dados de uma internação de urgência
	 * 
	 * Web Service #38823
	 * 
	 * @param atuSeq
	 * @return
	 */
	DadosInternacaoUrgenciaVO obterInternacaoPorAtendimentoUrgencia(Integer atuSeq) throws ServiceException;

	/**
	 * Busca o Servidor pela matricula e vinculo e então chama o método atualizarInternacao passando o mesmo
	 * 
	 * Se ocorrer algum erro negocial, é lançada uma ServiceException com a mensagem de erro.
	 * 
	 * Web Service #38824
	 * 
	 * @param seqInternacao
	 * @param matriculaProfessor
	 * @param vinCodigoProfessor
	 * @param nomeMicrocomputador
	 * @param matriculaServidorLogado
	 * @param vinCodigoServidorLogado
	 * @throws ServiceException
	 */
	void atualizarServidorProfessorInternacao(final Integer seqInternacao, final Integer matriculaProfessor, final Short vinCodigoProfessor, String nomeMicrocomputador,
			final Integer matriculaServidorLogado, final Short vinCodigoServidorLogado) throws ServiceException;

	/**
	 * Consulta utilizada para verificar se determinado módulo está ativo no sistema.
	 * 
	 * Web Service #38827
	 * 
	 * @param numeroConsulta
	 * @return Boolean
	 * @throws ServiceException
	 */
	Boolean verificarPacienteInternadoPorConsulta(Integer numeroConsulta) throws ServiceException;
	
	/**
	 * Consulta utilizada para verificar se o paciente está com ingresso em SO. 
	 * 
	 * Web Serviço #39498
	 * 
	 * @param numeroConsulta
	 * @return Boolean
	 * @throws ServiceException
	 */
	Boolean verificarPacienteIngressoSOPorConsulta(Integer numeroConsulta) throws ServiceException;
		
	/**
	 *  Rotina utilizada por médicos para dar alta na sala de observação quando for finalizar a consulta do paciente.
	 * 
	 * Web Service #37865
	 * 
	 * @param numeroConsulta
	 * @param codigoPaciente
	 * @param matricula
	 * @param vinCodigo 
	 * @param hostName
	 * @throws ServiceException
	 */
	void finalizarConsulta(Integer numeroConsulta, Integer codigoPaciente, Integer matricula, Short vinCodigo, String hostName) throws ServiceBusinessException, ServiceException;

	/**
	 * Rotina utilizada para internação da paciente automaticamente.
	 * Web Service #38819
	 * 
	 * @param matricula*
	 * @param vinCodigo 
	 * @param pacCodigo
	 * @param seqp	 
	 * @param numeroConsulta
	 * @param hostName
	 * @throws ServiceBusinessException
	 * @throws ServiceException
	 */
	void realizarInternacaoPacienteAutomaticamente(Integer matricula, Short vinCodigo, Integer pacCodigo, Short seqp, Integer numeroConsulta, String hostName, Long trgSeq) throws ServiceBusinessException, ServiceException;
	
	/**
	 * #42161 - Consulta utilizada para verificar se o leidto existe na tabela AGH.AIN_LEITOS.
	 * @param idLeito
	 * @return
	 * @throws ServiceException
	 */
	Boolean verificarLeitoExiste(String idLeito) throws ServiceException;
	
	/**
	 * #42540 - Consulta utilizada para pesquisar Leitos.
	 * @param unfs
	 * @param leito ou leitoID
	 * @return
	 * @throws ServiceException
	 */
	List<LeitoVO> pesquisarLeitosPorSeqUnf(List<Short> unfs, String leito) throws ServiceException;
	
	/**
	 * #42540 - Consulta utilizada para pesquisar Leitos Count.
	 * @param unfs
	 * @param leito
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarLeitosPorSeqUnfCount(List<Short> unfs, String leito) throws ServiceException;
	
}