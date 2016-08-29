package br.gov.mec.aghu.paciente.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.RelatorioMovimentacaoVO;
import br.gov.mec.aghu.paciente.vo.VAipSolicitantesVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * Controller para geração do relatório 'Movimentação'.
 * 
 * @author RicardoCosta
 */
public class RelatorioMovimentacaoController extends ActionReport {

	private static final long serialVersionUID = -3101085182709797378L;

	private static final String REDIRECT_RELATORIO_MOVIMENTACAO_PDF = "relatorioMovimentacaoPdf";
	private static final String REDIRECT_RELATORIO_MOVIMENTACAO = "relatorioMovimentacao";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	/**
	 * Data inicial.
	 */
	private Date dtInicial;

	/**
	 * Data final.
	 */
	private Date dtFinal;

	/**
	 * Solicitante.
	 */
	private VAipSolicitantesVO vAipSolicitantesVO;

	/**
	 * Situacao da Movimentação.
	 */
	private DominioSituacaoMovimentoProntuario situacaoMovimentoProntuario;

	/**
	 * Exibir área?
	 */
	private Boolean csExibirArea;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioMovimentacaoVO> colecao = new ArrayList<RelatorioMovimentacaoVO>(
			0);

	/**
	 * Lista utilizada pelo SuggestionBox
	 */
	private List<VAipSolicitantesVO> lista = null;

	/**
	 * Hash para VAipSolicitantes.
	 */
	private Map<Short, VAipSolicitantesVO> hashSeqVAipSolicitantes = new HashMap<Short, VAipSolicitantesVO>(
			0);

	private Boolean fimProcessamentoRelatorio = false;

