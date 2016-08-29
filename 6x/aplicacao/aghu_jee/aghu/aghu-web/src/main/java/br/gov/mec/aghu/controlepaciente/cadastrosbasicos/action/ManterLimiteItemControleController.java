package br.gov.mec.aghu.controlepaciente.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controlepaciente.cadastrosbasicos.business.ICadastrosBasicosControlePacienteFacade;
import br.gov.mec.aghu.model.EcpItemControle;
import br.gov.mec.aghu.model.EcpLimiteItemControle;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterLimiteItemControleController extends ActionController {

	private static final long serialVersionUID = -2203899856004802839L;
	
	private static final String PESQUISAR_LIMITES_ITEM_CONTROLE = "pesquisarLimitesItemControle";

	@EJB
	private ICadastrosBasicosControlePacienteFacade cadastrosBasicosControlePacienteFacade;

	private EcpItemControle itemControle;
	private EcpLimiteItemControle limiteItemControle;
	
	private boolean emEdicao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

	 

		//Edição ou Criação de Limites do item de controle
		if(limiteItemControle != null && limiteItemControle.getSeq() != null){
			limiteItemControle = cadastrosBasicosControlePacienteFacade.obterLimiteItemControle(limiteItemControle.getSeq());
			emEdicao = true;
			
		}else{
			//Inclusão de limite de item de controle
			limiteItemControle = new EcpLimiteItemControle();
			emEdicao = false;
		}
	
	}
	

	public String salvar() {
		try {
			if(emEdicao){
				cadastrosBasicosControlePacienteFacade.alterarLimiteItemControle(limiteItemControle);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_LIMITE_ITEM_CONTROLE");
				
			} else {
				limiteItemControle.setItemControle(itemControle);
				cadastrosBasicosControlePacienteFacade.inserirLimiteItemControle(limiteItemControle);
	
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_INCLUSAO_LIMITE_ITEM_CONTROLE");
			}
			
			return cancelar();
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public void limpar() {
		limiteItemControle = null;
		emEdicao = false;
	}

	public String cancelar() {
		limpar();
		return PESQUISAR_LIMITES_ITEM_CONTROLE;
	}

	public EcpItemControle getItemControle() {
		return itemControle;
	}

	public void setItemControle(EcpItemControle itemControle) {
		this.itemControle = itemControle;
	}

	public EcpLimiteItemControle getLimiteItemControle() {
		return limiteItemControle;
	}

	public void setLimiteItemControle(EcpLimiteItemControle limiteItemControle) {
		this.limiteItemControle = limiteItemControle;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}
}