package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.model.AipTituloLogradouros;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class TituloLogradouroController extends ActionController{

	private static final long serialVersionUID = -1532152968640074573L;
	private static final String REDIRECT_PESQUISAR_TITULO_LOGRADOURO = "tituloLogradouroList";

	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	private AipTituloLogradouros tituloLogradouro = new AipTituloLogradouros();
	
	@Inject
	private TituloLogradouroPaginatorController tituloLogradouroPaginatorController;
	
	private boolean exibirBotaoIncluirTituloLogradouro = false;

	public void iniciarEdicao(AipTituloLogradouros tituloLogradouro) {
		if (tituloLogradouro != null) { //Edição
			this.tituloLogradouro = tituloLogradouro;
		}
	}

	public String salvar() {
		try{
			this.transformarTextosCaixaAlta();
			boolean novo = false;
			if(tituloLogradouro == null || tituloLogradouro.getCodigo() == null ){
				novo = true;
			}
			cadastrosBasicosPacienteFacade.persistirTituloLogradouro(tituloLogradouro);
			
			this.apresentarMsgNegocio(Severity.INFO, novo ?
					"MENSAGEM_SUCESSO_PERSISTIR_TITULO_LOGRADOURO": "MENSAGEM_SUCESSO_EDITAR_TITULO_LOGRADOURO", this.tituloLogradouro.getDescricao());

			tituloLogradouro = new AipTituloLogradouros();

			exibirBotaoIncluirTituloLogradouro = true;
			
			this.tituloLogradouroPaginatorController.reiniciarPaginator();
			
			return REDIRECT_PESQUISAR_TITULO_LOGRADOURO;
		}
		catch (final ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	
	}
	
	private void transformarTextosCaixaAlta() {
		this.tituloLogradouro.setDescricao(this.tituloLogradouro.getDescricao() == null ? null : this.tituloLogradouro.getDescricao().toUpperCase());
	}

	
	public String cancelar() {
		tituloLogradouro = new AipTituloLogradouros();
		exibirBotaoIncluirTituloLogradouro = true;
		this.tituloLogradouroPaginatorController.reiniciarPaginator();
		return REDIRECT_PESQUISAR_TITULO_LOGRADOURO;
	}

	//SET's e GET's
	
	public AipTituloLogradouros getTituloLogradouro() {
		return tituloLogradouro;
	}

	public void setTituloLogradouro(final AipTituloLogradouros tituloLogradouros) {
		this.tituloLogradouro = tituloLogradouros;
	}

	public boolean isExibirBotaoIncluirTituloLogradouro() {
		return exibirBotaoIncluirTituloLogradouro;
	}

	public void setExibirBotaoIncluirTituloLogradouro(
			final boolean exibirBotaoIncluirTituloLogradouro) {
		this.exibirBotaoIncluirTituloLogradouro = exibirBotaoIncluirTituloLogradouro;
	}	
}