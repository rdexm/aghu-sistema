package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.math.BigDecimal;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.MbcUnidadeNotaSala;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumosId;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterConversaoUnidadeConsumoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 3288102063913966552L;

	private static final String CONVERSAO_LIST = "manterConversaoUnidadeConsumoList";
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	private IAghuFacade aghuFacade;	
	
	private ScoMaterial material;
	private ScoUnidadeMedida scoUnidadeMedida;
	private BigDecimal fatorConversao;
	
	/*
	 * Parâmetros da integração com #24345 Associar materiais para impressão de nota de sala
	 */
	
	private Integer codigoMaterial;
	private String codigoUnidadeMedida;
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void inicio() {
	 

	 

		material=null;
		scoUnidadeMedida=null;
		fatorConversao=null;

		if(this.codigoMaterial != null){
			// Integração com #24345 Associar materiais para impressão de nota de sala
			this.material = this.comprasFacade.obterScoMaterial(this.codigoMaterial);
		}
		
		if(StringUtils.isNotEmpty(this.codigoUnidadeMedida)){
			// Integração com #24345 Associar materiais para impressão de nota de sala
			this.scoUnidadeMedida = this.comprasCadastrosBasicosFacade.obterUnidadeMedida(this.codigoUnidadeMedida);
		}
		
	
	}
	

	public boolean isAtiva(MbcUnidadeNotaSala unidSala){
		return unidSala.getSituacao().isAtivo();
	}
	
	public String confirmar(){
		try {
			
			SceConversaoUnidadeConsumosId conversaoId = new SceConversaoUnidadeConsumosId();
			conversaoId.setMatCodigo(this.material.getCodigo());
			conversaoId.setUmdCodigo(this.scoUnidadeMedida.getCodigo());
			
			SceConversaoUnidadeConsumos conversao = new SceConversaoUnidadeConsumos();
			conversao.setId(conversaoId);
			conversao.setFatorConversao(fatorConversao);			
			
			this.estoqueFacade.persistirConversao(conversao);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_CONVERSAO");
			return this.cancelar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String cancelar(){
		return CONVERSAO_LIST;
	}
	
	// Metodo para Suggestion Box de Material
	public List<ScoMaterial>pesquisarMateriais(String param){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(param, null),pesquisarMateriaisCount(param));
	}
	
    public Long pesquisarMateriaisCount(String param){
        return this.comprasFacade.listarScoMatriaisAtivosCount(param);
    }
	
	//Metodo para Suggestion Box de especialidades
    public List<ScoUnidadeMedida> obterUnidades(String objPesquisa) {
		return this.returnSGWithCount(this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorCodigoDescricao(objPesquisa),obterUnidadesCount(objPesquisa));
	}
	
	public Long obterUnidadesCount(String objParam) {
		return this.comprasCadastrosBasicosFacade.pesquisarUnidadeMedidaPorCodigoDescricaoCount(objParam);
	}
	
	/*
	 * Getters and Setters abaixo...
	 */
	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public void setComprasFacade(IComprasFacade comprasFacade) {
		this.comprasFacade = comprasFacade;
	}

	public IComprasCadastrosBasicosFacade getComprasCadastrosBasicosFacade() {
		return comprasCadastrosBasicosFacade;
	}

	public void setComprasCadastrosBasicosFacade(
			IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade) {
		this.comprasCadastrosBasicosFacade = comprasCadastrosBasicosFacade;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoUnidadeMedida getScoUnidadeMedida() {
		return scoUnidadeMedida;
	}

	public void setScoUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.scoUnidadeMedida = scoUnidadeMedida;
	}

	public BigDecimal getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(BigDecimal fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}
	
	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}
}