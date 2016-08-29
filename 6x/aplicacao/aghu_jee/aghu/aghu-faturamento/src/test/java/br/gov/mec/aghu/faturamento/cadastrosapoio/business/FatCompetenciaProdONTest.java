package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.dao.FatCompetenciaProdDAO;
import br.gov.mec.aghu.faturamento.dao.FatContasHospitalaresDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoContaProdDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoItemContaProdDAO;
import br.gov.mec.aghu.model.FatCompetenciaProd;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.model.FatEspelhoContaProd;
import br.gov.mec.aghu.model.FatEspelhoItemContaHosp;
import br.gov.mec.aghu.model.FatEspelhoItemContaHospId;
import br.gov.mec.aghu.model.FatEspelhoItemContaProd;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class FatCompetenciaProdONTest extends AGHUBaseUnitTest<FatCompetenciaProdON>{

	@Mock
	private FatCompetenciaProdDAO mockedFatCompetenciaProdDAO;
	@Mock
	private FatContasHospitalaresDAO mockedFatContasHospitalaresDAO;
	@Mock
	private FatEspelhoContaProdDAO mockedFatEspelhoContaProdDAO;
	@Mock
	private FatEspelhoAihDAO mockedFatEspelhoAihDAO;
	@Mock
	private IAghuFacade mockedAghuFacade;
	@Mock
	private FatEspelhoItemContaHospDAO mockedFatEspelhoItemContaHospDAO;
	@Mock
	private FatEspelhoItemContaProdDAO mockedFatEspelhoItemContaProdDAO;
    @Mock
    private IRegistroColaboradorFacade mockedRegistroColaboradorFacade;
    @Mock
    private IServidorLogadoFacade mockedServidorLogadoFacade;

	@Before
	public void doBeforeEachTestCase() throws Exception {
		whenObterServidorLogado();
	}

	@Test
	public void gravarProducaoContaExiste() {

		final FatContasHospitalares fatContasHospitalares = new FatContasHospitalares();

		final FatCompetenciaProd fatCompetenciaProd = new FatCompetenciaProd();
		fatCompetenciaProd.setSeq(Integer.valueOf(0).shortValue());

		fatContasHospitalares.setIndContaReapresentada(true);
		fatContasHospitalares.setSeq(Integer.valueOf(0));
		fatContasHospitalares.setValorSh(BigDecimal.ZERO);
		fatContasHospitalares.setValorUti(BigDecimal.ZERO);
		fatContasHospitalares.setValorUtie(BigDecimal.ZERO);
		fatContasHospitalares.setValorSp(BigDecimal.ZERO);
		fatContasHospitalares.setValorAcomp(BigDecimal.ZERO);
		fatContasHospitalares.setValorRn(BigDecimal.ZERO);
		fatContasHospitalares.setValorSadt(BigDecimal.ZERO);
		fatContasHospitalares.setValorHemat(BigDecimal.ZERO);
		fatContasHospitalares.setValorTransp(BigDecimal.ZERO);
		fatContasHospitalares.setValorOpm(BigDecimal.ZERO);
		fatContasHospitalares.setValorAnestesista(BigDecimal.ZERO);
		fatContasHospitalares.setValorProcedimento(BigDecimal.ZERO);
		fatContasHospitalares.setCompetenciaProd(fatCompetenciaProd);

		final List<FatContasHospitalares> listaContasHospitalares = new ArrayList<FatContasHospitalares>();

		listaContasHospitalares.add(fatContasHospitalares);

		final FatEspelhoContaProd espelhoContaProd = new FatEspelhoContaProd();
		espelhoContaProd.setSeq(Integer.valueOf(0));
		espelhoContaProd.setValorTotalProd(BigDecimal.TEN);

		final FatEspelhoAih fatEspelhoAih = new FatEspelhoAih();
		fatEspelhoAih.setContaHospitalar(fatContasHospitalares);

		final FatEspelhoItemContaHospId espelhoItemContaHospId = new FatEspelhoItemContaHospId();
		espelhoItemContaHospId.setIchCthSeq(Integer.valueOf(0));
		espelhoItemContaHospId.setSeqp(Short.valueOf("0"));

		final FatEspelhoItemContaHosp espelhoItemContaHosp = new FatEspelhoItemContaHosp();
		espelhoItemContaHosp.setId(espelhoItemContaHospId);

		final List<FatEspelhoItemContaHosp> espelhoItemContaLista = new ArrayList<FatEspelhoItemContaHosp>();
		espelhoItemContaLista.add(espelhoItemContaHosp);

		final FatEspelhoItemContaProd fatEspelhoItemContaProd = new FatEspelhoItemContaProd();
		fatEspelhoItemContaProd.setCbo("");
		fatEspelhoItemContaProd.setCgc(Long.valueOf(0));
		fatEspelhoItemContaProd.setCnpjRegAnvisa(Long.valueOf(0));
		fatEspelhoItemContaProd.setCompetenciaUti("");
		fatEspelhoItemContaProd.setCpfCns(Long.valueOf(0));
		fatEspelhoItemContaProd.setDataPrevia(new Date());
		fatEspelhoItemContaProd.setIchCthSeq(Integer.valueOf(0));
		fatEspelhoItemContaProd.setIchSeq(Short.valueOf("0"));

		Mockito.when(mockedFatContasHospitalaresDAO.listarContasHospitalares()).thenReturn(listaContasHospitalares);

		Mockito.when(mockedFatEspelhoContaProdDAO.obterPorContaHospitalar(fatContasHospitalares)).thenReturn(espelhoContaProd);

		Mockito.when(mockedFatEspelhoAihDAO.obterPorCthSeqSeqp(Integer.valueOf(0), Integer.valueOf(1))).thenReturn(fatEspelhoAih);

		Mockito.when(mockedFatEspelhoItemContaHospDAO.listarEspelhosItensContaHospitalar(Integer.valueOf(0))).thenReturn(espelhoItemContaLista);

		try {

			this.systemUnderTest.gravarProducao(new Date());

		} catch (BaseException e) {
			// Engolida a exception
		}
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