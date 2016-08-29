package br.gov.mec.aghu.business.bancosangue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.bancosangue.dao.AbsItemSolicitacaoHemoterapicaJustificativaDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsJustificativaComponenteSanguineoDAO;
import br.gov.mec.aghu.model.AbsGrupoJustificativaComponenteSanguineo;
import br.gov.mec.aghu.model.AbsItemSolicitacaoHemoterapicaJustificativa;
import br.gov.mec.aghu.model.AbsJustificativaComponenteSanguineo;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

public class JustificativaItemSolicitacaoHemoterapicaRNTest extends AGHUBaseUnitTest<JustificativaItemSolicitacaoHemoterapicaRN>{

	/**
	 * ####### ATENCAO #######
	 * 
	 * Os metodos nao abaixo NAO foram testados pois nao possuiam logica de negocio
	 * no momento da implementacao dos testes.
	 * 
	 * public void atualizarJustificativaItemSolicitacaoHemoterapica(
	 *		AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa) throws ApplicationBusinessException {
	 *
	 * public void inserirJustificativaItemSolicitacaoHemoterapica(
	 *		AbsItemSolicitacaoHemoterapicaJustificativa itemSolicitacaoHemoterapicaJustificativa) throws ApplicationBusinessException {
	 * 
	 */

	
	private static final Log log = LogFactory.getLog(JustificativaItemSolicitacaoHemoterapicaRNTest.class);

	@Mock
	private AbsJustificativaComponenteSanguineoDAO mockedAbsJustificativaComponenteSanguineoDAO;
	@Mock
	private AbsItemSolicitacaoHemoterapicaJustificativaDAO mockedAbsItemSolicitacaoHemoterapicaJustificativaDAO;
	@Mock
	private JustificativaComponenteSanguineoRN mockedJustificativaComponenteSanguineoRN;


	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void preAtualizarJustificativaItemSolicitacaoHemoterapicaNulosTest() throws ApplicationBusinessException {
		
		systemUnderTest.preAtualizarJustificativaItemSolicitacaoHemoterapica(null, null);
	
	}