	/**
	 * Método invocado ao criar o componente.
	 */
	@PostConstruct
	public void inicio() {
		this.begin(conversation);
		// Valores iniciais.
		this.csExibirArea = true;
		this.situacaoMovimentoProntuario = DominioSituacaoMovimentoProntuario.R;

		// Utilizada pelo SuggestionBox.
		this.lista = pacienteFacade.obterViewAipSolicitantes(null);

		// Hash usado para melhorar performance.
		for (VAipSolicitantesVO vo : lista) {
			this.hashSeqVAipSolicitantes.put(vo.getCodigo(), vo);
		}
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws SistemaImpressaoException,
			ApplicationBusinessException, UnknownHostException, JRException,
			SystemException, IOException {

		Calendar cal = GregorianCalendar.getInstance();

		// Data Inicial
		if (this.dtInicial != null) {
			cal.setTime(dtInicial);
		} else {
			cal.setTime(new Date());
		}
		// TODO: Alterar componente de calendário e permitir a inserção de hora
		// e minutos. Feito isso remover as definição para ZERO.
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.dtInicial = cal.getTime();

		// Data Final
		if (this.dtFinal != null) {
			cal.setTime(dtFinal);
		} else {
			cal.setTime(new Date());
		}
		// TODO: Alterar componente de calendário e permitir a inserção de hora
		// e minutos. Feito isso remover as definição para ZERO.
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.dtFinal = cal.getTime();

		try {
			this.pacienteFacade.validaDatas(this.dtInicial, this.dtFinal);
		} catch (ApplicationBusinessException e) {
			if (e.getMessage() != null) {
				apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			} else {
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
			return null;
		}

		this.colecao = pacienteFacade.obterMovimentacoes(this.dtInicial,
				this.dtFinal, this.situacaoMovimentoProntuario,
				this.csExibirArea, this.vAipSolicitantesVO,
				this.hashSeqVAipSolicitantes);

		if (this.colecao == null || this.colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_RELATORIO_MOVIMENTACOES_POR_SITUACAO_VAZIO");

			return null;
		}
		return REDIRECT_RELATORIO_MOVIMENTACAO_PDF;
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void imprimirRelatorioMovimentacao()
			throws ApplicationBusinessException, JRException, SystemException,
			IOException {

		Calendar cal = GregorianCalendar.getInstance();

		// Data Inicial
		if (this.dtInicial != null) {
			cal.setTime(dtInicial);
		} else {
			cal.setTime(new Date());
		}
		// TODO: Alterar componente de calendário e permitir a inserção de hora
		// e minutos. Feito isso remover as definição para ZERO.
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.dtInicial = cal.getTime();

		// Data Final
		if (this.dtFinal != null) {
			cal.setTime(dtFinal);
		} else {
			cal.setTime(new Date());
		}
		// TODO: Alterar componente de calendário e permitir a inserção de hora
		// e minutos. Feito isso remover as definição para ZERO.
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.MILLISECOND, 0);
		this.dtFinal = cal.getTime();

		try {
			this.pacienteFacade.validaDatas(this.dtInicial, this.dtFinal);
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			return;
		}

		this.colecao = pacienteFacade.obterMovimentacoes(this.dtInicial,
				this.dtFinal, this.situacaoMovimentoProntuario,
				this.csExibirArea, this.vAipSolicitantesVO,
				this.hashSeqVAipSolicitantes);

		// gera e imprime relatorio

		DocumentoJasper documento  = gerarDocumento();

		try {
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf()
			throws ApplicationBusinessException, IOException, JRException,
			SystemException, DocumentException {
		this.fimProcessamentoRelatorio = false;
		DocumentoJasper documento = gerarDocumento();
		this.fimProcessamentoRelatorio = true;
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		// Tipo do Relatório.
		params.put(
				"tipo",
				(this.situacaoMovimentoProntuario != null) ? this.situacaoMovimentoProntuario
						.getDescricao() : "");

		// Não usar formatador. Essa operação poderia ficar com o JASPER.
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("nomeRelatorio", "AIPR_MOVIMENTACOES");
		params.put("dtHoje", sdf.format(new Date()));
		sdf = new SimpleDateFormat("dd/MM/yyyy");
		params.put("dtInicial", sdf.format(this.dtInicial));
		params.put("dtFinal", sdf.format(this.dtFinal));

		String hospital = cadastrosBasicosInternacaoFacade
				.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);

		return params;
	}

	
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/paciente/prontuario/report/relatorioMovimentacao.jasper";
	}


	public Collection<RelatorioMovimentacaoVO> recuperarColecao()
			throws ApplicationBusinessException {
		return this.colecao;
	}

	/**
	 * Método que alimenta a suggestion de Área Solicitante.
	 * 
	 * @param strPesquisa
	 * @return Lista de <code>VAipSolicitantes</code>.
	 */
	public List<VAipSolicitantesVO> pesquisarAreaSolicitante(String strPesquisa) {
		return pacienteFacade.pesquisarAreaSolicitante(lista,
				strPesquisa == null ? null : strPesquisa);
	}

	public void limparAreaSolicitante() {
		this.vAipSolicitantesVO = null;
	}

	public boolean isMostrarLinkAreaSolicitante() {
		return this.getvAipSolicitantesVO() != null;
	}

	public String voltar() {
		this.dtInicial = null;
		this.dtFinal = null;
		this.situacaoMovimentoProntuario = DominioSituacaoMovimentoProntuario.R;
		this.vAipSolicitantesVO = null;
		this.csExibirArea = Boolean.TRUE;
		return REDIRECT_RELATORIO_MOVIMENTACAO;
	}

	/*
	 * GET's e SET's.
	 */
	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public DominioSituacaoMovimentoProntuario getSituacaoMovimentoProntuario() {
		return situacaoMovimentoProntuario;
	}

	public void setSituacaoMovimentoProntuario(
			DominioSituacaoMovimentoProntuario situacaoMovimentoProntuario) {
		this.situacaoMovimentoProntuario = situacaoMovimentoProntuario;
	}

	public VAipSolicitantesVO getvAipSolicitantesVO() {
		return vAipSolicitantesVO;
	}

	public void setvAipSolicitantesVO(VAipSolicitantesVO vAipSolicitantesVO) {
		this.vAipSolicitantesVO = vAipSolicitantesVO;
	}

	public void setCsExibirArea(Boolean csExibirArea) {
		this.csExibirArea = csExibirArea;
	}

	public Boolean getCsExibirArea() {
		return csExibirArea;
	}

	public Boolean getFimProcessamentoRelatorio() {
		return fimProcessamentoRelatorio;
	}

	public void setFimProcessamentoRelatorio(Boolean fimProcessamentoRelatorio) {
		this.fimProcessamentoRelatorio = fimProcessamentoRelatorio;
	}

}
