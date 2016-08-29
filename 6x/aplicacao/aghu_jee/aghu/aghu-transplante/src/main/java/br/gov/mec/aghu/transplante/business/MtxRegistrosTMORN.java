package br.gov.mec.aghu.transplante.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MtxRegistrosTMO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.transplante.dao.MtxRegistrosTMODAO;
import br.gov.mec.aghu.transplante.dao.MtxTransplantesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MtxRegistrosTMORN extends BaseBusiness {

	private static final long serialVersionUID = 946077497578078336L;
	
	private static final Log LOG = LogFactory.getLog(MtxRegistrosTMORN.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private MtxRegistrosTMODAO mtxRegistrosTMODAO;
	
	@Inject 
	private MtxTransplantesDAO mtxTransplantesDAO; 
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	public void persistir(MtxRegistrosTMO mtxRegistrosTMO) throws ApplicationBusinessException{
		RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());		
		
		mtxRegistrosTMO.setMtxTransplante(mtxTransplantesDAO.obterPorChavePrimaria(mtxRegistrosTMO.getMtxTransplante().getSeq()));
		mtxRegistrosTMO.setDescricao(mtxRegistrosTMO.getDescricao().trim());
		mtxRegistrosTMO.setServidor(servidor);
		mtxRegistrosTMO.setCriadoEm(new Date());
		
		if(mtxRegistrosTMO.getSeq() == null){
			mtxRegistrosTMODAO.persistir(mtxRegistrosTMO);
		}else{
			mtxRegistrosTMODAO.merge(mtxRegistrosTMO);
			mtxRegistrosTMODAO.flush();
		}
	}	
}
