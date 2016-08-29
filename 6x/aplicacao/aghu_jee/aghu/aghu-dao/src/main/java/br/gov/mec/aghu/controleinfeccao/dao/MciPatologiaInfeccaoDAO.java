package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.DoencaInfeccaoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciDuracaoMedidaPreventiva;
import br.gov.mec.aghu.model.MciPalavraChavePatologia;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;

public class MciPatologiaInfeccaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciPatologiaInfeccao> {

	private static final long serialVersionUID = 4582827701522493568L;

	// #36265 C3
	public List<MciPatologiaInfeccao> obterPatologiaInfeccoesPorDmpSeq(Short dmpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciPatologiaInfeccao.class);
		criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.DMP_SEQ.toString(), dmpSeq));
		return this.executeCriteria(criteria);
	}
	
	public List<MciPatologiaInfeccao> pesquisarPatologiaInfeccao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MciPatologiaInfeccao patologiaInfeccao) {
		DetachedCriteria criteria = montaCriteriaMciPatologiaInfeccao(patologiaInfeccao);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarPatologiaInfeccaoCount(MciPatologiaInfeccao patologiaInfeccao) {
		DetachedCriteria criteria = montaCriteriaMciPatologiaInfeccao(patologiaInfeccao);
		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria montaCriteriaMciPatologiaInfeccao(final MciPatologiaInfeccao patologiaInfeccao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciPatologiaInfeccao.class);
		
		criteria.createAlias(MciPatologiaInfeccao.Fields.TOPOGRAFIA_INFECCAO.toString(), "TOPO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MciPatologiaInfeccao.Fields.DURACAO_MEDIDA_PREVENTIVA.toString(), "DURA", JoinType.LEFT_OUTER_JOIN);
		
		// Evita NPath complexity
		montaCriteriaMciPatologiaInfeccaoParte1(criteria, patologiaInfeccao);
		montaCriteriaMciPatologiaInfeccaoParte2(criteria, patologiaInfeccao);
		
		return criteria;
	}

	private void montaCriteriaMciPatologiaInfeccaoParte1(DetachedCriteria criteria, final MciPatologiaInfeccao patologiaInfeccao) {
		if (StringUtils.isNotBlank(patologiaInfeccao.getDescricao())) {
			criteria.add(Restrictions.ilike(MciPatologiaInfeccao.Fields.DESCRICAO.toString(), patologiaInfeccao.getDescricao(), MatchMode.ANYWHERE));
		}

		if (patologiaInfeccao.getSituacao() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.SITUACAO.toString(), patologiaInfeccao.getSituacao()));
		}

		if (patologiaInfeccao.getNotificaSsma() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.NOTIFICA_SSMA.toString(), patologiaInfeccao.getNotificaSsma()));
		}

		if (patologiaInfeccao.getImpNotificacao() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.IMP_NOTIFICACAO.toString(), patologiaInfeccao.getImpNotificacao()));
		}

		if (patologiaInfeccao.getHigienizacaoMaos() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.HIGIENIZACAO_MAOS.toString(), patologiaInfeccao.getHigienizacaoMaos()));
		}

		if (patologiaInfeccao.getUsoQuartoPrivativo() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.IND_QUARTO_USO_PRIVATIVO.toString(), patologiaInfeccao.getUsoQuartoPrivativo()));
		}

		if (patologiaInfeccao.getUsoMascara() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.USO_MASCARA.toString(), patologiaInfeccao.getUsoMascara()));
		}
	}

	private void montaCriteriaMciPatologiaInfeccaoParte2(DetachedCriteria criteria, final MciPatologiaInfeccao patologiaInfeccao) {

		if (patologiaInfeccao.getUsoMascaraN95() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.USO_MASCARA_N95.toString(), patologiaInfeccao.getUsoMascaraN95()));
		}

		if (patologiaInfeccao.getUsoOculos() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.USO_OCULOS.toString(), patologiaInfeccao.getUsoOculos()));
		}

		if (patologiaInfeccao.getUsoAvental() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.USO_AVENTAL.toString(), patologiaInfeccao.getUsoAvental()));
		}

		if (patologiaInfeccao.getTecnicaAsseptica() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.TECNICA_ASSEPTICA.toString(), patologiaInfeccao.getTecnicaAsseptica()));
		}

		if (patologiaInfeccao.getTecnicaAsseptica() != null) {
			criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.TECNICA_ASSEPTICA.toString(), patologiaInfeccao.getTecnicaAsseptica()));
		}

		if (patologiaInfeccao.getTopografiaInfeccao() != null) {
			criteria.add(Restrictions.eq("TOPO." + MciTopografiaInfeccao.Fields.SEQ.toString(), patologiaInfeccao.getTopografiaInfeccao().getSeq()));
		}

		if (patologiaInfeccao.getDuracaoMedidaPreventiva() != null) {
			criteria.add(Restrictions.eq("DURA." + MciDuracaoMedidaPreventiva.Fields.SEQ.toString(), patologiaInfeccao.getDuracaoMedidaPreventiva().getSeq()));
		}

	}
	
	public List<MciPatologiaInfeccao> listarPorToiSeq(Short toiSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciPatologiaInfeccao.class);
		criteria.add(Restrictions.eq(MciPatologiaInfeccao.Fields.TOPOGRAFIA_INFECCAO_SEQ.toString(), toiSeq));
		return this.executeCriteria(criteria);
	}
	
	//#1297 - C2 
	public List<DoencaInfeccaoVO> buscarDoencaInfeccao(String param) {
		DetachedCriteria criteria = montarCriteriaDoencaInfeccao(param);
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	//#1297 - C2 
	public Long buscarDoencaInfeccaoCount(String param) {
		DetachedCriteria criteria = montarCriteriaDoencaInfeccao(param);
		return executeCriteriaCount(criteria);
	}
	
	public List<DoencaInfeccaoVO> buscarDoencaInfeccaoPaiChaveAtivos(String param) {
		DetachedCriteria criteria = montarCriteriaDoencaInfeccao(param);
		criteria.add(Restrictions.eq("PCP." + MciPalavraChavePatologia.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("PCP."+ MciPalavraChavePatologia.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("PAI."+ MciPatologiaInfeccao.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("PAI."+ MciPatologiaInfeccao.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	public Long buscarDoencaInfeccaoPaiChaveAtivosCount(Object param) {
		DetachedCriteria criteria = montarCriteriaDoencaInfeccao(param.toString());
		criteria.add(Restrictions.eq("PCP." + MciPalavraChavePatologia.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria montarCriteriaDoencaInfeccao(String param){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciPatologiaInfeccao.class, "PAI");
		criteria.createAlias("PAI." + MciPatologiaInfeccao.Fields.PALAVRAS_CHAVES.toString(), "PCP", JoinType.LEFT_OUTER_JOIN);		
		criteria.add(Restrictions.eq("PAI." + MciPatologiaInfeccao.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("PCP." + MciPalavraChavePatologia.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		if(StringUtils.isNotBlank(param)) {
			if(StringUtils.isNumeric(param)){
				Integer seq = Integer.valueOf(param);
				criteria.add(Restrictions.eq("PAI." + MciPatologiaInfeccao.Fields.SEQ.toString(), seq));
			} else {
				Criterion c1 =  Restrictions.ilike("PAI." + MciPatologiaInfeccao.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE);
				Criterion c2 =  Restrictions.ilike("PCP." + MciPalavraChavePatologia.Fields.DESCRICAO.toString(), param, MatchMode.ANYWHERE);
				criteria.add(Restrictions.or(c1, c2));
			}
		}
		
		ProjectionList projection =	Projections.projectionList()
				.add(Projections.property("PAI."+ MciPatologiaInfeccao.Fields.SEQ.toString()), DoencaInfeccaoVO.Fields.SEQ_PAI.toString())
				.add(Projections.property("PCP."+ MciPalavraChavePatologia.Fields.DESCRICAO.toString()), DoencaInfeccaoVO.Fields.DESC_PALAVRA_CHAVE.toString())
				.add(Projections.property("PAI."+ MciPatologiaInfeccao.Fields.DESCRICAO.toString()), DoencaInfeccaoVO.Fields.DESC_PATOLOGIA.toString());

		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(DoencaInfeccaoVO.class));
		return criteria;
	}
}
