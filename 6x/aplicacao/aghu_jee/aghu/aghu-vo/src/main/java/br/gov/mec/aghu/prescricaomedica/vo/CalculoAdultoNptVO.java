package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioTipoLipidio;

/**
 * #989 - Calcular Nutrição Parenteral Total: VO de QMS_CTRL_BLOCK_A
 * 
 * @author aghu
 *
 */
@SuppressWarnings({"PMD.ExcessiveClassLength"})
public class CalculoAdultoNptVO {

	// LEGENDAS:
	// PNP = MPM_PRESCRICAO_NPTS
	// CPT = MPM_COMPOSICAO_PRESCRICAO_NPTS
	// IPN = MPM_ITEM_PRESCRICAO_NPTS
	// PC09 = Populado na procedure PC09 que é disparada nos eventos de inicialização da tela ou de alteração de campos.

	/*
	 * CAMPOS DE CÁLCULO NTP ADULTO
	 */
	public CalculoAdultoNptVO() {
		/*
		 * Valor padrão
		 */
		this.paramPercGlic50 = new BigDecimal("100");
		this.paramPercGlic10 = BigDecimal.ZERO;
		this.paramTipoLip = DominioTipoLipidio.LIPIDIO_10_PORCENTO;
	}

	private BigDecimal paramAmin; // 1. qms_ctrl_block_a.param_amin = IPN.QTDE_BASE_CALCULO
	private String unidParamAmin; // 2. qms_ctrl_block_a.unid_param_amin = LABEL
	private String totParamAminEd; // 3. qms_ctrl_block_a.TOT_PARAM_AMIN_ED = LABEL
	private BigDecimal volAa10; // 4. qms_ctrl_block_a.VOL_AA_10 = IPN.QTDE_PRESCRITA ou IPN.QTDE_CALCULADA
	private String ummDescricaoAminoacidosAd; // 5. AfaParamComponenteNptsVO.umm_descricao

	private BigDecimal paramTig;// 6. qms_ctrl_block_a.param_tig = IPN.QTDE_BASE_CALCULO
	private String unidParamTig; // 7. qms_ctrl_block_a.unid_param_tig = LABEL
	private String totParamTigEd; // 8. qms_ctrl_block_a.tot_param_tig_ed = LABEL
	private BigDecimal volGlicose50; // 9. qms_ctrl_block_a.vol_glicose_50 = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoGlicose50; // 10. AfaParamComponenteNptsVO.umm_descricao

	private BigDecimal paramPercGlic50; // 11. qms_ctrl_block_a.param_perc_glic50 = IPN.PERC_PARAM_CALCULO
	private BigDecimal paramPercGlic10; // 12. qms_ctrl_block_a.param_perc_glic10 = IPN.PERC_PARAM_CALCULO
	private BigDecimal volGlicose10; // 13. qms_ctrl_block_a.vol_glicose_10 = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoGlicose10; // 14. AfaParamComponenteNptsVO.umm_descricao

	private BigDecimal paramHeparina; // 15. qms_ctrl_block_a.param_heparina = IPN.QTDE_BASE_CALCULO
	private String unidParamHeparina; // 16. qms_ctrl_block_a.unid_param_heparina = LABEL
	private String totParamHeparinaEd; // 17. qms_ctrl_block_a.tot_param_heparina_ed = LABEL
	private BigDecimal volHeparina; // 18. qms_ctrl_block_a.vol_heparina = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoHeparina; // 19. AfaParamComponenteNptsVO.umm_descricao

	private BigDecimal volNacl20; // 20. qms_ctrl_block_a.vol_nacl_20 = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoCloretoSodio; // 21. AfaParamComponenteNptsVO.umm_descricao
	private String totParamSodioEd; // 22. qms_ctrl_block_a.TOT_PARAM_SODIO_ED = LABEL

	private BigDecimal volKcl; // 23. qms_ctrl_block_a.vol_kcl = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoCloretoPotassio; // 24. AfaParamComponenteNptsVO.umm_descricao
	private String totParamKclEd; // 25. qms_ctrl_block_a.TOT_PARAM_KCL_ED = LABEL

	private BigDecimal volK3Po4; // 26. qms_ctrl_block_a.vol_k3po4 = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoSulfatoMagnesio; // 27. AfaParamComponenteNptsVO.umm_descricao
	private String totParamK3po4Ed; // 28. qms_ctrl_block_a.TOT_PARAM_K3PO4_ED = LABEL

	private BigDecimal volMgso4; // 29. qms_ctrl_block_a.vol_mgso4 = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoFosfatoPotassio; // 30. AfaParamComponenteNptsVO.umm_descricao
	private String totParamMgso4Ed; // 31. qms_ctrl_block_a.TOT_PARAM_MGSO4_ED = LABEL

	private BigDecimal volAcetZn; // 32. qms_ctrl_block_a.vol_acet_zn = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoAcetatoZinco; // 33. AfaParamComponenteNptsVO.umm_descricao
	private String totParamAcetZnEd; // 34. qms_ctrl_block_a.TOT_PARAM_ACET_ZN_ED = LABEL

	private Short paramTempInfusaoSol; // 35. qms_ctrl_block_a.PARAM_TEMP_INFUSAO_SOL = PNP.TEMPO_H_INFUSAO_SOLUCAO
	private BigDecimal gotejoSolucao; // 36. qms_ctrl_block_a.GOTEJO_SOLUCAO = CPT.VELOCIDADE_ADMINISTRACAO
	private String unidadeMedidaGotejoSolucao; // 37. Considerar valor texto do parâmetro: P_AGHU_NPT_UNIDADE_MEDIDA_MLH

