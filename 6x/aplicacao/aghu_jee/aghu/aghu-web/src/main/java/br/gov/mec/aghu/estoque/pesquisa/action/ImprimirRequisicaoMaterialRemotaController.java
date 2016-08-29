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
import java.util.Objects;

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
import br.gov.mec.aghu.cups.business.ICupsFacade;
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

public class ImprimirRequisicaoMaterialRemotaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}


	private static final Log LOG = LogFactory.getLog(ImprimirRequisicaoMaterialRemotaController.class);

	private static final long serialVersionUID = 5886053486816642874L;

	private static final String IMPRIMIR_REQUISICAO_MATERIAL_REMOTA 	= "imprimirRequisicaoMaterialRemota";
	private static final String IMPRIMIR_REQUISICAO_MATERIAL_REMOTA_PDF = "imprimirRequisicaoMaterialRemotaPdf";

	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	@EJB
	private ICupsFacade cupsFacade;

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
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Método responsável por gerar a coleção de dados.
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
			dadosReq = estoqueFacade.buscaMateriaisItensImprimir(numeroRM, orderBy, true);

			if(dadosReq!=null && dadosReq.getRequisicaoMaterial()!=null){
				SceReqMaterial requisicao = estoqueFacade.obterRequisicaoMaterial(dadosReq.getRequisicaoMaterial().getSeq());
				if(requisicao.getIndImpresso().equals(DominioImpresso.N)){
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
		return IMPRIMIR_REQUISICAO_MATERIAL_REMOTA_PDF;
		
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException
	{
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			dadosReq = estoqueFacade.buscaMateriaisItensImprimir(numeroRM, orderBy, true);

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

		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	
	//TODO verificar metodo no merge da classe
	public void directPrint(ImpImpressora impressoraCups) throws BaseException, JRException, SystemException, IOException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			dadosReq = estoqueFacade.buscaMateriaisItensImprimir(numeroRM, orderBy, true);
			
			if(dadosReq!=null && dadosReq.getRequisicaoMaterial()!=null){
				SceReqMaterial requisicao = dadosReq.getRequisicaoMaterial();
				if(!Objects.equals(requisicao.getIndImpresso(), DominioImpresso.R)){
					requisicao.setIndImpresso(DominioImpresso.R);
					requisicao.setNomeImpressora(impressoraCups.getFilaImpressora());					
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
			
			//cupsFacade.enviarPdfCupsPorClasse(AghuParametrosEnum.P_CUPS_CLASSE_A_PDF, getArquivoGerado(), DominioNomeRelatorio.IMPRIMIR_REQUISICAO_MATERIAL);
		
			sistemaImpressao.imprimir(impressoraCups, documento.getJasperPrint());
			
			String msg = this.getBundle().getString("MENSAGEM_SUCESSO_IMPRESSAO");
			this.apresentarMsgNegocio(Severity.INFO,msg);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			this.apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
		}
	}
	
	/**
	 * Prepara novas vias para serem impressas
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
	public Collection<RequisicaoMaterialItensVO> recuperarColecao() throws ApplicationBusinessException {
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
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "SCER_IMP_RM_LOCAL");
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
		try {
			params.put("nomeUsuario", registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getUsuario());
		} catch (ApplicationBusinessException e) {
			params.put("nomeUsuario",null);
		}	
		params.put("imprimirValores", dadosReq.getSituacao().toLowerCase().equals("efetivada"));
		
		/*Vias*/
		params.put("duasVias", duasVias.toString());
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		return	"br/gov/mec/aghu/estoque/report/imprimirRequisicaoMateriaisRemoto.jasper";
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, 
																SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public String voltar(){
		if(voltarPara != null){
			return voltarPara;
		}
		
		return IMPRIMIR_REQUISICAO_MATERIAL_REMOTA;
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

	protected ICupsFacade getCupsFacade() {
		return cupsFacade;
	}

	protected void setCupsFacade(ICupsFacade cupsFacade) {
		this.cupsFacade = cupsFacade;
	}
	
	
}