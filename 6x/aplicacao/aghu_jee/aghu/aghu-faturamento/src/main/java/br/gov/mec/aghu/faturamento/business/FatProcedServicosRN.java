package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.business.FatCompetenciaCompatibilidRN.FatCompetenciaCompatibilidRNExceptionCode;
import br.gov.mec.aghu.faturamento.dao.FatProcedServicosDAO;
import br.gov.mec.aghu.model.FatProcedServicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


/**
 * @author eschweigert
 */
@Stateless
public class FatProcedServicosRN extends BaseBusiness {

	private static final long serialVersionUID = -3472390557979534732L;
	
	private static final Log LOG = LogFactory.getLog(FatProcedServicosRN.class);
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject 
	private FatProcedServicosDAO fatProcedServicosDAO;
	
	void persistirFatProcedServicos(final FatProcedServicos procedServico) throws ApplicationBusinessException{
		if(procedServico.getSeq() == null){
			inserir(procedServico);
		} else {
			alterar(procedServico);
		}
	}

	void excluir(final FatProcedServicos procedServico) {
		getFatProcedServicosDAO().remover(procedServico);
	}
	
	private void alterar(final FatProcedServicos procedServico) throws ApplicationBusinessException {
		antesDeAlterar(procedServico);
		getFatProcedServicosDAO().atualizar(procedServico);
	}

	/**
	 * ORADB: FATT_PSC_BRU
	 */
	private void antesDeAlterar(final FatProcedServicos procedServico) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(FatCompetenciaCompatibilidRNExceptionCode.RAP_00175);
		}
		
		procedServico.setAlteradoEm(new Date());
		procedServico.setServidorAlterado(servidorLogado);

		if(procedServico.getServidor() == null){
			procedServico.setServidor(servidorLogado);
		}
		
	}
	
	
	
	private void inserir(final FatProcedServicos procedServico) throws ApplicationBusinessException {
		antesDeInserir(procedServico);
		getFatProcedServicosDAO().persistir(procedServico);
	}

	/**
	 * ORADB: FATT_PSC_BRI
	 */
	private void antesDeInserir(final FatProcedServicos procedServico) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(FatCompetenciaCompatibilidRNExceptionCode.RAP_00175);
		}
		
		final Date data = new Date();
		
		procedServico.setCriadoEm(data);
		procedServico.setAlteradoEm(data);
		procedServico.setServidor(servidorLogado);
		procedServico.setServidorAlterado(servidorLogado);
	}

	
	protected FatProcedServicosDAO getFatProcedServicosDAO() {
		return fatProcedServicosDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
