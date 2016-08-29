package br.gov.mec.aghu.casca.menu.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.casca.model.Menu;
import br.gov.mec.aghu.casca.model.PalavraChaveMenu;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author kamila.nogueira
 * 
 */
public class MenucrudController extends ActionController {
	
	
	private static final long serialVersionUID = -2159102595252365686L;
	
	private static final String REDIRECIONA_PESQUISAR_MENU = "pesquisarMenu";
	
	private static final String REDIRECIONA_CADASTRAR_PALAVRA = "cadastrarPalavra";
	
	private final String REDIRECIONA_CADASTRAR_MENU = "cadastrarMenu";

	@EJB
	private ICascaFacade cascaFacade;
	
	private Menu menu;

	private List<PalavraChaveMenu> palavras;
	
	private PalavraChaveMenu palavraSelecionada;
	
	private PalavraChaveMenu palavra;
	
	public void iniciarCadastroNovo() {
		menu = new Menu();
	}
	
	public void iniciarEdicao(Menu menu) {
		this.menu = menu;
		
	}

	@PostConstruct
	protected void init(){
		iniciarCadastroNovo();
	}
	
	public void iniciar() {
		if(menu.getId() != null) {
			palavras = cascaFacade.listarPalavrasChave(menu.getId());
		}
	}
	
	/**
	 * Realiza a chamada para incluir/alterar um menu.
	 */
	public String salvar() {

		try {
			
			boolean edicao = menu.getId() != null;
			
			cascaFacade.salvarMenu(menu);

			String mensagem = edicao ? "CASCA_MENSAGEM_MENU_ALTERADO_COM_SUCESSO" : "CASCA_MENSAGEM_MENU_INCLUIDO_COM_SUCESSO";
			this.apresentarMsgNegocio(mensagem);
			
			return cancelar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelarPalavra() {
		return REDIRECIONA_CADASTRAR_MENU;
	}
	
	public String cancelar() {		
		iniciarCadastroNovo();
		
		return REDIRECIONA_PESQUISAR_MENU;
	}

	public String gravarPalavra() {
		Boolean edicaoPalavra = palavra.getId() != null;
		cascaFacade.persistir(palavra);
		String mensagem = edicaoPalavra  ? "CASCA_MENSAGEM_PALAVRA_MENU_ALTERADO_COM_SUCESSO" : "CASCA_MENSAGEM_PALAVRA_MENU_INCLUIDO_COM_SUCESSO";
		this.apresentarMsgNegocio(mensagem);
		return REDIRECIONA_CADASTRAR_MENU;
	}
	
	public String novaPalavra() {
		palavra = new PalavraChaveMenu();
		palavra.setAtivo(true);
		palavra.setMenu(menu);
		return REDIRECIONA_CADASTRAR_PALAVRA;
	}
	
	public String editarPalavra() {
		palavra = cascaFacade.obterPorChavePrimaria(palavra);
		return REDIRECIONA_CADASTRAR_PALAVRA;
	}
	
	public void removerPalavra() {
		cascaFacade.delete(palavraSelecionada);
		this.iniciar();
		apresentarMsgNegocio("CASCA_MENSAGEM_PALAVRA_MENU_EXCLUIDO_COM_SUCESSO");
	}
	
	public List<Menu> pesquisarMenuPorNome(String param) {
		return this.cascaFacade.pesquisarMenuPorNomeEId(param);
	}

	public List<Aplicacao> pesquisarAplicacaoPorNome(String param) {
		return cascaFacade.pesquisarAplicacaoPorNome(param);
	}

	// get's an set's
	public Menu getMenu() {
		return menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public Menu getMenuPai() {
		return menu.getMenuPai();
	}

	public void setMenuPai(Menu menuPai) {
		menu.setMenuPai(menuPai);
	}

	public Aplicacao getAplicacao() {
		return menu.getAplicacao();
	}

	public void setAplicacao(Aplicacao aplicacao) {
		menu.setAplicacao(aplicacao);
	}

	public List<PalavraChaveMenu> getPalavras() {
		return palavras;
	}

	public void setPalavras(List<PalavraChaveMenu> palavras) {
		this.palavras = palavras;
	}

	public PalavraChaveMenu getPalavraSelecionada() {
		return palavraSelecionada;
	}

	public void setPalavraSelecionada(PalavraChaveMenu palavraSelecionada) {
		this.palavraSelecionada = palavraSelecionada;
	}

	public PalavraChaveMenu getPalavra() {
		return palavra;
	}

	public void setPalavra(PalavraChaveMenu palavra) {
		this.palavra = palavra;
	}
}