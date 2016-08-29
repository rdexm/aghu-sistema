package br.gov.mec.aghu.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghEspecialidadesDAO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class AghEspecialidadeON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AghEspecialidadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AghEspecialidadesDAO aghEspecialidadesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4094174011234897327L;

	public List<AghEspecialidades> getListaEspecialidades(String parametro) {
		AghEspecialidadesDAO dao = getAghEspecialidadesDAO();
		List<AghEspecialidades> result = dao.pesquisarPorSiglaIndicesAmbulatorio(parametro);
		if (result.isEmpty()) {
			result = dao.pesquisarPorNomeIndicesAmbulatorio(parametro);
		}
		return result;
	}
	
	public List<AghEspecialidades> getListaEspecialidadesTodasSituacoes(String parametro) {
		AghEspecialidadesDAO dao = getAghEspecialidadesDAO();
		List<AghEspecialidades> result = dao.pesquisarPorSiglaIndicesAmbulatorioTodasSituacoes(parametro);
		if (result == null || result.isEmpty()) {
			result = dao.pesquisarPorNomeIndicesAmbulatorioTodasSituacoes(parametro);
		}
		return result;
	}	
	
	
	public List<AghEspecialidades> getListaTodasEspecialidades(String parametro) {
		AghEspecialidadesDAO dao = getAghEspecialidadesDAO();
		List<AghEspecialidades> result = dao.pesquisarPorSiglaIndicesAmbulatorioTodasSituacoes(parametro);
		if (result == null || result.isEmpty()) {
			result = dao.pesquisarPorNomeAmbulatorio(parametro);
		}
		return result;
	}
	
	public List<AghEspecialidades> pesquisarEspecialidadesAtivas(String parametro) {
		AghEspecialidadesDAO dao = getAghEspecialidadesDAO();
		List<AghEspecialidades> result = dao
				.pesquisarEspecialidadesAtivasPorSigla(parametro);
		if(result.isEmpty()){
			result = dao
			.pesquisarEspecialidadesAtivasPorNome(parametro);
		}
		return result;
	}

	public List<AghEspecialidades> pesquisarEspecialidadesAtivasOrigemSumario(String parametro, Integer atdSeq, RapServidores servidorLogado) {
		AghEspecialidadesDAO dao = getAghEspecialidadesDAO();
		List<AghEspecialidades> result = dao
				.pesquisarEspecialidadesAtivasPorSiglaOrigemSumario(parametro, atdSeq, servidorLogado);
		if(result.isEmpty()){
			result = dao
			.pesquisarEspecialidadesAtivasPorNomeOrigemSumario(parametro, atdSeq, servidorLogado);
		}
		return result;
	}

	public AghEspecialidades obterAghEspecialidadesPorChavePrimaria(Short chavePrimaria){
		AghEspecialidades retorno = getAghEspecialidadesDAO().obterPorChavePrimaria(chavePrimaria);
		if (retorno != null){
			retorno.getServidorChefe();
			retorno.getServidorChefe().getPessoaFisica();
			retorno.getServidorChefe().getId();
			retorno.getEspecialidade();
		}
		
		return retorno;
	}
	
	public AghEspecialidadesDAO getAghEspecialidadesDAO(){
		return aghEspecialidadesDAO;
	}

}