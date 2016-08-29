package br.gov.mec.aghu.sig.dao;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoCalculoPaciente;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoProcessamentoCusto;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.CntaConv;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.PacIntdConv;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoAtdReceita;
import br.gov.mec.aghu.model.SigCategoriaConsumos;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigObjetoCustos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CalculoAtendimentoPacienteVO;
import br.gov.mec.aghu.sig.custos.vo.InternacaoConvenioVO;

public class SigCalculoAtdReceitaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtdReceita> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1659396156145832059L;
	
	
	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(112);
		sql.append(" DELETE ").append(SigCalculoAtdReceita.class.getSimpleName().toString()).append(" caa ");
		sql.append(" WHERE caa.").append(SigCalculoAtdReceita.Fields.CALCULOS_ATD_PERMANENCIAS.toString()).append('.').append(SigCalculoAtdPermanencia.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT ca.").append(SigCalculoAtdPermanencia.Fields.SEQ.toString());
			sql.append(" FROM ").append(SigCalculoAtdPermanencia.class.getSimpleName().toString()).append(" ca ");
			sql.append(" WHERE ca.").append(SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString()).append('.').append(SigCalculoAtdPaciente.Fields.SEQ.toString());
			sql.append(" IN ( ");
				sql.append(" SELECT c.").append(SigCalculoAtdPaciente.Fields.SEQ.toString());
				sql.append(" FROM ").append( SigCalculoAtdPaciente.class.getSimpleName()).append(" c ");
				sql.append(" WHERE c.").append(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
			sql.append(" ) ");
		sql.append(" ) ");
		Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
	
	
	private Projection obterProjectionSumReceitaTotal() {
		return Projections.sqlProjection("sum(VLR_RECEITA) receitaTotal",
				new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE});
	}
	
	private Criterion obterCriteriosPesquisaValorReceita(Integer prontuario, Integer pmuSeq, Integer atdSeq, DominioCalculoPermanencia tipo) {
		Conjunction con = Restrictions.conjunction();
		
		con.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		
		if (atdSeq != null) {
			con.add(Restrictions.eq("atd." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		}
		
		if (pmuSeq != null) {
			con.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		
		con.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		con.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), tipo));
		con.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		return con; 
	}
	
	public BigDecimal obterValorTotalReceita(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		String aliasCar = "car";
		String aliasCpp = "cpp";
		String aliasCtc = "ctc";
		String aliasCac = "cac";
		String aliasAtd = "atd";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdReceita.class, aliasCar);
		criteria.createAlias(aliasCar + ponto + SigCalculoAtdReceita.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), aliasCpp);
		criteria.createAlias(aliasCar + ponto + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), aliasCtc);
		criteria.createAlias(aliasCpp + ponto + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), aliasCac);
		criteria.createAlias(aliasCac + ponto + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), aliasAtd);
		criteria.createAlias(aliasCac + ponto + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), "pmu");
		
		criteria.setProjection(obterProjectionSumReceitaTotal());
		
		// Restrictions
		criteria.add(obterCriteriosPesquisaValorReceita(prontuario, pmuSeq, atdSeq, DominioCalculoPermanencia.UI));
		criteria.add(Restrictions.or(Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.P),
				Restrictions.eq("pmu." + SigProcessamentoCusto.Fields.SITUACAO_PROCESSAMENTO_CUSTO.toString(), DominioSituacaoProcessamentoCusto.F)));
		criteria.add(Restrictions.isNotNull(aliasCar + ponto + SigCalculoAtdReceita.Fields.CCT_CODIGO.toString()));
		
		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}

	
	//C1 - Receita Geral
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaGeral(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdReceita.class, "car");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CENTRO_CUSTO.toString(), "cct");
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
				.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()), "ordemVisualizacao")
				.add(Projections.sqlProjection("sum(VLR_RECEITA) receitaTotal",
							new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
				.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()));	
		criteria.setProjection(projectionList);

		// Restrictions
		criteria.add(obterCriteriosPesquisaValorReceita(prontuario, pmuSeq, atdSeq, DominioCalculoPermanencia.UI));

		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		return executeCriteria(criteria);
	}
	
	//C3.1 -- Receita por Centro de Custo
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorCentroCusto(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> listaCtcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdReceita.class, "car"); 
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CENTRO_CUSTO.toString(), "cct");
		
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
			.add(Projections.property("obj." + SigObjetoCustos.Fields.NOME.toString()), "objNome")
			.add(Projections.property("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()), "phiDescricao")	
			.add(Projections.property("cct." + FccCentroCustos.Fields.CODIGO.toString()), "codCentroCusto")
			.add(Projections.property("cct." + FccCentroCustos.Fields.DESCRICAO.toString()), "descricaoCentroCusto")
			.add(Projections.sqlProjection("sum(VLR_RECEITA) receitaTotal",
							new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}))
			.add(Projections.groupProperty("obj." + SigObjetoCustos.Fields.NOME.toString()))
			.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()))	
			.add(Projections.groupProperty("cct." + FccCentroCustos.Fields.CODIGO.toString()))
			.add(Projections.groupProperty("cct." + FccCentroCustos.Fields.DESCRICAO.toString()));
		criteria.setProjection(projectionList);

		// Restrictions
		criteria.add(obterCriteriosPesquisaValorReceita(prontuario, pmuSeq, atdSeq, DominioCalculoPermanencia.UI));
		
		if(listaCtcSeq!=null && listaCtcSeq.size()>0){
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), listaCtcSeq));	
		}
		
		criteria.addOrder(Order.asc("obj." + SigObjetoCustos.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("cct." + FccCentroCustos.Fields.CODIGO.toString()));
		criteria.addOrder(Order.asc("cct." + FccCentroCustos.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		
		return executeCriteria(criteria);
	}
	
	//C5.1 -- Receita por Item de cada Categoria
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorCentroProducao(Integer prontuario, Integer pmuSeq, Integer atdSeq, List<Integer> listaCtcSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdReceita.class, "car"); 
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.PROCEDIMENTO_HOSPITALAR_INTERNO.toString(), "phi", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.OBJETO_CUSTO_VERSAO.toString(), "ocv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ocv." + SigObjetoCustoVersoes.Fields.OBJETO_CUSTO.toString(), "obj", JoinType.LEFT_OUTER_JOIN);
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
			.add(Projections.property("obj." + SigObjetoCustos.Fields.NOME.toString()), "objNome")
			.add(Projections.property("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()), "phiDescricao")	
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
			.add(Projections.sqlProjection("sum(VLR_RECEITA) receitaTotal",
							new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}))
			.add(Projections.groupProperty("obj." + SigObjetoCustos.Fields.NOME.toString()))
			.add(Projections.groupProperty("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()));
		criteria.setProjection(projectionList);

		criteria.add(Restrictions.eq("atd." + AghAtendimentos.Fields.PRONTUARIO.toString(), prontuario));
		if(atdSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), atdSeq));
		}
		if(pmuSeq != null) {
			criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		}
		criteria.add(Restrictions.or(
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.I),
				Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.SITUACAO_CALCULO_PACIENTE.toString(), DominioSituacaoCalculoPaciente.IA)));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		if(listaCtcSeq!=null && listaCtcSeq.size()>0){
			criteria.add(Restrictions.in("ctc." + SigCategoriaConsumos.Fields.SEQ.toString(), listaCtcSeq));	
		}
		criteria.add(Restrictions.eq("ctc." + SigCategoriaConsumos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc("obj." + SigObjetoCustos.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("phi." + FatProcedHospInternos.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		
		return executeCriteria(criteria);
	}
	
	//C6.1 -- Receita por especialidade medica
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorEspecialidade(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdReceita.class, "car"); 
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CENTRO_CUSTO.toString(), "cct");
		
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
			.add(Projections.property("esp." + AghEspecialidades.Fields.SEQ.toString()), "espSeq")
			.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()), "nomeReduzido")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
			.add(Projections.sqlProjection("sum(VLR_RECEITA) receitaTotal",
							new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}))
			.add(Projections.groupProperty("esp." + AghEspecialidades.Fields.SEQ.toString()))
			.add(Projections.groupProperty("esp." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()));
		criteria.setProjection(projectionList);
		criteria.add(obterCriteriosPesquisaValorReceita(prontuario, pmuSeq, atdSeq, DominioCalculoPermanencia.SM));
		criteria.addOrder(Order.asc("esp." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()));
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()));
		criteria.addOrder(Order.asc("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		
		return executeCriteria(criteria);
	}

	
	//C7.1 -- Receita por equipe medica
	public List<CalculoAtendimentoPacienteVO> pesquisarReceitaPorEquipeMedica(Integer prontuario, Integer pmuSeq, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdReceita.class, "car"); 
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.SIG_CATEGORIA_CONSUMO.toString(), "ctc");
		criteria.createAlias("car." + SigCalculoAtdReceita.Fields.CENTRO_CUSTO.toString(), "cct");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), "ser");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), "atd");
		
		ProjectionList projectionList;
		projectionList = Projections.projectionList()
			.add(Projections.property("ser." + RapServidores.Fields.VIN_CODIGO.toString()), "vinCodigoRespEquipe")
			.add(Projections.property("ser." + RapServidores.Fields.MATRICULA.toString()), "matriculaRespEquipe")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()), "agrupador")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()), "descricao")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()), "ctcSeq")
			.add(Projections.property("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()), "ordemVisualizacao")
			.add(Projections.sqlProjection("sum(VLR_RECEITA) receitaTotal",
							new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}))
			.add(Projections.groupProperty("ser." + RapServidores.Fields.VIN_CODIGO.toString()))
			.add(Projections.groupProperty("ser." + RapServidores.Fields.MATRICULA.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.AGRUPADOR.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.DESCRICAO.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.SEQ.toString()))
			.add(Projections.groupProperty("ctc." + SigCategoriaConsumos.Fields.ORDEM_VISUALIZACAO.toString()));
		
		criteria.setProjection(projectionList);
		
		criteria.add(obterCriteriosPesquisaValorReceita(prontuario, pmuSeq, atdSeq, DominioCalculoPermanencia.EQ));

		criteria.setResultTransformer(Transformers.aliasToBean(CalculoAtendimentoPacienteVO.class));
		
		return executeCriteria(criteria);
	}	
	
	public List<InternacaoConvenioVO> pesquisarIternacoesConvenioSus(Integer pmuSeq, Short pConvenioSus, boolean receitaSus){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdConsumo.class, "cca");
		
		criteria.setProjection(Projections.projectionList()
			.add(Projections.distinct(Projections.groupProperty("int."+AinInternacao.Fields.SEQ)),InternacaoConvenioVO.Fields.INT_SEQ.toString())
			.add(Projections.min("cpp."+SigCalculoAtdPermanencia.Fields.SEQ), InternacaoConvenioVO.Fields.CPP_SEQ.toString())
		);
		
		criteria.createAlias("cca." + SigCalculoAtdConsumo.Fields.CALCULO_ATIVIDADE_PERMANENCIA.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), "int");
		
		criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		
		if(receitaSus){
			criteria.add(Restrictions.eq("int." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		}
		else{
			criteria.add(Restrictions.ne("int." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		}
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		
		criteria.setResultTransformer(Transformers.aliasToBean(InternacaoConvenioVO.class));
		
		return executeCriteria(criteria);
	}
	
	
	public BigDecimal obterValorTotalReceitaInternacao(Integer pmuSeq, Short pConvenioSus, Integer intSeq, boolean receitaSus){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdReceita.class, "ccr");
		
		criteria.setProjection(Projections.sum("ccr."+SigCalculoAtdReceita.Fields.VALOR_RECEITA));
		
		criteria.createAlias("ccr." + SigCalculoAtdReceita.Fields.CALCULOS_ATD_PERMANENCIAS.toString(), "cpp");
		criteria.createAlias("cpp." + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.createAlias("cac." + SigCalculoAtdPaciente.Fields.INTERNACAO.toString(), "int");
		
		criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		
		if(receitaSus){
			criteria.add(Restrictions.eq("int." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		}
		else{
			criteria.add(Restrictions.ne("int." + AinInternacao.Fields.CSP_CNV_CODIGO.toString(), pConvenioSus));
		}		
		criteria.add(Restrictions.eq("int." + AinInternacao.Fields.SEQ.toString(), intSeq));
		criteria.add(Restrictions.eq("cpp." + SigCalculoAtdPermanencia.Fields.TIPO.toString(), DominioCalculoPermanencia.UI));
		
		BigDecimal  valor = (BigDecimal) executeCriteriaUniqueResult(criteria);
		return valor != null ? valor : BigDecimal.ZERO;
	}
	
	public BigDecimal obterValorTotalContaConvenio(Integer intSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PacIntdConv.class, "pacc");
		
		criteria.setProjection(Projections.sum("cnt."+CntaConv.Fields.QTDE_CSH.toString()));
		
		criteria.createAlias("pacc." + PacIntdConv.Fields.CNTA_CONVS.toString(), "cnt");
		criteria.createAlias("cnt." + CntaConv.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("atd." + AghAtendimentos.Fields.INTERNACAO.toString(), "int");
		
		criteria.add(Restrictions.eq("int." + AinInternacao.Fields.SEQ.toString(), intSeq));
		criteria.add(Restrictions.isNotNull("cnt." + CntaConv.Fields.DATA_COMP.toString()));
		
		BigDecimal  valor = (BigDecimal) executeCriteriaUniqueResult(criteria);
		return valor != null ? valor : BigDecimal.ZERO;
	}
	
	public BigDecimal obterValorTotalContaSus(Integer intSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasHospitalares.class, "cth");
		
		criteria.setProjection(Projections.sqlProjection("SUM( {alias}.VALOR_SH + {alias}.VALOR_UTI + {alias}.VALOR_UTIE + {alias}.VALOR_SP + {alias}.VALOR_ACOMP + {alias}.VALOR_RN + {alias}.VALOR_SADT + {alias}.VALOR_HEMAT + {alias}.VALOR_TRANSP + {alias}.VALOR_OPM) AS TOTAL_CONTA", 
					new String[]{"TOTAL_CONTA"}, new Type[] {BigDecimalType.INSTANCE}));
		
		criteria.createAlias("cth." + FatContasHospitalares.Fields.CONTA_INTERNACAO.toString(), "coi");
	
		criteria.add(Restrictions.eq("coi." + FatContasInternacao.Fields.INT_SEQ.toString(), intSeq));
		criteria.add(Restrictions.eq("cth." + FatContasHospitalares.Fields.IND_SITUACAO.toString(), DominioSituacaoConta.O));
		
		BigDecimal  valor = (BigDecimal) executeCriteriaUniqueResult(criteria);
		return valor != null ? valor : BigDecimal.ZERO;
	}
}