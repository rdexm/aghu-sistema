package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornJnDAO;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoFornJn;
import br.gov.mec.aghu.core.messages.MessagesUtils;
import br.gov.mec.aghu.core.test.AGHUBaseUnitTest;

/**
 * Teste unitário do objeto de negócio respnsável por journaling de AF's.
 * 
 * @author mlcruz
 */
public class ScoAutorizacaoFornJnONTest extends AGHUBaseUnitTest<ScoAutorizacaoFornJnON>{
	// Messages
	
	private static final String VALUE_SERVIDOR_GERACAO_AF = "Geração";
	private static final String VALUE_SERVIDOR_CONTROLADO_AF = "Alteração";
	private static final String VALUE_SERVIDOR_EXCLUSAO_AF = "Exclusão";
	private static final String VALUE_SERVIDOR_ESTORNO_AF = "Estorno";
	private static final String VALUE_SERVIDOR_ASSINATURA_AF = "Assinatura";
	private static final String VALUE_SERVIDOR_CHEFIA_AF = "Chefia Compras";
	
	/** DAO mockeada. */
	@Mock
	private ScoAutorizacaoFornJnDAO dao;
	
	/** Bundle mockeado. */
	@Mock
	private MessagesUtils messagesUtils;
	
	/**
	 * Testa obtenção de responsáveis de versão de AF.
	 */
	@Test
	public void testObtencaoResponsaveisAutorizacaoFornJn() {
		final Integer numeroAf = 1;
		final Short complementoAf = 1;
		final Short sequenciaAlteracao = 1;
		
		final String gerador = "Gerador XYZ";
		final String controlado = "Controlado XYZ";
		final String excluido = "Excluído XYZ";
		final String estorno = "Estorno XYZ";
		final String assinatura = "Assinatura XYZ";
		final String chefia = "Chefia XYZ";
		
		int i = 0;
		final Date dtGeracao = getDate(++i);
		final Date dtAlteracao = getDate(++i);
		final Date dtExclusao = getDate(++i);
		final Date dtEstorno = getDate(++i);
		final Date dtAssinaCoord = getDate(++i);
		final Date dtAssinaturaChefia = getDate(++i);
		
		final ScoAutorizacaoFornJn afJn = new ScoAutorizacaoFornJn();
		
		afJn.setServidor(getServidor(gerador));
		afJn.setDtGeracao(dtGeracao);
		
		afJn.setServidorControlado(getServidor(controlado));
		afJn.setDtAlteracao(dtAlteracao);
		
		afJn.setServidorExcluido(getServidor(excluido));
		afJn.setDtExclusao(dtExclusao);
		
		afJn.setServidorEstorno(getServidor(estorno));
		afJn.setDtEstorno(dtEstorno);
		
		afJn.setServidorAssinaCoord(getServidor(assinatura));
		afJn.setDtAssinaturaCoord(dtAssinaCoord);
		
		afJn.setServidorAutorizado(getServidor(chefia));
		afJn.setDtAssinaturaChefia(dtAssinaturaChefia);
		
		Mockito.when(dao.obterResponsaveisAutorizacaoFornJn(numeroAf, complementoAf, sequenciaAlteracao)).thenReturn(afJn);
		
		Mockito.when(messagesUtils.getResourceBundleValue("")).thenReturn("");
		
		systemUnderTest
				.obterResponsaveisAutorizacaoFornJn(
						numeroAf, complementoAf, sequenciaAlteracao).iterator();
	}
	
	/**
	 * Obtem data.
	 * 
	 * @param i Dia a ser incrementado.
	 * @return Data
	 */
	private Date getDate(int i) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, i);
		return cal.getTime();
	}

	/**
	 * Obtem servidor.
	 * 
	 * @param nome Nome do servidor.
	 * @return Servidor
	 */
	private RapServidores getServidor(String nome) {
		RapServidores servidor = new RapServidores();
		RapPessoasFisicas pessoa = new RapPessoasFisicas();
		pessoa.setNome(nome);
		servidor.setPessoaFisica(pessoa);
		return servidor;
	}

}