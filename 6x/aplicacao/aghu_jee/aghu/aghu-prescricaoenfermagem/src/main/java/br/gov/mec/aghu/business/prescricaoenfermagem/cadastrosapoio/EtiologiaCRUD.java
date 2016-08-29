package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeFatRelDiagnostico;
import br.gov.mec.aghu.model.EpeFatRelacionado;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeFatRelacionadoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class EtiologiaCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(EtiologiaCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeFatRelacionadoDAO epeFatRelacionadoDAO;

@Inject
private EpeFatRelDiagnosticoDAO epeFatRelDiagnosticoDAO;	

	private static final long serialVersionUID = 2969601492841136788L;
	
	public enum EtiologiaCRUDExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DESATIVAR_DIAGNOSTICO_RELACIONADO, MENSAGEM_EXCLUIR_DIAGNOSTICO_RELACIONADO, MENSAGEM_ETIOLOGIA_JA_EXISTENTE
	}

	public void removerEtiologia(Short seq) throws ApplicationBusinessException {
		EpeFatRelacionado etiologia = epeFatRelacionadoDAO.obterPorChavePrimaria(seq);
		verificarDiagnosticoRelacionadoAtivo(etiologia, false);	
		epeFatRelacionadoDAO.remover(etiologia);
	}
	
	private void verificarDiagnosticoRelacionadoAtivo(EpeFatRelacionado etiologia, Boolean acao) throws ApplicationBusinessException {
		DominioSituacao dominio = null;
		if (acao) {
			dominio = DominioSituacao.A;
		}
		List<EpeFatRelDiagnostico> fatRelDiagnostico = epeFatRelDiagnosticoDAO.pesquisarDiagnosticoPorSeq(etiologia.getSeq(), dominio);
		if (!fatRelDiagnostico.isEmpty()){ 
			throw new ApplicationBusinessException(acao ? EtiologiaCRUDExceptionCode.MENSAGEM_DESATIVAR_DIAGNOSTICO_RELACIONADO : EtiologiaCRUDExceptionCode.MENSAGEM_EXCLUIR_DIAGNOSTICO_RELACIONADO);
		}			
	}

	public void persistirEtiologia(EpeFatRelacionado etiologia, Boolean ativo) throws ApplicationBusinessException{

		//Verificar se o registro já existe com a mesma desrição 
		List<EpeFatRelacionado> etList = epeFatRelacionadoDAO.pesquisarEtiologias(etiologia.getDescricao());
		if (etList!=null && etList.size()>0){
			for (EpeFatRelacionado epeEtiol : etList){
				if (!epeEtiol.getSeq().equals(etiologia.getSeq())){
					throw new ApplicationBusinessException(EtiologiaCRUDExceptionCode.MENSAGEM_ETIOLOGIA_JA_EXISTENTE);
				}
			}
		}	
		
		if (etiologia.getSeq() == null){
			//Faz a inserção	
			setarDominioSituacao(etiologia, ativo);
			epeFatRelacionadoDAO.persistir(etiologia);
		}
		else{	
			//Faz a atualização	
			if(etiologia.getSituacao().isAtivo() && !ativo){				
				verificarDiagnosticoRelacionadoAtivo(etiologia, true);					
			}			
			setarDominioSituacao(etiologia, ativo);
			epeFatRelacionadoDAO.merge(etiologia);
		}
	}
	
	private void setarDominioSituacao(EpeFatRelacionado etiologia, Boolean ativo) {
		if (ativo){
			etiologia.setSituacao(DominioSituacao.A);
		}else{
			etiologia.setSituacao(DominioSituacao.I);
		}		
	}	
	
}
