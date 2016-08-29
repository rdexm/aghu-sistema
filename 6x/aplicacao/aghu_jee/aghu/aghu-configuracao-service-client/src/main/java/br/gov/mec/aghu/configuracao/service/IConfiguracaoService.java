package br.gov.mec.aghu.configuracao.service;


import java.util.Date;
import java.util.List;

import javax.ejb.Local;
import javax.jws.WebParam;

import br.gov.mec.aghu.configuracao.vo.CidVO;
import br.gov.mec.aghu.configuracao.vo.EquipeVO;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.configuracao.vo.EspecialidadeFiltro;
import br.gov.mec.aghu.configuracao.vo.OrigemPacAtendimentoVO;
import br.gov.mec.aghu.configuracao.vo.PacAtendimentoVO;
import br.gov.mec.aghu.configuracao.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.service.ServiceException;



/**
 * 
 * @author cvagheti
 * 
 */
@Local
@Deprecated
public interface IConfiguracaoService {

	/**
	 * Retorna as especialidades encontrados com o filtro especificado.
	 * 
	 * @param filtro
	 * @return
	 * @throws ServiceException
	 */
	List<Especialidade> pesquisarEspecialidade(@WebParam(name = "filtro") EspecialidadeFiltro filtro)
			throws ServiceException;
	
	
	List<Especialidade> pesquisarEspePorNomeSiglaListaSeq(@WebParam(name = "listEspId") List<Short> listEspId , @WebParam(name = "filtro") EspecialidadeFiltro filtro)
			throws ServiceException;	
	
	Long pesquisarEspePorNomeSiglaListaSeqCount(@WebParam(name = "listEspId") List<Short> listEspId , @WebParam(name = "filtro") EspecialidadeFiltro filtro)
			throws ServiceException;
	
