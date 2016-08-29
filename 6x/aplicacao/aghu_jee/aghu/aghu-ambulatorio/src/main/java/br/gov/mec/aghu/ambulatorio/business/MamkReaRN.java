package br.gov.mec.aghu.ambulatorio.business;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamAnamnesesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamQuestionarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamValorValidoQuestaoDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
/**#52053 - PAC1
 * @ORADB MAMK_REA_RN
 */
@Stateless
public class MamkReaRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5711194442526685040L;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	private MamValorValidoQuestaoDAO mamValorValidoQuestaoDAO;
	
	@Inject
	private MamQuestionarioDAO mamQuestionarioDAO;
	
	@Inject
	private MamAnamnesesDAO mamAnamnesesDAO;
	
	@Inject
	private MamQuestaoDAO mamQuestaoDAO;
	
//	@EJB
//	private MamkFuncaoGravacaoRN mamkFuncaoGravacaoRN;
	
	private static final String I = "I";
	private static final String U = "U";
	private static final String D = "D";
	private static final String N = "N";

	private static final Log LOG = LogFactory.getLog(MamkReaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public enum MamkReaRNExceptionCode implements BusinessExceptionCode {
		MAM_00684, MAM_00685, MAM_00683, MAM_00680, MAM_00681, MAM_00682, MAM_01824, MSG_ERRO_DATA_INVALIDA, MSG_ERRO_NUMERO_INVALIDO, MSG_MAMC_EXEC_FNC_GRAVA
	}
	/**
	 * Verifica se a especialidade que está sendo incluída está ativa.
	 * @ORADB MAMK_REA_RN.RN_REAP_VER_ESP_ATIV
	 */
	public void rnReapVerEspAtiv(Short pEspSeq) throws ApplicationBusinessException{
		DominioSituacao vAtivo =  this.aghEspecialidadesDAO.obterSituacaoEspecialidade(pEspSeq);
		if(DominioSituacao.I.equals(vAtivo)){
			throw new ApplicationBusinessException(MamkReaRNExceptionCode.MAM_00684, Severity.INFO);
		}
	}
	
	/**
	 * Valor válido questão deve estar ativo no cadastro.
	 * @ORADB MAMK_REA_RN.RN_REAP_VER_VVQ_ATIV
	 */
	public void rnReapVerVvqAtiv(Integer pVvqQusQutSeq, Short pVvqQusSeqP, Short pVvqSeqP) throws ApplicationBusinessException{
		String curVvt = this.mamValorValidoQuestaoDAO.obterSituacaoValorValidoQuestaoPorSeq(pVvqQusQutSeq, pVvqQusSeqP, pVvqSeqP);
		
		if(DominioSituacao.I.toString().equals(curVvt)){
			throw new ApplicationBusinessException(MamkReaRNExceptionCode.MAM_00685, Severity.INFO);
		}
	}
	
	/**
	 * Verifica se questionario que esta sendo incluido está ativo.
	 * @ORADB MAMK_REA_RN.RN_REAP_VER_QUT_ATIV
	 */
	public void rnReapVerQutAtiv(Integer pQutSeq) throws ApplicationBusinessException{
		String vAtivo = this.mamQuestionarioDAO.obterIndSituacaoQuestionarioPorSeq(pQutSeq);
		
		if(DominioSituacao.I.toString().equals(vAtivo)){
			throw new ApplicationBusinessException(MamkReaRNExceptionCode.MAM_00683, Severity.INFO);
		}
	}
	
	/**
	 * Verifica se a anamnese já foi validada.
	 * @ORADB MAMK_REA_RN.RN_REAP_VER_VALIDADO
	 */
	public void rnReapVerValidado(String pOper, Long pAnaSeq) throws ApplicationBusinessException{
		String vInd = this.mamAnamnesesDAO.obterSituacaoAnamnesePorSeq(pAnaSeq);
		if(DominioIndPendenteAmbulatorio.V.toString().equals(vInd)){
			if(I.equals(pOper)){
				throw new ApplicationBusinessException(MamkReaRNExceptionCode.MAM_00680, Severity.INFO);
			}else if(U.equals(pOper)){
				throw new ApplicationBusinessException(MamkReaRNExceptionCode.MAM_00681, Severity.INFO);
			}else if(D.equals(pOper)){
				throw new ApplicationBusinessException(MamkReaRNExceptionCode.MAM_00682, Severity.INFO);
			}
		}
	}
	
	/**
	 * Verifica o conteúdo digitado na respota.
	 * @ORADB MAMK_REA_RN.RN_REAP_VER_TIP_DADO
	 */
	public void rnReapVerTipDado(Long pAnaSeq, Integer pQusQutSeq, Short pQusSeqP, Short pSeqP, String pResposta) throws ApplicationBusinessException{
		String vTipoDado = mamQuestaoDAO.obterTipoDadoQuestaoPorSeq(pQusQutSeq, pQusSeqP);
		if(D.equals(vTipoDado)){
			String vMascara = "dd/MM/yyyy";
			if(vMascara.length() != pResposta.length()){
				throw new ApplicationBusinessException(MamkReaRNExceptionCode.MAM_01824, Severity.INFO);
			}
			formatarDataComVerificacao(pResposta, vMascara);
		}else if(N.equals(vTipoDado)){
			formatarNumeroComVerificacao(pResposta);
		}
	}
	
	/**
	 *#52503
	 * Método para formatar data.
	 */
	public void formatarDataComVerificacao(String data, String mascara) throws ApplicationBusinessException{
		if (StringUtils.isNotBlank(data) && StringUtils.isNotBlank(mascara)) {
			DateFormat formatar = new SimpleDateFormat(mascara);
			try {
				formatar.parse(data);
			} catch (ParseException e) {
				throw new ApplicationBusinessException(MamkReaRNExceptionCode.MSG_ERRO_DATA_INVALIDA, Severity.ERROR);
			}
		}
	}
	/**
	 * #52053
	 * Método para formatar string para número.
	 */
	public void formatarNumeroComVerificacao(String numero) throws ApplicationBusinessException{
		if (StringUtils.isNotBlank(numero)) {
			NumberFormat formatar = NumberFormat.getCurrencyInstance();
			try {
				formatar.parse(numero).toString();
			} catch (ParseException e) {
				throw new ApplicationBusinessException(MamkReaRNExceptionCode.MSG_ERRO_NUMERO_INVALIDO, Severity.ERROR);
			}
		}
	}
	
	/**
	 * Grava a resposta na respctiva tabela.
	 * @ORADB MAMK_REA_RN.RN_REAP_ATU_RESP
	 */
	public void rnReapAtuResp(Long pAnaSeq, Integer pQusQutSeq, Short pQusSeqP, Short pSeqP, String pResposta, DominioOperacaoBanco pOperacao, 
			Integer pPacCodigo) throws ApplicationBusinessException{
		String vTexto = StringUtils.EMPTY;
//		String vTipo = StringUtils.defaultString("A");
		Integer vFgrSeq = this.mamQuestaoDAO.obterFgrSeqQuestaoPorSeq(pQusQutSeq, pQusSeqP);
		
		if(vFgrSeq > 0){
			vTexto = StringUtils.EMPTY/*this.mamkFuncaoGravacaoRN.mamcExecFncGrava(vFgrSeq, vTipo, pAnaSeq, pQusQutSeq, pQusSeqP, pSeqP, pResposta, pOperacao, pPacCodigo, null)*/;
			if(vTexto != null){
				throw new ApplicationBusinessException(MamkReaRNExceptionCode.MSG_MAMC_EXEC_FNC_GRAVA, Severity.INFO, vTexto);
			}
		}
	}
	
	/**
	 * Verifica se a anamnese tem ou não autorelacionamento.
	 * @ORADB MAMK_REA_RN.RN_REAC_VER_COPIA
	 */
	public String rnReacVerCopia(Long pAnaSeq){
		String vRetorno;
		Long vAnaAnaSeq = this.mamAnamnesesDAO.obterAnaSeqAnamnesePorSeq(pAnaSeq);
		if(vAnaAnaSeq != null){
			vRetorno = DominioSimNao.S.toString();
		}else{
			vRetorno = DominioSimNao.N.toString();
		}
		
		return vRetorno;
	}

}
