package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
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
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoConsultoria;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultoriasInternacaoVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioListaConsultoriaController extends ActionReport {

	private static final long serialVersionUID = -3972627718923467879L;

	private static final Log LOG = LogFactory.getLog(RelatorioListaConsultoriaController.class);
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private List<ConsultoriasInternacaoVO> colecao;
	
	private DominioSituacaoConsultoria situacaoFiltro;
	
	private static final String LISTA_CONSULTORIAS = "prescricaomedica-pesquisarConsultoriasInternacao";
	
	//Constante referente ao nome do report
	private static final String NOME_REPORT = "MPMR_LISTA_CONSULTOR";
	//Constante referente ao título do relatório
	private static final String TITULO_RELATORIO = "Lista de Consultorias";

	private static final Object EXCECAO_CAPTURADA = "Exceção capturada: ";
		
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		
		String hospital = this.parametroFacade.obterAghParametroPorNome(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL.toString()).getVlrTexto();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		params.put("nomeReport", NOME_REPORT);
		params.put("tituloRelatorio", TITULO_RELATORIO);
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/prescricaomedica/report/");

		return params;
	}
	
	 /**
     * Método invocado pelo p:media para geração de PDF dentro de XHTML.
     */
    public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException {
		try {
			DocumentoJasper documento = gerarDocumento();
			return this.criarStreamedContentPdf(documento.getPdfByteArray(true));

		} catch (ApplicationBusinessException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(e);
			return null;
		}
    }
	
	public void directPrint() {

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public void downloadPdf() {
		try {
			DocumentoJasper documento = gerarDocumento();
			super.download(documento.getPdfByteArray(false), NOME_REPORT+".pdf", "text/pdf");
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch(ApplicationBusinessException e) { 
			apresentarExcecaoNegocio(e);
		} catch(Exception e) {
			LOG.error(EXCECAO_CAPTURADA, e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<ConsultoriasInternacaoVO> recuperarColecao() throws ApplicationBusinessException {
		return this.prescricaoMedicaFacade.formatarColecaoRelatorioConsultorias(colecao, situacaoFiltro);
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/prescricaomedica/report/relatorioListaConsultorias.jasper";
	}
	
	public String voltar() {
		return LISTA_CONSULTORIAS;
	}

	public List<ConsultoriasInternacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<ConsultoriasInternacaoVO> colecao) {
		this.colecao = colecao;
	}

	public DominioSituacaoConsultoria getSituacaoFiltro() {
		return situacaoFiltro;
	}

	public void setSituacaoFiltro(
			DominioSituacaoConsultoria situacaoFiltro) {
		this.situacaoFiltro = situacaoFiltro;
	}
}
