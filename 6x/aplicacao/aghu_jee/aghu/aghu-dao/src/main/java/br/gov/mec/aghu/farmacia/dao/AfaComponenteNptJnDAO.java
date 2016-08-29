package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaComponenteNptJn;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VMpmMdtosDescr;
import br.gov.mec.aghu.prescricaomedica.vo.ComponenteNPTVO;

public class AfaComponenteNptJnDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaComponenteNptJn> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 7742252292748640921L;

	public List<ComponenteNPTVO> pesquisarHistoricoComponenteNPT(Integer seqComponente) {
		DetachedCriteria criteria = montarCriteria(seqComponente);
		criteria.addOrder(Order.desc("ACN."+AfaComponenteNptJn.Fields.JN_DATE_TIME.toString()));
		return executeCriteria(criteria);
	}
	
	private DetachedCriteria montarCriteria(Integer seqGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaComponenteNptJn.class, "ACN");
		criteria.createAlias("ACN."+AfaComponenteNptJn.Fields.DESCRICAO_MEDICAMENTO.toString(), "VDE", JoinType.INNER_JOIN);
		criteria.createAlias("ACN."+AfaComponenteNptJn.Fields.AFAGRUPOCOMPONENTENPT.toString(), "GCN", JoinType.INNER_JOIN);
		criteria.createAlias("ACN."+AfaComponenteNptJn.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);

		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("VDE."+VMpmMdtosDescr.Fields.DESCRICAO_EDIT.toString()), ComponenteNPTVO.Fields.NOME_MEDICAMENTO.toString())
				.add(Projections.property("GCN."+AfaGrupoComponenteNpt.Fields.DESCRICAO.toString()), ComponenteNPTVO.Fields.GRUPO.toString())
				.add(Projections.property("PES."+RapPessoasFisicas.Fields.NOME.toString()), ComponenteNPTVO.Fields.CRIADO_POR.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.DESCRICAO.toString()), ComponenteNPTVO.Fields.DESCRICAO.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.MED_MAT_CODIGO.toString()), ComponenteNPTVO.Fields.MED_MAT_CODIGO.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.CRIADO_EM.toString()), ComponenteNPTVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.IND_SITUACAO.toString()), ComponenteNPTVO.Fields.SITUACAO.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.IND_ADULTO.toString()), ComponenteNPTVO.Fields.ADULTO.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.IND_PEDIATRIA.toString()), ComponenteNPTVO.Fields.PEDIATRIA.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.OBSERVACAO.toString()), ComponenteNPTVO.Fields.OBSERVACAO.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.ORDEM.toString()), ComponenteNPTVO.Fields.ORDEM.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.IDENTIF_COMPONENTE.toString()), ComponenteNPTVO.Fields.IDENTIFICACAO.toString())
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.JN_DATE_TIME.toString()), ComponenteNPTVO.Fields.DT_ATUALIZACAO.toString())
				);
		if (seqGrupo != null) {
			criteria.add(Restrictions.eq("ACN."+AfaComponenteNptJn.Fields.MED_MAT_CODIGO.toString(), seqGrupo));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ComponenteNPTVO.class));
		return criteria;
	}
	
	public List<ComponenteNPTVO> obterJnPorSeqGrupo(Short seqGrupo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaComponenteNptJn.class, "ACN");

		criteria.createAlias("ACN."+AfaComponenteNptJn.Fields.AFAGRUPOCOMPONENTENPT.toString(), "GCN", JoinType.INNER_JOIN);

		if (seqGrupo != null) {
			criteria.add(Restrictions.eq("GCN."+AfaGrupoComponenteNpt.Fields.SEQ.toString(), seqGrupo));
		}
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("GCN."+AfaGrupoComponenteNpt.Fields.DESCRICAO.toString()), ComponenteNPTVO.Fields.GRUPO.toString())	
				.add(Projections.property("ACN."+AfaComponenteNptJn.Fields.DESCRICAO.toString()), ComponenteNPTVO.Fields.DESCRICAO.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(ComponenteNPTVO.class));
		return this.executeCriteria(criteria);
	}


}
