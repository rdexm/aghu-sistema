package br.gov.mec.aghu.blococirurgico.business;

import org.junit.Assert;
import org.junit.Ignore;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

@Ignore
public class RetornoCirurgiaEmLoteONTest extends AGHUBaseUnitTest<RetornoCirurgiaEmLoteON> {
	
	private RetornoCirurgiaEmLoteON retornoCirurgiaEmLoteON;
	private MbcCirurgiasDAO mbcCirurgiasDAO;
	
	public void carregar(){
//		mockingContext = new Mockery() {{
//			setImposteriser(ClassImposteriser.INSTANCE);
//		}};
		
//		mbcCirurgiasDAO = mockingContext.mock(MbcCirurgiasDAO.class);
		
		// criação do objeto da classe a ser testada, com os devidos métodos
		// sobrescritos.
		retornoCirurgiaEmLoteON = new RetornoCirurgiaEmLoteON() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -4618757753071451476L;

			@Override
			protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
				return mbcCirurgiasDAO;
			}
			
		};
		
	}	
	
	public void testObterDataUltimoTransplante() throws ApplicationBusinessException{
		MbcCirurgias cirurgia = null;
		
//		mockingContext.checking(new Expectations() {{
//			oneOf(mbcCirurgiasDAO).obterCirurgiaPacientePorCrgSeq(with(any(Integer.class)));
//			will(returnValue(getMbcCururgias()));
//		}});
		
		try {
			this.retornoCirurgiaEmLoteON.obterDataUltimoTransplante(cirurgia);
			Assert.fail("Exceção com a mensagem MBC-00537 não lançada.");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals("MBC_00537", e.getMessage());
		}
		
		
	}
	
	private MbcCirurgias getMbcCururgias(){
		
		MbcCirurgias cirurgia = new MbcCirurgias();
		cirurgia.setSeq(1);
		
		AipPacientes paciente = new AipPacientes();
		paciente.setCodigo(1);
		cirurgia.setPaciente(paciente);
		
		return cirurgia;
	}
	
	public void testMudarSituacaoCirurgiaParaCancelado() throws ApplicationBusinessException{
		DominioSituacaoCirurgia situacao = DominioSituacaoCirurgia.CANC;
		this.retornoCirurgiaEmLoteON.mudarSituacao(situacao);
	}
	
	public void testMBC00516() throws ApplicationBusinessException{
		MbcCirurgias cirurgias = new MbcCirurgias();
		cirurgias.setDigitaNotaSala(Boolean.TRUE);
		cirurgias.setMotivoCancelamento(null);
//		this.retornoCirurgiaEmLoteON.validarIndDigtNotaSalaEMtcSeq(cirurgias);
	}
}
