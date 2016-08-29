package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável por prover os métodos de negócio genéricos para a entidade
 * de equipe para proffisionais.
 */
@Stateless
public class ProfissionaisEquipeCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ProfissionaisEquipeCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4700933007758564506L;

	private enum ProfissionaisEquipeCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_EQUIPE_OBRIGATORIA, ERRO_PERSISTIR_ERRO_PROFISSIONAL_EQUIPE, ERRO_PROFISSIONAL_EQUIPE_JA_EXISTENTE, ERRO_PERSISTIR_PROFISSIONAIS_EQUIPES, CODIGO_PROFISSIONAL_OBRIGATORIO;
	}

	public void persistirRapServidor(RapServidores rapServidor) throws ApplicationBusinessException {
		if (rapServidor.getId() == null) {
			// inclusão
			this.incluirRapServidor(rapServidor);
		} else {
			// edição
			this.atualizarRapServidor(rapServidor);
		}
	}

	/**
	 * Método responsável pela persistência de uma equipe para profissional.
	 * 
	 * @param rapServidor
	 *            para profissional
	 * @throws ApplicationBusinessException
	 */
	private void incluirRapServidor(RapServidores rapServidor) throws ApplicationBusinessException {
		try {
			IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();
			registroColaboradorFacade.atualizarRapServidores(rapServidor);
			flush();
		} catch (Exception e) {
			logError("Erro ao incluir a equipe para profissional.", e);
			throw new ApplicationBusinessException(ProfissionaisEquipeCRUDExceptionCode.ERRO_PERSISTIR_ERRO_PROFISSIONAL_EQUIPE);
		}
	}

	private void atualizarRapServidor(RapServidores rapServidor) throws ApplicationBusinessException {
		this.validarDados(rapServidor);

		try {
			IRegistroColaboradorFacade registroColaboradorFacade = this.getRegistroColaboradorFacade();
			registroColaboradorFacade.atualizarRapServidores(rapServidor);
			flush();
		} catch (Exception e) {
			logError("Erro ao atualizar a equipe para profissionais.", e);
			throw new ApplicationBusinessException(ProfissionaisEquipeCRUDExceptionCode.ERRO_PERSISTIR_PROFISSIONAIS_EQUIPES);
		}
	}

	private void validarDados(RapServidores rapServidor) throws ApplicationBusinessException {
		if (rapServidor.getId() == null) {
			throw new ApplicationBusinessException(ProfissionaisEquipeCRUDExceptionCode.CODIGO_PROFISSIONAL_OBRIGATORIO);
		}
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}

}
