package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RamalTelefonicoRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RamalTelefonicoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7076576469856518504L;

	public enum RamalTelefonicoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RAMALTELEFONICO_NAO_INFORMADO, MENSAGEM_RAMALTELEFONICO_UTILIZADO_COLABORADOR;
	}

	/**
	 * Constraint RAP_SER_RAM_FK1
	 * O aplicativo permite a exclusão de um ramal. Ao excluir um ramal, verificar se ele não está sendo usado no registro do colaborador - RapServidores
	 */
	public void validaExclusaoRamalTelefonico(RapRamalTelefonico ramalTelefonico) throws ApplicationBusinessException  {
		if (ramalTelefonico == null || ramalTelefonico.getNumeroRamal() == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}
		
		Long registros = this.getRegistroColaboradorFacade().pesquisarServidorCount(ramalTelefonico);
		
		if (registros > 0) {
			throw new ApplicationBusinessException(RamalTelefonicoRNExceptionCode.MENSAGEM_RAMALTELEFONICO_UTILIZADO_COLABORADOR);
		}
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
}