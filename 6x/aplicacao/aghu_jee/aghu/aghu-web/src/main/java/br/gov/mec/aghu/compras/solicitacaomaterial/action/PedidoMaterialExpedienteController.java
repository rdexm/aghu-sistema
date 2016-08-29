package br.gov.mec.aghu.compras.solicitacaomaterial.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoPedidoMatExpediente;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class PedidoMaterialExpedienteController extends ActionController {

	private static final long serialVersionUID = 6542225319650381298L;

	private final String PAGE_VISUALIZAR_CONSULTA = "pedidoMaterialExpedienteList";
	
	private static final Log LOG = LogFactory.getLog(PedidoMaterialExpedienteController.class);
	
	@EJB
	private IRegistroColaboradorFacade colaboradorFacade;

	@EJB
	private IComprasFacade comprasFacade;

	private ScoPedidoMatExpediente pedido;

	private Integer seq;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 

		if (this.seq != null) {
			this.pedido = this.comprasFacade
					.obterScoPedidoMatExpPorChavePrimaria(this.seq);
		}
	
	}
	

	public String conferir() {
		try {			
			atualizarPedido(this.pedido, Boolean.FALSE);
			//reiniciarPaginator(PedidoMaterialExpedientePaginatorController.class);
			return PAGE_VISUALIZAR_CONSULTA;
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	private void atualizarPedido(ScoPedidoMatExpediente pedido, Boolean recusa)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = this.colaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),new Date());
		this.comprasFacade.atualizarScoPedidoMatExp(pedido, recusa,	servidorLogado);
	}

	public String recusar() {
		try {			
			this.atualizarPedido(this.pedido, Boolean.TRUE);
			//reiniciarPaginator(PedidoMaterialExpedientePaginatorController.class);
			return PAGE_VISUALIZAR_CONSULTA;
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelar() {
		return PAGE_VISUALIZAR_CONSULTA;
	}

	public void setPedido(ScoPedidoMatExpediente pedido) {
		this.pedido = pedido;
	}

	public ScoPedidoMatExpediente getPedido() {
		return pedido;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}

	public Boolean getIndValidacaoPedido() {
		if(this.pedido != null) {
			return this.pedido.getIndValidacaoPedido();
		} else {
			return Boolean.FALSE;
		}
	}
}
