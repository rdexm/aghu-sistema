package br.gov.mec.aghu.transplante.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.MtxCriterioPriorizacaoTmo;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.transplante.dao.MtxCriterioPriorizacaoTmoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MtxCriterioPriorizacaoTmoRN extends BaseBusiness {


	private static final long serialVersionUID = -1394004736077551727L;
	
	@Inject
	private MtxCriterioPriorizacaoTmoDAO mtxCriterioPriorizacaoTmoDAO;
	
    @EJB
    protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	public boolean verificarExistenciaRegistro(Integer cidSeq){
		if(mtxCriterioPriorizacaoTmoDAO.verificarExistenciaRegistro(cidSeq) != null){
			return true;
		}else{
			return false;
		}
	}	
	public void gravarUsuarioDataCriacao(MtxCriterioPriorizacaoTmo mtxCriterioPriorizacaoTmo) throws ApplicationBusinessException{
		
		if(mtxCriterioPriorizacaoTmo != null){
			mtxCriterioPriorizacaoTmo.setServidor(obterUsuariologado());
			mtxCriterioPriorizacaoTmo.setCriadoEm(new Date());
			mtxCriterioPriorizacaoTmoDAO.persistir(mtxCriterioPriorizacaoTmo);
		}
	} 
	
	public void atualizarMtxCriterioPriorizacaoTmo(MtxCriterioPriorizacaoTmo mtxCriterioPriorizacaoTmo) throws ApplicationBusinessException{
		
		if(mtxCriterioPriorizacaoTmo != null){
			MtxCriterioPriorizacaoTmo mtxCriterioPriorizacaoTmoOriginal = preUpdate(mtxCriterioPriorizacaoTmo);
			mtxCriterioPriorizacaoTmoDAO.merge(mtxCriterioPriorizacaoTmoOriginal);
		}
	}

	private MtxCriterioPriorizacaoTmo preUpdate(MtxCriterioPriorizacaoTmo mtxCriterioPriorizacaoTmo) throws ApplicationBusinessException {
		MtxCriterioPriorizacaoTmo mtxCriterioPriorizacaoTmoOriginal = mtxCriterioPriorizacaoTmoDAO.obterPorChavePrimaria(mtxCriterioPriorizacaoTmo.getSeq());
		if(mtxCriterioPriorizacaoTmo.getCriticidade() != null){
			mtxCriterioPriorizacaoTmoOriginal.setCriticidade(mtxCriterioPriorizacaoTmo.getCriticidade());
		}
		if(mtxCriterioPriorizacaoTmo.getGravidade() != null){
			mtxCriterioPriorizacaoTmoOriginal.setGravidade(mtxCriterioPriorizacaoTmo.getGravidade());
		}
		if(mtxCriterioPriorizacaoTmo.getIndSituacao() != null){
			mtxCriterioPriorizacaoTmoOriginal.setIndSituacao(mtxCriterioPriorizacaoTmo.getIndSituacao());
		}
		if(mtxCriterioPriorizacaoTmo.getTipoTmo() != null){
			mtxCriterioPriorizacaoTmoOriginal.setTipoTmo(mtxCriterioPriorizacaoTmo.getTipoTmo());
		}
		if(mtxCriterioPriorizacaoTmo.getStatus() != null){
			mtxCriterioPriorizacaoTmoOriginal.setStatus(mtxCriterioPriorizacaoTmo.getStatus());
		}
		if(obterUsuariologado()!= null){
			mtxCriterioPriorizacaoTmoOriginal.setServidor(obterUsuariologado());
		}
		return mtxCriterioPriorizacaoTmoOriginal;
	} 
	
	public RapServidores obterUsuariologado() throws ApplicationBusinessException{
		return registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
	
	}	

}
