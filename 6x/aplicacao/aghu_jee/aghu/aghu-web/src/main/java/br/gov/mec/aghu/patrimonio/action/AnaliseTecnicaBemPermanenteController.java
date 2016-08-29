package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AnaliseTecnicaBemPermanenteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável pelo controle dos componentes, ações e valores da tela Solicitar Análise Técnica de Bem Permanente.
 *
 */
public class AnaliseTecnicaBemPermanenteController extends ActionController {

	private static final long serialVersionUID = -3857362754652099177L;

	private static final String SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";

	@EJB
	private IPatrimonioFacade patrimonioFacade;

	private Integer numeroRecebimento;
	
	private List<AnaliseTecnicaBemPermanenteVO> itensRecebimentoList;
	
	private List<AnaliseTecnicaBemPermanenteVO> itensSelecionados;

	private AnaliseTecnicaBemPermanenteVO itemMouseOver;
	
	private PtmAreaTecAvaliacao areaTecnicaAvaliacao;
	
	private Boolean allChecked;

	// Variável criada para receber o estado de exibição do checkbox, devido ao componente não atualizar corretamente por conta própria.
	private boolean allCheckbox;

	/**
	 * Método chamado no início da tela, para inicialização da conversação.
	 */
	@PostConstruct
	protected void inicializar() {

		this.begin(conversation);
		iniciar();
	}

	/**
	 * Método responsável por realizar as regras de inicialização da tela.
	 */
	public void iniciar() {

		if (numeroRecebimento != null) {
			pesquisar();
		} else {
			allChecked = false;
			allCheckbox = false;
			itensSelecionados = new ArrayList<AnaliseTecnicaBemPermanenteVO>();
		}
	}

