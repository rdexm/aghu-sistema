package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoParaSerRealizadoVO;
import br.gov.mec.aghu.patrimonio.vo.DetalhamentoRetiradaBemPermanenteVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Classe responsável pelo controle dos componentes, ações e valores da tela Regristar Retirada de Bem Permanente para Aceite Técnico.
 *
 */
public class RetiradaBemPermanenteController extends ActionController {

	private static final long serialVersionUID = 4883199949775436726L;

	private static final String PAGINA_RETIRADA_BEM_PERMANENTE = "patrimonio-retiradaBemPermanente";
	
	private static final String PAGINA_LISTAR_ACEITES_TECNICOS = "patrimonio-listarAceitesTecnicos";

	@Inject
	private ListaAceiteTecnicoPaginatorController listaAceiteTecnicoController;
	
	@Inject
	private RelatorioRetiradaBemPermanenteController relatorioRetiradaBemPermanenteController;

	@EJB
	private IPatrimonioFacade patrimonioFacade;

	private List<AceiteTecnicoParaSerRealizadoVO> itensRetiradaList = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();

	private List<AceiteTecnicoParaSerRealizadoVO> itensSelecionados = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();

	private List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoList = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
	
	private List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoListCompleta = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();

	private AceiteTecnicoParaSerRealizadoVO bensMouseOver = new AceiteTecnicoParaSerRealizadoVO();
	
	private DetalhamentoRetiradaBemPermanenteVO detalheMouseOver = new DetalhamentoRetiradaBemPermanenteVO();

	private Boolean detalhamento;
	
	private String paginaRetorno;

	@PostConstruct
	protected void inicializar() {

		this.begin(conversation);
	}

