package br.gov.mec.aghu.internacao.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.PersistenceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AlaCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AlaCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade iAghuFacade;

@EJB
private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private static final long serialVersionUID = 900799621242899945L;
	
	private enum AlaCRUDExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_ALA, ALA_PK_DUPLICADA ;
	}
	

	public AghAla buscaAghAlaPorId(String codigo) {
		return getAghuFacade().obterAghAlaPorChavePrimaria(codigo);
	}
	
	public void excluirAghAla(String codigo) throws ApplicationBusinessException {
		AghAla ala = null;
		try {
			 ala = this.cadastrosBasicosInternacaoFacade.buscaAghAlaPorId(codigo);
			getAghuFacade().excluirAghAla(ala);
			flush();
			
		} catch (PersistenceException e) {
			logError("Erro ao remover a ParametroSistema.", e);
			throw new ApplicationBusinessException(AlaCRUDExceptionCode.ERRO_REMOVER_ALA, ala.getCodigo());
		}		
	}
	
	public List<AghAla> pesquisaAlaList(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String codigo, String descricao) {
		return getAghuFacade().pesquisaAlaList( firstResult,
			 maxResult,  orderProperty,  asc,
			 codigo,  descricao);
	}

	public Long pesquisaAlaListCount(String codigo, String descricao) {
		return getAghuFacade().pesquisaAlaListCount(codigo,  descricao);
	}
	
	public AghAla persistirAghAla(AghAla ala, boolean isUpdate) throws ApplicationBusinessException {
		this.validarInclusaoAlteracaoAla(ala, isUpdate);
		
		if (isUpdate) {
			getAghuFacade().atualizarAghAla(ala);
		} else {
			ala.setCodigo(ala.getCodigo().toUpperCase());
			getAghuFacade().persistirAghAla(ala);
		}
		
		flush();
		return ala;
	}
	
	private void validarInclusaoAlteracaoAla(AghAla ala, boolean isUpdate) throws ApplicationBusinessException {
		if (!isUpdate) {
			AghAla s = this.getAghuFacade().obterAghAlaPorChavePrimaria(ala.getCodigo());
			if (s != null) {
				throw new ApplicationBusinessException(AlaCRUDExceptionCode.ALA_PK_DUPLICADA, ala.getCodigo());
			}
		}
	}

	protected IAghuFacade getAghuFacade() {
		return this.iAghuFacade;
	}
	
}
