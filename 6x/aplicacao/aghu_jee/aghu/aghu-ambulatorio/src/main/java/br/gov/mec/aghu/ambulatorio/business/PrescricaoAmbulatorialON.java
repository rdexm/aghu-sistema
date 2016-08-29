package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class PrescricaoAmbulatorialON extends BaseBusiness {

	private static final long serialVersionUID = 722288915659294396L;

	private static final Log LOG = LogFactory.getLog(PrescricaoAmbulatorialON.class);

	@Override
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IAghuFacade aghuFacade;

	public List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(Integer codigoPac, Integer atdSeq){
		List<AghAtendimentos> atendimentos = pesquisarAtendimentoParaPrescricaoMedica(codigoPac, atdSeq,
						DominioOrigemAtendimento.getOrigensPrescricaoInternacao(),
						DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial());
		
		return atendimentos;
	}

	public List<AghAtendimentos> pesquisarAtendimentoParaPrescricaoMedica(
			Integer codigoPac, Integer atdSeq, 
			List<DominioOrigemAtendimento> origensInternacao,
			List<DominioOrigemAtendimento> origensAmbulatorio) {

		List<AghAtendimentos> atendimentos = new ArrayList<AghAtendimentos>();
		
		if(origensInternacao != null && !origensInternacao.isEmpty()){
			//Para gap #34801, não considerar origem 'Hospital dia' nesta situação
			List<DominioOrigemAtendimento> origensInternacaoTemp = new ArrayList<DominioOrigemAtendimento>(origensInternacao);//.remove(DominioOrigemAtendimento.H);
			origensInternacaoTemp.remove(DominioOrigemAtendimento.H);
			
			atendimentos.addAll(
				aghuFacade.pesquisarAtendimentos(codigoPac, atdSeq, DominioPacAtendimento.S, origensInternacaoTemp)	
				);
		}
		
		if(origensAmbulatorio != null && !origensAmbulatorio.isEmpty()){
			if(origensAmbulatorio.contains(DominioOrigemAtendimento.A)){
				atendimentos.addAll(
					aghuFacade.pesquisarAtendimentosAmbulatoriaisPrescricaoMedica(codigoPac, atdSeq, 24)	
				);
			}
			if(origensAmbulatorio.contains(DominioOrigemAtendimento.C)){
				atendimentos.addAll(
					aghuFacade.pesquisarAtendimentosCirurgicosPrescricaoMedica(codigoPac, atdSeq, 24)
				);
			}
			if(origensAmbulatorio.contains(DominioOrigemAtendimento.X)){
				atendimentos.addAll(
					aghuFacade.pesquisarAtendimentosExternoPrescricaoMedica(codigoPac, atdSeq, 24)	
				);
			}
		}
	
		return atendimentos;
		
	}
}
