package br.gov.mec.aghu.paciente.prontuarioonline.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersaoDocumento;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghVersaoDocumento;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.InternacaoVO.StatusAltaObito;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Classe responsável pelos testes unitários da classe ConsultaInternacaoON.<br>
 * 
 * @author dcastro
 * 
 */
public class ConsultaInternacaoONTest extends AGHUBaseUnitTest<ConsultaInternacaoON>{
	
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;
	@Mock
	private ICertificacaoDigitalFacade mockedCertificacaoDigitalFacade;

	@Test(expected = IllegalArgumentException.class)
	public void obterStatusSumarioInternacaoComException() {

		this.systemUnderTest.atualizarStatusSumarioInternacao(null, null, null);
		Assert.fail("Deveria ter ocorrido uma IllegalArgumentException");

	}

	@Test
	public void obterStatusSumarioInternacaoComObitoNulo() {

		final DominioSimNao isObito = null;
		final DominioSimNao certificacaoEletronica = DominioSimNao.S;
		final AinInternacao internacao = new AinInternacao();
		final InternacaoVO internacaoVO = new InternacaoVO();
		
		AipPacientes paciente = new AipPacientes();
		paciente.setIndPacProtegido(DominioSimNao.N);
		internacao.setPaciente(paciente);

		Mockito.when(mockedPrescricaoMedicaFacade.verificarAltaSumarioObito(Mockito.any(AghAtendimentos.class))).thenReturn(isObito);

		try {
			this.systemUnderTest.atualizarStatusSumarioInternacao(internacao,
					certificacaoEletronica, internacaoVO);
			Assert.assertEquals(internacaoVO.getStatusAltaObito(),
					StatusAltaObito.NAO_APRESENTA_DOCUMENTO_ALTA_OBITO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void obterStatusSumarioInternacaoComObitoSimCertificacaoNao() {

		final DominioSimNao isObito = DominioSimNao.S;
		final DominioSimNao certificacaoEletronica = DominioSimNao.N;
		final AinInternacao internacao = new AinInternacao();
		final InternacaoVO internacaoVO = new InternacaoVO();

		AipPacientes paciente = new AipPacientes();
		paciente.setIndPacProtegido(DominioSimNao.N);
		internacao.setPaciente(paciente);
		
		Mockito.when(mockedPrescricaoMedicaFacade.verificarAltaSumarioObito(Mockito.any(AghAtendimentos.class))).thenReturn(isObito);

		try {
			this.systemUnderTest.atualizarStatusSumarioInternacao(internacao,
					certificacaoEletronica, internacaoVO);
			Assert.assertEquals(internacaoVO.getStatusAltaObito(),
					StatusAltaObito.APRESENTA_RELATORIO_SUMARIO_OBITO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void obterStatusSumarioInternacaoComObitoNaoCertificacaoNao() {

		final DominioSimNao isObito = DominioSimNao.N;
		final DominioSimNao certificacaoEletronica = DominioSimNao.N;
		final AinInternacao internacao = new AinInternacao();
		final InternacaoVO internacaoVO = new InternacaoVO();

		AipPacientes paciente = new AipPacientes();
		paciente.setIndPacProtegido(DominioSimNao.N);
		internacao.setPaciente(paciente);
		
		Mockito.when(mockedPrescricaoMedicaFacade.verificarAltaSumarioObito(Mockito.any(AghAtendimentos.class))).thenReturn(isObito);

		try {
			this.systemUnderTest.atualizarStatusSumarioInternacao(internacao,
					certificacaoEletronica, internacaoVO);
			Assert.assertEquals(internacaoVO.getStatusAltaObito(),
					StatusAltaObito.APRESENTA_RELATORIO_SUMARIO_ALTA);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}
	
	@Test
	public void obterStatusSumarioInternacaoComObitoSimCertificacaoSimSituacaoAssinado() {

		final DominioSimNao isObito = DominioSimNao.S;
		final DominioSimNao certificacaoEletronica = DominioSimNao.S;
		final InternacaoVO internacaoVO = new InternacaoVO();
		final AghVersaoDocumento versaoDocumento = new AghVersaoDocumento();
		versaoDocumento.setSeq(1);
		versaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.A);
		
		AghAtendimentos atendimento = new AghAtendimentos(1);
		AinInternacao internacao = new AinInternacao();
		internacao.setAtendimento(atendimento);

		AipPacientes paciente = new AipPacientes();
		paciente.setIndPacProtegido(DominioSimNao.N);
		internacao.setPaciente(paciente);

		Mockito.when(mockedPrescricaoMedicaFacade.verificarAltaSumarioObito(Mockito.any(AghAtendimentos.class))).thenReturn(isObito);

		Mockito.when(mockedCertificacaoDigitalFacade.verificarSituacaoDocumentoPorPaciente(Mockito.anyInt(), Mockito.any(DominioTipoDocumento.class))).thenReturn(versaoDocumento);

		try {
			this.systemUnderTest.atualizarStatusSumarioInternacao(internacao,
					certificacaoEletronica, internacaoVO);
			Assert.assertEquals(internacaoVO.getStatusAltaObito(),
					StatusAltaObito.APRESENTA_DOCUMENTO_ASSINADO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void obterStatusSumarioInternacaoComObitoNaoCertificacaoSimSituacaoAssinado() {

		final DominioSimNao isObito = DominioSimNao.N;
		final DominioSimNao certificacaoEletronica = DominioSimNao.S;
		final InternacaoVO internacaoVO = new InternacaoVO();
		final AghVersaoDocumento versaoDocumento = new AghVersaoDocumento();
		versaoDocumento.setSeq(1);
		versaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.A);

		AghAtendimentos atendimento = new AghAtendimentos(1);
		AinInternacao internacao = new AinInternacao();
		internacao.setAtendimento(atendimento);

		AipPacientes paciente = new AipPacientes();
		paciente.setIndPacProtegido(DominioSimNao.N);
		internacao.setPaciente(paciente);

		Mockito.when(mockedPrescricaoMedicaFacade.verificarAltaSumarioObito(Mockito.any(AghAtendimentos.class))).thenReturn(isObito);

		Mockito.when(mockedCertificacaoDigitalFacade.verificarSituacaoDocumentoPorPaciente(Mockito.anyInt(), Mockito.any(DominioTipoDocumento.class))).thenReturn(versaoDocumento);

		try {
			this.systemUnderTest.atualizarStatusSumarioInternacao(internacao,
					certificacaoEletronica, internacaoVO);
			Assert.assertEquals(internacaoVO.getStatusAltaObito(),
					StatusAltaObito.APRESENTA_DOCUMENTO_ASSINADO);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

	@Test
	public void obterStatusSumarioInternacaoComObitoNaoCertificacaoSimSituacaoNulo() {

		final DominioSimNao isObito = DominioSimNao.N;
		final DominioSimNao certificacaoEletronica = DominioSimNao.S;
		final InternacaoVO internacaoVO = new InternacaoVO();
		final AghVersaoDocumento versaoDocumento = new AghVersaoDocumento();
		versaoDocumento.setSeq(1);		

		AghAtendimentos atendimento = new AghAtendimentos(1);
		AinInternacao internacao = new AinInternacao();
		internacao.setAtendimento(atendimento);

		AipPacientes paciente = new AipPacientes();
		paciente.setIndPacProtegido(DominioSimNao.N);
		internacao.setPaciente(paciente);

		Mockito.when(mockedPrescricaoMedicaFacade.verificarAltaSumarioObito(Mockito.any(AghAtendimentos.class))).thenReturn(isObito);

		Mockito.when(mockedCertificacaoDigitalFacade.verificarSituacaoDocumentoPorPaciente(Mockito.anyInt(), Mockito.any(DominioTipoDocumento.class))).thenReturn(versaoDocumento);

		try {
			this.systemUnderTest.atualizarStatusSumarioInternacao(internacao,
					certificacaoEletronica, internacaoVO);
			Assert.assertEquals(internacaoVO.getStatusAltaObito(),
					StatusAltaObito.APRESENTA_RELATORIO_SUMARIO_ALTA);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}
	
	@Test
	public void obterStatusSumarioInternacaoComCertificacaoSimSituacaoPendente() {

		final DominioSimNao isObito = DominioSimNao.S;
		final DominioSimNao certificacaoEletronica = DominioSimNao.S;
		final InternacaoVO internacaoVO = new InternacaoVO();
		final AghVersaoDocumento versaoDocumento = new AghVersaoDocumento();
		versaoDocumento.setSeq(1);
		versaoDocumento.setSituacao(DominioSituacaoVersaoDocumento.P);

		AghAtendimentos atendimento = new AghAtendimentos(1);
		AinInternacao internacao = new AinInternacao();
		internacao.setAtendimento(atendimento);

		AipPacientes paciente = new AipPacientes();
		paciente.setIndPacProtegido(DominioSimNao.N);
		internacao.setPaciente(paciente);

		Mockito.when(mockedPrescricaoMedicaFacade.verificarAltaSumarioObito(Mockito.any(AghAtendimentos.class))).thenReturn(isObito);

		Mockito.when(mockedCertificacaoDigitalFacade.verificarSituacaoDocumentoPorPaciente(Mockito.anyInt(), Mockito.any(DominioTipoDocumento.class))).thenReturn(versaoDocumento);

		try {
			this.systemUnderTest.atualizarStatusSumarioInternacao(internacao,
					certificacaoEletronica, internacaoVO);
			Assert.assertEquals(internacaoVO.getStatusAltaObito(),
					StatusAltaObito.APRESENTA_DOCUMENTO_PENDENTE);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}
	
}
