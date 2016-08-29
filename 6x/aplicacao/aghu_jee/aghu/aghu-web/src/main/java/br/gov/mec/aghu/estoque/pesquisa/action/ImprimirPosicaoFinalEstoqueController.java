package br.gov.mec.aghu.estoque.pesquisa.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.EstocavelPosicaoFinalEstoque;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.estoque.vo.OrdenacaoPosicaoFinalEstoque;
import br.gov.mec.aghu.estoque.vo.PosicaoFinalEstoqueVO;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import net.sf.jasperreports.engine.JRException;

public class ImprimirPosicaoFinalEstoqueController extends ActionReport {

	private static final String _HIFEN_ = " - ";

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final Log LOG = LogFactory.getLog(ImprimirPosicaoFinalEstoqueController.class);

	private static final long serialVersionUID = 6205690148405047866L;

	private static final String POSICAO_FINAL_ESTOQUE 	  = "posicaoFinalEstoque";
	
	private static final String POSICAO_FINAL_ESTOQUE_PDF = "posicaoFinalEstoquePdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	/*	Dados que serão impressos em PDF. */
	private List<PosicaoFinalEstoqueVO> colecao = new ArrayList<PosicaoFinalEstoqueVO>();
	
	private ScoFornecedor fornecedor;
	
	private AfaTipoUsoMdto tipoUsoMdto;
	
	private SceAlmoxarifado almoxarifado;

