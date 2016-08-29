package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.compras.contaspagar.vo.FiltroTipoRespostaConsultoriaVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmTipoRespostaConsultoria;
import br.gov.mec.aghu.model.RapServidores;
/**
 * 
 */
public class MpmTipoRespostaConsultoriaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmTipoRespostaConsultoria> {

	private static final long serialVersionUID = -3759206142565906013L;
	
	public List<MpmTipoRespostaConsultoria> listarTiposRespostasConsultoria(FiltroTipoRespostaConsultoriaVO filtro,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = obterCriteriaTiposRespostasConsultoria(filtro);
		
		criteria.addOrder(Order.asc("MTC." + MpmTipoRespostaConsultoria.Fields.SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarTiposRespostasConsultoriaCount(FiltroTipoRespostaConsultoriaVO filtro) {
		DetachedCriteria criteria = obterCriteriaTiposRespostasConsultoria(filtro);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria obterCriteriaTiposRespostasConsultoria(FiltroTipoRespostaConsultoriaVO filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoRespostaConsultoria.class, "MTC");
		criteria.createAlias("MTC." + MpmTipoRespostaConsultoria.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		if (filtro.getCodigo() != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.SEQ.toString(), filtro.getCodigo()));
		}
		if (filtro.getDescricao() != null) {
			criteria.add(Restrictions.ilike("MTC." + MpmTipoRespostaConsultoria.Fields.DESCRICAO.toString(), filtro.getDescricao(), MatchMode.ANYWHERE));
		}
		if (filtro.getOrdemVisual() != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString(), filtro.getOrdemVisual()));
		}
		if (filtro.getPrimeiraVez() != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_PRIM_VEZ.toString(), filtro.getPrimeiraVez()));
		}
		if (filtro.getRespostaObrigatoria() != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_DIGIT_OBRIGATORIA.toString(), filtro.getRespostaObrigatoria()));
		}
		if (filtro.getAcompanhamento() != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_ACOMPANHAMENTO.toString(), filtro.getAcompanhamento()));
		}
		if (filtro.getRespObrigAcompanhamento() != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_DIGIT_OBRIG_ACOMP.toString(), filtro.getRespObrigAcompanhamento()));
		}
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_SITUACAO.toString(), filtro.getSituacao()));
		}
		return criteria;
	}
	
	public MpmTipoRespostaConsultoria obterMpmRespostaConsultoriaPorSeq(Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoRespostaConsultoria.class, "MTC");
		criteria.createAlias("MTC." + MpmTipoRespostaConsultoria.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.SEQ.toString(), seq));

		return (MpmTipoRespostaConsultoria) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MpmTipoRespostaConsultoria> pesquisarTiposRespostasPorSituacaoPrimeiraVezAcompanhamento(DominioSituacao situacao, 
			Boolean indDigitObrigatoria, Boolean indPrimVez, Boolean indDigitObrigAcomp, Boolean indAcompanhamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoRespostaConsultoria.class, "MTC");
		criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_SITUACAO.toString(), situacao));
		if (indDigitObrigatoria != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_DIGIT_OBRIGATORIA.toString(), indDigitObrigatoria));
		}
		if (indPrimVez != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_PRIM_VEZ.toString(), indPrimVez));
		}
		if (indDigitObrigAcomp != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_DIGIT_OBRIG_ACOMP.toString(), indDigitObrigAcomp));
		}
		if (indAcompanhamento != null) {
			criteria.add(Restrictions.eq("MTC." + MpmTipoRespostaConsultoria.Fields.IND_ACOMPANHAMENTO.toString(), indAcompanhamento));
		}
		return executeCriteria(criteria);
	}
	
	public List<MpmTipoRespostaConsultoria> pesquisarTiposRespostasConsultoria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmTipoRespostaConsultoria.class);
		criteria.add(Restrictions.eq(MpmTipoRespostaConsultoria.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(MpmTipoRespostaConsultoria.Fields.ORDEM_VISUALIZACAO.toString()));
		return executeCriteria(criteria);
	}
}
