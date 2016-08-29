package br.gov.mec.aghu.blococirurgico.business;


import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendasON.MbcAgendasONExceptionCode;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendaDiagnostico;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class MbcAgendasONTest extends AGHUBaseUnitTest<MbcAgendasON> {

	/**
	 * cenario com paciente e prontuario
	 * procedimento com lado cirurgia true
	 * 
	 */
	@Test
	public void verificaLadoCirurgiaObrigatorio(){
		MbcAgendas agendas = new MbcAgendas();
		AipPacientes paciente = new AipPacientes();
		paciente.setProntuario(1234);
		agendas.setPaciente(paciente);
		MbcProcedimentoCirurgicos procedimentoCirurgico = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico.setLadoCirurgia(true);
		agendas.setProcedimentoCirurgico(procedimentoCirurgico);
		
		try {
			systemUnderTest.verificaLadoCirurgiaObrigatorio(agendas);
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendasONExceptionCode.MBC_01834, e.getCode());
			
		}
	}	
	
	/**
	 * cenario com paciente e prontuario
	 * procedimento com lado cirurgia falso
	 * 
	 */
	@Test
	public void verificaLadoCirurgiaNaoObrigatorio(){
		MbcAgendas agendas = new MbcAgendas();
		AipPacientes paciente = new AipPacientes();
		paciente.setProntuario(1234);
		agendas.setPaciente(paciente);
		MbcProcedimentoCirurgicos procedimentoCirurgico = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico.setLadoCirurgia(false);
		agendas.setProcedimentoCirurgico(procedimentoCirurgico);
		
		try {
			systemUnderTest.verificaLadoCirurgiaObrigatorio(agendas);
		} catch (BaseException e) {
			Assert.fail("Exceção nao esperada gerada: MBC_01834");
		}
	}
	
	/**
	 * cenario com paciente e sem prontuario
	 * procedimento com lado cirurgia true
	 * 
	 */
	@Test
	public void verificaLadoCirurgiaSemProntuario(){
		MbcAgendas agendas = new MbcAgendas();
		AipPacientes paciente = new AipPacientes();
		agendas.setPaciente(paciente);
		MbcProcedimentoCirurgicos procedimentoCirurgico = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico.setLadoCirurgia(true);
		agendas.setProcedimentoCirurgico(procedimentoCirurgico);
		
		try {
			systemUnderTest.verificaLadoCirurgiaObrigatorio(agendas);
		} catch (BaseException e) {
			Assert.fail("Exceção nao esperada gerada: MBC_01834");
		}
	}	
	
	/**
	 * cenario com paciente e prontuario
	 * com agenda diagnostico e 
	 * com agh_cid 
	 */
	@Test
	public void verificarCidCodigo(){
		MbcAgendas agendas = new MbcAgendas();
		AipPacientes paciente = new AipPacientes();
		paciente.setProntuario(1234);
		agendas.setPaciente(paciente);
		MbcAgendaDiagnostico agendaDiagnostico = new MbcAgendaDiagnostico();
		AghCid aghCid = new AghCid(); 
		agendaDiagnostico.setAghCid(aghCid);
		try {
			systemUnderTest.verificarCidCodigo(agendas, agendaDiagnostico);
		} catch (BaseException e) {
			Assert.fail("Exceção nao esperada gerada: MBC_01832");
		}
	}	
	
	/**
	 * cenario com paciente e sem prontuario
	 * com agenda diagnostico e 
	 * com agh_cid 
	 */
	@Test
	public void verificarCidCodigoSemProntuario(){
		MbcAgendas agendas = new MbcAgendas();
		AipPacientes paciente = new AipPacientes();
		agendas.setPaciente(paciente);
		MbcAgendaDiagnostico agendaDiagnostico = new MbcAgendaDiagnostico();
		AghCid aghCid = new AghCid(); 
		agendaDiagnostico.setAghCid(aghCid);
		
		try {
			systemUnderTest.verificarCidCodigo(agendas, agendaDiagnostico);
		} catch (BaseException e) {
			Assert.fail("Exceção nao esperada gerada: MBC_01832");
		}
	}
	
	/**
	 * cenario com paciente e prontuario
	 * com agenda diagnostico e 
	 * sem agh_cid 
	 */
	@Test
	public void verificarCidCodigoSemCid(){
		MbcAgendas agendas = new MbcAgendas();
		AipPacientes paciente = new AipPacientes();
		paciente.setProntuario(1234);
		agendas.setPaciente(paciente);
		MbcAgendaDiagnostico agendaDiagnostico = new MbcAgendaDiagnostico();
		try {
			systemUnderTest.verificarCidCodigo(agendas, agendaDiagnostico);
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendasONExceptionCode.MBC_01832, e.getCode());
		}
	}
	
	/**
	 * cenario 
	 * com agenda situaçao ES 
	 * com material especial
	 */
	@Test
	public void verificarTipoESMaterialespecial(){
		MbcAgendas agendas = new MbcAgendas();
		agendas.setIndSituacao(DominioSituacaoAgendas.ES);
		agendas.setMaterialEspecial("cirurgias cardiacas");
		try {
			systemUnderTest.verificarMaterialEspecial(agendas);
		} catch (BaseException e) {
			Assert.fail("Exceção não esperada foi gerada: MBC_01833");
		}
		
	}
	
	
	/**
	 * cenario 
	 * com agenda situaçao LE 
	 * com material especial
	 */
	@Test
	public void verificarTipoLEMaterialespecial(){
		MbcAgendas agendas = new MbcAgendas();
		agendas.setIndSituacao(DominioSituacaoAgendas.LE);
		agendas.setMaterialEspecial("cirurgias cardiacas");
		try {
			systemUnderTest.verificarMaterialEspecial(agendas);
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendasONExceptionCode.MBC_01833, e.getCode());
		}
	}
	
	/**
	 * cenario 
	 * com agenda situaçao LE 
	 * sem material especial
	 */
	@Test
	public void verificarTipoLESemMaterialespecial(){
		MbcAgendas agendas = new MbcAgendas();
		agendas.setIndSituacao(DominioSituacaoAgendas.LE);
		try {
			systemUnderTest.verificarMaterialEspecial(agendas);
		} catch (BaseException e) {
			Assert.fail("Exceção não esperada foi gerada: MBC_01833");
		}
	}
}
