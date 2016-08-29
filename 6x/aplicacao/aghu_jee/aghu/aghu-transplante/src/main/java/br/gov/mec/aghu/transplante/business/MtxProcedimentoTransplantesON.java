package br.gov.mec.aghu.transplante.business;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.model.MtxProcedimentoTransplantes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.dao.MtxProcedimentoTransplantesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class MtxProcedimentoTransplantesON extends BaseBusiness implements Serializable{


	private static final long serialVersionUID = 3963816843129688782L;
	
	private static final Log LOG = LogFactory.getLog(MtxProcedimentoTransplantesON.class);

	@Inject
	private MtxProcedimentoTransplantesDAO mtxProcedimentoTransplantesDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	public enum MtxProcedimentoTransplantesONException implements BusinessExceptionCode {
		REGISTRO_JA_EXISTENTE
	}
	
	
	@Deprecated
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	
	public void adicionarProcedimentoTransplantes(MtxProcedimentoTransplantes mtxProcedimentoTransplantes) throws ApplicationBusinessException{
		if(mtxProcedimentoTransplantes.getOrgao() == DominioTipoOrgao.P){
			List<MtxProcedimentoTransplantes> mtxProcedimentoTransplantesList = this.mtxProcedimentoTransplantesDAO.verificarMtxProcedimentoTransplantes(mtxProcedimentoTransplantes);
			if(mtxProcedimentoTransplantesList.size() > 1){
				throw new ApplicationBusinessException(MtxProcedimentoTransplantesONException.REGISTRO_JA_EXISTENTE);
			}else{
				if(mtxProcedimentoTransplantesList.isEmpty()){
					mtxProcedimentoTransplantes.setServidor(this.servidorLogadoFacade.obterServidorLogado());
					mtxProcedimentoTransplantes.setCriadoEm(new Date());
					this.mtxProcedimentoTransplantesDAO.persistir(mtxProcedimentoTransplantes);
				}else if(mtxProcedimentoTransplantesList.get(0).getIndSituacao() != mtxProcedimentoTransplantes.getIndSituacao()){
					mtxProcedimentoTransplantes.setServidor(this.servidorLogadoFacade.obterServidorLogado());
					mtxProcedimentoTransplantes.setCriadoEm(new Date());
					this.mtxProcedimentoTransplantesDAO.persistir(mtxProcedimentoTransplantes);
				}else{
					throw new ApplicationBusinessException(MtxProcedimentoTransplantesONException.REGISTRO_JA_EXISTENTE);
				}
			}
		}else{
			List<MtxProcedimentoTransplantes> mtxProcedimentoTransplantesList = this.mtxProcedimentoTransplantesDAO.verificarMtxProcedimentoTransplantes(mtxProcedimentoTransplantes);
			if(!mtxProcedimentoTransplantesList.isEmpty()){
				if(mtxProcedimentoTransplantesList.size() != 0){
					throw new ApplicationBusinessException(MtxProcedimentoTransplantesONException.REGISTRO_JA_EXISTENTE);
				}
			}else{
				mtxProcedimentoTransplantes.setServidor(this.servidorLogadoFacade.obterServidorLogado());
				mtxProcedimentoTransplantes.setCriadoEm(new Date());
				this.mtxProcedimentoTransplantesDAO.persistir(mtxProcedimentoTransplantes);
			}
		}
	}
	
	public void excluirProcedimentoTransplantes(MtxProcedimentoTransplantes mtxProcedimentoTransplantes){
		MtxProcedimentoTransplantes entity = this.mtxProcedimentoTransplantesDAO.obterPorChavePrimaria(mtxProcedimentoTransplantes.getSeq());
		if(entity != null){
			this.mtxProcedimentoTransplantesDAO.remover(entity);
			this.mtxProcedimentoTransplantesDAO.flush();
		}
	
	}
	
	public void editarProcedimentoTransplantes(MtxProcedimentoTransplantes mtxProcedimentoTransplantes) throws ApplicationBusinessException{
		if(mtxProcedimentoTransplantes.getOrgao() == DominioTipoOrgao.X){
			List<MtxProcedimentoTransplantes> mtxProcedimentoTransplantesList = this.mtxProcedimentoTransplantesDAO.verificarMtxProcedimentoTransplantes(mtxProcedimentoTransplantes);
			
			if(mtxProcedimentoTransplantesList.size() > 0){
				throw new ApplicationBusinessException(MtxProcedimentoTransplantesONException.REGISTRO_JA_EXISTENTE);
			}else{
				MtxProcedimentoTransplantes entity = this.mtxProcedimentoTransplantesDAO.obterPorChavePrimaria(mtxProcedimentoTransplantes.getSeq());
				if(entity.getIndSituacao() != mtxProcedimentoTransplantes.getIndSituacao()){
					entity.setIndSituacao(mtxProcedimentoTransplantes.getIndSituacao());
					entity.setTipo(mtxProcedimentoTransplantes.getTipo());
					entity.setOrgao(mtxProcedimentoTransplantes.getOrgao());
					entity.setPciSeq(mtxProcedimentoTransplantes.getPciSeq());
					this.mtxProcedimentoTransplantesDAO.atualizar(entity);
				}else{
					throw new ApplicationBusinessException(MtxProcedimentoTransplantesONException.REGISTRO_JA_EXISTENTE);
				}
			}
		}else{
			List<MtxProcedimentoTransplantes> mtxProcedimentoTransplantesList = this.mtxProcedimentoTransplantesDAO.verificarMtxProcedimentoTransplantes(mtxProcedimentoTransplantes);
			if(mtxProcedimentoTransplantesList.size() != 0){
				throw new ApplicationBusinessException(MtxProcedimentoTransplantesONException.REGISTRO_JA_EXISTENTE);
			}else{
				MtxProcedimentoTransplantes entity = this.mtxProcedimentoTransplantesDAO.obterPorChavePrimaria(mtxProcedimentoTransplantes.getSeq());
				entity.setIndSituacao(mtxProcedimentoTransplantes.getIndSituacao());
				entity.setTipo(mtxProcedimentoTransplantes.getTipo());
				entity.setOrgao(mtxProcedimentoTransplantes.getOrgao());
				entity.setPciSeq(mtxProcedimentoTransplantes.getPciSeq());
				this.mtxProcedimentoTransplantesDAO.atualizar(entity);
			}
		}
	}


	public MtxProcedimentoTransplantesDAO getMtxProcedimentoTransplantesDAO() {
		return mtxProcedimentoTransplantesDAO;
	}


	public void setMtxProcedimentoTransplantesDAO(
			MtxProcedimentoTransplantesDAO mtxProcedimentoTransplantesDAO) {
		this.mtxProcedimentoTransplantesDAO = mtxProcedimentoTransplantesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	
}