	private BigDecimal paramLip; // 38. qms_ctrl_block_a.PARAM_LIP = IPN.QTDE_BASE_CALCULO
	private String unidParamLip; // 39. qms_ctrl_block_a.unid_param_lip = LABEL
	private String totParamLipEd; // 40. qms_ctrl_block_a.TOT_PARAM_LIP_ED = LABEL
	private DominioTipoLipidio paramTipoLip; // 41. qms_ctrl_block_a.PARAM_TIPO_LIP = PNP.PARAM_TIPO_LIPIDIO
	private BigDecimal volLipidios10; // 42. qms_ctrl_block_a.VOL_LIPIDIOS_10 = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoLipidios10; // 43. AfaParamComponenteNptsVO.umm_descrica

	private BigDecimal volLipidios20; // 44. qms_ctrl_block_a.VOL_LIPIDIOS_20 = IPN.QTDE_CALCULADA ou IPN.QTDE_PRESCRITA
	private String ummDescricaoLipidios20; // 45. Considerar AfaParamComponenteNptsVO.umm_descricao

	private Short paramTempInfusaoLip; // 46. qms_ctrl_block_a.PARAM_TEMP_INFUSAO_LIP = PNP.TEMPO_H_INFUSAO_LIPIDIOS
	private BigDecimal gotejoLipidios; // 47. qms_ctrl_block_a.GOTEJO_LIPIDIOS = CPT.VELOCIDADE_ADMINISTRACAO
	private String unidadeMedidaGotejoLipidios; // 48. Considerar valor texto do parâmetro: P_AGHU_NPT_UNIDADE_MEDIDA_MLH

	/*
	 * CAMPOS DA ANÁLISE FINAL
	 */
	private String volumeMlDiaEd; // 49. qms_ctrl_block_a.VOLUME_ML_DIA_ED = PC09
	private String caloriasDiaEd; // 50. qms_ctrl_block_a.CALORIAS_DIA_ED = PC09
	private String caloriasKgDiaEd; // 51. qms_ctrl_block_a.CALORIAS_KG_DIA_ED = PC09
	private String relCalGNitroEd; // 52. qms_ctrl_block_a.REL_CAL_G_NITRO_ED = PC09
	private String percCalAminEd; // 53. qms_ctrl_block_a.PERC_CAL_AMIN_ED = PC09
	private String percCalLipidiosEd; // 54. qms_ctrl_block_a.PERC_CAL_LIPIDIOS_ED = PC09
	private String percCalGlicoseEd; // 55. qms_ctrl_block_a.PERC_CAL_GLICOSE_ED = PC09
	private String relGlicoseLipiEd; // 56. qms_ctrl_block_a.REL_GLICOSE_LIPI_ED = PC09
	private String concGlicoseSemLipiEd; // 57. qms_ctrl_block_a.CONC_GLICOSE_SEM_LIPI_ED = PC09
	private String concGlicoseComLipiEd; // 58. qms_ctrl_block_a.CONC_GLICOSE_COM_LIPI_ED = PC09
	private String taxaInfusaoLipiEd; // 59. qms_ctrl_block_a.TAXA_INFUSAO_LIPI_ED = PC09
	private String osmolSemLipiEd; // 60. qms_ctrl_block_a.OSMOL_SEM_LIPI_ED = PC09
	private String osmolComLipiEd; // 61. qms_ctrl_block_a.OSMOL_COM_LIPI_ED = PC09
	private String relCalcioFosforoEd; // qms_ctrl_block_a.REL_CALCIO_FOSFORO_ED

	/*
	 * CAMPO DA MENSAGEM
	 */
	private String mensagem; // 62. Considerar RN10

