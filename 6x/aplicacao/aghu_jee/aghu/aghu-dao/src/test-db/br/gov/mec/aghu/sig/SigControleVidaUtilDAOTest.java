package br.gov.mec.aghu.sig;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoControleVidaUtil;
import br.gov.mec.aghu.sig.dao.SigControleVidaUtilDAO;

/**
 * 
 * @author rmalvezzi
 *
 */
public class SigControleVidaUtilDAOTest extends AbstractDAOTest<SigControleVidaUtilDAO> {

	@Override
	protected SigControleVidaUtilDAO doDaoUnderTests() {
		return new SigControleVidaUtilDAO() {
			private static final long serialVersionUID = 1L;

			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return SigControleVidaUtilDAOTest.this.runCriteria(criteria);
			}
			
			@Override
			protected Query createQuery(String query) {
				return SigControleVidaUtilDAOTest.this.createQuery(query);
			}
		};
	}
	
	@Test
	public void testeRemoverSigControleVidaUtilByIdProcessamentoCusto() {
		this.daoUnderTests.removerPorProcessamento(1);
	}

	@Test
	public void  testeBuscarDebitosControleVidaUtilParaProcessamentoMensal(){
		this.daoUnderTests.buscarDebitosControleVidaUtilParaProcessamentoMensal(DominioSituacao.A, DominioTipoControleVidaUtil.T);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}