package br.gov.mec.aghu.exames.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * @author twickert
 * 
 * Representa a montagem dos componentes para mascara de exames quando for pra Patologia
 *
 */
@Stateless
public class MascaraExamesCompPatologiaON extends BaseBusiness {
	
	private static final long serialVersionUID = 1917289430785811445L;
	
	public static final int COMPONENT_OFFSET = 20;
	
	private static final Log LOG = LogFactory.getLog(MascaraExamesCompPatologiaON.class);
	
//	@Inject
//	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
//	public Integer montaRecebimentoPatologia(NumeroApTipoVO numeroApTipoVO, Application application, UIComponent divCabecalho, Integer maiorAltura) throws BaseException {
//		
//		List<DataRecebimentoSolicitacaoVO> listaDataRecebimentoSolicitacaoVO = getAelItemSolicitacaoExameDAO().listarDataRecebimentoTipoExamePatologico(numeroApTipoVO.getNumeroAp(), numeroApTipoVO.getLu2Seq());
//		
//		for (DataRecebimentoSolicitacaoVO vo : listaDataRecebimentoSolicitacaoVO) {
//			
//			HtmlOutputText outputRecebimento = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
//			outputRecebimento.setId("temp_"+ MascaraExamesON.UNIQUE++);
//			outputRecebimento.setStyle("font-family: Courier New; font-size: 8pt;");
//			
//			outputRecebimento.setValue("Recebimento material/Paciente: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR")).format(vo.getDataRecebimento()) + " (Solicitação: " + vo.getSoeSeq() + ")");
//			maiorAltura +=15;
//			
//			UIComponent subDivRecebimento = getMascaraExamesON().criarDiv("subDiv" + MascaraExamesON.UNIQUE++, "top: " + maiorAltura + "px;width:745px; position: absolute; text-align: right;");
//			subDivRecebimento.getChildren().add(outputRecebimento);
//
//			divCabecalho.getChildren().add(subDivRecebimento);
//		}
//
//		List<DataLiberacaoVO> listaDataLiberacao = getAelItemSolicitacaoExameDAO().listarDataLiberacaoTipoExamePatologico(numeroApTipoVO.getNumeroAp(), numeroApTipoVO.getLu2Seq());
//		
//		for (DataLiberacaoVO dataLiberacao : listaDataLiberacao) {
//			
//			HtmlOutputText outputRecebimento = (HtmlOutputText) application.createComponent(HtmlOutputText.COMPONENT_TYPE);
//			outputRecebimento.setId("temp_"+ MascaraExamesON.UNIQUE++);
//			outputRecebimento.setStyle("font-family: Courier New; font-size: 8pt;");
//			if(dataLiberacao.getDataLiberacao() !=null){
//				outputRecebimento.setValue("Liberação: " + new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR")).format(dataLiberacao.getDataLiberacao()));
//				maiorAltura +=15;
//			}
//			
//			UIComponent subDivRecebimento = getMascaraExamesON().criarDiv("subDiv" + MascaraExamesON.UNIQUE++, "top: " + maiorAltura + "px;width:745px; position: absolute; text-align: right;");
//			subDivRecebimento.getChildren().add(outputRecebimento);
//
//			divCabecalho.getChildren().add(subDivRecebimento);
//		}
//		
//		return maiorAltura;
//	}

//	private MascaraExamesON getMascaraExamesON(){
//		return new MascaraExamesON();
//	}
//
//	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
//		return aelItemSolicitacaoExameDAO;
//	}
	

}
