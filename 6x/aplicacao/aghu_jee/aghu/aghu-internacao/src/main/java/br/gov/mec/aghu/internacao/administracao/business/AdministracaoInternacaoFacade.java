package br.gov.mec.aghu.internacao.administracao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;

/**
 * Porta de entrada do sub-módulo administração do módulo de internação.
 * 
 * @author lcmoura
 * 
 */

@Modulo(ModuloEnum.INTERNACAO)
@Stateless
public class AdministracaoInternacaoFacade extends BaseFacade implements IAdministracaoInternacaoFacade {


@EJB
private TrocarPacienteInternacaoON trocarPacienteInternacaoON;

@EJB
private AtualizarDataObitoON atualizarDataObitoON;

	private static final long serialVersionUID = 1894562120132057006L;

	/**
	 * Valida campos de entrada da tela.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validaCampos(Integer prontuario, Integer codigo) throws ApplicationBusinessException {
		getAtualizarDataObitoON().validaCampos(prontuario, codigo);
	}

	/**
	 * Metodo que busca os dados do paciente a partir de parametros.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('paciente','pesquisar')}")
	public AipPacientes buscarDadosPaciente(Integer prontuario, Integer codigo, Long cpf) throws ApplicationBusinessException {
		return getAtualizarDataObitoON().buscarDadosPaciente(prontuario, codigo, cpf);
	}


	/**
	 * 
	 * @param dataObito
	 * @param tipoDataObito
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validarDataObito(Date dataObito, DominioTipoDataObito tipoDataObito, AipPacientes paciente)
			throws ApplicationBusinessException {
		getAtualizarDataObitoON().validarDataObito(dataObito, tipoDataObito, paciente);
	}

	/**
	 * Persiste as alterações em paciente, salvando a data de óbito do mesmo.
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	@Override
	@Secure("#{s:hasPermission('paciente','alterarObito')}")
	public void atualizarDataObito(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		getAtualizarDataObitoON().atualizarDataObito(paciente, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	
	/**
	 * Método responsável pela atualizacao de uma internação.
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public AinInternacao atualizarInternacao(Integer intSeq, Integer prontuario, String nomeMicrocomputador) throws BaseException {
		return getTrocarPacienteInternacaoON().atualizarInternacao(intSeq, prontuario, nomeMicrocomputador);
	}
	
	@Override
	public String getStringKeyErroGenericoIntegracaoModulo() {
		return getTrocarPacienteInternacaoON().getStringKeyErroGenericoIntegracaoModulo();
	}

	protected AtualizarDataObitoON getAtualizarDataObitoON() {
		return atualizarDataObitoON;
	}
	
	protected TrocarPacienteInternacaoON getTrocarPacienteInternacaoON() {
		return trocarPacienteInternacaoON;
	}
}
