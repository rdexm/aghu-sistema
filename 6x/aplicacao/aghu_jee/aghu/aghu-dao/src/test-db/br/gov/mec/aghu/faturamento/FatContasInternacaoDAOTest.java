package br.gov.mec.aghu.faturamento;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.dao.FatContasInternacaoDAO;
import br.gov.mec.aghu.faturamento.vo.FatContasIntPacCirurgiasVO;

public class FatContasInternacaoDAOTest extends AbstractDAOTest<FatContasInternacaoDAO> {

	@Override
	protected FatContasInternacaoDAO doDaoUnderTests() {
		return new FatContasInternacaoDAO() {
			private static final long serialVersionUID = -5985789490676382832L;

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return FatContasInternacaoDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return FatContasInternacaoDAOTest.this.isOracle();
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return FatContasInternacaoDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}

	@Override
	protected void finalizeMocks() {
		
	}
	

	public static class CthIntSeqVO {

		private Integer cthSeq = null;
		private Integer intSeq = null;

		public CthIntSeqVO() {

			super();
		}

		public CthIntSeqVO(final Integer cthSeq, final Integer intSeq) {

			super();

			this.cthSeq = cthSeq;
			this.intSeq = intSeq;
		}

		public Integer getCthSeq() {

			return this.cthSeq;
		}

		public void setCthSeq(final Integer cthSeq) {

			this.cthSeq = cthSeq;
		}

		public Integer getIntSeq() {

			return this.intSeq;
		}

		public void setIntSeq(final Integer intSeq) {

			this.intSeq = intSeq;
		}

		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;
			result = prime * result + ((this.intSeq == null)
					? 0
					: this.intSeq.hashCode());
			result = prime * result + ((this.cthSeq == null)
					? 0
					: this.cthSeq.hashCode());
			return result;
		}

		@Override
		public boolean equals(final Object obj) {

			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (!(obj instanceof CthIntSeqVO)) {
				return false;
			}
			CthIntSeqVO other = (CthIntSeqVO) obj;
			if (this.intSeq == null) {
				if (other.intSeq != null) {
					return false;
				}
			} else if (!this.intSeq.equals(other.intSeq)) {
				return false;
			}
			if (this.cthSeq == null) {
				if (other.cthSeq != null) {
					return false;
				}
			} else if (!this.cthSeq.equals(other.cthSeq)) {
				return false;
			}
			return true;
		}
	}
	
	/**
	 * 
	 */
	private List<CthIntSeqVO> hqlListarParaCsvContasPeriodoViaInt(final Short cnvCodigo, final Byte cspSeq, final DominioSituacaoConta... indSituacao) {

		List<CthIntSeqVO> result = null;
		StringBuffer sql = null;
		SQLQuery query = null;
		String[] indSit = null;

		sql = new StringBuffer();
		sql.append(" select ");
		sql.append(" cth.seq as cthSeq");
		sql.append(" , coi.int_seq as intSeq");
		//		  ain_internacoes int left join AGH_UNIDADES_FUNCIONAIS unf on (unf.seq = int.unf_seq),
		sql.append(" from AGH.AIN_INTERNACOES int left join AGH.AGH_UNIDADES_FUNCIONAIS unf on (unf.seq = int.UNF_SEQ )");
		sql.append(" , AGH.AIP_PACIENTES pac");
		sql.append(" , AGH.FAT_CONTAS_INTERNACAO coi");
		sql.append(" , AGH.FAT_CONTAS_HOSPITALARES cth");
		//		pac.codigo = int.pac_codigo
		sql.append(" where pac.codigo = int.pac_codigo");
		//		AND int.seq = coi.int_seq
		sql.append(" 	and int.seq = coi.int_seq");
		//		AND coi.cth_seq = cth.seq
		sql.append(" 	and coi.cth_seq = cth.seq");
		//		AND cth.ind_situacao IN ('A','F','E')
		sql.append(" 	and cth.ind_situacao in ( :ind_sit )");
		//		AND cth.dt_alta_administrativa > timestamp '2004-08-24 23:59:59'
		//sql.append("    and cth.dt_alta_administrativa > timestamp '2004-08-24 23:59:59'");
		//		AND cth.csp_cnv_codigo = 1
		sql.append(" 	and cth.csp_cnv_codigo = :csp_cod");
		//		AND cth.csp_seq = 1
		sql.append(" 	and cth.csp_seq = :csp_seq");
		//		order by cth.seq asc;
		sql.append(" order by coi.seq asc");
		query = ((Session) entityManager.getDelegate()).createSQLQuery(sql.toString());
		indSit = new String[indSituacao.length];
		for (int i = 0; i < indSituacao.length; i++) {
			indSit[i] = indSituacao[i].name();
		}
		query.setParameterList("ind_sit", indSit);
		query.setParameter("csp_cod", cnvCodigo);
		query.setParameter("csp_seq", cspSeq);
		query.addScalar("cthSeq", IntegerType.INSTANCE);
		query.addScalar("intSeq", IntegerType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(CthIntSeqVO.class));
		result = query.list();

		return result;

	}

