package br.gov.mec.aghu.exames.contratualizacao.business;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.exames.contratualizacao.business.PacienteContratualizacaoCommand.PacienteContratualizacaoActionExceptionCode;
import br.gov.mec.aghu.model.AipCepLogradouros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.AipLogradouros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class PacienteContratualizacaoCommandTest extends AGHUBaseUnitTest<PacienteContratualizacaoCommand>{

	public enum PacienteContratualizacaoActionTestExceptionCode implements
		BusinessExceptionCode {
		MENSAGEM_NRO_CARTAO_SAUDE_INVALIDO;

	}

	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private IPacienteFacade mockedPacienteFacade;
	@Mock
	private ICadastroPacienteFacade mockedCadastroPacienteFacade;

	protected Throwable e;


	@Test
	public void localizarPacienteNomePacienteNullTest() {

		try {
			systemUnderTest.localizarPaciente(null, "TESTE NOME MAE", "10012012","F", "1212", "TESTE LOGRADOURO", "9898", null,
					"TESTE BAIRRO", (Integer) 98989, "TESTE CIDADE", "RS",
					999, "I", 99, NOME_MICROCOMPUTADOR);			
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_PACIENTE);
		} catch (ParseException e) {
			Assert.fail(e.getMessage());		
		}

	}

	@Test
	public void localizarPacienteLogradouroNullTest() {

		try {
			systemUnderTest.localizarPaciente("TESTE NOME", "TESTE NOME MAE", "10012012","F", "1212", null, "9898", null,
					"TESTE BAIRRO", (Integer) 98989, "TESTE CIDADE", "RS",
					999, "I", 99, NOME_MICROCOMPUTADOR);			
			
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_ENDERECO);
		} catch (ParseException e) {
			Assert.fail(e.getMessage());
		}

	}

	
	@Test
	public void validarDataObitoComSucessoTest(){
		try {
			systemUnderTest.validarDataObito(new AipPacientes());
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
		
	}
	
	@Test
	public void validarDataObitoComExceptionTest(){
		
		AipPacientes paciente = new AipPacientes();
		paciente.setDtObito(new Date());
		
		try {
			systemUnderTest.validarDataObito(paciente);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(
					e.getCode(),
					PacienteContratualizacaoActionExceptionCode.MENSAGEM_PACIENTE_DATA_OBITO_REGISTRADA);
		}
		
	}
	
	@Test
	public void ajustarNroLogradouroTamanhoIgualPermitidoTest(){
			Integer numLogradouro = systemUnderTest.ajustarNroLogradouro("12345");			
			Assert.assertEquals(numLogradouro, (Integer)12345);
	}
	
	@Test
	public void ajustarNroLogradouroTamanhoMenorPermitidoTest(){
			Integer numLogradouro = systemUnderTest.ajustarNroLogradouro("1234");			
			Assert.assertEquals(numLogradouro, (Integer)1234);		
	}
	
	@Test
	public void ajustarNroLogradouroTamanhoMaiorPermitidoTest(){
			Integer numLogradouro = systemUnderTest.ajustarNroLogradouro("1234567");			
			Assert.assertEquals(numLogradouro, (Integer)0);		
	}

	@Test
	public void carregarCidadePeloCepTest(){
		
		AipCidades cidade = new AipCidades();
		cidade.setCodigo(4083);
		
		AipLogradouros logradouro = new AipLogradouros();
		logradouro.setAipCidade(cidade);
		
		AipCepLogradouros cepLogradouros = new AipCepLogradouros();
		cepLogradouros.setLogradouro(logradouro);
						
		final List<AipCepLogradouros> listCepLogradouro = new ArrayList<AipCepLogradouros>();
		listCepLogradouro.add(cepLogradouros);
		
		Mockito.when(mockedPacienteFacade.listarCepLogradourosPorCEP(Mockito.anyInt())).thenReturn(listCepLogradouro);

		AipCidades cidadeRet = systemUnderTest.carregarCidade(99999999, null, null);
		Assert.assertNotNull(cidadeRet);
		Assert.assertEquals(cidadeRet.getCodigo(), (Integer)4083);
	}
	
	@Test
	public void carregarCidadePeloNomeUfTest(){

		AipLogradouros logradouro = new AipLogradouros();
		
		AipCepLogradouros cepLogradouros = new AipCepLogradouros();
		cepLogradouros.setLogradouro(logradouro);
						
		final List<AipCepLogradouros> listCepLogradouro = new ArrayList<AipCepLogradouros>();
		listCepLogradouro.add(cepLogradouros);
		
		
		AipCidades cidade = new AipCidades();
		cidade.setCodigo(4083);
		
		final List<AipCidades> listaCidades = new ArrayList<AipCidades>();
		listaCidades.add(cidade);
		
		Mockito.when(mockedPacienteFacade.listarCepLogradourosPorCEP(Mockito.anyInt())).thenReturn(listCepLogradouro);

		Mockito.when(mockedPacienteFacade.pesquisarCidadesPorNomeSiglaUf(Mockito.anyString(), Mockito.anyString())).thenReturn(listaCidades);

		AipCidades cidadeRet = systemUnderTest.carregarCidade(null, "TESTE NOME CIDADE", "RS");
		Assert.assertNotNull(cidadeRet);
		Assert.assertEquals(cidadeRet.getCodigo(), (Integer)4083);
	}
	
	@Test
	public void ajustarNomePacienteSucessoTest(){
		
		String nomeAjustado;
		try {
			nomeAjustado = systemUnderTest.ajustarNome("TesTE NOME PACIENTE 2", true);
			Assert.assertEquals(nomeAjustado, "TESTE NOME PACIENTE");
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
				
	}
	
	@Test
	public void ajustarNomePacienteTamanhoMaiorPermitidoTest(){
		
		try {
			systemUnderTest.ajustarNome("TesTE NOME PACIENTE COM TAMANHO SUPERIOR AO PERMITIDO", true);
			Assert.fail("Deveria ter gerado uma exception!");
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), PacienteContratualizacaoActionExceptionCode.MENSAGEM_NOME_PACIENTE_MAIOR_PERMITIDO);
		}
				
	}

	@Test
	public void ajustarNomeMaePacienteSucessoTest(){
		
		String nomeAjustado;
		try {
			nomeAjustado = systemUnderTest.ajustarNome("TesTE NOME Mãe PACIENTE 2", false);
			Assert.assertEquals(nomeAjustado, "TESTE NOME MAE PACIENTE");
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
				
	}
	
	@Test
	public void ajustarNomeMaePacienteTamanhoMaiorPermitidoTest(){
		
		String nomeAjustado;
		try {
			nomeAjustado = systemUnderTest.ajustarNome("TesTE NOME Mãe PACIENTE COM TAMANHO SUPERIOR AO PERMITIDO", false);
			Assert.assertEquals(nomeAjustado, "TESTE NOME MAE PACIENTE COM TAMANHO SUPERIOR AO PERMITIDO");
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
				
	}

	@Test
	public void carregarSexoPacienteSucessoTest(){
		
		try {
			DominioSexo sexo = systemUnderTest.carregarSexoPaciente("M");
			Assert.assertEquals(sexo.getDescricao(), DominioSexo.M.getDescricao());
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
				
	}
	
	@Test
	public void carregarSexoPacienteInvalidoTest(){
		
		try {
			DominioSexo sexo = systemUnderTest.carregarSexoPaciente("X");
			Assert.assertEquals(sexo.getDescricao(), DominioSexo.M.getDescricao());
		} catch (BaseException e) {
			Assert.assertEquals(e.getCode(), PacienteContratualizacaoActionExceptionCode.MENSAGEM_SEXO_PACIENTE_INVALIDO);
		}
				
	}
		
}
