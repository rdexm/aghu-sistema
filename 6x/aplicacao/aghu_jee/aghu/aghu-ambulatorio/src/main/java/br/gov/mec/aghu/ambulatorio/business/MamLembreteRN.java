package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamLembreteDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoLembrete;
import br.gov.mec.aghu.model.MamLembrete;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável por manter as regras de negócio da entidade AacConsultas.
 *
 */
@Stateless
public class MamLembreteRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8088657294544726911L;

	private static final Log LOG = LogFactory.getLog(MamLembreteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private MamLembreteDAO mamLembreteDAO;
	
	
	private static final String NEWLINE = System.getProperty("line.separator");
	
	/**
	 * @ORADB MAMC_GET_LEMBRETE
	 * @param numero
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public StringBuilder obterLembretePorNumeroConsulta(Integer numero) throws ApplicationBusinessException{
		
		List<MamLembrete> lista = new ArrayList<MamLembrete>();
		List<MamLembrete> lista2 = new ArrayList<MamLembrete>();
		
		lista = mamLembreteDAO.obterLembretePorNumeroConsulta(numero);
		lista2 = mamLembreteDAO.obterLembretes(numero);
		
		lista.addAll(lista2);
		
		StringBuilder retorno = new StringBuilder();
		
		if (lista != null && !lista.isEmpty()) {

			StringBuilder textoLivre = new StringBuilder(32);
			
			for (MamLembrete mamLembrete : lista) {
				textoLivre.append(mamLembrete.getDescricao())
					.append(NEWLINE);
				
				if (DominioSituacaoLembrete.E.equals(mamLembrete.getIndPendente())) {
					textoLivre.append(NEWLINE)
						.append(" <<< RESUMO DE CASO EXCLUÍDO >>>");
				}
			}	
				
			if (StringUtils.isNotBlank(textoLivre)) {				
				retorno.append(NEWLINE)
					.append("*** RESUMO DE CASO ***")
					.append(NEWLINE)
					.append(textoLivre)
					.append("******************************")
					.append(NEWLINE);					
			}				
		}
		return retorno;	
	}
}