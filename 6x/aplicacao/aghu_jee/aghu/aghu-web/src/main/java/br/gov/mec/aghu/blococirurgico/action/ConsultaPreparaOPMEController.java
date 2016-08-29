package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.ConsultaPreparaOPMEFiltroVO;
import br.gov.mec.aghu.blococirurgico.vo.ConsultarHistoricoProcessoOpmeVO;
import br.gov.mec.aghu.blococirurgico.vo.InfoProcdCirgRequisicaoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.MateriaisProcedimentoOPMEVO;
import br.gov.mec.aghu.blococirurgico.vo.RequisicoesOPMEsProcedimentosVinculadosVO;
import br.gov.mec.aghu.blococirurgico.vo.SolicitacaoCompraMaterialVO;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ConsultaPreparaOPMEController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ConsultaPreparaOPMEController.class);

	private static final long serialVersionUID = 8265416401296765542L;
	private static final String VISUALIZA_AUTORIZACAO = "visualizarAutorizacaoOpmePdf";
	private static final String VISUALIZAR_ORCAMENTO_COMPRA = "consultaPreparaOPME";
	private static final String SOLICITAR_COMPRA_CRUD = "compras-solicitarCompraCRUD";
	//private static final String SOLICITACAO_COMPRA_CRUD = "compras-solicitacaoCompraCRUD";
	
	
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
		
	private String voltarParaUrl;
	private InfoProcdCirgRequisicaoOPMEVO vo;
	private List<MateriaisProcedimentoOPMEVO> listMateriaisProcedimentos;
	private Boolean pesquisar;
	private ConsultaPreparaOPMEFiltroVO filtroVO;
	private Short requisicaoSeqSelecionada;
	private MateriaisProcedimentoOPMEVO matProcdOPMESelecionado;
	private RequisicoesOPMEsProcedimentosVinculadosVO prepararOpme;
	private MateriaisProcedimentoOPMEVO itemSelecionadoTable;
	private List<ConsultarHistoricoProcessoOpmeVO> listaHistoricoVO;
	private ConsultarHistoricoProcessoOpmeVO itemHistoricoSelecionado;
	private ScoSolicitacaoDeCompra scoSolicitacaoDeCompra;
	
	public void inicio() {
		popularInformacoesProcedimentoCirurgicoAtravesRequisicao();
		popularFiltroPesquisaProcedimentoMateriais();	
	}
	
	private void popularInformacoesProcedimentoCirurgicoAtravesRequisicao() {
		try {
			InfoProcdCirgRequisicaoOPMEVO vo = blocoCirurgicoOpmesFacade.consultarInformacoesProcedimentoCirurgicoAtravesRequisicaoOPMESemSituacao(requisicaoSeqSelecionada); 
			this.vo = vo != null ? vo : new InfoProcdCirgRequisicaoOPMEVO();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Excecao Capturada: ", e);
		}
	}

	private void popularFiltroPesquisaProcedimentoMateriais() {
		filtroVO =  new ConsultaPreparaOPMEFiltroVO();
		filtroVO.setRequisicaoSelecionada(requisicaoSeqSelecionada);
		
		this.pesquisar();
	}
	
	public void download(MateriaisProcedimentoOPMEVO item){
	    FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse resp = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	    resp.setContentType("application/zip, application/octet-stream");
	    resp.setHeader("Content-Disposition", "attachment;filename=" + "arquivo_orcamento_opme_.zip");
	    try {
			resp.getOutputStream().write(item.getAnexoOrcamento());
			context.responseComplete();
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		}
	}
	
	public String redirecionarSolicitacaoCompra() {
		return SOLICITAR_COMPRA_CRUD;
	}
	
	public void pesquisar(){
		try {
			listMateriaisProcedimentos = blocoCirurgicoOpmesFacade.consultarMaterialProcedimentoOPME(filtroVO);
			pesquisar = Boolean.TRUE;
			this.validarListaPesquisada();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Excecao Capturada: ", e);
		}
	}
	
	private void validarListaPesquisada(){
		if(listMateriaisProcedimentos != null && !listMateriaisProcedimentos.isEmpty()){
			Long codMaterial = null;
			for(int i = 0; i < listMateriaisProcedimentos.size(); i++){
				if(i > 0 && listMateriaisProcedimentos.get(i).getCodTabela() != null){
					if(listMateriaisProcedimentos.get(i).getCodTabela().equals(codMaterial)){
						listMateriaisProcedimentos.get(i).setMaterialSUS("");
						listMateriaisProcedimentos.get(i).setLicitado("");
						listMateriaisProcedimentos.get(i).setQtdeAut(null);
						listMateriaisProcedimentos.get(i).setValorUnitario(null);
						listMateriaisProcedimentos.get(i).setValorTotal(null);
					}
				}
				codMaterial = listMateriaisProcedimentos.get(i).getCodTabela();
			}
		}
	}

	public String voltar() {
		return voltarParaUrl;
	}

	public String visualizarAutorizacao() {
		return VISUALIZA_AUTORIZACAO;
	}

	public String visualizarOrcamentoCompra() {
		return VISUALIZAR_ORCAMENTO_COMPRA;
	}

	public void solicitarCompraMaterial() {
		try {
			this.setScoSolicitacaoDeCompra(blocoCirurgicoOpmesFacade.persistirSolicitacaoCompraMaterial(popularSolicitacaoCompraMaterialVO(matProcdOPMESelecionado)));
			this.apresentarMsgNegocio(Severity.INFO, "MSG_SUCESSO_GERACAO_SC_REQ");
			pesquisar();
			closeDialog("modalSolicitacaoCompraWG");
			openDialog("modalTelaConsultaSolicCompraWG");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			LOG.error("Excecao Capturada: ", e);
		}
	}
	
	public void abreModalSolicitarCompraMaterial(MateriaisProcedimentoOPMEVO item){
		this.setMatProcdOPMESelecionado(item);
		openDialog("modalSolicitacaoCompraWG");
	}

	private SolicitacaoCompraMaterialVO popularSolicitacaoCompraMaterialVO(MateriaisProcedimentoOPMEVO matProcdOPMESelecionado) throws ApplicationBusinessException {
		SolicitacaoCompraMaterialVO vo = new SolicitacaoCompraMaterialVO();
		vo.setServidorLogado(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		vo.setInfoReqOPME(this.vo);
		vo.setMatProcOPME(matProcdOPMESelecionado);
		return vo;
	}
	
	public void consultarHistorico(){
		this.listaHistoricoVO =	this.blocoCirurgicoOpmesFacade.consultarHistorico(this.prepararOpme.getFluxoSeq());
		
		if(this.listaHistoricoVO.isEmpty()){
			this.apresentarMsgNegocio(Severity.ERROR, "M01_SEM_REGISTROS");
		}else{
			openDialog("modalConsultarHistoricoProcessoWG");
		}
	
	}
	
	public String downloadArquivoLaudo(MateriaisProcedimentoOPMEVO item)  {
		return this.downloadViaHttpServletResponse(item.getAnexoOrcamento(), "arquivo_orcamento_opme_.zip");
	}
	
	private String downloadViaHttpServletResponse(byte[] dados, String nome) {

		// Instancia uma HTTP Response
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) context
				.getExternalContext().getResponse();
		// Seta o tipo de MIME
		response.setContentType("image/png");
		// Obtém o nome do arquivo em anexo: SOE_SEQ + SEQP com 3 zeros à esquerda + extensão
		String nomeArquivo = nome;
		// Seta o arquivo no cabeçalho
		response.addHeader("Content-disposition", "attachment; filename=\"" + nomeArquivo + "\"");
		// Escreve a resposta no HTTP RESPONSE
		ServletOutputStream os = null;
		try {
			os = response.getOutputStream();
			os.write(dados); // Escrevemos o STREAM de resposta/RESPONSE
			os.flush();
			context.responseComplete();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		} finally {
			IOUtils.closeQuietly(os);
		}
		return null;
	}
	
	public String redirecionarSolicitarCompraCRUD(){
		return SOLICITAR_COMPRA_CRUD;
	}
	
	// get set
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public InfoProcdCirgRequisicaoOPMEVO getVo() {
		return vo;
	}

	public void setVo(InfoProcdCirgRequisicaoOPMEVO vo) {
		this.vo = vo;
	}

	public List<MateriaisProcedimentoOPMEVO> getListMateriaisProcedimentos() {
		return listMateriaisProcedimentos;
	}

	public void setListMateriaisProcedimentos(List<MateriaisProcedimentoOPMEVO> listMateriaisProcedimentos) {
		this.listMateriaisProcedimentos = listMateriaisProcedimentos;
	}

	public Boolean getPesquisar() {
		return pesquisar;
	}

	public void setPesquisar(Boolean pesquisar) {
		this.pesquisar = pesquisar;
	}

	public ConsultaPreparaOPMEFiltroVO getFiltroVO() {
		return filtroVO;
	}

	public void setFiltroVO(ConsultaPreparaOPMEFiltroVO filtroVO) {
		this.filtroVO = filtroVO;
	}

	public Short getRequisicaoSeqSelecionada() {
		return requisicaoSeqSelecionada;
	}

	public void setRequisicaoSeqSelecionada(Short requisicaoSeqSelecionada) {
		this.requisicaoSeqSelecionada = requisicaoSeqSelecionada;
	}

	public MateriaisProcedimentoOPMEVO getMatProcdOPMESelecionado() {
		return matProcdOPMESelecionado;
	}

	public void setMatProcdOPMESelecionado(MateriaisProcedimentoOPMEVO matProcdOPMESelecionado) {
		this.matProcdOPMESelecionado = matProcdOPMESelecionado;
	}


	public RequisicoesOPMEsProcedimentosVinculadosVO getPrepararOpme() {
		return prepararOpme;
	}


	public void setPrepararOpme(RequisicoesOPMEsProcedimentosVinculadosVO prepararOpme) {
		this.prepararOpme = prepararOpme;
	}

	public MateriaisProcedimentoOPMEVO getItemSelecionadoTable() {
		return itemSelecionadoTable;
	}

	public void setItemSelecionadoTable(MateriaisProcedimentoOPMEVO itemSelecionadoTable) {
		this.itemSelecionadoTable = itemSelecionadoTable;
	}

	public List<ConsultarHistoricoProcessoOpmeVO> getListaHistoricoVO() {
		return listaHistoricoVO;
	}

	public void setListaHistoricoVO(List<ConsultarHistoricoProcessoOpmeVO> listaHistoricoVO) {
		this.listaHistoricoVO = listaHistoricoVO;
	}

	public ConsultarHistoricoProcessoOpmeVO getItemHistoricoSelecionado() {
		return itemHistoricoSelecionado;
	}

	public void setItemHistoricoSelecionado(ConsultarHistoricoProcessoOpmeVO itemHistoricoSelecionado) {
		this.itemHistoricoSelecionado = itemHistoricoSelecionado;
	}

	public ScoSolicitacaoDeCompra getScoSolicitacaoDeCompra() {
		return scoSolicitacaoDeCompra;
	}

	public void setScoSolicitacaoDeCompra(ScoSolicitacaoDeCompra scoSolicitacaoDeCompra) {
		this.scoSolicitacaoDeCompra = scoSolicitacaoDeCompra;
	}
}
