package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.dao.MamTrgPrevAtendDAO;
import br.gov.mec.aghu.emergencia.dao.MamTrgPrevAtendjnDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamTrgPrevAtend;
import br.gov.mec.aghu.model.MamTrgPrevAtendJn;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MamTrgPrevAtendRN extends BaseBusiness {

	private static final long serialVersionUID = -6837230253784788443L;

	@Inject
	private MamTrgPrevAtendDAO mamTrgPrevAtendDAO;
	
	@Inject
	private MamTrgPrevAtendjnDAO mamTrgPrevAtendjnDAO;
		
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}	 		 
	
	/***
	 * @ORADB MAM_TRG_PREV_ATENDS.MAMT_TPV_BRI – Executar antes de inserir na tabela MAM_TRG_PREV_ATENDS	 
	 * @throws ApplicationBusinessException
	 * RN15
	 */	
	public void preInserirPrevisaoAtendimento(MamTrgPrevAtend mamTrgPrevAtend) throws ApplicationBusinessException {  	
  		
		
		mamTrgPrevAtend.setSerVinCodigo(usuario.getVinculo());
		mamTrgPrevAtend.setSerMatricula(usuario.getMatricula());
		mamTrgPrevAtend.setGeradoEm(new Date());
	}
	
	public void inserirPrevisaoAtendimento(MamTrgPrevAtend mamTrgPrevAtend) throws ApplicationBusinessException{
		
		preInserirPrevisaoAtendimento(mamTrgPrevAtend);
		this.mamTrgPrevAtendDAO.persistir(mamTrgPrevAtend);
		
	} 
	
    public void atualizarPrevisaoAtendimento(MamTrgPrevAtend mamTrgPrevAtend) throws ApplicationBusinessException{
		
    	    	
    	MamTrgPrevAtend mamTrgPrevAtendOriginal = this.mamTrgPrevAtendDAO.obterPorChavePrimaria(mamTrgPrevAtend.getSeq());
    	
    	if (CoreUtil.modificados(mamTrgPrevAtend.getDthrPrevAtend(), mamTrgPrevAtendOriginal.getDthrPrevAtend()) ||
    		CoreUtil.modificados(mamTrgPrevAtend.getIndImediato(), mamTrgPrevAtendOriginal.getIndImediato()) ||
    		CoreUtil.modificados(mamTrgPrevAtend.getGeradoEm(), mamTrgPrevAtendOriginal.getGeradoEm()) ||	
    		CoreUtil.modificados(mamTrgPrevAtend.getTriagem(), mamTrgPrevAtendOriginal.getTriagem()) ||
    		CoreUtil.modificados(mamTrgPrevAtend.getSerVinCodigo(), mamTrgPrevAtendOriginal.getSerVinCodigo()) ||
    		CoreUtil.modificados(mamTrgPrevAtend.getSerMatricula(), mamTrgPrevAtendOriginal.getSerMatricula())) {
    		
    		MamTrgPrevAtendJn mamTrgPrevAtendJn = new MamTrgPrevAtendJn();
    		
    		mamTrgPrevAtendJn.setOperacao(DominioOperacoesJournal.UPD);
    		mamTrgPrevAtendJn.setDthrPrevAtend(mamTrgPrevAtendOriginal.getDthrPrevAtend());
    		mamTrgPrevAtendJn.setGeradoEm(mamTrgPrevAtendOriginal.getGeradoEm());
    		mamTrgPrevAtendJn.setIndImediato(mamTrgPrevAtendOriginal.getIndImediato());
    		mamTrgPrevAtendJn.setTriagem(mamTrgPrevAtendOriginal.getTriagem());
    		mamTrgPrevAtendJn.setSeq(mamTrgPrevAtendOriginal.getSeq());
    		mamTrgPrevAtendJn.setSerVinCodigo(mamTrgPrevAtendOriginal.getSerVinCodigo());
    		mamTrgPrevAtendJn.setSerMatricula(mamTrgPrevAtendOriginal.getSerMatricula());
    		
    		this.mamTrgPrevAtendjnDAO.persistir(mamTrgPrevAtendJn);
    	}
		
		this.mamTrgPrevAtendDAO.atualizar(mamTrgPrevAtend);
		
	} 

	

}
