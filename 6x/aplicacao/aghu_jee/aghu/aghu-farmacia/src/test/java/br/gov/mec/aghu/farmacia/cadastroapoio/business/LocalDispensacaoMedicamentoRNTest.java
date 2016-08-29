package br.gov.mec.aghu.farmacia.cadastroapoio.business;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.LocalDispensacaoMedicamentoRN.LocalDispensacaoMedicamentoRNExceptionCode;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.LocalDispensacaoMedicamentoRN.TipoUnfLdmEnum;
import br.gov.mec.aghu.farmacia.dao.AfaLocalDispensacaoMdtosJnDAO;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosId;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosJn;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;

public class LocalDispensacaoMedicamentoRNTest extends AGHUBaseUnitTest<LocalDispensacaoMedicamentoRN>{

	private static final Log log = LogFactory.getLog(LocalDispensacaoMedicamentoRNTest.class);

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";
	
	// Daos e Facades a serem mockadas
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IParametroFacade mockedParametroFacade;
	@Mock
	private BaseJournalFactory mockedBaseJournalFactory;
	@Mock
	private AfaLocalDispensacaoMdtosJnDAO mockedAfaLocalDispensacaoMdtosJnDAO;
	@Mock
	private AfaLocalDispensacaoMdtosJn mockedAfaLocalDispensacaoMdtosJn;

	@Test 
	public void verificarAtribuirCriadoEm() {
		AfaLocalDispensacaoMdtos localDispensacao = new AfaLocalDispensacaoMdtos();
		systemUnderTest.verificarAtribuirCriadoEm(localDispensacao);
		Assert.assertNotNull(localDispensacao.getCriadoEm());
	}

	@Test 
	public void verificarAtribuirVersion() {
		AfaLocalDispensacaoMdtos localDispensacao = new AfaLocalDispensacaoMdtos();
		systemUnderTest.verificarAtribuirVersion(localDispensacao);
		Assert.assertNotNull(localDispensacao.getVersion());
	}

	// *****METODOS UTILIZADOS PELAS TRIGGERS***********************************************************************

	@Test 
	public void validaExisteCaractUnFuncionalSim() {
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		AghCaractUnidFuncionais c1 = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionais c2 = new AghCaractUnidFuncionais();
		c1.setId(new AghCaractUnidFuncionaisId(Short.valueOf("1"),
				ConstanteAghCaractUnidFuncionais.UNID_COLETA));
		c2.setId(new AghCaractUnidFuncionaisId(Short.valueOf("2"),
				ConstanteAghCaractUnidFuncionais.UNID_FARMACIA));
		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(c1);
		caracteristicas.add(c2);
		unidadeFuncional.setCaracteristicas(caracteristicas);
		String[] caracteristicaArray = new String[] { ConstanteAghCaractUnidFuncionais.UNID_FARMACIA
				.getCodigo() };

		DominioSimNao sim = DominioSimNao.S;
		DominioSimNao retorno = systemUnderTest.existeCaractUnFuncional(
				unidadeFuncional, caracteristicaArray);

		Assert.assertEquals(sim, retorno);

	}

	@Test 
	public void validaExisteCaractUnFuncionalNao() {
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		AghCaractUnidFuncionais c1 = new AghCaractUnidFuncionais();
		AghCaractUnidFuncionais c2 = new AghCaractUnidFuncionais();
		c1.setId(new AghCaractUnidFuncionaisId(Short.valueOf("1"),
				ConstanteAghCaractUnidFuncionais.UNID_COLETA));
		c2.setId(new AghCaractUnidFuncionaisId(Short.valueOf("2"),
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA));
		Set<AghCaractUnidFuncionais> caracteristicas = new HashSet<AghCaractUnidFuncionais>();
		caracteristicas.add(c1);
		caracteristicas.add(c2);
		unidadeFuncional.setCaracteristicas(caracteristicas);
		String[] caracteristicaArray = new String[] { ConstanteAghCaractUnidFuncionais.UNID_FARMACIA
				.getCodigo() };
		DominioSimNao nao = DominioSimNao.N;

		DominioSimNao retorno = systemUnderTest.existeCaractUnFuncional(
				unidadeFuncional, caracteristicaArray);
		Assert.assertEquals(nao, retorno);
	}

