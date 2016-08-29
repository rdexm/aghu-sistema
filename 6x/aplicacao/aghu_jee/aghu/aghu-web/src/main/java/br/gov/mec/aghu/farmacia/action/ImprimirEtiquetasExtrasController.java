package br.gov.mec.aghu.farmacia.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.action.AbstractCrudController;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class ImprimirEtiquetasExtrasController extends AbstractCrudController<SceLoteDocImpressao> {	

	private static final long serialVersionUID = -6908384109692466514L;

	@Inject
	private IComprasFacade comprasFacade;

	@Inject
	private IEstoqueFacade estoqueFacade;
	
	private Integer pGrFarmIndustrial;
	private Integer pGrMatMedic;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private static final Log LOG = LogFactory.getLog(ImprimirEtiquetasExtrasController.class);
	
	@Override
	public void init() {
		if(getEntidade()==null){
			setEntidade(new SceLoteDocImpressao());
			//			getEntidade().setLote(new SceLote());
			//			getEntidade().getLote().setId(new SceLoteId());
		}
		
		if(pGrFarmIndustrial == null || pGrMatMedic == null){
			pGrFarmIndustrial = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_GR_FARM_INDUSTRIAL.toString()).intValue();
			pGrMatMedic = parametroFacade.obterValorNumericoAghParametros(AghuParametrosEnum.P_GR_MAT_MEDIC.toString()).intValue();
		}
	}

	//Suggestion Marcas
	public List<ScoMarcaComercial> pesquisarMarcas(String parametro){
		try {
			return this.returnSGWithCount(this.comprasFacade.getListaMarcasByNomeOrCodigo(parametro, 0, 100, null, true),pesquisarMarcasCount(parametro));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}	


	//Suggestion Materiais
	public List<ScoMaterial> pesquisarMateriais(String objPesquisa){
		List<ScoMaterial> lista = this.comprasFacade.listarScoMateriaisPorGrupoEspecifico(objPesquisa, 0, 100, null, true, pGrFarmIndustrial, pGrMatMedic);    
		return this.returnSGWithCount(lista,pesquisarMateriaisCount(objPesquisa));
	}

	public Long pesquisarMateriaisCount(String objPesquisa){
		return this.comprasFacade.listarScoMateriaisPorGrupoEspecificoCount(objPesquisa, pGrFarmIndustrial, pGrMatMedic);
	}

	public Long pesquisarMarcasCount(String objPesquisa){
		return this.comprasFacade.getListaMarcasByNomeOrCodigoCount(objPesquisa);
	}


	/*//**
	 * Chamada da impressão para Zebra.
	 *//**/
	private void gerarEtiquetas() {

		try {
			String nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			//this.farmaciaFacade.gerarEtiquetas(getEntidade(), nomeMicrocomputador);
//			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			this.imprimeEtiquetaExtrasOuInterfaceamentoUnitarizacao(
					getEntidade(), nomeMicrocomputador, null);
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO_ETIQUETAS");
				//#34551, rubens.silva //this.imprimirEtiquetasUnitarizacao();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (UnknownHostException e) {
				LOG.error("Exceção capturada:", e);
			}
	}
	
	public void imprimeEtiquetaExtrasOuInterfaceamentoUnitarizacao(
			SceLoteDocImpressao loteDocImpressao, String nomeMicrocomputador,
			Integer qtdeEtiquetas) throws BaseException {

		AghMicrocomputador micro = this.administracaoFacade
				.obterAghMicroComputadorPorNomeOuIP(
						nomeMicrocomputador,
						DominioCaracteristicaMicrocomputador.POSSUI_IMPRESSORA_UNITARIZADORA);

		if (micro == null) {
			AghUnidadesFuncionais unidadeFuncional = this.farmaciaFacade
					.getUnidadeFuncionalAssociada(nomeMicrocomputador);
			String etiquetas = this.farmaciaFacade
					.gerarEtiquetas(loteDocImpressao);
			this.sistemaImpressao.imprimir(etiquetas, unidadeFuncional,
					TipoDocumentoImpressao.ETIQUETA_BARRAS_MEDICAMENTOS);
		} else {
			this.estoqueFacade.gerarInterfaceamentoUnitarizacao(
					loteDocImpressao, nomeMicrocomputador, qtdeEtiquetas);
		}
	}

	public String voltar() {
		return "imprimirEtiquetasExtrasList";		
	}

	@Override
	protected boolean efetuarInclusao() {
		return true;
	}

	@Override
	protected void efetuarInclusao(SceLoteDocImpressao entidade) throws IllegalStateException, BaseException {
		if ( !(DateUtil.validaDataTruncadaMaiorIgual(entidade.getDtValidade(), new Date()))  ) {
			//focusDtvalidade = true;
			throw new ApplicationBusinessException("MENSAGEM_DATA_VALIDADE_MENOR_DATA_ATUAL", Severity.ERROR);
		}  

		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
		}

		this.estoqueFacade.efetuarInclusao(entidade, nomeMicrocomputador, new Date(), Boolean.FALSE);		
	}

	/**
	 * Unitarizacao de Medicamentos
	 * @return
	 *//*
	 *#34551
	public void imprimirEtiquetasUnitarizacao() {

		try {

			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			String nomeMicrocomputador = super.getEnderecoRedeHostRemoto();

			this.estoqueFacade.imprimirEtiquetasUnitarizacao(getEntidade(), servidorLogado, nomeMicrocomputador);

		} catch (MECBaseException e) {
			super.getLog().error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new AGHUNegocioExceptionSemRollback(AGHUNegocioExceptionCode.NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR, e));
		}

	}*/

	@Override
	protected void informarInclusaoSucesso(SceLoteDocImpressao entidade) {
		// Envia ZPL para impressora Zebra instalada no CUPS.
		this.gerarEtiquetas();
		setEntidade(new SceLoteDocImpressao());
		//		this.getStatusMessages().addFromResourceBundle(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_ETIQUETA_EXTRA", entidade.getMaterial().getDescricao());

	}

	@Override
	protected List<String> obterRazoesExcecao() {
		return this.estoqueFacade.obterRazoesExcessaoEtiquetasExtras();		
	}

	@Override
	protected SceLoteDocImpressao obterEntidadeOriginalViaEntidade(
			SceLoteDocImpressao entidade) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void efetuarAlteracao(SceLoteDocImpressao entidade)
	throws IllegalStateException, BaseException {
		// Etiquetas Extras não possui alteração
	}

	@Override
	protected void efetuarRemocao(SceLoteDocImpressao entidade)
	throws IllegalStateException, BaseException {
		// Etiquetas Extras não possui remoção
	}

	@Override
	protected void informarAlteracaoSucesso(SceLoteDocImpressao entidade) {
		// Etiquetas Extras não possui alteração
	}

	@Override
	protected void informarExclusaoSucesso(SceLoteDocImpressao entidade) {
		// Etiquetas Extras não possui exclusão
	}

	@Override
	protected void informarExclusaoErro(SceLoteDocImpressao entidade) {
		// Etiquetas Extras não possui exclusão
	}

	@Override
	protected void reiniciarPaginatorController() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void procederPreExclusao() {
		// Etiquetas Extras não possui exclusão
	}

	@Override
	protected void procederPosExclusao() {
		// Etiquetas Extras não possui exclusão
	}

	@Override
	protected void prepararInclusao() {
		// TODO Auto-generated method stub
	}

	@Override
	protected void prepararCancelamento() {
		// TODO Auto-generated method stub
	}

	@Override
	protected String getPaginaInclusao() {
		return "imprimirEtiquetasExtras";
	}

	@Override
	protected String getPaginaConfirmado() {
		return "imprimirEtiquetasExtrasList";
	}

	@Override
	protected String getPaginaCancelado() {
		return "imprimirEtiquetasExtrasList";
	}

	@Override
	protected String getPaginaErro() {
		return null;
	}
}