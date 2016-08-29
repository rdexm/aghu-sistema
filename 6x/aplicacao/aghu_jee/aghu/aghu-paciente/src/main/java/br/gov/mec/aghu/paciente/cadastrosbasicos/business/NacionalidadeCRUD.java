package br.gov.mec.aghu.paciente.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.AipNacionalidades;
import br.gov.mec.aghu.paciente.dao.AipNacionalidadesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de nacionalidade.
 */
@Stateless
public class NacionalidadeCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(NacionalidadeCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IBancoDeSangueFacade iBancoDeSangueFacade;

@Inject
private AipPacientesDAO aipPacientesDAO;

@Inject
private AipNacionalidadesDAO aipNacionalidadesDAO;

@EJB
private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8801008045081126481L;

	private enum NacionalidadeCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_PERSISTIR_NACIONALIDADE, SIGLA_NACIONALIDADE_OBRIGATORIO, DESCRICAO_NACIONALIDADE_OBRIGATORIO, 
		SIGLA_NACIONALIDADE_JA_EXISTENTE, DESCRICAO_NACIONALIDADE_JA_EXISTENTE, CODIGO_NACIONALIDADE_JA_EXISTENTE, 
		ERRO_REMOVER_NACIONALIDADE, ERRO_REMOVER_NACIONALIDADE_COM_PESSOA_FISICA, ERRO_REMOVER_NACIONALIDADE_COM_CANDIDATOS, 
		ERRO_REMOVER_NACIONALIDADE_COM_CANDIDATOS_DOADORES, ERRO_REMOVER_NACIONALIDADE_COM_PACIENTES;
	}
	
	/**
	 * Método responsável por incluir uma nova nacionalidade.
	 * 
	 * @param nacionalidade
	 * @throws ApplicationBusinessException
	 */
	public void incluirNacionalidade(AipNacionalidades nacionalidade) throws ApplicationBusinessException {
		this.validarDadosNacionalidade(nacionalidade, true);
		getAipNacionalidadesDAO().persistir(nacionalidade);
	}

	/**
	 * Método responsável pela atualização de uma nacionalidade.
	 * 
	 * @param nacionalidade
	 * @throws ApplicationBusinessException
	 */
	public void atualizarNacionalidade(AipNacionalidades nacionalidade) throws ApplicationBusinessException {
		this.validarDadosNacionalidade(nacionalidade, false);
		getAipNacionalidadesDAO().atualizar(nacionalidade);
	}

	/**
	 * Método responsável pelas validações dos dados de nacionalidade. Método
	 * utilizado para inclusão e atualização de nacionalidades.
	 * 
	 * @param nacionalidade
	 * @throws ApplicationBusinessException
	 */
	private void validarDadosNacionalidade(AipNacionalidades nacionalidade,
			Boolean novaNacionalidade) throws ApplicationBusinessException {
		if (StringUtils.isBlank(nacionalidade.getSigla())) {
			throw new ApplicationBusinessException(
					NacionalidadeCRUDExceptionCode.SIGLA_NACIONALIDADE_OBRIGATORIO);
		}

		if (StringUtils.isBlank(nacionalidade.getDescricao())) {
			throw new ApplicationBusinessException(
					NacionalidadeCRUDExceptionCode.DESCRICAO_NACIONALIDADE_OBRIGATORIO);
		}

		List<AipNacionalidades> nacionalidades = null;

		if (nacionalidade.getSigla() != null) {
			nacionalidades = getAipNacionalidadesDAO().getNacionalidadesComMesmaSigla(nacionalidade);
			if (nacionalidades != null && !nacionalidades.isEmpty()) {
				throw new ApplicationBusinessException(
						NacionalidadeCRUDExceptionCode.SIGLA_NACIONALIDADE_JA_EXISTENTE);
			}
		}

		if (novaNacionalidade && nacionalidade.getCodigo() != null) {
			nacionalidades = getAipNacionalidadesDAO().getNacionalidadesComMesmoCodigo(nacionalidade);
			if (nacionalidades != null && !nacionalidades.isEmpty()) {
				throw new ApplicationBusinessException(
						NacionalidadeCRUDExceptionCode.CODIGO_NACIONALIDADE_JA_EXISTENTE);
			}
		}

		// validar descrição duplicada
		nacionalidades = getAipNacionalidadesDAO().getNacionalidadesComMesmaDescricao(nacionalidade);
		if (nacionalidades != null && !nacionalidades.isEmpty()) {
			throw new ApplicationBusinessException(
					NacionalidadeCRUDExceptionCode.DESCRICAO_NACIONALIDADE_JA_EXISTENTE);
		}

	}


	/**
	 * Método para excluir uma nacionalidade.
	 *  
	 */
	public void excluirNacionalidade(AipNacionalidades nacionalidade) throws ApplicationBusinessException{
		
		Long pessoasFisicasCount = getRegistroColaboradorFacade()
				.pesquisarPessoasFisicasPorNacionalidadeCount(nacionalidade);
		
		if (pessoasFisicasCount > 0){
			throw new ApplicationBusinessException(
					NacionalidadeCRUDExceptionCode.ERRO_REMOVER_NACIONALIDADE_COM_PESSOA_FISICA);
		}
		
		Long candidatosCount = getRegistroColaboradorFacade()
				.pesquisarCandidatosPorNacionalidadeCount(nacionalidade);
		
		if (candidatosCount > 0){
			throw new ApplicationBusinessException(
					NacionalidadeCRUDExceptionCode.ERRO_REMOVER_NACIONALIDADE_COM_CANDIDATOS);
		}
		
		Long candidatosDoadoresCount = getBancoDeSangueFacade()
			.pesquisarCandidatosDoadoresPorNacionalidadeCount(nacionalidade);
		
		if (candidatosDoadoresCount > 0){
			throw new ApplicationBusinessException(
					NacionalidadeCRUDExceptionCode.ERRO_REMOVER_NACIONALIDADE_COM_CANDIDATOS_DOADORES);
		}
		
		Long pacientesCount = getAipPacientesDAO()
				.pesquisarPacientesPorNacionalidadeCount(nacionalidade);
		
		if (pacientesCount > 0){
			throw new ApplicationBusinessException(
					NacionalidadeCRUDExceptionCode.ERRO_REMOVER_NACIONALIDADE_COM_PACIENTES);
		}
		
		nacionalidade = getAipNacionalidadesDAO().merge(nacionalidade);
		
		this.getAipNacionalidadesDAO().remover(nacionalidade);
	}

	protected AipNacionalidadesDAO getAipNacionalidadesDAO(){
		return aipNacionalidadesDAO;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade(){
		return this.iBancoDeSangueFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}

	protected AipPacientesDAO getAipPacientesDAO(){
		return aipPacientesDAO;
	}
	
}
