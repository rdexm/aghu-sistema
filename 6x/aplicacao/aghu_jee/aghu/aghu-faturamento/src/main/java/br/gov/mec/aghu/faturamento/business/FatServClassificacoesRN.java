package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.FatCompetenciaCompatibilidRN.FatCompetenciaCompatibilidRNExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatServClassificacoesDAO;
import br.gov.mec.aghu.model.FatServClassificacoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


/**
 * @author eschweigert
 */
@Stateless
public class FatServClassificacoesRN extends BaseBusiness {

	private static final long serialVersionUID = 1262042335265881331L;

	private static final Log LOG = LogFactory.getLog(FatServClassificacoesRN.class);
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatServClassificacoesDAO fatServClassificacoesDAO;
	
	public void persistirFatServClassificacoes(final FatServClassificacoes servClass) throws ApplicationBusinessException{
		if(servClass.getSeq() == null){
			inserir(servClass);
		} else {
			alterar(servClass);
		}
	}

	public void excluir(final FatServClassificacoes servClass) {
		getFatServClassificacoesDAO().remover(servClass);
	}
	
	private void alterar(final FatServClassificacoes servClass) throws ApplicationBusinessException {
		antesDeAlterar(servClass);
		getFatServClassificacoesDAO().atualizar(servClass);
	}

	/**
	 * ORADB: FATT_FCS_BRU
	 */
	private void antesDeAlterar(final FatServClassificacoes servClass) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(FatCompetenciaCompatibilidRNExceptionCode.RAP_00175);
		}
		
		servClass.setAlteradoEm(new Date());
		servClass.setServidorAlterado(servidorLogado);
		
		if(servClass.getServidor() == null){
			servClass.setServidor(servidorLogado);
		}
	}
	
	
	
	private void inserir(final FatServClassificacoes servClass) throws ApplicationBusinessException {
		antesDeInserir(servClass);
		getFatServClassificacoesDAO().persistir(servClass);
	}


	/**
	 * ORADB: FATT_FCS_BRI
	 */
	private void antesDeInserir(final FatServClassificacoes servClass) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(FatCompetenciaCompatibilidRNExceptionCode.RAP_00175);
		}
		
		final Date data = new Date();
		
		servClass.setCriadoEm(data);
		servClass.setAlteradoEm(data);
		servClass.setServidor(servidorLogado);
		servClass.setServidorAlterado(servidorLogado);
	}
	

	protected FatServClassificacoesDAO getFatServClassificacoesDAO() {
		return fatServClassificacoesDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
