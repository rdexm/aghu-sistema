package br.gov.mec.aghu.exames.pesquisa.business;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.dao.AelExameGrupoCaracteristicaDAO;
import br.gov.mec.aghu.exames.dao.AelExamesDAO;
import br.gov.mec.aghu.exames.dao.AelExamesMaterialAnaliseDAO;
import br.gov.mec.aghu.exames.vo.RelatorioCaracteristicasResultadosPorExameVO;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesMaterialAnaliseId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioCaracteristicasResultadosPorExameON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioCaracteristicasResultadosPorExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelExamesMaterialAnaliseDAO aelExamesMaterialAnaliseDAO;

@Inject
private AelExameGrupoCaracteristicaDAO aelExameGrupoCaracteristicaDAO;

@Inject
private AelExamesDAO aelExamesDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7927498350609129296L;

	public List<RelatorioCaracteristicasResultadosPorExameVO> pesquisaCaracteristicasResultadosPorExame(String siglaExame, Integer manSeq) throws ApplicationBusinessException {

		List<RelatorioCaracteristicasResultadosPorExameVO> retorno = new ArrayList<RelatorioCaracteristicasResultadosPorExameVO>();
		AelExameGrupoCaracteristicaDAO dao = getAelExameGrupoCaracteristicaDAO();
		List<Object[]> listRel = dao.pesquisarRelatorioCaracteristicasResultados(siglaExame, manSeq);

		Iterator<Object[]> it = listRel.iterator();
		RelatorioCaracteristicasResultadosPorExameVO vo = null;
		
		AelExames exame = getAelExamesDAO().obterPeloId(siglaExame);
		
		AelExamesMaterialAnaliseId exameMatAnaliseId = new AelExamesMaterialAnaliseId();
		exameMatAnaliseId.setExaSigla(siglaExame);
		exameMatAnaliseId.setManSeq(manSeq);
		AelExamesMaterialAnalise exameMatAnalise = getAelExamesMaterialAnaliseDAO().obterPorChavePrimaria(exameMatAnaliseId);

		while (it.hasNext()) {

			Object[] obj = it.next();
			vo = new RelatorioCaracteristicasResultadosPorExameVO();
			
			if(exame!=null){
				vo.setSiglaExame(exame.getSigla());
				vo.setDescricaoUsualExame(exame.getDescricaoUsual());
			}
			
			if(exameMatAnalise!=null){
				vo.setManSeq(exameMatAnalise.getId().getManSeq());
				vo.setManDescricao(exameMatAnalise.getNomeUsualMaterial());
			}
			
			
			if (obj[0] != null) {
				vo.setEgcGcaSeq((Integer)obj[0]);
			}
			
			if (obj[1] != null) {
				vo.setCodigoFalante((Integer)obj[1]);
			}
			
			if (obj[2] != null) {
				vo.setGcaSeq((Integer)obj[2]);
			}
			
			if (obj[3] != null) {
				vo.setGcaDdescricao((String)obj[3]);
			}
			
			if (obj[4] != null) {
				vo.setOrdemImpressao((Integer)obj[4]);
			}
			
			if (obj[5] != null) {
				vo.setCacSeq((Integer)obj[5]);
			}
			
			if (obj[6] != null) {
				vo.setCacDescricao((String)obj[6]);
			}
			
			
			retorno.add(vo);
			
		}
		
		
		
		return retorno;
	}

	private AelExameGrupoCaracteristicaDAO getAelExameGrupoCaracteristicaDAO() {
		return aelExameGrupoCaracteristicaDAO;
	}

	protected AelExamesDAO getAelExamesDAO() {
		return aelExamesDAO;
	}
	

	protected AelExamesMaterialAnaliseDAO getAelExamesMaterialAnaliseDAO() {
		return aelExamesMaterialAnaliseDAO;
	}

}