	@Test 
	public void validaVerificaSituacaoUnidadeFuncionalAtivaTrue() {
		AghUnidadesFuncionais unfValidacao = new AghUnidadesFuncionais();
		unfValidacao.setIndSitUnidFunc(true);
		try {
			systemUnderTest.verificaSituacaoUnidadeFuncional(unfValidacao);
//			mockingContext.assertIsSatisfied();
		} catch (ApplicationBusinessException e) {
			Assert.fail("Não deveria lançar exceção pois está ativa");
		}
	}

	@Test 
	public void validaVerificaSituacaoUnidadeFuncionalAtivaFalse() {
		AghUnidadesFuncionais unfValidacao = new AghUnidadesFuncionais();
		unfValidacao.setIndSitUnidFunc(false);
		try {
			systemUnderTest.verificaSituacaoUnidadeFuncional(unfValidacao);
			Assert.fail("Não deveria lançar exceção pois não está ativa");
		} catch (ApplicationBusinessException e) {
			Assert.assertEquals(
					LocalDispensacaoMedicamentoRNExceptionCode.AFA_00242, e
							.getCode());
//			mockingContext.assertIsSatisfied();
		} catch (Exception e2) {
			Assert.fail("Não deveria lançar exceção diferente de AFA_00242");
		}
	}

	@Test 
	public void validaVerificaUnidadeFuncionalTipoComCaracteristicaTipoI()
			throws ApplicationBusinessException {

		AghUnidadesFuncionais unfBusca = new AghUnidadesFuncionais();
		TipoUnfLdmEnum tipoUnidadeFuncionalI = TipoUnfLdmEnum.TIPO_UNF_I;

		obterUnidadeFuncionalPorChavePrimariaComCaracteristica();
		buscarAghParametro();
		AghUnidadesFuncionais unfValidacaoI = systemUnderTest.getAghuFacade()
				.obterUnidadeFuncionalPorChavePrimaria(unfBusca);
		// Caracteristicas para validacao
		ConstanteAghCaractUnidFuncionais[] caracteristicas = new ConstanteAghCaractUnidFuncionais[3];
		caracteristicas[0] = ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA;
		caracteristicas[1] = ConstanteAghCaractUnidFuncionais.AREA_FECHADA_BANCO_DE_SANGUE;
		caracteristicas[2] = ConstanteAghCaractUnidFuncionais.UNID_ZONA_14;
		unfValidacaoI = setCaracteristicasTeste(unfValidacaoI, caracteristicas);

		try {
			systemUnderTest.verificaUnidadeFuncional(unfValidacaoI,
					tipoUnidadeFuncionalI);
//			mockingContext.assertIsSatisfied();
		} catch (ApplicationBusinessException e) {
			Assert
					.fail("Não deveria lançar exceção, pois possui a característica");
		}
	}

//	@Test 
	public void validaVerificaUnidadeFuncionalTipoSemCaracteristicaTipoI()
			throws ApplicationBusinessException {

		AghUnidadesFuncionais unfBusca = new AghUnidadesFuncionais();
		TipoUnfLdmEnum tipoUnidadeFuncionalI = TipoUnfLdmEnum.TIPO_UNF_I;

		obterUnidadeFuncionalPorChavePrimariaSemCaracteristica();
		buscarAghParametro();
		AghUnidadesFuncionais unfValidacaoI = systemUnderTest.getAghuFacade()
				.obterUnidadeFuncionalPorChavePrimaria(unfBusca);

		try {
			systemUnderTest.verificaUnidadeFuncional(unfValidacaoI,
					tipoUnidadeFuncionalI);
			Assert.fail("Não deveria passar sem lançar exceção, pois não possui a característica");
		} catch (ApplicationBusinessException e) {
			if (LocalDispensacaoMedicamentoRNExceptionCode.AFA_00244.equals(e
					.getCode())) {
				Assert.assertEquals(
						LocalDispensacaoMedicamentoRNExceptionCode.AFA_00244, e
								.getCode());
//				mockingContext.assertIsSatisfied();
			} else {
				Assert.fail("Não deveria lançar exceção, diferemte de AFA_00244");
			}
		} catch (Exception e) {
			Assert.fail("Não deveria lançar exceção, diferente de ApplicationBusinessException AFA_00244");
		}
	}
	
	//*********JOURNAL******************************************************************************************************
	
//	@Test 
	public void validaAruPosAtualizacaoRow_comAlteracao(){
//		insertAfaLocalDispensacaoMdtosJn();
		AfaLocalDispensacaoMdtos original = getAfaLocalDispensacaoMdtoOriginal();
		AfaLocalDispensacaoMdtos modificada = getAfaLocalDispensacaoMdtoAlterado();
		
		try {
			systemUnderTest.aruPosAtualizacaoRow(original, modificada, NOME_MICROCOMPUTADOR, new Date());
		} catch (Exception e) {
			log.error(e.getMessage());
			Assert.fail("Não deveria lançar exceção ao inserir journal");
		}
	}
	
