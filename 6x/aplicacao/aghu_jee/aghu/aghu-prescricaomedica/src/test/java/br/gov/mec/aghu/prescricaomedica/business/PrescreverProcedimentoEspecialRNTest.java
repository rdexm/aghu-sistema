package br.gov.mec.aghu.prescricaomedica.business;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MpmPrescricaoProcedimento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * 
 * 25/10/2010
 * 
 * @author bsoliveira
 *
 */
public class PrescreverProcedimentoEspecialRNTest extends AGHUBaseUnitTest<PrescreverProcedimentoEspecialRN>{

	@Mock
	private IFaturamentoFacade mockedFaturamentoFacade;

	/**
	 * Deve gerar exceção por os parametros serem nulos
	 */
	@Test(expected=IllegalArgumentException.class)
	public void verificarDuracaoTratamentoSolicitado001() {
		try {
			this.systemUnderTest.verificarDuracaoTratamentoSolicitado(null, null);
		} catch (BaseException expected) {
			Assert.fail();
		}		
	}
	
	/**
	 * Deve gerar exceção por o segundo parametro ser nulo
	 */
	@Test(expected=IllegalArgumentException.class)
	public void verificarDuracaoTratamentoSolicitado002() {
		try {
			this.systemUnderTest.verificarDuracaoTratamentoSolicitado(getMpmPrescricaoProcedimento(), null);
		} catch (BaseException expected) {
			Assert.fail();
		}		
	}
	
	/**
	 * Deve gerar exceção por o primeiro parametro ser nulo
	 */
	@Test(expected=IllegalArgumentException.class)
	public void verificarDuracaoTratamentoSolicitado003() {
		try {
			this.systemUnderTest.verificarDuracaoTratamentoSolicitado(null, new AghAtendimentos());
		} catch (BaseException expected) {
			Assert.fail();
		}		
	}
	
	private AghAtendimentos getAghAtendimentosComInternacaoValida() {
		FatConvenioSaudePlano convenio = new FatConvenioSaudePlano();
		convenio.setId(new FatConvenioSaudePlanoId(Short.valueOf("1"), Byte.valueOf("12")));
		
		AinInternacao internacao = new AinInternacao();
		internacao.setConvenioSaudePlano(convenio);
		
		AghAtendimentos atd = new AghAtendimentos();
		atd.setInternacao(internacao);
		
		return atd;
	}

	private AghAtendimentos getAghAtendimentosComInternacaoInvalida() {
		AinInternacao internacao = new AinInternacao();
		
		AghAtendimentos atd = new AghAtendimentos();
		atd.setInternacao(internacao);
		
		return atd;
	}
	
	private MpmPrescricaoProcedimento getMpmPrescricaoProcedimento() {
		MpmPrescricaoProcedimento newMpmPrescricaoProcedimento = new MpmPrescricaoProcedimento();
		
		ScoMaterial matCodigo = new ScoMaterial();
		newMpmPrescricaoProcedimento.setMatCodigo(matCodigo);
		
		return newMpmPrescricaoProcedimento;
	}

