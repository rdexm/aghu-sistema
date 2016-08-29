package br.gov.mec.aghu.faturamento;


import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.junit.Test;

import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemDAO;
import br.gov.mec.aghu.model.FatCompatExclusItem;

public class FatCompatExclusItemDAOTest extends AbstractDAOTest<FatCompatExclusItemDAO> {

	@Override
	protected FatCompatExclusItemDAO doDaoUnderTests() {
	     return new FatCompatExclusItemDAO() {

			private static final long serialVersionUID = -202807494866862531L;

				@Override
				protected <T> java.util.List<T> executeCriteria(DetachedCriteria criteria, boolean cacheble) {
					return FatCompatExclusItemDAOTest.this.runCriteria(criteria, cacheble);
				};

				@Override
				protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
					return FatCompatExclusItemDAOTest.this.runCriteria(criteria);
				}
				
				@Override
				public boolean isOracle() {
					return FatCompatExclusItemDAOTest.this.isOracle();
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
    public void buscarFatCompatExclusItemCountILoadTest() {

		if (isEntityManagerOk()) {
	        logger.info("Teste para: FatCompatExclusItemDAO método buscarFatCompatExclusItemCountILoad .");
	        Boolean indInternacao = true;
			DominioIndComparacao indComparacao = DominioIndComparacao.I;
			Date pCompetencia = new Date();
			DominioIndCompatExclus[] compatExclus = {DominioIndCompatExclus.PCI};
			
			List<FatCompatExclusItem> fatCompatExclusItem = this.daoUnderTests.buscarFatCompatExclusItemCountILoad(compatExclus, indComparacao, indInternacao, DateUtil.obterDataInicioCompetencia(pCompetencia));
	        if (!fatCompatExclusItem.isEmpty()) {
	            logger.info( " OK " );
	        } else {
	        	logger.info("falhou" );
	        }
		}
    }
	
    @Test
    public void buscaTiposRestricoesExistentesParaRealizadoTest() {

		if (isEntityManagerOk()) {
	        final Boolean indInternacao = Boolean.TRUE;
	        final DominioIndComparacao indComparacao = DominioIndComparacao.R;
	        final Date pCompetencia = DateUtil.obterDataInicioCompetencia(new Date());
	        final Integer iphSeq = 457;
	        final Short iphPhoSeq = 12;
	        
	        final List<DominioIndCompatExclus> fatCompatExclusItem = 
	        			daoUnderTests.buscaTiposRestricoesExistentesParaRealizado( iphPhoSeq, iphSeq, indComparacao, 
																				   indInternacao, pCompetencia,
																				   DominioIndCompatExclus.PCI, DominioIndCompatExclus.PNI);
			
	        if (!fatCompatExclusItem.isEmpty()) {
	            logger.info( " OK " );
	        } else {
	        	logger.info( " OK - retornou uma lista vazia " );
	        }
		}
    }

    @Test
    public void buscaTiposRestricoesExistentesParaItem() {

		if (isEntityManagerOk()) {
	        Boolean indInternacao = Boolean.TRUE;
			DominioIndComparacao indComparacao = DominioIndComparacao.R;
			Date pCompetencia = new Date();
			Integer iphSeq = 457;
			Short iphPhoSeq = 12;
		
			List<DominioIndCompatExclus> fatCompatExclusItem = daoUnderTests.buscaTiposRestricoesExistentesParaItem(iphPhoSeq, iphSeq, indComparacao, indInternacao, pCompetencia, DominioIndCompatExclus.ICP, DominioIndCompatExclus.PCI); 
	        if (!fatCompatExclusItem.isEmpty()) {
	            logger.info( " OK " );
	        } else {
	        	logger.info( " OK - retornou uma lista vazia " );
	        }
		}
    }
    
    @Test
    public void buscarFatCompatExclusItemCountRTest() {

		if (isEntityManagerOk()) {
	        Boolean indInternacao = true;
			DominioIndComparacao indComparacao = DominioIndComparacao.I;
			Date pCompetencia = DateUtil.obterDataInicioCompetencia(new Date());
			Integer procRealizado = 12;
			Short tabProcRealizado = 5727;// 5727	 5728  String tipoTrans, Date pCompetencia
			Short tabProcItem = 5728;
			
			FatCompatExclusItem fatCompatExclusItem = daoUnderTests.buscarFatCompatExclusItemCountR(tabProcRealizado,procRealizado, tabProcItem, 
																									procRealizado, indComparacao, indInternacao, null, 
																									pCompetencia,
																									DominioIndCompatExclus.PCI, DominioIndCompatExclus.ICP);  
	     
			if (fatCompatExclusItem == null) {
	        	logger.info( " OK - retornou uma lista vazia " );
	        } else {
	        	logger.info( " OK " );
	        }
		}
    }
    
}