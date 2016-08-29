package br.gov.mec.aghu.ambulatorio.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.type.BooleanType;
import org.hibernate.type.ByteType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacProcedHospEspecialidades;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MamProcedEspecialidade;
import br.gov.mec.aghu.model.MamProcedimento;
import br.gov.mec.aghu.model.MamProcedimentoRealizado;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;


public class MamProcedimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamProcedimento> {
	
	private static final long serialVersionUID = 7730566442462808291L;

	public List<MamProcedimento> obterProcedimentosPeloProcedimentoEspecialDiverso(final Integer seq, final Short mpmSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimento.class);
		criteria.add(Restrictions.eq(MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO_SEQ.toString(), mpmSeq));
		criteria.add(Restrictions.ne(MamProcedimento.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.isNotNull(MamProcedimento.Fields.SEQ.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	public Boolean existeProcedimentoPorPedSeq(final Short seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimento.class);

		criteria.add(Restrictions.eq(MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO_SEQ.toString(), seq));
		
		return (executeCriteriaCount(criteria) > 0)?true:false;
	}
	
	/**
	 * Monta criteria de pesquisa por procedimentos que ja foram selecionados = realizados
	 * 
	 * @param consultaNumero
	 * @return
	 */	
	private DetachedCriteria montarCriteriaProcedimentoPorNumeroConsultaUnion1(Integer consultaNumero) {
		String aliasPrdRealizado = "pol";
		String aliasPrd = "prd";
		String aliasCid = "cid";
		String separador = ".";
		String phiSeq = "phiSeq";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class, aliasPrdRealizado);
		criteria.createAlias(MamProcedimentoRealizado.Fields.PROCEDIMENTO.toString(), aliasPrd, CriteriaSpecification.LEFT_JOIN); 
		criteria.createAlias(MamProcedimentoRealizado.Fields.CID.toString(), aliasCid, CriteriaSpecification.LEFT_JOIN);
		criteria.setProjection(Projections.projectionList()
					.add(Projections.distinct(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString())), "seq")
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.DESCRICAO.toString()), "descricao")
					.add(Projections.property(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.QUANTIDADE.toString()), "quantidade")
					.add(Projections.property(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.SITUACAO.toString()), "situacao")
					.add(Projections.property(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.PADRAO_CONSULTA.toString()), "padraoConsulta")
					.add(Projections.sqlProjection("null as " + phiSeq, new String[]{phiSeq}, new Type[]{IntegerType.INSTANCE}), phiSeq)
					.add(Projections.property(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.CID_SEQ.toString()), "cidSeq")
					.add(Projections.property(aliasCid + separador + AghCid.Fields.CODIGO), "cidCodigo")
					.add(Projections.property(aliasCid + separador + AghCid.Fields.DESCRICAO), "cidDescricao")
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO_SEQ.toString()), "pedSeq"));
		
		criteria.add(Restrictions.eq(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		criteria.add(Restrictions.isNull(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.DTHR_MOVIMENTO.toString()));
		criteria.add(Restrictions.or(Restrictions.eq(aliasPrd + separador + MamProcedimento.Fields.SITUACAO.toString(), DominioSituacao.A), 
				Restrictions.isNull(aliasPrd + separador + MamProcedimento.Fields.SITUACAO.toString())));
		
		return criteria;
	}
		
	/**
	 * Pesquisa por procedimentos que ja foram selecionados = realizados
	 * 
	 * @param consultaNumero
	 * @return
	 */
	public List<Object[]> pesquisarProcedimentoPorNumeroConsultaUnion1(Integer consultaNumero) {
		return executeCriteria(montarCriteriaProcedimentoPorNumeroConsultaUnion1(consultaNumero));
	}
	
	/**
	 * Monta criteria de pesquisa por procedimentos não realizados.
	 * Obs.: o ped esta associado a esp ou esp pai da consulta na PHP do FAT
	 * 
	 * @param consultaNumero
	 * @param espSeq
	 * @param paiEspSeq
	 * @return
	 */	
	private DetachedCriteria montarCriteriaProcedimentoPorNumeroConsultaUnion2(Integer consultaNumero, Short espSeq, Short paiEspSeq, List<Integer> listaSeqsExcluir) {
		String aliasPrd = "prd";
		String aliasPed = "ped";
		String aliasPhp = "php";
		String aliasPhi = "phi";
		String aliasPrdRealizado = "pol";
		String separador = ".";
		String cidSeq = "cidSeq";
		String cidCodigo = "cidCodigo";
		String cidDescricao = "cidDescricao";
		String quantidade = "quantidade";
		String situacao = "situacao";
		String padraoConsulta = "padraoConsulta";
		
		List<Short> especialidades = new ArrayList<Short>();
		especialidades.add(espSeq);
		especialidades.add(paiEspSeq);		
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimento.class, aliasPrd);
		criteria.createAlias(aliasPrd + separador + MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO.toString(), aliasPed);
		criteria.createAlias(aliasPed + separador + MpmProcedEspecialDiversos.Fields.PROCED_HOSP_INTERNO.toString(), aliasPhi);
		criteria.createAlias(aliasPhi + separador + FatProcedHospInternos.Fields.PROCED_HOSP_ESPECIALIDADE.toString(), aliasPhp);
		
		criteria.setProjection(Projections.projectionList()
					.add(Projections.distinct(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString())), "seq")
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.DESCRICAO.toString()), "descricao")
					.add(Projections.sqlProjection("1 as " + quantidade, new String[]{quantidade}, new Type[]{ByteType.INSTANCE}), quantidade)
					.add(Projections.sqlProjection("'" + DominioSituacao.I + "' as " + situacao, new String[]{situacao}, new Type[]{StringType.INSTANCE}), situacao)
					.add(Projections.sqlProjection("null as " + padraoConsulta, new String[]{padraoConsulta}, new Type[]{BooleanType.INSTANCE}), padraoConsulta)
					.add(Projections.property(aliasPhi + separador + FatProcedHospInternos.Fields.SEQ.toString()), "phiSeq")
					.add(Projections.sqlProjection("null as " + cidSeq, new String[]{cidSeq}, new Type[]{IntegerType.INSTANCE}), cidSeq)
					.add(Projections.sqlProjection("null as " + cidCodigo, new String[]{cidCodigo}, new Type[]{StringType.INSTANCE}), cidCodigo)
					.add(Projections.sqlProjection("null as " + cidDescricao, new String[]{cidDescricao}, new Type[]{StringType.INSTANCE}), cidDescricao)
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO_SEQ.toString()), "pedSeq"));
		
		criteria.add(Restrictions.in(aliasPhp + separador + AacProcedHospEspecialidades.Fields.ESPECIALIDADE_SEQ.toString(), especialidades));
		criteria.add(Restrictions.eq(aliasPhp + separador + AacProcedHospEspecialidades.Fields.CONSULTA.toString(), Boolean.FALSE));
		criteria.add(Restrictions.eq(aliasPrd + separador + MamProcedimento.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class, aliasPrdRealizado);
		subCriteria.setProjection(Projections.property(MamProcedimentoRealizado.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		subCriteria.add(Restrictions.eqProperty(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.PROCEDIMENTO_SEQ.toString(), aliasPrd
				+ separador + MamProcedimento.Fields.SEQ.toString()));
						
		criteria.add(Restrictions.not(Subqueries.exists(subCriteria)));
		
		// Distinct do UNION (necessario devido a ausência de UNION no Hibernate)
//		DetachedCriteria criteriaUnion1 = montarCriteriaProcedimentoPorNumeroConsultaUnion1(consultaNumero);
//		criteriaUnion1.setProjection(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString()));
//		List<Integer> listaSeqs = executeCriteria(criteriaUnion1);
		
		if (listaSeqsExcluir != null && !listaSeqsExcluir.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString(), listaSeqsExcluir)));	
		}

		
		return criteria;
	}
	
	/**
	 * Pesquisa por procedimentos não realizados.
	 * Obs.: o ped esta associado a esp ou esp pai da consulta na PHP do FAT
	 * 
	 * @param consultaNumero
	 * @param espSeq
	 * @param paiEspSeq
	 * @return
	 */
	public List<Object[]> pesquisarProcedimentoPorNumeroConsultaUnion2(Integer consultaNumero, Short espSeq, Short paiEspSeq, List<Integer> listaSeqsExcluir) {
		return executeCriteria(montarCriteriaProcedimentoPorNumeroConsultaUnion2(consultaNumero, espSeq, paiEspSeq, listaSeqsExcluir));
	}
	
	/**
	 * Monta criteria de pesquisa por procedimentos não realizados.
	 * Obs.: o procedimento é não faturável (ped é nulo)
	 *       e esta associado a esp ou esp pai da consulta na associativa do mam
	 * 
	 * @param consultaNumero
	 * @param espSeq
	 * @param paiEspSeq
	 * @return
	 */	
	private DetachedCriteria montarCriteriaProcedimentoPorNumeroConsultaUnion3(Integer consultaNumero, Short espSeq, Short paiEspSeq, List<Integer> listaSeqsExcluir) {
		String aliasPrd = "prd";
		String aliasPde = "pde";
		String aliasPrdRealizado = "pol";
		String separador = ".";
		String cidSeq = "cidSeq";
		String phiSeq = "phiSeq";
		String cidCodigo = "cidCodigo";
		String quantidade = "quantidade";
		String situacao = "situacao";
		String padraoConsulta = "padraoConsulta";
		String cidDescricao = "cidDescricao";
		
		List<Short> especialidades = new ArrayList<Short>();
		especialidades.add(espSeq);
		especialidades.add(paiEspSeq);	
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedEspecialidade.class, aliasPde);
		criteria.createAlias(MamProcedEspecialidade.Fields.PROCEDIMENTO.toString(), aliasPrd);
		criteria.setProjection(Projections.projectionList()
					.add(Projections.distinct(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString())), "seq")
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.DESCRICAO.toString()), "descricao")
					.add(Projections.sqlProjection("1 as " + quantidade, new String[]{quantidade}, new Type[]{ByteType.INSTANCE}), quantidade)
					.add(Projections.sqlProjection("'" + DominioSituacao.I + "' as " + situacao, new String[]{situacao}, new Type[]{StringType.INSTANCE}), situacao)
					.add(Projections.sqlProjection("null as " + padraoConsulta, new String[]{padraoConsulta}, new Type[]{BooleanType.INSTANCE}), padraoConsulta)
					.add(Projections.sqlProjection("null as " + phiSeq, new String[]{phiSeq}, new Type[]{IntegerType.INSTANCE}), phiSeq)
					.add(Projections.sqlProjection("null as " + cidSeq, new String[]{cidSeq}, new Type[]{IntegerType.INSTANCE}), cidSeq)
					.add(Projections.sqlProjection("null as " + cidCodigo, new String[]{cidCodigo}, new Type[]{StringType.INSTANCE}), cidCodigo)
					.add(Projections.sqlProjection("null as " + cidDescricao, new String[]{cidDescricao}, new Type[]{StringType.INSTANCE}), cidDescricao)					
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO_SEQ.toString()), "pedSeq"));
		
		criteria.add(Restrictions.in(aliasPde + separador + MamProcedEspecialidade.Fields.ESPECIALIDADE_SEQ.toString(), especialidades));
		criteria.add(Restrictions.eq(aliasPde + separador + MamProcedEspecialidade.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPrd + separador + MamProcedimento.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class, aliasPrdRealizado);
		subCriteria.setProjection(Projections.property(MamProcedimentoRealizado.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		subCriteria.add(Restrictions.eqProperty(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.PROCEDIMENTO_SEQ.toString(), aliasPrd
				+ separador + MamProcedimento.Fields.SEQ.toString()));
						
		criteria.add(Restrictions.not(Subqueries.exists(subCriteria)));
		
		// Distinct do UNION (necessario devido a ausência de UNION no Hibernate)
//		DetachedCriteria criteriaUnion2 = montarCriteriaProcedimentoPorNumeroConsultaUnion2(consultaNumero, espSeq, paiEspSeq);
//		criteriaUnion2.setProjection(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString()));
//		List<Integer> listaSeqs = executeCriteria(criteriaUnion2);
		
		if (listaSeqsExcluir != null && !listaSeqsExcluir.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString(), listaSeqsExcluir)));	
		}		
		
		return criteria;
	}
	
	/**
	 * Pesquisa por procedimentos não realizados.
	 * Obs.: o procedimento é não faturável (ped é nulo)
	 *       e esta associado a esp ou esp pai da consulta na associativa do mam
	 * 
	 * @param consultaNumero
	 * @param espSeq
	 * @param paiEspSeq
	 * @return
	 */
	public List<Object[]> pesquisarProcedimentoPorNumeroConsultaUnion3(Integer consultaNumero, Short espSeq, Short paiEspSeq, List<Integer> listaSeqsExcluir) {
		return executeCriteria(montarCriteriaProcedimentoPorNumeroConsultaUnion3(consultaNumero, espSeq, paiEspSeq, listaSeqsExcluir));
	}
	
	/**
	 * Pesquisa por procedimentos não realizados.
	 * Obs.: procedimento não faturavel (ped é nulo)
	 *       e não esta associado a nenhuma especialidade na associativa do mam
	 * 
	 * @param consultaNumero
	 * @return
	 */
	public DetachedCriteria montarCriteriaProcedimentoPorNumeroConsultaUnion4(Integer consultaNumero, Short espSeq, Short paiEspSeq, List<Integer> listaSeqsExcluir) {
		String aliasPrd = "prd";
		String aliasPrdRealizado = "pol";
		String separador = ".";
		String cidSeq = "cidSeq";
		String phiSeq = "phiSeq";
		String cidCodigo = "cidCodigo";
		String quantidade = "quantidade";
		String situacao = "situacao";
		String padraoConsulta = "padraoConsulta";
		String cidDescricao = "cidDescricao";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimento.class, aliasPrd);
		criteria.setProjection(Projections.projectionList()
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString()), "seq")
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.DESCRICAO.toString()), "descricao")
					.add(Projections.sqlProjection("1 as " + quantidade, new String[]{quantidade}, new Type[]{ByteType.INSTANCE}), quantidade)
					.add(Projections.sqlProjection("'" + DominioSituacao.I + "' as " + situacao, new String[]{situacao}, new Type[]{StringType.INSTANCE}), situacao)
					.add(Projections.sqlProjection("null as " + padraoConsulta, new String[]{padraoConsulta}, new Type[]{BooleanType.INSTANCE}), padraoConsulta)
					.add(Projections.sqlProjection("null as " + phiSeq, new String[]{phiSeq}, new Type[]{IntegerType.INSTANCE}), phiSeq)
					.add(Projections.sqlProjection("null as " + cidSeq, new String[]{cidSeq}, new Type[]{IntegerType.INSTANCE}), cidSeq)
					.add(Projections.sqlProjection("null as " + cidCodigo, new String[]{cidCodigo}, new Type[]{StringType.INSTANCE}), cidCodigo)
					.add(Projections.sqlProjection("null as " + cidDescricao, new String[]{cidDescricao}, new Type[]{StringType.INSTANCE}), cidDescricao)
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO_SEQ.toString()), "pedSeq"));
		
		criteria.add(Restrictions.eq(aliasPrd + separador + MamProcedimento.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(aliasPrd + separador + MamProcedimento.Fields.GENERICO.toString(), Boolean.TRUE));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class, aliasPrdRealizado);
		subCriteria.setProjection(Projections.property(MamProcedimentoRealizado.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		subCriteria.add(Restrictions.eqProperty(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.PROCEDIMENTO_SEQ.toString(), aliasPrd
				+ separador + MamProcedimento.Fields.SEQ.toString()));
						
		criteria.add(Restrictions.not(Subqueries.exists(subCriteria)));
		
		// Distinct do UNION (necessario devido a ausência de UNION no Hibernate)
//		DetachedCriteria criteriaUnion3 = montarCriteriaProcedimentoPorNumeroConsultaUnion3(consultaNumero, espSeq, paiEspSeq);
//		criteriaUnion3.setProjection(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString()));
//		List<Integer> listaSeqs = executeCriteria(criteriaUnion3);
		
		if (listaSeqsExcluir != null && !listaSeqsExcluir.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString(), listaSeqsExcluir)));	
		}	
		
		return criteria;
	}
	
	public List<Object[]> pesquisarProcedimentoPorNumeroConsultaUnion4(Integer consultaNumero, Short espSeq, Short paiEspSeq, List<Integer> listaSeqsExcluir) {
		return executeCriteria(montarCriteriaProcedimentoPorNumeroConsultaUnion4(consultaNumero, espSeq, paiEspSeq, listaSeqsExcluir));
	}	
	
	/**
	 * Pesquisa por procedimentos não realizados.
	 * Obs.: criteria busca procedimentos que 
	 *       fazem parte da consulta por default 
	 *       (select migrado da procedure P_GERA_PROCED_PADRAO).
	 * 
	 * @param consultaNumero
	 * @param espSeq 
	 * @param paiEspSeq 
	 * @return
	 */
	public List<Object[]> pesquisarProcedimentoPorNumeroConsultaUnion5(Integer consultaNumero, Short espSeq, Short paiEspSeq, List<Integer> listaSeqsExcluir) {
		String aliasPrd = "prd";
		String aliasPrdRealizado = "pol";
		String aliasPrh = "prh";
		String aliasPed = "ped";
		String aliasPhi = "phi";		
		String separador = ".";
		String cidSeq = "cidSeq";
		String phiSeq = "phiSeq";
		String cidCodigo = "cidCodigo";
		String quantidade = "quantidade";
		String situacao = "situacao";
		String padraoConsulta = "padraoConsulta";
		String cidDescricao = "cidDescricao";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimento.class, aliasPrd);
		criteria.createAlias(aliasPrd + separador + MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO.toString(), aliasPed, CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias(aliasPed + separador + MpmProcedEspecialDiversos.Fields.PROCED_HOSP_INTERNO.toString(), aliasPhi, CriteriaSpecification.LEFT_JOIN);
		criteria.createAlias(aliasPhi + separador + FatProcedHospInternos.Fields.CONSULTA_PROCED_HOSPITALAR.toString(), aliasPrh, CriteriaSpecification.LEFT_JOIN);
		
		criteria.setProjection(Projections.projectionList()
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString()), "seq")
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.DESCRICAO.toString()), "descricao")
					.add(Projections.property(aliasPrh + separador + AacConsultaProcedHospitalar.Fields.QUANTIDADE.toString()), quantidade)
					.add(Projections.sqlProjection("'" + DominioSituacao.I + "' as " + situacao, new String[]{situacao}, new Type[]{StringType.INSTANCE}), situacao)
					.add(Projections.sqlProjection("'1' as " + padraoConsulta, new String[]{padraoConsulta}, new Type[]{StringType.INSTANCE}), padraoConsulta)
					.add(Projections.property(aliasPrh + separador + AacConsultaProcedHospitalar.Fields.PHI_SEQ.toString()), phiSeq)
					.add(Projections.property(aliasPrh + separador + AacConsultaProcedHospitalar.Fields.CID_SEQ.toString()), cidSeq)
					.add(Projections.sqlProjection("null as " + cidCodigo, new String[]{cidCodigo}, new Type[]{StringType.INSTANCE}), cidCodigo)
					.add(Projections.sqlProjection("null as " + cidDescricao, new String[]{cidDescricao}, new Type[]{StringType.INSTANCE}), cidDescricao)
					.add(Projections.property(aliasPrd + separador + MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO_SEQ.toString()), "pedSeq"));
		
		criteria.add(Restrictions.eq(aliasPrh + separador + AacConsultaProcedHospitalar.Fields.CON_NUMERO, consultaNumero));
		criteria.add(Restrictions.eq(aliasPrh + separador + AacConsultaProcedHospitalar.Fields.IND_CONSULTA, Boolean.FALSE));
		criteria.add(Restrictions.or(Restrictions.eq(aliasPrd + separador + MamProcedimento.Fields.SITUACAO.toString(), DominioSituacao.A),
									Restrictions.isNull(aliasPrd + separador + MamProcedimento.Fields.SITUACAO.toString())));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(MamProcedimentoRealizado.class, aliasPrdRealizado);
		subCriteria.setProjection(Projections.property(MamProcedimentoRealizado.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.CON_NUMERO.toString(), consultaNumero));
		subCriteria.add(Restrictions.eqProperty(aliasPrdRealizado + separador + MamProcedimentoRealizado.Fields.PROCEDIMENTO_SEQ.toString(), aliasPrd
				+ separador + MamProcedimento.Fields.SEQ.toString()));
						
		criteria.add(Restrictions.not(Subqueries.exists(subCriteria)));
		
		// Distinct do UNION (necessario devido a ausência de UNION no Hibernate)
//		DetachedCriteria criteriaUnion4 = montarCriteriaProcedimentoPorNumeroConsultaUnion4(consultaNumero, espSeq, paiEspSeq);
//		criteriaUnion4.setProjection(Projections.property(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString()));
//		List<Integer> listaSeqs = executeCriteria(criteriaUnion4);
		
		if (listaSeqsExcluir != null && !listaSeqsExcluir.isEmpty()) {
			criteria.add(Restrictions.not(Restrictions.in(aliasPrd + separador + MamProcedimento.Fields.SEQ.toString(), listaSeqsExcluir)));	
		}
		
		return executeCriteria(criteria);
	}	
	
	/**
	 * Pesquisa pelos procedimentos que estiverem associados a um dado
	 * procedimento especial diverso
	 * @param seq
	 * @return
	 */
	public List<MamProcedimento> pesquisarProcedimentosComProcedEspecialDiverso(Short seq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamProcedimento.class);
		criteria.add(Restrictions.eq(MamProcedimento.Fields.PROCED_ESPECIAL_DIVERSO_SEQ.toString(), seq));
		return executeCriteria(criteria);
		
	}
}
