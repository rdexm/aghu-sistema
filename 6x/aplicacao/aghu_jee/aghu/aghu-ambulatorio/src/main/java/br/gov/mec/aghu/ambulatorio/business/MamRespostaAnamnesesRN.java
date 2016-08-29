package br.gov.mec.aghu.ambulatorio.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamRespostaAnamnesesDAO;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamRespostaAnamneses;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.AGHUUtil;

@Stateless
public class MamRespostaAnamnesesRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2032949117818129472L;
	private static final Log LOG = LogFactory.getLog(MamRespostaAnamnesesRN.class);
	
	@Inject
	private MamRespostaAnamnesesDAO mamRespostaAnamnesesDAO;
	
	@EJB
	private MamkReaRN mamkReaRN;
	
	@EJB
	private MarcacaoConsultaRN marcacaoConsultaRN;
	
	private static final String EVENT_INSERT = "INSERT";
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public void removerRespostaAnamnese(MamRespostaAnamneses respostaAnamneses) throws NumberFormatException, ApplicationBusinessException{
		this.mamRespostaAnamnesesDAO.remover(respostaAnamneses);
		posDelete(respostaAnamneses);
	}

	/**TRI1 estória #52053
	 * @ORADB TRIGGER MAMT_REA_ARD
	 */
	private void posDelete(MamRespostaAnamneses respostaAnamneses) throws NumberFormatException, ApplicationBusinessException {
		if(respostaAnamneses.getResposta() != null){
			this.mamkReaRN.rnReapAtuResp(respostaAnamneses.getId().getAnaSeq(), respostaAnamneses.getId().getQusQutSeq(), respostaAnamneses.getId().getQusSeqp(),
					respostaAnamneses.getId().getSeqp(), respostaAnamneses.getResposta(), DominioOperacaoBanco.DEL, 
					this.marcacaoConsultaRN.obterCodigoPacienteOrigem(2, Integer.valueOf(respostaAnamneses.getId().getAnaSeq().toString())));
		} 
	}
	
	public void persistirRespostaAnamnese(MamRespostaAnamneses respostaAnamneses) throws ApplicationBusinessException{
		preInserir(respostaAnamneses);
		this.mamRespostaAnamnesesDAO.persistir(respostaAnamneses);
		posInserir(respostaAnamneses);
	}

	/**
	 * TRI2 estória #52053
	 * @ORADB TRIGGER MAMT_REA_BRI
	 */
	private void preInserir(MamRespostaAnamneses respostaAnamneses) throws ApplicationBusinessException {
		this.mamkReaRN.rnReapVerValidado("I", respostaAnamneses.getId().getAnaSeq());
		this.mamkReaRN.rnReapVerQutAtiv(respostaAnamneses.getId().getQusQutSeq());		
		this.mamkReaRN.rnReapVerEspAtiv(respostaAnamneses.getEspSeq());
		this.mamkReaRN.rnReapVerVvqAtiv(respostaAnamneses.getVvqQusQutSeq(), respostaAnamneses.getVvqQusSeqp(), respostaAnamneses.getVvqSeqp());
	}
	
	/**
	 * TRI3 estória #52053
	 * @ORADB MAMT_REA_ASI
	 */
	private void posInserir(MamRespostaAnamneses respostaAnamneses) throws ApplicationBusinessException {
		String event = StringUtils.defaultString("INSERT");
		mampEnforceReaRules(respostaAnamneses, event);
	}
	
	public void atualizarRespostaAnamnese(MamRespostaAnamneses respostaAnamneses) throws ApplicationBusinessException{
		preAtualizar(respostaAnamneses, this.mamRespostaAnamnesesDAO.obterOriginal(respostaAnamneses));
		this.mamRespostaAnamnesesDAO.atualizar(respostaAnamneses);
		posAtualizar(respostaAnamneses, this.mamRespostaAnamnesesDAO.obterOriginal(respostaAnamneses));
	}
	
	/**
	 * TRI4 estória #52053
	 * @ORADB MAMT_REA_BRU
	 */
	private void preAtualizar(MamRespostaAnamneses respostaAnamneses,
			MamRespostaAnamneses respostaAnamnesesOld) throws ApplicationBusinessException {
		if(respostaAnamneses.getResposta() != null && AGHUUtil.modificados(respostaAnamneses.getResposta(), respostaAnamnesesOld.getResposta())){
			this.mamkReaRN.rnReapVerTipDado(respostaAnamneses.getId().getAnaSeq(), respostaAnamneses.getId().getQusQutSeq(), 
					respostaAnamneses.getId().getQusSeqp(), respostaAnamneses.getId().getSeqp(), respostaAnamneses.getResposta());
		}
		
		if(!AGHUUtil.modificados(respostaAnamneses.getAipPesoPaciente(), respostaAnamnesesOld.getAipPesoPaciente()) &&
				!AGHUUtil.modificados(respostaAnamneses.getAipAlturaPaciente(), respostaAnamnesesOld.getAipAlturaPaciente()) &&
				!AGHUUtil.modificados(respostaAnamneses.getPlpPacCodigo(), respostaAnamnesesOld.getPlpPacCodigo()) &&
				!AGHUUtil.modificados(respostaAnamneses.getPlpSeqp(), respostaAnamnesesOld.getPlpSeqp()) &&
				!AGHUUtil.modificados(respostaAnamneses.getPipPacCodigo(), respostaAnamnesesOld.getPipPacCodigo()) &&
				!AGHUUtil.modificados(respostaAnamneses.getPipSeqp(), respostaAnamnesesOld.getPipSeqp()) &&
						!AGHUUtil.modificados(respostaAnamneses.getPdpPacCodigo(), respostaAnamnesesOld.getPdpPacCodigo()) &&
						!AGHUUtil.modificados(respostaAnamneses.getPdpSeqp(), respostaAnamnesesOld.getPdpSeqp())){
			this.mamkReaRN.rnReapVerValidado("U", respostaAnamneses.getId().getAnaSeq());
		} 
	}
	
	/**
	 * TRI5 estória #52053
	 * @ORADB MAMT_REA_ARU
	 */
	private void posAtualizar(MamRespostaAnamneses respostaAnamneses, MamRespostaAnamneses respostaAnamnesesOld) 
			throws NumberFormatException, ApplicationBusinessException {
		if(AGHUUtil.modificados(respostaAnamneses.getResposta(), respostaAnamnesesOld.getResposta())){
			this.mamkReaRN.rnReapAtuResp(respostaAnamneses.getId().getAnaSeq(), respostaAnamneses.getId().getQusQutSeq(), 
					respostaAnamneses.getId().getQusSeqp(), respostaAnamneses.getId().getSeqp(), respostaAnamneses.getResposta(), 
					DominioOperacaoBanco.UPD, this.marcacaoConsultaRN.obterCodigoPacienteOrigem(2, 
							Integer.valueOf(respostaAnamneses.getId().getAnaSeq().toString())));
		}
	}

	/**
	 * PRO1
	 * @ORADB MAMP_ENFORCE_REA_RULES
	 */
	public void mampEnforceReaRules(MamRespostaAnamneses lReaRowNew, String event) throws ApplicationBusinessException{
		if(EVENT_INSERT.equals(event)){
			/*se o registro de anamnese foi uma cópia (tem autorelacionamento) não devo consistir se o dado digitado é consistente*/
			String vCopia = this.mamkReaRN.rnReacVerCopia(lReaRowNew.getId().getAnaSeq());
			if(DominioSimNao.N.toString().equals(vCopia)){
				//verifica se a resposta dado é consistente (se tipo_dado é data então resposta deve ser uma data,
			   //se tipo_dado é número então resposta deve ser numérica, etc);
				this.mamkReaRN.rnReapVerTipDado(lReaRowNew.getId().getAnaSeq(), lReaRowNew.getId().getQusQutSeq(), lReaRowNew.getId().getQusSeqp(), 
						lReaRowNew.getId().getSeqp(), lReaRowNew.getResposta());
				//verifica se há a função de gravação e se houver procede à gravação
				if(lReaRowNew.getResposta() != null){
					this.mamkReaRN.rnReapAtuResp(lReaRowNew.getId().getAnaSeq(), lReaRowNew.getId().getQusQutSeq(), lReaRowNew.getId().getQusSeqp(), 
							lReaRowNew.getId().getSeqp(), lReaRowNew.getResposta(), DominioOperacaoBanco.INS, 
							this.marcacaoConsultaRN.obterCodigoPacienteOrigem(2, lReaRowNew.getId().getAnaSeq().intValue()));
				}
			}
		}
	}
}
