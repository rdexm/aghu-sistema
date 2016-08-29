package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.centrocusto.dao.FccCentroCustosDAO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoMaterialDAO;
import br.gov.mec.aghu.compras.dao.ScoUnidadeMedidaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.NumberUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndMovimento;
import br.gov.mec.aghu.dominio.DominioIndOperacaoBasica;
import br.gov.mec.aghu.dominio.DominioIndUsoCm;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceConsumoTotalMateriaisDAO;
import br.gov.mec.aghu.estoque.dao.SceEntradaSaidaSemLicitacaoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceEstqAlmoxMvtoDAO;
import br.gov.mec.aghu.estoque.dao.SceHistoricoProblemaMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceItemTransferenciaDAO;
import br.gov.mec.aghu.estoque.dao.SceMovimentoMaterialDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.estoque.vo.MovimentoMaterialVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceConsumoTotalMaterial;
import br.gov.mec.aghu.model.SceConsumoTotalMaterialId;
import br.gov.mec.aghu.model.SceEntradaSaidaSemLicitacao;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceEstqAlmoxMvto;
import br.gov.mec.aghu.model.SceEstqAlmoxMvtoId;
import br.gov.mec.aghu.model.SceHistoricoProblemaMaterial;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.SceTipoMovimentoId;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({"PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity", "PMD.ExcessiveClassLength"})
@Stateless
public class SceMovimentoMaterialRN extends BaseBusiness{

	@EJB
	private SceValidadesRN sceValidadesRN;
	@EJB
	private ControlarValidadeMaterialRN controlarValidadeMaterialRN;
	@EJB
	private SceEstqAlmoxMvtoRN sceEstqAlmoxMvtoRN;
	@EJB
	private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;
	@EJB
	private SceEstoqueGeralRN sceEstoqueGeralRN;
	@EJB
	private SceHistoricoProblemaMaterialRN sceHistoricoProblemaMaterialRN;
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;
	
