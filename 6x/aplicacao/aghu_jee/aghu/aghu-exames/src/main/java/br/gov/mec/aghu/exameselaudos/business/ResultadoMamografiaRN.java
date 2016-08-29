package br.gov.mec.aghu.exameselaudos.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioContornoNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioLimiteNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioLinfonodosAxilaresMamografia;
import br.gov.mec.aghu.dominio.DominioLocalizacaoMamografia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTamanhoNoduloMamografia;
import br.gov.mec.aghu.exames.dao.AelAgrpPesquisaXExameDAO;
import br.gov.mec.aghu.exameselaudos.business.ResultadoMamografiaUtil.ConsisteDadosRNExceptionCode;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;

/**
 * Utilizada pela estoria #5978 - Preencher achados radiologicos do resultado da mamografia (SISMAMA)
 *
 */
@Stateless
public class ResultadoMamografiaRN extends BaseBusiness {


private static final String C_RAD_NUM_FILM_D = "C_RAD_NUM_FILM_D";

private static final String C_RAD_NUM_FILM_E = "C_RAD_NUM_FILM_E";

@EJB
private LaudoMamografiaRN laudoMamografiaRN;

private static final Log LOG = LogFactory.getLog(ResultadoMamografiaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelAgrpPesquisaXExameDAO aelAgrpPesquisaXExameDAO;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6116321235468806687L;
	

	private ResultadoMamografiaUtil getResultadoMamografiaUtil() {
		return new ResultadoMamografiaUtil();
	}

	/**
	 * @ORADB - AELC_VER_EXA_SISMAMA
	 * 
	 * verifica se o exame pertence ao sismama
	 * 
	 * @param ufeEmaExaSigla
	 * @param ufeEmaManSeq
	 * @param ufeUnfSeq
	 * @return
	 */
	public Boolean verificarExameIsSismama(String ufeEmaExaSigla, Integer ufeEmaManSeq, Short ufeUnfSeq) {
		return !getAelAgrpPesquisaXExameDAO().pesquisarPorDescricaoAtivoPorUnfExecutaExame("SISMAMA", ufeEmaExaSigla, ufeEmaManSeq, ufeUnfSeq, DominioSituacao.A).isEmpty();
	}
	
	/**
	 * @ORADB - AGHC_VER_CARACT_UNF
	 * 
	 * Verifica se uma unidade funcional tem determinada caracteristica se tiver retorna 'S' se não tiver retorna 'N'
	 * 
	 * @param unfSeq
	 * @param caracteristica
	 * @return
	 */
	public Boolean verificarUnidadeFuncionalTemCaracteristica(Short unfSeq, ConstanteAghCaractUnidFuncionais caracteristica) {
		AghCaractUnidFuncionais cuf = getAghuFacade().obterAghCaractUnidFuncionaisPorChavePrimaria(new AghCaractUnidFuncionaisId(unfSeq, caracteristica));
		return cuf != null;
	}
	
	/**
	 * @ORADB - AELC_VER_SIT_EXM_MAM
	 * @param sitCodigo
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public Boolean verificarSituacaoExameMamografia(String sitCodigo) throws ApplicationBusinessException {
		String sitCodigoLi = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO).getVlrTexto();
		String sitCodigoAe = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA).getVlrTexto();
		String sitCodigoEx = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO).getVlrTexto();
		
		return sitCodigo != null
			&& (StringUtils.equals(sitCodigo, sitCodigoLi)
					|| StringUtils.equals(sitCodigo, sitCodigoAe)
					|| StringUtils.equals(sitCodigo, sitCodigoEx));
		
	}
	
	protected LaudoMamografiaRN getLaudoMamografiaRN() {
		return laudoMamografiaRN;
	}
	
	protected AelAgrpPesquisaXExameDAO getAelAgrpPesquisaXExameDAO() {
		return aelAgrpPesquisaXExameDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	//primeiro
	private void validacaoMamaNumFilm(Map<String, AelSismamaMamoResVO> mapMama, 
									  BaseListException listaException, 
									  String vSexoPaciente,boolean vHabilitaMamaEsquerda,
										 boolean vHabilitaMamaDireita) {
		
		List<String> lados = obterLados(vHabilitaMamaEsquerda,vHabilitaMamaDireita );
		
		Map<String, ConsisteDadosRNExceptionCode> mapMsgErr = obterMapaMsgErros();
		
		for(String lado : lados){
			
			String key = "C_RAD_NUM_FILM_".concat(lado);
			AelSismamaMamoResVO item = mapMama.get(key);
			if(item.getNumeroFilmes() != null && item.getNumeroFilmes() >= 1 && item.getNumeroFilmes() <=99){
				key = "C_CON_DIA_CAT_".concat(lado);
				item = mapMama.get(key);
				if(item.getCategoria() == null){
					getResultadoMamografiaUtil().incluirException(mapMsgErr.get(key),listaException);
				}
				
				key = "C_CON_RECOM_".concat(lado);
				item = mapMama.get(key);
				if(item.getRecomendacao() == null && vSexoPaciente.equals("F")){
					getResultadoMamografiaUtil().incluirException(mapMsgErr.get(key.concat("2")),listaException);
				}
			}
			else{
				getResultadoMamografiaUtil().incluirException(mapMsgErr.get(key.concat("2")),listaException);
			}
		}
	}
	
	//segundo
	private void validarAchadosRadiologicosMamaDireita(Map<String, AelSismamaMamoResVO> mapEntry, 
											BaseListException listaException, 
											boolean vHabilitaMamaEsquerda,
											String vSexoPaciente) {
		if(!vHabilitaMamaEsquerda) {
			
			/**
			 * MESMA VALIDAÇAO 1
			 */
			if(mapEntry.get(C_RAD_NUM_FILM_D) == null || mapEntry.get(C_RAD_NUM_FILM_D).getNumeroFilmes() == null) {
//			if(mapMamaD.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()).getResposta() == null) {
				getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03238,listaException);
			}
			
