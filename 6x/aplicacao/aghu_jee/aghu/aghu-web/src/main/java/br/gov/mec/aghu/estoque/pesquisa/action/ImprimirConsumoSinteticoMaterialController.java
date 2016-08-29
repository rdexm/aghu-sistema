package br.gov.mec.aghu.estoque.pesquisa.action;

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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioEstocavelConsumoSinteticoMaterial;
import br.gov.mec.aghu.dominio.DominioOrdenacaoConsumoSinteticoMaterial;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.estoque.vo.RelatorioConsumoSinteticoMaterialVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.VScoClasMaterial;
import net.sf.jasperreports.engine.JRException;


public class ImprimirConsumoSinteticoMaterialController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	private static final Log LOG = LogFactory.getLog(ImprimirConsumoSinteticoMaterialController.class);

	private static final long serialVersionUID = 6913533971923624046L;

	private static final String IMPRIMIR_CONSUMO_SINTETICO_MATERIAL     = "imprimirConsumoSinteticoMaterial";
	private static final String IMPRIMIR_CONSUMO_SINTETICO_MATERIAL_PDF = "imprimirConsumoSinteticoMaterialPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	/* Dados que serão impressos em PDF. */
	private List<RelatorioConsumoSinteticoMaterialVO> colecao = new ArrayList<RelatorioConsumoSinteticoMaterialVO>();

	private ScoFornecedor fornecedor;

	private AfaTipoUsoMdto tipoUsoMdto;

	private SceAlmoxarifado almoxarifado;

	/* Filtro */
	private DominioEstocavelConsumoSinteticoMaterial estocavel;
	private DominioOrdenacaoConsumoSinteticoMaterial ordenacao;
	private MovimentoMaterialVO mvtodataCompetencia;
	private ScoGrupoMaterial grupo;
	private FccCentroCustos centroCustos;
	private VScoClasMaterial classificacaoMaterial;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Pesquisa do relatório de consumo sintético de material considerando a classificação de materiais
	 */
	private void pesquisarRelatorioConsumoSinteticoMaterial() {

		Integer cctCodigo = this.centroCustos != null ? this.centroCustos.getCodigo() : null;
		Short almSeq = this.almoxarifado != null ? this.almoxarifado.getSeq() : null;
		Long cn5Numero = this.classificacaoMaterial != null ? this.classificacaoMaterial.getId().getNumero() : null;

		colecao = estoqueFacade.pesquisarRelatorioConsumoSinteticoMaterial(cctCodigo, almSeq, this.estocavel, cn5Numero,
					 												       mvtodataCompetencia.getCompetencia(), ordenacao, this.grupo);
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 */
	public String print()throws JRException, IOException, DocumentException, BaseException {
		this.pesquisarRelatorioConsumoSinteticoMaterial();
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return IMPRIMIR_CONSUMO_SINTETICO_MATERIAL_PDF;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		this.pesquisarRelatorioConsumoSinteticoMaterial();

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String voltar(){
		return IMPRIMIR_CONSUMO_SINTETICO_MATERIAL;
	}

	@Override
	public Collection<RelatorioConsumoSinteticoMaterialVO> recuperarColecao() throws ApplicationBusinessException {
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
		params.put("nomeRelatorio", "SCER_CONS_SINTET_MAT");
		
		if (classificacaoMaterial != null) {
			params.put("classificacao", classificacaoMaterial.getId().getDescricao());
		} else {
			params.put("classificacao", "");
		}
		
		if (grupo != null) {
			params.put("grupoMaterial", grupo.getDescricao());
		} else {
			params.put("grupoMaterial", "");
		}
		
		params.put("totalRegistros", colecao.size()-1);

		StringBuffer tituloRelatorio = new StringBuffer(50);
		tituloRelatorio.append("Consumo Sintético de Materiais em " + new SimpleDateFormat("MMMM/yyyy", new Locale("pt", "BR")).format(mvtodataCompetencia.getCompetencia()));
		
		if(this.classificacaoMaterial != null){
			params.put("classificacao", this.classificacaoMaterial.getId().getNumero() + " - " + this.classificacaoMaterial.getId().getDescricao());
		}
		
		params.put("tituloRelatorio", tituloRelatorio.toString());

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/estoque/report/imprimirConsumoSinteticoMat.jasper";
	}

	public void limparPesquisa() {
		this.mvtodataCompetencia = null;
		this.grupo = null;
		this.estocavel = null;
		this.ordenacao = null;
		this.centroCustos = null;
		this.classificacaoMaterial = null;
	}

	/**
	 * Método que realiza a pesquisa de competencias de estoque geral, por mes e ano.
	 */
	public List<MovimentoMaterialVO> pesquisarDatasCompetencias(String paramPesquisa) {
		List<MovimentoMaterialVO> lista = null;
		try {
			lista = estoqueFacade.pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(paramPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return lista;
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String parametro) throws BaseException {
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricao(parametro),pesquisarCentroCustoCount(parametro));
	}

	public Integer pesquisarCentroCustoCount(String param) {
		return centroCustoFacade.pesquisarCentroCustosAtivosOrdemDescricaoCount(param);
	}


	public void limparDadosCentroCusto() {
		this.setCentroCustos(null);
	}


	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, 
																JRException, SystemException,	DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/**
	 * Obtem lista para sugestion box de classificação de material de NÍVEL 5
	 */
	public List<VScoClasMaterial> obterClassificacaoMaterial(String param) {
		return this.comprasFacade.pesquisarClassificacaoMaterialTransferenciaAutoAlmoxarifados(param, null);
	}

	//suggestion ALmoxarifado
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}
	
	public List<ScoGrupoMaterial> pesquisarGrupoMaterialPorCodigoDescricao(String parametro) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltro(parametro);
	}
	
	public List<RelatorioConsumoSinteticoMaterialVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioConsumoSinteticoMaterialVO> colecao) {
		this.colecao = colecao;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public DominioOrdenacaoConsumoSinteticoMaterial getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(DominioOrdenacaoConsumoSinteticoMaterial ordenacao) {
		this.ordenacao = ordenacao;
	}

	public DominioEstocavelConsumoSinteticoMaterial getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(DominioEstocavelConsumoSinteticoMaterial estocavel) {
		this.estocavel = estocavel;
	}

	public MovimentoMaterialVO getMvtodataCompetencia() {
		return mvtodataCompetencia;
	}

	public void setMvtodataCompetencia(MovimentoMaterialVO mvtodataCompetencia) {
		this.mvtodataCompetencia = mvtodataCompetencia;
	}

	public ScoGrupoMaterial getGrupo() {
		return grupo;
	}

	public void setGrupo(ScoGrupoMaterial grupo) {
		this.grupo = grupo;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public AfaTipoUsoMdto getTipoUsoMdto() {
		return tipoUsoMdto;
	}

	public void setTipoUsoMdto(AfaTipoUsoMdto tipoUsoMdto) {
		this.tipoUsoMdto = tipoUsoMdto;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public FccCentroCustos getCentroCustos() {
		return centroCustos;
	}

	public void setCentroCustos(FccCentroCustos centroCustos) {
		this.centroCustos = centroCustos;
	}

	public VScoClasMaterial getClassificacaoMaterial() {
		return classificacaoMaterial;
	}

	public void setClassificacaoMaterial(VScoClasMaterial classificacaoMaterial) {
		this.classificacaoMaterial = classificacaoMaterial;
	}
}