package br.gov.mec.aghu.exames.service;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.exames.vo.DadosExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameMaterialAnaliseVO;
import br.gov.mec.aghu.exames.vo.ExameSignificativoVO;
import br.gov.mec.aghu.exames.vo.InformacaoExame;
import br.gov.mec.aghu.exames.vo.ItemSolicitacaoExame;
import br.gov.mec.aghu.exames.vo.RegiaoAnatomicaVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoExamesVO;
import br.gov.mec.aghu.exames.vo.SolicitacaoExamesValidaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.service.ServiceException;


/**
 * 
 * @author felipe
 *
 */

@Local
@Deprecated
public interface IExamesService {


	
	/**
	 * #34384 - Obter solicitação de exames	
	 * @param atdSeq
	 * @return
	 */
	List<SolicitacaoExamesVO> obterSolicitacaoExamesPorAtendimento(
			Integer atdSeq) throws ServiceException;
	
	/**
	 * Listar os itens de solicitação de exame do paciente.
	 * 
	 * Web Service #37238
	 * 
	 * @param siglaExame
	 * @param seqMaterial
	 * @param codPaciente
	 * @return
	 */
	List<ItemSolicitacaoExame> listarItemSolicitacaoExamePorSiglaMaterialPaciente(String siglaExame, Integer seqMaterial, Integer codPaciente) throws ServiceException;
	
	InformacaoExame buscarInformacaoExamePorItem(Integer soeSeq, Short seqp) throws ServiceException;

	/**
	 * Buscar os dados de unidades funcionais e exames que são significativos para o módulo da Perinatologia
	 * 
	 * Web Service #36152
	 * 
	 * @param unfSeq
	 * @param siglaExame
	 * @param seqMatAnls
	 * @param indCargaExame
	 * @param firstResult
	 * @param maxResults
	 * @return
	 * @throws ServiceException
	 */
	List<ExameSignificativoVO> pesquisarUnidadesFuncionaisExamesSignificativosPerinato(final Short unfSeq, final String siglaExame, final Integer seqMatAnls, final Boolean indCargaExame,
			final int firstResult, final int maxResults) throws ServiceException;

	/**
	 * Count de unidades funcionais e exames que são significativos para o módulo da Perinatologia
	 * 
	 * Web Service #36152
	 * 
	 * @param unfSeq
	 * @param siglaExame
	 * @param seqMatAnls
	 * @param indCargaExame
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarUnidadesFuncionaisExamesSignificativosPerinatoCount(final Short unfSeq, final String siglaExame, final Integer seqMatAnls, final Boolean indCargaExame)
			throws ServiceException;
	
	/**
	 * Buscar os dados de exames e material de analise ativos por descrição ou sigla
	 * 
	 * LIMITADO EM 100 REGISTROS
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @return
	 * @throws ServiceException
	 */
	List<ExameMaterialAnaliseVO> pesquisarAtivosPorSiglaOuDescricao(String parametro) throws ServiceException;
	
	/**
	 * Buscar os dados de exames e material de analise ativos por descrição ou sigla
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @return
	 * @throws ServiceException
	 */
	List<ExameMaterialAnaliseVO> pesquisarAtivosPorSiglaOuDescricao(String parametro, Integer maxResults) throws ServiceException;

