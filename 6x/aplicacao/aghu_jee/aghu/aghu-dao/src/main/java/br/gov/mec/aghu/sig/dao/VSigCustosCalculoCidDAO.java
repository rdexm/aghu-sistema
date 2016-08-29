package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioCidCustoPacienteDiagnostico;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.SigCalculoAtdCIDS;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.sig.custos.vo.VSigCustosCalculoCidVO;
import br.gov.mec.aghu.view.VSigCustosCalculoCid;

public class VSigCustosCalculoCidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VSigCustosCalculoCid> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3684912790292357952L;
	
	public List<VSigCustosCalculoCidVO> pesquisarDiagnosticosPrimeiroNivel(Integer pmuSeq, String cidPrincipal, List<AghCid> listaCids, DominioCidCustoPacienteDiagnostico tipo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VSigCustosCalculoCid.class, "ccc");
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("ccc." + VSigCustosCalculoCid.Fields.CID_SEQ.toString()), "cidSeq")
				.add(Projections.property("ccc." + VSigCustosCalculoCid.Fields.CID_CODIGO.toString()), "cidCodigo")
				.add(Projections.property("ccc." + VSigCustosCalculoCid.Fields.CID_DESCRICAO.toString()), "cidDescricao")
				.add(Projections.sqlProjection("sum(coalesce(CUSTO_TOTAL_INSUMOS,0) + coalesce(CUSTO_TOTAL_PESSOAS,0) + coalesce(CUSTO_TOTAL_EQUIPAMENTOS,0) + coalesce(CUSTO_TOTAL_SERVICOS,0)) custoTotal",
							new String[]{"custoTotal"}, new Type[] {BigDecimalType.INSTANCE}))
				.add(Projections.sqlProjection("sum(coalesce(RECEITA_TOTAL,0)) receitaTotal",
							new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}))
				.add(Projections.count(VSigCustosCalculoCid.Fields.CID_SEQ.toString()),VSigCustosCalculoCidVO.Fields.QUANTIDADE.toString())
				.add(Projections.groupProperty("ccc." + VSigCustosCalculoCid.Fields.CID_SEQ.toString()))
				.add(Projections.groupProperty("ccc." + VSigCustosCalculoCid.Fields.CID_CODIGO.toString()))
				.add(Projections.groupProperty("ccc." + VSigCustosCalculoCid.Fields.CID_DESCRICAO.toString()));	
		criteria.setProjection(projectionList);

		criteria.add(Restrictions.eq("ccc." + VSigCustosCalculoCid.Fields.PMU_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.eq("ccc." + VSigCustosCalculoCid.Fields.CID_IND_PRINCIPAL.toString(), true));
		if(StringUtils.isNotBlank(cidPrincipal)){
			criteria.add(Restrictions.eq("ccc." + VSigCustosCalculoCid.Fields.CID_DESCRICAO.toString(), cidPrincipal));	
		}
		
		if(!listaCids.isEmpty()){
			List<Integer> listaChavesCid = new ArrayList<Integer>();
			for(AghCid cid: listaCids){
				listaChavesCid.add(cid.getSeq());
			}
			if(tipo.equals(DominioCidCustoPacienteDiagnostico.Q)){
				DetachedCriteria subCriteria = DetachedCriteria.forClass(SigCalculoAtdCIDS.class, "cai");
				ProjectionList projectionSubList = Projections.projectionList()
						.add(Projections.property("cai." + SigCalculoAtdCIDS.Fields.SEQ.toString()), "seq");
				subCriteria.createAlias("cai."+SigCalculoAtdCIDS.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
				subCriteria.createAlias("cai."+SigCalculoAtdCIDS.Fields.CID.toString(), "cid");
				subCriteria.add(Restrictions.eqProperty("cac."+SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), "ccc."+VSigCustosCalculoCid.Fields.PMU_SEQ.toString()));
				subCriteria.add(Restrictions.eqProperty("cac."+SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), "ccc."+VSigCustosCalculoCid.Fields.ATD_SEQ.toString()));
				subCriteria.add(Restrictions.in("cid."+AghCid.Fields.SEQ.toString(), listaChavesCid));	
			    subCriteria.setProjection(projectionSubList);
			    
				criteria.add(Subqueries.exists(subCriteria));	
			} else {
				
				for(AghCid cid: listaCids){
					DetachedCriteria subCriteria = DetachedCriteria.forClass(SigCalculoAtdCIDS.class, "cai");
					ProjectionList projectionSubList = Projections.projectionList().add(Projections.property("cai." + SigCalculoAtdCIDS.Fields.SEQ.toString()), "seq");
					subCriteria.createAlias("cai."+SigCalculoAtdCIDS.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
					subCriteria.createAlias("cai."+SigCalculoAtdCIDS.Fields.CID.toString(), "cid");
					subCriteria.add(Restrictions.eqProperty("cac."+SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), "ccc."+VSigCustosCalculoCid.Fields.PMU_SEQ.toString()));
					subCriteria.add(Restrictions.eqProperty("cac."+SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), "ccc."+VSigCustosCalculoCid.Fields.ATD_SEQ.toString()));
					subCriteria.add(Restrictions.eq("cid."+AghCid.Fields.SEQ.toString(), cid.getSeq()));	
					subCriteria.setProjection(projectionSubList);
					criteria.add(Subqueries.exists(subCriteria));
				}
				
				//Não pode existir mais nenhum fora os cids da lista
				DetachedCriteria subCriteria = DetachedCriteria.forClass(SigCalculoAtdCIDS.class, "cai");
				ProjectionList projectionSubList = Projections.projectionList().add(Projections.property("cai." + SigCalculoAtdCIDS.Fields.SEQ.toString()), "seq");
				subCriteria.createAlias("cai."+SigCalculoAtdCIDS.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
				subCriteria.createAlias("cai."+SigCalculoAtdCIDS.Fields.CID.toString(), "cid");
				subCriteria.add(Restrictions.eqProperty("cac."+SigCalculoAtdPaciente.Fields.PMU_SEQ.toString(), "ccc."+VSigCustosCalculoCid.Fields.PMU_SEQ.toString()));
				subCriteria.add(Restrictions.eqProperty("cac."+SigCalculoAtdPaciente.Fields.ATD_SEQ.toString(), "ccc."+VSigCustosCalculoCid.Fields.ATD_SEQ.toString()));
				subCriteria.add(Restrictions.not(Restrictions.in("cid."+AghCid.Fields.SEQ.toString(), listaChavesCid)));	
				subCriteria.setProjection(projectionSubList);
				criteria.add(Subqueries.notExists(subCriteria));
			}
		}
		
		criteria.addOrder(Order.asc("ccc." + VSigCustosCalculoCid.Fields.CID_DESCRICAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(VSigCustosCalculoCidVO.class));
		return executeCriteria(criteria);
	}
	  
	public List<VSigCustosCalculoCidVO> pesquisarDiagnosticosSegundoNivel(Integer pmuSeq, Integer cidSeq, List<Integer> listaSeq, Integer quantidadeCids, DominioCidCustoPacienteDiagnostico tipo) {
		StringBuilder sb = new StringBuilder(1500);
		sb.append(" select ")
			.append(" res1.cidSeq as cidSeq, ")
			.append(" res1.id.cidCodigo as cidCodigo, ")
			.append(" res1.id.cidDescricao as cidDescricao, ")
			.append(" res1.id.atdSeq as atdSeq, ")
			.append(" sum( coalesce(res1.custoTotalInsumos,0) + coalesce(res1.custoTotalPessoas,0) + coalesce(res1.custoTotalEquipamentos,0) + coalesce(res1.custoTotalServicos,0) ) as custoTotal, ")
			.append(" sum( coalesce(res1.receitaTotal,0) ) as receitaTotal, ")
			.append(" count(res1.id.atdSeq) as quantidade, ")	
			.append(" res2.cidSeq as cidSeqSecundario, ")
			.append(" res2.id.cidCodigo as cidCodigoSecundario, ")
			.append(" res2.id.cidDescricao as cidDescricaoSecundario, ")
			.append(" sum( coalesce(res2.custoTotalInsumos,0) + coalesce(res2.custoTotalPessoas,0) + coalesce(res2.custoTotalEquipamentos,0) + coalesce(res2.custoTotalServicos,0) ) as custoTotalSecundario, ")
			.append(" sum( coalesce(res2.receitaTotal,0) ) as receitaTotalSecundario, ")
			.append(" count(res2.id.atdSeq) as quantidadeSecundario ")
		.append(" from ")
			.append(" SigCalculoAtdPaciente cac  ")
			.append(" inner join ").append(" cac.processamentoCusto ").append(" pmu  ")
			.append(" inner join ").append(" cac.sigCustosCalculoCids ").append(" res1 ")
			.append(" left outer join ").append(" cac.sigCustosCalculoCids ").append(" res2 ").append(" with( res2.id.principal=:parametroIndNaoPrincipal ) ")
		.append(" where ")
			.append(" res1.id.principal =:parametroIndPrincipal ")
			.append(" and pmu.seq =:parametroPmuSeq ")
			.append(" and res1.cidSeq =:parametroCidSeq ");
			
		//o tamanho da lista de seqs é igual a quantidade de cids selecionados
		boolean cidNaoEstaNaLista = quantidadeCids == listaSeq.size();
		boolean informouParametroLista = false;
		
		//Mais de um cid selecionado
		if(quantidadeCids > 1){
			
			//Todos, então os selecionados (fora ele) tem que ser secundário
			//Ou for Qualquer e não estiver entre os selecionados, então os selecionados tem que ser secundário
			if(tipo.equals(DominioCidCustoPacienteDiagnostico.T) || cidNaoEstaNaLista){
				sb.append(" and res2.cidSeq in ( :parametroListaSeq ) "); 
				informouParametroLista = true;
			}
		}
		//Somente um cid selecionado
		else if(quantidadeCids == 1){
			
			//Todos, tem que ser só o selecionado e estar como principal, então os secundários serão nulo
			if(tipo.equals(DominioCidCustoPacienteDiagnostico.T)){
				sb.append(" and res2.cidSeq is null");
			}
			//Qualquer, e este não for o selecionado, logo o selecionado tem que ser um secundário
			else if(cidNaoEstaNaLista){
				sb.append(" and res2.cidSeq in ( :parametroListaSeq ) "); 
				informouParametroLista = true;
			}
		}
		
		sb.append(" group by res1.cidSeq, res1.id.cidCodigo, res1.id.cidDescricao, res1.id.atdSeq, res2.cidSeq, res2.id.cidCodigo, res2.id.cidDescricao  ");
		sb.append(" order by res1.id.atdSeq, res2.id.cidDescricao ");
		
		final org.hibernate.Query query = this.createHibernateQuery(sb.toString());
		query.setParameter("parametroIndNaoPrincipal", false);
		query.setParameter("parametroIndPrincipal", true);
		query.setParameter("parametroPmuSeq", pmuSeq);
		query.setParameter("parametroCidSeq", cidSeq);
		
		if(informouParametroLista){
			query.setParameterList("parametroListaSeq", listaSeq);	
		}
		
		query.setResultTransformer(Transformers.aliasToBean(VSigCustosCalculoCidVO.class));
		return query.list();
	}
	
	public List<VSigCustosCalculoCidVO> obterListaPacientes(Integer pmuSeq, List<Integer> listaAtendimentos, Integer cidSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VSigCustosCalculoCid.class, "ccc");
		
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.property("atd." + AghAtendimentos.Fields.SEQ.toString()), "atdSeq")
				.add(Projections.property("atd." + AghAtendimentos.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property("pac." + AipPacientes.Fields.NOME.toString()), "pacNome")
				.add(Projections.sqlProjection("sum(coalesce(CUSTO_TOTAL_INSUMOS,0) + coalesce(CUSTO_TOTAL_PESSOAS,0) + coalesce(CUSTO_TOTAL_EQUIPAMENTOS,0) + coalesce(CUSTO_TOTAL_SERVICOS,0)) custoTotal",
							new String[]{"custoTotal"}, new Type[] {BigDecimalType.INSTANCE}))
				.add(Projections.sqlProjection("sum(coalesce(RECEITA_TOTAL, 0)) receitaTotal",
							new String[]{"receitaTotal"}, new Type[] {BigDecimalType.INSTANCE}))
				.add(Projections.groupProperty("atd." + AghAtendimentos.Fields.SEQ.toString()))
				.add(Projections.groupProperty("atd." + AghAtendimentos.Fields.PRONTUARIO.toString()))
				.add(Projections.groupProperty("pac." + AipPacientes.Fields.NOME.toString()));	
		criteria.setProjection(projectionList);
		
		criteria.createAlias("ccc."+VSigCustosCalculoCid.Fields.ATENDIMENTO.toString(), "atd");
		criteria.createAlias("atd."+AghAtendimentos.Fields.PACIENTE.toString(), "pac");
		criteria.add(Restrictions.eq("ccc." + VSigCustosCalculoCid.Fields.CID_IND_PRINCIPAL.toString(), true));
		criteria.add(Restrictions.eq("ccc." + VSigCustosCalculoCid.Fields.PMU_SEQ.toString(), pmuSeq));
		criteria.add(Restrictions.in("atd." + AghAtendimentos.Fields.SEQ.toString(), listaAtendimentos));
		criteria.addOrder(Order.asc("pac." + AipPacientes.Fields.NOME.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(VSigCustosCalculoCidVO.class));
		return executeCriteria(criteria);
	}
}