	/**
	 * Retorna as especialidades ativas de acordo com o param especificado.
	 * 
	 * Web Service #34334
	 * 
	 * @param param
	 * @param maxResults
	 * @return
	 * @throws ServiceException
	 */
	List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param, Integer maxResults) throws ServiceException;
	
	/**
	 * Retorna as especialidades ativas de acordo com o param especificado.
	 * 
	 * Web Service #34334
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSigla(String param) throws ServiceException;
	
	/**
	 * Retorna o número de especialidades ativas de acordo com o param especificado.
	 * 
	 * Web Service #34334
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaCount(String param) throws ServiceException;

	/**
	 * Retorna as especialidades ativas de acordo com os seqs.
	 * 
	 * Web Service #34337
	 * 
	 * @param listSeqs
	 * @return
	 * @throws ServiceException
	 */
	List<Especialidade> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs) throws ServiceException;
	
	/**
	 * Retorna as especialidades ativas de acordo com o param e seqs especificados.
	 * 
	 * Web Service #34334
	 * 
	 * @param param
	 * @param listSeqs
	 * @return
	 * @throws ServiceException
	 */
	List<Especialidade> pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqs(String param, List<Short> listSeqs) throws ServiceException;
	
	/**
	 * Retorna o número de especialidades ativas de acordo com o param e seqs especificados.
	 * 
	 * Web Service #34334
	 * 
	 * @param param
	 * @param listSeqs
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarEspecialidadesAtivasPorSeqNomeOuSiglaSeqsCount(String param, List<Short> listSeqs) throws ServiceException;
	
	/**
	 * Retorna as especialidades ativas de acordo com os seqs.
	 * 
	 * Web Service #34337
	 * 
	 * @param listSeqs
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return
	 * @throws ServiceException
	 */
	List<Especialidade> pesquisarEspecialidadesAtivasPorSeqs(List<Short> listSeqs,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) throws ServiceException;

	/**
	 * Retorna o número de especialidades ativas de acordo com os seqs
	 * 
	 * Web Service #34337
	 * 
	 * @param listSeqs
	 * @return
	 * @throws ServiceException
	 */
	Long pesquisarEspecialidadesAtivasPorSeqsCount(List<Short> listSeqs) throws ServiceException;
	
	Boolean verificarAtendimentoVigentePorCodigo(final Integer codigo) throws ServiceException;
	
	/**
	 * Retornar os Cids pesquisados por seq
	 * 
	 * Web Service #36118
	 * 
	 * @param listSeq
	 * @return
	 * @throws ServiceException
	 */
	List<CidVO> pesquisarCidPorSeq(List<Integer> listSeq) throws ServiceException;
	
	/**
	 * Retornar os Cids pesquisados por codigo e/ou descricao
	 * 
	 * Web Service #36117
	 * 
	 * @param param
	 * @return
	 * @throws ServiceException
	 */
	List<CidVO> pesquisarCidPorCodigoDescricao(String param) throws ServiceException;
	
	Long pesquisarCidPorCodigoDescricaoCount(String param) throws ServiceException;
	
	/**
	 * Busca nome da especialidade pelo seq
	 * 
	 * Web Service #38510
	 * 
	 * @param seq
	 * @return
	 * @throws ServiceException
	 */
	String pesquisarNomeEspecialidadePorSeq(Short seq) throws ServiceException;
	
	Especialidade obterEspecialidadePorSeq(Short espSeq) throws ServiceException;

	/**
	 * Buscar as Equipes ativas do CO
	 * 
	 * Web Service #38731
	 * 
	 * @return
	 */
	List<EquipeVO> pesquisarEquipesAtivasDoCO() throws ServiceException;
	
	/**
	 * Buscar a equipe sendo informado matícula e vínculo
	 * 
	 * Web Service #38721
	 * 
	 * @return
	 */
	List<EquipeVO> pesquisarEquipesPorMatriculaVinculo(final Integer matricula, final Short vinCodigo) throws ServiceException;

	/**
	 * Buscar agh_equipes para suggestionBox
	 * 
	 * Web Service #34712
	 * 
	 * @return
	 */
	List<EquipeVO> pesquisarEquipesAtivas(final String parametro) throws ServiceException;
	
	/**
	 * Web Service #38731 adaptado para popular Suggestions
	 */
	List<EquipeVO> pesquisarEquipeAtivaCO(final String parametro) throws ServiceException;
	Long pesquisarEquipeAtivaCOCount(final String parametro) throws ServiceException;
	
	/**
	 * Buscar unidades funcionais ativas por descrição e código
	 * 
	 * Web Service #36153
	 * 
	 * @param parametro
	 * @return
	 */
	List<UnidadeFuncionalVO> pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricao(final String parametro) throws ServiceException;

	/**
	 * Buscar count de unidades funcionais ativas por descrição e código
	 * 
	 * Web Service #36153
	 * 
	 * @param parametro
	 * @return
	 */
	Long pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricaoCount(final String parametro) throws ServiceException;
	
	/**
	 * Retornar os Cids ativos por seq
	 * 
	 * Web Service #37939
	 * 
	 * @param listSeq
	 * @return
	 */
	List<CidVO> pesquisarCidAtivosPorSeq(List<Integer> listSeq) throws ServiceException;
		
	/**
	 * Retornar os dados do Paciente Em Atendimento
	 * 
	 * Web Services #34389, #34391 e #34337
	 * 
	 * @param conNumero
	 * @return
	 */
	PacAtendimentoVO obterDadosPacientesEmAtendimento(Integer conNumero) throws ServiceException;
	
	/**
	 * Web Service #38475
	 * 
	 * Obter código do atendimento pelo número da consulta
	 * 
	 */
	Integer buscarSeqAtendimentoPorConNumero(Integer conNumero) throws ServiceException;	
	
	/**
	 * Web Service #38488
	 * Consulta utilizada para obter o UNF_SEQ de unidades funcionais com característica de unidade executora de cirurgias e Centro Obstétrico.
	 * @return UNF_SEQ
	 */
	List<Short> pesquisarUnidFuncExecutora();	
	

	/**
	 * Web Service #40702
	 * 
	 * Buscar data de início de atendimento
	 * 
	 * @param pacCodigo
	 * @param dthrInicio
	 * @return
	 */
	Date obterDataInicioAtendimentoPorPaciente(final Integer pacCodigo, final Date dthrInicio) throws ServiceException;
	
	/**
	 * Web Service #40705
	 * 
	 * Atualizar a data início do atendimento com a Data de Nascimento do Recém-Nascido na tabela AGH_ATENDIMENTOS
	 * 
	 * @param pacCodigo
	 * @param dthrInicio
	 * @param dthrNascimento
	 * @throws ServiceException
	 */
	void atualizarAtendimentoDthrNascimento(final Integer pacCodigo, final Date dthrInicio, final Date dthrNascimento) throws ServiceException;
	
	/**
	 * # 39006 - Serviço que obtem AghAtendimentos
	 * @param seq
	 * @return
	 */
	OrigemPacAtendimentoVO obterAghAtendimentosPorSeq(Integer seq) throws ServiceException;
	
	Especialidade buscarEspecialidadePorConNumero(Integer conNumero) throws ServiceException;
	
	PacAtendimentoVO obterAtendimentoPorPacienteDataInicioOrigem(final Integer pacCodigo, final Date dthrInicio, String ... origemAtendimento) throws ServiceException;
}
