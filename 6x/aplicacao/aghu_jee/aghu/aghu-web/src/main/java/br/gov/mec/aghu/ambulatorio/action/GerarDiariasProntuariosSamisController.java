package br.gov.mec.aghu.ambulatorio.action;

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
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.GerarDiariasProntuariosSamisVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;

import com.itextpdf.text.DocumentException;


/**
 * Controller para geração do relatório 'Materiais a coletar pela enfermagem'.
 */
public class GerarDiariasProntuariosSamisController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	private static final Log LOG = LogFactory.getLog(GerarDiariasProntuariosSamisController.class);

	private static final long serialVersionUID = 4174414765829586654L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final String GERAR_DIARIAS_PRONTUARIOS_SAMIS_PDF = "gerarDiariasProntuariosSamisPdf";
	private static final String GERAR_DIARIAS_PRONTUARIOS_SAMIS = "gerarDiariasProntuariosSamis";
	
	private Date dataDiaria;

	private Boolean iniciado;
	private Boolean mapaDesarquivamento;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<GerarDiariasProntuariosSamisVO> colecao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

	 

		if(iniciado == null || mapaDesarquivamento == null){
			iniciado = false;
			mapaDesarquivamento = false;
			colecao = new ArrayList<GerarDiariasProntuariosSamisVO>();
		}
	
	}
	


	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws ApplicationBusinessException 
	 * @throws DocumentException 
	 */
	public String print() throws ApplicationBusinessException, JRException, SystemException, IOException, DocumentException {
		if(dataDiaria == null ){
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_BLANK");
		}
		try {
			this.colecao = ambulatorioFacade.pesquisarMapaDesarquivamento(this.dataDiaria);
		} catch (ApplicationBusinessException exception) {
			this.apresentarExcecaoNegocio(exception);
		}
		mapaDesarquivamento = true;
		
		try{
			Boolean exibeMsgProntuarioJaMovimentado = Boolean.FALSE;
			ambulatorioFacade.movimentarProntuariosParaDesarquivamento(this.dataDiaria, obterLoginUsuarioLogado(), exibeMsgProntuarioJaMovimentado);
		} catch(ApplicationBusinessException e){
			this.apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return GERAR_DIARIAS_PRONTUARIOS_SAMIS_PDF;

	}

	public void inicioDiaria(){
		try {
			ambulatorioFacade.inicioDiaria(this.dataDiaria);
			iniciado = true;
			apresentarMsgNegocio(Severity.INFO, "DIARIA_INICIADA_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void fimDiaria(){

		try {
			ambulatorioFacade.fimDiaria(this.dataDiaria);

		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		}
		
		iniciado = false;
		mapaDesarquivamento = false;
		apresentarMsgNegocio(Severity.INFO, "DIARIA_FINALIZADA_SUCESSO");

	}

	@Override
	public Collection<GerarDiariasProntuariosSamisVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AACR_MAPA_DESARQ");
		
		AghParametros pDtReferencia;
		pDtReferencia = parametroFacade.getAghParametro(AghuParametrosEnum.P_DT_REFERENCIA);
		Date dataReferencia = pDtReferencia.getVlrData();
		SimpleDateFormat dataReferenciaFormatada = new SimpleDateFormat("dd/MM/yyyy");
		String dataReferenciaFormatadaStr = dataReferenciaFormatada.format(dataReferencia);
		params.put("tituloRelatorio", "Mapa de Desarquivamento para     "+ dataReferenciaFormatadaStr);
		
		String labelZona = null;
		try {
			labelZona = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LABEL_ZONA) + "/Sala";
		} catch (ApplicationBusinessException e) {
			labelZona = "Zona/Sala";
		}
		params.put("labelZona", labelZona);

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/ambulatorio/report/gerarDiariasProntariosSamis.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public String voltar(){
		return GERAR_DIARIAS_PRONTUARIOS_SAMIS;
	}
	
	public Date getDataDiaria() {
		return dataDiaria;
	}

	public void setDataDiaria(Date dataDiaria) {
		this.dataDiaria = dataDiaria;
	}

	public Boolean getIniciado() {
		return iniciado;
	}

	public void setIniciado(Boolean iniciado) {
		this.iniciado = iniciado;
	}

	public Boolean getMapaDesarquivamento() {
		return mapaDesarquivamento;
	}

	public void setMapaDesarquivamento(Boolean mapaDesarquivamento) {
		this.mapaDesarquivamento = mapaDesarquivamento;
	}
}