package br.gov.mec.aghu.compras.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.SolicitacaoDeComprasVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class ImprimirSolicitacaoDeComprasController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(ImprimirSolicitacaoDeComprasController.class);
	
	private static final long serialVersionUID = 3099300600546744615L;
	
	private static final String RELATORIO_SOLICITACAO_COMPRAS_PDF = "compras-imprimirSolicitacaoDeComprasPdfCadastro";

	@Inject
	private SistemaImpressao sistemaImpressao;
	 
	@EJB
	private IComprasFacade comprasFacade;
	
	private List<Integer> numSolicComps;
	
	@EJB
	protected IParametroFacade parametroFacade;
	
	
	/*	Dados que serão impressos em PDF. */
	private List<SolicitacaoDeComprasVO> colecao = new ArrayList<SolicitacaoDeComprasVO>();
	
	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public String voltar() {
		return this.voltarParaUrl;
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public String print(Integer numSolicitacao) throws BaseException, JRException, SystemException, IOException, DocumentException {

		if (numSolicComps == null ) {
			numSolicComps = new ArrayList<Integer>();
		}
		
		try{
			if (numSolicitacao!=null) {
				getNumSolicComps().clear();
				getNumSolicComps().add(numSolicitacao); 
			}
			
			colecao = this.comprasFacade.listarSolicitacoesDeComprasPorNumero(getNumSolicComps());
			
									
			if (colecao.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			
		
		
		return null;
			}
			DocumentoJasper documento = gerarDocumento();
			
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
			return RELATORIO_SOLICITACAO_COMPRAS_PDF;

		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws JRException
	 */
	public void directPrint() {

		try {
			colecao = this.comprasFacade.listarSolicitacoesDeComprasPorNumero(getNumSolicComps());
			
			if (colecao.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy");
		String hospital;
		try {
			hospital = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL).getVlrTexto();
		
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeHospital", hospital);
		params.put("nomeRelatorio", "SCOR_SC_POR_NRO");
		params.put("tituloRelatorio", "Solicitação de Compra");
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		params.put("logoAnexo", servletContext.getRealPath("/images/anexo-impressao.png"));

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
		}
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return	"br/gov/mec/aghu/compras/report/relatorioSolicitacaoDeCompras.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws DocumentException 
	 */
	
	@Override
	public Collection<SolicitacaoDeComprasVO> recuperarColecao() {
		return colecao;
		
	}
	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException, ApplicationBusinessException {
		
		DocumentoJasper documento = gerarDocumento();
		
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	
	/*	Getters and Setters */
	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

		
	public List<SolicitacaoDeComprasVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<SolicitacaoDeComprasVO> colecao) {
		this.colecao = colecao;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public List<Integer> getNumSolicComps() {
		return numSolicComps;
	}

	public void setNumSolicComps(List<Integer> numSolicComps) {
		this.numSolicComps = numSolicComps;
	}

	
}