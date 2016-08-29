package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPesquisaCirurgiasC2VO;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class PortalPesquisaCirurgiasONTest extends AGHUBaseUnitTest<PortalPesquisaCirurgiasON>{

	@Mock
	private MbcProfAtuaUnidCirgsDAO mockMbcProfAtuaUnidCirgsDAO;


	@Test
	public void testListarMbcProfAtuaUnidCirgsPorUnfSeq() {

		Assert.assertNotNull(systemUnderTest.listarMbcProfAtuaUnidCirgsPorUnfSeq(Short.valueOf("0"), "teste"));
	}
	
	private List<PortalPesquisaCirurgiasC2VO> getListaPortalPesquisaCirurgiasC2VO() {
		List<PortalPesquisaCirurgiasC2VO> list = new ArrayList<PortalPesquisaCirurgiasC2VO>();
		PortalPesquisaCirurgiasC2VO vo = new PortalPesquisaCirurgiasC2VO();
		list.add(vo);
		
		return list;
	};
}
