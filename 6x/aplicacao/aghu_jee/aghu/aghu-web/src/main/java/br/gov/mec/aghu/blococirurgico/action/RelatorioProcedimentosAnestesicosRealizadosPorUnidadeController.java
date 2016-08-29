package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioProcedimentosAnestesicosRealizadosPorUnidadeController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7066601675748201653L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private static final String RELATORIO = "relatorioProcedimentosAnestesicosRealizadosPorUnidade";
	private static final String RELATORIO_PDF = "relatorioProcedimentosAnestesicosRealizadosPorUnidadePdf";
	
	private static final Log LOG = LogFactory.getLog(RelatorioProcedimentosAnestesicosRealizadosPorUnidadeController.class);

	/*
	 * Filtros
	 */
	private Date dataInicial;
	private Date dataFinal;
	
	private List<RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO> colecao = new ArrayList<RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO>();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String voltar() {
		return RELATORIO;
	}
	
	/*
	 * Métodos do relatório
	 */	
	public String print() throws JRException, SystemException, IOException, ApplicationBusinessException, DocumentException {
		try{
			
			if (validaDatas(dataInicial, dataFinal)){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL");
			
	
		return null;
			}
			
			this.colecao = blocoCirurgicoFacade.listarProcedimentosAnestesicosRealizadosPorUnidade(dataInicial,dataFinal,ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS);
			
			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return null;
			}
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return "";
		} 
		DocumentoJasper documento = gerarDocumento();

		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
		return RELATORIO_PDF;
	}
	
	public void directPrint() {

		try {
			
			if (validaDatas(dataInicial, dataFinal)){
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_DATA_INICIAL_MAIOR_QUE_FINAL");
				return;
			}

			this.colecao = blocoCirurgicoFacade.listarProcedimentosAnestesicosRealizadosPorUnidade(dataInicial,dataFinal,ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS);
			
			if(colecao.isEmpty()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}

			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<RelatorioProcedimentosAnestesicosRealizadosPorUnidadeVO> recuperarColecao() {
		return this.colecao;
	}	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yy HH:mm", new Locale("pt", "BR"));
		SimpleDateFormat sdf_2 = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));		
		params.put("dataAtual", sdf_1.format(dataAtual));
		//String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		//params.put("hospitalLocal", hospital); 
		try {			
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", parametroRazaoSocial.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(sdf_2.format(dataInicial)).append(" A ").append(sdf_2.format(dataFinal));
		params.put("periodo", sb.toString());
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		return	"br/gov/mec/aghu/blococirurgico/report/relatorioProcedimentosAnestesicosRealizadosPorUnidade.jasper";		
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}
	
	public boolean validaDatas(Date dataInicial, Date dataFinal)  {

		if (dataInicial.after(dataFinal)) { 
			return true;
		}
		return false;		
	}
	
	/*
	 * 
	 */

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