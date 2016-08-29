package br.gov.mec.aghu.compras.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacaoLoteReposicao;
import br.gov.mec.aghu.dominio.DominioTipoMaterial;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoItemLoteReposicao;
import br.gov.mec.aghu.model.ScoLoteReposicao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoLoteReposicaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoLoteReposicao> {

	private static final long serialVersionUID = -4188568818411154354L;

	public List<ScoLoteReposicao> pesquisarLoteReposicaoPorCodigoDescricao(Object param) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLoteReposicao.class);

		String descricao = (String) param;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(descricao)){
			codigo = Integer.valueOf(descricao);
			descricao = null;
		}	

		if (codigo != null) {
			criteria.add(Restrictions.eq(ScoLoteReposicao.Fields.SEQ.toString(),
					codigo));
		}

		if (descricao != null) {
			criteria.add(Restrictions.ilike(ScoLoteReposicao.Fields.DESCRICAO.toString(),
					descricao, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.ne(ScoLoteReposicao.Fields.SITUACAO.toString(),
				DominioSituacaoLoteReposicao.EX));
		
		criteria.addOrder(Order.asc(ScoLoteReposicao.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria, 0, 100, null, true);
	}

	private DetachedCriteria obterCriteriaLote(ScoLoteReposicao loteReposicao,
			ScoGrupoMaterial grupoMaterial, Date dataInicioGeracao, Date dataFimGeracao,
			RapServidores servidorGeracao, ScoMaterial material, String nomeLote,
			DominioTipoMaterial tipoMaterial) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLoteReposicao.class, "LTR");
		criteria.createAlias(ScoLoteReposicao.Fields.SERVIDOR_GERACAO.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA, "PES", JoinType.LEFT_OUTER_JOIN);
		if (loteReposicao != null) {
			criteria.add(Restrictions.eq("LTR."+ScoLoteReposicao.Fields.SEQ.toString(), loteReposicao.getSeq()));
		}
		
		if (grupoMaterial != null) {
			criteria.add(Restrictions.eq("LTR."+ScoLoteReposicao.Fields.GRUPO_MATERIAL.toString(), grupoMaterial));
		}

		if (servidorGeracao != null) {
			criteria.add(Restrictions.eq("LTR."+ScoLoteReposicao.Fields.SERVIDOR_GERACAO.toString(), servidorGeracao));
		}
		
		if (tipoMaterial != null) {
			criteria.add(Restrictions.eq("LTR."+ScoLoteReposicao.Fields.TIPO_MATERIAL.toString(), tipoMaterial));
		}
		
		if (dataInicioGeracao != null) {
			criteria.add(Restrictions.ge("LTR."+ScoLoteReposicao.Fields.DT_GERACAO.toString(), DateUtil.truncaData(dataInicioGeracao)));
		}
		
		if (dataFimGeracao != null) {
			criteria.add(Restrictions.le("LTR."+ScoLoteReposicao.Fields.DT_GERACAO.toString(), DateUtil.truncaDataFim(dataFimGeracao)));
		}

		if (material != null) {
			DetachedCriteria subQuery = DetachedCriteria.forClass(ScoItemLoteReposicao.class, "ILR");
			subQuery.setProjection(Projections.property("ILR."+ScoItemLoteReposicao.Fields.LTR_SEQ.toString()));
			subQuery.add(Restrictions.eq("ILR."+ScoItemLoteReposicao.Fields.MATERIAL.toString(), material));
			
			criteria.add(Subqueries.propertyIn("LTR."+ScoLoteReposicao.Fields.SEQ.toString(), subQuery));
		}
		
		criteria.add(Restrictions.ne("LTR."+ScoLoteReposicao.Fields.SITUACAO.toString(),
				DominioSituacaoLoteReposicao.EX));
		
		return criteria;
	}
	
	public Long pesquisarLoteReposicaoCount(ScoLoteReposicao loteReposicao,
			ScoGrupoMaterial grupoMaterial, Date dataInicioGeracao, Date dataFimGeracao,
			RapServidores servidorGeracao, ScoMaterial material, String nomeLote,
			DominioTipoMaterial tipoMaterial) {
		DetachedCriteria criteria = this.obterCriteriaLote(loteReposicao, grupoMaterial, dataInicioGeracao, dataFimGeracao, servidorGeracao, material, nomeLote, tipoMaterial);
		return this.executeCriteriaCount(criteria);
	}

	public List<ScoLoteReposicao> pesquisarLoteReposicao(Integer firstResult, 
			Integer maxResult, String orderProperty, boolean asc, ScoLoteReposicao loteReposicao,
			ScoGrupoMaterial grupoMaterial, Date dataInicioGeracao, Date dataFimGeracao,
			RapServidores servidorGeracao, ScoMaterial material, String nomeLote,
			DominioTipoMaterial tipoMaterial) {
		DetachedCriteria criteria = this.obterCriteriaLote(loteReposicao, grupoMaterial, dataInicioGeracao, dataFimGeracao, servidorGeracao, material, nomeLote, tipoMaterial);
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public ScoLoteReposicao obterLoteReposicaoPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoLoteReposicao.class, "LTR");
		criteria.createAlias(ScoLoteReposicao.Fields.SERVIDOR_GERACAO.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ScoLoteReposicao.Fields.CC_APLICACAO.toString(), "CCA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV."+RapServidores.Fields.PESSOA_FISICA, "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(ScoLoteReposicao.Fields.SEQ.toString(), seq));
		
		return (ScoLoteReposicao) this.executeCriteriaUniqueResult(criteria);
	}
}