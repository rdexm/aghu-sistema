package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaComposicaoNptPadrao;
import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AfaComposicaoNptPadraoVO;


public class AfaComposicaoNptPadraoDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaComposicaoNptPadrao>{

	/**
	 * '
	 */
	private static final long serialVersionUID = -4402738573350988242L;

	
	/**	
	 * C2 
	 * @param filtro
	 * @return
	 */
	public List<AfaComposicaoNptPadraoVO> pesquisaAfaFormulaNptPadraoPorFiltros(Short formulaSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaComposicaoNptPadrao.class, "fnp");
		
		criteria.createAlias("fnp."+ AfaComposicaoNptPadrao.Fields.AFA_TIPO_VELOC_ADMINISTRACOES.toString(), "ftv", JoinType.INNER_JOIN);
		criteria.createAlias("fnp."+ AfaComposicaoNptPadrao.Fields.AFA_TIPO_COMPOSICOES.toString(), "ftc", JoinType.INNER_JOIN);
		criteria.createAlias("fnp."+ AfaComposicaoNptPadrao.Fields.AFA_FORMULA_NPT_PADROES.toString(), "ffn", JoinType.INNER_JOIN);
		
		criteria.createAlias("ftc."+ AfaTipoComposicoes.Fields.RAP_SERVIDOR.toString(), "rserv", JoinType.INNER_JOIN);
		criteria.createAlias("rserv."+ RapServidores.Fields.PESSOA_FISICA.toString(), "rservpf", JoinType.INNER_JOIN);
		
		ProjectionList p = Projections.projectionList();
			p.add(Projections.property("ftc."+AfaTipoComposicoes.Fields.DESCRICAO.toString()),AfaComposicaoNptPadraoVO.Fields.DESCRICAO_COMPOSICOES.toString());
			p.add(Projections.property("ftc."+AfaTipoComposicoes.Fields.SEQ.toString()),AfaComposicaoNptPadraoVO.Fields.SEQ_COMPOSICOES.toString());
			p.add(Projections.property("fnp."+AfaComposicaoNptPadrao.Fields.VELOCIDADE_ADMINISTRACAO.toString()),AfaComposicaoNptPadraoVO.Fields.VELOCIDADE_ADMINISTRACAO.toString());
			p.add(Projections.property("fnp."+AfaComposicaoNptPadrao.Fields.CRIADO_EM.toString()),AfaComposicaoNptPadraoVO.Fields.COMPOSICAO_CRIADO_EM.toString());
			p.add(Projections.property("fnp."+AfaComposicaoNptPadrao.Fields.ID_FNP_SEQ.toString()),AfaComposicaoNptPadraoVO.Fields.COMPOSICAO_ID_FNP_SEQ.toString());
			p.add(Projections.property("fnp."+AfaComposicaoNptPadrao.Fields.ID_SEQ_P.toString()),AfaComposicaoNptPadraoVO.Fields.COMPOSICAO_ID_SEQ_P.toString());
			p.add(Projections.property("ftv."+AfaTipoVelocAdministracoes.Fields.SEQ.toString()),AfaComposicaoNptPadraoVO.Fields.SEQ_VELOCIDADE_ADMINISTRACAO.toString());
			p.add(Projections.property("ftv."+AfaTipoVelocAdministracoes.Fields.DESCRICAO.toString()),AfaComposicaoNptPadraoVO.Fields.DESCRICAO_VELOCIDADE_ADMINISTRACAO.toString());
			p.add(Projections.property("rservpf."+RapPessoasFisicas.Fields.NOME.toString()),AfaComposicaoNptPadraoVO.Fields.COMPOSICAO_CRIADO_POR.toString());
		criteria.setProjection(p);
		
		criteria.add(Restrictions.eq("ffn."+AfaFormulaNptPadrao.Fields.SEQ.toString(),formulaSeq));
		criteria.addOrder(Order.asc("ftc."+AfaTipoComposicoes.Fields.SEQ.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AfaComposicaoNptPadraoVO.class));
		
		return executeCriteria(criteria);
	}

	
}
