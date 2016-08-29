package br.gov.mec.aghu.faturamento;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.faturamento.dao.VFatAssociacaoProcedimentoDAO;

public class VFatAssociacaoProcedimentoDAOTest extends AbstractDAOTest<VFatAssociacaoProcedimentoDAO> {
	
	@Override
	protected VFatAssociacaoProcedimentoDAO doDaoUnderTests() {
		return new VFatAssociacaoProcedimentoDAO() {
			private static final long serialVersionUID = -6181184287434962652L;

			@Override
			protected org.hibernate.SQLQuery createSQLQuery(String query) {
				return VFatAssociacaoProcedimentoDAOTest.this.createSQLQuery(query);
			};
			
			@Override
			public boolean isOracle() {
				return VFatAssociacaoProcedimentoDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}
	
	/**
	 * Testa cursor: RN_CTHP_VER_INS_CID.C_ICH_REALIZADOS
	 */
	@Test
	public void listarPorIphCodSusCthGrpSit() {
		if (isEntityManagerOk()) {
			final DominioSituacaoItenConta indSituacao = DominioSituacaoItenConta.A;
//			final Integer cthSeq = 397101; // ORACLE
			final Integer cthSeq = 15597;
			final Short cpgGrcSeq = Short.valueOf("6"); 
			final String codRegistro = "03";
			final Boolean cirurgiaMultipla = Boolean.TRUE;
			
			final List<Integer> seqs = this.daoUnderTests.obterMenorIchRealizados(cthSeq, cpgGrcSeq, cirurgiaMultipla, indSituacao, codRegistro );
			
			if(seqs != null && !seqs.isEmpty()){
				for (Integer vSeq : seqs) {
					logger.info( " seq "+vSeq);
				}
				Assert.assertTrue(true);
			} else {
				Assert.assertFalse(true);
			}
		}
	}
	
	@Override
	protected void finalizeMocks() {}
}