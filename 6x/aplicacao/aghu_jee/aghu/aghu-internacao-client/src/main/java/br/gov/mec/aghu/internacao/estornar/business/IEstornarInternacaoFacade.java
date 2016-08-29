package br.gov.mec.aghu.internacao.estornar.business;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public interface IEstornarInternacaoFacade extends Serializable {

	/**
	 * Método que verifica as regras necessárias antes de estornar a internação
	 * 
	 * @param intSeq
	 *            , justificativa
	 * @throws ApplicationBusinessException
	 */
	public boolean verificarRegrasAntesEstornar(Integer intSeq,
			String justificativa) throws BaseException;

	/**
	 * Método que realiza o estorno da internação
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	
	public void estornarInternacao(AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws ApplicationBusinessException;

}