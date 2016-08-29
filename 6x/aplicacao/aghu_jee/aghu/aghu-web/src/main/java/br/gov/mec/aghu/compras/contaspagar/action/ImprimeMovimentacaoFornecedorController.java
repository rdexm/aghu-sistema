package br.gov.mec.aghu.compras.contaspagar.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.RelatorioMovimentacaoFornecedorVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.ScoContatoFornecedor;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class ImprimeMovimentacaoFornecedorController extends ActionReport
		implements ActionPaginator {

	private static final long serialVersionUID = -3905943195220654004L;

	// Logger
	private static final Log LOG = LogFactory
			.getLog(ImprimeMovimentacaoFornecedorController.class);

	// Constante referente a busca do nome do hospital
	private static final String NOME_HOSPITAL = "P_HOSPITAL_RAZAO_SOCIAL";
	// Constante referente a busca do nome do hospital
	private static final String TITULO_HOSPITAL = "P_AGHU_TITULO_RELATORIO_MOVIMENTACAO_FORNECEDOR";
	// Constante da pagina responsavel pela visualização do relatório
	private static final String PAGE_RELATORIO_MOVIMENTACAO_FORNECEDOR_PDF = "compras-imprimeMovimentacaoFornecedorPdf";
	private static final String PAGE_RELATORIO_MOVIMENTACAO_FORNECEDOR = "br/gov/mec/aghu/compras/contaspagar/imprimeMovimentacaoFornecedor.jasper";
	// Constante da página responsável pela pesquisa
	private static final String PAGE_IMPRIME_MOVIMENTACAO_FORNECEDOR = "compras-imprimeMovimentacaoFornecedor";
	// Constante da página responsável por manter os contatos do fornecedor
	private static final String PAGE_CADASTRAR_CONTATO_FORNECEDOR = "compras-cadastrarContatoFornecedor";

	// Serviço
	@EJB
	private IComprasFacade comprasFacade;
	// Serviço impressão
	@Inject
	private SistemaImpressao sistemaImpressao;

	private Date dataInicio;
	private Date dataFim;
	private VScoFornecedor vscoFornecedor;
	private List<ScoContatoFornecedor> scoContatoFornecedores;
	private List<RelatorioMovimentacaoFornecedorVO> colecao = new ArrayList<RelatorioMovimentacaoFornecedorVO>();
	private ScoContatoFornecedor scoContatoFornecedorSelecionado;
	private Boolean exibirListagem;
	private boolean atualizarPesquisa;

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws ApplicationBusinessException, JRException,
			SystemException, IOException, DocumentException {	
		return "relatorio";
	}

	/**
	 * Método responsável pela impressão.
	 */
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Metódo por gerar as dependências a impressão
	 * 
	 * @return
	 */
	public String imprimirRelatorio() {
		try {
			carregarColecao();
			directPrint();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}

	/**
	 * Método responsável pela visualização do relatório
	 * 
	 * @return página de referência do pdf
	 * @throws BaseException
	 */
	public String visualizarRelatorio() throws BaseException {
		try {
			//pesquisar();
			carregarColecao();
		} catch (Exception e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_RELATORIO_VENCIMENTO_FORN_ERRO");
			return null;
		}
		if (colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_MOVIMENTACAO_FORNECEDOR_DADOS_NAO_ENCONTRADOS");
			return null;
		}
		return PAGE_RELATORIO_MOVIMENTACAO_FORNECEDOR_PDF;
	}

	/**
	 * Método que retorna o modelo do relatório
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return PAGE_RELATORIO_MOVIMENTACAO_FORNECEDOR;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	@Override
	public StreamedContent getRenderPdf() throws IOException,
			JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/**
	 * Método responsável por fornecer os dados do header do relatório
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		params.put("nomeRelatorio", "FCPR_VALID_FORN");
		String hospital = comprasFacade.pesquisarHospital(NOME_HOSPITAL).getVlrTexto();
		String titulo = comprasFacade.pesquisarHospital(TITULO_HOSPITAL).getVlrTexto();
		params.put("hospitalLocal", hospital);
		params.put("tituloRelatorio", titulo);
		
		if (this.vscoFornecedor.getCgcCpf() != null) {
			params.put("cgcCpfFornecedor", this.vscoFornecedor.getCgcCpf());
		}
		
		try{
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}
	
		return params;
	}

	public String verContatos() {
		return PAGE_CADASTRAR_CONTATO_FORNECEDOR;
	}

	/**
	 * Método responsável por limpar a tela de pesquisa
	 */
	public String limpar() {
		this.dataInicio = null;
		this.dataFim = null;
		this.vscoFornecedor = null;
		this.scoContatoFornecedorSelecionado = null;
		this.scoContatoFornecedores = null;
		this.colecao = null;
		this.exibirListagem = false;
		return PAGE_IMPRIME_MOVIMENTACAO_FORNECEDOR;
	}

	public void enviarEmail() throws ApplicationBusinessException,
			IllegalAccessException, InvocationTargetException, JRException,
			IOException, DocumentException {
		
		if(scoContatoFornecedorSelecionado == null || 
				scoContatoFornecedorSelecionado.getEMail() == null){
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MOVIMENTACAO_FORNECEDOR_SELECIONE_CONTATO");
			return;
		}
		
		try {
			carregarColecao();
			DocumentoJasper documento = gerarDocumento();
			comprasFacade.enviarEmailMovimentacaoFornecedor(
					this.scoContatoFornecedorSelecionado.getEMail(),
					documento.getPdfByteArray(false));

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_MOVIMENTACAO_FORNECEDOR_EMAIL_ENVIADO",
					this.vscoFornecedor.getRazaoSocial());
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar enviar e-mail", e);
		}
	}

	/**
	 * Método de Pesquisa é chamado automaticamente após o preenchimento dos
	 * campos Data Inicio, Data Fim e Fornecedor
	 * @throws ApplicationBusinessException 
	 * */
	public void pesquisar() throws ApplicationBusinessException {
		if (this.dataInicio == null || this.dataFim == null
				|| this.vscoFornecedor == null) {
			return;
		}
		this.exibirListagem = true;
		this.setScoContatoFornecedores(this.comprasFacade
				.pesquisarContatoFornecedorPorNumero(this.vscoFornecedor
						.getNumeroFornecedor()));
	}

	public void carregarColecao() throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		this.colecao = comprasFacade.pesquisaMovimentacaoFornecedor(this.vscoFornecedor,this.dataInicio, this.dataFim);
	}

	/**
	 * Método responsável por retornar a página de pesquisa
	 * 
	 * @return página de pesquisa
	 */
	public String voltar() {
		return PAGE_IMPRIME_MOVIMENTACAO_FORNECEDOR;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public ScoContatoFornecedor getScoContatoFornecedorSelecionado() {
		return scoContatoFornecedorSelecionado;
	}

	public void setScoContatoFornecedorSelecionado(
			ScoContatoFornecedor scoContatoFornecedorSelecionado) {
		this.scoContatoFornecedorSelecionado = scoContatoFornecedorSelecionado;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public VScoFornecedor getVscoFornecedor() {
		return vscoFornecedor;
	}

	public void setVscoFornecedor(VScoFornecedor vscoFornecedor) {
		this.vscoFornecedor = vscoFornecedor;
	}

	// SuggestionBox Fornecedor
	public List<VScoFornecedor> pesquisarVFornecedorPorCgcCpfRazaoSocial(
			final String pesquisa) {
		return this.returnSGWithCount(this.comprasFacade
				.pesquisarVFornecedorPorCgcCpfRazaoSocial(pesquisa),pesquisarVFornecedorPorCgcCpfRazaoSocialCount(pesquisa));
	}

	public Long pesquisarVFornecedorPorCgcCpfRazaoSocialCount(
			final String pesquisa) {
		return this.comprasFacade
				.pesquisarVFornecedorPorCgcCpfRazaoSocialCount(pesquisa);
	}

	@Override
	public Long recuperarCount() {
		return this.comprasFacade
				.pesquisarContatoFornecedorPorNumeroCount(this.vscoFornecedor
						.getNumeroFornecedor());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ScoContatoFornecedor> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return this.comprasFacade
				.pesquisarContatoFornecedorPorNumero(this.vscoFornecedor
						.getNumeroFornecedor());
	}

	@Override
	public Collection<RelatorioMovimentacaoFornecedorVO> recuperarColecao()
			throws ApplicationBusinessException {
		return getColecao();
	}

	public List<RelatorioMovimentacaoFornecedorVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioMovimentacaoFornecedorVO> colecao) {
		this.colecao = colecao;
	}

	public String getVoltar() {
		return PAGE_IMPRIME_MOVIMENTACAO_FORNECEDOR;
	}
	public List<ScoContatoFornecedor> getScoContatoFornecedores() {
		return scoContatoFornecedores;
	}

	public void setScoContatoFornecedores(
			List<ScoContatoFornecedor> scoContatoFornecedores) {
		this.scoContatoFornecedores = scoContatoFornecedores;
	}

	public Boolean getExibirListagem() {
		return exibirListagem;
	}

	public void setExibirListagem(Boolean exibirListagem) {
		this.exibirListagem = exibirListagem;
	}

	public boolean isAtualizarPesquisa() {
		return atualizarPesquisa;
	}

	public void setAtualizarPesquisa(boolean atualizarPesquisa) {
		this.atualizarPesquisa = atualizarPesquisa;
	}
}
