package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.vo.AelMotivoCancelaExamesVO;
import br.gov.mec.aghu.model.AelMotivoCancelaExames;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * @author amalmeida
 *
 */
public class AelMotivoCancelaExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelMotivoCancelaExames> {

	
		
	private static final long serialVersionUID = -193142275612597375L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMotivoCancelaExames.class);
		return criteria;
    }
	
	public AelMotivoCancelaExames obterPeloId(Short seq) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.SEQ.toString(), seq));
		return (AelMotivoCancelaExames) executeCriteriaUniqueResult(criteria);
	}

	public Long pesquisarMotivoCancelamentoCount(
			AelMotivoCancelaExames aelMotivoCancelaExames) {
		DetachedCriteria criteria = obterCriteria();
		
		montarRestricao(aelMotivoCancelaExames, criteria);
		
		return executeCriteriaCount(criteria);
	}

	public List<AelMotivoCancelaExames> pesquisarMotivoCancelamento(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AelMotivoCancelaExames aelMotivoCancelaExames) {
		
		DetachedCriteria criteria = obterCriteria();
		
		montarRestricao(aelMotivoCancelaExames, criteria);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		
	}

	private void montarRestricao(AelMotivoCancelaExames aelMotivoCancelaExames,
			DetachedCriteria criteria) {
		if(aelMotivoCancelaExames.getSeq() != null){
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.SEQ.toString(), aelMotivoCancelaExames.getSeq()));
		}
		if(aelMotivoCancelaExames.getDescricao() != null && !aelMotivoCancelaExames.getDescricao().equals("")){
			criteria.add(Restrictions.ilike(AelMotivoCancelaExames.Fields.DESCRICAO.toString(), aelMotivoCancelaExames.getDescricao(),MatchMode.ANYWHERE));
		}
		if(aelMotivoCancelaExames.getIndRetornaAExecutar() != null){
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_RETORNA_A_EXECUTAR.toString(), aelMotivoCancelaExames.getIndRetornaAExecutar()));
		}
		if(aelMotivoCancelaExames.getIndUsoLaboratorio() != null){
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_USO_LABORATORIO.toString(), aelMotivoCancelaExames.getIndUsoLaboratorio()));
		}
		if(aelMotivoCancelaExames.getIndPermiteIncluirResultado() != null){
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_PERMITE_INCLUIR_RESULTADO.toString(), aelMotivoCancelaExames.getIndPermiteIncluirResultado()));
		}
		if(aelMotivoCancelaExames.getIndUsoColeta() != null){
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_USO_COLETA.toString(), aelMotivoCancelaExames.getIndUsoColeta()));
		}
		if(aelMotivoCancelaExames.getIndPermiteComplemento() != null){
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_PERMITE_COMPLEMENTO.toString(), aelMotivoCancelaExames.getIndPermiteComplemento()));
		}
		if(aelMotivoCancelaExames.getIndSituacao() != null){
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_SITUACAO.toString(), aelMotivoCancelaExames.getIndSituacao()));
		}
	}
		
	
	/**
	 * Retorna uma lista de registros <br>
	 * da tabela AEL_MOTIVO_CANCELA_EXAMES <br>
	 * com situação ativa.
	 * 
	 * @return
	 */
	public List<AelMotivoCancelaExamesVO> listarMotivoCancelamentoExamesAtivos(final AelMotivoCancelaExames motivoCancelaExamesFiltro) {
		DetachedCriteria criteria = obterCriteria();
		List<AelMotivoCancelaExamesVO> listReturn = new ArrayList<AelMotivoCancelaExamesVO>();

		montarRestricao(motivoCancelaExamesFiltro, criteria);
		
		criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		criteria.addOrder(Order.asc(AelMotivoCancelaExames.Fields.DESCRICAO.toString()));

		List<AelMotivoCancelaExames> results = this.executeCriteria(criteria);
		
		for (AelMotivoCancelaExames aelMotivoCancelaExames : results) {
			AelMotivoCancelaExamesVO vo = new AelMotivoCancelaExamesVO();
			vo.setCodigo(aelMotivoCancelaExames.getSeq());
			vo.setDescricao(aelMotivoCancelaExames.getDescricao());
			vo.setIndRetornaAExecutar(aelMotivoCancelaExames.getIndRetornaAExecutar());
			
			listReturn.add(vo);
		}

		return listReturn;
	}

	private void montarRestricaoMotivoCancelaExames(DetachedCriteria criteria, Object param, Boolean isCount) {

		if(CoreUtil.isNumeroShort(param)){
			Short mocSeq = Short.parseShort(param.toString());
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.SEQ.toString(), mocSeq));

		}else if(param!=null && !param.toString().trim().equals("")){
			criteria.add(Restrictions.ilike(AelMotivoCancelaExames.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_USO_LABORATORIO.toString(), DominioSimNao.S));
		
		//adicionado melhoria #11756 
		criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(!isCount) {
			criteria.addOrder(Order.asc(AelMotivoCancelaExames.Fields.DESCRICAO.toString()));
		}
	}

	// Metódo para Suggestion Box Motivo de Cancelamento
	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExames(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMotivoCancelaExames.class);
		montarRestricaoMotivoCancelaExames(criteria, param, Boolean.FALSE);
		return executeCriteria(criteria, 0, 100, null, false);
	}
	
	public Long pesquisarMotivoCancelaExamesCount(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMotivoCancelaExames.class);
		montarRestricaoMotivoCancelaExames(criteria, param, Boolean.TRUE);
		return executeCriteriaCount(criteria);
	}
	
	// Metódo para Suggestion Box Motivo de Cancelamento na Coleta
	public List<AelMotivoCancelaExames> pesquisarMotivoCancelaExamesColeta(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelMotivoCancelaExames.class);
		
		if(CoreUtil.isNumeroShort(param)){
			Short mocSeq = Short.parseShort(param.toString());
			criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.SEQ.toString(), mocSeq));

		}else if(param!=null && !param.toString().trim().equals("")){
			criteria.add(Restrictions.ilike(AelMotivoCancelaExames.Fields.DESCRICAO.toString(), param.toString(), MatchMode.ANYWHERE));
		}
		
		//adicionado melhoria #11756 
		criteria.add(Restrictions.eq(AelMotivoCancelaExames.Fields.IND_USO_COLETA.toString(), DominioSimNao.S));
		criteria.addOrder(Order.asc(AelMotivoCancelaExames.Fields.DESCRICAO.toString()));
		
		//
		return executeCriteria(criteria);
	}
	
	
	
}
