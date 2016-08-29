package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.dao.MamExtratoTriagemDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamExtratoTriagem;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MamExtratoTriagemRN extends BaseBusiness {

	private static final long serialVersionUID = 1016168490909819304L;

	@Inject
	private MamExtratoTriagemDAO mamExtratoTriagemDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;
		
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}	 		 
	
	/***
	 * @ORADB MAMT_ETT_BRI – Executar antes de inserir na tabela MAM_EXTRATO_TRIAGENS	 
	 * @throws ApplicationBusinessException
	 * RN07
	 */	
	public void preInserirTriagemEncInterno(MamExtratoTriagem mamExtratoTriagem, String hostName) throws ApplicationBusinessException {  	
  		
		if (mamExtratoTriagem.getCriadoEm() == null){
			mamExtratoTriagem.setCriadoEm(new Date());
		}
		
		if (mamExtratoTriagem.getRapServidores() == null){			
			mamExtratoTriagem.setRapServidores((servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo()))));
		}
		mamExtratoTriagem.setMicNome(hostName);		  
	}
	
	public void inserir(MamExtratoTriagem mamExtratoTriagem, String hostName) throws ApplicationBusinessException{
		
		preInserirTriagemEncInterno(mamExtratoTriagem, hostName);
		this.mamExtratoTriagemDAO.persistir(mamExtratoTriagem);		
		
	} 

	

}
