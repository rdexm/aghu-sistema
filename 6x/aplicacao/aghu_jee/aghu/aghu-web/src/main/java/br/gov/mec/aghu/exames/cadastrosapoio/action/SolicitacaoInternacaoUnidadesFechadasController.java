package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.RelatorioTicketAreaExecutoraVO;
import br.gov.mec.aghu.exames.vo.VAelSolicAtendsVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class SolicitacaoInternacaoUnidadesFechadasController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final long serialVersionUID = 4783441259994105028L;

	private static final Log LOG = LogFactory.getLog(SolicitacaoInternacaoUnidadesFechadasController.class);

	private static final String PAGE_RELATORIO_TICKET_EXAMES_AREA_EXECUTORA_PDF = "exames-relatorioTicketExamesAreaExecutoraPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	// Referências gerais
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	// Referências especificas
	private AghUnidadesFuncionais unidadeExecutora;
	private List<VAelSolicAtendsVO> listaSolicitacaoExames;
	private Integer numero;
	private VAelSolicAtendsVO solicExameVO;
	private Integer exameSelecionadoId;
	private List<AelItemSolicitacaoExames> listaItensExame;
	private Date dtSolicitacao;
	private boolean ativarPesquisaAutomatica;

	// Referencias especificas
	private AelUnidExecUsuario usuarioUnidadeExecutora;

	// Referencias gerais
	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController;

	private String voltandoDe;
	private String voltarPara;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioTicketAreaExecutoraVO> colecao = new ArrayList<RelatorioTicketAreaExecutoraVO>(0);

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		if (numero == null || unidadeExecutora == null || unidadeExecutora.getSeq() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_BLANK");
		}

		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}

		this.colecao = examesFacade.pesquisarRelatorioTicketAreaExecutora(this.numero, unidadeExecutora.getSeq(), nomeMicrocomputador);

	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return PAGE_RELATORIO_TICKET_EXAMES_AREA_EXECUTORA_PDF;

	}

	@Override
	public Collection<RelatorioTicketAreaExecutoraVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {

			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}

			colecao = examesFacade.pesquisarRelatorioTicketAreaExecutora(this.numero, unidadeExecutora.getSeq(), nomeMicrocomputador);

			if (!colecao.isEmpty()) {
				try {
					DocumentoJasper documento = gerarDocumento();

					this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Ticket Exame");

				} catch (SistemaImpressaoException e) {
					apresentarExcecaoNegocio(e);

				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
				}
			}

		} catch (BaseException e1) {
			apresentarExcecaoNegocio(e1);
		}
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("hospitalLocal", hospital);
		params.put("tituloRelatorio", "Exames da " + unidadeExecutora.getDescricao());
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeRelatorio", "AELR_FICHA_EXAMES");
		// params.put("totalRegistros", colecao.size()-1);

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioTicketAreaExecutora.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	@Override
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/**
	 * Chamado no inicio de "cada conversacao"
	 */
	public void iniciar() {
	 

		if (voltandoDe != null && !"".equals(voltandoDe)) {
			voltandoDe = null;
			this.pesquisarSolicitacaoExames();

		} else {
			if (this.numero != null && this.dtSolicitacao != null) {
				this.pesquisarSolicitacaoExames();
			} else {
				// this.numero = null;
				Calendar calendar = Calendar.getInstance();
				calendar.add(Calendar.MONTH, -1);
				Date dataSolicit = calendar.getTime();
				this.dtSolicitacao = dataSolicit;

				// Habilita por padrão a pesquisa automatica (Chamado pelo compomente
				// responsavel por realizar o REFRESH da pesquisa.)
				this.ativarPesquisaAutomatica = true;

				// Obtem o USUARIO da unidade executora
				try {
					this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
				} catch (ApplicationBusinessException e) {
					usuarioUnidadeExecutora = null;
				}

				// Resgata a unidade executora associada ao usuario
				this.unidadeExecutora = this.unidadeExecutoraController.getUnidadeExecutora();
				if (unidadeExecutora != null) {
					this.pesquisarSolicitacaoExames();
				}
			}
		}
	
	}

	/**
	 * Pesquisa AUTOMATICA de soliciticacoes de Exames. Chamado pelo compomente responsavel por realizar o REFRESH da pesquisa.
	 */
	public void pesquisaAutomatica() {
		if (this.unidadeExecutora != null && this.ativarPesquisaAutomatica) {
			this.pesquisarSolicitacaoExames();
		}
	}

	/**
	 * Confirma a persistencia das informacoes
	 */
	public void pesquisarSolicitacaoExames() {

		// Desativa pesquisa/refresh automatico durante a pesquisa
		this.ativarPesquisaAutomatica = false;

		try {
			this.listaSolicitacaoExames = this.solicitacaoExameFacade.listarSolicitacaoExamesUnExecutora(this.unidadeExecutora, dtSolicitacao);
			if (listaSolicitacaoExames != null) {
				this.solicExameVO = listaSolicitacaoExames.get(0);
				selecionarExame();
			} else {

				this.listaItensExame = null;

			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		// Ativa pesquisa/refresh automatica
		this.ativarPesquisaAutomatica = true;

	}

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {

			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/*
	 * Metodos para Suggestion Box..
	 */

	public Date getDtSolicitacao() {
		return dtSolicitacao;
	}

	public void setDtSolicitacao(Date dtSolicitacao) {
		this.dtSolicitacao = dtSolicitacao;
	}

	// Metodo para pesquisa na suggestion box de unidade executora
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {

		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);

	}

	// Metodo para limpeza da suggestion box de unidade executora
	public void limparAghUnidadesFuncionaisExecutoras() {
		this.unidadeExecutora = null;

	}

	public void selecionarExame() {
		try {
			this.numero = solicExameVO.getNumero();
			this.carregarItens();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void carregarItens() throws BaseException {
		exameSelecionadoId = solicExameVO.getNumero();
		listaItensExame = this.solicitacaoExameFacade.buscarItensExamesAExecutar(exameSelecionadoId, unidadeExecutora.getSeq());// SoeSeq
	}

	public void imprimirSolicitacoes(AghUnidadesFuncionais unidExecutora, List<Integer> solicitacoes) throws BaseException, JRException, SystemException, IOException {
		this.unidadeExecutora = unidExecutora;
		for (Integer solicitacao : solicitacoes) {
			this.setNumero(solicitacao);
			directPrint();
		}
	}

	public String voltar() {
		colecao = new ArrayList<RelatorioTicketAreaExecutoraVO>(0);
		return voltarPara;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public List<VAelSolicAtendsVO> getListaSolicitacaoExames() {
		return listaSolicitacaoExames;
	}

	public void setListaSolicitacaoExames(List<VAelSolicAtendsVO> listaSolicitacaoExames) {
		this.listaSolicitacaoExames = listaSolicitacaoExames;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public VAelSolicAtendsVO getSolicExameVO() {
		return solicExameVO;
	}

	public void setSolicExameVO(VAelSolicAtendsVO solicExameVO) {
		this.solicExameVO = solicExameVO;
	}

	public Integer getExameSelecionadoId() {
		return exameSelecionadoId;
	}

	public void setExameSelecionadoId(Integer exameSelecionadoId) {
		this.exameSelecionadoId = exameSelecionadoId;
	}

	public List<AelItemSolicitacaoExames> getListaItensExame() {
		return listaItensExame;
	}

	public void setListaItensExame(List<AelItemSolicitacaoExames> listaItensExame) {
		this.listaItensExame = listaItensExame;
	}

	public boolean isAtivarPesquisaAutomatica() {
		return ativarPesquisaAutomatica;
	}

	public void setAtivarPesquisaAutomatica(boolean ativarPesquisaAutomatica) {
		this.ativarPesquisaAutomatica = ativarPesquisaAutomatica;
	}

	public boolean isDisableButton() {

		if (this.numero != null) {
			return false;
		} else {
			return true;
		}
	}

	public String getVoltandoDe() {
		return voltandoDe;
	}

	public void setVoltandoDe(String voltandoDe) {
		this.voltandoDe = voltandoDe;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}