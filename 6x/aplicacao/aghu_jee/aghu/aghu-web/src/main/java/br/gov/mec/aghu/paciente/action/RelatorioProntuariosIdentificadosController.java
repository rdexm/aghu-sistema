package br.gov.mec.aghu.paciente.action;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.AghParametrosVO;
import br.gov.mec.aghu.paciente.vo.ProntuarioIdentificadoVO;
import br.gov.mec.aghu.paciente.vo.ReImpressaoEtiquetasVO;

import com.itextpdf.text.DocumentException;

/**
 * Controller para geração do relatório 'Prontuários Identificados'.
 * 
 * @author RicardoCosta
 */

public class RelatorioProntuariosIdentificadosController extends ActionReport {

	private static final String ASTERISCOS = "***************************";

	/**
	 * 
	 */
	private static final long serialVersionUID = 2365729406023366102L;

	private static final Log LOG = LogFactory
			.getLog(RelatorioProntuariosIdentificadosController.class);

	private static final String REDIRECT_PRONTUARIOS_IDENTIFICADOS = "relatorioProntuariosIdentificadosPdf";

	// private static final String RELATORIO_PRONTUARIOS_IDENTIFICADOS =
	// "RELATORIO_PRONTUARIOS_IDENTIFICADOS";
	private static final String ETIQUETAS_NOME = "ETIQUETAS_NOME";

	private StreamedContent media;

	/**
	 * serialVersionUID.
	 */

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * Área a considerar.
	 */
	private FccCentroCustos areaConsiderar;

	/**
	 * Área a desconsiderar.
	 */
	private FccCentroCustos areaDesconsiderar;

	/**
	 * Date Inicial.
	 */
	private Date dtInicial = null;

	/**
	 * Date Final.
	 */
	private Date dtFinal = null;

	/**
	 * Coleção de dados a serem impressos (ProntuarioIdentificadoVO).
	 */
	private List<Object> colecaoProntImp = new ArrayList<Object>(0);

	/**
	 * Coleção de dados a serem impressos (ProntuarioIdentificadoVO).
	 */
	private List<Object> colecaoReImpEtqs = new ArrayList<Object>(0);

	/**
	 * Código ZPL que será enviado à impressora.
	 */
	private String zpl;

	/**
	 * Nome do arquivo JASPER que será utilizado na geração do PDF.
	 */
	private String relatorio = null;

	/**
	 * Atributo utilizado para controlar a exibicao do botao
	 * "Visualizar Impressão".
	 */
	private boolean exibirBtnVisualizar;

	/**
	 * Indica qual PDF deve abrir.
	 */
	private Integer tipoImpressao;

	/**
	 * Indica a unidade funcional SAMIS.
	 */
	private AghUnidadesFuncionais unidadeSamis;

	private boolean infAdicionais;

	/**
	 * Método invocado na criação do componente.
	 */
	@PostConstruct
	public void init() {
		this.begin(conversation);

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);

		this.tipoImpressao = 1;
		if (session.getAttribute(ETIQUETAS_NOME) != null) {
			this.tipoImpressao = 2;
		}

		this.exibirBtnVisualizar = false;
		this.setInfAdicionais(false);

