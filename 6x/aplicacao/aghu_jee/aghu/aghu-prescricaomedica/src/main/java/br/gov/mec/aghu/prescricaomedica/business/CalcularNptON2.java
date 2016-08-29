package br.gov.mec.aghu.prescricaomedica.business;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoComponenteNpt;
import br.gov.mec.aghu.dominio.DominioTipoLipidio;
import br.gov.mec.aghu.dominio.DominioTipoVolume;
import br.gov.mec.aghu.prescricaomedica.vo.AfaParamNptGlicose50VO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaParamNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.CalculoAdultoNptVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * #989 - ON auxiliar responsável pelo cálculo inicial na abertura da tela
 * @author israel.haas
 *
 */
@Stateless
@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class CalcularNptON2 extends BaseBusiness {

	private static final long serialVersionUID = -7739190001114008217L;

	private static final Log LOG = LogFactory.getLog(CalcularNptON2.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 1
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 */
	public void mpmCalculaComponentesNptParte1(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO) {
		
		if (calculoAdultoNptVO.getCalculoParametrosFixosVO().getCalculaNpt().equals(Boolean.FALSE)) {
			return;
		}
		
		BigDecimal volSolucao = null;
		BigDecimal volLipidios = null;
		
		if (tipo.equals(DominioTipoComponenteNpt.TEMPO_INFUSAO_SOLUCAO) || tipo.equals(DominioTipoComponenteNpt.GOTEJO_SOLUCAO)) {
			BigDecimal volAa10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAa10());
			BigDecimal volAaPed10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAaPed10());
			BigDecimal volAcetZn = this.obterValorNotNull(calculoAdultoNptVO.getVolAcetZn());
			BigDecimal volNacl20 = this.obterValorNotNull(calculoAdultoNptVO.getVolNacl20());
			BigDecimal volGlucoCa = this.obterValorNotNull(calculoAdultoNptVO.getVolGlucoCa());
			BigDecimal volKcl = this.obterValorNotNull(calculoAdultoNptVO.getVolKcl());
			BigDecimal volK3po4 = this.obterValorNotNull(calculoAdultoNptVO.getVolK3Po4());
			BigDecimal volMgso4 = this.obterValorNotNull(calculoAdultoNptVO.getVolMgso4());
			BigDecimal volGlicose5 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose5());
			BigDecimal volGlicose10 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose10());
			BigDecimal volGlicose50 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose50());
			BigDecimal volOligo = this.obterValorNotNull(calculoAdultoNptVO.getVolOligo());
			BigDecimal volOligoPed = this.obterValorNotNull(calculoAdultoNptVO.getVolOligoPed());
			
			volSolucao = volAa10.add(volAaPed10).add(volAcetZn).add(volNacl20).add(volGlucoCa)
					.add(volKcl).add(volK3po4).add(volMgso4).add(volGlicose5).add(volGlicose10).add(volGlicose50)
					.add(volOligo).add(volOligoPed);
			
			if (calculoAdultoNptVO.getVolHeparina().compareTo(BigDecimal.ZERO) > 0) {
				volSolucao = volSolucao.add(calculoAdultoNptVO.getVolHeparina()
						.divide(new BigDecimal(calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO().getConvMl())));
			}
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.GOTEJO_LIPIDIOS) || tipo.equals(DominioTipoComponenteNpt.TEMPO_INFUSAO_LIPIDIOS)) {
			
			volLipidios = this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios10()).add(this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios20()));
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.GOTEJO_SOLUCAO)) {
			BigDecimal volSolucTemp = volSolucao.divide(calculoAdultoNptVO.getGotejoSolucao(), 0, RoundingMode.HALF_UP);
			calculoAdultoNptVO.setParamTempInfusaoSol(volSolucTemp.shortValue());
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamTempInfusaoSol(volSolucTemp.doubleValue());
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.GOTEJO_LIPIDIOS)) {
			BigDecimal volLipTemp = volLipidios.divide(calculoAdultoNptVO.getGotejoLipidios(), 0, RoundingMode.HALF_UP);
			calculoAdultoNptVO.setParamTempInfusaoLip(volLipTemp.shortValue());
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamTempInfusaoLip(volLipTemp.doubleValue());
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.TEMPO_INFUSAO_SOLUCAO)) {
			if (calculoAdultoNptVO.getParamTempInfusaoSol().compareTo((short) 0) > 0
					&& volSolucao.compareTo(BigDecimal.ZERO) > 0) {
				
				BigDecimal gotejoSolTemp = volSolucao.divide(new BigDecimal(calculoAdultoNptVO.getParamTempInfusaoSol()), 1, RoundingMode.HALF_UP);
				calculoAdultoNptVO.setGotejoSolucao(gotejoSolTemp);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoSolucao(gotejoSolTemp.doubleValue());
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte2(tipo, calculoAdultoNptVO, volLipidios, volLipidios);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 2
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 */
	private void mpmCalculaComponentesNptParte2(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios) {
		
		if (tipo.equals(DominioTipoComponenteNpt.TEMPO_INFUSAO_LIPIDIOS)) {
			if (calculoAdultoNptVO.getParamTempInfusaoLip().compareTo((short) 0) > 0
					&& volLipidios.compareTo(BigDecimal.ZERO) > 0) {
				
				BigDecimal gotejoLipTemp = volLipidios.divide(new BigDecimal(calculoAdultoNptVO.getParamTempInfusaoLip()), 1, RoundingMode.HALF_UP);
				calculoAdultoNptVO.setGotejoLipidios(gotejoLipTemp);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoLipidios(gotejoLipTemp.doubleValue());
			}
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_LIPIDIOS_10)) {
			if (calculoAdultoNptVO.getVolLipidios10() == null
					&& calculoAdultoNptVO.getVolLipidios20() == null) {
				
				calculoAdultoNptVO.setParamLip(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamLip((double) 0);
				calculoAdultoNptVO.setTotParamLip(null);
				calculoAdultoNptVO.setTotParamLipEd(null);
				
			} else if (calculoAdultoNptVO.getVolLipidios10().compareTo(BigDecimal.ZERO) > 0) {
				AfaParamNptVO lip10VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO();
				switch (lip10VO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramLipTemp = ((calculoAdultoNptVO.getVolLipidios10().multiply(new BigDecimal(lip10VO.getConvMl())))
							.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamLip(paramLipTemp);
					
					BigDecimal totParamLip = paramLipTemp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamLip(totParamLip);
					break;
				case M:
					BigDecimal paramLipScTemp = ((calculoAdultoNptVO.getVolLipidios10().multiply(new BigDecimal(lip10VO.getConvMl())))
							.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamLip(paramLipScTemp);
					
					BigDecimal totParamLipSc = paramLipScTemp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamLip(totParamLipSc);
					break;
				default:
					break;
				}
				calculoAdultoNptVO.setTotParamLipEd(calculoAdultoNptVO.getTotParamLip().toString().concat(" ").concat(lip10VO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamLip(calculoAdultoNptVO.getParamLip().doubleValue());
			}
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_LIPIDIOS_20)) {
			if (calculoAdultoNptVO.getVolLipidios10() == null
					&& calculoAdultoNptVO.getVolLipidios20() == null) {
				
				calculoAdultoNptVO.setParamLip(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamLip(null);
				calculoAdultoNptVO.setTotParamLip(null);
				calculoAdultoNptVO.setTotParamLipEd(null);
				
			} else if (calculoAdultoNptVO.getVolLipidios20().compareTo(BigDecimal.ZERO) > 0) {
				AfaParamNptVO lip20VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO();
				switch (lip20VO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramLipTemp = ((calculoAdultoNptVO.getVolLipidios20().multiply(new BigDecimal(lip20VO.getConvMl())))
							.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamLip(paramLipTemp);
					
					BigDecimal totParamLip = paramLipTemp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamLip(totParamLip);
					break;
				case M:
					BigDecimal paramLipScTemp = ((calculoAdultoNptVO.getVolLipidios20().multiply(new BigDecimal(lip20VO.getConvMl())))
							.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamLip(paramLipScTemp);
					
					BigDecimal totParamLipSc = paramLipScTemp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamLip(totParamLipSc);
					break;
				default:
					break;
				}
				calculoAdultoNptVO.setTotParamLipEd(calculoAdultoNptVO.getTotParamLip().toString().concat(" ").concat(lip20VO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamLip(calculoAdultoNptVO.getParamLip().doubleValue());
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte3(tipo, calculoAdultoNptVO, volSolucao, volLipidios);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 3
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 */
	private void mpmCalculaComponentesNptParte3(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios) {
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_AA_10)) {
			if (calculoAdultoNptVO.getVolAa10() == null
					&& calculoAdultoNptVO.getVolAaPed10() == null) {
				
				calculoAdultoNptVO.setParamAmin(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamAmin(null);
				calculoAdultoNptVO.setTotParamAmin(null);
				calculoAdultoNptVO.setTotParamAminEd(null);
				
			} else if (calculoAdultoNptVO.getVolAaPed10().compareTo(BigDecimal.ZERO) > 0) {
				AfaParamNptVO aapVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO();
				switch (aapVO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramAminTemp = ((calculoAdultoNptVO.getVolAaPed10().multiply(new BigDecimal(aapVO.getConvMl())))
							.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamAmin(paramAminTemp);
					
					BigDecimal totParamAmin = paramAminTemp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamAmin(totParamAmin);
					break;
				case M:
					BigDecimal paramAminScTemp = ((calculoAdultoNptVO.getVolAaPed10().multiply(new BigDecimal(aapVO.getConvMl())))
							.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamAmin(paramAminScTemp);
					
					BigDecimal totParamAminSc = paramAminScTemp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamAmin(totParamAminSc);
					break;
				default:
					break;
				}
				calculoAdultoNptVO.setTotParamAminEd(calculoAdultoNptVO.getTotParamAmin().toString().concat(" ").concat(aapVO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamAmin(calculoAdultoNptVO.getParamAmin().doubleValue());
			}
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_NACL_20) || tipo.equals(DominioTipoComponenteNpt.RECALCULA)) {
			if (calculoAdultoNptVO.getVolNacl20() == null) {
				calculoAdultoNptVO.setParamSodio(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamSodio(null);
				calculoAdultoNptVO.setTotParamSodio(null);
				calculoAdultoNptVO.setTotParamSodioEd(null);
				
			} else {
				AfaParamNptVO sodVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO();
				switch (sodVO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramSodTemp = ((calculoAdultoNptVO.getVolNacl20().multiply(new BigDecimal(sodVO.getConvMl())))
							.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamSodio(paramSodTemp);
					
					BigDecimal totParamSod = paramSodTemp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamSodio(totParamSod);
					break;
				case M:
					BigDecimal paramSodScTemp = ((calculoAdultoNptVO.getVolNacl20().multiply(new BigDecimal(sodVO.getConvMl())))
							.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamSodio(paramSodScTemp);
					
					BigDecimal totParamSodSc = paramSodScTemp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamSodio(totParamSodSc);
					break;
				default:
					break;
				}
				calculoAdultoNptVO.setTotParamSodioEd(calculoAdultoNptVO.getTotParamSodio().toString().concat(" ").concat(sodVO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamSodio(calculoAdultoNptVO.getParamSodio().doubleValue());
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte4(tipo, calculoAdultoNptVO, volSolucao, volLipidios);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 4
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 */
	private void mpmCalculaComponentesNptParte4(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios) {
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_KCL) || tipo.equals(DominioTipoComponenteNpt.VOL_K3PO4) || tipo.equals(DominioTipoComponenteNpt.RECALCULA)) {
			BigDecimal paramKcl = null;
			BigDecimal totParamKcl = null;
			
			AfaParamNptVO kclVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO();
			switch (kclVO.getTpParamCalcUsado()) {
			case K:
				paramKcl = (calculoAdultoNptVO.getVolKcl().multiply(new BigDecimal(kclVO.getConvMl())))
					.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()));
				totParamKcl = paramKcl.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()));
				break;
			case M:
				paramKcl = (calculoAdultoNptVO.getVolKcl().multiply(new BigDecimal(kclVO.getConvMl())))
					.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()));
				totParamKcl = paramKcl.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()));
				break;

			default:
				break;
			}
			
			BigDecimal paramKpo = null;
			BigDecimal totParamKpo = null;
			
			AfaParamNptVO kpoVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO();
			switch (kpoVO.getTpParamCalcUsado()) {
			case K:
				paramKpo = (calculoAdultoNptVO.getVolK3Po4().multiply(new BigDecimal(kpoVO.getConvMl())))
					.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()));
				totParamKpo = paramKpo.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()));
				break;
			case M:
				paramKpo = (calculoAdultoNptVO.getVolK3Po4().multiply(new BigDecimal(kpoVO.getConvMl())))
					.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()));
				totParamKpo = paramKpo.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()));
				break;

			default:
				break;
			}
			
			if (paramKcl == null) {
				paramKcl = BigDecimal.ZERO;
				totParamKcl = BigDecimal.ZERO;
			}
			
			if (paramKpo == null) {
				paramKpo = BigDecimal.ZERO;
				totParamKpo = BigDecimal.ZERO;
			}
			
			this.mpmCalculaComponentesNptParte4Auxiliar(calculoAdultoNptVO, paramKcl, paramKpo, totParamKcl, totParamKpo, kclVO, kpoVO);
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte5(tipo,calculoAdultoNptVO, volSolucao, volLipidios);
	}
	
	private void mpmCalculaComponentesNptParte4Auxiliar(CalculoAdultoNptVO calculoAdultoNptVO,
			BigDecimal paramKcl, BigDecimal paramKpo, BigDecimal totParamKcl, BigDecimal totParamKpo,
			AfaParamNptVO kclVO, AfaParamNptVO kpoVO) {
		
		if (paramKcl.equals(BigDecimal.ZERO) && paramKpo.equals(BigDecimal.ZERO)) {
			calculoAdultoNptVO.setParamPotassio(null);
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPotassio(null);
			calculoAdultoNptVO.setTotParamPotassio(null);
			calculoAdultoNptVO.setTotParamPotassioEd(null);
			calculoAdultoNptVO.setParamPercKcl(new BigDecimal(70));
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercKcl((double) 70);
			calculoAdultoNptVO.setParamPercK3po4(new BigDecimal(30));
			// TODO -- Possível defeito, corrigir? O correto seria paramPercK3po4()?
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercKcl((double) 30);
			
		} else {
			calculoAdultoNptVO.setParamPotassio(paramKcl.add(paramKpo, new MathContext(3, RoundingMode.HALF_UP)));
			calculoAdultoNptVO.setTotParamPotassio(totParamKcl.add(totParamKpo, new MathContext(3, RoundingMode.HALF_UP)));
			calculoAdultoNptVO.setTotParamPotassioEd(calculoAdultoNptVO.getTotParamPotassio().toString().concat(" ").concat(kclVO.getUmmDescricao()));
			
			if (calculoAdultoNptVO.getVolKcl() != null) {
				calculoAdultoNptVO.setTotParamKclEd(totParamKcl.round(new MathContext(3, RoundingMode.HALF_UP)).toString()
						.concat(" ").concat(kclVO.getUmmDescricao()));
				
			} else {
				calculoAdultoNptVO.setTotParamKclEd(null);
			}
			
			if (calculoAdultoNptVO.getVolK3Po4() != null) {
				calculoAdultoNptVO.setTotParamK3po4Ed(totParamKpo.round(new MathContext(3, RoundingMode.HALF_UP)).toString()
						.concat(" ").concat(kpoVO.getUmmDescricao()));
				
			} else {
				calculoAdultoNptVO.setTotParamKclEd(null);
			}
			
			if (paramKcl.compareTo(BigDecimal.ZERO) > 0 && paramKpo.compareTo(BigDecimal.ZERO) > 0) {
				calculoAdultoNptVO.setParamPercKcl(new BigDecimal(100));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercKcl((double) 100);
				calculoAdultoNptVO.setParamPercK3po4(BigDecimal.ZERO);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercK3po4((double) 0);
			}
			
			if (paramKcl.equals(BigDecimal.ZERO) && paramKpo.compareTo(BigDecimal.ZERO) > 0) {
				calculoAdultoNptVO.setParamPercKcl(BigDecimal.ZERO);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercKcl((double) 0);
				calculoAdultoNptVO.setParamPercK3po4(new BigDecimal(100));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercK3po4((double) 100);
			}
		}
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 5
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 */
	private void mpmCalculaComponentesNptParte5(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios) {
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_MGSO4) || tipo.equals(DominioTipoComponenteNpt.RECALCULA)) {
			if (calculoAdultoNptVO.getVolMgso4() == null) {
				calculoAdultoNptVO.setParamMagnesio(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPotassio(null);
				calculoAdultoNptVO.setTotParamMagnesio(null);
				calculoAdultoNptVO.setTotParamMagnesioEd(null);
				
			} else {
				AfaParamNptVO magVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptSulfatoMagnesioVO();
				switch (magVO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramMag = ((calculoAdultoNptVO.getVolMgso4().multiply(new BigDecimal(magVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamMagnesio(paramMag);
					
					BigDecimal totParamMag = paramMag.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamMagnesio(totParamMag);
					break;
				case M:
					BigDecimal paramMagSc = ((calculoAdultoNptVO.getVolMgso4().multiply(new BigDecimal(magVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamMagnesio(paramMagSc);
				
					BigDecimal totParamMagSc = paramMagSc.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamMagnesio(totParamMagSc);
					break;

				default:
					break;
				}
				calculoAdultoNptVO.setTotParamMagnesioEd(calculoAdultoNptVO.getTotParamMagnesio().toString().concat(" ").concat(magVO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPotassio(calculoAdultoNptVO.getParamPotassio().doubleValue());
			}
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_GLUCO_CA)) {
			if (calculoAdultoNptVO.getVolGlucoCa() == null) {
				calculoAdultoNptVO.setParamCalcio(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamCalcio(null);
				calculoAdultoNptVO.setTotParamCalcio(null);
				calculoAdultoNptVO.setTotParamCalcioEd(null);
				
			} else {
				AfaParamNptVO calVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO();
				switch (calVO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramCal = ((calculoAdultoNptVO.getVolGlucoCa().multiply(new BigDecimal(calVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamCalcio(paramCal);
					
					BigDecimal totParamCal = paramCal.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamCalcio(totParamCal);
					break;
				case M:
					BigDecimal paramCalSc = ((calculoAdultoNptVO.getVolGlucoCa().multiply(new BigDecimal(calVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamCalcio(paramCalSc);
				
					BigDecimal totParamCalSc = paramCalSc.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(4, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamCalcio(totParamCalSc);
					break;

				default:
					break;
				}
				calculoAdultoNptVO.setTotParamCalcioEd(calculoAdultoNptVO.getTotParamCalcio().toString().concat(" ").concat(calVO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamCalcio(calculoAdultoNptVO.getParamCalcio().doubleValue());
			}
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_OLIGO)) {
			if (calculoAdultoNptVO.getVolOligo() == null && calculoAdultoNptVO.getVolOligoPed() == null) {
				calculoAdultoNptVO.setParamOligo(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamOligo(null);
				calculoAdultoNptVO.setTotParamOligo(null);
				calculoAdultoNptVO.setTotParamOligoEd(null);
			}
			
			if (calculoAdultoNptVO.getVolOligo().compareTo(BigDecimal.ZERO) > 0) {
				AfaParamNptVO olaVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO();
				switch (olaVO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramOla = ((calculoAdultoNptVO.getVolOligo().multiply(new BigDecimal(olaVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamOligo(paramOla);
				
					BigDecimal totParamOla = paramOla.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamOligo(totParamOla);
					break;
				case M:
					BigDecimal paramOlaSc = (calculoAdultoNptVO.getVolOligo().multiply(new BigDecimal(olaVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), 3, RoundingMode.HALF_UP);
					calculoAdultoNptVO.setParamOligo(paramOlaSc);
				
					BigDecimal totParamOlaSc = paramOlaSc.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamOligo(totParamOlaSc);
					break;

				default:
					break;
				}
				calculoAdultoNptVO.setTotParamOligoEd(calculoAdultoNptVO.getTotParamOligo().toString().concat(" ").concat(olaVO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamOligo(calculoAdultoNptVO.getParamOligo().doubleValue());
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte6(tipo, calculoAdultoNptVO, volSolucao, volLipidios);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 6
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 */
	private void mpmCalculaComponentesNptParte6(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao,
			BigDecimal volLipidios) {
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_OLIGO_PED)) {
			if (calculoAdultoNptVO.getVolOligoPed() == null) {
				calculoAdultoNptVO.setParamOligo(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamOligo(null);
				calculoAdultoNptVO.setTotParamOligo(null);
				calculoAdultoNptVO.setTotParamOligoEd(null);
			}
			
			if (calculoAdultoNptVO.getVolOligoPed().compareTo(BigDecimal.ZERO) > 0) {
				AfaParamNptVO olpVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO();
				switch (olpVO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramOlp = ((calculoAdultoNptVO.getVolOligoPed().multiply(new BigDecimal(olpVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamOligo(paramOlp);
				
					BigDecimal totParamOlp = paramOlp.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamOligo(totParamOlp);
					break;
				case M:
					BigDecimal paramOlpSc = ((calculoAdultoNptVO.getVolOligoPed().multiply(new BigDecimal(olpVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamOligo(paramOlpSc);
				
					BigDecimal totParamOlpSc = paramOlpSc.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamOligo(totParamOlpSc);
					break;

				default:
					break;
				}
				calculoAdultoNptVO.setTotParamOligoEd(calculoAdultoNptVO.getTotParamOligo().toString().concat(" ").concat(olpVO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamOligo(calculoAdultoNptVO.getParamOligo().doubleValue());
			}
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_ACET_ZN) || tipo.equals(DominioTipoComponenteNpt.RECALCULA)) {
			if (calculoAdultoNptVO.getVolAcetZn() == null) {
				calculoAdultoNptVO.setParamAcetZn(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamAcetZn(null);
				calculoAdultoNptVO.setTotParamAcetZn(null);
				calculoAdultoNptVO.setTotParamAcetZnEd(null);
				
			} else {
				AfaParamNptVO aznVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO();
				switch (aznVO.getTpParamCalcUsado()) {
				case K:
					BigDecimal paramAzn = ((calculoAdultoNptVO.getVolAcetZn().multiply(new BigDecimal(aznVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamAcetZn(paramAzn);
			
					BigDecimal totParamAzn = paramAzn.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamAcetZn(totParamAzn);
					break;
				case M:
					BigDecimal paramAznSc = ((calculoAdultoNptVO.getVolAcetZn().multiply(new BigDecimal(aznVO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()))).round(new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setParamAcetZn(paramAznSc);
				
					BigDecimal totParamAznSc = paramAznSc.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamAcetZn(totParamAznSc);
					break;

				default:
					break;
				}
				calculoAdultoNptVO.setTotParamAcetZnEd(calculoAdultoNptVO.getTotParamAcetZn().toString().concat(" ").concat(aznVO.getUmmDescricao()));
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamAcetZn(calculoAdultoNptVO.getParamAcetZn().intValue());
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte7(tipo, calculoAdultoNptVO, volSolucao, volLipidios);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 7
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 */
	private void mpmCalculaComponentesNptParte7(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao,
			BigDecimal volLipidios) {
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_GLICOSE_50) || tipo.equals(DominioTipoComponenteNpt.VOL_GLICOSE_10)) {
			AfaParamNptVO g10VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO();
			AfaParamNptGlicose50VO g50VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO();
			AfaParamNptVO kclVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO();
			BigDecimal paramG50 = null;
			BigDecimal totParamG50 = null;
			BigDecimal paramG10 = null;
			BigDecimal totParamG10 = null;
			
			// TODO -- Possível defeito. Corrigir? O correto seria testar o valor de g50VO?
			if (g10VO.getTpParamCalcUsado().equals(DominioTipoVolume.K)) {
				paramG50 = (calculoAdultoNptVO.getVolGlicose50().multiply(new BigDecimal(g50VO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()));
				totParamG50 = paramG50.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()));
			
			// TODO -- Possível defeito. Corrigir? O correto seria testar o valor de g50VO?
			} else if (kclVO.getTpParamCalcUsado().equals(DominioTipoVolume.M)) {
				paramG50 = (calculoAdultoNptVO.getVolGlicose50().multiply(new BigDecimal(g50VO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()));
				totParamG50 = paramG50.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()));
			}
			
			switch (g10VO.getTpParamCalcUsado()) {
			case K:
				paramG10 = (calculoAdultoNptVO.getVolGlicose10().multiply(new BigDecimal(g10VO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()));
				totParamG10 = paramG10.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()));
				break;
			case M:
				paramG10 = (calculoAdultoNptVO.getVolGlicose10().multiply(new BigDecimal(g10VO.getConvMl())))
						.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()));
				totParamG10 = paramG10.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()));
				break;

			default:
				break;
			}
			if (paramG50 == null) {
				paramG50 = BigDecimal.ZERO;
				totParamG50 = BigDecimal.ZERO;
			}
			
			if (paramG10 == null) {
				paramG10 = BigDecimal.ZERO;
				totParamG10 = BigDecimal.ZERO;
			}
			
			if (paramG50.equals(BigDecimal.ZERO) && paramG10.equals(BigDecimal.ZERO)) {
				calculoAdultoNptVO.setParamTig(null);
				// TODO -- Possível defeito. Corrigir? Qual é o correto?
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPotassio(null);
				calculoAdultoNptVO.setTotParamTig(null);
				calculoAdultoNptVO.setTotParamTigEd(null);
				calculoAdultoNptVO.setParamPercGlic50(new BigDecimal(100));
				// TODO -- Possível defeito. Corrigir? Qual é o correto?
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic50((double) 100);
				calculoAdultoNptVO.setParamPercGlic10(BigDecimal.ZERO);
				// TODO -- Possível defeito. Corrigir? Qual é o correto?
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic10((double) 0);
				
			} else {
				calculoAdultoNptVO.setParamTig(paramG50.add(paramG10, new MathContext(3, RoundingMode.HALF_UP)));
				calculoAdultoNptVO.setTotParamTig(totParamG50.add(totParamG10, new MathContext(3, RoundingMode.HALF_UP)));
				calculoAdultoNptVO.setTotParamTigEd(calculoAdultoNptVO.getTotParamTig().toString().concat(" ").concat(g50VO.getUmmDescricao()));
				
				if (paramG50.compareTo(BigDecimal.ZERO) > 0 && paramG10.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal percG50 = ((paramG50.multiply(new BigDecimal(100)))
							.divide((paramG50.add(paramG10)))).round(new MathContext(0, RoundingMode.HALF_UP));
					
					calculoAdultoNptVO.setParamPercGlic50(percG50);
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic50(percG50.doubleValue());
					calculoAdultoNptVO.setParamPercGlic10(new BigDecimal(100).subtract(percG50));
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic10(calculoAdultoNptVO.getParamPercGlic10().doubleValue());
					
				} else {
					if (paramG50.compareTo(BigDecimal.ZERO) > 0 && paramG10.equals(BigDecimal.ZERO)) {
						calculoAdultoNptVO.setParamPercGlic50(new BigDecimal(100));
						calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic50((double) 100);
						calculoAdultoNptVO.setParamPercGlic10(BigDecimal.ZERO);
						calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic10((double) 0);
					}
					
					if (paramG50.equals(BigDecimal.ZERO) && paramG10.compareTo(BigDecimal.ZERO) > 0) {
						calculoAdultoNptVO.setParamPercGlic50(BigDecimal.ZERO);
						calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic50((double) 0);
						calculoAdultoNptVO.setParamPercGlic10(new BigDecimal(100));
						calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamPercGlic10((double) 100);
					}
				}
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte8(tipo, calculoAdultoNptVO, volSolucao, volLipidios);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 8
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 */
	private void mpmCalculaComponentesNptParte8(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao,
			BigDecimal volLipidios) {
		
		boolean calculoReversoHep = false;
		
		if (tipo.equals(DominioTipoComponenteNpt.VOL_HEPARINA)) {
			if (calculoAdultoNptVO.getVolHeparina() == null) {
				calculoAdultoNptVO.setParamHeparina(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamHeparina(null);
				calculoAdultoNptVO.setTotParamHeparina(null);
				calculoAdultoNptVO.setTotParamHeparinaEd(null);
				
			} else {
				BigDecimal volAa10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAa10());
				BigDecimal volAaPed10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAaPed10());
				BigDecimal volAcetZn = this.obterValorNotNull(calculoAdultoNptVO.getVolAcetZn());
				BigDecimal volNacl20 = this.obterValorNotNull(calculoAdultoNptVO.getVolNacl20());
				BigDecimal volGlucoCa = this.obterValorNotNull(calculoAdultoNptVO.getVolGlucoCa());
				BigDecimal volKcl = this.obterValorNotNull(calculoAdultoNptVO.getVolKcl());
				BigDecimal volK3po4 = this.obterValorNotNull(calculoAdultoNptVO.getVolK3Po4());
				BigDecimal volMgso4 = this.obterValorNotNull(calculoAdultoNptVO.getVolMgso4());
				BigDecimal volGli5 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose5());
				BigDecimal volGli10 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose10());
				BigDecimal volGli50 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose50());
				BigDecimal volOligo = this.obterValorNotNull(calculoAdultoNptVO.getVolOligo());
				BigDecimal volOligoPed = this.obterValorNotNull(calculoAdultoNptVO.getVolOligoPed());
				BigDecimal volLip10 = this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios10());
				BigDecimal volLip20 = this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios20());
				
				BigDecimal volCalculoReversoHep = volAa10.add(volAaPed10).add(volAcetZn).add(volNacl20).add(volGlucoCa)
						.add(volKcl).add(volK3po4).add(volMgso4).add(volGli5).add(volGli10).add(volGli50).add(volOligo)
						.add(volOligoPed).add(volOligoPed).add(volLip10).add(volLip20);
				
				if (volCalculoReversoHep.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal paramHep = calculoAdultoNptVO.getVolHeparina().divide(volCalculoReversoHep, 3, RoundingMode.HALF_UP);
					calculoAdultoNptVO.setParamHeparina(paramHep);
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setParamHeparina(paramHep.doubleValue());
					
					BigDecimal totParamHep = volCalculoReversoHep.multiply(paramHep, new MathContext(3, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamHeparina(totParamHep);
					
					calculoAdultoNptVO.setTotParamHeparinaEd(totParamHep.toString().concat(" ")
							.concat(calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO().getUmmDescricao()));
					
					calculoReversoHep = true;
				}
			}
		}
		
		if (tipo.equals(DominioTipoComponenteNpt.VOLUME) || tipo.equals(DominioTipoComponenteNpt.RECALCULA)) {
			if (calculoAdultoNptVO.getParamVolDes().compareTo(BigDecimal.ZERO) > 0) {
				if (calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso().compareTo(calculoAdultoNptVO.getCalculoParametrosFixosVO().getLimiteCalculoPeso().doubleValue()) > 0) {
					BigDecimal totParamVolDes = calculoAdultoNptVO.getParamVolDes()
							.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(1, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamVolDes(totParamVolDes);
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setTipoParamCalculoVol(DominioTipoVolume.M);
					
				} else {
					BigDecimal totParamVolDes = calculoAdultoNptVO.getParamVolDes()
							.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(1, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamVolDes(totParamVolDes);
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setTipoParamCalculoVol(DominioTipoVolume.K);
				}
				calculoAdultoNptVO.setTotParamVolCalc(calculoAdultoNptVO.getTotParamVolDes());
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte9(tipo, calculoAdultoNptVO, volSolucao, volLipidios, calculoReversoHep);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 9
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 * @param calculoReversoHep
	 */
	private void mpmCalculaComponentesNptParte9(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios,
			final boolean calculoReversoHep) {
		
		if (tipo.equals(DominioTipoComponenteNpt.LIPIDIOS) || tipo.equals(DominioTipoComponenteNpt.RECALCULA)) {
			if (calculoAdultoNptVO.getParamTipoLip().equals(DominioTipoLipidio.LIPIDIO_10_PORCENTO)) {
				AfaParamNptVO lip1VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO();
				switch (lip1VO.getTpParamCalcUsado()) {
				case K:
					BigDecimal totParamLip = calculoAdultoNptVO.getParamLip()
						.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamLip(totParamLip);
					break;
				case M:
					BigDecimal totParamLipSc = calculoAdultoNptVO.getParamLip()
						.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamLip(totParamLipSc);
					break;

				default:
					break;
				}
				if (calculoAdultoNptVO.getTotParamLip().compareTo(BigDecimal.ZERO) > 0) {
					calculoAdultoNptVO.setTotParamLipEd(calculoAdultoNptVO.getTotParamLip().toString().concat(" ").concat(lip1VO.getUmmDescricao()));
					
				} else {
					calculoAdultoNptVO.setTotParamLipEd(null);
				}
				BigDecimal volLip10 = calculoAdultoNptVO.getTotParamLip()
						.divide(new BigDecimal(lip1VO.getConvMl()), new MathContext(lip1VO.getNroCasasDecimais(), RoundingMode.HALF_UP));
				calculoAdultoNptVO.setVolLipidios10(volLip10);
				
				if (calculoAdultoNptVO.getVolLipidios10().compareTo(new BigDecimal(lip1VO.getVolMaximo())) > 0) {
					calculoAdultoNptVO.setVolLipidios10(new BigDecimal(lip1VO.getVolMaximo()));
				}
				calculoAdultoNptVO.setVolLipidios20(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios10(calculoAdultoNptVO.getVolLipidios10().doubleValue());
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios20(null);
				
			} else if (calculoAdultoNptVO.getParamTipoLip().equals(DominioTipoLipidio.LIPIDIO_20_PORCENTO)) {
				AfaParamNptVO lip2VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO();
				switch (lip2VO.getTpParamCalcUsado()) {
				case K:
					BigDecimal totParamLip = calculoAdultoNptVO.getParamLip()
						.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamLip(totParamLip);
					break;
				case M:
					BigDecimal totParamLipSc = calculoAdultoNptVO.getParamLip()
						.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamLip(totParamLipSc);
					break;

				default:
					break;
				}
				if (calculoAdultoNptVO.getTotParamLip().compareTo(BigDecimal.ZERO) > 0) {
					calculoAdultoNptVO.setTotParamLipEd(calculoAdultoNptVO.getTotParamLip().toString().concat(" ").concat(lip2VO.getUmmDescricao()));
					
				} else {
					calculoAdultoNptVO.setTotParamLipEd(null);
				}
				calculoAdultoNptVO.setVolLipidios10(null);
				
				BigDecimal volLip20 = calculoAdultoNptVO.getTotParamLip()
						.divide(new BigDecimal(lip2VO.getConvMl()), new MathContext(lip2VO.getNroCasasDecimais(), RoundingMode.HALF_UP));
				calculoAdultoNptVO.setVolLipidios20(volLip20);
				
				if (calculoAdultoNptVO.getVolLipidios20().compareTo(new BigDecimal(lip2VO.getVolMaximo())) > 0) {
					calculoAdultoNptVO.setVolLipidios20(new BigDecimal(lip2VO.getVolMaximo()));
				}
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios20(calculoAdultoNptVO.getVolLipidios20().doubleValue());
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolLipidios10(null);
			}
			if (calculoAdultoNptVO.getVolLipidios10() == null && calculoAdultoNptVO.getVolLipidios20() == null
					&& calculoAdultoNptVO.getGotejoLipidios() != null) {
				calculoAdultoNptVO.setGotejoLipidios(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoLipidios(null);
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte10(tipo, calculoAdultoNptVO, volSolucao, volLipidios, calculoReversoHep);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 10
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 * @param calculoReversoHep
	 */
	private void mpmCalculaComponentesNptParte10(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios,
			boolean calculoReversoHep) {
		
		if (tipo.equals(DominioTipoComponenteNpt.AMINOACIDOS) || tipo.equals(DominioTipoComponenteNpt.RECALCULA)) {
			if (calculoAdultoNptVO.getParamAmin() == null || calculoAdultoNptVO.getParamAmin().equals(BigDecimal.ZERO)) {
				calculoAdultoNptVO.setVolAa10(null);
				calculoAdultoNptVO.setVolAaPed10(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAa10(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAaPed10(null);
				calculoAdultoNptVO.setTotParamAmin(null);
				calculoAdultoNptVO.setTotParamAminEd(null);
				
			} else {
				if (calculoAdultoNptVO.getDadosPesoAlturaVO().getIndPacPediatrico().equals(Boolean.TRUE)) {
					AfaParamNptVO aapVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO();
					switch (aapVO.getTpParamCalcUsado()) {
					case K:
						BigDecimal totParamAmin = calculoAdultoNptVO.getParamAmin()
							.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
						calculoAdultoNptVO.setTotParamAmin(totParamAmin);
						break;
					case M:
						BigDecimal totParamAminSc = calculoAdultoNptVO.getParamAmin()
							.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
						calculoAdultoNptVO.setTotParamAmin(totParamAminSc);
						break;

					default:
						break;
					}
					if (calculoAdultoNptVO.getTotParamAmin().compareTo(BigDecimal.ZERO) > 0) {
						calculoAdultoNptVO.setTotParamAminEd(calculoAdultoNptVO.getTotParamAmin().toString().concat(" ").concat(aapVO.getUmmDescricao()));
						
					} else {
						calculoAdultoNptVO.setTotParamAminEd(null);
					}
					BigDecimal volAaPed10 = calculoAdultoNptVO.getTotParamAmin()
							.divide(new BigDecimal(aapVO.getConvMl()), new MathContext(aapVO.getNroCasasDecimais(), RoundingMode.HALF_UP));
					calculoAdultoNptVO.setVolAaPed10(volAaPed10);
					
					if (calculoAdultoNptVO.getVolAaPed10().compareTo(new BigDecimal(aapVO.getVolMaximo())) > 0) {
						calculoAdultoNptVO.setVolAaPed10(new BigDecimal(aapVO.getVolMaximo()));
					}
					calculoAdultoNptVO.setVolAa10(null);
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAaPed10(calculoAdultoNptVO.getVolAaPed10().doubleValue());
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAa10(null);
					
				} else {
					AfaParamNptVO aadVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO();
					switch (aadVO.getTpParamCalcUsado()) {
					case K:
						BigDecimal totParamAmin = calculoAdultoNptVO.getParamAmin()
							.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
						calculoAdultoNptVO.setTotParamAmin(totParamAmin);
						break;
					case M:
						BigDecimal totParamAminSc = calculoAdultoNptVO.getParamAmin()
							.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
						calculoAdultoNptVO.setTotParamAmin(totParamAminSc);
						break;

					default:
						break;
					}
					if (calculoAdultoNptVO.getTotParamAmin().compareTo(BigDecimal.ZERO) > 0) {
						calculoAdultoNptVO.setTotParamAminEd(calculoAdultoNptVO.getTotParamAmin().toString().concat(" ").concat(aadVO.getUmmDescricao()));
						
					} else {
						calculoAdultoNptVO.setTotParamAminEd(null);
					}
					BigDecimal volAa10 = calculoAdultoNptVO.getTotParamAmin()
							.divide(new BigDecimal(aadVO.getConvMl()), new MathContext(aadVO.getNroCasasDecimais(), RoundingMode.HALF_UP));
					calculoAdultoNptVO.setVolAa10(volAa10);
					
					if (calculoAdultoNptVO.getVolAa10().compareTo(new BigDecimal(aadVO.getVolMaximo())) > 0) {
						calculoAdultoNptVO.setVolAa10(new BigDecimal(aadVO.getVolMaximo()));
					}
					calculoAdultoNptVO.setVolAaPed10(null);
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAa10(calculoAdultoNptVO.getVolAa10().doubleValue());
					calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolAaPed10(null);
				}
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte11(tipo, calculoAdultoNptVO, volSolucao, volLipidios, calculoReversoHep);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 11
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 * @param calculoReversoHep
	 */
	private void mpmCalculaComponentesNptParte11(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios,
			boolean calculoReversoHep) {
		
		if (tipo.equals(DominioTipoComponenteNpt.TIG) || tipo.equals(DominioTipoComponenteNpt.RECALCULA)) {
			if (calculoAdultoNptVO.getParamTig() == null || calculoAdultoNptVO.getParamTig().equals(BigDecimal.ZERO)) {
				calculoAdultoNptVO.setVolGlicose50(null);
				calculoAdultoNptVO.setVolGlicose10(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose50(null);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose10(null);
				calculoAdultoNptVO.setTotParamTig(null);
				calculoAdultoNptVO.setTotParamTigEd(null);
				
			} else {
				AfaParamNptVO g10VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO();
				AfaParamNptGlicose50VO g50VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO();
				switch (g10VO.getTpParamCalcUsado()) {
				case K:
					BigDecimal totParamTig = calculoAdultoNptVO.getParamTig()
						.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamTig(totParamTig);
					break;
				case M:
					BigDecimal totParamTigSc = calculoAdultoNptVO.getParamTig()
						.multiply(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getSc()), new MathContext(2, RoundingMode.HALF_UP));
					calculoAdultoNptVO.setTotParamTig(totParamTigSc);
					break;

				default:
					break;
				}
				if (calculoAdultoNptVO.getTotParamTig().compareTo(BigDecimal.ZERO) > 0) {
					calculoAdultoNptVO.setTotParamTigEd(calculoAdultoNptVO.getTotParamTig().toString().concat(" ").concat(g10VO.getUmmDescricao()));
					
				} else {
					calculoAdultoNptVO.setTotParamTigEd(null);
				}
				
				if (calculoAdultoNptVO.getParamPercGlic50().compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal volGli50 = ((
							(calculoAdultoNptVO.getTotParamTig().multiply(calculoAdultoNptVO.getParamPercGlic50()))
							.divide(new BigDecimal(100)))
							.divide(new BigDecimal(g50VO.getConvMl()))).round(new MathContext(g10VO.getNroCasasDecimais(), RoundingMode.HALF_UP));
					
					calculoAdultoNptVO.setVolGlicose50(volGli50);
					
					if (calculoAdultoNptVO.getVolGlicose50().compareTo(new BigDecimal(g50VO.getVolMaximo())) > 0) {
						calculoAdultoNptVO.setVolGlicose50(new BigDecimal(g50VO.getVolMaximo()));
					}
				} else {
					calculoAdultoNptVO.setVolGlicose50(null);
				}
				
				if (calculoAdultoNptVO.getParamPercGlic10().compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal volGli10 = ((
							(calculoAdultoNptVO.getTotParamTig().multiply(calculoAdultoNptVO.getParamPercGlic10()))
							.divide(new BigDecimal(100)))
							.divide(new BigDecimal(g10VO.getConvMl()))).round(new MathContext(g10VO.getNroCasasDecimais(), RoundingMode.HALF_UP));
					
					calculoAdultoNptVO.setVolGlicose10(volGli10);
					
					if (calculoAdultoNptVO.getVolGlicose10().compareTo(new BigDecimal(g10VO.getVolMaximo())) > 0) {
						calculoAdultoNptVO.setVolGlicose10(new BigDecimal(g10VO.getVolMaximo()));
					}
				} else {
					calculoAdultoNptVO.setVolGlicose10(null);
				}
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose50(calculoAdultoNptVO.getVolGlicose50().doubleValue());
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolGlicose10(calculoAdultoNptVO.getVolGlicose10().doubleValue());
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte12(tipo, calculoAdultoNptVO, volSolucao, volLipidios, calculoReversoHep);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 12
	 * 
	 * @param tipo
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 * @param calculoReversoHep
	 */
	private void mpmCalculaComponentesNptParte12(DominioTipoComponenteNpt tipo, CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios,
			boolean calculoReversoHep) {
		
		BigDecimal volAa10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAa10());
		BigDecimal volAaPed10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAaPed10());
		BigDecimal volAcetZn = this.obterValorNotNull(calculoAdultoNptVO.getVolAcetZn());
		BigDecimal volNacl20 = this.obterValorNotNull(calculoAdultoNptVO.getVolNacl20());
		BigDecimal volGlucoCa = this.obterValorNotNull(calculoAdultoNptVO.getVolGlucoCa());
		BigDecimal volKcl = this.obterValorNotNull(calculoAdultoNptVO.getVolKcl());
		BigDecimal volK3po4 = this.obterValorNotNull(calculoAdultoNptVO.getVolK3Po4());
		BigDecimal volMgso4 = this.obterValorNotNull(calculoAdultoNptVO.getVolMgso4());
		BigDecimal volGli5 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose5());
		BigDecimal volGli10 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose10());
		BigDecimal volGli50 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose50());
		BigDecimal volOligo = this.obterValorNotNull(calculoAdultoNptVO.getVolOligo());
		BigDecimal volOligoPed = this.obterValorNotNull(calculoAdultoNptVO.getVolOligoPed());
		
		volSolucao = volAa10.add(volAaPed10).add(volAcetZn).add(volNacl20).add(volGlucoCa).add(volKcl).add(volK3po4)
				.add(volMgso4).add(volGli5).add(volGli10).add(volGli50).add(volOligo).add(volOligoPed);
		
		volLipidios = this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios10()).add(this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios20()));
		
		if (calculoAdultoNptVO.getParamTempInfusaoSol().compareTo((short) 0) > 0
				&& volSolucao.compareTo(BigDecimal.ZERO) > 0 && !tipo.equals(DominioTipoComponenteNpt.TEMPO_INFUSAO_SOLUCAO)
				&& !tipo.equals(DominioTipoComponenteNpt.GOTEJO_SOLUCAO)) {
			
			BigDecimal gotejoSolucao = volSolucao
					.divide(new BigDecimal(calculoAdultoNptVO.getParamTempInfusaoSol()), 1, RoundingMode.HALF_UP);
			calculoAdultoNptVO.setGotejoSolucao(gotejoSolucao);
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoSolucao(gotejoSolucao.doubleValue());
		}
		
		if (calculoAdultoNptVO.getParamTempInfusaoLip().compareTo((short) 0) > 0
				&& volLipidios.compareTo(BigDecimal.ZERO) > 0 && !tipo.equals(DominioTipoComponenteNpt.GOTEJO_LIPIDIOS)
				&& !tipo.equals(DominioTipoComponenteNpt.TEMPO_INFUSAO_LIPIDIOS)) {
			
			BigDecimal gotejoLipidios = volLipidios
					.divide(new BigDecimal(calculoAdultoNptVO.getParamTempInfusaoLip()), 1, RoundingMode.HALF_UP);
			calculoAdultoNptVO.setGotejoLipidios(gotejoLipidios);
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setGotejoLipidios(gotejoLipidios.doubleValue());
		}
		
		if (calculoAdultoNptVO.getParamHeparina().compareTo(BigDecimal.ZERO) > 0
				&& volSolucao.compareTo(BigDecimal.ZERO) > 0) {
			
			if (!calculoReversoHep) {
				AfaParamNptVO hepVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptHeparinaVO();
				
				BigDecimal totParamHep = volSolucao
						.multiply(calculoAdultoNptVO.getParamHeparina(), new MathContext(hepVO.getNroCasasDecimais(), RoundingMode.HALF_UP));
				calculoAdultoNptVO.setTotParamHeparina(totParamHep);
				calculoAdultoNptVO.setTotParamHeparinaEd(totParamHep.toString().concat(" ").concat(hepVO.getUmmDescricao()));
				calculoAdultoNptVO.setVolHeparina(totParamHep);
				calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolHeparina(totParamHep.doubleValue());
			}
		} else {
			calculoAdultoNptVO.setVolHeparina(null);
			calculoAdultoNptVO.getCalculoParametrosFixosVO().setVolHeparina(null);
			calculoAdultoNptVO.setTotParamHeparina(null);
			calculoAdultoNptVO.setTotParamHeparinaEd(null);
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte13(calculoAdultoNptVO, volSolucao, volLipidios);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 13
	 * 
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 */
	private void mpmCalculaComponentesNptParte13(CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios) {
		
		BigDecimal calLipidios = BigDecimal.ZERO;
		BigDecimal calAmin = BigDecimal.ZERO;
		BigDecimal calGlicose = BigDecimal.ZERO;
		BigDecimal concGlicose = BigDecimal.ZERO;
		
		if (calculoAdultoNptVO.getVolLipidios10().compareTo(BigDecimal.ZERO) > 0) {
			AfaParamNptVO lip1VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO();
			
			calLipidios = calLipidios.add(
					(calculoAdultoNptVO.getVolLipidios10()
							.multiply(new BigDecimal(lip1VO.getConvMl()))
							.multiply(new BigDecimal(lip1VO.getConvCalorias()))));
		}
		
		if (calculoAdultoNptVO.getVolLipidios20().compareTo(BigDecimal.ZERO) > 0) {
			AfaParamNptVO lip2VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO();
			
			calLipidios = calLipidios.add(
					(calculoAdultoNptVO.getVolLipidios20()
							.multiply(new BigDecimal(lip2VO.getConvMl()))
							.multiply(new BigDecimal(lip2VO.getConvCalorias()))));
		}
		
		if (calculoAdultoNptVO.getVolAa10().compareTo(BigDecimal.ZERO) > 0) {
			AfaParamNptVO aadVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO();
			
			calAmin = calAmin.add(
					(calculoAdultoNptVO.getVolAa10()
							.multiply(new BigDecimal(aadVO.getConvMl()))
							.multiply(new BigDecimal(aadVO.getConvCalorias()))));
		}
		
		if (calculoAdultoNptVO.getVolAaPed10().compareTo(BigDecimal.ZERO) > 0) {
			AfaParamNptVO aapVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO();
			
			calAmin = calAmin.add(
					(calculoAdultoNptVO.getVolAaPed10()
							.multiply(new BigDecimal(aapVO.getConvMl()))
							.multiply(new BigDecimal(aapVO.getConvCalorias()))));
		}
		
		if (calculoAdultoNptVO.getVolGlicose5().compareTo(BigDecimal.ZERO) > 0) {
			AfaParamNptVO g5VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO();
			
			calGlicose = calGlicose.add(
					(calculoAdultoNptVO.getVolGlicose5()
							.multiply(new BigDecimal(g5VO.getConvMl()))
							.multiply(new BigDecimal(g5VO.getConvCalorias()))));
			
			concGlicose = concGlicose.add(
					(calculoAdultoNptVO.getVolGlicose5()
							.multiply(new BigDecimal(g5VO.getConvMl()))));
		}
		
		if (calculoAdultoNptVO.getVolGlicose10().compareTo(BigDecimal.ZERO) > 0) {
			AfaParamNptVO g10VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO();
			
			calGlicose = calGlicose.add(
					(calculoAdultoNptVO.getVolGlicose10()
							.multiply(new BigDecimal(g10VO.getConvMl()))
							.multiply(new BigDecimal(g10VO.getConvCalorias()))));
			
			concGlicose = concGlicose.add(
					(calculoAdultoNptVO.getVolGlicose10()
							.multiply(new BigDecimal(g10VO.getConvMl()))));
		}
		
		if (calculoAdultoNptVO.getVolGlicose50().compareTo(BigDecimal.ZERO) > 0) {
			AfaParamNptGlicose50VO g50VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO();
			
			calGlicose = calGlicose.add(
					(calculoAdultoNptVO.getVolGlicose50()
							.multiply(new BigDecimal(g50VO.getConvMl()))
							.multiply(new BigDecimal(g50VO.getConvCalorias()))));
			
			concGlicose = concGlicose.add(
					(calculoAdultoNptVO.getVolGlicose50()
							.multiply(new BigDecimal(g50VO.getConvMl()))));
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte14(calculoAdultoNptVO, volSolucao, volLipidios, calLipidios, calAmin, calGlicose, concGlicose);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 14
	 * 
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 * @param calLipidios
	 * @param calAmin
	 * @param calGlicose
	 * @param concGlicose
	 */
	private void mpmCalculaComponentesNptParte14(CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios,
			BigDecimal calLipidios, BigDecimal calAmin, BigDecimal calGlicose, BigDecimal concGlicose) {
		
		BigDecimal volTotal = null;
		BigDecimal calTotais = null;
		BigDecimal rel1gNitroAmin = null;
		BigDecimal gNitroAmin = null;
		
		if (volSolucao.compareTo(BigDecimal.ZERO) > 0 || volLipidios.compareTo(BigDecimal.ZERO) > 0) {
			volTotal = volSolucao.add(volLipidios);
			calculoAdultoNptVO.setVolumeMlDia(volTotal);
			calculoAdultoNptVO.setVolumeMlDiaEd(volTotal.toString());
		}
		
		if (calAmin.compareTo(BigDecimal.ZERO) > 0 || calLipidios.compareTo(BigDecimal.ZERO) > 0
				|| calGlicose.compareTo(BigDecimal.ZERO) > 0) {
			
			calTotais = calAmin.add(calLipidios).add(calGlicose);
			calculoAdultoNptVO.setCaloriasDia(calTotais.round(new MathContext(0, RoundingMode.HALF_UP)));
			calculoAdultoNptVO.setCaloriasDiaEd(calculoAdultoNptVO.getCaloriasDia().toString());
			calculoAdultoNptVO.setCaloriasKgDia(calTotais.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()), 1, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setCaloriasKgDiaEd(calculoAdultoNptVO.getCaloriasKgDia().toString());
			
			BigDecimal percCalAmin = ((calAmin.multiply(new BigDecimal(100))).divide(calTotais)).round(new MathContext(0, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setPercCalAmin(percCalAmin);
			calculoAdultoNptVO.setPercCalAminEd(percCalAmin.toString().concat(" %"));
			
			BigDecimal percCalGlic = ((calGlicose.multiply(new BigDecimal(100))).divide(calTotais)).round(new MathContext(0, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setPercCalGlicose(percCalGlic);
			calculoAdultoNptVO.setPercCalGlicoseEd(percCalGlic.toString().concat(" %"));
			
			BigDecimal percCalLip = ((calLipidios.multiply(new BigDecimal(100))).divide(calTotais)).round(new MathContext(0, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setPercCalLipidios(percCalLip);
			calculoAdultoNptVO.setPercCalLipidiosEd(percCalLip.toString().concat(" %"));
			
			if (calculoAdultoNptVO.getVolAa10().compareTo(BigDecimal.ZERO) > 0) {
				AfaParamNptVO aadVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO();
				rel1gNitroAmin = BigDecimal.ONE.divide(new BigDecimal(aadVO.getConvUnidNitrogenio()));
				gNitroAmin = (calculoAdultoNptVO.getVolAa10().multiply(new BigDecimal(aadVO.getConvMl()))).divide(rel1gNitroAmin);
				
				BigDecimal relCalGNitro = ((calLipidios.add(calGlicose)).divide(gNitroAmin)).round(new MathContext(0, RoundingMode.HALF_UP));
				calculoAdultoNptVO.setRelCalGNitro(relCalGNitro);
				calculoAdultoNptVO.setRelCalGNitroEd(relCalGNitro.toString().concat(":1"));
			}
			
			if (calculoAdultoNptVO.getVolAaPed10().compareTo(BigDecimal.ZERO) > 0) {
				AfaParamNptVO aapVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO();
				rel1gNitroAmin = BigDecimal.ONE.divide(new BigDecimal(aapVO.getConvUnidNitrogenio()));
				gNitroAmin = (calculoAdultoNptVO.getVolAaPed10().multiply(new BigDecimal(aapVO.getConvMl()))).divide(rel1gNitroAmin);
				
				BigDecimal relCalGNitro = ((calLipidios.add(calGlicose)).divide(gNitroAmin)).round(new MathContext(0, RoundingMode.HALF_UP));
				calculoAdultoNptVO.setRelCalGNitro(relCalGNitro);
				calculoAdultoNptVO.setRelCalGNitroEd(relCalGNitro.toString().concat(":1"));
			}
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte15(calculoAdultoNptVO, volSolucao, volLipidios, calLipidios, calGlicose, concGlicose);
	}
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 15
	 * 
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 * @param calLipidios
	 * @param calGlicose
	 * @param concGlicose
	 */
	private void mpmCalculaComponentesNptParte15(CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios,
			BigDecimal calLipidios, BigDecimal calGlicose, BigDecimal concGlicose) {
		
		BigDecimal tempoInfusaoLip = null;
		BigDecimal qtdeUnidLip = null;
		
		if (concGlicose.compareTo(BigDecimal.ZERO) > 0 && volSolucao.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal concGliSemLip = ((concGlicose.multiply(new BigDecimal(100))).divide(volSolucao)).round(new MathContext(1, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setConcGlicoseSemLipi(concGliSemLip);
			calculoAdultoNptVO.setConcGlicoseSemLipiEd(concGliSemLip.toString().concat(" %"));
			
		} else {
			calculoAdultoNptVO.setConcGlicoseSemLipi(null);
			calculoAdultoNptVO.setConcGlicoseSemLipiEd(null);
		}
		
		if (concGlicose.compareTo(BigDecimal.ZERO) > 0 && volSolucao.compareTo(BigDecimal.ZERO) > 0
				&& volLipidios.compareTo(BigDecimal.ZERO) > 0) {
			
			BigDecimal concGliComLip = ((concGlicose.multiply(new BigDecimal(100)))
					.divide((volSolucao.add(volLipidios)))).round(new MathContext(1, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setConcGlicoseComLipi(concGliComLip);
			calculoAdultoNptVO.setConcGlicoseComLipiEd(concGliComLip.toString().concat(" %"));
			
		} else {
			calculoAdultoNptVO.setConcGlicoseComLipi(null);
			calculoAdultoNptVO.setConcGlicoseComLipiEd(null);
		}
		
		if (calculoAdultoNptVO.getVolLipidios10().compareTo(BigDecimal.ZERO) > 0
				&& calculoAdultoNptVO.getGotejoLipidios().compareTo(BigDecimal.ZERO) > 0) {
			
			AfaParamNptVO lip1VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO();
			tempoInfusaoLip = calculoAdultoNptVO.getVolLipidios10().divide(calculoAdultoNptVO.getGotejoLipidios());
			qtdeUnidLip = calculoAdultoNptVO.getVolLipidios10().multiply(new BigDecimal(lip1VO.getConvMl()));
			
			BigDecimal taxaInfusaoLip = ((qtdeUnidLip.divide(tempoInfusaoLip))
					.divide(new BigDecimal(calculoAdultoNptVO.getDadosPesoAlturaVO().getPeso()))).round(new MathContext(2, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setTaxaInfusaoLipi(taxaInfusaoLip);
			calculoAdultoNptVO.setTaxaInfusaoLipiEd(taxaInfusaoLip.toString().concat(" ").concat(lip1VO.getUmmDescricao().concat("/kg/h")));
		}
		// Continuação...
		this.mpmCalculaComponentesNptParte16(calculoAdultoNptVO, volSolucao, volLipidios, calLipidios, calGlicose);
	}
	
	
	/**
	 * @ORADB MPMP_CALCULA_A - Parte 16
	 * 
	 * @param calculoAdultoNptVO
	 * @param volSolucao
	 * @param volLipidios
	 * @param calLipidios
	 * @param calGlicose
	 */
	private void mpmCalculaComponentesNptParte16(CalculoAdultoNptVO calculoAdultoNptVO, BigDecimal volSolucao, BigDecimal volLipidios,
			BigDecimal calLipidios, BigDecimal calGlicose) {
		
		BigDecimal relCalcioFosforo = null;
		BigDecimal mosmSolucao = null;
		BigDecimal mosmLipidios = null;
		
		if ((calculoAdultoNptVO.getVolLipidios10() == null && calculoAdultoNptVO.getVolLipidios20() == null)
				|| (calculoAdultoNptVO.getVolLipidios10().equals(BigDecimal.ZERO) && calculoAdultoNptVO.getVolLipidios20().equals(BigDecimal.ZERO))
				|| calculoAdultoNptVO.getGotejoLipidios() == null || calculoAdultoNptVO.getGotejoLipidios().equals(BigDecimal.ZERO)) {
			
			calculoAdultoNptVO.setTaxaInfusaoLipi(null);
			calculoAdultoNptVO.setTaxaInfusaoLipiEd(null);
		}
		
		if (calLipidios.compareTo(BigDecimal.ZERO) > 0 && calGlicose.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal relGlicLipG = (new BigDecimal(100).subtract((
					(calLipidios.multiply(new BigDecimal(100)))
					.divide((calGlicose.add(calLipidios)))).round(new MathContext(0, RoundingMode.HALF_UP))));
			calculoAdultoNptVO.setRelGlicoseLipiG(relGlicLipG);
			
			BigDecimal relGlicLipL = ((calLipidios.multiply(new BigDecimal(100)))
					.divide((calGlicose.add(calLipidios)))).round(new MathContext(0, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setRelGlicoseLipiL(relGlicLipL);
			calculoAdultoNptVO.setRelGlicoseLipiEd(relGlicLipG.toString().concat(":").concat(relGlicLipL.toString()));
			
		} else {
			calculoAdultoNptVO.setRelGlicoseLipiG(null);
			calculoAdultoNptVO.setRelGlicoseLipiL(null);
			calculoAdultoNptVO.setRelGlicoseLipiEd(null);
		}
		
		if (calculoAdultoNptVO.getVolK3Po4().compareTo(BigDecimal.ZERO) > 0
				&& calculoAdultoNptVO.getVolGlucoCa().compareTo(BigDecimal.ZERO) > 0) {
			
			AfaParamNptVO cal1VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO();
			AfaParamNptVO kpoVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO();
			
			relCalcioFosforo = (calculoAdultoNptVO.getVolGlucoCa().multiply(new BigDecimal(cal1VO.getConvMl())))
					.divide((calculoAdultoNptVO.getVolK3Po4().multiply(new BigDecimal(kpoVO.getConvMlFosforo()))));
			calculoAdultoNptVO.setRelCalcioFosforo(relCalcioFosforo.round(new MathContext(1, RoundingMode.HALF_UP)));
			calculoAdultoNptVO.setRelCalcioFosforoEd("1:".concat(calculoAdultoNptVO.getRelCalcioFosforo().toString()));
			
		} else {
			calculoAdultoNptVO.setRelCalcioFosforo(null);
			calculoAdultoNptVO.setRelCalcioFosforoEd(null);
		}
		
		BigDecimal volAa10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAa10());
		BigDecimal volAaPed10 = this.obterValorNotNull(calculoAdultoNptVO.getVolAaPed10());
		BigDecimal volAcetZn = this.obterValorNotNull(calculoAdultoNptVO.getVolAcetZn());
		BigDecimal volNacl20 = this.obterValorNotNull(calculoAdultoNptVO.getVolNacl20());
		BigDecimal volGlucoCa = this.obterValorNotNull(calculoAdultoNptVO.getVolGlucoCa());
		BigDecimal volKcl = this.obterValorNotNull(calculoAdultoNptVO.getVolKcl());
		BigDecimal volK3po4 = this.obterValorNotNull(calculoAdultoNptVO.getVolK3Po4());
		BigDecimal volMgso4 = this.obterValorNotNull(calculoAdultoNptVO.getVolMgso4());
		BigDecimal volGli5 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose5());
		BigDecimal volGli10 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose10());
		BigDecimal volGli50 = this.obterValorNotNull(calculoAdultoNptVO.getVolGlicose50());
		BigDecimal volOligo = this.obterValorNotNull(calculoAdultoNptVO.getVolOligo());
		BigDecimal volOligoPed = this.obterValorNotNull(calculoAdultoNptVO.getVolOligoPed());
		
		AfaParamNptVO aadVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoAdVO();
		AfaParamNptVO aapVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAminoacidoPedVO();
		AfaParamNptVO aznVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptAcetatoZincoVO();
		AfaParamNptVO sodVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoSodioVO();
		AfaParamNptVO calVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlucoCalcioVO();
		AfaParamNptVO kclVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptCloretoPotassioVO();
		AfaParamNptVO kpoVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptFosfatoPotassioVO();
		AfaParamNptVO magVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptSulfatoMagnesioVO();
		AfaParamNptVO g5VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose5VO();
		AfaParamNptVO g10VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose10VO();
		AfaParamNptGlicose50VO g50VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptGlicose50VO();
		AfaParamNptVO olaVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosAdVO();
		AfaParamNptVO olpVO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptOligoelementosPedVO();
		AfaParamNptVO lip1VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio10VO();
		AfaParamNptVO lip2VO = calculoAdultoNptVO.getAfaParamComponenteNptsVO().getAfaParamNptLipidio20VO();
		
		mosmSolucao = (volAa10.multiply(new BigDecimal(aadVO.getConvMlMosm())))
				.add((volAaPed10.multiply(new BigDecimal(aapVO.getConvMlMosm()))))
				.add((volAcetZn.multiply(new BigDecimal(aznVO.getConvMlMosm()))))
				.add((volNacl20.multiply(new BigDecimal(sodVO.getConvMlMosm()))))
				.add((volGlucoCa.multiply(new BigDecimal(calVO.getConvMlMosm()))))
				.add((volKcl.multiply(new BigDecimal(kclVO.getConvMlMosm()))))
				.add((volK3po4.multiply(new BigDecimal(kpoVO.getConvMlMosm()))))
				.add((volMgso4.multiply(new BigDecimal(magVO.getConvMlMosm()))))
				.add((volGli5.multiply(new BigDecimal(g5VO.getConvMlMosm()))))
				.add((volGli10.multiply(new BigDecimal(g10VO.getConvMlMosm()))))
				.add((volGli50.multiply(new BigDecimal(g50VO.getConvMlMosm()))))
				.add((volOligo.multiply(new BigDecimal(olaVO.getConvMlMosm()))))
				.add((volOligoPed.multiply(new BigDecimal(olpVO.getConvMlMosm()))));
		
		BigDecimal volLip10 = this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios10());
		BigDecimal volLip20 = this.obterValorNotNull(calculoAdultoNptVO.getVolLipidios20());
		
		mosmLipidios = (volLip10.multiply(new BigDecimal(lip1VO.getConvMlMosm())))
				.add((volLip20.multiply(new BigDecimal(lip2VO.getConvMlMosm()))));
		
		if (mosmSolucao.compareTo(BigDecimal.ZERO) > 0 && volSolucao.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal osmolSemLip = ((mosmSolucao.multiply(new BigDecimal(1000)))
					.divide(volSolucao)).round(new MathContext(0, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setOsmolSemLipi(osmolSemLip);
			calculoAdultoNptVO.setOsmolSemLipiEd(osmolSemLip.toString().concat(" mOsm/L"));
			
		} else {
			calculoAdultoNptVO.setOsmolSemLipi(null);
			calculoAdultoNptVO.setOsmolSemLipiEd(null);
		}
		
		if (mosmSolucao.compareTo(BigDecimal.ZERO) > 0 && mosmLipidios.compareTo(BigDecimal.ZERO) > 0
				&& volSolucao.compareTo(BigDecimal.ZERO) > 0) {
			
			BigDecimal osmolComLip = (((mosmSolucao.add(mosmLipidios))
					.multiply(new BigDecimal(1000)))
					.divide((volSolucao.add(volLipidios)))).round(new MathContext(0, RoundingMode.HALF_UP));
			calculoAdultoNptVO.setOsmolComLipi(osmolComLip);
			calculoAdultoNptVO.setOsmolComLipiEd(osmolComLip.toString().concat(" mOsm/L"));
			
		} else {
			calculoAdultoNptVO.setOsmolComLipi(null);
			calculoAdultoNptVO.setOsmolComLipiEd(null);
		}
	}
	
	private BigDecimal obterValorNotNull(BigDecimal valor) {
		return valor != null ? valor : BigDecimal.ZERO;
	}
}
