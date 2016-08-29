package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioProcedimentoCustoPaciente;
import br.gov.mec.aghu.dominio.DominioTipoCompetencia;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdProcedimentos;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.CalculoProcedimentoVO;
import br.gov.mec.aghu.sig.custos.vo.ItemProcedHospVO;
import br.gov.mec.aghu.view.VSigCustosCalculoProced;

public class VSigCustosCalculoProcedDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VSigCustosCalculoProced> {

	private static final long serialVersionUID = 7024276428638494300L;	
	
	/***
	 * #48994 - Consulta C5 (Lista de Procedimentos)
	 * @param pmuSeq
	 * @param descricao
	 * @return
	 */
	public List<ItemProcedHospVO> buscarProcedimentos(Integer pmuSeq, String descricao){
		
		DetachedCriteria criteria = this.criarCriteriaBuscarProcedimentos(pmuSeq, descricao);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()		
			.add(Projections.property("iph." + FatItensProcedHospitalar.Fields.PHO_SEQ), ItemProcedHospVO.Fields.IPH_PHO_SEQ.toString()) 
			.add(Projections.property("iph." + FatItensProcedHospitalar.Fields.SEQ), ItemProcedHospVO.Fields.IPH_SEQ.toString()) 
			.add(Projections.property("iph." + FatItensProcedHospitalar.Fields.COD_TABELA), ItemProcedHospVO.Fields.COD_TABELA.toString()) 
			.add(Projections.property("iph." + FatItensProcedHospitalar.Fields.DESCRICAO), ItemProcedHospVO.Fields.DESCRICAO.toString()) 
		));
		
		
		criteria.setResultTransformer(Transformers.aliasToBean(ItemProcedHospVO.class));
		criteria.addOrder(Order.asc("iph." + FatItensProcedHospitalar.Fields.DESCRICAO));
		criteria.addOrder(Order.asc("iph." + FatItensProcedHospitalar.Fields.COD_TABELA));
		
		return this.executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * #48994 - Count da consulta C5
	 * @param pmuSeq
	 * @param descricao
	 * @return
	 */
	public Long buscarProcedimentosCount(Integer pmuSeq, String descricao){
		DetachedCriteria criteria = this.criarCriteriaBuscarProcedimentos(pmuSeq, descricao);
		return executeCriteriaCount(criteria);
	}
	
	private  DetachedCriteria criarCriteriaBuscarProcedimentos(Integer pmuSeq, String descricao){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdProcedimentos.class, "cpp");
		criteria.createAlias("cpp."+SigCalculoAtdProcedimentos.Fields.CALCULO_ATD_PACIENTE, "cac", JoinType.INNER_JOIN);
		criteria.createAlias("cpp."+SigCalculoAtdProcedimentos.Fields.ITEM_PROCEDIMENTO_HOSPITALAR, "iph", JoinType.INNER_JOIN);
		 
		criteria.add(Restrictions.eq("cac."+SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), pmuSeq));
		
		if (StringUtils.isNotEmpty(descricao)) {
			
			if(StringUtils.isNumeric(descricao)){
				criteria.add(Restrictions.eq("iph." + FatItensProcedHospitalar.Fields.COD_TABELA.toString(), Long.valueOf(descricao)));
			}
			else{
				criteria.add(Restrictions.ilike("iph." + FatItensProcedHospitalar.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
			}
		}
		
		return criteria;
	}
	
	/**
	 * #48994 - Consulta C2 (Lista dos procedimentos do primeiro nível)
	 * @param pmuSeq
	 * @param procedimentoPrincipal
	 * @param tipo
	 * @param procedimentos
	 * @return
	 */
	public List<CalculoProcedimentoVO> buscarProcedimentosPrimeiroNivel(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, ItemProcedHospVO procedimentoPrincipal, DominioProcedimentoCustoPaciente tipo, List<ItemProcedHospVO> procedimentos, List<Short> conveniosSelecionados){

		DetachedCriteria criteria = DetachedCriteria.forClass(VSigCustosCalculoProced.class, "vproc");
		
		criteria.setProjection(Projections.projectionList()		
			.add(Projections.groupProperty("vproc." + VSigCustosCalculoProced.Fields.IPH_PHO_SEQ), CalculoProcedimentoVO.Fields.IPH_PHO_SEQ.toString()) 
			.add(Projections.groupProperty("vproc." + VSigCustosCalculoProced.Fields.IPH_SEQ), CalculoProcedimentoVO.Fields.IPH_SEQ.toString()) 
			.add(Projections.groupProperty("vproc." + VSigCustosCalculoProced.Fields.COD_TABELA), CalculoProcedimentoVO.Fields.COD_TABELA.toString()) 
			.add(Projections.groupProperty("vproc." + VSigCustosCalculoProced.Fields.DESCRICAO), CalculoProcedimentoVO.Fields.DESCRICAO.toString()) 
			.add(Projections.sqlProjection("sum( coalesce({alias}.CUSTO_TOTAL_INSUMOS,0) + coalesce({alias}.CUSTO_TOTAL_PESSOAS,0) + coalesce({alias}.CUSTO_TOTAL_EQUIPAMENTOS,0) + coalesce({alias}.CUSTO_TOTAL_SERVICOS,0) ) as "+CalculoProcedimentoVO.Fields.CUSTO_TOTAL, new String[]{CalculoProcedimentoVO.Fields.CUSTO_TOTAL.toString()}, new Type[] {BigDecimalType.INSTANCE}))
			.add(Projections.sqlProjection("sum( coalesce({alias}.RECEITA_TOTAL,0) ) as "+CalculoProcedimentoVO.Fields.RECEITA_TOTAL , new String[]{CalculoProcedimentoVO.Fields.RECEITA_TOTAL.toString()}, new Type[] {BigDecimalType.INSTANCE})) 
			.add(Projections.countDistinct("vproc." + VSigCustosCalculoProced.Fields.ATD_SEQ.toString()), CalculoProcedimentoVO.Fields.QUANTIDADE.toString()) 
		);
		
		if(tipoCompetencia == DominioTipoCompetencia.CUSTOS){
			criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.PMU_SEQ.toString(), processamento.getSeq()));
		}
		else if(tipoCompetencia == DominioTipoCompetencia.FATURAMENTO){
			criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.COMP_FAT.toString(), processamento.getCompetenciaMesAno()));
		}
		
		criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.PRINCIPAL.toString(), true));
		
		if(procedimentoPrincipal != null){
			criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.IPH_PHO_SEQ.toString(), procedimentoPrincipal.getIphPhoSeq()));
			criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.IPH_SEQ.toString(), procedimentoPrincipal.getIphSeq()));
		}
		
		if(tipo.equals(DominioProcedimentoCustoPaciente.Q)){
			
			//Para cada procedimento, tem que existir um, ou qualquer um no cálculo de procedimentos
			DetachedCriteria subCriteria = DetachedCriteria.forClass(SigCalculoAtdProcedimentos.class, "ccp");
			subCriteria.setProjection(Projections.projectionList().add(Projections.property("ccp." + SigCalculoAtdProcedimentos.Fields.SEQ.toString()), "seq"));
			subCriteria.add(Restrictions.eqProperty("ccp."+SigCalculoAtdProcedimentos.Fields.CALCULO_ATD_PACIENTE.toString(), "vproc."+VSigCustosCalculoProced.Fields.CALCULO_ATENDIMENTO_PACIENTE.toString()));
			
			Criterion[] possibilidades = new Criterion[procedimentos.size()];
			for (int index = 0; index < procedimentos.size(); index++) {
				ItemProcedHospVO vo = procedimentos.get(index);
				possibilidades[index] =  Restrictions.and(
						Restrictions.eq("ccp." + SigCalculoAtdProcedimentos.Fields.IPH_PHO_SEQ.toString(), vo.getIphPhoSeq()),
						Restrictions.eq("ccp." + SigCalculoAtdProcedimentos.Fields.IPH_SEQ.toString(), vo.getIphSeq())
				);	
			}
			
			subCriteria.add(Restrictions.or( possibilidades ));
			criteria.add(Subqueries.exists(subCriteria));
		}
		else if(tipo.equals(DominioProcedimentoCustoPaciente.T)){
			
			//Para procedimento, ele tem que existir todos no cálculo de procedimentos
			for (ItemProcedHospVO vo : procedimentos) {
				DetachedCriteria subCriteria = DetachedCriteria.forClass(SigCalculoAtdProcedimentos.class, "ccp");
				subCriteria.setProjection(Projections.projectionList().add(Projections.property("ccp." + SigCalculoAtdProcedimentos.Fields.SEQ.toString()), "seq"));
				subCriteria.add(Restrictions.eqProperty("ccp."+SigCalculoAtdProcedimentos.Fields.CALCULO_ATD_PACIENTE.toString(), "vproc."+VSigCustosCalculoProced.Fields.CALCULO_ATENDIMENTO_PACIENTE.toString()));
				subCriteria.add(Restrictions.eq("ccp." + SigCalculoAtdProcedimentos.Fields.IPH_PHO_SEQ.toString(), vo.getIphPhoSeq()));
				subCriteria.add(Restrictions.eq("ccp." + SigCalculoAtdProcedimentos.Fields.IPH_SEQ.toString(), vo.getIphSeq()));
				criteria.add(Subqueries.exists(subCriteria));	
			}
			
			//Não pode existir mais nenhum fora os procedimentos da lista da lista
			DetachedCriteria subCriteria = DetachedCriteria.forClass(SigCalculoAtdProcedimentos.class, "ccp");
			subCriteria.setProjection(Projections.projectionList().add(Projections.property("ccp." + SigCalculoAtdProcedimentos.Fields.SEQ.toString()), "seq"));
			subCriteria.add(Restrictions.eqProperty("ccp."+SigCalculoAtdProcedimentos.Fields.CALCULO_ATD_PACIENTE.toString(), "vproc."+VSigCustosCalculoProced.Fields.CALCULO_ATENDIMENTO_PACIENTE.toString()));
			
			Criterion[] possibilidadesIn = new Criterion[procedimentos.size()];
			for (int index = 0; index < procedimentos.size(); index++) {
				ItemProcedHospVO vo = procedimentos.get(index);
				possibilidadesIn[index] =  Restrictions.or(
						Restrictions.ne("ccp." + SigCalculoAtdProcedimentos.Fields.IPH_PHO_SEQ.toString(), vo.getIphPhoSeq()),
						Restrictions.ne("ccp." + SigCalculoAtdProcedimentos.Fields.IPH_SEQ.toString(), vo.getIphSeq())
				);	
			}
			
			subCriteria.add(Restrictions.and( possibilidadesIn ));
			criteria.add(Subqueries.notExists(subCriteria));
		}
		
		if(!conveniosSelecionados.isEmpty()){
			criteria.add(Restrictions.in("vproc." + VSigCustosCalculoProced.Fields.CNV_CODIGO.toString(), conveniosSelecionados));
		}
		
		criteria.addOrder(Order.asc("vproc." + VSigCustosCalculoProced.Fields.DESCRICAO));
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoProcedimentoVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 * #48994 - Consulta C3 (Lista dos procedimentos do segundo nível)
	 * @param pmuSeq
	 * @param iphPhoSeq
	 * @param iphSeq
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<CalculoProcedimentoVO> buscarProcedimentosSegundoNivel(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, Short iphPhoSeq, Integer iphSeq, List<ItemProcedHospVO> listaProcedimentos, Integer quantidadeProcedimentos, DominioProcedimentoCustoPaciente tipo, List<Short> conveniosSelecionados){
	
		StringBuilder hql = new StringBuilder(1500);
		hql.append(" SELECT ") 
			.append("RES1.").append(VSigCustosCalculoProced.Fields.IPH_PHO_SEQ).append(" as ").append(CalculoProcedimentoVO.Fields.IPH_PHO_SEQ)
			.append(",RES1.").append(VSigCustosCalculoProced.Fields.IPH_SEQ).append(" as  ").append(CalculoProcedimentoVO.Fields.IPH_SEQ)
			.append(",RES1.").append(VSigCustosCalculoProced.Fields.COD_TABELA).append(" as ").append(CalculoProcedimentoVO.Fields.COD_TABELA)
			.append(",RES1.").append(VSigCustosCalculoProced.Fields.DESCRICAO).append(" as ").append(CalculoProcedimentoVO.Fields.DESCRICAO)
			.append(",RES1.").append(VSigCustosCalculoProced.Fields.ATD_SEQ).append(" as ").append(CalculoProcedimentoVO.Fields.ATD_SEQ)
			.append(",SUM( coalesce(RES1.").append(VSigCustosCalculoProced.Fields.CUSTO_TOTAL_INSUMOS).append(",0) + coalesce(RES1.").append(VSigCustosCalculoProced.Fields.CUSTO_TOTAL_PESSOAS).append(",0) + coalesce(RES1.").append(VSigCustosCalculoProced.Fields.CUSTO_TOTAL_EQUIPAMENTOS).append(",0) + coalesce(RES1.").append(VSigCustosCalculoProced.Fields.CUSTO_TOTAL_SERVICOS).append(",0) ) as ").append(CalculoProcedimentoVO.Fields.CUSTO_TOTAL)
			.append(",SUM( coalesce(RES1.").append(VSigCustosCalculoProced.Fields.RECEITA_TOTAL).append(",0) ) as ").append(CalculoProcedimentoVO.Fields.RECEITA_TOTAL)
			.append(",COUNT(DISTINCT RES1.").append(VSigCustosCalculoProced.Fields.ATD_SEQ).append(") as ").append(CalculoProcedimentoVO.Fields.QUANTIDADE)
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.IPH_PHO_SEQ).append(" as ").append(CalculoProcedimentoVO.Fields.IPH_PHO_SEQ_SECUNDARIO) 
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.IPH_SEQ).append(" as ").append(CalculoProcedimentoVO.Fields.IPH_SEQ_SECUNDARIO)
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.COD_TABELA).append(" as ").append(CalculoProcedimentoVO.Fields.COD_TABELA_SECUNDARIO)
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.DESCRICAO).append(" as ").append(CalculoProcedimentoVO.Fields.DESCRICAO_SECUNDARIO)
			.append(",SUM( coalesce(RES2.").append(VSigCustosCalculoProced.Fields.CUSTO_TOTAL_INSUMOS).append(",0) + coalesce(RES2.").append(VSigCustosCalculoProced.Fields.CUSTO_TOTAL_PESSOAS).append(",0) + coalesce(RES2.").append(VSigCustosCalculoProced.Fields.CUSTO_TOTAL_EQUIPAMENTOS).append(",0) + coalesce(RES2.").append(VSigCustosCalculoProced.Fields.CUSTO_TOTAL_SERVICOS).append(",0) ) as ").append(CalculoProcedimentoVO.Fields.CUSTO_TOTAL_SECUNDARIO)
			.append(",SUM( coalesce(RES2.").append(VSigCustosCalculoProced.Fields.RECEITA_TOTAL).append(",0) ) as ").append(CalculoProcedimentoVO.Fields.RECEITA_TOTAL_SECUNDARIO)
			.append(",COUNT(DISTINCT RES2.").append(VSigCustosCalculoProced.Fields.ATD_SEQ).append(") as ").append(CalculoProcedimentoVO.Fields.QUANTIDADE_SECUNDARIO)
			
		.append(" FROM ")  
			.append(SigCalculoAtdPaciente.class.getSimpleName()).append(" CAC ")
			.append("inner join CAC.").append(SigCalculoAtdPaciente.Fields.SIG_CUSTOS_CALCULO_PROCEDIMENTOS).append(" RES1 ")
			.append("left outer join CAC.").append(SigCalculoAtdPaciente.Fields.SIG_CUSTOS_CALCULO_PROCEDIMENTOS).append(" RES2 ").append(" with ( RES2.").append(VSigCustosCalculoProced.Fields.PRINCIPAL).append(" = 'N') ")
			
		.append(" WHERE ")
			.append("RES1.").append(VSigCustosCalculoProced.Fields.PRINCIPAL).append(" = 'S' ")
			.append("AND RES1.").append(VSigCustosCalculoProced.Fields.IPH_PHO_SEQ).append(" = :iphPhoSeq ")
			.append("AND RES1.").append(VSigCustosCalculoProced.Fields.IPH_SEQ).append(" = :iphSeq ");
			
			
		if(tipoCompetencia == DominioTipoCompetencia.CUSTOS){
			hql.append("AND RES1.").append(VSigCustosCalculoProced.Fields.PMU_SEQ).append(" = :pmuSeq ");
		}
		else if(tipoCompetencia == DominioTipoCompetencia.FATURAMENTO){
			hql.append("AND RES1.").append(VSigCustosCalculoProced.Fields.COMP_FAT).append(" = :compFat1 ");
		}
		
		boolean procedimentoNaoEstaNaLista = quantidadeProcedimentos == listaProcedimentos.size();
		
		//Corresponde ao iphPhoSeq||iphSeq in ( listaProcedimentos ), já que não tem como concatenar as chaves no hql
		StringBuilder possibilidadesIn = new StringBuilder(1500);
		for (int index = 0; index < listaProcedimentos.size(); index++) {
			ItemProcedHospVO vo = listaProcedimentos.get(index);
			possibilidadesIn.append(" (RES2.").append(VSigCustosCalculoProced.Fields.IPH_PHO_SEQ).append(" = ").append(vo.getIphPhoSeq());
			possibilidadesIn.append(" AND RES2.").append(VSigCustosCalculoProced.Fields.IPH_SEQ).append(" = ").append(vo.getIphSeq());
			possibilidadesIn.append(") "); 
			if(index!=listaProcedimentos.size()-1){
				possibilidadesIn.append(" OR ");	
			}
		}
		
		//Mais de um procedimento selecionado
		if(quantidadeProcedimentos > 1){
			//Todos, então os selecionados (fora ele) tem que ser secundário
			//Ou se for qualquer e o procedimento não estiver entre os selecionados, os secundários terão que ser ele também
			if(tipo.equals(DominioProcedimentoCustoPaciente.T) || procedimentoNaoEstaNaLista){
				hql.append("AND (").append(possibilidadesIn).append(" ) ");
			}
		}
		//Foi selecionado somente um procedimento
		else if(quantidadeProcedimentos == 1){
			
			//Todos, tem que ser só o selecionado e estar como principal, então os secundários serão nulo
			if(tipo.equals(DominioProcedimentoCustoPaciente.T)){
				hql.append("AND RES2.").append(VSigCustosCalculoProced.Fields.IPH_PHO_SEQ).append(" is null ");
				hql.append("AND RES2.").append(VSigCustosCalculoProced.Fields.IPH_SEQ).append(" is null ");
			}
			//Qualquer, e este não for o selecionado, logo o selecionado tem que ser um secundário
			else if(procedimentoNaoEstaNaLista){
				hql.append("AND (").append(possibilidadesIn).append(" ) ");
			}
		}
		
		if(!conveniosSelecionados.isEmpty()){
			hql.append("AND RES1.").append(VSigCustosCalculoProced.Fields.CNV_CODIGO).append(" in ( :convenios ) ");
		}
		
		hql.append(" GROUP BY ") 
			.append("RES1.").append(VSigCustosCalculoProced.Fields.IPH_PHO_SEQ)
			.append(",RES1.").append(VSigCustosCalculoProced.Fields.IPH_SEQ)
			.append(",RES1.").append(VSigCustosCalculoProced.Fields.COD_TABELA)
			.append(",RES1.").append(VSigCustosCalculoProced.Fields.DESCRICAO)
			.append(",RES1.").append(VSigCustosCalculoProced.Fields.ATD_SEQ)
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.IPH_PHO_SEQ)
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.IPH_SEQ)
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.COD_TABELA)
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.DESCRICAO)
		.append(" ORDER BY ") 
			.append("RES1.").append(VSigCustosCalculoProced.Fields.ATD_SEQ)
			.append(",RES2.").append(VSigCustosCalculoProced.Fields.DESCRICAO)
		;
		
		final org.hibernate.Query query = this.createHibernateQuery(hql.toString());
		
		if(tipoCompetencia == DominioTipoCompetencia.CUSTOS){
			query.setParameter("pmuSeq", processamento.getSeq());
		}
		else if(tipoCompetencia == DominioTipoCompetencia.FATURAMENTO){
			query.setParameter("compFat1", processamento.getCompetenciaMesAno());
		}
		
		query.setParameter("iphPhoSeq", iphPhoSeq);
		query.setParameter("iphSeq", iphSeq);
		
		if(!conveniosSelecionados.isEmpty()){
			query.setParameterList("convenios", conveniosSelecionados);
		}
		
		query.setResultTransformer(Transformers.aliasToBean(CalculoProcedimentoVO.class));

		return query.list();
	}	
	
	/**
	 * #48994 - Consulta C4 (Lista de pacientes)
	 * @param pmuSeq
	 * @param atendimentos
	 * @return
	 */
	public List<CalculoProcedimentoVO> buscarPacientesProcedimentos(SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, List<Integer> atendimentos, List<Short> conveniosSelecionados){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(VSigCustosCalculoProced.class, "vproc");
		
		criteria.setProjection(Projections.projectionList()		
			.add(Projections.groupProperty("atd." + AghAtendimentos.Fields.SEQ), CalculoProcedimentoVO.Fields.ATD_SEQ.toString()) 
			.add(Projections.groupProperty("atd." + AghAtendimentos.Fields.PRONTUARIO), CalculoProcedimentoVO.Fields.PRONTUARIO.toString()) 
			.add(Projections.groupProperty("pac." + AipPacientes.Fields.NOME), CalculoProcedimentoVO.Fields.NOME_PACIENTE.toString())
			.add(Projections.sqlProjection("sum( coalesce({alias}.CUSTO_TOTAL_INSUMOS,0) + coalesce({alias}.CUSTO_TOTAL_PESSOAS,0) + coalesce({alias}.CUSTO_TOTAL_EQUIPAMENTOS,0) + coalesce({alias}.CUSTO_TOTAL_SERVICOS,0) ) as "+CalculoProcedimentoVO.Fields.CUSTO_TOTAL, new String[]{CalculoProcedimentoVO.Fields.CUSTO_TOTAL.toString()}, new Type[] {BigDecimalType.INSTANCE}))
			.add(Projections.sqlProjection("sum( coalesce({alias}.RECEITA_TOTAL,0) ) as "+CalculoProcedimentoVO.Fields.RECEITA_TOTAL , new String[]{CalculoProcedimentoVO.Fields.RECEITA_TOTAL.toString()}, new Type[] {BigDecimalType.INSTANCE})) 
		);
		
		criteria.createAlias("vproc."+VSigCustosCalculoProced.Fields.ATENDIMENTO, "atd", JoinType.INNER_JOIN);
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE, "pac", JoinType.INNER_JOIN);
		
		
		if(tipoCompetencia == DominioTipoCompetencia.CUSTOS){
			criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.PMU_SEQ.toString(), processamento.getSeq()));
		}
		else if(tipoCompetencia == DominioTipoCompetencia.FATURAMENTO){
			criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.COMP_FAT.toString(), processamento.getCompetenciaMesAno()));
		}
		
		criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.PRINCIPAL.toString(), true));
		criteria.add(Restrictions.in("vproc." + VSigCustosCalculoProced.Fields.ATD_SEQ, atendimentos));
		
		if(!conveniosSelecionados.isEmpty()){
			criteria.add(Restrictions.in("vproc." + VSigCustosCalculoProced.Fields.CNV_CODIGO.toString(), conveniosSelecionados));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(CalculoProcedimentoVO.class));
		return executeCriteria(criteria);
	}

	public List<Integer> buscarContasPaciente( SigProcessamentoCusto processamento, DominioTipoCompetencia tipoCompetencia, Integer atdSeq, List<Short> conveniosSelecionados) {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(VSigCustosCalculoProced.class, "vproc");
		
		criteria.setProjection(Projections.projectionList()			
			.add(Projections.distinct(Projections.property("vproc."+VSigCustosCalculoProced.Fields.CONTA)))
		);		
		
		if(tipoCompetencia == DominioTipoCompetencia.CUSTOS){
			criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.PMU_SEQ.toString(), processamento.getSeq()));
		}
		else if(tipoCompetencia == DominioTipoCompetencia.FATURAMENTO){
			criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.COMP_FAT.toString(), processamento.getCompetenciaMesAno()));
		}
		
		criteria.add(Restrictions.eq("vproc." + VSigCustosCalculoProced.Fields.ATD_SEQ, atdSeq));
		
		if(!conveniosSelecionados.isEmpty()){
			criteria.add(Restrictions.in("vproc." + VSigCustosCalculoProced.Fields.CNV_CODIGO.toString(), conveniosSelecionados));
		}
		return executeCriteria(criteria);
	}
}