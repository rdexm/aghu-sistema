package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AfaItemNptPadrao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.AfaComposicaoNptPadraoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.AfaFormulaNptPadraoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


/**
 * 
 * @ORADB: mpmk_pendente
 * 
 * @author gmneto
 * 
 */
@Stateless
public class ManterFormulaNptPadraoRN extends BaseBusiness {

	
	
	@Inject
	private AfaFormulaNptPadraoDAO afaFormulaNptPadraoDAO;
	
	
	private AfaItemNptPadrao afaItemNptPadrao = new AfaItemNptPadrao();
	
	private AfaComposicaoNptPadraoDAO afaComposicaoNptPadraoDAO;
	
	private static final long serialVersionUID = -4307025924398715560L;

	public enum AtualizarPrescricaoPendenteExceptionCode implements
			BusinessExceptionCode {
		MPM_02065, MPM_02066, MPM_02067, PRESCRICAO_NULA;
	}

	public void criaAfaForumlaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao,RapServidores rapServidores){
		afaFormulaNptPadraoDAO.persistir(aplicaRegra(afaFormulaNptPadrao,rapServidores,false));
		
	}
	
	public void atualizarAfaForumlaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao,RapServidores rapServidores){
		afaFormulaNptPadraoDAO.merge(aplicaRegra(afaFormulaNptPadrao,rapServidores,true));
	}

	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	public AfaFormulaNptPadrao aplicaRegra(AfaFormulaNptPadrao afaFormulaNptPadrao,RapServidores rapServidores,boolean editar){
		
		afaFormulaNptPadrao.setCriadoEm(new Date());	
		afaFormulaNptPadrao.setAlteradoEm(new Date());
		afaFormulaNptPadrao.setRapServidoresByAfaFnpSerFk1(rapServidores);
		afaFormulaNptPadrao.setRapServidoresByAfaFnpSerFk2(rapServidores);
		
		if(editar){
			afaItemNptPadrao.setVersion(1);
		}
		else{
			afaItemNptPadrao.setVersion(0);
		}
		return afaFormulaNptPadrao;
	}


	public AfaFormulaNptPadraoDAO getAfaFormulaNptPadraoDAO() {
		return afaFormulaNptPadraoDAO;
	}



	public void setAfaFormulaNptPadraoDAO(AfaFormulaNptPadraoDAO afaFormulaNptPadraoDAO) {
		this.afaFormulaNptPadraoDAO = afaFormulaNptPadraoDAO;
	}

	public AfaItemNptPadrao getAfaItemNptPadrao() {
		return afaItemNptPadrao;
	}

	public void setAfaItemNptPadrao(AfaItemNptPadrao afaItemNptPadrao) {
		this.afaItemNptPadrao = afaItemNptPadrao;
	}

	public AfaComposicaoNptPadraoDAO getAfaComposicaoNptPadraoDAO() {
		return afaComposicaoNptPadraoDAO;
	}

	public void setAfaComposicaoNptPadraoDAO(AfaComposicaoNptPadraoDAO afaComposicaoNptPadraoDAO) {
		this.afaComposicaoNptPadraoDAO = afaComposicaoNptPadraoDAO;
	}
	

}

