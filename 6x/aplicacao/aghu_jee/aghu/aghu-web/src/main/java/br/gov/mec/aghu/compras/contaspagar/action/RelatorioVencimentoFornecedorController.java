package br.gov.mec.aghu.compras.contaspagar.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.contaspagar.vo.DatasVencimentosFornecedorVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioVencimentoFornecedorController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -3905943195220654004L;
	
	//Logger
	private static final Log LOG = LogFactory.getLog(RelatorioVencimentoFornecedorController.class);

	//Constante da pagina responsavel pela visualização do relatório
	private static final String PAGE_RELATORIO_DATA_VENCIMENTO_FORNECEDOR_PDF = "relatorioVencimentoFornecedorPdf";
	//Constante da página responsável pela pesquisa
	private static final String PAGE_RELATORIO_DATA_VENCIMENTO_FORNECEDOR = "relatorioVencimentoFornecedor";
	//Constante referente a busca do nome do hospital
	private static final String NOME_HOSPITAL = "P_HOSPITAL_RAZAO_SOCIAL";	
	//Constante referente a busca do nome do hospital
	private static final String TITULO_HOSPITAL = "P_AGHU_TITULO_RELATORIO_DT_VCTO_FORNECEDOR";
	//Serviço
	@EJB
	private IComprasFacade comprasFacade;
	//Serviço impressão
	@Inject
	private SistemaImpressao sistemaImpressao;
	// Representa um fornecedor
	private VScoFornecedor vscoFornecedor;
	
	private String voltarPara;

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}
	
	//Coleção
	private List<DatasVencimentosFornecedorVO> colecao = new ArrayList<DatasVencimentosFornecedorVO>();

	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws ApplicationBusinessException, JRException, SystemException, IOException, DocumentException {
	
		DocumentoJasper documento;
		
		try {
			documento = gerarDocumento(true);
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	 	return "relatorio";
	}

	/**
	 * Método responsável pela impressão.
	 */
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	/**
	 * Metódo por gerar as dependências a impressão
	 * @return
	 */
	public String imprimirRelatorio() {
		try {
			pesquisar();
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
	 * @return página de referência do pdf
	 * @throws BaseException
	 */
	public String visualizarRelatorio() throws BaseException {			
		try {
			pesquisar();
		} catch (Exception e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_VENCIMENTO_FORN_ERRO");
			return null;
		}
		if(colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_VENCIMENTO_FORN_VAZIO");
			return null;
		}		
		return PAGE_RELATORIO_DATA_VENCIMENTO_FORNECEDOR_PDF;
	}
	
	public String visualizarRelatorioJaPreenchido() {
		return PAGE_RELATORIO_DATA_VENCIMENTO_FORNECEDOR_PDF;
	}
	
	/**
	 * Método que retorna o modelo do relatório
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/contaspagar/relatorioVencimentosFornecedores.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	@Override
	 public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException {
		DocumentoJasper documento;
		try {
			documento = gerarDocumento(true);
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
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
		  return params;
	 }
	
	@Override
	public Collection<DatasVencimentosFornecedorVO> recuperarColecao() throws ApplicationBusinessException {
		return getColecao();
	}
	
	/**
	 * Método responsável por limpar a tela de pesquisa
	 */
	public void limpar() {
		vscoFornecedor = null;
		colecao = null;
	}
	
	/**
	 * Método responsável por retornar a página de pesquisa
	 * @return página de pesquisa
	 */
	public String voltar() {
		if (this.voltarPara != null) {
			return this.voltarPara;
		}
		return PAGE_RELATORIO_DATA_VENCIMENTO_FORNECEDOR;
	}

	public List<VScoFornecedor> pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(final String pesquisa) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarVFornecedorPorNumeroCgcCpfRazaoSocial(pesquisa),pesquisarVFornecedorPorNumeroCgcCpfRazaoSocialCount(pesquisa));
	}
	
	public Long pesquisarVFornecedorPorNumeroCgcCpfRazaoSocialCount(final String pesquisa) {
		return this.comprasFacade.pesquisarVFornecedorPorNumeroCgcCpfRazaoSocialCount(pesquisa);
	}
	
	
	public void pesquisar() throws BaseException {
		this.colecao = comprasFacade.pesquisaDatasVencimentoFornecedor(this.vscoFornecedor);
	}
	
	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public List<DatasVencimentosFornecedorVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<DatasVencimentosFornecedorVO> colecao) {
		this.colecao = colecao;
	}


	public VScoFornecedor getVscoFornecedor() {
		return vscoFornecedor;
	}

	public void setVscoFornecedor(VScoFornecedor vscoFornecedor) {
		this.vscoFornecedor = vscoFornecedor;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}
