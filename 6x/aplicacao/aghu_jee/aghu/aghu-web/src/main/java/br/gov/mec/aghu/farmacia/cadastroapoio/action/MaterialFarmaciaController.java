package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoMaterial;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class MaterialFarmaciaController extends AbstractCrudController<ScoMaterial> {  

	private static final long serialVersionUID = -6947801798831235885L;
	
	private static final String PAGE_MATERIAL_LIST= "materialList";
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private MaterialFarmaciaPaginatorController materialFarmaciaPaginatorController;
	
	private Integer codigoMaterial;
	private Boolean exibirBotaoNovo = false;
	private DominioSituacao situacao;
	
	public void inicio() {
	 

		if(this.codigoMaterial != null) {
			setIsUpdate(Boolean.TRUE);
			setEntidade(this.comprasFacade.obterScoMaterialPorChavePrimaria(this.codigoMaterial));
			this.situacao = getEntidade().getIndSituacao();
		} else {
			setIsUpdate(Boolean.FALSE);
			this.situacao = DominioSituacao.A;
		}
	
	}

	public String voltar(){
		materialFarmaciaPaginatorController.getDataModel().reiniciarPaginator();
		deveAparecerBotaoNovo();
		return PAGE_MATERIAL_LIST;
	}
	
	@Override
	protected void informarAlteracaoSucesso(ScoMaterial entidade) {
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MATERIAL", entidade.getNome());
	}

	@Override
	protected void informarInclusaoSucesso(ScoMaterial entidade) {
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MATERIAL", entidade.getNome());
	}
	
	public Integer getCodigoMaterial() {
		return this.codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}   
	
	@Override
	public String confirmar() {
		if(super.getEntidade() != null){
			setaValoresDefaulsSCOMaterialCasoNulos(super.getEntidade());
		}
		
		return super.confirmar();
	}
	
	/**
	 * Método que verifica se o botão contrato deve aparecer na tela de
	 * internação.
	 * 
	 * @return
	 */
	public boolean deveAparecerBotaoNovo(){
		boolean retorno = false;
		try{
			AghuParametrosEnum parametroEnum = AghuParametrosEnum.P_AGHU_DEVE_APARECER_BOTAO_NOVO_MATERIAL;
			AghParametros parametroAparecerBotaoNovo = this.parametroFacade.buscarAghParametro(parametroEnum);
			if ("S".equals(parametroAparecerBotaoNovo.getVlrTexto())){
					setExibirBotaoNovo(true);
					retorno = true;
				
			}
		} catch (ApplicationBusinessException e) {

			apresentarExcecaoNegocio(e);

		}
		return retorno;
	}
	
	// Seta valores defauls caso estejam nulos.
	private void setaValoresDefaulsSCOMaterialCasoNulos(final ScoMaterial material) {
		if(material != null){
			if(material.getIndEstocavel() == null){
				material.setEstocavel(false);
			}
			
			if(material.getIndMenorPreco() == null){
				material.setIndMenorPreco(DominioSimNao.N);
			}
			
			if(material.getIndAtuQtdeDisponivel() == null ){
				material.setIndAtuQtdeDisponivel(DominioSimNao.N);
			}
			
			if(material.getIndGenerico() == null){
				material.setIndGenerico(DominioSimNao.N);
			}
			
			if(material.getIndCcih() == null ){
				material.setIndCcih(DominioSimNao.N);
			}
			
			if(material.getIndFaturavel() == null){
				material.setIndFaturavel(DominioSimNao.N);
			}
			
			if(material.getIndControleValidade() == null){
				material.setIndControleValidade(DominioSimNao.S);
			}

			if(material.getIndSituacao() == null){
				material.setIndSituacao(DominioSituacao.A);
			}
			
			if(material.getIndUtilizaEspacoFisico() == null) {
				material.setIndUtilizaEspacoFisico(false);
			}
			
			setaValoresDefaultMaterial(material);
			
			setaValoresDefaulsSCOMaterialPerigoso(material);
		}
	}

	private void setaValoresDefaultMaterial(final ScoMaterial material) {
		if(material.getIndTermolabil() == null) {
			material.setIndTermolabil(false);
		}
		
		if(material.getIndSustentavel() == null) {
			material.setIndSustentavel(false);
		}
		
		if(material.getIndCapCmed() == null) {
			material.setIndCapCmed(DominioSimNao.N);
		}
		
		if(material.getIndConfaz() == null) {
			material.setIndConfaz(DominioSimNao.N);
		}
		
		if(material.getIndVinculado() == null) {
			material.setIndVinculado(false);
		}
	}
	
	private void setaValoresDefaulsSCOMaterialPerigoso(ScoMaterial material) {
		if(material.getIndCorrosivo() == null){
			material.setIndCorrosivo(false);
		}
		if(material.getIndInflamavel() == null){
			material.setIndInflamavel(false);
		}
		if(material.getIndRadioativo() == null){
			material.setIndRadioativo(false);
		}
		if(material.getIndReativo() == null){
			material.setIndReativo(false);
		}
		if(material.getIndToxico() == null){
			material.setIndToxico(false);
		}
	}

	
	@Override
	protected void prepararInclusao() {
		this.codigoMaterial = null;
	}
	
	@Override
	protected boolean efetuarInclusao() {    
		getEntidade().setIndSituacao(this.situacao);
		return !this.getIsUpdate().booleanValue();
	}

	@Override
	protected void reiniciarPaginatorController() {
		materialFarmaciaPaginatorController.getDataModel().reiniciarPaginator();
	}
	
	@Override
	protected void informarExclusaoErro(ScoMaterial entidade) {
		// material de farmacia nao permite exclusao
	}

	@Override
	protected void informarExclusaoSucesso(ScoMaterial entidade) {
		// material de farmacia nao permite exclusao
	}
	
	@Override
	protected void procederPosExclusao() {
		// material de farmacia nao permite exclusao
	}
	
	@Override
	protected void prepararCancelamento() {
		// nao se aplica
	}

	@Override
	protected void procederPreExclusao() {
		// nao se aplica		
	}

	@Override
	protected List<String> obterRazoesExcecao() {

		return this.comprasFacade.obterRazoesExcessaoMaterial();
	}

	@Override
	protected ScoMaterial obterEntidadeOriginalViaEntidade(ScoMaterial entidade) {
		
		Integer codigo = null;
		
		codigo = entidade != null ? entidade.getCodigo() : null;
		
		return this.comprasFacade.obterScoMaterialPorChavePrimaria(codigo);
	}

	@Override
	protected void efetuarInclusao(ScoMaterial entidade)
			throws IllegalStateException, ApplicationBusinessException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = getEnderecoIPv4HostRemoto().getHostAddress(); 
			this.comprasFacade.efetuarInclusao(entidade, nomeMicrocomputador, new Date());
		} catch (UnknownHostException e1) {
			throw new ApplicationBusinessException(e1.getMessage(), Severity.ERROR);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	protected void efetuarAlteracao(ScoMaterial entidade)
			throws IllegalStateException, ApplicationBusinessException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = getEnderecoIPv4HostRemoto().getHostAddress();
			this.comprasFacade.efetuarAlteracao(entidade, nomeMicrocomputador, new Date());
		} catch (UnknownHostException e1) {
			throw new ApplicationBusinessException(e1.getMessage(), Severity.ERROR);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	@Override
	protected void efetuarRemocao(ScoMaterial entidade)
			throws IllegalStateException, ApplicationBusinessException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = getEnderecoIPv4HostRemoto().getHostAddress();
			this.comprasFacade.efetuarRemocao(entidade, nomeMicrocomputador, new Date());		
		} catch (UnknownHostException e1) {
			throw new ApplicationBusinessException(e1.getMessage(), Severity.ERROR);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limpar(){
		init();
	}

	@Override
	protected String getPaginaInclusao() {
		return "materialCRUD";
	}

	@Override
	protected String getPaginaConfirmado() {
		return "materialList";
	}

	@Override
	protected String getPaginaCancelado() {
		// nao se aplica
		return null;
	}

	@Override
	protected String getPaginaErro() {
		// nao se aplica
		return null;
	}
	
	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	@Override
	public void init() {
		// não se aplica
		
	}
}
