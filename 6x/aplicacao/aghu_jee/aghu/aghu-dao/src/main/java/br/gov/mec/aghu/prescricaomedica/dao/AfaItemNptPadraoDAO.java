package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaComposicaoNptPadrao;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaItemNptPadrao;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.AfaItemNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.TipoComposicaoComponenteVMpmDosagemVO;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AfaItemNptPadraoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaItemNptPadrao>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8633518962350574790L;

	public List<AfaItemNptPadraoVO> pesquisaAfaItemNptPadraoPorFiltro(Short composicaoSeqp, Short composicaoFnpSeq) {
		DetachedCriteria criteria = obterCriteriaAfaItemNptPadraoPorFiltro();
		
		criteria.add(Restrictions.eq("inp."+AfaItemNptPadrao.Fields.ID_CNT_FNP_SEQ.toString(),composicaoFnpSeq));
		if(composicaoSeqp != null){
			criteria.add(Restrictions.eq("inp."+AfaItemNptPadrao.Fields.ID_CNT_SEQ_P.toString(),composicaoSeqp));
		}
		criteria.addOrder(Order.asc("acn."+AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString()));
		return executeCriteria(criteria);
		
	}
	
	public List<AfaItemNptPadraoVO> pesquisaAfaItemNptPadraoPorFiltroOrder(Short composicaoSeqp, Short composicaoFnpSeq){
		DetachedCriteria criteria = obterCriteriaAfaItemNptPadraoPorFiltro();
		criteria.add(Restrictions.eq("inp."+AfaItemNptPadrao.Fields.ID_CNT_FNP_SEQ.toString(),composicaoFnpSeq));
		if(composicaoSeqp != null){
			criteria.add(Restrictions.eq("inp."+AfaItemNptPadrao.Fields.ID_CNT_SEQ_P.toString(),composicaoSeqp));
		}
		criteria.addOrder(Order.asc("acn."+AfaComponenteNpt.Fields.ORDEM.toString()));
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria obterCriteriaAfaItemNptPadraoPorFiltro(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaItemNptPadrao.class, "inp");

		criteria.createAlias("inp."+AfaItemNptPadrao.Fields.AFA_COMPONENTE_NPT.toString(), "acn");
		criteria.createAlias("inp."+AfaItemNptPadrao.Fields.AFA_COMPOSICAO_NPT_PADROES.toString(), "cnp");
		criteria.createAlias("inp."+AfaItemNptPadrao.Fields.AFA_FORMA_DOSAGEM.toString(), "afd");
		criteria.createAlias("afd."+AfaFormaDosagem.Fields.UNIDADE_MEDICAS.toString(), "und");
		criteria.createAlias("inp."+AfaItemNptPadrao.Fields.V_MPM_DOSAGEM.toString(), "vmp");
		criteria.createAlias("inp."+AfaItemNptPadrao.Fields.RAP_SERVIDORES.toString(), "rserv", JoinType.INNER_JOIN);
		criteria.createAlias("rserv."+RapServidores.Fields.PESSOA_FISICA.toString(), "rservpf", JoinType.INNER_JOIN);
		
		criteria.setFetchMode("inp."+ AfaItemNptPadrao.Fields.RAP_SERVIDORES.toString(), FetchMode.JOIN);
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("inp."+AfaItemNptPadrao.Fields.ID.toString()), AfaItemNptPadraoVO.Fields.AFA_ITEM_NPT_PADRAO_ID.toString());
		p.add(Projections.property("inp."+AfaItemNptPadrao.Fields.RAP_SERVIDORES.toString()), AfaItemNptPadraoVO.Fields.AFA_ITEM_NPT_PADRAO_USUARIO.toString());
		p.add(Projections.property("inp."+AfaItemNptPadrao.Fields.CRIADO_EM.toString()), AfaItemNptPadraoVO.Fields.AFA_ITEM_NPT_PADRAO_CRIADO_EM.toString());
		p.add(Projections.property("acn."+AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString()), AfaItemNptPadraoVO.Fields.MED_MAT_CODIGO_COMPONENTE_NPTS.toString());
		p.add(Projections.property("acn."+AfaComponenteNpt.Fields.DESCRICAO.toString()), AfaItemNptPadraoVO.Fields.DESCRICAO_COMPONENTE_NPTS.toString());
		p.add(Projections.property("inp."+AfaItemNptPadrao.Fields.QTDE.toString()), AfaItemNptPadraoVO.Fields.QTD_ITEM_NPT.toString());
		p.add(Projections.property("vmp."+VMpmDosagem.Fields.SEQ_DOSAGEM.toString()), AfaItemNptPadraoVO.Fields.SEQ_VMPM_DOSAGEM.toString());
		p.add(Projections.property("vmp."+VMpmDosagem.Fields.SEQ_UNIDADE.toString()), AfaItemNptPadraoVO.Fields.SEQ_UNIDADE_VMPM_DOSAGEM.toString());
		p.add(Projections.property("rservpf."+RapPessoasFisicas.Fields.NOME.toString()), AfaItemNptPadraoVO.Fields.AFA_ITEM_NPT_PADRAO_CRIADO_POR.toString());
		p.add(Projections.property("und."+MpmUnidadeMedidaMedica.Fields.DESCRICAO.toString()), AfaItemNptPadraoVO.Fields.UNIDADE_MEDICA.toString());
		p.add(Projections.property("afd."+AfaFormaDosagem.Fields.SEQ.toString()), AfaItemNptPadraoVO.Fields.FDS_SEQ.toString());
		p.add(Projections.property("und."+MpmUnidadeMedidaMedica.Fields.SEQ.toString()), AfaItemNptPadraoVO.Fields.UMM_SEQ.toString());
		criteria.setProjection(p);
		
		criteria.setResultTransformer(Transformers.aliasToBean(AfaItemNptPadraoVO.class));
		return criteria;
	}
	
	/**
	 * 
	 * @param Monta Conculta C9
	 * @return
	 */
	
	public DetachedCriteria montaConsulta(short seqAfaTipoComposicoes,Object objPesquisa){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaItemNptPadrao.class);
		criteria.createAlias(AfaItemNptPadrao.Fields.AFA_COMPOSICAO_NPT_PADROES.toString(), "cnp");
		criteria.createAlias("cnp."+AfaComposicaoNptPadrao.Fields.AFA_TIPO_COMPOSICOES.toString(), "atc");
		criteria.createAlias(AfaItemNptPadrao.Fields.AFA_COMPONENTE_NPT.toString(), "cnt");
		criteria.createAlias(AfaItemNptPadrao.Fields.V_MPM_DOSAGEM.toString(), "vmp");
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("cnt."+AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString()),
				TipoComposicaoComponenteVMpmDosagemVO.Fields.MED_MAT_CODIGO.toString());
		p.add(Projections.property("cnt."+AfaComponenteNpt.Fields.DESCRICAO.toString()),
				TipoComposicaoComponenteVMpmDosagemVO.Fields.DESCRICAO.toString());
		p.add(Projections.property(AfaItemNptPadrao.Fields.QTDE.toString()),
				TipoComposicaoComponenteVMpmDosagemVO.Fields.QTDE.toString());
 		p.add(Projections.property("vmp."+VMpmDosagem.Fields.SEQ_UNIDADE.toString()),
 				TipoComposicaoComponenteVMpmDosagemVO.Fields.SEQ_UNIDADE.toString());
		
 		criteria.add(Restrictions.eq("atc."+AfaTipoComposicoes.Fields.SEQ.toString(),seqAfaTipoComposicoes));
		
		String strPesquisa = (String) objPesquisa;
		
		if (CoreUtil.isNumeroShort(strPesquisa)) {
			criteria.add(Restrictions.eq("cnt."+AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString(),Integer.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike("cnt."+AfaComponenteNpt.Fields.DESCRICAO.toString(),strPesquisa, MatchMode.ANYWHERE));
		}
		
		criteria.setProjection(p);
		criteria.setResultTransformer(Transformers.aliasToBean(TipoComposicaoComponenteVMpmDosagemVO.class));
		
		return criteria;
	}

	/**
	 * Consulta para suggestion box SB4 estoria #3538
	 * @param formulaSeq
	 * @return
	 */
	public List<TipoComposicaoComponenteVMpmDosagemVO> pesquisaComponenteVinculadoComposicaoFormula(short seqAfaTipoComposicoes,Object objeto){
		DetachedCriteria criteria = montaConsulta(seqAfaTipoComposicoes,objeto);		
		return executeCriteria(criteria,0, 100, "afaComponenteNpt", true);
	}
	
	public long pesquisaComponenteVinculadoComposicaoFormulaCount(short seqAfaTipoComposicoes,Object objeto){
		DetachedCriteria criteria = montaConsulta(seqAfaTipoComposicoes,objeto);		
		return executeCriteriaCount(criteria);
	} 
}
