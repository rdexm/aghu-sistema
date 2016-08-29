package br.gov.mec.aghu.registrocolaborador;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoaTipoInformacoesDAO;
import br.gov.mec.aghu.registrocolaborador.vo.CursorBuscaCboVO;

public class RapPessoaTipoInformacoesDAOTest extends AbstractDAOTest<RapPessoaTipoInformacoesDAO> {
	
	@Override
	protected RapPessoaTipoInformacoesDAO doDaoUnderTests() {
		return new RapPessoaTipoInformacoesDAO() {
			private static final long serialVersionUID = -3483652691351294048L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return RapPessoaTipoInformacoesDAOTest.this.runCriteria(criteria);
			}

			@Override
			protected Object executeCriteriaUniqueResult(
					DetachedCriteria criteria) {
				return RapPessoaTipoInformacoesDAOTest.this.runCriteriaUniqueResult(criteria);
			}
			
			@Override
			public boolean isOracle() {
				return RapPessoaTipoInformacoesDAOTest.this.isOracle();
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}

	@Test
	public void executarCursorConsulta() {
		if (isEntityManagerOk()) {
			// assert
			this.daoUnderTests.listarPessoaTipoInformacoes(20386, (short) 9, (short) 9);
			// System.out.println("size=" + result.size());
			Assert.assertTrue(true);
		}
	}
	
	//listarPessoaTipoInformacoes(Integer pesCodigo, Date dtRealizado)
	@Test
	public void executarCursorConsultaPorDate() {

		List<CursorBuscaCboVO> result = null;
		List<Short> seqShorts = new ArrayList<Short>();
		seqShorts.add(Short.valueOf("7"));
		seqShorts.add(Short.valueOf("8"));
		seqShorts.add(Short.valueOf("11"));
		seqShorts.add(Short.valueOf("12"));
		seqShorts.add(Short.valueOf("13"));
		
		if (isEntityManagerOk()) {
			Calendar data = Calendar.getInstance();
			data.add(Calendar.YEAR, -1);
			// assert
			result = this.daoUnderTests.listarPessoaTipoInformacoes(
					1977, 
					data.getTime(), 
					seqShorts, 
					DateUtil.truncaData(DateUtil.obterUltimoDiaDoMes(new Date())));
			// System.out.println("size=" + result.size());
			logger.info("###############################################");
			if (result == null || result.isEmpty()) {
				logger.info("Retornou null.");
			} else {
				logger.info("Retornou " + result.size() + " registros.");
				for (CursorBuscaCboVO cursorBuscaCboVO : result) {
					logger.info("Valor = " + cursorBuscaCboVO.getValor());
					logger.info("TiiSeq = " + cursorBuscaCboVO.getTiiSeq());
				}
			}
		}
	}
	
	@Test
	public void testListarPorPessoaFisicaValorTipoInformacao() {

		String result = null;

		if (isEntityManagerOk()) {
			// assert
			result = this.daoUnderTests.listarPorPessoaFisicaValorTipoInformacao(20386, new String[] { "223104", "225151" }, new Date());
			logger.info("Valor Anestesiologista=" + result);
			Assert.assertTrue(true);
		}
	}
	
	/**
	 * Efetua o teste do cursor: AIPC_GET_CNS_RESP.C_BUSCA_CNS
	 */
	@Test
	public void obterRapPessoaTipoInformacoesPorPesCPFTiiSeq() {
		if (isEntityManagerOk()) {
			// assert
			final Long cpf = 2054L;
			final Short tiiSeq = Short.valueOf("9");
			String  rpti = this.daoUnderTests.obterRapPessoaTipoInformacoesPorPesCPFTiiSeq(cpf, tiiSeq);
			logger.info(rpti);
		} 
	}


	@Override
	protected void finalizeMocks() {
	}
}