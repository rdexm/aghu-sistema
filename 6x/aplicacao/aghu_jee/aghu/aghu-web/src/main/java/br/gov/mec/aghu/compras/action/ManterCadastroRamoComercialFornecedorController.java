package br.gov.mec.aghu.compras.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.jfree.util.Log;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoFornRamoComercialId;
import br.gov.mec.aghu.model.ScoFornecedorMarca;
import br.gov.mec.aghu.model.ScoFornecedorMarcaId;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoRamoComercial;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCadastroRamoComercialFornecedorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 2886547572554808231L;

	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	private ScoFornecedorMarca scoFornecedorMarca;
	private ScoFornecedorMarcaId id;

	private ScoMarcaComercial scoMarcaComercial;
	private String descricaoMarca;
		
	private Integer codigoFornecedor;
	private Integer numeroPac;
	private String voltarParaUrl;
	
	//Parametros
	private VScoFornecedor vcoFornecedor;
	private ScoFornRamoComercial fornRamoComercial;
	private ScoRamoComercial ramoComercial;
	private Integer frnNumero;
	private Short rcmCodigo;
	
	@Inject @Paginator
	private DynamicDataModel<ScoFornRamoComercial> dataModel;

	public void inicio(){
	 

	 

		this.fornRamoComercial = new ScoFornRamoComercial();		
				
	
	}
	
	
	@Override
	public Long recuperarCount() {
		return comprasCadastrosBasicosFacade.pesquisarScoRamosComerciaisPorFornecedorCount(vcoFornecedor);
	}

	@Override
	public List<ScoFornRamoComercial> recuperarListaPaginada(Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		return comprasCadastrosBasicosFacade.pesquisarScoRamosComerciaisPorFornecedor(vcoFornecedor, firstResult, maxResults, orderProperty, asc);
	}
	
	/**
	 * SugestionBox
	 * @param param
	 * @return
	 */
	public List<ScoRamoComercial> pesquisarRamoComercial(String param) {
		return  this.comprasFacade.listarRamosComerciaisAtivos(param);
	}
	
	public List<VScoFornecedor> pesquisarFornecedores(String param) {
		return this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(param);
		
	}
	
	public void pesquisar(){
		if(vcoFornecedor!=null){
			//reiniciarPaginator();
			dataModel.reiniciarPaginator();
		}
	}
	public void excluir() {
		try {
			ScoFornRamoComercial fRamoComer =  this.comprasCadastrosBasicosFacade.obterFornRamoComerciaNumeroCodigo(frnNumero,rcmCodigo);
			this.comprasCadastrosBasicosFacade.excluirScoFornRamoComercial(fRamoComer);
			//this.comprasCadastrosBasicosFacade.flush();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_M2_FORN_RC");
			this.pesquisar();
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravar() throws ApplicationBusinessException {
		ScoFornRamoComercial fornRamoC = new ScoFornRamoComercial();
		ScoFornRamoComercialId id = new ScoFornRamoComercialId();
		id.setFrnNumero(vcoFornecedor.getNumeroFornecedor());
		id.setRcmCodigo(ramoComercial.getCodigo());
		fornRamoC.setId(id);
		fornRamoC.setScoRamoComercial(ramoComercial);
		
		try{
			this.comprasCadastrosBasicosFacade.cadastrarScoFornRamoComercial(fornRamoC);
			//this.comprasCadastrosBasicosFacade.flush();
		
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_M1_FORN_RAMO_COMERCIAL");
		
		}  catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}catch(Exception e){
			//this.logError(e.getMessage());
			Log.error(e);
		}
		
	
		
		this.ramoComercial = null;
		//reiniciarPaginator();
		dataModel.reiniciarPaginator();
	}
	
	public void limpar() {
		vcoFornecedor = null;
		
		//this.setAtivo(false);
		dataModel.setPesquisaAtiva(Boolean.FALSE);
	}

//	public void excluir(){
//		try{
//			if(id != null) {
//				ScoFornecedorMarca fornecedorMarcaExcluir = this.comprasFacade.obterScoFornecedorMarcaPorId(id);
//				if(fornecedorMarcaExcluir != null) {
//					this.comprasFacade.excluirScoFornecedorMarca(fornecedorMarcaExcluir);
//					this.getStatusMessages().addFromResourceBundle(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_MARCA_FORNECEDOR", 
//																   fornecedorMarcaExcluir.getScoMarcaComercial().getDescricao());		
//					reiniciarPaginator();
//				}
//			}
//		} catch (AGHUNegocioExceptionSemRollback e) {
//			super.apresentarExcecaoNegocio(e);
//		}
//	}
	
	
//	@SuppressWarnings("rawtypes")
//	@Override
//	protected List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
//		
//		return this.comprasFacade.listaFornecedorMarca(firstResult, maxResult, orderProperty, asc, this.scoFornecedor, descricaoMarca);
//	}
//
//	@Override
//	protected Integer recuperarCount() {
//		return this.comprasFacade.listaFornecedorMarcaCount(this.scoFornecedor);
//	}	
	
	// Getters and Setters

	public ScoFornecedorMarca getScoFornecedorMarca() {
		return scoFornecedorMarca;
	}

	public void setScoFornecedorMarca(ScoFornecedorMarca scoFornecedorMarca) {
		this.scoFornecedorMarca = scoFornecedorMarca;
	}

	public ScoMarcaComercial getScoMarcaComercial() {
		return scoMarcaComercial;
	}

	public void setScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) {
		this.scoMarcaComercial = scoMarcaComercial;
	}

	public ScoFornecedorMarcaId getId() {
		return id;
	}

	public void setId(ScoFornecedorMarcaId id) {
		this.id = id;
	}

	public String getDescricaoMarca() {
		return descricaoMarca;
	}

	public void setDescricaoMarca(String descricaoMarca) {
		this.descricaoMarca = descricaoMarca;
	}

	public Integer getCodigoFornecedor() {
		return codigoFornecedor;
	}

	public void setCodigoFornecedor(Integer codigoFornecedor) {
		this.codigoFornecedor = codigoFornecedor;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public VScoFornecedor getVcoFornecedor() {
		return vcoFornecedor;
	}

	public void setVcoFornecedor(VScoFornecedor vcoFornecedor) {
		this.vcoFornecedor = vcoFornecedor;
	}

	public ScoFornRamoComercial getFornRamoComercial() {
		return fornRamoComercial;
	}

	public void setFornRamoComercial(ScoFornRamoComercial fornRamoComercial) {
		this.fornRamoComercial = fornRamoComercial;
	}

	public ScoRamoComercial getRamoComercial() {
		return ramoComercial;
	}

	public void setRamoComercial(ScoRamoComercial ramoComercial) {
		this.ramoComercial = ramoComercial;
	}

	public Integer getFrnNumero() {
		return frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	public Short getRcmCodigo() {
		return rcmCodigo;
	}

	public void setRcmCodigo(Short rcmCodigo) {
		this.rcmCodigo = rcmCodigo;
	}

	public DynamicDataModel<ScoFornRamoComercial> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScoFornRamoComercial> dataModel) {
		this.dataModel = dataModel;
	}
}
