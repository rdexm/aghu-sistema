package br.gov.mec.aghu.estoque.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.dao.SceDevolucaoAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemDasDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceDevolucaoAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemDas;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DevolucaoAlmoxarifadoRN extends BaseBusiness {

	@EJB
	private ManterTransferenciaMaterialRN manterTransferenciaMaterialRN;
	@EJB
	private SceMovimentoMaterialRN sceMovimentoMaterialRN;
	@EJB
	private ControlarValidadeMaterialRN controlarValidadeMaterialRN;
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	
	private static final Log LOG = LogFactory.getLog(DevolucaoAlmoxarifadoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceItemDasDAO sceItemDasDAO;
	
	@Inject
	private SceDevolucaoAlmoxarifadoDAO sceDevolucaoAlmoxarifadoDAO;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6334821673780790016L;
	
	public enum DevolucaoAlmoxarifadoRNExceptionCode implements BusinessExceptionCode {
		SCE_00292, SCE_00297, CENTRO_CUSTO_NAO_CADASTRADO_OU_INATIVO, SCE_00293, SCE_00508, SCE_0035, SCE_00366, SCE_00367, SCE_00369, SCE_00512;
	}
	
	/**
	 * Function 
	 * ORADB: SCEC_VER_IND_VALID
	 */
	public Boolean verificarControleValidade(Integer ealSeq) throws ApplicationBusinessException{
		SceEstoqueAlmoxarifado estoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoAtivoPorId(ealSeq);
		if(estoqueAlmoxarifado==null){
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_00292);
		}
		return estoqueAlmoxarifado.getIndControleValidade();
		
	}
	
	/**
	 * Procedure
	 * ORADB: SCEK_DAL_RN.RN_DALP_VER_ALTER
	 * @param estorno
	 * @throws ApplicationBusinessException
	 */
	public void verificarEstorno(Boolean estorno) throws ApplicationBusinessException{
		if(estorno){
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_00508);
		}
	}
	
	/**
	 * 
	 * Procedure
	 * ORADB: SCEK_DAL_RN.RN_DALP_ATU_MVTO_EST
	 * @param seq
	 * @param almoxarifado
	 * @param tipoMovimento
	 * @param centroCusto
	 * @param dataGeracao
	 * @throws BaseException
	 */
	public void atualizarMovimentoEstorno(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException{
		Integer quantidade;
		Boolean encontrou = false;
		List<SceItemDas> lista = this.getSceItemDasDAO().pesquisarItensPorDevolucao(devolucaoAlmoxarifado.getSeq());
		for(SceItemDas item: lista){
			encontrou = true;
			ScoMaterial material = null;
			if(item.getEstoqueAlmoxarifado()!=null){
				material = item.getEstoqueAlmoxarifado().getMaterial();
			}
			ScoUnidadeMedida umdDocumento = null;
			if(item.getUnidadeMedida()!=null){
				umdDocumento = item.getUnidadeMedida();
			}
			ScoUnidadeMedida umdEstoque = null;
			if(item.getUnidadeMedida()!=null && item.getEstoqueAlmoxarifado().getUnidadeMedida()!=null){
				umdEstoque = item.getEstoqueAlmoxarifado().getUnidadeMedida();
			}
			
			quantidade = item.getQuantidade();
			
			SceEstoqueAlmoxarifado estoqueAlmoxarifado = null;
			if(item.getEstoqueAlmoxarifado()!=null){
				estoqueAlmoxarifado = item.getEstoqueAlmoxarifado();
			}
			ScoFornecedor fornecedor = null;
			if(estoqueAlmoxarifado!=null){
				fornecedor = estoqueAlmoxarifado.getFornecedor();
			}
			
			this.getSceMovimentoMaterialRN().atualizarMovimentoMaterial(devolucaoAlmoxarifado.getAlmoxarifado(), material, umdEstoque, quantidade, null, true, devolucaoAlmoxarifado.getTipoMovimento(), null, devolucaoAlmoxarifado.getSeq(), null, null, fornecedor, null, devolucaoAlmoxarifado.getCentroCusto(), null, null, null, nomeMicrocomputador, true);
			Integer ealSeq = null;
			if(estoqueAlmoxarifado!=null){
				ealSeq = estoqueAlmoxarifado.getSeq();
			}
			if(this.verificarControleValidade(ealSeq)==true){
				Short tmvSeq = null;
				Byte tmvComplemento = null;
				if(devolucaoAlmoxarifado.getTipoMovimento()!=null && devolucaoAlmoxarifado.getTipoMovimento().getId()!=null){
					tmvSeq = devolucaoAlmoxarifado.getTipoMovimento().getId().getSeq();
					tmvComplemento = devolucaoAlmoxarifado.getTipoMovimento().getId().getComplemento();
				}
				this.getControlarValidadeMaterialRN().atualizarValidadesEstoque(tmvSeq, tmvComplemento,devolucaoAlmoxarifado.getSeq(), ealSeq, umdDocumento, umdEstoque, material);
			}
		}
		if(!encontrou){
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_00293);
		}
	}
	
	
	

			    
	
	/**
	 * Procedure
	 * ORADB: SCEK_SCE_RN.RN_SCEP_VER_ALT_ESTO
	 */
	public void verificarAltEstorno(Boolean estorno) throws ApplicationBusinessException{
		if(!estorno){
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_00297);
		}
	}
	
	/**
	 * Procedure
	 * ORADB:  SCEK_SCE_RN.RN_SCEP_VER_CCT_ATIV
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void verificarCentroCusto(Integer codigo) throws ApplicationBusinessException{
		FccCentroCustos centroCusto = this.getCentroCustoFacade().obterFccCentroCustosAtivos(codigo);
		if(centroCusto == null){
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.CENTRO_CUSTO_NAO_CADASTRADO_OU_INATIVO, codigo);
		}
	}
	
	/**
	 * Procedure
	 * ORADB: SCEK_DAL_RN.RN_DALP_ATU_ESTORNO
	 * @param servidor
	 * @param dataEstorno
	 * @param tipoMovimento
	 * @param nroDocGeracao
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void atualizarEstorno(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) throws ApplicationBusinessException{
		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		Date vlrData = parametro.getVlrData();
		SceMovimentoMaterial movimentoMaterial = this.getSceMovimentoMaterialDAO().obterSceMovimentoMaterialPorTmvSeqTmvComplNroDoc(devolucaoAlmoxarifado.getTipoMovimento(), devolucaoAlmoxarifado.getSeq());
		if(movimentoMaterial==null || movimentoMaterial.getId()==null ||  movimentoMaterial.getId().getDtCompetencia()==null){
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_0035);
		} else if(movimentoMaterial.getId().getDtCompetencia()!=null){
			Calendar dtComptencia = Calendar.getInstance();
			dtComptencia.setTime(movimentoMaterial.getId().getDtCompetencia());
			Calendar dtParametro = Calendar.getInstance();
			dtParametro.setTime(vlrData);
			if(dtComptencia.get(Calendar.MONTH)!=dtParametro.get(Calendar.MONTH)){
				throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_00366);	
			}
		} else {
			devolucaoAlmoxarifado.setDtEstorno(new Date());
		}
	}
	
	/**
	 * Procedure
	 * ORADB: SCEK_DAL_RN.RN_DALP_VER_FILHO
	 * @param dalSeq
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("ucd")
	public void verificarFilho(Integer dalSeq) throws ApplicationBusinessException{
		SceDevolucaoAlmoxarifado devolucaoAlmoxarifado = this.getSceDevolucaoAlmoxarifadoDAO().obterPorChavePrimaria(dalSeq);
		Set<SceItemDas> itens = devolucaoAlmoxarifado.getSceItemDas();
		if(itens==null ||itens.size()==0){
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_00369);
		}
	}
	
	public void atualizarDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifadoOld, SceDevolucaoAlmoxarifado devolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException{
		this.preAtualizarDevolucaoAlmoxarifado(devolucaoAlmoxarifadoOld, devolucaoAlmoxarifado, nomeMicrocomputador);
		this.getSceDevolucaoAlmoxarifadoDAO().merge(devolucaoAlmoxarifado);
	}
	
	public void persistirDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) throws ApplicationBusinessException{
		this.executarBeforeInsertDevolucaoAlmoxarifado(devolucaoAlmoxarifado);
		this.getSceDevolucaoAlmoxarifadoDAO().persistir(devolucaoAlmoxarifado);
		this.getSceDevolucaoAlmoxarifadoDAO().flush();
	}
	
	/**
	 * Trigger
	 * ORADB: SCET_DAL_BRU 
	 * @throws BaseException 
	 */
	public void preAtualizarDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifadoOld, SceDevolucaoAlmoxarifado devolucaoAlmoxarifado, String nomeMicrocomputador) throws BaseException{
		Short vlrNumerico = null;
		if(!devolucaoAlmoxarifado.getTipoMovimento().equals(devolucaoAlmoxarifadoOld.getTipoMovimento())){
			AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_DA);
			vlrNumerico = parametro.getVlrNumerico().shortValue();
			if((devolucaoAlmoxarifado.getTipoMovimento()!=null && devolucaoAlmoxarifado.getTipoMovimento().getId()!=null && !devolucaoAlmoxarifado.getTipoMovimento().getId().getSeq().equals(vlrNumerico))||
					(devolucaoAlmoxarifado.getTipoMovimento()==null && vlrNumerico != null) ||
					(devolucaoAlmoxarifado.getTipoMovimento()!=null && vlrNumerico == null)	){
				throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_00367);
			} else {
				this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(devolucaoAlmoxarifado.getTipoMovimento());
			}
		}
		if(!devolucaoAlmoxarifado.getAlmoxarifado().equals(devolucaoAlmoxarifadoOld.getAlmoxarifado())){
			SceAlmoxarifado almoxarifado = null;
			Short almoxSeq = null;
			if(devolucaoAlmoxarifado.getAlmoxarifado()!=null){
				almoxarifado = devolucaoAlmoxarifado.getAlmoxarifado();
				almoxSeq = almoxarifado.getSeq();
			}
			this.getManterTransferenciaMaterialRN().isAlmoxarifadoValido(almoxSeq);
		}
		
		if(!devolucaoAlmoxarifado.getCentroCusto().equals(devolucaoAlmoxarifadoOld.getCentroCusto())){
			FccCentroCustos centroCusto = null;
			Integer cctCodigo = null;
			if(devolucaoAlmoxarifado.getCentroCusto()!=null){
				centroCusto = devolucaoAlmoxarifado.getCentroCusto();
				cctCodigo = centroCusto.getCodigo();
			}
			this.verificarCentroCusto(cctCodigo);
		}
		
		if(!devolucaoAlmoxarifadoOld.getEstorno().equals(devolucaoAlmoxarifado.getEstorno())){
			this.verificarAltEstorno(devolucaoAlmoxarifado.getEstorno());
			this.atualizarEstorno(devolucaoAlmoxarifado);
		}
		this.atualizarMovimentoEstorno(devolucaoAlmoxarifado, nomeMicrocomputador);
		if(!devolucaoAlmoxarifado.getDtGeracao().equals(devolucaoAlmoxarifadoOld.getDtGeracao())){
			throw new ApplicationBusinessException(DevolucaoAlmoxarifadoRNExceptionCode.SCE_00512);
		}
		this.verificarEstorno(devolucaoAlmoxarifadoOld.getEstorno());
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: SCET_DAL_BRI
	 * 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void executarBeforeInsertDevolucaoAlmoxarifado(SceDevolucaoAlmoxarifado devolucaoAlmoxarifadoNew) 
			throws ApplicationBusinessException {
		
		if (devolucaoAlmoxarifadoNew == null) {
			throw new IllegalArgumentException("Parametro devolucaoAlmoxarifadoNew não informado!");
		}
		
		AghParametros paramTipoMovimentoDa = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TMV_DOC_DA);
		
		/*
		 * Move tipo de movimento do parametro
		 * e o complemento ativo
		 */
		SceTipoMovimento tipoMovimento = getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(
				paramTipoMovimentoDa.getVlrNumerico().shortValue());
		
		devolucaoAlmoxarifadoNew.setTipoMovimento(tipoMovimento);
		
		// Verifica tipo de movimento ativo
		verificarTipoMovimentoAtivo(tipoMovimento);
		
		// Verifica almoxarifado ativo
		verificarAlmoxarifadoAtivo(devolucaoAlmoxarifadoNew);
		
		// Verifica centro de custo ativo
		verificarCentroCusto(devolucaoAlmoxarifadoNew.getCentroCusto().getCodigo());
		
		// Atualiza servidor e data de geração
		devolucaoAlmoxarifadoNew = atualizarServidorDataGeracao(devolucaoAlmoxarifadoNew);
		
		// Garante a não geração de 'DA' já estornada
		devolucaoAlmoxarifadoNew.setEstorno(Boolean.FALSE);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: SCEK_DAL_RN.RN_DALP_VER_TMV_ATIV
	 * 
	 * @param tipoMovimento
	 */
	public SceTipoMovimento verificarTipoMovimentoAtivo(SceTipoMovimento tipoMovimento) throws ApplicationBusinessException {
		return getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(tipoMovimento);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: SCEK_DAL_RN.RN_DALP_VER_ALM_ATIV
	 * 
	 * @param devolucaoAlmoxarifado
	 * @return
	 */
	public Boolean verificarAlmoxarifadoAtivo(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) {
		if (devolucaoAlmoxarifado == null) {
			throw new IllegalArgumentException("Parametro devolucaoAlmoxarifado não informado!");
		}
		return getManterTransferenciaMaterialRN().isAlmoxarifadoValido(devolucaoAlmoxarifado.getAlmoxarifado().getSeq());
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: SCEK_DAL_RN.RN_DALP_ATU_GERACAO
	 * 
	 * @param devolucaoAlmoxarifado
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public SceDevolucaoAlmoxarifado atualizarServidorDataGeracao(SceDevolucaoAlmoxarifado devolucaoAlmoxarifado) throws ApplicationBusinessException {
		if (devolucaoAlmoxarifado == null) {
			throw new IllegalArgumentException("Parametro devolucaoAlmoxarifado não informado!");
		}
		
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		devolucaoAlmoxarifado.setServidor(servidorLogado);
		devolucaoAlmoxarifado.setDtGeracao(new Date());
		return devolucaoAlmoxarifado;
	}
	
	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	private SceItemDasDAO getSceItemDasDAO() {
		return sceItemDasDAO;
	}
	
	private SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}
	
	private SceDevolucaoAlmoxarifadoDAO getSceDevolucaoAlmoxarifadoDAO() {
		return sceDevolucaoAlmoxarifadoDAO;
	}

	protected SceMovimentoMaterialRN getSceMovimentoMaterialRN() {
		return sceMovimentoMaterialRN;
	}
	
	private ControlarValidadeMaterialRN getControlarValidadeMaterialRN() {
		return controlarValidadeMaterialRN;
	}
	
	protected SceTipoMovimentosRN getSceTipoMovimentosRN() {
		return sceTipoMovimentosRN;
	}
	
	protected ManterTransferenciaMaterialRN getManterTransferenciaMaterialRN() {
		return manterTransferenciaMaterialRN;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
