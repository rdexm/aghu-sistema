package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoFormaPagamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class FormaPagamentoController extends ActionController {


	private static final Log LOG = LogFactory.getLog(FormaPagamentoController.class);

	private static final long serialVersionUID = 4731533190708355034L;

	private static final String FORMA_PAGAMENTO_LIST = "formaPagamentoList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private ScoFormaPagamento formaPagamento;
	private Short codigo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

	 

		if (this.getCodigo() == null) {
			this.setFormaPagamento(new ScoFormaPagamento());
			this.getFormaPagamento().setSituacao(DominioSituacao.A);
			
		} else {
			formaPagamento = comprasCadastrosBasicosFacade.obterFormaPagamento(this.getCodigo());
			
			if(formaPagamento == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		}
		return null;
	
	}

	public String gravar() {
		try {
			if (this.getFormaPagamento() != null) {
				final boolean novo = this.getFormaPagamento().getCodigo() == null;

				if (novo) {
					this.comprasCadastrosBasicosFacade.inserirFormaPagamento(this.getFormaPagamento());
					this.apresentarMsgNegocio( Severity.INFO, "MENSAGEM_FORMA_PAGAMENTO_INSERT_SUCESSO", getFormaPagamento().getDescricao());
					
				} else {
					this.comprasCadastrosBasicosFacade.alterarFormaPagamento(this.getFormaPagamento());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_FORMA_PAGAMENTO_UPDATE_SUCESSO", this.getFormaPagamento().getDescricao());
				}

				return cancelar();
			}
		} catch (final BaseException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (final BaseRuntimeException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}

	public String cancelar() {
		codigo = null;
		formaPagamento = null;
		return FORMA_PAGAMENTO_LIST;
	}

	public ScoFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(ScoFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}
	
	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}
}
