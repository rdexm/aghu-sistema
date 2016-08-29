package br.gov.mec.aghu.registrocolaborador.service;

import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.registrocolaborador.vo.InstQualificadora;
import br.gov.mec.aghu.registrocolaborador.vo.InstQualificadoraFiltro;
import br.gov.mec.aghu.registrocolaborador.vo.RapPessoasFisicaVO;
import br.gov.mec.aghu.registrocolaborador.vo.RapServidorConselhoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Servidor;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.registrocolaborador.vo.UsuarioFiltro;
import br.gov.mec.aghu.service.ServiceBusinessException;
import br.gov.mec.aghu.service.ServiceException;

/**
 * 
 * @author cvagheti
 * 
 */
@Local
@Deprecated
public interface IRegistroColaboradorService {

	/**
	 * Retorna usuário encontrados com o filtro especificado.
	 * 
	 * @param filtro
	 * @return
	 * @throws ServiceException
	 */
	Usuario buscaUsuario(UsuarioFiltro filtro) throws ServiceException;

	/**
	 * Retorna as Instituições Qualificadoras com o filtro especificado.
	 * 
	 * @param instQualificadoraFiltro
	 * @return
	 * @throws ServiceException
	 */
	List<InstQualificadora> buscaInstQualificadora(
			InstQualificadoraFiltro instQualificadoraFiltro)
			throws ServiceException;

