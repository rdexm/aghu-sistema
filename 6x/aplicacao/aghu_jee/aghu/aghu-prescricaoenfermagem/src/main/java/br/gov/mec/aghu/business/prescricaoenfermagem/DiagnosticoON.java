package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricaoEnfermagemDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.EpePrescricaoEnfermagemVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class DiagnosticoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(DiagnosticoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeDiagnosticoDAO epeDiagnosticoDAO;
	
@Inject
private EpePrescricaoEnfermagemDAO epePrescricaoEnfermagemDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4719203394124034943L;

	/**
	 * 
	 */
	
	public List<DiagnosticoVO> pesquisarDiagnosticos(Short dgnSnbGnbSeq, Short dgnSnbSequencia, String dgnDescricao, Short dgnSequencia) {
		List<EpeDiagnostico> listaDiagnosticos = this.getEpeDiagnosticoDAO().pesquisarDiagnosticos(dgnSnbGnbSeq, dgnSnbSequencia, dgnDescricao, dgnSequencia);
		List<DiagnosticoVO> listaDiagnosticosVO = new ArrayList<DiagnosticoVO>();
		for(int i=0; i<listaDiagnosticos.size(); i++){
			DiagnosticoVO diagnosticoVO = new DiagnosticoVO();
			diagnosticoVO.setSequencia(listaDiagnosticos.get(i).getId().getSequencia());
			diagnosticoVO.setSnbGnbSeq(listaDiagnosticos.get(i).getId().getSnbGnbSeq());
			diagnosticoVO.setSnbSequencia(listaDiagnosticos.get(i).getId().getSnbSequencia());
			diagnosticoVO.setDescricao(listaDiagnosticos.get(i).getDescricao());
			if(i==0){
				diagnosticoVO.setSelecionado(true);
			} else {
				diagnosticoVO.setSelecionado(false);
			}
			listaDiagnosticosVO.add(diagnosticoVO);
		}
		return listaDiagnosticosVO;
	}
	
	public List<EpePrescricaoEnfermagemVO> pesquisarPrescricaoEnfermagemPorAtendimento(Integer atdSeq) {
		
		List<EpePrescricaoEnfermagem> prescricoesEnf =   epePrescricaoEnfermagemDAO.pesquisarPrescricaoEnfermagemPorAtendimento(atdSeq);
		// transforma para o tipo de retorno
		List<EpePrescricaoEnfermagemVO> listaRetorno = new ArrayList<EpePrescricaoEnfermagemVO>();
		for (EpePrescricaoEnfermagem prescEnf : prescricoesEnf) {
			EpePrescricaoEnfermagemVO vo = new EpePrescricaoEnfermagemVO();
			vo.setCriadoEm(prescEnf.getCriadoEm());
			listaRetorno.add(vo);
		}
		
		return listaRetorno;	
	 }

	protected EpeDiagnosticoDAO getEpeDiagnosticoDAO() {
		return epeDiagnosticoDAO;
	}
}
