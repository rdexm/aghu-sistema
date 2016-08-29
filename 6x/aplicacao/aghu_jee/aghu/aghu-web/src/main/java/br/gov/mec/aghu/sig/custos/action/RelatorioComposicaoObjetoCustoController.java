package br.gov.mec.aghu.sig.custos.action;

import java.io.IOException;
import java.net.UnknownHostException;
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
import br.gov.mec.aghu.dominio.DominioComposicaoObjetoCusto;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.sig.custos.vo.ComposicaoObjetoCustoVO;
import net.sf.jasperreports.engine.JRException;

public class RelatorioComposicaoObjetoCustoController extends ActionReport {

	private static final String ERRO_GERAR_RELATORIO = "ERRO_GERAR_RELATORIO";
	private static final String RELATORIO_COMPOSICAO_OBJETO_CUSTO_PDF = "relatorioComposicaoObjetoCustoPdf";
	private static final long serialVersionUID = -34890423893423L;
	private static final String RELATORIO_COMPOSICAO_OBJETO_CUSTO = "relatorioComposicaoObjetoCusto";
	private static final Log LOG = LogFactory.getLog(RelatorioComposicaoObjetoCustoController.class);

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	/*	Dados que serão impressos em PDF. */
	private List<ComposicaoObjetoCustoVO> colecao = new ArrayList<ComposicaoObjetoCustoVO>(0);

	@EJB
	private ICustosSigFacade custosSigFacade;

	private FccCentroCustos filtoCentroCusto;
	private SigObjetoCustos filtroObjetoCusto;
	private DominioSituacaoVersoesCustos filtroSituacao;
	private DominioComposicaoObjetoCusto filtroComposicaoObjetoCusto;

	@PostConstruct
	protected void inicializar(){
		LOG.debug("begin conversation");
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		//this.setFiltroSituacao(DominioSituacaoVersoesCustos.A);
		if (this.getFiltroComposicaoObjetoCusto() == null) {
			this.setFiltroComposicaoObjetoCusto(DominioComposicaoObjetoCusto.R);
		}
	
	}
	
	

	/**
	 * Metodo que efetua a pesquisa do relatório
	 * @throws BaseException 
	 */
	public void pesquisar() throws ApplicationBusinessException {
		this.colecao = custosSigFacade.buscarComposicaoObjetosCusto(this.getFiltoCentroCusto(), this.getFiltroObjetoCusto(), this.getFiltroSituacao(),
				this.getFiltroComposicaoObjetoCusto());
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws JRException, SystemException, IOException, BaseException {

		try {
			this.pesquisar();

			if (this.colecao != null && !this.colecao.isEmpty()) {
				this.imprimirRelatorioCopiaSeguranca(true);
				return RELATORIO_COMPOSICAO_OBJETO_CUSTO_PDF;
			}
		} catch (DocumentException e) {
			this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws JRException
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
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (DocumentException e) {
			this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (BaseException e) {
			this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (IOException e) {
			this.apresentarMsgNegocio(Severity.ERROR, ERRO_GERAR_RELATORIO);
		} catch (JRException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public void limpar() {
		this.setFiltoCentroCusto(null);
		this.setFiltroObjetoCusto(null);
		this.setFiltroSituacao(null);
		this.setFiltroComposicaoObjetoCusto(DominioComposicaoObjetoCusto.A);
	}
	
	public String voltar(){
		return RELATORIO_COMPOSICAO_OBJETO_CUSTO;
	}

	@Override
	public Collection<ComposicaoObjetoCustoVO> recuperarColecao() throws ApplicationBusinessException {
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
		params.put("nomeRelatorio", "Relatório de composição de objeto de custos");
		params.put("tituloRelatorio", this.definirTituloRelatorio());
		params.put("totalRegistros", colecao.size() - 1);

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		params.put("subRelatorioItensComposicaoPorRecurso",
				classLoader.getResource("br/gov/mec/aghu/sig/custos/report/relatorioComposicaoObjetoCusto_itensComposicaoPorRecurso.jasper"));
		params.put("subRelatorioItensComposicaoPorAtividade",
				classLoader.getResource("br/gov/mec/aghu/sig/custos/report/relatorioComposicaoObjetoCusto_itensComposicaoPorAtividade.jasper"));
		params.put("subRelatorioItensPhi", classLoader.getResource("br/gov/mec/aghu/sig/custos/report/relatorioComposicaoObjetoCusto_itensPhi.jasper"));
		params.put("subRelatorioItensDirecionadorRateio",
				classLoader.getResource("br/gov/mec/aghu/sig/custos/report/relatorioComposicaoObjetoCusto_itensDirecionadorRateio.jasper"));
		params.put("subRelatorioItensCliente", classLoader.getResource("br/gov/mec/aghu/sig/custos/report/relatorioComposicaoObjetoCusto_itensCliente.jasper"));
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/sig/custos/report/relatorioComposicaoObjetoCusto.jasper";

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
		if (this.getFiltoCentroCusto() == null && this.getFiltroObjetoCusto() == null) {
			return "Relatório de composição dos objetos de custo não vinculados a centro de custo";
		} else if (this.getFiltoCentroCusto() != null && this.getFiltroObjetoCusto() == null) {
			return "Relatório de composição dos objetos de custo do centro de custo " + this.getFiltoCentroCusto().getCodigoDescricao();
		} else if (this.getFiltoCentroCusto() == null && this.getFiltroObjetoCusto() != null) {
			return "Relatório de composição do objeto de custo não vinculado a centro de custo";
		} else if (this.getFiltoCentroCusto() != null && this.getFiltroObjetoCusto() != null) {
			return "Relatório de composição do objeto de custo do centro de custo " + this.getFiltoCentroCusto().getCodigoDescricao();
		} else {
			return "Relatório de composição de objetos de custo";
		}
	}

	/**
	 * Método utilizado após selecionador ou excluir o centro de custo selecionado
	 */
	public void desmarcarObjetoCustoSelecionado() {
		this.setFiltroObjetoCusto(null);
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
	public List<SigObjetoCustos> pesquisarObjetoCusto(String paramPesquisa) {
		return custosSigFacade.pesquisarObjetoCustoAssociadoCentroCustoOuSemCentroCusto(paramPesquisa, this.getFiltoCentroCusto());
	}

	//Gets and Sets
	public FccCentroCustos getFiltoCentroCusto() {
		return filtoCentroCusto;
	}

	public void setFiltoCentroCusto(FccCentroCustos filtoCentroCusto) {
		this.filtoCentroCusto = filtoCentroCusto;
	}

	public DominioSituacaoVersoesCustos getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(DominioSituacaoVersoesCustos filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public SigObjetoCustos getFiltroObjetoCusto() {
		return filtroObjetoCusto;
	}

	public void setFiltroObjetoCusto(SigObjetoCustos filtroObjetoCusto) {
		this.filtroObjetoCusto = filtroObjetoCusto;
	}

	public DominioComposicaoObjetoCusto getFiltroComposicaoObjetoCusto() {
		return filtroComposicaoObjetoCusto;
	}

	public void setFiltroComposicaoObjetoCusto(DominioComposicaoObjetoCusto filtroComposicaoObjetoCusto) {
		this.filtroComposicaoObjetoCusto = filtroComposicaoObjetoCusto;
	}

}