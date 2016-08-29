package br.gov.mec.aghu.exames.pesquisa.action;

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
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.RelatorioAgendamentoProfissionalVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import net.sf.jasperreports.engine.JRException;


public class RelatorioAgendamentoProfissionalPaginatorController extends ActionReport{

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -7427874994709793622L;

	private static final String RELATORIO_AGENDAMENTO_PROFISSIONAL = "relatorioAgendamentoProfissionalAmbulatorio";
	private static final String RELATORIO_AGENDAMENTO_PROFISSIONAL_PDF = "relatorioAgendamentoProfissionalPdf";

	@EJB
	private IExamesFacade examesFacade;
	
	// campos filtro
	private Date dataReferenciaIni;
	private Date dataReferenciaFim;
	
	private List<RelatorioAgendamentoProfissionalVO> colecao = new ArrayList<RelatorioAgendamentoProfissionalVO>(0);
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String voltar(){
		return RELATORIO_AGENDAMENTO_PROFISSIONAL;
	}
	
	public String print()throws BaseException, JRException, SystemException, IOException, DocumentException {
		if(dataReferenciaIni != null && dataReferenciaFim != null && DateUtil.calcularDiasEntreDatas(this.dataReferenciaIni, this.dataReferenciaFim)>90){
			apresentarMsgNegocio(Severity.ERROR, "ERRO_DIFERENCA_DATAS", 90, 
									DateUtil.dataToString(this.dataReferenciaIni, DateConstants.DATE_PATTERN_DDMMYYYY), 
									DateUtil.dataToString(this.dataReferenciaFim, DateConstants.DATE_PATTERN_DDMMYYYY), 
									DateUtil.calcularDiasEntreDatas(this.dataReferenciaIni, this.dataReferenciaFim));
		

	
		return null;
		}
		
		this.examesFacade.setTimeout(300);//5 min
		colecao = this.examesFacade.pesquisarRelatorioAgendamentoProfissional(dataReferenciaIni, dataReferenciaFim);
		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_AGENDAMENTO_PROFISSIONAL_PDF;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			
			if(dataReferenciaIni != null && dataReferenciaFim != null && DateUtil.calcularDiasEntreDatas(this.dataReferenciaIni, this.dataReferenciaFim)>90){
				apresentarMsgNegocio(Severity.ERROR, "ERRO_DIFERENCA_DATAS", 90, 
										DateUtil.dataToString(this.dataReferenciaIni, DateConstants.DATE_PATTERN_DDMMYYYY), 
										DateUtil.dataToString(this.dataReferenciaFim, DateConstants.DATE_PATTERN_DDMMYYYY), 
										DateUtil.calcularDiasEntreDatas(this.dataReferenciaIni, this.dataReferenciaFim));
			
			}else{
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),	super.getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
			}
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}

	@Override
	public Collection<RelatorioAgendamentoProfissionalVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("hospitalLocal", hospital);
		params.put("tituloRelatorio", "Total de Solicitações Agendadas pelo Profissional");  
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		//params.put("nomeRelatorio", "");
		params.put("totalRegistros", colecao.size()-1);
		params.put("periodo", "Período: "+DateFormatUtil.obterDataFormatada(dataReferenciaIni, DateConstants.DATE_PATTERN_DDMMYYYY) +" à "+
										  DateFormatUtil.obterDataFormatada(dataReferenciaFim, DateConstants.DATE_PATTERN_DDMMYYYY));
		
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioAgendamentoProfissional.jasper";
	}

	
	/**
	 * Método para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {

		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}


	public Date getDataReferenciaIni() {
		return dataReferenciaIni;
	}


	public void setDataReferenciaIni(Date dataReferenciaIni) {
		this.dataReferenciaIni = dataReferenciaIni;
	}


	public Date getDataReferenciaFim() {
		return dataReferenciaFim;
	}


	public void setDataReferenciaFim(Date dataReferenciaFim) {
		this.dataReferenciaFim = dataReferenciaFim;
	}
	

}
