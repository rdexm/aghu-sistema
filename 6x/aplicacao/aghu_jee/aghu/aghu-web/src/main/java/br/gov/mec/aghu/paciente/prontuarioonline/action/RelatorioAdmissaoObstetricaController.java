/**
 * 
 */
package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.http.Http;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaExamesCondutaVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaExamesRealizadosVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO;
import br.gov.mec.aghu.internacao.vo.SumarioAdmissaoObstetricaInternacaoVO.ParametrosReportEnum;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.vo.SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author Guilherme Finotti Carvalho
 * 
 */

public class RelatorioAdmissaoObstetricaController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioAdmissaoObstetricaController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject @Http 
	private ConversationContext conversationContext;

	private static final long serialVersionUID = 575915605491652912L;

	private static final String TITULO_RELATORIO = "Admissão Obstétrica";

	private DominioNomeRelatorio relatorio;
	private String fileName;
	private String origem;
	private Integer atdSeq;
	private Integer pacCodigo;
	private Integer conNumero;
	private Short gsoSeqp;
	private Date dthrMovimento;
	private Boolean gerouArquivo;
	private String titlePdfView;
	private Map<String, Object> parametrosEspecificos;
	private String nomeRelatorio;
	private String nomeRelatorioRodape;
	private String nomeArquivoRelatorio;
	private List<SumarioAdmissaoObstetricaInternacaoVO> dadosRelatorio = null;
	private Integer numeroProntuario;
	private Boolean voltarEmergencia = Boolean.TRUE;
	private String abaOrigem; 
	private Short unfSeq;
	private Integer paramCid;

	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		conversationContext.setConcurrentAccessTimeout(900000000000l);
	}

	public void inicio() throws ApplicationBusinessException {
		if (dadosRelatorio == null) {
			gerarDados();
		}
	}
	
	/**
	 * Inserido suppresswarnings devido a merge parcial da Emergência/Perinatologia 
	 * Remover quando estabilizada a perinatologia se o método não for utilizado
	 * 
	 * Obs.: ainda restam ser puxadas alterações dos xhtml e controllers do branch modularizado da Emergência/Perinatologia
	 */
	@SuppressWarnings("unused")
	private void setParameters(){
		obterInformacaoesPacienteSessao();				
		if(getRequestParameter("dthrMovimento") != null){
			this.setDthrMovimento(this.converterStringDataToDate(getRequestParameter("dthrMovimento")));
		}
		if(getRequestParameter("voltarPara") != null){
			this.setOrigem(getRequestParameter("voltarPara"));
		}
		if(getRequestParameter("gerarPendenciaDeAssinaturaDigital") != null && !getRequestParameter("gerarPendenciaDeAssinaturaDigital").isEmpty()){
			super.setGerarPendenciaDeAssinaturaDigital(Boolean.valueOf(getRequestParameter("gerarPendenciaDeAssinaturaDigital")));
		}	
		if(getRequestParameter("voltarEmergencia") != null && !getRequestParameter("voltarEmergencia").isEmpty()){
			this.setVoltarEmergencia(Boolean.valueOf(getRequestParameter("voltarEmergencia")));
		} 
		if(getRequestParameter("paramCid") != null){
			this.setParamCid(Integer.valueOf(getRequestParameter("paramCid")));
		}
		if(getRequestParameter("abaOrigem") != null){
			this.setAbaOrigem(getRequestParameter("abaOrigem"));
		}
		this.setServidorLogado();
	}

	private void obterInformacaoesPacienteSessao() {
		if(StringUtils.isNotBlank(getRequestParameter("usuarioAutenticado")) && !getRequestParameter("usuarioAutenticado").isEmpty()){
			super.setUsuarioAutenticado(Boolean.valueOf(getRequestParameter("usuarioAutenticado")));
		} 		
		if(StringUtils.isNotBlank(getRequestParameter("matricula")) && !getRequestParameter("matricula").isEmpty()){
			super.setMatricula(Integer.valueOf(getRequestParameter("matricula")));
		}		
		if(StringUtils.isNotBlank(getRequestParameter("vinculo")) && !getRequestParameter("vinculo").isEmpty()){
			super.setVinculo(Short.valueOf(getRequestParameter("vinculo")));
		}
		if(StringUtils.isNotBlank(getRequestParameter("pacCodigo"))){
			this.setPacCodigo(Integer.valueOf(getRequestParameter("pacCodigo")));
		}
		if(StringUtils.isNotBlank(getRequestParameter("numeroConsulta"))) {
			this.setConNumero(Integer.valueOf(getRequestParameter("numeroConsulta")));
		}
		if(StringUtils.isNotBlank(getRequestParameter("seqp"))) {
			this.setGsoSeqp(Short.valueOf(getRequestParameter("seqp")));	
		}
		if(StringUtils.isNotBlank(getRequestParameter("unfSeq"))) {
			this.setUnfSeq(Short.valueOf(getRequestParameter("unfSeq")));	
		}
	}
	
	private Date converterStringDataToDate(String data){
		String[] splitData = data.split("/");
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(splitData[2]), Integer.parseInt(splitData[1]), Integer.parseInt(splitData[0]));
		return cal.getTime();
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/internacao/report/sumarioAdmObstetricaInternacao/SumarioAdmObstetrica.jasper";
	}

	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return dadosRelatorio;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		gerarDados();
		
		Map<String, Object> params = new HashMap<String, Object>();

		String leito = "";
		String nome = "";
		String prontuario = "";
		String pacCodigo = null;
		String QPAC_BA = "";

		List<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO> gestacoesAnteriores = new ArrayList<SumarioAtdRecemNascidoSlPartoGestacoesAnterioresVO>();
		List<SumarioAdmissaoObstetricaExamesRealizadosVO> examesMae = new ArrayList<SumarioAdmissaoObstetricaExamesRealizadosVO>();
		List<SumarioAdmissaoObstetricaExamesCondutaVO> conduta = new ArrayList<SumarioAdmissaoObstetricaExamesCondutaVO>();

		if (dadosRelatorio != null && !dadosRelatorio.isEmpty()) {
			SumarioAdmissaoObstetricaInternacaoVO vo = dadosRelatorio.get(0);

			leito = vo.getLeito();
			prontuario = vo.getProntuario();
			
			if(vo.getParametrosHQL().get(ParametrosReportEnum.QGESTACAO_GSO_PAC_CODIGO) != null) {
				pacCodigo = vo.getParametrosHQL().get(ParametrosReportEnum.QGESTACAO_GSO_PAC_CODIGO).toString();
			}
			if(vo.getParametrosHQL().get(ParametrosReportEnum.QPAC_BA) != null) {				
				QPAC_BA = vo.getParametrosHQL().get(ParametrosReportEnum.QPAC_BA).toString();
			}
			nome = vo.getNome();
			conduta = vo.getCondutas();
			examesMae = vo.getExamesRealizados();
			gestacoesAnteriores = vo.getGestacoesAnteriores();
		}

		// informa todos os parametros necessarios
		params.put("tituloRelatorio", TITULO_RELATORIO);
		params.put(
				"textoCabecalho",
				"Este documento é uma compilação dos registros eletrônicos de diversos profissionais que atenderam a paciente.");
		params.put("caminhoLogo", recuperarCaminhoLogo());
				
		params.put("leito", leito);
		params.put("prontuario", prontuario);
		params.put("nome", nome);
		params.put("voMaster", dadosRelatorio);
		params.put("pacCodigo", pacCodigo);
		params.put("QPAC_BA", QPAC_BA);
		params.put("voExamesMae", examesMae);
		params.put("voGestacoesAnteriores", gestacoesAnteriores);
		params.put("voConduta", conduta);
		params.put("SUBREPORT_DIR", "br/gov/mec/aghu/internacao/report/sumarioAdmObstetricaInternacao/");

		if (getParametrosEspecificos() != null) {
			params.putAll(getParametrosEspecificos());
		}

		return params;
	}

	public void gerarDados() {
		
		dadosRelatorio = new ArrayList<SumarioAdmissaoObstetricaInternacaoVO>();
		SumarioAdmissaoObstetricaInternacaoVO vo = null;
		setParameters();
		
		try {
			vo = prontuarioOnlineFacade.obterRelatorioSumarioAdmissaoObstetricaInternacao(super.getMatricula(), super.getVinculo(), 
																						  pacCodigo, conNumero, 
																						  gsoSeqp, dthrMovimento);
		}catch(BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		if(vo != null) {
			dadosRelatorio.add(vo);
			if(StringUtils.isNotBlank(vo.getProntuario())) {
				String prontuario = StringUtils.remove(vo.getProntuario(), "/");
				if(prontuario != null) {
					setNumeroProntuario(Integer.valueOf(prontuario));
				}
			}
		}
		setTitlePdfView(TITULO_RELATORIO);
		setNomeRelatorio(TITULO_RELATORIO);

	}

	public String recuperarCaminhoLogo(){		
		//Pega o realPath da imagem
		ServletContext servletContext = (ServletContext)  FacesContext.getCurrentInstance().getExternalContext().getContext();
		return servletContext.getRealPath("/resources/img/logoClinicas.jpg");				

	}

	public StreamedContent getRenderPdf() throws IOException,
			JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}

	public StreamedContent getRenderPdfParaUsuarioAutenticado() throws IOException,
			JRException, DocumentException, ApplicationBusinessException {
		Boolean gerarPendencia = getGerarPendenciaDeAssinaturaDigital() != null ? getGerarPendenciaDeAssinaturaDigital() : false;
		DocumentoJasper documento = gerarDocumento(DominioTipoDocumento.ACO, !gerarPendencia);
		if(gerarPendencia) {
			this.apresentarMsgNegocio(Severity.INFO, "PENDENCIA_ASSINATURA_GERADA_SUCESSO");
		}		
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}
	
	public void directPrint() {
		try {
			Boolean gerarPendencia = getGerarPendenciaDeAssinaturaDigital() != null ? getGerarPendenciaDeAssinaturaDigital() : Boolean.TRUE;
			DocumentoJasper documento = gerarDocumento(!gerarPendencia);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String voltar() {
		return getOrigem();
	}
	
	public DominioNomeRelatorio getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(DominioNomeRelatorio relatorio) {
		this.relatorio = relatorio;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getTitlePdfView() {
		return titlePdfView;
	}

	public void setTitlePdfView(String titlePdfView) {
		this.titlePdfView = titlePdfView;
	}

	public Map<String, Object> getParametrosEspecificos() {
		return parametrosEspecificos;
	}

	public void setParametrosEspecificos(
			Map<String, Object> parametrosEspecificos) {
		this.parametrosEspecificos = parametrosEspecificos;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public String getNomeRelatorioRodape() {
		return nomeRelatorioRodape;
	}

	public void setNomeRelatorioRodape(String nomeRelatorioRodape) {
		this.nomeRelatorioRodape = nomeRelatorioRodape;
	}

	public String getNomeArquivoRelatorio() {
		return nomeArquivoRelatorio;
	}

	public void setNomeArquivoRelatorio(String nomeArquivoRelatorio) {
		this.nomeArquivoRelatorio = nomeArquivoRelatorio;
	}

	public List<?> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(
			List<SumarioAdmissaoObstetricaInternacaoVO> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Date getDthrMovimento() {
		return dthrMovimento;
	}

	public void setDthrMovimento(Date dthrMovimento) {
		this.dthrMovimento = dthrMovimento;
	}

	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}
	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}

	public Integer getParamCid() {
		return paramCid;
	}

	public void setParamCid(Integer cid) {
		this.paramCid = cid;
	}

	public Boolean getVoltarEmergencia() {
		return voltarEmergencia;
	}

	public void setVoltarEmergencia(Boolean voltarEmergencia) {
		this.voltarEmergencia = voltarEmergencia;
	}

	public String getAbaOrigem() {
		return abaOrigem;
	}

	public void setAbaOrigem(String abaOrigem) {
		this.abaOrigem = abaOrigem;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}	
}