	@SuppressWarnings("unchecked")
	private List<CthIntSeqVO> hqlListarParaCsvContasPeriodoViaDcs(final Short cnvCodigo, final Byte cspSeq, final DominioSituacaoConta... indSituacao) {

		List<CthIntSeqVO> result = null;
		StringBuffer sql = null;
		SQLQuery query = null;
		String[] indSit = null;

		sql = new StringBuffer();
		sql.append(" select ");
		sql.append(" cth.seq as cthSeq");
		sql.append(" , coi.dcs_seq as intSeq");
		//		  ain_internacoes int left join AGH_UNIDADES_FUNCIONAIS unf on (unf.seq = int.unf_seq),
		sql.append(" from AGH.FAT_DADOS_CONTA_SEM_INT dcs left join AGH.AGH_UNIDADES_FUNCIONAIS unf on (unf.seq = dcs.UNF_SEQ )");
		sql.append(" , AGH.AIP_PACIENTES pac");
		sql.append(" , AGH.FAT_CONTAS_INTERNACAO coi");
		sql.append(" , AGH.FAT_CONTAS_HOSPITALARES cth");
		//		pac.codigo = int.pac_codigo
		sql.append(" where pac.codigo = dcs.pac_codigo");
		//		AND int.seq = coi.int_seq
		sql.append(" 	and dcs.seq = coi.dcs_seq");
		//		AND coi.cth_seq = cth.seq
		sql.append(" 	and coi.cth_seq = cth.seq");
		//		AND cth.ind_situacao IN ('A','F','E')
		sql.append(" 	and cth.ind_situacao in ( :ind_sit )");
		//		AND cth.dt_alta_administrativa > timestamp '2004-08-24 23:59:59'
		//sql.append("    and cth.dt_alta_administrativa > timestamp '2004-08-24 23:59:59'");
		//		AND cth.csp_cnv_codigo = 1
		sql.append(" 	and cth.csp_cnv_codigo = :csp_cod");
		//		AND cth.csp_seq = 1
		sql.append(" 	and cth.csp_seq = :csp_seq");
		//		order by cth.seq asc;
		sql.append(" order by coi.seq asc");
		query = ((Session) entityManager.getDelegate()).createSQLQuery(sql.toString());
		indSit = new String[indSituacao.length];
		for (int i = 0; i < indSituacao.length; i++) {
			indSit[i] = indSituacao[i].name();
		}
		query.setParameterList("ind_sit", indSit);
		query.setParameter("csp_cod", cnvCodigo);
		query.setParameter("csp_seq", cspSeq);
		query.addScalar("cthSeq", IntegerType.INSTANCE);
		query.addScalar("intSeq", IntegerType.INSTANCE);
		query.setResultTransformer(Transformers.aliasToBean(CthIntSeqVO.class));
		result = query.list();

		return result;

	}

	@Test
	public void buscarContaInternacaoCursorTci2(){
		if (isEntityManagerOk()) {
			Integer cthSeq = 430330;
			this.daoUnderTests.buscarContaInternacaoCursorTci2(cthSeq );
		}
	}

	@Test
	public void obterFatContasIntPacCirurgiasVO(){
		if (isEntityManagerOk()) {
			Integer cthSeq = 479674;
			List<FatContasIntPacCirurgiasVO> results = this.daoUnderTests.obterFatContasIntPacCirurgiasVO(cthSeq );
			Assert.assertNotNull(results);
		}
	}
}