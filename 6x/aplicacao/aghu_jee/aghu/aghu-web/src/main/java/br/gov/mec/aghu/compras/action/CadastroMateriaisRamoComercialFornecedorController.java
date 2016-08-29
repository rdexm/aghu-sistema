package br.gov.mec.aghu.compras.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ClassificacaoVO;
import br.gov.mec.aghu.compras.vo.RamoComercialVO;
import br.gov.mec.aghu.model.ScoFnRamoComerClas;
import br.gov.mec.aghu.model.ScoFnRamoComerClasId;
import br.gov.mec.aghu.model.ScoFornRamoComercial;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroMateriaisRamoComercialFornecedorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ClassificacaoVO> dataModel;
	private static final long serialVersionUID = 2886547572554808231L;

	@EJB
	private IComprasFacade comprasFacade;

	private ScoFornecedor fornecedor;
	private RamoComercialVO ramoComercial;
	private ClassificacaoVO classificacao;
		
	private Integer numeroFrn;
	private String voltarParaUrl;
	private Long codigoClassificacaoExcluir;
	
	private static final String PAGE_CADASTRO_MATERIAIS_RAMO_COMERCIAL_FORNECEDOR = "cadastrarMateriaisRamoComercialFornecedor";

	private enum CadastroMateriaisRamoComercialFornecedorControllerExceptionCode implements BusinessExceptionCode{
		MENSAGEM_CLASSFICACAO_GRAVADA_COM_SUCESSO,
		MENSAGEM_CLASSFICACAO_EXCLUIDA_COM_SUCESSO;
	}
	
	public void inicio(){
		
		this.ramoComercial = null;
		this.classificacao = null;
		if (numeroFrn!=null){
			fornecedor = comprasFacade.obterFornecedorPorNumero(numeroFrn);			
		} else {
			this.voltar();			
		}		
	}
	
	// suggestion Ramos Comerciais
	public List<ScoFornRamoComercial> pesquisarRamosComerciais(String param){
		
		List<ScoFornRamoComercial> lista = new ArrayList<ScoFornRamoComercial>();		
		try {
			return  this.returnSGWithCount(this.comprasFacade.obterRamosComerciais(param, this.fornecedor.getNumero()),pesquisarRamosComerciaisCount(param));
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}		
		return lista;
	}
	
	// suggestion Ramos ComerciaisCount
	public Long pesquisarRamosComerciaisCount(String param){
		
		try{
			return this.comprasFacade.obterRamosComerciaisCount(param, this.fornecedor.getNumero());
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}		
		return 0L;		
	}
	
	// suggestion Classificacoes
	public List<ClassificacaoVO> pesquisarClassificacoes(String param){
		
		List<ClassificacaoVO> lista = new ArrayList<ClassificacaoVO>();		
		try{
			lista = this.comprasFacade.obterClassificacoes(param);
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}		
		return  this.returnSGWithCount(lista,pesquisarClassificacoesCount(param));				
	}
	
	// suggestion ClassificacoesCount
	public Long pesquisarClassificacoesCount(String param){
		
		try{
			return this.comprasFacade.obterClassificacoesCount(param);
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}		
		return 0L;
	}
	
	public void pesquisar() {
		this.dataModel.setPesquisaAtiva(Boolean.TRUE);
		//TODO verificar como fazer setOrder em DataModal
		//super.setOrder(ClassificacaoVO.Fields.DESCRICAO.toString() + " ASC");
		this.dataModel.reiniciarPaginator();
	}

	@Override
	public Long recuperarCount() {
		
		try{
			if(this.fornecedor != null && this.ramoComercial != null){
				return this.comprasFacade.listarClassificacoesCount(this.fornecedor.getNumero(), this.ramoComercial.getCodigo());
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return 0L;
	}

	@Override
	public List<ClassificacaoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<ClassificacaoVO> lista = new ArrayList<ClassificacaoVO>();
		try{
			if(this.fornecedor != null && this.ramoComercial != null){
				lista = this.comprasFacade.listarClassificacoes(this.fornecedor.getNumero(), this.ramoComercial.getCodigo(), orderProperty, asc);
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		
		if (lista == null) {
			return new ArrayList<ClassificacaoVO>();
		} else if(lista.size()>(firstResult+maxResult)){
			lista = lista.subList(firstResult, firstResult+maxResult);
		} else {
			lista = lista.subList(firstResult, lista.size());
		}
		
		return lista;		
	}
	
	public void limpar() {
		this.ramoComercial = null;		
		this.dataModel.setPesquisaAtiva(Boolean.FALSE);
	}
	
	public void adionarClassificacao(){
		
		try {
			
			if(this.fornecedor != null && this.classificacao != null && this.ramoComercial != null) {
				ScoFnRamoComerClas scoFnRamoComerClas = new ScoFnRamoComerClas();
				
				ScoFnRamoComerClasId scoFnRamoComerClasId = new ScoFnRamoComerClasId();
				scoFnRamoComerClasId.setCn5Numero(this.classificacao.getCodigo());
				scoFnRamoComerClasId.setFrmFrnNumero(this.fornecedor.getNumero());
				scoFnRamoComerClasId.setFrmRcmCodigo(this.ramoComercial.getCodigo());
				
				scoFnRamoComerClas.setId(scoFnRamoComerClasId);
			
				this.comprasFacade.inserirScoFnRamoComerClas(scoFnRamoComerClas);
				this.classificacao = null;
				apresentarMsgNegocio(Severity.INFO, CadastroMateriaisRamoComercialFornecedorControllerExceptionCode.MENSAGEM_CLASSFICACAO_GRAVADA_COM_SUCESSO.toString());
				this.dataModel.reiniciarPaginator();	
			}			
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}		
	}	
		
	public void exluirClassificacao(){
		
		try {
			
			if(this.codigoClassificacaoExcluir != null && this.fornecedor != null && this.ramoComercial != null) {				
				
				ScoFnRamoComerClasId scoFnRamoComerClasId = new ScoFnRamoComerClasId();
				scoFnRamoComerClasId.setCn5Numero(this.codigoClassificacaoExcluir);
				scoFnRamoComerClasId.setFrmFrnNumero(this.fornecedor.getNumero());
				scoFnRamoComerClasId.setFrmRcmCodigo(this.ramoComercial.getCodigo());
				
				this.comprasFacade.removerScoFnRamoComerClas(scoFnRamoComerClasId);
				this.classificacao = null;
				apresentarMsgNegocio(Severity.INFO, CadastroMateriaisRamoComercialFornecedorControllerExceptionCode.MENSAGEM_CLASSFICACAO_EXCLUIDA_COM_SUCESSO.toString());
				this.dataModel.reiniciarPaginator();
			}
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}		
	}	
		
	public String voltar() {
		return PAGE_CADASTRO_MATERIAIS_RAMO_COMERCIAL_FORNECEDOR;
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}
	
	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}
	
	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}
	
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public RamoComercialVO getRamoComercial() {
		return ramoComercial;
	}
	
	public void setRamoComercial(RamoComercialVO ramoComercial) {
		this.ramoComercial = ramoComercial;
	}
	
	public ClassificacaoVO getClassificacao() {
		return classificacao;
	}
	
	public void setClassificacao(ClassificacaoVO classificacao) {
		this.classificacao = classificacao;
	}

	public Long getCodigoClassificacaoExcluir() {
		return codigoClassificacaoExcluir;
	}

	public void setCodigoClassificacaoExcluir(Long codigoClassificacaoExcluir) {
		this.codigoClassificacaoExcluir = codigoClassificacaoExcluir;
	}

	public DynamicDataModel<ClassificacaoVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ClassificacaoVO> dataModel) {
	 this.dataModel = dataModel;
	}
}
