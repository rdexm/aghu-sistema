package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ImprimirVencimentoTributosVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class ImprimirVencimentoTributosController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(ImprimirVencimentoTributosController.class);

	/**
	 * @author felipe.rocha
	 */
	private static final long serialVersionUID = -1006442888234676013L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;


	private DominioTipoTributo tipoTributo;

	private String voltarPara;
	
	private final static String NOME_ARQUIVO= "CALENDARIO_VENCIMENTO_TRIBUTOS";
	
	
	private File arquivoGerado;

	private List<ImprimirVencimentoTributosVO> colecao = new ArrayList<ImprimirVencimentoTributosVO>();
	
	private Date dataApuracao;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/faturamento/report/imprimirVencimentoTributos.jasper";
	}

	public void print() throws BaseException, JRException, SystemException, IOException {
		try {
			recuperarColecao();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String visualizarRelatorio() {
		return "imprimirVencimentoTributosPdf";
	}
	@Override
	public Collection<ImprimirVencimentoTributosVO> recuperarColecao() throws ApplicationBusinessException {
		colecao = recuperaRelatorio();
		return colecao;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		AghParametros parametroRazaoSocial = null;
		AghParametros parametroSigla = null;
		try {
			parametroRazaoSocial = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			parametroSigla = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_SIGLA);		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		if (parametroRazaoSocial != null) {
			params.put("razaoSocial", parametroRazaoSocial.getVlrTexto());
		}
		
		if (parametroSigla != null) {
			params.put("sigla", parametroSigla.getVlrTexto());
		}
		
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/faturamento/report/");

		return params;
	}
	
	public DocumentoJasper buscarDocumentoGerado() throws ApplicationBusinessException{
		return this.gerarDocumento();
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}
	
	
	public void dispararDownloadPDF() {
		// Gera o PDF
		try {
			DocumentoJasper documento = gerarDocumento();
					
			final File file = File.createTempFile(NOME_ARQUIVO +  DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_MM) +   DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_YYYY), ".pdf");
			final FileOutputStream out = new FileOutputStream(file);

			out.write(documento.getPdfByteArray(false));
			out.flush();
			out.close();
			
			arquivoGerado = file;

			if(arquivoGerado != null) {
				download(arquivoGerado, DominioMimeType.PDF.getContentType());
			}
			arquivoGerado = null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
					
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	
	
	private List<ImprimirVencimentoTributosVO> recuperaRelatorio() throws ApplicationBusinessException {
		List<ImprimirVencimentoTributosVO> lista = new LinkedList<ImprimirVencimentoTributosVO>();
		try {
			lista.add(faturamentoFacade.recuperaTributosVencidos(getTipoTributo(), getDataApuracao()));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return lista;
	}

	
	public String voltar() {
		return this.voltarPara;
	}

	public DominioTipoTributo getTipoTributo() {
		return tipoTributo;
	}

	public void setTipoTributo(DominioTipoTributo tipoTributo) {
		this.tipoTributo = tipoTributo;
	}
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Date getDataApuracao() {
		return dataApuracao;
	}

	public void setDataApuracao(Date dataApuracao) {
		this.dataApuracao = dataApuracao;
	}

}