		// busca unidade funcional SAMIS
		AghParametros aghParam = null;
		try {
			aghParam = parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_CUPS_REDIRECIONAMENTO);
			if (aghParam != null && aghParam.getVlrNumerico() != null
					&& aghParam.getVlrNumerico().intValue() == 1) {
				aghParam = parametroFacade
						.buscarAghParametro(AghuParametrosEnum.P_UNIDADE_SAMIS);
				unidadeSamis = new AghUnidadesFuncionais(aghParam
						.getVlrNumerico().shortValue());
			}
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
			return;
		}
	}

	/**
	 * Método invocado pelo botão 'Etiquetas' e responsável por gerar o conteúdo
	 * que é utilizado pelas etiquetas Zebras.
	 */
	public void etiquetasZebra() {

		StringBuffer zplAux = new StringBuffer();

		for (Object item : this.colecaoProntImp) {

			ProntuarioIdentificadoVO obj = (ProntuarioIdentificadoVO) item;

			zplAux.append(pacienteFacade.gerarZpl(
					Integer.parseInt(obj.getProntuario1()), (short) 0,
					obj.getNome()));

			// FIXME: Mudar a localicação desse operação. A mesma só deve
			// ocorrer quando de fato for impressa as etiquetas. Nesse momento
			// aqui a impressão ainda pode ser cancelada. Atualmente o controle
			// da impressão fica com o navegador, portanto não há como saber se
			// de fato foi impresso.
			pacienteFacade.atualizaVolume(Integer.parseInt(obj.getCodigo()),
					(short) 0);
		}

		this.zpl = zplAux.toString();

		// envia impressao das etiquetas
		try {
			if(unidadeSamis == null) {
				this.apresentarMsgNegocio(Severity.ERROR,
						"ERROR_UNIDADE_SAMIS_NAO_CADASTRADA");
				return;
			}
			this.sistemaImpressao.imprimir(zpl, unidadeSamis,
					TipoDocumentoImpressao.ETIQUETAS_BARRAS_PRONTUARIO);

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_ETIQUETAS");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}

		this.exibirBtnVisualizar = true;
	}

	/**
	 * Método invocado pelo botão 'Etq. Nomes' (Etiquetas A4).
	 */
	public void etiquetasA4() throws ApplicationBusinessException, JRException,
			SystemException, IOException, ParseException {
		this.exibirBtnVisualizar = true;
		this.tipoImpressao = 2;
		this.relatorio = "/br/gov/mec/aghu/paciente/report/etiquetasNome.jasper";

		this.colecaoReImpEtqs = obterEtiquetasA4();

		DocumentoJasper documento = gerarDocumento(false);
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.setAttribute(ETIQUETAS_NOME, documento);
		carregarRelatorio();
	}

	/**
	 * Método invocado pelo botão 'Visualizar Impressão'.
	 */
	public void visualizarImpressao() {
		this.exibirBtnVisualizar = false;
		this.tipoImpressao = 1;
	}

	/**
	 * Método responsável por gerar o conteúdo que é utilizado pelas etiquetas
	 * A4.
	 */
	private List<Object> obterEtiquetasA4() {

		List<Object> retorno = new ArrayList<Object>();
		String codigo = null;
		boolean isRight = false;

		ReImpressaoEtiquetasVO etq = new ReImpressaoEtiquetasVO();

		Iterator<Object> it = this.colecaoProntImp.iterator();
		// TODO: Melhorar o preenchimento deste VO, o mesmo está assim, pois é
		// necessário imprimir em duas colunas.
		while (it.hasNext()) {
			ProntuarioIdentificadoVO obj = (ProntuarioIdentificadoVO) it.next();

			if (codigo == null || !obj.getCodigo().equals(codigo)) {
				if (isRight) {
					isRight = false;
					codigo = obj.getCodigo();
					etq.setpLeito(codigo);
					etq.setpNome(ASTERISCOS);
					etq.setpProntuario("0/");
					retorno.add(etq);
					etq = new ReImpressaoEtiquetasVO();
				} else {
					isRight = true;
					codigo = obj.getCodigo();
					etq.setLeito(codigo);
					etq.setNome(ASTERISCOS);
					etq.setProntuario("0/");
				}
			}
			if (isRight) {
				isRight = false;
				codigo = obj.getCodigo();
				etq.setpLeito(obj.getLtoLtoId());
				etq.setpNome(obj.getNome());
				etq.setpProntuario(obj.getProntuario());
				retorno.add(etq);
				etq = new ReImpressaoEtiquetasVO();
			} else {
				isRight = true;
				codigo = obj.getCodigo();
				etq.setLeito(obj.getLtoLtoId());
				etq.setNome(obj.getNome());
				etq.setProntuario(obj.getProntuario());
			}

			if (it.hasNext()) {
				obj = (ProntuarioIdentificadoVO) it.next();

				if (codigo == null || !obj.getCodigo().equals(codigo)) {
					if (isRight) {
						isRight = false;
						codigo = obj.getCodigo();
						etq.setpLeito(codigo);
						etq.setpNome(ASTERISCOS);
						etq.setpProntuario("0/");
						retorno.add(etq);
						etq = new ReImpressaoEtiquetasVO();
					} else {
						isRight = true;
						codigo = obj.getCodigo();
						etq.setLeito(codigo);
						etq.setNome(ASTERISCOS);
						etq.setProntuario("0/");
					}
				}
				if (isRight) {
					isRight = false;
					codigo = obj.getCodigo();
					etq.setpLeito(obj.getLtoLtoId());
					etq.setpNome(obj.getNome());
					etq.setpProntuario(obj.getProntuario());
					retorno.add(etq);
					etq = new ReImpressaoEtiquetasVO();
				} else {
					isRight = true;
					codigo = obj.getCodigo();
					etq.setLeito(obj.getLtoLtoId());
					etq.setNome(obj.getNome());
					etq.setProntuario(obj.getProntuario());
				}
			}

			if (!it.hasNext()) {
				if (!retorno.contains(etq)) {
					retorno.add(etq);
				}
			}
		}

		return retorno;
	}

	/**
	 * Impressao direta via CUPS.
	 * 
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void impressaoDireta() throws ApplicationBusinessException,
			JRException, SystemException, IOException, ParseException {
		if (dtInicial.compareTo(dtFinal) > 0) {
			this.apresentarMsgNegocio("DATA_INICIAL_MAIOR_DATA_FINAL");
			return;
		}
		this.print();

		this.relatorio = "/br/gov/mec/aghu/paciente/prontuario/report/relatorioProntuarioIdentificado.jasper";

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}

	/**
	 * Busca os dados para geração do relatório.
	 */
	public String print() throws ApplicationBusinessException, JRException,
			SystemException, IOException, ParseException {
		if (dtInicial.compareTo(dtFinal) > 0) {
			this.apresentarMsgNegocio("DATA_INICIAL_MAIOR_DATA_FINAL");
			return null;
		}
		return REDIRECT_PRONTUARIOS_IDENTIFICADOS;
	}

	private DocumentoJasper gerarRelatorioProntuariosIdentificados()
			throws ApplicationBusinessException {
		boolean updateDtIdent = false;
		Integer codAreaConsiderar = null;
		Integer codAreaDesconsiderar = null;

		if (this.areaConsiderar != null) {
			codAreaConsiderar = this.areaConsiderar.getCodigo();
		}

		if (this.areaDesconsiderar != null) {
			codAreaDesconsiderar = this.areaDesconsiderar.getCodigo();
		}

		try {
			this.pacienteFacade.validaDadosRelProntuarioIdent(dtInicial,
					dtFinal);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		// Data Inicial
		if (this.dtInicial == null) {
			// Busca em AGH_PARAMETRO
			AghParametrosVO aghParametroVO = new AghParametrosVO();
			aghParametroVO.setNome("P_DATA_REF_IDEN_PAC");
			this.parametroFacade.getAghpParametro(aghParametroVO);
			this.dtInicial = aghParametroVO.getVlrData();
			updateDtIdent = true;
		}

		// Data Final
		if (this.dtFinal == null) {
			this.dtFinal = Calendar.getInstance().getTime();
			updateDtIdent = true;
		}

		// Atualiza data de identificação.
		if (updateDtIdent) {
			AghParametrosVO aghParametroVO = new AghParametrosVO();
			aghParametroVO.setNome("P_DATA_REF_IDEN_PAC");
			aghParametroVO.setVlrData(dtFinal);

			this.parametroFacade.setAghpParametro(aghParametroVO,
					"AIPR_PRNT_IDENT", true);
		}

		// Recupera coleção.
		this.colecaoProntImp = pacienteFacade.obterProntuariosIdentificados(
				this.dtInicial, this.dtFinal, codAreaConsiderar,
				codAreaDesconsiderar, this.infAdicionais);

		this.tipoImpressao = 1;

		this.relatorio = "/br/gov/mec/aghu/paciente/prontuario/report/relatorioProntuarioIdentificado.jasper";

		DocumentoJasper documento = gerarDocumento(false);
		return documento;
	}

	public void carregarRelatorio() {

		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);

		DocumentoJasper documento = null;

		switch (this.tipoImpressao) {
		case 1:
			try {
				documento = gerarRelatorioProntuariosIdentificados();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
			}
			// session.removeAttribute(RELATORIO_PRONTUARIOS_IDENTIFICADOS);
			break;
		case 2:
			documento = (DocumentoJasper) session.getAttribute(ETIQUETAS_NOME);
			session.removeAttribute(ETIQUETAS_NOME);
			break;
		default:
			break;
		}
		try {
			media = this.criarStreamedContentPdf(documento
					.getPdfByteArray(false));
		} catch (JRException | IOException | DocumentException e) {
			LOG.error(e.getMessage(), e);
		}

	}

	@Override
	public Map<String, Object> recuperarParametros() {

		// Formatador para Data. Essa operação poderia ficar com o JASPER.
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("nomeRelatorio", "AIPR_PRNT_IDENT");
		params.put("dtHoje", sdf.format(new Date()));

		sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		params.put("dtInicial", sdf.format(this.dtInicial));
		params.put("dtFinal", sdf.format(this.dtFinal));

		String hospital = cadastrosBasicosInternacaoFacade
				.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);

		return params;
	}

	/**
	 * Método invocado ao usuário cancelar impressão.
	 * 
	 * @return String
	 */
	public void actionCancel() {
		try {
			cadastrosBasicosInternacaoFacade.cancelarImpressao();
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			this.apresentarMsgNegocio(Severity.WARN, e.getLocalizedMessage());
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return this.relatorio;
	}

	@Override
	public Collection<Object> recuperarColecao()
			throws ApplicationBusinessException {
		switch (this.tipoImpressao) {
		case 1:
			return this.colecaoProntImp;
		case 2:
			return this.colecaoReImpEtqs;
		default:
			break;
		}
		return null;
	}

	/**
	 * Método que alimenta a suggestion de Centro de custo (Área).
	 * 
	 * @param strPesquisa
	 * @return Lista de <code>FccCentroCustos</code>.
	 */
	public List<FccCentroCustos> pesquisarCentroCustos(String strPesquisa) {
		return returnSGWithCount(
				centroCustoFacade
						.pesquisarCentroCustosPorCodigoDescricao(strPesquisa),
				pesquisarCentroCustosCount(strPesquisa));
	}

	public Long pesquisarCentroCustosCount(String filtro) {
		return centroCustoFacade
				.pesquisarCentroCustosPorCodigoDescricaoCount(filtro);
	}

	/**
	 * Limpa Suggestion área considerar
	 */
	public void limparAreaConsiderar() {
		this.areaConsiderar = null;
	}

	/**
	 * Limpa Suggestion área desconsiderar
	 */
	public void limparAreaDesconsiderar() {
		this.areaDesconsiderar = null;
	}

	/**
	 * Verifica se exibe ou não icone de excluir área considerar.
	 * 
	 * @return
	 */
	public boolean isMostrarLinkExcluirAreaConsiderar() {
		return this.getAreaConsiderar() != null;
	}

	/**
	 * Verifica se exibe ou não icone de excluir área desconsiderar.
	 * 
	 * @return
	 */
	public boolean isMostrarLinkExcluirAreaDesconsiderar() {
		return this.getAreaDesconsiderar() != null;
	}

	/**
	 * Método Limpar
	 */
	public String limparPesquisa() {
		this.tipoImpressao = 1;
		this.setDtFinal(null);
		this.setDtInicial(null);
		this.setAreaConsiderar(null);
		this.setAreaDesconsiderar(null);
		this.setInfAdicionais(false);
		return "relatorioProntuariosIdentificados";
	}

	/**
	 * Metodo voltar da tela pdf.
	 * 
	 * @return tela de pesquisa.
	 */
	public String voltar() {
		this.tipoImpressao = 1;
		return "relatorioProntuariosIdentificados";
	}

	/*
	 * GET's e SET's.
	 */

	public FccCentroCustos getAreaConsiderar() {
		return areaConsiderar;
	}

	public void setAreaConsiderar(FccCentroCustos areaConsiderar) {
		this.areaConsiderar = areaConsiderar;
	}

	public FccCentroCustos getAreaDesconsiderar() {
		return areaDesconsiderar;
	}

	public void setAreaDesconsiderar(FccCentroCustos areaDesconsiderar) {
		this.areaDesconsiderar = areaDesconsiderar;
	}

	public String getZpl() {
		return zpl;
	}

	public void setZpl(String zpl) {
		this.zpl = zpl;
	}

	public boolean isExibirBtnVisualizar() {
		return exibirBtnVisualizar;
	}

	public void setExibirBtnVisualizar(boolean exibirBtnVisualizar) {
		this.exibirBtnVisualizar = exibirBtnVisualizar;
	}

	public Integer getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(Integer tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

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

	public void setInfAdicionais(boolean infAdicionais) {
		this.infAdicionais = infAdicionais;
	}

	public boolean isInfAdicionais() {
		return infAdicionais;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

}