package br.gov.mec.aghu.internacao.administracao.business;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IAdministracaoInternacaoFacade extends Serializable {

	/**
	 * Valida campos de entrada da tela.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public void validaCampos(Integer prontuario, Integer codigo)
			throws ApplicationBusinessException;

	/**
	 * Metodo que busca os dados do paciente a partir de parametros.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	
	public AipPacientes buscarDadosPaciente(Integer prontuario, Integer codigo, Long cpf)
			throws ApplicationBusinessException;

	/**
	 * 
	 * @param dataObito
	 * @param tipoDataObito
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	public void validarDataObito(Date dataObito,
			DominioTipoDataObito tipoDataObito, AipPacientes paciente)
			throws ApplicationBusinessException;

	/**
	 * Persiste as alterações em paciente, salvando a data de óbito do mesmo.
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	
	public void atualizarDataObito(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException;

	/**
	 * Método responsável pela atualizacao de uma internação.
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	public AinInternacao atualizarInternacao(Integer intSeq, Integer prontuario, String nomeMicrocomputador) throws BaseException;

	public String getStringKeyErroGenericoIntegracaoModulo();

}