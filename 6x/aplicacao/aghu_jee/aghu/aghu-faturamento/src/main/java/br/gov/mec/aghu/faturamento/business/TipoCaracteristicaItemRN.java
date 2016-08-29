package br.gov.mec.aghu.faturamento.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.model.FatTipoCaractItens;

/**
 * <p>
 * Linhas: 24 <br/>
 * Cursores: 0 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 1 tabelas <br/>
 * Alteracoes: 0 tabelas <br/>
 * Metodos: 1 <br/>
 * Metodos externos: 0 <br/>
 * Horas: 1.4 <br/>
 * Pontos: 0.2 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_TCT_RN</code>
 * </p>
 * 
 * @author gandriotti
 */
@SuppressWarnings("PMD.HierarquiaONRNIncorreta")
@Stateless
public class TipoCaracteristicaItemRN
		extends AbstractFatDebugLogEnableRN {

private static final Log LOG = LogFactory.getLog(TipoCaracteristicaItemRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatTipoCaractItensDAO fatTipoCaractItensDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -915236872116637380L;

	protected FatTipoCaractItensDAO getFatTipoCaractItensDAO() {

		return fatTipoCaractItensDAO;
	}

	/**
	 * <p>
	 * TODO Migrar para DAO <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>FATK_TCT_RN.RN_TCTC_VER_SEQCARAC</code>
	 * </p>
	 * 
	 * @param caracteristica
	 * @return
	 * @see FatTipoCaractItens
	 */
	public Integer obterTipoCaractItemSeq(final DominioFatTipoCaractItem caracteristica) {

		Integer result = null;
		FatTipoCaractItensDAO dao = null;
		List<FatTipoCaractItens> tctList = null;
		FatTipoCaractItens tct = null;

		dao = this.getFatTipoCaractItensDAO();
		tctList = dao.listarTipoCaractItensPorCaracteristica(caracteristica.getDescricao());
		if ((tctList != null) && !tctList.isEmpty()) {
			if (tctList.size() > 1) {
				throw new IllegalStateException("Esperando apenas um item para carateristica [" + caracteristica
						+ "] mas recebido: " + tctList);
			}
			tct = tctList.get(0);
			result = tct.getSeq();
		}

		return result;
	}
}
