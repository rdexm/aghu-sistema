package br.gov.mec.aghu.controleinfeccao.business;

import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;

import br.gov.mec.aghu.controleinfeccao.dao.MciNotificacaoGmrDAO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioNotificGermeMultirresistenteVO;
import br.gov.mec.aghu.core.business.BaseBusiness;


@Stateless
public class RelatorioNotificGermeMultirresistenteRN extends BaseBusiness {
	
	
	private static final long serialVersionUID = -6859107032113993395L;
	@Inject
	private MciNotificacaoGmrDAO mciNotificacaoGmrDAO;

	public List<RelatorioNotificGermeMultirresistenteVO> obterDadosRelatorio(String paramFibrose, Integer bacteriaSeq, Short unidadeSeq, Boolean indNotificao){
		List<RelatorioNotificGermeMultirresistenteVO>  lista = mciNotificacaoGmrDAO.listarRelatorioPacientesPortadoresGermeMultiResistente(paramFibrose, bacteriaSeq, unidadeSeq, indNotificao);
		if (StringUtils.isNotBlank(paramFibrose)) {
			for (RelatorioNotificGermeMultirresistenteVO relatorioNotificGermeMultirresistenteVO : lista) {
				relatorioNotificGermeMultirresistenteVO.setIndFibroseCistica(mciNotificacaoGmrDAO.obterAtendimentoFibrose(paramFibrose, relatorioNotificGermeMultirresistenteVO.getPacCodigo()));
				
			}
		}
		return lista;
	}




	@Override
	protected Log getLogger() {
		return null;
	}

}
