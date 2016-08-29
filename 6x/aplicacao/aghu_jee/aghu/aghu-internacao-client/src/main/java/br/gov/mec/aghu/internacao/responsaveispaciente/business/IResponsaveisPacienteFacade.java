package br.gov.mec.aghu.internacao.responsaveispaciente.business;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public interface IResponsaveisPacienteFacade extends Serializable {

	/**
	 * Método que obtém o número de responsáveis por um paciente para uma certa
	 * internação
	 * 
	 * @param intSeq
	 * @return listaResponsaveisPaciente
	 */
	
	public List<AinResponsaveisPaciente> pesquisarResponsaveisPaciente(
			Integer intSeq);

	/**
	 * ORADB Procedure ainp_delete_rep_rows Métoro responsável por remover
	 * responsáveis de um paciente Método sincronizado devido ao semáforo na
	 * procedure
	 * 
	 * @param responsaveisPaciente
	 * @throws ApplicationBusinessException
	 */
	
	public void removerResponsaveisPacienteInternacao(
			List<AinResponsaveisPaciente> responsaveisPaciente)
			throws ApplicationBusinessException;

	/**
	 * Método que obtém o responsável do paciente contratante
	 * 
	 * @param intSeq
	 * @return AinResponsaveisPaciente
	 */
	
	public AinResponsaveisPaciente obterResponsaveisPacienteTipoConta(
			Integer intSeq);
	
	/**
	 * Método que obtém o nome do responsável do paciente contratante
	 * 
	 * @param intSeq
	 * @return AinResponsaveisPaciente
	 */
	public String obterNomeResponsavelPacienteTipoConta(Integer intSeq);

	/**
	 * Método responsável pela atualização da lista de responsáveis pelo
	 * paciente.
	 * 
	 * @param listaResponsaveisPaciente
	 * @throws ApplicationBusinessException
	 */
	
	
	public void atualizarListaResponsaveisPaciente(
			List<AinResponsaveisPaciente> listaResponsaveisPaciente,
			List<AinResponsaveisPaciente> listaResponsaveisPacienteExcluidos,
			AinInternacao internacao, List<AinResponsaveisPaciente> listResponsavelPacienteOld) throws ApplicationBusinessException;

	/**
	 * Método que valida as regras de responsáveis de paciente
	 * 
	 * @param listaResponsaveisPaciente
	 * @param listaResponsaveisPacienteExcluidos
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public void validarRegrasResponsaveisPaciente(
			List<AinResponsaveisPaciente> listaResponsaveisPaciente,
			AinInternacao internacao) throws ApplicationBusinessException;

}