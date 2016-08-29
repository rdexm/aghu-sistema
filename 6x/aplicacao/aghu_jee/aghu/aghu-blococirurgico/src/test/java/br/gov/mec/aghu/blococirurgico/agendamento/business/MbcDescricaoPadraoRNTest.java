package br.gov.mec.aghu.blococirurgico.agendamento.business;

import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.agendamento.business.MbcDescricaoPadraoRN.DescricaoPadraoRNExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MbcDescricaoPadrao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MbcDescricaoPadraoRNTest extends AGHUBaseUnitTest<MbcDescricaoPadraoRN>{

	/**
	 * Teste enviando especialidade ativa
	 */
	@Test
	public void verificaEspecialidadeAtiva() {
		MbcDescricaoPadrao descricaoPadrao = new MbcDescricaoPadrao();
		
		AghEspecialidades aghEspecialidades = new AghEspecialidades();
		aghEspecialidades.setIndSituacao(DominioSituacao.A);
		descricaoPadrao.setAghEspecialidades(aghEspecialidades);
		
		try {
			systemUnderTest.verificaEspecialidadeAtiva(descricaoPadrao);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada não esperada: " + e.getMessage());
		}
	}

	/**
	 * Teste enviando especialidade Inativa
	 */
	@Test
	public void verificaEspecialidadeNaoAtiva() {
		MbcDescricaoPadrao descricaoPadrao = new MbcDescricaoPadrao();
		
		AghEspecialidades aghEspecialidades = new AghEspecialidades();
		aghEspecialidades.setIndSituacao(DominioSituacao.I);
		descricaoPadrao.setAghEspecialidades(aghEspecialidades);
		
		try {
			systemUnderTest.verificaEspecialidadeAtiva(descricaoPadrao);
			Assert.fail("Exceção esperada naão gerada: MBC_00667");
		} catch (BaseException e) {
			Assert.assertEquals(DescricaoPadraoRNExceptionCode.MBC_00667, e.getCode());
		}
	}

	
	/**
	 * Teste enviando procedimento ativa
	 */
	@Test
	public void verificaProcedimentoAtivo() {
		MbcDescricaoPadrao descricaoPadrao = new MbcDescricaoPadrao();
		
		MbcProcedimentoCirurgicos procedimentoCirurgicos = new MbcProcedimentoCirurgicos();
		procedimentoCirurgicos.setIndSituacao(DominioSituacao.A);
		descricaoPadrao.setMbcProcedimentoCirurgicos(procedimentoCirurgicos);
		
		try {
			systemUnderTest.verificaProcedimentoAtivo(descricaoPadrao);
		} catch (BaseException e) {
			Assert.fail("Exceção gerada não esperada: " + e.getMessage());
		}
	}

	/**
	 * Teste enviando especialidade Inativa
	 */
	@Test
	public void verificaProcedimentoNaoAtivo() {
		MbcDescricaoPadrao descricaoPadrao = new MbcDescricaoPadrao();
		
		MbcProcedimentoCirurgicos procedimentoCirurgicos = new MbcProcedimentoCirurgicos();
		procedimentoCirurgicos.setIndSituacao(DominioSituacao.I);
		descricaoPadrao.setMbcProcedimentoCirurgicos(procedimentoCirurgicos);
		
		try {
			systemUnderTest.verificaProcedimentoAtivo(descricaoPadrao);
			Assert.fail("Exceção esperada naão gerada: MBC_00683");
		} catch (BaseException e) {
			Assert.assertEquals(DescricaoPadraoRNExceptionCode.MBC_00683, e.getCode());
		}
	}
	
	

}
