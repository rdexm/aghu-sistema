package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgiaPdtSalaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcCirurgiasCursorCirVO;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.PdtDadoDesc;
import br.gov.mec.aghu.model.PdtDescricao;

public class PdtDadoDescDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtDadoDesc> {

	private static final long serialVersionUID = 3523624873732719767L;

	public PdtDadoDesc obterDadoDescPorDdtSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDadoDesc.class, "pdt");
		
/*		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property(PdtDadoDesc.Fields.ID_DTHR_INICIO.toString()));
		projection.add(Projections.property(PdtDadoDesc.Fields.ID_DTHR_FIM.toString()));
		projection.add(Projections.property(PdtDadoDesc.Fields.ID_CARATER.toString()));
		projection.add(Projections.property(PdtDadoDesc.Fields.ID_SEDACAO.toString()));
		projection.add(Projections.property(PdtDadoDesc.Fields.ID_NRO_FILME.toString()));
		projection.add(Projections.property(PdtDadoDesc.Fields.ID_OBSERVACOES_PROC.toString()));
		
		criteria.setProjection(projection);
		*/
		criteria.add(Restrictions.eq("pdt." + PdtDadoDesc.Fields.DDT_SEQ.toString(), seq));		
		
		return (PdtDadoDesc) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria montarPesquisaCirurgiaComDescricaoPdtMesmaSala(Short unfSeq, Date dataCirurgia, 
			Integer crgSeq, Short sciUnfSeq, Short sciSeqp) {
		
		String aliasCrg = "crg";
		String aliasSci = "sci";
		String aliasDdt = "ddt";
		String aliasPdd = "pdd";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtDadoDesc.class, aliasPdd);
		
		criteria.createAlias(aliasPdd + ponto + PdtDadoDesc.Fields.PDT_DESCRICAO, aliasDdt);
		criteria.createAlias(aliasDdt + ponto + PdtDescricao.Fields.MBC_CIRURGIAS, aliasCrg);
		criteria.createAlias(aliasCrg + ponto + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), aliasSci);
		
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.DATA.toString(), dataCirurgia));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.UNF_SEQ.toString(), unfSeq));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
		criteria.add(Restrictions.ne(aliasCrg + ponto + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SCI_UNF_SEQ.toString(), sciUnfSeq));
		criteria.add(Restrictions.eq(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString(), sciSeqp));
		criteria.add(Restrictions.isNotNull(aliasPdd + ponto + PdtDadoDesc.Fields.DTHR_INICIO.toString()));
		criteria.add(Restrictions.isNotNull(aliasPdd + ponto + PdtDadoDesc.Fields.DTHR_FIM.toString()));
		
		criteria.addOrder(Order.asc(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc(aliasDdt + ponto + PdtDescricao.Fields.SEQ.toString()));
		
		return criteria;
	}
	
	public List<DescricaoCirurgiaPdtSalaVO> pesquisarCirurgiaComDescricaoPdtMesmaSala(Short unfSeq, Date dataCirurgia, 
			Integer crgSeq, Short sciUnfSeq, Short sciSeqp) {
		
		String aliasCrg = "crg";
		String aliasSci = "sci";
		String aliasDdt = "ddt";
		String aliasPdd = "pdd";
		String ponto = ".";
		
		DetachedCriteria criteria = montarPesquisaCirurgiaComDescricaoPdtMesmaSala(unfSeq, dataCirurgia, crgSeq, sciUnfSeq, sciSeqp);
		
		Projection proj = Projections.projectionList()
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), DescricaoCirurgiaPdtSalaVO.Fields.CRG_SEQ.toString())
				.add(Projections.property(aliasDdt + ponto + PdtDescricao.Fields.SEQ.toString()), DescricaoCirurgiaPdtSalaVO.Fields.DDT_SEQ.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString()), DescricaoCirurgiaPdtSalaVO.Fields.SCI_SEQP.toString())
				.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.PAC_CODIGO.toString()), DescricaoCirurgiaPdtSalaVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property(aliasSci + ponto + MbcSalaCirurgica.Fields.NOME.toString()), DescricaoCirurgiaPdtSalaVO.Fields.SALA.toString())
				.add(Projections.property(aliasPdd + ponto + PdtDadoDesc.Fields.DTHR_INICIO.toString()), DescricaoCirurgiaPdtSalaVO.Fields.DTHR_INICIO_PDT.toString())
				.add(Projections.property(aliasPdd + ponto + PdtDadoDesc.Fields.DTHR_FIM.toString()), DescricaoCirurgiaPdtSalaVO.Fields.DTHR_FIM_PDT.toString());		
		
		criteria.setProjection(proj);
		
		criteria.setResultTransformer(Transformers.aliasToBean(DescricaoCirurgiaPdtSalaVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<MbcCirurgiasCursorCirVO> pesquisarOutrasDescricoesPdtPorSala(final Integer dcgCrgSeq, 
			 final Date dataCrg, final Short unfSeq, final Short sciUnfSeq, final Short sciSeqp) {

		String aliasCrg = "crg";
		String aliasSci = "sci";
		String aliasDdt = "ddt";
		String aliasPdd = "pdd";
		String ponto = ".";
		
		DetachedCriteria criteria = montarPesquisaCirurgiaComDescricaoPdtMesmaSala(unfSeq, dataCrg, dcgCrgSeq, sciUnfSeq, sciSeqp);
	
		Projection projection = Projections.projectionList()
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SEQ.toString()), MbcCirurgiasCursorCirVO.Fields.CRG_SEQ.toString())
		.add(Projections.property(aliasDdt + ponto + PdtDescricao.Fields.SEQ.toString()), MbcCirurgiasCursorCirVO.Fields.DDT_SEQ.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.SCI_SEQP.toString()), MbcCirurgiasCursorCirVO.Fields.SCI_SEQP.toString())
		.add(Projections.property(aliasCrg + ponto + MbcCirurgias.Fields.PAC_CODIGO.toString()), MbcCirurgiasCursorCirVO.Fields.PAC_CODIGO.toString())
		.add(Projections.property(aliasSci + ponto + MbcSalaCirurgica.Fields.NOME.toString()), MbcCirurgiasCursorCirVO.Fields.SALA.toString())
		.add(Projections.property(aliasPdd + ponto + PdtDadoDesc.Fields.DTHR_INICIO.toString()), MbcCirurgiasCursorCirVO.Fields.DTHR_INICIO_CIRG.toString())
		.add(Projections.property(aliasPdd + ponto + PdtDadoDesc.Fields.DTHR_FIM.toString()), MbcCirurgiasCursorCirVO.Fields.DTHR_FIM_CIRG.toString());
		
		criteria.setProjection(projection);
		
		criteria.setResultTransformer(Transformers.aliasToBean(MbcCirurgiasCursorCirVO.class));
		
		return executeCriteria(criteria);		
	}

	public List<PdtDadoDesc> pesquisarDadoDescPorDdtSeq(final Integer ddtSeq){
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtDadoDesc.class, "pdt");
		
		criteria.add(Restrictions.eq("pdt." + PdtDadoDesc.Fields.DDT_SEQ.toString(), ddtSeq));
		
		return executeCriteria(criteria);
	}
}
