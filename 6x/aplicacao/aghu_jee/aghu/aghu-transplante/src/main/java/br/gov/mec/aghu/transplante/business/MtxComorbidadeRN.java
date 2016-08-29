package br.gov.mec.aghu.transplante.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MtxComorbidade;
import br.gov.mec.aghu.transplante.dao.MtxComorbidadeDAO;
import br.gov.mec.aghu.transplante.dao.MtxComorbidadePacienteDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MtxComorbidadeRN extends BaseBusiness {
	
	private static final long serialVersionUID = -393318181657615293L;
	
	@Inject
	private MtxComorbidadeDAO mtxComorbidadeDAO;
	@Inject
	private MtxComorbidadePacienteDAO mtxComorbidadePacienteDAO;

	private enum ComorbidadeRNExceptionCode implements BusinessExceptionCode {
		COMORBIDADE_JA_CADASTRADA,
		MENSAGEM_TIPO_OBRIGATORIO_COMORBIDADE,
		COMORBIDADE_COM_E_SEM_CID,
		MENSAGEM_OBRIGATORIEDADE_DIAGNOSTICO;
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void validarInclusaoComorbidade(MtxComorbidade mtxComorbidade) throws ApplicationBusinessException, BaseException{
		if(mtxComorbidade.getCidSeq() != null && mtxComorbidade.getDescricao() != null){
			throw new ApplicationBusinessException(ComorbidadeRNExceptionCode.COMORBIDADE_COM_E_SEM_CID);
		}
		if(mtxComorbidade.getCidSeq() == null && mtxComorbidade.getDescricao() == null){
			throw new ApplicationBusinessException(ComorbidadeRNExceptionCode.COMORBIDADE_COM_E_SEM_CID);
		}
		if(mtxComorbidade.getDescricao() != null && (mtxComorbidade.getDescricao().trim().equals("") || mtxComorbidade.getDescricao().length() > 300)){
			throw new ApplicationBusinessException(ComorbidadeRNExceptionCode.MENSAGEM_OBRIGATORIEDADE_DIAGNOSTICO);
		}
		if(mtxComorbidade.getTipo() == null){
			throw new ApplicationBusinessException(ComorbidadeRNExceptionCode.MENSAGEM_TIPO_OBRIGATORIO_COMORBIDADE);
		}
		Long result = mtxComorbidadeDAO.validarInclusaoComorbidade(mtxComorbidade);
		if(result > 0){
		  throw new ApplicationBusinessException(ComorbidadeRNExceptionCode.COMORBIDADE_JA_CADASTRADA);
		}
	}
	
	public boolean validarGravarComorbidadePaciente(List<MtxComorbidade> listaComorbidades, List<MtxComorbidade> listaComorbidadesExcluidas){
		if(listaComorbidades.isEmpty() && listaComorbidadesExcluidas.isEmpty()){
			return false;
		}
		return true;
	}
	
	public List<MtxComorbidade> removerComorbidadePacienteJaInseridas(List<MtxComorbidade> listaComorbidades, AipPacientes aipPacientes){
		List<MtxComorbidade> tempList = new ArrayList<MtxComorbidade>();
		tempList.addAll(listaComorbidades);
		for (MtxComorbidade mtxComorbidade : tempList) {
			if(mtxComorbidadePacienteDAO.pesquisarMtxComorbidadePaciente(aipPacientes, mtxComorbidade) != null){
				listaComorbidades.remove(mtxComorbidade);
			}
		}
		return listaComorbidades;
	}
}
