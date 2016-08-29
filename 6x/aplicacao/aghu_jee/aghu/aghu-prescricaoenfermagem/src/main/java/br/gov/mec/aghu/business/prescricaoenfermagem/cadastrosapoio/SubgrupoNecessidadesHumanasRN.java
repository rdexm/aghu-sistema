package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeGrupoNecesBasicaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SubgrupoNecessidadesHumanasRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SubgrupoNecessidadesHumanasRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeDiagnosticoDAO epeDiagnosticoDAO;

@Inject
private EpeGrupoNecesBasicaDAO epeGrupoNecesBasicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3315412510099409090L;

	public enum SubgrupoNecessidadesHumanasRNExceptionCode implements BusinessExceptionCode {
		EPE_00062, EPE_00063, EPE_00065
	}

	/**
	 * PROCEDURE
	 * ORADB PROCEDURE RN_SNBP_VER_DIAGNOST
	 * @throws ApplicationBusinessException 
	 * #4956 RN12
	 */
	public void verificarDiagnosticoAtivo(EpeSubgrupoNecesBasica epeSubgrupoNecesBasica)
			throws ApplicationBusinessException {
		List<EpeDiagnostico> diagnosticos =  getEpeDiagnosticoDAO().pesquisarDiagnosticoAtivoPorGnbSeqESnbSequencia(epeSubgrupoNecesBasica.getId().getGnbSeq(), epeSubgrupoNecesBasica.getId().getSequencia());
		if(diagnosticos != null && !diagnosticos.isEmpty()){
			throw new ApplicationBusinessException(SubgrupoNecessidadesHumanasRNExceptionCode.EPE_00063);
		}
	}

	/**
	 * PROCEDURE
	 * ORADB PROCEDURE RN_SNBP_VER_GRUPO
	 * @throws ApplicationBusinessException 
	 * #4956 RN9
	 */
	public void verificarGrupoNecessidadeAtivo(Short seqGrupoNecessidadesHumanas) throws ApplicationBusinessException {
		EpeGrupoNecesBasica epeGrupoNecesBasica = getEpeGrupoNecesBasicaDAO().obterPorChavePrimaria(seqGrupoNecessidadesHumanas);
		if(epeGrupoNecesBasica == null){
			throw new ApplicationBusinessException(SubgrupoNecessidadesHumanasRNExceptionCode.EPE_00065);
		}else{
			if(!epeGrupoNecesBasica.getSituacao().equals(DominioSituacao.A)){
				throw new ApplicationBusinessException(SubgrupoNecessidadesHumanasRNExceptionCode.EPE_00062);
			}
		}
	}
	
	
	protected EpeGrupoNecesBasicaDAO getEpeGrupoNecesBasicaDAO() {
		return epeGrupoNecesBasicaDAO;
	}
	
	protected EpeDiagnosticoDAO getEpeDiagnosticoDAO() {
		return epeDiagnosticoDAO;
	}
}