	/**
	 * Count dos dados de exames e material de analise ativos por descrição ou sigla
	 * 
	 * Web Service #36154
	 * 
	 * @param parametro
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarAtivosPorSiglaOuDescricaoCount(String parametro) throws ServiceException;
	
	/**
	 * Persistir dados de exames significativos
	 * 
	 * Web Service #36157
	 * 
	 * @param unfSeq
	 * @param exaSigla
	 * @param matAnlsSeq
	 * @param data
	 * @param matricula
	 * @param vinCodigo
	 * @param indPreNatal
	 */
	void persistirAelUnidExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq, Date data, Integer matricula, Short vinCodigo, Boolean indPreNatal, Boolean indCargaExame) throws ServiceException;

	/**
	 * Excluir dados de exames significativos
	 * 
	 * Web Service #36158
	 * 
	 * @param unfSeq
	 * @param exaSigla
	 * @param matAnlsSeq
	 */
	void removerAelUnidExameSignificativo(Short unfSeq, String exaSigla, Integer matAnlsSeq) throws ServiceException;
	
	/**
	 * Obter dados de exames significativos para uma unidade
	 * 
	 * Web Service #37705
	 * 
	 * @param unfSeq
	 * @param indCargaExame
	 * @return
	 */
	List<ExameSignificativoVO> pesquisarAelUnidExameSignificativoPorUnfSeq(final Short unfSeq, final Boolean indCargaExame);
	
	/**
	 * Buscar os dados de exames e material de analise por sigla e sequencial de material de análise
	 * 
	 * Web Service #37700
	 * 
	 * @param sigla
	 * @param seqMatAnls
	 * @return
	 */
	List<DadosExameMaterialAnaliseVO> pesquisarExamesPorSiglaMaterialAnalise(String sigla, Integer seqMatAnls) throws ServiceException;
	
	/**
	 * WebService #41147 - Busca a data do primeiro exame para um atendimento através do número da consulta
	 * @param conNumero
	 * @return
	 */
	Date obterDataPrimeiraSolicitacaoExamePeloNumConsulta(Integer conNumero);
	
	/**
	 * Chamada para Web Service #38474
	 * Utilizado nas estórias #864 e #27542 
	 * @param atdSeq
	 * @return Boolean
	 * @throws ApplicationBusinessException
	 */
	Boolean verificarExameVDRLnaoSolicitado(Integer atdSeq) throws ApplicationBusinessException;
	
	/**
	 * Web Service #39251 utilizado na estória #864
	 * @param pacCodigo
	 * @return Boolean
	 */
	Boolean verificaPacienteEmProjetoPesquisa(Integer pacCodigo);
	
	/**
	 * #39003 - Serviço que busca ultima solicitacao de exames
	 * @param atdSeq
	 * @return
	 * @throws ServiceException 
	 * @throws ApplicationBusinessException
	 */
	SolicitacaoExamesValidaVO buscarUltimaSolicitacaoExames(Integer atdSeq) throws ServiceException;
	

	/**
	 * #42108 - #25685
	 * Consulta utilizada para pesquisar as Regiões Anatômicas.
	 * @param descricao
	 * @return
	 */
	List<RegiaoAnatomicaVO> pesquisarRegioesAnatomicasAtivasPorDescricaoSeq(String param);
	
	/**
	 * #42108 - #25685
	 * Consulta utilizada para pesquisar as Regiões Anatômicas.
	 * @param descricao
	 * @return
	 */
	Long pesquisarRegioesAnatomicasAtivasPorDescricaoSeqCount(String param);

	/**
	 * 
	 * #42109 - #25685
	 * Consulta utilizada para verificar se o Achado já existe.
	 * @param seqs
	 * @param descricao
	 * @return
	 */
	Boolean verificarRegioesPorSeqAchadoDescricao(List<Integer> seqs, String descricao);
	
	/**
	 * #42899 - #25685
	 * Consulta utilizada para pesquisar Regioes Anatomicas. 
	 * @param descricao
	 * @return
	 */
	List<RegiaoAnatomicaVO> buscarRegioesAnatomicas(String descricao);
	
	/**
	 *  Serviço para verificar se o atendimento do Recém Nascido possui alguma solicitação de exame. Serviço #42021
	 * @param atdSeq
	 * @param situacoes
	 * @return List<SolicitacaoExamesVO>
	 */
	List<SolicitacaoExamesVO> listarSolicitacaoExamesPorSeqAtdSituacoes(Integer atdSeq, String ... situacoes) throws ServiceException;
	
	/**
	 * #42899 - #25685
	 * Consulta utilizada para pesquisar Regiao Anatomica por Seq - Serviço auxiliar. 
	 * @param descricao
	 * @return
	 */
	RegiaoAnatomicaVO obterRegiaoAnotomicaPorId(Integer seq);
	
	/**
	 * #43852: Serviço para validar permissão para solicitar exames
	 * @param atendimentoSeq
	 */
	void verificarPermissoesParaSolicitarExame(Integer atendimentoSeq)throws ServiceException;
}
