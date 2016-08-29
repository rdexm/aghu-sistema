package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.FcpCalendarioVencimentoTributosDAO;
import br.gov.mec.aghu.compras.dao.FcpCalendarioVencimentoTributosJnDAO;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.model.FcpCalendarioVencimentoTributos;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Testes de {@link FcpCalendarioVencimentoTributosRN}
 * 
 * @author Willian Buarque
 *
 */
public class FcpCalendarioVencimentoTributosRNTest extends AGHUBaseUnitTest<FcpCalendarioVencimentoTributosRN> {

	@Mock
	private FcpCalendarioVencimentoTributosDAO mockedCalendarioVencimentoTributosDAO;

	@Mock
	private IServidorLogadoFacade mockedIServidorLogadoFacade;
	
	@Mock
	private FcpCalendarioVencimentoTributosJnDAO mockedCalendarioVencimentoTributosJnDAO;


	@Test
	public void pesquisarFcpCalendarioVencimentoTributoTest01() throws BaseException {
		Date dataApuracao = DateUtil.obterData(2014, 1, 1);
		DominioTipoTributo tipoTributo = DominioTipoTributo.F;

		testarPesquisarFcpCalendarioVencimentoTributo(dataApuracao, tipoTributo);
	}

	private void testarPesquisarFcpCalendarioVencimentoTributo(final Date dataApuracao,
			final DominioTipoTributo tipoTributo) throws BaseException {
		
		List<FcpCalendarioVencimentoTributos> calendarioVencimentoTributos = new ArrayList<FcpCalendarioVencimentoTributos>();
		Mockito.when(mockedCalendarioVencimentoTributosDAO.pesquisarCalendarioVencimentoPorApuracao(Mockito.any(Date.class), Mockito.any(Date.class), Mockito.any(DominioTipoTributo.class)))
				.thenReturn(calendarioVencimentoTributos);
		
		this.systemUnderTest.pesquisarFcpCalendarioVencimentoTributo(dataApuracao, tipoTributo);
	}
}