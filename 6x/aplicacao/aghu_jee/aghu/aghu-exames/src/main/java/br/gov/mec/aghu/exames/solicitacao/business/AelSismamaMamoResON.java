/**
 * 
 */
package br.gov.mec.aghu.exames.solicitacao.business;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSismamaMama;
import br.gov.mec.aghu.dominio.DominioSismamaMamaExaminada;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaSimNaoNaoSabe;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoCadDAO;
import br.gov.mec.aghu.exames.dao.AelSismamaMamoResDAO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSismamaMamoCad;
import br.gov.mec.aghu.model.AelSismamaMamoRes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.Dominio;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @author aghu
 * 
 */
@Stateless
public class AelSismamaMamoResON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AelSismamaMamoResON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Inject
	private AelSismamaMamoResDAO aelSismamaMamoResDAO;
	
	@Inject
	private AelSismamaMamoCadDAO aelSismamaMamoCadDAO;

	public enum AelSismamaMamoResONExceptionCode implements BusinessExceptionCode {
		ERRO_CODIGO_RES_INVALIDO;
	}

	private static final long	serialVersionUID				= 6626690216836374221L;

	private static final String	RESPOSTA_BOOLEANA_VERDADEIRA	= "3";
	private static final String	RESPOSTA_BOOLEANA_FALSA			= "0";

	/**
	 * @param values
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @throws ApplicationBusinessException
	 */
	public void gravarAelSismamaMamoRes(final Map<DominioSismamaMamoCadCodigo, Object> values, final Integer iseSoeSeq, final Short iseSeqp) throws ApplicationBusinessException {
		if (values != null && !values.isEmpty()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			for (final DominioSismamaMamoCadCodigo key : values.keySet()) {
				if (key.equals(DominioSismamaMamoCadCodigo.C_MAMO_DIAG)) {
					continue;
				}

				final AelSismamaMamoCad aelSismamaMamoCad = this.getAelSismamaMamoCadDAO().obterPorChavePrimaria(key.toString());
				if (aelSismamaMamoCad == null) {
					throw new ApplicationBusinessException(AelSismamaMamoResONExceptionCode.ERRO_CODIGO_RES_INVALIDO, key.toString());
				}
				final AelSismamaMamoRes aelSismamaMamoRes = new AelSismamaMamoRes();
				aelSismamaMamoRes.setAelSismamaMamoCad(aelSismamaMamoCad);
				aelSismamaMamoRes.setCriadoEm(new Date());

				AelItemSolicitacaoExames itemSolicitacaoExame = getAelItemSolicitacaoExameDAO().obterPorId(iseSoeSeq, iseSeqp);
				aelSismamaMamoRes.setItemSolicitacaoExames(itemSolicitacaoExame);

				aelSismamaMamoRes.setServidor(servidorLogado);

				if (key.equals(DominioSismamaMamoCadCodigo.C_CLI_DIAG)) {
					final Boolean cMamoDiag = (Boolean) values.get(DominioSismamaMamoCadCodigo.C_MAMO_DIAG);
					final String resposta;
					if (cMamoDiag != null && cMamoDiag 
							&& values.get(DominioSismamaMamoCadCodigo.C_CLI_DIAG) != null
							&& values.get(DominioSismamaMamoCadCodigo.C_CLI_DIAG) instanceof DominioSismamaMamaExaminada) {
						resposta = Integer.toString(((DominioSismamaMamaExaminada)values.get(DominioSismamaMamoCadCodigo.C_CLI_DIAG)).getCodigo());
					} else {
						resposta = RESPOSTA_BOOLEANA_FALSA;
					}
					aelSismamaMamoRes.setResposta(resposta);
				} else {
					final Object value = values.get(key);
					if (value != null) {
						if (value instanceof Boolean) {
							final Boolean resposta = (Boolean) value;
							aelSismamaMamoRes.setResposta(resposta ? RESPOSTA_BOOLEANA_VERDADEIRA : RESPOSTA_BOOLEANA_FALSA);
						} else if (value instanceof Dominio) {
							final Dominio resposta = (Dominio) value;
							aelSismamaMamoRes.setResposta(Integer.toString(resposta.getCodigo()));
						} else if (value instanceof Date) {
							final SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yyyy HH:mm:ss", Locale.ENGLISH);
							final String resposta = sdf.format(DateUtil.truncaData((Date) value));
							aelSismamaMamoRes.setResposta(resposta.toUpperCase());
						} else {
							aelSismamaMamoRes.setResposta(value.toString());
						}
					}
				}
				this.getAelSismamaMamoResDAO().persistir(aelSismamaMamoRes);
			}
			this.getAelSismamaMamoResDAO().flush();
		}
	}
	
	public Map<String, Object> inicializarMapSismama() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put(DominioSismamaMamoCadCodigo.C_ANM_NOD.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_BIOPSIA_LI_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_BIOPSIA_LI_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_DIREITA.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_DUTECT_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_DUTECT_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESQUERDA.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESVAZIA_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESVAZIA_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_M_PELE_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_M_PELE_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_IMP_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_IMP_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_RED_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_RED_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_RECON_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_RECON_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_SEGMEN_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_SEGMEN_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_TUMOR_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_ANO_TUMOR_E.name(), null);
		
		map.put(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_INC_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_INC_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_EXC_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_EXC_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_CENTRALECTOMIA_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_CENTRALECTOMIA_E.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_MASTEC_POUP_PE_D.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_MASTEC_POUP_PE_E.name(), null);

		map.put(DominioSismamaMamoCadCodigo.C_ANA_GRAVIDA.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_MENOP_IDADE.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_MENOP_NLEMB.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_MEST_NLEMB.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_NAOFEZCIRUR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_NUNCAMEST.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_RADIO.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MDIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANA_USAHORMONIO.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		map.put(DominioSismamaMamoCadCodigo.C_ANM_EXA_PROF.name(), DominioSismamaMama.NAO_SABE);
		map.put(DominioSismamaMamoCadCodigo.C_ANM_MAMO_ANO.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_ANM_MAMOGRAF.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		map.put(DominioSismamaMamoCadCodigo.C_ANM_NOD_MD.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANM_NOD_ME.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_ANM_PARENTE_CANCER.name(), DominioSismamaSimNaoNaoSabe.NAO_SABE);
		map.put(DominioSismamaMamoCadCodigo.C_AVALIACAO_ADJ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_DESC_DIR.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_DESC_ESQ.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name(), null);
		map.put(DominioSismamaMamoCadCodigo.C_MAMO_DIAG.name(), true);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_PA_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_PA_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QIE_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QIE_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QII_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QII_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSE_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSE_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSI_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSI_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_RRA_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_RRA_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQEXT_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQEXT_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINF_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINF_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINT_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINT_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQSUP_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQSUP_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_LES_PALPA.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_LINF_AX_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_LINF_AX_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_LINF_SUPRA_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_LINF_SUPRA_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_PA_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_PA_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QIE_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QIE_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QII_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QII_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSE_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSE_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSI_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSI_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_RRA_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_RRA_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQEXT_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQEXT_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINF_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINF_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINT_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINT_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQSUP_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQSUP_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_PAPIL_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CLI_PAPIL_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_CONTROLE_RADIOL.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_AREA_DENS_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_AREA_DENS_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_DIF_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_DIF_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_FOC_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_FOC_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_DIST_ARQ_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_DIST_ARQ_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_MICROCAL_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_MICROCAL_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_NODULO_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_DIAG_NODULO_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_LESAO_DIAG.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_MAMO_RASTR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_AREA_DENS_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_AREA_DENS_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_DIF_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_DIF_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_FOC_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_FOC_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_DIST_ARQ_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_DIST_ARQ_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_MICROCAL_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_MICROCAL_ESQ.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_NODULO_DIR.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_RAD_NODULO_ESQ.name(), false);

		map.put(DominioSismamaMamoCadCodigo.C_TMP_CAT3_LINFO_AXILAR_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_CAT3_LINFO_AXILAR_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_DIAG_LINFO_AXILAR_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_DIAG_LINFO_AXILAR_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ADENSA_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ADENSA_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ASDIFU_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ASDIFU_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ASFOCA_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_DISFOCA_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_DISFOCA_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_LINAXI_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_LINAXI_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_MICRO_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_MICRO_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_NOD_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_NOD_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_CINCO_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_CINCO_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_QUATRO_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_QUATRO_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_TRES_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_TRES_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_ZERO_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_ZERO_E.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_MAM_RAST_ALVO.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_MAM_RAST_RISCO.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_MAM_RAST_TRAT.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_QTNEO_MAMA_D.name(), false);
		map.put(DominioSismamaMamoCadCodigo.C_TMP_QTNEO_MAMA_E.name(), false);

		map.put(DominioSismamaMamoCadCodigo.D_ANA_MESTRUAC.name(), null);

		return map;
	}

	protected AelSismamaMamoCadDAO getAelSismamaMamoCadDAO() {
		return aelSismamaMamoCadDAO;
	}

	protected AelSismamaMamoResDAO getAelSismamaMamoResDAO() {
		return aelSismamaMamoResDAO;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
