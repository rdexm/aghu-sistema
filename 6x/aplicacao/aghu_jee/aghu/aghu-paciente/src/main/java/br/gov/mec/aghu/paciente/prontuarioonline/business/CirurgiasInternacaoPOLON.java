package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgiaEAgenda;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.CirurgiasInternacaoPOLVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;

@Stateless
public class CirurgiasInternacaoPOLON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(CirurgiasInternacaoPOLON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IProntuarioOnlineFacade prontuarioOnlineFacade;

@EJB
private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	private static final long serialVersionUID = 8320868510713104416L;

	public List<CirurgiasInternacaoPOLVO> pesquisarCirurgiasInternacaoPOL(Integer codigo) {		
			
		List<CirurgiasInternacaoPOLVO> listaCirurgias = getBlocoCirurgicoFacade().pesquisarCirurgiasInternacaoPOL(codigo);
		
		//for (CirurgiasInternacaoPOLVO cirurgiaVO: listaCirurgias){
		for(int i =0 ; i < listaCirurgias.size(); i++){
			
			CirurgiasInternacaoPOLVO cirurgiaVO = listaCirurgias.get(i);
			
			//Habilita botão Motivo Cancelamento se Situacao = Cirurgia/PDT Cancelada

			if(cirurgiaVO.getSituacao() != null && cirurgiaVO.getSituacao() == DominioSituacaoCirurgia.CANC) {
				cirurgiaVO.setHabilitaBotaoMotivoCancel(Boolean.TRUE);				
			}else{
				cirurgiaVO.setHabilitaBotaoMotivoCancel(Boolean.FALSE);
			}	
				
			//Habilita icone Documento Assinado conforme RN0
			
			cirurgiaVO.setBotaoDocAssinado(getProntuarioOnlineFacade().habilitarBotaoDocAssinadoCirurgiasPol(cirurgiaVO.getSeq()));

			//Concatena dados para o "selectOneRadio" prevendo varios procedimentos p/ uma mesma cirurgia
			StringBuffer idRadio = new StringBuffer();
			idRadio.append(cirurgiaVO.getSeq()).append(cirurgiaVO.getEprPciSeq()).append(cirurgiaVO.getEprEspSeq()).append(cirurgiaVO.getIndRespProc());

			cirurgiaVO.setIdRadio(idRadio.toString());

			
			if(cirurgiaVO.getSituacao() != null){
				cirurgiaVO.setSituacaoCirurgiaEAgenda(DominioSituacaoCirurgiaEAgenda.valueOf(cirurgiaVO.getSituacao().name()));
			}
			
		}
		
				
		List<CirurgiasInternacaoPOLVO> listaAgendasCirurgias = getBlocoCirurgicoFacade().pesquisarAgendasProcCirurgicosInternacaoPOL(codigo);
		
		for (CirurgiasInternacaoPOLVO agendaCirurgiaVO: listaAgendasCirurgias){	
			
			//Seta SEQ da cirurgia = 0  
			agendaCirurgiaVO.setSeq(Integer.valueOf("0"));
			
			//Seta Situacao = null e Desabilita botão Motivo Cancelamento   
			agendaCirurgiaVO.setSituacao(null);//(DominioSituacaoCirurgia.valueOf("")); //????????? NULO			
			
			agendaCirurgiaVO.setHabilitaBotaoMotivoCancel(Boolean.FALSE);
			
			//Desabilita icone Documento Assinado		
			agendaCirurgiaVO.setBotaoDocAssinado(Boolean.FALSE);
			
			//Concatena dados para o "selectOneRadio" prevendo varios procedimentos p/ uma mesma cirurgia
			StringBuffer idRadio = new StringBuffer();
			idRadio.append(agendaCirurgiaVO.getSeq()).append(agendaCirurgiaVO.getEprPciSeq()).append(agendaCirurgiaVO.getEprEspSeq()).append(agendaCirurgiaVO.getIndRespProc());

			agendaCirurgiaVO.setIdRadio(idRadio.toString());
			
			if(agendaCirurgiaVO.getIndSituacao() != null){
				agendaCirurgiaVO.setSituacaoCirurgiaEAgenda(DominioSituacaoCirurgiaEAgenda.valueOf(agendaCirurgiaVO.getIndSituacao().name()));
			}
			
		}
		
		listaCirurgias.addAll(listaAgendasCirurgias);		
		
		if (!(listaCirurgias.isEmpty())){ //???????????????????
			
			//remove row nula da VO
			while(listaCirurgias.remove(null)){
				listaCirurgias.remove(null);
			}			
		}
							
		CoreUtil.ordenarLista(listaCirurgias, "data", false);
		
		
		for (CirurgiasInternacaoPOLVO vo : listaCirurgias) {
			String pacOruAccNummer = blocoCirurgicoFacade.obterPacOruAccNummer(vo.getSeq());
			vo.setPacOruAccNummer(pacOruAccNummer);
		}
		return listaCirurgias;
	}
		
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

}