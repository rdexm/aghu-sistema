package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

public class MovTriagemDispMdtosRNTest extends AGHUBaseUnitTest<MovimentacaoTriagemDispensacaoMdtosRN>{

	// Daos e Facades a serem mockadas
	@Mock
	private IPrescricaoMedicaFacade mockedPrescricaoMedicaFacade;

	// TESTES METODO verificaCamposObrigatorios
	@Test
	public void verificaCamposObrigatoriosTodosPreenchidos() {

		List<AfaDispensacaoMdtos> listaTeste = criaListaAfaDispensacao(true);
		try {
			systemUnderTest.verificaCamposObrigatorios(listaTeste);
		} catch (ApplicationBusinessException e) {
			Assert
					.fail("Não deveria lançar exceção pois todos os campos estavam preenchidos");
		}
	}

	@Test
	public void verificaCamposObrigatoriosVazios() {

		List<AfaDispensacaoMdtos> listaTesteVazio = criaListaAfaDispensacao(false);
		for (AfaDispensacaoMdtos dispMdto : listaTesteVazio) {
			dispMdto.setMedicamento(criarMedicamento(true));
		}
		try {
			systemUnderTest.verificaCamposObrigatorios(listaTesteVazio);
			Assert
					.fail("Deveria lançar exceção pois todos os campos estavam vazios");
		} catch (ApplicationBusinessException e) {
			// Campos vazios
			Assert.assertTrue("Lançada exceção ApplicationBusinessException", true);
		}
	}

	// TESTES METODO removeDispensacaoesNaoPreenchidas

	@Test
	public void removeDispensacaoesNaoPreenchidasCom() {

		List<AfaDispensacaoMdtos> listaTeste = criaListaAfaDispensacao(false);
		listaTeste = systemUnderTest
				.removeDispensacaoesNaoPreenchidas(listaTeste);
		Assert.assertEquals(0, listaTeste.size());
	}

	@Test
	public void removeDispensacaoesNaoPreenchidasSem() {

		List<AfaDispensacaoMdtos> listaTeste = criaListaAfaDispensacao(true);
		listaTeste = systemUnderTest
				.removeDispensacaoesNaoPreenchidas(listaTeste);
		Assert.assertNotSame(0, listaTeste.size());
	}

	// TESTES METODO buscaDispensacaoOriginal
	@Test
	public void buscaDispensacaoOriginalCom() {
		AfaDispensacaoMdtos dispMdto = criaAfaDispensacaoMdto(true);
		List<AfaDispensacaoMdtos> dispMdtoList = criaListaAfaDispensacao(true);
		AfaDispensacaoMdtos dispMdtoResult = systemUnderTest
				.buscaDispensacaoOriginal(dispMdto, dispMdtoList);
		Assert.assertEquals(dispMdto, dispMdtoResult);
	}

	//@Test
	public void buscaDispensacaoOriginalSem() {
		AfaDispensacaoMdtos dispMdto = criaAfaDispensacaoMdto(true);
		List<AfaDispensacaoMdtos> dispMdtoList = criaListaAfaDispensacao(false);
		AfaDispensacaoMdtos dispMdtoResult = systemUnderTest
				.buscaDispensacaoOriginal(dispMdto, dispMdtoList);
		Assert.assertNull(dispMdtoResult);
	}

	// Util
	private List<AfaDispensacaoMdtos> criaListaAfaDispensacao(Boolean listaOK) {

		List<AfaDispensacaoMdtos> dispMdtos = new ArrayList<AfaDispensacaoMdtos>();
		dispMdtos.add(criaAfaDispensacaoMdto(listaOK));
		dispMdtos.add(criaAfaDispensacaoMdto(listaOK));
		dispMdtos.add(criaAfaDispensacaoMdto(listaOK));

		return dispMdtos;
	}

	private AfaDispensacaoMdtos criaAfaDispensacaoMdto(Boolean instanciaOk) {

		AfaDispensacaoMdtos dispMdto = new AfaDispensacaoMdtos();

		if (instanciaOk) {
			dispMdto.setSeq(Long.valueOf(1));
			dispMdto.setQtdeDispensada(new BigDecimal(2));
			dispMdto.setQtdeSolicitada(new BigDecimal(2));
			dispMdto.setIndSituacao(DominioSituacaoDispensacaoMdto.S);
			// UNF
			AghUnidadesFuncionais unfFarmacia = new AghUnidadesFuncionais();
			unfFarmacia.setSeq(Short.valueOf("175"));
			dispMdto.setUnidadeFuncional(unfFarmacia);
			// MEDICAMENTO
			AfaMedicamento medicamento = criarMedicamento(instanciaOk);
			dispMdto.setMedicamento(medicamento);
		}

		return dispMdto;
	}

	private AfaMedicamento criarMedicamento(Boolean mdtoOK) {
		// MEDICAMENTO
		AfaMedicamento medicamento = null;

		if (mdtoOK) {
			medicamento = new AfaMedicamento();
			medicamento.setMatCodigo(1);
		}
		return medicamento;
	}

}