	/*
	 * VARIÁVEIS UTILIZADAS NA CAMADA NEGOCIAL (NÃO TELA!)
	 */
	private BigDecimal paramPotassio; // qms_ctrl_block_a.param_potassio
	private BigDecimal paramPercKcl; // qms_ctrl_block_a.param_perc_kcl
	private BigDecimal paramPercK3po4; // qms_ctrl_block_a.param_perc_k3po4
	private BigDecimal paramCalcio; // qms_ctrl_block_a.param_calcio
	private BigDecimal paramMagnesio; // qms_ctrl_block_a.param_magnesio
	private BigDecimal paramSodio; // qms_ctrl_block_a.param_sodio
	private BigDecimal paramAcetZn; // qms_ctrl_block_a.param_acet_zn
	private BigDecimal volGlicose5; // qms_ctrl_block_a.vol_glicose_5
	private BigDecimal volGlucoCa; // qms_ctrl_block_a.vol_gluco_ca
	private BigDecimal totParamAmin; // qms_ctrl_block_a.tot_param_amin
	private BigDecimal totParamLip; // qms_ctrl_block_a.tot_param_lip
	private BigDecimal totParamTig; // qms_ctrl_block_a.tot_param_tig
	private BigDecimal totParamHeparina; // qms_ctrl_block_a.tot_param_heparina
	private BigDecimal totParamSodio; // qms_ctrl_block_a.tot_param_sodio
	private BigDecimal totParamPotassio; // qms_ctrl_block_a.tot_param_potassio
	private String totParamPotassioEd; // qms_ctrl_block_a.tot_param_potassio_ed
	private BigDecimal totParamMagnesio; // qms_ctrl_block_a.tot_param_magnesio
	private String totParamMagnesioEd; // tqms_ctrl_block_a.ot_param_magnesio_ed
	private BigDecimal totParamCalcio; // qms_ctrl_block_a.tot_param_calcio
	private String totParamCalcioEd; // qms_ctrl_block_a.tot_param_calcio_ed
	private BigDecimal totParamAcetZn; // qms_ctrl_block_a.tot_param_acet_zn
	private BigDecimal volumeMlDia; // qms_ctrl_block_a.VOLUME_ML_DIA
	private BigDecimal paramVolDes; // qms_ctrl_block_a.PARAM_VOL_DES
	private BigDecimal totParamVolDes; // qms_ctrl_block_a.TOT_PARAM_VOL_DES
	private BigDecimal totParamVolCalc; // qms_ctrl_block_a.TOT_PARAM_VOL_CALC
	private BigDecimal caloriasDia; // qms_ctrl_block_a.CALORIAS_DIA
	private BigDecimal caloriasKgDia; // qms_ctrl_block_a.CALORIAS_KG_DIA
	private BigDecimal relCalGNitro; // qms_ctrl_block_a.REL_CAL_G_NITRO
	private BigDecimal percCalAmin; // qms_ctrl_block_a.PERC_CAL_AMIN
	private BigDecimal percCalLipidios; // qms_ctrl_block_a.PERC_CAL_LIPIDIOS
	private BigDecimal percCalGlicose; // qms_ctrl_block_a.PERC_CAL_GLICOSE
	private BigDecimal relGlicoseLipiL; // qms_ctrl_block_a.REL_GLICOSE_LIPI_L
	private BigDecimal relGlicoseLipiG; // qms_ctrl_block_a.REL_GLICOSE_LIPI_G
	private BigDecimal relCalcioFosforo; // qms_ctrl_block_a.REL_CALCIO_FOSFORO
	private BigDecimal concGlicoseSemLipi; // qms_ctrl_block_a.CONC_GLICOSE_SEM_LIPI
	private BigDecimal concGlicoseComLipi; // qms_ctrl_block_a.CONC_GLICOSE_COM_LIPI
	private BigDecimal taxaInfusaoLipi; // qms_ctrl_block_a.TAXA_INFUSAO_LIPI
	private BigDecimal osmolSemLipi; // qms_ctrl_block_a.OSMOL_SEM_LIPI
	private BigDecimal osmolComLipi; // qms_ctrl_block_a.OSMOL_COM_LIPI
	private BigDecimal volAaPed10; // qms_ctrl_block_a.vol_aa_ped_10 = ?? IPN.QTDE_PRESCRITA ou IPN.QTDE_CALCULADA ??
	private BigDecimal volOligo; // qms_ctrl_block_a.vol_oligo
	private BigDecimal volOligoPed; // qms_ctrl_block_a.vol_oligo_ped
	private BigDecimal paramOligo; // qms_ctrl_block_a.param_oligo
	private BigDecimal totParamOligo; // qms_ctrl_block_a.param_oligo
	private String totParamOligoEd; // qms_ctrl_block_a.tot_param_oligo_ed

	/*
	 * Booleanos "POP_" de cada componente, que definem se o componente deve ser atualizado ou inserido
	 */

	private Boolean popLipidios10; // QMS_CTRL_BLOCK_A.POP_LIPIDIOS_10
	private Boolean popLipidios20; // QMS_CTRL_BLOCK_A.POP_LIPIDIOS_20
	private Boolean popAmin10; // QMS_CTRL_BLOCK_A.POP_AMIN_10
	private Boolean popAminPed10; // QMS_CTRL_BLOCK_A.POP_AMIN_PED_10
	private Boolean popAcetZn; // QMS_CTRL_BLOCK_A.POP_ACET_ZN
	private Boolean popNacl20; // QMS_CTRL_BLOCK_A.POP_NACL_20
	private Boolean popGlucoCa; // QMS_CTRL_BLOCK_A.POP_GLUCO_CA
	private Boolean popVolKcl; // QMS_CTRL_BLOCK_A.POP_VOL_KCL
	private Boolean popK3PO4; // QMS_CTRL_BLOCK_A.POP_K3PO4
	private Boolean popMGSO4; // QMS_CTRL_BLOCK_A.POP_MGSO4
	private Boolean popGlicose50; // QMS_CTRL_BLOCK_A.POP_GLICOSE_50
	private Boolean popGlicose10; // QMS_CTRL_BLOCK_A.POP_GLICOSE_10
	private Boolean popGlicose5; // QMS_CTRL_BLOCK_A.POP_GLICOSE_5
	private Boolean popHeparina; // QMS_CTRL_BLOCK_A.POP_HEPARINA
	private Boolean popOligo; // QMS_CTRL_BLOCK_A.POP_OLIGO
	private Boolean popOligoPed; // QMS_CTRL_BLOCK_A.POP_PED
	
	/*
	 * Atributos utilizados na realização do cálculo
	 */
	private DadosPesoAlturaVO dadosPesoAlturaVO = new DadosPesoAlturaVO();
	private CalculoParametrosFixosVO calculoParametrosFixosVO = new CalculoParametrosFixosVO(); // MPMK_VARIAVEIS
	private AfaParamComponenteNptsVO afaParamComponenteNptsVO = new AfaParamComponenteNptsVO(); // MPMP_CARGA_CALCULO_NPT

	public BigDecimal getParamAmin() {
		return paramAmin;
	}

	public void setParamAmin(BigDecimal paramAmin) {
		this.paramAmin = paramAmin;
	}

	public String getUnidParamAmin() {
		return unidParamAmin;
	}

	public void setUnidParamAmin(String unidParamAmin) {
		this.unidParamAmin = unidParamAmin;
	}

