package br.gov.mec.aghu.perinatologia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.model.McoIntercorrencia;
import br.gov.mec.aghu.perinatologia.dao.McoIntercorrenciaDAO;
import br.gov.mec.aghu.perinatologia.vo.IntercorrenciaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class McoIntercorrenciaRN extends BaseBusiness {
	private static final long serialVersionUID = -7824944539622686246L;

	@Inject
	private McoIntercorrenciaDAO mcoIntercorrenciaDAO;

//	@Inject
//	@QualificadorUsuario
//	private Usuario usuario;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Pesquisa de ativos por Seq Ou Descricao
	 * 
	 * C2 de #37859
	 * 
	 * @param parametro
	 * @return
	 */
	public List<IntercorrenciaVO> pesquisarMcoIntercorrenciaAtivosPorSeqOuDescricao(final String parametro)
			throws ApplicationBusinessException {

		List<IntercorrenciaVO> result = new ArrayList<IntercorrenciaVO>();
		List<McoIntercorrencia> list = mcoIntercorrenciaDAO.pesquisarAtivosPorSeqOuDescricao(parametro, 100);

		if (list != null && !list.isEmpty()) {

			for (McoIntercorrencia mcoIntercorrencia : list) {
				result.add(new IntercorrenciaVO(mcoIntercorrencia.getAghCid() != null ? mcoIntercorrencia.getAghCid().getCodigo() : null, mcoIntercorrencia));
			}
		}
		return result;
	}
}
