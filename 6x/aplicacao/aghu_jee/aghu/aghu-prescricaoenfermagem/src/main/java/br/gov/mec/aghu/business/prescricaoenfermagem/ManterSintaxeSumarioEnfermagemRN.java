package br.gov.mec.aghu.business.prescricaoenfermagem;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoItemPrescricaoSumario;
import br.gov.mec.aghu.model.EpeItemCuidadoSumario;
import br.gov.mec.aghu.model.EpeItemCuidadoSumarioId;
import br.gov.mec.aghu.model.EpeItemPrescricaoSumario;
import br.gov.mec.aghu.model.EpePrescricoesCuidados;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeItemCuidadoSumarioDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeItemPrescricaoSumarioDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpePrescricoesCuidadosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ManterSintaxeSumarioEnfermagemRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ManterSintaxeSumarioEnfermagemRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeItemPrescricaoSumarioDAO epeItemPrescricaoSumarioDAO;

@Inject
private EpeItemCuidadoSumarioDAO epeItemCuidadoSumarioDAO;

@EJB
private IAghuFacade aghuFacade;

@Inject
private EpePrescricoesCuidadosDAO epePrescricoesCuidadosDAO;

	private static final long serialVersionUID = -1283517200611475304L;

	/**
	 * ORADB Procedure EPEK_SINTAXE_SUMARIO.EPEP_SINT_SUMR_CUID
	 * 
	 * Esta rotina monta a sintaxe do sumario do item dos cuidados médicos.
	 * 
	 * @throws ApplicationBusinessException
	 *  
	 */
	public Integer montaSintaxeSumarioItemCuidados(
			Integer seqAtendimento, Integer seqAtendimentoPaciente,
			Integer seqPrescricaoCuidado) throws ApplicationBusinessException {
		EpePrescricoesCuidadosDAO epePrescricaoCuidadoDAO = getEpePrescricaoCuidadoDAO();
		EpeItemPrescricaoSumarioDAO epeItemPrescricaoSumarioDAO = getEpeItemPrescricaoSumarioDAO();
		EpeItemCuidadoSumarioDAO mpmItemCuidadoSumarioDAO = getEpeItemCuidadoSumarioDAO();
		
		List<EpeItemCuidadoSumario> listItemCuidadoSumario = new ArrayList<EpeItemCuidadoSumario>();
		int ituSeq=0;
		int count=0;
		String descTfq = null;
		StringBuffer sintaxeSumrCuid = null;
		
		for (EpePrescricoesCuidados prescricaoCuidado : epePrescricaoCuidadoDAO.listPrescricaoCuidadoPorId(seqAtendimento, seqPrescricaoCuidado)){
			
			count+=1;
			EpeItemCuidadoSumario ics=new EpeItemCuidadoSumario();				
			
			EpeItemCuidadoSumarioId id= new EpeItemCuidadoSumarioId();
			id.setSeqp(count);
			ics.setId(id);
			ics.setTipoFrequenciaAprazamento(prescricaoCuidado.getTipoFrequenciaAprazamento());
			ics.setCuidado(prescricaoCuidado.getCuidado());
			
			if (prescricaoCuidado.getTipoFrequenciaAprazamento().getSintaxe()!=null){
				descTfq = prescricaoCuidado.getTipoFrequenciaAprazamento().getSintaxeFormatada(prescricaoCuidado.getFrequencia().shortValue())+ " ";
				ics.setFrequencia(Integer.valueOf(prescricaoCuidado.getFrequencia()));
			}else{
				descTfq = prescricaoCuidado.getTipoFrequenciaAprazamento().getDescricao() + " " ;
			}
			if (prescricaoCuidado.getDescricao()!=null){
				ics.setDescricao(prescricaoCuidado.getDescricao());
				sintaxeSumrCuid = new StringBuffer(prescricaoCuidado.getCuidado().getDescricao().toLowerCase())
											.append(' ')
											.append(prescricaoCuidado.getDescricao())
											.append(' ');
			}else{
				sintaxeSumrCuid = new StringBuffer(prescricaoCuidado.getCuidado().getDescricao()).append(' ');
			}
			sintaxeSumrCuid.append(' ').append(descTfq);
			listItemCuidadoSumario.add(ics);
		}
		
		EpeItemPrescricaoSumario ips = new EpeItemPrescricaoSumario();
		ips.setAtendimentoPaciente(getAghuFacade().obterAtendimentoPaciente(seqAtendimento, seqAtendimentoPaciente));
		ips.setTipo(DominioTipoItemPrescricaoSumario.POSITIVO_4);
		ips.setDescricao(sintaxeSumrCuid.toString());
		
		List<EpeItemPrescricaoSumario> lista = epeItemPrescricaoSumarioDAO
				.listarItensPrescricaoSumario(seqAtendimento,
						seqAtendimentoPaciente, sintaxeSumrCuid.toString(),
						DominioTipoItemPrescricaoSumario.POSITIVO_4);
		if (lista == null || lista.isEmpty()) {
			epeItemPrescricaoSumarioDAO.persistir(ips);				
			ituSeq = ips.getSeq();	
			for (EpeItemCuidadoSumario ics : listItemCuidadoSumario){
				ics.setItemPrescricaoSumario(ips);
				ics.getId().setIsuSeq(ituSeq);
				ics.setItemPrescricaoSumario(ips);
				
				mpmItemCuidadoSumarioDAO.persistir(ics);
			}
		} else {
			ituSeq = lista.get(0).getSeq();
		}
		return ituSeq;
	}

	private EpeItemCuidadoSumarioDAO getEpeItemCuidadoSumarioDAO() {
		return epeItemCuidadoSumarioDAO;
	}

	private EpeItemPrescricaoSumarioDAO getEpeItemPrescricaoSumarioDAO() {
		return epeItemPrescricaoSumarioDAO;
	}

	private EpePrescricoesCuidadosDAO getEpePrescricaoCuidadoDAO() {
		return epePrescricoesCuidadosDAO;
	}
	
	private IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

}
