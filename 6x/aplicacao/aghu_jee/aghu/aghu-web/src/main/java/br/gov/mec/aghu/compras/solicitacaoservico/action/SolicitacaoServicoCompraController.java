package br.gov.mec.aghu.compras.solicitacaoservico.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.model.ScoSolicitacaoCompraServico;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class SolicitacaoServicoCompraController extends ActionController {

	private static final Log LOG = LogFactory.getLog(SolicitacaoServicoCompraController.class);
	
	private static final String PAGE_SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";

	private static final long serialVersionUID = -3462929396765844271L;

	private Integer numeroSolicitacaoServico;

	private ScoSolicitacaoServico solicitacaoServico;

	private ScoSolicitacaoDeCompra solicitacaoCompra;

	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;

	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	private Integer numeroSC;

	private String voltarPara;
	private boolean adicionou;
	private boolean deletou;
	private boolean showModalExclusao;

	private List<ScoSolicitacaoCompraServico> listaSolServicosCompras = new ArrayList<ScoSolicitacaoCompraServico>();

	private ScoSolicitacaoCompraServico solCompraServico = new ScoSolicitacaoCompraServico();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void init() {
	 

	 


		this.limpar();
		if (this.getNumeroSC() != null) {
			setSolicitacaoCompra(this.solicitacaoComprasFacade.obterSolicitacaoDeCompra(this.getNumeroSC()));
		} else {
			setSolicitacaoCompra(null);
		}
		setSolicitacaoServico(solicitacaoServicoFacade.obterSolicitacaoServico(getNumeroSolicitacaoServico()));
		listaSolServicosCompras = this.solicitacaoServicoFacade.pesquisarSolicitacaoDeCompraPorServico(getSolicitacaoServico());
	
	}
	

	// Método para carregar suggestion Solicitação de Compra
	public List<ScoSolicitacaoDeCompra> pesquisarSolicitacaoCompraPorNumeroOuDescricao(String pesquisa) {

		return this.solicitacaoComprasFacade.pesquisarSolicitacaoCompraPorNumeroOuDescricao(pesquisa, true);
	}

	public void adicionar() {

		try {

			this.getSolCompraServico().setScoSolicitacaoServico(this.getSolicitacaoServico());
			this.getSolCompraServico().setScoSolicitacaoDeCompra(this.getSolicitacaoCompra());

			if (!this.getListaSolServicosCompras().contains(this.getSolCompraServico())) {
				this.getListaSolServicosCompras().add(this.getSolCompraServico());

			} else {
				this.solicitacaoServicoFacade.mensagemErroDuplicadoScoSolServicoComprasSolCompras();

			}

			this.limpar();
			this.adicionou = true;

		} catch (final ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limpar() {

		this.setSolicitacaoCompra(null);
		this.setSolCompraServico(new ScoSolicitacaoCompraServico());
		this.setShowModalExclusao(false);

	}

	public void cancelarPendenteItens() {
		this.setShowModalExclusao(false);

	}

	public void gravar() {

		try {
			// Deletar todos os registros de servicos
			for (ScoSolicitacaoCompraServico solicitacaoCompraServico : this.getListaDiferencaTelaBanco()) {
				this.solicitacaoServicoFacade.excluirScoSolCompraServico(solicitacaoCompraServico);
			}

			for (ScoSolicitacaoCompraServico solicitacaoCompraServico : this.getListaSolServicosCompras()) {
				this.solicitacaoServicoFacade.inserirScoSolCompraServico(solicitacaoCompraServico);
			}

			this.apresentarMsgNegocio(Severity.INFO, "MESSAGEM_SUCESSO_ASS_SERVICO_COMPRA_PERSISTENTE");
			this.setShowModalExclusao(false);

		} catch (final ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		}

	}

	public void excluir(ScoSolicitacaoCompraServico itemScoSolCompra) {

		this.getListaSolServicosCompras().remove(itemScoSolCompra);
		this.deletou = true;
		this.adicionou = true;

	}

	public String gerarSC() {
		return PAGE_SOLICITACAO_COMPRA_CRUD;
	}

	public List<ScoSolicitacaoCompraServico> getListaDiferencaTelaBanco() {

		List<ScoSolicitacaoCompraServico> listaSolicitacoesServCompraBanco = this.solicitacaoServicoFacade
				.pesquisarSolicitacaoDeCompraPorServico(getSolicitacaoServico());

		/****
		 * Pega todas os registros do banco e compara com os da tela restanto
		 * apenas os que devem ser apagados
		 ***/
		listaSolicitacoesServCompraBanco.removeAll(getListaSolServicosCompras());
		return listaSolicitacoesServCompraBanco;

	}

	public boolean isDiferencaTelaBanco() {

		List<ScoSolicitacaoCompraServico> listaSolicitacoesServCompraBanco = this.solicitacaoServicoFacade
				.pesquisarSolicitacaoDeCompraPorServico(getSolicitacaoServico());

		/****
		 * Pega todas os registros do banco e compara com os da tela restanto
		 * apenas os que devem ser apagados caso nao existão associações para sc
		 * verifica se usuario adicionou algo a lista
		 ***/
		if (listaSolicitacoesServCompraBanco.size() > 0) {
			boolean flagLista = (this.getListaSolServicosCompras().size() > listaSolicitacoesServCompraBanco.size());
			listaSolicitacoesServCompraBanco.removeAll(getListaSolServicosCompras());
			return (listaSolicitacoesServCompraBanco.size() > 0 || flagLista);
		} else {
			return (this.getListaSolServicosCompras().size() > 0);
		}

	}

	public String validaItensPendentes() {

		this.setShowModalExclusao(isDiferencaTelaBanco());

		if (!this.showModalExclusao) {
			return this.voltar();
		} else {
			return null;
		}

	}

	public String voltar() {
		return voltarPara;
	}

	public String novaLista() {
		this.listaSolServicosCompras = new ArrayList<ScoSolicitacaoCompraServico>();
		return "";
	}

	public void mostrarConfirmacao() {

		this.setShowModalExclusao(isDiferencaTelaBanco());
	}

	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	public Integer getNumeroSolicitacaoServico() {
		return numeroSolicitacaoServico;
	}

	public void setNumeroSolicitacaoServico(Integer numeroSolicitacaoServico) {
		this.numeroSolicitacaoServico = numeroSolicitacaoServico;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public boolean isAdicionou() {
		return adicionou;
	}

	public void setAdicionou(boolean adicionou) {
		this.adicionou = adicionou;
	}

	public boolean isDeletou() {
		return deletou;
	}

	public void setDeletou(boolean deletou) {
		this.deletou = deletou;
	}

	public boolean isShowModalExclusao() {
		return showModalExclusao;
	}

	public void setShowModalExclusao(boolean showModalExclusao) {
		this.showModalExclusao = showModalExclusao;
	}

	public List<ScoSolicitacaoCompraServico> getListaSolServicosCompras() {
		return listaSolServicosCompras;
	}

	public void setListaSolServicosCompras(List<ScoSolicitacaoCompraServico> listaSolServicosCompras) {
		this.listaSolServicosCompras = listaSolServicosCompras;
	}

	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	public ScoSolicitacaoCompraServico getSolCompraServico() {
		return solCompraServico;
	}

	public void setSolCompraServico(ScoSolicitacaoCompraServico solCompraServico) {
		this.solCompraServico = solCompraServico;
	}

	public Integer getNumeroSC() {
		return numeroSC;
	}

	public void setNumeroSC(Integer numeroSC) {
		this.numeroSC = numeroSC;
	}
}