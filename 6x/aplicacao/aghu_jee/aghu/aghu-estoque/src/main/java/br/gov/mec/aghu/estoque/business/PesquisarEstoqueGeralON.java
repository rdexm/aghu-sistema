package br.gov.mec.aghu.estoque.business;

import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.vo.EstoqueGeralVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class PesquisarEstoqueGeralON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisarEstoqueGeralON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceEstoqueGeralDAO sceEstoqueGeralDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2382982587270875605L;

	private enum PesquisarEstoqueGeralONExceptionCode implements BusinessExceptionCode {
		COMPETENCIA_INVALIDA;
	}
	
	/**
	 * Mátodo que realiza a pesquisa de datas de competência, por mes e ano, em estoque geral,
	 * com as validações necessárias
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<EstoqueGeralVO> pesquisarDatasCompetenciasMovimentosMateriaisPorMesAno(Object parametro) throws ApplicationBusinessException{
		
		Integer ano = null, mes = null;
		
		if(parametro!=null){
			final String vlPesquisa = (String) parametro;

			if (vlPesquisa != null && !StringUtils.isBlank(vlPesquisa)) {

			// 04 ou 4
			if ((vlPesquisa.length() == 1 || vlPesquisa.length() == 2)
					&& !Pattern.compile("[0-9]{1,2}").matcher(vlPesquisa)
							.matches()) {
				throw new ApplicationBusinessException(PesquisarEstoqueGeralONExceptionCode.COMPETENCIA_INVALIDA);

				// 2011
			} else if ((vlPesquisa.length() == 4)
					&& !Pattern.compile("[0-9]{4}").matcher(vlPesquisa)
							.matches()) {
				throw new ApplicationBusinessException(PesquisarEstoqueGeralONExceptionCode.COMPETENCIA_INVALIDA);

				// 3/82 ou 03/1982
			} else if ((vlPesquisa.length() > 4 && vlPesquisa.length() < 7)
					&& !Pattern.compile("[0-9]{1,2}/[0-9]{2,4}")
							.matcher(vlPesquisa).matches()) {
				throw new ApplicationBusinessException(PesquisarEstoqueGeralONExceptionCode.COMPETENCIA_INVALIDA);

				// 11/03/1982
			} else if ((vlPesquisa.length() > 7)
					&& !Pattern.compile("[0-9]{1,2}/[0-9]{2}/[0-9]{4}")
							.matcher(vlPesquisa).matches()) {
				throw new ApplicationBusinessException(PesquisarEstoqueGeralONExceptionCode.COMPETENCIA_INVALIDA);
			}

			
			final String[] comp = vlPesquisa.split("/");

			// 11/03/1982
			if (comp.length == 3) {
				mes = Integer.parseInt(comp[1]);
				ano = Integer.parseInt(comp[2]);

				// 03/1982
			} else if (comp.length == 2) {
				mes = Integer.parseInt(comp[0]);

				if (comp[1].length() == 2) {
					ano = Integer.parseInt("20" + comp[1]);
				} else {
					ano = Integer.parseInt(comp[1]);
				}

			} else {

				// 1982
				if (vlPesquisa.length() == 4) {
					ano = Integer.parseInt(vlPesquisa);

					// 01
				} else if (vlPesquisa.length() == 1 || vlPesquisa.length() == 2) {
					mes = Integer.parseInt(vlPesquisa);

				} else {
					if (vlPesquisa.indexOf('/') > 0) {
						mes = Integer.parseInt(vlPesquisa.substring(0,
								vlPesquisa.indexOf('/')));
					} else {
						throw new ApplicationBusinessException(PesquisarEstoqueGeralONExceptionCode.COMPETENCIA_INVALIDA);
						}
					}
				}
			}
		}
		return getSceEstoqueGeralDAO().pesquisarDatasCompetenciasEstoqueGeralPorMesAno(mes, ano);
	}

	private SceEstoqueGeralDAO getSceEstoqueGeralDAO(){
		return sceEstoqueGeralDAO;
	}
}
