package br.gov.mec.aghu.prescricaomedica.business;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObtCausaAntecedente;
import br.gov.mec.aghu.model.MpmObtCausaAntecedenteId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaAntecedenteDAO;
import br.gov.mec.aghu.prescricaomedica.vo.SumarioAltaDiagnosticosCidVO;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class ManterObtCausaAntecedenteONTest extends AGHUBaseUnitTest<ManterObtCausaAntecedenteON>{
	
	@Mock
	private MpmObtCausaAntecedenteDAO mockedMpmObtCausaAntecedenteDAO;
	@Mock
	private ManterObtCausaAntecedenteRN mockedManterObtCausaAntecedenteRN;
	@Mock
	private MpmAltaSumarioDAO mockedMpmAltaSumarioDAO;


	@Test
	public void versionarObtCausaAntecedenteTest() {
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		MpmAltaSumarioId id = new MpmAltaSumarioId();
		id.setApaAtdSeq(1);
		id.setApaSeq(2);
		id.setSeqp((short)3);
		altaSumario.setId(id);
		Short antigoAsuSeqp = Short.valueOf("0");
		try {
			systemUnderTest.versionarObtCausaAntecedente(altaSumario,antigoAsuSeqp);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void persistirCausaAntecedenteTest(){
		SumarioAltaDiagnosticosCidVO sumarioAltaDiagnosticosCidVO = new SumarioAltaDiagnosticosCidVO();
		sumarioAltaDiagnosticosCidVO.setId(new MpmAltaSumarioId());
		sumarioAltaDiagnosticosCidVO.setCid(new AghCid());
		try {
			systemUnderTest.persistirCausaAntecedente(sumarioAltaDiagnosticosCidVO);
			sumarioAltaDiagnosticosCidVO.setObitoCausaAntecendente(new MpmObtCausaAntecedente());
			sumarioAltaDiagnosticosCidVO.getObitoCausaAntecendente().setId(new MpmObtCausaAntecedenteId());
			sumarioAltaDiagnosticosCidVO.getObitoCausaAntecendente().getId().setAsuApaAtdSeq(1);
			sumarioAltaDiagnosticosCidVO.getObitoCausaAntecendente().getId().setAsuApaSeq(1);
			sumarioAltaDiagnosticosCidVO.getObitoCausaAntecendente().getId().setAsuSeqp(Short.valueOf("1"));
			sumarioAltaDiagnosticosCidVO.getObitoCausaAntecendente().getId().setSeqp(Short.valueOf("1"));
			sumarioAltaDiagnosticosCidVO.getObitoCausaAntecendente().setIndCarga(DominioSimNao.N);
			systemUnderTest.persistirCausaAntecedente(sumarioAltaDiagnosticosCidVO);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

	@Test
	public void removerObtCausaAntecedenteTest(){
		MpmAltaSumario altaSumario = new MpmAltaSumario();
		MpmAltaSumarioId id = new MpmAltaSumarioId();
		altaSumario.setId(id);

		try{
			systemUnderTest.removerObtCausaAntecedente(altaSumario);
		} catch (BaseException e) {
			Assert.fail(e.getMessage());
		}
	}

}
