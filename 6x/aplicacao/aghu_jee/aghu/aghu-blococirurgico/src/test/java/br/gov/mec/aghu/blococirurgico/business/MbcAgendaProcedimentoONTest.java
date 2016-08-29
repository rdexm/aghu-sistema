package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import br.gov.mec.aghu.blococirurgico.business.MbcAgendaProcedimentoON.MbcAgendaProcedimentoONExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgsId;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.VMbcProcEsp;

@Ignore
public class MbcAgendaProcedimentoONTest extends AGHUBaseUnitTest<MbcAgendaProcedimentoON> {


//	@Before
//	public void doBeforeEachTestCase() {
//	
//		systemUnderTest = new MbcAgendaProcedimentoON(){
//			private static final long serialVersionUID = 4088209823079721414L;
//			
//			@Override
//			public void addMessageAlteracaoSala(Date tempoSala, Date dataFormatadaBanco, VMbcProcEsp procEsp) {};
//			
//			@Override
//			public void addMessagepopulaRegimeSus(DominioRegimeProcedimentoCirurgicoSus dominioRegimeProc, VMbcProcEsp procEsp){};
//		};
//	}
	
	/**
	 * Teste valida lista vazia e procedimento selecionado
	 */
	@Test
	public void validarAgendaProcedimentoAdicionadoExistenteListaVazia(){
		MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();
		MbcEspecialidadeProcCirgs especialidadeProcCirgs = new MbcEspecialidadeProcCirgs();
		MbcEspecialidadeProcCirgsId id = new MbcEspecialidadeProcCirgsId(Integer.valueOf(1), Short.valueOf("2"));
		especialidadeProcCirgs.setId(id);
		agendaProcedimento.setMbcEspecialidadeProcCirgs(especialidadeProcCirgs);
		List<MbcAgendaProcedimento> agendaProcedimentoList = new ArrayList<MbcAgendaProcedimento>();
		try {
			systemUnderTest.validarAgendaProcedimentoAdicionadoExistente(agendaProcedimentoList, agendaProcedimento);
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaProcedimentoONExceptionCode.MBC_00974, e.getCode());
		}
	}	
	
	/**
	 * Teste valida lista com procedimento diferente procedimento selecionado
	 */
	@Test
	public void validarAgendaProcedimentoAdicionadoExistenteListaDiferenteSelecionado(){
		MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();
		MbcProcedimentoCirurgicos procedimentoCirurgico = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico.setSeq(1);
		agendaProcedimento.setProcedimentoCirurgico(procedimentoCirurgico);
		
		
		MbcAgendaProcedimento agendaProcedimento2 = new MbcAgendaProcedimento();
		MbcProcedimentoCirurgicos procedimentoCirurgico2 = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico2.setSeq(2);
		agendaProcedimento2.setProcedimentoCirurgico(procedimentoCirurgico2);
		
		List<MbcAgendaProcedimento> agendaProcedimentoList = new ArrayList<MbcAgendaProcedimento>();
		agendaProcedimentoList.add(agendaProcedimento2);
		try {
			systemUnderTest.validarAgendaProcedimentoAdicionadoExistente(agendaProcedimentoList, agendaProcedimento);
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaProcedimentoONExceptionCode.MBC_00804, e.getCode());
		}
	}	
	
	/**
	 * Teste valida lista com procedimento igual procedimento selecionado
	 */
	@Test
	public void validarAgendaProcedimentoAdicionadoExistenteListaIgualSelecionado(){
		MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();
		MbcProcedimentoCirurgicos procedimentoCirurgico = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico.setSeq(1);
		agendaProcedimento.setProcedimentoCirurgico(procedimentoCirurgico);
		
		
		MbcAgendaProcedimento agendaProcedimento2 = new MbcAgendaProcedimento();
		MbcProcedimentoCirurgicos procedimentoCirurgico2 = new MbcProcedimentoCirurgicos();
		procedimentoCirurgico2.setSeq(1);
		agendaProcedimento2.setProcedimentoCirurgico(procedimentoCirurgico2);
		
		List<MbcAgendaProcedimento> agendaProcedimentoList = new ArrayList<MbcAgendaProcedimento>();
		agendaProcedimentoList.add(agendaProcedimento2);
		
		try {
			systemUnderTest.validarAgendaProcedimentoAdicionadoExistente(agendaProcedimentoList, agendaProcedimento);
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaProcedimentoONExceptionCode.MBC_00804, e.getCode());
		}
	}	
	
	
	/**
	 * Teste tempo sala maior que o tempo do procedimento em minutos
	 */
	@Test
	public void validaTempoMinimoSalaMinutos(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		procEsp.setTempoMinimo(Short.valueOf("35"));
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(0, 0, 0, 0, procEsp.getTempoMinimo(), 0);
		Date tempoSala = DateUtil.obterData(2013, 04, 02, 0, 36);
		
		Calendar cal = Calendar.getInstance();
//		try {
//			cal.setTime(systemUnderTest.validaTempoMinimo(tempoSala, procEsp));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Calendar expected = Calendar.getInstance();
		expected.setTime(tempoSala);
		
		Assert.assertEquals(cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE),expected.get(Calendar.HOUR_OF_DAY) + expected.get(Calendar.MINUTE));
	}
	
	/**
	 * Teste tempo do procedimento maior que tempo sala em minutos
	 * 
	 * Passa 
	 * Tempo da sala: 0:35
	 * Tempo do procedimento: 0:37
	 * 
	 * Deverá retornar Tempo do procedimento!
	 * 
	 */
	@Test
	public void validaTempoMinimoProcEspMinutos(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		procEsp.setTempoMinimo(Short.valueOf("37"));
		//Transforma o tempo do procedimento em gregorian calendar para comparar.
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(0, 0, 0, 0, procEsp.getTempoMinimo(), 0);
		//Cria o tempo da sala
		Date tempoSala = DateUtil.obterData(2013, 04, 02, 0, 35);
		//Busca qual o tempo foi utilizado e seta no Calendar para buscar o MINUTO
		Calendar cal = Calendar.getInstance();
//		try {
//			cal.setTime(systemUnderTest.validaTempoMinimo(tempoSala, procEsp));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Assert.assertEquals(cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE),gc.get(Calendar.HOUR_OF_DAY) + gc.get(Calendar.MINUTE));
		
	}
	
	/**
	 * Teste tempo do procedimento igual tempo da sala em minutos
	 */
	@Test
	public void validaTempoIgualMinutos(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		procEsp.setTempoMinimo(Short.valueOf("34"));
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(0, 0, 0, 0, procEsp.getTempoMinimo(), 0);
		Date tempoSala = DateUtil.obterData(2013, 04, 02, 0, 34);
		
		Calendar cal = Calendar.getInstance();
//		try {
//			cal.setTime(systemUnderTest.validaTempoMinimo(tempoSala, procEsp));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Assert.assertEquals(cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE),gc.get(Calendar.HOUR_OF_DAY) + gc.get(Calendar.MINUTE));
		
	}
	

	/**
	 * Teste tempo sala maior que o tempo do procedimento em horas
	 */
	@Test
	public void validaTempoMinimoSalaHoras(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		procEsp.setTempoMinimo(Short.valueOf("95"));
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(0, 0, 0, 0, procEsp.getTempoMinimo(), 0);
		Date tempoSala = DateUtil.obterData(2013, 04, 02, 1, 36);

		Calendar cal = Calendar.getInstance();
//		try {
//			cal.setTime(systemUnderTest.validaTempoMinimo(tempoSala, procEsp));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Calendar expected = Calendar.getInstance();
		expected.setTime(tempoSala);
		
		Assert.assertEquals(cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE),expected.get(Calendar.HOUR_OF_DAY) + expected.get(Calendar.MINUTE));
		
	}
	
	/**
	 * Teste tempo do procedimento maior que tempo sala em horas
	 */
	@Test
	public void validaTempoMinimoProcEspHoras(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		procEsp.setTempoMinimo(Short.valueOf("150"));
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(0, 0, 0, 0, procEsp.getTempoMinimo(), 0);
		Date tempoSala = DateUtil.obterData(2013, 04, 02, 2, 0);

		Calendar cal = Calendar.getInstance();
//		try {
//			cal.setTime(systemUnderTest.validaTempoMinimo(tempoSala, procEsp));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		//Assert.assertEquals(cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE),gc.get(Calendar.HOUR_OF_DAY) + gc.get(Calendar.MINUTE));
		
	}
	
	/**
	 * Teste tempo do procedimento igual tempo da sala em horas
	 */
	@Test
	public void validaTempoIgualHoras(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		procEsp.setTempoMinimo(Short.valueOf("150"));
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(0, 0, 0, 0, procEsp.getTempoMinimo(), 0);
		Date tempoSala = DateUtil.obterData(2013, 04, 02, 2, 30);
		
		Calendar cal = Calendar.getInstance();
//		try {
//			cal.setTime(systemUnderTest.validaTempoMinimo(tempoSala, procEsp));
//		} catch (ParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		Assert.assertEquals(cal.get(Calendar.HOUR_OF_DAY) + cal.get(Calendar.MINUTE),gc.get(Calendar.HOUR_OF_DAY) + gc.get(Calendar.MINUTE));
		
	}
	
	
	/**
	 * Teste existe regimeSus na tela e no procedimento selecionado
	 */
	@Test
	public void populaRegimeSusnaTelaESelecionado(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		DominioRegimeProcedimentoCirurgicoSus procSel = DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO;
		procEsp.setRegimeProcedSus(procSel);
		DominioRegimeProcedimentoCirurgicoSus dominioRegimeProc = DominioRegimeProcedimentoCirurgicoSus.HOSPITAL_DIA;
		Assert.assertEquals(systemUnderTest.populaRegimeSus(dominioRegimeProc, procEsp),procSel);
	}	
		
	/**
	 * Teste existe regimeSus na tela e não exite regimeSus no procedimento selecionado
	 */
	@Test
	public void populaRegimeSusnaTela(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		DominioRegimeProcedimentoCirurgicoSus dominioRegimeProc = DominioRegimeProcedimentoCirurgicoSus.HOSPITAL_DIA;
		Assert.assertEquals(systemUnderTest.populaRegimeSus(dominioRegimeProc, procEsp),dominioRegimeProc);
	}	
	
	/**
	 * Teste nao existe regimeSus na tela e  exite regimeSus no procedimento selecionado
	 */
	@Test
	public void populaRegimeSusProcSelecionado(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		DominioRegimeProcedimentoCirurgicoSus procSel = DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO;
		procEsp.setRegimeProcedSus(procSel);
		DominioRegimeProcedimentoCirurgicoSus dominioRegimeProc = null;
		Assert.assertEquals(systemUnderTest.populaRegimeSus(dominioRegimeProc, procEsp),procSel);
	}	
	
	
	
	/**
	 * Teste nao existe regimeSus na tela e não exite regimeSus no procedimento selecionado
	 */
	@Test
	public void populaRegimeSusNemTelaNemSelecionado(){
		VMbcProcEsp procEsp = new VMbcProcEsp();
		DominioRegimeProcedimentoCirurgicoSus dominioRegimeProc = null;
		Assert.assertNull(systemUnderTest.populaRegimeSus(dominioRegimeProc, procEsp));
	}	
	
	/**
	 * Teste qtdade zero
	 */
	@Test
	public void validaQtdZero(){
		MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();
		MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs = new MbcEspecialidadeProcCirgs();
		MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos = new MbcProcedimentoCirurgicos();
		agendaProcedimento.setMbcEspecialidadeProcCirgs(mbcEspecialidadeProcCirgs);
		agendaProcedimento.getMbcEspecialidadeProcCirgs().setMbcProcedimentoCirurgicos(mbcProcedimentoCirurgicos);
		agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().setDescricao("Descricao");
		agendaProcedimento.setQtde(Short.valueOf("0"));
		try {
			systemUnderTest.validarQtdeAgendaProcedimento(agendaProcedimento);
			Assert.fail("Exceção esperada não gerada: MBC_00974");
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaProcedimentoONExceptionCode.MBC_00974, e.getCode());
		}
	}	
	
	/**
	 * Teste qtdade vazia
	 */
	@Test
	public void validaQtdVazia(){
		MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();
		MbcEspecialidadeProcCirgs mbcEspecialidadeProcCirgs = new MbcEspecialidadeProcCirgs();
		MbcProcedimentoCirurgicos mbcProcedimentoCirurgicos = new MbcProcedimentoCirurgicos();
		agendaProcedimento.setMbcEspecialidadeProcCirgs(mbcEspecialidadeProcCirgs);
		agendaProcedimento.getMbcEspecialidadeProcCirgs().setMbcProcedimentoCirurgicos(mbcProcedimentoCirurgicos);
		agendaProcedimento.getMbcEspecialidadeProcCirgs().getMbcProcedimentoCirurgicos().setDescricao("Descricao");
		try {
			systemUnderTest.validarQtdeAgendaProcedimento(agendaProcedimento);
			Assert.fail("Exceção esperada não gerada: MBC_00974");
		} catch (BaseException e) {
			Assert.assertEquals(MbcAgendaProcedimentoONExceptionCode.MBC_00974, e.getCode());
		}
	}	

	
	/**
	 * Teste qtdade legal
	 */
	@Test
	public void validaQtdLegal(){
		MbcAgendaProcedimento agendaProcedimento = new MbcAgendaProcedimento();
		agendaProcedimento.setQtde(Short.valueOf("3"));
		
		try {
			systemUnderTest.validarQtdeAgendaProcedimento(agendaProcedimento);
		} catch (BaseException e) {
			Assert.fail("Exceção esperada não gerada: MBC_00974");
		}
	}	
}