	/**
	 * Método que realiza a ação do botão Pesquisar.
	 */
	public void pesquisar() {

		try {
			allChecked = false;
			allCheckbox = false;
			itensSelecionados = new ArrayList<AnaliseTecnicaBemPermanenteVO>();
			itensRecebimentoList = patrimonioFacade.consultarItensRecebimento(numeroRecebimento);
			if (!itensRecebimentoList.isEmpty()) {
				List<PtmAreaTecAvaliacao> areas = patrimonioFacade.listarAreaTecAvaliacaoPorCodigoCentroCusto(itensRecebimentoList.get(0).getCentroCustoAutTec());
				if (!areas.isEmpty()) {
					areaTecnicaAvaliacao = areas.get(0);
				}
			}
		} catch (ApplicationBusinessException e) {
			if(e.getMessage().equals("ITEM_RECEBIMENTO_NAO_ENCONTRADO")){
				itensRecebimentoList = new ArrayList<AnaliseTecnicaBemPermanenteVO>();
			}
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método que realiza a ação do botão Limpar.
	 */
	public void limpar() {

		allChecked = false;
		allCheckbox = false;
		numeroRecebimento = null;
		areaTecnicaAvaliacao = null;
		itensRecebimentoList = null;
		itensSelecionados = new ArrayList<AnaliseTecnicaBemPermanenteVO>();
	}

	/**
	 * Método que realiza a ação de consulta do suggestion box de Área Técnica de Avaliação.
	 * @param parametro - Parâmetro informado
	 * @return Lista de Áreas
	 */
	public List<PtmAreaTecAvaliacao> pesquisarAreas(String parametro) {
		return returnSGWithCount(this.patrimonioFacade.pesquisarAreaTecAvaliacaoPorCodigoNomeOuCC((String) parametro), this.patrimonioFacade.pesquisarAreaTecAvaliacaoPorCodigoNomeOuCCCount((String) parametro));
	}

	/**
	 * Método que realiza a ação do botão Adicionar.
	 */
	public void adicionar() {
		try {
			this.patrimonioFacade.atualizarItensRecebimentoAnaliseTecnica(itensSelecionados, areaTecnicaAvaliacao, numeroRecebimento);
			
			apresentarMsgNegocio(Severity.INFO, "RECEB_AREA_TEC_ANALISE");
			
			pesquisar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método responsável por marcar todos os checkboxes na grid.
	 */
	public void checkAll() {

		itensSelecionados = new ArrayList<AnaliseTecnicaBemPermanenteVO>();

		if (allChecked) {
			for (AnaliseTecnicaBemPermanenteVO vo : itensRecebimentoList) {
				if (vo.getMatriculaServidor() == null && vo.getSeqArea() == null) {
					vo.setSelecionado(false);
				}
			}
		} else {
			for (AnaliseTecnicaBemPermanenteVO vo : itensRecebimentoList) {
				if (vo.getMatriculaServidor() == null && vo.getSeqArea() == null) {
					vo.setSelecionado(true);
					itensSelecionados.add(vo);
				}
			}
		}

		allChecked = !allChecked;
		allCheckbox = allChecked;
	}

	/**
	 * Método responsável por marcar ou desmarcar um checkbox na grid.
	 * 
	 * @param item - Item selecionado
	 */
	public void checkItem(AnaliseTecnicaBemPermanenteVO item) {

		if (itensSelecionados.contains(item)) {
			itensSelecionados.remove(item);
		} else {
			itensSelecionados.add(item);
		}

		alterarSelecaoNaListaVO();
	}
	
	/**
	 * Método responsável por alterar a lista de itens selecionados na grid.
	 */
	private void alterarSelecaoNaListaVO() {

		for (AnaliseTecnicaBemPermanenteVO vo : itensRecebimentoList) {
			if (vo.getMatriculaServidor() == null) {
				if (itensSelecionados.contains(vo)) {
					vo.setSelecionado(true);
				} else {
					vo.setSelecionado(false);
				}
			}
		}
	}
	
	/**
	 * Método que trunca o valor String de um campo para caber na coluna da grid, conforme tamanho informado.
	 * @param valor - Valor do campo a ser truncado
	 * @param tamanho - tamanho máximo da string na coluna
	 * @return Valor truncado para ser exibido na grid
	 */
	public String reduzirCampoTexto(String valor, int tamanho) {

		if (valor != null && valor.length() >= tamanho) {
			return valor.substring(0, tamanho) + "...";
		}
		
		return valor;
	}

	public String redirecionarSolicitacaoCompra() {
		return SOLICITACAO_COMPRA_CRUD;
	}

	public Integer getNumeroRecebimento() {
		return numeroRecebimento;
	}

	public void setNumeroRecebimento(Integer numeroRecebimento) {
		this.numeroRecebimento = numeroRecebimento;
	}

	public List<AnaliseTecnicaBemPermanenteVO> getItensRecebimentoList() {
		return itensRecebimentoList;
	}

	public void setItensRecebimentoList(List<AnaliseTecnicaBemPermanenteVO> itensRecebimentoList) {
		this.itensRecebimentoList = itensRecebimentoList;
	}

	public PtmAreaTecAvaliacao getAreaTecnicaAvaliacao() {
		return areaTecnicaAvaliacao;
	}

	public void setAreaTecnicaAvaliacao(PtmAreaTecAvaliacao areaTecnicaAvaliacao) {
		this.areaTecnicaAvaliacao = areaTecnicaAvaliacao;
	}

	public List<AnaliseTecnicaBemPermanenteVO> getItensSelecionados() {
		return itensSelecionados;
	}

	public void setItensSelecionados(List<AnaliseTecnicaBemPermanenteVO> itensSelecionados) {
		this.itensSelecionados = itensSelecionados;
	}

	public AnaliseTecnicaBemPermanenteVO getItemMouseOver() {
		return itemMouseOver;
	}

	public void setItemMouseOver(AnaliseTecnicaBemPermanenteVO itemMouseOver) {
		this.itemMouseOver = itemMouseOver;
	}

	public boolean isAllCheckbox() {
		return allCheckbox;
	}

	public void setAllCheckbox(boolean allCheckbox) {
		this.allCheckbox = allCheckbox;
	}

}
