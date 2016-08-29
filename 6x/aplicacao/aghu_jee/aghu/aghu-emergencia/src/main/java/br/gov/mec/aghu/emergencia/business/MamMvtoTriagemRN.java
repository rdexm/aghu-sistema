package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.dao.MamMvtoTriagemDAO;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamMvtoTriagem;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MamMvtoTriagemRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3423984755101821178L;	
   	
	@Inject
	private MamMvtoTriagemDAO mamMvtoTriagemDAO;
		
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@Override
	protected Log getLogger() {
		return null;
	}	 		 
	
	/**
	 * @ORADB MAMT_MVR_BRI - Executar antes de inserir na tabela MAM_MVTO_TRIAGENS.
	 * @param mamMvtoTriagem
	 * @param hostName
	 * @throws ApplicationBusinessException
	 */
	public void preInserirMvtoTriagem(MamMvtoTriagem mamMvtoTriagem) throws ApplicationBusinessException {  	
  		
		if (mamMvtoTriagem.getCriadoEm() == null){
			mamMvtoTriagem.setCriadoEm(new Date());
		}
		
		if (mamMvtoTriagem.getRapServidores() == null){			
			mamMvtoTriagem.setRapServidores(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		}
	}
	
	public void inserir(MamMvtoTriagem mamMvtoTriagem) throws ApplicationBusinessException{
		
		preInserirMvtoTriagem(mamMvtoTriagem);
		this.mamMvtoTriagemDAO.persistir(mamMvtoTriagem);		
	} 

	

}
