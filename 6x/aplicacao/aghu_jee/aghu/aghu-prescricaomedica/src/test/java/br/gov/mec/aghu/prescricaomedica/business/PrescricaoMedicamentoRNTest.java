package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMdtoId;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmItemPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMdtoDAO;
import br.gov.mec.aghu.prescricaomedica.vo.VerificaAtendimentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class PrescricaoMedicamentoRNTest extends AGHUBaseUnitTest<PrescricaoMedicamentoRN>{
	
	private static final String NOME_MICROCOMPUTADOR = "AGHU_2K46";

	@Mock
	private MpmPrescricaoMdtoDAO mockedMpmPrescricaoMdtosDAO;
	@Mock
	private MpmItemPrescricaoMdtoDAO mockedMpmItemPrescricaoMdtosDAO;
	@Mock
	private PrescricaoMedicaRN mockedPrescricaoMedicaRN;
	@Mock
	private IAghuFacade mockedAghuFacade;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;
	
	/**
	 * Testa método que define número de meses de gestação. Deve retornar um
	 * NullPointerException
	 * 
	 * @throws AGHUNegocioException
	 */
	@Test(expected = NullPointerException.class)
	public void preInserirNullTest() throws BaseException {

		// Valor atual
		systemUnderTest.preInserir(null, NOME_MICROCOMPUTADOR, new Date(), false);
	}

	@Test
	public void preInserirTest() throws BaseException {
		MpmPrescricaoMdto prescricaoMdtos = new MpmPrescricaoMdto(
				new MpmPrescricaoMdtoId(Integer.getInteger("1"), Long
						.getLong("1")), new MpmTipoFrequenciaAprazamento(),
				new RapServidores(), new AfaTipoVelocAdministracoes(),
				new Date(), new Date(), true, true, DominioIndPendenteItemPrescricao.P,
				null, null);

		AghAtendimentos atendimento = new AghAtendimentos();
		atendimento.setSeq(1);
		MpmPrescricaoMedica prescricaoMedica = new MpmPrescricaoMedica();
		MpmPrescricaoMedicaId idPrescricaoMedica = new MpmPrescricaoMedicaId(Integer
				.valueOf("1"),Integer
				.valueOf("1"));
		
		prescricaoMedica.setId(idPrescricaoMedica);
		prescricaoMedica.setAtendimento(atendimento);
		prescricaoMdtos.setPrescricaoMedica(prescricaoMedica);

		MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento = new MpmTipoFrequenciaAprazamento();
		tipoFrequenciaAprazamento.setSeq(Short.valueOf("1"));
		prescricaoMdtos.setTipoFreqAprazamento(tipoFrequenciaAprazamento);
		prescricaoMdtos.setFrequencia(Short.valueOf("1"));

		RapServidores servidor = new RapServidores(new RapServidoresId(1, (short) 2));
		prescricaoMdtos.setServidor(servidor);

		Mockito.when(mockedPrescricaoMedicaRN.verificaAtendimento(Mockito.any(Date.class), Mockito.any(Date.class), Mockito.anyInt(), Mockito.anyInt(),
				Mockito.anyInt(), Mockito.anyInt())).thenReturn(new VerificaAtendimentoVO());

		whenObterServidorLogado();

		systemUnderTest.preInserir(prescricaoMdtos, NOME_MICROCOMPUTADOR, new Date(), false);

		assert !prescricaoMdtos.getIndItemRecTransferencia();

		assert !prescricaoMdtos.getIndBombaInfusao();
	}

    private void whenObterServidorLogado() throws BaseException {
		RapServidores rap =  new RapServidores(new RapServidoresId(1, (short) 1)) ;
		RapPessoasFisicas pf = new RapPessoasFisicas();
		pf.setNome("PESSOA FÍSICA");
		rap.setPessoaFisica(pf);
		Mockito.when(mockedRegistroColaboradorFacade.obterServidorAtivoPorUsuario(Mockito.anyString())).thenReturn(rap);
		
		Mockito.when(mockedServidorLogadoFacade.obterServidorLogado()).thenReturn(rap);
    }

}
