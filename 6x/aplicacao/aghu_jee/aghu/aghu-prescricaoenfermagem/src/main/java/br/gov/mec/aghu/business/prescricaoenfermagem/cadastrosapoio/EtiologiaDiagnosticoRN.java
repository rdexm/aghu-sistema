package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCuidadoDiagnostico;
import br.gov.mec.aghu.model.EpeFatDiagPaciente;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelDiagnosticoId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCuidadoDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatDiagPacienteDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EtiologiaDiagnosticoRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(EtiologiaDiagnosticoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeCuidadoDiagnosticoDAO epeCuidadoDiagnosticoDAO;

@Inject
private EpeFatDiagPacienteDAO epeFatDiagPacienteDAO;

@Inject
private EpeFatRelDiagnosticoDAO epeFatRelDiagnosticoDAO;

	private static final long serialVersionUID = 823867733426095821L;
	
	public enum EtiologiaDiagnosticoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_ADICAO_MANTER_DIAGNOSTICO_ETIOLOGIA, EPE_00083, MENSAGEM_ERRO_EXCLUSAO_1_MANTER_DIAGNOSTICO_ETIOLOGIA, 
		MENSAGEM_ERRO_EXCLUSAO_2_MANTER_DIAGNOSTICO_ETIOLOGIA
	}

	// RN1
	// TRIGGER AGH.EPET_FDG_BRI
	// BEFORE INSERT
	// ON EPE_FAT_REL_DIAGNOSTICOS
	//
	public void prePersistirEtiologiaDiagnostico(EpeFatRelDiagnostico etiologiaDiagnostico) throws ApplicationBusinessException {
		if (!DominioSituacao.A.equals(etiologiaDiagnostico.getSituacao())) {
			throw new ApplicationBusinessException(EtiologiaDiagnosticoRNExceptionCode.MENSAGEM_ERRO_ADICAO_MANTER_DIAGNOSTICO_ETIOLOGIA);
		}
		getEpeFatRelDiagnosticoDAO().persistir(etiologiaDiagnostico);
	}
	
	// Ativacao / Inativacao
	//
	public void ativarDesativarEtiologiaDiagnostico(EpeFatRelDiagnostico etiologiaDiagnostico) throws ApplicationBusinessException{
		preUpdate(etiologiaDiagnostico);
		getEpeFatRelDiagnosticoDAO().merge(etiologiaDiagnostico);
	}

	// RN2
	// TRIGGER AGH.EPET_FDG_BRU
	// BEFORE UPDATE
	// ON EPE_FAT_REL_DIAGNOSTICOS
	//
	private void preUpdate(EpeFatRelDiagnostico etiologiaDiagnostico) throws ApplicationBusinessException {
		EpeFatRelDiagnostico original = getEpeFatRelDiagnosticoDAO().obterOriginal(etiologiaDiagnostico);
		if (DominioSituacao.I.equals(etiologiaDiagnostico.getSituacao()) && !etiologiaDiagnostico.getSituacao().equals(original.getSituacao())) {
			verificaCuidadoDiagnosticoAtivoParaEtiologia(etiologiaDiagnostico);
		}		
	}

	// RN3
	// PROCEDURE RN_FDGP_VER_CDG
	// Crítica para verificar, ao tentar inativar FATOR REL DIAGNOSTICO,
	// se existe CUIDADO DIAGNOSTICO ativo para o FATOR REL DIAGNOSTICO.
	// Se existir, não pode desativar!!!
	//
	private void verificaCuidadoDiagnosticoAtivoParaEtiologia(EpeFatRelDiagnostico etiologiaDiagnostico) throws ApplicationBusinessException {
		List<EpeCuidadoDiagnostico> epeCuidadoDiagnosticoAtivo = getEpeCuidadoDiagnosticoDAO().obterEpeCuidadoDiagnosticoAtivo
							  (etiologiaDiagnostico.getId().getDgnSnbGnbSeq(), etiologiaDiagnostico.getId().getDgnSnbSequencia(),
							  etiologiaDiagnostico.getId().getDgnSequencia(), etiologiaDiagnostico.getId().getFreSeq());
		
		if (epeCuidadoDiagnosticoAtivo != null && !epeCuidadoDiagnosticoAtivo.isEmpty()) {
			throw new ApplicationBusinessException(EtiologiaDiagnosticoRNExceptionCode.EPE_00083);
		}
	}

	// Exclusão
	//
	public void excluirEtiologiaDiagnostico(EpeFatRelDiagnosticoId id) throws ApplicationBusinessException {
		EpeFatRelDiagnostico etiologiaDiagnostico = epeFatRelDiagnosticoDAO.obterPorChavePrimaria(id);
		preDelete(etiologiaDiagnostico);
		epeFatRelDiagnosticoDAO.removerPorId(id);
	}

	protected void preDelete(EpeFatRelDiagnostico etiologiaDiagnostico) throws ApplicationBusinessException {
		List<EpeFatDiagPaciente> epeFatDiagPaciente = getEpeFatDiagPacienteDAO().obterEpeCuidadoDiagnostico(
						etiologiaDiagnostico.getId().getDgnSnbGnbSeq(),	etiologiaDiagnostico.getId().getDgnSnbSequencia(),
						etiologiaDiagnostico.getId().getDgnSequencia(), etiologiaDiagnostico.getId().getFreSeq());

		if (epeFatDiagPaciente != null && !epeFatDiagPaciente.isEmpty()) {
			throw new ApplicationBusinessException(EtiologiaDiagnosticoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_1_MANTER_DIAGNOSTICO_ETIOLOGIA);
		}
		
		List<EpeCuidadoDiagnostico> epeCuidadoDiagnostico = getEpeCuidadoDiagnosticoDAO().obterEpeCuidadoDiagnostico(
						etiologiaDiagnostico.getId().getDgnSnbGnbSeq(),	etiologiaDiagnostico.getId().getDgnSnbSequencia(),
						etiologiaDiagnostico.getId().getDgnSequencia(),	etiologiaDiagnostico.getId().getFreSeq());

		if (epeCuidadoDiagnostico != null && !epeCuidadoDiagnostico.isEmpty()) {
			throw new ApplicationBusinessException(EtiologiaDiagnosticoRNExceptionCode.MENSAGEM_ERRO_EXCLUSAO_2_MANTER_DIAGNOSTICO_ETIOLOGIA);
		}
		
	}

	protected EpeFatRelDiagnosticoDAO getEpeFatRelDiagnosticoDAO() {
		return epeFatRelDiagnosticoDAO;
	}
	
	protected EpeCuidadoDiagnosticoDAO getEpeCuidadoDiagnosticoDAO() {
		return epeCuidadoDiagnosticoDAO;
	}
	
	protected EpeFatDiagPacienteDAO getEpeFatDiagPacienteDAO() {
		return epeFatDiagPacienteDAO;
	}
}