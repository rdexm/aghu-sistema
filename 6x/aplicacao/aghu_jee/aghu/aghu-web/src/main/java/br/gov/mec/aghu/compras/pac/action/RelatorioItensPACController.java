package br.gov.mec.aghu.compras.pac.action;

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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import net.sf.jasperreports.engine.JRException;


public class RelatorioItensPACController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -6872588674993687980L;

	private static final String RELATORIO_ITENS_PAC     = "relatorioItensPAC";
	private static final String RELATORIO_ITENS_PAC_PDF = "relatorioItensPACPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	private Integer numero;	
	
	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IAghuFacade aghuFacade;	

	private List<ItensPACVO> colecao = new ArrayList<ItensPACVO>(0);
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void directPrint()  throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			colecao = this.pacFacade.pesquisarRelatorioItensPAC(numero,true);

			if (colecao.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}

		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Apresenta PDF na tela do navegador.
	 */	
	public String print()throws JRException, IOException, DocumentException {
		try {
			colecao = this.pacFacade.pesquisarRelatorioItensPAC(numero, true);

			if (colecao.isEmpty()) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			
		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
			}

			limparPesquisa();

			return RELATORIO_ITENS_PAC_PDF;

		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}		
	}

	
	@Override
	public Collection<ItensPACVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao;
	}

	
	@Override
	public String recuperarArquivoRelatorio() {	
		return "br/gov/mec/aghu/compras/report/itensPAC.jasper";
	}

	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String hospital = aghuFacade.getRazaoSocial();		
		params.put("hospitalLocal", hospital);
		
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		
		String endereco = aghuFacade.getEndereco();
		params.put("enderecoHospitalLocal", endereco);
		
		String cidade = aghuFacade.getCidade();
		params.put("cidadeHospitalLocal", cidade);
				
		String cgc = aghuFacade.getCgc();
		params.put("cgcHospitalLocal", cgc);
		
		String fax = aghuFacade.getFax();
		params.put("faxHospitalLocal", fax);

		params.put("funcionalidade", "Itens do Processo Administrativo de Compras");
		params.put("nomeRelatorio", "SCOR_ITENS_LIC");

		return params;
	}

	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * @throws DocumentException 
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	
	public void limparPesquisa() {
		numero = null;		
	}
	
	public String voltar(){
		return RELATORIO_ITENS_PAC;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public List<ItensPACVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ItensPACVO> colecao) {
		this.colecao = colecao;
	}

	

}