package br.gov.mec.aghu.tabelassistema.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioCategoriaTabela;
import br.gov.mec.aghu.model.AghCoresTabelasSistema;
import br.gov.mec.aghu.model.AghTabelasSistema;

public class TabelasSistemaPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject @Paginator
	private DynamicDataModel<AghTabelasSistema> dataModel;

	@Inject
	private TabelasSistemaController tabelasSistemaController;
	
	private static final String PAGE_CRUD = "tabelasSistemaCRUD";
	
	private Integer tabelaSistemaSeq;
	private String nomeTabelaSistema;
	private AghCoresTabelasSistema corTabelaSistema;
	private DominioCategoriaTabela categoriaTabelaSistema;
	private String versaoTabelaSistema;
	private String origemTabelaSistema;
	
	private Integer seqTabelaSistemaExclusao;
	
	private boolean exibirBotaoIncluirTabela;
	
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluirTabela = true;
	}
	
	public void limparPesquisa() {
		this.exibirBotaoIncluirTabela = false;
		this.dataModel.setPesquisaAtiva(false);
		tabelaSistemaSeq = null;
		nomeTabelaSistema = null;
		corTabelaSistema = null;
		categoriaTabelaSistema = null;
		versaoTabelaSistema = null;
	}
	
	public void confirmarExclusao(Integer seq) {
		this.seqTabelaSistemaExclusao = seq;
		super.openDialog("modalConfirmacaoExclusaoWG");
	}
	
	private static final long serialVersionUID = 3367734253141009167L;

	public String editar() {
		tabelasSistemaController.iniciar();
		return PAGE_CRUD;
	}
	
	public String incluir() {
		tabelasSistemaController.setSeqTabela(null);
		tabelasSistemaController.iniciar();
		return PAGE_CRUD;
	}
	
	@Override
	public Long recuperarCount() {
		return this.aghuFacade.pesquisarTabelasSistemaCount(tabelaSistemaSeq, nomeTabelaSistema,
				corTabelaSistema, categoriaTabelaSistema, versaoTabelaSistema, origemTabelaSistema);
	}

	@Override
	public List<AghTabelasSistema> recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		return this.aghuFacade
				.pesquisarTabelasSistema(firstResult, maxResult, orderProperty,
						asc, tabelaSistemaSeq, nomeTabelaSistema,
						corTabelaSistema, categoriaTabelaSistema,
						versaoTabelaSistema, origemTabelaSistema);
	}
	
	public void excluir(Integer seq) {
		this.seqTabelaSistemaExclusao = seq;
		AghTabelasSistema tabelaSistema = aghuFacade.obterTabelaSistemaPeloId(this.seqTabelaSistemaExclusao);
		try {
			
			if (tabelaSistema != null) {
				aghuFacade.removerTabelaSistema(tabelaSistema);
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_REMOCAO_TABELA_SISTEMA",
						tabelaSistema.getNome());
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,
						"MENSAGEM_ERRO_REMOCAO_TABELA_SISTEMA");
			}

			this.tabelaSistemaSeq = null;
			this.dataModel.reiniciarPaginator();
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally{
			this.seqTabelaSistemaExclusao = null;
		}
	}
	
	public List<AghCoresTabelasSistema> pesquisarCoresTabelasSistema(String parametro){
		return aghuFacade.pesquisarCoresTabelasSistema(parametro);
	}
	
	public String obterEstiloColuna(AghTabelasSistema tabelaSistema) {
		String retorno = "";
		if (tabelaSistema != null && tabelaSistema.getCor() != null) {
			if ("Preta".equalsIgnoreCase(tabelaSistema.getCor().getCor())){
				retorno = "background-color:#000000; ";
			}
			else if ("Vermelha".equalsIgnoreCase(tabelaSistema.getCor().getCor())){
				retorno = "background-color:#FF0000; ";
			}
			else if ("Amarela".equalsIgnoreCase(tabelaSistema.getCor().getCor())){
				retorno = "background-color:#FFFF00; ";
						}
			else if ("Verde".equalsIgnoreCase(tabelaSistema.getCor().getCor())){
				retorno = "background-color:#7FFF00; ";
			}
			else if ("Roxa".equalsIgnoreCase(tabelaSistema.getCor().getCor())){
				retorno = "background-color:#800080; ";
			}
			else if ("Cinza".equalsIgnoreCase(tabelaSistema.getCor().getCor())){
				retorno = "background-color:#808080; ";
			}
		}

		return retorno;
	}	

	public String countNumLinhasTabela(AghTabelasSistema tabelaSistema) {

		StringBuilder result = new StringBuilder();

		boolean existeTabela = aghuFacade.verificarTabelaExistente(tabelaSistema.getNome());

		if (existeTabela) {
			result.append(aghuFacade.countNumLinhasTabela(tabelaSistema.getNome()).toString());
		} else {
			result.append('-');
		}

		return result.toString();
	}

	public Integer getTabelaSistemaSeq() {
		return tabelaSistemaSeq;
	}

	public void setTabelaSistemaSeq(Integer tabelaSistemaSeq) {
		this.tabelaSistemaSeq = tabelaSistemaSeq;
	}

	public String getNomeTabelaSistema() {
		return nomeTabelaSistema;
	}

	public void setNomeTabelaSistema(String nomeTabelaSistema) {
		this.nomeTabelaSistema = nomeTabelaSistema;
	}

	public AghCoresTabelasSistema getCorTabelaSistema() {
		return corTabelaSistema;
	}

	public void setCorTabelaSistema(AghCoresTabelasSistema corTabelaSistema) {
		this.corTabelaSistema = corTabelaSistema;
	}

	public DominioCategoriaTabela getCategoriaTabelaSistema() {
		return categoriaTabelaSistema;
	}

	public void setCategoriaTabelaSistema(
			DominioCategoriaTabela categoriaTabelaSistema) {
		this.categoriaTabelaSistema = categoriaTabelaSistema;
	}

	public String getVersaoTabelaSistema() {
		return versaoTabelaSistema;
	}

	public void setVersaoTabelaSistema(String versaoTabelaSistema) {
		this.versaoTabelaSistema = versaoTabelaSistema;
	}

	public Integer getSeqTabelaSistemaExclusao() {
		return seqTabelaSistemaExclusao;
	}

	public void setSeqTabelaSistemaExclusao(Integer seqTabelaSistemaExclusao) {
		this.seqTabelaSistemaExclusao = seqTabelaSistemaExclusao;
	}

	public boolean isExibirBotaoIncluirTabela() {
		return exibirBotaoIncluirTabela;
	}

	public void setExibirBotaoIncluirTabela(boolean exibirBotaoIncluirTabela) {
		this.exibirBotaoIncluirTabela = exibirBotaoIncluirTabela;
	}

	public String getOrigemTabelaSistema() {
		return origemTabelaSistema;
	}

	public void setOrigemTabelaSistema(String origemTabelaSistema) {
		this.origemTabelaSistema = origemTabelaSistema;
	}

	public DynamicDataModel<AghTabelasSistema> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghTabelasSistema> dataModel) {
		this.dataModel = dataModel;
	}
}