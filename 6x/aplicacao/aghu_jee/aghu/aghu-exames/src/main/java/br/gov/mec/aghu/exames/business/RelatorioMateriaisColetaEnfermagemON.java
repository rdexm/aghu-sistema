package br.gov.mec.aghu.exames.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioMateriaisColetaEnfermagemON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioMateriaisColetaEnfermagemON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AelExamesDAO aelExamesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 316006606075698061L;

	public List<MateriaisColetarEnfermagemVO> pesquisarRelatorioMateriaisColetaEnfermagem(
			AghUnidadesFuncionais unidadeFuncional) throws ApplicationBusinessException {
		List<AelItemSolicitacaoExames> solicitacoes = getAelExamesDAO().pesquisarRelatorioMateriaisColetaEnfermagem(unidadeFuncional);

		List<MateriaisColetarEnfermagemVO> materiaisPaciente = new ArrayList<MateriaisColetarEnfermagemVO>();

		for(AelItemSolicitacaoExames aelItemSolicitacaoExame : solicitacoes){

			if (!agruparAmostras(materiaisPaciente, aelItemSolicitacaoExame)){
				MateriaisColetarEnfermagemVO materiaisColetarEnfermagemVO = new MateriaisColetarEnfermagemVO();
				materiaisColetarEnfermagemVO.setTitulo("Materiais a coletar em "
						+ new Date() + " pelo paciente");
				materiaisColetarEnfermagemVO.setUnfDescricao(unidadeFuncional
						.getDescricao());
				materiaisColetarEnfermagemVO.setAndarAlaDescricao(unidadeFuncional
						.getAndarAlaDescricao());
	
				materiaisColetarEnfermagemVO.setQrtLeito(getPrescricaoMedicaFacade().buscarResumoLocalPaciente(aelItemSolicitacaoExame.getSolicitacaoExame().getAtendimento()));
	
				if(aelItemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente().getProntuario()!=null){
					materiaisColetarEnfermagemVO.setProntuario(aelItemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente().getProntuario().toString());
				}else{
					materiaisColetarEnfermagemVO.setProntuario("");
				}
	
				materiaisColetarEnfermagemVO.setNomePaciente(aelItemSolicitacaoExame.getSolicitacaoExame().getAtendimento().getPaciente().getNome());
				SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM HH:mm");
				materiaisColetarEnfermagemVO.setDthrProgramada(sdf1.format(aelItemSolicitacaoExame.getDthrProgramada()));
				materiaisColetarEnfermagemVO.setSolicitacao(aelItemSolicitacaoExame.getSolicitacaoExame().getSeq().toString());
				materiaisColetarEnfermagemVO.setDescricaoExameMaterialAnalise(aelItemSolicitacaoExame.getExame().getDescricaoUsual()+" / "+aelItemSolicitacaoExame.getMaterialAnalise().getDescricao());
				materiaisColetarEnfermagemVO.setNroAmostras(aelItemSolicitacaoExame.getNroAmostras() == null ? "" : aelItemSolicitacaoExame.getNroAmostras().toString());
				materiaisColetarEnfermagemVO.setDescricao(aelItemSolicitacaoExame.getUnidadeFuncional().getDescricao());
	
				materiaisPaciente.add(materiaisColetarEnfermagemVO);
			}
		}

		if(materiaisPaciente != null && !materiaisPaciente.isEmpty()) {
			Collections.sort(materiaisPaciente);
		}
		
		obterLeitoProntuarioNomePAC(materiaisPaciente);

		return materiaisPaciente;
	}

	private void obterLeitoProntuarioNomePAC(List<MateriaisColetarEnfermagemVO> materiaisPaciente){
		String qrtLeitoAnterior = "";
		String prontuarioAnterior = "";
		String nomePacienteAnterior = "";
		int count = 0;
		for(MateriaisColetarEnfermagemVO material : materiaisPaciente){
			if(material.getQrtLeito().equals(qrtLeitoAnterior) && !(count % 10 == 0)) {
				material.setQrtLeito("");
			}else{
				qrtLeitoAnterior = material.getQrtLeito();
			}

			if(material.getProntuario().equals(prontuarioAnterior) && !(count % 10 == 0)){
				material.setProntuario("");
			}else{
				prontuarioAnterior = material.getProntuario();
			}

			if(material.getNomePaciente().equals(nomePacienteAnterior) && !(count % 10 == 0)){
				material.setNomePaciente("");
			}else{
				nomePacienteAnterior = material.getNomePaciente();
			}

			count++;
		}		
	}
	

	private boolean agruparAmostras(List<MateriaisColetarEnfermagemVO> materiaisPaciente, AelItemSolicitacaoExames aelItemSolicitacaoExame){
		boolean agrupou = false;
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM HH:mm");
		
		for (MateriaisColetarEnfermagemVO materialPaciente : materiaisPaciente){    
			if (aelItemSolicitacaoExame.getId().getSoeSeq().toString().equals(materialPaciente.getSolicitacao())
					&& materialPaciente.getDescricao().equals(aelItemSolicitacaoExame.getUnidadeFuncional().getDescricao())) {
				String dataProgramadaFormat = sdf1.format(aelItemSolicitacaoExame.getDthrProgramada());			
				if (materialPaciente.getDthrProgramada().equals(dataProgramadaFormat)){
					materialPaciente.setDescricaoExameMaterialAnalise(materialPaciente.getDescricaoExameMaterialAnalise()+" - "+aelItemSolicitacaoExame.getExame().getDescricaoUsual()+" / "+aelItemSolicitacaoExame.getMaterialAnalise().getDescricao());
					agrupou = true;
					break;
				}
			}
		}
				
		return agrupou;	
	}
	
	
	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}

}
