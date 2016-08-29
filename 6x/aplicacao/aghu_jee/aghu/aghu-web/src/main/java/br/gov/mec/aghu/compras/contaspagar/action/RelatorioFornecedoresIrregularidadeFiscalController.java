package br.gov.mec.aghu.compras.contaspagar.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.business.IContasPagarFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.compras.contaspagar.vo.FornecedorVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RelatorioFornecedoresIrregularidadeFiscalController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8273529834505061611L;
	
	@EJB
	private IContasPagarFacade contasPagarFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private RelatorioVencimentoFornecedorController relatorioVencimentoFornecedorController;
	
	private static final String PAGE_RELATORIO_FORN_IRREGULAR = "relatorioFornecedoresIrregularidadeFiscal";
	
	private FornecedorVO fornecedor;
	private Date dataInicial;
	private Date dataFinal;
	private List<DatasVencimentosFornecedorVO> listaFornecedores;
	
	private boolean pesquisaAtiva;
	
	private boolean todosSelecionados;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.dataFinal = new Date();
	}
	
	/*
	 * Métodos para SuggestionBox
	 */
	public List<FornecedorVO> pesquisarFornecedores(final String pesquisa) {
        List<FornecedorVO> result = comprasFacade.pesquisarFornecedoresAtivosVO(pesquisa, 0, 100, null, true);
        Long count = comprasFacade.listarFornecedoresAtivosCount(pesquisa);
		return this.returnSGWithCount(result,count);
	}


	public void pesquisar() {
		try {
			this.todosSelecionados = false;
			this.pesquisaAtiva = true;
			this.listaFornecedores = this.contasPagarFacade
					.pesquisarFornecedoresIrregularidadeFiscal(this.dataInicial, this.dataFinal,
							this.fornecedor != null ? this.fornecedor.getNumero() : null);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparPesquisa() {
		this.listaFornecedores = null;
		this.fornecedor = null;
		this.dataInicial = null;
		this.dataFinal = new Date();
		this.pesquisaAtiva = false;
		this.todosSelecionados = false;
	}
	
	private List<DatasVencimentosFornecedorVO> obterListaSelecionados() {
		List<DatasVencimentosFornecedorVO> selecionados = new ArrayList<DatasVencimentosFornecedorVO>();
		for (DatasVencimentosFornecedorVO vo : this.listaFornecedores) {
			if (vo.getSelecionado().equals(Boolean.TRUE)) {
				selecionados.add(vo);
			}
		}
		return selecionados;
	}
	
	public void imprimirRelatorio() {
		relatorioVencimentoFornecedorController.setColecao(listaFornecedores);
		relatorioVencimentoFornecedorController.setVoltarPara(PAGE_RELATORIO_FORN_IRREGULAR);
		relatorioVencimentoFornecedorController.directPrint();
	}
	
	public String visualizarRelatorio() {
		relatorioVencimentoFornecedorController.setColecao(listaFornecedores);
		relatorioVencimentoFornecedorController.setVoltarPara(PAGE_RELATORIO_FORN_IRREGULAR);
		return relatorioVencimentoFornecedorController.visualizarRelatorioJaPreenchido();
	}
	
	public void enviarEmail() {
		List<DatasVencimentosFornecedorVO> selecionados = obterListaSelecionados();
		if (possuiSelecionados(selecionados)) {
			try {
				this.contasPagarFacade.enviarEmailFornecedoresIrregularidadeFiscal(selecionados);
				pesquisar();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_COMUNICADOS_ENVIADOS_POR_EMAIL");
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void selecionarTodosFornecedores(){
		for (DatasVencimentosFornecedorVO vo : this.listaFornecedores) {
			vo.setSelecionado(this.todosSelecionados);
		}
	}
	
	private boolean possuiSelecionados(List<DatasVencimentosFornecedorVO> selecionados) {
		if (selecionados.isEmpty()) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SELECIONE_UM_FORNECEDOR_IRREGULAR");
			return false;
		}
		return true;
	}
	
	public String truncarDescricao(String descricao) {
		int limite = 30;
		if (StringUtils.length(descricao) >= limite) {
			return StringUtils.substring(descricao, 0, limite).concat("...");
		}
		return descricao;
	}
	
	// ### GETs e SETs ###
	
	public FornecedorVO getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(FornecedorVO fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public List<DatasVencimentosFornecedorVO> getListaFornecedores() {
		return listaFornecedores;
	}

	public void setListaFornecedores(
			List<DatasVencimentosFornecedorVO> listaFornecedores) {
		this.listaFornecedores = listaFornecedores;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public boolean isTodosSelecionados() {
		return todosSelecionados;
	}

	public void setTodosSelecionados(boolean todosSelecionados) {
		this.todosSelecionados = todosSelecionados;
		for (DatasVencimentosFornecedorVO vo : this.listaFornecedores) {
			vo.setSelecionado(this.todosSelecionados);
		}
	}
}

