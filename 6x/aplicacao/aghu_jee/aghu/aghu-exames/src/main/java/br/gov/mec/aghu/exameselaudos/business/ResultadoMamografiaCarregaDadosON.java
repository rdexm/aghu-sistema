package br.gov.mec.aghu.exameselaudos.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioCategoriaBiRadsMamografia;
import br.gov.mec.aghu.dominio.DominioComposicaoMama;
import br.gov.mec.aghu.dominio.DominioContornoNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioDistribuicaoMicrocalcificacaoMamografia;
import br.gov.mec.aghu.dominio.DominioFormaMicrocalcificacaoMamografia;
import br.gov.mec.aghu.dominio.DominioLimiteNoduloMamografia;
import br.gov.mec.aghu.dominio.DominioLinfonodosAxilaresMamografia;
import br.gov.mec.aghu.dominio.DominioLocalizacaoMamografia;
import br.gov.mec.aghu.dominio.DominioPeleMamografia;
import br.gov.mec.aghu.dominio.DominioRecomendacaoMamografia;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioTamanhoNoduloMamografia;
import br.gov.mec.aghu.exameselaudos.sismama.vo.AelSismamaMamoResVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Utilizada pela estoria #5978 - Preencher achados radiologicos do resultado da mamografia (SISMAMA)
 *
 */
@Stateless
public class ResultadoMamografiaCarregaDadosON extends BaseBusiness {


@EJB
private LaudoMamografiaRN laudoMamografiaRN;

private static final Log LOG = LogFactory.getLog(ResultadoMamografiaCarregaDadosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	/**
	 * 
	 */
	private static final long serialVersionUID = 6116332235578506589L;

	protected LaudoMamografiaRN getLaudoMamografiaRN() {
		return laudoMamografiaRN;
	}	
	
	/**
	 * Regra de tela - p_carrega_dados_inf_clinica
	 * @param ise
	 * @return
	 */
	public String carregarInformacoesClinicas(AelItemSolicitacaoExames ise) {
		String resposta = getLaudoMamografiaRN().obterRespostaMamo(ise.getId().getSoeSeq(), ise.getId().getSeqp(), DominioSismamaMamoCadCodigo.C_INF_CLINICA);
		if (StringUtils.isEmpty(resposta)) {
			resposta = ise.getSolicitacaoExame().getInformacoesClinicas();
		}
		return resposta;
	}	
	
	/**
	 * Recebe um Map e verifica se ele contem C_RAD_NUM_FILM_E. Se positivo, quer dizer que é o Map da esquerda e troca a ultima letra de D para E.
	 * Caso contrario, retorna o que ele recebeu, que já é uma propriedade da direita.
	 */
	private String recuperarStringDominio(Map<String, AelSismamaMamoResVO> mapMama, String dominio) {
		if (mapMama.containsKey(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString())) {
			return StringUtils.removeEnd(dominio, "D").concat("E");
		}
		return dominio;
	}

	/**
	 * Recebe um Map e verifica se ele contem C_RAD_NUM_FILM_E. Se positivo, quer dizer que é o Map da esquerda e troca a ultima letra de D para E.
	 * Caso contrario, retorna o que ele recebeu, que já é uma propriedade da direita.
	 */
	private DominioSismamaMamoCadCodigo recuperarDominio(Map<String, AelSismamaMamoResVO> mapMama, DominioSismamaMamoCadCodigo dominio) {
		if (mapMama.containsKey(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_E.toString())) {
			return DominioSismamaMamoCadCodigo.valueOf(StringUtils.removeEnd(dominio.toString(), "D").concat("E"));		
		}
		return dominio;
	}
	
	/**
	 * Regra de tela - p_carrega_dados_mama_dir - p_carrega_dados_mama_esq  
	 */
	public void carregarDadosMama(List<AelSismamaMamoRes> sisMamoRes, String habilitaMama, Map<String, AelSismamaMamoResVO> mapMama) {
		if (habilitaMama.equals("S")) {
			//mapeia as respostas para rápida consulta
			Map<String,AelSismamaMamoRes> mapResposta = new HashMap<String, AelSismamaMamoRes>();
			for(AelSismamaMamoRes res : sisMamoRes){
				mapResposta.put(res.getAelSismamaMamoCad().getCodigo(), res);
			}
			
			List<DominioSismamaMamoCadCodigo> listPerguntas = obterPerguntas();
			
			
			for (DominioSismamaMamoCadCodigo perg : listPerguntas) {
				adicionarRespostaMap(mapMama, mapResposta, recuperarStringDominio(mapMama, perg.toString()));
			}
		}
	}

	private List<DominioSismamaMamoCadCodigo> obterPerguntas() {
		List<DominioSismamaMamoCadCodigo> listPerguntas = new ArrayList<DominioSismamaMamoCadCodigo>();
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_RAD_PELE_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_RAD_COMPOSIC_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_NOD_SIM_01D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_NOD_SIM_02D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_NOD_SIM_03D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_MICRO_SIM_01D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_MICRO_SIM_02D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_MICRO_SIM_03D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM01D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM02D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM01D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM02D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM01D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM02D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM01D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_AR_DENS_SIM02D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_LINF_AUX_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_DILAT_DUC_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_NOD_DEN_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_NOD_CAL_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_NOD_DEN_HET_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_CALC_VASC_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_CALC_ASP_BEN_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_LINF_INTRAM_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_DIS_ARQ_CIR_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_IMP_INTEG_D);
		listPerguntas.add(DominioSismamaMamoCadCodigo.C_IMP_SIN_RUP_D);
		return listPerguntas;
	}
	
