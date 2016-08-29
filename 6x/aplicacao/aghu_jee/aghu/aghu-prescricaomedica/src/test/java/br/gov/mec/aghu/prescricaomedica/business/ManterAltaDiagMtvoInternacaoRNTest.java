package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmAltaDiagMtvoInternacao;
import br.gov.mec.aghu.model.MpmAltaDiagMtvoInternacaoId;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaDiagMtvoInternacaoRNTest extends AGHUBaseUnitTest<ManterAltaDiagMtvoInternacaoRN>{

	@Mock
	private ManterAltaSumarioRN mockedAltaSumarioRN;
	
	/**
	 * Deve retornae exceção MPM_02598.
	 */
	@Test
	public void verificarAtualizacao001Test() {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		AghCid cid = new AghCid();
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		novoAltaDiagMtvoInternacao.setDescCid("teste1");
		novoAltaDiagMtvoInternacao.setCid(cid);
		
		antigoAltaDiagMtvoInternacao.setDescCid("teste2");
		antigoAltaDiagMtvoInternacao.setCid(cid);
					
		try {
		
			systemUnderTest.verificarAtualizacao(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02598", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02599.
	 */
	@Test
	public void verificarAtualizacao002Test() {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		AghCid cid1 = new AghCid();
		AghCid cid2 = new AghCid();
		
		cid1.setSeq(Integer.valueOf(1));
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		novoAltaDiagMtvoInternacao.setDescCid("teste");
		novoAltaDiagMtvoInternacao.setCid(cid1);
		
		cid2.setSeq(Integer.valueOf(2));
		antigoAltaDiagMtvoInternacao.setDescCid("teste");
		antigoAltaDiagMtvoInternacao.setCid(cid2);
					
		try {
		
			systemUnderTest.verificarAtualizacao(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02599", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * 
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void verificarAtualizacao003Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		AghCid cid1 = new AghCid();
		
		cid1.setSeq(Integer.valueOf(1));
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		novoAltaDiagMtvoInternacao.setDescCid("teste");
		novoAltaDiagMtvoInternacao.setCid(cid1);
		
		antigoAltaDiagMtvoInternacao.setDescCid("teste");
		antigoAltaDiagMtvoInternacao.setCid(cid1);
					
		systemUnderTest.verificarAtualizacao(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02601.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos001Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.getId().setAsuApaAtdSeq(Integer.valueOf(1));
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.getId().setAsuApaAtdSeq(Integer.valueOf(2));
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02601.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos002Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.getId().setAsuApaSeq(Integer.valueOf(1));
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.getId().setAsuApaSeq(Integer.valueOf(2));
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02601.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos003Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.getId().setAsuSeqp(Short.valueOf("1"));
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.getId().setAsuSeqp(Short.valueOf("2"));
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02601.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos004Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.getId().setSeqp(Byte.valueOf("1"));
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.getId().setSeqp(Byte.valueOf("2"));
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02601.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos005Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.setDescCid("teste1");
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.setDescCid("teste2");
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02601.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos006Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.setComplCid("teste1");
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.setComplCid("teste2");
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02601.
	 * @throws ApplicationBusinessException 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos007Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		AghCid cid1 = new AghCid();
		AghCid cid2 = new AghCid();
		cid1.setSeq(Integer.valueOf(1));
		cid2.setSeq(Integer.valueOf(2));
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.setCid(cid1);
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.setCid(cid2);
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void verificarAlteracaoCampos008Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.getId().setAsuApaAtdSeq(Integer.valueOf(1));
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.getId().setAsuApaAtdSeq(Integer.valueOf(2));
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void verificarAlteracaoCampos009Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.getId().setAsuApaSeq(Integer.valueOf(1));
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.getId().setAsuApaSeq(Integer.valueOf(2));
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void verificarAlteracaoCampos010Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.getId().setAsuSeqp(Short.valueOf("1"));
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.getId().setAsuSeqp(Short.valueOf("2"));
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void verificarAlteracaoCampos011Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.getId().setSeqp(Byte.valueOf("1"));
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.getId().setSeqp(Byte.valueOf("2"));
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void verificarAlteracaoCampos012Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.setDescCid("teste1");
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.setDescCid("teste2");
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void verificarAlteracaoCampos013Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.setComplCid("teste1");
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.setComplCid("teste2");
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws ApplicationBusinessException 
	 */
	@Test
	public void verificarAlteracaoCampos014Test() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		AghCid cid1 = new AghCid();
		AghCid cid2 = new AghCid();
		cid1.setSeq(Integer.valueOf(1));
		cid2.setSeq(Integer.valueOf(2));
		
		novoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		novoAltaDiagMtvoInternacao.setCid(cid1);
		
		antigoAltaDiagMtvoInternacao.setId(new MpmAltaDiagMtvoInternacaoId());
		antigoAltaDiagMtvoInternacao.setCid(cid2);
		
		novoAltaDiagMtvoInternacao.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);
		
	}
	
	/**
	 * Não deve apresentar exceção.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarCiaSeqTest001() throws ApplicationBusinessException {
		
		MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmAltaDiagMtvoInternacao antigoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
		MpmCidAtendimento cid1 = new MpmCidAtendimento();
		cid1.setSeq(Integer.valueOf(1));
		MpmCidAtendimento cid2 = new MpmCidAtendimento();
		cid2.setSeq(Integer.valueOf(2));
		
		novoAltaDiagMtvoInternacao.setCidAtendimento(cid1);
		antigoAltaDiagMtvoInternacao.setCidAtendimento(cid2);

		systemUnderTest.verificarCiaSeq(novoAltaDiagMtvoInternacao, antigoAltaDiagMtvoInternacao);

	}

}
