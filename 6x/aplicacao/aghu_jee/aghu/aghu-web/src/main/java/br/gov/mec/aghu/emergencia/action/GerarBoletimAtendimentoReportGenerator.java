package br.gov.mec.aghu.emergencia.action;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
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
import br.gov.mec.aghu.emergencia.vo.BoletimAtendimentoVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.report.AghuReportGenerator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.service.ServiceException;

import com.itextpdf.text.DocumentException;

public class GerarBoletimAtendimentoReportGenerator extends AghuReportGenerator {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1736478364873L;

	@Inject
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private static final String TIPO_PARAMETRO = "vlrTexto";
	private Integer consulta; // = 20352819;//17658453; //21129917;
	private String labelZona;
	private String labelSala;
	private String instituicao;

	protected void carregarParametros(){
		try {
			labelZona = obterParametroPorNome("P_AGHU_LABEL_ZONA", TIPO_PARAMETRO);
			labelSala = obterParametroPorNome("P_AGHU_LABEL_SALA", TIPO_PARAMETRO);
			//logo = (String)configuracaoService.obterAghParametroPorNome("P_AGHU_LOGO_HOSPITAL", TIPO_PARAMETRO);
			instituicao = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		} catch (ServiceException e) {
			//EXIBE EXCEÇÃO NA TELA
			Log.error("ERRO");
		}
	}

	private String obterParametroPorNome(String nome, String tipo) throws ServiceException {
		AghParametros aghParametros = parametroFacade.obterAghParametroPorNome(nome);

		Object retorno = null;

		if (aghParametros != null) {

			switch (tipo) {
			case "vlrData":
				retorno = aghParametros.getVlrData();
				break;
			case "vlrNumerico":
				retorno = aghParametros.getVlrNumerico();
				break;
			case "vlrTexto":
				retorno = aghParametros.getVlrTexto();
				break;
			case "rotinaConsistencia":
				retorno = aghParametros.getRotinaConsistencia();
				break;
			case "exemploUso":
				retorno = aghParametros.getExemploUso();
				break;
			case "tipoDado":
				retorno = aghParametros.getTipoDado();
				break;
			case "vlrDataPadrao":
				retorno = aghParametros.getVlrDataPadrao();
				break;
			case "vlrNumericoPadrao":
				retorno = aghParametros.getVlrNumericoPadrao();
				break;
			case "vlrTextoPadrao":
				retorno = aghParametros.getVlrTextoPadrao();
				break;
			default:
				throw new ServiceException();
			}
		}
		
		return (String) retorno;
	}

	protected List<BoletimAtendimentoVO> carregarDadosBoletim() {
		try {
			return emergenciaFacade.carregarDadosBoletim(consulta);
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
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		this.carregarParametros();

		params.put("caminhoLogo", recuperaLogo());
		params.put("dataAtual", sdf.format(dataAtual));
		params.put("hospitalLocal", instituicao);
		params.put("labelZona", labelZona+":");
		params.put("labelSala", labelSala+":");

		params.put("SUBREPORT_DIR","br/gov/mec/aghu/perinatologia/report/");
		
		return params;	
	}
	
	private String recuperaLogo() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/logoClinicas.jpg");
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
		return "br/gov/mec/aghu/perinatologia/report/relatorioBoletimAtendimento.jasper";
	}

	@Override
	protected Collection<BoletimAtendimentoVO> recuperarColecao()
			throws ApplicationBusinessException {
		return carregarDadosBoletim();
	}

	public Integer getConsulta() {
		return consulta;
	}

	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}
}
