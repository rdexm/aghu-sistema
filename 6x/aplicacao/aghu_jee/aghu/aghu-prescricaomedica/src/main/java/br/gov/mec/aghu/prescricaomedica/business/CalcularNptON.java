package br.gov.mec.aghu.prescricaomedica.business;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioTipoCalculoNpt;
import br.gov.mec.aghu.dominio.DominioTipoComponenteNpt;
import br.gov.mec.aghu.dominio.DominioTipoLipidio;
import br.gov.mec.aghu.dominio.DominioTipoVolume;
import br.gov.mec.aghu.farmacia.dao.AfaDecimalComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaParamComponenteNptDAO;
import br.gov.mec.aghu.farmacia.dao.AfaTipoVelocAdministracoesDAO;
import br.gov.mec.aghu.model.AfaDecimalComponenteNpt;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AfaMensCalculoNpt;
import br.gov.mec.aghu.model.AfaParamComponenteNpt;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmParamCalculoPrescricao;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.prescricaomedica.dao.AfaFormulaNptPadraoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.AfaMensCalculoNptDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmParamCalculoPrescricaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmUnidadeMedidaMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaParamComponenteNptsVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaParamNptGlicose50VO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaParamNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.CalculoAdultoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.CalculoParametrosFixosVO;
import br.gov.mec.aghu.prescricaomedica.vo.DadosPesoAlturaVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmVerificaParamCalculoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * #989 - Calcular Nutrição Parenteral Total: ON
 * 
 * @author aghu
 *
 */
@Stateless
@SuppressWarnings({"PMD.ExcessiveClassLength","PMD.NPathComplexity"})
public class CalcularNptON extends BaseBusiness {

	private static final long serialVersionUID = -3223350423987469999L;

	private static final String KG_DIA = "/kg/dia";
	private static final String M2_DIA = "/m²/dia";
	private static final String ML_DIA = "/ml/dia";
	private static final String MG_KG_DIA = "/mg/kg/dia";

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private CalcularNptON2 calcularNptON2;

	@Inject
	private AfaFormulaNptPadraoDAO afaFormulaNptPadraoDAO;

	@Inject
	private AfaDecimalComponenteNptDAO afaDecimalComponenteNptDAO;

	@Inject
	private AfaParamComponenteNptDAO afaParamComponenteNptDAO;

	@Inject
	private AfaTipoVelocAdministracoesDAO afaTipoVelocAdministracoesDAO;

	@Inject
	private MpmUnidadeMedidaMedicaDAO mpmUnidadeMedidaMedicaDAO;

	@Inject
	private MpmParamCalculoPrescricaoDAO mpmParamCalculoPrescricaoDAO;

	@Inject
	private AfaMensCalculoNptDAO afaMensCalculoNptDAO;

	@EJB
	private CalcularNptRN calcularNptRN;

	private static final Log LOG = LogFactory.getLog(CalcularNptON.class);

	protected enum CalcularNptONExceptionCode implements BusinessExceptionCode {
		NPT_INFORMAR_FORMULA_PARA_CALCULO, MPM_03448, MPM_03442, MPM_03478, MPM_03479, MPM_03480, MPM_03481, MPM_03482, MPM_03483, MPM_03484, MPM_03485, MPM_03486, MPM_03487, MPM_03488, MPM_03489, MPM_03503, MPM_03504, MPM_03490, MPM_03491, MPM_03492, MPM_03493, MPM_03494, MPM_03495, MPM_03496, MPM_03497, MPM_03498, MPM_03499, MPM_03500, MPM_03501, MPM_03502, MPM_03505, MPM_03506, MPM_03507, MPM_03508, MPM_03509, MPM_03410, MPM_03411, MPM_03741, MPM_03742, MPM_03744, MPM_03745, MPM_03743, MPM_03412, MPM_NPT_PENDENCIA_CALCULO_TITULO, MPM_NPT_PENDENCIA_CALCULO_MENSAGEM, NPT_CALCULO_ADULTO_SUCESSO, NPT_CALCULO_ADULTO_ERRO, ERRO_LEITURA_VO;
	}

	public String verificarCalculoNpt(MpmPrescricaoNptVO prescricaoNptVO, final boolean indPacPediatrico) throws ApplicationBusinessException {

		// RN01
		if (prescricaoNptVO.getSeq() == null) {
			throw new ApplicationBusinessException(CalcularNptONExceptionCode.NPT_INFORMAR_FORMULA_PARA_CALCULO);
		}

		// RN02
		AfaFormulaNptPadrao afaFormulaNptPadrao = this.afaFormulaNptPadraoDAO.obterPorChavePrimaria(prescricaoNptVO.getFnpSeq());
		if (afaFormulaNptPadrao != null && StringUtils.equalsIgnoreCase(afaFormulaNptPadrao.getIndPadrao(), "S")) {
			throw new ApplicationBusinessException(CalcularNptONExceptionCode.MPM_03448);
		}

		// RN03
		// TODO: redirecionar p/ os xhtmls
		if (indPacPediatrico) {
			return "#1103";
		} else {
			return "#989";
		}
	}

	/**
	 * Se retornar true, chama RN05 (modal "Dados Peso/Altura"), senão, chama RN06
	 * 
	 * @param prescricaoNptVO
	 * @return
	 */
	public boolean chamarModalPesoAltura(MpmPrescricaoNptVO prescricaoNptVO) {
		// RN04
		List<MpmParamCalculoPrescricao> listMpmParamCalculoPrescr = this.mpmParamCalculoPrescricaoDAO.pesquisarMoverParametroCalculo(prescricaoNptVO.getAtdSeq());

		if (listMpmParamCalculoPrescr != null && !listMpmParamCalculoPrescr.isEmpty()) {

			for (MpmParamCalculoPrescricao mpmParamCalculoPrescricao : listMpmParamCalculoPrescr) {

				if (mpmParamCalculoPrescricao.getPepPacCodigo() != null) {
					return false;
				}
			}
			return true;

		} else {
			return true;
		}
	}