	public String getTotParamAminEd() {
		return totParamAminEd;
	}

	public void setTotParamAminEd(String totParamAminEd) {
		this.totParamAminEd = totParamAminEd;
	}

	public BigDecimal getVolAa10() {
		return volAa10;
	}

	public void setVolAa10(BigDecimal volAa10) {
		this.volAa10 = volAa10;
	}

	public String getUmmDescricaoAminoacidosAd() {
		return ummDescricaoAminoacidosAd;
	}

	public void setUmmDescricaoAminoacidosAd(String ummDescricaoAminoacidosAd) {
		this.ummDescricaoAminoacidosAd = ummDescricaoAminoacidosAd;
	}

	public BigDecimal getParamTig() {
		return paramTig;
	}

	public void setParamTig(BigDecimal paramTig) {
		this.paramTig = paramTig;
	}

	public String getUnidParamTig() {
		return unidParamTig;
	}

	public void setUnidParamTig(String unidParamTig) {
		this.unidParamTig = unidParamTig;
	}

	public String getTotParamTigEd() {
		return totParamTigEd;
	}

	public void setTotParamTigEd(String totParamTigEd) {
		this.totParamTigEd = totParamTigEd;
	}

	public BigDecimal getVolGlicose50() {
		return volGlicose50;
	}

	public void setVolGlicose50(BigDecimal volGlicose50) {
		this.volGlicose50 = volGlicose50;
	}

	public String getUmmDescricaoGlicose50() {
		return ummDescricaoGlicose50;
	}

	public void setUmmDescricaoGlicose50(String ummDescricaoGlicose50) {
		this.ummDescricaoGlicose50 = ummDescricaoGlicose50;
	}

	public BigDecimal getParamPercGlic50() {
		return paramPercGlic50;
	}

	public void setParamPercGlic50(BigDecimal paramPercGlic50) {
		this.paramPercGlic50 = paramPercGlic50;
	}

	public BigDecimal getParamPercGlic10() {
		return paramPercGlic10;
	}

	public void setParamPercGlic10(BigDecimal paramPercGlic10) {
		this.paramPercGlic10 = paramPercGlic10;
	}

	public BigDecimal getVolGlicose10() {
		return volGlicose10;
	}

	public void setVolGlicose10(BigDecimal volGlicose10) {
		this.volGlicose10 = volGlicose10;
	}

	public String getUmmDescricaoGlicose10() {
		return ummDescricaoGlicose10;
	}

	public void setUmmDescricaoGlicose10(String ummDescricaoGlicose10) {
		this.ummDescricaoGlicose10 = ummDescricaoGlicose10;
	}

	public BigDecimal getParamHeparina() {
		return paramHeparina;
	}

	public void setParamHeparina(BigDecimal paramHeparina) {
		this.paramHeparina = paramHeparina;
	}

	public String getUnidParamHeparina() {
		return unidParamHeparina;
	}

	public void setUnidParamHeparina(String unidParamHeparina) {
		this.unidParamHeparina = unidParamHeparina;
	}

	public String getTotParamHeparinaEd() {
		return totParamHeparinaEd;
	}

	public void setTotParamHeparinaEd(String totParamHeparinaEd) {
		this.totParamHeparinaEd = totParamHeparinaEd;
	}

	public BigDecimal getVolHeparina() {
		return volHeparina;
	}

	public void setVolHeparina(BigDecimal volHeparina) {
		this.volHeparina = volHeparina;
	}

	public String getUmmDescricaoHeparina() {
		return ummDescricaoHeparina;
	}

	public void setUmmDescricaoHeparina(String ummDescricaoHeparina) {
		this.ummDescricaoHeparina = ummDescricaoHeparina;
	}

	public BigDecimal getVolNacl20() {
		return volNacl20;
	}

	public void setVolNacl20(BigDecimal volNacl20) {
		this.volNacl20 = volNacl20;
	}

	public String getUmmDescricaoCloretoSodio() {
		return ummDescricaoCloretoSodio;
	}

	public void setUmmDescricaoCloretoSodio(String ummDescricaoCloretoSodio) {
		this.ummDescricaoCloretoSodio = ummDescricaoCloretoSodio;
	}

	public String getTotParamSodioEd() {
		return totParamSodioEd;
	}

	public void setTotParamSodioEd(String totParamSodioEd) {
		this.totParamSodioEd = totParamSodioEd;
	}

	public BigDecimal getVolKcl() {
		return volKcl;
	}

	public void setVolKcl(BigDecimal volKcl) {
		this.volKcl = volKcl;
	}

	public String getUmmDescricaoCloretoPotassio() {
		return ummDescricaoCloretoPotassio;
	}

	public void setUmmDescricaoCloretoPotassio(String ummDescricaoCloretoPotassio) {
		this.ummDescricaoCloretoPotassio = ummDescricaoCloretoPotassio;
	}

	public String getTotParamKclEd() {
		return totParamKclEd;
	}

	public void setTotParamKclEd(String totParamKclEd) {
		this.totParamKclEd = totParamKclEd;
	}

	public BigDecimal getVolK3Po4() {
		return volK3Po4;
	}

	public void setVolK3Po4(BigDecimal volK3Po4) {
		this.volK3Po4 = volK3Po4;
	}

	public String getUmmDescricaoSulfatoMagnesio() {
		return ummDescricaoSulfatoMagnesio;
	}

	public void setUmmDescricaoSulfatoMagnesio(String ummDescricaoSulfatoMagnesio) {
		this.ummDescricaoSulfatoMagnesio = ummDescricaoSulfatoMagnesio;
	}

