package br.gov.mec.aghu.estoque.action;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemDevolucaoAlmoxarifadoVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceDocumentoValidade;
import br.gov.mec.aghu.model.SceDocumentoValidadeID;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceLote;
import br.gov.mec.aghu.model.SceLoteDocumento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para tela Manter Devolução ao Almoxarifado.
 * 
 * @author diego.pacheco
 *
 */


public class ManutencaoDevolucaoAlmoxarifadoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ManutencaoDevolucaoAlmoxarifadoController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 3525227507850219627L;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	private List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO;
	
	// Atributos para Manter devolução ao almoxarifado
	private Integer daSeq;
	private Date daDtGeracao;
	private RapServidores daResponsavel;
	private SceAlmoxarifado daAlmoxarifado;
	private FccCentroCustos daCentroCusto;
	private String daObservacao;
	private SceDevolucaoAlmoxarifado devolucaoAlmoxarifado;
	
	// Atributos para Manter item de DA (devolução ao almoxarifado)
	private SceEstoqueAlmoxarifado itemDaEstoqueAlmoxarifado;
	private Integer itemDaQtde;
	private ItemDevolucaoAlmoxarifadoVO itemDaSelecionado;
	// parametros para seleção do item de DA (radio)
	private Integer itemDaDalSeq;
	private Integer itemDaEalSeq;
	
	// Atributos para Manter validade de item de DA
	private Date validadeItemDaData;
	private Integer validadeItemDaQtde;
	private SceDocumentoValidade validadeSelecionada;
	// parametros para seleção da validade (radio)
	private String validDataStr;
	private Date validData;
	
	// Atributos para Manter lote de item de DA
	private SceLote loteItemDaLote;
	private ScoFornecedor loteItemDaFornecedor;
	private Integer loteItemDaQtde;
	
	private final static String DATE_PATTERN_DDMMYYYY = "dd/MM/yyyy";
	private final static Integer TEMP_DEVOLUCAO_ALMOX_ID = -1; 
	
	
	public void iniciar() {
	 

	 

		if (daSeq != null) {
			devolucaoAlmoxarifado = estoqueFacade.pesquisarDevolucaoAlmoxarifado(daSeq, null, null, false, false,null,null).get(0);
			carregarListaItemDevolucaoAlmoxarifado(daSeq);
			daDtGeracao = devolucaoAlmoxarifado.getDtGeracao();
			daResponsavel = devolucaoAlmoxarifado.getServidor();
			daAlmoxarifado = devolucaoAlmoxarifado.getAlmoxarifado();
			daCentroCusto = devolucaoAlmoxarifado.getCentroCusto();
			daObservacao = devolucaoAlmoxarifado.getObservacao();
		} else {
			devolucaoAlmoxarifado = new SceDevolucaoAlmoxarifado();
			carregarListaItemDevolucaoAlmoxarifado(null);
			daDtGeracao = new Date();
			daResponsavel = getUsuarioLogado();	
		}
	
	}
	
	
	/**
	 * Metodo para pesquisa na suggestion box de almoxarifado.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifado(String paramPesquisa) {
		return this.estoqueFacade.pesquisarAlmoxarifadosAtivosPorSeqOuDescricao(paramPesquisa);
	}
	
	/**
	 * Metodo para pesquisa na suggestion box de centro custo.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustosAtivosPorCodigoOuDescricao(paramPesquisa);
	}
	
	/**
	 * Metodo para pesquisa na suggestion box de lotes.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<SceLote> pesquisarLotes(String paramPesquisa) {
		return this.estoqueFacade.listarLotesPorCodigoOuMarcaComercialEMaterial(
				paramPesquisa, this.itemDaSelecionado.getEstoqueAlmoxarifado().getMaterial().getCodigo());
	}
	
	/**
	 * Metodo para pesquisa na suggestion box de fornecedores.
	 * 
	 * @param paramPesquisa
	 * @return
	 */
	public List<ScoFornecedor> pesquisarFornecedores(String paramPesquisa) {
		return this.comprasFacade.obterFornecedor(paramPesquisa);
	}	
	
	private void carregarListaItemDevolucaoAlmoxarifado(Integer dalSeq) {
		if (dalSeq != null) {
			listaItemDevolucaoAlmoxarifadoVO = estoqueFacade
					.pesquisarItensComMaterialLoteDocumentosPorDevolucaoAlmoxarifado(dalSeq);			
		} else {
			listaItemDevolucaoAlmoxarifadoVO = new ArrayList<ItemDevolucaoAlmoxarifadoVO>();
		}
	}
	
	public void carregarItemDevolucaoAlmoxarifado() {
		limparItemDa();
		limparValidadeItemDa();
		limparLoteItemDa();
		validDataStr = null;
		validData = null;
		validadeSelecionada = null;
		
		for (ItemDevolucaoAlmoxarifadoVO itemDaVO : listaItemDevolucaoAlmoxarifadoVO) {
			if (itemDaDalSeq.equals(itemDaVO.getDalSeq()) && itemDaEalSeq.equals(itemDaVO.getEalSeq())) {
				itemDaSelecionado = itemDaVO;	
				break;
			}
		}
	}
	
	public void carregarValidadeItemDa() {
		try {
			validData = converterStringParaData(validDataStr);
		} catch (ParseException e) {
			LOG.error(e.getMessage(),e);
		}
		for (SceDocumentoValidade validade : itemDaSelecionado.getListaValidades()) {
			if (validData.getTime() == validade.getId().getData().getTime()) {
				validadeSelecionada = validade;
				break;
			}
		}
	}
	
	/**
	 * Adiciona um item de DA
	 */
	public void adicionarItemDa() {
		ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO = new ItemDevolucaoAlmoxarifadoVO();
		
		itemDaSelecionado = null;
		
		if (devolucaoAlmoxarifado.getSeq() == null) {
			/*	Necessário setar -1 como id para permitir a seleção do item 
				por radio button quando é uma nova DA	*/
			itemDevolucaoAlmoxarifadoVO.setDalSeq(TEMP_DEVOLUCAO_ALMOX_ID);
			devolucaoAlmoxarifado.setSeq(TEMP_DEVOLUCAO_ALMOX_ID);
		} else {
			itemDevolucaoAlmoxarifadoVO.setDalSeq(devolucaoAlmoxarifado.getSeq());
		}
		
		// Seta atributos informados para item da DA
		itemDevolucaoAlmoxarifadoVO.setEalSeq(itemDaEstoqueAlmoxarifado.getSeq());
		itemDevolucaoAlmoxarifadoVO.setCodigoMaterial(itemDaEstoqueAlmoxarifado.getMaterial().getCodigo());
		itemDevolucaoAlmoxarifadoVO.setNomeMaterial(itemDaEstoqueAlmoxarifado.getMaterial().getNome());
		itemDevolucaoAlmoxarifadoVO.setNroFornecedor(itemDaEstoqueAlmoxarifado.getFornecedor().getNumero());
		itemDevolucaoAlmoxarifadoVO.setNomeFornecedor(itemDaEstoqueAlmoxarifado.getFornecedor().getNomeFantasia());
		itemDevolucaoAlmoxarifadoVO.setEstoqueAlmoxarifado(itemDaEstoqueAlmoxarifado);
		itemDevolucaoAlmoxarifadoVO.setQuantidade(itemDaQtde);
		itemDevolucaoAlmoxarifadoVO.setUnidadeMedida(itemDaEstoqueAlmoxarifado.getUnidadeMedida().getCodigo());
		
		try {
			estoqueFacade.verificarItemDaDuplicado(itemDevolucaoAlmoxarifadoVO, listaItemDevolucaoAlmoxarifadoVO);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			return;
		}

		listaItemDevolucaoAlmoxarifadoVO.add(itemDevolucaoAlmoxarifadoVO);	
		
		limparItemDa();
	}
	
	public void removerItemDa(ItemDevolucaoAlmoxarifadoVO itemDevolucaoAlmoxarifadoVO) {
		listaItemDevolucaoAlmoxarifadoVO.remove(itemDevolucaoAlmoxarifadoVO);
		if (itemDevolucaoAlmoxarifadoVO == itemDaSelecionado) {
			itemDaSelecionado = null;
		}
	}
	
	public void adicionarValidadeItemDa() {
		SceDocumentoValidade documentoValidade = new SceDocumentoValidade();
		SceDocumentoValidadeID documentoValidadeId = new SceDocumentoValidadeID();
		documentoValidadeId.setData(validadeItemDaData);
		documentoValidade.setId(documentoValidadeId);
		documentoValidade.setQuantidade(validadeItemDaQtde);
		
		try {
			estoqueFacade.verificarQuantidadeValidadesContraItemDa(itemDaSelecionado, documentoValidade);
			estoqueFacade.verificarQuantidadeDocumentoValidade(itemDaSelecionado, documentoValidade);
			estoqueFacade.verificarValidadeDataDuplicada(itemDaSelecionado.getListaValidades(), documentoValidade);
			itemDaSelecionado.getListaValidades().add(documentoValidade);
			limparValidadeItemDa();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	public void removerValidadeItemDa(SceDocumentoValidade documentoValidade) {
		itemDaSelecionado.getListaValidades().remove(documentoValidade);
	}
	
	public void adicionarLoteItemDa() {
		
		SceLoteDocumento loteDocumento = new SceLoteDocumento();
		loteDocumento.setLotCodigo(loteItemDaLote.getId().getCodigo());
		loteDocumento.setLotMatCodigo(loteItemDaLote.getId().getMatCodigo());
		loteDocumento.setLotMcmCodigo(loteItemDaLote.getId().getMcmCodigo());
		loteDocumento.setFornecedor(loteItemDaFornecedor);
		loteDocumento.setQuantidade(loteItemDaQtde);
		
		try {
			estoqueFacade.verificarQuantidadeLoteContraQuantidadeValidade(loteDocumento, validadeSelecionada);
			loteDocumento.setDtValidade(validadeSelecionada.getId().getData());
			itemDaSelecionado.getListaLoteDocumento().add(loteDocumento);			
			limparLoteItemDa();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
	}
	
	public void removerLoteItemDa(SceLoteDocumento loteDocumento) {
		itemDaSelecionado.getListaLoteDocumento().remove(loteDocumento);
	}
	
	private void limparDa() {
		daSeq = null;
		daDtGeracao = null;
		daResponsavel = null;
		daAlmoxarifado = null;
		daCentroCusto = null;
		daObservacao = null;
	}
	
	private void limparItemDa() {
		itemDaEstoqueAlmoxarifado = null;
		itemDaQtde = null;
	}
	
	private void limparValidadeItemDa() {
		validadeItemDaData = null;
		validadeItemDaQtde = null;
	}
	
	private void limparLoteItemDa() {
		loteItemDaLote = null;
		loteItemDaFornecedor = null;
		loteItemDaQtde = null;
	}
	
	public String obterDescricaoMarcaComercialLote(Integer mcmCodigo) {
		ScoMarcaComercial marcaComercial = comprasFacade.obterMarcaComercialPorCodigo(mcmCodigo);
		if (marcaComercial != null) {
			return marcaComercial.getDescricao();
		} else {
			return "";
		}
	}
	
	public String gravar() {
		String nomeMicrocomputador = null;
		Boolean novaDa = Boolean.FALSE;
		
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		try {
			
			if (devolucaoAlmoxarifado.getSeq() == TEMP_DEVOLUCAO_ALMOX_ID) {
				novaDa = Boolean.TRUE;
			}
			
			// Seta atributos informados para a DA
			devolucaoAlmoxarifado.setAlmoxarifado(daAlmoxarifado);
			devolucaoAlmoxarifado.setCentroCusto(daCentroCusto);
			devolucaoAlmoxarifado.setObservacao(daObservacao);
			estoqueFacade.persistirDevolucaoAlmoxarifado(devolucaoAlmoxarifado, listaItemDevolucaoAlmoxarifadoVO, nomeMicrocomputador);
			
		//	this.estoqueFacade.flush();
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_DEVOLUCAO_ALMOXARIFADO");
			
			if (novaDa) {
				daSeq = devolucaoAlmoxarifado.getSeq();	
			}
			
			listaItemDevolucaoAlmoxarifadoVO = estoqueFacade
					.pesquisarItensComMaterialLoteDocumentosPorDevolucaoAlmoxarifado(daSeq);
			
			// Desfaz seleção dos radio buttons
			itemDaDalSeq = null;
			itemDaEalSeq = null;
			itemDaSelecionado = null;
			validDataStr = null;
			validData = null;
			validadeSelecionada = null;
			
		} catch (BaseException e) {
			if (novaDa) {
				devolucaoAlmoxarifado.setSeq(TEMP_DEVOLUCAO_ALMOX_ID);	
			}
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
			return null;
		}
		return cancelar();
	}
	
	public String cancelar() {
		limparDa();
		limparItemDa();
		limparValidadeItemDa();
		limparLoteItemDa();
		itemDaDalSeq = null;
		itemDaEalSeq = null;
		itemDaSelecionado = null;
		validDataStr = null;
		validData = null;
		validadeSelecionada = null;
		listaItemDevolucaoAlmoxarifadoVO = null;
		return "pesquisarDevolucaoAlmoxarifado";
	}
	
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifado(String paramPesquisa) {
		AghParametros paramFornecedorPadrao = null;
		try {
			paramFornecedorPadrao = parametroFacade.obterAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
		if (daAlmoxarifado != null && paramFornecedorPadrao != null) {
			return this.estoqueFacade.pesquisarEstoqueMaterialPorAlmoxarifado(
					daAlmoxarifado.getSeq(), paramFornecedorPadrao.getVlrNumerico().intValue(), paramPesquisa);	
		} else {
			return new ArrayList<SceEstoqueAlmoxarifado>();
		}
	}
	
	public String getDescricaoMaterial() {
		String labelMaterial =    WebUtil.initLocalizedMessage("LABEL_MATERIAL", null, (Object[])null);      //getMessages().get("LABEL_MATERIAL");
		if (this.itemDaSelecionado != null) {
			return labelMaterial + ": " + itemDaSelecionado.getCodigoMaterial() + " - " + itemDaSelecionado.getNomeMaterial();
		} else {
			return labelMaterial + ": ";
		}
	}
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if(str != null) {
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public Boolean verificarValidadeItemDaPersistido(SceDocumentoValidade docValidade) {
		return estoqueFacade.verificarValidadeItemDaPersistido(docValidade);
	}
	
	public Date converterStringParaData(String str) throws ParseException {
		DateFormat df = new SimpleDateFormat(DATE_PATTERN_DDMMYYYY);
		return df.parse(str);
	}	
	
	public RapServidores getUsuarioLogado() {
		try {
			return registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			return null;
		}
	}	

	public Integer getDaSeq() {
		return daSeq;
	}

	public void setDaSeq(Integer daSeq) {
		this.daSeq = daSeq;
	}

	public Date getDaDtGeracao() {
		return daDtGeracao;
	}

	public void setDaDtGeracao(Date daDtGeracao) {
		this.daDtGeracao = daDtGeracao;
	}

	public RapServidores getDaResponsavel() {
		return daResponsavel;
	}

	public void setDaResponsavel(RapServidores daResponsavel) {
		this.daResponsavel = daResponsavel;
	}

	public SceAlmoxarifado getDaAlmoxarifado() {
		return daAlmoxarifado;
	}

	public void setDaAlmoxarifado(SceAlmoxarifado daAlmoxarifado) {
		this.daAlmoxarifado = daAlmoxarifado;
	}

	public FccCentroCustos getDaCentroCusto() {
		return daCentroCusto;
	}

	public void setDaCentroCusto(FccCentroCustos daCentroCusto) {
		this.daCentroCusto = daCentroCusto;
	}

	public String getDaObservacao() {
		return daObservacao;
	}

	public void setDaObservacao(String daObservacao) {
		this.daObservacao = daObservacao;
	}

	public SceDevolucaoAlmoxarifado getDevolucaoAlmoxarifado() {
		return devolucaoAlmoxarifado;
	}

	public void setDevolucaoAlmoxarifado(
			SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) {
		this.devolucaoAlmoxarifado = devolucaoAlmoxarifado;
	}

	public SceEstoqueAlmoxarifado getItemDaEstoqueAlmoxarifado() {
		return itemDaEstoqueAlmoxarifado;
	}

	public void setItemDaEstoqueAlmoxarifado(
			SceEstoqueAlmoxarifado itemDaEstoqueAlmoxarifado) {
		this.itemDaEstoqueAlmoxarifado = itemDaEstoqueAlmoxarifado;
	}

	public Integer getItemDaQtde() {
		return itemDaQtde;
	}

	public void setItemDaQtde(Integer itemDaQtde) {
		this.itemDaQtde = itemDaQtde;
	}

	public List<ItemDevolucaoAlmoxarifadoVO> getListaItemDevolucaoAlmoxarifadoVO() {
		return listaItemDevolucaoAlmoxarifadoVO;
	}

	public void setListaItemDevolucaoAlmoxarifadoVO(
			List<ItemDevolucaoAlmoxarifadoVO> listaItemDevolucaoAlmoxarifadoVO) {
		this.listaItemDevolucaoAlmoxarifadoVO = listaItemDevolucaoAlmoxarifadoVO;
	}

	public ItemDevolucaoAlmoxarifadoVO getItemDaSelecionado() {
		return itemDaSelecionado;
	}

	public void setItemDaSelecionado(ItemDevolucaoAlmoxarifadoVO itemDaSelecionado) {
		this.itemDaSelecionado = itemDaSelecionado;
	}

	public Date getValidadeItemDaData() {
		return validadeItemDaData;
	}

	public void setValidadeItemDaData(Date validadeItemDaData) {
		this.validadeItemDaData = validadeItemDaData;
	}

	public Integer getValidadeItemDaQtde() {
		return validadeItemDaQtde;
	}

	public void setValidadeItemDaQtde(Integer validadeItemDaQtde) {
		this.validadeItemDaQtde = validadeItemDaQtde;
	}

	public SceLote getLoteItemDaLote() {
		return loteItemDaLote;
	}

	public void setLoteItemDaLote(SceLote loteItemDaLote) {
		this.loteItemDaLote = loteItemDaLote;
	}

	public ScoFornecedor getLoteItemDaFornecedor() {
		return loteItemDaFornecedor;
	}

	public void setLoteItemDaFornecedor(ScoFornecedor loteItemDaFornecedor) {
		this.loteItemDaFornecedor = loteItemDaFornecedor;
	}

	public Integer getLoteItemDaQtde() {
		return loteItemDaQtde;
	}

	public void setLoteItemDaQtde(Integer loteItemDaQtde) {
		this.loteItemDaQtde = loteItemDaQtde;
	}

	public SceDocumentoValidade getValidadeSelecionada() {
		return validadeSelecionada;
	}

	public void setValidadeSelecionada(SceDocumentoValidade validadeSelecionada) {
		this.validadeSelecionada = validadeSelecionada;
	}

	public Integer getItemDaDalSeq() {
		return itemDaDalSeq;
	}

	public void setItemDaDalSeq(Integer itemDaDalSeq) {
		this.itemDaDalSeq = itemDaDalSeq;
	}

	public Integer getItemDaEalSeq() {
		return itemDaEalSeq;
	}

	public void setItemDaEalSeq(Integer itemDaEalSeq) {
		this.itemDaEalSeq = itemDaEalSeq;
	}

	public String getValidDataStr() {
		return validDataStr;
	}

	public void setValidDataStr(String validDataStr) {
		this.validDataStr = validDataStr;
	}

	public Date getValidData() {
		return validData;
	}

	public void setValidData(Date validData) {
		this.validData = validData;
	}

}
