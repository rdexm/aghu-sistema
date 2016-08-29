package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AtendimentosEmergenciaPOLRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AtendimentosEmergenciaPOLRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAmbulatorioFacade ambulatorioFacade;

@EJB
private IAghuFacade aghuFacade;


				
	private static final long serialVersionUID = -2699480934203619857L;


	public Integer obterAtendimentoPorTriagem(Long trgSeq) {
		AghAtendimentos atendimento = getAghuFacade().obterAtendimentoPorTriagem(trgSeq);
		if (atendimento != null && atendimento.getSeq() != null){
			return atendimento.getSeq();
		}else{
			return null;
		}		
	}		
	/**
	 * @ORADB MAMP_ATU_TMP_TRIAGEM
	 * @param trgSeq
	 * @param rgtSeq
	 * @return
	 */
	public Boolean verificarImpressao(Long trgSeq, Long rgtSeq) {
		List<MamEvolucoes> evolucao = getAmbulatorioFacade().obterEvolucaoPorTriagemERegistro(trgSeq,rgtSeq);
		List<MamAnamneses> anamnese = getAmbulatorioFacade().obterAnamnesePorTriagemERegistro(trgSeq,rgtSeq);
		
		if (  (evolucao != null && !evolucao.isEmpty())  ||  (anamnese != null && !anamnese.isEmpty())  ){
			return true;
		}else{
			return false;
		}		
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade(){
		return this.ambulatorioFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}
