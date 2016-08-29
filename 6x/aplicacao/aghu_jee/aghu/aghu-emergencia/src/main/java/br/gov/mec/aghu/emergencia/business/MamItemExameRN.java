package br.gov.mec.aghu.emergencia.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.dao.MamItemExameDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.dao.MamDescritorDAO;
import br.gov.mec.aghu.emergencia.dao.MamObrigatoriedadeDAO;
import br.gov.mec.aghu.emergencia.vo.ItemObrigatorioVO;
import br.gov.mec.aghu.model.MamDescritor;
import br.gov.mec.aghu.model.MamItemExame;
import br.gov.mec.aghu.model.MamObrigatoriedade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Regras de negócio relacionadas à entidade MamItemExame
 * 
 * @author luismoura
 * 
 */
@Stateless
public class MamItemExameRN extends BaseBusiness {
	private static final long serialVersionUID = 6105864971876026661L;

	public enum MamItemExameRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVICO_INDISPONIVEL_SINAL_VITAL, //
		MENSAGEM_SUCESSO_ADICAO_EXAME, //
		SUCESSO_ALTERACAO_SITUACAO_EXAME, //
		MENSAGEM_EXAME_JA_EXISTENTE, //
		MENSAGEM_SUCESSO_EXCLUSAO_EXAME, //
		;
	}

	@Inject
	private MamObrigatoriedadeDAO mamObrigatoriedadeDAO;

	@Inject
	private MamDescritorDAO mamDescritorDAO;

	@Inject
	private MamItemExameDAO mamItemExameDAO;

	@Override
	protected Log getLogger() {
		return null;
	}

	/**
	 * Busca a lista de itens obrigatórios de exames à partir das obrigatoriedades
	 * 
	 * @param mamObrigatoriedades
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ItemObrigatorioVO> pesquisarItensExames(List<MamObrigatoriedade> mamObrigatoriedades) throws ApplicationBusinessException {
		List<ItemObrigatorioVO> result = new ArrayList<ItemObrigatorioVO>();
		for (MamObrigatoriedade mamObrigatoriedade : mamObrigatoriedades) {
			if (mamObrigatoriedade.getMamItemExame() != null) {
				// Popula a lista de exames
				result.add(this.buscarItensExames(mamObrigatoriedade));
			}
		}
		return result;
	}

	/**
	 * Busca o item de exame obrigatório à partir de uma obrigatoriedade
	 * 
	 * @param mamObrigatoriedade
	 * @return
	 */
	private ItemObrigatorioVO buscarItensExames(MamObrigatoriedade mamObrigatoriedade) throws ApplicationBusinessException {
		if (mamObrigatoriedade.getMamItemExame() != null) {
			ItemObrigatorioVO itemObrigatorioVO = new ItemObrigatorioVO(mamObrigatoriedade.getSeq(), mamObrigatoriedade.getIndSituacao().isAtivo());
			itemObrigatorioVO.setSeqItem(mamObrigatoriedade.getMamItemExame().getSeq());
			itemObrigatorioVO.setDescricao(mamObrigatoriedade.getMamItemExame().getDescricao());
			itemObrigatorioVO.setSigla(null);
			return itemObrigatorioVO;
		}
		return null;
	}

	/**
	 * Monta um registro de MamObrigatoriedade, executando trigger de pos-update
	 * 
	 * RN08 de #32658
	 * 
	 * @param itemObrigatorioVO
	 * @throws ApplicationBusinessException
	 */
	public MamObrigatoriedade montarRegistroExame(Integer seqDesc, ItemObrigatorioVO itemObrigatorioVO) throws ApplicationBusinessException {
		MamObrigatoriedade mamObrigatoriedade = new MamObrigatoriedade();

		// 1. Setar o SEQ do registro selecionado no item 9 do quadro descritivo no campo MAM_OBRIGATORIEDADES.EMS_SEQ
		MamItemExame mamItemExame = mamItemExameDAO.obterPorChavePrimaria(itemObrigatorioVO.getSeqItem());
		mamObrigatoriedade.setMamItemExame(mamItemExame);
		mamObrigatoriedade.setIndSituacao(DominioSituacao.getInstance(itemObrigatorioVO.getIndSituacaoObr()));

		// 2. Setar o SEQ de MAM_DESCRITORES do fieldset “Descritores” no campo MAM_OBRIGATORIEDADES.DCT_SEQ
		MamDescritor descritor = mamDescritorDAO.obterPorChavePrimaria(seqDesc);
		mamObrigatoriedade.setMamDescritor(descritor);

		// 3. Se o registro já tiver sido cadastrado apresentar mensagem “MENSAGEM_EXAME_JA_EXISTENTE”
		// Verificar se existe algum MAM_OBRIGATORIEDADES da mesma MAM_DESCRITORES que já tenha o mesmo EMS_SEQ
		boolean existsItem = mamObrigatoriedadeDAO.existsItemExamePorDescritor(itemObrigatorioVO.getSeqItem(), descritor);
		if (existsItem) {
			throw new ApplicationBusinessException(MamItemExameRNExceptionCode.MENSAGEM_EXAME_JA_EXISTENTE);
		}

		return mamObrigatoriedade;
	}
}
