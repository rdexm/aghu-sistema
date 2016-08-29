package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.MamTrgEncInternoDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.MamTrgEncInterno;
import br.gov.mec.aghu.model.MamTrgEncInternoId;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class MamTrgEncInternoRN extends BaseBusiness {

	private static final long serialVersionUID = -276588332066080964L;


	@Inject
	private MamTrgEncInternoDAO mamTrgEncInternoDAO;
	/*
	@EJB
	private IConfiguracaoService configuracaoService;*/
	
	@EJB
	private IAghuFacade aghuFacede;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@Inject
	private AacConsultasDAO consultaDAO;
		
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	/***
	 * @ORADB MAM_TRG_ENC_INTERNOS.MAMT_TEI_BRI – Trigger before insert
	 * @param mamUnidAtendeEsp
	 * @throws ApplicationBusinessException
	 * RN07
	 */
	
	public void preInserirTriagemEncInterno(MamTrgEncInterno mamTrgEncInterno, String hostName) throws ApplicationBusinessException {  	
  		mamTrgEncInterno.setCriadoEm(new Date());
		mamTrgEncInterno.setIndImpresso(Boolean.FALSE);		
		mamTrgEncInterno.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
		mamTrgEncInterno.setMicNome(hostName);		  
	 }

	
	 public void inserirTriagemEncInterno(Long seqTriagem, Integer consultaNumero, String hostName) throws ApplicationBusinessException {	  	
	  		
		    MamTrgEncInternoId mamTrgEncInternoId = new MamTrgEncInternoId(seqTriagem, this.mamTrgEncInternoDAO.obterMaxSeqPTriagem(seqTriagem));
		 
		    MamTrgEncInterno mamTrgEncInterno = new MamTrgEncInterno();
		    
		    mamTrgEncInterno.setId(mamTrgEncInternoId);
		    
		    mamTrgEncInterno.setConsulta(consultaDAO.obterConsulta(consultaNumero));
		    
		    preInserirTriagemEncInterno(mamTrgEncInterno,hostName);
		    
		    this.mamTrgEncInternoDAO.persistir(mamTrgEncInterno);
	}

	public Boolean imprimirEmitirBoletimAtendimento(Short unfSeq) throws ServiceException {
		 return this.aghuFacede.verificarCaracteristicaUnidadeFuncional(unfSeq, ConstanteAghCaractUnidFuncionais.IMPRIME_BA_EMERGENCIA);
	}

}
