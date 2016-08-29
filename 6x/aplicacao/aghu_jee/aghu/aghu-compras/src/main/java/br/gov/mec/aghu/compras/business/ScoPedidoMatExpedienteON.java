package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.compras.dao.ScoPedidoMatExpedienteDAO;
import br.gov.mec.aghu.compras.vo.ScoPedidoMatExpedienteVO;
import br.gov.mec.aghu.model.ScoPedItensMatExpediente;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoPedidoMatExpedienteON extends BaseBusiness{ 

	public enum ScoPedidoMatExpedienteONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_ERRO_CONSULTA_EMAIL_FORNECEDOR, MENSAGEM_ERRO_CONSULTA_EMAIL_SUPORTE, MENSAGEM_PARAMETRO_EMAIL_NAO_CADASTRADO,
		DATA_INICIAL_SUPERIOR_A_FINAL;
	}

	
	private static final long serialVersionUID = 4257768394534622012L;

	@Inject
	private ScoPedidoMatExpedienteDAO scoPedidoMatExpedienteDAO;

	@EJB
	public IParametroFacade parametroFacade;


	public ScoPedidoMatExpediente obterScoPedidoMatExpPorChavePrimaria(
			Integer seq) {
		ScoPedidoMatExpediente pedido = this.getScoPedidoMatExpedienteDAO()
				.obterPedidoMatExpPorChavePrimaria(seq);
		if (pedido != null) {
			if (pedido.getValorTotal() == null) {
				pedido = calcularTotalPedido(pedido);
			}
		}
		return pedido;
	}

	private ScoPedidoMatExpediente calcularTotalPedido(
			ScoPedidoMatExpediente pedido) {
		List<ScoPedItensMatExpediente> itens = pedido.getListaItens();
		BigDecimal totalPedido = BigDecimal.ZERO;		
		for (ScoPedItensMatExpediente scoPedItensMatExpediente : itens) {
			totalPedido = totalPedido.add(scoPedItensMatExpediente
					.getValorTotal());
		}
		pedido.setValorTotal(totalPedido);
		return pedido;
	}
	
	public List<ScoPedidoMatExpediente> pesquisarPedidoMatExp(final ScoPedidoMatExpedienteVO filtro, final Integer firstResult,
			final Integer maxResults, final String orderProperty, final boolean asc) throws ApplicationBusinessException {
		
		this.validaDatas(filtro.getDataInicioPedido(), filtro.getDataFimPedido());
		this.validaDatas(filtro.getDataInicioEmissao(), filtro.getDataFimEmissao());
				
		return this.getScoPedidoMatExpedienteDAO().pesquisarPedidoMatExp(filtro, firstResult, maxResults, orderProperty, asc);
	}
	
	public List<ScoPedidoMatExpedienteVO> pesquisarNotasFiscais(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, ScoPedidoMatExpedienteVO scoPedidoMatExpedienteVO) 
	throws ApplicationBusinessException {
		
		return this.getScoPedidoMatExpedienteDAO().pesquisarNotasFiscais(firstResult, maxResult, orderProperty, asc, scoPedidoMatExpedienteVO);
	}
	
	
	public void validaDatas(Date dtInicial, Date dtFinal) throws ApplicationBusinessException {
	
		if (dtInicial != null && dtFinal != null) {
			if (dtInicial.after(dtFinal)) {
				throw new ApplicationBusinessException(ScoPedidoMatExpedienteONExceptionCode.DATA_INICIAL_SUPERIOR_A_FINAL);
			}
		}
	}

		protected ScoPedidoMatExpedienteDAO getScoPedidoMatExpedienteDAO() {
		return scoPedidoMatExpedienteDAO;
	}

	protected void setScoPedidoMatExpedienteDAO(ScoPedidoMatExpedienteDAO scoPedidoMatExpedienteDAO) {
		this.scoPedidoMatExpedienteDAO = scoPedidoMatExpedienteDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected void setParametroFacade(IParametroFacade parametroFacade) {
		this.parametroFacade = parametroFacade;
	}

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}	

}