	/*Filtro*/
	private OrdenacaoPosicaoFinalEstoque orderBy;
	private EstocavelPosicaoFinalEstoque estocavel;
	private MovimentoMaterialVO mvtodataCompetencia;
	private ScoGrupoMaterial grupo;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Determina qual fornecedor será utilizado na consulta de estoque geral
	 * @return
	 */
	private Integer getFiltroFornecedor(){
		
		if(this.fornecedor != null){
			// Prepara a consulta através do fornecedor informado
			return fornecedor.getNumero();
			
		} else {
			// Prepara a consulta através do fornecedor padrão do HU
			try {
				return parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		return null;
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		try{

			final Date dtcomp = this.mvtodataCompetencia!=null ? this.mvtodataCompetencia.getCompetencia():null;
			final Integer grupoSeq = this.grupo != null? this.grupo.getCodigo():null;
			
			if(dtcomp == null){
				apresentarMsgNegocio(Severity.ERROR, "Um valor é obrigatório para o campo Mês Competência");		
				return null;
			}
			
			if(grupoSeq	== null){
				apresentarMsgNegocio(Severity.ERROR, "Um valor é obrigatório para o campo Grupo");
				return null;
			}

			String siglaTipoUsoMdto = this.tipoUsoMdto != null ? this.tipoUsoMdto.getSigla() : null;
			Short almoxSeq = this.almoxarifado != null ? this.almoxarifado.getSeq() : null;

			this.colecao = this.estoqueFacade.buscaDadosPosicaoFinalEstoque(dtcomp, grupoSeq, estocavel.toString(), orderBy.toString(), this.getFiltroFornecedor(), siglaTipoUsoMdto, almoxSeq);

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return POSICAO_FINAL_ESTOQUE_PDF;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			Date dtcomp = this.mvtodataCompetencia!=null?this.mvtodataCompetencia.getCompetencia():null;
			Integer grupoSeq = this.grupo != null? this.grupo.getCodigo():null;

			String siglaTipoUsoMdto = this.tipoUsoMdto != null ? this.tipoUsoMdto.getSigla() : null;
			Short almoxSeq = this.almoxarifado != null ? this.almoxarifado.getSeq() : null;

			this.colecao = estoqueFacade.buscaDadosPosicaoFinalEstoque(dtcomp, grupoSeq, estocavel.toString(), orderBy.toString(), this.getFiltroFornecedor(), siglaTipoUsoMdto, almoxSeq);

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
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	
	
	@Override
	public Collection<PosicaoFinalEstoqueVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", DateUtil.obterDataFormatada(dataAtual, DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "SCER_POS_FINAL_ESTQ");

		params.put("totalRegistros", colecao.size()-1);
		params.put("grupoMaterial", this.grupo.getCodigo() + _HIFEN_ + this.grupo.getDescricao());

		String descricaoFornecedor = null;
		if(this.fornecedor != null){
			descricaoFornecedor = this.fornecedor.getNumero() + _HIFEN_ + this.fornecedor.getRazaoSocial();
		} else{
			try {
				Integer numero = parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
				ScoFornecedor fornecedorPadrao = this.comprasFacade.obterFornecedorPorNumero(numero);
				descricaoFornecedor =  fornecedorPadrao.getNumero() + _HIFEN_ + fornecedorPadrao.getRazaoSocial();
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
			}	
		}

		StringBuffer tituloRelatorio = new StringBuffer(50);
		tituloRelatorio.append("Posição Final de Estoque - " + new SimpleDateFormat("MMMM/yyyy", new Locale("pt", "BR")).format(mvtodataCompetencia.getCompetencia())); 

		params.put("tipoUsoMedicamento", this.tipoUsoMdto != null ? this.tipoUsoMdto.getSigla()+_HIFEN_+this.tipoUsoMdto.getDescricao():null);
		
		if(this.almoxarifado != null){
			tituloRelatorio.append(" Almoxarifado: " + this.almoxarifado.getSeq() + _HIFEN_ + this.almoxarifado.getDescricao()); 
		}else{
			tituloRelatorio.append(" Fornecedor: ").append(descricaoFornecedor);
		}
		
		params.put("tituloRelatorio", tituloRelatorio.toString());
		params.put("filtroTodos", this.estocavel.equals(EstocavelPosicaoFinalEstoque.T));

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		return	"br/gov/mec/aghu/estoque/report/imprimirPosicaoFinalEstoque.jasper";
	}
	
	public void limparPesquisa(){
		this.mvtodataCompetencia = null;
		this.grupo = null;
		this.estocavel = null;
		this.orderBy = null;
		this.fornecedor = null;
	}
	
	/**
	 * Método que realiza a pesquisa de competencias de estoque geral, por mes e ano.
	 */
	public List<MovimentoMaterialVO> pesquisarDatasCompetencias(String paramPesquisa){
		List<MovimentoMaterialVO> lista = null;
		try {
			lista = estoqueFacade.pesquisarDatasCompetenciasMovimentoMaterialPorMesAno(paramPesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return lista;
	}
	
	/**
	 * Pesquisa as tipos de uso de medicamentos ativos
	 */
	public List<AfaTipoUsoMdto> pesquisaTipoUsoMdtoAtivos(String siglaOuDescricao) {
		return farmaciaApoioFacade.pesquisaTipoUsoMdtoAtivos(siglaOuDescricao);
	}
	
	/**
	 * Pesquisa de almoxarifados
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifados(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}
	
	/**
	 * Pesquisa para o suggestion de fornecedores
	 */
	public List<ScoFornecedor> pesquisarFornecedoresPorNumeroRazaoSocial(String objPesquisa) {
		return this.comprasFacade.pesquisarFornecedoresPorNumeroRazaoSocial(objPesquisa);
	}
	
	/**
	 * Realiza a pesquisa para a SB de Grupos
	 */
	public List<ScoGrupoMaterial> obterGrupos(String objPesquisa) {
		return this.comprasFacade.obterGrupoMaterialPorSeqDescricao(objPesquisa);
	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, 
																JRException, SystemException,	DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	
	public String voltar(){
		return POSICAO_FINAL_ESTOQUE;
	}
	
	

	public List<PosicaoFinalEstoqueVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<PosicaoFinalEstoqueVO> colecao) {
		this.colecao = colecao;
	}

	public OrdenacaoPosicaoFinalEstoque getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(OrdenacaoPosicaoFinalEstoque orderBy) {
		this.orderBy = orderBy;
	}

	public EstocavelPosicaoFinalEstoque getEstocavel() {
		return estocavel;
	}

	public void setEstocavel(EstocavelPosicaoFinalEstoque estocavel) {
		this.estocavel = estocavel;
	}

	public MovimentoMaterialVO getMvtodataCompetencia() {
		return mvtodataCompetencia;
	}

	public void setMvtodataCompetencia(MovimentoMaterialVO mvtodataCompetencia) {
		this.mvtodataCompetencia = mvtodataCompetencia;
	}

	public ScoGrupoMaterial getGrupo() {
		return grupo;
	}

	public void setGrupo(ScoGrupoMaterial grupo) {
		this.grupo = grupo;
	}
	
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}
	
	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public AfaTipoUsoMdto getTipoUsoMdto() {
		return tipoUsoMdto;
	}

	public void setTipoUsoMdto(AfaTipoUsoMdto tipoUsoMdto) {
		this.tipoUsoMdto = tipoUsoMdto;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}
}