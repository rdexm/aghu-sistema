package br.gov.mec.aghu.internacao.estornar.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.model.AinInternacao;

/**
 * Porta de entrada do sub-módulo Estornar Internação do módulo de Internação.
 * 
 * @author lcmoura
 * 
 */

@Modulo(ModuloEnum.INTERNACAO)
@Stateless
public class EstornarInternacaoFacade extends BaseFacade implements IEstornarInternacaoFacade{


@EJB
private EstornarInternacaoON estornarInternacaoON;

	private static final long serialVersionUID = -3013371625692604277L;

	/**
	 * Método que verifica as regras necessárias antes de estornar a internação
	 * 
	 * @param intSeq
	 *            , justificativa
	 * @throws ApplicationBusinessException
	 */
	@Override
	public boolean verificarRegrasAntesEstornar(Integer intSeq, String justificativa) throws BaseException {
		return getEstornarInternacaoON().verificarRegrasAntesEstornar(intSeq, justificativa);
	}

	/**
	 * Método que realiza o estorno da internação
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('internacao','extornar')}")
	public void estornarInternacao(AinInternacao internacao, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		getEstornarInternacaoON().estornarInternacao(internacao, nomeMicrocomputador, dataFimVinculoServidor);
	}

	protected EstornarInternacaoON getEstornarInternacaoON() {
		return estornarInternacaoON;
	}
}
