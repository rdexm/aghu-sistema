package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.vo.MamLaudoAihVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamLaudoAih;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ListaLaudoAihON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(ListaLaudoAihON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private ListaLaudoAihRN listaLaudoAihRN;

	@EJB
	private IAghuFacade iAghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -217044772790053268L;
	
	public List<MamLaudoAihVO> listarLaudosAIH(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc, AipPacientes paciente) {
		
		List<MamLaudoAihVO> listaLaudosAIH = new ArrayList<MamLaudoAihVO>();
		
		List<MamLaudoAih> listaMamLaudoAih = getAmbulatorioFacade().listarLaudosAIH(firstResult, maxResult, orderProperty, asc, paciente);
 		
			for(MamLaudoAih item : listaMamLaudoAih){
				MamLaudoAihVO laudoAih = new MamLaudoAihVO();
				laudoAih.setSeq(item.getSeq());
				laudoAih.setEspecialidade(item.getEspecialidade());
				laudoAih.setDthrCriacao(item.getDthrCriacao());
				laudoAih.setEspecialidade(item.getEspecialidade());
				laudoAih.setIndPendente(item.getIndPendente());
				laudoAih.setMamLaudoAihs(item.getMamLaudoAihs());
				laudoAih.setIndSituacao(item.getIndSituacao());
				laudoAih.setObsRevisaoMedica(item.getObsRevisaoMedica());
				
				if (item.getDtProvavelInternacao() != null) {
					laudoAih.setDtProvavelInternacao(item.getDtProvavelInternacao());
				} else {
					laudoAih.setDtProvavelInternacao(item.getDthrCriacao());
				}
				
				List<AghEquipes> equipes = getAghuFacade().pesquisarEquipeRespLaudoAih(item.getSeq(), item.getServidorRespInternacao());
				if(!equipes.isEmpty()){
					laudoAih.setEquipe(equipes.get(0));
				}
				
				listaLaudosAIH.add(laudoAih);
			}
		 
		 return listaLaudosAIH;
	}
	
	public Long listarLaudosAIHCount(AipPacientes paciente) {
		return getAmbulatorioFacade().listarLaudosAIHCount(paciente);
	}

	public MamLaudoAih imprimirLaudoAih(Long seq) throws ApplicationBusinessException{

		return getListaLaudoAihRN().gravarMamLaudoAih(seq);

	}

	private ListaLaudoAihRN getListaLaudoAihRN() {
		return listaLaudoAihRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return iAmbulatorioFacade;
	}



	
}
