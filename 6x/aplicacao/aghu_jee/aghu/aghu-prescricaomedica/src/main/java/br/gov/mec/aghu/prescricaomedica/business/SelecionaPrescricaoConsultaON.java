package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Arrays;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Stateless
public class SelecionaPrescricaoConsultaON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(SelecionaPrescricaoConsultaON.class);

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private MpmPrescricaoMedicaDAO prescricaoMedicaDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	public List<AghAtendimentos> pesquisarAtendimentoPrescricaoMedica(Integer codigoPac){
		List<AghAtendimentos> atendimentos = getAmbulatorioFacade()
				.pesquisarAtendimentoParaPrescricaoMedica(
						null,codigoPac,
						Arrays.asList(
								DominioOrigemAtendimento.I,
								DominioOrigemAtendimento.U,
								DominioOrigemAtendimento.N
								),
						Arrays.asList(
								DominioOrigemAtendimento.A,
								DominioOrigemAtendimento.C,
								DominioOrigemAtendimento.X
								));
		
		for(int i = 0 ; i < atendimentos.size() ; i++){
			AghAtendimentos atendimento =  atendimentos.get(i);
			List<MpmPrescricaoMedica> prescricao = getMpmPrescricaoMedicaDAO().prescricoesAtendimentoDataFim(atendimento, null);
			if(prescricao == null || prescricao.isEmpty()){
				atendimentos.set(i, null);
			}
		}
		
		while(atendimentos.contains(null)){
			atendimentos.remove(null);
		}
		return atendimentos;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO() {
		return this.prescricaoMedicaDAO;
	}
	
	
}
