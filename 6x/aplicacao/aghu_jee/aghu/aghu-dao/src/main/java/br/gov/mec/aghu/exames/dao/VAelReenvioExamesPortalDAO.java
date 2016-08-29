package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;

import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameReenvioGrupoVO;
import br.gov.mec.aghu.view.VAelReenvioExamesPortal;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class VAelReenvioExamesPortalDAO extends BaseDao<VAelReenvioExamesPortal> {

	private static final long serialVersionUID = 3257162530007732360L;
	
	public List<MensagemSolicitacaoExameReenvioGrupoVO> buscarExamesParaReenvioDaView(int maxResults) {

		DetachedCriteria criteria = DetachedCriteria.forClass(VAelReenvioExamesPortal.class);
		
		List<VAelReenvioExamesPortal> list = executeCriteria(criteria, 0, maxResults, null, true);
		List<MensagemSolicitacaoExameReenvioGrupoVO> retorno = new ArrayList<MensagemSolicitacaoExameReenvioGrupoVO>();
		for (VAelReenvioExamesPortal entidade : list) {
			MensagemSolicitacaoExameReenvioGrupoVO vo = new MensagemSolicitacaoExameReenvioGrupoVO();
			vo.setGeiSeq(entidade.getGeiSeq());
			vo.setSoeSeq(entidade.getSoeSeq());
			vo.setReenviarEm(entidade.getReenviarEm());
			retorno.add(vo);
		}
		
		return retorno;
	}

}
