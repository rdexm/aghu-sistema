package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.dao.AelLoteExameDAO;
import br.gov.mec.aghu.exames.dao.AelLoteExameUsualDAO;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class AelLoteExameUsualRNTest extends AGHUBaseUnitTest<AelLoteExameUsualRN>{
	
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private AelLoteExameUsualDAO mockedAelLoteExameUsualDAO;
	@Mock
	private AelLoteExameDAO mockedAelLoteExameDAO;
	@Mock
	private ICadastrosApoioExamesFacade mockedCadastrosApoioExamesFacade;
	private AelLoteExameUsual loteExame = new AelLoteExameUsual();
	
	@Before
	public void doBeforeEachTestCase() throws Exception{
		
		
		AelGrupoExameUsual grupo = new AelGrupoExameUsual(45, null, null, null);
		grupo.setIndSituacao(DominioSituacao.A);
		
		loteExame.setGruSeq(grupo);
		loteExame.setIndLoteDefault(DominioSimNao.S);
		loteExame.setSeq(Short.parseShort("143"));
		loteExame.setOrigem(DominioOrigemAtendimento.A);
		loteExame.setCriadoEm(new Date());
		
	}
	
	@Test
	public void aelLeuCk1() {
		try{
			systemUnderTest.aelLeuCk1(this.loteExame);

		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void rnLeupVerEspecial() {
		try{
			systemUnderTest.rnLeupVerEspecial(this.loteExame);

		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void rnLeupVerExmeUsu() {
		try{
			systemUnderTest.rnLeupVerExmeUsu(this.loteExame);

		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void rnLeupVerUnidFun() {
		try{
			systemUnderTest.rnLeupVerUnidFun(this.loteExame);

		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void rnLeupVerDefault() {
		try{
			systemUnderTest.rnLeupVerDefault(this.loteExame);

		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void rnLeupVerEspOrig() {
		try{
			systemUnderTest.rnLeupVerEspOrig(this.loteExame);

		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void rnLeupVerUnidOrig() {
		try{
			systemUnderTest.rnLeupVerUnidOrig(this.loteExame);

		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void rnLeupVerGruOrig() {
		try{
			systemUnderTest.rnLeupVerGruOrig(this.loteExame);

		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
	
	@Test
	public void aelLeuCk3(){
		try{
			systemUnderTest.aelLeuCk3(this.loteExame);
		} catch (ApplicationBusinessException e) {
			Assert.fail(e.getMessage());
		}
	}
}