package br.gov.mec.aghu.configuracao;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.configuracao.dao.AghAtendimentoDAO;
import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioRNClassificacaoNascimento;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.prescricaomedica.vo.DadosRegistroCivilVO;

public class AghAtendimentoDAOTest extends AbstractDAOTest<AghAtendimentoDAO> {

	private static final Log log = LogFactory.getLog(AghAtendimentoDAOTest.class);

	@Override 
	protected AghAtendimentoDAO doDaoUnderTests() {
		return new AghAtendimentoDAO() {
			private static final long serialVersionUID = -2220285789400697606L;

			@Override
			protected Query createHibernateQuery(String query) {
				return AghAtendimentoDAOTest.this.createHibernateQuery(query);
			}
			
			@Override
			public boolean isOracle() {
				return AghAtendimentoDAOTest.this.isOracle();
			}
			
			@Override
			protected <T> List<T> executeCriteria(DetachedCriteria criteria) {
				return AghAtendimentoDAOTest.this.runCriteria(criteria);
			}
		};
	}

	@Override
	protected void initMocks() {
		
	}
	
	public void verificarAbo(){
		if (isEntityManagerOk()) {
			final Integer pIntSeq = 119887;
			final DominioRNClassificacaoNascimento result = this.daoUnderTests.verificarAbo(pIntSeq);
			log.info( " DominioRNClassificacaoNascimento "+result);
			Assert.assertNotNull(result);
		}
	}
	
	public void obterDadosRegistroCivil(){
		if (isEntityManagerOk()) {
			final Integer pIntSeq = 339001;
			final DadosRegistroCivilVO result = this.daoUnderTests.obterDadosRegistroCivil(pIntSeq);
			log.info( " DadosRegistroCivilVO "+result);
			Assert.assertNotNull(result);
		}
	}

	@Test
	public void listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(){
		if (isEntityManagerOk()) {
			String sitCodigoLi = "LI";
			String sitCodigoAe = "AE";
			Integer mamPcSeqp = 1;
			Integer mamPcPleSeq = 973250;
			Integer asuApaAtdSeq = 1;
			Integer asuApaSeq = 1;
			Short apeSeqp = 1;
			final List<AelItemSolicitacaoExames> result = this.daoUnderTests.listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(sitCodigoLi, sitCodigoAe, mamPcSeqp, mamPcPleSeq, asuApaAtdSeq, asuApaSeq, apeSeqp);
//			final List<AelItemSolicitacaoExames> result = this.daoUnderTests.listarAelItemSolicitacaoExamesPorAtdSeqSitCodigo(1, sitCodigoLi, sitCodigoAe);
			
			log.info( " AelItemSolicitacaoExames "+result);
			Assert.assertNotNull(result);
		}
	}

	@Override
	protected void finalizeMocks() {
	}	
}