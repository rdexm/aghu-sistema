package br.gov.mec.aghu.estoque.action;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioAgruparRelMensal;
import br.gov.mec.aghu.dominio.DominioMes;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.faturamento.action.RelatorioResumoAIHEmLotePdfController.RelatorioResumoAIHEmLotePdfExceptionCode;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.util.AghuEnumUtils;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RotinaMensalMateriaisController extends ActionReport{

	private static final String END_NL = "> \n";
	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -2936007996959704915L;
	private static final Log LOG = LogFactory.getLog(RotinaMensalMateriaisController.class);
	private static final String ROTINA_MENSAL_MATERIAIS = "rotinaMensalMateriais";
	private static final String ROTINA_MENSAL_MATERIAIS_PDF = "rotinaMensalMateriaisPdf";
	private static final String RELATORIO_MENSAL_MATERIAL_PDF = "relatorioMensalMaterialPdf";


	public enum EnumTargetRotinaMensalMateriais{
		TITLE_RELATORIO_AJUSTE_ESTOQUE, 
		TITLE_RELATORIO_MATERIAIS_CLASSIFICACAO_ABC,
		TITLE_RELATORIO_MATERIAIS_CLASSIFICACAO_ABC_RODAPE,
		TITLE_VISUALIZAR_RELATORIO_ROTINA_MENSAL_MATERIAIS,
		TITLE_VISUALIZAR_RELATORIO_AJUSTE_ESTOQUE,
		TITLE_VISUALIZAR_RELATORIO_AJUSTE_ESTOQUE_RODAPE,
		TITLE_VISUALIZAR_RELATORIO_MATERIAIS_CLASSIFICACAO_ABC,		
		MENSAGEM_SUCESSO_IMPRESSAO, 
		TITLE_VISUALIZAR_RELATORIO_MENSAL_MATERIAL,
		TITLE_VISUALIZAR_RELATORIO_MENSAL_MATERIAL_RODAPE,
		TITLE_RELATORIO_MENSAL_MATERIAL;
	}

	private enum RotinaMensalMateriaisControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS, ERRO_COMPETENCIA_OBRIGATORIA, ERRO_RELATORIO_OBRIGATORIO;
	}

	private static final Set<DominioNomeRelatorio> relatoriosCompetenciaObrigatoria = new HashSet<DominioNomeRelatorio>();
	private static final Set<DominioNomeRelatorio> relatoriosFornecedorDesabilitado = new HashSet<DominioNomeRelatorio>();
	private static final Set<DominioNomeRelatorio> relatoriosResumoDesabilitado = new HashSet<DominioNomeRelatorio>();

	static{
		relatoriosCompetenciaObrigatoria.add(DominioNomeRelatorio.RELATORIO_AJUSTE_ESTOQUE);
		relatoriosCompetenciaObrigatoria.add(DominioNomeRelatorio.RELATORIO_CLASSIFICACAO_ABC);
		relatoriosCompetenciaObrigatoria.add(DominioNomeRelatorio.RELATORIO_RESUMO_GERAL_MATERIAIS);

		relatoriosFornecedorDesabilitado.add(DominioNomeRelatorio.RELATORIO_AJUSTE_ESTOQUE);
		relatoriosFornecedorDesabilitado.add(DominioNomeRelatorio.RELATORIO_CLASSIFICACAO_ABC);		

		relatoriosResumoDesabilitado.add(DominioNomeRelatorio.RELATORIO_AJUSTE_ESTOQUE);
		relatoriosResumoDesabilitado.add(DominioNomeRelatorio.RELATORIO_CLASSIFICACAO_ABC);		
	}

	//relatório selecionado
	private DominioNomeRelatorio relatorio;

	//Atributos de filtro de relatórios
	private MovimentoMaterialVO movimentoMaterial;
	private ScoFornecedor fornecedor;
	private Boolean resumo;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	//dados do relatório de Ajuste de Estoque
	private List<?> dadosRelatorio = null;

	//nome do arquivo do relatório
	private String fileName;

	//indica se houve geração do arquivo do relatório
	private Boolean gerouArquivo;

	private String titlePdfView;

	private Map<String, Object> parametrosEspecificos;

	private String nomeRelatorio;

	private String nomeRelatorioRodape;

	private String nomeArquivoRelatorio;
	
	private Boolean disableButtonCsv = Boolean.FALSE;
	
	private DominioAgruparRelMensal agrupar = DominioAgruparRelMensal.GRUPO_MATERIAL;


	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String inicio()  {

		// Gera o PDF
		try {
			DocumentoJasper documento = gerarDocumento();

			final File file = File.createTempFile(DominioNomeRelatorio.RELATORIO_MENSAL_MATERIAL.toString(),".pdf");
			final FileOutputStream out = new FileOutputStream(file);

			out.write(documento.getPdfByteArray(false));
			out.flush();
			out.close();

			setGerouArquivo(Boolean.TRUE);
			
			setAgrupar(DominioAgruparRelMensal.GRUPO_MATERIAL);

			fileName = file.getAbsolutePath();
			return this.voltar();


		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(
					RelatorioResumoAIHEmLotePdfExceptionCode.ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE));
		}

		return null;
	}
	
	
	public String voltar(){
		return ROTINA_MENSAL_MATERIAIS;
	}


	@Override
	public String recuperarArquivoRelatorio() {
		return getNomeArquivoRelatorio();
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return dadosRelatorio;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("nomeInstituicao", hospital);
		params.put("nomeRelatorio", getNomeRelatorio());		
		params.put("nomeRelatorioRodape", getNomeRelatorioRodape());
		
		if(agrupar == null || agrupar.equals(DominioAgruparRelMensal.GRUPO_MATERIAL)){
			params.put("coluna1", "Cod");	
		}else{
			params.put("coluna1", "SIAFI");
		}
				
		if(getParametrosEspecificos() != null){
			params.putAll(getParametrosEspecificos());
		}
		return params;
	}

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório.
	 * @throws BaseException 
	 */
	public String print()throws JRException, IOException, DocumentException, BaseException{		
		Boolean dataCompetenciaOuRelatorioNaoInformados = verificarDataCompetenciaOuRelatorioNaoInformados();

		if(!dataCompetenciaOuRelatorioNaoInformados){
			switch(relatorio){
			case RELATORIO_RESUMO_GERAL_MATERIAIS:
				dadosRelatorio = pesquisarDadosRelatorioMensalMaterial(getAgrupar());
				break;
			case RELATORIO_AJUSTE_ESTOQUE:
				dadosRelatorio = pesquisarDadosRelatorioAjusteEstoque();
				break;
			case RELATORIO_CLASSIFICACAO_ABC:
				dadosRelatorio = pesquisarDadosRelatorioMateriaisClassificacaoABC();
				break;
			}			
			if(dadosRelatorio == null || dadosRelatorio.isEmpty()){
				exibirMensagemPesquisaSemDados();			
			}else{
			
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return ROTINA_MENSAL_MATERIAIS_PDF;
			}		
		}		

		return null;
	}

	
	/**
	 * 
	 */
	public void verificarRelatorioSelecionado() {
		if(relatorio.equals(DominioNomeRelatorio.RELATORIO_RESUMO_GERAL_MATERIAIS)){
			setDisableButtonCsv(Boolean.TRUE);
		}else{
			setDisableButtonCsv(Boolean.FALSE);
		}
		if(DominioNomeRelatorio.RELATORIO_CLASSIFICACAO_ABC.equals(relatorio)) {
			setFornecedor(null);
		}
	}

	/**
	 * Pesquisa referente ao relatório da estória #6638
	 * @return Target para o relatório
	 *  
	 */
	private List<?> pesquisarDadosRelatorioMateriaisClassificacaoABC() throws ApplicationBusinessException{
		HashMap<String, Object> params = new HashMap<String, Object>(); 
		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
		Calendar c = Calendar.getInstance();

		setTitlePdfView(this.getBundle().getString(EnumTargetRotinaMensalMateriais
				.TITLE_VISUALIZAR_RELATORIO_MATERIAIS_CLASSIFICACAO_ABC.toString()));
		setNomeRelatorio(this.getBundle().getString(EnumTargetRotinaMensalMateriais.TITLE_RELATORIO_MATERIAIS_CLASSIFICACAO_ABC.toString()));
		setNomeRelatorioRodape(this.getBundle().getString(EnumTargetRotinaMensalMateriais.TITLE_RELATORIO_MATERIAIS_CLASSIFICACAO_ABC_RODAPE.toString()));
		setNomeArquivoRelatorio("br/gov/mec/aghu/estoque/report/relatorioMensalMateriaisClassificacaoAbc.jasper");

		c.setTime(getMovimentoMaterial().getCompetencia());
		c.add(Calendar.MONTH, -2);
		params.put("TrimestreReferencia", "Consumo Médio de " + sdf.format(c.getTime()).toUpperCase() + " a " + sdf.format(getMovimentoMaterial().getCompetencia()).toUpperCase());
		setParametrosEspecificos(params);

		return this.estoqueFacade.pesquisarDadosRelatorioMensalMateriaisClassificacaoAbc(getMovimentoMaterial().getCompetencia());
	}

	/**
	 * Pesquisa referente ao relatório da estória #6639
	 * @return Target para o relatório
	 *  
	 */
	private List<?> pesquisarDadosRelatorioAjusteEstoque() throws ApplicationBusinessException{
		List<String> siglasTipoMovimento = obterListaSiglas();
		HashMap<String, Object> params = new HashMap<String, Object>(); 
		setTitlePdfView(this.getBundle().getString(EnumTargetRotinaMensalMateriais.TITLE_VISUALIZAR_RELATORIO_AJUSTE_ESTOQUE.toString()));
		setNomeRelatorio(this.getBundle().getString(EnumTargetRotinaMensalMateriais.TITLE_VISUALIZAR_RELATORIO_AJUSTE_ESTOQUE.toString()));
		setNomeArquivoRelatorio("br/gov/mec/aghu/estoque/report/relatorioAjusteEstoque.jasper");
		params.put("subReport", Thread.currentThread().getContextClassLoader().getResource("br/gov/mec/aghu/estoque/report/subRelatorioAjusteEstoque.jasper"));
		setParametrosEspecificos(params);
		setNomeRelatorioRodape(this.getBundle().getString(EnumTargetRotinaMensalMateriais.TITLE_VISUALIZAR_RELATORIO_AJUSTE_ESTOQUE_RODAPE.toString()));
		return this.estoqueFacade.pesquisarDadosRelatorioAjusteEstoque(getMovimentoMaterial().getCompetencia(), siglasTipoMovimento);
	}

	/**
	 * Pesquisa referente ao relatório da estória #6639
	 * @return Target para o relatório
	 *  
	 */
	private List<?> pesquisarDadosRelatorioMensalMaterial(DominioAgruparRelMensal agrupar) throws BaseException{

		DominioMes mesCompetencia = AghuEnumUtils.retornaMes(getMovimentoMaterial().getCompetencia());
		
		HashMap<String, Object> params = new HashMap<String, Object>(); 
		setTitlePdfView(this.getBundle().getString(EnumTargetRotinaMensalMateriais
				.TITLE_VISUALIZAR_RELATORIO_MENSAL_MATERIAL.toString()));
		setNomeRelatorio(this.getBundle().getString(EnumTargetRotinaMensalMateriais.
				TITLE_RELATORIO_MENSAL_MATERIAL.toString()) + mesCompetencia.toString());
		setNomeArquivoRelatorio("br/gov/mec/aghu/estoque/report/relatorioMensalMateriais.jasper");
		setParametrosEspecificos(params);
		setNomeRelatorioRodape(this.getBundle().getString(EnumTargetRotinaMensalMateriais.TITLE_VISUALIZAR_RELATORIO_MENSAL_MATERIAL_RODAPE.toString()));
		List<?> lista = this.estoqueFacade.pesquisarDadosRelatorioMensalMaterial(getMovimentoMaterial().getCompetencia(), agrupar);
	//	converteParaXml(lista);
		return lista;
	}


	/**
	 * UTILIZADO PARA TESTES NO IREPORT
	 * @param source
	 */
	public void converteParaXml(List<? extends Object> source) {

		try {               
			StringBuffer xml = new StringBuffer("\n <?xml version=\"1.0\" encoding=\"UTF-8\"?> \n <aghu> \n");
			xml.append(montarXml(source))
			.append("</aghu>");
			LOG.info(xml);
		} catch (IllegalArgumentException e) {
			LOG.error(e.getMessage(), e);
		} catch (IllegalAccessException e) {
			LOG.error(e.getMessage(), e);
		}

	}



	private String montarXml(List<? extends Object> source)
	throws IllegalAccessException {
		StringBuffer xml = new StringBuffer();
		if(source != null && !source.isEmpty()){   
			Field[] fields = source.get(0).getClass().getDeclaredFields();
			for(Object vo : source){
				xml.append("    <").append(vo.getClass().getSimpleName()).append(END_NL);
				for(Field field : fields){
					field.setAccessible(true);
					String nomeField = field.getName();


					Class<?> typeField = field.getType();
					if(typeField.getName().contains("List")){
						List<Object> list = (List<Object>)field.get(vo);

						xml.append("        <").append(nomeField).append(END_NL);
						xml.append(montarXml(list));
						xml.append("\n        </").append(nomeField).append(END_NL);

					}else{
						Object valor = field.get(vo);
						xml.append("        <").append(nomeField).append('>');
						xml.append(valor);
						xml.append("</").append(nomeField).append(END_NL);
					}


				}
				xml.append("    </").append(vo.getClass().getSimpleName()).append(END_NL);
			}
		}
		return xml.toString();
	}



	/**
	 * Renderiza o PDF.
	 * 
	 * @param out
	 * @throws IOException
	 * @throws JRException
	 * @author marzon.castilho
	 * @throws DocumentException 
	 * @throws ApplicationBusinessException 
	 * @since 31/05/2011
	 */
	public StreamedContent getRenderPdf() throws IOException, 
	JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
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
				apresentarMsgNegocio(Severity.INFO, EnumTargetRotinaMensalMateriais.MENSAGEM_SUCESSO_IMPRESSAO.toString());
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
		String file=null;
		String fileName=null;
		Boolean dataCompetenciaOuRelatorioNaoInformados = verificarDataCompetenciaOuRelatorioNaoInformados();
		if(!dataCompetenciaOuRelatorioNaoInformados){
			try {
				switch(relatorio){
				case RELATORIO_AJUSTE_ESTOQUE:
					List<String> siglasTipoMovimento = obterListaSiglas();
					file = this.estoqueFacade.gerarCSVRelatorioAjusteEstoque(getMovimentoMaterial().getCompetencia(), siglasTipoMovimento);
					fileName =  this.estoqueFacade.nameHeaderEfetuarDownloadCSVRelatorioAjusteEstoque(movimentoMaterial.getCompetencia());
					break;
				case RELATORIO_CLASSIFICACAO_ABC:
					file = this.estoqueFacade.gerarCSVRelatorioMensalMateriaisClassificacaoAbc(getMovimentoMaterial().getCompetencia());
					fileName =  this.estoqueFacade.nameHeaderEfetuarDownloadCSVMensalMateriaisClassificacaoAbc(movimentoMaterial.getCompetencia());
					break;
				}
				this.media = new DefaultStreamedContent(new FileInputStream(file),"text/csv", fileName);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			} 
		}
	}

	/**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	public void dispararDownload(){
		if(fileName != null){
			try {
				switch(relatorio){
				case RELATORIO_RESUMO_GERAL_MATERIAIS:
					this.download(fileName);
					break;
				case RELATORIO_AJUSTE_ESTOQUE:
					this.download(fileName, this.estoqueFacade.nameHeaderEfetuarDownloadCSVRelatorioAjusteEstoque(movimentoMaterial.getCompetencia()));
					break;
				case RELATORIO_CLASSIFICACAO_ABC:
					this.download(fileName, this.estoqueFacade.nameHeaderEfetuarDownloadCSVMensalMateriaisClassificacaoAbc(movimentoMaterial.getCompetencia()));
					break;
				}
				setGerouArquivo(Boolean.FALSE);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}

	/**
	 * Método responsável por verificar se a data de competencia e/ou 
	 * o relatório foi(ram) informado(s) para a realização da pesquisa.
	 * Utilizado, independente do relatório a ser escolhido.
	 * @return
	 */
	private Boolean verificarDataCompetenciaOuRelatorioNaoInformados(){
		String mensagemErro = null;
		Boolean erro = Boolean.FALSE;
		String nomeRelatorioSelecionado = null;

		if(relatorio==null){
			mensagemErro = this.getBundle().getString(RotinaMensalMateriaisControllerExceptionCode
					.ERRO_RELATORIO_OBRIGATORIO.toString());
			apresentarMsgNegocio(Severity.ERROR,
					mensagemErro);
			erro = Boolean.TRUE;
		}else{
			switch(relatorio){
			case RELATORIO_RESUMO_GERAL_MATERIAIS:
				nomeRelatorioSelecionado = EnumTargetRotinaMensalMateriais.TITLE_VISUALIZAR_RELATORIO_MENSAL_MATERIAL.toString();
				break;
			case RELATORIO_AJUSTE_ESTOQUE:
				nomeRelatorioSelecionado = EnumTargetRotinaMensalMateriais.TITLE_VISUALIZAR_RELATORIO_AJUSTE_ESTOQUE.toString();
				break;
			case RELATORIO_CLASSIFICACAO_ABC:
				nomeRelatorioSelecionado = EnumTargetRotinaMensalMateriais.TITLE_VISUALIZAR_RELATORIO_MATERIAIS_CLASSIFICACAO_ABC.toString();
				break;				
			}
			if((movimentoMaterial==null || movimentoMaterial.getCompetencia()== null) && nomeRelatorioSelecionado != null){
				mensagemErro = this.getBundle().getString(RotinaMensalMateriaisControllerExceptionCode
						.ERRO_COMPETENCIA_OBRIGATORIA.toString());
				apresentarMsgNegocio(Severity.ERROR,
						mensagemErro, new Object[]{this.getBundle().getString(nomeRelatorioSelecionado)});
				erro = Boolean.TRUE;
			}				
		}

		return erro;
	}


	public List<MovimentoMaterialVO> pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(String parametro){
		List<MovimentoMaterialVO> lista = null;
		try {
			lista = this.estoqueFacade.pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(parametro);
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}		
		return lista;
	}

	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocialNomeFantasia(String parametro){
		return this.comprasFacade.pesquisarFornecedoresPorNumeroRazaoSocialNomeFantasia(parametro, 0, 100);
	}

	/**
	 * Método que restorna as siglas utilizadas para a pesquisa
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<String> obterListaSiglas() throws ApplicationBusinessException{
		List<String> siglasTipoMovimento = new ArrayList<String>();
		siglasTipoMovimento.add(this.estoqueFacade.obterSiglaTipoMovimento(AghuParametrosEnum.P_SIGLA_TIPO_MOVIMENTO_AJUSTE_MAIS));
		siglasTipoMovimento.add(this.estoqueFacade.obterSiglaTipoMovimento(AghuParametrosEnum.P_SIGLA_TIPO_MOVIMENTO_AJUSTE_MENOS));
		return siglasTipoMovimento;
	}

	private void exibirMensagemPesquisaSemDados(){
		String mensagem = this.getBundle().getString(RotinaMensalMateriaisControllerExceptionCode
				.MENSAGEM_PESQUISA_SEM_DADOS.toString());
		apresentarMsgNegocio(Severity.WARN, mensagem, new Object[0]);
	}

	/**
	 * Retorna os relatórios a serem apresentados na tela
	 * @return
	 */
	public DominioNomeRelatorio[] listarRelatorios() {
		return new DominioNomeRelatorio[] {
				DominioNomeRelatorio.RELATORIO_RESUMO_GERAL_MATERIAIS,
				DominioNomeRelatorio.RELATORIO_RESUMO_GERAL_MATERIAIS_CONSIG,
				DominioNomeRelatorio.RELATORIO_CONSIGNADOS_FORN,
				DominioNomeRelatorio.RELATORIO_EXTRATO_CONTA_FORN,
				DominioNomeRelatorio.RELATORIO_CLASSIFICACAO_ABC,
				DominioNomeRelatorio.RELATORIO_AJUSTE_ESTOQUE,
				DominioNomeRelatorio.RELATORIO_QUANTIDADE_MOV_TIPO,
				DominioNomeRelatorio.RELATORIO_GERAL_MATERIAIS_TERCEIROS,
				DominioNomeRelatorio.RELATORIO_RESUMO_MATERIAIS_ESTOCAVEIS,
				DominioNomeRelatorio.RELATORIO_CONSUMO_ANALITICO,
				DominioNomeRelatorio.RELATORIO_CONSUMO_POR_CONTA_CC_GRUPO,
				DominioNomeRelatorio.RELATORIO_CONSUMO_SINTETICO,
				DominioNomeRelatorio.RELATORIO_PRODUCAO_GRAFICA,
				DominioNomeRelatorio.RELATORIO_PRODUCAO_ROUPARIA,
				DominioNomeRelatorio.RELATORIO_PRODUCAO_FARMACIA
		};
	}


	public void limparCampos(){
		setFornecedor(null);
		setResumo(null);
		setMovimentoMaterial(null);
		setRelatorio(null);
		setGerouArquivo(Boolean.FALSE);
		setDisableButtonCsv(Boolean.FALSE);
	}

	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public Boolean isCompetenciaObrigatoria(){
		Boolean obrigatoria = Boolean.FALSE;
		if(getRelatorio() != null){
			obrigatoria = relatoriosCompetenciaObrigatoria.contains(getRelatorio());			
		}
		return obrigatoria;
	}

	public Boolean isDesabilitarFornecedor(){
		Boolean desabilitar = Boolean.FALSE;
		if(getRelatorio() != null){
			desabilitar = relatoriosFornecedorDesabilitado.contains(getRelatorio());			
		}
		return desabilitar;
	}

	public Boolean isDesabilitarResumo(){
		Boolean desabilitar = Boolean.FALSE;
		if(getRelatorio() != null){
			desabilitar = relatoriosResumoDesabilitado.contains(getRelatorio());			
		}
		return desabilitar;
	}
	
	public Boolean isDesabilitarResumoGeralMateriais(){
		Boolean desabilitar = Boolean.FALSE;
		if(getRelatorio() != null && !getRelatorio().equals(DominioNomeRelatorio.RELATORIO_RESUMO_GERAL_MATERIAIS)){
			desabilitar = Boolean.TRUE;		
		}
		return desabilitar;
	}
	

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public DominioNomeRelatorio getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(DominioNomeRelatorio relatorio) {
		this.relatorio = relatorio;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public Boolean getResumo() {
		return resumo;
	}

	public void setResumo(Boolean resumo) {
		this.resumo = resumo;
	}

	public MovimentoMaterialVO getMovimentoMaterial() {
		return movimentoMaterial;
	}

	public void setMovimentoMaterial(MovimentoMaterialVO movimentoMaterial) {
		this.movimentoMaterial = movimentoMaterial;
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
		String titlePdfViewBase = this.getBundle().getString(EnumTargetRotinaMensalMateriais
				.TITLE_VISUALIZAR_RELATORIO_ROTINA_MENSAL_MATERIAIS.toString());
		this.titlePdfView = titlePdfViewBase + " - " + titlePdfView;
	}

	public String imprimirRelatorioPdfFile(){	

		try{
			
			dadosRelatorio = pesquisarDadosRelatorioMensalMaterial(this.agrupar);

		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(new BaseException(
					RelatorioResumoAIHEmLotePdfExceptionCode.ERRO_AO_GERAR_PDF_RELATORIO_RESUMO_AIH_EM_LOTE));
		}

		return RELATORIO_MENSAL_MATERIAL_PDF;		
	}

	
	public Boolean getDisableButtonCsv() {
		return disableButtonCsv;
	}


	public void setDisableButtonCsv(Boolean disableButtonCsv) {
		this.disableButtonCsv = disableButtonCsv;
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

	public Map<String, Object> getParametrosEspecificos() {
		return parametrosEspecificos;
	}

	public void setParametrosEspecificos(Map<String, Object> parametrosEspecificos) {
		this.parametrosEspecificos = parametrosEspecificos;
	}
	
	public DominioAgruparRelMensal getAgrupar() {
		return agrupar;
	}

	public void setAgrupar(DominioAgruparRelMensal agrupar) {
		this.agrupar = agrupar;
	}
}