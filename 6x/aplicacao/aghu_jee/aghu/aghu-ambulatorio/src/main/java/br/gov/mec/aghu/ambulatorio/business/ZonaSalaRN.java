package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ZonaSalaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ZonaSalaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6088714391950348257L;

	public enum ZonaSalaRNExceptionCode implements BusinessExceptionCode {
		LABEL_NAO_POSSIVEL_RETORNAR_OCORRENCIA_EXCLUIDA,
		LABEL_NAO_POSSIVEL_ALTERAR_OCORRENCIA_EXCLUIDA
		;
	}	
	
	/**
	 * Trigger executada antes de inserir
	 * 
	 * ORADB Trigger AACT_USL_BRI
	 * @param zonaSala
	 * @throws ApplicationBusinessException  
	 */
	public void zonaSalaAntesInserir(AacUnidFuncionalSalas zonaSala) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		zonaSala.setCriadoEm(new Date());
		zonaSala.setAlteradoEm(new Date());
		zonaSala.setServidor(servidorLogado);
	}
	
	/**
	 * Trigger executada antes de atualizar
	 * 
	 * ORADB Trigger AACT_USL_BRU
	 * @param zonaSala
	 */
	public void zonaSalaAntesAtualizar(AacUnidFuncionalSalas oldZonaSala, AacUnidFuncionalSalas newZonaSala) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		newZonaSala.setAlteradoEm(new Date());
		newZonaSala.setServidorAlterado(servidorLogado);
		
		if (oldZonaSala.getIndExcluido() && !newZonaSala.getIndExcluido()) {
			throw new ApplicationBusinessException(
					ZonaSalaRNExceptionCode.LABEL_NAO_POSSIVEL_RETORNAR_OCORRENCIA_EXCLUIDA);
		}
		if (oldZonaSala.getIndExcluido() && newZonaSala.getIndExcluido()) {
			throw new ApplicationBusinessException(
					ZonaSalaRNExceptionCode.LABEL_NAO_POSSIVEL_ALTERAR_OCORRENCIA_EXCLUIDA);
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