	/**
	 * Iniciar Cálculo Adulto NPT
	 * 
	 * @param prescricaoNptVo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public CalculoAdultoNptVO iniciarCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo) throws ApplicationBusinessException {
		
		if(prescricaoNptVo == null || prescricaoNptVo.getAtdSeq() == null){
			throw new IllegalArgumentException();
		}
		
		final CalculoAdultoNptVO calculoAdultoNptVO = new CalculoAdultoNptVO();

		// RN06
		calculoAdultoNptVO.setDadosPesoAlturaVO(this.verificarParametroCalculo(prescricaoNptVo.getAtdSeq()));

		// RN07
		this.verificarPesoPaciente(calculoAdultoNptVO.getDadosPesoAlturaVO());

		// RN08
		calculoAdultoNptVO.setCalculoParametrosFixosVO(this.mpmpCargaCalculoNptParte1(calculoAdultoNptVO.getDadosPesoAlturaVO()));
		calculoAdultoNptVO.setAfaParamComponenteNptsVO(this.mpmpCargaCalculoNptParte2(calculoAdultoNptVO.getDadosPesoAlturaVO()));

		// RN09 - PC4 – MPMP_PARAM_BASE_CALCULO
		this.obterParametrosBaseCalculo(calculoAdultoNptVO);

		if (Boolean.FALSE.equals(prescricaoNptVo.getIndPacPediatrico())) { // Observação: As regras de RN01 a RN09 também deve ser executadas na estória #1103. Não duplicar regras em comum.
			// RN10 - PC05
			obterUnidadesMedida(calculoAdultoNptVO);

			// RN11 e RN12 (PC07)
			this.popularDadosNptAdulto(prescricaoNptVo, calculoAdultoNptVO);

			// RN13
			this.verificarCalculoPesoPaciente(calculoAdultoNptVO);

			// RN14
			this.copiarValoresParametrosFixos(calculoAdultoNptVO);

			// RN15
			this.calcularNptON2.mpmCalculaComponentesNptParte1(DominioTipoComponenteNpt.RECALCULA, calculoAdultoNptVO);

		}

		return calculoAdultoNptVO;
	}

	/**
	 * @ORADB MPMP_CARGA_CALCULO_NPT - Parte 1 - Carrega as informações de CalculoParametrosFixosVO
	 * 
	 * @param dadosPesoAlturaVO
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public CalculoParametrosFixosVO mpmpCargaCalculoNptParte1(DadosPesoAlturaVO dadosPesoAlturaVO) throws ApplicationBusinessException {

		AghParametros pLimiteCalcPeso = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_LIMITE_CALC_PESO);
		AghParametros pVelocAdmMlH = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_VELOC_ADM_ML_H);
		AghParametros pComposicaoSolNpt = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMPOSICAO_SOL_NPT);
		AghParametros pComposicaoSolLipidios = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMPOSICAO_SOL_LIPIDIOS);

		CalculoParametrosFixosVO calculoParametrosFixosVO = new CalculoParametrosFixosVO();

		calculoParametrosFixosVO.setParamCalcDifUsadoPac(Boolean.FALSE);
		calculoParametrosFixosVO.setParamTrocaUnidade(Boolean.FALSE);
		calculoParametrosFixosVO.setLimiteCalculoPeso(pLimiteCalcPeso.getVlrNumerico().intValue());

		if (dadosPesoAlturaVO.getPeso().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) > 0) {
			calculoParametrosFixosVO.setTipoCalculoPacAgora(DominioTipoVolume.M);
		} else {
			calculoParametrosFixosVO.setTipoCalculoPacAgora(DominioTipoVolume.K);
		}

		if (calculoParametrosFixosVO.getSeqVelocAdmMlH() == null) {
			calculoParametrosFixosVO.setSeqVelocAdmMlH(pVelocAdmMlH.getVlrNumerico().intValue());

			AfaTipoVelocAdministracoes afaTipoVelocAdm = this.afaTipoVelocAdministracoesDAO.obterPorChavePrimaria(calculoParametrosFixosVO.getSeqVelocAdmMlH());
			calculoParametrosFixosVO.setDescVelocAdmMlH(afaTipoVelocAdm.getDescricao());
		}

		calculoParametrosFixosVO.setComposicaoSolNpt(pComposicaoSolNpt.getVlrNumerico().intValue());
		calculoParametrosFixosVO.setComposicaoSolLipidios(pComposicaoSolLipidios.getVlrNumerico().intValue());

		return calculoParametrosFixosVO;
	}

	/**
	 * @ORADB MPMP_CARGA_CALCULO_NPT - Parte 2 - Carrega a lista de AfaParamComponenteNptsVO
	 * 
	 * @param dadosPesoAlturaVO
	 * @return
	 */
	public AfaParamComponenteNptsVO mpmpCargaCalculoNptParte2(DadosPesoAlturaVO dadosPesoAlturaVO) {

		AfaParamComponenteNptsVO afaParamComponenteNptsVO = new AfaParamComponenteNptsVO();

		List<AfaParamComponenteNpt> curComp = this.afaParamComponenteNptDAO.listarAfaParamComponenteNtpsCalculoNptAtivos();

		if (curComp != null && !curComp.isEmpty()) {

			for (AfaParamComponenteNpt afaParamComponenteNpt : curComp) {
				MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica = this.mpmUnidadeMedidaMedicaDAO.obterPorChavePrimaria(afaParamComponenteNpt.getUmmSeq());

				switch (afaParamComponenteNpt.getAfaComponenteNpt().getIdentifComponente()) {
				case LIPIDIOS_10:
					AfaParamNptVO afaParam1 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptLipidio10VO(afaParam1);
					break;
				case LIPIDIOS_20:
					AfaParamNptVO afaParam2 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptLipidio20VO(afaParam2);
					break;
				case AMINOACIDOS_AD:
					AfaParamNptVO afaParam3 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParam3.setConvUnidNitrogenio(afaParamComponenteNpt.getFatorConvUnidNitrogenio());
					afaParamComponenteNptsVO.setAfaParamNptAminoacidoAdVO(afaParam3);
					break;
				case GLICOSE_50:
					AfaParamNptGlicose50VO afaParam4 = new AfaParamNptGlicose50VO();
					afaParam4.setUmmSeq(afaParamComponenteNpt.getUmmSeq());
					afaParam4.setUmmDescricao(mpmUnidadeMedidaMedica.getDescricao());
					afaParam4.setConvMl(afaParamComponenteNpt.getFatorConversaoMl());
					afaParam4.setConvCalorias(afaParamComponenteNpt.getFatorConvUnidCalorias());
					afaParam4.setConvMlMosm(afaParamComponenteNpt.getFatorConvMlMosm());
					afaParam4.setTipoParamCalculo(this.converterTipoCalculoNptEmTipoVolume(afaParamComponenteNpt.getTipoParamCalculo()));
					afaParam4.setVolMaximo(afaParamComponenteNpt.getVolumeMaximoMl());
					afaParam4.setTipoCaloria(afaParamComponenteNpt.getTipoCaloria());
					afaParam4.setNroCasasDecimais(this.obterCadasDecimais(afaParamComponenteNpt.getId().getCnpMedMatCodigo(), dadosPesoAlturaVO.getPeso()).intValue());
					afaParam4.setPcnSeqp(afaParamComponenteNpt.getId().getSeqp().intValue());
					// --
					afaParam4.setTigUmmSeq(afaParamComponenteNpt.getUmmSeq());
					afaParam4.setTigUmmDescricao("mg");
					afaParam4.setTigConvMl(afaParamComponenteNpt.getFatorConversaoMl());
					afaParam4.setTigConvCalorias(afaParamComponenteNpt.getFatorConvUnidCalorias());
					afaParam4.setTigConvMlMosm(afaParamComponenteNpt.getFatorConvMlMosm());
					afaParam4.setTigTipoParamCalculo(this.converterTipoCalculoNptEmTipoVolume(afaParamComponenteNpt.getTipoParamCalculo()));
					afaParam4.setTigVolMaximo(afaParamComponenteNpt.getVolumeMaximoMl());
					afaParam4.setTigTipoCaloria(afaParamComponenteNpt.getTipoCaloria());
					afaParam4.setTigNroCasasDecimais(this.obterCadasDecimais(afaParamComponenteNpt.getId().getCnpMedMatCodigo(), dadosPesoAlturaVO.getPeso()).intValue());
					afaParam4.setTigPcnSeqp(afaParamComponenteNpt.getId().getSeqp().intValue());
					afaParamComponenteNptsVO.setAfaParamNptGlicose50VO(afaParam4);
					break;
				case GLICOSE_10:
					AfaParamNptVO afaParam5 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptGlicose10VO(afaParam5);
					break;
				case HEPARINA:
					AfaParamNptVO afaParam6 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptHeparinaVO(afaParam6);
					break;
				case CLORETO_SODIO:
					AfaParamNptVO afaParam7 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptCloretoSodioVO(afaParam7);
					break;
				case GLUCO_CALCIO:
					AfaParamNptVO afaParam8 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptGlucoCalcioVO(afaParam8);
					break;
				case SULFATO_MAGNESIO:
					AfaParamNptVO afaParam9 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptSulfatoMagnesioVO(afaParam9);
					break;
				case CLORETO_POTASSIO:
					AfaParamNptVO afaParam10 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptCloretoPotassioVO(afaParam10);
					break;
				case FOSFATO_POTASSIO:
					AfaParamNptVO afaParam11 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParam11.setConvMlFosforo(afaParamComponenteNpt.getFatorConvMlFosforo());
					afaParamComponenteNptsVO.setAfaParamNptFosfatoPotassioVO(afaParam11);
					break;
				case AMINOACIDOS_PED:
					AfaParamNptVO afaParam12 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParam12.setConvUnidNitrogenio(afaParamComponenteNpt.getFatorConvUnidNitrogenio());
					afaParamComponenteNptsVO.setAfaParamNptAminoacidoPedVO(afaParam12);
					break;
				case ACETATO_ZINCO:
					AfaParamNptVO afaParam13 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptAcetatoZincoVO(afaParam13);
					break;
				case GLICOSE_5:
					AfaParamNptVO afaParam14 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptGlicose5VO(afaParam14);
					break;
				case OLIGOELEMENTOS_AD:
					AfaParamNptVO afaParam15 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptOligoelementosAdVO(afaParam15);
					break;
				case OLIGOELEMENTOS_PED:
					AfaParamNptVO afaParam16 = this.popularAfaParamNpt(dadosPesoAlturaVO, afaParamComponenteNpt, mpmUnidadeMedidaMedica);
					afaParamComponenteNptsVO.setAfaParamNptOligoelementosPedVO(afaParam16);
					break;
				default:
					break;
				}
			}
		}
		return afaParamComponenteNptsVO;
	}

