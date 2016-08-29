package br.gov.mec.aghu.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghProfissionaisEquipeDAO;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghProfissionaisEquipe;
import br.gov.mec.aghu.model.AghProfissionaisEquipeId;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AghProfissionaisEquipeON extends BaseBusiness {

	private static final long serialVersionUID = 8656289514773118274L;
	
	private static final Log LOG = LogFactory.getLog(AghProfissionaisEquipeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AghProfissionaisEquipeDAO aghProfissionaisEquipeDAO;

	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;

	public void removerAghProfissionaisEquipePorAGHEquipe(Integer equipe) {
		aghProfissionaisEquipeDAO.removerAghProfissionaisEquipePorAGHEquipe(equipe);
	}
	
	public void  inserirAghProfissionaisEquipePorRapServidoresVO(List<RapServidoresVO> profissionaisEquipe, final AghEquipes equipe) {
		for (RapServidoresVO vo : profissionaisEquipe) {
			
			final AghProfissionaisEquipe ape = new AghProfissionaisEquipe(new AghProfissionaisEquipeId(vo.getMatricula(), vo.getVinculo(), 
																										equipe.getSeq()));
			ape.setEquipe(equipe);
			ape.setServidor(getRegistroColaboradorFacade().obterRapServidor(new RapServidoresId(vo.getMatricula(), vo.getVinculo())));
			
			aghProfissionaisEquipeDAO.persistir(ape);
		}
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}
}