	/**
	 * Testa a execucao com passagem de parâmetros com atributos nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void preAtualizarJustificativaItemSolicitacaoHemoterapicaAtribNulosTest() throws ApplicationBusinessException {
		
		AbsItemSolicitacaoHemoterapicaJustificativa newItem = new AbsItemSolicitacaoHemoterapicaJustificativa();
		AbsItemSolicitacaoHemoterapicaJustificativa oldItem = new AbsItemSolicitacaoHemoterapicaJustificativa();

		
		systemUnderTest.preAtualizarJustificativaItemSolicitacaoHemoterapica(newItem, oldItem);
		
	}

	/**
	 * Testa a execucao de ItemSolicitacaoHemoterapicaJustificativa com DescricaoLivre diferentes.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void preAtualizarJustificativaItemSolicitacaoHemoterapicaDiferentesTest() throws ApplicationBusinessException {
		
		AbsItemSolicitacaoHemoterapicaJustificativa newItem = new AbsItemSolicitacaoHemoterapicaJustificativa();
		AbsItemSolicitacaoHemoterapicaJustificativa oldItem = new AbsItemSolicitacaoHemoterapicaJustificativa();
		
		newItem.setDescricaoLivre("teste");
		oldItem.setDescricaoLivre("descricaoLivre");
		
		systemUnderTest.preAtualizarJustificativaItemSolicitacaoHemoterapica(newItem, oldItem);
		
	}

	/**
	 * Testa a execucao de ItemSolicitacaoHemoterapicaJustificativa com DescricaoLivre iguais.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void preAtualizarJustificativaItemSolicitacaoHemoterapicaIguaisTest() throws ApplicationBusinessException {
		
		AbsItemSolicitacaoHemoterapicaJustificativa newItem = new AbsItemSolicitacaoHemoterapicaJustificativa();
		AbsItemSolicitacaoHemoterapicaJustificativa oldItem = new AbsItemSolicitacaoHemoterapicaJustificativa();
		
		newItem.setDescricaoLivre("teste");
		oldItem.setDescricaoLivre("teste");
		
		systemUnderTest.preAtualizarJustificativaItemSolicitacaoHemoterapica(newItem, oldItem);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void preInserirJustificativaItemSolicitacaoHemoterapicaNulosTest() throws ApplicationBusinessException {
		
		systemUnderTest.preInserirJustificativaItemSolicitacaoHemoterapica(null);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros com atributos nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void preInserirJustificativaItemSolicitacaoHemoterapicaAtribNulosTest() throws ApplicationBusinessException {
		
		AbsItemSolicitacaoHemoterapicaJustificativa newItem = new AbsItemSolicitacaoHemoterapicaJustificativa();
		
		systemUnderTest.preInserirJustificativaItemSolicitacaoHemoterapica(newItem);
		
	}
	
	/**
	 * Testa a execucao de ItemSolicitacaoHemoterapicaJustificativa com DescricaoLivre.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void preInserirJustificativaItemSolicitacaoHemoterapicaComDescricaoTest() throws ApplicationBusinessException {
		
		AbsItemSolicitacaoHemoterapicaJustificativa newItem = new AbsItemSolicitacaoHemoterapicaJustificativa();
		
		newItem.setDescricaoLivre("teste");
		
		
		 
		
		systemUnderTest.preInserirJustificativaItemSolicitacaoHemoterapica(newItem);
		
	}
	
	/**
	 * Testa a execucao com passagem de parâmetros nulos.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarDescricaoLivreNuloTest() throws ApplicationBusinessException {
		
		
		 
		
		systemUnderTest.verificarDescricaoLivre(null);
		
	}
	
	
	/**
	 * Testa a execucao de passando parametro valido.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarDescricaoLivreTest() throws ApplicationBusinessException {
		
		AbsJustificativaComponenteSanguineo componente = new AbsJustificativaComponenteSanguineo();
		Mockito.when(mockedAbsJustificativaComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(componente);

		systemUnderTest.verificarDescricaoLivre(1);
	}

	/**
	 * Testa a execucao de passando parametro valido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void verificarDescricaoLivreComGrupoJustificativaComponenteSanguineoTest() throws ApplicationBusinessException {

		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		jcs.setGrupoJustificativaComponenteSanguineo(new AbsGrupoJustificativaComponenteSanguineo());
		jcs.setDescricaoLivre(true);
		Mockito.when(mockedAbsJustificativaComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(jcs);
		
		systemUnderTest.verificarDescricaoLivre(1);
	}

	/**
	 * Testa a execucao de passando parametro valido.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=ApplicationBusinessException.class)
	public void verificarDescricaoLivreComGrupoJustificativaComponenteSanguineoSemDescricaoTest() throws ApplicationBusinessException {

		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		jcs.setGrupoJustificativaComponenteSanguineo(new AbsGrupoJustificativaComponenteSanguineo());
		jcs.setDescricaoLivre(false);
		Mockito.when(mockedAbsJustificativaComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(jcs);

		systemUnderTest.verificarDescricaoLivre(1);
	}

	/**
	 * Testa a execucao de passando parametro valido.
	 * @throws ApplicationBusinessException
	 */
	@Test(expected=NullPointerException.class)
	public void verificarDescricaoLivreComGrupoJustificativaComponenteSanguineoDescricaoNuloTest() throws ApplicationBusinessException {

		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		jcs.setGrupoJustificativaComponenteSanguineo(new AbsGrupoJustificativaComponenteSanguineo());
		jcs.setDescricaoLivre(null);
		Mockito.when(mockedAbsJustificativaComponenteSanguineoDAO.obterPorChavePrimaria(Mockito.anyInt())).thenReturn(jcs);

		systemUnderTest.verificarDescricaoLivre(1);
	}
	

	/**
	 * Testa a execucao de passando parametro valido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void excluirJustificativaItemSolicitacaoHemoterapicaTest() throws ApplicationBusinessException {
		
		AbsItemSolicitacaoHemoterapicaJustificativa itemSHJ = new AbsItemSolicitacaoHemoterapicaJustificativa();
		
		systemUnderTest.excluirJustificativaItemSolicitacaoHemoterapica(itemSHJ);
	}

	/**
	 * Testa a execucao de passando parametro valido.
	 * @throws ApplicationBusinessException
	 */
	@Test
	public void excluirJustificativaItemSolicitacaoHemoterapicaComJustificativaComponenteSanguineoTest() throws ApplicationBusinessException {
		
		AbsItemSolicitacaoHemoterapicaJustificativa itemSHJ = new AbsItemSolicitacaoHemoterapicaJustificativa();
		AbsJustificativaComponenteSanguineo jcs = new AbsJustificativaComponenteSanguineo();
		itemSHJ.setJustificativaComponenteSanguineo(jcs);
		
		systemUnderTest.excluirJustificativaItemSolicitacaoHemoterapica(itemSHJ);
	}
	
	
}
