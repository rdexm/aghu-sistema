package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.dominio.DominioCoresPacientesTriagem;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.VAfaPrcrDispMdtos;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

@Stateless
public class PesquisarPacientesParaDispensacaoRN extends BaseBusiness
		implements Serializable {

private static final Log LOG = LogFactory.getLog(PesquisarPacientesParaDispensacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6718076901052889731L;

	/**
	 * @ORADB AFAC_ORDENA_DISP
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public DominioCoresPacientesTriagem afacOrdenaDisp(VAfaPrcrDispMdtos paciente){
		AghAtendimentos atendimento = getAghuFacade().obterAtendimento(paciente.getId().getAtdSeq(), DominioPacAtendimento.N, Arrays.asList(DominioOrigemAtendimento.I)); 
		if(atendimento!= null && atendimento.getProntuario() > 0) {
			return DominioCoresPacientesTriagem.VERMELHO;
		} else{
			if(paciente.getApaAtdSeq() != null && paciente.getApaSeq() != null && paciente.getSumSeqp() != null) {
				return DominioCoresPacientesTriagem.VERMELHO;
			}
		}
		
		Integer vSolic	= paciente.getCountSolic() != null ? paciente.getCountSolic() : 0;
		Integer vDisp = paciente.getCountDisp() != null ? paciente.getCountDisp() : 0;
		Integer vConf = paciente.getCountConf() != null ? paciente.getCountConf() : 0;
		Integer vEnv = paciente.getCountEnv() != null ? paciente.getCountEnv() : 0;
		Integer vTriado = paciente.getCountTriado() != null ? paciente.getCountTriado() : 0;
		Integer vOcorr = paciente.getCountOcorr() != null ? paciente.getCountOcorr() : 0;

		if (vSolic == 0 && vTriado == 0 && vOcorr == 0) {
			return DominioCoresPacientesTriagem.AZUL;
		}
		
		if(vSolic >0) {
			if(vDisp >0 || vConf > 0 || vEnv >0 || vOcorr > 0 ) {
				return DominioCoresPacientesTriagem.AMARELO;
			}
		}
		
		if(vTriado >0 && vDisp==0) {
			return DominioCoresPacientesTriagem.ROXO;
		}
		
		if(vTriado==0 && vOcorr > 0 && vSolic == 0) {
			return DominioCoresPacientesTriagem.MARROM;
		}
		
		if(vTriado > 0 && vDisp >0) {
			return DominioCoresPacientesTriagem.BRANCO;
		}
		
		return DominioCoresPacientesTriagem.CINZA;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	
	protected AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO(){
		return  afaDispensacaoMdtosDAO;
	}
	
	protected Boolean notNullEMaiorQueZero(BigDecimal valor){
		if(valor != null && (valor.compareTo(BigDecimal.ZERO) > 0)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * @ORADB afac_retorna_trp_seq
	 * @param atendimento
	 * @return
	 */
	public Integer afacRetornaTrpSeq(AghAtendimentos atendimento){
		return atendimento != null ? atendimento.getTrpSeq(): null;
	}
	
}