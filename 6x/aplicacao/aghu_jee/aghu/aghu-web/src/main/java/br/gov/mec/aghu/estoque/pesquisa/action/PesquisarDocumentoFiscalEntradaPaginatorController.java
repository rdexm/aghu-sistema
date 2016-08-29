package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoEntrada;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoFiscalEntrada;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.PesquisaDocumentoFiscalEntradaVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisarDocumentoFiscalEntradaPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<PesquisaDocumentoFiscalEntradaVO> dataModel;

	private PesquisaDocumentoFiscalEntradaVO parametroSelecionado;

	// private static final String PAGE_ESTOQUE_GERAR_NOTA_RECEBIMENTO =
	// "estoque-gerarNotaRecebimento";
	//	private static final String PAGE_ESTOQUE_GERAR_NOTA_RECEBIMENTO_SEM_AF = 
	// "estoque-gerarNotaRecebimentoSemAf";
	private static final String PAGE_ESTOQUE_MANTER_DOCUMENTO_FISCAL_ENTRADA_CRUD = "estoque-manterDocumentoFiscalEntradaCRUD";
	// Atenção nesta navegação, pois é utilizada em 2 locais:
	// origemRecebimentoMaterialServico e receberMaterialServico
	private static final String PAGE_COMPRAS_RECEBER_MATERIAL_SERVICO = "compras-receberMaterialServico";
	private static final String PAGE_ESTOQUE_CONFIRMACAO_RECEBIMENTO = "estoque-confirmacaoRecebimento";
	
	private static final String PAGE_COMPRAS_RECEBER_MATERIAL_SERVICO_SEM_AF = "compras-receberMaterialServicoSemAF";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2045794245553047684L;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@EJB
	private IComprasFacade comprasFacade;

	// Campos de filtro para pesquisa
	private Integer numeroInterno;
	private Long numeroDocumento;
	private String serie;
	private DominioTipoDocumentoEntrada tipo;
	private DominioTipoDocumentoFiscalEntrada tipoDocumentoFiscalEntrada;
	private Date dataGeracao;
	private Date dataVencimento;
	private ScoFornecedor fornecedor;
	private String voltarPara;

	// Parâmetros para integração com a estória #6558 - Gerar Nota de
	// recebimento
	private Integer numeroFornecedor;
	private boolean origemGerarNotaRecebimento;
	private boolean origemGerarNotaRecebimentoSemAf;

	private boolean origemConfirmacaoRecebimento;
	private boolean origemRecebimentoMaterialServico;

	private boolean ativo;

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 


		/*
		 * Integração com a estória #6558 - Gerar Nota de recebimento Quanto o
		 * número do Fornecedor é informado por parâmetro o Fornecedor é
		 * definido pela tela de gerar nota de recebimento, já tipo de Documento
		 * Fiscal de Entrada recebe o valor padrão NFS
		 */
		if (this.numeroFornecedor != null && !this.ativo) {
			this.fornecedor = this.comprasFacade.obterFornecedorPorNumero(this.numeroFornecedor);
			this.tipoDocumentoFiscalEntrada = DominioTipoDocumentoFiscalEntrada.NFS;
			this.pesquisar();
		}

		/*
		 * Integração com a estória #16137 - Gerar Nota de Recebimento sem AF
		 * Tipo de Documento Fiscal de Entrada recebe o valor padrão NFS
		 */
		if ((isOrigemRecebimentoMaterialServico() || isOrigemConfirmacaoRecebimento() || this.origemGerarNotaRecebimentoSemAf) && !this.ativo) {
			this.pesquisar();
		}

		this.ativo = true;

	
	}

	/**
	 * Pesquisa principal e paginada
	 */

	/**
	 * Recupera uma instancia com os filtros de pesquisa atualizados
	 * 
	 * @return
	 */
	private SceDocumentoFiscalEntrada getElementoFiltroPesquisa() {

		final SceDocumentoFiscalEntrada elementoFiltroPesquisa = new SceDocumentoFiscalEntrada();

		// Popula filtro de pesquisa
		elementoFiltroPesquisa.setSeq(this.numeroInterno);
		elementoFiltroPesquisa.setNumero(this.numeroDocumento);
		elementoFiltroPesquisa.setSerie(this.serie);
		elementoFiltroPesquisa.setTipo(this.tipo);
		elementoFiltroPesquisa.setTipoDocumentoFiscalEntrada(this.tipoDocumentoFiscalEntrada);
		elementoFiltroPesquisa.setDtGeracao(this.dataGeracao);
		elementoFiltroPesquisa.setDtAutorizada(this.dataVencimento);
		elementoFiltroPesquisa.setFornecedor(this.fornecedor);

		return elementoFiltroPesquisa;
	}

	/**
	 * Pesquisa principal
	 */
	public void pesquisar() {
		// Reinicia paginator e realiza a pesquisa principal
		this.dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.pesquisarDocumentoFiscalEntradaCount(this.getElementoFiltroPesquisa());
	}

	@Override
	public List<PesquisaDocumentoFiscalEntradaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<SceDocumentoFiscalEntrada> listaDocumentoFiscalEntrada = this.estoqueFacade.pesquisarDocumentoFiscalEntrada(firstResult, maxResult, orderProperty, asc, this.getElementoFiltroPesquisa());
		List<PesquisaDocumentoFiscalEntradaVO> resultados = new LinkedList<PesquisaDocumentoFiscalEntradaVO>();

		if (listaDocumentoFiscalEntrada != null && !listaDocumentoFiscalEntrada.isEmpty()) {

			for (SceDocumentoFiscalEntrada documentoFiscalEntrada : listaDocumentoFiscalEntrada) {

				PesquisaDocumentoFiscalEntradaVO vo = new PesquisaDocumentoFiscalEntradaVO();

				vo.setSeq(documentoFiscalEntrada.getSeq());
				vo.setNumero(documentoFiscalEntrada.getNumero());
				vo.setSerie(documentoFiscalEntrada.getSerie());
				vo.setTipo(documentoFiscalEntrada.getTipo());
				vo.setTipoDocumentoFiscalEntrada(documentoFiscalEntrada.getTipoDocumentoFiscalEntrada());
				vo.setDtGeracao(documentoFiscalEntrada.getDtGeracao());
				vo.setDtEmissao(documentoFiscalEntrada.getDtEmissao());
				vo.setDtEntrada(documentoFiscalEntrada.getDtEntrada());
				vo.setDtAutorizada(documentoFiscalEntrada.getDtAutorizada());
				vo.setCpfCnpfFornecedor(this.comprasFacade.obterCnpjCpfFornecedorFormatado(documentoFiscalEntrada.getFornecedor()));
				vo.setFornecedor(documentoFiscalEntrada.getFornecedor());
				vo.setFornecedorEventual(documentoFiscalEntrada.getFornecedorEventual());
				vo.setValorTotalNf(documentoFiscalEntrada.getValorTotalNf());

				resultados.add(vo);

			}
		}
		return resultados;
	}

	public String inserirEditar() {
		if (verificarAlteradoOutroUsuario()) {
			return null;
		}
		return PAGE_ESTOQUE_MANTER_DOCUMENTO_FISCAL_ENTRADA_CRUD;
	}

	/**
	 * Exclui um item selecionado na listagem da consulta atraves de uma modal
	 * de exclusao
	 */
	public void excluir() {
		try {

			if (verificarAlteradoOutroUsuario()) {
				return;
			}

			this.estoqueBeanFacade.removerDocumentoFiscalEntrada(this.parametroSelecionado.getSeq());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_MANTER_DOCUMENTO_FISCAL_ENTRADA", this.parametroSelecionado.getSeq());

			this.dataModel.reiniciarPaginator();
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.parametroSelecionado = null;
		}
	}

	private boolean verificarAlteradoOutroUsuario() {
		if (this.parametroSelecionado != null && this.estoqueFacade.obterDocumentoFiscalEntradaPorSeq(this.parametroSelecionado.getSeq()) == null) {
			apresentarMsgNegocio(Severity.INFO, "REGISTRO_NULO_EXCLUSAO");
			this.parametroSelecionado = null;
			this.pesquisar();
			return true;
		}
		return false;
	}

	/**
	 * Limpa os filtros da pesquisa principal
	 */
	public void limparPesquisa() {
		this.numeroInterno = null;
		this.numeroDocumento = null;
		this.serie = null;
		this.tipo = null;
		this.tipoDocumentoFiscalEntrada = null;
		this.dataGeracao = null;
		this.dataVencimento = null;
		this.fornecedor = null;
		this.getDataModel().limparPesquisa();
		this.ativo = true;
	}

	/**
	 * Pesquisas para suggestion box
	 */

	/**
	 * Obtem lista para sugestion box de fornecedores
	 * 
	 * @param param
	 * @return
	 */
	public List<ScoFornecedor> obterFornecedores(String param) {
		return comprasFacade.listarFornecedoresAtivos(param, 0, 100, null, false);
	}

	/**
	 * Quando um documento fiscal de entrada é selecionado através da tela
	 * "gerar nota de recebimento", a alteração do fornecedor deve advertir o
	 * usuário quando o mesmo for diferente do fornecedor original da
	 * autorização de fornecimento.
	 */
	public void validarFornecedorGerarNotaRecebimento() {
		if (Boolean.TRUE.equals(this.origemGerarNotaRecebimento) || Boolean.TRUE.equals(this.origemGerarNotaRecebimentoSemAf)) {
			if (this.fornecedor != null && !this.fornecedor.getNumero().equals(this.numeroFornecedor)) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_FORNECEDOR_ORIGINAL_GERAR_NR_MODIFICADO");
			}
		}
	}

	public String verificarValorNF(Integer seqNota, Double valorTotalNf) {
		if (this.voltarPara.equalsIgnoreCase("estoque-confirmacaoRecebimento")) {
			if (valorTotalNf == null || valorTotalNf == 0) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_ASSOCIAR_NF_RECEBIMENTO");
				return "";
			}
			
			this.numeroFornecedor = null;
			limparPesquisa();

			return PAGE_ESTOQUE_CONFIRMACAO_RECEBIMENTO;
		} else if (this.voltarPara.equalsIgnoreCase("compras-receberMaterialServicoSemAF")){
			return PAGE_COMPRAS_RECEBER_MATERIAL_SERVICO_SEM_AF; 
		}
		
		return PAGE_COMPRAS_RECEBER_MATERIAL_SERVICO;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		final String retorno = this.voltarPara;
		limparParametros();
		
		if (this.voltarPara.equalsIgnoreCase("compras-receberMaterialServicoSemAF")) {
			return PAGE_COMPRAS_RECEBER_MATERIAL_SERVICO_SEM_AF; 
		}

		this.numeroFornecedor = null;
		limparPesquisa();

		return retorno;
	}

	/**
	 * Limpa parâmetros de conversação
	 */
	private void limparParametros() {
		this.numeroFornecedor = null;
		this.origemGerarNotaRecebimento = false;
		this.origemGerarNotaRecebimentoSemAf = false;
		this.origemConfirmacaoRecebimento = false;
		this.origemRecebimentoMaterialServico = false;
		this.numeroDocumento = null;
		this.serie = null;
	}

	public String obterFornecedorDescricao(ScoFornecedor fornecedor) {
		if (fornecedor != null) {
			return this.comprasFacade.obterFornecedorDescricao(fornecedor.getNumero());
		}
		return "";
	}

	/*
	 * Getters e setters
	 */

	public Integer getNumeroInterno() {
		return numeroInterno;
	}

	public void setNumeroInterno(Integer numeroInterno) {
		this.numeroInterno = numeroInterno;
	}

	public Long getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(Long numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public String getSerie() {
		return serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	public DominioTipoDocumentoEntrada getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoDocumentoEntrada tipo) {
		this.tipo = tipo;
	}

	public DominioTipoDocumentoFiscalEntrada getTipoDocumentoFiscalEntrada() {
		return tipoDocumentoFiscalEntrada;
	}

	public void setTipoDocumentoFiscalEntrada(DominioTipoDocumentoFiscalEntrada tipoDocumentoFiscalEntrada) {
		this.tipoDocumentoFiscalEntrada = tipoDocumentoFiscalEntrada;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Date getDataVencimento() {
		return dataVencimento;
	}

	public void setDataVencimento(Date dataVencimento) {
		this.dataVencimento = dataVencimento;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public boolean isOrigemGerarNotaRecebimento() {
		return origemGerarNotaRecebimento;
	}

	public void setOrigemGerarNotaRecebimento(boolean origemGerarNotaRecebimento) {
		this.origemGerarNotaRecebimento = origemGerarNotaRecebimento;
	}

	public boolean isOrigemGerarNotaRecebimentoSemAf() {
		return origemGerarNotaRecebimentoSemAf;
	}

	public void setOrigemGerarNotaRecebimentoSemAf(boolean origemGerarNotaRecebimentoSemAf) {
		this.origemGerarNotaRecebimentoSemAf = origemGerarNotaRecebimentoSemAf;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setOrigemConfirmacaoRecebimento(boolean origemConfirmacaoRecebimento) {
		this.origemConfirmacaoRecebimento = origemConfirmacaoRecebimento;
	}

	public boolean isOrigemConfirmacaoRecebimento() {
		return origemConfirmacaoRecebimento;
	}

	public boolean isOrigemRecebimentoMaterialServico() {
		return origemRecebimentoMaterialServico;
	}

	public void setOrigemRecebimentoMaterialServico(boolean origemRecebimentoMaterialServico) {
		this.origemRecebimentoMaterialServico = origemRecebimentoMaterialServico;
	}

	public DynamicDataModel<PesquisaDocumentoFiscalEntradaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PesquisaDocumentoFiscalEntradaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public PesquisaDocumentoFiscalEntradaVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(PesquisaDocumentoFiscalEntradaVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public boolean isAtivo() {
		return ativo;
	}
}