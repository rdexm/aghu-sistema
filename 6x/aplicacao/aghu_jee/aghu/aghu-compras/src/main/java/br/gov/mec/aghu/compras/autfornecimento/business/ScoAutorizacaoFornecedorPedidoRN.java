package br.gov.mec.aghu.compras.autfornecimento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ScoAutorizacaoFornecedorPedidoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ScoAutorizacaoFornecedorPedidoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoAutorizacaoFornecedorPedidoDAO scoAutorizacaoFornecedorPedidoDAO;

	private static final long serialVersionUID = 2211955322392231841L;

	public void persistir(final ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido) {
		if(autorizacaoFornecedorPedido.getVersion() == null){
			this.getScoAutorizacaoFornecedorPedidoDAO().persistir(autorizacaoFornecedorPedido);
		} else {
			this.getScoAutorizacaoFornecedorPedidoDAO().atualizar(autorizacaoFornecedorPedido);
		}
	}

	protected ScoAutorizacaoFornecedorPedidoDAO getScoAutorizacaoFornecedorPedidoDAO() {
		return scoAutorizacaoFornecedorPedidoDAO;
	}

}
