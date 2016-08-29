package br.gov.mec.aghu.faturamento.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.stringtemplate.impl.RegistroCsvContasPeriodo;
import br.gov.mec.aghu.faturamento.stringtemplate.interfaces.RegistroCsv;
import br.gov.mec.aghu.faturamento.vo.BuscaContaVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.FatExclusaoCritica;
import br.gov.mec.aghu.model.FatMotivoDesdobramento;
import br.gov.mec.aghu.model.FatProcedHospInternos;

public class GeracaoArquivoCsvRNTest {

	private final Log log = LogFactory.getLog(this.getClass());
	private final Random rand = new Random(System.currentTimeMillis());

	private RegistroCsv getRegCsv() {

		RegistroCsv result = null;
		Integer cthSeq = null;
		Integer pacProntuario = null;
		String pacNome = null;
		DominioSituacaoConta cthIndSit = null;
		String unfDesc = null;
		String intLeito = null;
		Long cthNroAih = null;
		Integer cmce = null;
		Boolean isDesdobramento = null;
		Date cthDataInt = null;
		Date cthDataAlta = null;
		Date cthDataSms = null;
		String cthIndSms = null;
		Boolean isForaEstado = null;
		Boolean isEspCir = null;
		Boolean isCobraAih = null;
		String fcfDesc = null;
		String cthSmsStatus = null;
		String cthCodExcCrit = null;
		String iphCodDescSol = null;
		String iphCodDescReal = null;
		String msgErro = null;
		BigDecimal valorTotal = null;

		cthSeq = Integer.valueOf(this.rand.nextInt());
		pacProntuario = Integer.valueOf(this.rand.nextInt());
		pacNome = "pacNome";
		cthIndSit = DominioSituacaoConta.A;
		unfDesc = "unfDesc";
		intLeito = "intLeito";
		cthNroAih = Long.valueOf(this.rand.nextLong());
		cmce = Integer.valueOf((byte) this.rand.nextInt());
		isDesdobramento = Boolean.FALSE;
		cthDataInt = new Date();
		cthDataAlta = new Date();
		cthDataSms = new Date();
		cthIndSms = "cthIndSms";
		isForaEstado = Boolean.FALSE;
		isEspCir = Boolean.FALSE;
		isCobraAih = Boolean.FALSE;
		fcfDesc = "fcfDesc";
		cthSmsStatus = "cthSmsStatus";
		iphCodDescSol = "iphCodDescSol";
		iphCodDescReal = "iphCodDescReal";
		valorTotal = BigDecimal.valueOf(this.rand.nextDouble());
		result = new RegistroCsvContasPeriodo(cthSeq, pacProntuario, pacNome, cthIndSit, unfDesc, intLeito, cthNroAih, cmce, isDesdobramento, cthDataInt,
				cthDataAlta, cthDataSms, cthIndSms, isForaEstado, isEspCir, isCobraAih, fcfDesc, cthSmsStatus, cthCodExcCrit, iphCodDescSol, iphCodDescReal,
				msgErro, valorTotal);

		return result;
	}

	@Test
	public void testObterRegistroTitulo() {

		RegistroCsv result = null;
		RegistroCsv reg = null;

		//setup
		reg = this.getRegCsv();
		//assign
		result = GeracaoArquivoCsvRN.obterRegistroTitulo(reg);
		Assert.assertNotNull(result);
		Assert.assertEquals(reg.obterTitulosComoLista(), result.obterRegistrosComoLista());
	}

