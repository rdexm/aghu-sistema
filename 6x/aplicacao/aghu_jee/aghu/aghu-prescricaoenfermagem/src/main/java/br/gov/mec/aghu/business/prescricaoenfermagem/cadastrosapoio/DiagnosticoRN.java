package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCaractDefDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCaractDefDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSubgrupoNecesBasicaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class DiagnosticoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(DiagnosticoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeCaractDefDiagnosticoDAO epeCaractDefDiagnosticoDAO;

@Inject
private EpeFatRelDiagnosticoDAO epeFatRelDiagnosticoDAO;

@Inject 
private EpeSubgrupoNecesBasicaDAO epeSubgrupoNecesBasicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1925619591771334384L;
	
	public enum DiagnosticoRNExceptionCode implements BusinessExceptionCode {
		EPE_00068, EPE_00069, EPE_00070, EPE_00071, MENSAGEM_ERRO_FATOR_RELACIONADO_MANTER_DIAGNOSTICO, MENSAGEM_ERRO_CARACTERISTICA_DEFINIDORA_MANTER_DIAGNOSTICO
	}

	
	/**
	 * PROCEDURE
	 * ORADB PROCEDURE EPEK_DGN_RN.RN_DGNP_VER_SUBGRUPO
	 * @throws ApplicationBusinessException 
	 * #4957 RN5
	 */
	public void prePersistirValidarEpeSubgrupoNecesBasica(EpeSubgrupoNecesBasicaId id) throws ApplicationBusinessException {
		
		//EpeSubgrupoNecesBasica subgrupo = getPrescricaoEnfermagemFacade().obterEpeSubgrupoNecesBasicaPorChavePrimaria(id);
		EpeSubgrupoNecesBasica subgrupo = epeSubgrupoNecesBasicaDAO.obterOriginal(id);
		
		if(subgrupo != null){
			if(!subgrupo.getSituacao().equals(DominioSituacao.A)){
				throw new ApplicationBusinessException(DiagnosticoRNExceptionCode.EPE_00068);
			}
		}else{
			throw new ApplicationBusinessException(DiagnosticoRNExceptionCode.EPE_00069);
		}
	}
	
	/**
	 * PROCEDURE
	 * ORADB PROCEDURE EPEK_DGN_RN.RN_DGNP_VER_CRD
	 * @throws ApplicationBusinessException 
	 * #4957 RN6
	 */
	public void prePersistirValidarEpeCaractDefDiagnostico(Short snbGnbSeq, Short snbSequencia, Short sequencia) throws ApplicationBusinessException {
		List<EpeCaractDefDiagnostico> listEpeCaractDefDiagnostico = getEpeCaractDefDiagnosticoDAO().pesquisarCaractDefDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia);
		if(listEpeCaractDefDiagnostico != null && !listEpeCaractDefDiagnostico.isEmpty()){
			throw new ApplicationBusinessException(DiagnosticoRNExceptionCode.EPE_00071);
		}
	}
	
	/**
	 * PROCEDURE
	 * ORADB PROCEDURE EPEK_DGN_RN.RN_DGNP_VER_FDG
	 * @throws ApplicationBusinessException 
	 * #4957 RN7
	 */
	public void prePersistirValidarEpeFatRelDiagnostico(Short snbGnbSeq, Short snbSequencia, Short sequencia) throws ApplicationBusinessException {
		List<EpeFatRelDiagnostico> listEpeFatRelDiagnostico = getEpeFatRelDiagnosticoDAO().pesquisarFatRelDiagnosticoPorSubgrupoAtivo(snbGnbSeq, snbSequencia, sequencia);
		if(listEpeFatRelDiagnostico != null && !listEpeFatRelDiagnostico.isEmpty()){
			throw new ApplicationBusinessException(DiagnosticoRNExceptionCode.EPE_00070);
		}
	}
	
	/**
	 * PROCEDURE
	 * ORADB PROCEDURE CGRI$CHK_EPE_DIAGNOSTICOS
	 * @throws ApplicationBusinessException 
	 * #4957 RN8
	 */
	public void preRemoverValidarEpeCaractDefDiagnosticoEpeFatRelDiagnostico(Short snbGnbSeq, Short snbSequencia, Short sequencia) throws ApplicationBusinessException {
		List<EpeFatRelDiagnostico> listEpeFatRelDiagnostico = getEpeFatRelDiagnosticoDAO().pesquisarFatRelDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia);
		if(listEpeFatRelDiagnostico != null && !listEpeFatRelDiagnostico.isEmpty()){
			throw new ApplicationBusinessException(DiagnosticoRNExceptionCode.MENSAGEM_ERRO_FATOR_RELACIONADO_MANTER_DIAGNOSTICO);
		}
		List<EpeCaractDefDiagnostico> listEpeCaractDefDiagnostico = getEpeCaractDefDiagnosticoDAO().pesquisarCaractDefDiagnosticoPorSubgrupo(snbGnbSeq, snbSequencia, sequencia);
		if(listEpeCaractDefDiagnostico != null && !listEpeCaractDefDiagnostico.isEmpty()){
			throw new ApplicationBusinessException(DiagnosticoRNExceptionCode.MENSAGEM_ERRO_CARACTERISTICA_DEFINIDORA_MANTER_DIAGNOSTICO);
		}
	}
	
	protected EpeCaractDefDiagnosticoDAO getEpeCaractDefDiagnosticoDAO() {
		return epeCaractDefDiagnosticoDAO;
	}
	
	protected EpeFatRelDiagnosticoDAO getEpeFatRelDiagnosticoDAO() {
		return epeFatRelDiagnosticoDAO;
	}
}