	/**
	 * Popula mpmp_carga_calculo_npt
	 * 
	 * @param dadosPesoAlturaVO
	 * @param afaParamComponenteNpt
	 * @param mpmUnidadeMedidaMedica
	 * @return
	 */
	private AfaParamNptVO popularAfaParamNpt(DadosPesoAlturaVO dadosPesoAlturaVO, AfaParamComponenteNpt afaParamComponenteNpt, MpmUnidadeMedidaMedica mpmUnidadeMedidaMedica) {

		AfaParamNptVO afaParam = new AfaParamNptVO();

		afaParam.setUmmSeq(afaParamComponenteNpt.getUmmSeq());
		afaParam.setUmmDescricao(mpmUnidadeMedidaMedica.getDescricao());
		afaParam.setConvMl(afaParamComponenteNpt.getFatorConversaoMl());
		afaParam.setConvCalorias(afaParamComponenteNpt.getFatorConvUnidCalorias());
		afaParam.setConvMlMosm(afaParamComponenteNpt.getFatorConvMlMosm());
		afaParam.setTipoParamCalculo(this.converterTipoCalculoNptEmTipoVolume(afaParamComponenteNpt.getTipoParamCalculo()));
		afaParam.setVolMaximo(afaParamComponenteNpt.getVolumeMaximoMl());
		afaParam.setTipoCaloria(afaParamComponenteNpt.getTipoCaloria());
		afaParam.setNroCasasDecimais(this.obterCadasDecimais(afaParamComponenteNpt.getId().getCnpMedMatCodigo(), dadosPesoAlturaVO.getPeso()).intValue());
		afaParam.setPcnSeqp(afaParamComponenteNpt.getId().getSeqp().intValue());

		return afaParam;
	}

	/**
	 * Converte volume
	 * @param tipoCalculoNpt
	 * @return
	 */
	private DominioTipoVolume converterTipoCalculoNptEmTipoVolume(DominioTipoCalculoNpt tipoCalculoNpt) {
		if (DominioTipoCalculoNpt.K.equals(tipoCalculoNpt)) {
			return DominioTipoVolume.K;
		} else if (DominioTipoCalculoNpt.M.equals(tipoCalculoNpt)) {
			return DominioTipoVolume.M;
		} else if (DominioTipoCalculoNpt.A.equals(tipoCalculoNpt)) {
			return DominioTipoVolume.A;
		} else if (DominioTipoCalculoNpt.V.equals(tipoCalculoNpt)) {
			return DominioTipoVolume.V;
		} else if (DominioTipoCalculoNpt.T.equals(tipoCalculoNpt)) {
			return DominioTipoVolume.T;
		}
		return null;
	}

	/**
	 * @ORADB GET_CASAS_DECIMAIS
	 * 
	 * @param cnpMedMatCodigo
	 * @param peso
	 * @return
	 */
	public Short obterCadasDecimais(Integer cnpMedMatCodigo, Double peso) {

		List<AfaDecimalComponenteNpt> afaComponenteNpt = this.afaDecimalComponenteNptDAO.obterAfaDecimalComponenteNptPorCodMatPeso(cnpMedMatCodigo, peso);

		if (afaComponenteNpt != null && !afaComponenteNpt.isEmpty()) {
			return afaComponenteNpt.get(0).getNroCasasDecimais();
		} else {
			return 1;
		}
	}

	/**
	 * @ORADB MPMP_VERIF_PARAM_CALCULO
	 * 
	 * @param atdSeq
	 * @return
	 */
	public DadosPesoAlturaVO verificarParametroCalculo(final Integer atdSeq) {

		DadosPesoAlturaVO dadosPesoAlturaVO = new DadosPesoAlturaVO();

		if (dadosPesoAlturaVO.getPeso() == null) {
			MpmVerificaParamCalculoVO verificaParamCalculoVO = this.mpmParamCalculoPrescricaoDAO.obterParametrosCalculo(atdSeq);

			if (verificaParamCalculoVO != null) {
				dadosPesoAlturaVO.setPeso(verificaParamCalculoVO.getPeso().doubleValue());
				dadosPesoAlturaVO.setAltura(verificaParamCalculoVO.getAltura());
				dadosPesoAlturaVO.setSc(verificaParamCalculoVO.getSc().doubleValue());
				dadosPesoAlturaVO.setPcaCriadoEm(verificaParamCalculoVO.getCriadoEm());
			}
		}
		return dadosPesoAlturaVO;
	}

	/**
	 * RN07: Verificar peso do Paciente
	 * 
	 * @param dadosPesoAlturaVO
	 * @throws ApplicationBusinessException
	 */
	public void verificarPesoPaciente(DadosPesoAlturaVO dadosPesoAlturaVO) throws ApplicationBusinessException {
		if (dadosPesoAlturaVO.getPeso() == null) {
			throw new ApplicationBusinessException(CalcularNptONExceptionCode.MPM_03442);
		}
	}

