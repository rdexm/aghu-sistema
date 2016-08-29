package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.dominio.DominioIdentificacaoComponenteNPT;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.VMpmMdtosDescr;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteComposicaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;
import br.gov.mec.aghu.view.VMpmDosagem;

public class AfaComponenteNptDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaComponenteNpt> {

	private static final long serialVersionUID = 4070084250474804889L;
	
	public List<ComponenteNPTVO> pesquisarComponentesNPT(VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao,Integer firstResult, Integer maxResult,String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteria(medicamento, grupo,
				descricao, situacao,adulto, pediatria,
				ordem,identificacao);
		return executeCriteria(criteria,firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarComponentesNPTCount(VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao) {
		DetachedCriteria criteria = montarCriteria(medicamento,grupo,
				descricao, situacao, adulto, pediatria,
				ordem,identificacao);
		return this.executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria montarFiltros1(DetachedCriteria criteria,VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao){
		
		if(medicamento != null){
			criteria.add(Restrictions.eq("ACN."+AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString(), medicamento.getMatCodigo()));
		}
		if(grupo != null){
			criteria.add(Restrictions.eq("GCN."+AfaGrupoComponenteNpt.Fields.SEQ.toString(), grupo.getSeq()));
		}
		if(StringUtils.isNotBlank(descricao)){
			criteria.add(Restrictions.ilike("ACN."+AfaComponenteNpt.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if(ordem != null){
			criteria.add(Restrictions.eq("ACN."+AfaComponenteNpt.Fields.ORDEM.toString(), ordem));
		}
		if(identificacao != null){
			criteria.add(Restrictions.eq("ACN."+AfaComponenteNpt.Fields.IDENTIF_COMPONENTE.toString(), identificacao));
		}
		return criteria;
	}
	
	private DetachedCriteria montarFiltros2(DetachedCriteria criteria,VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao){
		if(situacao != null){
			DominioSituacao indSituacaao = DominioSituacao.I;
			if(DominioSimNao.S.equals(situacao)){
				indSituacaao = DominioSituacao.A;
			}
			criteria.add(Restrictions.eq("ACN."+AfaComponenteNpt.Fields.IND_SITUACAO.toString(), indSituacaao));
		}
		if(adulto != null){
			criteria.add(Restrictions.eq("ACN."+AfaComponenteNpt.Fields.IND_ADULTO.toString(), DominioSimNao.S.equals(adulto) ? true : false ));
		}
		if(pediatria != null){
			criteria.add(Restrictions.eq("ACN."+AfaComponenteNpt.Fields.IND_PEDIATRIA.toString(), DominioSimNao.S.equals(pediatria) ? true : false));
		}
		return criteria;
	}
	
	private DetachedCriteria montarCriteria(VMpmMdtosDescr medicamento,AfaGrupoComponenteNpt grupo,
			String descricao,DominioSimNao situacao,DominioSimNao adulto,DominioSimNao pediatria,
			Short ordem,DominioIdentificacaoComponenteNPT identificacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaComponenteNpt.class, "ACN");
		criteria.createAlias("ACN."+AfaComponenteNpt.Fields.DESCRICAO_MEDICAMENTO.toString(), "VDE", JoinType.INNER_JOIN);
		criteria.createAlias("ACN."+AfaComponenteNpt.Fields.AFAGRUPOCOMPONENTENPT.toString(), "GCN", JoinType.INNER_JOIN);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("VDE."+VMpmMdtosDescr.Fields.DESCRICAO_EDIT.toString()), ComponenteNPTVO.Fields.NOME_MEDICAMENTO.toString())
				.add(Projections.property("GCN."+AfaGrupoComponenteNpt.Fields.DESCRICAO.toString()), ComponenteNPTVO.Fields.GRUPO.toString())
				.add(Projections.property("GCN."+AfaGrupoComponenteNpt.Fields.SEQ.toString()), ComponenteNPTVO.Fields.SEQ_GRUPO.toString())
				.add(Projections.property("ACN."+AfaComponenteNpt.Fields.DESCRICAO.toString()), ComponenteNPTVO.Fields.DESCRICAO.toString())
				.add(Projections.property("ACN."+AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString()), ComponenteNPTVO.Fields.MED_MAT_CODIGO.toString())
				.add(Projections.property("ACN."+AfaComponenteNpt.Fields.CRIADO_EM.toString()), ComponenteNPTVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("ACN."+AfaComponenteNpt.Fields.IND_SITUACAO.toString()), ComponenteNPTVO.Fields.SITUACAO.toString())
				.add(Projections.property("ACN."+AfaComponenteNpt.Fields.IND_ADULTO.toString()), ComponenteNPTVO.Fields.ADULTO.toString())
				.add(Projections.property("ACN."+AfaComponenteNpt.Fields.IND_PEDIATRIA.toString()), ComponenteNPTVO.Fields.PEDIATRIA.toString())
				.add(Projections.property("ACN."+AfaComponenteNpt.Fields.ORDEM.toString()), ComponenteNPTVO.Fields.ORDEM.toString())
				.add(Projections.property("ACN."+AfaComponenteNpt.Fields.IDENTIF_COMPONENTE.toString()), ComponenteNPTVO.Fields.IDENTIFICACAO.toString())
				);

		criteria.add(Restrictions.eq("GCN."+AfaGrupoComponenteNpt.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria = montarFiltros1(criteria,medicamento, grupo,
				descricao, situacao, adulto,pediatria,
				ordem,identificacao);
		
		criteria = montarFiltros2(criteria,medicamento, grupo,
				descricao, situacao, adulto,pediatria,
				ordem,identificacao);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ComponenteNPTVO.class));
		return criteria;
	}
	
	
	public List<AfaComponenteNpt> listarPorSeqGrupo(Short gcnSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaComponenteNpt.class,"ACN");
		
		criteria.createAlias("ACN."+AfaComponenteNpt.Fields.AFAGRUPOCOMPONENTENPT.toString(), "GCN", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("GCN."+AfaGrupoComponenteNpt.Fields.SEQ.toString(), gcnSeq));
		
		//criteria.setResultTransformer(Transformers.aliasToBean(ComponenteNPTVO.class));
		return this.executeCriteria(criteria);
	}
	
	public List<AfaFormulaNptPadrao> verificaDelecaoComponente(final Integer seq) {

		StringBuffer sql = new StringBuffer(500);
			sql.append("select fnp.descricao descricao");
			sql.append(" from afa_formula_npt_padroes fnp,");
			sql.append(" afa_composicao_npt_padroes cnt,");
			sql.append(" afa_item_npt_padroes inp");
			sql.append(" where inp.cnp_med_mat_codigo = :seq");
			sql.append(" and cnt.fnp_seq    = inp.cnt_fnp_seq");
			sql.append(" and cnt.seqp       = inp.cnt_seqp");
			sql.append(" and fnp.seq        = cnt.fnp_seq");
			sql.append(" and fnp.ind_situacao  = 'A'");

		SQLQuery q = createSQLQuery(sql.toString());
		q.setInteger("seq", seq);
		
		List<AfaFormulaNptPadrao> listaVO = q
				.addScalar("descricao", StringType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(AfaFormulaNptPadrao.class)).list();

		return listaVO;
	}
	
	public ComponenteComposicaoVO obterComponenteComposicao(DominioIdentificacaoComponenteNPT identifComponente) {
		StringBuffer hql = new StringBuffer(500);
		hql.append(" select ");
		hql.append("  cnp." + AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString() + " as "
				+ ComponenteComposicaoVO.Fields.MED_MAT_CODIGO.toString());
		hql.append(", tcg." + AfaCompoGrupoComponente.Fields.TIC_SEQ.toString() + " as "
				+ ComponenteComposicaoVO.Fields.TIC_SEQ.toString());
		hql.append(", vds." + VMpmDosagem.Fields.SEQ_UNIDADE.toString() + " as "
				+ ComponenteComposicaoVO.Fields.SEQ_UNIDADE.toString());
		hql.append(", vds." + VMpmDosagem.Fields.SEQ_DOSAGEM.toString() + " as "
				+ ComponenteComposicaoVO.Fields.SEQ_DOSAGEM.toString());
		hql.append(", vds." + VMpmDosagem.Fields.DESCRICAO_UNIDADE.toString() + " as "
				+ ComponenteComposicaoVO.Fields.DESCRICAO_UNIDADE.toString());

		// from
		hql.append(" from ");
		hql.append(AfaGrupoComponenteNpt.class.getName()).append(" gcn, ");
		hql.append(AfaCompoGrupoComponente.class.getName()).append(" tcg, ");
		hql.append(AfaComponenteNpt.class.getName()).append(" cnp, ");
		hql.append(VMpmDosagem.class.getName()).append(" vds ");

		// where
		hql.append(" where cnp.");
		hql.append(AfaComponenteNpt.Fields.IDENTIF_COMPONENTE.toString());
		hql.append(" = :identifComponente");
		
		hql.append(" and cnp.");
		hql.append(AfaComponenteNpt.Fields.GCN_SEQ.toString());
		hql.append(" = gcn.");
		hql.append(AfaGrupoComponenteNpt.Fields.SEQ.toString());

		hql.append(" and tcg.");
		hql.append(AfaCompoGrupoComponente.Fields.GCN_SEQ.toString());
		hql.append(" = gcn.");
		hql.append(AfaGrupoComponenteNpt.Fields.SEQ.toString());
		
		hql.append(" and vds.");
		hql.append(VMpmDosagem.Fields.SEQ_MEDICAMENTO.toString());
		hql.append(" = cnp.");
		hql.append(AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString());

		hql.append(" and vds.");
		hql.append(VMpmDosagem.Fields.FDS_IND_USUAL_NPT.toString());
		hql.append(" = 'S'");

		final Query query = createHibernateQuery(hql.toString());

		query.setParameter("identifComponente", identifComponente);
		query.setResultTransformer(Transformers.aliasToBean(ComponenteComposicaoVO.class));

		List<ComponenteComposicaoVO> lista = query.list();
		if (!lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
}
