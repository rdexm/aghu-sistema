package br.gov.mec.aghu.farmacia.business;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmSumarioAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;

public class RelatorioPrescricaoPorUnidadeRNTest {

	// Classe a ser testada
	private RelatorioPrescricaoPorUnidadeRN systemUnderTest;

	@Before
	public void doBeforeEachTestCase() {
		// criação do objeto da classe a ser testada, com os devidos métodos
		// sobrescritos.
		systemUnderTest = new RelatorioPrescricaoPorUnidadeRN() {
			
			/**
			 * 
			 */
			private static final long serialVersionUID = -5937879403229509230L;

			public String buscarMensagemLocalizada(String chave, Object[] parametros) {

				if(LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_LEITO.getNameField().equals(chave)) {
					return LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_LEITO.toString() + parametros[0];
				}
				
				if(LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_QUARTO.getNameField().equals(chave)) {
					return LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_QUARTO.toString() + parametros[0];
				}
				
				if(LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL.getNameField().equals(chave)) {
					return LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL.toString() + parametros[0];
				}
				
				return null;
			};
		};
	}
	
	public enum LocalAtendimentoTestEnum {
		RELATORIO_LOCAL_ATENDIMENTO_LEITO("Leito :"), 
		RELATORIO_LOCAL_ATENDIMENTO_QUARTO("Quarto :"), 
		RELATORIO_LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL("Unidade :");

		private String fields;

		private LocalAtendimentoTestEnum(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return this.fields;
		}
		
		public String getNameField() {
			return this.name();
		}
	}
	
	public enum SituacoesPrescricoesRelatorioTestEnum {
		PRESCRICAO_COM_ALTA_MEDICA("A"), 
		PRESCRICAO_EM_USO("U"), 
		PRESCRICAO_PENDENTE("*"),
		NULL("");

		private String fields;

		private SituacoesPrescricoesRelatorioTestEnum(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		public String getNameField() {
			return this.name();
		}
	}
	
	@Test
	public void obterSituacaoPrescricao_prescricaoComAlta(){
		MpmSumarioAlta sumarioAlta = new MpmSumarioAlta();
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		aghAtendimentos.setSumariosAlta(new HashSet<MpmSumarioAlta>());
		aghAtendimentos.getSumariosAlta().add(sumarioAlta);
		String situacaoPrescricao = systemUnderTest.obterSituacaoPrescricao(aghAtendimentos, null, null);
		if(SituacoesPrescricoesRelatorioTestEnum.PRESCRICAO_COM_ALTA_MEDICA.toString().equals(situacaoPrescricao)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A situação prescrição deve ser " + SituacoesPrescricoesRelatorioTestEnum.PRESCRICAO_COM_ALTA_MEDICA.getNameField());
		}
	}
	
	@Test
	public void obterSituacaoPrescricao_prescricaoNula(){
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		MpmPrescricaoMedica prescricao = null;
		
		String situacaoPrescricao = systemUnderTest.obterSituacaoPrescricao(aghAtendimentos, null, prescricao);
		
		if(SituacoesPrescricoesRelatorioTestEnum.NULL.toString().equals(situacaoPrescricao)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A situação prescrição deve ser " + SituacoesPrescricoesRelatorioTestEnum.NULL.getNameField());
		}
	}
	
	@Test
	public void obterSituacaoPrescricao_prescricaoEmUso(){
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setSituacao(DominioSituacaoPrescricao.U);
		
		String situacaoPrescricao = systemUnderTest.obterSituacaoPrescricao(aghAtendimentos, null, prescricao);
		
		if(SituacoesPrescricoesRelatorioTestEnum.PRESCRICAO_EM_USO.toString().equals(situacaoPrescricao)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A situação prescrição deve ser " + SituacoesPrescricoesRelatorioTestEnum.PRESCRICAO_EM_USO.getNameField());
		}
	}
	
	@Test
	public void obterSituacaoPrescricao_prescricaoPendente(){
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setSituacao(DominioSituacaoPrescricao.L);//Qualquer coisa diferente de U
		prescricao.setServidorValida(new RapServidores());
		prescricao.getServidorValida().setId(new RapServidoresId());
		
		String situacaoPrescricao = systemUnderTest.obterSituacaoPrescricao(aghAtendimentos, null, prescricao);
		
		if(SituacoesPrescricoesRelatorioTestEnum.PRESCRICAO_PENDENTE.toString().equals(situacaoPrescricao)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A situação prescrição deve ser " + SituacoesPrescricoesRelatorioTestEnum.PRESCRICAO_PENDENTE.getNameField());
		}
	}
	
