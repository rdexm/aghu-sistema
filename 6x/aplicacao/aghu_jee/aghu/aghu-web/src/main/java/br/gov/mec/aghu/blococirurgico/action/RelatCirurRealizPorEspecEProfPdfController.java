package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.MbcRelatCirurRealizPorEspecEProfVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatCirurRealizPorEspecEProfPdfController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -3203228561732064047L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private Boolean isDirectPrint;
	private Short unidadeFuncional;
	private String descricaoUnidadeFuncional;
	private Short especialidade;
	private Date dataInicial;
	private Date dataFinal;
	
	private static final Log LOG = LogFactory.getLog(RelatCirurRealizPorEspecEProfPdfController.class);

	@PostConstruct
	protected void init() {
		begin(conversation, true);		
	}
	
	private static final String RELATORIO_CIRURGIAS_REAL_ESPC_PROF_PDF = "relatorioCirurRealizPorEspecEProfPdf";
	private static final String RELATORIO_CIRURGIAS_REAL_ESPC_PROF = "relatorioCirurRealizPorEspecEProf";

	
	public String inicio() {
		if (isDirectPrint) {
			try {
				directPrint();
				return RELATORIO_CIRURGIAS_REAL_ESPC_PROF;
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error("Exceção capturada: ", e);
				apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			}
		}
		return null;
	}

	@Override
	public Collection<MbcRelatCirurRealizPorEspecEProfVO> recuperarColecao()
			throws ApplicationBusinessException {
		
		 Collection<MbcRelatCirurRealizPorEspecEProfVO> colecao = null;
		
		try {
			colecao = blocoCirurgicoFacade.obterCirurRealizPorEspecEProf(unidadeFuncional, dataInicial, dataFinal, especialidade);
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		return colecao;

	} 

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 */
	public void directPrint() throws ApplicationBusinessException {

		try {
			DocumentoJasper  documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {			
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String imprimirRelatorio(){
		isDirectPrint = true;
		return RELATORIO_CIRURGIAS_REAL_ESPC_PROF_PDF;
	}
	
	public String visualizarRelatorio(){
		isDirectPrint = false;
		return RELATORIO_CIRURGIAS_REAL_ESPC_PROF_PDF;
	}

	@Override
	public String recuperarArquivoRelatorio()  {
		return "br/gov/mec/aghu/blococirurgico/report/RelatCirurRealizPorEspecEProf.jasper";
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public String print()throws ApplicationBusinessException, JRException, SystemException, IOException, DocumentException {
	
		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
		return "relatorio";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException 
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	public Map<String, Object> recuperarParametros() {

		final Map<String, Object> params = new HashMap<String, Object>();

		String nomeHospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		params.put("nomeHospital", (nomeHospital != null) ? nomeHospital.toUpperCase() : nomeHospital);
		params.put("unidadeFuncional", unidadeFuncional);
		params.put("especialidade", especialidade);
		params.put("dataInicial", dataInicial);
		params.put("dataFinal", dataFinal);
		params.put("descricaoUnidadeFuncional", descricaoUnidadeFuncional);
		
		
		
		String subRelatorio = "RelatCirurRealizPorEspecEProfSubReport.jasper";
		params.put("subRelatorio", subRelatorio);
		
		String subReportDir ="br/gov/mec/aghu/blococirurgico/report/";
		params.put("SUBREPORT_DIR",subReportDir);
	
		
		return params;
	}
	
	public String voltar(){    	
	    return RELATORIO_CIRURGIAS_REAL_ESPC_PROF;
	    	
	}

	protected void apresentarExcecaoNegocio(ApplicationBusinessException e) {
		// Apenas apresenta a mensagem de erro negocial para o cliente
		apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return cadastrosBasicosInternacaoFacade;
	}

	public void setCadastrosBasicosInternacaoFacade(
			ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade) {
		this.cadastrosBasicosInternacaoFacade = cadastrosBasicosInternacaoFacade;
	}

	public Boolean getIsDirectPrint() {
		return isDirectPrint;
	}

	public void setIsDirectPrint(Boolean isDirectPrint) {
		this.isDirectPrint = isDirectPrint;
	}

	public Short getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(Short unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public String getDescricaoUnidadeFuncional() {
		return descricaoUnidadeFuncional;
	}

	public void setDescricaoUnidadeFuncional(String descricaoUnidadeFuncional) {
		this.descricaoUnidadeFuncional = descricaoUnidadeFuncional;
	}

	public Short getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Short especialidade) {
		this.especialidade = especialidade;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}
