package br.gov.mec.aghu.business.prescricaoenfermagem.cadastrosapoio;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.EpeGrupoNecesBasica;
import br.gov.mec.aghu.model.EpeSubgrupoNecesBasica;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeGrupoNecesBasicaDAO;
import br.gov.mec.aghu.prescricaoenfermagem.dao.EpeSubgrupoNecesBasicaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoNecessidadesHumanasCRUD extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GrupoNecessidadesHumanasCRUD.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private EpeGrupoNecesBasicaDAO epeGrupoNecesBasicaDAO;

	@Inject
	private EpeSubgrupoNecesBasicaDAO epeSubgrupoNecesBasicaDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = 4012452673469945684L;

	public enum EpeGrupoNecesBasicaCRUDExceptionCode implements BusinessExceptionCode {
		MENSAGEM_EXCLUIR_SUBGRUPO_ASSOCIADO, EPE_00064, MENSAGEM_GR_NECES_HUMANA_JA_EXISTENTE
	}

	public void removerGrupoNecessidadesHumanas(Short seq) throws ApplicationBusinessException {
		EpeGrupoNecesBasica grupoNecessidadesHumanas = epeGrupoNecesBasicaDAO.obterPorChavePrimaria(seq);
		verificarSubGrupoNecessidadesHumanasAssociado(grupoNecessidadesHumanas, false, true);
		epeGrupoNecesBasicaDAO.remover(grupoNecessidadesHumanas);
	}

	private void verificarSubGrupoNecessidadesHumanasAssociado(EpeGrupoNecesBasica grupoNecessidadesHumanas, Boolean acao,
			Boolean exclusao) throws ApplicationBusinessException {
		DominioSituacao dominio = null;
		if (acao) {
			dominio = DominioSituacao.A;
		}
		List<EpeSubgrupoNecesBasica> epeSubGrupoAssociado = epeSubgrupoNecesBasicaDAO.pesquisarSubgrupoNecessidadesBasicasPorSeq(
				grupoNecessidadesHumanas.getSeq(), dominio);
		if (!epeSubGrupoAssociado.isEmpty()) {
			if (exclusao) {
				throw new ApplicationBusinessException(EpeGrupoNecesBasicaCRUDExceptionCode.MENSAGEM_EXCLUIR_SUBGRUPO_ASSOCIADO);
			} else {
				throw new ApplicationBusinessException(EpeGrupoNecesBasicaCRUDExceptionCode.EPE_00064);
			}
		}
	}

	public void persistirGrupoNecessidadesHumanas(EpeGrupoNecesBasica grupoNecessidadesHumanas, Boolean ativo)
			throws ApplicationBusinessException {

		List<EpeGrupoNecesBasica> necList = epeGrupoNecesBasicaDAO.pesquisarGrupoNecesBasica(grupoNecessidadesHumanas.getDescricao());
		if (necList!=null && necList.size()>0){
			for (EpeGrupoNecesBasica grNecBas : necList){
				if (!grNecBas.getSeq().equals(grupoNecessidadesHumanas.getSeq()) && grNecBas.getDescricao().equals(grupoNecessidadesHumanas.getDescricao())){
					throw new ApplicationBusinessException(EpeGrupoNecesBasicaCRUDExceptionCode.MENSAGEM_GR_NECES_HUMANA_JA_EXISTENTE);
				}
			}	
		}

		
		if (grupoNecessidadesHumanas.getSeq() == null) {
			// Faz a inserção
			setarDominioSituacao(grupoNecessidadesHumanas, ativo);
			epeGrupoNecesBasicaDAO.persistir(grupoNecessidadesHumanas);
		} else {
			// Faz a atualização
			if (grupoNecessidadesHumanas.getSituacao().isAtivo() && !ativo) {
				verificarSubGrupoNecessidadesHumanasAssociado(grupoNecessidadesHumanas, true, false);
			}
			setarDominioSituacao(grupoNecessidadesHumanas, ativo);
			epeGrupoNecesBasicaDAO.merge(grupoNecessidadesHumanas);
		}
	}

	private void setarDominioSituacao(EpeGrupoNecesBasica grupoNecessidadesHumanas, Boolean ativo) {
		if (ativo) {
			grupoNecessidadesHumanas.setSituacao(DominioSituacao.A);
		} else {
			grupoNecessidadesHumanas.setSituacao(DominioSituacao.I);
		}
	}

}
