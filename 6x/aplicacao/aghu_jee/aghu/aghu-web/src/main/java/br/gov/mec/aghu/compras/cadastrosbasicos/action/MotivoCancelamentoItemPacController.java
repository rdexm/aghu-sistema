package br.gov.mec.aghu.compras.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMotivoCancelamentoItem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Conjunto de regras de negócio para estória
 * "#24709 - Cadastro de Motivo de Cancelamento de Itens PAC"
 * @author joao.gloria
 */
public class MotivoCancelamentoItemPacController extends ActionController{

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final long serialVersionUID = -8915932045976673620L;
	private static final String MOTIVO_CANCELAMENTO_ITEM_PAC_LIST = "motivoCancelamentoItemPacList";

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private String codigo;
	private ScoMotivoCancelamentoItem scoMotivoCancelamentoItem;
	private Boolean edicao;
	private Boolean indAtivo;

	public void iniciar()
	{
	 

	 

		if(this.getCodigo() == null){
			this.setScoMotivoCancelamentoItem(new ScoMotivoCancelamentoItem());
			this.setIndAtivo(true);
			this.getScoMotivoCancelamentoItem().setIndAtivo(DominioSituacao.A);
			this.setEdicao(false);
		}
		else{
			//Busca o elemento para editar
			this.setScoMotivoCancelamentoItem(this.comprasCadastrosBasicosFacade.obterScoMotivoCancelamentoItem(this.getCodigo()));
			this.setEdicao(true);
			
			if (this.scoMotivoCancelamentoItem.getIndAtivo() == DominioSituacao.A) {
				this.setIndAtivo(true);
			}
			else {
				this.setIndAtivo(false);
			}
		}
	
	}
	
		
	public String gravar() {
		
		try {
			if (this.indAtivo) {
				this.scoMotivoCancelamentoItem.setIndAtivo(DominioSituacao.A);
			}
			else {
				this.scoMotivoCancelamentoItem.setIndAtivo(DominioSituacao.I);
			}
			
			this.comprasCadastrosBasicosFacade.persistirScoMotivoCancelamentoItem(this.getScoMotivoCancelamentoItem());

			if (this.scoMotivoCancelamentoItem.getVersion()>0 ) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MOTIVO_CANCELAMENTO_ITEM_PAC");
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MOTIVO_CANCELAMENTO_ITEM_PAC");
			}
			
			this.limpar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return MOTIVO_CANCELAMENTO_ITEM_PAC_LIST;
	}
	
	private void limpar(){
		this.codigo = null;
		this.edicao = Boolean.FALSE;
		this.indAtivo = Boolean.FALSE;
		this.scoMotivoCancelamentoItem = null;
	}
	
	public String cancelar() {
		this.setScoMotivoCancelamentoItem(new ScoMotivoCancelamentoItem());
		return MOTIVO_CANCELAMENTO_ITEM_PAC_LIST;
	}
	
	public ScoMotivoCancelamentoItem getScoMotivoCancelamentoItem() {
		return scoMotivoCancelamentoItem;
	}

	public void setScoMotivoCancelamentoItem(
			ScoMotivoCancelamentoItem scoMotivoCancelamentoItem) {
		this.scoMotivoCancelamentoItem = scoMotivoCancelamentoItem;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}
	
	public void setCodigo(String codigo){
		this.codigo = codigo;
	}
	
	public String getCodigo(){
		return this.codigo;
	}
	
	public Boolean getIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(Boolean indAtivo) {
		this.indAtivo = indAtivo;
	}

}
