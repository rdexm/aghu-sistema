package br.gov.mec.aghu.compras.contaspagar.business;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class TituloON extends BaseBusiness {

	private static final long serialVersionUID = -5075817603010064130L;

	private static final Log LOG = LogFactory.getLog(TituloON.class);

	// Quebra de linha para tool tips
	public static final String BREAK_LINE = "<br/>";

	// Cores das colunas
	public static final String COR_AMARELA = "background-color:#FFFF66";
	public static final String COR_VERMELHA = "background-color:#F08080";
	public static final String COR_VERDE = "background-color:#00CC66";
	public static final String COR_BRANCA = "background-color:white";

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Preenche cor de fundo do campo situação
	 * 
	 * @param situacao
	 * @param tabelaTitulo
	 *            Tabela título ou tabela de pagamentos
	 * @return
	 */
	public String colorirCampoSituacao(DominioSituacaoTitulo situacao) {
		if (DominioSituacaoTitulo.APG.equals(situacao)) {
			return COR_AMARELA; // A pagar
		} else if (DominioSituacaoTitulo.BLQ.equals(situacao)) {
			return COR_VERMELHA; // Bloqueado
		} else if (DominioSituacaoTitulo.PG.equals(situacao)) {
			return COR_VERDE; // Pago
		}
		return COR_BRANCA;
	}

}
