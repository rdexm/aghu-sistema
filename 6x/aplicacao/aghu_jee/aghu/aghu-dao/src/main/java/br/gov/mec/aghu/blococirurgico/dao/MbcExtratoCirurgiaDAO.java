package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.MonitorCirurgiaSalaPreparoVO;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioSituacaoExtratoCirurgia;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcExtratoCirurgia;
import br.gov.mec.aghu.model.MbcExtratoCirurgiaId;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.utils.DateUtil;


public class MbcExtratoCirurgiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcExtratoCirurgia> { 


	private static final long serialVersionUID = 447549832130567956L;


 	@Override
	protected void obterValorSequencialId(MbcExtratoCirurgia elemento) {
		if (elemento == null || elemento.getId() == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		Short maxSeqp = this.obterSeqpMax(elemento.getId().getCrgSeq());
		if (maxSeqp != null) {
			elemento.getId().setSeqp(Short.valueOf(String.valueOf(maxSeqp + 1)));
		}else{
			elemento.getId().setSeqp(Short.valueOf("1"));
		}
		
	}


 	public Short obterSeqpMax(Integer crgSeq) {
		DetachedCriteria criteria = this.obterCriteria();
		criteria.setProjection(
				Projections.max(MbcExtratoCirurgia.Fields.SEQP.toString()));
		criteria.add(Restrictions.eq(
				MbcExtratoCirurgia.Fields.CIRURGIA_SEQ.toString(), crgSeq));
				
		return (Short) this.executeCriteriaUniqueResult(criteria);
	}
 	
 	
 	protected DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(MbcExtratoCirurgia.class);
	}
	
	public MbcExtratoCirurgia buscarMotivoCancelCirurgia(Integer seq) {
					
			DetachedCriteria criteria = DetachedCriteria.forClass(MbcExtratoCirurgia.class,"t1");
			
			criteria.createAlias("t1." + MbcExtratoCirurgia.Fields.CIRURGIA.toString(), "cirurgias");			
			criteria.createAlias("cirurgias." + MbcCirurgias.Fields.QUESTAO.toString(), "questao", Criteria.LEFT_JOIN);
						
			criteria.add(Restrictions.eq("cirurgias."+MbcCirurgias.Fields.SEQ.toString(), seq));
			criteria.add(Restrictions.eq("cirurgias."+MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.CANC));			
			criteria.add(Subqueries.propertyIn("t1." + MbcExtratoCirurgia.Fields.CRIADO_EM.toString(),subCriteriaExtratoCirurgia()));				
			
			criteria.addOrder(Order.desc("cirurgias."+MbcCirurgias.Fields.SEQ.toString()));

			
			return (MbcExtratoCirurgia) executeCriteriaUniqueResult(criteria);
		}


