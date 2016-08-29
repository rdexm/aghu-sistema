package br.gov.mec.aghu.estoque.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioIndProducaoInterna;
import br.gov.mec.aghu.dominio.DominioOrderBy;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoRequisicaoMaterial;
import br.gov.mec.aghu.dominio.TipoDocumentoImpressao;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.estoque.vo.RequisicaoMaterialItensVO;
import br.gov.mec.aghu.estoque.vo.RequisicaoMaterialVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.ScePacoteMateriaisId;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMateriaisClassificacoes;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({ "PMD.AghuTooManyMethods", "PMD.CyclomaticComplexity" })
@Stateless
public class SceReqMateriaisRN extends BaseBusiness {

	@EJB
	private SceMovimentoMaterialRN sceMovimentoMaterialRN;
	@EJB
	private SceItemRmsRN sceItemRmsRN;
	@EJB
	private SceAlmoxarifadosRN sceAlmoxarifadosRN;
	@EJB
	private SceTipoMovimentosRN sceTipoMovimentosRN;

	private static final Log LOG = LogFactory.getLog(SceReqMateriaisRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@Inject
	private SceReqMateriaisDAO sceReqMateriaisDAO;
	
	@Inject
	private SceAlmoxarifadoDAO sceAlmoxarifadoDAO;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private ICentroCustoFacade centroCustoFacade;

	@EJB
	private ICascaFacade cascaFacade;

	@Inject
	private SceItemRmsDAO sceItemRmsDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1520616142862363276L;

	public enum SceReqMateriaisRNExceptionCode implements BusinessExceptionCode {
		NUMERO_CUM_NAO_ENCONTRADA, SCE_00382, SCE_00887, SCE_00307, SCE_00308, SCE_00316, SCE_00317, SCE_00309, SCE_00311, SCE_00796, SCE_00312, SCE_00313, SCE_00314, SCE_00315, SCE_00367, RN_EFETIVADA_001, RN_EFETIVADA_002, RN_EFETIVADA_003, MENSAGEM_ALMOXARIFADO_NAO_INFORMADO, MENSAGEM_CC_REQ_NAO_INFORMADO, MENSAGEM_CC_REQ_AP_NAO_INFORMADO, SCE_ERRO_PARAMETRO, SCE_ERRO_PARAMETRO_TIPO_IMPRESSAO, MENSAGEM_RM_NAO_ENCONTRADA;
	}

	/*
	 * Métodos para Inserir SceReqMateriais
	 */

	/**
	 * ORADB SCET_RMS_BRI (INSERT)
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	private void preInserir(SceReqMaterial sceReqMateriais) throws BaseException {
		this.atualizarSceReqMateriaisServidorLogado(sceReqMateriais); // RN1
		this.atualizarSceReqMateriaisSituacaoConfirmadaServidorLogado(sceReqMateriais); // RN2
		this.validarSceReqMateriaisNumeroCUM(sceReqMateriais); // RN3
		this.atualizarSceReqMateriaisTipoMovimento(sceReqMateriais); // RN4
		this.validarSceReqMateriaisAlmoxarifadoAtivo(sceReqMateriais); // RN5
		this.validarSceReqMateriaisInclusao(sceReqMateriais); // RN6
	}

	/**
	 * Inserir SceReqMateriais
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	public void inserir(SceReqMaterial sceReqMateriais) throws BaseException {
		this.preInserir(sceReqMateriais);
		this.getSceReqMateriaisDAO().persistir(sceReqMateriais);
	}

	/*
	 * Métodos para atualizar SceReqMateriais
	 */

	/**
	 * ORADB SCET_RMS_BRU (UPDATE)
	 * 
	 * @param sceReqMateriais
	 * @param sceReqMateriaisOriginal
	 * @throws ApplicationBusinessException
	 * 
	 */
	private void preAtualizar(SceReqMaterial sceReqMateriais, SceReqMaterial sceReqMateriaisOriginal, String nomeMicrocomputador)
			throws BaseException {

		if (!sceReqMateriais.getIndSituacao().equals(sceReqMateriaisOriginal.getIndSituacao())) {

			atualizarEfetivacao(sceReqMateriais);
			atualizarSceReqMateriaisSituacaoConfirmadaServidorLogado(sceReqMateriais);

		}

		verificarRequisicaoEstornada(sceReqMateriaisOriginal);

		verificarAlteracoesRequisicaoMateriais(sceReqMateriais, sceReqMateriaisOriginal);

		this.validarSceReqMateriaisNumeroCUM(sceReqMateriaisOriginal);

		if (!sceReqMateriais.getIndSituacao().equals(sceReqMateriaisOriginal.getIndSituacao())) {

			atualizarCancelamento(sceReqMateriais);

		}

		if (sceReqMateriais.getTipoMovimento()!=null && !sceReqMateriais.getTipoMovimento().equals(sceReqMateriaisOriginal.getTipoMovimento())) {

			AghParametros parametroTipoMovimento = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_RM);
			SceTipoMovimento tipoMovimentoParam = getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(
					parametroTipoMovimento.getVlrNumerico().shortValue());

			if (!sceReqMateriais.getTipoMovimento().equals(tipoMovimentoParam)) {

				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00367);

			}

		}

		if (sceReqMateriais.getTipoMovimento()!=null && !sceReqMateriais.getTipoMovimento().equals(sceReqMateriaisOriginal.getTipoMovimento())) {

			getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(sceReqMateriais.getTipoMovimento());

		}

