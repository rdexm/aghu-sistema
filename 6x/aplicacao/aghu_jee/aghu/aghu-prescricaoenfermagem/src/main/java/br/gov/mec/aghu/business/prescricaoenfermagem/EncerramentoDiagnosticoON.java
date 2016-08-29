package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.EpePrescCuidDiagnostico;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.model.EpePrescricoesCuidadosId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescCuidDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author diego.pacheco
 *
 */
@Stateless
public class EncerramentoDiagnosticoON extends BaseBusiness {


@EJB
private ManutencaoPrescricaoCuidadoON manutencaoPrescricaoCuidadoON;

private static final Log LOG = LogFactory.getLog(EncerramentoDiagnosticoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO;

@Inject
private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3373519232355888633L;
	
	public enum EncerramentoDiagnosticoONExceptionCode implements BusinessExceptionCode {
		ERRO_REMOVER_PRESCRICAO_CUIDADO_COM_AUTO_RELACIONAMENTO;
	}
	
	@SuppressWarnings("deprecation")
	public void removerPrescCuidadosDiagnosticosSelecionados (List<DiagnosticoEtiologiaVO> listaDiagnosticoEtiologiaVO, 
			Integer penAtdSeq, Integer penSeq) throws ApplicationBusinessException {
		
		EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO = getEpePrescricoesCuidadosDAO();
		EpePrescCuidDiagnosticoDAO epePrescCuidDiagnosticoDAO = getEpePrescCuidDiagnosticoDAO();
		ManutencaoPrescricaoCuidadoON manutencaoPrescricaoCuidadoON = getManutencaoPrescricaoCuidadoON();
		
		for (DiagnosticoEtiologiaVO diagnosticoEtiologiaVO : listaDiagnosticoEtiologiaVO) {

			if (diagnosticoEtiologiaVO.getSelecionado()) {
				
				List<EpePrescCuidDiagnostico> listaPrescCuidDiagnostico = epePrescCuidDiagnosticoDAO
						.listarPrescCuidDiagnosticoPorAtdSeqEDiagnostico(
								diagnosticoEtiologiaVO.getPrcAtdSeq(),
								diagnosticoEtiologiaVO.getCdgFdgDgnSnbGnbSeq(),
								diagnosticoEtiologiaVO.getCdgFdgDgnSnbSequencia(),
								diagnosticoEtiologiaVO.getCdgFdgDgnSequencia(),
								diagnosticoEtiologiaVO.getCdgFdgFreSeq(),
								penAtdSeq,
								penSeq);
				
				for (EpePrescCuidDiagnostico prescCuidDiagnostico : listaPrescCuidDiagnostico) {
					EpePrescricoesCuidadosId prescricaoCuidadoId = new EpePrescricoesCuidadosId();
					prescricaoCuidadoId.setSeq(prescCuidDiagnostico.getId().getPrcSeq());
					//Atributo setado para buscar a prescricao cuidado associada a prescricao cuidado diagnostico
					prescricaoCuidadoId.setAtdSeq(penAtdSeq);
					EpePrescricoesCuidados prescricaoCuidado =
							epePrescricoesCuidadosDAO.obterPorChavePrimaria(prescricaoCuidadoId);
					epePrescCuidDiagnosticoDAO.refresh(prescCuidDiagnostico);
					
					// #44571: Prescrição de Enfermagem -Excluir Diagnóstico
					
	//				Integer prcAtdSeq = prescricaoCuidado.getPrescricaoEnfermagem().getAtendimento().getSeq();
	//				Integer prcSeq = prescricaoCuidado.getId().getSeq();
					
					// *** Alternativa: buscar prescricaoCuidado pelo cui_seq (do  EpePrescCuidDiagnostico) e pelo pen_atd_seq e pen_seq
					
			/*		if (epePrescricoesCuidadosDAO.
							obterCountPrescricaoCuidadoAutoRelacionamentoPorAtdSeqESeq(prcAtdSeq, prcSeq) > 0) {
						throw new ApplicationBusinessException(
								EncerramentoDiagnosticoONExceptionCode.ERRO_REMOVER_PRESCRICAO_CUIDADO_COM_AUTO_RELACIONAMENTO,
								diagnosticoEtiologiaVO.getDescricaoDiagnostico(),
								diagnosticoEtiologiaVO.getDescricaoEtiologia());
					} */

					manutencaoPrescricaoCuidadoON.removerPrescricaoCuidado(prescricaoCuidado);
				}
			}
		}
	}
	
	protected EpePrescCuidDiagnosticoDAO getEpePrescCuidDiagnosticoDAO() {
		return epePrescCuidDiagnosticoDAO;
	}
	
	protected ManutencaoPrescricaoCuidadoON getManutencaoPrescricaoCuidadoON() {
		return manutencaoPrescricaoCuidadoON;
	}
	
	protected EpePrescricoesCuidadosDAO getEpePrescricoesCuidadosDAO() {
		return epePrescricoesCuidadosDAO;
	}

	
}
