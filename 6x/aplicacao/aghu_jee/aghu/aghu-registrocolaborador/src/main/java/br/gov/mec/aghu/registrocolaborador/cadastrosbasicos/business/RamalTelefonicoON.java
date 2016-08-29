package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.RapRamalTelefonico;
import br.gov.mec.aghu.registrocolaborador.dao.RapRamalTelefonicoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RamalTelefonicoON extends BaseBusiness {

	
	@EJB
	private RamalTelefonicoRN ramalTelefonicoRN;
	
	private static final Log LOG = LogFactory.getLog(RamalTelefonicoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	
	@Inject
	private RapRamalTelefonicoDAO rapRamalTelefonicoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2228852649415567972L;

	public enum RamalTelefonicoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RAMALTELEFONICO_NAO_INFORMADO, MENSAGEM_RAMALTELEFONICO_JA_CADASTRADO, MENSAGEM_PARAMETRO_NAO_INFORMADO;
	}

	
	public void salvar(RapRamalTelefonico rapRamalTelefonico, boolean alteracao) throws ApplicationBusinessException {

		if (rapRamalTelefonico == null || rapRamalTelefonico.getNumeroRamal() < 0) {
			throw new ApplicationBusinessException(RamalTelefonicoONExceptionCode.MENSAGEM_RAMALTELEFONICO_NAO_INFORMADO);
		}

		if (! alteracao) { // esta cadastrando um novo ramal
			RapRamalTelefonico ramalExistente = obterRamalTelefonico(rapRamalTelefonico.getNumeroRamal());
			if (ramalExistente != null) {
				throw new ApplicationBusinessException(RamalTelefonicoONExceptionCode.MENSAGEM_RAMALTELEFONICO_JA_CADASTRADO);
				
			} else {
				rapRamalTelefonicoDAO.persistir(rapRamalTelefonico);
			}
			
		} else { // esta alterando o ramal existente
			rapRamalTelefonicoDAO.merge(rapRamalTelefonico);
		}
	}

	public RapRamalTelefonico obterRamalTelefonico(Integer id) throws ApplicationBusinessException {
		if (id == null) {
			throw new ApplicationBusinessException(RamalTelefonicoONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		return getRamalTelefonicoDAO().obterPorChavePrimaria(id); 
	}

	
	public void excluirRamalTelefonico(Integer nrRamalTelefonico) throws ApplicationBusinessException  {
		if (nrRamalTelefonico == null) {
			throw new ApplicationBusinessException(RamalTelefonicoONExceptionCode.MENSAGEM_RAMALTELEFONICO_NAO_INFORMADO);
		}
		
		RapRamalTelefonico paraExcluir = obterRamalTelefonico(nrRamalTelefonico);
		getRamalTelefonicoRN().validaExclusaoRamalTelefonico(paraExcluir);
		
		rapRamalTelefonicoDAO.remover(paraExcluir);
	}

	protected RamalTelefonicoRN getRamalTelefonicoRN() {
		return ramalTelefonicoRN;
	}
	
	protected RapRamalTelefonicoDAO getRamalTelefonicoDAO() {
		return rapRamalTelefonicoDAO;
	}
}