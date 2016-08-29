package br.gov.mec.aghu.blococirurgico;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CursorCirurgiaSusProceHospitalarInternoVO;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class MbcProcEspPorCirurgiasDAOTest extends
		AbstractDAOTest<MbcProcEspPorCirurgiasDAO> {

	@Override
	protected MbcProcEspPorCirurgiasDAO doDaoUnderTests() {
		return new MbcProcEspPorCirurgiasDAO() {

			private static final long serialVersionUID = 7416819175937316069L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return MbcProcEspPorCirurgiasDAOTest.this.runCriteria(criteria);
			}

		};
	}
	
	@Test
	public void testPesquisarCirurgiaSusProcedimentoHospitalarInterno() {

		if (isEntityManagerOk()) {
			
			final Integer pCrgSeq = 689053; //38320
			final Byte pCspSeq = 2;
			final Short pCnvCodigo = 1;
			final DominioSituacao dominioSituacao = DominioSituacao.A;
			final DominioIndRespProc dominioIndRespProc = DominioIndRespProc.NOTA;
			final Short pTipoGrupoContaSus = 6;

			try {
				List<CursorCirurgiaSusProceHospitalarInternoVO> result = doDaoUnderTests().
						pesquisarCirurgiaSusProcedimentoHospitalarInterno(pCrgSeq, pCspSeq, pCnvCodigo, dominioSituacao, dominioIndRespProc, pTipoGrupoContaSus);
				
				if (result == null || result.isEmpty()) {
					logger.info("Retornou null.");
				} else {
					logger.info("Retornou " + result.size() + " registros.");
				}

			} catch (Exception e) {
				logger.error(e.getMessage());
				Assert.fail("Not expecting exception: " + e);
			}
		}
	}

	@Override
	protected void initMocks() {

	}

	@Override
	protected void finalizeMocks() {

	}
}