	// ********EXPECTATIONS*************************************************************************************************


	// Expectations para farmaciaFacade
	private void obterUnidadeFuncionalPorChavePrimariaComCaracteristica() {
		AghUnidadesFuncionais entidade = new AghUnidadesFuncionais();
		entidade.setAtivo(true);
		entidade.setDescricao("Teste JUnit");
		entidade.setCaracteristicas(setCaracteristircasUnFuncionalTeste());
		
		Mockito.when(mockedAghuFacade.obterUnidadeFuncionalPorChavePrimaria(Mockito.any(AghUnidadesFuncionais.class))).thenReturn(entidade);
	}
	
	private Set<AghCaractUnidFuncionais> setCaracteristircasUnFuncionalTeste() {
		Set<AghCaractUnidFuncionais> caracts = new HashSet<AghCaractUnidFuncionais>();
		AghCaractUnidFuncionais c1 = new AghCaractUnidFuncionais();
		c1.setId(new AghCaractUnidFuncionaisId(Short.valueOf("4"),
				ConstanteAghCaractUnidFuncionais.UNID_COLETA));
		AghCaractUnidFuncionais c2 = new AghCaractUnidFuncionais();
		c2.setId(new AghCaractUnidFuncionaisId(Short.valueOf("5"),
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA));
		AghCaractUnidFuncionais c3 = new AghCaractUnidFuncionais();
		c3.setId(new AghCaractUnidFuncionaisId(Short.valueOf("6"),
				ConstanteAghCaractUnidFuncionais.UNID_ECOGRAFIA));
		caracts.add(c1);
		caracts.add(c2);
		caracts.add(c3);

		return caracts;
	}

	// Expectations para farmaciaFacade
	private void obterUnidadeFuncionalPorChavePrimariaSemCaracteristica() {
		AghUnidadesFuncionais entidade = new AghUnidadesFuncionais();
		entidade.setAtivo(true);
		entidade.setDescricao("Teste JUnit Unidade Funcional S/ Caracteristica");
		entidade.setCaracteristicas(setCaracteristircasUnFuncionalTeste());
		
		Mockito.when(mockedAghuFacade.obterUnidadeFuncionalPorChavePrimaria(Mockito.any(AghUnidadesFuncionais.class))).thenReturn(entidade);		
	}

	// Expectations para farmaciaFacade
	private void buscarAghParametro() throws ApplicationBusinessException {
		AghParametros param = new AghParametros();
		param.setVlrTexto("Unid Internacao,Unid Emergencia,Unid Hosp Dia,CO,Zona Ambulatorio");
		
		Mockito.when(mockedParametroFacade.buscarAghParametro(Mockito.any(AghuParametrosEnum.class))).thenReturn(param);
	}

	public AghUnidadesFuncionais setCaracteristicasTeste(
			AghUnidadesFuncionais entidade,
			ConstanteAghCaractUnidFuncionais[] caracteristicasTeste) {

		Set<AghCaractUnidFuncionais> caractsUnf = new HashSet<AghCaractUnidFuncionais>();

		for (ConstanteAghCaractUnidFuncionais constanteAghCaractUnidFuncionais : caracteristicasTeste) {
			AghCaractUnidFuncionais aghCaract = new AghCaractUnidFuncionais();
			AghCaractUnidFuncionaisId id = new AghCaractUnidFuncionaisId();
			id.setCaracteristica(constanteAghCaractUnidFuncionais);
			aghCaract.setId(id);
			caractsUnf.add(aghCaract);
		}

		entidade.setCaracteristicas(caractsUnf);

		return entidade;
	}
	
	//Expectations para afaMedicamentoEquivalenteJNDAO
//	private void insertAfaLocalDispensacaoMdtosJn(){
//		mockingContext.checking(new Expectations(){{
//			allowing(mockedAfaLocalDispensacaoMdtosJnDAO).persistir(mockedAfaLocalDispensacaoMdtosJn);
//			allowing(mockedAfaLocalDispensacaoMdtosJnDAO).flush();
//		}});
//	}
	
