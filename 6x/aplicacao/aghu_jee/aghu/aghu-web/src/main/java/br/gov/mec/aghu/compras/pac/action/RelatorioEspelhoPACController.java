package br.gov.mec.aghu.compras.pac.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.RelatorioEspelhoPACVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatorioEspelhoPACController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioEspelhoPACController.class);
	
	private static final long serialVersionUID = 4916702392530524924L;

	private static final String RELATORIO_ESPELHO_PAC = "relatorioEspelhoPAC";
	private static final String RELATORIO_ESPELHO_PAC_PDF = "compras-relatorioEspelhoPACPdf";
	
	
	private enum RelatorioEspelhoPACControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS;
	}
	
	public enum EnumTargetRelatorioEspelhoLicitacao{
		MENSAGEM_SUCESSO_IMPRESSAO,
		LABEL_SAUDACAO_RELATORIO_ESPELHO,
		LABEL_MENSAGEM_RELATORIO_ESPELHO;
	}
	
	// Variaveis da pesquisa
	private Integer numLicitacao;	
	
	private Boolean imprimeLote;
	private Boolean imprimeValor;
	
	// indica para onde o botao voltar deve redirecionar
	private String voltarParaUrl;
	
	//armazena dados do relatório
	private Set<RelatorioEspelhoPACVO> dados = null;

	//controla a exibição do campo hora de entrega
	private Boolean dataEntregaInformada;
	
		
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	// indica para onde o botao voltar deve redirecionar
	private String voltarPara;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		this.setImprimeLote(true);
		this.setImprimeValor(true);
	}
	
	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio(){
		return "br/gov/mec/aghu/compras/report/relatorioEspelhoPAC.jasper";
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {		
		return getDados();
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		
		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		String relatorio = DominioNomeRelatorio.RELATORIO_ESPELHO.getDescricao();
		String cidade = aghuFacade.getCidade();
		String saudacao = super.getBundle().getString(EnumTargetRelatorioEspelhoLicitacao.LABEL_SAUDACAO_RELATORIO_ESPELHO.toString());
		String mensagemRelatorioEspelho = super.getBundle().getString(EnumTargetRelatorioEspelhoLicitacao.LABEL_MENSAGEM_RELATORIO_ESPELHO.toString());		
		
		params.put("nomeInstituicao", hospital);
		params.put("nomeRelatorio", relatorio);
		params.put("cidade", cidade);
		params.put("saudacao", saudacao);
		params.put("mensagem", mensagemRelatorioEspelho);
		params.put("imprimeLote", this.getImprimeLote());
		params.put("imprimeValor", this.getImprimeValor());
		
		try {
			params.put("instituicaoAbreviatura", parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_PARAMETRO_HU));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return params;
	}

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório.
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 */
	
	public String print() throws ApplicationBusinessException, JRException, IOException, DocumentException{
		String retorno = null,
			   mensagem = null;
		
		if(getNumLicitacao()!=null){
			setDados(this.pacFacade.gerarDadosRelatorioEspelhoPAC(getNumLicitacao()));
			
			if(getDados() == null || getDados().isEmpty()){
				mensagem = super.getBundle().getString(RelatorioEspelhoPACControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
				apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
				
			} else{
			
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_ESPELHO_PAC_PDF;
			}
		}
		return retorno;
	}
	
	/**
	 * Realiza a impressão do relatório
	 */
	public void impressaoDireta(){
		try {
			if(print() != null)
			{
				DocumentoJasper documento = gerarDocumento();
				getSistemaImpressao().imprimir(documento.getJasperPrint(),
						super.getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO, EnumTargetRelatorioEspelhoLicitacao.MENSAGEM_SUCESSO_IMPRESSAO.toString());
			}				
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public void limparCampos(){
		setNumLicitacao(null);
		this.setImprimeLote(true);
		this.setImprimeValor(true);
	}
	
	public String voltar() {
		if(voltarParaUrl != null){
			return voltarParaUrl;
		}
		return RELATORIO_ESPELHO_PAC;
	}

	
	public String print(Integer numLicitacao)
			throws BaseException, JRException, IOException, DocumentException {
		this.numLicitacao = numLicitacao;
		return this.print();
	}

	public Integer getNumLicitacao() {
		return numLicitacao;
	}

	public void setNumLicitacao(Integer numLicitacao) {
		this.numLicitacao = numLicitacao;
	}

	public Set<RelatorioEspelhoPACVO> getDados() {
		return dados;
	}

	public void setDados(Set<RelatorioEspelhoPACVO> dados) {
		this.dados = dados;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public Boolean getDataEntregaInformada() {
		return dataEntregaInformada;
	}

	public void setDataEntregaInformada(Boolean dataEntregaInformada) {
		this.dataEntregaInformada = dataEntregaInformada;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Boolean getImprimeLote() {
		return imprimeLote;
	}

	public void setImprimeLote(Boolean imprimeLote) {
		this.imprimeLote = imprimeLote;
	}

	public Boolean getImprimeValor() {
		return imprimeValor;
	}

	public void setImprimeValor(Boolean imprimeValor) {
		this.imprimeValor = imprimeValor;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}