	private void adicionarRespostaMap(Map<String, AelSismamaMamoResVO> mapMama,Map<String,AelSismamaMamoRes> mapResposta, String chaveDominio) {
		AelSismamaMamoResVO vo = new AelSismamaMamoResVO();
		
		AelSismamaMamoRes res = mapResposta.get(chaveDominio);
		
		if(res != null){
			preencherCamposPrincipais(mapMama, res, chaveDominio, vo);
			preencherCamposFieldsetNodulos(mapMama, res, chaveDominio, vo, mapResposta);
			preencherCamposFieldsetMicrocalcificacoes(mapMama, res, chaveDominio, vo, mapResposta);
			preencherCamposAssimetria(mapMama, res, chaveDominio, vo, mapResposta);
			preencherCamposAssimetriaDifusa(mapMama, res, chaveDominio, vo, mapResposta);
			preencherCamposDistorcaoFocal(mapMama, res, chaveDominio, vo, mapResposta);
			preencherCamposAreaDensa(mapMama, res, chaveDominio, vo, mapResposta);
			preencherCamposlinfonodosAxilares(mapMama, res, chaveDominio, vo, mapResposta);
			preencherCamposOutrosAchados(mapMama, res, chaveDominio, vo);
		}
	}	
	
	private void preencherCamposPrincipais(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo) {
		if (!StringUtils.isEmpty(res.getResposta())) {
			if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_RAD_NUM_FILM_D.toString()))) {
				vo.setNumeroFilmes(Short.parseShort(res.getResposta()));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_RAD_PELE_D.toString()))) {
				vo.setPele(DominioPeleMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_RAD_COMPOSIC_D.toString()))) {
				vo.setComposicao(DominioComposicaoMama.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			}
		}
	}
	
	private void preencherCamposFieldsetNodulos(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo, Map<String,AelSismamaMamoRes> mapResposta) {
		if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_SIM_01D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta,
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_LOC_01D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_TAM_01D), 
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_CONT_01D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_LIM_01D));
		} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_SIM_02D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta,
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_LOC_02D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_TAM_02D), 
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_CONT_02D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_LIM_02D));
		} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_SIM_03D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta,
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_LOC_03D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_TAM_03D), 
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_CONT_03D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_LIM_03D));
		} 
	}
	
	private void preencherCamposFieldsetMicrocalcificacoes(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo, Map<String,AelSismamaMamoRes> mapResposta) {
		if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_SIM_01D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio,  mapResposta,
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_LOC_01D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_FORM_01D),
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_01D));
		} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_SIM_02D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta,
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_LOC_02D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_FORM_02D),
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_02D));
		} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_SIM_03D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta,
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_LOC_03D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_FORM_03D),
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_MICRO_DISTR_03D));
		}
	}
	
	private void preencherCamposAssimetria(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo, Map<String,AelSismamaMamoRes> mapResposta) {
		if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM01D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC01D));
		} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_ASSI_FOC_SIM02D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_ASSI_FOC_LOC02D));
		}
	}
	
	private void preencherCamposAssimetriaDifusa(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo, Map<String,AelSismamaMamoRes> mapResposta) {
		if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM01D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC01D));
		} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_ASSI_DIF_SIM02D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_ASSI_DIFU_LOC02D));
		}		
	}
	
	private void preencherCamposDistorcaoFocal(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo, Map<String,AelSismamaMamoRes> mapResposta) {
		if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM01D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC01D));
		} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_DIS_FOC_SIM02D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_DIS_FOC_LOC02D));
		}		
	}
	
	private void preencherCamposAreaDensa(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo, Map<String,AelSismamaMamoRes> mapResposta) {
		if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_AR_DENS_SIM01D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_AR_DENS_LOC01D));
		} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_AR_DENS_SIM02D.toString()))) {
			validarCamposChecked(vo, res, mapMama, chaveDominio, mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_AR_DENS_LOC02D));
		}		
	}
	
	private void preencherCamposlinfonodosAxilares(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo, Map<String,AelSismamaMamoRes> mapResposta) {
		if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_LINF_AUX_D.toString()))) {
			if (!StringUtils.isEmpty(res.getResposta())) {
				vo.setLinfonodoAxilar(DominioLinfonodosAxilaresMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			}
			preencherCampos(mapMama, vo.getLinfonodoAxilar(), mapResposta, recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_LINF_AUX_AUM_D), 
					recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_LINF_AUX_DENSO_D), recuperarDominio(mapMama, DominioSismamaMamoCadCodigo.C_LINF_AUX_CONF_D));
		}
	}
	
	private void preencherCamposOutrosAchados(Map<String, AelSismamaMamoResVO> mapMama, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo) {
		if (!StringUtils.isEmpty(res.getResposta())) {
			if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_LINF_AUX_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_DILAT_DUC_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_DEN_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_CAL_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_NOD_DEN_HET_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_CALC_VASC_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_CALC_ASP_BEN_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_LINF_INTRAM_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_DIS_ARQ_CIR_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_IMP_INTEG_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			} else if (chaveDominio.equals(recuperarStringDominio(mapMama, DominioSismamaMamoCadCodigo.C_IMP_SIN_RUP_D.toString()))) {
				vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
				mapMama.put(chaveDominio, vo);
			}
		}
	}
	
	private void validarCamposChecked(AelSismamaMamoResVO vo, AelSismamaMamoRes res, Map<String, AelSismamaMamoResVO> mapMama,
		String chaveDominio, Map<String,AelSismamaMamoRes> mapResposta, DominioSismamaMamoCadCodigo... dominios) {
		
		if (!StringUtils.isEmpty(res.getResposta())) {
			vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
			mapMama.put(chaveDominio, vo);
		}
		
		if (vo.isChecked()) {
			preencherCampos(mapMama, null, mapResposta,dominios);
		} else {
			setarValoresNulos(mapMama, dominios);
		}		
	}
	
	private void setarValoresNulos(Map<String, AelSismamaMamoResVO> map, DominioSismamaMamoCadCodigo... dominios) {
		for (DominioSismamaMamoCadCodigo dominio : dominios) {
			if (dominio.toString().contains("LOC")) {
				AelSismamaMamoResVO vo = new AelSismamaMamoResVO();
				vo.setLocalizacao(DominioLocalizacaoMamografia.DEFAULT);
				map.put(dominio.toString(), vo);
			} else {
				map.put(dominio.toString(), new AelSismamaMamoResVO());
			}
		}
	}
	
	private void preencherCampos(Map<String, AelSismamaMamoResVO> mapMama,  
			DominioLinfonodosAxilaresMamografia dominioLinfonodoAxilar,	Map<String,AelSismamaMamoRes> mapResposta, 
			DominioSismamaMamoCadCodigo... dominios) {
		
		for (DominioSismamaMamoCadCodigo dominio : dominios) {
			
			AelSismamaMamoRes res = mapResposta.get(dominio.toString());
			
			if(res != null){
				AelSismamaMamoResVO vo = new AelSismamaMamoResVO();				
				
				if (dominio.toString().contains("LOC") 
					|| dominio.toString().contains("FOC") 
					|| dominio.toString().contains("DIFU")) {
					vo.setLocalizacao(DominioLocalizacaoMamografia.getDominioPorCodigo(res.getResposta()));
				} else if (dominio.toString().contains("TAM")) {
					vo.setTamanho(DominioTamanhoNoduloMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				} else if (dominio.toString().contains("CONT")) {
					vo.setContorno(DominioContornoNoduloMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				} else if (dominio.toString().contains("LIM")) {
					vo.setLimite(DominioLimiteNoduloMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				} else if (dominio.toString().contains("FORM")) {
					vo.setForma(DominioFormaMicrocalcificacaoMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				} else if (dominio.toString().contains("DISTR")) {
					vo.setDistribuicao(DominioDistribuicaoMicrocalcificacaoMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				} else if (dominio.toString().contains("AUX")) {
					if (dominioLinfonodoAxilar != null) {
						vo.setChecked(false);
					} else {
						vo.setChecked(carregarNoduloChecked(Integer.parseInt(res.getResposta())));
					}
				}
				mapMama.put(dominio.toString(), vo);
			}
		}		
	}	
	
	private boolean carregarNoduloChecked(Integer resposta) {
		if (resposta == 3) {
			return true;
		}
		
		return false;
	}

	/**
	 * Regra de Tela - p_carrega_dados_conc_diag 
	 */
	public void carregarDadosConcDiag(List<AelSismamaMamoRes> sisMamoRes, Map<String, AelSismamaMamoResVO> mapConclusao) {
		for (AelSismamaMamoRes res : sisMamoRes) {
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D.toString());
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_E.toString());
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_CON_RECOM_D.toString());
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_CON_RECOM_E.toString());
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_OBS_GERAIS.toString());
			
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_RESPONSAVEL.toString());
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_SER_MATR_RESID.toString());
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_SER_VIN_COD_RESID.toString());
			adicionarRespostaMapConclusao(mapConclusao, res, DominioSismamaMamoCadCodigo.C_RESIDENTE.toString());
		}
	}
	
	private void adicionarRespostaMapConclusao(Map<String, AelSismamaMamoResVO> mapConclusao, AelSismamaMamoRes res, String chaveDominio) {
		AelSismamaMamoResVO vo = new AelSismamaMamoResVO();
		
		if (res.getAelSismamaMamoCad().getCodigo().equals(chaveDominio)) {
			preencherCamposAbaConclusao(mapConclusao, res, chaveDominio, vo);
		}
	}	

	private void preencherCamposAbaConclusao(Map<String, AelSismamaMamoResVO> mapConclusao, AelSismamaMamoRes res, String chaveDominio, AelSismamaMamoResVO vo) {
		if (chaveDominio.equals(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_D.toString())) {
			if (!StringUtils.isEmpty(res.getResposta())) {
				vo.setCategoria(DominioCategoriaBiRadsMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				mapConclusao.put(chaveDominio, vo);
			}
		} else if (chaveDominio.equals(DominioSismamaMamoCadCodigo.C_CON_DIA_CAT_E.toString())) {
			if (!StringUtils.isEmpty(res.getResposta())) {
				vo.setCategoria(DominioCategoriaBiRadsMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
				mapConclusao.put(chaveDominio, vo);
			}
		} else if (chaveDominio.equals(DominioSismamaMamoCadCodigo.C_CON_RECOM_D.toString())) {
			if (!StringUtils.isEmpty(res.getResposta())) {
				setarValoresRecomendacao(mapConclusao, res, chaveDominio, vo, DominioSismamaMamoCadCodigo.C_CON_RECOM_D);
			}
		} else if (chaveDominio.equals(DominioSismamaMamoCadCodigo.C_CON_RECOM_E.toString())) {
			if (!StringUtils.isEmpty(res.getResposta())) {
				setarValoresRecomendacao(mapConclusao, res, chaveDominio, vo, DominioSismamaMamoCadCodigo.C_CON_RECOM_E);
			}
		} else if (chaveDominio.equals(DominioSismamaMamoCadCodigo.C_OBS_GERAIS.toString())) {
			if (!StringUtils.isEmpty(res.getResposta())) {
				vo.setObservacoes(res.getResposta().replaceAll("<br>", "\n"));
				mapConclusao.put(chaveDominio, vo);
			}
		}
	}
	
	private void setarValoresRecomendacao(Map<String, AelSismamaMamoResVO> mapConclusao, AelSismamaMamoRes res, String chaveDominio, 
		AelSismamaMamoResVO vo, DominioSismamaMamoCadCodigo dominio) {
		if (Integer.parseInt(res.getResposta()) == 0) {
			setarValoresNulos(mapConclusao, dominio);
		} else {
			vo.setRecomendacao(DominioRecomendacaoMamografia.getDominioPorCodigo(Integer.parseInt(res.getResposta())));
			mapConclusao.put(chaveDominio, vo);
		}
	}
}
