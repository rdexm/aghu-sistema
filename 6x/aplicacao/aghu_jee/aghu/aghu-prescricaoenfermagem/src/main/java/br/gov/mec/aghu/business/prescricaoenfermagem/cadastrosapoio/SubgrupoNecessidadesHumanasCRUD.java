package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeDiagnostico;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasicaId;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSubgrupoNecesBasicaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class SubgrupoNecessidadesHumanasCRUD extends BaseBusiness {


@EJB
private SubgrupoNecessidadesHumanasRN subgrupoNecessidadesHumanasRN;

private static final Log LOG = LogFactory.getLog(SubgrupoNecessidadesHumanasCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeDiagnosticoDAO epeDiagnosticoDAO;

@Inject
private EpeSubgrupoNecesBasicaDAO epeSubgrupoNecesBasicaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1698007720904023457L;

	public enum SubgrupoNecessidadesHumanasCRUDExceptionCode implements BusinessExceptionCode {
		MENSAGEM_EXCLUIR_SUBGRUPO_DIAGNOSTICO_ASSOCIADO
	}
	
	public void removerSubgrupoNecessidadesHumanas(EpeSubgrupoNecesBasicaId id) throws ApplicationBusinessException{			
		//Faz exclusão
		List<EpeDiagnostico> diagnosticos =  getEpeDiagnosticoDAO().pesquisarDiagnosticoAtivoPorGnbSeqESnbSequencia(id.getGnbSeq(), id.getSequencia());
		if(diagnosticos != null && !diagnosticos.isEmpty()){
			throw new ApplicationBusinessException(SubgrupoNecessidadesHumanasCRUDExceptionCode.MENSAGEM_EXCLUIR_SUBGRUPO_DIAGNOSTICO_ASSOCIADO);
		}
		epeSubgrupoNecesBasicaDAO.removerPorId(id);
	}

	public void persistirSubgrupoNecessidadesHumanas(EpeSubgrupoNecesBasica epeSubgrupoNecesBasica, Boolean checkboxSubGrupoAtivo, Short seqGrupoNecessidadesHumanas) throws ApplicationBusinessException{
		
		gravarSituacaoSubgrupo(epeSubgrupoNecesBasica, checkboxSubGrupoAtivo);
		
		if (epeSubgrupoNecesBasica.getId() != null && epeSubgrupoNecesBasica.getId().getSequencia() != null){
			//Faz a atualização
			EpeSubgrupoNecesBasica epeSubgrupoNecesBasicaOriginal = getEpeSubgrupoNecesBasicaDAO().obterOriginal(epeSubgrupoNecesBasica);
			if(epeSubgrupoNecesBasica.getSituacao().equals(DominioSituacao.I) && !epeSubgrupoNecesBasica.getSituacao().equals(epeSubgrupoNecesBasicaOriginal.getSituacao())){
				getSubgrupoNecessidadesHumanasRN().verificarDiagnosticoAtivo(epeSubgrupoNecesBasica);
			}else{
				getSubgrupoNecessidadesHumanasRN().verificarGrupoNecessidadeAtivo(epeSubgrupoNecesBasica.getId().getGnbSeq());
			}
			getEpeSubgrupoNecesBasicaDAO().atualizar(epeSubgrupoNecesBasica);
		}
		else{
			//Faz a inserção
			getSubgrupoNecessidadesHumanasRN().verificarGrupoNecessidadeAtivo(seqGrupoNecessidadesHumanas);
			
			Short sequencia = getEpeSubgrupoNecesBasicaDAO().obterMaxSequenciaSubgrupoNecessBasica(seqGrupoNecessidadesHumanas);
			if(sequencia == null || sequencia < 1){
				sequencia = 1;
			}else{
				sequencia++;
			}
			epeSubgrupoNecesBasica.setId(new EpeSubgrupoNecesBasicaId(seqGrupoNecessidadesHumanas, sequencia));
			getEpeSubgrupoNecesBasicaDAO().persistir(epeSubgrupoNecesBasica);
		}
	}

	private void gravarSituacaoSubgrupo(EpeSubgrupoNecesBasica epeSubgrupoNecesBasica, Boolean checkboxSubGrupoAtivo) {
		if(checkboxSubGrupoAtivo){
			epeSubgrupoNecesBasica.setSituacao(DominioSituacao.A);
		}else{
			epeSubgrupoNecesBasica.setSituacao(DominioSituacao.I);
		}
	}

	protected SubgrupoNecessidadesHumanasRN getSubgrupoNecessidadesHumanasRN() {
		return subgrupoNecessidadesHumanasRN;
	}
	
	protected EpeSubgrupoNecesBasicaDAO getEpeSubgrupoNecesBasicaDAO() {
		return epeSubgrupoNecesBasicaDAO;
	}
	
	protected EpeDiagnosticoDAO getEpeDiagnosticoDAO() {
		return epeDiagnosticoDAO;
	}
}