	public String getTotParamK3po4Ed() {
		return totParamK3po4Ed;
	}

	public void setTotParamK3po4Ed(String totParamK3po4Ed) {
		this.totParamK3po4Ed = totParamK3po4Ed;
	}

	public BigDecimal getVolMgso4() {
		return volMgso4;
	}

	public void setVolMgso4(BigDecimal volMgso4) {
		this.volMgso4 = volMgso4;
	}

	public String getUmmDescricaoFosfatoPotassio() {
		return ummDescricaoFosfatoPotassio;
	}

	public void setUmmDescricaoFosfatoPotassio(String ummDescricaoFosfatoPotassio) {
		this.ummDescricaoFosfatoPotassio = ummDescricaoFosfatoPotassio;
	}

	public String getTotParamMgso4Ed() {
		return totParamMgso4Ed;
	}

	public void setTotParamMgso4Ed(String totParamMgso4Ed) {
		this.totParamMgso4Ed = totParamMgso4Ed;
	}

	public BigDecimal getVolAcetZn() {
		return volAcetZn;
	}

	public void setVolAcetZn(BigDecimal volAcetZn) {
		this.volAcetZn = volAcetZn;
	}

	public String getUmmDescricaoAcetatoZinco() {
		return ummDescricaoAcetatoZinco;
	}

	public void setUmmDescricaoAcetatoZinco(String ummDescricaoAcetatoZinco) {
		this.ummDescricaoAcetatoZinco = ummDescricaoAcetatoZinco;
	}

	public String getTotParamAcetZnEd() {
		return totParamAcetZnEd;
	}

	public void setTotParamAcetZnEd(String totParamAcetZnEd) {
		this.totParamAcetZnEd = totParamAcetZnEd;
	}

	public Short getParamTempInfusaoSol() {
		return paramTempInfusaoSol;
	}

	public void setParamTempInfusaoSol(Short paramTempInfusaoSol) {
		this.paramTempInfusaoSol = paramTempInfusaoSol;
	}

	public BigDecimal getGotejoSolucao() {
		return gotejoSolucao;
	}

	public void setGotejoSolucao(BigDecimal gotejoSolucao) {
		this.gotejoSolucao = gotejoSolucao;
	}

	public String getUnidadeMedidaGotejoSolucao() {
		return unidadeMedidaGotejoSolucao;
	}

	public void setUnidadeMedidaGotejoSolucao(String unidadeMedidaGotejoSolucao) {
		this.unidadeMedidaGotejoSolucao = unidadeMedidaGotejoSolucao;
	}

	public BigDecimal getParamLip() {
		return paramLip;
	}

	public void setParamLip(BigDecimal paramLip) {
		this.paramLip = paramLip;
	}

	public String getUnidParamLip() {
		return unidParamLip;
	}

	public void setUnidParamLip(String unidParamLip) {
		this.unidParamLip = unidParamLip;
	}

	public String getTotParamLipEd() {
		return totParamLipEd;
	}

	public void setTotParamLipEd(String totParamLipEd) {
		this.totParamLipEd = totParamLipEd;
	}

	public DominioTipoLipidio getParamTipoLip() {
		return paramTipoLip;
	}

	public void setParamTipoLip(DominioTipoLipidio paramTipoLip) {
		this.paramTipoLip = paramTipoLip;
	}

	public BigDecimal getVolLipidios10() {
		return volLipidios10;
	}

	public void setVolLipidios10(BigDecimal volLipidios10) {
		this.volLipidios10 = volLipidios10;
	}

	public String getUmmDescricaoLipidios10() {
		return ummDescricaoLipidios10;
	}

	public void setUmmDescricaoLipidios10(String ummDescricaoLipidios10) {
		this.ummDescricaoLipidios10 = ummDescricaoLipidios10;
	}

	public BigDecimal getVolLipidios20() {
		return volLipidios20;
	}

	public void setVolLipidios20(BigDecimal volLipidios20) {
		this.volLipidios20 = volLipidios20;
	}

	public String getUmmDescricaoLipidios20() {
		return ummDescricaoLipidios20;
	}

	public void setUmmDescricaoLipidios20(String ummDescricaoLipidios20) {
		this.ummDescricaoLipidios20 = ummDescricaoLipidios20;
	}

	public Short getParamTempInfusaoLip() {
		return paramTempInfusaoLip;
	}

	public void setParamTempInfusaoLip(Short paramTempInfusaoLip) {
		this.paramTempInfusaoLip = paramTempInfusaoLip;
	}

	public BigDecimal getGotejoLipidios() {
		return gotejoLipidios;
	}

	public void setGotejoLipidios(BigDecimal gotejoLipidios) {
		this.gotejoLipidios = gotejoLipidios;
	}

	public String getUnidadeMedidaGotejoLipidios() {
		return unidadeMedidaGotejoLipidios;
	}

	public void setUnidadeMedidaGotejoLipidios(String unidadeMedidaGotejoLipidios) {
		this.unidadeMedidaGotejoLipidios = unidadeMedidaGotejoLipidios;
	}

	public String getVolumeMlDiaEd() {
		return volumeMlDiaEd;
	}

	public void setVolumeMlDiaEd(String volumeMlDiaEd) {
		this.volumeMlDiaEd = volumeMlDiaEd;
	}

	public String getCaloriasDiaEd() {
		return caloriasDiaEd;
	}

	public void setCaloriasDiaEd(String caloriasDiaEd) {
		this.caloriasDiaEd = caloriasDiaEd;
	}

