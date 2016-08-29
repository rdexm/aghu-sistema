package br.gov.mec.aghu.ambulatorio.business;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamEvolucoesDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamQuestaoDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamQuestionarioDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamValorValidoQuestaoDAO;
import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/** #51886 - PAC1 
 * @ORADB MAMK_REV_RN **/
@Stateless
public class MamkRevRN extends BaseBusiness {

	private static final long serialVersionUID = 1722304360759140897L;

	private static final Log LOG = LogFactory.getLog(MamkRevRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MamEvolucoesDAO mamEvolucoesDAO;
	
	@Inject
	private AghEspecialidadesDAO aghEspecialidadesDAO;
	
	@Inject
	private MamQuestionarioDAO mamQuestionarioDAO;
	
	@Inject
	private MamValorValidoQuestaoDAO mamValorValidoQuestaoDAO;
	
	@Inject
	private MamQuestaoDAO mamQuestaoDAO;
	
	@EJB
	private MamkFuncaoGravacaoRN mamkFuncaoGravacaoRN;
	
	public enum MamkRevRNExceptionCode implements BusinessExceptionCode {
		MAM_00323, MAM_00324, MAM_00325, MAM_00326, MAM_01850, MSG_MAMC_EXEC_FNC_GRAVA,MAM_02205,MSG_ERRO_51886_NUMERO_INVALIDO,MSG_ERRO_51886_DATA_INVALIDA
	}
	
	/**SÃ³ inclui/altera/exclui se ind_pendente <> 'V' em mam_evoluÃ§Ãµes 
	 * @ORADB MAMK_REV_RN.RN_REVP_CONSISTE**/
	public void rnRevpConsiste(Long pEvoSeq) throws ApplicationBusinessException{
		if(pEvoSeq != null){
			MamEvolucoes curEvo = new MamEvolucoes();
			curEvo = this.mamEvolucoesDAO.obterEvolucaoPorSeq(pEvoSeq);
			if (curEvo != null) {
				if (DominioIndPendenteAmbulatorio.V.equals(curEvo.getPendente())) {
					throw new ApplicationBusinessException(MamkRevRNExceptionCode.MAM_00323, Severity.INFO);
				}
			}
		}
	}
	
	/**@ORADB MAMK_REV_RN.RN_REVP_ESPEC_ATIVA**/
	public void rnRevpEspecAtiva(Short pEspSeq) throws ApplicationBusinessException{
		if(pEspSeq != null){
			DominioSituacao curEsp = this.aghEspecialidadesDAO.obterSituacaoEspecialidadePorSeq(pEspSeq);
			
			if (!DominioSituacao.A.equals(curEsp)) {
				throw new ApplicationBusinessException(MamkRevRNExceptionCode.MAM_00325, Severity.INFO);
			}
		}
	}
	
	/**@ORADB MAMK_REV_RN.RN_REVP_QUESTI_ATIVO**/
	public void rnRevpQuestiAtivo(Integer pQusQutSeq) throws ApplicationBusinessException{
		if(pQusQutSeq != null){
			String curQut = this.mamQuestionarioDAO.obterSituacaoQuestionarioPorSeq(pQusQutSeq);
			if (!DominioSituacao.A.toString().equals(curQut)) {
				throw new ApplicationBusinessException(MamkRevRNExceptionCode.MAM_00324, Severity.INFO);
			}
		}
	}
	
	/**@ORADB MAMK_REV_RN.RN_REVP_VVQ_ATIVO**/
	public void rnRevpVvqAtivo(Integer cVvqQusQutSeq, Short cVvqQusSeqP, Short cVvqSeqP) throws ApplicationBusinessException{
		if(cVvqQusQutSeq != null && cVvqQusSeqP != null && cVvqSeqP != null){
			String curVvt = this.mamValorValidoQuestaoDAO.obterSituacaoPorSeq(cVvqQusQutSeq, cVvqQusSeqP, cVvqSeqP);
			if (!DominioSituacao.A.toString().equals(curVvt)) {
				throw new ApplicationBusinessException(MamkRevRNExceptionCode.MAM_00326, Severity.INFO);
			}
		}
	}
	
	/**Verifica o conteÃºdo digitado na resposta 
	 * @ORADB MAMK_REV_RN.RN_REVP_VER_TIP_DADO**/
	public void rnRevpVerTipDado(Long pEvoSeq, Integer pQusQutSeq, Short pQusSeqP, Short pSeqP, String pResposta) throws ApplicationBusinessException{
		//Verifica o tipo de dado digitado na resposta de uma pergunta de evoluÃ§Ã£o.
		String vTipoDado = StringUtils.EMPTY;
		String vMascara = StringUtils.EMPTY;
		String curQus = mamQuestaoDAO.obterTipoDadoPorSeq(pQusQutSeq, pQusSeqP);
		vTipoDado = curQus;
		if (vTipoDado.equals("D")) {
			vMascara = "dd/MM/yyyy";
			if (vMascara.length() != pResposta.length()) {
				throw new ApplicationBusinessException(MamkRevRNExceptionCode.MAM_01850, Severity.INFO, vMascara);
			}
			formatarDataComVerificacao(pResposta, vMascara);
		}else if(vTipoDado.equals("N")){
			formatarNumeroComVerificacao(pResposta);
		}
	}
	
	/**Grava a resposta na respctiva tabela  
	 * @ORADB MAMK_REV_RN.RN_REVP_ATU_RESP**/
	public void rnRevpAtuResp(Long pEvoSeq, Integer pQusQutSeq, Short pQusSeqP, Short pSeqP, String pResposta, DominioOperacaoBanco pOperacao, Integer pPacCodigo) throws ApplicationBusinessException{
		Integer vFgrSeq = null;
		String vTexto = StringUtils.EMPTY;
		String vTipo = StringUtils.defaultString("E");
		Integer curQus = this.mamQuestaoDAO.obterFgrSeqPorSeq(pQusQutSeq, pQusSeqP);
		
		vFgrSeq = curQus;
		if (vFgrSeq > 0) {
			vTexto = mamkFuncaoGravacaoRN.mamcExecFncGrava(vFgrSeq, vTipo, pEvoSeq, pQusQutSeq, pQusSeqP, pSeqP, pResposta, pOperacao, pPacCodigo, null);
			if(vTexto !=  null){
				throw new ApplicationBusinessException(MamkRevRNExceptionCode.MSG_MAMC_EXEC_FNC_GRAVA, Severity.INFO, vTexto);
			}
		}
	}
	
	/**Verifica se a evoluÃ§Ã£o tem ou nÃ£o autorelacionamento   
	 * @ORADB MAMK_REV_RN.RN_REVC_VER_COPIA**/
	public String rnRevcVerCopia(Long pEvoSeq) throws ApplicationBusinessException{
		Long vEvoEvoSeq = null;
		MamEvolucoes curEvo = this.mamEvolucoesDAO.obterEvolucaoPorSeq(pEvoSeq);
		if (curEvo != null) {
			vEvoEvoSeq = curEvo.getEvolucao().getSeq();
		}		
		String vRetorno = vEvoEvoSeq != null ? DominioSimNao.S.toString() : DominioSimNao.N.toString();
		
		return vRetorno;
	}
	
	/**#51886 - Valida e retorna a data formatada**/
	public void formatarDataComVerificacao(String data, String mascara) throws ApplicationBusinessException{
		if (StringUtils.isNotBlank(data) && StringUtils.isNotBlank(mascara)) {
			DateFormat formatar = new SimpleDateFormat(mascara);
			try {
				formatar.parse(data);
			} catch (ParseException e) {
				throw new ApplicationBusinessException(MamkRevRNExceptionCode.MSG_ERRO_51886_DATA_INVALIDA, Severity.ERROR);
			}
		}
	}

	/**#51886 - Valida e retorna um numero formatado**/
	public void formatarNumeroComVerificacao(String numero) throws ApplicationBusinessException{
		if (StringUtils.isNotBlank(numero)) {
			NumberFormat formatar = NumberFormat.getCurrencyInstance();
			try {
				formatar.parse(numero).toString();
			} catch (ParseException e) {
				throw new ApplicationBusinessException(MamkRevRNExceptionCode.MSG_ERRO_51886_NUMERO_INVALIDO, Severity.ERROR);
			}
		}
	}	
}
