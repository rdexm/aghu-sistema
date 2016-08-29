package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmAltaDiagSecundario;
import br.gov.mec.aghu.model.MpmAltaDiagSecundarioId;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterAltaDiagSecundarioRNTest extends AGHUBaseUnitTest<ManterAltaDiagSecundarioRN>{
	
	@Mock
	private ManterAltaSumarioRN mockedAltaSumarioRN;
	
	/**
	 * Deve retornae exceção MPM_02612.
	 */
	@Test
	public void verificarAtualizacao001Test() {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		AghCid cid = new AghCid();
		
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		novoAltaDiagSecundario.setDescCid("teste1");
		novoAltaDiagSecundario.setCidSeq(cid);
		
		antigoAltaDiagSecundario.setDescCid("teste2");
		antigoAltaDiagSecundario.setCidSeq(cid);
					
		try {
		
			systemUnderTest.verificarAtualizacao(novoAltaDiagSecundario, antigoAltaDiagSecundario);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02612", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Deve retornar exceção MPM_02613.
	 */
	@Test
	public void verificarAtualizacao002Test() {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		AghCid cid1 = new AghCid();
		AghCid cid2 = new AghCid();
		
		cid1.setSeq(Integer.valueOf(1));
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		novoAltaDiagSecundario.setDescCid("teste");
		novoAltaDiagSecundario.setCidSeq(cid1);
		
		cid2.setSeq(Integer.valueOf(2));
		antigoAltaDiagSecundario.setDescCid("teste");
		antigoAltaDiagSecundario.setCidSeq(cid2);
					
		try {
		
			systemUnderTest.verificarAtualizacao(novoAltaDiagSecundario, antigoAltaDiagSecundario);
			fail("Deveria ter ocorrido uma exceção!!!");
			
		} catch (ApplicationBusinessException atual) {
			
			assertEquals("MPM_02613", atual.getMessage());
			
		}
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * 
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificarAtualizacao003Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		AghCid cid1 = new AghCid();
		
		cid1.setSeq(Integer.valueOf(1));
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		novoAltaDiagSecundario.setDescCid("teste");
		novoAltaDiagSecundario.setCidSeq(cid1);
		
		antigoAltaDiagSecundario.setDescCid("teste");
		antigoAltaDiagSecundario.setCidSeq(cid1);
					
		systemUnderTest.verificarAtualizacao(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02615.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos001Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.getId().setAsuApaAtdSeq(Integer.valueOf(1));
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.getId().setAsuApaAtdSeq(Integer.valueOf(2));
		
		novoAltaDiagSecundario.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02615.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos002Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.getId().setAsuApaSeq(Integer.valueOf(1));
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.getId().setAsuApaSeq(Integer.valueOf(2));
		
		novoAltaDiagSecundario.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02615.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos003Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.getId().setAsuSeqp(Short.valueOf("1"));
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.getId().setAsuSeqp(Short.valueOf("2"));
		
		novoAltaDiagSecundario.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02615.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos004Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.getId().setSeqp(Short.valueOf("1"));
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.getId().setSeqp(Short.valueOf("2"));
		
		novoAltaDiagSecundario.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02615.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos005Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.setDescCid("teste1");
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.setDescCid("teste2");
		
		novoAltaDiagSecundario.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02615.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos006Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.setComplCid("teste1");
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.setComplCid("teste2");
		
		novoAltaDiagSecundario.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Deve retornar exceção MPM_02615.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void verificarAlteracaoCampos007Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		AghCid cid1 = new AghCid();
		AghCid cid2 = new AghCid();
		cid1.setSeq(Integer.valueOf(1));
		cid2.setSeq(Integer.valueOf(2));
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.setCidSeq(cid1);
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.setCidSeq(cid2);
		
		novoAltaDiagSecundario.setIndCarga(Boolean.TRUE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificarAlteracaoCampos008Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.getId().setAsuApaAtdSeq(Integer.valueOf(1));
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.getId().setAsuApaAtdSeq(Integer.valueOf(2));
		
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificarAlteracaoCampos009Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.getId().setAsuApaSeq(Integer.valueOf(1));
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.getId().setAsuApaSeq(Integer.valueOf(2));
		
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificarAlteracaoCampos010Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.getId().setAsuSeqp(Short.valueOf("1"));
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.getId().setAsuSeqp(Short.valueOf("2"));
		
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificarAlteracaoCampos011Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.getId().setSeqp(Short.valueOf("1"));
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.getId().setSeqp(Short.valueOf("2"));
		
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificarAlteracaoCampos012Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.setDescCid("teste1");
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.setDescCid("teste2");
		
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificarAlteracaoCampos013Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.setComplCid("teste1");
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.setComplCid("teste2");
		
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Não deve retornar exceção.
	 * @throws AGHUNegocioExceptionSemRollback 
	 */
	@Test
	public void verificarAlteracaoCampos014Test() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		AghCid cid1 = new AghCid();
		AghCid cid2 = new AghCid();
		cid1.setSeq(Integer.valueOf(1));
		cid2.setSeq(Integer.valueOf(2));
		
		novoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		novoAltaDiagSecundario.setCidSeq(cid1);
		
		antigoAltaDiagSecundario.setId(new MpmAltaDiagSecundarioId());
		antigoAltaDiagSecundario.setCidSeq(cid2);
		
		novoAltaDiagSecundario.setIndCarga(Boolean.FALSE);
		
		systemUnderTest.verificarAlteracaoCampos(novoAltaDiagSecundario, antigoAltaDiagSecundario);
		
	}
	
	/**
	 * Não deve apresentar exceção.
	 * 
	 * @throws AGHUNegocioExceptionSemRollback
	 */
	@Test
	public void verificarAtendimentoTest001() throws ApplicationBusinessException {
		
		MpmAltaDiagSecundario novoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmAltaDiagSecundario antigoAltaDiagSecundario = new MpmAltaDiagSecundario();
		MpmCidAtendimento cid1 = new MpmCidAtendimento();
		cid1.setSeq(Integer.valueOf(1));
		MpmCidAtendimento cid2 = new MpmCidAtendimento();
		cid2.setSeq(Integer.valueOf(2));
		
		novoAltaDiagSecundario.setMpmCidAtendimentos(cid1);
		antigoAltaDiagSecundario.setMpmCidAtendimentos(cid2);

		systemUnderTest.verificarAtendimento(novoAltaDiagSecundario, antigoAltaDiagSecundario);

	}
	
}
