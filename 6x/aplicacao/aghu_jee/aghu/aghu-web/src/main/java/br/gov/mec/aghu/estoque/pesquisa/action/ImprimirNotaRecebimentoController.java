package br.gov.mec.aghu.estoque.pesquisa.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.NotaRecebimentoItensVO;
import br.gov.mec.aghu.estoque.vo.NotaRecebimentoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;

public class ImprimirNotaRecebimentoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(ImprimirNotaRecebimentoController.class);

	private static final long serialVersionUID = 2626631316486142858L;

	private static final String IMPRIMIR_NOTA_RECEBIMENTO 	  = "imprimirNotaRecebimento";
	private static final String IMPRIMIR_NOTA_RECEBIMENTO_PDF = "imprimirNotaRecebimentoPdf";

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;

	@EJB
	private IParametroFacade parametroFacade;

	/*	Dados que serão impressos em PDF. */
	private List<NotaRecebimentoItensVO> colecao = new ArrayList<NotaRecebimentoItensVO>();
	private NotaRecebimentoVO dadosNota;

	/*Filtro*/
	private DominioSimNao duasVias = DominioSimNao.N;
	private Integer numNotaRec;
	private boolean considerarNotaEmpenho;
	private String voltarPara = null;
	private boolean considerarUnidadeFuncional;
	
	private boolean esconderVoltar;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void inicio() {
	 

		HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		// Apenas quando tiver o parametro 'esconderVoltar' eh necessario carregar desta forma.
		if (request.getParameter("esconderVoltar") != null) {
			numNotaRec = Integer.valueOf((String)request.getParameter("numNotaRec"));
			considerarNotaEmpenho = Boolean.valueOf((String)request.getParameter("considerarNotaEmpenho"));
			considerarUnidadeFuncional = Boolean.valueOf((String)request.getParameter("considerarUnidadeFuncional"));
		}
		
	
	}
	
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException,
			IOException, DocumentException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			this.carregarColecao();
			estoqueBeanFacade.atualizarImpressaoNotaRecebimento(numNotaRec,
					nomeMicrocomputador);

			if (colecao == null || colecao.isEmpty()) {
				this.carregarColecao();
			}
			DocumentoJasper documento = gerarDocumento();

			media = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getPdfByteArray(false)));
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
			return null;
		}
		return IMPRIMIR_NOTA_RECEBIMENTO_PDF;
	}

	private void carregarColecao() throws BaseException {
		this.dadosNota = estoqueFacade.pesquisaDadosNotaRecebimento(this.numNotaRec, this.considerarNotaEmpenho);
		this.colecao = dadosNota.getItensNotaRecebimento();
		Collections.sort(this.colecao);
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {
			this.carregarColecao();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}
		
		final DocumentoJasper documento = gerarDocumento();
		
		if (this.considerarUnidadeFuncional) {
			final AghParametros param = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UNIDADE_ALMOX);
			final AghUnidadesFuncionais unidadesFuncionais = new AghUnidadesFuncionais(param.getVlrNumerico().shortValue());
			try {
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), unidadesFuncionais, TipoDocumentoImpressao.NR_RCEBIMENTO_MATERIAIS);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (SistemaImpressaoException e) {
				this.apresentarExcecaoNegocio(e);
			} catch (JRException e) {
				LOG.error(e.getMessage(),e);
				apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
		
		// else {
		try {
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	/**
	 * Prepara novas vias para serem impressas
	 */
	protected void prepararImprimirNovasVias(List<NotaRecebimentoItensVO> voPai, List<NotaRecebimentoItensVO> colVOPai, Byte nroViasPme) {
		
		for (NotaRecebimentoItensVO list : voPai) {
			list.setOrdemTela(1);
		}

		Integer ordemTela = 2;
		for (int i = 0; i < nroViasPme - 1; i++) {
			for (NotaRecebimentoItensVO notaRecebimentoItensVO : voPai) {

				NotaRecebimentoItensVO copia = notaRecebimentoItensVO.copiar();
				copia.setOrdemTela(ordemTela);

				colVOPai.add(copia);
			}
			ordemTela++;
		}
		voPai.addAll(colVOPai);
		duasVias = DominioSimNao.N;
	}

	@Override
	public Collection<NotaRecebimentoItensVO> recuperarColecao() throws ApplicationBusinessException {
		if(duasVias!=null && duasVias.equals(DominioSimNao.S)){
			List<NotaRecebimentoItensVO> colVOPai = new ArrayList<NotaRecebimentoItensVO>();
			this.prepararImprimirNovasVias(colecao, colVOPai, Byte.valueOf("2"));
		}
		return colecao;
	}
	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
		
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "SCER_IMP_NR_COMP");
		params.put("tituloRelatorio", "Nota de Recebimento de Materiais");
		params.put("totalRegistros", colecao.size()-1);

		params.put("tipoDocumento", dadosNota.getTipoDocumento());
		params.put("numeroDocumento", dadosNota.getNumeroDocumento());
		params.put("dtEmissaoDocumento", dadosNota.getDtEmissaoDocumento());
		params.put("numeroFornecedor", dadosNota.getNumeroFornecedor());
		params.put("cgcFornecedor", dadosNota.getCgcFornecedor() == null ? null : CoreUtil.formatarCNPJ(Long.valueOf(dadosNota.getCgcFornecedor())));
		params.put("razaoSocialFornecedor", dadosNota.getRazaoSocialFornecedor());
		params.put("numeroBanco", dadosNota.getNumeroBanco());
		params.put("numeroAgencia", dadosNota.getNumeroAgencia());
		params.put("numeroConta", dadosNota.getNumeroConta());
		params.put("numeroNotaReceb", dadosNota.getNumeroNotaReceb());
		params.put("dtGeracaoNota", dadosNota.getDtGeracaoNota());
		params.put("numeroEmpenho", dadosNota.getNumeroEmpenho());
		params.put("anoListaItens", dadosNota.getAnoListaItens());
		params.put("frfCodigo", dadosNota.getFrfCodigo());
		params.put("afnNumero", dadosNota.getAfnNumero());
		params.put("codigoConvenio", dadosNota.getCodigoConvenio());
		params.put("modalidadeEmpenho", dadosNota.getModalidadeEmpenho());
		params.put("valorEmpenho", dadosNota.getValorEmpenho() != null ? dadosNota.getValorEmpenho() : 0d);
		params.put("naturezaDespesa", dadosNota.getNaturezaDespesa());
		params.put("dtGeracaoAf", dadosNota.getDtGeracaoAf());
		params.put("codigoModalidadeLicitacao", dadosNota.getCodigoModalidadeLicitacao());
		params.put("artigoLicitacao", dadosNota.getArtigoLicitacao());
		params.put("incisoArtigoLicitacao", dadosNota.getIncisoArtigoLicitacao());
		params.put("dtCompetencia",
				dadosNota.getDtCompetencia() == null ? null : new SimpleDateFormat("MMM/yyyy", new Locale("pt", "BR")).format(dadosNota.getDtCompetencia()));
		params.put("numeroTitulo", dadosNota.getNumeroTitulo());
		params.put("dtVencimento", dadosNota.getDtVencimento());
		params.put("recebimento", dadosNota.getRecebimento());
		params.put("dtRecebimento", dadosNota.getDtRecebimento());
		params.put("confirmacao", dadosNota.getConfirmacao());

		/*Vias*/
		params.put("duasVias", duasVias.toString());
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		return	"br/gov/mec/aghu/estoque/report/imprimirNotaRecebimento.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, 
																SystemException, DocumentException {
		try {
			if (colecao == null || colecao.isEmpty()) {
				this.carregarColecao();
			}
			DocumentoJasper documento = gerarDocumento();

			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String voltar(){
		if(voltarPara != null){
			return voltarPara;
		}
		
		return IMPRIMIR_NOTA_RECEBIMENTO;
	}

	public DominioSimNao getDuasVias() {
		return duasVias;
	}

	public void setDuasVias(DominioSimNao duasVias) {
		this.duasVias = duasVias;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public IEstoqueBeanFacade getEstoqueBeanFacade() {
		return estoqueBeanFacade;
	}

	public void setEstoqueBeanFacade(IEstoqueBeanFacade estoqueBeanFacade) {
		this.estoqueBeanFacade = estoqueBeanFacade;
	}

	public List<NotaRecebimentoItensVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<NotaRecebimentoItensVO> colecao) {
		this.colecao = colecao;
	}

	public Integer getNumNotaRec() {
		return numNotaRec;
	}

	public void setNumNotaRec(Integer numNotaRec) {
		this.numNotaRec = numNotaRec;
	}

	public boolean isConsiderarNotaEmpenho() {
		return considerarNotaEmpenho;
	}

	public void setConsiderarNotaEmpenho(boolean considerarNotaEmpenho) {
		this.considerarNotaEmpenho = considerarNotaEmpenho;
	}

	public void setConsiderarUnidadeFuncional(boolean considerarUnidadeFuncional) {
		this.considerarUnidadeFuncional = considerarUnidadeFuncional;
	}

	public boolean isConsiderarUnidadeFuncional() {
		return considerarUnidadeFuncional;
	}

	public boolean isEsconderVoltar() {
		return esconderVoltar;
	}

	public void setEsconderVoltar(boolean esconderVoltar) {
		this.esconderVoltar = esconderVoltar;
	}
}