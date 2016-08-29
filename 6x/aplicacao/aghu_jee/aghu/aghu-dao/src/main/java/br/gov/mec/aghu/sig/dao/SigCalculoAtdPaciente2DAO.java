package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdCIDS;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoAtdReceita;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;

public class SigCalculoAtdPaciente2DAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtdPaciente> {

	private static final long serialVersionUID = -1446205225788441L;

	public BigDecimal buscarValorUltimaFatura(Integer prontuario) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_RECEITA.toString(), "car");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), "int");
		
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.groupProperty("atd." + AghAtendimentos.Fields.PRONTUARIO.toString()))
				.add(Projections.groupProperty("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString()))
				.add(Projections.groupProperty("int." + AinInternacao.Fields.DT_INTERNACAO.toString()))
				.add(Projections.sqlProjection("sum(VLR_RECEITA) totalFatura", new String[]{"totalFatura"}, new Type[] {BigDecimalType.INSTANCE}));
		criteria.setProjection(Projections.distinct(projectionList));
		
		criteria.add(Restrictions.or(
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		criteria.add(Restrictions.or(Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.P),
				Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.F)));
		
		criteria.addOrder(Order.desc("int." + AinInternacao.Fields.DT_INTERNACAO.toString()));
		
		List<Object[]> results = executeCriteria(criteria);
		if(!results.isEmpty()) {
			return (BigDecimal) results.get(0)[3];
		} else {
			return null;
		}
	}
	
	public BigDecimal buscarValorTotalReceita(Integer prontuario, Integer pmuSeq, boolean isEspecialidade, boolean isEquipeMedica, Integer atdSeq, List<Integer> seqCategorias) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_RECEITA.toString(), "car");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.sqlProjection("sum(VLR_RECEITA) receitaTotal",
						new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(prontuario != null) {
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		}
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(isEspecialidade && !isEquipeMedica) {
			criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CENTRO_CUSTO.toString(), "cct");
			criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), "esp");
			criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.SM));
		} else if(isEquipeMedica && !isEspecialidade) {
			criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), "ser");
			criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.EQ));
		} else {
			criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		}
		if(seqCategorias != null && !seqCategorias.isEmpty()) {
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), seqCategorias));
		}
		criteria.add(Restrictions.or(Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.P),
				Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.F)));
		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}
	
	public BigDecimal buscarCustoTotalPesquisa(Integer prontuario, Integer pmuSeq, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca");
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.sqlProjection("sum(coalesce(VLR_TOT_INSUMOS,0) + coalesce(VLR_TOT_PESSOAS,0) + coalesce(VLR_TOT_EQUIPAMENTOS,0) + coalesce(VLR_TOT_SERVICOS,0)) custoTotal",
							new String[]{"custoTotal"}, new Type[] {BigDecimalType.INSTANCE}));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(prontuario!=null){
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));	
		}
		
		criteria.add(Subqueries.propertyIn("atd."+AghAtendimentos.Fields.SEQ.toString(), 
				this.subqueryCustosReceitas(
						listaCID, 
						listaCentroCusto,
						listaEspecialidades,
						responsaveis		
				)));
		
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		criteria.add(Restrictions.or(Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.P),
				Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.F)));
		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}
	
	public BigDecimal buscarReceitaTotalPesquisa(Integer prontuario, Integer pmuSeq, List<AghCid> listaCID, List<FccCentroCustos> listaCentroCusto, List<AghEspecialidades> listaEspecialidades, List<RapServidores> responsaveis) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_RECEITA.toString(), "car");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
				.add(Projections.sqlProjection("sum(VLR_RECEITA) receitaTotal",
						new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}));
		criteria.setProjection(projectionList);
		criteria.add(Restrictions.or(
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
                Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		if(prontuario != null) {
			criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		}
		
		criteria.add(Subqueries.propertyIn("atd."+AghAtendimentos.Fields.SEQ.toString(), 
				this.subqueryCustosReceitas(
						listaCID, 
						listaCentroCusto,
						listaEspecialidades,
						responsaveis		
				)));
		
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		criteria.add(Restrictions.or(Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.P),
				Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.F)));
		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}
	
	private DetachedCriteria subqueryCustosReceitas(List<AghCid> cids, 
			List<FccCentroCustos> cCUstos,
			List<AghEspecialidades> especialidades,
			List<RapServidores> profissionaisResponsaveis){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPaciente.class, "cap");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("cap." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString())), SigCalculoAtdPaciente.Fields.ATD_SEQ.toString()));
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.LISTA_CALCULOS_CIDS.toString(), "cids", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cam", JoinType.INNER_JOIN);
		criteria.createAlias("cam."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_CONSUMO.toString(), "cca", JoinType.INNER_JOIN);
		criteria.createAlias("cap."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu", JoinType.INNER_JOIN);
		
		if(!cids.isEmpty()){
			criteria.add(Restrictions.in("cids." + SigCalculoAtdCIDS.Fields.CID.toString(), cids));
		}
		
		if(!cCUstos.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO.toString(), cCUstos));
		}
		
		if(!especialidades.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidades));
		}
		
		if(!profissionaisResponsaveis.isEmpty()){
			criteria.add(Restrictions.in("cam." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), profissionaisResponsaveis));
		}
		criteria.add(Restrictions.or(Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.P),
				Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.F)));
		
		return criteria;
	}
}