	public String getCaloriasKgDiaEd() {
		return caloriasKgDiaEd;
	}

	public void setCaloriasKgDiaEd(String caloriasKgDiaEd) {
		this.caloriasKgDiaEd = caloriasKgDiaEd;
	}

	public String getRelCalGNitroEd() {
		return relCalGNitroEd;
	}

	public void setRelCalGNitroEd(String relCalGNitroEd) {
		this.relCalGNitroEd = relCalGNitroEd;
	}

	public String getPercCalAminEd() {
		return percCalAminEd;
	}

	public void setPercCalAminEd(String percCalAminEd) {
		this.percCalAminEd = percCalAminEd;
	}

	public String getPercCalLipidiosEd() {
		return percCalLipidiosEd;
	}

	public void setPercCalLipidiosEd(String percCalLipidiosEd) {
		this.percCalLipidiosEd = percCalLipidiosEd;
	}

	public String getPercCalGlicoseEd() {
		return percCalGlicoseEd;
	}

	public void setPercCalGlicoseEd(String percCalGlicoseEd) {
		this.percCalGlicoseEd = percCalGlicoseEd;
	}

	public String getRelGlicoseLipiEd() {
		return relGlicoseLipiEd;
	}

	public void setRelGlicoseLipiEd(String relGlicoseLipiEd) {
		this.relGlicoseLipiEd = relGlicoseLipiEd;
	}

	public String getConcGlicoseSemLipiEd() {
		return concGlicoseSemLipiEd;
	}

	public void setConcGlicoseSemLipiEd(String concGlicoseSemLipiEd) {
		this.concGlicoseSemLipiEd = concGlicoseSemLipiEd;
	}

	public String getConcGlicoseComLipiEd() {
		return concGlicoseComLipiEd;
	}

	public void setConcGlicoseComLipiEd(String concGlicoseComLipiEd) {
		this.concGlicoseComLipiEd = concGlicoseComLipiEd;
	}

	public String getTaxaInfusaoLipiEd() {
		return taxaInfusaoLipiEd;
	}

	public void setTaxaInfusaoLipiEd(String taxaInfusaoLipiEd) {
		this.taxaInfusaoLipiEd = taxaInfusaoLipiEd;
	}

	public String getOsmolSemLipiEd() {
		return osmolSemLipiEd;
	}

	public void setOsmolSemLipiEd(String osmolSemLipiEd) {
		this.osmolSemLipiEd = osmolSemLipiEd;
	}

	public String getOsmolComLipiEd() {
		return osmolComLipiEd;
	}