	/**
	 * Deve gerar exceção por nao encontrar um valor para duracao tratamento solicitado.
	 * @throws BaseException 
	 * 
	 */
	public void verificarDuracaoTratamentoSolicitado004() throws BaseException {		
		//Implementação do Mock
		FatConvGrupoItemProced umFatConvGrupoItensProced = new FatConvGrupoItemProced();
		umFatConvGrupoItensProced.setIndInformaTempoTrat(true);
		
		final FatProcedHospInternos umFatProcedHospInternos = new FatProcedHospInternos();
		umFatProcedHospInternos.setSeq(Integer.valueOf("1"));
		
		List<FatProcedHospInternos> lista = new ArrayList<FatProcedHospInternos>();
		lista.add(umFatProcedHospInternos);
		
		Mockito.when(mockedFaturamentoFacade.buscaProcedimentoHospitalarInterno(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(lista);

		Mockito.when(mockedFaturamentoFacade.obterFatConvGrupoItensProcedPeloId(Mockito.anyShort(), Mockito.anyByte(), Mockito.anyInt())).thenReturn(umFatConvGrupoItensProced);
		
		MpmPrescricaoProcedimento entity = getMpmPrescricaoProcedimento();
		entity.setDuracaoTratamentoSolicitado(Short.valueOf("1"));
		this.systemUnderTest.verificarDuracaoTratamentoSolicitado(getMpmPrescricaoProcedimento(), getAghAtendimentosComInternacaoValida());
				
	}

	/**
	 * Deve gerar exceção por encontrar um valor para duracao tratamento solicitado.
	 * 
	 */
	public void verificarDuracaoTratamentoSolicitado005() {		
		//Implementação do Mock
		FatConvGrupoItemProced umFatConvGrupoItensProced = new FatConvGrupoItemProced();
		umFatConvGrupoItensProced.setIndInformaTempoTrat(false);
		

		final FatProcedHospInternos umFatProcedHospInternos = new FatProcedHospInternos();
		umFatProcedHospInternos.setSeq(Integer.valueOf("1"));

		List<FatProcedHospInternos> lista = new ArrayList<FatProcedHospInternos>();
		lista.add(umFatProcedHospInternos);

		Mockito.when(mockedFaturamentoFacade.buscaProcedimentoHospitalarInterno(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(lista);

		Mockito.when(mockedFaturamentoFacade.obterFatConvGrupoItensProcedPeloId(Mockito.anyShort(), Mockito.anyByte(), Mockito.anyInt())).thenReturn(umFatConvGrupoItensProced);

		MpmPrescricaoProcedimento entity = getMpmPrescricaoProcedimento();
		entity.setDuracaoTratamentoSolicitado(Short.valueOf("1"));
		try {
			this.systemUnderTest.verificarDuracaoTratamentoSolicitado(entity, getAghAtendimentosComInternacaoValida());
			Assert.fail();
		} catch (BaseException expected) {
			assertEquals(expected.getMessage(), "MPM_03604");
		}		
	}

	/**
	 * Deve gerar exceção por a internacao estar invalida.
	 * 
	 */
	@Test
	public void verificarDuracaoTratamentoSolicitado006() {

		MpmPrescricaoProcedimento entity = getMpmPrescricaoProcedimento();
		try {
			this.systemUnderTest.verificarDuracaoTratamentoSolicitado(entity, getAghAtendimentosComInternacaoInvalida());
			Assert.fail();
		} catch (BaseException expected) {
			assertEquals(expected.getMessage(), "AIN_00268");
		}		
	}
	
	/**
	 * Deve gerar exceção por o AtendimentoUrgencia estar invalido.
	 * 
	 */
	@Test
	public void verificarDuracaoTratamentoSolicitado007() {

		MpmPrescricaoProcedimento entity = getMpmPrescricaoProcedimento();
		try {
			this.systemUnderTest.verificarDuracaoTratamentoSolicitado(entity, getAghAtendimentosComAtendimentoUrgenciaInvalida());
			Assert.fail();
		} catch (BaseException expected) {
			assertEquals(expected.getMessage(), "MPM_01175");
		}		
	}

	private AghAtendimentos getAghAtendimentosComAtendimentoUrgenciaInvalida() {
		AinAtendimentosUrgencia atendimentoUrgencia = new AinAtendimentosUrgencia();
		
		AghAtendimentos atd = new AghAtendimentos();
		atd.setAtendimentoUrgencia(atendimentoUrgencia);
		
		return atd;	
	}

	/**
	 * Deve gerar exceção por o HospitalDia estar invalido.
	 * 
	 */
	@Test
	public void verificarDuracaoTratamentoSolicitado008() {

		MpmPrescricaoProcedimento entity = getMpmPrescricaoProcedimento();
		try {
			this.systemUnderTest.verificarDuracaoTratamentoSolicitado(entity, getAghAtendimentosComHospitalDiaInvalida());
			Assert.fail();
		} catch (BaseException expected) {
			assertEquals(expected.getMessage(), "AHD_00090");
		}		
	}

	private AghAtendimentos getAghAtendimentosComHospitalDiaInvalida() {
		AhdHospitaisDia hospitalDia = new AhdHospitaisDia();
		
		AghAtendimentos atd = new AghAtendimentos();
		atd.setHospitalDia(hospitalDia);
		
		return atd;	
	}
	
	/**
	 * Deve gerar exceção por nao encontrar uma das associacoes obrigatorias.
	 * @throws BaseException 
	 * 
	 */
	public void verificarDuracaoTratamentoSolicitado009() throws BaseException {		
		//Implementação do Mock
		FatConvGrupoItemProced umFatConvGrupoItensProced = new FatConvGrupoItemProced();
		umFatConvGrupoItensProced.setIndInformaTempoTrat(true);
		
		final FatProcedHospInternos umFatProcedHospInternos = new FatProcedHospInternos();

		List<FatProcedHospInternos> lista = new ArrayList<FatProcedHospInternos>();
		lista.add(umFatProcedHospInternos);

		Mockito.when(mockedFaturamentoFacade.buscaProcedimentoHospitalarInterno(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyShort())).thenReturn(lista);

		Mockito.when(mockedFaturamentoFacade.obterFatConvGrupoItensProcedPeloId(Mockito.anyShort(), Mockito.anyByte(), Mockito.anyInt())).thenReturn(umFatConvGrupoItensProced);

		this.systemUnderTest.verificarDuracaoTratamentoSolicitado(getMpmPrescricaoProcedimento(), getAghAtendimentosComInternacaoValida());
			
	}

	
	/**
	 * Não deve gerar exceção por todos os parametros estarem nulos.
	 * @throws BaseException
	 */
	@Test
	public void verificarConvenioExigeJustificativaParaProcedimento001() throws BaseException {

		this.systemUnderTest.verificarConvenioExigeJustificativaParaProcedimento(null, null, null, null);

	}
	
	/**
	 * Deve gerar exceção por matCodigo não é nulo.
	 * @throws BaseException
	 */
	@Test(expected=BaseException.class)
	public void verificarConvenioExigeJustificativaParaProcedimento002() throws BaseException {

		this.systemUnderTest.verificarConvenioExigeJustificativaParaProcedimento(Integer.valueOf("1"), null, null, null);

	}
	
	/**
	 * Deve gerar exceção por pciSeq não é nulo.
	 * @throws BaseException
	 */
	@Test(expected=BaseException.class)
	public void verificarConvenioExigeJustificativaParaProcedimento003() throws BaseException {

		this.systemUnderTest.verificarConvenioExigeJustificativaParaProcedimento(null, Integer.valueOf("1"), null, null);

	}
	
	/**
	 * Deve gerar exceção por pedSeq não é nulo.
	 * @throws BaseException
	 */
	@Test(expected=BaseException.class)
	public void verificarConvenioExigeJustificativaParaProcedimento004() throws BaseException {

		this.systemUnderTest.verificarConvenioExigeJustificativaParaProcedimento(null, null, Short.valueOf("1"), null);
	
	}	
	
}