	private DetachedCriteria subCriteriaExtratoCirurgia() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcExtratoCirurgia.class,"t2");

		criteria.setProjection(Projections
				.projectionList().add(Projections.max("t2." + MbcExtratoCirurgia.Fields.CRIADO_EM.toString())));
		
		criteria.add(Restrictions.eqProperty("t2." + MbcExtratoCirurgia.Fields.CIRURGIA_SEQ.toString(), "cirurgias."+MbcCirurgias.Fields.SEQ.toString()));		
		criteria.add(Restrictions.eq("t2." + MbcExtratoCirurgia.Fields.SITUACAO_CIRG.toString(), DominioSituacaoExtratoCirurgia.CANC));		

		return criteria;
	}

	public List<MbcExtratoCirurgia> listarMbcExtratoCirurgiaPorCirurgia(Integer crgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcExtratoCirurgia.class);
		criteria.createAlias(MbcExtratoCirurgia.Fields.SERVIDOR.toString(), "SERV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(MbcExtratoCirurgia.Fields.CIRURGIA_SEQ.toString(), crgSeq));
		return executeCriteria(criteria);
	}
	
	public List<MbcExtratoCirurgia> pesquisarMbcExtratoCirurgiaPorCirurgiaSituacao(Integer crgSeq, DominioSituacaoCirurgia situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcExtratoCirurgia.class);
		criteria.add(Restrictions.eq(MbcExtratoCirurgia.Fields.CIRURGIA_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcExtratoCirurgia.Fields.SITUACAO_CIRG.toString(), situacao));
		criteria.addOrder(Order.asc(MbcExtratoCirurgia.Fields.CRIADO_EM.toString()));
		return executeCriteria(criteria);
	}
	

	public List<MonitorCirurgiaSalaPreparoVO> pesquisarMonitorCirurgiaSalaPreparo(final Short unfSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcExtratoCirurgia.class, "EXT");
		
		ProjectionList projecoesCriteria = Projections.projectionList();
		projecoesCriteria.add(Projections.property("EXT." +	MbcExtratoCirurgia.Fields.CIRURGIA_SEQ.toString()), "crgSeq");
		projecoesCriteria.add(Projections.property("EXT." +	MbcExtratoCirurgia.Fields.CRIADO_EM.toString()), "chegada");
		criteria.setProjection(projecoesCriteria);
		
		criteria.add(Restrictions.eq("EXT." + MbcExtratoCirurgia.Fields.SITUACAO_CIRG.toString(), DominioSituacaoCirurgia.PREP));

		DetachedCriteria subCriteriaCirurgia = DetachedCriteria.forClass(MbcCirurgias.class, "CRG");
		subCriteriaCirurgia.createAlias("CRG." + MbcCirurgias.Fields.SALA_CIRURGICA.toString(), "SLC");
		subCriteriaCirurgia.createAlias("CRG." + MbcCirurgias.Fields.UNIDADE_FUNCIONAL.toString(), "UNF");
		subCriteriaCirurgia.setProjection(Projections.property("CRG." + MbcCirurgias.Fields.SEQ.toString()));
		subCriteriaCirurgia.add(Property.forName("CRG." + MbcCirurgias.Fields.SEQ.toString()).eqProperty("EXT." + MbcExtratoCirurgia.Fields.CIRURGIA_SEQ.toString()));
	
		Date hoje = DateUtil.truncaData(new Date());
		Date ontem = DateUtil.adicionaDias(hoje, -1);
		subCriteriaCirurgia.add(Restrictions.between("CRG." + MbcCirurgias.Fields.DATA.toString(), ontem, hoje));

		subCriteriaCirurgia.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), DominioSituacaoCirurgia.PREP));
		subCriteriaCirurgia.add(Restrictions.eq("SLC." + MbcSalaCirurgica.Fields.VISIVEL_MONITOR.toString(), Boolean.TRUE));

		subCriteriaCirurgia.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unfSeq));

		criteria.add(Subqueries.exists(subCriteriaCirurgia)); // MBC_EXTRATO_CIRURGIAS.CRG_SEQ IN (...
		
		criteria.setResultTransformer(Transformers.aliasToBean(MonitorCirurgiaSalaPreparoVO.class));
		return executeCriteria(criteria);
	}
	
	public List<MbcExtratoCirurgia> pesquisarMbcExtratoCirurgiaPorCirurgiaSituacaoPeriodo(Integer crgSeq, DominioSituacaoCirurgia situacao, Date dataIni, Date dataFim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcExtratoCirurgia.class);
		criteria.add(Restrictions.eq(MbcExtratoCirurgia.Fields.CIRURGIA_SEQ.toString(), crgSeq));
		criteria.add(Restrictions.eq(MbcExtratoCirurgia.Fields.SITUACAO_CIRG.toString(), situacao));
		criteria.add(Restrictions.between(MbcExtratoCirurgia.Fields.CRIADO_EM.toString(), dataIni, dataFim));
		return executeCriteria(criteria);
	}

	public MbcExtratoCirurgia obterMbcExtratoCirurgiaPorId(MbcExtratoCirurgiaId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcExtratoCirurgia.class);
		criteria.add(Restrictions.eq(MbcExtratoCirurgia.Fields.ID.toString(), id));
		return (MbcExtratoCirurgia)executeCriteriaUniqueResult(criteria);
	}
//38
	public List<MbcExtratoCirurgia> obterExtratoAmbulatorioInternacaoPorSituacaoCrgSeq(Integer crgSeq, DominioSituacaoCirurgia situacao,
			DominioSituacaoCirurgia situacaoCirg) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MbcExtratoCirurgia.class, "EXT");
			criteria.createAlias("EXT." + MbcExtratoCirurgia.Fields.CIRURGIA.toString(), "CRG", JoinType.INNER_JOIN);
			criteria.add(Restrictions.in("CRG." + MbcCirurgias.Fields.ORIGEM_PAC_CIRG.toString(), new DominioOrigemPacienteCirurgia[] {DominioOrigemPacienteCirurgia.A, 
			DominioOrigemPacienteCirurgia.I}));
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SITUACAO.toString(), situacao));
			criteria.add(Restrictions.eq("EXT." + MbcExtratoCirurgia.Fields.SITUACAO_CIRG.toString(), situacaoCirg));
			criteria.add(Restrictions.eq("CRG." + MbcCirurgias.Fields.SEQ.toString(), crgSeq));
			return executeCriteria(criteria);
			}
}