	public void setOsmolComLipiEd(String osmolComLipiEd) {
		this.osmolComLipiEd = osmolComLipiEd;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public BigDecimal getParamPotassio() {
		return paramPotassio;
	}

	public void setParamPotassio(BigDecimal paramPotassio) {
		this.paramPotassio = paramPotassio;
	}

	public BigDecimal getParamPercKcl() {
		return paramPercKcl;
	}

	public void setParamPercKcl(BigDecimal paramPercKcl) {
		this.paramPercKcl = paramPercKcl;
	}

	public BigDecimal getParamPercK3po4() {
		return paramPercK3po4;
	}

	public void setParamPercK3po4(BigDecimal paramPercK3po4) {
		this.paramPercK3po4 = paramPercK3po4;
	}

	public BigDecimal getParamCalcio() {
		return paramCalcio;
	}

	public void setParamCalcio(BigDecimal paramCalcio) {
		this.paramCalcio = paramCalcio;
	}

	public BigDecimal getParamMagnesio() {
		return paramMagnesio;
	}

	public void setParamMagnesio(BigDecimal paramMagnesio) {
		this.paramMagnesio = paramMagnesio;
	}

	public BigDecimal getParamAcetZn() {
		return paramAcetZn;
	}

	public void setParamAcetZn(BigDecimal paramAcetZn) {
		this.paramAcetZn = paramAcetZn;
	}

	public BigDecimal getVolGlicose5() {
		return volGlicose5;
	}

	public void setVolGlicose5(BigDecimal volGlicose5) {
		this.volGlicose5 = volGlicose5;
	}

	public BigDecimal getVolGlucoCa() {
		return volGlucoCa;
	}

	public void setVolGlucoCa(BigDecimal volGlucoCa) {
		this.volGlucoCa = volGlucoCa;
	}

	public BigDecimal getTotParamAmin() {
		return totParamAmin;
	}

	public void setTotParamAmin(BigDecimal totParamAmin) {
		this.totParamAmin = totParamAmin;
	}

	public BigDecimal getTotParamLip() {
		return totParamLip;
	}

	public void setTotParamLip(BigDecimal totParamLip) {
		this.totParamLip = totParamLip;
	}

	public BigDecimal getTotParamTig() {
		return totParamTig;
	}

	public void setTotParamTig(BigDecimal totParamTig) {
		this.totParamTig = totParamTig;
	}

	public BigDecimal getTotParamHeparina() {
		return totParamHeparina;
	}

	public void setTotParamHeparina(BigDecimal totParamHeparina) {
		this.totParamHeparina = totParamHeparina;
	}

	public BigDecimal getTotParamSodio() {
		return totParamSodio;
	}

	public void setTotParamSodio(BigDecimal totParamSodio) {
		this.totParamSodio = totParamSodio;
	}

	public BigDecimal getTotParamPotassio() {
		return totParamPotassio;
	}

	public void setTotParamPotassio(BigDecimal totParamPotassio) {
		this.totParamPotassio = totParamPotassio;
	}

	public String getTotParamPotassioEd() {
		return totParamPotassioEd;
	}

	public void setTotParamPotassioEd(String totParamPotassioEd) {
		this.totParamPotassioEd = totParamPotassioEd;
	}

	public BigDecimal getTotParamMagnesio() {
		return totParamMagnesio;
	}

	public void setTotParamMagnesio(BigDecimal totParamMagnesio) {
		this.totParamMagnesio = totParamMagnesio;
	}

	public String getTotParamMagnesioEd() {
		return totParamMagnesioEd;
	}

	public void setTotParamMagnesioEd(String totParamMagnesioEd) {
		this.totParamMagnesioEd = totParamMagnesioEd;
	}

	public BigDecimal getParamSodio() {
		return paramSodio;
	}

	public void setParamSodio(BigDecimal paramSodio) {
		this.paramSodio = paramSodio;
	}

	public BigDecimal getTotParamCalcio() {
		return totParamCalcio;
	}

	public void setTotParamCalcio(BigDecimal totParamCalcio) {
		this.totParamCalcio = totParamCalcio;
	}

	public String getTotParamCalcioEd() {
		return totParamCalcioEd;
	}

	public void setTotParamCalcioEd(String totParamCalcioEd) {
		this.totParamCalcioEd = totParamCalcioEd;
	}

	public BigDecimal getTotParamAcetZn() {
		return totParamAcetZn;
	}

	public void setTotParamAcetZn(BigDecimal totParamAcetZn) {
		this.totParamAcetZn = totParamAcetZn;
	}

	public BigDecimal getVolumeMlDia() {
		return volumeMlDia;
	}

	public void setVolumeMlDia(BigDecimal volumeMlDia) {
		this.volumeMlDia = volumeMlDia;
	}

	public BigDecimal getParamVolDes() {
		return paramVolDes;
	}

	public void setParamVolDes(BigDecimal paramVolDes) {
		this.paramVolDes = paramVolDes;
	}

	public BigDecimal getTotParamVolDes() {
		return totParamVolDes;
	}

	public void setTotParamVolDes(BigDecimal totParamVolDes) {
		this.totParamVolDes = totParamVolDes;
	}

	public BigDecimal getTotParamVolCalc() {
		return totParamVolCalc;
	}

	public void setTotParamVolCalc(BigDecimal totParamVolCalc) {
		this.totParamVolCalc = totParamVolCalc;
	}

	public BigDecimal getCaloriasDia() {
		return caloriasDia;
	}

	public void setCaloriasDia(BigDecimal caloriasDia) {
		this.caloriasDia = caloriasDia;
	}

	public BigDecimal getCaloriasKgDia() {
		return caloriasKgDia;
	}

	public void setCaloriasKgDia(BigDecimal caloriasKgDia) {
		this.caloriasKgDia = caloriasKgDia;
	}

	public BigDecimal getRelCalGNitro() {
		return relCalGNitro;
	}

	public void setRelCalGNitro(BigDecimal relCalGNitro) {
		this.relCalGNitro = relCalGNitro;
	}

	public BigDecimal getPercCalAmin() {
		return percCalAmin;
	}

	public void setPercCalAmin(BigDecimal percCalAmin) {
		this.percCalAmin = percCalAmin;
	}

	public BigDecimal getPercCalLipidios() {
		return percCalLipidios;
	}

	public void setPercCalLipidios(BigDecimal percCalLipidios) {
		this.percCalLipidios = percCalLipidios;
	}

	public BigDecimal getPercCalGlicose() {
		return percCalGlicose;
	}

	public void setPercCalGlicose(BigDecimal percCalGlicose) {
		this.percCalGlicose = percCalGlicose;
	}

	public BigDecimal getRelGlicoseLipiL() {
		return relGlicoseLipiL;
	}

	public void setRelGlicoseLipiL(BigDecimal relGlicoseLipiL) {
		this.relGlicoseLipiL = relGlicoseLipiL;
	}

	public BigDecimal getRelGlicoseLipiG() {
		return relGlicoseLipiG;
	}

	public void setRelGlicoseLipiG(BigDecimal relGlicoseLipiG) {
		this.relGlicoseLipiG = relGlicoseLipiG;
	}

	public BigDecimal getRelCalcioFosforo() {
		return relCalcioFosforo;
	}

	public void setRelCalcioFosforo(BigDecimal relCalcioFosforo) {
		this.relCalcioFosforo = relCalcioFosforo;
	}

	public BigDecimal getConcGlicoseSemLipi() {
		return concGlicoseSemLipi;
	}

	public void setConcGlicoseSemLipi(BigDecimal concGlicoseSemLipi) {
		this.concGlicoseSemLipi = concGlicoseSemLipi;
	}

	public BigDecimal getConcGlicoseComLipi() {
		return concGlicoseComLipi;
	}

	public void setConcGlicoseComLipi(BigDecimal concGlicoseComLipi) {
		this.concGlicoseComLipi = concGlicoseComLipi;
	}

	public BigDecimal getTaxaInfusaoLipi() {
		return taxaInfusaoLipi;
	}

	public void setTaxaInfusaoLipi(BigDecimal taxaInfusaoLipi) {
		this.taxaInfusaoLipi = taxaInfusaoLipi;
	}

	public BigDecimal getOsmolSemLipi() {
		return osmolSemLipi;
	}

	public void setOsmolSemLipi(BigDecimal osmolSemLipi) {
		this.osmolSemLipi = osmolSemLipi;
	}

	public BigDecimal getOsmolComLipi() {
		return osmolComLipi;
	}

	public void setOsmolComLipi(BigDecimal osmolComLipi) {
		this.osmolComLipi = osmolComLipi;
	}

	public String getRelCalcioFosforoEd() {
		return relCalcioFosforoEd;
	}

	public void setRelCalcioFosforoEd(String relCalcioFosforoEd) {
		this.relCalcioFosforoEd = relCalcioFosforoEd;
	}

	public BigDecimal getVolAaPed10() {
		return volAaPed10;
	}

	public void setVolAaPed10(BigDecimal volAaPed10) {
		this.volAaPed10 = volAaPed10;
	}

	public BigDecimal getVolOligo() {
		return volOligo;
	}

	public void setVolOligo(BigDecimal volOligo) {
		this.volOligo = volOligo;
	}

	public BigDecimal getVolOligoPed() {
		return volOligoPed;
	}

	public void setVolOligoPed(BigDecimal volOligoPed) {
		this.volOligoPed = volOligoPed;
	}

	public BigDecimal getParamOligo() {
		return paramOligo;
	}

	public void setParamOligo(BigDecimal paramOligo) {
		this.paramOligo = paramOligo;
	}

	public BigDecimal getTotParamOligo() {
		return totParamOligo;
	}

	public void setTotParamOligo(BigDecimal totParamOligo) {
		this.totParamOligo = totParamOligo;
	}

	public String getTotParamOligoEd() {
		return totParamOligoEd;
	}

	public void setTotParamOligoEd(String totParamOligoEd) {
		this.totParamOligoEd = totParamOligoEd;
	}

	public Boolean getPopLipidios10() {
		return popLipidios10;
	}

	public void setPopLipidios10(Boolean popLipidios10) {
		this.popLipidios10 = popLipidios10;
	}

	public Boolean getPopLipidios20() {
		return popLipidios20;
	}

	public void setPopLipidios20(Boolean popLipidios20) {
		this.popLipidios20 = popLipidios20;
	}

	public Boolean getPopAmin10() {
		return popAmin10;
	}

	public void setPopAmin10(Boolean popAmin10) {
		this.popAmin10 = popAmin10;
	}

	public Boolean getPopAminPed10() {
		return popAminPed10;
	}

	public void setPopAminPed10(Boolean popAminPed10) {
		this.popAminPed10 = popAminPed10;
	}

	public Boolean getPopAcetZn() {
		return popAcetZn;
	}

	public void setPopAcetZn(Boolean popAcetZn) {
		this.popAcetZn = popAcetZn;
	}

	public Boolean getPopNacl20() {
		return popNacl20;
	}

	public void setPopNacl20(Boolean popNacl20) {
		this.popNacl20 = popNacl20;
	}

	public Boolean getPopGlucoCa() {
		return popGlucoCa;
	}

	public void setPopGlucoCa(Boolean popGlucoCa) {
		this.popGlucoCa = popGlucoCa;
	}

	public Boolean getPopVolKcl() {
		return popVolKcl;
	}

	public void setPopVolKcl(Boolean popVolKcl) {
		this.popVolKcl = popVolKcl;
	}

	public Boolean getPopK3PO4() {
		return popK3PO4;
	}

	public void setPopK3PO4(Boolean popK3PO4) {
		this.popK3PO4 = popK3PO4;
	}

	public Boolean getPopMGSO4() {
		return popMGSO4;
	}

	public void setPopMGSO4(Boolean popMGSO4) {
		this.popMGSO4 = popMGSO4;
	}

	public Boolean getPopGlicose50() {
		return popGlicose50;
	}

	public void setPopGlicose50(Boolean popGlicose50) {
		this.popGlicose50 = popGlicose50;
	}

	public Boolean getPopGlicose10() {
		return popGlicose10;
	}

	public void setPopGlicose10(Boolean popGlicose10) {
		this.popGlicose10 = popGlicose10;
	}

	public Boolean getPopGlicose5() {
		return popGlicose5;
	}

	public void setPopGlicose5(Boolean popGlicose5) {
		this.popGlicose5 = popGlicose5;
	}

	public Boolean getPopHeparina() {
		return popHeparina;
	}

	public void setPopHeparina(Boolean popHeparina) {
		this.popHeparina = popHeparina;
	}

	public Boolean getPopOligo() {
		return popOligo;
	}

	public void setPopOligo(Boolean popOligo) {
		this.popOligo = popOligo;
	}

	public Boolean getPopOligoPed() {
		return popOligoPed;
	}

	public void setPopOligoPed(Boolean popOligoPed) {
		this.popOligoPed = popOligoPed;
	}
	
	public DadosPesoAlturaVO getDadosPesoAlturaVO() {
		return dadosPesoAlturaVO;
	}
	
	public void setDadosPesoAlturaVO(DadosPesoAlturaVO dadosPesoAlturaVO) {
		this.dadosPesoAlturaVO = dadosPesoAlturaVO;
	}
	
	public AfaParamComponenteNptsVO getAfaParamComponenteNptsVO() {
		return afaParamComponenteNptsVO;
	}
	public void setAfaParamComponenteNptsVO(AfaParamComponenteNptsVO afaParamComponenteNptsVO) {
		this.afaParamComponenteNptsVO = afaParamComponenteNptsVO;
	}
	
	public CalculoParametrosFixosVO getCalculoParametrosFixosVO() {
		return calculoParametrosFixosVO;
	}
	
	public void setCalculoParametrosFixosVO(CalculoParametrosFixosVO calculoParametrosFixosVO) {
		this.calculoParametrosFixosVO = calculoParametrosFixosVO;
	}
}