	/**
	 * @ORADB: MPMP_POPULA_DADOS_A
	 * 
	 * @param prescricaoNptVo
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 */
	public void popularDadosNptAdulto(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException {

		String percent = " %";

		if (calculoAdultoNptVO.getCalculoParametrosFixosVO().getTipoCalculoPacAgora() != prescricaoNptVo.getTipoParamVolume()) {
			calculoAdultoNptVO.setParamVolDes(BigDecimal.ZERO);
			calculoAdultoNptVO.setTotParamVolDes(BigDecimal.ZERO);
			calculoAdultoNptVO.setTotParamVolCalc(BigDecimal.ZERO);
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setTipoParamCalculoVol(null);
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamCalcDifUsadoPac(Boolean.TRUE);
		} else {
			calculoAdultoNptVO.setTotParamVolDes(prescricaoNptVo.getParamVolumeMl() == null ? null : BigDecimal.valueOf(prescricaoNptVo.getParamVolumeMl()));
			calculoAdultoNptVO.setTotParamVolDes(prescricaoNptVo.getVolumeDesejado() == null ? null : BigDecimal.valueOf(prescricaoNptVo.getVolumeDesejado()));
			calculoAdultoNptVO.setTotParamVolCalc(prescricaoNptVo.getVolumeCalculado() == null ? null : BigDecimal.valueOf(prescricaoNptVo.getVolumeCalculado()));
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setTipoParamCalculoVol(prescricaoNptVo.getTipoParamVolume());
		}

		calculoAdultoNptVO.setParamTipoLip(prescricaoNptVo.getParamTipoLipidio());
		calculoAdultoNptVO.setParamTempInfusaoSol(prescricaoNptVo.getTempoHInfusaoSolucao());
		calculoAdultoNptVO.setParamTempInfusaoLip(prescricaoNptVo.getTempoHInfusaoLipidios());

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getCaloriasDia(), calculoAdultoNptVO.getCaloriasDia(), calculoAdultoNptVO.getCaloriasDiaEd(), null);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getCaloriasKgDia(), calculoAdultoNptVO.getCaloriasKgDia(), calculoAdultoNptVO.getCaloriasKgDiaEd(), null);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getRelCalNProtNitrogenio(), calculoAdultoNptVO.getRelCalGNitro(), calculoAdultoNptVO.getRelCalGNitroEd(), null);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getPercCalAminoacidos(), calculoAdultoNptVO.getPercCalAmin(), calculoAdultoNptVO.getPercCalAminEd(), percent);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getPercCalLipidios(), calculoAdultoNptVO.getPercCalLipidios(), calculoAdultoNptVO.getPercCalLipidiosEd(), percent);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getPercCalGlicose(), calculoAdultoNptVO.getPercCalGlicose(), calculoAdultoNptVO.getPercCalGlicoseEd(), percent);

		calculoAdultoNptVO.setRelGlicoseLipiL(prescricaoNptVo.getLipidiosRelGlicLipid() == null ? null : BigDecimal.valueOf(prescricaoNptVo.getLipidiosRelGlicLipid()));

		this.popularDadosNptAdultoParte2(prescricaoNptVo, calculoAdultoNptVO);
	}

	/**
	 * 
	 * @param prescricaoNptVo
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 */
	private void popularDadosNptAdultoParte2(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException {
		String percent = " %";
		String gKgH = " g/kg/h";
		String m0smL = " mOsm/L";

		calculoAdultoNptVO.setRelGlicoseLipiG(prescricaoNptVo.getGlicoseRelGlicLipid() == null ? null : BigDecimal.valueOf(prescricaoNptVo.getGlicoseRelGlicLipid()));

		if (prescricaoNptVo.getGlicoseRelGlicLipid() != null) {
			calculoAdultoNptVO.setRelGlicoseLipiG(BigDecimal.valueOf(prescricaoNptVo.getGlicoseRelGlicLipid()));
		}

		calculoAdultoNptVO.setRelGlicoseLipiEd(prescricaoNptVo.getGlicoseRelGlicLipid() == null ? "" : prescricaoNptVo.getGlicoseRelGlicLipid().toString() + ":" + prescricaoNptVo.getLipidiosRelGlicLipid() == null ? "" : prescricaoNptVo.getLipidiosRelGlicLipid().toString());

		if (prescricaoNptVo.getRelacaoCalcioFosforo() != null) {
			calculoAdultoNptVO.setRelCalcioFosforo(BigDecimal.valueOf(prescricaoNptVo.getRelacaoCalcioFosforo()));
			calculoAdultoNptVO.setRelCalcioFosforoEd("1:" + prescricaoNptVo.getRelacaoCalcioFosforo().toString());
		}

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getConcGlicSemLipidios(), calculoAdultoNptVO.getConcGlicoseSemLipi(), calculoAdultoNptVO.getConcGlicoseSemLipiEd(), percent);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getConcGlicSemLipidios(), calculoAdultoNptVO.getConcGlicoseSemLipi(), calculoAdultoNptVO.getConcGlicoseSemLipiEd(), percent);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getConcGlicComLipidios(), calculoAdultoNptVO.getConcGlicoseComLipi(), calculoAdultoNptVO.getConcGlicoseComLipiEd(), percent);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getTaxaInfusaoLipidios(), calculoAdultoNptVO.getTaxaInfusaoLipi(), calculoAdultoNptVO.getTaxaInfusaoLipiEd(), gKgH);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getOsmolaridadeSemLipidios(), calculoAdultoNptVO.getOsmolSemLipi(), calculoAdultoNptVO.getOsmolSemLipiEd(), m0smL);

		this.converterValoresDadosNptAdulto(prescricaoNptVo.getOsmolaridadeComLipidios(), calculoAdultoNptVO.getOsmolComLipi(), calculoAdultoNptVO.getOsmolComLipiEd(), m0smL);

		this.popularDadosNptAdultoParte3(prescricaoNptVo, calculoAdultoNptVO);
	}

	/**
	 * 
	 * @param prescricaoNptVo
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 */
	private void popularDadosNptAdultoParte3(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException {

		AghParametros pComposicaoSolNpt = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMPOSICAO_SOL_NPT);
		AghParametros pComposicaoLipidiosNpt = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_COMPOSICAO_SOL_LIPIDIOS);

		for (MpmComposicaoPrescricaoNptVO cptVO : prescricaoNptVo.getComposicoes()) {
			if (pComposicaoSolNpt.getVlrNumerico().shortValue() == cptVO.getTicSeq()) {
				calculoAdultoNptVO.setGotejoSolucao(cptVO.getVelocidadeAdministracao());
			} else if (pComposicaoLipidiosNpt.getVlrNumerico().shortValue() == cptVO.getTicSeq()) {
				calculoAdultoNptVO.setGotejoLipidios(cptVO.getVelocidadeAdministracao());
			}

			for (MpmItemPrescricaoNptVO itemPrescrNptVO : cptVO.getComponentes()) {

				if (itemPrescrNptVO.getQtdeBaseCalculo() != null) {

					this.atribuirValoresNptAdulto(itemPrescrNptVO, calculoAdultoNptVO);
				}
			}
		}
		if (calculoAdultoNptVO.getParamAmin() == null && calculoAdultoNptVO.getParamLip() == null && calculoAdultoNptVO.getParamTig() == null && calculoAdultoNptVO.getParamHeparina() == null && calculoAdultoNptVO.getParamSodio() == null && calculoAdultoNptVO.getParamPotassio() == null
				&& calculoAdultoNptVO.getParamCalcio() == null && calculoAdultoNptVO.getParamMagnesio() == null) {
			calculoAdultoNptVO.setGotejoSolucao(null);
			calculoAdultoNptVO.setGotejoLipidios(null);
		}
	}

	/**
	 * 
	 * @param itemPrescrNptVO
	 * @param calculoAdultoNptVO
	 */
	private void atribuirValoresNptAdulto(MpmItemPrescricaoNptVO itemPrescrNptVO, CalculoAdultoNptVO calculoAdultoNptVO) {

		if (calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO() != null && DominioTipoVolume.A.equals(calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO().getTipoParamCalculo())
				&& calculoAdultoNptVO.getCalculoParametrosFixosVO().getTipoCalculoPacAgora() != itemPrescrNptVO.getTipoParamCalculo()) {
			this.popularDadosNptAdultoVolumes(calculoAdultoNptVO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, itemPrescrNptVO.getIdentifComponente());
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamCalcDifUsadoPac(Boolean.TRUE);
		} else if (calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO() != null && calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO().getUmmSeq() != itemPrescrNptVO.getUmmSeq()) {
			this.popularDadosNptAdultoVolumes(calculoAdultoNptVO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, itemPrescrNptVO.getIdentifComponente());
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamTrocaUnidade(Boolean.TRUE);
		} else {
			this.popularDadosNptAdultoVolumes(calculoAdultoNptVO, itemPrescrNptVO.getQtdePrescrita(), itemPrescrNptVO.getQtdeBaseCalculo(), itemPrescrNptVO.getTotParamCalculo(), itemPrescrNptVO.getIdentifComponente());
			this.atribuirValoresAdicionais(calculoAdultoNptVO, itemPrescrNptVO);
		}
	}

	/**
	 * 
	 * @param calculoAdultoNptVO
	 * @param itemPrescrNptVO
	 */
	private void atribuirValoresAdicionais(CalculoAdultoNptVO calculoAdultoNptVO, MpmItemPrescricaoNptVO itemPrescrNptVO) {
		if (DominioIdentificacaoComponenteNPT.GLICOSE_50.equals(itemPrescrNptVO.getIdentifComponente())) {
			calculoAdultoNptVO.setParamPercGlic50(itemPrescrNptVO.getPercParamCalculo());

		} else if (DominioIdentificacaoComponenteNPT.GLICOSE_10.equals(itemPrescrNptVO.getIdentifComponente())) {
			calculoAdultoNptVO.setParamPercGlic10(itemPrescrNptVO.getPercParamCalculo());

		} else if (DominioIdentificacaoComponenteNPT.CLORETO_POTASSIO.equals(itemPrescrNptVO.getIdentifComponente())) {
			calculoAdultoNptVO.setParamPercKcl(itemPrescrNptVO.getPercParamCalculo());

		} else if (DominioIdentificacaoComponenteNPT.FOSFATO_POTASSIO.equals(itemPrescrNptVO.getIdentifComponente())) {
			calculoAdultoNptVO.setParamPercK3po4(itemPrescrNptVO.getPercParamCalculo());

		}
	}

	/**
	 * 
	 * @param calculoAdultoNptVO
	 * @param vol
	 * @param param
	 * @param totParam
	 * @param identifComponente
	 */
	private void popularDadosNptAdultoVolumes(CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal vol, BigDecimal param, BigDecimal totParam, DominioIdentificacaoComponenteNPT identifComponente) {

		switch (identifComponente) {

		case AMINOACIDOS_AD:
			calculoAdultoNptVO.setVolAa10(vol);
			calculoAdultoNptVO.setParamAmin(param);
			calculoAdultoNptVO.setTotParamAmin(totParam);
			break;

		case GLICOSE_50:
			calculoAdultoNptVO.setVolGlicose50(vol);
			calculoAdultoNptVO.setParamTig(param);
			calculoAdultoNptVO.setTotParamTig(totParam);
			break;

		case GLICOSE_10:
			calculoAdultoNptVO.setVolGlicose10(vol);
			calculoAdultoNptVO.setParamTig(param);
			calculoAdultoNptVO.setTotParamTig(totParam);
			break;

		case HEPARINA:
			calculoAdultoNptVO.setVolHeparina(vol);
			calculoAdultoNptVO.setParamHeparina(param);
			calculoAdultoNptVO.setTotParamHeparina(totParam);
			break;

		case CLORETO_SODIO:
			calculoAdultoNptVO.setVolNacl20(vol);
			calculoAdultoNptVO.setParamSodio(param);
			calculoAdultoNptVO.setTotParamSodio(totParam);
			break;

		case CLORETO_POTASSIO:
			calculoAdultoNptVO.setVolKcl(vol);
			calculoAdultoNptVO.setParamPotassio(param);
			calculoAdultoNptVO.setTotParamPotassio(totParam);
			break;

		case FOSFATO_POTASSIO:
			calculoAdultoNptVO.setVolK3Po4(vol);
			calculoAdultoNptVO.setParamPotassio(param);
			calculoAdultoNptVO.setTotParamPotassio(totParam);
			break;

		case SULFATO_MAGNESIO:
			calculoAdultoNptVO.setVolMgso4(vol);
			calculoAdultoNptVO.setParamMagnesio(param);
			calculoAdultoNptVO.setTotParamMagnesio(totParam);
			break;

		case GLUCO_CALCIO:
			calculoAdultoNptVO.setVolGlucoCa(vol);
			calculoAdultoNptVO.setParamCalcio(param);
			calculoAdultoNptVO.setTotParamCalcio(totParam);
			break;

		case ACETATO_ZINCO:
			calculoAdultoNptVO.setVolAcetZn(vol);
			calculoAdultoNptVO.setParamAcetZn(param);
			calculoAdultoNptVO.setTotParamAcetZn(totParam);
			break;

		case LIPIDIOS_20:
			calculoAdultoNptVO.setVolLipidios20(vol);
			calculoAdultoNptVO.setParamLip(param);
			calculoAdultoNptVO.setTotParamLip(totParam);
			break;

		case LIPIDIOS_10:
			calculoAdultoNptVO.setVolLipidios10(vol);
			calculoAdultoNptVO.setParamLip(param);
			calculoAdultoNptVO.setTotParamLip(totParam);
			break;

		default:
			break;
		}
	}

	/**
	 * 
	 * @param valorOriginal
	 * @param novoValor
	 * @param novoValorEd
	 * @param unidade
	 */
	public void converterValoresDadosNptAdulto(final Double valorOriginal, BigDecimal novoValor, String novoValorEd, final String unidade) {
		if (valorOriginal != null) {
			novoValor = BigDecimal.valueOf(valorOriginal);
			novoValorEd = unidade == null ? valorOriginal.toString() : valorOriginal.toString().concat(unidade);
		}
	}

	/**
	 * @ORADB MPMP_PARAM_BASE_CALCULO
	 * 
	 * @param calculoAdultoNptVO
	 */
	public void obterParametrosBaseCalculo(CalculoAdultoNptVO calculoAdultoNptVO) {

		calculoAdultoNptVO.getDadosPesoAlturaVO();
		calculoAdultoNptVO.getCalculoParametrosFixosVO();
		calculoAdultoNptVO.getAfaParamComponenteNptsVO();

		if (calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso().compareTo(calculoAdultoNptVO.getCalculoParametrosFixosVO().getLimiteCalculoPeso().doubleValue()) > 0) {
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setTipoCalculoPacAgora(DominioTipoVolume.M);
		} else {
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setTipoCalculoPacAgora(DominioTipoVolume.K);
		}

		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO());

		AfaParamNptGlicose50VO g50TigVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO();
		if (g50TigVO.getTipoParamCalculo().equals(DominioTipoVolume.A)) {
			g50TigVO.setTpParamCalcUsado(calculoAdultoNptVO.getCalculoParametrosFixosVO().getTipoCalculoPacAgora());
		} else {
			g50TigVO.setTpParamCalcUsado(g50TigVO.getTipoParamCalculo());
		}

		if (g50TigVO.getTigTipoParamCalculo().equals(DominioTipoVolume.A)) {
			g50TigVO.setTigTpParamCalcUsado(calculoAdultoNptVO.getCalculoParametrosFixosVO().getTipoCalculoPacAgora());
		} else {
			g50TigVO.setTigTpParamCalcUsado(g50TigVO.getTigTipoParamCalculo());
		}

		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptSulfatoMagnesioVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO());
		this.verificaParamBaseCalculo(calculoAdultoNptVO.getCalculoParametrosFixosVO(), calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO());
	}

	private void verificaParamBaseCalculo(CalculoParametrosFixosVO calculoParametrosFixosVO, AfaParamNptVO paramNptVO) {
		if (paramNptVO.getTipoParamCalculo().equals(DominioTipoVolume.A)) {
			paramNptVO.setTpParamCalcUsado(calculoParametrosFixosVO.getTipoCalculoPacAgora());
		} else {
			paramNptVO.setTpParamCalcUsado(paramNptVO.getTipoParamCalculo());
		}
	}

	/**
	 * #989 - PC05 - Procedure para definição das unidades de medida para Lipídios, Aminoácidos, Glicose e Heparina
	 * 
	 * @param calculoAdultoNptVO
	 */
	public void obterUnidadesMedida(CalculoAdultoNptVO calculoAdultoNptVO) {

		List<AfaMensCalculoNpt> listaAfaMensCalculoNptDAO = this.afaMensCalculoNptDAO.listarMensCalculoNptAtivos();
		if (!listaAfaMensCalculoNptDAO.isEmpty()) {
			calculoAdultoNptVO.setMensagem(listaAfaMensCalculoNptDAO.get(0).getDescricao());
		}

		AfaParamNptVO lip1VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO();
		calculoAdultoNptVO.setUnidParamLip(this.obterUnidParamPorTipoVolume(lip1VO, lip1VO.getTpParamCalcUsado()));

		AfaParamNptVO aadVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO();
		calculoAdultoNptVO.setUnidParamAmin(this.obterUnidParamPorTipoVolume(aadVO, aadVO.getTpParamCalcUsado()));

		AfaParamNptGlicose50VO g50TigVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO();
		calculoAdultoNptVO.setUnidParamTig(this.obterUnidParamPorTipoVolumeGli50(g50TigVO, g50TigVO.getTigTpParamCalcUsado()));

		AfaParamNptVO hepVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO();
		calculoAdultoNptVO.setUnidParamHeparina(this.obterUnidParamPorTipoVolume(hepVO, hepVO.getTpParamCalcUsado()));
	}

	/**
	 * 
	 * @param paramNptVO
	 * @param tpParamCalcUsado
	 * @return
	 */
	private String obterUnidParamPorTipoVolume(AfaParamNptVO paramNptVO, DominioTipoVolume tpParamCalcUsado) {
		String retorno = paramNptVO.getUmmDescricao();
		if (tpParamCalcUsado.equals(DominioTipoVolume.K)) {
			retorno = retorno.concat(KG_DIA);

		} else if (tpParamCalcUsado.equals(DominioTipoVolume.M)) {
			retorno = retorno.concat(M2_DIA);

		} else if (tpParamCalcUsado.equals(DominioTipoVolume.V)) {
			retorno = retorno.concat(ML_DIA);

		} else if (tpParamCalcUsado.equals(DominioTipoVolume.T)) {
			retorno = retorno.concat(MG_KG_DIA);
		}
		return retorno;
	}

	/**
	 * 
	 * @param paramNptGli50VO
	 * @param tpParamCalcUsado
	 * @return
	 */
	private String obterUnidParamPorTipoVolumeGli50(AfaParamNptGlicose50VO paramNptGli50VO, DominioTipoVolume tpParamCalcUsado) {
		String retorno = "";
		if (tpParamCalcUsado.equals(DominioTipoVolume.K)) {
			retorno = paramNptGli50VO.getUmmDescricao().concat(KG_DIA);

		} else if (tpParamCalcUsado.equals(DominioTipoVolume.M)) {
			retorno = paramNptGli50VO.getTigUmmDescricao().concat(M2_DIA);

		} else if (tpParamCalcUsado.equals(DominioTipoVolume.V)) {
			retorno = paramNptGli50VO.getUmmDescricao().concat(ML_DIA);

		} else if (tpParamCalcUsado.equals(DominioTipoVolume.T)) {
			retorno = paramNptGli50VO.getUmmDescricao().concat(KG_DIA);
		}
		return retorno;
	}

	/**
	 * #989 - PC06 - Procedure para disparar mensagem para que o usuário informe novamente os campos, caso o peso esteja abaixo ou acima do limite de calculo em relação ao peso anterior do paciente.
	 * 
	 * @param calculoAdultoNptVO
	 */
	public void verificarCalculoPesoPaciente(CalculoAdultoNptVO calculoAdultoNptVO) {

		final CalculoParametrosFixosVO calculoParametrosFixosVO = calculoAdultoNptVO.getCalculoParametrosFixosVO();
		final AfaParamComponenteNptsVO afaParamComponenteNptsVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO();

		if ((calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) > 0 && calculoAdultoNptVO.getDadosPesoAlturaVO().getPesoAnterior().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) < 0)
				|| (calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) < 0 && calculoAdultoNptVO.getDadosPesoAlturaVO().getPesoAnterior().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) > 0)) {

			if (afaParamComponenteNptsVO.getAfaParamNptLipidio10VO().getTipoParamCalculo().equals(DominioTipoVolume.A)) {
				calculoAdultoNptVO.setParamLip(BigDecimal.ZERO);
				calculoAdultoNptVO.setTotParamLip(BigDecimal.ZERO);
				calculoAdultoNptVO.setTotParamLipEd("0");
			}

			if (afaParamComponenteNptsVO.getAfaParamNptAminoacidoAdVO().equals(DominioTipoVolume.A)) {
				calculoAdultoNptVO.setParamAmin(BigDecimal.ZERO);
				calculoAdultoNptVO.setTotParamAmin(BigDecimal.ZERO);
				calculoAdultoNptVO.setTotParamAminEd("0");
			}

			if (afaParamComponenteNptsVO.getAfaParamNptGlicose50VO().getTigTipoParamCalculo().equals(DominioTipoVolume.A)) {
				calculoAdultoNptVO.setParamTig(BigDecimal.ZERO);
				calculoAdultoNptVO.setTotParamTig(BigDecimal.ZERO);
				calculoAdultoNptVO.setTotParamTigEd("0");
			}

			if (afaParamComponenteNptsVO.getAfaParamNptHeparinaVO().getTipoParamCalculo().equals(DominioTipoVolume.A)) {
				calculoAdultoNptVO.setParamHeparina(BigDecimal.ZERO);
				calculoAdultoNptVO.setTotParamHeparina(BigDecimal.ZERO);
				calculoAdultoNptVO.setTotParamHeparinaEd("0");
			}

			if (calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) < 0 && calculoAdultoNptVO.getDadosPesoAlturaVO().getPesoAnterior().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) > 0
					&& !calculoAdultoNptVO.getDadosPesoAlturaVO().isMostrouMensTrocaPeso()) {

				calculoAdultoNptVO.getDadosPesoAlturaVO().setMostrouMensTrocaPeso(true);
				calculoAdultoNptVO.getDadosPesoAlturaVO().setExibirMpm3410(true);
			}

			if (calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) > 0 && calculoAdultoNptVO.getDadosPesoAlturaVO().getPesoAnterior().compareTo(calculoParametrosFixosVO.getLimiteCalculoPeso().doubleValue()) < 0
					&& !calculoAdultoNptVO.getDadosPesoAlturaVO().isMostrouMensTrocaPeso()) {

				calculoAdultoNptVO.getDadosPesoAlturaVO().setMostrouMensTrocaPeso(true);
				calculoAdultoNptVO.getDadosPesoAlturaVO().setExibirMpm3411(true);
			}
		}
	}

	/**
	 * #989 - PC08 - Procedure para definição de algumas variáveis e a cópia de alguns atributos de CalculoAdultoNptVO para CalculoParametrosFixosVOque serão utilizados no cálculo.
	 * 
	 * @param calculoAdultoNptVO
	 */
	public void copiarValoresParametrosFixos(CalculoAdultoNptVO calculoAdultoNptVO) {

		calculoAdultoNptVO.getCalculoParametrosFixosVO().setCalculaNpt(Boolean.TRUE);

		if (calculoAdultoNptVO.getParamPercKcl() == null) {
			calculoAdultoNptVO.setParamPercKcl(new BigDecimal(70));
		}
		if (calculoAdultoNptVO.getParamPercK3po4() == null) {
			calculoAdultoNptVO.setParamPercK3po4(new BigDecimal(30));
		}
		if (calculoAdultoNptVO.getParamTipoLip() == null) {
			calculoAdultoNptVO.setParamTipoLip(DominioTipoLipidio.LIPIDIO_20_PORCENTO);
		}
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamTig(calculoAdultoNptVO.getParamTig().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamAmin(calculoAdultoNptVO.getParamAmin().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamLip(calculoAdultoNptVO.getParamLip().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamTipoLip(calculoAdultoNptVO.getParamTipoLip());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic50(calculoAdultoNptVO.getParamPercGlic50().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic10(calculoAdultoNptVO.getParamPercGlic10().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamHeparina(calculoAdultoNptVO.getParamHeparina().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamTempInfusaoSol(calculoAdultoNptVO.getParamTempInfusaoSol().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamTempInfusaoLip(calculoAdultoNptVO.getParamTempInfusaoLip().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamSodio(calculoAdultoNptVO.getParamSodio().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPotassio(calculoAdultoNptVO.getParamPotassio().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercKcl(calculoAdultoNptVO.getParamPercKcl().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercK3po4(calculoAdultoNptVO.getParamPercK3po4().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamCalcio(calculoAdultoNptVO.getParamCalcio().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamMagnesio(calculoAdultoNptVO.getParamMagnesio().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamAcetZn(calculoAdultoNptVO.getParamAcetZn().intValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAa10(calculoAdultoNptVO.getVolAa10().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose50(calculoAdultoNptVO.getVolGlicose50().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose10(calculoAdultoNptVO.getVolGlicose10().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolNacl20(calculoAdultoNptVO.getVolNacl20().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolKcl(calculoAdultoNptVO.getVolKcl().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolK3po4(calculoAdultoNptVO.getVolK3Po4().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolMsgo4(calculoAdultoNptVO.getVolMgso4().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlucoCa(calculoAdultoNptVO.getVolGlucoCa().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolHeparina(calculoAdultoNptVO.getVolHeparina().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAcetZn(calculoAdultoNptVO.getVolAcetZn().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoSolucao(calculoAdultoNptVO.getGotejoSolucao().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios10(calculoAdultoNptVO.getVolLipidios10().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios20(calculoAdultoNptVO.getVolLipidios20().doubleValue());
		calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoLipidios(calculoAdultoNptVO.getGotejoLipidios().doubleValue());
	}

	/*
	 * ONs componentes NPT (TELA)
	 */

	/**
	 * ON01
	 * 
	 * @param paramPercGlic50
	 * @throws ApplicationBusinessException
	 */
	public void validarProporcaoGlicose50Menor100(BigDecimal paramPercGlic50) throws ApplicationBusinessException {
		if (CoreUtil.maior(paramPercGlic50, 100)) {
			throw new ApplicationBusinessException(CalcularNptONExceptionCode.MPM_03741);
		}
	}

	/**
	 * ON02
	 * 
	 * @param paramPercGlic10
	 * @throws ApplicationBusinessException
	 */
	public void validarProporcaoGlicose10Menor100(BigDecimal paramPercGlic10) throws ApplicationBusinessException {
		if (CoreUtil.maior(paramPercGlic10, 100)) {
			throw new ApplicationBusinessException(CalcularNptONExceptionCode.MPM_03742);
		}
	}

	/**
	 * ON03
	 * 
	 * @param paramPercGlic50
	 * @param paramPercGlic10
	 * @throws ApplicationBusinessException
	 */
	public void validarSomaProporcaoGlicose(BigDecimal paramPercGlic50, BigDecimal paramPercGlic10) throws ApplicationBusinessException {
		final double valor1 = paramPercGlic50 != null ? paramPercGlic50.doubleValue() : 0;
		final double valor2 = paramPercGlic10 != null ? paramPercGlic10.doubleValue() : 0;
		final double soma = valor1 + valor2;
		if (soma != 100) {
			throw new ApplicationBusinessException(CalcularNptONExceptionCode.MPM_03743);
		}
	}

	/**
	 * ON03
	 * 
	 * @param paramTempInfusaoSol
	 * @throws ApplicationBusinessException
	 */
	public void validarIntervaloTempoInfusaoSolucao(Short paramTempInfusaoSol) throws ApplicationBusinessException {
		validaIntervaloInfusao(paramTempInfusaoSol, CalcularNptONExceptionCode.MPM_03744);
	}

	/**
	 * ON4
	 * 
	 * @param paramTempInfusaoLip
	 * @throws ApplicationBusinessException
	 */
	public void validarIntervaloTempoInfusaoLipidios(Short paramTempInfusaoLip) throws ApplicationBusinessException {
		validaIntervaloInfusao(paramTempInfusaoLip, CalcularNptONExceptionCode.MPM_03745);
	}

	/**
	 * ON03/ON04
	 * 
	 * @param tempoInfusao
	 * @param exceptionCode
	 * @throws ApplicationBusinessException
	 */
	private void validaIntervaloInfusao(Short tempoInfusao, CalcularNptONExceptionCode exceptionCode) throws ApplicationBusinessException {
		final boolean valido = tempoInfusao != null && tempoInfusao >= 12 && tempoInfusao <= 24;
		if (!valido) {
			throw new ApplicationBusinessException(exceptionCode);
		}
	}

	/**
	 * ON07 até ON36
	 * 
	 * @param valorParametro
	 * @param ex
	 * @throws ApplicationBusinessException
	 */
	public void validarParametrosMenorIgualZero(Number valorParametro, CalcularNptONExceptionCode ex) throws ApplicationBusinessException {
		if (valorParametro != null && CoreUtil.menorOuIgual(valorParametro, 0)) {
			throw new ApplicationBusinessException(ex);
		}
	}

	/**
	 * Validação do botão gravar: ON01, ON02, ON03, ON04, ON05, ON07 à ON38
	 * 
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 */
	private void validarCalculoNpt(CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException {
		validarProporcaoGlicose50Menor100(calculoAdultoNptVO.getParamPercGlic50()); // ON1
		validarProporcaoGlicose10Menor100(calculoAdultoNptVO.getParamPercGlic10()); // ON2
		validarSomaProporcaoGlicose(calculoAdultoNptVO.getParamPercGlic50(), calculoAdultoNptVO.getParamPercGlic10()); // ON3
		validarIntervaloTempoInfusaoSolucao(calculoAdultoNptVO.getParamTempInfusaoSol()); // ON4
		validarIntervaloTempoInfusaoLipidios(calculoAdultoNptVO.getParamTempInfusaoLip()); // ON5

		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamVolDes(), CalcularNptONExceptionCode.MPM_03478); // ON7
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getTotParamVolDes(), CalcularNptONExceptionCode.MPM_03479); // ON8
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamLip(), CalcularNptONExceptionCode.MPM_03480); // ON9
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamAmin(), CalcularNptONExceptionCode.MPM_03481); // ON10
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamSodio(), CalcularNptONExceptionCode.MPM_03482); // ON11
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamCalcio(), CalcularNptONExceptionCode.MPM_03483); // ON12
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamMagnesio(), CalcularNptONExceptionCode.MPM_03484); // ON13
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamOligo(), CalcularNptONExceptionCode.MPM_03485); // ON14
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamPotassio(), CalcularNptONExceptionCode.MPM_03486); // ON15
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamAcetZn(), CalcularNptONExceptionCode.MPM_03487); // ON16
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamTig(), CalcularNptONExceptionCode.MPM_03488); // ON17
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamHeparina(), CalcularNptONExceptionCode.MPM_03489); // ON18
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamTempInfusaoSol(), CalcularNptONExceptionCode.MPM_03490); // ON19
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getParamTempInfusaoLip(), CalcularNptONExceptionCode.MPM_03491); // ON20
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolNacl20(), CalcularNptONExceptionCode.MPM_03492); // ON21
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolGlucoCa(), CalcularNptONExceptionCode.MPM_03493); // ON22
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolKcl(), CalcularNptONExceptionCode.MPM_03494); // ON23
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolK3Po4(), CalcularNptONExceptionCode.MPM_03495); // ON24
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolMgso4(), CalcularNptONExceptionCode.MPM_03496); // ON25
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolOligo(), CalcularNptONExceptionCode.MPM_03497); // ON26
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolOligoPed(), CalcularNptONExceptionCode.MPM_03498); // ON27
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolLipidios10(), CalcularNptONExceptionCode.MPM_03499); // ON28
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolLipidios20(), CalcularNptONExceptionCode.MPM_03500); // ON29
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolGlicose5(), CalcularNptONExceptionCode.MPM_03501); // ON30
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolGlicose10(), CalcularNptONExceptionCode.MPM_03502); // ON31
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolGlicose50(), CalcularNptONExceptionCode.MPM_03503); // ON32
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolHeparina(), CalcularNptONExceptionCode.MPM_03504); // ON33
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getGotejoSolucao(), CalcularNptONExceptionCode.MPM_03505); // ON34
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getGotejoLipidios(), CalcularNptONExceptionCode.MPM_03506); // ON35
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolAa10(), CalcularNptONExceptionCode.MPM_03507); // ON36
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolAaPed10(), CalcularNptONExceptionCode.MPM_03508); // ON37
		validarParametrosMenorIgualZero(calculoAdultoNptVO.getVolAcetZn(), CalcularNptONExceptionCode.MPM_03509); // ON38
	}

	/**
	 * Gravar cálculo NPT: Considerar ON01, ON02, ON03, ON04, ON05, ON07 à ON38, e RN16 e ON43
	 * 
	 * @param prescricaoNptVo
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	public void gravarCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException, IllegalAccessException, InvocationTargetException {
		validarCalculoNpt(calculoAdultoNptVO);
		this.calcularNptRN.popularCalculoAdulto(prescricaoNptVo, calculoAdultoNptVO); // MPMP_POPULA_CALCULO_A
	}

	/**
	 * ON41: Atualizar Peso Alturar do Cálculo Adulto NPT
	 * 
	 * @param prescricaoNptVo
	 * @param calculoAdultoNptVO
	 * @throws ApplicationBusinessException
	 */
	public void atualizarPesoAlturaCalculoNpt(MpmPrescricaoNptVO prescricaoNptVo, CalculoAdultoNptVO calculoAdultoNptVO) throws ApplicationBusinessException {

		// RN06
		calculoAdultoNptVO.setDadosPesoAlturaVO(this.verificarParametroCalculo(prescricaoNptVo.getAtdSeq()));

		// RN07
		this.verificarPesoPaciente(calculoAdultoNptVO.getDadosPesoAlturaVO());

		// RN09 - PC4 – MPMP_PARAM_BASE_CALCULO
		this.obterParametrosBaseCalculo(calculoAdultoNptVO);

		if (!prescricaoNptVo.getIndPacPediatrico()) { // Observação: As regras de RN01 a RN09 também deve ser executadas na estória #1103. Não duplicar regras em comum.
			// RN10 - PC05
			obterUnidadesMedida(calculoAdultoNptVO);

			// RN13
			this.verificarCalculoPesoPaciente(calculoAdultoNptVO);

			// RN15
			this.calcularNptON2.mpmCalculaComponentesNptParte1(DominioTipoComponenteNpt.RECALCULA, calculoAdultoNptVO);

		}
	}

	/*
	 * Getters and Setters
	 */

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
}
