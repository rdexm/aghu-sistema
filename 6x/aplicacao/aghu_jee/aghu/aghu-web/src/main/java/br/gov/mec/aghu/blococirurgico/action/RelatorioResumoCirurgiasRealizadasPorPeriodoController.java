package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioResumoCirurgiasRealizadasPorPeriodoListVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioResumoCirurgiasRealizadasPorPeriodoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioResumoCirurgiasRealizadasPorPeriodoController extends ActionReport  {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -7066601675748201653L;
	
	private AghUnidadesFuncionais unidadeCirurgica;
	private Date dataInicial;
	private Date dataFinal;

	private List<RelatorioResumoCirurgiasRealizadasPorPeriodoListVO> colecao;
	private RelatorioResumoCirurgiasRealizadasPorPeriodoVO dados;

	private static final Log LOG = LogFactory.getLog(RelatorioResumoCirurgiasRealizadasPorPeriodoController.class);

	private static final String RESUMO_CIRURGIAS = "relatorioResumoCirurgiasRealizadasPorPeriodo";
	private static final String RESUMO_CIRURGIAS_PDF = "relatorioResumoCirurgiasRealizadasPorPeriodoPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioResumoCirurgiasRealizadasPorPeriodo.jasper";
	}
	
	public String voltar() {
		return RESUMO_CIRURGIAS;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> parametros = new HashMap<String, Object>();
		
	  try {
		
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			parametros.put("hospitalLocal", parametroRazaoSocial.getVlrTexto());
			
			parametros.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
			
			String uniDescricao = this.unidadeCirurgica.getDescricao();
			parametros.put("uniDescricao", uniDescricao);
			
			parametros.put("dataInicio", DateUtil.obterDataFormatada(dataInicial, "dd/MM/yyyy"));
			parametros.put("dataFinal", DateUtil.obterDataFormatada(dataFinal, "dd/MM/yyyy"));
			
			this.dados = this.blocoCirurgicoCadastroFacade.buscaDadosRelatorio(unidadeCirurgica, dataInicial, dataFinal);
			
			parametros.put("totalCanceladas", this.dados.getTotalCanceladas());
			parametros.put("totalPendentesRet", this.dados.getTotalPendentesRet());
			parametros.put("totalEmerg", this.dados.getTotalEmerg());
			parametros.put("totalUrg", this.dados.getTotalUrg());
			parametros.put("totalEletiva", this.dados.getTotalEletiva());
			parametros.put("totalCirurgRealizadas", this.dados.getTotalCirurgRealizadas());
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	  
		return parametros;
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioResumoCirurgiasRealizadasPorPeriodoListVO> recuperarColecao(){		
		return colecao; 
	}
	
	public void carregarColecao() throws ApplicationBusinessException {		
		colecao = this.blocoCirurgicoCadastroFacade.buscaDadosRelatorioDetalhe(unidadeCirurgica, dataInicial, dataFinal);
	}
	
	public String print()throws JRException, IOException, DocumentException {
		try {
			carregarColecao();
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));			
		return RESUMO_CIRURGIAS_PDF;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
	}
	
	public void directPrint() {
		try {
				this.carregarColecao();
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error("Exceção capturada: ", e);
		} catch (JRException e) {
			LOG.error("Exceção capturada: ", e);
		}
	}
	
	public void limparCampos(){
		this.setUnidadeCirurgica(null);
		this.setDataInicial(null);
		this.setDataFinal(null);
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}
	
	/* Suggestion "Unidade Cirurgica" */
	public List<AghUnidadesFuncionais> pesquisarUnidadeCirurgica(String objUnidade){
		return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(objUnidade);
	}
	
	/* Getters and Setters */
	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
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