	private static final Log LOG = LogFactory.getLog(SceMovimentoMaterialRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceItemTransferenciaDAO sceItemTransferenciaDAO;
	
	@Inject
	private SceEntradaSaidaSemLicitacaoDAO sceEntradaSaidaSemLicitacaoDAO;
	
	@Inject
	private SceConsumoTotalMateriaisDAO sceConsumoTotalMateriaisDAO;
	
	@Inject
	private ScoMaterialDAO scoMaterialDAO; 

	

	@Inject
	private ScoUnidadeMedidaDAO scoUnidadeMedidaDAO; 

	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private SceEstqAlmoxMvtoDAO sceEstqAlmoxMvtoDAO;
	
	@Inject
	private SceValidadeDAO sceValidadeDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceItemRmsDAO sceItemRmsDAO;
	
	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;
	
	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;
	
	@Inject
	private ScoFornecedorDAO scoFornecedorDAO;
	
	@Inject
	private SceMovimentoMaterialDAO sceMovimentoMaterialDAO;
	
	@Inject
	private SceHistoricoProblemaMaterialDAO sceHistoricoProblemaMaterialDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	@Inject
	private SceTipoMovimentosDAO sceTipoMovimentosDAO;

	@Inject
	private FccCentroCustosDAO fccCentroCustosDAO;  
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2070436252878025533L;

	public enum SceMovimentoMaterialRNExceptionCode implements BusinessExceptionCode {
		SCE_00302,SCE_00303,SCE_00294,SCE_00295,SCE_00293,SCE_00862,SCE_00863,MENSAGEM_TIPO_MOVIMENTO_NAO_CADASTRADO,MENSAGEM_DOCUMENTO_RCSAF_NAO_ENCONTRADO,SCE_00430,
		MENSAGEM_SALDO_INSUFICIENTE_FRN,MENSAGEM_SALDO_INSUFICIENTE_DESBLOQUEIO,MENSAGEM_SALDO_INSUFICIENTE_ALM,MENSAGEM_NRO_TRANSF_NAO_ENCONTRADO,
		SCE_00432,SCE_00431, MENSAGEM_SALDO_FINANC_INSUFICIENTE, MENSAGEM_SALDO_FINANC_SUPERIOR_DF, SCE_00433, ERRO_VALOR_MINIMO_MOVIMENTO, ERRO_CM_MOVIMENTO, MENSAGEM_MOVIMENTO_FORA_COMPETENCIA,
		MENSAGEM_ROTINA_FECHAMENTO_EM_EXECUCAO,  ERRO_ALMOXARIFADO_NAO_ASSOC;
	}

	/**
	 * ORADB PROCEDURE SCEK_RMS_RN.RN_RMSP_ATU_ESTORNO
	 * Método verifica se há movimentos na requisição e se o estorno da RN esta dentro do mês de competência.
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 * @throws BaseException 
	 */
	public void verificarMovimentoMaterialEstornoPorRequisicaoMateral(SceReqMaterial sceReqMateriais) throws BaseException {

		AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		SceMovimentoMaterial movimento = getSceMovimentoMaterialDAO().obterSceMovimentoMaterialPorTmvSeqTmvComplNroDoc(sceReqMateriais.getTipoMovimento(), sceReqMateriais.getSeq());

		if (movimento == null) {

			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00302);

		} else {

			if (!DateUtil.isDatasIguais(DateUtil.truncaData(movimento.getId().getDtCompetencia()), DateUtil.truncaData(parametro.getVlrData()))) {

				throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00303);

			}

		}

	}

	/**
	 * ORADB PROCEDURE SCEK_RMS_RN.RN_RMSP_ATU_MVTO
	 * Gera Movimento de Estoque 
	 * @param sceReqMateriais
	 * @throws BaseException 
	 */
	public void gerarMovimento(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		List<SceItemRms> itens = getSceItemRmsDAO().pesquisarSceItemRmsPorSceReqMateriaisAlmoxarifado(sceReqMateriais.getSeq());

		for( SceItemRms item : itens) {

			if (item.getQtdeEntregue() == null) {

				item.setQtdeEntregue(item.getQtdeRequisitada());

			}

			if (item.getQtdeEntregue() > 0) {


				this.atualizarMovimentoMaterial(sceReqMateriais.getAlmoxarifado(),item.getEstoqueAlmoxarifado().getMaterial(),item.getScoUnidadeMedida(),
						item.getQtdeEntregue(),item.getQtdeRequisitada(),Boolean.FALSE,sceReqMateriais.getTipoMovimento(),null,sceReqMateriais.getSeq(),
						null,null,item.getEstoqueAlmoxarifado().getFornecedor(),sceReqMateriais.getCentroCusto(),sceReqMateriais.getCentroCustoAplica(),
						null,null,null, nomeMicrocomputador, true);			

				if (item.getEstoqueAlmoxarifado().getIndControleValidade()) {
					

					SceEstoqueAlmoxarifado ealOrigem = item.getEstoqueAlmoxarifado();
					SceTipoMovimento tipoMovimento = sceReqMateriais.getTipoMovimento();
					Integer nroDocumento = sceReqMateriais.getSeq();
					Integer quantidade = item.getQtdeEntregue();
					
					// TODO Chamada da PROCEDURE SCEK_VAL_ATUALIZACAO.SCEP_VAL_ATU_VALID
					this.getControlarValidadeMaterialRN().atualizarQuantidadesValidade(null, ealOrigem, tipoMovimento, nroDocumento, quantidade);

				}

			}

		}

	}

	/**
	 * ORADB PROCEDURE SCEK_SCE_RN.RN_SCEP_ATU_MVTO
	 * Insere um movimento material
	 * @throws BaseException 
	 */
	public void atualizarMovimentoMaterial(SceAlmoxarifado almoxarifado, ScoMaterial material, ScoUnidadeMedida unidadeMedida, 
			Integer quantidade, Integer qtdeRequisitada, Boolean indEstorno, SceTipoMovimento tipoMovimento, 
			SceTipoMovimento tipoMovimentoDocumento, Integer nroDocGeracao, Short itemDocGeracao, String historico, 
			ScoFornecedor fornecedor, FccCentroCustos centroCustoRequisita, FccCentroCustos centroCusto, 
			SceAlmoxarifado almoxarifadoComplemento, BigDecimal valor, Integer nroDocRefere, String nomeMicrocomputador,
			Boolean flush) throws BaseException {

		SceMovimentoMaterial sceMovimentoMaterial = new SceMovimentoMaterial();
		
		almoxarifado = getSceAlmoxarifadoDAO().obterPorChavePrimaria(almoxarifado.getSeq());
		sceMovimentoMaterial.setAlmoxarifado(almoxarifado);
		material = getScoMaterialDAO().obterPorChavePrimaria(material.getCodigo());
		sceMovimentoMaterial.setMaterial(material);
		unidadeMedida = getScoUnidadeMedidaDAO().obterPorChavePrimaria(unidadeMedida.getCodigo());
		sceMovimentoMaterial.setUnidadeMedida(unidadeMedida);
		sceMovimentoMaterial.setQuantidade(quantidade);
		sceMovimentoMaterial.setQtdeRequisitada(qtdeRequisitada);
		sceMovimentoMaterial.setIndEstorno(indEstorno);
//		getSceMovimentoMaterialDAO().obterPorChavePrimaria(tipoMovimento.getId());
		sceMovimentoMaterial.setTipoMovimento(tipoMovimento);
		sceMovimentoMaterial.setTipoMovimentoDocumento(tipoMovimentoDocumento);
		sceMovimentoMaterial.setNroDocGeracao(nroDocGeracao);
		sceMovimentoMaterial.setItemDocGeracao(itemDocGeracao);
		sceMovimentoMaterial.setHistorico(historico);
		fornecedor= getScoFornecedorDAO().obterPorChavePrimaria(fornecedor.getNumero());
		sceMovimentoMaterial.setFornecedor(fornecedor);
		if (centroCusto !=null){
			centroCustoRequisita = getFccCentroCustosDAO().obterPorChavePrimaria(centroCusto.getCodigo());
		}
		
		sceMovimentoMaterial.setCentroCustoRequisita(centroCustoRequisita);
		sceMovimentoMaterial.setCentroCusto(centroCusto);
		
		if(almoxarifadoComplemento != null){
			almoxarifadoComplemento = getSceAlmoxarifadoDAO().obterPorChavePrimaria(almoxarifadoComplemento.getSeq());
		} else {
			almoxarifadoComplemento = getSceAlmoxarifadoDAO().obterPorChavePrimaria(almoxarifado.getSeq());
		}
		
		sceMovimentoMaterial.setAlmoxarifadoComplemento(almoxarifadoComplemento);
		sceMovimentoMaterial.setValor(valor);
		sceMovimentoMaterial.setNroDocRefere(nroDocRefere);

		this.inserir(sceMovimentoMaterial, nomeMicrocomputador, flush);
	}



	/**
	 * ORADB PROCEDURE SCEK_RMS_RN.RN_RMSP_ATU_MVTO_EST
	 * Gera Movimento de Estoque 
	 * @param sceReqMateriais
	 * @throws BaseException 
	 */
	public void gerarMovimentoEstoque(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {

		List<SceItemRms> itens = getSceItemRmsDAO().pesquisarSceItemRmsPorSceReqMateriaisAlmoxarifado(sceReqMateriais.getSeq());
		Integer quantidade = null;
		Boolean encontrouMovimento = false;

		for (SceItemRms item : itens) {

			encontrouMovimento = true;

			quantidade = item.getQtdeEntregue();
			
			if (item.getQtdeEntregue() == null) {

				item.setQtdeEntregue(item.getQtdeRequisitada());

			}

			if (item.getQtdeEntregue() > 0) {

				SceMovimentoMaterial sceMovimentoMaterial = new SceMovimentoMaterial();

				sceMovimentoMaterial.setAlmoxarifado(sceReqMateriais.getAlmoxarifado());
				sceMovimentoMaterial.setAlmoxarifadoComplemento(null);
				sceMovimentoMaterial.setCentroCusto(sceReqMateriais.getCentroCustoAplica());
				sceMovimentoMaterial.setCentroCustoRequisita(sceReqMateriais.getCentroCusto());
				sceMovimentoMaterial.setDtGeracao(new Date());
				sceMovimentoMaterial.setFornecedor(item.getEstoqueAlmoxarifado().getFornecedor());
				sceMovimentoMaterial.setHistorico(null);
				sceMovimentoMaterial.setIndEstorno(Boolean.TRUE);
				sceMovimentoMaterial.setItemDocGeracao(null);
				sceMovimentoMaterial.setTipoMovimento(sceReqMateriais.getTipoMovimento());

				sceMovimentoMaterial.setMaterial(item.getEstoqueAlmoxarifado().getMaterial());

				sceMovimentoMaterial.setNroDocGeracao(sceReqMateriais.getSeq());
				sceMovimentoMaterial.setNroDocRefere(null);
				sceMovimentoMaterial.setQtdeRequisitada(item.getQtdeRequisitada());
				sceMovimentoMaterial.setQuantidade(quantidade);
				sceMovimentoMaterial.setUnidadeMedida(item.getScoUnidadeMedida());
				sceMovimentoMaterial.setValor(null);

				inserir(sceMovimentoMaterial, nomeMicrocomputador, true);

				if (item.getEstoqueAlmoxarifado().getIndControleValidade()) {
					
					// TODO REALIZAR A INTEGRAÇAO
					Short tmvSeq = sceReqMateriais.getTipoMovimento().getId().getSeq();
					Byte tmvComplemento = sceReqMateriais.getTipoMovimento().getId().getComplemento(); 
					Integer nroDocumento = sceReqMateriais.getSeq(); 
					Integer ealSeq = item.getEstoqueAlmoxarifado().getSeq();
					ScoUnidadeMedida umdCodigoDocumento = item.getScoUnidadeMedida(); 
					ScoUnidadeMedida umdCodigoEstoque = item.getEstoqueAlmoxarifado().getUnidadeMedida();
					ScoMaterial matCodigo = item.getEstoqueAlmoxarifado().getMaterial();
		
					// TODO Chamada da PROCEDURE SCEP_VAL_EST_VALID
					this.getControlarValidadeMaterialRN().atualizarValidadesEstoque(tmvSeq, tmvComplemento, nroDocumento, ealSeq, umdCodigoDocumento, umdCodigoEstoque, matCodigo);

				}

			}

		}

		if (!encontrouMovimento) {

			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00293);

		}

	}


	/**
	 * Inserir SceMovimentoMaterial
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 */
	public void inserir(SceMovimentoMaterial sceMovimentoMaterial, String nomeMicrocomputador, boolean flush)
			throws BaseException {

		this.preInserir(sceMovimentoMaterial, nomeMicrocomputador, flush);
		this.validaGeradoNaCompetencia(sceMovimentoMaterial);
		this.getSceMovimentoMaterialDAO().persistir(sceMovimentoMaterial);
		if (flush) {
			this.getSceMovimentoMaterialDAO().flush();
		}
		this.posInserir(sceMovimentoMaterial, nomeMicrocomputador, flush);
		
	}
	
	private void validaGeradoNaCompetencia(
			SceMovimentoMaterial sceMovimentoMaterial) throws ApplicationBusinessException {
			DominioSimNao validaMovCompetencia = DominioSimNao.valueOf(StringUtils.upperCase(getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_AGHU_VALIDA_MOVIMENTO_FORA_COMPETENCIA)
					.getVlrTexto()));
	
			Calendar calGeracao = Calendar.getInstance(); 
			calGeracao.setTime(sceMovimentoMaterial.getDtGeracao());
			calGeracao.set(Calendar.DAY_OF_MONTH, 1);
			calGeracao.set(Calendar.HOUR_OF_DAY, 0);
			calGeracao.set(Calendar.MINUTE, 0);
			calGeracao.set(Calendar.SECOND, 0);
			calGeracao.set(Calendar.MILLISECOND, 0);
			
			Calendar calCompetencia = Calendar.getInstance(); 
			calCompetencia.setTime(sceMovimentoMaterial.getDtCompetencia());
			
			if (DominioSimNao.S.equals(validaMovCompetencia)
				&& (calGeracao.compareTo(calCompetencia) != 0)) {
			throw new ApplicationBusinessException(
					SceMovimentoMaterialRNExceptionCode.MENSAGEM_MOVIMENTO_FORA_COMPETENCIA,
					sceMovimentoMaterial.getDtGeracao(), sceMovimentoMaterial
							.getDtCompetencia());
			}
	}


	public List<SceTipoMovimento> obterTipoMovimentoPorSeqDescricaoAjustes(String param) throws ApplicationBusinessException {
		List<Short> valoresParam = new ArrayList<Short>();
		valoresParam.add(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_AJUSTE_POS).getVlrNumerico().shortValue());
		valoresParam.add(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_AJUSTE_NEG).getVlrNumerico().shortValue());

		return getSceTipoMovimentosDAO().obterTipoMovimentoPorSeqDescricaoAjustes(param, valoresParam);	
	}

	public List<SceTipoMovimento> obterTipoMovimentoPorSeqDescricaoBloqueioDesbloqueioComProblema(String param, boolean mostrarTodos) throws ApplicationBusinessException {
		
		List<Short> valoresParam = new ArrayList<Short>();
		
		if (mostrarTodos) {
		
			valoresParam.add(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_BLOQ_PROB_DISP).getVlrNumerico().shortValue());
			valoresParam.add(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_BLOQ_PROB_BLOQ).getVlrNumerico().shortValue());
		
		} else {
			
			valoresParam.add(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DISP_BLOQ_PROB).getVlrNumerico().shortValue()); 
			valoresParam.add(getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_BLOQ_BLOQ_PROB).getVlrNumerico().shortValue());
			
		}

		return getSceTipoMovimentosDAO().obterTipoMovimentoPorSeqDescricaoAjustes(param, valoresParam);	
	
	}

	protected void posInserir(SceMovimentoMaterial sceMovimentoMaterial, String nomeMicrocomputador, boolean flush)
			throws BaseException {
		//Regras do SCEP_ENFORCE_MMT_RULES
		this.enforceMmtRules(sceMovimentoMaterial, nomeMicrocomputador, flush);
	}

	/** 
	 * ORADB TRIGGER SCEP_ENFORCE_MMT_RULES (INSERT)
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 * Obs: if p_event in ('INSERT','UPDATE')  não foi implementado no insert
	 */
	private void enforceMmtRules(SceMovimentoMaterial sceMovimentoMaterial, String nomeMicrocomputador, boolean flush)
			throws BaseException {

		AghParametros aghParametroFf = null;
		AghParametros aghParametroNr = null;

		try {

			aghParametroFf = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_FF);
			aghParametroNr = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);

		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00862);
		}

		if (sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE)) {

			if (sceMovimentoMaterial.getTipoMovimento().getId().getSeq() == aghParametroFf.getVlrNumerico().shortValue()) {

				this.atualizarSceEstqAlmoxs(sceMovimentoMaterial.getDtGeracao(), sceMovimentoMaterial.getMaterial().getCodigo(), sceMovimentoMaterial
						.getAlmoxarifado().getSeq(), DominioIndMovimento.FFX, nomeMicrocomputador, flush);

			}


			if (sceMovimentoMaterial.getTipoMovimento().getId().getSeq() == aghParametroNr.getVlrNumerico().shortValue()) {

				this.atualizarSceEstqAlmoxs(sceMovimentoMaterial.getDtGeracao(), sceMovimentoMaterial.getMaterial().getCodigo(), sceMovimentoMaterial
						.getAlmoxarifado().getSeq(), DominioIndMovimento.COM, nomeMicrocomputador, flush);

			}

			if (sceMovimentoMaterial.getTipoMovimento().getIndOperacaoBasica().equals(DominioIndOperacaoBasica.DB) 
					&& sceMovimentoMaterial.getTipoMovimento().getMovimentoConsumo().equals(Boolean.TRUE) ) {

				this.atualizarSceEstqAlmoxs(sceMovimentoMaterial.getDtGeracao(), sceMovimentoMaterial.getMaterial().getCodigo(), sceMovimentoMaterial
						.getAlmoxarifado().getSeq(), DominioIndMovimento.CON, nomeMicrocomputador, flush);

			}

		} else {

			if (sceMovimentoMaterial.getTipoMovimento().getId().getSeq() == aghParametroFf.getVlrNumerico().shortValue()) {

				this.atualizarSceEstqAlmoxs(null, sceMovimentoMaterial.getMaterial().getCodigo(), sceMovimentoMaterial.getAlmoxarifado().getSeq(),
						DominioIndMovimento.FFX, nomeMicrocomputador, flush);

			}


			if (sceMovimentoMaterial.getTipoMovimento().getId().getSeq() == aghParametroNr.getVlrNumerico().shortValue()) {

				this.atualizarSceEstqAlmoxs(null, sceMovimentoMaterial.getMaterial().getCodigo(), sceMovimentoMaterial.getAlmoxarifado().getSeq(),
						DominioIndMovimento.COM, nomeMicrocomputador, flush);

			}

			if (sceMovimentoMaterial.getTipoMovimento().getIndOperacaoBasica().equals(DominioIndOperacaoBasica.DB) 
					&& sceMovimentoMaterial.getTipoMovimento().getMovimentoConsumo().equals(Boolean.TRUE) ) {

				this.atualizarSceEstqAlmoxs(null, sceMovimentoMaterial.getMaterial().getCodigo(), sceMovimentoMaterial.getAlmoxarifado().getSeq(),
						DominioIndMovimento.CON, nomeMicrocomputador, flush);

			}

		}

	}

	public void atualizarSceEstqAlmoxs(Date dtGeracao, Integer matCodigo, Short almSeq, DominioIndMovimento indMovimento, String nomeMicrocomputador) throws BaseException {
		this.atualizarSceEstqAlmoxs(dtGeracao, matCodigo, almSeq, indMovimento, nomeMicrocomputador, true);
	}

	/**
	 * ORADB PROCEDURE SCEK_MMT_RN.RN_MMTP_ATU_ESTQ_ALM
	 * 
	 * @param dtGeracao
	 * @param matcodigo
	 * @param almSeq
	 * @param indMovimento
	 * @throws BaseException
	 */
	public void atualizarSceEstqAlmoxs(Date dtGeracao, Integer matCodigo, Short almSeq, DominioIndMovimento indMovimento, String nomeMicrocomputador, boolean flush) throws BaseException {

		List<SceEstoqueAlmoxarifado> listaEstAlm = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmPorMaterialAlmoxirafado(matCodigo, almSeq);

		for (SceEstoqueAlmoxarifado estqAlmoxs: listaEstAlm) {

			/* COMPRA */
			if (indMovimento.equals(DominioIndMovimento.COM)) {

				if (dtGeracao != null) {

					estqAlmoxs.setDtUltimaCompra(dtGeracao);

				} else {

					estqAlmoxs.setDtUltimaCompra(obterDataUltimaCompra(matCodigo, almSeq)); //seta data da ultimacompra.

				}

			}

			/*  CONSUMO */
			if (indMovimento.equals(DominioIndMovimento.CON)) {

				if (dtGeracao != null) {

					estqAlmoxs.setDtUltimoConsumo(dtGeracao);

				} else {

					estqAlmoxs.setDtUltimoConsumo(getSceMovimentoMaterialDAO().obterDataUltimaRequisicao(matCodigo, almSeq)); //seta data ultimo consumo

				}

			}

			/* COMPRA FUNDO FIXO  */
			if (indMovimento.equals(DominioIndMovimento.FFX)) {

				if (dtGeracao != null) {

					estqAlmoxs.setDtUltimaCompraFf(dtGeracao);

				} else {

					estqAlmoxs.setDtUltimaCompraFf(obterDataUltimaCff(matCodigo, almSeq)); //seta data ultima compra fundo fixo.

				}

			}

			/*
			 * Atualizar tabela sce_estq_almoxs
			 */
			this.getSceEstoqueAlmoxarifadoRN().atualizar(estqAlmoxs, nomeMicrocomputador, flush);

		}

	}


	/**
	 * ORADB TRIGGER SCET_MMT_BRI (INSERT)  
	 * Pré-inserir SceMovimentoMaterial
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 */
	protected void preInserir(SceMovimentoMaterial sceMovimentoMaterial, String nomeMicrocomputador, Boolean flush) throws BaseException{

		this.validaTipoMovimento(sceMovimentoMaterial); // RN1
		this.validaFechamentoMensal(sceMovimentoMaterial); // RN2
		this.atualizaServidorDtGeracao(sceMovimentoMaterial);//RN3 
		this.validaCentroCusto(sceMovimentoMaterial); //RN4 e RN5
		this.atualizaCodigoCentroCusto(sceMovimentoMaterial); //RN6
		/**
		 * RN7
		 * Sprint 43, estória #12310.
		 */
		this.atualizaEstoquePorTipoMovimento(sceMovimentoMaterial, nomeMicrocomputador);  
		
		this.validaValorMovimenoMinimo(sceMovimentoMaterial);

		
		// Verifica se o movimento de material de origem possuí um próximo tipo de movimento
		if(sceMovimentoMaterial.getTipoMovimento().getTipoMovimentoProximo() != null && sceMovimentoMaterial.getTipoMovimento().getTipoMovimentoProximo().getId() != null){

			// Obtém o próximo movimento (Ex. 18) do movimento de origem (Ex. 17)
			final SceTipoMovimento proximoTipoMovimento = sceMovimentoMaterial.getTipoMovimento().getTipoMovimentoProximo();
	
			// Intancia um novo movimento de material para o próximo movimento
			SceMovimentoMaterial novoMovimentoMaterial = new SceMovimentoMaterial();

			novoMovimentoMaterial.setMaterial(sceMovimentoMaterial.getMaterial()); 
			novoMovimentoMaterial.setUnidadeMedida(sceMovimentoMaterial.getUnidadeMedida()); 
			novoMovimentoMaterial.setQuantidade(sceMovimentoMaterial.getQuantidade()); 
			novoMovimentoMaterial.setIndEstorno(sceMovimentoMaterial.getIndEstorno());
			
			novoMovimentoMaterial.setTipoMovimento(proximoTipoMovimento); 
			
			novoMovimentoMaterial.setTipoMovimentoDocumento(sceMovimentoMaterial.getTipoMovimento()); 
			
			novoMovimentoMaterial.setNroDocGeracao(sceMovimentoMaterial.getNroDocGeracao()); 
			novoMovimentoMaterial.setItemDocGeracao(sceMovimentoMaterial.getItemDocGeracao()); 
			novoMovimentoMaterial.setHistorico(sceMovimentoMaterial.getHistorico()); 
			novoMovimentoMaterial.setFornecedor(sceMovimentoMaterial.getFornecedor()); 
	
			// Verifica so tipo de movimento é uma "Reapropriação de Centro de Custo Débito"
			AghParametros parametroReapropriacaoCentroCustoDebito = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_MOV_REAPR_CR);
			Short valorParametroReapropriacaoCentroCustoDebito = parametroReapropriacaoCentroCustoDebito.getVlrNumerico().shortValue();
			
			if(valorParametroReapropriacaoCentroCustoDebito != null && valorParametroReapropriacaoCentroCustoDebito.equals(sceMovimentoMaterial.getTipoMovimento().getId().getSeq())){
				/*
				 * Em caso de reapropriação de Centro de Custo (Auto-Relacionamento):
				 * - Saída: o centro de custo recebe centro de custo que requisita
				 * - Entrada: o centro de custo que requisita recebe o centro de custo
				 */
				novoMovimentoMaterial.setCentroCustoRequisita(sceMovimentoMaterial.getCentroCusto()); 
				novoMovimentoMaterial.setCentroCusto(sceMovimentoMaterial.getCentroCustoRequisita());	
			} else{
				novoMovimentoMaterial.setCentroCustoRequisita(sceMovimentoMaterial.getCentroCustoRequisita()); 
				novoMovimentoMaterial.setCentroCusto(sceMovimentoMaterial.getCentroCusto()); 
			}


			if(sceMovimentoMaterial.getAlmoxarifadoComplemento() != null ){
				
				/* 
				 * Quando almoxarifado complemento existir (caso da transferência) os 
				 * almoxarifados serão complementares tanto na saída quanto na entrada
				 */
				novoMovimentoMaterial.setAlmoxarifado(sceMovimentoMaterial.getAlmoxarifadoComplemento());
				novoMovimentoMaterial.setAlmoxarifadoComplemento(sceMovimentoMaterial.getAlmoxarifado()); 
				
			}  else {
				novoMovimentoMaterial.setAlmoxarifado(sceMovimentoMaterial.getAlmoxarifado());
			}
	
			novoMovimentoMaterial.setValor(sceMovimentoMaterial.getValor());
			novoMovimentoMaterial.setNroDocRefere(sceMovimentoMaterial.getNroDocRefere());
	
			/*
			 * ORADB PROCEDURE SCEP_MMT_TMV_PROX (INSERT SIMPLES EM SCE_MOVIMENTO_MATERIAIS)
			 * Gera o próximo movimento de material através de um movimento de origem
			 */
			this.inserir(novoMovimentoMaterial, nomeMicrocomputador, flush);
		
		}

	}


	/**
	 * ORADB PROCEDURE SCEP_MMT_ATU_POR_TMV 
	 */
	public void atualizaEstoquePorTipoMovimento(SceMovimentoMaterial sceMovimentoMaterial, String nomeMicrocomputador) throws BaseException {
		
		SceTipoMovimento tpMovimentoOriginal = this.getSceTipoMovimentosDAO().obterSceTipoMovimentosSeqComplemento(sceMovimentoMaterial.getTipoMovimento());

		this.validaTipoMovimentoCadastrado(sceMovimentoMaterial.getTipoMovimento());//RN1
		this.atualizaIndUsoCm(sceMovimentoMaterial, tpMovimentoOriginal);//RN2
		this.atualizaIndEstorno(sceMovimentoMaterial, tpMovimentoOriginal); //RN3
		this.atualizaFornecedorMovimentoTransferencia(sceMovimentoMaterial);

		/*
		 * RN4,RN5,RN6,RN7,RN8 e RN9
		 */
		SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmPorMaterialAlmoxFornecedor(sceMovimentoMaterial);

		if (sceEstoqueAlmoxarifado != null) {

			this.atualizarProceduresRn4Rn5(sceMovimentoMaterial, sceEstoqueAlmoxarifado, nomeMicrocomputador);

		}else{
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.ERRO_ALMOXARIFADO_NAO_ASSOC, sceMovimentoMaterial.getAlmoxarifado().getDescricao() );
		}

	}

	/**
	 * ORADB PROCEDURES SCEP_MMT_ATU_BLPROBL e SCEP_MMT_ATU_EAL
	 * @param sceMovimentoMaterial
	 * @param sceEstoqueAlmoxarifado
	 * @throws BaseException
	 * @throws ApplicationBusinessException
	
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private void atualizarProceduresRn4Rn5(SceMovimentoMaterial sceMovimentoMaterial, SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, String nomeMicrocomputador) throws BaseException {

		AghParametros aghParametroDF = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_DF);//(Devolução ao Fornecedor)
		Integer newEalSeq = sceEstoqueAlmoxarifado.getSeq();
		SceTipoMovimento tipoMovimento = sceMovimentoMaterial.getTipoMovimento();

		/*--------------------Variáveis locais para consultas-------------------------*/
		DominioIndOperacaoBasica indQtdDisponivelTipoMovimento = tipoMovimento.getIndQtdeDisponivel();
		Boolean indUsaQtdDisponivelTipoMovimento = tipoMovimento.getIndUsaQtdeDisponivel();
		DominioIndOperacaoBasica indQtdBloqueadaTipoMovimento = tipoMovimento.getIndQtdeBloqueada();
		DominioIndOperacaoBasica indOperacaoBasicaTipoMovimento = tipoMovimento.getIndOperacaoBasica();
		DominioIndOperacaoBasica indQtdBloqProblema = tipoMovimento.getIndQtdeBloqProblema();
		/*----------------------------------------------------------------------------*/

		AghParametros aghParamFrnHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);

		Integer vQtdeBloqProblema = 0;
		Integer vQtdeEstqAlmx = 0;
		Boolean vIndBloqEntrTransf = Boolean.FALSE;
		Boolean vTmvEntrTransf = Boolean.FALSE;
		Integer vQtdeDisp = 0;
		Integer vQtdeBloqEntrTransf = 0;
		Integer vQtdeBloq = 0;
		Double valorTotalNovo = new Double(0); 
		Integer qtdeTotalNova = 0;
		
		MovimentoMaterialVO movimentoMaterialVO = new MovimentoMaterialVO();
		movimentoMaterialVO.setResiduoAnt(new Double(0));
		movimentoMaterialVO.setValorTotalAnt(new Double(0));
		movimentoMaterialVO.setQtdeTotalAnt(0);
		

		if (DominioIndOperacaoBasica.S.equals(indQtdBloqProblema) && !tipoMovimento.getId().getSeq().equals(aghParametroDF)) {
			/*
			 * ORADB SCEP_MMT_ATU_BLPROBL
			 */
			this.atualizaSceHistProblemaMateriais(sceMovimentoMaterial, newEalSeq, tipoMovimento); //RN4

		} else {  //RN4

			/*
			 * ORADB SCEP_MMT_ATU_EAL
			 */

			/* Busca a  Qtde em Validade do material, almox e fornecedor */
			/* Busca indicador que informa se material entra como disponível */
			ScoMaterial material = this.getComprasFacade().obterMaterialPorCodigoSituacao(sceMovimentoMaterial.getMaterial());

			/* Busca qtde disp, bloqueada e em uso do material no almox */
			if (sceEstoqueAlmoxarifado.getIndSituacao().equals(DominioSituacao.I)) {

				throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00430, sceEstoqueAlmoxarifado.getMaterial().getCodigo());

			}

			/* Busca qtde bloqueada por problema */
			SceHistoricoProblemaMaterial histProblemaMaterial =  this.getSceHistoricoProblemaMaterialDAO().pesquisarQtdeBloqueadaPorProblema(newEalSeq);

			if (histProblemaMaterial == null) {

				vQtdeBloqProblema = 0;

			}else{
				
				Integer qtdeProblema = histProblemaMaterial.getQtdeProblema()!=null?histProblemaMaterial.getQtdeProblema():0;
				Integer qtdeDf = histProblemaMaterial.getQtdeDf()!=null?histProblemaMaterial.getQtdeDf():0;
				Integer qtdeDesbloqueada = histProblemaMaterial.getQtdeDesbloqueada()!=null?histProblemaMaterial.getQtdeDesbloqueada():0;
				vQtdeBloqProblema  = qtdeProblema + qtdeDf + qtdeDesbloqueada;
			}

			Integer qtdeBloqEntrTrans = sceEstoqueAlmoxarifado.getQtdeBloqEntrTransf()!=null?sceEstoqueAlmoxarifado.getQtdeBloqEntrTransf():0;
			Integer qtdEmUso = sceEstoqueAlmoxarifado.getQtdeEmUso()!=null?sceEstoqueAlmoxarifado.getQtdeEmUso():0;
			
			vQtdeEstqAlmx = sceEstoqueAlmoxarifado.getQtdeDisponivel() + sceEstoqueAlmoxarifado.getQtdeBloqueada() + qtdeBloqEntrTrans + qtdEmUso;
			
			movimentoMaterialVO.setQtdeTotalAnt(vQtdeEstqAlmx + vQtdeBloqProblema);

			if (Boolean.TRUE.equals(indUsaQtdDisponivelTipoMovimento)) {

				if (material.getIndAtuQtdeDisponivel().equals(DominioSimNao.S)) { //Impossível refatorar o pojo para alterar esse DominioSimNao

					indQtdDisponivelTipoMovimento = DominioIndOperacaoBasica.S;
					indQtdBloqueadaTipoMovimento = DominioIndOperacaoBasica.N;

					//tipoMovimento.setIndQtdeDisponivel(DominioIndOperacaoBasica.S);					
					//tipoMovimento.setIndQtdeBloqueada(DominioIndOperacaoBasica.N);
				
				}

			}

			AghParametros aghParametroTmvDocPi = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_PI);
			
			if (tipoMovimento.getId().getSeq() == aghParametroTmvDocPi.getVlrNumerico().shortValue()) {
				
				indQtdDisponivelTipoMovimento = DominioIndOperacaoBasica.S;
				indQtdBloqueadaTipoMovimento = DominioIndOperacaoBasica.N;
//				tipoMovimento.setIndQtdeDisponivel(DominioIndOperacaoBasica.S);
//				tipoMovimento.setIndQtdeBloqueada(DominioIndOperacaoBasica.N);
				
			}
			
			
			AghParametros aghParametroDVMV = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_DVMV);

			/*
			 *  O Estoque deverá sair do Saldo Disponível porque a DA gerada na rotina RN_ESLP_DEVOL_EMPS  também atualiza o Saldo Disponível.
			 */
			if (tipoMovimento.getId().getSeq() == aghParametroDVMV.getVlrNumerico().shortValue()
					&& sceMovimentoMaterial.getFornecedor() != null && sceMovimentoMaterial.getFornecedor().getNumero() != aghParamFrnHcpa.getVlrNumerico().intValue()) { //Devolução EMPS

				indQtdDisponivelTipoMovimento = DominioIndOperacaoBasica.S;
				indQtdBloqueadaTipoMovimento = DominioIndOperacaoBasica.N;
//				tipoMovimento.setIndQtdeDisponivel(DominioIndOperacaoBasica.S);
//				tipoMovimento.setIndQtdeBloqueada(DominioIndOperacaoBasica.N);

			}


			SceAlmoxarifado sceAlmoxarifados =  this.getSceAlmoxarifadoDAO().obterAlmoxarifadoPorSeq(sceMovimentoMaterial.getAlmoxarifado().getSeq());

			if (sceAlmoxarifados == null) {

				vIndBloqEntrTransf = Boolean.FALSE;

			} else {

				vIndBloqEntrTransf = sceAlmoxarifados.getIndBloqEntrTransf();

			}

			AghParametros aghParametroDocTrCompl = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_TR_COMPL);

			if (tipoMovimento.getId().getSeq() == aghParametroDocTrCompl.getVlrNumerico().shortValue()) {

				vTmvEntrTransf = Boolean.TRUE;

			} else {

				vTmvEntrTransf = Boolean.FALSE;

			}

			
			
			
			
			/*
			 *  Prepara qtde disponível 
			 */
			vQtdeDisp = sceEstoqueAlmoxarifado.getQtdeDisponivel();
			vQtdeBloqEntrTransf = sceEstoqueAlmoxarifado.getQtdeBloqEntrTransf();

			if (DominioIndOperacaoBasica.S.equals(indQtdDisponivelTipoMovimento)) {

				if ((DominioIndOperacaoBasica.CR.equals(indOperacaoBasicaTipoMovimento) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
						|| (DominioIndOperacaoBasica.DB.equals(indOperacaoBasicaTipoMovimento) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))) {

					if (vTmvEntrTransf.equals(Boolean.TRUE) && vIndBloqEntrTransf.equals(Boolean.TRUE)) {

						vQtdeBloqEntrTransf = sceEstoqueAlmoxarifado.getQtdeBloqEntrTransf() + sceMovimentoMaterial.getQuantidade();

					} else {

						vQtdeDisp = sceEstoqueAlmoxarifado.getQtdeDisponivel() + sceMovimentoMaterial.getQuantidade();

					}

				} else if ((DominioIndOperacaoBasica.DB.equals(indOperacaoBasicaTipoMovimento) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
						|| 			(DominioIndOperacaoBasica.CR.equals(indOperacaoBasicaTipoMovimento) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))) {

					if (sceEstoqueAlmoxarifado.getQtdeDisponivel() >= sceMovimentoMaterial.getQuantidade()) {

						vQtdeDisp =  sceEstoqueAlmoxarifado.getQtdeDisponivel() - sceMovimentoMaterial.getQuantidade();

					} else {

						String erroMatCodigo = sceMovimentoMaterial.getMaterial()!=null? sceMovimentoMaterial.getMaterial().getCodigo().toString():"0";
						String erroQtdeD = sceEstoqueAlmoxarifado.getQtdeDisponivel()!=null?sceEstoqueAlmoxarifado.getQtdeDisponivel().toString():"0";
						String erroQtdeBloq = sceEstoqueAlmoxarifado.getQtdeBloqueada()!=null?sceEstoqueAlmoxarifado.getQtdeBloqueada().toString():"0";

						if (sceMovimentoMaterial.getFornecedor() != null && sceMovimentoMaterial.getFornecedor().getNumero() != aghParamFrnHcpa.getVlrNumerico().intValue()) { 

							throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_SALDO_INSUFICIENTE_FRN, erroMatCodigo,erroQtdeD,erroQtdeBloq );

						} else {

							throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_SALDO_INSUFICIENTE_DESBLOQUEIO, erroMatCodigo,erroQtdeD,erroQtdeBloq );

						}

					}

				}

			}

			/*
			 *  Prepara qtde bloqueada
			 */
			vQtdeBloq = atualizaQtdeBloqueada(sceMovimentoMaterial, sceEstoqueAlmoxarifado, indOperacaoBasicaTipoMovimento, indQtdBloqueadaTipoMovimento);
			
		
			/* Atualiza qtde disponivel e bloqueada na tabela SCE_ESTQ_ALMOXS */
			sceEstoqueAlmoxarifado.setQtdeDisponivel(vQtdeDisp);
			sceEstoqueAlmoxarifado.setQtdeBloqueada(vQtdeBloq);
			sceEstoqueAlmoxarifado.setQtdeBloqEntrTransf(vQtdeBloqEntrTransf);

			this.getSceEstoqueAlmoxarifadoRN().atualizar(sceEstoqueAlmoxarifado, nomeMicrocomputador, true);

			/*
			 * Zera a Qtde Disponível em validade do material se o seu saldo disponível em estoque ficar zerado
			 */
			this.atualizaQtdeDisponivelSceValidade(sceEstoqueAlmoxarifado,vQtdeDisp, vQtdeBloqEntrTransf);

		}

		/**
		 * RN6
		 */
		if (tipoMovimento.getIndGuardaQtdePosMvto().equals(Boolean.TRUE)) {

			this.atualizaQtdePosMovimento(sceMovimentoMaterial, movimentoMaterialVO.getQtdeTotalAnt(), indOperacaoBasicaTipoMovimento);

		}

		/**
		 * RN7
		 */
		if (tipoMovimento.getIndAltValrEstqAlmoxMvto().equals(Boolean.TRUE)) {

			this.atualizaCalculoMvtoCustoMedioPonderado(sceMovimentoMaterial, movimentoMaterialVO, valorTotalNovo, qtdeTotalNova, tipoMovimento, indOperacaoBasicaTipoMovimento);

		}

		/**
		 * RN8 - Atualiza tabela SCE_ESTQ_GERAIS para DB ou CR 
		 */
		if (tipoMovimento.getIndAltValrEstqGeral().equals(Boolean.TRUE)) {

			this.atualizaSceEstqGeralParaDBCR(sceMovimentoMaterial, movimentoMaterialVO, qtdeTotalNova, tipoMovimento, indOperacaoBasicaTipoMovimento);

			AghParametros aghParametroDocDA = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_DA); //3
			AghParametros aghParametroDocRM = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_RM); //5

			if ((tipoMovimento.getId().getSeq().equals(aghParametroDocDA.getVlrNumerico().shortValue()) 
					|| tipoMovimento.getId().getSeq().equals(aghParametroDocRM.getVlrNumerico().shortValue()))
					&& sceMovimentoMaterial.getFornecedor() != null && sceMovimentoMaterial.getFornecedor().getNumero().equals(aghParamFrnHcpa.getVlrNumerico().intValue())) {

				//SCEP_MMT_ATU_CONSTOT
				atualizaConsumtoTotalMateriais(sceMovimentoMaterial, aghParametroDocDA, aghParametroDocRM, tipoMovimento);

			}

		}

		/**
		 * RN9 - Atualiza a tabela SCE_ESTQ_ALMOX_MVTOS (SCEP_MMT_ATU_EAM)
		 */
		this.atualizaEstqAlmMov(sceMovimentoMaterial, sceEstoqueAlmoxarifado, tipoMovimento);

	}


	/** 
	 * ORADB PROCEDURE SCEP_MMT_ATU_EAM
	 * Atualiza Qtde acumulada do movimento e respectivo valor(se Qtde também) (se indicado pelo tipo de movimento) 
	 * @param sceMovimentoMaterial
	 * @param sceEstoqueAlmoxarifado
	 * @throws BaseException
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void atualizaEstqAlmMov(SceMovimentoMaterial sceMovimentoMaterial,
			SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado,
			SceTipoMovimento tipoMovimento) throws BaseException {

		Integer ealSeq = sceEstoqueAlmoxarifado.getSeq();
		Short tmvSeq = sceMovimentoMaterial.getTipoMovimento().getId().getSeq();
		Byte tmvComplemento = sceMovimentoMaterial.getTipoMovimento().getId().getComplemento();

		SceEstqAlmoxMvto sceEstqAlmoxMvto = this.getSceEstqAlmoxMvtoDAO().obterSceEstqAlmoxMvto(ealSeq, tmvSeq, tmvComplemento); 

		if(tipoMovimento.getIndAltQtdeEstqAlmoxMvto().equals(Boolean.TRUE) && sceMovimentoMaterial.getQuantidade() >0 ){

			if (sceEstqAlmoxMvto == null) {

				sceEstqAlmoxMvto = new SceEstqAlmoxMvto();

				if (sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE) || sceMovimentoMaterial.getTipoMovimento().getIndDependeCaract().equals(Boolean.TRUE)){

					SceEstqAlmoxMvtoId estqAlmoxMvtoId = new SceEstqAlmoxMvtoId();

					estqAlmoxMvtoId.setEalSeq(ealSeq);
					estqAlmoxMvtoId.setTmvSeq(tmvSeq);
					estqAlmoxMvtoId.setTmvComplemento(tmvComplemento);
					sceEstqAlmoxMvto.setId(estqAlmoxMvtoId);
					sceEstqAlmoxMvto.setSceTipoMovimentos(sceMovimentoMaterial.getTipoMovimento());
					sceEstqAlmoxMvto.setSceEstoqueAlmoxarifado(sceEstoqueAlmoxarifado);


					sceEstqAlmoxMvto.setQuantidade(sceMovimentoMaterial.getQuantidade());
					sceEstqAlmoxMvto.setValor(sceMovimentoMaterial.getValor());

					/**
					 * Insere na tabela SCE_ESTQ_ALMOX_MVTOS
					 */
					this.getSceEstqAlmoxMvtoRN().inserir(sceEstqAlmoxMvto);

				} else{

					throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00433);

				}


			} else {

				Integer qtde = 0;
				Double valor = new Double(0);
				sceEstqAlmoxMvto.setSceTipoMovimentos(sceMovimentoMaterial.getTipoMovimento());
				sceEstqAlmoxMvto.setSceEstoqueAlmoxarifado(sceEstoqueAlmoxarifado);

				if (sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE)) {
					
					qtde =  sceEstqAlmoxMvto.getQuantidade() + sceMovimentoMaterial.getQuantidade();
					valor = sceEstqAlmoxMvto.getValor().doubleValue();

					if (tipoMovimento.getIndAltValrEstqAlmoxMvto().equals(Boolean.TRUE)) {

						valor = valor + sceMovimentoMaterial.getValor().doubleValue();
						
					}

					sceEstqAlmoxMvto.setQuantidade(qtde);
					sceEstqAlmoxMvto.setValor(new BigDecimal(valor));

					/**
					 * Atualiza tabela SCE_ESTQ_ALMOX_MVTOS
					 */
					this.getSceEstqAlmoxMvtoRN().atualizar(sceEstqAlmoxMvto);

				} else {

					qtde =  sceEstqAlmoxMvto.getQuantidade() - sceMovimentoMaterial.getQuantidade();
					valor = sceEstqAlmoxMvto.getValor().doubleValue();

					if (qtde <= 0) {

						/**
						 * Delete na tabela SCE_ESTQ_ALMOX_MVTOS
						 */
						this.getSceEstqAlmoxMvtoDAO().remover(sceEstqAlmoxMvto);
						this.getSceEstqAlmoxMvtoDAO().flush();

					} else if (tipoMovimento.getIndAltValrEstqAlmoxMvto().equals(Boolean.TRUE)) {

						valor = valor - sceMovimentoMaterial.getValor().doubleValue();

						sceEstqAlmoxMvto.setValor(new BigDecimal(valor));
						sceEstqAlmoxMvto.setQuantidade(qtde);

						/**
						 * Atualiza tabela SCE_ESTQ_ALMOX_MVTOS
						 */
						this.getSceEstqAlmoxMvtoRN().atualizar(sceEstqAlmoxMvto);
					}

				}

			}

		}

	}


	/**
	 * ORADB PROCEDURE SCEP_MMT_ATU_EGR
	 * @param sceMovimentoMaterial
	 * @param qtdeTotalAnt
	 * @param residuoAnt
	 * @param valorTotalAnt
	 * @param qtdeTotalNova
	 * @throws BaseException
	 */
	private void atualizaSceEstqGeralParaDBCR(
			SceMovimentoMaterial sceMovimentoMaterial,
			MovimentoMaterialVO movimentoMaterialVO, Integer qtdeTotalNova,
			SceTipoMovimento tipoMovimento,
			DominioIndOperacaoBasica indOperacaoBasica) throws BaseException {

		Double valorTotalNovo;
		valorTotalNovo =  movimentoMaterialVO.getValorTotalAnt();

		if ((DominioIndOperacaoBasica.CR.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
				|| 			(DominioIndOperacaoBasica.DB.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))) {


			valorTotalNovo = movimentoMaterialVO.getValorTotalAnt() + sceMovimentoMaterial.getValor().doubleValue();
			qtdeTotalNova = movimentoMaterialVO.getQtdeTotalAnt() + sceMovimentoMaterial.getQuantidade();

		} else if ((DominioIndOperacaoBasica.DB.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
				|| 			(DominioIndOperacaoBasica.CR.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))) {


			valorTotalNovo = movimentoMaterialVO.getValorTotalAnt() - sceMovimentoMaterial.getValor().doubleValue();
			qtdeTotalNova = movimentoMaterialVO.getQtdeTotalAnt() - sceMovimentoMaterial.getQuantidade();

		}

		/* Leva no Movimento o Valor saldo ( - ou +) e zera Valor se Qtde ficar =  0 */
		if (qtdeTotalNova < 0) {
			
			qtdeTotalNova = 0;

		}

		if (qtdeTotalNova == 0) {
			
			if (valorTotalNovo != 0) {

				AghParametros aghParametroDocDF = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_DF);

				/*
				 * DF - Devolução Fornecedor - Não deve permitir Valor Movto diferente do Valor do Documento.
				 */
				if (tipoMovimento.getId().getSeq() == aghParametroDocDF.getVlrNumerico().shortValue()) {

					String erroMatCodigo = sceMovimentoMaterial.getMaterial()!=null ? sceMovimentoMaterial.getMaterial().getCodigo().toString():"0";
					BigDecimal erroValor = sceMovimentoMaterial.getValor();

					if (valorTotalNovo < 0) {

						throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_SALDO_FINANC_INSUFICIENTE, erroMatCodigo,erroValor,movimentoMaterialVO.getValorTotalAnt());

					} else {

						throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_SALDO_FINANC_SUPERIOR_DF, erroMatCodigo,erroValor,movimentoMaterialVO.getValorTotalAnt());

					}
					
				}

				sceMovimentoMaterial.setValor(new BigDecimal(sceMovimentoMaterial.getValor().doubleValue() + valorTotalNovo));
				valorTotalNovo = new Double(0);
				
			}

		}

		/* Busca dt_competencia do Sistema (Parâmetro) */
		AghParametros aghParamCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);

		SceEstoqueGeral  estoqueGeral = this.getSceEstoqueGeralDAO().pesquisarEstoqueGeralPorMatDtCompFornecedor(sceMovimentoMaterial.getMaterial().getCodigo(), aghParamCompetencia.getVlrData(), sceMovimentoMaterial.getFornecedor().getNumero());
		estoqueGeral.setValor(valorTotalNovo);
		estoqueGeral.setQtde(qtdeTotalNova);
		
		BigDecimal custoMedio = new BigDecimal(sceMovimentoMaterial.getCustoMedioPonderadoGer().doubleValue());
		//custoMedio =  NumberUtil.truncate_FLOOR(custoMedio, 4);
		estoqueGeral.setCustoMedioPonderado(custoMedio);
		estoqueGeral.setResiduo(movimentoMaterialVO.getResiduoAnt());

		/* Atualiza tabela SCE_ESTQ_GERAIS somente para materiais Consignados (CONSE e CONSE) */
		AghParametros aghParametroDocConse = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_CONSE);
		AghParametros aghParametroDocConss = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_CONSS);

		if (tipoMovimento.getId().getSeq() == aghParametroDocConse.getVlrNumerico().shortValue()) {

			estoqueGeral.setValorConsignado(estoqueGeral.getValorConsignado() + sceMovimentoMaterial.getValor().doubleValue());
			estoqueGeral.setQtdeConsignada(estoqueGeral.getQtdeConsignada()+ sceMovimentoMaterial.getQuantidade());

		} else if (tipoMovimento.getId().getSeq() == aghParametroDocConss.getVlrNumerico().shortValue()) {

			estoqueGeral.setValorConsignado(estoqueGeral.getValorConsignado() - sceMovimentoMaterial.getValor().doubleValue());
			estoqueGeral.setQtdeConsignada(estoqueGeral.getQtdeConsignada() - sceMovimentoMaterial.getQuantidade());


		}

		/* Atualiza tabela SCE_ESTQ_GERAIS */
		this.getSceEstoqueGeralRN().atualizar(estoqueGeral);

	}


	/**
	 * ORADB PROCEDURE SCEP_MMT_CALC_VAL
	 * @param sceMovimentoMaterial
	 * @param qtdeTotalAnt
	 * @throws BaseException
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private MovimentoMaterialVO atualizaCalculoMvtoCustoMedioPonderado(
			SceMovimentoMaterial sceMovimentoMaterial,
			MovimentoMaterialVO movimentoMaterialVO, Double valorTotalNovo,
			Integer qtdeTotalNova, SceTipoMovimento tipoMovimento,
			DominioIndOperacaoBasica indOperacaoBasica) throws ApplicationBusinessException {

		/* Busca dt_competencia do Sistema (Parâmetro) */
		AghParametros aghParamCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		
		Integer frnNumero = sceMovimentoMaterial.getFornecedor().getNumero();
		BigDecimal custoMedioForn = BigDecimal.ZERO;

		BigDecimal custoMedioAnt = BigDecimal.ZERO;

		AghParametros aghParamDocTr = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_TR);

		if (aghParamDocTr != null) {

			if (tipoMovimento.getId().getSeq() == aghParamDocTr.getVlrNumerico().intValue()) {

				SceEstoqueGeral estoqueGeralConsignado = this.getSceEstoqueGeralDAO().pesquisarNumTransferencia(sceMovimentoMaterial.getNroDocGeracao(), aghParamCompetencia.getVlrData());
				
				if (estoqueGeralConsignado == null) {
					
					throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_NRO_TRANSF_NAO_ENCONTRADO);
				
				}

				AghParametros aghParamFrnHcpa = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);

				if(sceMovimentoMaterial.getFornecedor().getNumero().intValue() == aghParamFrnHcpa.getVlrNumerico().intValue()
						&& !estoqueGeralConsignado.getFornecedor().getNumero().equals(sceMovimentoMaterial.getFornecedor().getNumero())) {
					
					//Para buscar custos do fornecedor (Material Consignado deve sair ao custo do fornecedor)
					frnNumero = estoqueGeralConsignado.getFornecedor() !=null?estoqueGeralConsignado.getFornecedor().getNumero():frnNumero;
					custoMedioForn = estoqueGeralConsignado.getCustoMedioPonderado()!=null? estoqueGeralConsignado.getCustoMedioPonderado():custoMedioForn;

				}

			}

		}

		/* Busca Valor e Quantidade  em estoque  */
		SceEstoqueGeral estoqueValorQtde = this.getSceEstoqueGeralDAO().pesquisarEstoqueGeralPorMatDtCompFornecedor(sceMovimentoMaterial.getMaterial().getCodigo(), aghParamCompetencia.getVlrData(), frnNumero);

		if (estoqueValorQtde == null) {
			//Material não encontrado na competência atual
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00431);
		}

		/* Guarda valor estoque para retorno à BRI */
		movimentoMaterialVO.setValorTotalAnt(estoqueValorQtde.getValor());
		movimentoMaterialVO.setQtdeTotalAnt(estoqueValorQtde.getQtde());

		if (custoMedioForn != null && custoMedioForn.doubleValue() > 0) {  //material Consignado

			custoMedioAnt = custoMedioForn;

		} else {

			custoMedioAnt = estoqueValorQtde.getCustoMedioPonderado()!=null?estoqueValorQtde.getCustoMedioPonderado():custoMedioAnt;

		}

		movimentoMaterialVO.setResiduoAnt(estoqueValorQtde.getResiduo()!= null?estoqueValorQtde.getResiduo():movimentoMaterialVO.getResiduoAnt());

		/* Calcula valor do movimentosa utilizando Custo Médio Ponderado */
		if (DominioIndUsoCm.U.equals(tipoMovimento.getIndUsoCm())) {

			if (Boolean.TRUE.equals(tipoMovimento.getIndUsoResiduo())) {
				
				double resultado = sceMovimentoMaterial.getQuantidade() * custoMedioAnt.doubleValue();
				BigDecimal valor = new BigDecimal(resultado).setScale(2, BigDecimal.ROUND_HALF_EVEN);
				sceMovimentoMaterial.setValor(valor); 
				
				sceMovimentoMaterial.setResiduo(new BigDecimal(movimentoMaterialVO.getResiduoAnt()));

			} else {

				if (custoMedioAnt == null || !BigDecimal.ZERO.equals(custoMedioAnt)) {
					
					double resultado = sceMovimentoMaterial.getQuantidade() * custoMedioAnt.doubleValue(); 
					BigDecimal valor = new BigDecimal(resultado).setScale(2, BigDecimal.ROUND_HALF_EVEN);

					sceMovimentoMaterial.setValor(valor); 

				} else {
					
					if(sceMovimentoMaterial.getValor() == null || sceMovimentoMaterial.getQuantidade() == null){
						throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.ERRO_CM_MOVIMENTO, sceMovimentoMaterial.getMaterial().getCodigo());
					}
				
					double resultado = sceMovimentoMaterial.getValor().doubleValue() / sceMovimentoMaterial.getQuantidade();
					BigDecimal valor = new BigDecimal(resultado).setScale(2, BigDecimal.ROUND_HALF_EVEN);

					sceMovimentoMaterial.setValor(valor);

				}

			}


			sceMovimentoMaterial.setCustoMedioPonderadoGer(custoMedioAnt);

		} else {

			sceMovimentoMaterial.setCustoMedioPonderadoGer(custoMedioAnt);
			sceMovimentoMaterial.setResiduo(new BigDecimal(movimentoMaterialVO.getResiduoAnt()));
			
			movimentoMaterialVO.setCustoMedioPonderado(sceMovimentoMaterial.getCustoMedioPonderadoGer());
			movimentoMaterialVO.setResiduo(sceMovimentoMaterial.getResiduo());

		}

		movimentoMaterialVO.setValor(sceMovimentoMaterial.getValor());
		
		/* Calcula Custo Médio Ponderado a partir do valor do movimento */
		if (tipoMovimento.getIndUsoCm().equals(DominioIndUsoCm.C)){
			//SCEP_MMT_CALC_CM
			
			this.atualizaCustoMedioPonderado(sceMovimentoMaterial, movimentoMaterialVO, valorTotalNovo,qtdeTotalNova, indOperacaoBasica);

		}
		
		
		return movimentoMaterialVO;
	
	}

	/**
	 * ORADB PROCEDURE SCEP_MMT_CALC_CM
	 * @param sceMovimentoMaterial
	 * @param valorTotalAnt
	 * @throws ApplicationBusinessException
	 */
	private void atualizaCustoMedioPonderado(SceMovimentoMaterial sceMovimentoMaterial,MovimentoMaterialVO movimentoMaterialVO, Double valorTotalNovo,Integer qtdeTotalNova, DominioIndOperacaoBasica indOperacaoBasica) throws ApplicationBusinessException {

		Integer qtdeBloqProblema = 0;
		Integer qtdeTotalEstqAlmx = 0; 

		SceEstoqueAlmoxarifado estAlmoxarifado = this.getSceEstoqueAlmoxarifadoDAO().pesquisarSaldosEstoque(sceMovimentoMaterial);

		if (estAlmoxarifado == null) {

			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00432);
		}

		/* Leitura qtde bloq por problema  */

		SceHistoricoProblemaMaterial sceHistProblemaMaterial =  this.getSceHistoricoProblemaMaterialDAO().pesquisarQtdeBloqueadaPorMaterialFornecedor(sceMovimentoMaterial.getMaterial().getCodigo(), sceMovimentoMaterial.getFornecedor().getNumero());

		if (sceHistProblemaMaterial != null) {

			qtdeBloqProblema = sceHistProblemaMaterial.getQtdeProblema() - sceHistProblemaMaterial.getQtdeDf() - sceHistProblemaMaterial.getQtdeDesbloqueada();

		}

		qtdeTotalEstqAlmx = estAlmoxarifado.getQtdeDisponivel() + estAlmoxarifado.getQtdeBloqueada() + estAlmoxarifado.getQtdeEmUso();
		qtdeTotalNova = qtdeTotalEstqAlmx + qtdeBloqProblema;

		/* Calcula Custo mèdio a partir do valor e quantidade de material em estoque. */

		if ((DominioIndOperacaoBasica.CR.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
				|| 			(DominioIndOperacaoBasica.DB.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))) {

			valorTotalNovo = movimentoMaterialVO.getValorTotalAnt() + sceMovimentoMaterial.getValor().doubleValue();

		} else {
			
			valorTotalNovo = movimentoMaterialVO.getValorTotalAnt() - sceMovimentoMaterial.getValor().doubleValue();

		}

		if (valorTotalNovo > new Double(0) && qtdeTotalNova > 0) {
			
			Double custoMedioCalculado = this.calcularCustoMedioPonderado(valorTotalNovo, qtdeTotalNova);

			BigDecimal custoMedio = NumberUtil.truncateFLOOR(new BigDecimal(custoMedioCalculado), 4);
			custoMedio = NumberUtil.truncate(custoMedio, 4);
			
		    sceMovimentoMaterial.setCustoMedioPonderadoGer(custoMedio);
		    
			Double valorMultiplicacao = NumberUtil.truncateHALFEVEN(qtdeTotalNova * sceMovimentoMaterial.getCustoMedioPonderadoGer().doubleValue(), 2);
			BigDecimal valorResiduo = new BigDecimal(valorTotalNovo - valorMultiplicacao);
			valorResiduo = new BigDecimal(NumberUtil.truncateHALFEVEN(valorResiduo.doubleValue(), 2));
			sceMovimentoMaterial.setResiduo(valorResiduo);
			
			movimentoMaterialVO.setResiduoAnt(sceMovimentoMaterial.getResiduo().doubleValue());
			movimentoMaterialVO.setResiduo(sceMovimentoMaterial.getResiduo());
			movimentoMaterialVO.setCustoMedioPonderado(sceMovimentoMaterial.getCustoMedioPonderadoGer());

		}

	}


	/**
	 * ORADB PROCEDURE SCEP_MMT_QT_POS_MVTO 
	 * @param sceMovimentoMaterial
	 * @param qtdeTotalAnt
	 */
	private void atualizaQtdePosMovimento(SceMovimentoMaterial sceMovimentoMaterial, Integer qtdeTotalAnt, DominioIndOperacaoBasica indOperacaoBasica) {

		if ((DominioIndOperacaoBasica.CR.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
				|| 			(DominioIndOperacaoBasica.DB.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))) {

			// p_new_qtde_pos_movimento := p_qtde_total_ant + p_new_quantidade;
			sceMovimentoMaterial.setQtdePosMovimento(Long.valueOf(qtdeTotalAnt + sceMovimentoMaterial.getQuantidade()));

		} else if ((DominioIndOperacaoBasica.DB.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
				|| 			(DominioIndOperacaoBasica.CR.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))) {


			sceMovimentoMaterial.setQtdePosMovimento(Long.valueOf(qtdeTotalAnt - sceMovimentoMaterial.getQuantidade()));

		}

	}

	private void atualizaQtdeDisponivelSceValidade(SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado, Integer qtdeDisp, Integer qtdeBloqEntrTransf) throws ApplicationBusinessException {
		
		qtdeDisp = (qtdeDisp == null ? 0 : qtdeDisp);
		qtdeBloqEntrTransf = (qtdeBloqEntrTransf == null ? 0 : qtdeBloqEntrTransf);
		
		if ((qtdeDisp + qtdeBloqEntrTransf) == 0) {

			List<SceValidade> listValidades = this.getSceValidadesDAO().pesquisarValidadeEalSeqDataValidadeComQuantidadeDisponivel(sceEstoqueAlmoxarifado.getSeq(),null); 
			
			for (SceValidade validade: listValidades ) {

				validade.setQtdeConsumida(validade.getQtdeEntrada());
				validade.setQtdeDisponivel(0);
				this.getSceValidadesRN().atualizar(validade);

			}

		}
		
	}

	private Integer atualizaQtdeBloqueada(
			SceMovimentoMaterial sceMovimentoMaterial,
			SceEstoqueAlmoxarifado sceEstoqueAlmoxarifado,
			DominioIndOperacaoBasica indOperacaoBasica,
			DominioIndOperacaoBasica indQtdBloqueada)
			throws ApplicationBusinessException {

		Integer qtdeBloq;
		qtdeBloq = sceEstoqueAlmoxarifado.getQtdeBloqueada();

		if (DominioIndOperacaoBasica.S.equals(indQtdBloqueada)) {

			if((DominioIndOperacaoBasica.CR.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
					|| 			(DominioIndOperacaoBasica.DB.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))){

				qtdeBloq = sceEstoqueAlmoxarifado.getQtdeBloqueada() + sceMovimentoMaterial.getQuantidade();


			} else if ((DominioIndOperacaoBasica.DB.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE))
					|| 			(DominioIndOperacaoBasica.CR.equals(indOperacaoBasica) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))){


				if (sceEstoqueAlmoxarifado.getQtdeBloqueada() >= sceMovimentoMaterial.getQuantidade()) {

					qtdeBloq = sceEstoqueAlmoxarifado.getQtdeBloqueada() - sceMovimentoMaterial.getQuantidade();

				} else {

					String erroMatCodigo = sceMovimentoMaterial.getMaterial()!=null? sceMovimentoMaterial.getMaterial().getCodigo().toString():"0";
					String erroQtdeD = sceEstoqueAlmoxarifado.getQtdeDisponivel()!=null?sceEstoqueAlmoxarifado.getQtdeDisponivel().toString():"0";
					String erroQtdeBloq = sceEstoqueAlmoxarifado.getQtdeBloqueada()!=null?sceEstoqueAlmoxarifado.getQtdeBloqueada().toString():"0";

					throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_SALDO_INSUFICIENTE_ALM, erroMatCodigo, sceMovimentoMaterial.getQuantidade(), erroQtdeD, erroQtdeBloq);

				}

			}
		}

		return qtdeBloq;

	}


	/**
	 * ORADB PROCEDURE SCEP_MMT_ATU_BLPROBL
	 */
	private void atualizaSceHistProblemaMateriais(SceMovimentoMaterial sceMovimentoMaterial, Integer newEalSeq, SceTipoMovimento tipoMovimento) throws BaseException {

		/* Atualiza tabela SCE_HISTORICO_BLOQUEIO_PROBLEMA */
		//realiza insert e ou update em sce_hist_problema_materiais conforme procedure.
		//Integer newEalSeq = null;
		Integer vNroDoc = null;

		if (sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE)) {

			SceEntradaSaidaSemLicitacao entrSaidaSemLicitacao = this.getSceEntradaSaidaSemLicitacaoDAO().obterEntradaSaidaSemLicitacaoPorSeqMatCodigo(sceMovimentoMaterial.getNroDocGeracao(), sceMovimentoMaterial.getMaterial().getCodigo());

			if (entrSaidaSemLicitacao == null) {

				AghParametros aghParametroRCSAF = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_RCSAF); 
				SceHistoricoProblemaMaterial histProbMaterial = this.getSceHistoricoProblemaMaterialDAO().obterDocumentoRCSAFPendente(sceMovimentoMaterial.getMaterial().getCodigo(),aghParametroRCSAF);  

				if (histProbMaterial == null) {

					throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_DOCUMENTO_RCSAF_NAO_ENCONTRADO);

				}


			} else {

				vNroDoc = entrSaidaSemLicitacao.getEslSeq();

			}

		}

		Short vMprSeq = 40; //    -- MATERIAL RECEBIDO SEM AF - NÃO ESPERADO

		if (tipoMovimento.getIndOperacaoBasica().equals(DominioIndOperacaoBasica.CR) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE)) {

			SceHistoricoProblemaMaterial sceHistoricoProblemaMaterial = new SceHistoricoProblemaMaterial();
			sceHistoricoProblemaMaterial.getSceEstqAlmox().setSeq(newEalSeq);
			sceHistoricoProblemaMaterial.setDtGeracao(new Date());
			sceHistoricoProblemaMaterial.getMotivoProblema().setSeq(vMprSeq);
			sceHistoricoProblemaMaterial.setFornecedor(sceMovimentoMaterial.getFornecedor());
			sceHistoricoProblemaMaterial.setQtdeProblema(sceMovimentoMaterial.getQuantidade());
			sceHistoricoProblemaMaterial.setIndEfetivado(Boolean.FALSE);
			sceHistoricoProblemaMaterial.getSceEntradaSaidaSemLicitacao().setSeq(sceMovimentoMaterial.getNroDocGeracao());

			this.getSceHistoricoProblemaMaterialRN().inserir(sceHistoricoProblemaMaterial, true);

		} else if (tipoMovimento.getIndOperacaoBasica().equals(DominioIndOperacaoBasica.CR) &&  sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE)) {

			SceHistoricoProblemaMaterial histProbMaterial = this.getSceHistoricoProblemaMaterialDAO().obterHistProbMaterialPorEstoqueEsl(vNroDoc, newEalSeq);				
			if (histProbMaterial != null) {
				Integer qtdeDf = histProbMaterial.getQtdeDf()+ sceMovimentoMaterial.getQuantidade();
				histProbMaterial.setQtdeDf(qtdeDf);
				this.getSceHistoricoProblemaMaterialRN().atualizar(histProbMaterial, false);
			}

		} else if (tipoMovimento.getIndOperacaoBasica().equals(DominioIndOperacaoBasica.DB) &&  sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE)) {

			/*    VERIFICAR
			 * NA PROCEDURE ESSES DOIS ELSEs ESTÃO IGUAIS. COM ESSA LINHA COMENTADA:
			 * -- set QTDE_DESBLOQUEADA = QTDE_DESBLOQUEADA + p_new_quantidade
			 */
			SceHistoricoProblemaMaterial histProbMaterial = this.getSceHistoricoProblemaMaterialDAO().obterHistProbMaterialPorEstoqueEsl(vNroDoc, newEalSeq);
			if (histProbMaterial != null) {
				Integer qtdeDf = histProbMaterial.getQtdeDf()+ sceMovimentoMaterial.getQuantidade();
				histProbMaterial.setQtdeDf(qtdeDf);
				this.getSceHistoricoProblemaMaterialRN().atualizar(histProbMaterial, false);
			}
		} else {

			SceHistoricoProblemaMaterial histProbMaterial = this.getSceHistoricoProblemaMaterialDAO().obterHistProbMaterialPorEstoqueEsl(vNroDoc, newEalSeq);
			if (histProbMaterial != null) {
				Integer qtdeDf = histProbMaterial.getQtdeDf() - sceMovimentoMaterial.getQuantidade();
				histProbMaterial.setQtdeDf(qtdeDf);
				this.getSceHistoricoProblemaMaterialRN().atualizar(histProbMaterial, false);
			}
		}

	}

	private void atualizaIndEstorno(SceMovimentoMaterial sceMovimentoMaterial, SceTipoMovimento tpMovimentoOriginal) throws ApplicationBusinessException {

		if (tpMovimentoOriginal.getIndDependeCaract().equals(Boolean.TRUE) && sceMovimentoMaterial.getTipoMovimentoDocumento() != null) {

			SceTipoMovimento tpMovimentoDocumentoOriginal = this.getSceTipoMovimentosDAO().obterSceTipoMovimentosSeqComplemento(sceMovimentoMaterial.getTipoMovimentoDocumento());

			if (tpMovimentoDocumentoOriginal == null) {
				
				throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_TIPO_MOVIMENTO_NAO_CADASTRADO);
			
			} else if (sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE)){
				/*
				 * VERIFICAR SE NÃO PRECISA SETAR EM OUTRA VÁRIAVEL CONFORME PROCEDURE,  v_new_ind_estorno
				 */
				sceMovimentoMaterial.setIndEstorno(Boolean.FALSE);
			
			} else {
				
				sceMovimentoMaterial.setIndEstorno(Boolean.TRUE);
			
			}

		}

		if (tpMovimentoOriginal.getIndFornecedorProp().equals(Boolean.FALSE)) {
			
			sceMovimentoMaterial.setFornecedor(null);
		
		}
	
	}

	private void atualizaIndUsoCm(SceMovimentoMaterial sceMovimentoMaterial, SceTipoMovimento tpMovimentoOriginal) throws ApplicationBusinessException {
		/*
		 * 	Busca Tipo Movimento de Produção Interna
		 */
		AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_PI);

		if (aghParametro != null && aghParametro.getVlrNumerico().equals(sceMovimentoMaterial.getTipoMovimento().getId().getSeq())) {
			
			if (sceMovimentoMaterial.getValor() == null) {
				
				tpMovimentoOriginal.setIndUsoCm(DominioIndUsoCm.U);
			
			} else {
				
				tpMovimentoOriginal.setIndUsoCm(DominioIndUsoCm.C);
			
			}
		
		}
	
	}

	private void validaTipoMovimentoCadastrado(SceTipoMovimento sceTipoMovimentos) throws ApplicationBusinessException {

		if (sceTipoMovimentos == null) {
			
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_TIPO_MOVIMENTO_NAO_CADASTRADO);
		
		}
	
	}

	private void atualizaCodigoCentroCusto(SceMovimentoMaterial sceMovimentoMaterial)throws ApplicationBusinessException{

		AghParametros aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_MOV_REAPR_CR);

		if(sceMovimentoMaterial != null 
				&& sceMovimentoMaterial.getTipoMovimento() != null 
				&& sceMovimentoMaterial.getTipoMovimento().getId() != null
				&& aghParametro.getVlrNumerico().shortValue() == sceMovimentoMaterial.getTipoMovimento().getId().getSeq() ){
			sceMovimentoMaterial.getCentroCusto().setCodigo(sceMovimentoMaterial.getCentroCustoRequisita().getCodigo());	
		}

	}

	/*
	 *  RN3: SCEK_MMT_RN.RN_MMTP_ATU_GERACAO: Seta ser_matricula e ser_vin_codigo com o usuário logado e dt_geracao com a data do momento.
	 */
	public void atualizaServidorDtGeracao(SceMovimentoMaterial sceMovimentoMaterial) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		sceMovimentoMaterial.setServidor(servidorLogado);
		sceMovimentoMaterial.setDtGeracao(new Date());

	}



	/**
	 * ORADB PROCEDURE SCEK_MMT_RN.RN_MMTP_CONSIST_MVTO
	 * Consulta Tipo de movimento, tabela sce_tipo_movimentos.
	 * @param sceMovimentoMaterial
	 * @throws BaseException 
	 */
	public void validaTipoMovimento(SceMovimentoMaterial sceMovimentoMaterial) throws BaseException {

		SceTipoMovimentoId tipoMovimentoId = null;

		if (sceMovimentoMaterial != null && sceMovimentoMaterial.getTipoMovimento() != null) {

			tipoMovimentoId = sceMovimentoMaterial.getTipoMovimento().getId();

		}

		getSceTipoMovimentosRN().verificarTipoMovimento(tipoMovimentoId, sceMovimentoMaterial);

	}

	/**
	 * ORADB PROCEDURES SCEK_MMT_RN.RN_MMTP_VER_CCTR_ATV e RN_MMTP_VER_CCTA_ATV
	 * Valida o Centro de custo
	 * @param sceMovimentoMaterial
	 * @throws BaseException 
	 */
	public void validaCentroCusto(SceMovimentoMaterial sceMovimentoMaterial) throws ApplicationBusinessException{

		//RN4 - RN_MMTP_VER_CCTR_ATV
		if (sceMovimentoMaterial.getCentroCustoRequisita() != null) {
		
			this.validaCentroCustoAtivo(sceMovimentoMaterial.getCentroCustoRequisita().getCodigo());
		
		}

		//RN5 - RN_MMTP_VER_CCTA_ATV
		if (sceMovimentoMaterial.getCentroCusto() != null) {
			
			this.validaCentroCustoAtivo(sceMovimentoMaterial.getCentroCusto().getCodigo());
		
		}

	}

	/**
	 * ORADB PROCEDURE RN_SCEP_VER_CCT_ATIV
	 * Valida o Centro de custo se está ativo
	 * @param sceMovimentoMaterial
	 * @throws BaseException 
	 */
	public void validaCentroCustoAtivo(Integer cCodigo) throws ApplicationBusinessException{
		
		FccCentroCustos centroCusto = getCentroCustoFacade().pesquisarCentroCustoAtivoPorCodigo(cCodigo);

		if (centroCusto == null) {
			
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00295);
		
		}

	}

	/**
	 * ORADB PROCEDURE SCEK_SCE_RN.RN_SCEP_ATU_DT_COMP
	 * Consulta Tipo de movimento, tabela sce_tipo_movimentos.
	 * @param sceMovimentoMaterial
	 * @throws BaseException 
	 */
	public void validaFechamentoMensal(SceMovimentoMaterial sceMovimentoMaterial) throws ApplicationBusinessException {

		AghParametros fechamentoMensal;
		AghParametros competencia;

		fechamentoMensal = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FECHAMENTO_MENSAL);
		competencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);

		if (fechamentoMensal != null && competencia != null && fechamentoMensal.getVlrTexto().equals("N")) {

			sceMovimentoMaterial.setDtCompetencia(DateUtil.truncaData(competencia.getVlrData()));

		}
		
		if(fechamentoMensal.getVlrTexto().equals("S")){
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MENSAGEM_ROTINA_FECHAMENTO_EM_EXECUCAO);
		}

	}


	/**
	 * ORADB PROCEDURE SCEK_SCE_RN.RN_SCEP_ATU_DT_COMP
	 * Consulta Tipo de movimento, tabela sce_tipo_movimentos.
	 * @param sceMovimentoMaterial
	 * @throws BaseException 
	 */
	@SuppressWarnings("ucd")
	public void validarAtualizarDataCompetencia(Date dataCompetencia) throws BaseException{
		AghParametros parametroCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
		Date dataParametroCompetencia = parametroCompetencia.getVlrData();
		if(!dataParametroCompetencia.equals(dataCompetencia)){
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00294);
		}
	}


	/**
	 * Pesquisa o consumo médio trimestral de materiais de acordo com sua movimentação
	 * ORADB Function SCEC_CONS_MED_TRIM
	 * @param integer 
	 * @param mesCompetencia 
	 * @return Consumo médio trimestral
	 */
	@SuppressWarnings("ucd")
	public Double obterConsumoMediaTrimestralMovimentoMaterial(Date mesCompetencia, Integer codigoMaterial){
		return getSceMovimentoMaterialDAO().obterConsumoMediaTrimestralMovimentoMaterial(mesCompetencia, codigoMaterial);
	}

	/**
	 * ORADB FUNCTION SCEC_BUSCA_DT_UL_COM
	 * @param matCodigo
	 * @param AlmSeq
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public Date obterDataUltimaCompra(Integer matCodigo, Short almSeq) throws ApplicationBusinessException {
		AghParametros aghParametro;
		try {

			aghParametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_NR);
		} catch (BaseException e) {

			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00863);
		}

		List<SceMovimentoMaterial> movimentos = getSceMovimentoMaterialDAO().obterDataUltimaCompra(matCodigo, almSeq, aghParametro.getVlrNumerico().shortValue());

		if (!movimentos.isEmpty()) {

			return movimentos.get(0).getDtGeracao();

		}

		return null;

	}

	/**
	 * ORADB FUNCTION SCEC_BUSCA_DT_UL_CFF
	 * @param matCodigo
	 * @param AlmSeq
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public Date obterDataUltimaCff(Integer matCodigo, Short almSeq) throws ApplicationBusinessException {

		AghParametros aghParametro;
		try {
			aghParametro  = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_FF);
		} catch (BaseException e) {
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00863);
		}

		List<SceMovimentoMaterial> movimentos = getSceMovimentoMaterialDAO().obterDataUltimaCompra(matCodigo, almSeq, aghParametro.getVlrNumerico().shortValue());

		if (!movimentos.isEmpty()) {

			return movimentos.get(0).getDtGeracao();

		}

		return null;

	}
	
	/**
	 * 
	 * @param sceMovimentoMaterial
	 * @throws ApplicationBusinessException
	 */
	private void atualizaFornecedorMovimentoTransferencia(SceMovimentoMaterial sceMovimentoMaterial) throws ApplicationBusinessException {
		
		AghParametros aghParametroDocTrCompl = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_TR_COMPL);

		if (sceMovimentoMaterial.getTipoMovimento().getId().getSeq().equals(aghParametroDocTrCompl.getVlrNumerico().shortValue())) {
			
			SceItemTransferencia itemTransferencia = getSceItemTransferenciaDAO().obterItemTransferenciaPorEstoqueAlmoxarifado(sceMovimentoMaterial.getNroDocGeracao(), sceMovimentoMaterial.getMaterial().getCodigo(), sceMovimentoMaterial.getAlmoxarifado().getSeq());
			if(itemTransferencia!=null){
				sceMovimentoMaterial.setFornecedor(itemTransferencia.getEstoqueAlmoxarifado().getFornecedor());
			}
			
		}
		
	}
	
	
	/** Busca os valores para serem impressos nos relatórios de impressão de requisição de material
	 * ORADB CF_CUSTOFormula
	 **/
	public Double obterValorMaterialNoMovimento(Integer numeroDocumento, Date dtCompetencia, Integer codigoMaterial, Short tmvSeq, Byte tmvComp, Integer frnNumeroHCPA){
		Double valor = getSceMovimentoMaterialDAO().obterValorMaterialNoMovimento(numeroDocumento, codigoMaterial, tmvSeq, tmvComp);
		
		if(valor==null || valor.equals(new Double("0"))){
			valor = getSceEstoqueGeralDAO().getCustoMedioPonderado(codigoMaterial, dtCompetencia, frnNumeroHCPA);
			if(valor==null){
				return new Double("0");
			}
		}
		
		return valor;

	}
	
	/** Busca os valores para serem impressos nos relatórios de impressão de requisição de material (Correção incidente #28423)
	 * ORADB CF_CUSTOFormula
	 **/
	public Double obterValorMaterialNoMovimentoRelatorioPorRM(Integer numeroDocumento, Date dtCompetencia, Integer codigoMaterial, Short tmvSeq, Byte tmvComp){
		Double valor = getSceMovimentoMaterialDAO().obterValorMaterialNoMovimento(numeroDocumento, codigoMaterial, tmvSeq, tmvComp);
		
		if(valor==null){
			return new Double("0");
		}
		
		return valor;

	}

	/**
	 * ORADB PROCEDURE SCEP_MMT_ATU_CONSTOT
	 * @param sceMovimentoMaterial
	 * @param sceEstoqueAlmoxarifado
	 *  
	 */
	private void atualizaConsumtoTotalMateriais(
			SceMovimentoMaterial sceMovimentoMaterial,
			AghParametros aghParametroDocDA, AghParametros aghParametroDocRM,
			SceTipoMovimento tipoMovimento) throws ApplicationBusinessException {

		Integer matCodigo = sceMovimentoMaterial.getMaterial().getCodigo();
		Integer cctCodigo = sceMovimentoMaterial.getCentroCusto().getCodigo();

		/* Busca dt_competencia do Sistema (Parâmetro) */
		AghParametros aghParamCompetencia = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);

		Date dataCompetencia = aghParamCompetencia.getVlrData();
		Short almSeq = sceMovimentoMaterial.getAlmoxarifado().getSeq();

		/* Busca Quantidade e Valor Total consumido pelo material */
		SceConsumoTotalMaterial consumoTotalMaterial = this.getSceConsumoTotalMateriaisDAO().obterSceConsumoTotalMaterial(matCodigo, cctCodigo, dataCompetencia, almSeq);

		Double valorTotalNovo = new Double(0); 
		Integer qtdeTotalNova = 0;
		
		Double valorTotalAnt = new Double(0); 
		Integer qtdeTotalAnt = 0;
		
		if (consumoTotalMaterial == null) {

			if (((tipoMovimento.getId().getSeq().shortValue() == aghParametroDocDA.getVlrNumerico().shortValue()) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE)) 
					|| ((tipoMovimento.getId().getSeq().shortValue() == aghParametroDocRM.getVlrNumerico().shortValue()) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))){

				qtdeTotalNova = qtdeTotalAnt - sceMovimentoMaterial.getQuantidade();
				valorTotalNovo = valorTotalAnt - sceMovimentoMaterial.getValor().doubleValue();

			} else if (((tipoMovimento.getId().getSeq().shortValue() == aghParametroDocRM.getVlrNumerico().shortValue()) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE)) 
					|| ((tipoMovimento.getId().getSeq().shortValue() == aghParametroDocDA.getVlrNumerico().shortValue()) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))){

				qtdeTotalNova = sceMovimentoMaterial.getQuantidade();
				valorTotalNovo = sceMovimentoMaterial.getValor().doubleValue();

			}

			SceConsumoTotalMaterial sceConsumoTotalMaterial = new SceConsumoTotalMaterial();
			SceConsumoTotalMaterialId consumoTotalMaterialID = new SceConsumoTotalMaterialId();

			if (dataCompetencia != null) {

				consumoTotalMaterialID.setDtCompetencia(dataCompetencia);

			}

			consumoTotalMaterialID.setAlmSeq(sceMovimentoMaterial.getAlmoxarifado().getSeq());
			consumoTotalMaterialID.setFccCodigo(sceMovimentoMaterial.getCentroCusto().getCodigo());
			consumoTotalMaterialID.setMatCodigo(sceMovimentoMaterial.getMaterial().getCodigo());
			sceConsumoTotalMaterial.setId(consumoTotalMaterialID);

			sceConsumoTotalMaterial.setQuantidade(qtdeTotalNova);
			sceConsumoTotalMaterial.setValor(valorTotalNovo);

			/**
			 *  Inclui registro de Consumo: tabela =  SCE_CONSUMO_TOTAL_MATERIAIS
			 */
			this.getSceConsumoTotalMateriaisDAO().persistir(sceConsumoTotalMaterial);

		} else { 

			qtdeTotalAnt = consumoTotalMaterial.getQuantidade();
			valorTotalAnt = consumoTotalMaterial.getValor();

			if (((tipoMovimento.getId().getSeq() == aghParametroDocDA.getVlrNumerico().shortValue()) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE)) 
					|| ((tipoMovimento.getId().getSeq().shortValue() == aghParametroDocRM.getVlrNumerico().shortValue()) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))){

				qtdeTotalNova = qtdeTotalAnt - sceMovimentoMaterial.getQuantidade();
				valorTotalNovo = valorTotalAnt - sceMovimentoMaterial.getValor().doubleValue();

			} else if (((tipoMovimento.getId().getSeq().shortValue() == aghParametroDocRM.getVlrNumerico().shortValue()) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.FALSE)) 
					|| ((tipoMovimento.getId().getSeq().shortValue() == aghParametroDocDA.getVlrNumerico().shortValue()) && sceMovimentoMaterial.getIndEstorno().equals(Boolean.TRUE))){

				qtdeTotalNova = qtdeTotalAnt + sceMovimentoMaterial.getQuantidade();
				valorTotalNovo = valorTotalAnt + sceMovimentoMaterial.getValor().doubleValue();

			}

			consumoTotalMaterial.setQuantidade(qtdeTotalNova);
			consumoTotalMaterial.setValor(valorTotalNovo);
			
			/**
			 *  Atualiza Consumo Total do material: tabela =  SCE_CONSUMO_TOTAL_MATERIAIS
			 */
			this.getSceConsumoTotalMateriaisDAO().atualizar(consumoTotalMaterial);

		}
				
	}

	/**
	 * Regra criada pela melhoria #17059
	 * @param sceMovimentoMaterial
	 * @throws BaseException
	 */
	private void validaValorMovimenoMinimo(SceMovimentoMaterial sceMovimentoMaterial) throws ApplicationBusinessException {
		
		final Double valorMinimo = 0.01;
		
		verificaCustoMedioPonderadorIgualZero(sceMovimentoMaterial);
		
		if ((sceMovimentoMaterial.getValor().doubleValue() < valorMinimo) && (sceMovimentoMaterial.getCustoMedioPonderadoGer() != null)) {
			
			Double quantidadeMinima = valorMinimo / sceMovimentoMaterial.getCustoMedioPonderadoGer().doubleValue();

			Double valorTotal = (quantidadeMinima * sceMovimentoMaterial.getCustoMedioPonderadoGer().doubleValue());
			
			if (valorTotal < valorMinimo) {
				
				quantidadeMinima = quantidadeMinima + 1;
				
			}
			
			if (quantidadeMinima < 1.00) {
				quantidadeMinima = 1.00;
			}
			
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.ERRO_VALOR_MINIMO_MOVIMENTO, sceMovimentoMaterial.getMaterial().getCodigo() ,sceMovimentoMaterial.getCustoMedioPonderadoGer(), quantidadeMinima.intValue());
			
		}
		
	}
	
	private void verificaCustoMedioPonderadorIgualZero(SceMovimentoMaterial sceMovimentoMaterial) throws ApplicationBusinessException {
		if (verificaCustoMedioZero(sceMovimentoMaterial)) {
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.ERRO_CM_MOVIMENTO, sceMovimentoMaterial.getMaterial().getCodigo());
		}

	}

	private Boolean verificaCustoMedioZero(SceMovimentoMaterial sceMovimentoMaterial) {
		return sceMovimentoMaterial.getCustoMedioPonderadoGer() != null && sceMovimentoMaterial.getCustoMedioPonderadoGer().equals(BigDecimal.ZERO);
	}

	
	/**
	 * Calcula custo médio ponderado
	 * @param valor
	 * @param quantidade
	 * @return
	 */
	public Double calcularCustoMedioPonderado(Double valor, Integer quantidade) {

		Double resultado = 0d;
		
		if(quantidade == null){
			quantidade = 0;
		}
		
		if(valor == null){
			valor = 0d;
		}

		if(quantidade != 0 && valor != 0) {
			resultado = valor / quantidade;
		}
		
		return resultado;
	}

	/*
	 * Getters e Setters ONs, RNs
	 */

	protected SceTipoMovimentosRN getSceTipoMovimentosRN() {
		return sceTipoMovimentosRN;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SceMovimentoMaterialDAO getSceMovimentoMaterialDAO() {
		return sceMovimentoMaterialDAO;
	}
	
	protected SceItemRmsDAO getSceItemRmsDAO(){
		return sceItemRmsDAO;
	}

	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO() {
		return sceTipoMovimentosDAO;
	}

	protected SceValidadesRN getSceValidadesRN() {
		return sceValidadesRN;
	}

	protected SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN(){
		return sceEstoqueAlmoxarifadoRN;
	}

	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO(){
		return sceEstoqueAlmoxarifadoDAO;
	}

	protected SceEntradaSaidaSemLicitacaoDAO getSceEntradaSaidaSemLicitacaoDAO(){
		return sceEntradaSaidaSemLicitacaoDAO;
	}

	protected SceHistoricoProblemaMaterialDAO getSceHistoricoProblemaMaterialDAO(){
		return sceHistoricoProblemaMaterialDAO;
	}

	protected SceHistoricoProblemaMaterialRN getSceHistoricoProblemaMaterialRN(){
		return sceHistoricoProblemaMaterialRN;
	}

	protected SceValidadeDAO getSceValidadesDAO() {
		return sceValidadeDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}

	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO(){
		return sceEstoqueGeralDAO;
	}

	protected SceEstoqueGeralRN getSceEstoqueGeralRN(){
		return sceEstoqueGeralRN;
	}


	protected SceConsumoTotalMateriaisDAO getSceConsumoTotalMateriaisDAO(){
		return sceConsumoTotalMateriaisDAO;
	}

	protected SceEstqAlmoxMvtoDAO getSceEstqAlmoxMvtoDAO(){
		return sceEstqAlmoxMvtoDAO;
	}
	
	public ScoMaterialDAO getScoMaterialDAO() {
		return scoMaterialDAO;
	}

	public void setScoMaterialDAO(ScoMaterialDAO scoMaterialDAO) {
		this.scoMaterialDAO = scoMaterialDAO;
	}	

	public ScoUnidadeMedidaDAO getScoUnidadeMedidaDAO() {
		return scoUnidadeMedidaDAO;
	}

	public void setScoUnidadeMedidaDAO(ScoUnidadeMedidaDAO scoUnidadeMedidaDAO) {
		this.scoUnidadeMedidaDAO = scoUnidadeMedidaDAO;
	}

	public void setSceItemTransferenciaDAO(SceItemTransferenciaDAO sceItemTransferenciaDAO) {
		this.sceItemTransferenciaDAO = sceItemTransferenciaDAO;
	}

	public void setSceMovimentoMaterialDAO(SceMovimentoMaterialDAO sceMovimentoMaterialDAO) {
		this.sceMovimentoMaterialDAO = sceMovimentoMaterialDAO;
	}	

	public ScoFornecedorDAO getScoFornecedorDAO() {
		return scoFornecedorDAO;
	}

	public void setScoFornecedorDAO(ScoFornecedorDAO scoFornecedorDAO) {
		this.scoFornecedorDAO = scoFornecedorDAO;
	}

	public void setSceTipoMovimentosDAO(SceTipoMovimentosDAO sceTipoMovimentosDAO) {
		this.sceTipoMovimentosDAO = sceTipoMovimentosDAO;
	}

	protected SceEstqAlmoxMvtoRN getSceEstqAlmoxMvtoRN(){
		return sceEstqAlmoxMvtoRN;
	}
	
	protected SceItemTransferenciaDAO getSceItemTransferenciaDAO() {
		return sceItemTransferenciaDAO;
	}
	
	protected ControlarValidadeMaterialRN getControlarValidadeMaterialRN() {
		return controlarValidadeMaterialRN;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	} 
	
	public FccCentroCustosDAO getFccCentroCustosDAO() {
		return fccCentroCustosDAO;
	}

	public void setFccCentroCustosDAO(FccCentroCustosDAO fccCentroCustosDAO) {
		this.fccCentroCustosDAO = fccCentroCustosDAO;
	} 

	
}
