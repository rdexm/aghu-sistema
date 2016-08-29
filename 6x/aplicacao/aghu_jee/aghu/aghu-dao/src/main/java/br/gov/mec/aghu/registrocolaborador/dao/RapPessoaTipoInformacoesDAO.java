package br.gov.mec.aghu.registrocolaborador.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Table;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.model.RapPessoaTipoInformacoes;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoInformacoes;
import br.gov.mec.aghu.registrocolaborador.vo.CursorBuscaCboVO;
import br.gov.mec.aghu.registrocolaborador.vo.PessoaTipoInformacoesVO;

/**
 * 
 * @modulo registrocolaborador
 *
 */
public class RapPessoaTipoInformacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<RapPessoaTipoInformacoes> {

	private static final long serialVersionUID = 3260507751530477864L;

	@Override
	protected void obterValorSequencialId(RapPessoaTipoInformacoes elemento) {

		if (elemento == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		final Integer seq = this.getNextVal(SequenceID.RAP_PTI_SQ1);
		elemento.getId().setSeq(seq.longValue());
	}

	protected DetachedCriteria obterCriteriaPorPessoaFisicaTipoInformacoes(Integer pesCodigo, Short[] codTipoInformacoes) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class);
		result.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.PES_CODIGO.toString(), pesCodigo));
		result.add(Restrictions.in(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), codTipoInformacoes));

		return result;
	}

	/**
	 * Implementa do cursor <code>c_get_valor_cbo</code>
	 * 
	 * @param pesCodigo
	 * @param codTipoInformacoes
	 * @return
	 */
	public List<RapPessoaTipoInformacoes> listarPorPessoaFisicaTipoInformacao(Integer pesCodigo, Short[] codTipoInformacoes, final Date dtRealizado) {
		DetachedCriteria criteria = this.obterCriteriaPorPessoaFisicaTipoInformacoes(pesCodigo, codTipoInformacoes, dtRealizado);
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaPorPessoaFisicaTipoInformacoes(Integer pesCodigo, Short[] codTipoInformacoes, Date dtRealizado) {
		DetachedCriteria criteria = obterCriteriaPorPessoaFisicaTipoInformacoes(pesCodigo, codTipoInformacoes);
		// and trunc(c_data) between PII.DT_inicio and
		// nvl(PII.DT_FIM,trunc(sysdate))
		criteria.add(Restrictions
				.sqlRestriction(
						isOracle() ? " trunc(?) between {alias}.DT_INICIO and nvl({alias}.DT_FIM, trunc(sysdate)) "
								: " date_trunc('month', ?::timestamp) between {alias}.DT_INICIO and case when {alias}.DT_FIM is null then date_trunc('month', now()) else {alias}.DT_FIM end ",
						dtRealizado, DateType.INSTANCE));
		return criteria;
	}

	@SuppressWarnings("unchecked")
	public List<PessoaTipoInformacoesVO> pesquisarPessoaTipoInformacoes(Integer pesCodigo, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {

		StringBuilder sql = montarConsulta();
		sql.append(" ORDER BY RPTI.TII_SEQ ");

		SQLQuery query = createSQLQuery(sql.toString());
		query.setParameter("PMR_PES_CODIGO", pesCodigo);
		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);

		final List<PessoaTipoInformacoesVO> vos = query.addScalar(PessoaTipoInformacoesVO.Fields.PES_CODIGO.toString(), IntegerType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.PES_NOME.toString(), StringType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.TII_SEQ.toString(), ShortType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.SEQ.toString(), LongType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.VALOR.toString(), StringType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.CRIADO_EM.toString(), DateType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.ALTERADO_EM.toString(), DateType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.DT_INICIO.toString(), TimestampType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.DT_FIM.toString(), TimestampType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.TIPO_INFORMACAO_DESCRICAO.toString(), StringType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.CBO_DESCRICAO.toString(), StringType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.MATRICULA_CRIADO_POR.toString(), IntegerType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.VIN_CODIGO_CRIADO_POR.toString(), ShortType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.NOME_CRIADO_POR.toString(), StringType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.MATRICULA_ALTERADO_POR.toString(), IntegerType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.VIN_CODIGO_ALTERADO_POR.toString(), ShortType.INSTANCE)
				.addScalar(PessoaTipoInformacoesVO.Fields.NOME_ALTERADO_POR.toString(), StringType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PessoaTipoInformacoesVO.class)).list();

		return vos;
	}

	@SuppressWarnings("unchecked")
	public Long pesquisarPessoaTipoInformacoesCount(Integer pesCodigo) {

		final SQLQuery query = createSQLQuery(montarConsulta().toString());
		query.setParameter("PMR_PES_CODIGO", pesCodigo);

		final List<PessoaTipoInformacoesVO> vos = query.addScalar(PessoaTipoInformacoesVO.Fields.PES_CODIGO.toString(), IntegerType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(PessoaTipoInformacoesVO.class)).list();

		if (vos != null && !vos.isEmpty()) {
			return (long) vos.size();
		} else {
			return 0L;
		}
	}

	private StringBuilder montarConsulta() {
		final StringBuilder sql = new StringBuilder(1400);

		sql.append(" SELECT ").append("           RPF.NOME AS ").append(PessoaTipoInformacoesVO.Fields.PES_NOME.toString())

		.append("        , RPTI.PES_CODIGO AS ").append(PessoaTipoInformacoesVO.Fields.PES_CODIGO.toString()).append("        , RPTI.TII_SEQ AS ")
				.append(PessoaTipoInformacoesVO.Fields.TII_SEQ.toString()).append("        , RPTI.SEQ     AS ")
				.append(PessoaTipoInformacoesVO.Fields.SEQ.toString()).append("        , RPTI.VALOR   AS ")
				.append(PessoaTipoInformacoesVO.Fields.VALOR.toString()).append("        , RPTI.CRIADO_EM AS ")
				.append(PessoaTipoInformacoesVO.Fields.CRIADO_EM.toString()).append("        , RPTI.ALTERADO_EM AS ")
				.append(PessoaTipoInformacoesVO.Fields.ALTERADO_EM.toString()).append("        , RPTI.DT_INICIO AS ")
				.append(PessoaTipoInformacoesVO.Fields.DT_INICIO.toString()).append("        , RPTI.DT_FIM AS ")
				.append(PessoaTipoInformacoesVO.Fields.DT_FIM.toString())

				.append("        ,  RTI.DESCRICAO AS ").append(PessoaTipoInformacoesVO.Fields.TIPO_INFORMACAO_DESCRICAO.toString())

				.append("        , CASE WHEN (SELECT COUNT(CBO.CODIGO) FROM AGH.FAT_CBOS CBO WHERE CBO.CODIGO = RPTI.VALOR) > 1 THEN ")
				.append(" 			        (SELECT CBO.DESCRICAO FROM AGH.FAT_CBOS CBO WHERE CBO.CODIGO = RPTI.VALOR AND DT_FIM IS NULL) ELSE ")
				.append(" 			        (SELECT CBO.DESCRICAO FROM AGH.FAT_CBOS CBO WHERE CBO.CODIGO = RPTI.VALOR )").append(" 		  END AS ")
				.append(PessoaTipoInformacoesVO.Fields.CBO_DESCRICAO.toString())

				.append("        , RSCRI.MATRICULA AS ").append(PessoaTipoInformacoesVO.Fields.MATRICULA_CRIADO_POR.toString())
				.append("        , RSCRI.VIN_CODIGO AS ").append(PessoaTipoInformacoesVO.Fields.VIN_CODIGO_CRIADO_POR.toString())
				.append("        , RSCRIPES.NOME AS ").append(PessoaTipoInformacoesVO.Fields.NOME_CRIADO_POR.toString())

				.append("        , RSALT.MATRICULA AS ").append(PessoaTipoInformacoesVO.Fields.MATRICULA_ALTERADO_POR.toString())
				.append("        , RSALT.VIN_CODIGO AS ").append(PessoaTipoInformacoesVO.Fields.VIN_CODIGO_ALTERADO_POR.toString())
				.append("        , RSALTPES.NOME AS ").append(PessoaTipoInformacoesVO.Fields.NOME_ALTERADO_POR.toString())

				.append(" FROM       AGH.").append(RapPessoaTipoInformacoes.class.getAnnotation(Table.class).name()).append(" RPTI ")
				.append("  LEFT JOIN AGH.").append(RapPessoasFisicas.class.getAnnotation(Table.class).name())
				.append(" RPF ON RPF.CODIGO = RPTI.PES_CODIGO ")

				.append("  LEFT JOIN AGH.").append(RapTipoInformacoes.class.getAnnotation(Table.class).name())
				.append(" RTI ON RTI.SEQ = RPTI.TII_SEQ ")

				.append("  LEFT JOIN AGH.").append(RapServidores.class.getAnnotation(Table.class).name())
				.append(" RSCRI ON RSCRI.MATRICULA = RPTI.SER_MATRICULA_CRIADO ").append(" 	   AND RSCRI.VIN_CODIGO = RPTI.SER_VIN_CODIGO_CRIADO ")

				.append("  LEFT JOIN AGH.").append(RapPessoasFisicas.class.getAnnotation(Table.class).name())
				.append(" RSCRIPES ON RSCRIPES.CODIGO = RSCRI.PES_CODIGO ")

				.append("  LEFT JOIN AGH.").append(RapServidores.class.getAnnotation(Table.class).name())
				.append(" RSALT ON RSALT.MATRICULA = RPTI.SER_MATRICULA_ALTERADO ")
				.append("      AND RSALT.VIN_CODIGO = RPTI.SER_VIN_CODIGO_ALTERADO ")

				.append("  LEFT JOIN AGH.").append(RapPessoasFisicas.class.getAnnotation(Table.class).name())
				.append(" RSALTPES ON RSALTPES.CODIGO = RSALT.PES_CODIGO ")

				.append(" WHERE ").append(" 	RPTI.PES_CODIGO = :PMR_PES_CODIGO ");

		return sql;

	}

	public List<RapPessoaTipoInformacoes> listarRapPessoaTipoInformacoesPorPesCodigoTiiSeq(final Integer pesCodigo, final Short tiiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class);

		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.PES_CODIGO.toString(), pesCodigo));
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), tiiSeq));

		return executeCriteria(criteria);
	}

	/**
	 * DAOTest: RapPessoaTipoInformacoesDAOTest.obterRapPessoaTipoInformacoesPorPesCPFTiiSeq (Retestar qd alterado) eSchweigert 17/09/2012
	 */
	public String obterRapPessoaTipoInformacoesPorPesCPFTiiSeq(final Long cpf, final Short tiiSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class);

		criteria.createAlias(RapPessoaTipoInformacoes.Fields.PESSOAS_FISICA.toString(), "pes");
		criteria.add(Restrictions.eq("pes."+RapPessoasFisicas.Fields.CPF.toString(), cpf));
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), tiiSeq));
		criteria.setProjection(Projections.property(RapPessoaTipoInformacoes.Fields.VALOR.toString()));
		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	public String buscaCbo(Integer matricula, Short vinCodigo, Short[] codTipoInformacoes) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapServidores.class, "SER");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.createAlias("PES." + RapPessoasFisicas.Fields.TIPO_INFORMACAO.toString(), "TII");
		criteria.add(Restrictions.eq("SER."+RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("SER."+RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigo));
		criteria.add(Restrictions.in("TII."+RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), codTipoInformacoes));
		criteria.addOrder(Order.asc("TII."+RapPessoaTipoInformacoes.Fields.TII_SEQ.toString()));
		criteria.setProjection(Projections.property("TII."+RapPessoaTipoInformacoes.Fields.VALOR.toString()));
		
		List<String> result = executeCriteria(criteria);
		if(!result.isEmpty()) {
			return result.get(0);
		}
		
		return null;
	}
	
	public Long qtdPorPessoaFisicaTipoInformacao(Integer pesCodigo, Short[] codTipoInformacoes) {

		Long result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorPessoaFisicaTipoInformacoes(pesCodigo, codTipoInformacoes);
		result = this.executeCriteriaCount(criteria);

		return result;
	}

	public boolean existePessoaFisicaComTipoInformacao(Integer pesCodigo, Short[] codTipoInformacoes) {

		boolean result = false;
		Long qtd = null;

		qtd = this.qtdPorPessoaFisicaTipoInformacao(pesCodigo, codTipoInformacoes);
		result = (qtd != null) && (qtd.intValue() > 0);

		return result;
	}

	/**
	 * Implementa do cursor <code>c_ver_cbo_anestesista</code>
	 * 
	 * @param pesCodigo
	 * @param valorTipoInformacao
	 * @return
	 */
	public String listarPorPessoaFisicaValorTipoInformacao(Integer pesCodigo, String[] valorTipoInformacao, final Date dtRealizado) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class);
		criteria.setProjection(Projections.property(RapPessoaTipoInformacoes.Fields.VALOR.toString()));
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.PES_CODIGO.toString(), pesCodigo));

		criteria.createAlias(RapPessoaTipoInformacoes.Fields.TIPO_INFORMACAO.toString(), RapPessoaTipoInformacoes.Fields.TIPO_INFORMACAO.toString());
		// Alterado SQL portaria
		/*
		 * and ( (pii.valor = '223104' and c_data between pii.dt_inicio and
		 * nvl(pii.dt_fim,trunc(sysdate))) or (pii.valor = '225151' and c_data
		 * between pii.dt_inicio and nvl(pii.dt_fim,trunc(sysdate))) );
		 */
		// criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.VALOR.toString(),
		// valorTipoInformacao));
		Criterion criterion = Restrictions
				.sqlRestriction(
						isOracle() ? " ? between {alias}.DT_INICIO and nvl({alias}.DT_FIM, trunc(sysdate)) "
								: "  ? between {alias}.DT_INICIO and case when {alias}.DT_FIM is null then date_trunc('month', now()) else {alias}.DT_FIM end ",
						dtRealizado, DateType.INSTANCE);
		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.eq(RapPessoaTipoInformacoes.Fields.VALOR.toString(), valorTipoInformacao[0]), criterion),
				Restrictions.and(Restrictions.eq(RapPessoaTipoInformacoes.Fields.VALOR.toString(), valorTipoInformacao[1]), criterion)));

		List<Object> result = executeCriteria(criteria);
		if (result == null || result.isEmpty()) {
			return null;
		}

		return result.get(0).toString();
	}

	/**
	 * Implementa cursor <code>c_busca_cbos</code>
	 * 
	 * @param pesCodigo
	 * @param tipoInf
	 * @param tipoInfSec
	 * @return
	 */
	@SuppressWarnings("ucd")
	public List<CursorBuscaCboVO> listarPessoaTipoInformacoes(Integer pesCodigo, Short tipoInf, Short tipoInfSec) {
		DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class);

		criteria.setProjection(Projections.projectionList().add(Projections.property(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString()), "tiiSeq")
				.add(Projections.property(RapPessoaTipoInformacoes.Fields.VALOR.toString()), "valor"));
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), tipoInf));
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.PES_CODIGO.toString(), pesCodigo));
		criteria.addOrder(Order.asc(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(CursorBuscaCboVO.class));
		List<CursorBuscaCboVO> lista = executeCriteria(criteria);
		List<CursorBuscaCboVO> retorno = new ArrayList<CursorBuscaCboVO>();
		for (CursorBuscaCboVO item : lista) {
			retorno.add(item);
		}

		// union
		criteria = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class);

		criteria.setProjection(Projections.projectionList().add(Projections.property(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString()), "tiiSeq")
				.add(Projections.property(RapPessoaTipoInformacoes.Fields.VALOR.toString()), "valor"));
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), tipoInfSec));
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.PES_CODIGO.toString(), pesCodigo));
		criteria.addOrder(Order.asc(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(CursorBuscaCboVO.class));
		lista = executeCriteria(criteria);
		for (CursorBuscaCboVO item : lista) {
			retorno.add(item);
		}

		return retorno;
	}

	/**
	 * 
	 * ORADB: FATK_CAP_UNI.RN_CAPC_CBO_PROC_RES.c_get_valor_cbo
	 * 
	 * @param pesCodigo
	 * @param tipoInf
	 * @param tipoInfSec
	 * @return
	 */
	public List<CursorBuscaCboVO> listarPessoaTipoInformacoes(final Integer pesCodigo, final Date dtRealizado, List<Short> resultSeqTipoInformacaoShort, Date ultimoDiaMes) {
		/*
		 * SELECT pii.tii_seq, SUBSTR(pii.valor,1,6) valor FROM
		 * rap_pessoa_tipo_informacoes pii WHERE pii.pes_codigo = c_pes_codigo
		 * AND pii.tii_seq IN (SELECT seq FROM rap_tipo_informacoes WHERE
		 * descricao LIKE '%CBO%') AND c_dt_realizacao BETWEEN dt_inicio AND
		 * NVL(dt_fim,TRUNC(LAST_DAY(SYSDATE))) ORDER BY pii.tii_seq ;
		 */

		final DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class);

		criteria.setProjection(Projections.projectionList().add(Projections.property(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString()), "tiiSeq")
				.add(Projections.property(RapPessoaTipoInformacoes.Fields.VALOR.toString()), "valor"));

		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.PES_CODIGO.toString(), pesCodigo));

		//esse subselect foi movido para fora dessa consulta, assim dentro do loop de faturamento do ambulatorio
		//essa parte será executada apenas uma vez e passada como parametro.
		
		// AND pii.tii_seq IN (SELECT seq FROM rap_tipo_informacoes WHERE
		// descricao LIKE '%CBO%')
		
		if(resultSeqTipoInformacaoShort != null){
			criteria.add(Restrictions.in(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), resultSeqTipoInformacaoShort));
		}

		// AND c_dt_realizacao BETWEEN dt_inicio AND
		// NVL(dt_fim,TRUNC(LAST_DAY(SYSDATE)))
		criteria.add(Restrictions.sqlRestriction(" ? BETWEEN {alias}.dt_inicio AND CASE WHEN {alias}.dt_fim IS NULL THEN ? ELSE {alias}.dt_fim END ",
				new Object[] { dtRealizado, ultimoDiaMes }, new Type[] { TimestampType.INSTANCE, TimestampType.INSTANCE }));

		criteria.addOrder(Order.asc(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(CursorBuscaCboVO.class));

		return executeCriteria(criteria);
	}
	
	public RapPessoaTipoInformacoes obterRapPessoa(Integer codigoPessoa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class, "PTI");
		criteria.createAlias("PTI." + RapPessoaTipoInformacoes.Fields.TIPO_INFORMACAO.toString(), "TII");
		
		criteria.add(Restrictions.eq("PTI." + RapPessoaTipoInformacoes.Fields.PES_CODIGO.toString(), codigoPessoa));
		criteria.add(Restrictions.ilike("TII." + RapTipoInformacoes.Fields.DESCRICAO.toString(), "CARTAO SUS (CNS)"));
		
		return (RapPessoaTipoInformacoes) this.executeCriteriaUniqueResult(criteria);		
	}
	
	public RapPessoaTipoInformacoes obterTipoInformacao(Integer pesCodigo, Short seqTipoInf){
		RapPessoaTipoInformacoes retorno = null;
		final DetachedCriteria criteria = DetachedCriteria.forClass(RapPessoaTipoInformacoes.class);
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.PES_CODIGO.toString(), pesCodigo));
		criteria.add(Restrictions.eq(RapPessoaTipoInformacoes.Fields.TII_SEQ.toString(), seqTipoInf));
		List<RapPessoaTipoInformacoes> listaCbosPrincipal = executeCriteria(criteria);
		if (!listaCbosPrincipal.isEmpty()){
			retorno = listaCbosPrincipal.get(0);
		}
		return retorno;
	}
}