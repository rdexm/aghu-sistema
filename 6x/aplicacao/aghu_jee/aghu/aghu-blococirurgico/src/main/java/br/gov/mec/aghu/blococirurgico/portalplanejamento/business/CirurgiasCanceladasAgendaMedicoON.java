package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

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
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.CirurgiasCanceladasAgendaMedicoVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class CirurgiasCanceladasAgendaMedicoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(CirurgiasCanceladasAgendaMedicoON.class);

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

	
	private static final long serialVersionUID = 5422856624813137110L;
	
	protected enum CirurgiasCanceladasAgendaMedicoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_MOT_DESMARCAR_NAO_CONFIGURADO
		;
	}
	
	public List<CirurgiasCanceladasAgendaMedicoVO> pesquisarCirgsCanceladasByMedicoEquipe(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Integer serMatricula, Short serVinCodigo, Short pucUnfSeq, DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) throws ApplicationBusinessException {
				
		if (orderProperty == null) {
			orderProperty = CirurgiasCanceladasAgendaMedicoVO.Fields.NOME.toString();
			asc = Boolean.TRUE;
		}		

		List<CirurgiasCanceladasAgendaMedicoVO> listaCirurgiasCanceladasAgendaMedicoVO = 
			getMbcAgendasDAO().pesquisarCirgsCanceladasByMedicoEquipe(firstResult, maxResult, orderProperty, asc, serMatricula, serVinCodigo, pucUnfSeq, indFuncaoProf, espSeq, unfSeq, pacCodigo);
				
		for(CirurgiasCanceladasAgendaMedicoVO vo : listaCirurgiasCanceladasAgendaMedicoVO){
			
			setarDadosVO(vo);			

			// FUNCTION MBCC_GET_AGD_MOTIVOS			
				
			AghParametros parametroMotivoDesmarcar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);		
			if(parametroMotivoDesmarcar == null){
				throw new ApplicationBusinessException(CirurgiasCanceladasAgendaMedicoONExceptionCode.MENSAGEM_PARAMETRO_MOT_DESMARCAR_NAO_CONFIGURADO);
			}
			
			Short vlrNumerico = parametroMotivoDesmarcar.getVlrNumerico().shortValueExact();		
			List<MbcCirurgias> listaMotivoCirurgiasCanceladas = getMbcCirurgiasDAO().pesquisarMotivoCirurgiasCanceladas(vo.getAgdSeq(), vlrNumerico);

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
						motivo.append("");	
					}	
					motivo.append(cirurgia.getComplementoCanc());	
				}			
				if(motivo != null){
					motivo.append("; ");	
				}
			}		
			if (motivo != null) {
				String motivoCancelamento = motivo.toString();
				if(motivo.length()>0){
					motivoCancelamento = motivo.substring(0, motivo.length()-1);
				}
				vo.setMotivoCancelamento(motivoCancelamento);
			}						
		}						
		return listaCirurgiasCanceladasAgendaMedicoVO;
	}
	
	private void setarDadosVO(CirurgiasCanceladasAgendaMedicoVO vo) {
		if(vo.getProntuario() != null){
			vo.setPacProntuario(CoreUtil.formataProntuario(vo.getProntuario().toString()));
		}		
		if(vo.getNome() != null){
			vo.setNome(getAmbulatorioFacade().obterDescricaoCidCapitalizada(vo.getNome(), CapitalizeEnum.TODAS));
		}		 
		if(vo.getPciDescricao() != null){
			vo.setPciDescricao(getAmbulatorioFacade().obterDescricaoCidCapitalizada(vo.getPciDescricao(), CapitalizeEnum.PRIMEIRA));
		}		
	}

	public Long pesquisarCirgsCanceladasByMedicoEquipeCount(Integer serMatricula, Short serVinCodigo, Short pucUnfSeq,
			DominioFuncaoProfissional indFuncaoProf, Short espSeq, Short unfSeq, Integer pacCodigo) {
		return getMbcAgendasDAO().pesquisarCirgsCanceladasByMedicoEquipeCount(serMatricula, serVinCodigo, pucUnfSeq, indFuncaoProf, espSeq, unfSeq, pacCodigo);
	}	
	

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.iAmbulatorioFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO() {
		return mbcAgendasDAO;
	}	

	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
}