	//Retorna AfaMedicamentoEquivalente original
	private AfaLocalDispensacaoMdtos getAfaLocalDispensacaoMdtoOriginal(){
		AfaLocalDispensacaoMdtos entidade = new AfaLocalDispensacaoMdtos();
		AfaLocalDispensacaoMdtosId id = new AfaLocalDispensacaoMdtosId();
		id.setMedMatCodigo(1);
		id.setUnfSeq(Short.valueOf("1"));
		entidade.setId(id);
		
		Calendar c = Calendar.getInstance();
		c.set(2011,  4, 11);
		entidade.setCriadoEm(c.getTime());
//		entidade.setCriadoEm(DateUtil.obterData(2011, 05, 11));
				
		entidade.setId(new AfaLocalDispensacaoMdtosId());
		entidade.setServidor(new RapServidores());
		entidade.setVersion(1); 
		AghUnidadesFuncionais unidadeSolicitante = new AghUnidadesFuncionais();
		AghUnidadesFuncionais unidadeDispDoseFrac = new AghUnidadesFuncionais();
		AghUnidadesFuncionais unidadeDispAlternativa = new AghUnidadesFuncionais();
		AghUnidadesFuncionais unidadeDispUsoDomiciliar = new AghUnidadesFuncionais();
		AghUnidadesFuncionais unidadeFuncionalDoseIntAlterado = new AghUnidadesFuncionais();
		unidadeSolicitante.setSeq(Short.valueOf("1"));
		unidadeDispDoseFrac.setSeq(Short.valueOf("1"));
		unidadeDispAlternativa.setSeq(Short.valueOf("1"));
		unidadeDispUsoDomiciliar.setSeq(Short.valueOf("1"));
		unidadeFuncionalDoseIntAlterado.setSeq(Short.valueOf("1"));
		entidade.setUnidadeFuncional(unidadeSolicitante);
		entidade.setUnidadeFuncionalDispDoseFrac(unidadeDispDoseFrac);
		entidade.setUnidadeFuncionalDispAlternativa(unidadeDispAlternativa);
		entidade.setUnidadeFuncionalDispUsoDomiciliar(unidadeDispUsoDomiciliar);
		entidade.setUnidadeFuncionalDispDoseInt(unidadeFuncionalDoseIntAlterado);
		
		return entidade;
	}

	
	//Retorna AfaMedicamentoEquivalente alterado
	private AfaLocalDispensacaoMdtos getAfaLocalDispensacaoMdtoAlterado(){
		
		AfaLocalDispensacaoMdtos entidade = new AfaLocalDispensacaoMdtos();
		AfaLocalDispensacaoMdtosId id = new AfaLocalDispensacaoMdtosId();
		id.setMedMatCodigo(1);
		id.setUnfSeq(Short.valueOf("1"));
		entidade.setId(id);
		
		Calendar c = Calendar.getInstance();
		c.set(2011,  4, 11);
		entidade.setCriadoEm(c.getTime());
//		entidade.setCriadoEm(DateUtil.obterData(2011, 05, 11));
		
		entidade.setId(new AfaLocalDispensacaoMdtosId());
		entidade.setServidor(new RapServidores());
		entidade.setVersion(1);
		AghUnidadesFuncionais unidadeSolicitante = new AghUnidadesFuncionais();
		AghUnidadesFuncionais unidadeDispDoseFrac = new AghUnidadesFuncionais();
		AghUnidadesFuncionais unidadeDispAlternativa = new AghUnidadesFuncionais();
		AghUnidadesFuncionais unidadeDispUsoDomiciliar = new AghUnidadesFuncionais();
		AghUnidadesFuncionais unidadeFuncionalDoseIntAlterado = new AghUnidadesFuncionais();
		unidadeSolicitante.setSeq(Short.valueOf("1"));
		unidadeDispDoseFrac.setSeq(Short.valueOf("1"));
		unidadeDispAlternativa.setSeq(Short.valueOf("1"));
		unidadeDispUsoDomiciliar.setSeq(Short.valueOf("1"));
		unidadeFuncionalDoseIntAlterado.setSeq(Short.valueOf("10")); //ALTERADO
		entidade.setUnidadeFuncional(unidadeSolicitante);
		entidade.setUnidadeFuncionalDispDoseFrac(unidadeDispDoseFrac);
		entidade.setUnidadeFuncionalDispAlternativa(unidadeDispAlternativa);
		entidade.setUnidadeFuncionalDispUsoDomiciliar(unidadeDispUsoDomiciliar);
		entidade.setUnidadeFuncionalDispDoseInt(unidadeFuncionalDoseIntAlterado);
		
		return entidade;
	}
}
