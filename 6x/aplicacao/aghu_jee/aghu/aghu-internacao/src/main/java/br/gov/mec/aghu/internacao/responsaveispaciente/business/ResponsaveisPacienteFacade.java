package br.gov.mec.aghu.internacao.responsaveispaciente.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;

/**
 * Porta de entrada do sub-módulo Responsaveis de Paciente do módulo de
 * internação.
 * 
 * @author lcmoura
 * 
 */

@Modulo(ModuloEnum.INTERNACAO)

@Stateless
public class ResponsaveisPacienteFacade extends BaseFacade implements IResponsaveisPacienteFacade {


@EJB
private ResponsaveisPacienteON responsaveisPacienteON;

	private static final long serialVersionUID = -7882497430000562662L;

	/**
	 * Método que obtém o número de responsáveis por um paciente para uma certa
	 * internação
	 * 
	 * @param intSeq
	 * @return listaResponsaveisPaciente
	 */
	@Override
	@Secure("#{s:hasPermission('responsavelPaciente','pesquisar')}")
	public List<AinResponsaveisPaciente> pesquisarResponsaveisPaciente(Integer intSeq) {
		return getResponsaveisPacienteON().pesquisarResponsaveisPaciente(intSeq);
	}

	/**
	 * ORADB Procedure ainp_delete_rep_rows Métoro responsável por remover
	 * responsáveis de um paciente Método sincronizado devido ao semáforo na
	 * procedure
	 * 
	 * @param responsaveisPaciente
	 * @throws ApplicationBusinessException
	 */
 	@Override
	@Secure("#{s:hasPermission('responsavelPaciente','excluir')}")
	public synchronized void removerResponsaveisPacienteInternacao(List<AinResponsaveisPaciente> responsaveisPaciente)
			throws ApplicationBusinessException {
		getResponsaveisPacienteON().removerResponsaveisPacienteInternacao(responsaveisPaciente);
	}

	/**
	 * Método que obtém o responsável do paciente contratante
	 * 
	 * @param intSeq
	 * @return AinResponsaveisPaciente
	 */
	@Override
	@Secure("#{s:hasPermission('responsavelPaciente','pesquisar')}")
	public AinResponsaveisPaciente obterResponsaveisPacienteTipoConta(Integer intSeq) {
		return getResponsaveisPacienteON().obterResponsaveisPacienteTipoConta(intSeq);
	}
	
	/**
	 * Método que obtém o nome do responsável do paciente contratante
	 * 
	 * @param intSeq
	 * @return AinResponsaveisPaciente
	 */
	@Override
	@Secure("#{s:hasPermission('responsavelPaciente','pesquisar')}")
	public String obterNomeResponsavelPacienteTipoConta(Integer intSeq) {
		return getResponsaveisPacienteON().obterNomeResponsavelPacienteTipoConta(intSeq);
	}

	/**
	 * Método responsável pela atualização da lista de responsáveis pelo
	 * paciente.
	 * 
	 * @param listaResponsaveisPaciente
	 * @throws ApplicationBusinessException
	 */
    @Override
	
 	@Secure("#{s:hasPermission('responsavelPaciente','excluir')}")
	public void atualizarListaResponsaveisPaciente(List<AinResponsaveisPaciente> listaResponsaveisPaciente,
			List<AinResponsaveisPaciente> listaResponsaveisPacienteExcluidos, AinInternacao internacao, 
			List<AinResponsaveisPaciente> listResponsavelPacienteOld) throws ApplicationBusinessException {

		getResponsaveisPacienteON().atualizarListaResponsaveisPaciente(listaResponsaveisPaciente, listaResponsaveisPacienteExcluidos,
				internacao, listResponsavelPacienteOld);

	}

	/**
	 * Método que valida as regras de responsáveis de paciente
	 * 
	 * @param listaResponsaveisPaciente
	 * @param listaResponsaveisPacienteExcluidos
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void validarRegrasResponsaveisPaciente(List<AinResponsaveisPaciente> listaResponsaveisPaciente, AinInternacao internacao)
			throws ApplicationBusinessException {
		getResponsaveisPacienteON().validarRegrasResponsaveisPaciente(listaResponsaveisPaciente, internacao);
	}

	protected ResponsaveisPacienteON getResponsaveisPacienteON() {
		return responsaveisPacienteON;
	}
	
}
