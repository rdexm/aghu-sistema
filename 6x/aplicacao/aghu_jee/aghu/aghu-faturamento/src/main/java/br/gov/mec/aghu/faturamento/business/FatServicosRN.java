package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.FatCompetenciaCompatibilidRN.FatCompetenciaCompatibilidRNExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatServicosDAO;
import br.gov.mec.aghu.model.FatServicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


/**
 * @author eschweigert
 */
@Stateless
public class FatServicosRN extends BaseBusiness {

	private static final long serialVersionUID = -3472390557979534732L;

	private static final Log LOG = LogFactory.getLog(FatServicosRN.class);
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private FatServicosDAO fatServicosDAO;
	
	public void persistirFatServicos(final FatServicos servico) throws ApplicationBusinessException{
		if(servico.getSeq() == null){
			inserir(servico);
		} else {
			alterar(servico);
		}
	}

	public void excluir(final FatServicos servico) {
		getFatServicosDAO().remover(servico);
	}
	
	private void alterar(final FatServicos servico) throws ApplicationBusinessException {
		antesDeAlterar(servico);
		getFatServicosDAO().merge(servico);
	}

	/**
	 * ORADB: FATT_FSE_BRU
	 */
	private void antesDeAlterar(final FatServicos servico) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(FatCompetenciaCompatibilidRNExceptionCode.RAP_00175);
		}
		
		servico.setAlteradoEm(new Date());
		servico.setServidorAlterado(servidorLogado);
		
		if(servico.getServidor() == null){
			servico.setServidor(servidorLogado);
		}
	}
	
	
	
	private void inserir(final FatServicos servico) throws ApplicationBusinessException {
		antesDeInserir(servico);
		getFatServicosDAO().persistir(servico);
	}


	/**
	 * ORADB: FATT_FSE_BRI
	 */
	private void antesDeInserir(final FatServicos servico) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(FatCompetenciaCompatibilidRNExceptionCode.RAP_00175);
		}
		
		final Date data = new Date();
		
		servico.setCriadoEm(data);
		servico.setAlteradoEm(data);
		servico.setServidor(servidorLogado);
		servico.setServidorAlterado(servidorLogado);
	}

	protected FatServicosDAO getFatServicosDAO() {
		return fatServicosDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
