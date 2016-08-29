package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.MamkRevRN.MamkRevRNExceptionCode;
import br.gov.mec.aghu.ambulatorio.dao.MamRespostaEvolucoesDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamRespostaEvolucoes;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MamRespostaEvolucoesRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = 433514862023395056L;
	
	private static final Log LOG = LogFactory.getLog(MamRespostaEvolucoesRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final String EVENT_INSERT = "INSERT";
	
	@EJB
	private MamkRevRN mamkRevRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private MamRespostaEvolucoesDAO mamRespostaEvolucoesDAO;
	
	public void atualizar(MamRespostaEvolucoes repostaEvolucoes) throws ApplicationBusinessException{
		preUpdate(repostaEvolucoes, mamRespostaEvolucoesDAO.obterOriginal(repostaEvolucoes));
		this.mamRespostaEvolucoesDAO.atualizar(repostaEvolucoes);
		posUpdate(repostaEvolucoes);
	}

	/**@throws ApplicationBusinessException 
	 * @throws NumberFormatException 
	 * @ORADB TRIGGER MAMT_REV_ARD */
	private void posDelete(MamRespostaEvolucoes repostaEvolucoes) throws NumberFormatException, ApplicationBusinessException{
		
		if(repostaEvolucoes.getResposta() != null){
			this.mamkRevRN.rnRevpAtuResp(repostaEvolucoes.getEvolucao().getSeq(), repostaEvolucoes.getId().getQusQutSeq(), repostaEvolucoes.getId().getQusSeqp(),
					repostaEvolucoes.getId().getSeqp(), repostaEvolucoes.getResposta(), DominioOperacaoBanco.DEL, 
					this.marcacaoConsultaRN.obterCodigoPacienteOrigem(3, Integer.valueOf(repostaEvolucoes.getEvolucao().getSeq().toString())));
		}
	}
	
	public void remover(MamRespostaEvolucoes repostaEvolucao) throws NumberFormatException, ApplicationBusinessException {
		preDelete(repostaEvolucao);
		mamRespostaEvolucoesDAO.remover(repostaEvolucao);
		posDelete(repostaEvolucao);
	}
	
	/**@throws ApplicationBusinessException 
	 * @ORADB TRIGGER MAMT_REV_ASD */
	private void preDelete(MamRespostaEvolucoes respostaEvolucao) throws ApplicationBusinessException{
		String event = StringUtils.defaultString("DELETE");
		mampEnforceRevRules(event, respostaEvolucao);
	}
	
	/**@ORADB TRIGGER MAMT_REV_ASI */
	private void posInsert(MamRespostaEvolucoes respostaEvolucao) throws ApplicationBusinessException{
		String event = StringUtils.defaultString("INSERT");
		mampEnforceRevRules(event, respostaEvolucao);
	}
	
	/**@ORADB TRIGGER MAMT_REV_BRI */
	private void preInsert(MamRespostaEvolucoes respostaEvolucao) throws ApplicationBusinessException{
		this.mamkRevRN.rnRevpConsiste(respostaEvolucao.getId().getEvoSeq());
		this.mamkRevRN.rnRevpQuestiAtivo(respostaEvolucao.getId().getQusQutSeq());
		this.mamkRevRN.rnRevpEspecAtiva(respostaEvolucao.getEspSeq());
		this.mamkRevRN.rnRevpVvqAtivo(respostaEvolucao.getVvqQusQutSeq(), respostaEvolucao.getVvqQusSeqp(), respostaEvolucao.getVvqSeqp());
	}
	
	public void persistir(MamRespostaEvolucoes respostaEvolucao) throws ApplicationBusinessException{
		preInsert(respostaEvolucao);
		mamRespostaEvolucoesDAO.persistir(respostaEvolucao);
		posInsert(respostaEvolucao);
	}
	
	/**@throws ApplicationBusinessException 
	 * @ORADB TRIGGER MAMT_REV_ASU */
	private void posUpdate(MamRespostaEvolucoes respostaEvolucao) throws ApplicationBusinessException{
		String event = StringUtils.defaultString("UPDATE");
		mampEnforceRevRules(event, respostaEvolucao);
	}
	
	/**@throws ApplicationBusinessException 
	 * @ORADB TRIGGER MAMT_REV_BRU */
	private void preUpdate(MamRespostaEvolucoes newRespEvo, MamRespostaEvolucoes OldRespEvo) throws ApplicationBusinessException{
		if(newRespEvo.getResposta() != null && AGHUUtil.modificados(newRespEvo.getResposta(), OldRespEvo.getResposta())){
			this.mamkRevRN.rnRevpVerTipDado(newRespEvo.getEvolucao().getSeq(), newRespEvo.getId().getQusQutSeq(), newRespEvo.getId().getQusSeqp(),
					newRespEvo.getId().getSeqp(), newRespEvo.getResposta());
		}
		
		if(!AGHUUtil.modificados(newRespEvo.getResposta(), OldRespEvo.getResposta()) &&
		  !AGHUUtil.modificados(newRespEvo.getPlpPacCodigo(), OldRespEvo.getPlpPacCodigo()) &&
		  !AGHUUtil.modificados(newRespEvo.getPlpSeqp(), OldRespEvo.getPlpSeqp()) &&
		  !AGHUUtil.modificados(newRespEvo.getPipPacCodigo(), OldRespEvo.getPipPacCodigo()) &&
		  !AGHUUtil.modificados(newRespEvo.getPipSeqp(), OldRespEvo.getPipSeqp()) &&
		  !AGHUUtil.modificados(newRespEvo.getPdpPacCodigo(), OldRespEvo.getPdpPacCodigo()) &&
		  !AGHUUtil.modificados(newRespEvo.getPdpSeqp(), OldRespEvo.getPdpSeqp())){
			
			this.mamkRevRN.rnRevpConsiste(newRespEvo.getEvolucao().getSeq());
		}
		
		if(AGHUUtil.modificados(newRespEvo.getId().getQusQutSeq(), OldRespEvo.getId().getQusQutSeq())){
			this.mamkRevRN.rnRevpQuestiAtivo(newRespEvo.getId().getQusQutSeq());
		}
		
		if(AGHUUtil.modificados(newRespEvo.getEspSeq(), OldRespEvo.getEspSeq())){
			this.mamkRevRN.rnRevpEspecAtiva(newRespEvo.getEspSeq());
		}
		
		if(AGHUUtil.modificados(newRespEvo.getId().getQusQutSeq(), OldRespEvo.getId().getQusQutSeq()) ||
				AGHUUtil.modificados(newRespEvo.getId().getQusSeqp(), OldRespEvo.getId().getQusSeqp()) ||
					AGHUUtil.modificados(newRespEvo.getId().getSeqp(), OldRespEvo.getId().getSeqp())){
			
			this.mamkRevRN.rnRevpVvqAtivo(newRespEvo.getId().getQusQutSeq(), newRespEvo.getId().getQusSeqp(), newRespEvo.getId().getSeqp());
		}
	}
	
	/** @throws ApplicationBusinessException 
	 * @throws NumberFormatException 
	 * @ORADB TRIGGER MAMT_REV_ASU */
	public void posUpdate(MamRespostaEvolucoes newRespEvo, MamRespostaEvolucoes OldRespEvo) throws NumberFormatException, ApplicationBusinessException{
		if(AGHUUtil.modificados(newRespEvo.getResposta(), OldRespEvo.getResposta())){
			this.mamkRevRN.rnRevpAtuResp(newRespEvo.getEvolucao().getSeq(), newRespEvo.getId().getQusQutSeq(), newRespEvo.getId().getQusSeqp(),	newRespEvo.getId().getSeqp(),
					newRespEvo.getResposta(), DominioOperacaoBanco.UPD, this.marcacaoConsultaRN.obterCodigoPacienteOrigem(3, Integer.valueOf(newRespEvo.getEvolucao().getSeq().toString())));
		}
	}
	
	/**PRO 1 
	 * @ORADB MAMP_ENFORCE_REV_RULES**/
	public void mampEnforceRevRules(String pEvent, MamRespostaEvolucoes lRevRowNew) throws ApplicationBusinessException{
		String vCopia = null;
		if (EVENT_INSERT.equals(pEvent)) {
			//Se o registro de evolução foi uma cópia (tem autorelacionamento) não devo consistir se o dado digitado é consistente
			vCopia = this.mamkRevRN.rnRevcVerCopia(lRevRowNew.getId().getEvoSeq());
			if (DominioSimNao.N.toString().equals(vCopia)) {
				//Verifica se a resposta dado é consistente (Se tipo_dado é data então resposta deve ser uma data, 
				//se tipo_dado é número então resposta deve ser numérica, etc);
				
				this.mamkRevRN.rnRevpVerTipDado(lRevRowNew.getId().getEvoSeq(), lRevRowNew.getId().getQusQutSeq(), lRevRowNew.getId().getQusSeqp(),
						lRevRowNew.getId().getSeqp(), lRevRowNew.getResposta());
				if (lRevRowNew.getResposta() != null) {
					this.mamkRevRN.rnRevpAtuResp(lRevRowNew.getId().getEvoSeq(), lRevRowNew.getId().getQusQutSeq(), lRevRowNew.getId().getQusSeqp(),
							lRevRowNew.getId().getSeqp(), lRevRowNew.getResposta(), DominioOperacaoBanco.INS, marcacaoConsultaRN.obterCodigoPacienteOrigem(3, lRevRowNew.getId().getEvoSeq().intValue()));
				}
			}
		}
	}
	
	/**PRO 2 
	 * @ORADB RN_TMPP_DT_REGISTRO**/
	public void rnTmppDtRegistro(Integer pPacCodigo, Date pDtRegistro) throws ApplicationBusinessException{
		 Date vDtNascimento = null;
		 Date cpac = this.aipPacientesDAO.obterDataNascimentoPaciente(pPacCodigo);
		 vDtNascimento = cpac;
		 if(DateUtil.validaDataMaior(vDtNascimento, pDtRegistro)){
			 throw new ApplicationBusinessException(MamkRevRNExceptionCode.MAM_02205, Severity.INFO);
		 }
	}
}