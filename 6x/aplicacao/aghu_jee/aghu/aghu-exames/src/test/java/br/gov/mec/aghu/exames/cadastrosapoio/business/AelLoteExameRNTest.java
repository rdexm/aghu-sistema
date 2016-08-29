package br.gov.mec.aghu.exames.cadastrosapoio.business;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AelLoteExameDAO;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AelLoteExame;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelLoteExameRNTest extends AGHUBaseUnitTest<AelLoteExameRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelLoteExameDAO mockedAelLoteExameDAO;
	
	private AelLoteExameUsual loteExameUsual = new AelLoteExameUsual();
	private AelLoteExame loteExame = new AelLoteExame();
	
	@Before
	public void doBeforeEachTestCase() throws Exception{

		AelGrupoExameUsual grupo = new AelGrupoExameUsual(45, null, null, null);
		grupo.setIndSituacao(DominioSituacao.A);

		loteExameUsual.setGruSeq(grupo);
		loteExameUsual.setIndLoteDefault(DominioSimNao.S);

		loteExame.setAelLoteExamesUsuais(loteExameUsual);

		AelExamesMaterialAnalise man = new AelExamesMaterialAnalise();
		man.setIndDependente(false);

		loteExame.setExamesMaterialAnalise(man);

	}
	
	@Test
	public void validarExamesUsualAtivo(){
		try{
			systemUnderTest.validarExamesUsualAtivo(this.loteExame);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void validarExamesDependentes(){
		try{
			systemUnderTest.validarExamesDependentes(this.loteExame);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
}