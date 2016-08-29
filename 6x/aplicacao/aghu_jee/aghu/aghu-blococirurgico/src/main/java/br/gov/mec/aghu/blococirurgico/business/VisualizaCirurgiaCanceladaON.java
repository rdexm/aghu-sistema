package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.VisualizaCirurgiaCanceladaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class VisualizaCirurgiaCanceladaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VisualizaCirurgiaCanceladaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4150100427842280060L;

	protected enum VisualizarCirurgiaCanceladaONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_MOT_DESMARCAR_NAO_CONFIGURADO
		;
	}
	
	public VisualizaCirurgiaCanceladaVO buscarCirurgiaCancelada (Integer agdSeq) throws ApplicationBusinessException {
		
		//Recupera Parametro referente a motivo de cancelamento fixo para desmarcar cirurgia no Prontuario Online
		AghParametros parametroMotivoDesmarcar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
		if(parametroMotivoDesmarcar == null){
			throw new ApplicationBusinessException(VisualizarCirurgiaCanceladaONExceptionCode.MENSAGEM_PARAMETRO_MOT_DESMARCAR_NAO_CONFIGURADO);
		}
		Short vlrNumerico = parametroMotivoDesmarcar.getVlrNumerico().shortValueExact();
		
		VisualizaCirurgiaCanceladaVO vo = new VisualizaCirurgiaCanceladaVO();

		MbcAgendas agenda = this.getMbcAgendasDAO().obterPorChavePrimaria(agdSeq);
		
		vo.setProntuario(agenda.getPaciente().getProntuario());
		vo.setPacCodigo(agenda.getPaciente().getCodigo());
		vo.setPacNome(agenda.getPaciente().getNome());
		vo.setRegime(agenda.getRegime());
		vo.setTempo(agenda.getTempoSala());
		vo.setProcedimentoPrincipal(agenda.getProcedimentoCirurgico().getDescricao());
		vo.setComentario(agenda.getComentario());
		
		//FUNCTION MBCC_GET_AGD_NRO_CAN
		vo.setNumeroCancelamentos(this.getMbcCirurgiasDAO().pesquisarMotivoCirurgiasCanceladasCount(agdSeq, vlrNumerico).intValue());
		
		//FUNCTION MBCC_GET_AGD_MOTIVOS
		vo.setMotivoCancelamento(this.obterMotivoCancelamento(agdSeq, vlrNumerico));

		//FUNCTION MBCC_GET_AGD_DT_CANC
		List<MbcCirurgias> listaDataCirurgiasCanceladas = getMbcCirurgiasDAO().pesquisarMotivoCirurgiasCanceladasOrdenaPorData(agdSeq, vlrNumerico);
		if (listaDataCirurgiasCanceladas != null && listaDataCirurgiasCanceladas.size() > 0) {
			vo.setDataCancelamento(listaDataCirurgiasCanceladas.get(0).getData());
		}
		
		return vo;
		
	}
	
	public String obterMotivoCancelamento (Integer agdSeq, Short vlrNumerico) {
		
		List<MbcCirurgias> listaMotivoCirurgiasCanceladas = getMbcCirurgiasDAO().pesquisarMotivoCirurgiasCanceladas(agdSeq, vlrNumerico);
		
		String motivoCancelamento = null;
		StringBuilder motivo = new StringBuilder();		
		for(MbcCirurgias cirurgia : listaMotivoCirurgiasCanceladas){
			
			if(cirurgia.getMotivoCancelamento() != null){
				motivo.append(getAmbulatorioFacade().obterDescricaoCidCapitalizada(cirurgia.getMotivoCancelamento().getDescricao(), CapitalizeEnum.PRIMEIRA));
			}			
			if(cirurgia.getQuestao() != null){
				motivo.append(". ").append(cirurgia.getQuestao().getDescricao());					
			}			
			if(cirurgia.getComplementoCanc() != null){
				if(cirurgia.getQuestao() == null){
					motivo.append(": ");					
				}else{
					motivo.append(' ');	
				}	
				motivo.append(cirurgia.getComplementoCanc());	
			}			
			if(motivo != null){
				motivo.append("; ");	
			}
		}		
		if (motivo != null){
			motivoCancelamento = motivo.substring(0, motivo.length()-1);
		}
		return motivoCancelamento;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.iAmbulatorioFacade;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}	
}