	@Test
	public void obterSituacaoPrescricao_servidorValidaNaoNulo(){
		AghAtendimentos aghAtendimentos = new AghAtendimentos();
		MpmPrescricaoMedica prescricao = new MpmPrescricaoMedica();
		prescricao.setSituacao(DominioSituacaoPrescricao.L);//Qualquer coisa diferente de U
		prescricao.setServidorValida(new RapServidores());
		prescricao.getServidorValida().setId(new RapServidoresId());
		prescricao.getServidorValida().getId().setMatricula(123456);
		
		String situacaoPrescricao = systemUnderTest.obterSituacaoPrescricao(aghAtendimentos, null, prescricao);
		
		if(SituacoesPrescricoesRelatorioTestEnum.NULL.toString().equals(situacaoPrescricao)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A situação prescrição deve ser " + SituacoesPrescricoesRelatorioTestEnum.NULL.getNameField());
		}
	}
	
	@Test
	public void obterLocalizacaoPacienteParaRelatorio_aghAtendimentoNull(){
		AghAtendimentos aghAtendimento= null;
		try{
			systemUnderTest.obterLocalizacaoPacienteParaRelatorio(aghAtendimento);
			Assert.fail("Deveria ter lançado IllegalArgumentException");
		}catch (IllegalArgumentException e) {
			Assert.assertTrue(true);
		}
	}
	
	@Test
	public void obterLocalizacaoPacienteParaRelatorio_leitoNotNull(){
		AghAtendimentos aghAtendimento= new AghAtendimentos();
		AinLeitos leito = new AinLeitos();
		leito.setLeitoID("123456");
		aghAtendimento.setLeito(leito);
		
		String localCorreto = LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_LEITO + aghAtendimento.getLeito().getLeitoID();
		
		String localizacaoPaciente = systemUnderTest.obterLocalizacaoPacienteParaRelatorio(aghAtendimento);
		
		if(localCorreto.equals(localizacaoPaciente)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A localização do paciente deve ser "  + localCorreto);
		}
	}
	
	@Test
	public void obterLocalizacaoPacienteParaRelatorio_quartoNotNull(){
		AghAtendimentos aghAtendimento= new AghAtendimentos();
		AinQuartos quarto = new AinQuartos();
		quarto.setNumero(Short.valueOf("12345"));
		aghAtendimento.setQuarto(quarto);
		
		String localCorreto = LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_QUARTO + aghAtendimento.getQuarto().getDescricao();
		
		String localizacaoPaciente = systemUnderTest.obterLocalizacaoPacienteParaRelatorio(aghAtendimento);
		
		if(localCorreto.equals(localizacaoPaciente)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A localização do paciente deve ser "  + localCorreto);
		}
	}
	
	@Test
	public void obterLocalizacaoPacienteParaRelatorio_siglaUnidadeFuncionalNotNull(){
		AghAtendimentos aghAtendimento= new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setSigla("REUMATO");
		aghAtendimento.setUnidadeFuncional(unidadeFuncional);
		
		String localCorreto = LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL + aghAtendimento.getUnidadeFuncional().getSigla();
		
		String localizacaoPaciente = systemUnderTest.obterLocalizacaoPacienteParaRelatorio(aghAtendimento);
		
		if(localCorreto.equals(localizacaoPaciente)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A localização do paciente deve ser "  + localCorreto);
		}
	}
	
	//@Test
	public void obterLocalizacaoPacienteParaRelatorio_andarAlaUnidadeFuncionalNotNull(){
		AghAtendimentos aghAtendimento= new AghAtendimentos();
		AghUnidadesFuncionais unidadeFuncional = new AghUnidadesFuncionais();
		unidadeFuncional.setIndAla(AghAla.L);
		unidadeFuncional.setDescricao("ABC");
		aghAtendimento.setUnidadeFuncional(unidadeFuncional);
		
		String localCorreto = LocalAtendimentoTestEnum.RELATORIO_LOCAL_ATENDIMENTO_UNIDADE_FUNCIONAL + aghAtendimento.getUnidadeFuncional().getAndarAlaDescricao();
		
		String localizacaoPaciente = systemUnderTest.obterLocalizacaoPacienteParaRelatorio(aghAtendimento);
		
		if(localCorreto.equals(localizacaoPaciente)){
			Assert.assertTrue(true);
		}else{
			Assert.fail("A localização do paciente deve ser " + localCorreto);
		}
	}
}