	@Test
	public void testObterRegistro() {

		GeracaoArquivoCsvRN objRn = null;
		RegistroCsv result = null;
		List<Object> expect = null;
		List<Object> regList = null;
		BuscaContaVO vo = null;
		FatContasHospitalares cth = null;
		FatAih aih = null;
		FatMotivoDesdobramento dsd = null;
		Integer cmce = null;
		FatExclusaoCritica exc = null;
		String leito = null;
		Byte mdsSeq= null;
		AghUnidadesFuncionais unf = null;
		AipPacientes pac = null;
		BigDecimal valorTotal = null;
		Boolean isCobraAih = null;
		Boolean isEspCir = null;
		Boolean isForaEstado = null;
		Boolean isDesdobrada = null;
		String fcfDesc = null;
		String statusSms = null;
		String iphSsmSol = null;
		String iphSsmReal = null;
		String caracterInternacao = null;
		String msgErro = null;
		
		//setup
		expect = new LinkedList<Object>();
		valorTotal = BigDecimal.valueOf(this.rand.nextDouble());
		isCobraAih = Boolean.FALSE;
		isEspCir = Boolean.FALSE;
		isForaEstado = Boolean.FALSE;
		isDesdobrada = Boolean.FALSE;
		fcfDesc = "fcfDesc";
		statusSms = "statusSms";
		iphSsmSol = "iphSsmSol";
		iphSsmReal = "iphSsmReal";
		caracterInternacao = "caracterInternacao";
		msgErro = "msgErro";
		//int
		unf = new AghUnidadesFuncionais();
		unf.setSeq(Short.valueOf((short) this.rand.nextInt()));
		unf.setDescricao("unfDesc");
		pac = new AipPacientes();
		pac.setProntuario(Integer.valueOf(this.rand.nextInt()));
		pac.setNome("Nome");
		leito = "leito";
		//cth		
		aih = new FatAih();
		aih.setNroAih(Long.valueOf(this.rand.nextLong()));
		dsd = new FatMotivoDesdobramento();
		dsd.setSeq(Short.valueOf((short) this.rand.nextInt()));
		cmce = Integer.valueOf((byte) this.rand.nextInt());
		exc = new FatExclusaoCritica();
		exc.setCodigo("excCritica");
		cth = new FatContasHospitalares();
		cth.setAih(aih);
		cth.setMotivoDesdobramento(dsd);
		cth.setSeq(Integer.valueOf(this.rand.nextInt()));
		cth.setIndSituacao(DominioSituacaoConta.A);
		cth.setDataInternacaoAdministrativa(new Date());
		cth.setDtAltaAdministrativa(new Date());
		cth.setDtEnvioSms(new Date());
		cth.setIndAutorizadoSms("indAutorizadoSms");
		cth.setExclusaoCritica(exc);
		cth.setProcedimentoHospitalarInterno(new FatProcedHospInternos());
		cth.setProcedimentoHospitalarInternoRealizado(new FatProcedHospInternos());
		cth.setConvenioSaudePlano(new FatConvenioSaudePlano());
		cth.getConvenioSaudePlano().setId(new FatConvenioSaudePlanoId());
		//vo
		vo = new BuscaContaVO(
				cth.getSeq(),
				pac.getNome(), 
				pac.getProntuario(),
				cth.getIndSituacao(), 
				unf.getDescricao(), 
				leito,
				cmce,
				aih.getNroAih(), 
				mdsSeq,
				cth.getDataInternacaoAdministrativa(), 
				cth.getDtAltaAdministrativa(), 
				cth.getDtEnvioSms(), 
				cth.getIndAutorizadoSms(), 
				cth.getProcedimentoHospitalarInterno().getSeq(), 
				cth.getConvenioSaudePlano().getId().getCnvCodigo(), 
				cth.getConvenioSaudePlano().getId().getSeq(), 
				cth.getProcedimentoHospitalarInternoRealizado().getSeq(), 
				cth.getIndEnviadoSms(), 
				cth.getExclusaoCritica().getCodigo(), 
				cth.getValorSh(), 
				cth.getValorUti(), 
				cth.getValorUtie(), 
				cth.getValorSp(), 
				cth.getValorAcomp(), 
				cth.getValorRn(), 
				cth.getValorSadt(), 
				cth.getValorHemat(), 
				cth.getValorTransp(), 
				cth.getValorOpm(), 
				cth.getValorAnestesista(), 
				cth.getValorProcedimento());	
		vo.setIsDesdobrada(Boolean.FALSE);
		vo.setIsForaEstado(Boolean.FALSE);
		vo.setIsCobraAih(Boolean.FALSE);
		vo.setIsEspCir(Boolean.FALSE);
		vo.setValorTotal(valorTotal);
		vo.setIphSsmReal(iphSsmReal);
		vo.setIphSsmSol(iphSsmSol);
		vo.setFcfDesc(fcfDesc);
		vo.setStatusSms(statusSms);
		vo.setCmce(cmce);
		vo.setCaracterInternacao(caracterInternacao);
		vo.setMsgErro(msgErro);
		//expect
		expect.add(cth.getSeq());
		expect.add(pac.getProntuario());
		expect.add(pac.getNome());
		expect.add(cth.getIndSituacao());
		expect.add(unf.getDescricao());
		expect.add(leito);
		expect.add(cth.getAih().getNroAih());
		expect.add(cmce);
		expect.add(isDesdobrada.booleanValue()
				? RegistroCsvContasPeriodo.MAGIC_STRING_BOOLEAN_TRUE_EQ_S
				: RegistroCsvContasPeriodo.MAGIC_STRING_BOOLEAN_FALSE_EQ_N);
		expect.add(String.format(RegistroCsvContasPeriodo.STRING_FORMAT_DD_MM_YYYY, cth.getDataInternacaoAdministrativa()));
		expect.add(String.format(RegistroCsvContasPeriodo.STRING_FORMAT_DD_MM_YYYY, cth.getDtAltaAdministrativa()));
		expect.add(String.format(RegistroCsvContasPeriodo.STRING_FORMAT_DD_MM_YYYY_HH_MM, cth.getDtEnvioSms()));
		expect.add(caracterInternacao);
		expect.add(isForaEstado.booleanValue()
				? RegistroCsvContasPeriodo.MAGIC_STRING_BOOLEAN_TRUE_EQ_S
				: RegistroCsvContasPeriodo.MAGIC_STRING_BOOLEAN_FALSE_EQ_N);
		expect.add(isEspCir.booleanValue()
				? RegistroCsvContasPeriodo.MAGIC_STRING_BOOLEAN_TRUE_EQ_S
				: RegistroCsvContasPeriodo.MAGIC_STRING_BOOLEAN_FALSE_EQ_N);
		expect.add(isCobraAih.booleanValue()
				? RegistroCsvContasPeriodo.MAGIC_STRING_BOOLEAN_TRUE_EQ_S
				: RegistroCsvContasPeriodo.MAGIC_STRING_BOOLEAN_FALSE_EQ_N);
		expect.add(fcfDesc);
		expect.add(statusSms);
		expect.add(cth.getExclusaoCritica().getCodigo());
		expect.add(iphSsmSol);
		expect.add(iphSsmReal);
		expect.add(msgErro);
		expect.add(valorTotal.toString().replace(".", ","));
		//obj
		objRn = new GeracaoArquivoCsvRN();
		//assign
		try {
			result = objRn.obterRegistro(vo);
			Assert.assertNotNull(result);
			regList = result.obterRegistrosComoLista();
			Assert.assertNotNull(regList);
			Assert.assertFalse(regList.isEmpty());
			Assert.assertEquals(expect, regList);
		} catch (Exception e) {
			this.log.info(Arrays.toString(e.getStackTrace()));
			Assert.fail("Not expecting exception: " + e);
		}
	}
}
