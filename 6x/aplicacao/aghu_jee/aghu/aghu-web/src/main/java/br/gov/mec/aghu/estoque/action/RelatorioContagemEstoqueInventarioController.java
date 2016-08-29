package br.gov.mec.aghu.estoque.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
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
import br.gov.mec.aghu.dominio.DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque;
import br.gov.mec.aghu.dominio.DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RelatorioContagemEstoqueParaInventarioVO;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class RelatorioContagemEstoqueInventarioController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	private static final Log LOG = LogFactory.getLog(RelatorioContagemEstoqueInventarioController.class);
	
	private static final String RELATORIO_CONTAGEM_ESTOQUE_INVENTARIO     = "relatorioContagemEstoqueInventario";
	private static final String RELATORIO_CONTAGEM_ESTOQUE_INVENTARIO_PDF = "relatorioContagemEstoqueInventarioPdf";

	private static final long serialVersionUID = 8168636044788866984L;


	private enum RelatorioContagemEstoqueInventarioExceptionCode implements
	BusinessExceptionCode {
		MENSAGEM_PESQUISA_SEM_DADOS;
	}
	
	private enum EnumRelatorioContagemEstoqueInventarioControllerMessageCode{
		MENSAGEM_SUCESSO_IMPRESSAO, 
		LABEL_RELATORIO_CONTAGEM_DE_ESTOQUE_PARA_INVENTARIO, 
		LABEL_RELATORIO_CONTAGEM_DE_ESTOQUE_PARA_INVENTARIO_RODAPE		
	}

	// Filtros
	private SceAlmoxarifado almoxarifado;
	private ScoGrupoMaterial grupoMaterial;
	private ScoFornecedor fornecedor;
	private DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocaveis;
	private DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem;
	private boolean mostraSaldo;
	private Boolean gerouArquivo;
	private String fileName;
	private boolean disponivelEstoque;
	
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

	private List<RelatorioContagemEstoqueParaInventarioVO> dados = null;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

	 

		if(estocaveis == null){
			setEstocaveis(DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque.S);
			setOrdem(DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario.G);
			setMostraSaldo(true);	
			setDisponivelEstoque(true);
		}		
	
	}
	
	
	/**
	 * Recupera arquivo compilado do Jasper
	 */
	@Override
	public String recuperarArquivoRelatorio() {

		return "br/gov/mec/aghu/estoque/report/relatorioContagemEstoqueInventario.jasper";

	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioContagemEstoqueParaInventarioVO> recuperarColecao() throws ApplicationBusinessException {
		return dados;
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		StringBuffer msg = new StringBuffer();
		
		params.put("nomeInstituicao", this.cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
		
		msg.append(super.getBundle().getString(EnumRelatorioContagemEstoqueInventarioControllerMessageCode.LABEL_RELATORIO_CONTAGEM_DE_ESTOQUE_PARA_INVENTARIO.toString()))
				
		.append(" - Almox ").append(getAlmoxarifado().getSeq()).append(' ').append(getAlmoxarifado().getDescricao());
		if(getGrupoMaterial() != null){
			msg.append("  Grupo ").append(getGrupoMaterial().getCodigo()).append(' ').append(getGrupoMaterial().getDescricao());
		}
		params.put("nomeRelatorio", msg.toString());

		msg = new StringBuffer(super.getBundle().getString(EnumRelatorioContagemEstoqueInventarioControllerMessageCode.LABEL_RELATORIO_CONTAGEM_DE_ESTOQUE_PARA_INVENTARIO_RODAPE.toString()));
		params.put("nomeRelatorioRodape", msg.toString());

		params.put("mostraSaldo", isMostraSaldo());
		
		return params;
	}

	/**
	 * Método que carrega a lista de VO's para ser usado no relatório.
	 * @author Rodrigo.figueiredo
	 * @throws ApplicationBusinessException
	 * @throws DocumentException 
	 * @throws IOException 
	 * @throws JRException 
	 * @since 13/09/2011
	 */
	public String print() throws ApplicationBusinessException, JRException, IOException, DocumentException {
		String retorno = null, mensagem = null;
		Short seqAlmoxarifado = null;
		Integer codigoGrupo = null;
		
		if(getAlmoxarifado() != null){
			seqAlmoxarifado = getAlmoxarifado().getSeq();
		}
		if(getGrupoMaterial() != null){
			codigoGrupo = getGrupoMaterial().getCodigo();
		}
		
		dados = this.estoqueFacade.pesquisarDadosRelatorioContagemEstoqueInventario(seqAlmoxarifado, 
				codigoGrupo, getEstocaveis(), getOrdem(), getDisponivelEstoque());

		if (dados != null && !dados.isEmpty()) {
			retorno = RELATORIO_CONTAGEM_ESTOQUE_INVENTARIO_PDF ;
			
		} else {
			mensagem = super.getBundle().getString(RelatorioContagemEstoqueInventarioExceptionCode.MENSAGEM_PESQUISA_SEM_DADOS.toString());
			apresentarMsgNegocio(Severity.WARN, mensagem,new Object[0]);
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

	/**
	 * Dispara a impressão do relatório
	 */
	public void impressaoDireta(){ 
		try {
			if(print() != null){
				DocumentoJasper documento = gerarDocumento();
				getSistemaImpressao().imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio(Severity.INFO, 
					EnumRelatorioContagemEstoqueInventarioControllerMessageCode.MENSAGEM_SUCESSO_IMPRESSAO.toString());	
			}			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			
		}catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}

	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos(){
		setAlmoxarifado(null);
		setGrupoMaterial(null);
		setFornecedor(null);
		setEstocaveis(null);
		setMostraSaldo(false);
		setGerouArquivo(false);
		setDisponivelEstoque(true);	
	}
	
	/**
	 * Gera o arquivo CSV para o relatório
	 */
	public void gerarCSV() {
		Short seqAlmoxarifado = null;
		Integer codigoGrupo = null;
		
		if(getAlmoxarifado() != null){
			seqAlmoxarifado = getAlmoxarifado().getSeq();
		}
		if(getGrupoMaterial() != null){
			codigoGrupo = getGrupoMaterial().getCodigo();
		}
		try {
			fileName = this.estoqueFacade.geraCSVRelatorioContagemEstoqueInventario(seqAlmoxarifado, 
				codigoGrupo, getEstocaveis(), getOrdem(), getDisponivelEstoque(), mostraSaldo);
			setGerouArquivo(true);
			this.dispararDownload();
			
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
				download(fileName);
				setGerouArquivo(false);
				fileName = null;				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}
	
	/**
	 * Pesquisa do suggestion de grupo de material
	 */
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorCodigoDescricao(parametro);
	}

	/**
	 * Pesquisa do suggestion de almoxarifado
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosPorCodigoDescricao(
			String parametro) {
		return this.estoqueFacade.pesquisarAlmoxarifadosPorCodigoDescricao(parametro);
	}
	
	public String voltar(){
		return RELATORIO_CONTAGEM_ESTOQUE_INVENTARIO; 
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

	public void setEstocaveis(DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque estocaveis) {
		this.estocaveis = estocaveis;
	}

	public DominioFiltroEstocavelRelatorioMensalPosicaoFinalEstoque getEstocaveis() {
		return estocaveis;
	}

	public void setMostraSaldo(boolean mostraSaldo) {
		this.mostraSaldo = mostraSaldo;
	}

	public boolean isMostraSaldo() {
		return mostraSaldo;
	}
	
	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public void setOrdem(DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario ordem) {
		this.ordem = ordem;
	}

	public DominioFiltroOrdemlRelatorioContagemEstoqueParaInventario getOrdem() {
		return ordem;
	}
	
	public void setDisponivelEstoque(Boolean disponivelEstoque) {
		this.disponivelEstoque = disponivelEstoque;
	}

	public Boolean getDisponivelEstoque() {
		return disponivelEstoque;
	}
}
