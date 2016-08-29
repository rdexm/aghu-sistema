package br.gov.mec.aghu.controlepaciente.service;

import java.util.List;

import javax.ejb.Local;

import br.gov.mec.aghu.controlepaciente.vo.DadosSinaisVitaisVO;
import br.gov.mec.aghu.controlepaciente.vo.EcpItemControleVO;
import br.gov.mec.aghu.controlepaciente.vo.RegistroControlePacienteServiceVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Local
@Deprecated
public interface IControlePacienteService {

	/**
	 * Busca os dados dos respectivos sinais vitais
	 * 
	 * #35315
	 * 
	 * @param listSeqs
	 * @return
	 */
	List<DadosSinaisVitaisVO> pesquisarDadosSinaisVitais(List<Short> listSeqs) throws ServiceException, ApplicationBusinessException;

	/**
	 * Busca os sinais vitais por sigla, descricao, grupo e situacao
	 * 
	 * #35290
	 * 
	 * @param strPesquisa
	 * @param seqGrupo
	 * @param maxResults
	 * @return
	 */
	List<DadosSinaisVitaisVO> pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupo(String strPesquisa, Integer seqGrupo, Integer maxResults);

	/**
	 * Busca número de sinais vitais por sigla, descricao, grupo e situacao
	 * 
	 * #35290
	 * 
	 * @param strPesquisa
	 * @param seqGrupo
	 * @return
	 */
	Long pesquisarSinaisVitaisAtivosPorSiglaDescricaoSeqGrupoCount(String strPesquisa, Integer seqGrupo);
	
	Boolean verificarExisteSinalVitalPorPaciente(Short iceSeq, Integer pacCodigo) throws ServiceException;
	
	String obterDescricaoItemControle(Short iceSeq) throws ServiceException;

	/**
	 *  Busca os valores dos últimos Sinais Vitais que foram registrados até uma hora antes da consulta, sendo infirmado o Número da Consulta.
	 * 
	 * #35602
	 * 
	 * @return
	 */
	List<DadosSinaisVitaisVO> pesquisarUltimosSinaisVitaisPelaConsulta(
			Integer consulta) throws ServiceException,
			ApplicationBusinessException;

	List<DadosSinaisVitaisVO> pesquisarUltimosSinaisVitaisPeloCodigoPaciente(
			Integer pacCodigo) throws ServiceException,
			ApplicationBusinessException;
	
	List<EcpItemControleVO> buscarItensControlePorPacientePeriodo(
			Integer pacCodigo, Long trgSeq);

	List<RegistroControlePacienteServiceVO> pesquisarRegistrosPaciente(
			Integer pacCodigo, List<EcpItemControleVO> listaItensControle, Long trgSeq)
			throws ApplicationBusinessException;
	
	Boolean verificarExisteSinalVitalPorPaciente(Integer pacCodigo) throws ServiceException;

	void excluirRegistroControlePaciente(Long seqHorarioControle) throws ServiceException, ApplicationBusinessException;

	boolean validaUnidadeFuncionalInformatizada(Short unfSeq) throws ApplicationBusinessException;
	
	void atualizarAtdSeqHorarioControlePaciente(Integer atdSeq, Long trgSeq) throws ServiceException;
}
