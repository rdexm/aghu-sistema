package br.gov.mec.aghu.exames.solicitacao.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSolicitacaoExameLote;
import br.gov.mec.aghu.exames.dao.AelLoteExameUsualDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.TipoLoteVO;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class LoteExameUsualON extends BaseBusiness {


	@EJB
	private AelLoteExameUsualRN aelLoteExameUsualRN;
	
	private static final Log LOG = LogFactory.getLog(LoteExameUsualON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@Inject
	private AelLoteExameUsualDAO aelLoteExameUsualDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -1740047913589181467L;

	public List<TipoLoteVO> getDadosLote(DominioSolicitacaoExameLote dominio) {
		List<AelLoteExameUsual> listaAelLoteExameUsual = new ArrayList<AelLoteExameUsual>();
		switch (dominio) {
		case U:
			listaAelLoteExameUsual = getAelLoteExamesUsualDAO()
					.getDadosLoteUnidades();
			break;
		case G:
			listaAelLoteExameUsual = getAelLoteExamesUsualDAO()
					.getDadosLoteGrupos();
			break;
		case E:
			listaAelLoteExameUsual = getAelLoteExamesUsualDAO()
					.getDadosLoteEspecialidades();
			break;
		default:
			break;
		}
		return aelLoteExameUsualParaTipoLoteVO(listaAelLoteExameUsual, dominio);
	}

	/**
	 * Transforma a lista de pojo em lista de VO.
	 * @param listAelLoteExameUsual
	 * @param dominio
	 * @return
	 */
	private List<TipoLoteVO> aelLoteExameUsualParaTipoLoteVO(List<AelLoteExameUsual> listAelLoteExameUsual, DominioSolicitacaoExameLote dominio) {
		List<TipoLoteVO> listaTipoLote = new ArrayList<TipoLoteVO>();
		
		if (listAelLoteExameUsual != null) {
			for (AelLoteExameUsual loteExameUsual : listAelLoteExameUsual) {
				TipoLoteVO tipoLote = new TipoLoteVO(Integer.valueOf(loteExameUsual.getSeq().intValue()), getDescricao(dominio, loteExameUsual));
				listaTipoLote.add(tipoLote);
			}
		}
		return listaTipoLote;
	}

	/**
	 * Busca a descrição conforme o domíinio.
	 * @param dominio
	 * @param loteExameUsual
	 * @return
	 */
	private String getDescricao(DominioSolicitacaoExameLote dominio, AelLoteExameUsual loteExameUsual) {
		String descricao = "";
		
		switch (dominio) {
		case U:
			AghUnidadesFuncionais unf = loteExameUsual.getUnfSeq();
			if (unf != null) {
				descricao = unf.getDescricao();
			}
			break;
		case G:
			AelGrupoExameUsual gru = loteExameUsual.getGruSeq();
			if (gru != null) {
				descricao = gru.getDescricao();
			}
			break;
		case E:
			AghEspecialidades esp = loteExameUsual.getEspSeq();
			if (esp != null) {
				descricao = esp.getNomeEspecialidade();
			}
			break;
		default:
			break;
		}
		
		return descricao;
	}
	
	/***
	 * Inserir lote de exame usual
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirAelLoteExameUsual(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException {
		getAelLoteExameUsualRN().inserirAelLoteExameUsual(loteExameUsual);
	}
	
	/***
	 * atualizar lote de exame usual
	 * @param loteExame
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void atualizarAelLoteExameUsual(AelLoteExameUsual loteExameUsual) throws ApplicationBusinessException {
		getAelLoteExameUsualRN().atualizarAelLoteExameUsual(loteExameUsual);
	}
	
	/***
	 * remover lote de exame usual
	 * @param loteExame
	 * @throws BaseException 
	 */
	public void removerAelLoteExameUsual(Short loteSeq) throws BaseException {
		getAelLoteExameUsualRN().removerAelLoteExameUsual(loteSeq);
	}

	private AelLoteExameUsualDAO getAelLoteExamesUsualDAO() {
		return aelLoteExameUsualDAO;
	}
	
	private AelLoteExameUsualRN getAelLoteExameUsualRN() {
		return aelLoteExameUsualRN;
	}

}