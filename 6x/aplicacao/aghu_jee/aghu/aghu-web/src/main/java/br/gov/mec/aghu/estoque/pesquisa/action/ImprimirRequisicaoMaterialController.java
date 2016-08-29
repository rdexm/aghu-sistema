package br.gov.mec.aghu.estoque.pesquisa.action;

import java.io.ByteArrayInputStream;
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
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.dominio.DominioImpresso;
import br.gov.mec.aghu.dominio.DominioOrderBy;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RequisicaoMaterialItensVO;
import br.gov.mec.aghu.estoque.vo.RequisicaoMaterialVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;



public class ImprimirRequisicaoMaterialController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final Log LOG = LogFactory.getLog(ImprimirRequisicaoMaterialController.class);

	private static final long serialVersionUID = 6205690148405047866L;
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private static final String IMPRIMIR_RM_PDF = "imprimirRequisicaoMaterialPdf";
	private static final String PESQUISAR_REQUISICOES = "estoque-consultarGeralRequisicaoMaterial";
	private static final String GERACAO_REQUISICOES = "estoque-geracaoRequisicaoMaterial";
	private static final String IMPRIMIR_RM = "imprimirRequisicaoMaterial";
	private static final String CONSULTAR_RM = "estoque-consultarRM";
	
	/*	Dados que serão impressos em PDF. */
	private List<RequisicaoMaterialItensVO> colecao = new ArrayList<RequisicaoMaterialItensVO>();
	private RequisicaoMaterialVO dadosReq = new RequisicaoMaterialVO();

	/*Filtro*/
	private DominioOrderBy[] orderByLst = DominioOrderBy.values();
	private DominioSimNao duasVias = DominioSimNao.N;
	private DominioOrderBy orderBy;
	private Integer numeroRM;
	private String voltarPara = null;


	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public String voltar(){
		if(PESQUISAR_REQUISICOES.equals(voltarPara)){
			return PESQUISAR_REQUISICOES;
		}else if(CONSULTAR_RM.equals(voltarPara)){
			return CONSULTAR_RM;
		}
		return null;
	}
	
	public String voltarFiltro(){
		return IMPRIMIR_RM;
	}
	
	public String voltarGeracao(){
		return GERACAO_REQUISICOES;
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
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try{
			dadosReq = estoqueFacade.buscaMateriaisItensImprimir(numeroRM, orderBy, false);

			if(dadosReq!=null && dadosReq.getRequisicaoMaterial()!=null){
				SceReqMaterial requisicao = dadosReq.getRequisicaoMaterial();
				if(requisicao.getIndImpresso().equals(DominioImpresso.N) && StringUtils.isBlank(this.voltarPara)){
					requisicao.setIndImpresso(DominioImpresso.L);
					estoqueBeanFacade.gravarRequisicaoMaterial(requisicao, nomeMicrocomputador);
				}
				this.colecao = dadosReq.getItemVO();
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);		
		return null;
		}
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return IMPRIMIR_RM_PDF;
		
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws JRException
	 */
	public void directPrint()  {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			dadosReq = estoqueFacade.buscaMateriaisItensImprimir(numeroRM, orderBy, false);

			if(dadosReq!=null && dadosReq.getRequisicaoMaterial()!=null){
				SceReqMaterial requisicao = dadosReq.getRequisicaoMaterial();
				if(requisicao.getIndImpresso().equals(DominioImpresso.N) && StringUtils.isBlank(this.voltarPara)){
					requisicao.setIndImpresso(DominioImpresso.L);					
					estoqueBeanFacade.gravarRequisicaoMaterial(requisicao, nomeMicrocomputador);
				}
				this.colecao = dadosReq.getItemVO();
			}
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public void directPrint(ImpImpressora impressoraCups) throws BaseException, JRException, SystemException, IOException, DocumentException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			dadosReq = estoqueFacade.buscaMateriaisItensImprimir(numeroRM, orderBy, false);

			if(dadosReq!=null && dadosReq.getRequisicaoMaterial()!=null){
				SceReqMaterial requisicao = dadosReq.getRequisicaoMaterial();
				if(requisicao.getIndImpresso().equals(DominioImpresso.N)){
					requisicao.setIndImpresso(DominioImpresso.L);					
					estoqueBeanFacade.gravarRequisicaoMaterial(requisicao, nomeMicrocomputador);
				}
				this.colecao = dadosReq.getItemVO();
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}

		print();

		try {			
			// Gera o PDF
			DocumentoJasper documento = gerarDocumento();
			sistemaImpressao.imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);			
			//StatusMessages.instance().add(Severity.ERROR,e.getLocalizedMessage());
		}
	}
	
	
	/**
	 * Prepara novas vias para serem impressas
	 * 
	 * @param voPai
	 * @param colVOPai
	 * @param internacao
	 */
	protected void prepararImprimirNovasVias(List<RequisicaoMaterialItensVO> voPai, List<RequisicaoMaterialItensVO> colVOPai, Byte nroViasPme) {
		
		for (RequisicaoMaterialItensVO list : voPai) {
			list.setOrdemTela(1);
		}

		Integer ordemTela = 2;
		for (int i = 0; i < nroViasPme - 1; i++) {
			for (RequisicaoMaterialItensVO requisicaoMaterialItensVO : voPai) {

				RequisicaoMaterialItensVO copia = requisicaoMaterialItensVO.copiar();
				copia.setOrdemTela(ordemTela);

				colVOPai.add(copia);
			}
			ordemTela++;
		}
		voPai.addAll(colVOPai);
		duasVias = DominioSimNao.N;
	}
	
	@Override
	public Collection<RequisicaoMaterialItensVO> recuperarColecao() {
		if(duasVias!=null && duasVias.equals(DominioSimNao.S)){
			List<RequisicaoMaterialItensVO> colVOPai = new ArrayList<RequisicaoMaterialItensVO>();
			this.prepararImprimirNovasVias(colecao, colVOPai, Byte.valueOf("2"));
		}
		return colecao;		
	}	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "SCER_IMP_RM");
		params.put("tituloRelatorio", "Requisição de Materiais");
		params.put("totalRegistros", colecao.size()-1);

		/*outros parâmetros e valores*/
		params.put("reqMaterial", dadosReq.getReqMaterial());
		params.put("almoxSeq", dadosReq.getAlmoxSeq());
		params.put("almoxDesc", dadosReq.getAlmoxDesc());
		params.put("situacao", dadosReq.getSituacao());
		params.put("dataGeracao", dadosReq.getDataGeracao());
		params.put("dataConfirmacao", dadosReq.getDataConfirmacao());
		params.put("dataEfetivacao", dadosReq.getDataEfetivacao());
		params.put("centroCustoCodigo", dadosReq.getCentroCustoCodigo());
		params.put("centroCustoAplicacaoCodigo", dadosReq.getCentroCustoAplicacaoCodigo());
		params.put("centroCustoDescricao", dadosReq.getCentroCustoDescricao());
		params.put("centroCustoAplicacaoDescricao", dadosReq.getCentroCustoAplicacaoDescricao());
		params.put("tipoMovimentoSeq", dadosReq.getTipoMovimentoSeq());
		params.put("tipoMovimentoComplemento", dadosReq.getTipoMovimentoComplemento());
		params.put("nomePessoa", dadosReq.getNomePessoa());
		params.put("numeroRamal", dadosReq.getNumeroRamal());
		params.put("nomePaciente", dadosReq.getPaciente());
		params.put("prontuario", dadosReq.getProntuario());
		
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
		params.put("logoCorrosivo", servletContext.getRealPath("/img/corrosivo.gif"));
		params.put("logoInflamavel", servletContext.getRealPath("/img/inflamavel.gif"));
		params.put("logoRadioativo", servletContext.getRealPath("/img/radioativo.gif"));
		params.put("logoReativo", servletContext.getRealPath("/img/reativo.gif"));
		params.put("logoToxico", servletContext.getRealPath("/img/toxico.gif"));
		
		try {
			params.put("nomeUsuario", registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getUsuario());
		} catch (ApplicationBusinessException e) {
			params.put("nomeUsuario",null);
		}
		params.put("imprimirValores", dadosReq.getSituacao().toLowerCase().equals("efetivada"));
		params.put("observacaoRequisicao", dadosReq.getObservacao());
		
		/*Vias*/
		params.put("duasVias", duasVias.toString());
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		return	"br/gov/mec/aghu/estoque/report/imprimirRequisicaoMateriais.jasper";
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
	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public List<RequisicaoMaterialItensVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RequisicaoMaterialItensVO> colecao) {
		this.colecao = colecao;
	}

	public DominioOrderBy[] getOrderByLst() {
		return orderByLst;
	}

	public void setOrderByLst(DominioOrderBy[] orderByLst) {
		this.orderByLst = orderByLst;
	}

	public DominioSimNao getDuasVias() {
		return duasVias;
	}

	public void setDuasVias(DominioSimNao duasVias) {
		this.duasVias = duasVias;
	}

	public Integer getNumeroRM() {
		return numeroRM;
	}

	public void setNumeroRM(Integer numeroRM) {
		this.numeroRM = numeroRM;
	}

	public void setOrderBy(DominioOrderBy orderBy) {
		this.orderBy = orderBy;
	}

	public DominioOrderBy getOrderBy() {
		return orderBy;
	}

	public RequisicaoMaterialVO getDadosReq() {
		return dadosReq;
	}

	public void setDadosReq(RequisicaoMaterialVO dadosReq) {
		this.dadosReq = dadosReq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}