	/**
	 * Método responsável por inicializar a tela.
	 * 
	 * @return caminho para a tela de Retirada de Bem Permanente.
	 */
	public String iniciarRetirada() {

		itensSelecionados = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		itensRetiradaList = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		itensDetalhamentoList = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
		itensDetalhamentoListCompleta = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
		detalhamento = Boolean.FALSE;

		if (listaAceiteTecnicoController != null && listaAceiteTecnicoController.getListaAceiteTecnicoSelecionadosVO() != null) {
			for (AceiteTecnicoParaSerRealizadoVO item : listaAceiteTecnicoController.getListaAceiteTecnicoSelecionadosVO()) {
				itensRetiradaList.add(item);
			}
		}

		try {
			patrimonioFacade.validarInicializacaoRetiradaBemPermanente(itensRetiradaList);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGINA_RETIRADA_BEM_PERMANENTE;
	}

	/**
	 * Método responsável por inicializar a grid de detalhamento.
	 */
	public void detalhar() {

		itensDetalhamentoList = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
		itensDetalhamentoListCompleta = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();

		try {
			patrimonioFacade.validarInicializacaoDetalharRetiradaBemPermanente(itensRetiradaList);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return;
		}

		for (AceiteTecnicoParaSerRealizadoVO item : itensRetiradaList) {
			for (long i = item.getQuantidadeRetirada(); i > 0; i--) {
				DetalhamentoRetiradaBemPermanenteVO detalhe = new DetalhamentoRetiradaBemPermanenteVO();

				detalhe.setAceiteVO(item);
				detalhe.setCodigo(item.getCodigo());
				detalhe.setEsl(item.getEsl());
				detalhe.setItemRecebimento(item.getItemRecebimento());
				detalhe.setMaterial(item.getMaterial());
				detalhe.setRecebimento(item.getRecebimento());
				
				itensDetalhamentoListCompleta.add(detalhe);
				if (item.isInst()) {
					itensDetalhamentoList.add(detalhe);
				}
			}
		}

		detalhamento = Boolean.TRUE;
	}

	/**
	 * Método responsável por desfazer o detalhamento.
	 */
	public void desfazer() {

		itensDetalhamentoList = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
		itensDetalhamentoListCompleta = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
		itensSelecionados = new ArrayList<AceiteTecnicoParaSerRealizadoVO>();
		detalhamento = Boolean.FALSE;

		for (AceiteTecnicoParaSerRealizadoVO item : itensRetiradaList) {
			item.setInst(false);
			item.setQuantidadeRetirada(null);
		}
	}

	/**
	 * Método responsável por gravar os dados da Retirada de Bem Permanente.
	 * 
	 * @return caminho para a tela anterior.
	 */
	public String gravar() {

		try {
			patrimonioFacade.validarInicializacaoDetalharRetiradaBemPermanente(itensRetiradaList);

			if (detalhamento == Boolean.FALSE) {
				for (AceiteTecnicoParaSerRealizadoVO item : itensRetiradaList) {
					for (long i = item.getQuantidadeRetirada(); i > 0; i--) {
						DetalhamentoRetiradaBemPermanenteVO detalhe = new DetalhamentoRetiradaBemPermanenteVO();

						detalhe.setAceiteVO(item);
						detalhe.setCodigo(item.getCodigo());
						detalhe.setEsl(item.getEsl());
						detalhe.setItemRecebimento(item.getItemRecebimento());
						detalhe.setMaterial(item.getMaterial());
						detalhe.setRecebimento(item.getRecebimento());
						
						itensDetalhamentoListCompleta.add(detalhe);
					}
				}
			}

			patrimonioFacade.registrarRetiradaBemPermanente(itensDetalhamentoListCompleta);
			relatorioRetiradaBemPermanenteController.setItensDetalhamentoListCompleta(itensDetalhamentoListCompleta);
			relatorioRetiradaBemPermanenteController.setItemRetiradaList(itensRetiradaList);
			relatorioRetiradaBemPermanenteController.imprimir();
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_RETIRADA");
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGINA_LISTAR_ACEITES_TECNICOS;
	}

	/**
	 * Método responsável por marcar ou desmarcar um checkbox na grid.
	 * 
	 * @param item - Item selecionado
	 */
	public void checkItem(AceiteTecnicoParaSerRealizadoVO item) {

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

		for (AceiteTecnicoParaSerRealizadoVO vo : itensRetiradaList) {
			if (itensSelecionados.contains(vo)) {
				vo.setInst(true);
			} else {
				vo.setInst(false);
			}
		}
	}
	
	/**
	 * Método que trunca o valor String de um campo para caber na coluna da grid, conforme tamanho informado.
	 * 
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
	
	public String voltar() {
		if (paginaRetorno == null) {
			return PAGINA_LISTAR_ACEITES_TECNICOS;
		} else {
			String retorno = paginaRetorno;
			paginaRetorno = null;
			return retorno;
		}
	}

	public List<AceiteTecnicoParaSerRealizadoVO> getItensSelecionados() {
		return itensSelecionados;
	}

	public void setItensSelecionados(List<AceiteTecnicoParaSerRealizadoVO> itensSelecionados) {
		this.itensSelecionados = itensSelecionados;
	}

	public List<AceiteTecnicoParaSerRealizadoVO> getItensRetiradaList() {
		return itensRetiradaList;
	}

	public void setItensRetiradaList(List<AceiteTecnicoParaSerRealizadoVO> itensRetiradaList) {
		this.itensRetiradaList = itensRetiradaList;
	}

	public AceiteTecnicoParaSerRealizadoVO getBensMouseOver() {
		return bensMouseOver;
	}

	public void setBensMouseOver(AceiteTecnicoParaSerRealizadoVO bensMouseOver) {
		this.bensMouseOver = bensMouseOver;
	}

	public List<DetalhamentoRetiradaBemPermanenteVO> getItensDetalhamentoList() {
		return itensDetalhamentoList;
	}

	public void setItensDetalhamentoList(
			List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoList) {
		this.itensDetalhamentoList = itensDetalhamentoList;
	}

	public Boolean getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(Boolean detalhamento) {
		this.detalhamento = detalhamento;
	}

	public DetalhamentoRetiradaBemPermanenteVO getDetalheMouseOver() {
		return detalheMouseOver;
	}

	public void setDetalheMouseOver(
			DetalhamentoRetiradaBemPermanenteVO detalheMouseOver) {
		this.detalheMouseOver = detalheMouseOver;
	}

	public String getPaginaRetorno() {
		return paginaRetorno;
	}

	public void setPaginaRetorno(String paginaRetorno) {
		this.paginaRetorno = paginaRetorno;
	}

	public List<DetalhamentoRetiradaBemPermanenteVO> getItensDetalhamentoListCompleta() {
		return itensDetalhamentoListCompleta;
	}

	public void setItensDetalhamentoListCompleta(
			List<DetalhamentoRetiradaBemPermanenteVO> itensDetalhamentoListCompleta) {
		this.itensDetalhamentoListCompleta = itensDetalhamentoListCompleta;
	}

}
