package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StringType;
import br.gov.mec.aghu.model.FatRetornaAih;
import br.gov.mec.aghu.model.FatRetornaAihId;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class FatRetornaAihDAO extends BaseDao<FatRetornaAih> {
	
	private static final long serialVersionUID = -6047818948736904099L;

	public void removeAll() {
		StringBuilder hql = new StringBuilder();
		hql.append("DELETE ").append(FatRetornaAih.class.getName());
		Query query = createHibernateQuery(hql.toString());
		query.executeUpdate();
	}
	
	public List<FatRetornaAih> obterCursorDeContaParaAtualizar() {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatRetornaAih.class);
		criteria.addOrder(Order.asc(FatRetornaAih.Fields.IND_TIPO_LAUDO.toString()));
		return executeCriteria(criteria);
	}
	
	// metodo em sql nativo pois a tabela não tem chave primaria e não faz os binds corretos na entidade
	public List<FatRetornaAihId> obterCursorDeContaParaAtualizarNativo(){
		
		StringBuilder hql = new StringBuilder(530);
		hql	
			.append("select "
					+ " nro_laudo        nroLaudo , "
					+ " cth_seq         cthSeq , "
					+ " nro_cpf_auditor nroCpfAuditor  , "
					+ " nro_aih         nroAih, "
					+ " dt_emissao_aih  dtEmissaoAih, "
					+ " cod_sus_aut     codSusAut, "
					+ " ind_tipo_laudo  indTipoLaudo, "
					+ " cod_sus_cma1     codSusCma1, "
					+ " cod_sus_cma2     codSusCma2, "
					+ " cod_sus_cma3     codSusCma3, "
					+ " cod_sus_cma4     codSusCma4, "
					+ " cod_sus_cma5     codSusCma5, "
					+ " nro_cns_auditor  nroCnsAuditor "
					+ " FROM AGH.fat_retorna_aihs "
					+ " ORDER BY ind_tipo_laudo ");

		
		SQLQuery query = createSQLQuery(hql.toString());

		List<FatRetornaAihId> list = query.addScalar("nroLaudo", LongType.INSTANCE)
									 	.addScalar("cthSeq", IntegerType.INSTANCE)
									 	.addScalar("nroCpfAuditor", LongType.INSTANCE)
									 	.addScalar("nroAih", LongType.INSTANCE)
									 	.addScalar("dtEmissaoAih", DateType.INSTANCE)
									 	.addScalar("codSusAut", LongType.INSTANCE)
									 	.addScalar("indTipoLaudo", StringType.INSTANCE)
									 	.addScalar("codSusCma1", LongType.INSTANCE)
									 	.addScalar("codSusCma2", IntegerType.INSTANCE)
									 	.addScalar("codSusCma3", IntegerType.INSTANCE)
									 	.addScalar("codSusCma4", IntegerType.INSTANCE)
									 	.addScalar("codSusCma5", IntegerType.INSTANCE)
									 	.addScalar("nroCnsAuditor", LongType.INSTANCE)
									 	.setResultTransformer(Transformers.aliasToBean(FatRetornaAihId.class)).list();
		
		return list;
	}
}
