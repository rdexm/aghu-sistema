package br.gov.mec.aghu.exames.cadastrosapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelServidorUnidAssinaElet;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


@Stateless
public class AelServidorUnidAssinaEletRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AelServidorUnidAssinaEletRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = 7105275324002030883L;
	
	/**
	 * @ORADB AELT_SUA_BRI 
	 * @param entidade
	 * @throws ApplicationBusinessException 
	 */
	public void aeltSuaBri(AelServidorUnidAssinaElet entidade) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(entidade == null){
			entidade = new AelServidorUnidAssinaElet();
		}
		Date dataAtual = new Date();
		entidade.setCriadoEm(dataAtual);
		entidade.setAlteradoEm(dataAtual);
		
		entidade.setServidorCriado(servidorLogado);
		entidade.setServidorAlterado(servidorLogado);
	}
	
	
	/**
	 * @ORADB AELT_SUA_BRU
	 * @param entidade
	 * @throws ApplicationBusinessException 
	 */
	public void aeltSuaBru(AelServidorUnidAssinaElet entidade) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Date dataAtual = new Date();
		entidade.setAlteradoEm(dataAtual);
		
		entidade.setServidorAlterado(servidorLogado);
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}