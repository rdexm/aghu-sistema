package br.gov.mec.aghu.exames.pesquisa.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
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
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.dominio.DominioLocalColeta;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioMateriaisColetarInternacaoFiltroVO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.SolicitacaoColetarVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import net.sf.jasperreports.engine.JRException;



public class RelatorioMateriaisColetarController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(RelatorioMateriaisColetarController.class);

	private static final long serialVersionUID = 957435759783799993L;

	private static final String RELATORIO_MATERIAIS_COLETAR_INTERNACAO = "relatorioMateriaisColetarInternacao";
	private static final String RELATORIO_MATERIAIS_COLETAR_INTERNACAO_PDF = "relatorioMateriaisColetarInternacaoPdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	/*	fitro da tela de pesquisa	*/
	private RelatorioMateriaisColetarInternacaoFiltroVO filtro = new RelatorioMateriaisColetarInternacaoFiltroVO();

	/*	Dados que serão impressos em PDF. */
	//private List<RelatorioMateriaisColetarInternacaoVO> colecao = new ArrayList<RelatorioMateriaisColetarInternacaoVO>();
	private List<SolicitacaoColetarVO> colecao = new ArrayList<SolicitacaoColetarVO>();
	
	private AghUnidadesFuncionais unidadeColetaDefault;
	private AelSitItemSolicitacoes sitItemSolicitacaoDefault;
	private ImpImpressora impressoraEtiquetaDefault;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		filtro = new RelatorioMateriaisColetarInternacaoFiltroVO();
	}
	
	public void iniciar() {
		unidadeColetaDefault = solicitacaoExameFacade.buscarAghUnidadesFuncionaisPorParametro(AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT.toString());
		impressoraEtiquetaDefault = solicitacaoExameFacade.obterImpressoraEtiquetas(obterNomeMicrocomputador());
		
		List<AelSitItemSolicitacoes> lstSitSolic = examesFacade.buscarListaAelSitItemSolicitacoesPorParametro("AC","AC");		
		if(lstSitSolic!=null && lstSitSolic.size()>0){
			sitItemSolicitacaoDefault = lstSitSolic.get(0);
		}

		this.filtro.setLocalColeta(DominioLocalColeta.NENHUM);
		 
		this.mudaOpcoes();
	}

	/**
	 * Metodo que efetua a pesquisa do relatório
	 */
	public void pesquisar() throws BaseException{
		try {
			String nomeMicrocomputador = obterNomeMicrocomputador();
			
			this.colecao = solicitacaoExameFacade.buscaMateriaisColetarInternacao(this.filtro, nomeMicrocomputador);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	private String obterNomeMicrocomputador() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		return nomeMicrocomputador; 
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		try{
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			
			this.colecao = solicitacaoExameFacade.buscaMateriaisColetarInternacao(this.filtro, nomeMicrocomputador);
			
			// #9418 - Contigencia Consular Últimos Arquivos Impressos
			if (this.colecao != null && !this.colecao.isEmpty()){
				this.imprimirRelatorioCopiaSeguranca(true);
			}else {
				apresentarMensagemDadosNaoEncontrados();
			
		
		return null;
			}
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
			
		} catch (DocumentException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			return null;
		}
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_MATERIAIS_COLETAR_INTERNACAO_PDF;
	}
	
	private void apresentarMensagemDadosNaoEncontrados()
			throws BaseException {
		this.apresentarMsgNegocio(Severity.ERROR,
				"ERRO_DADOS_NAO_ENCONTRADOS");
		if (solicitacaoExameFacade.verificaDiaPlantao()){
			this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_DADOS_NAO_ENCONTRADOS_PLANTAO");
		}
	}

	public String voltar(){
		return RELATORIO_MATERIAIS_COLETAR_INTERNACAO;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			this.colecao = solicitacaoExameFacade.buscaMateriaisColetarInternacao(this.filtro, nomeMicrocomputador);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}

		try {
			if (this.colecao != null && !this.colecao.isEmpty()){
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
				// #9418 - Contigencia Consular Últimos Arquivos Impressos
				this.imprimirRelatorioCopiaSeguranca(true);
			}else{
				apresentarMensagemDadosNaoEncontrados();
			}

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<SolicitacaoColetarVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_COLETA_INTERNAC");
		params.put("tituloRelatorio", "Materiais de Pacientes Internados a Coletar pela unidade " + filtro.getUnidadeColeta().getDescricao());
		params.put("totalRegistros", colecao.size()-1);
		
		params.put("subRelatorio",Thread.currentThread()
				.getContextClassLoader().getResource("br/gov/mec/aghu/exames/report/relatorioMateriaisColetar_subreport1.jasper"));
		
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		return	"br/gov/mec/aghu/exames/report/relatorioMateriaisColetar.jasper";			
			
	}
	
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	public void mudaOpcoes() {
		this.filtro.setAelSolicitacaoExames(null);
		this.filtro.setUnidadeColeta(unidadeColetaDefault);
		this.filtro.setUnidadeFuncionalSolicitante(null);
		this.filtro.setDtHrExecucao(new Date());
		this.filtro.setIndImpressaoEtiquetas(DominioSimNao.S);
		
		switch (this.filtro.getLocalColeta()) {
		case NENHUM:
			this.filtro.setAelSitItemSolicitacoes(null);	 
			this.filtro.setCaracteristica(null);
			this.filtro.setImpressora(null);			
			this.filtro.setTipoColeta(null);

			break;		
		case CTI:
			this.filtro.setAelSitItemSolicitacoes(sitItemSolicitacaoDefault);			
			this.filtro.setCaracteristica(ConstanteAghCaractUnidFuncionais.UNID_CTI);			
			this.filtro.setImpressora(impressoraEtiquetaDefault);
			this.filtro.setTipoColeta(null);
			
			break;
		case NORMAL:
			this.filtro.setAelSitItemSolicitacoes(sitItemSolicitacaoDefault);			 
			this.filtro.setCaracteristica(null);
			this.filtro.setImpressora(impressoraEtiquetaDefault);			
			this.filtro.setTipoColeta(DominioTipoColeta.N);

			break;			
		}
	}

	// Metodo para Suggestion Box de Solicitações
	public List<AelSolicitacaoExames> buscarAelSolicitacaoExames(String valor){
		return examesFacade.buscarAelSolicitacaoExames((String)valor);
	}

	// Metodo para Suggestion Box de Situação de itens
	public List<AelSitItemSolicitacoes> buscarListaAelSitItemSolicitacoesPorParametro(String valor){
		return examesFacade.buscarListaAelSitItemSolicitacoesPorParametro((String)valor, "AC","EC");
	}

	// Metodo para Suggestion Box de Unidade de Coleta (Unidade Funcional)
	public List<AghUnidadesFuncionais> obterUnidadeColeta(String parametro){
		return this.aghuFacade.listarAghUnidadesFuncionaisAtivasColetaveis(parametro);
	}

	// Metodo para Suggestion Box Unidade Solicitante
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalSolicitanteAvisada(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoEDescricao(param);
	}

	// Metodo para Suggestion Box de impressoras
	public List<ImpImpressora> pesquisarImpressora(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarImpressora(paramPesquisa, true);
	}

	public List<AacConsultas> obtemListSituacaoExames() {
		return examesFacade.listarSituacaoExames();
	}

	public RelatorioMateriaisColetarInternacaoFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(RelatorioMateriaisColetarInternacaoFiltroVO filtro) {
		this.filtro = filtro;
	}
}