		if (sceReqMateriais.getAlmoxarifado()!=null && !sceReqMateriais.getAlmoxarifado().equals(sceReqMateriaisOriginal.getAlmoxarifado())) {

			getSceAlmoxarifadosRN().verificarAlmoxariadoAtivoPorId(sceReqMateriais.getAlmoxarifado().getSeq());

		}

		if (sceReqMateriais.getEstorno()) {

			List<SceItemRms> itensRms = getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriaisQntEntregue(sceReqMateriais);

			if (itensRms != null && !itensRms.isEmpty()) {

				getSceMovimentoMaterialRN().verificarMovimentoMaterialEstornoPorRequisicaoMateral(sceReqMateriais);

			}

			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			getSceMovimentoMaterialRN().gerarMovimentoEstoque(sceReqMateriais, nomeMicrocomputador);
			sceReqMateriais.setServidorEstornado(servidorLogado);
			sceReqMateriais.setDtEstorno(new Date());

		}

	}

	public void validaCamposObrigatorios(SceReqMaterial sceReqMateriais) throws ApplicationBusinessException {
		if (sceReqMateriais.getAlmoxarifado() == null) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.MENSAGEM_ALMOXARIFADO_NAO_INFORMADO);
		}
		if (sceReqMateriais.getCentroCusto() == null) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.MENSAGEM_CC_REQ_NAO_INFORMADO);
		}
		if (sceReqMateriais.getCentroCustoAplica() == null) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.MENSAGEM_CC_REQ_AP_NAO_INFORMADO);
		}
	}

	public RequisicaoMaterialVO buscaMateriaisItensImprimir(Integer reqMat, DominioOrderBy orderBy, boolean inserirMedia)
			throws BaseException {
		RequisicaoMaterialVO requisicao = getSceReqMateriaisDAO().buscaMateriaisItensImprimir(reqMat);

		if (requisicao != null) {

			SceItemRmsRN rmsRN = null;
			AghParametros param = null;

			if (inserirMedia || requisicao.getSituacao().equalsIgnoreCase("efetivada")) {
				rmsRN = this.getSceItemRmsRN();
				param = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);
			}

			requisicao.setRequisicaoMaterial(getSceReqMateriaisDAO().obterMaterialPorId(requisicao.getReqMaterial()));

			List<SceItemRms> itens = getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriaisOrderBy(requisicao.getReqMaterial(),
					orderBy, requisicao.getRequisicaoMaterial().getIndSituacao());

			int counter = 1;
			for (SceItemRms sceItemRms : itens) {
				RequisicaoMaterialItensVO itemVO = new RequisicaoMaterialItensVO();

				itemVO.setItem(((Integer) counter++));
				itemVO.setMaterialCod(sceItemRms.getEstoqueAlmoxarifado().getMaterial().getCodigo());
				itemVO.setNomeMaterial(sceItemRms.getEstoqueAlmoxarifado().getMaterial().getNome());
				itemVO.setUnidade(sceItemRms.getScoUnidadeMedida().getCodigo());
				itemVO.setEndereco(sceItemRms.getEstoqueAlmoxarifado().getEndereco());
				itemVO.setQuantSolic(sceItemRms.getQtdeRequisitada());
				itemVO.setQuantEntr(sceItemRms.getQtdeEntregue());
				itemVO.setIndCorrosivo(sceItemRms.getEstoqueAlmoxarifado().getMaterial().getIndCorrosivo());
				itemVO.setIndInflamavel(sceItemRms.getEstoqueAlmoxarifado().getMaterial().getIndInflamavel());
				itemVO.setIndRadioativo(sceItemRms.getEstoqueAlmoxarifado().getMaterial().getIndRadioativo());
				itemVO.setIndReativo(sceItemRms.getEstoqueAlmoxarifado().getMaterial().getIndReativo());
				itemVO.setIndToxico(sceItemRms.getEstoqueAlmoxarifado().getMaterial().getIndToxico());

				if (sceItemRms.getObservacao() != null) {
					itemVO.setObservacao(sceItemRms.getObservacao().toUpperCase());
				}

				if (inserirMedia) {

					rmsRN.obterConsumos(sceItemRms, param.getVlrData(), sceItemRms.getEstoqueAlmoxarifado().getMaterial().getCodigo(),
							requisicao.getRequisicaoMaterial().getCentroCustoAplica().getCodigo());

					itemVO.setMediaSemestre(sceItemRms.getMediaSemestre());
					itemVO.setMediaTrintaDias(sceItemRms.getMediaTrintaDias());
				}

				if (requisicao.getSituacao().equalsIgnoreCase("efetivada")) {
					Double valorMaterial = getSceMovimentoMaterialRN().obterValorMaterialNoMovimentoRelatorioPorRM(
							requisicao.getReqMaterial(), param.getVlrData(), itemVO.getMaterialCod(), requisicao.getTipoMovimentoSeq(),
							Byte.valueOf(requisicao.getTipoMovimentoComplemento()));
					itemVO.setValorMaterial(valorMaterial);
				}
				//adiciona o item
				requisicao.getItemVO().add(itemVO);
			}
		} else {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.MENSAGEM_RM_NAO_ENCONTRADA);
		}
		return requisicao;
	}

	/**
	 * ORADB P_DEFINE_IMPRESSORA Define a impressora que vai ser usada pra
	 * impressão remota
	 * 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public ImpImpressora defineImpressoraImpressao(SceReqMaterial reqMateriais) throws ApplicationBusinessException {

		ImpImpressora impressoraImp = null;
		List<SceItemRms> itens = getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriais(reqMateriais.getSeq());
		ScoMaterial material = itens.get(0).getEstoqueAlmoxarifado().getMaterial();
		ScoGrupoMaterial grupoMaterialRm = material.getGrupoMaterial();

		String v_p_class_mat_soros = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CLASS_MAT_SOROS).getVlrTexto();

		if (v_p_class_mat_soros == null) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_ERRO_PARAMETRO,
					AghuParametrosEnum.P_CLASS_MAT_SOROS.toString());
		}

		Long v_classe = Long.parseLong(v_p_class_mat_soros);
		Long v_classe_fim = Long.parseLong(v_p_class_mat_soros.replace("00", "99"));

		List<ScoMateriaisClassificacoes> matClasses = getComprasFacade().buscarScoMateriaisClassificacoesPorMaterialEClassificacao(
				material.getCodigo(), v_classe, v_classe_fim);

		BigDecimal v_almox_central = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_CENTRAL);
		BigDecimal v_farm_central = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_FARM_CENTRAL);
		BigDecimal v_psico_entorp = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_PSICO_ENTORP);
		BigDecimal v_higienizacao = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_HIGIENIZACAO);
		BigDecimal v_snd = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_SND);
		BigDecimal v_rouparia = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_ROUPARIA);
		BigDecimal v_engenharia = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_ENGENHARIA);
		BigDecimal v_patocli = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_PATOCLI);
		BigDecimal v_cnpq = obterParametroImpressao(AghuParametrosEnum.P_ALMOX_CNPQ);
		BigDecimal v_gr_mat_expediente = obterParametroImpressao(AghuParametrosEnum.P_GR_MAT_EXPEDIENTE);
		BigDecimal v_gr_mat_higiene = obterParametroImpressao(AghuParametrosEnum.P_GR_HIGIENE_LIMPEZA);
		BigDecimal v_gr_mat_informatica = obterParametroImpressao(AghuParametrosEnum.P_GR_MAT_INFORMATICA);
		BigDecimal v_gr_mat_utiliz_grafica = obterParametroImpressao(AghuParametrosEnum.P_GR_MAT_UTILIZ_GRAFICA);
		BigDecimal v_gr_mat_audio_video = obterParametroImpressao(AghuParametrosEnum.P_GR_MAT_AUDIO_VIDEO);
		BigDecimal v_gr_mat_medico_hosp = obterParametroImpressao(AghuParametrosEnum.P_GRPO_MAT_MEDICO_HOSP);
		BigDecimal v_gr_acess_sist_monit = obterParametroImpressao(AghuParametrosEnum.P_GR_MAT_ACESS_SIST_MONIT);
		BigDecimal v_gr_mat_medicamentos = obterParametroImpressao(AghuParametrosEnum.P_GR_MAT_MEDIC);
		BigDecimal v_gr_mat_diagnostico = obterParametroImpressao(AghuParametrosEnum.P_GR_MAT_DIAGNOSTICO);
		BigDecimal v_gr_ortese_protese = obterParametroImpressao(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
		BigDecimal v_gr_comb_e_gases = obterParametroImpressao(AghuParametrosEnum.P_GR_COMB_E_GASES);

		/* IFs */
		/*
		 * -- Quando Centro de Custo do Usuário é o mesmo do almox de entrega do
		 * material. -- Para RM gerada no próprio almoxarifado a impressora
		 * destino não é gravada, -- salvo alguns casos em que o usuário é do
		 * Ccusto do almox mas remete a RM de fora. -- Obs. Deve ser tratado
		 * conforme definição de cada almoxarifado.
		 */

		SceAlmoxarifado almoxSelect = reqMateriais.getAlmoxarifado();
		if ((material.getIndProducaoInterna() != null && material.getIndProducaoInterna().equals(DominioIndProducaoInterna.G))
				&& material.getIndEstocavel().equals(DominioSimNao.N)) {
			return buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_GRAFICA, almoxSelect);
		}

		if (reqMateriais.getCentroCusto().getCodigo().intValue() == reqMateriais.getAlmoxarifado().getCentroCusto().getCodigo()
				&& reqMateriais.getAlmoxarifado().getSeq().intValue() != v_farm_central.intValue()
				&& reqMateriais.getAlmoxarifado().getSeq().intValue() != v_psico_entorp.intValue()
				&& reqMateriais.getAlmoxarifado().getSeq().intValue() != v_higienizacao.intValue()
				&& reqMateriais.getAlmoxarifado().getSeq().intValue() != v_rouparia.intValue()) {
			impressoraImp = null;
		}

		if (reqMateriais.getAlmoxarifado().getSeq().intValue() != v_almox_central.intValue()) {
			if((material.getIndProducaoInterna() != null && material.getIndProducaoInterna().equals(DominioIndProducaoInterna.F))){
				impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_FARM_IND, almoxSelect);
			} else {
				impressoraImp = obterImpOutrosAlmox(reqMateriais, matClasses, v_farm_central, v_psico_entorp, v_higienizacao, v_snd, v_rouparia, v_engenharia, v_patocli, v_cnpq);
			}
		}

		if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_almox_central.intValue()) {
			impressoraImp = obterImpAlmoxCentral(reqMateriais, material, grupoMaterialRm, v_gr_mat_expediente, v_gr_mat_higiene,
					v_gr_mat_informatica, v_gr_mat_utiliz_grafica, v_gr_mat_audio_video, v_gr_mat_medico_hosp, v_gr_acess_sist_monit,
					v_gr_mat_medicamentos, v_gr_mat_diagnostico, v_gr_ortese_protese, v_gr_comb_e_gases);
		}

		return impressoraImp;
	}

	private BigDecimal obterParametroImpressao(AghuParametrosEnum paramNomeImp) throws ApplicationBusinessException {
		BigDecimal vlr = this.getParametroFacade().buscarAghParametro(paramNomeImp).getVlrNumerico();

		if (vlr == null) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_ERRO_PARAMETRO, paramNomeImp.toString());
		}

		return vlr;
	}

	private ImpImpressora obterImpOutrosAlmox(SceReqMaterial reqMateriais, List<ScoMateriaisClassificacoes> matClasses,
			BigDecimal v_farm_central, BigDecimal v_psico_entorp, BigDecimal v_higienizacao, BigDecimal v_snd, BigDecimal v_rouparia,
			BigDecimal v_engenharia, BigDecimal v_patocli, BigDecimal v_cnpq) throws ApplicationBusinessException {
		
		SceAlmoxarifado almoxSelect = reqMateriais.getAlmoxarifado();
		ImpImpressora impressoraImp = null;

		if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_farm_central.intValue()) {
			if (matClasses != null && matClasses.size() > 0) {
				impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_SOROS, almoxSelect);
			} else {
				impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_FARM_CENTRAL, almoxSelect);
			}
		} else if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_psico_entorp.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_FARM_CENTRAL, almoxSelect);
		} else if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_higienizacao.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_HIGIENE, almoxSelect);
		} else if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_rouparia.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_ROUPARIA, almoxSelect);
		} else if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_snd.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_SND, almoxSelect);
		} else if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_engenharia.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_ENG, almoxSelect);
		} else if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_patocli.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_PATOCLI, almoxSelect);
		} else if (reqMateriais.getAlmoxarifado().getSeq().intValue() == v_cnpq.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_CNPQ, almoxSelect);
		} else if (impressoraImp == null) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO, almoxSelect);
		}

		return impressoraImp;
	}

	private ImpImpressora obterImpAlmoxCentral(SceReqMaterial reqMateriais, ScoMaterial material, ScoGrupoMaterial grupoMaterialRm,
			BigDecimal v_gr_mat_expediente, BigDecimal v_gr_mat_higiene, BigDecimal v_gr_mat_informatica,
			BigDecimal v_gr_mat_utiliz_grafica, BigDecimal v_gr_mat_audio_video, BigDecimal v_gr_mat_medico_hosp,
			BigDecimal v_gr_acess_sist_monit, BigDecimal v_gr_mat_medicamentos, BigDecimal v_gr_mat_diagnostico,
			BigDecimal v_gr_ortese_protese, BigDecimal v_gr_comb_e_gases) throws ApplicationBusinessException {
		ImpImpressora impressoraImp = null;
		SceAlmoxarifado almoxSelect = reqMateriais.getAlmoxarifado();

		BigDecimal v_gr_equip_protecao = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_EQUIP_PROTECAO)
				.getVlrNumerico();
		BigDecimal v_gr_rouparia = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_ROUPARIA).getVlrNumerico();
		BigDecimal v_gr_utensilios_diversos = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_UTENSILIOS_DIVERSOS)
				.getVlrNumerico();
		BigDecimal v_gr_prod_int_grafica = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_PROD_INT_GRAFICA)
				.getVlrNumerico();

		if (grupoMaterialRm.getCodigo().intValue() == v_gr_mat_medico_hosp.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_MEDICO_HOSP, almoxSelect);
		} else if (grupoMaterialRm.getCodigo().intValue() == v_gr_mat_higiene.intValue()
				|| grupoMaterialRm.getCodigo().intValue() == v_gr_mat_expediente.intValue()
				|| grupoMaterialRm.getCodigo().intValue() == v_gr_mat_informatica.intValue()
				|| grupoMaterialRm.getCodigo().intValue() == v_gr_mat_utiliz_grafica.intValue()
				|| grupoMaterialRm.getCodigo().intValue() == v_gr_mat_audio_video.intValue()
				|| grupoMaterialRm.getCodigo().intValue() == v_gr_equip_protecao.intValue()
				|| grupoMaterialRm.getCodigo().intValue() == v_gr_rouparia.intValue()
				|| grupoMaterialRm.getCodigo().intValue() == v_gr_utensilios_diversos.intValue()
				|| grupoMaterialRm.getCodigo().intValue() == v_gr_prod_int_grafica.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_EXPEDIENTE, almoxSelect);
		} else if (material.getIndProducaoInterna() != null && material.getIndProducaoInterna().equals(DominioIndProducaoInterna.F)) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_FARM_IND, almoxSelect);
		} else if (material.getIndProducaoInterna() != null && material.getIndProducaoInterna().equals(DominioIndProducaoInterna.G)) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_EXPEDIENTE, almoxSelect);
		} else if (material.getIndProducaoInterna() != null && material.getIndProducaoInterna().equals(DominioIndProducaoInterna.R)) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_ROUPARIA, almoxSelect);
		} else if (grupoMaterialRm.getEngenhari()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_ENG, almoxSelect);
		} else if (grupoMaterialRm.getCodigo().intValue() == v_gr_comb_e_gases.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_ENG, almoxSelect);
		} else if (grupoMaterialRm.getCodigo().intValue() == v_gr_acess_sist_monit.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_ENG, almoxSelect);
		} else if (grupoMaterialRm.getCodigo().intValue() == v_gr_mat_medicamentos.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_MEDICAMENTOS, almoxSelect);
		} else if (grupoMaterialRm.getCodigo().intValue() == v_gr_mat_diagnostico.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_MEDICAMENTOS, almoxSelect);
		} else if (grupoMaterialRm.getCodigo().intValue() == v_gr_ortese_protese.intValue()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO_ORTESE_PROTESE, almoxSelect);
		} else if (reqMateriais.getAlmoxarifado().getIndCentral()) {
			impressoraImp = buscaImpressora(TipoDocumentoImpressao.RM_PEDIDO, almoxSelect);
		}

		return impressoraImp;
	}

	public ImpImpressora buscaImpressora(TipoDocumentoImpressao tipoImpressora, SceAlmoxarifado almoxSelect) throws ApplicationBusinessException {
		List<ImpImpressora> lstImp = null;
		ImpImpressora impre = null;
		
		Short unfSeq = obtemUnfSeqPorAlmoxarifado(almoxSelect);
		if (unfSeq != null) {
			lstImp = getAghuFacade().listarImpImpressorasPorUnfSeqETipoDocumentoImpressao(unfSeq, tipoImpressora);
		} else {
			lstImp = getAghuFacade().listarImpImpressorasPorTipoDocumentoImpressao(tipoImpressora);
		}

		if (lstImp != null) {
			for (ImpImpressora impImpressora : lstImp) {
				if (impImpressora != null) {
					impre = impImpressora;
					impre.getDescricao();
				}
			}
		}

		if (impre == null) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_ERRO_PARAMETRO_TIPO_IMPRESSAO,
					tipoImpressora.getDescricao());
		}

		return impre;
	}
	
	protected SceAlmoxarifadoDAO getSceAlmoxarifadoDAO() {
		return sceAlmoxarifadoDAO;
	}

	/**
	 * Código da Unicadade Funcinal
	 */
	private Short obtemUnfSeqPorAlmoxarifado(SceAlmoxarifado almoxSelect){
		return aghuFacade.obtemUnfSeqPorAlmoxarifado(almoxSelect.getSeq());
	}

	/**
	 * Atualizar SceReqMateriais
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 * 
	 */
	public void atualizar(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {

		final SceReqMaterial sceReqMateriaisOriginal = this.getSceReqMateriaisDAO().obterOriginal(sceReqMateriais.getSeq());

		this.preAtualizar(sceReqMateriais, sceReqMateriaisOriginal, nomeMicrocomputador);
		this.getSceReqMateriaisDAO().merge(sceReqMateriais);
		this.posAtualizar(sceReqMateriais, sceReqMateriaisOriginal, nomeMicrocomputador);

	}

	/**
	 * ORADB SCEP_ENFORCE_RMS_RULES (UPDATE)
	 * 
	 * @param sceReqMateriais
	 * @param sceReqMateriaisOriginal
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void posAtualizar(SceReqMaterial sceReqMateriais, SceReqMaterial sceReqMateriaisOriginal, String nomeMicrocomputador)
			throws BaseException {
		if (sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E)) {
			List<SceItemRms> itensRequisicao = this.getSceItemRmsDAO().pesquisarListaSceItemRmsPorSceReqMateriais(sceReqMateriais.getSeq());
			for (SceItemRms item : itensRequisicao) {
				if (item.getQtdeEntregue() == null) {
					item.setQtdeEntregue(item.getQtdeRequisitada());
					this.getSceItemRmsRN().atualizar(item);
				}
			}
		}

		if (!sceReqMateriais.getIndSituacao().equals(sceReqMateriaisOriginal.getIndSituacao())
				&& sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E) && !sceReqMateriais.getEstorno()) {

			this.getSceItemRmsRN().verificarSaldoTerceiros(sceReqMateriais);

			this.getSceMovimentoMaterialRN().gerarMovimento(sceReqMateriais, nomeMicrocomputador);
			// TODO falta implementar método atualizarPedido
			//this.getSceItemPedidoMateriaisRN().atualizarPedido(sceReqMateriais);
			this.gerarRequisicaoMaterialFornecedor(sceReqMateriais);

		}

		if ((!sceReqMateriaisOriginal.getEstorno().equals(sceReqMateriais.getEstorno())) && sceReqMateriais.getEstorno()) {
			this.atualizarRequisicaoMateriaisFornecedor(sceReqMateriais, nomeMicrocomputador);
		}

		if ((!sceReqMateriaisOriginal.getIndSituacao().equals(sceReqMateriais.getIndSituacao()))
				&& (sceReqMateriaisOriginal.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E) && sceReqMateriais
						.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.A))) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.RN_EFETIVADA_001);
		}
		if ((!sceReqMateriaisOriginal.getIndSituacao().equals(sceReqMateriais.getIndSituacao()))
				&& (sceReqMateriaisOriginal.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E) && sceReqMateriais
						.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C))) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.RN_EFETIVADA_002);
		}
		if ((!sceReqMateriaisOriginal.getIndSituacao().equals(sceReqMateriais.getIndSituacao()))
				&& (sceReqMateriaisOriginal.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E) && sceReqMateriais
						.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G))) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.RN_EFETIVADA_003);
		}

	}

	/*
	 * RNs inserir
	 */

	/**
	 * ORADB scek_rms_rn.rn_rmsp_atu_servidor Atualiza servidor com o usuário
	 * logado e data de geração com a data atual.
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSceReqMateriaisServidorLogado(SceReqMaterial sceReqMateriais) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		sceReqMateriais.setServidor(servidorLogado);
		sceReqMateriais.setDtGeracao(new Date());
	}

	/**
	 * ORADB scek_rms_rn.rn_rmsp_atu_confirma Se a situação do SceReqMateriais
	 * for igual a C (Confirmada): Atualiza servidor confirmado com o usuário
	 * logado e data de confirmação com a data atual.
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSceReqMateriaisSituacaoConfirmadaServidorLogado(SceReqMaterial sceReqMateriais)
			throws ApplicationBusinessException {
		if (DominioSituacaoRequisicaoMaterial.C.equals(sceReqMateriais.getIndSituacao())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			sceReqMateriais.setServidorConfirmado(servidorLogado);
			sceReqMateriais.setDtConfirmacao(new Date());
		}
	}

	/**
	 * RN3 Valida número CUM de SceReqMateriais através do servidor confirmado
	 * 
	 * @param sceReqMateriais
	 */
	public void validarSceReqMateriaisNumeroCUM(SceReqMaterial sceReqMateriais) throws ApplicationBusinessException {

		if (sceReqMateriais.getRmrPaciente() != null) {

			RapServidores rapServidor = sceReqMateriais.getRmrPaciente().getServidor();

			if (rapServidor == null) {
				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.NUMERO_CUM_NAO_ENCONTRADA);
			} else {
				sceReqMateriais.setServidor(rapServidor);
			}
		}
	}

	/**
	 * RN4 Atualiza o tipo de movimento do SceReqMateriais
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	public void atualizarSceReqMateriaisTipoMovimento(SceReqMaterial sceReqMateriais) throws BaseException {

		final AghParametros parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TMV_DOC_RM);

		if (parametro != null) {
			SceTipoMovimento sceTipoMovimentos = this.getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(
					parametro.getVlrNumerico().shortValue());
			sceReqMateriais.setTipoMovimento(sceTipoMovimentos);
			sceReqMateriais.setEstorno(false);
		}
	}

	/**
	 * RN5 Verifica se o almoxarifado de SceReqMateriais está cadastrado ou
	 * ativo
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	public void validarSceReqMateriaisAlmoxarifadoAtivo(SceReqMaterial sceReqMateriais) throws BaseException {
		this.getSceAlmoxarifadosRN().verificarAlmoxariadoAtivoPorId(sceReqMateriais.getAlmoxarifado().getSeq());
	}

	/**
	 * ORADB scek_rms_rn.rn_rmsp_ver_inclusao Verifica se o almoxarifado de
	 * SceReqMateriais está cadastrado ou ativo
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	public void validarSceReqMateriaisInclusao(SceReqMaterial sceReqMateriais) throws BaseException {

		// Verifica se a situação do material de requisão é E (Efetivada)
		if (DominioSituacaoRequisicaoMaterial.E.equals(sceReqMateriais.getIndSituacao())) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00317);
		}

		// Verifica se a situação do material de requisão é G (Gerada)
		if (DominioSituacaoRequisicaoMaterial.G.equals(sceReqMateriais.getIndSituacao())) {

			if (sceReqMateriais.getDtEfetivacao() != null || sceReqMateriais.getServidorEfetivado() != null
					|| sceReqMateriais.getDtEstorno() != null || sceReqMateriais.getServidorEstornado() != null
					|| sceReqMateriais.getDtConfirmacao() != null || sceReqMateriais.getServidorConfirmado() != null
					|| sceReqMateriais.getEstorno()) {

				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00382,
						DominioSituacaoRequisicaoMaterial.G.toString());
			}

		}

		//  Verifica se a situação do material de requisão é C (Confirmada)
		if (DominioSituacaoRequisicaoMaterial.C.equals(sceReqMateriais.getIndSituacao())) {

			if (sceReqMateriais.getDtEfetivacao() != null || sceReqMateriais.getServidorEfetivado() != null
					|| sceReqMateriais.getDtEstorno() != null || sceReqMateriais.getServidorEstornado() != null
					|| sceReqMateriais.getEstorno()) {

				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00382,
						DominioSituacaoRequisicaoMaterial.C.toString());
			}

		}

	}

	/*
	 * RNs atualizar
	 */

	/**
	 * ORADB scek_rms_rn.rn_rmsp_ver_alterac Verificar alterações invalidas da
	 * requisição de materiais
	 * 
	 * @param sceReqMateriais
	 * @param sceReqMateriaisOriginal
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	protected void verificarAlteracoesRequisicaoMateriais(SceReqMaterial sceReqMateriais, SceReqMaterial sceReqMateriaisOriginal)
			throws ApplicationBusinessException {
		Boolean isRequisicaoMaterialAutomatica = false;
		if (sceReqMateriaisOriginal.getAutomatica() != null && sceReqMateriaisOriginal.getAutomatica()) {
			isRequisicaoMaterialAutomatica = true;
		}

		Calendar dtReqMaterial = Calendar.getInstance();
		Calendar dtReqMaterialOriginal = Calendar.getInstance();
		if (sceReqMateriais.getDtGeracao() != null) {
			dtReqMaterial.setTime(sceReqMateriais.getDtGeracao());
			dtReqMaterial.set(Calendar.MILLISECOND, 0);
		}

		if (sceReqMateriaisOriginal.getDtGeracao() != null) {
			dtReqMaterialOriginal.setTime(sceReqMateriaisOriginal.getDtGeracao());
			dtReqMaterialOriginal.set(Calendar.MILLISECOND, 0);
		}

		if ((sceReqMateriais.getServidor()!=null && !sceReqMateriais.getServidor().equals(sceReqMateriaisOriginal.getServidor()))
				|| (!(dtReqMaterial.equals(dtReqMaterialOriginal)) && sceReqMateriais.getDtGeracao() != null && sceReqMateriaisOriginal
						.getDtGeracao() != null)) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00307);
		}

		if (sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E)
				&& (!isRequisicaoMaterialAutomatica && (sceReqMateriaisOriginal.getIndSituacao()
						.equals(DominioSituacaoRequisicaoMaterial.G) || sceReqMateriaisOriginal.getIndSituacao().equals(
						DominioSituacaoRequisicaoMaterial.A)))) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00308);
		}

		if (sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.A)
				&& sceReqMateriaisOriginal.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E)) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00316);
		}

		if (!sceReqMateriaisOriginal.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E) && sceReqMateriais.getEstorno()) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00309);
		}

		if ((sceReqMateriais.getServidorConfirmado() != null && !sceReqMateriais.getServidorConfirmado().equals(
				sceReqMateriaisOriginal.getServidorConfirmado()))
				|| (sceReqMateriais.getDtConfirmacao() != null && !DateUtil.isDatasIguais(
						DateUtil.truncaData(sceReqMateriais.getDtConfirmacao()),
						DateUtil.truncaData(sceReqMateriaisOriginal.getDtConfirmacao())))) {

			if (!sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C)) {
				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00311);
			}
		}

		if ((sceReqMateriais.getServidorCancelado() != null && !sceReqMateriais.getServidorCancelado().equals(
				sceReqMateriaisOriginal.getServidorCancelado()))
				|| (sceReqMateriais.getDtCancelamento() != null && !DateUtil.isDatasIguais(
						DateUtil.truncaData(sceReqMateriais.getDtCancelamento()),
						DateUtil.truncaData(sceReqMateriaisOriginal.getDtCancelamento())))) {
			if (!sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.A)) {
				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00796);
			}
		}

		if ((sceReqMateriais.getServidorEfetivado() != null && !sceReqMateriais.getServidorEfetivado().equals(
				sceReqMateriaisOriginal.getServidorEfetivado()))
				|| (sceReqMateriais.getDtEfetivacao() != null && !DateUtil.isDatasIguais(
						DateUtil.truncaData(sceReqMateriais.getDtEfetivacao()),
						DateUtil.truncaData(sceReqMateriaisOriginal.getDtEfetivacao())))) {
			if (!sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E)) {
				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00312);
			}
		}

		if ((sceReqMateriais.getServidorEstornado() != null && !sceReqMateriais.getServidorEstornado().equals(
				sceReqMateriaisOriginal.getServidorEstornado()))
				|| (sceReqMateriais.getDtEstorno() != null && !DateUtil.isDatasIguais(DateUtil.truncaData(sceReqMateriais.getDtEstorno()),
						DateUtil.truncaData(sceReqMateriaisOriginal.getDtEstorno())))) {
			if (!sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.E)) {
				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00313);
			}
			if (!sceReqMateriais.getEstorno()) {
				throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00314);
			}
		}

		if ((sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G) || sceReqMateriais.getIndSituacao().equals(
				DominioSituacaoRequisicaoMaterial.A))
				&& sceReqMateriaisOriginal.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C)) {
			sceReqMateriais.setServidorConfirmado(null);
			sceReqMateriais.setDtConfirmacao(null);
		}

		if (!sceReqMateriaisOriginal.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G)
				|| !sceReqMateriais.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G)) {
			if (sceReqMateriais.getAlmoxarifado()!=null && !sceReqMateriais.getAlmoxarifado().equals(sceReqMateriaisOriginal.getAlmoxarifado())
					|| (sceReqMateriais.getGrupoMaterial() != null && !sceReqMateriais.getGrupoMaterial().equals(
							sceReqMateriaisOriginal.getGrupoMaterial()))
					|| (sceReqMateriais.getCentroCusto()!=null && !sceReqMateriais.getCentroCusto().equals(sceReqMateriaisOriginal.getCentroCusto()))
					|| (sceReqMateriais.getCentroCustoAplica()!=null&&!sceReqMateriais.getCentroCustoAplica().equals(sceReqMateriaisOriginal.getCentroCustoAplica()))
					|| (sceReqMateriais.getTipoMovimento()!=null && !sceReqMateriais.getTipoMovimento().equals(sceReqMateriaisOriginal.getTipoMovimento()))) {

				if (!habilitarEdicao(sceReqMateriais, getServidorLogadoFacade().obterServidorLogado())) {
					throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00315);
				}

			}
		}

	}

	public Boolean habilitarEdicao(SceReqMaterial rm, RapServidores servidorLogado) {
		Boolean ret = Boolean.FALSE;

		FccCentroCustos ccAtuacao = getCentroCustoFacade().obterCentroCustosPorCodigoCentroCustoAtuacaoOuLotacao();
		Boolean isAlmoxarife = getCascaFacade().usuarioTemPerfil(servidorLogado.getUsuario(), "ADM29");

		// se eh o usuario solicitante...
		if (Objects.equals(rm.getServidor(), servidorLogado) || Objects.equals(rm.getCentroCusto(), ccAtuacao)) {

			if (rm.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C)) {
				ret = true;
			}
		}

		// se eh o almoxarife
		if (Objects.equals(rm.getAlmoxarifado().getCentroCusto(), ccAtuacao) && isAlmoxarife) {
			if (rm.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.G)
					|| rm.getIndSituacao().equals(DominioSituacaoRequisicaoMaterial.C)) {
				ret = true;
			}
		}
		return ret;
	}

	/**
	 * TODO: Implementar V2 ORADB RN_RMSP_GERA_RM_FORN *
	 * 
	 * @param sceReqMateriais
	 */
	private void gerarRequisicaoMaterialFornecedor(SceReqMaterial sceReqMateriais) {
		// TODO Auto-generated method stub

	}

	/**
	 * ORADB scek_rms_rn.RN_RMSP_ATU_RMS_FORN Atualiza a requisicao a estornada.
	 * 
	 * @param sceReqMateriais
	 * @throws BaseException
	 */
	private void atualizarRequisicaoMateriaisFornecedor(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		List<SceReqMaterial> reqs = getSceReqMateriaisDAO().pesquisaRequisicoesMateriaisPorRequisicaoMaterial(sceReqMateriais);
		for (SceReqMaterial req : reqs) {
			if (!req.getEstorno()) {
				req.setEstorno(Boolean.TRUE);
				atualizar(req, nomeMicrocomputador);
			}
		}
	}

	/**
	 * ORADB scek_rms_rn.rn_rmsp_atu_efetiva Se a situação do SceReqMateriais
	 * for igual a E (Confirmada):
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	private void atualizarEfetivacao(SceReqMaterial sceReqMateriais) throws ApplicationBusinessException {

		if (DominioSituacaoRequisicaoMaterial.E.equals(sceReqMateriais.getIndSituacao())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			sceReqMateriais.setServidorEfetivado(servidorLogado);
			sceReqMateriais.setDtEfetivacao(new Date());
			getSceTipoMovimentosRN().verificarTipoMovimentoAtivo(sceReqMateriais.getTipoMovimento().getId().getSeq());

		}

	}

	/**
	 * ORADB scek_rms_rn.rn_rmsp_atu_cancela Se a situação do SceReqMateriais
	 * for igual a A (Cancelada):
	 * 
	 * @param sceReqMateriais
	 * @throws ApplicationBusinessException
	 */
	private void atualizarCancelamento(SceReqMaterial sceReqMateriais) throws ApplicationBusinessException {
		if (DominioSituacaoRequisicaoMaterial.A.equals(sceReqMateriais.getIndSituacao())) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			sceReqMateriais.setServidorCancelado(servidorLogado);
			sceReqMateriais.setDtCancelamento(new Date());
		}
	}

	/**
	 * ORADB scek_rms_rn.rn_rmsp_ver_estorno Verifica se requisição já está
	 * estornada.
	 * 
	 * @param sceReqMateriaisOriginal
	 * @throws ApplicationBusinessException
	 */
	protected void verificarRequisicaoEstornada(SceReqMaterial sceReqMateriaisOriginal) throws ApplicationBusinessException {
		if (sceReqMateriaisOriginal.getEstorno()) {
			throw new ApplicationBusinessException(SceReqMateriaisRNExceptionCode.SCE_00887);
		}
	}

	/**
	 * Busca o parametro para efetuar a pesquisa por materiais
	 * 
	 * @param almoxSeq
	 * @param paramPesq
	 * @param usaFornNaPesquisa
	 * @return
	 * 
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueMaterialPorAlmoxarifadoCodigoGrupoMaterial(Short almoxSeq, Object paramPesq,
			List<Integer> listaGrupos, boolean usaFornNaPesquisa, Boolean somenteEstocaveis, Boolean somenteDiretos)
			throws ApplicationBusinessException {

		AghParametros parametroFornecedor = null;
		Integer numeroFornecedor = null;
		if (usaFornNaPesquisa) {
			parametroFornecedor = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
			if (parametroFornecedor.getVlrNumerico() != null) {
				numeroFornecedor = parametroFornecedor.getVlrNumerico().intValue();
			}
		}

		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueMaterialPorAlmoxarifadoCodigoGrupoMaterial(almoxSeq, numeroFornecedor,
				listaGrupos, paramPesq, somenteEstocaveis, somenteDiretos);
	}

	public List<SceEstoqueAlmoxarifado> pesquisarEstoqueAlmoxarifadoPorPacote(Integer codigoGrupo, ScePacoteMateriaisId pacote) {
		return getSceEstoqueAlmoxarifadoDAO().pesquisarEstoqueAlmoxarifadoPorPacote(codigoGrupo, pacote);
	}

	/**
	 * get de RNs e DAOs
	 */

	protected SceReqMateriaisDAO getSceReqMateriaisDAO() {
		return sceReqMateriaisDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected SceItemRmsDAO getSceItemRmsDAO() {
		return sceItemRmsDAO;
	}

	protected SceTipoMovimentosRN getSceTipoMovimentosRN() {
		return sceTipoMovimentosRN;
	}

	protected SceAlmoxarifadosRN getSceAlmoxarifadosRN() {
		return sceAlmoxarifadosRN;
	}

	protected SceMovimentoMaterialRN getSceMovimentoMaterialRN() {
		return sceMovimentoMaterialRN;
	}

	protected SceItemRmsRN getSceItemRmsRN() {
		return sceItemRmsRN;
	}

	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return this.centroCustoFacade;
	}

	protected ICascaFacade getCascaFacade() {
		return this.cascaFacade;
	}

}