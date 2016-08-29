package br.gov.mec.aghu.transplante.business;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelCampoLaudoDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.model.MtxExameUltResults;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.transplante.dao.MtxExameUltResultsDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MtxExameUltResultsRN extends BaseBusiness implements Serializable{

	private static final long serialVersionUID = -7874858792542263114L;

	private static final Log LOG = LogFactory.getLog(MtxExameUltResultsRN.class);
	
	public enum MtxExameUltResultsRNException implements BusinessExceptionCode {
		REGISTRO_DUPLICADO
	}
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject 
	private MtxExameUltResultsDAO mtxExameUltResultsDAO;
	
	@Inject
	private AelExamesDAO aelExamesDAO;
	
	@Inject 
	private AelCampoLaudoDAO aelCampoLaudoDAO;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	public void inserirMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException {
		
		if(verificarExistenciaExameLaudo(mtxExameUltResults)){
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			
			mtxExameUltResults.setAelExames(aelExamesDAO.obterPorChavePrimaria(mtxExameUltResults.getAelExames().getSigla()));
			mtxExameUltResults.setCampoLaudo(aelCampoLaudoDAO.obterPorChavePrimaria(mtxExameUltResults.getCampoLaudo().getSeq()));			
			mtxExameUltResults.setCriadoEm(new Date());
			mtxExameUltResults.setServidor(servidorLogado);
			
			mtxExameUltResultsDAO.persistir(mtxExameUltResults);
		}else{
			throw new ApplicationBusinessException(MtxExameUltResultsRNException.REGISTRO_DUPLICADO);
		}
	}
	
	public void atualizarMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException {
		
		if(verificarExistenciaExameLaudo(mtxExameUltResults)){
			mtxExameUltResults.setAelExames(aelExamesDAO.obterPorChavePrimaria(mtxExameUltResults.getAelExames().getSigla()));
			mtxExameUltResults.setCampoLaudo(aelCampoLaudoDAO.obterPorChavePrimaria(mtxExameUltResults.getCampoLaudo().getSeq()));
			
			mtxExameUltResultsDAO.merge(mtxExameUltResults);
			mtxExameUltResultsDAO.flush(); 
		}else{
			throw new ApplicationBusinessException(MtxExameUltResultsRNException.REGISTRO_DUPLICADO);
		}
	}
	
	public void excluirMtxExameUltResults(MtxExameUltResults mtxExameUltResults) throws ApplicationBusinessException{
		mtxExameUltResults = mtxExameUltResultsDAO.obterPorChavePrimaria(mtxExameUltResults.getSeq());
		mtxExameUltResultsDAO.remover(mtxExameUltResults);
		mtxExameUltResultsDAO.flush();
	}
	
	private Boolean verificarExistenciaExameLaudo(MtxExameUltResults mtxExameUltResults){
		String sigla = mtxExameUltResults.getAelExames().getSigla();
		Integer seqLaudo = mtxExameUltResults.getCampoLaudo().getSeq();
		Integer seq = mtxExameUltResults.getSeq(); 
		
		MtxExameUltResults existe = mtxExameUltResultsDAO.obterMtxExameUltResultsExameLaudo(sigla, seqLaudo);
		
		if(existe != null) {
			if((existe.getSeq() != seq)){
				return false;
			}
		}
		return true;
	}

}
