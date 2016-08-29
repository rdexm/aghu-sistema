package br.gov.mec.aghu.emergencia.action;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import net.sf.jasperreports.engine.JRException;

import org.jfree.util.Log;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.FormularioEncExternoVO;
import br.gov.mec.aghu.report.AghuReportGenerator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.service.ServiceException;

import com.itextpdf.text.DocumentException;

public class ImprimirFormularioEncExternoReportGenerator extends AghuReportGenerator {
	
	private static final long serialVersionUID = 1736471114873L;

	@Inject
	private IEmergenciaFacade emergenciaFacade;
		
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	
	private String textoEncExt;
	private String cidadeHosp;

	private Long triagem;
	private Short seqp;
	
	protected void carregarParametros() {
		try{
			textoEncExt = (String) parametroFacade.obterAghParametroPorNome("P_AGHU_TEXTO_FORMULARIO_ENC_EXTERNO", "vlrTexto");
			cidadeHosp = (String) parametroFacade.obterAghParametroPorNome("P_HOSPITAL_END_CIDADE", "vlrTexto");
		} catch (ApplicationBusinessException e) {
			//EXIBE EXCEÇÃO NA TELA
			Log.error("ERRO: " + e.getMessage());
		}
	}

	protected List<FormularioEncExternoVO> carregarDadosFormulario() {
		try {
			return emergenciaFacade.carregarDadosFormulario(triagem, seqp, cidadeHosp);
		} catch (ApplicationBusinessException e) {
			//EXIBE EXCEÇÃO NA TELA
			Log.error("ERRO");
		}
		return null;
	}
	
	@Override
	protected Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();

		this.carregarParametros();

		params.put("caminhoLogo", recuperaLogo());
		params.put("hospitalLocal", cidadeHosp);
		params.put("dataAtual", dataAtual);
		params.put("textoFromExt", textoEncExt);
		params.put("SUBREPORT_DIR","br/gov/mec/aghu/perinatologia/report/");

		return params;	
		
	}
	
	private String recuperaLogo() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/logoprefeiturahospital.jpg");
		return path;
	}

	public void gerarBoletim(InetAddress endereco) throws ServiceException, BaseException, JRException{
		DocumentoJasper documento = gerarDocumento();

		this.sistemaImpressao.imprimir(documento.getJasperPrint(), endereco);
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public void gerarPdf(OutputStream out) throws DocumentException, IOException, JRException, BaseException {
		out.write(gerarDocumento().getPdfByteArray(false));
	}



	@Override
	protected void executarPosGeracaoRelatorio(Map<String, Object> arg0)
			throws ApplicationBusinessException {
		Log.info("OK");
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/perinatologia/report/relatorioFichaEncExterno.jasper";
	}

	@Override
	protected Collection<FormularioEncExternoVO> recuperarColecao()
			throws ApplicationBusinessException {
		return carregarDadosFormulario();
	}

	public Long getTriagem() {
		return triagem;
	}

	public void setTriagem(Long triagem) {
		this.triagem = triagem;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
}
