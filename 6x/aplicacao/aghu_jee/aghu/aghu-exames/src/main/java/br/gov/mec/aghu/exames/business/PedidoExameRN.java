package br.gov.mec.aghu.exames.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelPedidoExameDAO;
import br.gov.mec.aghu.model.AelPedidoExame;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Centralizar a funcoes reutilizaveis do Modulo de Exames.
 * 
 * @author rcorvalao
 * 
 */
@Stateless
public class PedidoExameRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PedidoExameRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelPedidoExameDAO aelPedidoExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8623694116893295981L;

	/**
	 * remove todos os pedidos de exames relacionados a um atendimento.
	 * 
	 * @param atendimento
	 */
	public void excluirPedidosExamesPorAtendimento(AghAtendimentos atendimento) {
		AelPedidoExameDAO dao = getAelPedidoExameDao();

		List<AelPedidoExame> pedidosExamesDoAtendimento = dao
				.buscarPedidosExamePeloAtendimento(atendimento.getSeq());

		for (AelPedidoExame pedidoExame : pedidosExamesDoAtendimento) {
			dao.remover(pedidoExame);
		}

	}

	private AelPedidoExameDAO getAelPedidoExameDao() {
		return aelPedidoExameDAO;
	}

}
