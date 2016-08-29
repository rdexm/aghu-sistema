package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroFatTipoCaractItensController extends ActionController {


	private static final long serialVersionUID = 3168102810916461797L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	private static final String PAGINA_LISTAGEM = "pesquisaFatTipoCaractItens";

	private Boolean modoEdicao;

	private FatTipoCaractItens entidade;
	

	public void iniciar() {
	 

		if(getEntidade() == null || getEntidade().getSeq() == null) {
			setEntidade(new FatTipoCaractItens());			
			modoEdicao = false;
		} else {
			modoEdicao = true;
			setEntidade(faturamentoFacade.obterTipoCaracteristicaItemPorChavePrimaria(getEntidade().getSeq()));
		}
	
	}

	public String cancelar() {
		limparParametros();
		return PAGINA_LISTAGEM;
	}



	public String gravar() {
		try {
			if(getModoEdicao()) {
				faturamentoFacade.atualizarFatTipoCaractItens(getEntidade());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_FTCI",getEntidade().getCaracteristica());
			} else {
				faturamentoFacade.inserirFatTipoCaractItens(getEntidade());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_FTCI",getEntidade().getCaracteristica());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return cancelar();
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
	
	
	public Boolean getModoEdicao() {
		return modoEdicao;
	}


	private void limparParametros() {
		setModoEdicao(false);
		setEntidade(null);
	}

	public FatTipoCaractItens getEntidade() {
		return entidade;
	}

	public void setEntidade(FatTipoCaractItens entidade) {
		this.entidade = entidade;
	}
}