	/**
	 * Retorna dados de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @return
	 * @throws ServiceException
	 */
	List<Servidor> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome) throws ServiceException;
	
	/**
	 * Retorna dados de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @throws ServiceException
	 */
	List<Servidor> pesquisarMedicosEmergencia(List<Short> vinCodigos, List<Integer> matriculas, String nome,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ServiceException;
	
	/**
	 * Retorna count de médicos que atuam na emergência pelo nome, matrícula e código do vínculo
	 * 
	 * Web Service #34397
	 * 
	 * @param vinCodigos
	 * @param matriculas
	 * @param nome
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarMedicosEmergenciaCount(List<Short> vinCodigos, List<Integer> matriculas, String nome) throws ServiceException;

	/**
	 * Retorna dados de servidores ativos por nome, vínculo e matrícula
	 * 
	 * Web Service #34399
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @param nome
	 * @param maxResults
	 * @return
	 * @throws ServiceException
	 */
	List<Servidor> pesquisarServidoresAtivos(Short vinCodigo, Integer matricula, String nome, Integer maxResults) throws ServiceException;
	
	/**
	 * Retorna dados de servidores ativos por nome, vínculo e matrícula
	 * 
	 * Web Service #34399
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @param nome
	 * @return
	 * @throws ServiceException
	 */
	List<Servidor> pesquisarServidoresAtivos(Short vinCodigo, Integer matricula, String nome) throws ServiceException;
	
	/**
	 * Retorna count de servidores ativos por nome, vínculo e matrícula
	 * 
	 * Web Service #34399
	 * 
	 * @param vinCodigoVRapPessoaServidor
	 * @param matricula
	 * @param nome
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarServidoresAtivosCount(Short vinCodigo, Integer matricula, String nome) throws ServiceException;
	
	/**
	 * Pesquisa profissional pela central de custo
	 * 
	 * Web Service #36698
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	List<RapServidorConselhoVO> pesquisarServidoresConselho(String strPesquisa, List<Integer> centroCusto) throws ServiceException;
	
	/**
	 * Count Pesquisa profissional pela central de custo
	 * 
	 * Web Service #36698
	 * @param strPesquisa
	 * @param centroCusto
	 * @return
	 */
	Long pesquisarServidoresConselhoCount(String strPesquisa, List<Integer> centroCusto) throws ServiceException;
	
	/**
	 * Buscar profissional por centro de custo
	 * 
	 * Web Service #38778
	 * 
	 * @param siglas
	 * @param centroCusto
	 * @return
	 */
	List<RapServidorConselhoVO> pesquisarServidoresConselhoPorSiglaCentroCusto(final List<String> siglas, final List<Integer> centroCusto) throws ServiceException;

	/**
	 * Count de profissional por centro de custo
	 * 
	 * Web Service #38778
	 * 
	 * @param siglas
	 * @param centroCusto
	 * @return
	 */
	Long pesquisarServidoresConselhoPorSiglaCentroCustoCount(final List<String> siglas, final List<Integer> centroCusto) throws ServiceException;

	/**
	 * Buscar profissional por matricula e vínculo
	 * 
	 * Web Service #38729
	 * 
	 * @param siglas
	 * @param matricula
	 * @param vinculo
	 * @return
	 */
	RapServidorConselhoVO obterServidorConselhoPorSiglaMatriculaVinculo(final List<String> siglas, final Integer matricula, final Short vinculo);
	
	/**
	 * #36699 - Serviço para obter pessoa fisica
	 * @param vinculos
	 * @param matriculas
	 * @return
	 * @throws ServiceException 
	 */
	List<RapPessoasFisicaVO> obterRapPessoasFisicasPorMatriculaVinculo(final List<Short> vinculos, final List<Integer> matriculas) throws ServiceException;

	String buscarNomeResponsavelPorMatricula(Short codigo, Integer matricula) throws ServiceException;
	

	/**
	 * Retorna usuário encontrados com o username especificado.
	 * 
	 * Webservice #37666
	 * 
	 * @param username
	 * @return
	 * @throws ServiceException
	 */
	Servidor buscarServidor(String username) throws ServiceBusinessException, ServiceException;
	
	boolean usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas) throws ServiceException;
	
	
	/**
	 * #42162 - Consulta utilizada para verificar se o Servidor existe na tabela AGH.RAP_SERVIDORES.
	 * @param codigo
	 * @param matricula
	 * @return
	 * @throws ServiceException
	 */
	Boolean verificarServidorExiste(Short codigo, Integer matricula) throws ServiceException;
	
	/**
	 * #42159 - Consulta utilizada para verificar se o leito já está cadastrado para o profissional.
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 * @throws ServiceException
	 */
	Servidor obterVRapPessoaServidorPorVinCodigoMatricula(Integer matricula, Short vinCodigo) throws ServiceException;
	
	/**
	 * #42160 - Consulta utilizada para pesquisar os profissionais relacionados aos centros de custo de Neonatologia e Pediatria.
	 * @param vinCodigos
	 * @param cctCodigos 
	 * @param matriculaVinculo
	 * @param nome
	 * @return
	 * @throws ServiceException
	 */
	List<Servidor> pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNome(List<Short> vinCodigos, List<Integer> cctCodigos, Integer matriculaVinCodigo, String nome) throws ServiceException;
	
	/**
	 * #42160 - Consulta utilizada para pesquisar os profissionais relacionados aos centros de custo de Neonatologia e Pediatria.
	 * @param vinCodigos
	 * @param cctCodigos 
	 * @param matriculaVinculo
	 * @param nome
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarVRapPessoaServidorPorVinCodigoMatriculaCCTNomeCount(List<Short> vinCodigos, List<Integer> cctCodigos, Integer matriculaVinCodigo, String nome) throws ServiceException;

	List<RapServidorConselhoVO> pesquisarServidoresConselhoPorSiglaCentroCusto(List<String> siglas, List<Integer> centroCusto, Integer maxResults) throws ServiceException;

	RapServidorConselhoVO obterServidorConselhoPeloId(String sigla, Integer matricula, Short vinculo);
	
	
	/**
	 * #39000 - Serviço que retorna existe servidor categoria prof medicos
	 * 
	 * Web Service #39000
	 * 
	 * @param matricula
	 * @param vinculo
	 * @throws ServiceBusinessException
	 * @throws ServiceException
	 */
	Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ServiceBusinessException, ServiceException;
	
	RapServidorConselhoVO usuarioRelatoriosDefinitivos(Integer matricula, Short vinCodigo, String[] siglas) throws ServiceException;

	/**
	 * #44878 - Serviço que verifica se servidor possui CBO de Anestesista.
	 * 
	 * Web Service #44878
	 * 
	 * @param vinCodigo
	 * @param matricula
	 * @param siglasConselhos
	 * @param codigosTipoInformacoes
	 * @throws ServiceBusinessException
	 * @throws ServiceException
	 */
	boolean verificarProfissionalPossuiCBOAnestesista(Short vinCodigo, Integer matricula, String[] siglasConselhos, String[] codigosTipoInformacoes) throws ServiceException;
}