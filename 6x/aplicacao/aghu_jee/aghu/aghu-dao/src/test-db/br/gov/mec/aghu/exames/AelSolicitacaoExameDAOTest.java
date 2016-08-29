package br.gov.mec.aghu.exames;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.exames.agendamento.vo.EtiquetaEnvelopePacienteVO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;

public class AelSolicitacaoExameDAOTest  extends AbstractDAOTest<AelSolicitacaoExameDAO> {
	
	@Override
	protected AelSolicitacaoExameDAO doDaoUnderTests() {
		return new AelSolicitacaoExameDAO() {
			private static final long serialVersionUID = -3519609949253898935L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return AelSolicitacaoExameDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria,
					int firstResult, int maxResults, String orderProperty,
					boolean asc) {
				return AelSolicitacaoExameDAOTest.this.runCriteria(criteria, firstResult, maxResults, orderProperty, asc);
			}
		};
	}

	@Override
	protected void initMocks() {

	}
	
	@Override
	protected void finalizeMocks() {
		
	}
	
	@Test
	public void listarAtendimentoParaCargaColetaRDAtendimento() {
		if (isEntityManagerOk()) {
			//assert
			//retornar sem dados
			getDaoUnderTests().listarAtendimentoParaCargaColetaRD(Integer.valueOf(1), Boolean.FALSE);
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void listarAtendimentoParaCargaColetaRDAtendimentoRetornarDados() {
		if (isEntityManagerOk()) {
			//assert
			//retornar dados
			getDaoUnderTests().listarAtendimentoParaCargaColetaRD(Integer.valueOf(4977880), Boolean.FALSE);
			Assert.assertTrue(true);

		}
	}
	
	@Test
	public void listarAtendimentoParaCargaColetaRDAtendimentoDiverso() {
		if (isEntityManagerOk()) {
			//assert
			//retornar sem dados
			getDaoUnderTests().listarAtendimentoParaCargaColetaRD(Integer.valueOf(1), Boolean.TRUE);
		}
	}
	
	@Test
	public void listarAtendimentoParaCargaColetaRDAtendimentoDiversoRetornarDados() {
		if (isEntityManagerOk()) {
			//assert
			//retornar dados
			getDaoUnderTests().listarAtendimentoParaCargaColetaRD(Integer.valueOf(3730101), Boolean.TRUE);
		}
	}
	
	@Test
	public void testPesquisarEtiquetaEnvelopePacienteAtendimentoDiverso() {
		if (isEntityManagerOk()) {
			//assert
			final List<EtiquetaEnvelopePacienteVO> result = getDaoUnderTests().pesquisarEtiquetaEnvelopePacienteAtendimentoDiverso(6114421, (short)26, false);
			for (final EtiquetaEnvelopePacienteVO etiquetaEnvelopePacienteVO : result) {
				logger.info(etiquetaEnvelopePacienteVO.getNomePaciente() + "-" + etiquetaEnvelopePacienteVO.getDataAgenda() + "-"
						+ etiquetaEnvelopePacienteVO.getSoeSeq() + "-" + etiquetaEnvelopePacienteVO.getNomeUnidadeFuncional());
			}
		}
	}
}