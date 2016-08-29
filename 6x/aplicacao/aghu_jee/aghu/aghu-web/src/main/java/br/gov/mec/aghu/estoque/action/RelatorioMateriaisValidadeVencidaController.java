package br.gov.mec.aghu.estoque.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RelatorioMateriaisValidadeVencidaVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioMateriaisValidadeVencidaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioMateriaisValidadeVencidaController.class);

	private static final long serialVersionUID = 3885346716364239685L;

	private static final String RELATORIO_MATERIAIS_VALIDADE_VENCIDA     = "relatorioMateriaisValidadeVencida";
	private static final String RELATORIO_MATERIAIS_VALIDADE_VENCIDA_PDF = "relatorioMateriaisValidadeVencidaPdf";

	private enum RelatorioMateriaisValidadeVencidaControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS;
	}
	
	public enum EnumRelatorioMateriaisValidadeVencidaControllerMessageCode {
		LABEL_NOME_INSTITUICAO, 
		MENSAGEM_SUCESSO_IMPRESSAO,
		TITLE_RELATORIO_MATERIAL_VALIDADE_VENCIDA_OU_A_VENCER,
		TITLE_RELATORIO_MATERIAL_VALIDADE_VENCIDA_OU_A_VENCER_RODAPE;
	}
	
	// Filtros
	private SceAlmoxarifado almoxarifado;
	private ScoGrupoMaterial grupoMaterial;
	private Date dataInicial;
	private Date dataFinal;
	private ScoFornecedor fornecedor;
	private Boolean gerouArquivo;
	private String fileName;
	
	// Variável utilizada caso o botão voltar tenha que voltar para mais de uma
	// tela
	private String origem;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private List<RelatorioMateriaisValidadeVencidaVO> dados = null;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/estoque/report/relatorioMateriaisValidadeVencida.jasper";
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioMateriaisValidadeVencidaVO> recuperarColecao() throws ApplicationBusinessException {
		return dados;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		String msg = "";
		
		params.put("nomeInstituicao", this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());

		msg = super.getBundle().getString(EnumRelatorioMateriaisValidadeVencidaControllerMessageCode.TITLE_RELATORIO_MATERIAL_VALIDADE_VENCIDA_OU_A_VENCER.toString());
		params.put("nomeRelatorio", msg);

		msg =  super.getBundle().getString(EnumRelatorioMateriaisValidadeVencidaControllerMessageCode.TITLE_RELATORIO_MATERIAL_VALIDADE_VENCIDA_OU_A_VENCER_RODAPE.toString());
		params.put("nomeRelatorioRodape", msg);
		
		if(getDataInicial() != null && getDataFinal() != null){
			params.put("dataInicialEFinal", DateUtil.obterDataFormatada(getDataInicial(), DateConstants.DATE_PATTERN_DDMMYYYY) + " a " +
											DateUtil.obterDataFormatada(getDataFinal(), DateConstants.DATE_PATTERN_DDMMYYYY) );
		}else{
			params.put("dataInicialEFinal", "Até " + DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY) );
		}
		
		return params;
	}

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório.
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 */
	public String print() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		String retorno = null;
		Short seqAlmoxarifado = null;
		Integer codigoMaterial = null,
				numeroFornecedor = null;
		
		if(getAlmoxarifado() != null){
			seqAlmoxarifado = getAlmoxarifado().getSeq();
		}
		if(getGrupoMaterial() != null){
			codigoMaterial = getGrupoMaterial().getCodigo();
		}
		if(getFornecedor() != null){
			numeroFornecedor = getFornecedor().getNumero();
		}
		
		try{
			dados = this.estoqueFacade.pesquisarDadosRelatorioMaterialValidadeVencida(seqAlmoxarifado, codigoMaterial, dataInicial, dataFinal, numeroFornecedor);
			
			if (dados != null && !dados.isEmpty()) {
				retorno = RELATORIO_MATERIAIS_VALIDADE_VENCIDA_PDF;
			} else {
				apresentarMsgNegocio(Severity.WARN, RelatorioMateriaisValidadeVencidaControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
			}
		}catch(ApplicationBusinessException ane){
			super.apresentarExcecaoNegocio(ane);
		}		

	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return retorno;
	}

	/**
	 * Renderiza o PDF.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, 
																JRException, SystemException,	DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public void limparCampos(){
		setAlmoxarifado(null);
		setGrupoMaterial(null);
		setDataInicial(null);
		setDataFinal(null);
		setFornecedor(null);
	}
	
	public void impressaoDireta(){ 
		try {
			if(print() != null){
				DocumentoJasper documento = gerarDocumento();
				getSistemaImpressao().imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, EnumRelatorioMateriaisValidadeVencidaControllerMessageCode.MENSAGEM_SUCESSO_IMPRESSAO.toString());	
			}			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}

	/**
	 * Gera o arquivo CSV para o relatório
	 */
	public void gerarCSV() {
		Short seqAlmoxarifado = null;
		Integer codigoGrupo = null,
				numeroFornecedor = null;
		
		if(getAlmoxarifado() != null){
			seqAlmoxarifado = getAlmoxarifado().getSeq();
		}
		if(getGrupoMaterial() != null){
			codigoGrupo= getGrupoMaterial().getCodigo();
		}
		if(getFornecedor() != null){
			numeroFornecedor = getFornecedor().getNumero();
		}
		try {
			fileName = this.estoqueFacade.gerarCSVRelatorioRelatorioMaterialValidadeVencida(seqAlmoxarifado, codigoGrupo, dataInicial, dataFinal, numeroFornecedor);
			
			if(fileName == null){
				apresentarMsgNegocio(Severity.WARN, RelatorioMateriaisValidadeVencidaControllerExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
			}else{
				setGerouArquivo(Boolean.TRUE);	
				this.dispararDownload();
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		} 
	}
	
	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload(){
		if(fileName != null){
			try {
				this.download(fileName);
				setGerouArquivo(Boolean.FALSE);
				fileName = null;		
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
	
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(parametro);
	}
	
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosPorCodigoDescricao(
			String parametro) {
		return this.estoqueFacade.pesquisarAlmoxarifadosPorCodigoDescricao(parametro);
	}

	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(String objPesquisa) {
		return this.comprasFacade.pesquisarFornecedoresPorNumeroRazaoSocial(objPesquisa);
	}
	
	public String voltar(){
		return RELATORIO_MATERIAIS_VALIDADE_VENCIDA;
	}
	
	
	
	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public String getOrigem() {
		return origem;
	}
	
	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
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

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
}