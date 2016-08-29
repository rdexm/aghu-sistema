package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio.EpeSinCaractDefinidoraRN.EpeSinCaractDefinidoraRNExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidora;
import br.gov.mec.aghu.model.EpeSinCaractDefinidoraId;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class EpeSinCaractDefinidoraRNTest extends AGHUBaseUnitTest<EpeSinCaractDefinidoraRN>{
	
	
	/**
	 * Exception EPE_00075
	 */
	@Test
	public void validarEpeSinCaractDefinidoraSituacaoNula() {
		
		EpeSinCaractDefinidoraId caracteristicaDefinidoraId = new EpeSinCaractDefinidoraId();
		caracteristicaDefinidoraId.setCdeCodigo(140);
		caracteristicaDefinidoraId.setCdeCodigoPossui(280);
		
		EpeSinCaractDefinidora sinCaracDefinidora = new EpeSinCaractDefinidora();
		sinCaracDefinidora.setId(caracteristicaDefinidoraId);
		
		EpeCaractDefinidora caracDefinidora = new EpeCaractDefinidora();
		caracDefinidora.setSituacao(null);
		
		sinCaracDefinidora.setCaractDefinidoraByCdeCodigo(caracDefinidora);
		sinCaracDefinidora.setCaractDefinidoraByCdeCodigoPossui(caracDefinidora);
		
		try {
			systemUnderTest.inserirEpeSinCaractDefinidora(sinCaracDefinidora);
			Assert.fail("Exceção esperada não gerada: EPE_00075");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(EpeSinCaractDefinidoraRNExceptionCode.EPE_00075, e.getCode());
		}
	}
	
	
	/**
	 * Exception EPE_00076
	 */
	@Test
	public void validarEpeSinCaractDefinidoraSituacaoInativo() {
		
		EpeSinCaractDefinidoraId caracteristicaDefinidoraId = new EpeSinCaractDefinidoraId();
		caracteristicaDefinidoraId.setCdeCodigo(140);
		caracteristicaDefinidoraId.setCdeCodigoPossui(280);
		
		EpeSinCaractDefinidora sinCaracDefinidora = new EpeSinCaractDefinidora();
		sinCaracDefinidora.setId(caracteristicaDefinidoraId);
		
		EpeCaractDefinidora caracDefinidora = new EpeCaractDefinidora();
		caracDefinidora.setSituacao(DominioSituacao.I);
		
		sinCaracDefinidora.setCaractDefinidoraByCdeCodigo(caracDefinidora);
		sinCaracDefinidora.setCaractDefinidoraByCdeCodigoPossui(caracDefinidora);
		
		try {
			systemUnderTest.inserirEpeSinCaractDefinidora(sinCaracDefinidora);
			Assert.fail("Exceção esperada não gerada: EPE_00076");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(EpeSinCaractDefinidoraRNExceptionCode.EPE_00076, e.getCode());
		}
	}

}