			if(mapEntry.containsKey(C_RAD_NUM_FILM_D) &&  getResultadoMamografiaUtil().validarSeNumFilmes(mapEntry.get(C_RAD_NUM_FILM_D).getNumeroFilmes())) {
//			if(Integer.parseInt(mapMamaD.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()).getResposta()) >= 1
//			  && Integer.parseInt(mapMamaD.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()).getResposta()) <= 99) {
			
			
				if(!mapEntry.containsKey("C_CON_DIA_CAT_D") || mapEntry.get("C_CON_DIA_CAT_D").getCategoria() == null) {
				//if(mapMamaD.get(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D.toString()).getResposta() == null) {
					getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03383,listaException);
				}
				
				if(!mapEntry.containsKey("C_CON_RECOM_D") || (mapEntry.get("C_CON_RECOM_D").getRecomendacao() == null 
					&& vSexoPaciente.equals("F"))) {
				//if(mapMamaD.get(DominioSismamaMamoCadCodigo.C_CON_RECOM_D.toString()).getResposta() == null
					getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03385,listaException);
				}
			} 
			else {
				getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03216,listaException);
			}
		}
	}
	
	private void validarRecomendacoes(Map<String, AelSismamaMamoResVO> mapMama,BaseListException listaException, String vSexoPaciente, boolean vHabilitaMamaEsquerda,
			 boolean vHabilitaMamaDireita){
		List<String> lados = obterLados(vHabilitaMamaEsquerda,vHabilitaMamaDireita);
		
		Map<String, ConsisteDadosRNExceptionCode> mapMsgErr = obterMapaMsgErros();
		
		for(String lado : lados){
			
			String key = "C_CON_RECOM_".concat(lado);
			AelSismamaMamoResVO item = mapMama.get(key);
			
			if(item != null && item.getRecomendacao() != null && "M".equals(vSexoPaciente)) {
				
				getResultadoMamografiaUtil().incluirException(mapMsgErr.get(key),listaException);
				
			}
		}
	}
	
	/**
	 * Executa as validações dos campos de nódulos das mamas direita e esquerda.
	 * @param mapMama
	 * @param listaException
	 * @author bruno.mourao
	 * @since 16/01/2013
	 */
	private void validarNodulos(Map<String, AelSismamaMamoResVO> mapMamaD,BaseListException listaException,boolean vHabilitaMamaEsquerda,
			 boolean vHabilitaMamaDireita){
		List<String> lados = obterLados(vHabilitaMamaEsquerda,vHabilitaMamaDireita);
		
		List<String> numerosItem = obterNumeros(3);
		
		Map<String, ConsisteDadosRNExceptionCode> mapMsgErr = obterMapaMsgErros();
		
		for(String num : numerosItem){
			for(String lado : lados){
				
				AelSismamaMamoResVO item = mapMamaD.get("C_NOD_SIM_0".concat(num).concat(lado));
				
				if(item.isChecked()) {
					
					String key = "C_NOD_LOC_0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampoNodulo(item.getLocalizacao(),mapMsgErr.get(key),listaException);
					
					key = "C_NOD_TAM_0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampoNodulo(item.getTamanho(),mapMsgErr.get(key),listaException);
					
					key = "C_NOD_CONT_0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampoNodulo(item.getContorno(),mapMsgErr.get(key),listaException);
					
					key = "C_NOD_LIM_0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampoNodulo(item.getLimite(),mapMsgErr.get(key),listaException);
					
				}
			}
		}
	}
	
	private void verificarValorCampo(Object campo, ConsisteDadosRNExceptionCode err, BaseListException listaException){
		if(campo == null){
			getResultadoMamografiaUtil().incluirException(err,listaException);
		}
	}

	private void verificarValorCampoNodulo(Object campo, ConsisteDadosRNExceptionCode err, BaseListException listaException){
		if (campo == null) {
			getResultadoMamografiaUtil().incluirException(err,listaException);
		}
		else if (campo instanceof DominioLocalizacaoMamografia && Integer.valueOf(((DominioLocalizacaoMamografia) campo).getCodigo()).equals(Integer.valueOf(0))) {
			getResultadoMamografiaUtil().incluirException(err,listaException);
		}
		else if (campo instanceof DominioTamanhoNoduloMamografia && Integer.valueOf(((DominioTamanhoNoduloMamografia) campo).getCodigo()).equals(Integer.valueOf(0))) {
			getResultadoMamografiaUtil().incluirException(err,listaException);
		}
		else if (campo instanceof DominioContornoNoduloMamografia && Integer.valueOf(((DominioContornoNoduloMamografia) campo).getCodigo()).equals(Integer.valueOf(0))) {
			getResultadoMamografiaUtil().incluirException(err,listaException);
		}
		else if (campo instanceof DominioLimiteNoduloMamografia && Integer.valueOf(((DominioLimiteNoduloMamografia) campo).getCodigo()).equals(Integer.valueOf(0))) {
			getResultadoMamografiaUtil().incluirException(err,listaException);
		}
		
	}
	
	
	/**
	 * Valida os campos de microcalcificações das mamas direita e esquerda
	 * @param mapMama
	 * @param listaException
	 * @author bruno.mourao
	 * @since 16/01/2013
	 */
	private void validarMicrocalcificacoes(Map<String, AelSismamaMamoResVO> mapMamaD, BaseListException listaException,boolean vHabilitaMamaEsquerda,
			 boolean vHabilitaMamaDireita){
		
		List<String> lados = obterLados(vHabilitaMamaEsquerda, vHabilitaMamaDireita);
		
		List<String> numerosItem = obterNumeros(3);
		
		Map<String, ConsisteDadosRNExceptionCode> mapMsgErr = obterMapaMsgErros();
		for(String num : numerosItem){
			for(String lado : lados){
				
				AelSismamaMamoResVO item = mapMamaD.get("C_MICRO_SIM_0".concat(num).concat(lado));
				
				if(item.isChecked()) {
					
					String key = "C_MICRO_LOC_0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampo(item.getLocalizacao(),mapMsgErr.get(key),listaException);
					
					key = "C_MICRO_FORM_0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampo(item.getForma(),mapMsgErr.get(key),listaException);
					
					key = "C_MICRO_DISTR_0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampo(item.getDistribuicao(),mapMsgErr.get(key),listaException);
					
				}
			}
		}
	}

	/**
	 * Monta um mapa com as exceções de não preenchimento de cada resposta.
	 * @return
	 * @author bruno.mourao
	 * @since 16/01/2013
	 */
	private Map<String, ConsisteDadosRNExceptionCode> obterMapaMsgErros() {
		Map<String, ConsisteDadosRNExceptionCode> mapMsgErr = new HashMap<String, ResultadoMamografiaUtil.ConsisteDadosRNExceptionCode>();
		
		//Lado direito
		mapMsgErr.put("C_CON_RECOM_D", ConsisteDadosRNExceptionCode.AEL_03387);
		mapMsgErr.put("C_CON_RECOM_D2", ConsisteDadosRNExceptionCode.AEL_03385);
		
		mapMsgErr.put(C_RAD_NUM_FILM_D, ConsisteDadosRNExceptionCode.AEL_03245);
		mapMsgErr.put("C_RAD_NUM_FILM_D2", ConsisteDadosRNExceptionCode.AEL_03246);
		
		mapMsgErr.put("C_CON_DIA_CAT_D", ConsisteDadosRNExceptionCode.AEL_03383);
		
		//Nodulos
		mapMsgErr.put("C_NOD_LOC_01D", ConsisteDadosRNExceptionCode.AEL_03250);
		mapMsgErr.put("C_NOD_TAM_01D", ConsisteDadosRNExceptionCode.AEL_03252);
		mapMsgErr.put("C_NOD_CONT_01D", ConsisteDadosRNExceptionCode.AEL_03255);
		mapMsgErr.put("C_NOD_LIM_01D", ConsisteDadosRNExceptionCode.AEL_03258);
		
		mapMsgErr.put("C_NOD_LOC_02D", ConsisteDadosRNExceptionCode.AEL_03250);
		mapMsgErr.put("C_NOD_TAM_02D", ConsisteDadosRNExceptionCode.AEL_03253);
		mapMsgErr.put("C_NOD_CONT_02D", ConsisteDadosRNExceptionCode.AEL_03256);
		mapMsgErr.put("C_NOD_LIM_02D", ConsisteDadosRNExceptionCode.AEL_03259);
		
		mapMsgErr.put("C_NOD_LOC_03D", ConsisteDadosRNExceptionCode.AEL_03251);
		mapMsgErr.put("C_NOD_TAM_03D", ConsisteDadosRNExceptionCode.AEL_03254);
		mapMsgErr.put("C_NOD_CONT_03D", ConsisteDadosRNExceptionCode.AEL_03257);
		mapMsgErr.put("C_NOD_LIM_03D", ConsisteDadosRNExceptionCode.AEL_03260);
		
		//Microcalcificações
		mapMsgErr.put("C_MICRO_LOC_01D", ConsisteDadosRNExceptionCode.AEL_03280);
		mapMsgErr.put("C_MICRO_FORM_01D", ConsisteDadosRNExceptionCode.AEL_03283);
		mapMsgErr.put("C_MICRO_DISTR_01D", ConsisteDadosRNExceptionCode.AEL_03286);
		
		mapMsgErr.put("C_MICRO_LOC_02D", ConsisteDadosRNExceptionCode.AEL_03281);
		mapMsgErr.put("C_MICRO_FORM_02D", ConsisteDadosRNExceptionCode.AEL_03284);
		mapMsgErr.put("C_MICRO_DISTR_02D", ConsisteDadosRNExceptionCode.AEL_03287);
		
		mapMsgErr.put("C_MICRO_LOC_03D", ConsisteDadosRNExceptionCode.AEL_03282);
		mapMsgErr.put("C_MICRO_FORM_03D", ConsisteDadosRNExceptionCode.AEL_03285);
		mapMsgErr.put("C_MICRO_DISTR_03D", ConsisteDadosRNExceptionCode.AEL_03288);
		
		//Assimetria/Distorção Focal
		mapMsgErr.put("C_ASSI_FOC_LOC01D", ConsisteDadosRNExceptionCode.AEL_03298);
		mapMsgErr.put("C_ASSI_FOC_LOC02D", ConsisteDadosRNExceptionCode.AEL_03299);
		mapMsgErr.put("C_ASSI_DIFU_LOC01D", ConsisteDadosRNExceptionCode.AEL_03300);
		mapMsgErr.put("C_ASSI_DIFU_LOC02D", ConsisteDadosRNExceptionCode.AEL_03301);
		
		//Assimetria Difusa/ Area Densa
		mapMsgErr.put("C_DIS_FOC_LOC01D", ConsisteDadosRNExceptionCode.AEL_03302);
		mapMsgErr.put("C_DIS_FOC_LOC02D", ConsisteDadosRNExceptionCode.AEL_03303);
		mapMsgErr.put("C_AR_DENS_LOC01D", ConsisteDadosRNExceptionCode.AEL_03304);
		mapMsgErr.put("C_AR_DENS_LOC02D", ConsisteDadosRNExceptionCode.AEL_03305);
		
		//Linfonodos Axilares
		mapMsgErr.put("C_LINF_AUX_D", ConsisteDadosRNExceptionCode.AEL_03319);
		
		//Lado esquerdo
		mapMsgErr.put("C_CON_RECOM_E", ConsisteDadosRNExceptionCode.AEL_03388);
		mapMsgErr.put("C_CON_RECOM_E2", ConsisteDadosRNExceptionCode.AEL_03386);
		
		mapMsgErr.put(C_RAD_NUM_FILM_E, ConsisteDadosRNExceptionCode.AEL_03245);
		mapMsgErr.put("C_RAD_NUM_FILM_E2", ConsisteDadosRNExceptionCode.AEL_03247);
		
		
		mapMsgErr.put("C_CON_DIA_CAT_E", ConsisteDadosRNExceptionCode.AEL_03384);
		
		//Nódulos
		mapMsgErr.put("C_NOD_LOC_01E", ConsisteDadosRNExceptionCode.AEL_03261);
		mapMsgErr.put("C_NOD_TAM_01E", ConsisteDadosRNExceptionCode.AEL_03264);
		mapMsgErr.put("C_NOD_CONT_01E", ConsisteDadosRNExceptionCode.AEL_03267);
		mapMsgErr.put("C_NOD_LIM_01E", ConsisteDadosRNExceptionCode.AEL_03270);
		
		mapMsgErr.put("C_NOD_LOC_02E", ConsisteDadosRNExceptionCode.AEL_03262);
		mapMsgErr.put("C_NOD_TAM_02E", ConsisteDadosRNExceptionCode.AEL_03265);
		mapMsgErr.put("C_NOD_CONT_02E", ConsisteDadosRNExceptionCode.AEL_03268);
		mapMsgErr.put("C_NOD_LIM_02E", ConsisteDadosRNExceptionCode.AEL_03271);
		
		mapMsgErr.put("C_NOD_LOC_03E", ConsisteDadosRNExceptionCode.AEL_03263);
		mapMsgErr.put("C_NOD_TAM_03E", ConsisteDadosRNExceptionCode.AEL_03266);
		mapMsgErr.put("C_NOD_CONT_03E", ConsisteDadosRNExceptionCode.AEL_03269);
		mapMsgErr.put("C_NOD_LIM_03E", ConsisteDadosRNExceptionCode.AEL_03272);
		
		//Microcalcificações
		mapMsgErr.put("C_MICRO_LOC_01E", ConsisteDadosRNExceptionCode.AEL_03289);
		mapMsgErr.put("C_MICRO_FORM_01E", ConsisteDadosRNExceptionCode.AEL_03292);
		mapMsgErr.put("C_MICRO_DISTR_01E", ConsisteDadosRNExceptionCode.AEL_03295);

		mapMsgErr.put("C_MICRO_LOC_02E", ConsisteDadosRNExceptionCode.AEL_03290);
		mapMsgErr.put("C_MICRO_FORM_02E", ConsisteDadosRNExceptionCode.AEL_03293);
		mapMsgErr.put("C_MICRO_DISTR_02E", ConsisteDadosRNExceptionCode.AEL_03296);
		
		mapMsgErr.put("C_MICRO_LOC_03E", ConsisteDadosRNExceptionCode.AEL_03291);
		mapMsgErr.put("C_MICRO_FORM_03E", ConsisteDadosRNExceptionCode.AEL_03294);
		mapMsgErr.put("C_MICRO_DISTR_03E", ConsisteDadosRNExceptionCode.AEL_03297);
		
		//Assimetria/Distorção Focal
		mapMsgErr.put("C_ASSI_FOC_LOC01E", ConsisteDadosRNExceptionCode.AEL_03306);
		mapMsgErr.put("C_ASSI_FOC_LOC02E", ConsisteDadosRNExceptionCode.AEL_03307);
		mapMsgErr.put("C_ASSI_DIFU_LOC01E", ConsisteDadosRNExceptionCode.AEL_03308);
		mapMsgErr.put("C_ASSI_DIFU_LOC02E", ConsisteDadosRNExceptionCode.AEL_03309);
		
		//Assimetria Difusa/ Area Densa
		mapMsgErr.put("C_DIS_FOC_LOC01E", ConsisteDadosRNExceptionCode.AEL_03310);
		mapMsgErr.put("C_DIS_FOC_LOC02E", ConsisteDadosRNExceptionCode.AEL_03311);
		mapMsgErr.put("C_AR_DENS_LOC01E", ConsisteDadosRNExceptionCode.AEL_03312);
		mapMsgErr.put("C_AR_DENS_LOC02E", ConsisteDadosRNExceptionCode.AEL_03313);
		
		//Linfonodos Axilares
		mapMsgErr.put("C_LINF_AUX_E", ConsisteDadosRNExceptionCode.AEL_03321);
		
		return mapMsgErr;
	}

	/**
	 * Valida os campos de Assimetria e Distorção Focal das mamas direita e esquerda
	 * @param mapMama
	 * @param listaException
	 * @author bruno.mourao
	 * @since 16/01/2013
	 */
	private void validarAssimetriaDistorcaoFocal(Map<String, AelSismamaMamoResVO> mapMamaD,BaseListException listaException,boolean vHabilitaMamaEsquerda,
			 boolean vHabilitaMamaDireita){

		List<String> lados = obterLados(vHabilitaMamaEsquerda, vHabilitaMamaDireita);
		
		List<String> numerosItem = obterNumeros(2);
		
		Map<String, ConsisteDadosRNExceptionCode> mapMsgErr = obterMapaMsgErros();
		
		for(String num : numerosItem){
			for(String lado : lados){
				AelSismamaMamoResVO item = mapMamaD.get("C_ASSI_FOC_SIM0".concat(num).concat(lado));
				
				if(item.isChecked()) {
					
					String key = "C_ASSI_FOC_LOC0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampo(item.getLocalizacao(),mapMsgErr.get(key),listaException);
					
				}
				
				item = mapMamaD.get("C_ASSI_DIF_SIM0".concat(num).concat(lado));
				
				if(item.isChecked()) {
					
					String key = "C_ASSI_DIFU_LOC0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampo(item.getLocalizacao(),mapMsgErr.get(key),listaException);
					
				}
				
			}
		}
		
	}

	/**
	 * Obtém uma quantidade de números, para que possam ser iterados
	 * @param numeros
	 * @return
	 * @author bruno.mourao
	 * @since 16/01/2013
	 */
	private List<String> obterNumeros(Integer numeros) {
		List<String> numerosItem = new ArrayList<String>();
		for(Integer i=1; i<= numeros; i++){
			numerosItem.add(i.toString());
		}
		return numerosItem;
	}

	/**
	 * Obtém lados direito (D) e esquerdo (E) de acordo com os as informações de habilitar ou não
	 * @return
	 * @author bruno.mourao
	 * @since 16/01/2013
	 */
	private List<String> obterLados(boolean vHabilitaMamaEsquerda, boolean vHabilitaMamaDireita) {
		List<String> lados = new ArrayList<String>();
		if(vHabilitaMamaDireita){
			lados.add("D");
		}
		if(vHabilitaMamaEsquerda){
			lados.add("E");
		}
		return lados;
	}
	
	/**
	 * Valida os campos de Assimetria Difusa e Area densa das mamas direita e esquerda
	 * @param mapMama
	 * @param listaException
	 * @author bruno.mourao
	 * @since 16/01/2013
	 */
	private void validarAssimetriaDifusaAreaDensa(Map<String, AelSismamaMamoResVO> mapMamaD,BaseListException listaException,boolean vHabilitaMamaEsquerda,
			 boolean vHabilitaMamaDireita){
		
		List<String> lados = obterLados(vHabilitaMamaEsquerda, vHabilitaMamaDireita);
		
		List<String> numerosItem = obterNumeros(2);
		
		Map<String, ConsisteDadosRNExceptionCode> mapMsgErr = obterMapaMsgErros();
		
		for(String num : numerosItem){
			for(String lado : lados){
				
				AelSismamaMamoResVO item = mapMamaD.get("C_DIS_FOC_SIM0".concat(num).concat(lado));
				if(item.isChecked()) {
					
					String key = "C_DIS_FOC_LOC0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampo(item.getLocalizacao(),mapMsgErr.get(key),listaException);
					
				}
				
				item = mapMamaD.get("C_AR_DENS_SIM0".concat(num).concat(lado));
				if(item.isChecked()) {
					
					String key = "C_AR_DENS_LOC0".concat(num).concat(lado);
					item = mapMamaD.get(key);
					verificarValorCampo(item.getLocalizacao(),mapMsgErr.get(key),listaException);
					
				}
			}
		}
	}

	/**
	 * Valida os campos ligados ao Linfonodo Axilar das mamas direita e esquerda.
	 * @param mapMama
	 * @param listaException
	 * @author bruno.mourao
	 * @since 16/01/2013
	 */
	private void validarLinfonodoAxilar(Map<String, AelSismamaMamoResVO> mapMama,  
			BaseListException listaException,boolean vHabilitaMamaEsquerda,
			 boolean vHabilitaMamaDireita) {
		
		List<String> lados = obterLados(vHabilitaMamaEsquerda, vHabilitaMamaEsquerda);
		
		Map<String, ConsisteDadosRNExceptionCode> mapMsgErr = obterMapaMsgErros();
		
		for(String lado : lados){
			
			String key = "C_LINF_AUX_".concat(lado);
			AelSismamaMamoResVO item = mapMama.get(key);
			if(item != null && item.getLinfonodoAxilar() != null && (
					item.getLinfonodoAxilar().equals(DominioLinfonodosAxilaresMamografia.NAO_VISIBILIZADO) ||
					item.getLinfonodoAxilar().equals(DominioLinfonodosAxilaresMamografia.NORMAL))) {
				
				String keyChkAum = "C_LINF_AUX_AUM_".concat(lado);
				String keyChkDenso = "C_LINF_AUX_DENSO_".concat(lado);
				String keyChkConf = "C_LINF_AUX_CONF_".concat(lado);
				
				if (mapMama.containsKey(keyChkAum) && mapMama.get(keyChkAum).isChecked()
						|| mapMama.containsKey(keyChkDenso) && mapMama.get(keyChkDenso).isChecked()
						|| mapMama.containsKey(keyChkConf) && mapMama.get(keyChkConf).isChecked()) {
					getResultadoMamografiaUtil().incluirException(mapMsgErr.get(key),listaException);
				}
			}
		}
	}
	
	//MAMA ESQUERDA
	private void validarAchadosRadiologicosMamaEsquerda(Map<String, AelSismamaMamoResVO> mapEntry, 
														BaseListException listaException, 
														boolean vHabilitaMamaDireita,
														String vSexoPaciente) {
		if(!vHabilitaMamaDireita) {
			
			/**
			 * MESMA VALIDAÇAO 1
			 */
			if(mapEntry.get(C_RAD_NUM_FILM_E) == null || mapEntry.get(C_RAD_NUM_FILM_E).getNumeroFilmes() == null) {
			//if(mapMamaE.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString()).getResposta() == null) {
				getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03239,listaException);
			}
			
			
			if(mapEntry.containsKey(C_RAD_NUM_FILM_E) &&  getResultadoMamografiaUtil().validarSeNumFilmes(mapEntry.get(C_RAD_NUM_FILM_E).getNumeroFilmes())) {
			//if(Integer.parseInt(mapMamaE.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString()).getResposta()) >= 1
			//  && Integer.parseInt(mapMamaE.get(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString()).getResposta()) <= 99) {
			
			
				if(!mapEntry.containsKey("C_CON_DIA_CAT_E") || mapEntry.get("C_CON_DIA_CAT_E").getCategoria() == null) {
				//if(mapMamaE.get(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_E.toString()).getResposta() == null) {
					getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03384,listaException);
				}
				
				if(!mapEntry.containsKey("C_CON_RECOM_E") || (mapEntry.get("C_CON_RECOM_E").getRecomendacao() == null  && vSexoPaciente.equals("F"))) {
				//if(mapMamaE.get(DominioSismamaMamoCadCodigo.C_CON_RECOM_E.toString()).getResposta() == null
				   //&& vSexoPaciente.equals("F")) {
					
					getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03386,listaException);
				}
			} 
			else {
				getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03222,listaException);
			}
		}
	}
	
	/**
	 * @throws BaseListException 
	 * @ORADB p_consiste_dados
	 * 
	 */
	public void consistirDados(AelItemSolicitacaoExames aelItemSolicitacaoExames, 
								 Map<String, AelSismamaMamoResVO> mapMamaD, 
								 Map<String, AelSismamaMamoResVO> mapMamaE, 
								 Map<String, AelSismamaMamoResVO> mapConclusao,
								 String rxMamaBilateral,
								 String vSexoPaciente,
								 boolean vHabilitaMamaEsquerda,
								 boolean vHabilitaMamaDireita, 
								 String medicoResponsavel) throws BaseListException {
		
		BaseListException listaException = new BaseListException();
		
		//Junta todos os mapas
		Map<String, AelSismamaMamoResVO> mapMama = new HashMap<String, AelSismamaMamoResVO>();
		
		mapMama.putAll(mapMamaD);
		mapMama.putAll(mapMamaE);
		mapMama.putAll(mapConclusao);
		
		//Valida recomendações da direita e esquerda
		validarRecomendacoes(mapMama,listaException,vSexoPaciente,vHabilitaMamaEsquerda,vHabilitaMamaDireita);
		
		//Valida número de filmes da direita e da esquerda
		validarNumFilmesRecomendacoesHabilitadas(vHabilitaMamaEsquerda, vHabilitaMamaDireita,mapMama,listaException,vSexoPaciente);
		
		//Validações individuais de cada mama (fazendo a mesma coisa do método validarNumFilmesRecomendacoesHabilitadas)
		if(vHabilitaMamaDireita) { //IF  k_variaveis.v_habilita_mama_direita = 'S'
			validarAchadosRadiologicosMamaDireita(mapMama,listaException,vHabilitaMamaEsquerda,vSexoPaciente);
		}
		if(vHabilitaMamaEsquerda) { //IF  k_variaveis.v_habilita_mama_direita = 'S'
			validarAchadosRadiologicosMamaEsquerda(mapMama, listaException, vHabilitaMamaDireita, vSexoPaciente);
		}
		
		//Valida Nodulos 1,2 e 3 da direita e da esquerda
		validarNodulos(mapMama,listaException,vHabilitaMamaEsquerda,vHabilitaMamaDireita);
		
		//Valida Microcalcificações 1,2 e 3 da direita e da esquerda
		validarMicrocalcificacoes(mapMama,listaException,vHabilitaMamaEsquerda,vHabilitaMamaDireita);
		
		//Valida Assimetria/Distorção Focal 1 e 2 da direita e da esquerda
		validarAssimetriaDistorcaoFocal(mapMama,listaException,vHabilitaMamaEsquerda,vHabilitaMamaDireita);
		
		//Valida Assimetria Difusa/Area densa 1 e 2 da direita e da esquerda
		validarAssimetriaDifusaAreaDensa(mapMama,listaException,vHabilitaMamaEsquerda,vHabilitaMamaDireita);
		
		//Valida Linfonodos Axilares direita e esquerda
		validarLinfonodoAxilar(mapMama, listaException,vHabilitaMamaEsquerda,vHabilitaMamaDireita);
		
		if(medicoResponsavel == null){
		//if(mapConclusao.get(DominioSismamaMamoCadCodigo.L_VSS_NOME.toString()).getResposta().equals(null)) {
			getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03328,listaException);
		}
		
		if(listaException.hasException()){
			throw listaException;
		}
		
	}
	
	private void validarNumFilmesRecomendacoesHabilitadas(boolean vHabilitaMamaEsquerda, boolean vHabilitaMamaDireita,
			Map<String, AelSismamaMamoResVO> mapMama,BaseListException listaException, String vSexoPaciente){

		if(vHabilitaMamaEsquerda && vHabilitaMamaDireita) {
			
			List<String> lados = obterLados(vHabilitaMamaEsquerda, vHabilitaMamaDireita);
			
			for(String lado : lados){
				
				String key = "C_RAD_NUM_FILM_".concat(lado);
				AelSismamaMamoResVO item = mapMama.get(key);
				
				if(item != null && item.getNumeroFilmes() != null && item.getNumeroFilmes() == 0) {
					
					getResultadoMamografiaUtil().incluirException(ConsisteDadosRNExceptionCode.AEL_03245,listaException);
					
				}
			}
			
			validacaoMamaNumFilm(mapMama, listaException, vSexoPaciente,vHabilitaMamaEsquerda, vHabilitaMamaDireita);
		}
	}
	
	public List<AelSismamaMamoRes> obterMapaRespostasMamo(Integer soeSeq, Short seqp) {
		return getLaudoMamografiaRN().obterMapaRespostasMamo(soeSeq,seqp);
	}
	
	public List<AelSismamaMamoRes> obterRespostasMamografiaRespNull(Integer soeSeq, Short seqp) {
		return getLaudoMamografiaRN().obterRespostasMamografiaRespNull(soeSeq,seqp);
	}
	
	/**
	 * @ORADB p_imprime_laudo
	 * @param AelItemSolicitacaoExames
	 */
	public Integer imprimirLaudo(AelItemSolicitacaoExames ise) {
	
		Integer vTipoVel = 2; 
		
		if(ise !=  null && ise.getSolicitacaoExame() != null && ise.getSolicitacaoExame().getAtendimento() != null) {
			List<AghAtendimentos> atendimentos = new ArrayList<AghAtendimentos>();
			atendimentos = getAghuFacade().buscarAtendimentosPaciente(ise.getSolicitacaoExame().getAtendimento().getProntuario(),
																			 ise.getSolicitacaoExame().getAtendimento().getSeq());
		
		
			if(atendimentos != null && atendimentos.size() > 0) {
				if(ise.getSolicitacaoExame().getAtendimentoDiverso() != null 
				   && ise.getSolicitacaoExame().getAtendimentoDiverso().getSeq() != null
				   || ( atendimentos.get(0).getOrigem().equals(DominioOrigemAtendimento.X.toString()) 
					   || atendimentos.get(0).getOrigem().equals(DominioOrigemAtendimento.D.toString()) 
					  )
				   || ( atendimentos.get(0).getOrigem().equals(DominioOrigemAtendimento.A.toString()) && atendimentos.get(0).getProntuario() == null  )) 
				    {
					
					vTipoVel = 3;
					
				   }
			}
		}
		return vTipoVel;
	}
	
}