package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoEntrada;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoFiscalEntrada;
import br.gov.mec.aghu.estoque.action.GerarNotaRecebimentoSemAfController;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.SceDocumentoFiscalEntradaVO;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.ScoFornecedor;

public class ManterDocumentoFiscalEntradaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 8919020356375252492L;

	private static final String PAGE_ESTOQUE_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA = "estoque-pesquisarDocumentoFiscalEntrada";

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private GerarNotaRecebimentoSemAfController gerarNotaRecebimentoSemAfController;
	
	// Parâmetros da conversação
	private Integer seq;
	private String voltarPara;
	private boolean emEdicao;

	// Parâmetros para integração com a estória #6558 - Gerar Nota de
	// recebimento
	private Integer numeroFornecedor;
	private boolean origemGerarNotaRecebimento;

	// Variaveis que representam os campos do XHTML
	private SceDocumentoFiscalEntradaVO documentoFiscalEntradaVO;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 


		this.documentoFiscalEntradaVO = new SceDocumentoFiscalEntradaVO();

		if (this.emEdicao) {

			final SceDocumentoFiscalEntrada documentoFiscalEntrada = this.estoqueFacade.obterDocumentoFiscalEntradaPorSeq(this.seq);

			this.documentoFiscalEntradaVO.setSeq(documentoFiscalEntrada.getSeq());
			this.documentoFiscalEntradaVO.setNumero(documentoFiscalEntrada.getNumero());
			this.documentoFiscalEntradaVO.setSerie(documentoFiscalEntrada.getSerie() != null ? documentoFiscalEntrada.getSerie().trim() : null);
			this.documentoFiscalEntradaVO.setTipo(documentoFiscalEntrada.getTipo());
			this.documentoFiscalEntradaVO.setTipoDocumentoFiscalEntrada(documentoFiscalEntrada.getTipoDocumentoFiscalEntrada());

			this.documentoFiscalEntradaVO.setDtGeracao(documentoFiscalEntrada.getDtGeracao());
			this.documentoFiscalEntradaVO.setDtEmissao(documentoFiscalEntrada.getDtEmissao());
			this.documentoFiscalEntradaVO.setDtEntrada(documentoFiscalEntrada.getDtEntrada());
			this.documentoFiscalEntradaVO.setDtAutorizada(documentoFiscalEntrada.getDtAutorizada());

			this.documentoFiscalEntradaVO.setFornecedor(documentoFiscalEntrada.getFornecedor());
			this.documentoFiscalEntradaVO.setValorTotalNf(documentoFiscalEntrada.getValorTotalNf());

		} else {

			this.documentoFiscalEntradaVO = new SceDocumentoFiscalEntradaVO();
			this.documentoFiscalEntradaVO.setTipo(DominioTipoDocumentoEntrada.NF);
			this.documentoFiscalEntradaVO.setTipoDocumentoFiscalEntrada(DominioTipoDocumentoFiscalEntrada.NFS);
			this.documentoFiscalEntradaVO.setSerie("U");

			/*
			 * Integração com a estória #6558 - Gerar Nota de recebimento Quanto
			 * o número do fornecedor é informado por parâmetro o fornecedor é
			 * definido pela tela de gerar nota de recebimento
			 */
			if (this.numeroFornecedor != null) {
				ScoFornecedor fornecedor = this.comprasFacade.obterFornecedorPorNumero(this.numeroFornecedor);
				this.documentoFiscalEntradaVO.setFornecedor(fornecedor);
			}

		}
	
	}

	/**
	 * Confirma a operacao de gravar/alterar um material
	 * 
	 * @return
	 */
	public String confirmar() {

		try {

			// Instancia ScoMaterial de acordo com o tipo de operação:
			// Inclusão/Edição
			SceDocumentoFiscalEntrada documentoFiscalEntrada = null;
			if (this.emEdicao) {
				documentoFiscalEntrada = this.estoqueFacade.obterDocumentoFiscalEntradaPorSeq(this.seq);

				if (documentoFiscalEntrada == null) {
					apresentarMsgNegocio(Severity.INFO, "OPTIMISTIC_LOCK");
					return this.voltar();
				}

			} else {
				documentoFiscalEntrada = new SceDocumentoFiscalEntrada();
			}

			documentoFiscalEntrada.setSeq(this.documentoFiscalEntradaVO.getSeq());
			documentoFiscalEntrada.setNumero(this.documentoFiscalEntradaVO.getNumero());
			documentoFiscalEntrada.setSerie(this.documentoFiscalEntradaVO.getSerie());
			documentoFiscalEntrada.setTipo(this.documentoFiscalEntradaVO.getTipo());
			documentoFiscalEntrada.setTipoDocumentoFiscalEntrada(this.documentoFiscalEntradaVO.getTipoDocumentoFiscalEntrada());

			documentoFiscalEntrada.setDtGeracao(this.documentoFiscalEntradaVO.getDtGeracao());
			documentoFiscalEntrada.setDtEmissao(this.documentoFiscalEntradaVO.getDtEmissao());
			documentoFiscalEntrada.setDtEntrada(this.documentoFiscalEntradaVO.getDtEntrada());
			documentoFiscalEntrada.setDtAutorizada(this.documentoFiscalEntradaVO.getDtAutorizada());

			documentoFiscalEntrada.setFornecedor(this.documentoFiscalEntradaVO.getFornecedor());
			documentoFiscalEntrada.setValorTotalNf(this.documentoFiscalEntradaVO.getValorTotalNf());

			// Persiste documento fiscal de entrada
			this.estoqueBeanFacade.persistirDocumentoFiscalEntrada(documentoFiscalEntrada);

			// Determina o tipo de mensagem de confirmação através do tipo de
			// operação
			String mensagem = null;
			if (this.emEdicao) {
				mensagem = "MENSAGEM_SUCESSO_ALTERAR_MANTER_DOCUMENTO_FISCAL_ENTRADA";
			} else {
				mensagem = "MENSAGEM_SUCESSO_INSERIR_MANTER_DOCUMENTO_FISCAL_ENTRADA";
			}
			apresentarMsgNegocio(Severity.INFO, mensagem, documentoFiscalEntrada.getNumero());

			// Entra AUTOMATICAMENTE em modo de edição após um inclusão!
			if (!this.emEdicao) {
				this.seq = documentoFiscalEntrada.getSeq();
				this.documentoFiscalEntradaVO.setSeq(documentoFiscalEntrada.getSeq());
				this.emEdicao = true;
				this.gerarNotaRecebimentoSemAfController.setSeqDocumentoFiscalEntrada(this.seq);
				this.gerarNotaRecebimentoSemAfController.setNumeroFornecedorDocumentoFiscalEntrada(documentoFiscalEntradaVO.getFornecedor().getNumero());
				return "estoque-gerarNotaRecebimentoSemAf";

			}

			this.inicio();

			if (this.voltarPara == null) {
				this.limpar();
				return PAGE_ESTOQUE_PESQUISAR_DOCUMENTO_FISCAL_ENTRADA;
			} else {
				return this.voltar();
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		final String voltarPara = this.voltarPara;
		this.limpar();
		return voltarPara;
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
		return comprasFacade.obterFornecedor(param);
	}

	/**
	 * Quando um documento fiscal de entrada é selecionado através da tela
	 * "gerar nota de recebimento", a alteração do fornecedor deve advertir o
	 * usuário quando o mesmo for diferente do fornecedor original da
	 * autorização de fornecimento.
	 */
	public void validarFornecedorGerarNotaRecebimento() {
		if (Boolean.TRUE.equals(this.origemGerarNotaRecebimento)) {
			final ScoFornecedor fornecedor = documentoFiscalEntradaVO.getFornecedor();
			if (fornecedor != null && !fornecedor.getNumero().equals(this.numeroFornecedor)) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_FORNECEDOR_ORIGINAL_GERAR_NR_MODIFICADO");
			}
		}
	}

	private void limpar() {
		this.seq = null;
		this.voltarPara = null;
		this.emEdicao = false;
		this.numeroFornecedor = null;
		this.origemGerarNotaRecebimento = false;
		this.documentoFiscalEntradaVO = new SceDocumentoFiscalEntradaVO();
	}

	/*
	 * Getters e setters
	 */

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public SceDocumentoFiscalEntradaVO getDocumentoFiscalEntradaVO() {
		return documentoFiscalEntradaVO;
	}

	public void setDocumentoFiscalEntradaVO(SceDocumentoFiscalEntradaVO documentoFiscalEntradaVO) {
		this.documentoFiscalEntradaVO = documentoFiscalEntradaVO;
	}

	public boolean isEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
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

}