package br.gov.mec.aghu.compras.solicitacaoservico.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoSolicitacaoCompraServicoDAO;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServico;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServicoId;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoSolicitacaoCompraServicoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(ScoSolicitacaoCompraServicoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoSolicitacaoCompraServicoDAO scoSolicitacaoCompraServicoDAO;

	private static final long serialVersionUID = -4338416372797483187L;

	public enum ScoSolicitacaoCompraServicoONExceptionCode implements
			BusinessExceptionCode {MENSAGEM_PARAM_OBRIG,
		 MENSAGEM_SOL_SERVICO_DUPLICADO, MENSAGEM_SOL_COMPRA_DUPLICADO;
	}

	public List<ScoSolicitacaoCompraServico> listarSolicitacaoCompraServico(
			ScoSolicitacaoDeCompra numeroSC, ScoSolicitacaoServico numeroSS) {
		
		return getScoSolicitacaoCompraServicoDAO().listarSolicitacaoCompraServico(numeroSC, numeroSS);		
		
	}

	public void inserirSolicitacaoCompraServico(ScoSolicitacaoCompraServico solicitacaoCompraServico)
			throws ApplicationBusinessException {
		
		if (solicitacaoCompraServico == null) {
			throw new ApplicationBusinessException(
					ScoSolicitacaoCompraServicoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		ScoSolicitacaoCompraServicoId id =  new ScoSolicitacaoCompraServicoId();
		id.setSlcNumero(solicitacaoCompraServico.getScoSolicitacaoDeCompra().getNumero());
		id.setSlsNumero(solicitacaoCompraServico.getScoSolicitacaoServico().getNumero());
		
		solicitacaoCompraServico.setId(id);
		
		this.getScoSolicitacaoCompraServicoDAO().persistir(solicitacaoCompraServico);
		this.getScoSolicitacaoCompraServicoDAO().flush();
	}

	public void excluirSolicitacaoCompraServico(ScoSolicitacaoCompraServico solicitacaoCompraServico)
			throws ApplicationBusinessException {

		if (solicitacaoCompraServico == null) {
			throw new ApplicationBusinessException(
					ScoSolicitacaoCompraServicoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		this.getScoSolicitacaoCompraServicoDAO().remover(
				this.getScoSolicitacaoCompraServicoDAO().obterPorChavePrimaria(solicitacaoCompraServico.getId()));
		this.getScoSolicitacaoCompraServicoDAO().flush();
	}
	
	
	public void excluirSolicitacaoCompraServico(List<ScoSolicitacaoCompraServico> solCompraServicoList)
			throws ApplicationBusinessException {
		for (ScoSolicitacaoCompraServico solicitacaoCompraServico : solCompraServicoList){
					
			if (solicitacaoCompraServico == null) {
				throw new ApplicationBusinessException(
						ScoSolicitacaoCompraServicoONExceptionCode.MENSAGEM_PARAM_OBRIG);
			}
			
			ScoSolicitacaoCompraServicoId id =  new ScoSolicitacaoCompraServicoId();
			id.setSlcNumero(solicitacaoCompraServico.getScoSolicitacaoDeCompra().getNumero());
			id.setSlsNumero(solicitacaoCompraServico.getScoSolicitacaoServico().getNumero());
			
			solicitacaoCompraServico.setId(id);
			
			this.getScoSolicitacaoCompraServicoDAO().persistir(solicitacaoCompraServico);
			this.getScoSolicitacaoCompraServicoDAO().flush();
		}	
	}
	
	public void mensagemErroDuplicadoScoSolServicoCompras() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(
				ScoSolicitacaoCompraServicoONExceptionCode.MENSAGEM_SOL_SERVICO_DUPLICADO);		
	}
	
	public void mensagemErroDuplicadoScoSolServicoComprasSolCompras() throws ApplicationBusinessException {
		throw new ApplicationBusinessException(
				ScoSolicitacaoCompraServicoONExceptionCode.MENSAGEM_SOL_COMPRA_DUPLICADO);		
	}
	
	public List<ScoSolicitacaoCompraServico> pesquisarSolicitacaoDeCompraPorServico(ScoSolicitacaoServico solicitacaoServico){
		return getScoSolicitacaoCompraServicoDAO().pesquisarSolicitacaoDeCompraPorServico(solicitacaoServico);
	}
		

	private ScoSolicitacaoCompraServicoDAO getScoSolicitacaoCompraServicoDAO() {
		return scoSolicitacaoCompraServicoDAO;
	}
	
}