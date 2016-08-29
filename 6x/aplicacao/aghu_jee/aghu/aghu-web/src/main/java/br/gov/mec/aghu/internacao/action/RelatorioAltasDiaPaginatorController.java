package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RelatorioAltasDiaVO;
import br.gov.mec.aghu.model.AghParametros;
import net.sf.jasperreports.engine.JRException;


/**
 * @author tfelini
 */
public class RelatorioAltasDiaPaginatorController  extends ActionReport {

	private static final long serialVersionUID = 3967522972046913795L;	
	private static final Log LOG = LogFactory.getLog(RelatorioAltasDiaPaginatorController.class);
	private static final String RELATORIO_ALTAS_DIA = "relatorioAltasDia";

	private Date dataAlta = new Date();
	
	@EJB
	private IInternacaoFacade internacaoFacade; 
	
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;

	
	@PostConstruct
	public void inicio() {
		begin(conversation);
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {
			DocumentoJasper documento = gerarDocumento(false);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio("MENSAGEM_SUCESSO_IMPRESSAO");
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} catch (JRException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String imprimirRelatorio(){
		try {
			this.directPrint();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		return null;
	}
	
	public String visualizarRelatorio(){
		return "internacao-visualizarRelatorio";
	}

	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {	
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));		
		
	}	
	
	@Override
	public List<RelatorioAltasDiaVO> recuperarColecao() throws ApplicationBusinessException {
		return internacaoFacade.pesquisaAltasDia(this.dataAlta);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioAltasDia.jasper";
	}
	
	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat sdf_2 = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			AghParametros hospital = parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", hospital.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeRelatorio", "AINR_ALTAS_DIA");
		params.put("tituloRelatorio", "Altas registradas em ".concat(sdf_2.format(this.dataAlta)));
		
		return params;
	}

	public Date getDataAlta() {
		return dataAlta;
	}


	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}
	
	public String voltar() {
		return RELATORIO_ALTAS_DIA;
	}
}