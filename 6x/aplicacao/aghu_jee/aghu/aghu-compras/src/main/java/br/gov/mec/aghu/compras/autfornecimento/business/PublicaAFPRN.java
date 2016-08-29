package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.dao.ScoPropostaFornecedorDAO;
import br.gov.mec.aghu.dominio.DominioAfpPublicado;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgrAcessoFornAfp;
import br.gov.mec.aghu.model.ScoPropostaFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class PublicaAFPRN extends BaseBusiness{

	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;
	
	@Inject
	private ScoPropostaFornecedorDAO scoPropostaFornecedorDAO;
	

	
	@EJB
	private ScoProgEntregaItemAutorizacaoFornecimentoRN scoProgEntregaItemAutorizacaoFornecimentoRN;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	private static final Log LOG = LogFactory.getLog(PublicaAFPRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8067774112360772224L;

	public Boolean publicaAfpFornecedor(Integer afnNumero, Integer afeNumero) throws ApplicationBusinessException{
		ScoAutorizacaoForn autorizacaoForn = this
				.scoAutorizacaoFornDAO.obterPorChavePrimaria(afnNumero);
		
		ScoPropostaFornecedor propostaFornecedor =  scoPropostaFornecedorDAO.obterPorChavePrimaria(autorizacaoForn.getPropostaFornecedor().getId());
		Short prazoEntrega = propostaFornecedor.getPrazoEntrega();
		
		List<ScoProgEntregaItemAutorizacaoFornecimento> listaParcelas = scoProgEntregaItemAutorizacaoFornecimentoDAO.listarParcelasNaoEnviadas(afnNumero, prazoEntrega);
		
		for(ScoProgEntregaItemAutorizacaoFornecimento parcela: listaParcelas){
			Calendar dataNova = Calendar.getInstance();
			dataNova.setTime(new Date());
			dataNova.add(Calendar.DAY_OF_YEAR, prazoEntrega);
			parcela.setDtPrevEntrega(dataNova.getTime());
			scoProgEntregaItemAutorizacaoFornecimentoRN.persistir(parcela);
		}		
		
		if (comprasFacade.existeAcessoFornecedorPorFornecedorDtEnvio(autorizacaoForn.getScoFornecedor())){
			
			ScoAutorizacaoFornecedorPedidoId id = new ScoAutorizacaoFornecedorPedidoId();
			id.setAfnNumero(afnNumero);
			id.setNumero(afeNumero);
			
			ScoAutorizacaoFornecedorPedido autorizacaoFornPedido = comprasFacade.obterScoAutorizacaoFornecimentoPedido(id);
			
			autorizacaoFornPedido.setIndPublicado(DominioAfpPublicado.P);
			autorizacaoFornPedido.setDtPublicacao(new Date());
			
			comprasFacade.persistirScoAutorizacaoFornecedorPedido(autorizacaoFornPedido);
			
			ScoProgrAcessoFornAfp progAcesso = new ScoProgrAcessoFornAfp();
			
			progAcesso.setScoFornecedor(autorizacaoForn.getScoFornecedor());
			progAcesso.setScoAutorizacaoFornecedorPedido(autorizacaoFornPedido);
			
			comprasFacade.persistirScoProgrAcessoFornAfp(progAcesso);
			
			return (comprasFacade.contarProgrEntregasPorAfeAfeNum(afnNumero, afeNumero) > 0);
			
			
		}
			
		return false;
	}
	
	
	public Boolean publicaAfpFornecedorEntrega(Integer afnNumero, Integer afeNumero) throws ApplicationBusinessException{
		ScoAutorizacaoForn autorizacaoForn = this
				.scoAutorizacaoFornDAO.obterPorChavePrimaria(afnNumero);
		
		ScoAutorizacaoFornecedorPedidoId id = new ScoAutorizacaoFornecedorPedidoId();
		id.setAfnNumero(afnNumero);
		id.setNumero(afeNumero);		
		ScoAutorizacaoFornecedorPedido autorizacaoFornPedido = comprasFacade.obterScoAutorizacaoFornecimentoPedido(id);
		
		if (comprasFacade.existeAcessoFornecedorPorFornecedorDtEnvio(autorizacaoForn.getScoFornecedor())){			
			if (!this.comprasFacade.verificarAcessosFronAFP(afnNumero, afeNumero, autorizacaoForn.getScoFornecedor())){				
				ScoProgrAcessoFornAfp progAcesso = new ScoProgrAcessoFornAfp();
				
				progAcesso.setScoFornecedor(autorizacaoForn.getScoFornecedor());
				progAcesso.setScoAutorizacaoFornecedorPedido(autorizacaoFornPedido);
				
				comprasFacade.persistirScoProgrAcessoFornAfp(progAcesso);
				
			}
		}
		
		autorizacaoFornPedido.setIndPublicado(DominioAfpPublicado.P);
		autorizacaoFornPedido.setDtPublicacao(new Date());
		
		comprasFacade.persistirScoAutorizacaoFornecedorPedido(autorizacaoFornPedido);
		
		return (comprasFacade.contarProgrEntregasPorAfeAfeNum(afnNumero, afeNumero) > 0);		
			
	}
	
}
