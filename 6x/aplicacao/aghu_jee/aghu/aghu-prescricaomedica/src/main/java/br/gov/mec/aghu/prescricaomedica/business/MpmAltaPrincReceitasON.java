package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.model.MpmAltaPrincReceitas;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPrincReceitasDAO;

@Stateless
public class MpmAltaPrincReceitasON  extends BaseBusiness{

	private static final long serialVersionUID = 1863753908426790353L;

	private static final Log LOG = LogFactory.getLog(MpmAltaPrincReceitasON.class);
	
	@Inject
	private MpmAltaPrincReceitasDAO mpmAltaPrincReceitasDAO;

	
	public void removerAltaPrincReceitas(MpmAltaSumario mpmAltaSumario) {
		
		List<MpmAltaPrincReceitas> mpmAltaPrincReceitas = getMpmAltaPrincReceitasDAO().obterAltaPrincReceitasPorAltaSumario(mpmAltaSumario);
		
		if(mpmAltaPrincReceitas != null && mpmAltaPrincReceitas.size() > 0){
			for(MpmAltaPrincReceitas mpmAltaPrincReceita : mpmAltaPrincReceitas){
				getMpmAltaPrincReceitasDAO().remover(mpmAltaPrincReceita);
			}
		}
	}
	
	protected MpmAltaPrincReceitasDAO getMpmAltaPrincReceitasDAO() {
		return mpmAltaPrincReceitasDAO;
	}
	
	protected void setMpmAltaPrincReceitasDAO(MpmAltaPrincReceitasDAO mpmAltaPrincReceitasDAO) {
		this.mpmAltaPrincReceitasDAO = mpmAltaPrincReceitasDAO;
	}

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
}
