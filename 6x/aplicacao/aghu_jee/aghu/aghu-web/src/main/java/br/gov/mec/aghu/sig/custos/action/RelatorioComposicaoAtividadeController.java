package br.gov.mec.aghu.sig.custos.action;

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
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.ComposicaoAtividadeVO;
import net.sf.jasperreports.engine.JRException;

public class RelatorioComposicaoAtividadeController extends ActionReport {

	private static final String RELATORIO_COMPOSICAO_ATIVIDADE = "relatorioComposicaoAtividade";
	private static final String RELATORIO_COMPOSICAO_ATIVIDADE_PDF= "relatorioComposicaoAtividadePdf";
	private static final long serialVersionUID = 957435759783799993L;
	private static final Log LOG = LogFactory.getLog(RelatorioComposicaoAtividadeController.class);
	private final String ERRO_GERAR_RELATORIO = "ERRO_GERAR_RELATORIO";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	/*	Dados que serão impressos em PDF. */
	private List<ComposicaoAtividadeVO> colecao = new ArrayList<ComposicaoAtividadeVO>();

	@EJB
	private ICustosSigFacade custosSigFacade;

	private FccCentroCustos filtoCentroCusto;
	private SigAtividades filtroAtividade;
	private DominioSituacao filtroSituacao;

	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}

	
	/**
	 * Metodo que efetua a pesquisa do relatório
	 * @throws BaseException 
	 */
	public void pesquisar() throws ApplicationBusinessException {
		this.colecao = custosSigFacade.buscarComposicaoAtividades(this.getFiltoCentroCusto(), this.getFiltroAtividade(), this.getFiltroSituacao());
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		try {
			this.pesquisar();

			if (this.colecao != null && !this.colecao.isEmpty()) {
				this.imprimirRelatorioCopiaSeguranca(true);
				return RELATORIO_COMPOSICAO_ATIVIDADE_PDF;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws JRException
	 * @throws IOException 
	 * @throws BaseException 
	 */
	public void directPrint() {

		try {
			this.pesquisar();

			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			if (this.colecao != null && !this.colecao.isEmpty()) {
				this.imprimirRelatorioCopiaSeguranca(true);
			}

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); 
		} catch (DocumentException | JRException | IOException e) {
			this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} 
	}

	public void limpar() {
		this.setFiltoCentroCusto(null);
		this.setFiltroAtividade(null);
		this.setFiltroSituacao(null);
	}
	
	public String voltar(){
		return RELATORIO_COMPOSICAO_ATIVIDADE;
	}

	@Override
	public Collection<ComposicaoAtividadeVO> recuperarColecao() throws ApplicationBusinessException {
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
		params.put("nomeRelatorio", "Relatório de composição de atividades");
		params.put("tituloRelatorio", this.definirTituloRelatorio());
		params.put("totalRegistros", colecao.size() - 1);
		params.put(
				"subRelatorio",
				Thread.currentThread().getContextClassLoader()
						.getResource("br/gov/mec/aghu/sig/custos/report/relatorioComposicaoAtividade_itensComposicao.jasper"));

		/*
		params.put("subRelatorio",Thread.currentThread()
				.getContextClassLoader().getResource("C:/aghu/workspace/aghu-construcao/src/hot/br/gov/mec/aghu/exames/report/relatorioMateriaisColetar_subreport1.jasper"));
		*/
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/sig/custos/report/relatorioComposicaoAtividade.jasper";

	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws DocumentException 
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/**
	 * Método que define o titulo utilizado no relatório
	 * @return
	 */
	private String definirTituloRelatorio() {
		if (this.getFiltoCentroCusto() == null && this.getFiltroAtividade() == null) {
			return "Relatório de composição das atividades não vinculadas a centro de custo";
		} else if (this.getFiltoCentroCusto() != null && this.getFiltroAtividade() == null) {
			return "Relatório de composição das atividades do centro de custo " + this.getFiltoCentroCusto().getCodigoDescricao();
		} else if (this.getFiltoCentroCusto() == null && this.getFiltroAtividade() != null) {
			return "Relatório de composição da atividade não vinculada a centro de custo";
		} else if (this.getFiltoCentroCusto() != null && this.getFiltroAtividade() != null) {
			return "Relatório de composição da atividade do centro de custo " + this.getFiltoCentroCusto().getCodigoDescricao();
		} else {
			return "Relatório de composição de atividades";
		}
	}

	/**
	 * Método utilizado após selecionador ou excluir o centro de custo selecionado
	 */
	public void desmarcarAtividadeSelecionada() {
		this.setFiltroAtividade(null);
	}

	/**
	 * Método para a busca da suggestion box de centros de custo
	 */
	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, null);
	}

	/**
	 * Método para a buisca da suggestion box de atividade
	 */
	public List<SigAtividades> pesquisarAtividades(String objPesquisa) {
		return this.custosSigFacade.listarAtividadesRestringindoCentroCusto(this.getFiltoCentroCusto(), objPesquisa);
	}

	//Gets and Sets
	public FccCentroCustos getFiltoCentroCusto() {
		return filtoCentroCusto;
	}

	public void setFiltoCentroCusto(FccCentroCustos filtoCentroCusto) {
		this.filtoCentroCusto = filtoCentroCusto;
	}

	public SigAtividades getFiltroAtividade() {
		return filtroAtividade;
	}

	public void setFiltroAtividade(SigAtividades filtroAtividade) {
		this.filtroAtividade = filtroAtividade;
	}

	public DominioSituacao getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(DominioSituacao filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}
}