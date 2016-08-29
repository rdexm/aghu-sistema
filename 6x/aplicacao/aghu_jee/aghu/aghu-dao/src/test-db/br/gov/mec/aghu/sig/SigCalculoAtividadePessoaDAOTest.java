package br.gov.mec.aghu.sig;

import org.hibernate.Query;
import org.junit.Test;

import br.gov.mec.aghu.dao.AbstractDAOTest;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.sig.dao.SigCalculoAtividadePessoaDAO;

/**
 * @author rogeriovieira
 */
public class SigCalculoAtividadePessoaDAOTest extends AbstractDAOTest<SigCalculoAtividadePessoaDAO> {

	@Override
	protected SigCalculoAtividadePessoaDAO doDaoUnderTests() {
		return new SigCalculoAtividadePessoaDAO() {

			private static final long serialVersionUID = 1L;

			@Override
			protected Query createHibernateQuery(String query) {
				return SigCalculoAtividadePessoaDAOTest.this.createHibernateQuery(query);
			}
		};
	}
	
	@Test
	public void buscarDetalheFolhaPessoalTest(){
		this.getDaoUnderTests().buscarGruposOcupacaoAlocacaoExcedente(40, DominioTipoObjetoCusto.AS);
	}
	
	@Test
	public void buscarAtividadesAlocacaoExcedenteTest(){
		this.getDaoUnderTests().buscarAtividadesAlocacaoExcedente(40, 13, 31313, DominioTipoObjetoCusto.AS);
	}
	
	@Test
	public void buscarAtividadesComValoresRealizadosCalculadosTest(){
		this.getDaoUnderTests().buscarAtividadesComValoresRealizadosCalculados(40, DominioTipoObjetoCusto.AS);
	}

	@Override
	protected void initMocks() {
	}

	@Override
	protected void finalizeMocks() {
	}
}