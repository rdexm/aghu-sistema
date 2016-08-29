package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesaId;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.vo.FsoGrupoNaturezaDespesaCriteriaVO;
import br.gov.mec.aghu.orcamento.dao.FsoGrupoNaturezaDespesaDAO;
import br.gov.mec.aghu.orcamento.dao.FsoNaturezaDespesaDAO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário da classe FsoGrupoNaturezaDespesaON.
 * 
 * @author mlcruz
 */
public class FsoGrupoNaturezaDespesaONTest extends AGHUBaseUnitTest<FsoGrupoNaturezaDespesaON> {
	@Mock
	private FsoGrupoNaturezaDespesaDAO dao;
	@Mock
	private FsoNaturezaDespesaDAO ntdDao;
	@Mock
	private FsoNaturezaDespesaON naturezaDespesaOn;

	/**
	 * Testa inclusão de grupo.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testInclusaoGrupo() throws ApplicationBusinessException {
		final FsoGrupoNaturezaDespesa model = new FsoGrupoNaturezaDespesa();
		model.setCodigo(123456);
		model.setDescricao("Grupo A");
		model.setIndSituacao(DominioSituacao.A);

		Mockito.when(dao.contarGruposNaturezaDespesa(Mockito.any(FsoGrupoNaturezaDespesaCriteriaVO.class))).thenReturn(0l);
		Mockito.when(dao.existeDescricaoDuplicada(model)).thenReturn(false);

		systemUnderTest.incluir(model);
	}

	/**
	 * Testa inclusão de grupo com chave duplicada.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testInclusaoGrupoChaveDuplicada() throws ApplicationBusinessException {
		final FsoGrupoNaturezaDespesa model = new FsoGrupoNaturezaDespesa();
		model.setCodigo(2);
		model.setDescricao("Grupo B");
		model.setIndSituacao(DominioSituacao.A);

		Mockito.when(dao.contarGruposNaturezaDespesa(Mockito.any(FsoGrupoNaturezaDespesaCriteriaVO.class))).thenReturn(1l);

		systemUnderTest.incluir(model);
	}

	/**
	 * Testa alteracao de grupo.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testAlteracaoGrupo() throws ApplicationBusinessException {
		final FsoGrupoNaturezaDespesa model = new FsoGrupoNaturezaDespesa();
		model.setCodigo(123456);
		model.setDescricao("Grupo C");
		model.setIndSituacao(DominioSituacao.A);

		Mockito.when(dao.existeDescricaoDuplicada(model)).thenReturn(false);
		systemUnderTest.alterar(model);
	}

	/**
	 * Testa alteração de grupo com descrição duplicada.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testAlteracaoGrupoDescricaoDuplicada() throws ApplicationBusinessException {
		final FsoGrupoNaturezaDespesa model = new FsoGrupoNaturezaDespesa();
		model.setCodigo(123456);
		model.setDescricao("Grupo D");
		model.setIndSituacao(DominioSituacao.I);

		Mockito.when(dao.existeDescricaoDuplicada(model)).thenReturn(true);

		systemUnderTest.alterar(model);
	}

	/**
	 * Testa exclusão de grupo.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void testExclusaoGrupo() throws ApplicationBusinessException {
		final List<FsoNaturezaDespesa> listaNtd = new ArrayList<FsoNaturezaDespesa>();
		final FsoGrupoNaturezaDespesa model = new FsoGrupoNaturezaDespesa();

		model.setCodigo(5);
		model.setDescricao("Grupo E");
		model.setIndSituacao(DominioSituacao.I);

		Mockito.when(ntdDao.listarNaturezaDespesaPorGrupoNatureza(model)).thenReturn(listaNtd);
		Mockito.when(dao.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(model);

		systemUnderTest.excluir(model.getCodigo());
	}

	/**
	 * Testa exclusão de grupo com natureza vinculada.
	 * 
	 * @throws ApplicationBusinessException
	 */
	@Test(expected = ApplicationBusinessException.class)
	public void testExclusaoGrupoNaturezaVinculada() throws ApplicationBusinessException {

		final List<FsoNaturezaDespesa> listaNtd = new ArrayList<FsoNaturezaDespesa>();
		final FsoGrupoNaturezaDespesa model = new FsoGrupoNaturezaDespesa();
		final FsoNaturezaDespesa ntd = new FsoNaturezaDespesa();
		ntd.setId(new FsoNaturezaDespesaId((Integer) 1, (byte) 12));
		ntd.setDescricao("Natureza 1");
		ntd.setIndSituacao(DominioSituacao.A);
		ntd.setGrupoNaturezaDespesa(model);
		ntd.setCodClassifNatureza((byte) 12);
		ntd.setContaPatrimonial(null);
		listaNtd.add(ntd);

		model.setCodigo(5);
		model.setDescricao("Grupo E");
		model.setIndSituacao(DominioSituacao.I);

		Mockito.when(dao.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(model);
		Mockito.when(ntdDao.listarNaturezaDespesaPorGrupoNatureza(model)).thenReturn(listaNtd);

		systemUnderTest.excluir(model.getCodigo());

	}

}
