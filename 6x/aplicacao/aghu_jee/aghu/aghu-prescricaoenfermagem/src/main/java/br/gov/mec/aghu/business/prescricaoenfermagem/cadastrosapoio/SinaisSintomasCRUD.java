package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCaractDefDiagnosticoDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeCaractDefinidoraDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSinCaractDefinidoraDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class SinaisSintomasCRUD extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SinaisSintomasCRUD.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private EpeCaractDefDiagnosticoDAO epeCaractDefDiagnosticoDAO;

@Inject
private EpeSinCaractDefinidoraDAO epeSinCaractDefinidoraDAO;

@Inject
private EpeCaractDefinidoraDAO epeCaractDefinidoraDAO;	

	private static final long serialVersionUID = 2969601492841136788L;
	
	public enum SinaisSintomasCRUDExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SINAIS_SINTOMAS_ERRO_EXCLUSAO_EPE_SIN_CARACT_DEFINIDORAS, 
		MENSAGEM_SINAIS_SINTOMAS_ERRO_EXCLUSAO_EPE_CARACT_DEF_DIAGNOSTICOS,
		MENSAGEM_SINAIS_SINTOMAS_JA_EXISTENTE
	}

	public void verificaExclusaoEpeCaractDefinidora(EpeCaractDefinidora epeCaractDefinidora) throws ApplicationBusinessException {
		if (getEpeSinCaractDefinidoraDAO().possuiCaractDefinidora(epeCaractDefinidora)) {
			throw new ApplicationBusinessException(SinaisSintomasCRUDExceptionCode.MENSAGEM_SINAIS_SINTOMAS_ERRO_EXCLUSAO_EPE_SIN_CARACT_DEFINIDORAS);
		}
		if (getEpeCaractDefDiagnosticoDAO().possuiEpeCaractDefinidora(epeCaractDefinidora)) {
			throw new ApplicationBusinessException(SinaisSintomasCRUDExceptionCode.MENSAGEM_SINAIS_SINTOMAS_ERRO_EXCLUSAO_EPE_CARACT_DEF_DIAGNOSTICOS);
		}
	}
	
	public void persistir(EpeCaractDefinidora sinaisSintomas) throws ApplicationBusinessException{

		List<EpeCaractDefinidora> ssiList = getEpeCaractDefinidoraDAO().listarSinaisSintomasPorCodigoDescricaoSituacao(Integer.valueOf(0), Integer.valueOf(10),
												EpeCaractDefinidora.Fields.DESCRICAO.toString() , true, null, sinaisSintomas.getDescricao(), null);

		if (ssiList!=null && ssiList.size()>0){
			for (EpeCaractDefinidora epeCaraDef : ssiList){
				if (!epeCaraDef.getCodigo().equals(sinaisSintomas.getCodigo()) && epeCaraDef.getDescricao().equals(sinaisSintomas.getDescricao())){
					throw new ApplicationBusinessException(SinaisSintomasCRUDExceptionCode.MENSAGEM_SINAIS_SINTOMAS_JA_EXISTENTE);
				}
			}	
		}

        if(sinaisSintomas.getCodigo() == null) {
            getEpeCaractDefinidoraDAO().persistir(sinaisSintomas);
        } else {
            getEpeCaractDefinidoraDAO().merge(sinaisSintomas);
        }

	}

	
	protected EpeSinCaractDefinidoraDAO getEpeSinCaractDefinidoraDAO() {
		return epeSinCaractDefinidoraDAO;
	}
	
	protected EpeCaractDefDiagnosticoDAO getEpeCaractDefDiagnosticoDAO() {
		return epeCaractDefDiagnosticoDAO;
	}
	
	protected EpeCaractDefinidoraDAO getEpeCaractDefinidoraDAO() {
		return epeCaractDefinidoraDAO;
	}

	
}
