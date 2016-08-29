/**
 * 
 */
package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.vo.RelatorioAtendEmergObstetricaCondutaVO;
import br.gov.mec.aghu.paciente.vo.RelatorioAtendEmergObstetricaIntercorrenciasVO;
import br.gov.mec.aghu.paciente.vo.RelatorioAtendEmergObstetricaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * @author aghu
 *
 */

@SuppressWarnings("PMD.NPathComplexity")
public class RelatorioAtendEmergObstetricaPOLController extends ActionReport {

	private static final String PAC_CODIGO = "pacCodigo";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 this.setParameters();
	}

	private static final Log LOG = LogFactory.getLog(RelatorioAtendEmergObstetricaPOLController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;

	private static final long serialVersionUID = -8612485059154640780L;

	private static final String HISTORIA_OBSTETRICA_POR_GESTACAO_PACIENTE_LIST_POL = "pol-historiaObstetricaPorGestacaoPacienteListPOL";

	private List<RelatorioAtendEmergObstetricaVO> dadosRelatorio = new ArrayList<RelatorioAtendEmergObstetricaVO>();
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	//Parametros do processamento
	private Date criadoEm;
	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Integer conNumero;
	private String voltarPara;
	private String abaOrigem;
	private Integer cid;
	private Boolean voltarEmergencia;
	private Boolean previa;
	
	private enum EnumTargetRelatorioAtendEmergObstetricaPOL {
		TITULO_RELATORIO("Consulta na Emergência Obstétrica"),
		TITLE_PDF_VIEW("Consulta na Emergência Obstétrica"),
		TEXTO_CABECALHO("Este documento é uma compilação dos registros eletrônicos de diversos profissionais que atenderam a paciente."),
		NOME_RELATORIO("Consulta na Emergência Obstétrica"),  
		NOME_ARQUIVO_RELATORIO("br/gov/mec/aghu/paciente/prontuarioonline/report/atdEmergenciaObstetrica/AtendimentoEmergenciaObstetrica.jasper"),
		SUBREPORT_DIR("br/gov/mec/aghu/paciente/prontuarioonline/report/atdEmergenciaObstetrica/"),
		NOME_RELATORIO_RODAPE("MCOR_CONS_EMER_OBS"); 
		
		private String str;

		private EnumTargetRelatorioAtendEmergObstetricaPOL(String str) {
			this.str = str;
		}

		@Override
		public String toString() {
			return this.str;
		}
	}

	private void setParameters(){
		if(getRequestParameter(PAC_CODIGO) != null && !getRequestParameter(PAC_CODIGO).isEmpty()){
			this.setGsoPacCodigo(Integer.valueOf(getRequestParameter(PAC_CODIGO)));
		}
		
		if(getRequestParameter("seqp") != null && !getRequestParameter("seqp").isEmpty()){
			this.setGsoSeqp(Short.valueOf(getRequestParameter("seqp")));
		}
		
		if(getRequestParameter("numeroConsulta") != null && !getRequestParameter("numeroConsulta").isEmpty()){
			this.setConNumero(Integer.valueOf(getRequestParameter("numeroConsulta")));
		}
		
		if(getRequestParameter("usuarioAutenticado") != null && !getRequestParameter("usuarioAutenticado").isEmpty()){
			super.setUsuarioAutenticado(Boolean.valueOf(getRequestParameter("usuarioAutenticado")));
		} 
		
		if(getRequestParameter("matricula") != null && !getRequestParameter("matricula").isEmpty()){
			super.setMatricula(Integer.valueOf(getRequestParameter("matricula")));
		}
		
		if(getRequestParameter("vinculo") != null && !getRequestParameter("vinculo").isEmpty()){
			super.setVinculo(Short.valueOf(getRequestParameter("vinculo")));
		}
		this.setPrevia(false);
		if(getRequestParameter("previa") != null && !getRequestParameter("previa").isEmpty()){
			this.setPrevia(Boolean.valueOf(getRequestParameter("previa")));
		}
		
		if(getRequestParameter("gerarPendenciaDeAssinaturaDigital") != null && !getRequestParameter("gerarPendenciaDeAssinaturaDigital").isEmpty()){
			super.setGerarPendenciaDeAssinaturaDigital(Boolean.valueOf(getRequestParameter("gerarPendenciaDeAssinaturaDigital")));
		} 
		
		if(getRequestParameter("voltarEmergencia") != null && !getRequestParameter("voltarEmergencia").isEmpty()){
			this.setVoltarEmergencia(Boolean.valueOf(getRequestParameter("voltarEmergencia")));
		} 
		
		
		this.setServidorLogado();
		
		getParametroRetorno();
		
		String cid = getRequestParameter("paramCid");
		if(StringUtils.isNotBlank(cid)){		
			this.setCid(Integer.valueOf(cid));
		}
	}

	private void getParametroRetorno() {
		if(getRequestParameter("voltarPara") != null && !getRequestParameter("voltarPara").isEmpty()){
			this.setVoltarPara(getRequestParameter("voltarPara"));
		}
		
		if(getRequestParameter("abaOrigem") != null && !getRequestParameter("abaOrigem").isEmpty()){
			this.setAbaOrigem(getRequestParameter("abaOrigem"));
		}
	}
	
	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio() {
		return EnumTargetRelatorioAtendEmergObstetricaPOL.NOME_ARQUIVO_RELATORIO.toString();
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		gerarDados();
		return dadosRelatorio;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();
		
		String leito = null;
		String prontuario = null;
		String nomePac = null;
		Integer pacCodigo = null;
		Integer conNumero = null;
		List<RelatorioAtendEmergObstetricaCondutaVO> condutas = null;
		List<RelatorioAtendEmergObstetricaIntercorrenciasVO> intercorrencias = null;
		
		if(dadosRelatorio != null && !dadosRelatorio.isEmpty()){
			condutas = new ArrayList<RelatorioAtendEmergObstetricaCondutaVO>();
			intercorrencias = new ArrayList<RelatorioAtendEmergObstetricaIntercorrenciasVO>();
			
			RelatorioAtendEmergObstetricaVO vo = dadosRelatorio.get(0);
			leito = vo.getLeito();
			prontuario = vo.getProntuario();
			nomePac = vo.getNomePac();
			pacCodigo = vo.getPacCodigo();
			conNumero = vo.getConNumero();
			condutas = vo.getCondutas();
			intercorrencias = vo.getIntercorrencias();
		}		
		
		// informa todos os parametros necessarios
		params.put("tituloRelatorio", EnumTargetRelatorioAtendEmergObstetricaPOL.TITULO_RELATORIO.toString());
		params.put("textoCabecalho", EnumTargetRelatorioAtendEmergObstetricaPOL.TEXTO_CABECALHO.toString());
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}		
		params.put("SUBREPORT_DIR", EnumTargetRelatorioAtendEmergObstetricaPOL.SUBREPORT_DIR.toString());
		params.put("nomeRelatorio", EnumTargetRelatorioAtendEmergObstetricaPOL.NOME_RELATORIO.toString());
		params.put("voMaster", dadosRelatorio);
		params.put("leito", leito); 
		params.put("prontuario", prontuario); 
		params.put("nomePac", nomePac); 
		params.put(PAC_CODIGO, pacCodigo); 
		params.put("conNumero", conNumero);
		params.put("voCondutas", condutas);
		params.put("voIntercorrencias", intercorrencias);
		if(previa){
			params.put("imagemFundo", recuperaCaminhoImgBackground());
		}
		
					
		return params;
	}
	
	private String recuperaCaminhoImgBackground() {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		String path = servletContext.getRealPath("/resources/img/report_previa.png");
		return path;
	}
	
	public void gerarDados() {
		try {
			setDadosRelatorio(new ArrayList<RelatorioAtendEmergObstetricaVO>());
			RelatorioAtendEmergObstetricaVO vo = prontuarioOnlineFacade.obterRelatorioAtendEmergObstetrica(getCriadoEm(), getMatricula(), getVinculo(), getGsoPacCodigo(), getGsoSeqp(), getConNumero());
			if(vo != null){
				dadosRelatorio.add(vo);
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}	

	/**
	 * Renderiza o PDF
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws JRException
	 * @throws DocumentException
	 * @throws ApplicationBusinessException 
	 * @throws BaseException
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException  {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}
	
	public StreamedContent getRenderPdfParaUsuarioAutenticado() throws IOException, JRException, DocumentException, ApplicationBusinessException  {
		Boolean gerarPendencia = getGerarPendenciaDeAssinaturaDigital() != null ? getGerarPendenciaDeAssinaturaDigital() : Boolean.FALSE;
		DocumentoJasper documento = gerarDocumento(DominioTipoDocumento.CEO, gerarPendencia);
		
		if(gerarPendencia) {
			this.apresentarMsgNegocio(Severity.INFO, "PENDENCIA_ASSINATURA_GERADA_SUCESSO");
		}
		return criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}

	public void directPrint() {
		try {
			Boolean gerarPendencia = getGerarPendenciaDeAssinaturaDigital() != null ? getGerarPendenciaDeAssinaturaDigital() : Boolean.TRUE;
			DocumentoJasper documento = gerarDocumento(gerarPendencia);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String voltar() {
		limparDados();
		return HISTORIA_OBSTETRICA_POR_GESTACAO_PACIENTE_LIST_POL;
	}
	
	private void limparDados() {
		setConNumero(null);
		setCriadoEm(null);
		setDadosRelatorio(new ArrayList<RelatorioAtendEmergObstetricaVO>());
		setGsoPacCodigo(null);
		setGsoSeqp(null);
		setMatricula(null);
		setVinculo(null);
	}

	public List<?> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(List<RelatorioAtendEmergObstetricaVO> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public String getTitlePdfView() {
		return EnumTargetRelatorioAtendEmergObstetricaPOL.TITLE_PDF_VIEW.toString();
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getAbaOrigem() {
		return abaOrigem;
	}

	public void setAbaOrigem(String abaOrigem) {
		this.abaOrigem = abaOrigem;
	}

	public Integer getCid() {
		return cid;
	}

	public void setCid(Integer cid) {
		this.cid = cid;
	}

	public Boolean getVoltarEmergencia() {
		return voltarEmergencia;
	}

	public void setVoltarEmergencia(Boolean voltarEmergencia) {
		this.voltarEmergencia = voltarEmergencia;
	}

	public Boolean getPrevia() {
		return previa;
	}

	public void setPrevia(Boolean previa) {
		this.previa = previa;
	}
	
}
